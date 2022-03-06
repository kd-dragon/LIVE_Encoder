/**
 * test : 2021.06.17
 */

//페이지 로딩시에 채팅 연결
window.onload = function() {
	var liveStatus = $("#lbStatus").val();
	
	if(liveStatus == "1") {
		connect();
	}
}

//브라우저 종료시에 채팅방 나가기 적용
$(window).bind("beforeunload", function(e) {
	var msg = {type : "LEAVE", sessionId : sessionId};
	disconnect(msg);
});


//날짜 형식
function nowDate() {
	var date = new Date();
	var month = date.getMonth() + 1;
	var day = date.getDate();
	var hour = date.getHours();
	var minute = date.getMinutes();
	
	month = String(month);
	day = String(day);
	hour = String(hour);
	minute = String(minute);
		
	if(month.length == 1) {
		month = "0" + month;
	}
	
	if(day.length == 1) {
		day = "0" + day;
	}
	
	if(hour.length == 1) {
		hour = "0" + hour;
	}
	
	if(minute.length == 1) {
		minute = "0" + minute;
	}
	return date.getFullYear() + "-" + month + "-" + day + " " + hour + ":" + minute;
}

var userId = $("#chatId").val();
var userName = $("#chatName").val();
var stomp = null;
var socket;
var sessionIdList = [];
var sessionId = "";
var num = 0;
var userCnt = "";
var lbStatus = $("#lbStatus").val();


//전체 화면 채팅
var parentsNode = "";
if(lbStatus == '1') {
	parentsNode += '<div class="chat-ctrl" onclick="toggleLiveChat(event);" ondblclick="toggleLiveChat(event);">';
	parentsNode += '	<span><i class="icon-bubble"></i></span>';
	parentsNode += '</div>';
	
	//채팅
	parentsNode += '<div class="live-chat" id="live-chat" onclick="stopEvent(event);" ondblclick="stopEvent(event);">';
	parentsNode += '<h4>라이브 채팅 <b id="totalUser2"></b></h4>';
	parentsNode += '	<div class="chat-list" id="fullChatListDiv">';
	parentsNode += '		<div class="chat-list-inner" id="fullChatList">';
	parentsNode += '			<ul id="fullChatWrap">';
	parentsNode += '			</ul>';
	parentsNode += '		</div>';
	parentsNode += '	</div>';
	parentsNode += '	<div class="chat-write">';
	parentsNode += '		<textarea name="" id="textMessage2" placeholder="최대 200자로 제한되며, 욕설, 비난글은 삭제됩니다." cols="" rows="" title="" onclick="event.stopPropagation();" ondblclick="event.stopPropagation();" onkeypress="if(event.keyCode==13) return false;" onkeydown="if(event.keyCode==13) javascript:sendChk(event, \'fullscreen\');"></textarea>';
	parentsNode += '<button type="button" id="chatbtn" class="" ondblclick="sendChk(event, \'fullscreen\');" onclick="sendChk(event, \'fullscreen\');">등록</button>';
	parentsNode += '	</div>';
}

var childNode = "";



//sockjs 연결
function connect(event) {
	if(userId != null || userId != "") {
		socket = new SockJS('http://192.168.0.132:9090/websocket');
		stomp = Stomp.over(socket);
		//개발자 모드 콘솔 숨기기
		//stomp.debug = null;
		stomp.connect({}, onConnected, onError);
		console.log("connected!!!");
	} else {
	
		var node = '<div class="no-data">';
			node += '<i class="ft-video-off"></i>';
			node += '라이브 채팅이 일시적으로 중단 되었습니다.';
			node += '</div>';
			
		$('#chatWrap, #fullChatList').html(node);
	}
}

//connect가 정상적인 경우 실행
function onConnected() {
	var lbSeq = $("#lbSeq").val();
	stomp.subscribe('/broker/'+lbSeq, onMessageReceived);
	stomp.send('/app/chat.join/'+lbSeq, {}, JSON.stringify({userId : userId, type : 'JOIN', lbSeq : lbSeq, userName : userName}));
	console.log("채팅 로딩 완료.");
}

//connect 실패
function onError(error) {

	var node = '<div class="no-data">';
		node += '<i class="ft-video-off"></i>';
		node += '라이브 채팅이 일시적으로 중단 되었습니다.';
		node += '</div>';
		
		$('#chatWrap, #fullChatList').html(node);
}

//채팅 끄기
function roomOut() {
	var msg = { type : "LEAVE", sessionId : sessionId };
	disconnect(msg);
}

//연결 종료
function disconnect(event) {
	var lbSeq = $("#lbSeq").val();
	if(stomp) {
		var sendMsg = {
			userId : userId,
			type : event.type,
			sessionId : event.sessionId,
			lbSeq : lbSeq,
			userName : userName
		};
	}
	stomp.send('/app/chat.leave/'+ lbSeq, {}, JSON.stringify(sendMsg));
}


//전체화면 채팅 on / off
function toggleLiveChat(event) {
	//클릭 이벤트가 상위로 넘어가지 않게 방지
	event.stopPropagation();
	
	var className = $(".chat-ctrl").attr("class");
	
	if($('.plyr__video-wrapper').attr('class').indexOf('fullscreen') > -1) {
		if(className.indexOf('off') > -1) {
			$('.chat-ctrl').removeClass('off');
			$('.live-chat').removeClass('display-block');
		} else {
			$('.chat-ctrl').addClass('off');
			$('.live-chat').addClass('display-block');			
		}
	}
}

//전체 화면에서 라이브 채팅 영역 클릭 시 동영상 멈추는 이벤트 방지
function stopEvent(event) {
	var target = event.target;
	target = $(target).attr('id');
	
	if(target == 'chatbtn' || target == 'textMessage2') {
		return;
	} else {
		event.stopPropagation();
	}
}

//메시지 전송 전 체크
//type : fullscreen / default
function sendChk(event, type) {
	
	event.stopPropagation();
	
	var msg;
	var txtMsg = "";
	
	if(type == 'fullscreen') {
		txtMsg = $("#textMessage2").val();
	} else {
		txtMsg = $("#textMessage").val();
	}
	
	if(txtMsg == null || txtMsg == "") {
		return;
	} else {
		msg = txtMsg;
	}
	var sendMsg = { type : "SEND", content : msg, sessionId : sessionId };
	send(sendMsg);
}

//메시지 전송
function send(event) {
	var lbSeq = $("#lbSeq").val();
	var lbTitle = $("#lbTitle").val();
	
	num++;
	if(stomp) {
		var sendMsg = {
			userId : userId,
			content : event.content,
			type : event.type,
			sessionId : event.sessionId,
			messageId : sessionId + "-" + num,
			lbSeq : lbSeq,
			date : nowDate(),
			lbTitle : lbTitle,
			userName : userName
		};
		stomp.send('/app/chat.send/'+lbSeq, {}, JSON.stringify(sendMsg));
		
		$("#textMessage, #textMessage2").val("");
		$("#textMessage, #textMessage2").focus();
	}
}

function msgDelete(content, messageId) {
	var lbSeq = $("#lbSeq").val();
	if(stomp) {
		var sendMsg = {
			content : content,
			messageId : messageId,
			lbSeq : lbSeq,
			type : 'DELETE'
		};
		stomp.send('/app/chat.delete/'+lbSeq, {}, JSON.stringify(sendMsg));
	}
}

//type에 따라 실행되는 함수
function onMessageReceived(payload) {
	var msg = JSON.parse(payload.body);
	
	if(sessionId == "") {
		sessionId = msg.sessionId;
	}
	
	if(sessionIdList.length == 0) {
		sessionIdList.push(msg.sessionId);
	}
	
	//채팅 연결된 경우
	if(msg.type === 'JOIN') {
		if(msg.status === 'SUCCESS') {
			$("#chatWrap").css("display", "block");
			//$("#connectJoin").css("display", "none");
			//$("#connectOut").css("display", "block");
			$("#textMessage").attr("disabled", false);
			$("#textMessage").attr("readonly", false);
			
			var html ="";
			childNode = "";
			
			if(userId == msg.userId && !sessionIdList.includes(msg.sessionId)) {
				html += "<div class=\"chat-box\">";
				html += "<div class=\"chat-writer-info\"><b class=\"writer\">" + msg.userName + "("+msg.userId+")</b><small>&nbsp;&nbsp;"+nowDate()+"</small>";
				html += "</div>";
				html += "<div class=\"chat-box-text\">"+ msg.userName + "("+msg.userId+")님이 채팅방에 입장하였습니다.</div>";
				html += "</div>";
				
				childNode += '<li>';
				childNode += '<span class="writer-info">';
				childNode += '	<i class="name">' + msg.userName + '(' + msg.userId + ')</i>';
				childNode += '	<i>' + nowDate() + '</i>';
				childNode += '	<p>' + msg.userId +' 님이 채팅방에 입장하였습니다.</p>';
				childNode += '</li>';
					
				sessionIdList.push(msg.sessionId);
				
			} else if(userId == msg.userId) {
				html += "<div class=\"chat-box adm\">";
				html += "<div class=\"chat-writer-info\"><b class=\"writer\">"+ msg.userName + "("+msg.userId+")</b><small>&nbsp;&nbsp;"+nowDate();+"</small>";
				html += "</div>";
				html += "<div class=\"chat-box-text\">채팅방에 입장하였습니다.</div>";
				html += "</div>";
				
				// 전체 화면
				childNode += '<li class="mine">';
				childNode += '<span class="writer-info">';
				childNode += '	<i class="name">' + msg.userName + '(' + msg.userId + ')</i>';
				childNode += '	<i>' + nowDate() + '</i>';
				childNode += '	<p>채팅방에 입장하였습니다.</p>';
				childNode += '</li>';
				
			} else {
				html += "<div class=\"chat-box\">";
				html += "<div class=\"chat-writer-info\"><b class=\"writer\">"+ msg.userName + "("+ msg.userId+")</b><small>&nbsp;&nbsp;"+nowDate()+"</small>";
				html += "</div>";
				html += "<div class=\"chat-box-text\">"+ msg.userName + "("+msg.userId+")님이 채팅방에 입장하였습니다.</div>";
				html += "</div>";
				
				// 전체 화면 채팅 쌓기 
				childNode += '<li>';
				childNode += '<span class="writer-info">';
				childNode += '	<i class="name">' + msg.userName + '(' + msg.userId + ')</i>';
				childNode += '	<i>' + nowDate() + '</i>';
				childNode += '	<p>' + msg.userName + '(' + msg.userId +') 님이 채팅방에 입장하였습니다.</p>';
				childNode += '</li>';
			}
			
			$("#chatWrap").append(html);
			$("#fullChatWrap").append(childNode);
			
		} else {
			alert("채팅방 로딩에 오류가 발생하였습니다.");
		}
		
		userCnt = msg.roomUserCnt;
		$("#totalUser, #totalUser2").html(userCnt);
		
	} else if(msg.type === 'LEAVE') {
		if(msg.status === 'SUCCESS') {
			var html = "";
			childNode = "";
			
			if((userId == msg.userId && sessionId != msg.sessionId)) {
				sessionIdList.pop(msg.sessionId);
				
				html += "<div class=\"chat-box\">";
				html += "<div class=\"chat-writer-info\"><b class=\"writer\">"+msg.userId+"</b><small>&nbsp;&nbsp;"+nowDate()+"</small>";
				html += "</div>";
				html += "<div class=\"chat-box-text\">"+ msg.userName + "("+msg.userId+") 님이 채팅방을 나갔습니다.</div>";
				html += "</div>";
				
				// 전체 화면 채팅 쌓기 
				childNode += '<li>';
				childNode += '<span class="writer-info">';
				childNode += '	<i class="name">' + msg.userName + '(' + msg.userId + ')</i>';
				childNode += '	<i>' + nowDate() + '</i>';
				childNode += '	<p>' + msg.userName + '(' + msg.userId +') 님이 채팅방을 나갔습니다.</p>';
				childNode += '</li>';
				
				$("#chatWrap").append(html);
				$("#fullChatWrap").append(childNode);
				
			} else if(userId == msg.userId) {
				//alert("채팅방을 나갔습니다.");
				$("#textMessage").attr("disabled", true);
				$("#textMessage").attr("readonly", true);
				//$("#connectJoin").css("display", "block");
				//$("#connectOut").css("display", "none");
				sessionId  = "";
				sessionIdList.pop(msg.sessionId);
				stomp.disconnect();
				console.log("disconnected!!!");
				
			} else {
				html += "<div class=\"chat-box\">";
				html += "<div class=\"chat-writer-info\"><b class=\"writer\">"+msg.userId+"</b><small>&nbsp;&nbsp;"+nowDate()+"</small>";
				html += "</div>";
				html += "<div class=\"chat-box-text\">"+ msg.userName + "("+msg.userId+") 님이 채팅방을 나갔습니다.</div>";
				html += "</div>";
				
				// 전체 화면 채팅 쌓기 
				childNode += '<li>';
				childNode += '<span class="writer-info">';
				childNode += '	<i class="name">' + msg.userName + '(' + msg.userId + ')</i>';
				childNode += '	<i>' + nowDate() + '</i>';
				childNode += '	<p>' + msg.userName + '(' + msg.userId +') 님이 채팅방을 나갔습니다.</p>';
				childNode += '</li>';
				
				$("#chatWrap").append(html);
				$("#fullChatWrap").append(childNode);
				
			}
		} else { 
			alert("다시 시도해주시기 바랍니다.");
		}
		
		userCnt = msg.roomUserCnt;
		$("#totalUser, #totalUser2").html(userCnt);
		
	} else if(msg.type === 'SEND') {
		if(msg.status === 'SUCCESS') {
			var html = "";
			childNode = '';
			
			var messageId = msg.messageId;
			if(userId == msg.userId) {
				if( sessionId != msg.sessionId){
					html += "<div class=\"chat-box\">";
					childNode += '<li>';
				} else {
					html += "<div class=\"chat-box adm\">";
					childNode += '<li class="mine">';									
				}
			} else {
				html += "<div class=\"chat-box\">";		
				childNode += '<li>';		
			} 
			
			html += "<div class=\"chat-writer-info\"><b class=\"writer\">"+ msg.userName + "("+msg.userId+")</b><small>&nbsp;&nbsp;"+nowDate()+"</small>";
			html += "<small>&nbsp;&nbsp;<a onclick='javascript:msgDelete(\""+msg.content+"\",\""+messageId+"\");'>삭제</a></small></div>";
			html += "<div class=\"chat-box-text\" data-msgId="+messageId+">"+msg.content+"</div>";				
			html += "</div>";
			
			// 전체 화면 채팅 쌓기 
			childNode += '<span class="writer-info">';
			childNode += '	<i class="name">' + msg.userName + '(' + msg.userId + ')</i>';
			childNode += '	<i>' + nowDate() + '</i>';
			childNode += '  <i onclick="javascript:msgDelete(\'' + msg.content + '\', \'' + messageId + '\');">삭제</i>';   
			childNode += '	<p class="chat-box-text" data-msgId="' + messageId + '">' + msg.content + '</p>';
			childNode += '</li>';
				
			$("#chatWrap").append(html);
			$("#fullChatWrap").append(childNode);
			
		} else {
			alert("메시지를 전송하는데 실패했습니다.");
		}
	} else if(msg.type === 'DELETE') {
		if(msg.status === 'SUCCESS') {
			$('.chat-box-text[data-msgId="'+msg.messageId+'"]').text(msg.content);
		} else {
			alert("메시지 삭제를 실패했습니다.");
		}
	}
	$("#chatList").scrollTop($("#chatList").prop("scrollHeight"));
	//전체 화면 스크롤
	$("#fullChatListDiv").scrollTop($("#fullChatListDiv").prop("srcollHeight"));

}

//메시지 글자수 200자 제한
$("#textMessage, #textMessage2").on("keyup", function() {
	if($(this).val().length > 200) {
		alert("글자수는 200자까지 입력할 수 있습니다.");
		$(this).val($(this).val().substring(0, 200));
	}
});
