$(function () {
    $('#rate').radialIndicator();
    var val=$("#rate").attr("data-val");
    var radialObj=$("#rate").data('radialIndicator');
    radialObj.option("barColor","orange");
    radialObj.option("percentage",true);
    radialObj.option("barWidth",10);
    radialObj.option("radius",40);
    radialObj.value(val);


    $('#tabs div').click(function () {
        $(this).addClass('tab_active');
        var show=$('#contents .tab_content').eq($(this).index());
        show.show();
        $('#tabs div').not($(this)).removeClass('tab_active');
        $('#contents .tab_content').not(show).hide();
        if($(this).index()==2){
            /**
             * 获取项目投资记录
             *   ajax 拼接tr
             *    追加tr 到 recordList
             */
            // alert("投资用户列表");
            loadInvestRecodesList($("#itemId").val());

        }
    });
});

function loadInvestRecodesList(itemId){
    var str="<tr>"
    var params={};
    params.itemId=itemId;

    if(itemId==null){
        str=str+"请先登录"
        str=str+"</tr>";
    }
    $.ajax({
        type:"post",
        url:ctx+"/busItemInvest/queryBusItemInvestsByItemId",
        data:params,
        dataType:"json",
        success:function (data) {
            if(data.code==200){
                var paginator=data.result.paginator;// 分页信息
                var list=data.result.list;
                if(list!=null){
                    initTrHtml(list);
                    initPageHtml(paginator);//分页
                }else{
                    //alert("暂无数据!");
                    $("#recordList").html("<img style='margin-left: -70px;padding:40px;'" +
                        "src='/img/zanwushuju.png'>");

                }

            }else {
                $("#recordList").html(str+data.msg+"</tr>");
            }

        }

    });


};

function initTrHtml(list) {
    if(list.length>0){
        var trs="";
        for(var i=0;i<list.length;i++) {
            var tempData = list[i];

            trs=trs+"<tr>";
            trs=trs+"<td>"+tempData.userId+"</td><td>"+tempData.investAmount+"</td><td>"+tempData.updatetime+"</td></tr>"

        }
    }
    $("#recordList").html(trs);

}

function  initPageHtml(paginator) {
    var navigatepageNums = paginator.navigatepageNums;
    if (navigatepageNums.length > 0) {
        var lis = "";
        for (var j = 0; j < navigatepageNums.length; j++) {
            var page = navigatepageNums[j];

            var href = "javascript:toPageData(" + page + ")";
            if (page == paginator.pageNum) {
                lis = lis + "<li class='active'><a href='" + href + "' title='第" + page + "页' >" + page + "</a></li>";
            } else {
                lis = lis + "<li ><a href='" + href + "' title='第" + page + "页' >" + page + "</a></li>";
            }
        }
        $("#pages").html(lis);
    }
}
function toRecharge() {
    $.ajax({
        type:"post",
        url:ctx+"/user/userAuthCheck",
        dataType:"json",
        success:function (data) {
            if(data.code==200){
                window.location.href=ctx+"/account/rechargePage";
            }else {
                layer.confirm(data.msg,{
                    btn:['执行认证','稍后认证']
                },function () {
                    window.location.href=ctx+"/user/auth";
                })
            }
        }
    })

}

function doInvest() {

    var usableAmount=parseFloat($("#ye").attr("data-value"));
    var amount=$("#usableMoney").val();
    var itemId=$("#itemId").val();
    /**
     * 账户余额  >0
     * 投资金额 >账户余额
     * 投资金额>=最小投资金额(单笔投资)
     * 投资金额<=最大投资金额(单笔投资)
     * 投资记录非空
     */
    if(usableAmount==0){
        layer.tips("可用余额不满足本次投资金额","#tz");
        return;
    }
    if(isEmpty(amount)){
        layer.tips("请输入投资金额","#usableMoney");
        return;
    }
    if(amount>usableAmount){
        layer.tips("投资金额不能大于账户可用余额","#usableMoney");
        return;
    }
    //起头金额
    var sinleMinInvestAmount=parseFloat($("#minInvestMoney").attr("data-value"));
    if(sinleMinInvestAmount>0){
        if(amount<sinleMinInvestAmount){
            layer.tips("投资金额不能小于起投金额","#usableMoney");
            return;
        }
    }

    var sinleMaxInvestAmount=parseFloat($("#maxInvestMoney").attr("data-value"));
    if(sinleMaxInvestAmount>0){
        if(amount>sinleMaxInvestAmount){
            layer.tips("投资金额不能大于单笔最大起投金额","#usableMoney");
            return;
        }
    }
    layer.prompt({title:'输入任何口令，并确认',formType:1},function (pass,index) {
        layer.close(index);
        var businessPassword=pass;
        if(isEmpty(businessPassword)){
            layer.msg("交易密码不能为空！")
            return;
        }
        $.ajax({
            type:"post",
            url:ctx+"/busItemInvest/userInvest",
            data:{
                itemId:itemId,
                amount:amount,
                businessPassword:businessPassword
            },
            dataType:"json",
            success:function (data) {
                if(data.code==200){
                    layer.msg("项目投标成功！");
                    window.location.reload(false);
                }else {
                    layer.msg(data.msg);
                }
            }
        })
    });


    
}