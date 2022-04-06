$(function() {
    var usertype = getQueryString('usertype');
    var loginUrl = '/o2o-1/local/logincheck';
    var loginCount = 0;

    $('#submit').click(function() {
        var userName = $('#username').val();
        var password = $('#psw').val();
        var verifyCodeActual = $('#j_captcha').val();
        var needVerify = false;
        if (loginCount >= 3) {
            if (!verifyCodeActual) {
                $.toast('请输入验证码！');
                return;
            } else {
                needVerify = true;
            }
        }
        $.ajax({
            url : loginUrl,
            async : false,
            cache : false,
            type : "post",
            dataType : 'json',
            data : {
                userName : userName,
                password : password,
                verifyCodeActual : verifyCodeActual,
                needVerify : needVerify,
            },
            success : function(data) {
               if(data.success){
                  $.toast('登陆成功！');
                   if(usertype == 1){
                       //若用户在前端展示系统页面则自动退回到前端展示系统
                       window.location.href='/o2o-1/frontend/index';
                   }else {
                       //若用户再店家管理系统页面，则自动回到店铺列表
                       window.location.href='/o2o-1/shopadmin/shoplist';
                   }
               }else {
                    $.toast('登陆失败！'+data.errMsg);
                    loginCount++;
                    if (loginCount >= 3) {
                        //登录失败三次，需要做验证码校验
                        $('#verifyPart').show();
                    }
                }
            }
        });
    });

    $('#register').click(function() {
        window.location.href = '/o2o-1/admin/register?userType=back';
    });
});