/*새창팝업열기 2018-09-18 개발화면에서 삭제*/
function openPopup(popUrl, popName, popWidth, popHeight){
	//alert(popWidth);
	if(popName == null || popName == '' || popName == 'undefined'){
		popName = "_blank";
	}
	var popOption = "left=10, top=10, width="+popWidth+",height="+popHeight+", resizable=0, scrollbars=yes";
	window.open(popUrl, popName, popOption);
}

/* 팝업 페이지 길어지는 경우 top버튼*/
$(function(){$(window).scroll(function(){  //스크롤하면 아래 코드 실행
       var num = $(this).scrollTop();  // 스크롤값
       if( num > 100 ){  // 스크롤을 x이상 했을 때
          $(".pop-top").addClass('pop-top-fixed');
       }else{
           $(".pop-top").removeClass('pop-top-fixed');
       }
  });
});

$(document).ready(function(){
	/* 카테고리 클릭 이벤트 */
	$('.depth-box span').click(function(){
		$('.depth-box span').removeClass('active');
		$(this).addClass('active');
		$(this).closest('li').siblings('li').find('.depth-box').removeClass('open');
		$(this).siblings('.depth-box').toggleClass('open');
	});

	/* 채널 VOD설정 */
	$('#optionCh').change(function(){
		var optionCHname = $("#optionCh option:selected").val();
		if(optionCHname == 'vod'){
		$('.option-ch').removeClass('d-none');
		}
	});
	/* VOD저장여부 */
	$("#vodSave").click(function(){
		$('.option-save').removeClass('d-none');
	});
//	$("#vodUnsave").click(function(){
//		$('.option-save').addClass('d-none');
//	});

	/* 미디어속성, 클립정보 */
	$('#unitTab1 a').click(function(){
		$('.unitTab a').removeClass('active');
		$(this).addClass('active');
		$('.unitTab-cont-2').removeClass('height-auto');
		$('.unitTab-cont-1').addClass('height-auto');
	});
	$('#unitTab2 a').click(function(){
		$('.unitTab a').removeClass('active');
		$(this).addClass('active');
		$('.unitTab-cont-1').removeClass('height-auto');
		$('.unitTab-cont-2').addClass('height-auto');
	});


	/* 탭 클릭시 morris chart 오류 부분 개선 임시스크립트
	$("#statistics-tab1").click(
		function(){
		$("#statistics-2").css("height","0");
		$("#statistics-3").css("height","0");
		
	});
	$("#statistics-tab2").click(
		function(){
		$("#statistics-2").css("height","auto");
		$("#statistics-3").css("height","0");
		
	});
	$("#statistics-tab3").click(
		function(){
		$("#statistics-2").css("height","0");
		$("#statistics-3").css("height","auto");
		
	});
 */
	/* 썸네일삭제 예시 */
/*	$('.del-file').click(function(){
	if(confirm('첨부된 파일을 삭제하시겠습니까?')){
		$(this).closest('.upload-file-box').find('.upload-asis').addClass('d-none');
		$(this).closest('.upload-file-box').find('.add-file').removeClass('d-none');
	}else{}
	
	});*/


	/* 상세검색시 액션 */
	$("#searchMore").click(function(){
		$(".searchMore").toggleClass("d-none");
	});

	/* 콘텐츠 유형별 보기 */
	$("#contType").change(function() {
		var state = $("#contType option:selected").val();
		//alert(state);
		$(".select-section").removeClass("d-block d-inline");
		$(".select-section").addClass("d-none");
		$("#"+state).addClass("d-block");
		$("."+state).addClass("d-inline");
	});

	/* 갤러리 마우스 오버시 */
	$(".gallery-img").mouseover(function(){
		$(".view-over").removeClass("d-none");
	});
	$(".view-over").mouseover(function(){
		$(this).removeClass("d-none");
	});	
	$(".view-over").mouseout(function(){
		$(this).addClass("d-none");	
	});

	/*갤러리 목록 보기형식 버튼*/
	$("#btnViewType").click(function(){
		$("#btnViewType i").toggleClass("ft-list");
		$("#btnViewType i").toggleClass("ft-grid");
	});


	/* 화면해상도에 따라 높이값 가변적으로 변경 */
	var totalHeight = screen.height - 500 +"px";
	$(".height-screen").css("height",totalHeight);
	//alert(totalHeight);


	/* 주소록 색인 */
	$(".choice-consonant li span").click (
		function(){
		$(".choice-consonant li span").removeClass ("badge-info")
		$(".choice-consonant li span").addClass ("badge-light-important")
		$(this).removeClass("badge-light-important");
		$(this).addClass("badge-info");
		}
		)

		/* 카테고리 +/- tree구조 - checkbox가 없는 형태*/
		$(".noChk.cate-group li span").click(
		function(){
			if($(this).attr('class')===undefined||$(this).attr('class')===''){
				$(".cate-group li span").removeClass("active");
				$(this).closest("li").siblings().removeClass("open"); 
				$(this).addClass("active");
				$(this).closest("li").addClass("open");
			}else{
				$(this).removeClass("active");
				$(this).closest("li").removeClass("open");
			}
		}	
	);
		/* 카테고리 +/- tree구조 - checkbox가 있는 형태*/
		$(document).on('click', '.haveChk.cate-group li span', function(){
		var id = $(this).attr('id').replace('span_', '');
		if($("#chk_"+id).prop("checked")){
			$("#chk_"+id).prop("checked",false);

			$(this).removeClass("active");
			$(this).closest("li").removeClass("open");

			
		} else {
			$("#chk_"+id).prop("checked",true);
			$(".haveChk.cate-group li span").removeClass("active");
			$(this).closest("li").siblings().removeClass("open"); 
			$(this).addClass("active");
			$(this).closest("li").addClass("open");

		}
	});

	var categroup1 = $("ul.sub1-cate-group > li");
	categroup1.click(function(){
		$(this).addClass("open").addClass("active");
		categroup1.not($(this)).removeClass("open").removeClass("active");
	});

	/* 콘텐츠 검색 기간선택 */
	$(function() {
	  $('input[name="daterange"]').daterangepicker({
		linkedCalendars: true,
		opens: 'right',
	  }, function(start, end, label) {
		console.log("A new date selection was made: " + start.format('YYYY-MM-DD') + ' to ' + end.format('YYYY-MM-DD'));
	  });
	});


	/* 상세화면에서 듀얼리스 disabled처리 */
	$(".view-form .box1 select").attr("disabled","disabled");
	$(".view-form .box2 select").attr("disabled","disabled");
	$(".view-form .box1 .filter").css("display","none");
	$(".view-form .box2 .filter").css("display","none");


	/*팝업-추가등록*/
	$("#btnAddTr").click (
		function(){
		$(".popup-table .addTr").slideDown();
	});

	/* 동작샘플 */
	$('#reContents').click(function(){
		confirm("콘텐츠 복원은 요청일 00:00~04:00동안 진행됩니다.");
	});

	/* 카테고리 박스 */
	// 카테고리박스 높이
	setInterval(function(){categoryBoxAction()},500); 
	function categoryBoxAction(){ 
	cateH = $(window).height()-100+'px';
	}
	//카테고리영역 닫힘
	function closeCate(){
		$(".categoryBtnBox").removeClass("open");
	}

	$(".categoryBox-close").click(function() {
		$(".categoryBtnBox").removeClass("open");
	});
	//카테고리 버튼
	$(".categoryBtn").click(function() {
		$('.categoryBox').css('height','calc(100% - 2rem)');
		$('.categoryBox').css('overflow','hidden');
		$(".categoryBtnBox").toggleClass("open");
		});
	//ESC버튼 누를 경우 카테고리영역 닫힘
	window.onkeyup = function(e) {
		var key = e.keyCode ? e.keyCode : e.which;
		if(key == 27) {
			closeCate();
		}
	}
	// 카테고리박스 항목 오픈 동작 예시
	$("ul.side-category").find("a").click(function(){

		if($(this).parent(".hasSub").hasClass("open")) {
			$(this).parent(".hasSub").removeClass("open");
		}else{
			$(this).parent(".hasSub").addClass("open");
		}
	});

	
	/* 태그입력 박스 클릭시 커서 위치 */
	$(".tag-wrap").click(function() {
	  $(".tag-input").focus();
	});

	/* 관리자 댓글 수정 예시 */
	$(".list-btn-edit").click(function() {
		$(".reply-box.re-reply.edit").removeClass("edit-hidden");
		$(".reply-box.rb-03").addClass("edit-hidden");
	});
	$(".rb-btn-cancel").click(function() {
		$(".reply-box.re-reply.edit").addClass("edit-hidden");
		$(".reply-box.rb-03").removeClass("edit-hidden");
	});
	

	/* 화면 크기에 따른 no-live 폰트사이즈 변경 2020-11-04 */
	fSize = function() {
	var fontSize = $(".no-live-content").width() * 0.10;
	$(".no-live-content i").css('font-size', fontSize);
	$(".no-live-content span").css('font-size', fontSize * 0.60);
	};
	$(window).resize(fSize);
	$(document).ready(fSize);


});

/* 공지사항 롤링 동작 */
$(document).ready(function(){
	var noticeH =  $(".notice-area").height(); //공지사항 높이값
	var num = $(".notice-rolling li").length; //공지사항 개수
	var max = noticeH * num; // 총 높이
	var move = 0; //초기값
	function noticeRolling(){
		move += noticeH;
		$(".notice-rolling:not(:animated)").animate({"top":-move},600,function(){
			if( move >= max ){ //총높이가 최대값보다 커지면
				$(this).css("top",0); //높이 0으로 초기화
				move = 0; //초기값도 0으로 초기화
			};
		});
	};
	noticeRollingPlay = setInterval(noticeRolling,3000);
	$(".notice-rolling").append($(".notice-rolling li").first().clone()); //첫번째 li복사

	$(".rolling_stop").click(function(){
		clearInterval(noticeRollingPlay); //자동롤링 해제
	});
	$(".rolling_start").click(function(){
		noticeRollingPlay = setInterval(noticeRolling,3000); //자동롤링 시작
	});
});

	/*200306  전체콘텐츠 > 이미지목록 높이값 조절 */
setInterval(function(){galleryConts()},500); 
function galleryConts(){ 
	var galleryContsImgH = $('.gallery-img').height();

	$('.gallery-scroll-02').css({
		'height':  galleryContsImgH - 23
		});
}

	/*200421  기본정보 썸네일 높이값 조절 */
setInterval(function(){thumbImg()},500); 
function thumbImg(){ 
	var thumbImgH = $('.w-thumb').height();
	$('.info-thumb').css({
		'height':  thumbImgH,
		'overflow': 'hidden'
		});
	
}

setInterval(function(){thumbBox()},500); 
function thumbBox(){
	var thumbBoxH=$('.thumb-fixed').height();
	$('.thumbList-box').css({
		'height': thumbBoxH
	});
}


	/*200422  대표이미지 관리 수정- 셀렉박스 선택별 변화 */
$('#thumbOption').change(function() {
	var state = $('#thumbOption option:selected').val();
	if ( state == 'thumbOption1' ) {
		$('.image-box').hide();
		$('.movie-box').hide();
	} else if ( state == 'thumbOption2' ){
		$('.image-box').show();
		$('.movie-box').hide();
	} else if ( state == 'thumbOption3' ){
		$('.movie-box').show();
		$('.image-box').hide();
	} else if ( state == 'thumbOption4'){
		$('#register-file').trigger('click');
		$('.image-box').hide();
		$('.movie-box').hide();		 
	}
});

$('#register-file').on('change', function() {
    readimg(this);
});

function readimg() {
	var file = event.target.files[0];
	var reader = new FileReader();

	reader.onload = function(e){
		$('#selected-img').attr('src', e.target.result);
		console.log(e.target.result)
	}
	reader.readAsDataURL(file);
}

	/*200422  콘텐츠 그룹 등록- 대표이미지 셀렉박스 선택별 변화 */
$('#registImg').change(function() {
	var state2 = $('#registImg option:selected').val();
	if ( state2 == 'registImg1' ) {
		$('.registFile').hide();
		$('#registImg').css({
			'border-radius': '0.21rem'
		});
	} else if ( state2 == 'registImg2' ){
		$('.registFile').hide();
		$('#registImg').css({
			'border-radius': '0.21rem'
		});
	} else if ( state2 == 'registImg3' ){
		$('.registFile').show();
		$('#registImg').css({
			'border-top-right-radius': '0',
			'border-bottom-right-radius': '0'
		});		
	}
});


	/* 스트리밍 일시정지 및 재시작 */
	$('.livePauseOk').click( function() {
		if( $(this).html() == '예' ) {
		  $('.livePause').addClass('hidden');
		  $('.liveRestart').removeClass('hidden');
		} 
	 });
	 $('.liveRestart').click( function() {
		if( $(this).html() == '실시간 스트리밍 재시작' ) {
		  $('.livePause').removeClass('hidden');
		  $('.liveRestart').addClass('hidden');
		} 
	});


	/* 디바이스 선택에 따른 차트변화*/
	$('.mDevice1').css({height: '0', overflow: 'hidden'});
	$('#deviceOption').change(function() {
		var state = $('#deviceOption option:selected').val();	
		if ( state == 'deviceOption1' ) {
			$('.mDevice1').css({height: '0', overflow: 'hidden'});
			$('.webDevice1').css({height: 'auto', overflow: 'hidden'});
		} else if ( state == 'deviceOption2' ){
			$('.mDevice1').css({height: 'auto', overflow: 'hidden'});
			$('.webDevice1').css({height: '0', overflow: 'hidden'});
		}
	});
	$('.mDevice2').css({height: '0', overflow: 'hidden'});
 	$('#deviceOption-2').change(function() {
		var state = $('#deviceOption-2 option:selected').val();	
		if ( state == 'deviceOption1-2' ) {
			$('.mDevice2').css({height: '0', overflow: 'hidden'});
			$('.webDevice2').css({height: 'auto', overflow: 'hidden'});
		} else if ( state == 'deviceOption2-2' ){
			$('.mDevice2').css({height: 'auto', overflow: 'hidden'});
			$('.webDevice2').css({height: '0', overflow: 'hidden'});
		}
	});
	$('.mDevice3').css({height: '0', overflow: 'hidden'});
 	$('#deviceOption-3').change(function() {
		var state = $('#deviceOption-3 option:selected').val();	
		if ( state == 'deviceOption1-3' ) {
			$('.mDevice3').css({height: '0', overflow: 'hidden'});
			$('.webDevice3').css({height: 'auto', overflow: 'hidden'});
		} else if ( state == 'deviceOption2-3' ){
			$('.mDevice3').css({height: 'auto', overflow: 'hidden'});
			$('.webDevice3').css({height: '0', overflow: 'hidden'});
		}
	});
	$('.mDevice4').css({height: '0', overflow: 'hidden'});
 	$('#deviceOption-4').change(function() {
		var state = $('#deviceOption-4 option:selected').val();	
		if ( state == 'deviceOption1-4' ) {
			$('.mDevice4').css({height: '0', overflow: 'hidden'});
			$('.webDevice4').css({height: 'auto', overflow: 'hidden'});
		} else if ( state == 'deviceOption2-4' ){
			$('.mDevice4').css({height: 'auto', overflow: 'hidden'});
			$('.webDevice4').css({height: '0', overflow: 'hidden'});
		}
	});
	$('.mDevice5').css({height: '0', overflow: 'hidden'});
 	$('#deviceOption-5').change(function() {
		var state = $('#deviceOption-5 option:selected').val();	
		if ( state == 'deviceOption1-5' ) {
			$('.mDevice5').css({height: '0', overflow: 'hidden'});
			$('.webDevice5').css({height: 'auto', overflow: 'hidden'});
		} else if ( state == 'deviceOption2-5' ){
			$('.mDevice5').css({height: 'auto', overflow: 'hidden'});
			$('.webDevice5').css({height: '0', overflow: 'hidden'});
		}
	});


