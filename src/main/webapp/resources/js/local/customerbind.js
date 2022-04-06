$(function() {
	var bindUrl = '/o2o-1/local/bindlocalauth';  //controller里面的方法路径
	//usertype=1则为前端展示系统，其他为店家管理系统
	var usertype=getQueryString("usertype");

	$('#submit').click(function() {
		var userName = $('#username').val();
		var password = $('#psw').val();
		var verifyCodeActual = $('#j_captcha').val();
		var needVerify = false;
		if (!verifyCodeActual) {
			$.toast('请输入验证码！');
			return;
		}
		$.ajax({
			url : bindUrl,
			async : false,
			cache : false,
			type : "post",
			dataType : 'json',
			data : {
				userName : userName,
				password : password,
				verifyCodeActual : verifyCodeActual
			},
			success : function(data) {
				if (data.success) {
					$.toast('绑定成功！');
					if(usertype == 1){
						//若用户在前端展示系统页面则自动退回到前端展示系统
						window.location.href='/o2o-1/frontend/index';
					}else {
						//若用户再店家管理系统页面，则自动回到店铺列表
						window.location.href='/o2o-1/shopdamin/shoplist';
					}
					/*window.location.href = 'javascript :history.back(-1);';*/
				} else {
					$.toast('绑定失败！'+data.errMsg);
					$('#captcha_img').click();
				}
			}
		});
	});
});