package com.mooc.web.local;

import com.mooc.dto.LocalAuthExection;
import com.mooc.entity.LocalAuth;
import com.mooc.entity.PersonInfo;
import com.mooc.entity.ProductCategory;
import com.mooc.enums.LocalAuthStateEnum;
import com.mooc.service.LocalAuthService;
import com.mooc.util.CodeUtil;
import com.mooc.util.DESUtil;
import com.mooc.util.HttpServletRequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*登录检验，退出，修改密码，绑定账号*/

@Controller
@RequestMapping("/local")
public class LocalAuthController {

    @Autowired
    private LocalAuthService localAuthService;

    /*修改密码*/
    @RequestMapping(value = "/changelocalpwd",method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> changeLocalPwd(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        //验证码校验
        if(!CodeUtil.checkVerifyCode(request)){
            modelMap.put("success",false);
            modelMap.put("errMsg","输入了错误的验证码");
            return modelMap;
        }
        //获取账号
        String userName = HttpServletRequestUtil.getString(request, "userName");
        //获取原密码
        String password = HttpServletRequestUtil.getString(request, "password");
        //获取新密码
        String newPassword = HttpServletRequestUtil.getString(request, "newPassword");
        //从session中获取当前用户信息(微信登陆后)
        PersonInfo user = (PersonInfo) request.getSession().getAttribute("user");
        //非空判断，要求账号新旧密码以及当前的用户session非空，且新旧密码不相同
        if(userName!=null && password!=null && newPassword!=null
        && user!=null && user.getUserId()!=null){
            try{
                //查看原先账号，看看与输入的账号是否一致，不一致则认为是非法操作
                LocalAuth localAuth = localAuthService.getLocalAuthByUserId(user.getUserId());
                if(localAuth==null || !localAuth.getUserName().equals(userName)){
                    //不一致则退出
                    modelMap.put("success",false);
                    modelMap.put("errMsg","输入的账号非本次登录的账号");
                    return modelMap;
                }
                //修改平台账号的用户密码
                LocalAuthExection le = localAuthService.modifyLocalAuth(user.getUserId(), userName, password, newPassword, new Date());
                if(le.getState()== LocalAuthStateEnum.SUCCESS.getState()){
                    modelMap.put("success",true);
                }else {
                    modelMap.put("success",false);
                    modelMap.put("errMsg",le.getStateInfo());
                }
            } catch (Exception e) {
                modelMap.put("success",false);
                modelMap.put("errMsg",e.toString());
            }
        }else {
            modelMap.put("success",false);
            modelMap.put("errMsg","请输入密码");

        }
        return modelMap;
    }

    @RequestMapping(value = "/logincheck",method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> logincheck(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        //获取是否需要进行验证码校验的标识符(输入密码输错三次就需要验证码验证)
        boolean needVerify = HttpServletRequestUtil.getBoolean(request, "needVerify");
        if(needVerify && !CodeUtil.checkVerifyCode(request)){
            modelMap.put("success",false);
            modelMap.put("errMsg","输入了错误的验证码");
            return modelMap;
        }
        //获取输入的帐号
        String userName = HttpServletRequestUtil.getString(request, "userName");
        //获取输入的密码
        String password = HttpServletRequestUtil.getString(request, "password");
        //非空验证
        if(userName!=null && password !=null){
            //传入账号和密码去获取平台账号信息
            LocalAuth localAuth = localAuthService.getLocalAuthByUsernamAndPwd(userName, password);
            if(localAuth!=null){
                //若能取到账号信息则登陆成功
                modelMap.put("success",true);
                //同时在session里设置用户信息
                request.getSession().setAttribute("user",localAuth.getPersonInfo());
            }else {
                modelMap.put("success",false);
                modelMap.put("errMsg","用户名或密码错误");
            }
        }else {
            modelMap.put("success",false);
            modelMap.put("errMsg","用户名或密码均不能为空");
        }
        return modelMap;
    }

    /*退出操作*/
    @RequestMapping(value = "/logout",method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> logout(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        //将用户session置为空
        request.getSession().setAttribute("user",null);
        modelMap.put("success",true);
        return modelMap;
    }

    /*用户绑定*/
    @RequestMapping(value = "/bindlocalauth", method = RequestMethod.POST)
    @ResponseBody
    private Map<String, Object> bindLocalAuth(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        if (!CodeUtil.checkVerifyCode(request)) {
            modelMap.put("success", false);
            modelMap.put("errMsg", "输入了错误的验证码");
            return modelMap;
        }
        String userName = HttpServletRequestUtil.getString(request, "userName");
        String password = HttpServletRequestUtil.getString(request, "password");
        PersonInfo user = (PersonInfo) request.getSession()
                .getAttribute("user");
        if (userName != null && password != null && user != null
                && user.getUserId() != null) {
            //加密
            password = DESUtil.getEncryptString(password);
            LocalAuth localAuth = new LocalAuth();
            localAuth.setUserName(userName);
            localAuth.setPassword(password);
            localAuth.setUserId(user.getUserId());
            LocalAuthExection le = localAuthService.bindLocalAuth(localAuth);
            if (le.getState() == LocalAuthStateEnum.SUCCESS.getState()) {
                modelMap.put("success", true);
            } else {
                modelMap.put("success", false);
                modelMap.put("errMsg", le.getStateInfo());
            }
        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", "用户名和密码均不能为空");
        }
        return modelMap;
    }

}
