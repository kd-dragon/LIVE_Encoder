/*=========================================================================================
    File Name: dashboard-fitness.js
    Description: Dahsboard fitness js
    ----------------------------------------------------------------------------------------
    Item Name: Robust - Responsive Admin Theme
    Version: 1.2
    Author: GeeksLabs
    Author URL: http://www.themeforest.net/user/geekslabs
==========================================================================================*/
(function(window, document, $) {
    'use strict';
    // Checkbox & Radio 1
    $('.icheck-activity').iCheck({
        checkboxClass: 'icheckbox_square-blue',
        radioClass: 'iradio_square-blue',
    });

    $("#friends-activity, #daily-activity").perfectScrollbar({
        wheelPropagation: true
    });
    /*****************************************************
    *               Knob Card Statistics              *
    *****************************************************/
    var rtl = false;
    if($('html').data('textdirection') == 'rtl')
        rtl = true;
    $(".knob").knob({
        rtl:rtl,
        draw: function() {
            var ele = this.$;
            var style = ele.attr('style');
            var fontSize = parseInt(ele.css('font-size'), 10);
            var updateFontSize = Math.ceil(fontSize * 1.65);
            style = style.replace("bold", "normal");
            style = style + "font-size: " +updateFontSize+"px;";
            var icon = ele.attr('data-knob-icon');
            ele.hide();
            $('<i class="knob-center-icon '+icon+'"></i>').insertAfter(ele).attr('style',style);

            // "tron" case
            if (this.$.data('skin') == 'tron') {

                this.cursorExt = 0.3;

                var a = this.arc(this.cv), // Arc
                    pa, // Previous arc
                    r = 1;

                this.g.lineWidth = this.lineWidth;

                if (this.o.displayPrevious) {
                    pa = this.arc(this.v);
                    this.g.beginPath();
                    this.g.strokeStyle = this.pColor;
                    this.g.arc(this.xy, this.xy, this.radius - this.lineWidth, pa.s, pa.e, pa.d);
                    this.g.stroke();
                }

                this.g.beginPath();
                this.g.strokeStyle = r ? this.o.fgColor : this.fgColor;
                this.g.arc(this.xy, this.xy, this.radius - this.lineWidth, a.s, a.e, a.d);
                this.g.stroke();

                this.g.lineWidth = 2;
                this.g.beginPath();
                this.g.strokeStyle = this.o.fgColor;
                this.g.arc(this.xy, this.xy, this.radius - this.lineWidth + 1 + this.lineWidth * 2 / 3, 0, 2 * Math.PI, false);
                this.g.stroke();

                return false;
            }
        }
    });

    var weeklyActivityChart = Morris.Line({
        element: 'weekly-activity-chart',
        data: [{
            "day": Date.parse('2016-12-05'),
            "단문": 1000,
            "장문": 40,
            "이미지": 62,
            "음성": 62
        }, {
            "day": Date.parse('2016-12-06'),
            "단문": 150,
            "장문": 200,
            "이미지": 120,
            "음성": 55
        }, {
            "day": Date.parse('2016-12-07'),
            "단문": 200,
            "장문": 105,
            "이미지": 70,
            "음성": 68
        }, {
            "day": Date.parse('2016-12-08'),
            "단문": 125,
            "장문": 150,
            "이미지": 75,
            "음성": 55
        }, {
            "day": Date.parse('2016-12-09'),
            "단문": 150,
            "장문": 275,
            "이미지": 100,
            "음성": 66
        }, {
            "day": Date.parse('2016-12-10'),
            "단문": 200,
            "장문": 325,
            "이미지": 80,
            "음성": 57
        }, {
            "day": Date.parse('2016-12-11'),
            "단문": 260,
            "장문": 130,
            "이미지": 90,
            "음성": 62
        }],
        xkey: 'day',
        xLabels:['day'],
        ykeys: ['단문', '장문', '이미지', '음성'],
        labels: ['단문', '장문', '이미지', '음성'],
        resize: true,
        smooth: true,
        pointSize: 4,
        pointStrokeColors:['#37BC9B', '#43A047', '#967ADC', '#E91E63'],
        gridLineColor: '#e3e3e3',
        behaveLikeLine: true,
        numLines: 6,
        gridtextSize: 14,
        lineWidth: 1.3,
        hideHover: 'auto',
        lineColors: ['#37BC9B', '#43A047', '#967ADC', '#E91E63'],
        xLabelFormat:
                    function(x){var day=x.getDay();
                    var days=["일","월","화","수","목","금","토"];
                    return days[day];}
    });

    /************************************
    *       Top Selling Categories      *
    ************************************/

    // Set paths
    // ------------------------------

    require.config({
        paths: {
            echarts: '/app-assets/vendors/js/charts/echarts'
        }
    });


    // Configuration
    // ------------------------------

    require(
        [
            'echarts',
            'echarts/chart/pie',
            'echarts/chart/funnel'
        ],


    // Charts setup
    function (ec) {

        // Initialize chart
        // ------------------------------
        var topCategoryChart = ec.init(document.getElementById('activity-division'));

        // Chart Options
        // ------------------------------
        var topCategoryChartOptions = {

            // Add title
            title: {
                text: '주간 메시지 전송 비율',
                subtext: '',
                x: 'center',
                textStyle: {
    				color: '#333333'
                },
                subtextStyle: {
    				color: '#333333'
                }
            },

            // Add custom colors
            color: ['#37BC9B', '#43A047', '#967ADC', '#E91E63'/*, '#818a91'*/],

            // Enable drag recalculate
            calculable: true,

            // Add series
            series: [
                {
                    name: 'Top Categories',
                    type: 'pie',
                    radius: ['50%', '70%'],
                    center: ['50%', '60%'],
                    itemStyle: {
                        normal: {
                            label: {
                                show: true,
    							textStyle: {
    								color: '#333333'
    							}
                            },
                            labelLine: {
                                show: true,
    							lineStyle: {
    								color: '#333333'
    							}
                            }
                        },
                        emphasis: {
                            label: {
                                show: true,
                                formatter: '{b}' + '\n\n' + '{c} ({d}%)',
                                position: 'center',
                                textStyle: {
                                    fontSize: '12',
                                    fontWeight: '500'
                                }
                            }
                        }
                    },

					  
					  
					  
                    data: [
                        {value: 335, name: '단문 메시지'},
                        {value: 618, name: '장문 메시지'},
                        {value: 235, name: '멀티 메시지'},
                        {value: 556, name: '음성 메시지'},
                        /*{value: 100, name: 'Other'}*/
                    ]
                }
            ]
        };

        // Apply options
        // ------------------------------

        topCategoryChart.setOption(topCategoryChartOptions);

        // Resize chart
        // ------------------------------

        $(function () {

            // Resize chart on menu width change and window resize
            $(window).on('resize', resize);
            $(".menu-toggle").on('click', resize);

            // Resize function
            function resize() {
                setTimeout(function() {

                    // Resize chart
                    topCategoryChart.resize();
                }, 200);
            }
        });
        }
    );

    /************************************************************
    *               Social Cards Content Slider                 *
    ************************************************************/
    // RTL Support
    var rtl = false;
    if($('html').data('textdirection') == 'rtl'){
        rtl = true;
    }
    if(rtl === true)
        $(".tweet-slider").attr('dir', 'rtl');
    if(rtl === true)
        $(".fb-post-slider").attr('dir', 'rtl');

    // Tweet Slider
    $(".tweet-slider").unslider({
        autoplay: true,
        arrows: false,
        nav: false,
        infinite: true
    });

    // FB Post Slider
    $(".fb-post-slider").unslider({
        autoplay: true,
        delay: 3500,
        arrows: false,
        nav: false,
        infinite: true
    });

})(window, document, jQuery);


$(window).on("load", function(){

    /************************************************************
    *                           Maps Route                      *
    ************************************************************/

    routes_map = new GMaps({
        div: '#routes',
        lat: 40.673266,
        lng: -73.943119,
        scrollwheel: false,
        styles: [{"featureType":"landscape","stylers":[{"hue":"#FFBB00"},{"saturation":43.400000000000006},{"lightness":37.599999999999994},{"gamma":1}]},{"featureType":"road.highway","stylers":[{"hue":"#FFC200"},{"saturation":-61.8},{"lightness":45.599999999999994},{"gamma":1}]},{"featureType":"road.arterial","stylers":[{"hue":"#FF0300"},{"saturation":-100},{"lightness":51.19999999999999},{"gamma":1}]},{"featureType":"road.local","stylers":[{"hue":"#FF0300"},{"saturation":-100},{"lightness":52},{"gamma":1}]},{"featureType":"water","stylers":[{"hue":"#0078FF"},{"saturation":-13.200000000000003},{"lightness":2.4000000000000057},{"gamma":1}]},{"featureType":"poi","stylers":[{"hue":"#00FF6A"},{"saturation":-1.0989010989011234},{"lightness":11.200000000000017},{"gamma":1}]}]
    });

    routes_map.drawRoute({
        origin: [40.674072, -73.944525],
        destination: [40.670084, -73.950436],
        travelMode: 'walking',
        strokeColor: '#37BC9B',
        strokeOpacity: 0.6,
        strokeWeight: 6
    });
});
