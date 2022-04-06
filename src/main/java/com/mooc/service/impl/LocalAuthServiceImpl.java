package com.mooc.service.impl;

import com.mooc.dao.LocalAuthDao;
import com.mooc.dao.PersonInfoDao;
import com.mooc.dto.LocalAuthExection;
import com.mooc.entity.LocalAuth;
import com.mooc.entity.PersonInfo;
import com.mooc.enums.LocalAuthStateEnum;
import com.mooc.exceptions.LocalAuthOperationException;
import com.mooc.service.LocalAuthService;
import com.mooc.util.DESUtil;
import com.mooc.util.ImageUtil;
import com.mooc.util.MD5;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.util.Date;

@Service
public class LocalAuthServiceImpl implements LocalAuthService {

    @Autowired
    private LocalAuthDao localAuthDao;
    @Autowired
    private PersonInfoDao personInfoDao;


    public LocalAuth getLocalAuthByUsernamAndPwd(String username, String password) {
        //需要对密码进行加密
        return localAuthDao.queryLocalByUserNameAndPwd(username,DESUtil.getEncryptString(password));
    }

    public LocalAuth getLocalAuthByUserId(long userId) {
        return localAuthDao.queryLocalByUserId(userId);
    }


    @Transactional
    public LocalAuthExection bindLocalAuth(LocalAuth localAuth)
            throws RuntimeException {
        if (localAuth == null || localAuth.getPassword() == null
                || localAuth.getUserName() == null
                || localAuth.getUserId() == null) {
            return new LocalAuthExection(LocalAuthStateEnum.NULL_AUTH_INFO);
        }
        LocalAuth tempAuth = localAuthDao.queryLocalByUserId(localAuth
                .getUserId());
        if (tempAuth != null) {
            return new LocalAuthExection(LocalAuthStateEnum.ONLY_ONE_ACCOUNT);
        }
        try {
            localAuth.setCreateTime(new Date());
            localAuth.setLastEditTime(new Date());
            localAuth.setPassword(DESUtil.getEncryptString(localAuth.getPassword()));
            int effectedNum = localAuthDao.insertLocalAuth(localAuth);
            if (effectedNum <= 0) {
                throw new RuntimeException("帐号绑定失败");
            } else {
                return new LocalAuthExection(LocalAuthStateEnum.SUCCESS,
                        localAuth);
            }
        } catch (Exception e) {
            throw new RuntimeException("insertLocalAuth error: "
                    + e.getMessage());
        }
    }

    /*修改密码*/
    @Transactional
    public LocalAuthExection modifyLocalAuth(Long userId, String username, String password, String newPassword, Date lastEditTime) throws LocalAuthOperationException {
        //原来的密码加密，之后再新密码加密
        String p_old=DESUtil.getEncryptString(password);
        String P_new=DESUtil.getEncryptString(newPassword);
        //非空判断
        if (userId != null && username != null && password != null
                && newPassword != null && !password.equals(newPassword)) {
            try {

                int effectedNum = localAuthDao.updateLocalAuth(userId,
                        username, p_old,P_new, new Date());
                if (effectedNum <= 0) {
                    throw new RuntimeException("更新密码失败");
                }
                return new LocalAuthExection(LocalAuthStateEnum.SUCCESS);
            } catch (Exception e) {
                throw new RuntimeException("更新密码失败:" + e.toString());
            }
        } else {
            return new LocalAuthExection(LocalAuthStateEnum.NULL_AUTH_INFO);
        }

    }

    /*注册*/

    @Transactional
    public LocalAuthExection register(LocalAuth localAuth,
                                       CommonsMultipartFile profileImg) throws RuntimeException {
        if (localAuth == null || localAuth.getPassword() == null
                || localAuth.getUserName() == null) {
            return new LocalAuthExection(LocalAuthStateEnum.NULL_AUTH_INFO);
        }
        try {
            localAuth.setCreateTime(new Date());
            localAuth.setLastEditTime(new Date());
            localAuth.setPassword(DESUtil.getEncryptString(localAuth.getPassword()));
            if (localAuth.getPersonInfo() != null
                    && localAuth.getPersonInfo().getUserId() == null) {
                if (profileImg != null) {
                    localAuth.getPersonInfo().setCreateTime(new Date());
                    localAuth.getPersonInfo().setLastEditTime(new Date());
                    localAuth.getPersonInfo().setEnableStatus(1);
                    //不添加图片
                    /*try {
                        addProfileImg(localAuth, profileImg);
                    } catch (Exception e) {
                        throw new RuntimeException("addUserProfileImg error: "
                                + e.getMessage());
                    }*/
                }
                try {
                    PersonInfo personInfo = localAuth.getPersonInfo();
                    int effectedNum = personInfoDao
                            .insertPersonInfo(personInfo);
                    localAuth.setUserId(personInfo.getUserId());
                    if (effectedNum <= 0) {
                        throw new RuntimeException("添加用户信息失败");
                    }
                } catch (Exception e) {
                    throw new RuntimeException("insertPersonInfo error: "
                            + e.getMessage());
                }
            }
            int effectedNum = localAuthDao.insertLocalAuth(localAuth);
            if (effectedNum <= 0) {
                throw new RuntimeException("帐号创建失败");
            } else {
                return new LocalAuthExection(LocalAuthStateEnum.SUCCESS,
                        localAuth);
            }
        } catch (Exception e) {
            throw new RuntimeException("insertLocalAuth error: "
                    + e.getMessage());
        }
    }

    /*private void addProfileImg(LocalAuth localAuth,
                               CommonsMultipartFile profileImg) {
        String dest = PathUtil.getPersonInfoImagePath();
        String profileImgAddr = ImageUtil.generateThumbnail(profileImg, dest);
        localAuth.getPersonInfo().setProfileImg(profileImgAddr);
    }*/

}
