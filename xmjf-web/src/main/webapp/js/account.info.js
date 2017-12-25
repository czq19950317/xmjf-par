var chart=null;
$(function () {
    loadAccountInfoData();
    loadInvestInfoData();
});
function loadAccountInfoData() {
    $.ajax({
        type:"post",
        url:ctx+"/account/accountInfo",
        dataType:"json",
        success:function (data) {
            var data1=data.data1;
            var data2=data.data2;
            if(data1.length>0){
                $('#pie_chart').highcharts({
                    chart: {
                        plotBackgroundColor: null,
                        plotBorderWidth: null,
                        plotShadow: false,
                        spacing : [100, 0 , 40, 0]
                    },
                    title: {
                        floating:true,
                        text: '总资产:'+data2+"￥"
                    },
                    tooltip: {
                        pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
                    },
                    plotOptions: {
                        pie: {
                            allowPointSelect: true,
                            cursor: 'pointer',
                            dataLabels: {
                                enabled: true,
                                format: '<b>{point.name}</b>: {point.percentage:.1f} %',
                                style: {
                                    color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
                                }
                            },
                            point: {
                                events: {
                                    /*mouseOver: function(e) {  // 鼠标滑过时动态更新标题
                                        // 标题更新函数，API 地址：https://api.hcharts.cn/highcharts#Chart.setTitle
                                        chart.setTitle({
                                            text: e.target.name+ '\t'+ e.target.y + ' %'
                                        });
                                    }*/
                                    //,
                                    // click: function(e) { // 同样的可以在点击事件里处理
                                    //     chart.setTitle({
                                    //         text: e.point.name+ '\t'+ e.point.y + ' %'
                                    //     });
                                    // }
                                }
                            },
                        }
                    },
                    series: [{
                        type: 'pie',
                        innerSize: '80%',
                        name: '市场份额',
                        data: data1
                    }]
                }, function(c) {
                    // 环形图圆心
                    var centerY = c.series[0].center[1],
                        titleHeight = parseInt(c.title.styles.fontSize);
                    c.setTitle({
                        y:centerY + titleHeight/2
                    });
                    chart = c;
                });
            }

        }
    })
}

function loadInvestInfoData() {
    $.ajax({
        type:"post",
        url:ctx+"/busItemInvest/queryInvestInfoByUserId",
        dataType:"json",
        success:function (data) {
            var data1=data.data1;
            var data2=data.data2;
            if(data1.length>0){
                $("#line_chart").highcharts({
                    chart: {
                        type: 'spline'
                    },
                    title: {
                        text: '用户投资收益折线图'
                    },
                    subtitle: {
                        text: '数据来源: SXT_P2P'
                    },
                    xAxis: {
                        categories: data1
                    },
                    yAxis: {
                        title: {
                            text: '总金额'
                        },
                        labels: {
                            formatter: function () {
                                return this.value + '°';
                            }
                        }
                    },
                    tooltip: {
                        crosshairs: true,
                        shared: true
                    },
                    plotOptions: {
                        spline: {
                            marker: {
                                radius: 4,
                                lineColor: '#666666',
                                lineWidth: 1
                            }
                        }
                    },
                    series: [{
                        name: '投资',
                        marker: {
                            symbol: 'square'
                        },
                        data: data2
                    }]
                })

            }
            
        }
    })

}