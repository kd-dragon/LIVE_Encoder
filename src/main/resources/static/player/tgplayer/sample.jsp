<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!doctype html>
<html>
<head>
<title>TG video player PIP player Example</title>

<script src="${pageContext.request.contextPath}/player/tgplayer/plugin/js/jquery-3.4.1.min.js"></script>
<script src="${pageContext.request.contextPath}/player/tgplayer/plugin/js/jquery-ui.min.js"></script>

</head>
<style>
body {
	height: 3000px;
	font-family: Arial, sans-serif;
}

</style>
<body>
	<!-- 기본 플레이어 영역 -->
	<div id="videoEl" style="width:852px;height:480px;">
		
	</div>
	<br/><br/><br/><br/><br/><br/>
	영상 총 길이 : <input id="duration" type="text" size="20" />	영상 재생 시간 : <input id="currentTime" type="text" size="20" /><br/>
	<!-- 구간반복 상태 : <input id="loopStat" type="text" size="20" /> 구간반복 시작 시간 : <input id="loopStart" type="text" size="20" /> 구간반복 종료 시간 : <input id="loopEnd" type="text" size="20" />  <br/> -->
	
	<input type="button" value="북마크 추가" onclick="javascript:bookmarkAdd();"></input>
	<input type="button" value="북마크 삭제" onclick="javascript:bookmarkRemove();"></input>
	<input type="button" value="영상 재생/정지" onclick="javascript:vodPlay();"></input>
	<input type="button" value="재생 시간" onclick="javascript:vodCurrentTime();"></input>
	<input type="button" value="자막 " onclick="javascript:trackView();"></input>
	<input type="button" value="구간 반복 활성/비활성" onclick="javascript:ableLoop();"></input>
	<input type="button" value="구간 반복 정지 " onclick="javascript:offLoop();"></input>
	<input type="button" value="배속(1.2) " onclick="javascript:playRate(1.2);"></input>
</body>
<script src="${pageContext.request.contextPath}/tgplayer/conponentRoot.js"></script>
<script src="${pageContext.request.contextPath}/tgplayer/player_init_unpack.js"></script>

<script>
	$('#videoEl').createVideo({ //플레이어 생성 영역 DIV의 아이디
		id: 'video' //생성될 비디오 태그 아이디 *필수값
		,src: [ //영상  size: 영상 Quality, src: 영상 주소 *필수값
			{ src: '/player/tgplayer/sample/video/sample_480.mp4', type: 'video/mp4', size: 480 }
			,{ src: '/player/tgplayer/sample/video/sample_720.mp4', type: 'video/mp4', size: 720 }
			,{ src: '/player/tgplayer/sample/video/sample_1080.mp4', type: 'video/mp4', size: 1080 }
		]
		,thumb: '/player/tgplayer/sample/thumbnail/sample_thumb.png' //썸네일 주소
		,subtitle: [
			{ kind: 'captions', label: 'Korea', srclang: 'ko', src: '/player/tgplayer/sample/subtitle/sample_ko.vtt', default: true }
			, { kind: 'captions', label: 'English', srclang: 'en', src: '/player/tgplayer/sample/subtitle/sample_en.vtt' }
		]
		,speed: [ 0.8, 1, 1.2, 1.4, 1.6 ]
		,watermark: [//워터마크 pos:워터마크 위치 0: 좌상, 1: 좌하, 2: 우상, 3: 우하, url: 워터마크 주소
			{ pos: 0, url: '/player/tgplayer/sample/watermark/demo.png' }
		] 
		,autoPlay: false
		,startTime: 0 //영상 재생 시작 시간
		,seeking: true // false : 현재 재생시간 이후는 seeking 되지 않도록
		,progress: true // false : 프로그레스바 클릭 되지 않도록
		,pip: true
 		,preview: [
			{ enabled: true, src: '/player/tgplayer/sample/preview/sample_preview.vtt' }
 	    ]
		,bookmark: [ //북마크 time: 시간, text: 설명
			{ time: 2, text: 'one' }
			, { time: 8, text: 'two' }
			, { time: 10, text: 'three' } 
			, { time: 16, text: 'four' }
			, { time: 25, text: 'five' } 
		]
		,loop: [
			{ enable: true, start: 10, end: 15 }
		]
	});
	
	function offLoop(){
		$('#video').offLoop();
	}
	
	function ableLoop(){
		$('#video').ableLoop();
	}
	
	function bookmarkAdd(){
		$('#video').bookmarkAdd([{ time: 40, text: "add"}]); //북마크 추가	
	}
	
	function bookmarkRemove(){
		$('#video').bookmarkRemove(3);  // 3번째 북마크 제거	
	}
	
	function vodPlay(){
		$('#video').vodPlay();
	}
	
	function vodCurrentTime(){
		var time = $('#video').vodCurrentTime();
		$('#currentTime').val(time);
	}
	
	function playRate(rate){
		$('#video').playRate(rate); // rate 값으로 배속 변경
	}
	
	function playRateRtn(rate){
		var playRate = rate;
		//alert(playRate);
	}
	
	function trackView(){	
		$('#video').trackView(0); // 자막 index(-1:disabled, 0: 첫번째 자막, 1: 두번째 자막 ...) 
	}
	
	function trackViewRtn(stat){ // 자막 index(-1:disabled, 0: 첫번째 자막, 1: 두번째 자막 ...) 
		var trackViewstat = stat;
		//alert(trackViewstat);
	}
</script>
</html>