/*=========================================================================================
    File Name: bar.js
    Description: Morris bar chart
    ----------------------------------------------------------------------------------------
    Item Name: Robust - Responsive Admin Template
    Version: 2.0
    Author: PIXINVENT
    Author URL: http://www.themeforest.net/user/pixinvent
==========================================================================================*/

// Bar chart
// ------------------------------
$(window).on("load", function(){

    Morris.Bar({
        element: 'bar-chart',
        data: [{
                y: '신속도',
                a: 75,
                b: 88
            }, {
                y: '친절도',
                a: 87,
                b: 95
            }, {
                y: '전문성',
                a: 94,
                b: 96
            }, {
                y: '공정성',
                a: 90,
                b: 93
            }, {
                y: '신뢰도',
                a: 93,
                b: 97
            }, {
                y: '만족도',
                a: 90,
                b: 93
            }, {
                y: '총점',
                a: 88,
                b: 93
            }
        ],
        xkey: 'y',
        ykeys: ['a', 'b'],
        labels: ['전체', '행정지원과'],
        barGap: 6,
        barSizeRatio: 0.35,
        smooth: true,
        gridLineColor: '#e3e3e3',
        numLines: 5,
        gridtextSize: 14,
        fillOpacity: 0.4,
        resize: true,
        barColors: ['#00A5A8', '#FF7D4D']
    });
});