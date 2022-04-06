package com.mooc.service;

import com.mooc.dto.LocalAuthExection;
import com.mooc.entity.LocalAuth;
import com.mooc.exceptions.LocalAuthOperationException;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.util.Date;

public interface LocalAuthService {
    /**/
    LocalAuth getLocalAuthByUsernamAndPwd(String username,String password);

    /*通过userid获取平台账号信息*/
    LocalAuth getLocalAuthByUserId(long userId);

    /**
     *绑定用户和平台账号
     * @param localAuth
     * @return
     * @throws RuntimeException
     */
    LocalAuthExection bindLocalAuth(LocalAuth localAuth)
            throws LocalAuthOperationException;

    /*修改平台账号的登录密码*/
    LocalAuthExection modifyLocalAuth(Long userId, String username, String password,
                                      String newPassword, Date lastEditTime)
            throws LocalAuthOperationException;


    LocalAuthExection register(LocalAuth localAuth,
                               CommonsMultipartFile profileImg);

}
