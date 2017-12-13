$(function () {
    $("#identityNext").click(function () {
        var realName=$("#realName").val();
        var idCard=$("#card").val();
        var businessPassword=$("#_ocx_password").val();
        var confirmPassword=$("#_ocx_password1").val();
        if(isEmpty(realName)){
            layer.tips("真实姓名不能为空","#realName");
            return;
        }
        if(isEmpty(idCard)){
            layer.tips("用户身份证不能为空","#card");
            return;
        }
        if(idCard.length!=18){
            layer.tips("身份证号不合法","#_ocx_password");
            return;
        }
        if(isEmpty(businessPassword)||isEmpty(confirmPassword)){
            layer.tips("密码非法！","#_ocx_password1");
            return;
        }
        var parmas={};
        parmas.realName=realName;
        parmas.idCard=idCard;
        parmas.businessPassword=businessPassword;
        parmas.confirmPassword=confirmPassword;
        $.ajax({
            type:"post",
            url:ctx+"/user/userAuth",
            data:parmas,
            dataType:"json",
            success:function (data) {
                if(data.code==200){
                    window.location.href=ctx+"/account/rechargePage";
                }else {
                    layer.tips(data.msg,"#identityNext");
                    return;
                }
            }

        })
    })
});