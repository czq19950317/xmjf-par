$(function () {
    loadRechargeRecodesData();
});
function loadRechargeRecodesData() {
    $.ajax({
        type:"post",
        url:ctx+"/account/queryRechargeRecodesByUserId",
        dataType:"JSON",
        success:function (data) {
            var paginator=  data.paginator;//分页信息
            var list=data.list;
            if(list.length>0){
                initDivsHtml(list);
            }else {
                alert("记录不存在");
            }
        }
    })

}
function  initDivsHtml(list) {
    if(list.length>0){
        var divs="";
        for (var i=0;i<list.length;i++){
            var tempData=list[i];
            divs=divs+"<div class='table-content-first'>";
            divs=divs+tempData.addtime+"</div>";
            divs=divs+"<div class='table-content-first'>";
            divs=divs+tempData.rechargeAmount+"元</div>";
            divs=divs+"<div class='table-content-first'>";
            var status=tempData.status;
            if(status==0){
                divs=divs+"支付失败";
            }
            if(status==1){
                divs=divs+"已支付";
            }
            if(status==2){
                divs=divs+"待支付";
            }
            divs=divs+"</div>"
        }
        $("#rechargeList").html(divs);
    }
}