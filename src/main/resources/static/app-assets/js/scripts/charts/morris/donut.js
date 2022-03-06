/*=========================================================================================
    File Name: donut.js
    Description: Morris donut chart
    ----------------------------------------------------------------------------------------
    Item Name: Robust - Responsive Admin Template
    Version: 2.0
    Author: PIXINVENT
    Author URL: http://www.themeforest.net/user/pixinvent
==========================================================================================*/

// Donut chart
// ------------------------------
$(window).on("load", function(){

    Morris.Donut({
        element: 'donut-chart',
        data: [{
            label: "매우 우수",
            value: 25
        }, {
            label: "우수",
            value: 40
        }, {
            label: "보통",
            value: 25
        }, {
            label: "미흡",
            value: 10
        }, {
            label: "매우 미흡",
            value: 10
        }, ],
        resize: true,
        colors: ['#3BAFDA', '#00A5A8', '#AB47BC', '#F6BB42','#DA4453']
    });
});