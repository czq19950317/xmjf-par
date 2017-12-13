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