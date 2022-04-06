package com.mooc.interceptor.shopadmin;

import com.mooc.entity.PersonInfo;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/*
* 店家管理系统登录验证拦截器,继承HandlerInterceptorAdapter的抽象类
* */
public class ShopLoginInterceptor extends HandlerInterceptorAdapter {
    /*主要做事前拦截，即用户操作发生前，设置拦截,(在controller方法执行之前)*/

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
       //从session中取出用户信息来
        Object userObj = request.getSession().getAttribute("user");
        if(userObj!=null){
            //若用户信息不为空则将session里面的用户信息转为personInfo实体类。。。
            PersonInfo user = (PersonInfo) userObj;
            //空值判断,确保userId不为空，并且该账号的可用状态为1，并且用户类型为店家
            if(user!=null && user.getUserId()!=null && user.getUserId()>0 && user.getEnableStatus()==1){
                //若通过验证则返回true，拦截器返回true之后，用户接下来的操作得以正常进行
                return true;
            }
        }

        //若不满足登录验证，则直接跳转到帐号登陆页面
        PrintWriter out=response.getWriter();
        out.println("<html>");
        out.println("<script>");
        out.println("window.open ('" + request.getContextPath()
                + "/local/login?usertype=2','_self')");  //店家管理的页面
        out.println("</script>");
        out.println("</html>");
        return false;
    }
}
