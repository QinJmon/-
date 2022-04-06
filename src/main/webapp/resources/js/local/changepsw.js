$(function() {
    var url = '/o2o-1/local/changelocalpwd';
    var usertype = getQueryString('usertype');
    $('#submit').click(function() {
        var userName = $('#userName').val();
        if (!userName) {
            $.toast('请输入用户名！');
            return;
        }
        var password = $('#password').val();
        if (!password) {
            $.toast('请输入密码！');
            return;
        }

        var newPassword = $('#newPassword').val();
        if (!newPassword) {
            $.toast('请输入密码！');
            return;
        }
        var confirmPassword = $('#confirmPassword').val();
        // 新密码和确认密码不相同
        if (newPassword != confirmPassword) {
            $.toast('新密码和确认密码输入不相同，请重新输入');
            return;
        }
        // 新密码和原密码相同
        if (newPassword == password) {
            $.toast('新密码和原密码相同，请重新输入');
            return;
        }
        var formData = new FormData();
        formData.append('userName', userName);
        formData.append('password', password);
        formData.append('newPassword', newPassword);
        var verifyCodeActual = $('#j_captcha').val();
        if (!verifyCodeActual) {
            $.toast('请输入验证码！');
            return;
        }
        formData.append("verifyCodeActual", verifyCodeActual);
        $.ajax({
            url : url,
            type : 'POST',
            data : formData,
            contentType : false,
            processData : false,
            cache : false,
            success : function(data) {
                if (data.success) {
                    $.toast('密码修改成功！');
                    if(usertype == 1){
                        //若用户在前端展示系统页面则自动退回到前端展示系统
                        window.location.href='/o2o-1/frontend/index';
                    }else {
                        //若用户再店家管理系统页面，则自动回到店铺列表
                        window.location.href='/o2o-1/shopadmin/shoplist';
                    }
                } else {
                    $.toast('提交失败'+data.errMsg);
                    $('#captcha_img').click();
                }
            }
        });
    });

    $('#back').click(function() {
        window.location.href = '/o2o-1/shopadmin/shoplist' ;
    });
});