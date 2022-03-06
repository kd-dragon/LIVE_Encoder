var isMobile = /Android|webOS|iPhone|iPad|iPod|BlackBerry/i.test(navigator.userAgent) ? true : false;
var isIOS = /iPhone|iPad|iPod/i.test(navigator.userAgent) ? true : false;
var isAndroid = /Android|webOS|BlackBerry/i.test(navigator.userAgent) ? true : false;
var isIE = /Netscape|trident|msie/i.test(navigator.userAgent) ? true: false;
var password = "tigenlicense";
var code = "M";

$('head').append('<meta http-equiv="Expires" content="-1">');
$('head').append('<meta http-equiv="pragma" content="no-cache">');
$('head').append('<meta http-equiv="Cache-Control" content="No-Cache">');

document.writeln("<link th:href='@{/player/tgplayer/plugin/css/tg_player.css}' rel='stylesheet'>");
document.writeln("<link th:href='@{/player/tgplayer/plugin/css/tg_player_init.css}' rel='stylesheet'>");
document.writeln("<link th:href='@{/player/tgplayer/plugin/css/marker/tg_markers.css}' rel='stylesheet'>");
document.writeln("<link th:href='@{/player/tgplayer/plugin/css/slider/tg_slider.css}' rel='stylesheet'>");
if(isMobile){
	document.writeln("<link th:href='@{/player/tgplayer/plugin/css/slider/tg_cs_mobile.css}' rel='stylesheet'>");
}else{
	document.writeln("<link th:href='@{/player/tgplayer/plugin/css/slider/tg_cs_web.css}' rel='stylesheet'>");
}

if(isIE) {
	document.writeln("<script type='text/javascript' th:src='@{/player/tgplayer/plugin/js/tg_player_IE.js}'></script>");
} else {
	document.writeln("<script type='text/javascript' th:src='@{/player/tgplayer/plugin/js/tg_player.js}'></script>");
}

document.writeln("<script type='text/javascript' th:src='@{/player/tgplayer/plugin/js/tg_player.js}'></script>");
document.writeln("<script type='text/javascript' th:src='@{/player/tgplayer/plugin/js/tg_player_IE.js}'></script>");

document.writeln("<script type='text/javascript' th:src='@{/player/tgplayer/plugin/js/baseblow.js}'></script>");
document.writeln("<script type='text/javascript' th:src='@{/player/tgplayer/license.js}'></script>");

document.writeln("<script type='text/javascript' th:src='@{/player/tgplayer/plugin/js/hls/tg_hls.js}'></script>");
document.writeln("<script type='text/javascript' th:src='@{/player/tgplayer/plugin/js/loop/tg_loop.js}'></script>");
document.writeln("<script type='text/javascript' th:src='@{/player/tgplayer/plugin/js/marker/tg_markers.js}'></script>");
document.writeln("<script type='text/javascript' th:src='@{/player/tgplayer/plugin/js/slider/tg_slider.js}'></script>");

var tg_player;
var source;
var thumbNail;
var subtitle;
var speed;
var watermark;
var autoPlay;
var live;
var startTime;
var seeking;
var progress;
var pip;
var preview;
var bookmark;
var loop;
var loopEnabled = false;

var seekingPoint = 0; //체크값

var hls = []; var i = 0;
var trackShowVal = false;
var playStartChk = false;

var tg_slider, oLeft, oRight, idx = 0;

var video;

$.fn.createVideo = function(options) {
    var id = this.attr('id');

    source = options.src;
    thumbNail = options.thumb != undefined ? options.thumb : null;
    subtitle = options.subtitle != undefined ? options.subtitle : null;
    speed = options.speed != undefined ? options.speed : [0.8, 1, 1.2, 1.4, 1.6];
    watermark = options.watermark != undefined ? options.watermark : null;
    autoPlay = options.autoPlay != undefined ? options.autoPlay : false;
    live = options.live != undefined ? options.live : false;
    startTime = options.startTime != undefined ? options.startTime : 0;
    seeking = options.seeking != undefined ? options.seeking : true;
    progress = options.progress != undefined ? options.progress : true;
    pip = options.pip != undefined ? options.pip : false;
    preview = options.preview[0] != undefined ? options.preview[0] : null;
    bookmark = options.bookmark != undefined ? options.bookmark : null;
    loop = options.loop[0] != undefined ? options.loop[0] : null;
    fullscreen = options.fullscreen[0] != undefined ? options.fullscreen[0] : null;

//    var seeker = seeking;
    
    if(!progress){
    	$("<style></style>").appendTo("head").html(" .plyr__progress{ pointer-events: none; } ");
    }
    
    if (keyCheck(tg_player_licenseKey)) {
    	$('#' + id).append('<video id="'+options.id+'" crossorigin preload=metadata ></video>');
		
    	
    	
    	
//	if(live) {
		tg_player = new Plyr("#" + options.id, {progress : false, ratio:'16:9', speed: { selected: 1, options: [1] }});
	/*} else {
        tg_player = new Plyr("#"+options.id, {
        	playsinline: true
            ,loop: { active: false }
            ,tooltips: { controls: true, seek: true }      
            ,captions: { active: true, update: true}
        	,speed: { selected: 1, options: speed }
        	,controls: [ 'play-large', 'restart', 'play', 'progress', 'current-time', 'duration', 'mute', 'volume', 'settings', 'airplay', 'fullscreen', 'capture' ]	
            ,listeners: {
            	seek: function customSeekBehavior(e) {
    				var currentTime = tg_player.currentTime;
    				var newTime = _getTargetTime(tg_player, e);
    				
    				if (!seeking) {
    					if(currentTime > seekingPoint){
						    seekingPoint = currentTime;
						}
						if(newTime > seekingPoint){
						    tg_player.currentTime = seekingPoint;
							e.preventDefault();
//							console.log("prevented");
							return false;
						}else{
						    tg_player.currentTime = currentTime;
						}
    				}else{
    					tg_player.currentTime = newTime;
    				}
            	}
            }
           ,previewThumbnails: preview
            ,i18n: {
                qualityBadge: { 2160: '4K', 1440: 'FHD', 1080: 'FHD', 720: 'HD', 576: 'SD', 480: 'SD', 360: 'SD' }
            }
            ,ratio:'16:9'
            ,fullscreen: fullscreen
        });
	}*/
        
        var isHls = false;
	    if(source[0].src.toString().indexOf("m3u8") > 0 ) isHls = true;
        if(!isIOS && isHls){
        	
            tg_player.source = {
        		type: 'video'
        	    ,title: 'TigenSoft html5 video player'    
        	    ,tracks: subtitle
		    ,poster: thumbNail
        	};
            
	    	var video = document.querySelector("#"+options.id);
	    	if(Hls.isSupported()) {
	    	    var hls = new Hls();

	    	    tg_player.poster = thumbNail;
	    	    hls.loadSource(source[0].src);
	    	    hls.attachMedia(video);
	    	    hls.on(Hls.Events.MANIFEST_PARSED,function() {
	    	      //video.play();
	    	    });
	    	  }
	    }else{
	        tg_player.source = {
        		type: 'video'
        	    ,title: 'TigenSoft html5 video player'    
        		,sources: source    
        		,poster: thumbNail
        		,tracks: subtitle
        	};
	    }
        
		if (watermark != null){
			if ("0" == watermark[0].pos) {//좌상
				$('.plyr__video-wrapper').append("<div class='watermark'><img src='"+watermark[0].url+"' style='position: absolute; margin:10px;top: 0; left: 0; display: inline; z-index: 2000; opacity: 0.5; width: 200px'/></div>");
			} else if ("1" == watermark[0].pos) {//좌하
				$('.plyr__video-wrapper').append("<div class='watermark'><img src='"+watermark[0].url+"' style='position: absolute; margin:10px;bottom: 0; left: 0; display: inline; z-index: 2000; opacity: 0.5; width: 200px'/></div>");
			} else if ("2" == watermark[0].pos) {//우상
				$('.plyr__video-wrapper').append("<div class='watermark'><img src='"+watermark[0].url+"' style='position: absolute; margin:10px;top: 0; right: 0; display: inline; z-index: 2000; opacity: 0.5; width: 200px'/></div>");
			} else if ("3" == watermark[0].pos) {//우하
				$('.plyr__video-wrapper').append("<div class='watermark'><img src='"+watermark[0].url+"' style='position: absolute; margin:10px;bottom: 0; right: 0; display: inline; z-index: 2000; opacity: 0.5; width: 200px'/></div>");
			}
			
		}
		tg_player.on('play', function(event) {
			if(!playStartChk){
				playStartChk = true;
				playStart();
			}
		});
		
		tg_player.on('pause', function(event) {
			$('#currentTime').val(tg_player.currentTime);
			playEnd();
		});
		
		//종료
		tg_player.on('ended',function(e){
			playEnd();
		});
		
        tg_player.on("loadeddata", function() {
        	video = document.querySelector("video");
        	idx++;
       		tg_player.currentTime = startTime;
            if(autoPlay){
            	tg_player.volume = 0;//muted
            	tg_player.play();//autoplay
            }
            $('#duration').val(tg_player.duration);

            if(loop.enable){
            	loopEnabled = true;
            	loopstart = parseFloat(loop.start).toFixed(0);
            	loopend = parseFloat(loop.end).toFixed(0);
            	$('#loopStat').val(loopEnabled);
	            if(idx==2){
	            	$('#loopStart').val(loop.start);
	            	$('#loopEnd').val(loop.end);
	            	$('.plyr__progress__container').prepend("<div id='sliderEl' class='sliderEl' ><div id='tg_slider' class='fly wrap'></div></div>");
	            	$('.plyr__controls').append("<div id='loopEl' class='loopEl' onclick='loopClick()'>loop on</div>");
	            	tg_slider = new Slider(document.getElementById('tg_slider'), {
	                    isDate: false,
	                    min: "0",
	                    max: parseFloat(tg_player.duration).toFixed(0),
	                    start: parseFloat(loop.start).toFixed(0),
	                    end: parseFloat(loop.end).toFixed(0),
	                    overlap: false
	                });
	            	oLeft = 0, oRight = tg_player.duration;
	            	tg_slider.subscribe('stop', function(data) {
	            		if(oLeft!=data.left){
	            			tg_player.currentTime = data.left;  	
	            			oLeft = data.left;  	
	            		}else if(oRight!=data.right){
	            			tg_player.currentTime = data.right;  	  
	            			oRight = data.right;
	            		}
	            		
	            		$('#loopStart').val(parseFloat(data.left).toFixed(0));
	            		$('#loopEnd').val(parseFloat(data.right).toFixed(0));
	            		
	            		$('#video').setLoop([{ start: data.left, end: data.right }]);
	            	});
	            }
	            
	            tg_player.on('timeupdate', function() {
        		if(tg_player.currentTime > loopend && loopEnabled){
        			tg_player.currentTime = loopstart*1;
        		}
        		if(tg_player.currentTime < loopstart && loopEnabled){
        			tg_player.currentTime = loopstart*1;
        		}
        	});
            }
            
            var trackDef = -1;
            if(subtitle!=null){
            	for ( var i = 0; i < subtitle.length; i++) {
            		if(subtitle[i].default!=undefined) trackDef = i;
            	}
            	tg_player.currentTrack = trackDef; 
            }
            
        	
           
        });
        
        var oldAttr, oldTop;
        oldAttr = $('#' + id).attr("style");
        oldTop = $('#' + id).attr("top");
        if(pip){
	        $(window).scroll(function() {
	            var position = $(window).scrollTop();
	        	if ($(this).scrollTop() >= $(window).height() - (240 + 10)) {
	        		
	        		$('#' + id).attr("style", "position:absolute;right:10px;z-index:9999999999 !important;width: 424px;height:240px");
	        		$('#' + id).css("top", $(window).height()-(240+10));
	        		
	        		var currentPosition = parseInt($('#' + id).css("top")); 
	        		
	        		$('#' + id).stop(true, true).animate({"top": position + currentPosition + "px"}, 2000);
	            } else {
	            	$('#' + id).attr("style", oldAttr);
	            	$('#' + id).css("top", oldTop);	
	        		
	            }
	        });
        }
        if(bookmark!=null){
	        $('#'+options.id).marker({ markers : bookmark, el: tg_player});
        }
        
        /*tg_player.on('ratechange', function() {
			playRateRtn(tg_player.speed);
		});*/
        
       /* tg_player.on("captionsenabled", function action(event) {
        	trackViewRtn(tg_player.currentTrack);
        });
        tg_player.on("captionsdisabled", function action(event) {
        	trackViewRtn(tg_player.currentTrack);
        });*/
	} else {

	}
    
};

function keyCheck(license) {
    var bf = new Blowfish(password);

    license = bf.base64Decode(license);

    var res = bf.decrypt(license);
    res = bf.trimZeros(res);

    var str = res.split("::");

    var url = window.location.protocol + "//" + window.location.host;

    var protocol = "";
    if (url.indexOf("http://") >= 0)
        protocol = "http://";
    else if (url.indexOf("https://") >= 0)
        protocol = "https://";
    else
        protocol = "file://";

    var domain = url.split(protocol)[1];

    domain = domain.split(":")[0];

    var nowDate = (new Date()).yyyymmdd();

   /* if (str[0].indexOf(domain) < 0) {
    	if(str[0].indexOf("*") > -1){
    		if (str[0].indexOf(domain.split(".")[1]+"."+domain.split(".")[2]) > -1) {
    			return true;
    		}
    	}
        console.log("domain wrong");
        alert("license wrong");
        return false;
    } else if (compareTo(str[1], nowDate) < 0) {
        console.log("date over");
        alert("license wrong");
        return false;
    } else if (str[2].indexOf(code) < 0) {
        console.log("pcode wrong");
        alert("license wrong");
        return false;
    }*/
    console.log("license ok");
    return true;
}

Date.prototype.yyyymmdd = function() {
    var yyyy = this.getFullYear().toString();
    var mm = (this.getMonth() + 1).toString();
    var dd = this.getDate().toString();

    return yyyy + (mm[1] ? mm : '0' + mm[0]) + (dd[1] ? dd : '0' + dd[0]);
}

function compareTo(value, anotherString) {
    var len1 = value.length;
    var len2 = anotherString.length;
    var lim = Math.min(len1, len2);
    var v1 = value;
    var v2 = anotherString;

    var k = 0;
    while (k < lim) {
        var c1 = v1[k];
        var c2 = v2[k];
        if (c1 != c2) {
            return c1 - c2;
        }
        k++;
    }
    return len1 - len2;
}

function _getTargetTime(player, input) { //seek control
	if ( typeof input === "object" && (input.type === "input" || input.type === "change") ) {
		return input.target.value / input.target.max * player.media.duration;
	} else {
    	return Number(input);
	}
}


$.fn.vodCurrentTime = function() {
	return tg_player.currentTime;
};

$.fn.bookmarkAdd = function(options) {
	tg_player.markers.add(options); 
};

$.fn.bookmarkRemove = function(options) {
	tg_player.markers.remove([options-1]);
};

$.fn.vodPlay = function(options) {
	if(!tg_player.playing){
		tg_player.play();
	}else{	
		tg_player.pause();
	}
};

$.fn.playRate = function(options) {
	tg_player.speed = options;
};

$.fn.trackView = function(options) {
	if(tg_player.currentTrack==-1){
		tg_player.currentTrack = options;
	}else{
		tg_player.currentTrack = -1; 
	}
};

$.fn.movePlaytime = function(time) {
	console.log("북마크로 이동  :: "+time);

	tg_player.on("ready", function () {
		tg_player.currentTime = time;
		tg_player.play();
	})
};


$.fn.setLoop = function(options) {
	//loopEnabled = true;
	loopstart = options[0].start;
	loopend = options[0].end;
	//$('#loopStat').val(loopEnabled);
	//$("#loopEl").html("loop on");
	
	tg_player.on('timeupdate', function() {
		if(tg_player.currentTime > loopend && loopEnabled){
			tg_player.currentTime = loopstart*1;
		}
		if(tg_player.currentTime < loopstart && loopEnabled){
			tg_player.currentTime = loopstart*1;
		}
	});
};

$.fn.ableLoop = function(options) {
	if($("#sliderEl").length > 0) {
		$("#sliderEl").remove();
		$("#loopEl").remove();
		loopEnabled = false;
		$('#loopStat').val(loopEnabled);
	}else{
		$('#loopStat').val(loopEnabled);
    	$('#loopStart').val(0);
    	$('#loopEnd').val(tg_player.duration);
    	
    	$('.plyr__progress__container').prepend("<div id='sliderEl' class='sliderEl' ><div id='tg_slider' class='fly wrap'></div></div>");
    	$('.plyr__controls').append("<div id='loopEl' class='loopEl' onclick='loopClick()'>loop off</div>");
    	
    	tg_slider = new Slider(document.getElementById('tg_slider'), {
            isDate: false,
            min: "0",
            max: parseFloat(tg_player.duration).toFixed(0),
            start: 0,
            end: parseFloat(tg_player.duration).toFixed(0),
            overlap: false
        });
    	oLeft = 0, oRight = tg_player.duration;
    	tg_slider.subscribe('stop', function(data) {
    		if(oLeft!=data.left){
    			tg_player.currentTime = data.left;  	
    			oLeft = data.left;  	
    		}else if(oRight!=data.right){
    			tg_player.currentTime = data.right;  	  
    			oRight = data.right;
    		}
    		
    		$('#loopStart').val(parseFloat(data.left).toFixed(0));
    		$('#loopEnd').val(parseFloat(data.right).toFixed(0));
    		
    		$('#video').setLoop([{ start: data.left, end: data.right }]);
    	});
	}
};

$.fn.offLoop = function() {
	/*loopEnabled = false;
	$('#loopStat').val(loopEnabled);
	$("#loopEl").html("loop off");*/
	if(!loopEnabled){
		loopEnabled = true;
		$('#loopStat').val(loopEnabled);
		$("#loopEl").html("loop on");
	}else{
		loopEnabled = false;
		$('#loopStat').val(loopEnabled);
		$("#loopEl").html("loop off");
	}
};

function loopClick(){
	if(!loopEnabled){
		loopEnabled = true;
		$('#loopStat').val(loopEnabled);
		$("#loopEl").html("loop on");
	}else{
		loopEnabled = false;
		$('#loopStat').val(loopEnabled);
		$("#loopEl").html("loop off");
	}
}

function getContextPath() {
	
	var isUseable = false;
	var path = "";
	if(isUseable) {
		var hostIndex = location.href.indexOf( location.host ) + location.host.length;
		path = location.href.substring( hostIndex, location.href.indexOf('/', hostIndex + 1) );
	}
	
	return path;
};



function loadHLS(source) {
    video.pause();
    hls[i] = new Hls(); 
	hls[i].loadSource(source);
	hls[i].attachMedia(video);
	video.play();
    i++;
}
