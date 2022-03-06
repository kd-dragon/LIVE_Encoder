var isMobile = /Android|webOS|iPhone|iPad|iPod|BlackBerry/i.test(navigator.userAgent) ? true : false;
var isIOS = /iPhone|iPad|iPod/i.test(navigator.userAgent) ? true : false;
var isAndroid = /Android|webOS|BlackBerry/i.test(navigator.userAgent) ? true : false;
var isIPhone = /(iPhone|iPod)/gi.test(navigator.platform) ? true : false;
var isIE = /Netscape|trident|msie/i.test(navigator.userAgent) ? true: false;
var password = "tigenlicense";
var code = "M";
var seekingPoint = 0;
var autoQualInt = 1;
var ChangeTime = 0;

$('head').append('<meta http-equiv="Expires" content="-1">');
$('head').append('<meta http-equiv="pragma" content="no-cache">');
$('head').append('<meta http-equiv="Cache-Control" content="No-Cache">');

document.writeln("<link href='"+getContextPath()+"/resources/player/tgplayer/plugin/css/tg_player.css' rel='stylesheet'>");
document.writeln("<link href='"+getContextPath()+"/resources/player/tgplayer/plugin/css/tg_player_init.css' rel='stylesheet'>");
document.writeln("<link href='"+getContextPath()+"/resources/player/tgplayer/plugin/css/marker/tg_markers.css' rel='stylesheet'>");
document.writeln("<link href='"+getContextPath()+"/resources/player/tgplayer/plugin/css/slider/tg_slider.css' rel='stylesheet'>");
if(isMobile){
	document.writeln("<link href='"+getContextPath()+"/resources/player/tgplayer/plugin/css/slider/tg_cs_mobile.css' rel='stylesheet'>");
}else{
	document.writeln("<link href='"+getContextPath()+"/resources/player/tgplayer/plugin/css/slider/tg_cs_web.css' rel='stylesheet'>");
}

document.writeln("<link href='"+getContextPath()+"/resources/player/tgplayer/plugin/css/vr/tg_vr.css' rel='stylesheet'>");

if(isIE){
	document.writeln("<script type='text/javascript' src='"+getContextPath()+"/resources/player/tgplayer/plugin/js/tg_player_IE.js'></script>");	
}else{
	document.writeln("<script type='text/javascript' src='"+getContextPath()+"/resources/player/tgplayer/plugin/js/tg_player.js'></script>");
}


document.writeln("<script type='text/javascript' src='"+getContextPath()+"/resources/player/tgplayer/plugin/js/baseblow.js'></script>");
document.writeln("<script type='text/javascript' src='"+getContextPath()+"/resources/player/tgplayer/license.js'></script>");

document.writeln("<script type='text/javascript' src='"+getContextPath()+"/resources/player/tgplayer/plugin/js/hls/tg_hls.js'></script>");
document.writeln("<script type='text/javascript' src='"+getContextPath()+"/resources/player/tgplayer/plugin/js/loop/tg_loop.js'></script>");
document.writeln("<script type='text/javascript' src='"+getContextPath()+"/resources/player/tgplayer/plugin/js/marker/tg_markers.js'></script>");
document.writeln("<script type='text/javascript' src='"+getContextPath()+"/resources/player/tgplayer/plugin/js/slider/tg_slider.js'></script>");

document.writeln("<script type='text/javascript' src='"+getContextPath()+"/resources/player/tgplayer/plugin/js/vr/tg_vr.js'></script>");

var tg_player;
var source;
var thumbNail;
var subtitle;
var speed;
var watermark;
var autoPlay;
var startTime;
var seeking;
var progress;
var pip;
var preview;
var bookmark;
var loop;
var loopEnabled = false;

var trackShowVal = false;

var tg_slider, oLeft, oRight, idx = 0;
var hls = []; var i = 0;

var video;



$.fn.createVideo = function(options) {
    var id = this.attr('id');
    
    source = options.src;
    thumbNail = options.thumb != undefined ? options.thumb : null;
    subtitle = options.subtitle != undefined ? options.subtitle : null;
    speed = options.speed != undefined ? options.speed : [0.8, 1, 1.2, 1.4, 1.6];
    watermark = options.watermark != undefined ? options.watermark : null;
    autoPlay = options.autoPlay != undefined ? options.autoPlay : false;
    startTime = options.startTime != undefined ? options.startTime : 0;
    seeking = options.seeking != undefined ? options.seeking : true;
    progress = options.progress != undefined ? options.progress : true;
    pip = options.pip != undefined ? options.pip : false;
    preview = options.preview[0] != undefined ? options.preview[0] : null;
    bookmark = options.bookmark != undefined ? options.bookmark : null;
    loop = options.loop[0] != undefined ? options.loop[0] : null;
    fullscreen = options.fullscreen[0] != undefined ? options.fullscreen[0] : null;
    
    if(!progress){
    	$("<style></style>").appendTo("head").html(" .plyr__progress{ pointer-events: none; } ");
    }
    
    	$('#' + id).append('<video id="'+options.id+'" preload=metadata ></video>');
    	

	var controlsFlag;
	if(isIPhone){
		controlsFlag = [ 'play-large', 'restart', 'play', 'progress', 'current-time', 'mute', 'volume', 'settings', 'fullscreen'];
	}else {
		controlsFlag = [ 'play-large', 'restart', 'play', 'progress', 'current-time', 'duration', 'mute', 'volume', 'settings', 'fullscreen']
	}
	var isHls = false;
    if(source[0].src.toString().indexOf("m3u8") > 0 ) isHls = true;
    var playerOptions = {
    	playsinline: true
        ,loop: { active: false }
        ,tooltips: { controls: true, seek: true }      
        ,captions: { active: true, update: true}
    	,speed: { selected: 1, options: speed }
    	,controls: controlsFlag	
    	,invertTime : false
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
						console.log("prevented");
						return false;
					}else{
					    tg_player.currentTime = newTime;
					}
				}else{
					tg_player.currentTime = newTime;
				}
        	}
        }
        ,previewThumbnails: preview
        ,i18n: {
            qualityBadge: { 2160: '4K', 1440: 'FHD', 1080: 'FHD', 720: 'HD', 640: 'SD', 576: 'SD', 480: 'SD', 360: 'SD', 320: 'SD', 240 : 'SD' , 1 : "auto" }
        }
        ,ratio:"16:9"
        ,fullscreen : fullscreen
    };
    
    var video = document.querySelector("#"+options.id);
    // hls 와 직링크 분기처리
    if(isHls){
    	if(!isIOS){
	    	if(Hls.isSupported()) {
	    	    var hls = new Hls();
	    	    /*tg_player.poster = thumbNail;*/
				hls.loadSource(source[0].src);
				
				hls.on(Hls.Events.ERROR, function (event, data) {
				  if (data.fatal) {
				    switch (data.type) {
				      case Hls.ErrorTypes.NETWORK_ERROR:
				        // try to recover network error
				        console.log('fatal network error encountered, try to recover');
				        hls.startLoad();
				        break;
				      case Hls.ErrorTypes.MEDIA_ERROR:
				        console.log('fatal media error encountered, try to recover');
				        hls.recoverMediaError();
				        break;
				      default:
				        // cannot recover
				        hls.destroy();
				        break;
				    }
				  }
				}); 
				
	    	    hls.on(Hls.Events.MANIFEST_PARSED,function(event,data) {
	    	    	var avalQualList = [];
    	    		var sourceList = [];	
					
					// 자동 품질 선택 메뉴 추가
					avalQualList.push(autoQualInt);
					var _sourceObj = {};
					_sourceObj.src = new Array(source[0].src);
					_sourceObj.size = autoQualInt;
					_sourceObj.type = 'video/mp4';
					sourceList.push(_sourceObj);

	    	    	hls.levels.map(function(v){
						avalQualList.push(v.height);
						var sourceObj = {};
						sourceObj.src = v.url;
						sourceObj.size = v.height;
						sourceObj.type = 'video/mp4';
						sourceList.push(sourceObj);
	    	    	});

					playerOptions.quality = {
						default : avalQualList[0],
						options : avalQualList,
						forced : true,
	    	    	}
					
	    	    	tg_player = new Plyr("#"+options.id,playerOptions);
	    	    	tg_player.poster = thumbNail;
	    	    	
	    	    	tg_player.source = {
	    	        		type: 'video'
	    	        	    ,title: 'TigenSoft html5 video player' 
	    	        		,sources: sourceList
	    	        		,tracks: subtitle
	    	        };
	    	    	
	    	    	
					tg_player.on('qualitychange',function(e){
						// 품질변경시, 현재시간은 가져올수 없기 때문에, innerText로 직접 가져와 초 단위로 처리.
						if(isIE) {
							var txtArray = e.target.innerText.replace(/^\s+|\s+$/gm,'\n').split("\n").filter(function(item){
								return item !== null && item !== undefined && item !== '';
							});
							console.log(txtArray[3]);
							ChangeTime = TimeValid(txtArray[3]);	
						} else {
							console.log(e.target.innerText.split("\n")[2]);
							ChangeTime = TimeValid(e.target.innerText.split("\n")[2]);							
						}

						var newQuality = e.detail.quality;
						var _lvlIndex = 0;
						window.hls.levels.forEach(function(level, levelIndex){
							if (level.height == newQuality) {
							_lvlIndex = levelIndex + 1;
							window.hls.currentLevel = levelIndex;
							}
						})
						loadHLS($('source')[_lvlIndex].src);
					});
					  	        
					__initialPlayer__(tg_player,"HLS");

	    	    });

    	    	hls.attachMedia(video);
		    	window.hls = hls;		    	  
	    	  }
	    }else{
	    	
	    }
	    
    }else{
    	tg_player_pro = new Plyr("#"+options.id,playerOptions);
    	window.tg_player = tg_player_pro;
    	tg_player_pro.source = {
    		type: 'video'
			,title: 'TigenSoft html5 video player'    
	    	,sources: source    
			,poster: thumbNail
			,tracks: subtitle
    	};

    }
    
    function updateQuality(newQuality) {
    	console.log("updateQuality");
        window.hls.levels.forEach(function(level, levelIndex){
          if (level.height === newQuality) {
            window.hls.currentLevel = levelIndex;
          }
        });
    }
	
};

function loadHLS(source) {
	hls[i] = new Hls(); 
	hls[i].loadSource(source);
	hls[i].attachMedia(video);
	// video.play();
	i++;
}


function __initialPlayer__(tgplayer,opt){

	tg_player.on('play', function(e){
		// console.log("play");
		// if(!isIOS && isHls){
		//	  hls.startLoad();
		// }
	});


	if (watermark != null){
	  var xPos = 0;
	  var yPos = 0;
	  
	  if ("0" == watermark[0].pos) {//좌상
		  xPos = 0;
		  yPos = 0;
	  } else if ("1" == watermark[0].pos) {//좌하
		  xPos = 0;
		  yPos = 100;
	  } else if ("2" == watermark[0].pos) {//우상
		  xPos = 100;
		  yPos = 0;
	  } else if ("3" == watermark[0].pos) {//우하
		  xPos = 100;
		  yPos = 100;
	  }
	  $('.plyr__video-wrapper').append("<div class='watermark'><img src='"+watermark[0].url+"' style='position: absolute; margin:10px;top: "+yPos+"; left: "+xPos+"; display: inline; z-index: 20; opacity: 0.5; width: 200px'/></div>");
  }
  
  tg_player.on('pause', function(event) {
	  //event.preventDefault();
	  $('#currentTime').val(tg_player.currentTime);
  });
  
  tg_player.on('enterfullscreen', function(event) { 
	  $('.plyr__video-wrapper').attr("style","");
  });
  

  var loadFirst = true;
  tg_player.on("loadeddata", function() {
	  video = document.querySelector("video");
	  if(opt == 'HLS'){
		  if(loadFirst){
			  loadHLS(source[0].src);
			  loadFirst = !loadFirst;
		  }
	  }
	  idx++;
	  tg_player.currentTime = startTime;
	  if(ChangeTime != 0){
		  tg_player.currentTime = ChangeTime;
	  }
		 
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
	  }
	  
	  var trackDef = -1;
	  if(subtitle!=null){
		  for ( var i = 0; i < subtitle.length; i++) {
			  if(subtitle[i].default!=undefined) trackDef = i;
		  }
		  tg_player.currentTrack = trackDef; 
	  }
	  
	  tg_player.on('timeupdate', function(e) {
		  if(loop.enable){
			  if(tg_player.currentTime > loopend && loopEnabled){
				  tg_player.currentTime = loopstart*1;
			  }
			  if(tg_player.currentTime < loopstart && loopEnabled){
				  tg_player.currentTime = loopstart*1;
			  }
		  }
	  });
	 
  });
  
  var oldAttr, oldTop, oldWidth, oldHeight;
  oldAttr = $('#' + id).attr("style");
  oldTop = $('#' + id).attr("top");
  
  
  if(pip){
	  $(window).scroll(function() {
		  var position = $(window).scrollTop();
		  if(oldWidth > $('#' + id).attr('width'))
		  oldWidth = $('#' + id).width();
		  oldHeight = $('#' + id).height();
		  if ($(this).scrollTop() >= $(window).height() - (240 + 10))  {
			  if( $("#player__blackContainer").length == 0){
				  $('#' + id).wrap('<div id="player__blackContainer"></div>');
				  $('#player__blackContainer').css({
					  "width" : oldWidth +'px',
					  "height" : oldHeight +'px',
					  "background-color" : "black"
				  });	
			  }
			  $('#' + id).attr("style", "position:absolute;right:10px;z-index:9999999999 !important;width: 424px;height:240px");
			  $('#' + id).css("top", $(window).height()-(240+10));
			  
			  var currentPosition = parseInt($('#' + id).css("top")); 
			  
			  $('#' + id).stop(true, true).animate({"top": position + currentPosition + "px"}, 2000);
		  } else {
			  if(typeof oldAttr == 'undefined'){
				  oldAttr = '';
			  }
			  if(typeof oldTop == 'undefined'){
				  oldTop = '';
			  }
			  
			  $('#' + id).attr("style", oldAttr);
			  $('#' + id).css("top", oldTop);
			  $('#player__blackContainer').css({
				  "height" : "",
				  "width" : "",
				  "background-color" : "black"
			  });       		
		  }
	  });
  }
  
  if(bookmark!=null){
	  $('#'+options.id).marker({ markers : bookmark, el: tg_player});
  }
  
  tg_player.on('ratechange', function() {
	  playRateRtn(tg_player.speed);
  });
  
  tg_player.on("captionsenabled", function action(event) {
	  trackViewRtn(tg_player.currentTrack);
  });
  
  tg_player.on("captionsdisabled", function action(event) {
	  trackViewRtn(tg_player.currentTrack);
  });
  
  //tg_player.vr({projection: '360'});
  window.tg_player = tg_player;
  this.tg_player = tg_player;

}


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

    if (str[0].indexOf(domain) < 0 && str[0].indexOf("0.0.0.0") != 0 ) {
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
    }
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
	console.log("_getTargetTime");
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
	loopEnabled = false;
	$('#loopStat').val(loopEnabled);
	$("#loopEl").html("loop off");
};

$.fn.onLoop = function() {
	loopEnabled = true;
	$('#loopStat').val(loopEnabled);
	$("#loopEl").html("loop on");
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

function fullscreen(){
	$('#video').fullscreen();
}

$.fn.fullscreen = function() {
	tg_player.fullscreen.toggle();	
}

$.fn.setCurrentTime = function(t) {
	tg_player.currentTime = t;
}

Date.prototype.yyyymmdd = function() {
    var yyyy = this.getFullYear().toString();
    var mm = (this.getMonth() + 1).toString();
    var dd = this.getDate().toString();

    return yyyy + (mm[1] ? mm : '0' + mm[0]) + (dd[1] ? dd : '0' + dd[0]);
}

Date.prototype.hhmmss = function() {
	var hh = this.getHours();
	var mm = this.getMinutes();
	var ss = this.getSeconds();

	return [(hh>9 ? '' : '0') + hh, (mm>9 ? '' : '0') + mm, (ss>9 ? '' : '0') + ss,].join('');
};

Date.prototype.yyyymmddhhmmss = function() {
	return this.yyyymmdd() + this.hhmmss();
};

function TimeValid(st){
	var time = st.split(":");
	var resultTime = 0;
	if(time.length == 2){
		resultTime = (time[0] * 60) + (time[1] * 1);
	}else if(time.length == 3){
		resultTime = (time[0] * 60 * 60) + (time[1] * 60) + (time[2] * 1);
	}
	return resultTime;
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