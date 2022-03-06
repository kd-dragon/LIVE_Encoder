var currIdx = 0;
var chkIdx = 0;
var uploadCompleteCnt = 0;
var fileTotalCnt = 0;
var fileTotalSize = 0;
var uploadingTotalSize = 0;

var requestUrl = "";
var callBackFunc;
var fileArr = new Array();
var uploadingResultArr = new Array();

var abortXhr;	// 파일 업로드 취소 시, xhr 객체를 중지시킬 변수

var fileExist = false;

/*var videoExtensions = ["mpeg","mov","asf","asx","rm","mp4","avi","3gp","ogm,","mkv","wmv","flv","m2t","mpg","mxf"];
var audioExtensions = ["mp3","wma","mp4","flac","ogg","gsm","dct","au","aiff","vox","wav","aac","atrac","ra","ram","dss","msv","dvf","mid","ape"];
var imageExtensions = ["jpg","jpeg","png","bif","gif","tif","tiff","rle","dib","raw"];*/


function extNotExist(obj) {
	$(obj).attr("src", "/uploader/css/icon/etc.svg");
}

function setFileObject(idx, file) {
	var fileSize = file.size;
	var fileName = file.name;
	var date = new Date().valueOf();
	var fileId = idx + "file_" + date;

	var fileInfo = {
		"id" : fileId,
		"file" : file,
		"fileSize" : fileSize,
		"fileName" : fileName,
		'serverFileName' : '',
		'sliceSize': 1 * 1024 * 1024,
		'start': 0,
		'totalBlobSize': 0,
		'percent': 0,
		'uploadDate': '',
		'uploadPause': false,
		'loadCookie': false
	};

	return fileInfo;
}

function convertFileSize(size) {
    var fileSize;
    var quot = 0;

    if(size < 1024) {
      fileSize = size + "B";
    } else if(size >= 1024 && size < 1048576) {
      quot = (size / 1024).toFixed(1);
      fileSize = quot + "KB";
    } else if(size >= 1048576 && size < 1073741824) {
      quot = (size / 1048576).toFixed(1);
      fileSize = quot + "MB";
    } else if(size >= 1073741824 && size < 1099511627776) {
      quot = (size / 1073741824).toFixed(1);
      fileSize = quot + "GB";
    }

    return fileSize;
}

function dynamicFileTable(fileInfo) {
	var fileHtml = "";
	
	fileTotalCnt = fileArr.length;
	
	var file = fileInfo.file;
	var fileSize = "";
	var quot = 0.0;
	
	if(checkFile(file)){
		
		fileTotalSize += file.size;

		fileSize = convertFileSize(file.size);
		
		var fileLen = file.length;
		var lastDot = file.name.lastIndexOf(".");
		var fileExt = file.name.substring(lastDot + 1, fileLen).toLowerCase();

		var html = "<tr id='tr_" + fileInfo.id + "'>";
		html += "<td><div class='d-inline-block form-check abc-checkbox abc-checkbox-info'>"
		.concat("<input type='checkbox' class='custom-control-input' name='chk' id='chk" + chkIdx + "' value='" + fileInfo.id + "' />")
		.concat("<label class='custom-control-label' for='chk" + chkIdx + "'></label></div></td>")
		.concat("<td class='text-left'><img src='/uploader/css/icon/" + fileExt + ".svg' class='file-icon' alt='' onerror='extNotExist(this);' />" + file.name + "</td>")
		.concat("<td class='text-right'>" + fileSize + "</td>")
//		.concat("<td>0%</td>")
//		.concat("<td>대기</td>")
		.concat("</tr>");
		fileHtml += html;
		chkIdx++;
	
		fileExist=true;
		
		$("#fileTotalCnt").html(fileTotalCnt);
		$("#fileTotalSize").html(convertFileSize(fileTotalSize));
	
		$("#fileTable").find("tbody").html(fileHtml);
		
		$("#fileDropZone > tr:first").hide();
		
	} else {
		alert("[업로드실패] 파일의 용량이 큽니다.");
	}
	
}

function refreshOrCloseWindow() {
	document.onkeydown = function(e) {
		/* F5, Ctrl + r, Ctrl + F5 Check */
		if(e.keyCode == 116 || e.ctrlKey == true && (e.keyCode == 82)) {
			e.cancelBubble = true;
			e.returnValue = false;

			for(var i = 0; i < fileArr.length; i++) {
				var fileObj = fileArr[i];

				setRefreshCookie(fileObj);
			}

			location.href = window.location.href;
		}
	}
	
	window.onbeforeunload = function(e) {
		for(var i = 0; i < fileArr.length; i++) {
			var fileObj = fileArr[i];

			setRefreshCookie(fileObj);
		}
	}
}

function fileDropDown() {
	var fileDropZone = $("#fileDropZone");
	
	if(fileExist){
		alert("학습영상은 한개의 영상만 등록할 수 있습니다.");
		return;
	}

	fileDropZone.on('dragenter', function(e) {
		e.stopPropagation();
		e.preventDefault();
	});

	fileDropZone.on('dragleave', function(e) {
		e.stopPropagation();
		e.preventDefault();
	});

	fileDropZone.on('dragover', function(e) {
		e.stopPropagation();
		e.preventDefault();
	});

	fileDropZone.on('drop', function(e) {
		e.preventDefault();

		var dropFiles = e.originalEvent.dataTransfer.files;
		//var dropFileArr = new Array();

		if(dropFiles != null) {
			if(dropFiles.length < 1) {
				alert("폴더 업로드는 불가능합니다.");
				return;
			}
			
			if(fileArr.length > 0 || fileExist){
				alert("한개의 영상만 등록할 수 있습니다.");
				return;
			}

			//for(var i = 0; i < dropFiles.length; i++) {
				var dropFile = dropFiles[0];
				var fileInfo = setFileObject(0, dropFile);
				
				//dropFileArr.push(fileInfo);
				fileArr.push(fileInfo);
			//}

			dynamicFileTable(fileInfo);
		} else {
			alert("파일을 드롭하는 중 에러가 발생했습니다.");
		}
	});

	fileDropZone.bind("dblclick", function(e) {
		$("#files").click();
	});
}

function addFile() {
	var selectFiles = document.getElementById("files").files;
	
	if(selectFiles.length > 1 || fileExist){
		alert("한개의 영상만 등록할 수 있습니다.");
		return;
	}
	
	var file = selectFiles[0];
	var fileInfo = setFileObject(0, file);

	fileArr.push(fileInfo);

	dynamicFileTable(fileInfo);
}

/*function deleteFile() {
	var chkObj = $("input:checkbox[name=chk]");

	chkObj.each(function() {
		if($(this).is(":checked") == true) {
			var id = $(this).val();

			for(var i = 0; i < fileArr.length; i++) {
				var fileObj = fileArr[i];

				if(fileObj.id == id) {
					fileArr.splice(i, 1);
					$(this).closest("tr").remove();
					break;
				}
			}
		}
	});
	
	currIdx = 0;
	
	if(fileArr.length == 0) {
		$("#fileDropZone > tr:first").show();
	} else {
		fileTotalCnt = fileArr.length;
		fileTotalSize = 0;
		
		for(var i = 0; i < fileArr.length; i++) {
			var file = fileArr[i].file;
			
			fileTotalSize += file.size;
		}
		
		$("#fileTotalCnt").html(fileTotalCnt);
		$("#fileTotalSize").html(convertFileSize(fileTotalSize));
	}
	
	fileExist=false;
}*/

function deleteFile() {
	
	var chkObj = $("input:checkbox[name=chk]");
	var chkCnt = 0;
	
	chkObj.each(function() {
		if($(this).is(":checked") == true) {
			chkCnt++;
			var id = $(this).val();
			
			if(fileArr.length > 0){
				fileArr = new Array();
				$(this).closest("tr").remove();
				
				/*for(var i = 0; i < fileArr.length; i++) {
					var fileObj = fileArr[i];
	
					if(fileObj.id == id) {
						fileArr.splice(i, 1);
						$(this).closest("tr").remove();
						break;
					}
				}*/
			} else {
				$(this).closest("tr").remove();
			}
		}
		
	});
	
	if(chkCnt == 0){
		alert("삭제할 영상을 선택해주세요");
		return;
	}
	
	currIdx = 0;
	
	if(fileArr.length == 0) {
		$("#fileDropZone > tr:first").show();
		$("#fileTotalCnt").html(0);
		$("#fileTotalSize").html("0.0MB");
		
	} else {
		fileTotalCnt = fileArr.length;
		fileTotalSize = 0;
		
		for(var i = 0; i < fileArr.length; i++) {
			var file = fileArr[i].file;
			
			fileTotalSize = file.size;
		}
		
		$("#fileTotalCnt").html(fileTotalCnt);
		$("#fileTotalSize").html(convertFileSize(fileTotalSize));
	}
	
	fileExist=false;
}


function deleteAllFile() {
	var chkObj = $("input:checkbox[name=chk]");

	chkObj.each(function() {
		$(this).closest("tr").remove();
	});
	
	fileArr = new Array();
	
	fileTotalCnt = 0;
	fileTotalSize = 0;
	
	$("#fileTotalCnt").html(0);
	$("#fileTotalSize").html("0.0MB");
	
	$("#fileDropZone > tr:first").show();
}


function checkFilesCnt() {
	if(fileArr.length == 0) {
		return false;
	} else {
		return true;
	}
}


// 순차 업로드
function tg1st_fileUpload(url, callFunc) {
	$("#uploadContentsCnt").text(uploadCompleteCnt + "/" + fileTotalCnt);
	$("#uploadContentsSize").text("0.0 /" + convertFileSize(fileTotalSize));
	
	console.log("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	
	var fileObj = fileArr[0];
	
	requestUrl = url;
	callBackFunc = callFunc;

	fileObj = checkResumeFileUpload(fileObj);
	fileArr[0] = fileObj;

	setCookie(fileObj.id, fileObj, 7);

	if(fileObj.start == 0) {
		send(fileObj, fileObj.start, fileObj.sliceSize, callFunc, url);
	} else {
		send(fileObj, (fileObj.start + fileObj.sliceSize), (fileObj.start + (fileObj.sliceSize * 2)), callFunc, url);
	}
}

function fileUploadPaused() {
	if(fileArr.length != 0) {
		fileArr[currIdx].uploadPause = true;
	}
}

function detectChkFile() {
	var chkObj = $("input:checkbox[name=upChk]");
	var fileDetect = false;

	chkObj.each(function() {
		if($(this).is(":checked") == true) {
			var id = $(this).val();

			for(var i = 0; i < fileArr.length; i++) {
				var fileObj = fileArr[i];

				if(fileObj.id == id) {
					if(fileObj.percent != 100) {
						currIdx = i;
						fileDetect = true;
						break;
					}
				}
			}

			if(fileDetect) {
				return false;
			}
		}
	});

	return fileDetect;
}

function checkCurrFile(fileId) {
	if(fileArr.length != 0) {
		//var tr = $("#uploadContentsTable > tbody > tr");
		var tr = $("#fileTable > tbody > tr");
		var fileDetect = false;

		tr.each(function() {
			var tr_id = $(this).attr("id");

			if(fileId == tr_id.replace("tr_", "")) {
				fileDetect = true;
			}

			if(fileDetect) {
				return false;
			}
		});
	}

	return fileDetect;
}

function fileUploadAgain() {
	if(fileArr[currIdx].uploadPause) {
		var result = detectChkFile();

		if(result) {
			var fileObj = fileArr[currIdx];

			fileObj = checkResumeFileUpload(fileObj);

			fileObj.uploadPause = false;
			fileArr[currIdx].uploadPause = false;

			if(fileObj.start == 0) {
				send(fileObj, fileObj.start, fileObj.sliceSize, callBackFunc, requestUrl);
			} else {
				send(fileObj, (fileObj.start + fileObj.sliceSize), (fileObj.start + (fileObj.sliceSize * 2)), callBackFunc, requestUrl);
			}
		}
	}
}

function send(piece, start, end, callFunc, url) {
	var formData = new FormData();
	var xhr = new XMLHttpRequest();
	var lastEnd = 0;

	//console.log("size : " + piece.fileSize + " , end : " + end);

	if(piece.fileSize - end < 0) {
		end = piece.fileSize;
		lastEnd = end - start;
	}

	if(end < piece.fileSize) {
		xhr.onreadystatechange = function(e) {
			if(xhr.readyState == XMLHttpRequest.DONE) {
				//console.log('Done Sending Chunk');
				//console.log(e.target.responseText);

				var data = e.target.responseText.split(",");

				var result = checkCurrFile(data[0]);

				if(result) {
					fileArr[currIdx].serverFileName = data[2];
					fileArr[currIdx].totalBlobSize = data[3];
					//fileArr[currIdx].sliceSize = sliceSize;
					fileArr[currIdx].start = start;

					//console.log("totalBlobSize : " + totalBlobSize);

					var trId = "tr_" + data[0];
					//var tr = $("#uploadContentsTable > tbody").find("#" + trId);
					var tr = $("#fileTable > tbody").find("#" + trId);
					var td = tr.children();
					var percentage = (end / piece.fileSize) * 100;
					var totalPercentage = (uploadingTotalSize / fileTotalSize) * 100;
					
					if(uploadingTotalSize == 0) {
						uploadingTotalSize = end;
					} else {
						uploadingTotalSize += (end - start);
					}
					
					$("#uploadContentsSize").text(convertFileSize(uploadingTotalSize) + "/" + convertFileSize(fileTotalSize));
					
					$("#totalProgress").css("width", totalPercentage.toFixed(1) + "%");
					td.eq(2).find("div").find("div").css("width", percentage.toFixed(1) + "%");
					td.eq(4).text("전송중");

					fileArr[currIdx].percent = percentage.toFixed(1);
					fileArr[currIdx].uploadDate = new Date().toLocaleString();

					setCookie(fileArr[currIdx].id, fileArr[currIdx], 7);

					send(fileArr[currIdx], start + fileArr[currIdx].sliceSize, start + (fileArr[currIdx].sliceSize * 2), callFunc, url);
				}
			}
		}
	} else {
		xhr.onload = function(e) {
			$("#uploadContentsCnt").text(uploadCompleteCnt + "/" + fileTotalCnt);
			
			uploadingTotalSize += lastEnd;
			
			$("#uploadContentsSize").text(convertFileSize(uploadingTotalSize) + "/" + convertFileSize(fileTotalSize));

			var tr = $("#tr_" + fileArr[currIdx].id);
			var td = tr.children();
			var percentage = (end / piece.fileSize) * 100;
			var totalPercentage = (uploadingTotalSize / fileTotalSize) * 100;
			
			$("#totalProgress").css("width", totalPercentage.toFixed(1) + "%");
			td.eq(2).find("div").find("div").css("width", percentage.toFixed(1) + "%");
			td.eq(4).text("완료");
			
			fileArr[currIdx].percent = percentage.toFixed(1);
			
			var data = xhr.response.split(",");

			if(data[3] == piece.fileSize) {
				delCookie(data[0]);
				
				fileArr[currIdx].fileName = data[1];
				fileArr[currIdx].serverFileName = data[2];
				
				var uploadingResult = {
					"fileName" : fileArr[currIdx].fileName,
					"serverFileName" : fileArr[currIdx].serverFileName,
					"fileSize" : fileArr[currIdx].fileSize
				};
				
				uploadingResultArr.push(uploadingResult);
				
				fileArr.splice(currIdx, 1);

				uploadCompleteCnt++;
				
				// 순차 업로드
				if(uploadCompleteCnt < fileTotalCnt) {
					$("#uploadContentsCnt").text(uploadCompleteCnt + "/" + fileTotalCnt);
					
					var fileObj;
					var tr = $("#uploadContentsTable > tbody").find("tr");
					var trId = "";

					for(var i = 0; i < tr.length; i++) {
						if(i != 0) {
							trId = $(tr[i]).attr("id");

							if(trId == ('tr_' + fileArr[0].id)) {
								break;
							}
						}
					}

					var fileId = trId.replace("tr_", "");

					for(var i = 0; i < fileArr.length; i++) {
						var id = fileArr[i].id;

						if(fileId == id) {
							currIdx = i;
							fileObj = fileArr[i];
							break;
						}
					}

					send(fileObj, fileObj.start, fileObj.sliceSize, callFunc, url);
				} else {
					$("#uploadContentsCnt").text(uploadCompleteCnt + "/" + fileTotalCnt);
					
					callFunc();
				}
			}
		};
	}

	xhr.onerror = function(e) {
		console.log('Error');
		console.log(e);
	};

	xhr.open("POST", url, true);

	var slicedPart = slice(piece.file, start, end);

	formData.append("fileId", piece.id)
	formData.append('fileName', piece.fileName);
	formData.append('serverFileName', piece.serverFileName);
	formData.append('size', piece.fileSize);
	formData.append('start', start);
	formData.append('end', end);
	formData.append('file', slicedPart);
	formData.append('blobSize', slicedPart.size);
	formData.append('totalBlobSize', piece.totalBlobSize);
	formData.append('uploadPause', piece.uploadPause);

	//console.log(slicedPart);

	if(!piece.uploadPause) {
		setCookie(piece.id, piece, 7);
		
		abortXhr = xhr;

		xhr.send(formData);
	} else {
		var trId = "tr_" + fileArr[currIdx].id;
		//var tr = $("#uploadContentsTable > tbody").find("#" + trId);
		var tr = $("#fileTable > tbody").find("#" + trId);
		var td = tr.children();
		
		td.eq(4).text("대기중");
	}
}

function slice(file, start, end) {
	var slice = file.mozSlice ? file.mozSlice :
						file.webkitSlice ? file.webkitSlice :
						file.slice ? file.slice : noop;

	return slice.bind(file)(start, end);
}

function noop() {}

function cancelFileUpload() {
	abortXhr.abort();
	uploadingTotalSize = 0;
}

function setCookie(name, obj, days) {
	var time = new Date();
	var expires = time.setDate(time.getDate() + days);
	var value = JSON.stringify(obj);

	document.cookie = name + '=' + escape(value) + ';path=/;expires=' + expires;
}

function getCookie(name) {
	var value = document.cookie.match('(^|;) ?' + name + '=([^;]*)(;|$)');
	return value ? value[2] : null;
}

function delCookie(name) {
	var time = new Date();
	time.setDate(time.getDate() + -1);
	var expires = time.toGMTString();

	document.cookie = name + '=' + "" + ';path=/;expires=' + expires + ";";
}

function delAllCookie() {
	var cookies = document.cookie.split(";");

	for(var i in cookies) {
		var cookie = cookies[i];
		var cookieSplit = cookie.split("=");

		delCookie(cookieSplit[0]);
	}
}

function setRefreshCookie(fileObj) {
	if(fileObj.percent != 0.0 && fileObj.percent != 100.0) {
		fileObj.loadCookie = false;

		setCookie(fileObj.id, fileObj, 7);
	}
}

function checkResumeFileUpload(fileObj) {
	var cookies = document.cookie.split(";");

	for(var i in cookies) {
		var cookie = cookies[i];
		var isDetect = false;

		if(cookie.includes("file")) {
			for(var j = 0; j < fileArr.length; j++) {
				var fileInfo = fileArr[j];
				var cookieSplit = cookie.split("=");
				var cookieValue = unescape(cookieSplit[1]);

				if(cookieValue != "") {
					var jsonValue = JSON.parse(cookieValue);

					if(jsonValue.fileName == fileInfo.fileName && jsonValue.loadCookie == false) {
						var remainFileSize = jsonValue.fileSize - jsonValue.totalBlobSize;

						var confirmText = "\"" + jsonValue.fileName + "\""
						.concat(" 파일이 일부 전송된 상태입니다.\n")
						.concat("계속 이어서 파일을 전송할 수 있습니다.\n")
						.concat("\n")
						.concat("전체 크기 : " + convertFileSize(jsonValue.fileSize) + "\n")
						.concat("마지막 전송 날짜 : " + jsonValue.uploadDate + "\n")
						.concat("전송된 크기 : " + convertFileSize(jsonValue.totalBlobSize) + "\n")
						.concat("남은 크기 : " + convertFileSize(remainFileSize) + "\n\n")
						.concat("이어서 전송하시겠습니까?");

						var confirmObj = confirm(confirmText);

						if(confirmObj) {
							var tr = $("#tr_" + fileObj.id);
							tr.attr("id", "tr_" + jsonValue.id);
							tr.children().first().find("input").val(jsonValue.id);

							fileObj.id = jsonValue.id;
							fileObj.serverFileName = jsonValue.serverFileName;
							fileObj.start = jsonValue.start;
							fileObj.totalBlobSize = jsonValue.totalBlobSize;
							fileObj.percent = jsonValue.percent;
							fileObj.uploadDate = jsonValue.uploadDate;
						} else {
							delCookie(jsonValue.id);
						}

						isDetect = true;
						break;
					}
				}
			}
		}

		if(isDetect) {
			break;
		}
	}

	fileObj.loadCookie = true;

	return fileObj;
}


// 저용량 파일 업로드
function tg1st_lowVolumeFileUpload(callFunc) {
	var uploadCount = 0;
	var totalSize = 0;
	
	$("#uploadContentsCnt").text(uploadCount + "/" + fileTotalCnt);
	
	old_Jb('#groupUploadForm').ajaxForm({
		url: "/content/group/lowVolumeFileUpload.do",
		processData: false,
		contentType: false,
		dataType: "json",
		type: "POST",
		beforeSubmit: function(data, form, option) {
			var k = 0;
			var fileLength = fileArr.length;
			var dataSize = data.length;
			var maxSize = fileLength + dataSize;
			
			for(var i = dataSize; i < maxSize; i++) {
				let fileSize = fileArr[k].size;
				
				if(fileSize <= 1099511627776) {
					var obj = {
						name: "files",
						value: fileArr[k],
						type: "file"
					};
					
					data[k] = obj;
					k++;
				}
			}
		},
		uploadProgress : function(event, position, total, percentComplete) {
			var tr = $("#tr_" + fileArr[uploadCount].id);
			var td = tr.children();
			var fileSize = fileArr[uploadCount].fileSize;
			var totalPercentage = (totalSize / fileTotalSize) * 100;
			
			$("#uploadContentsSize").text(convertFileSize(position) + "/" + convertFileSize(fileTotalSize));
			$("#totalProgress").css("width", totalPercentage.toFixed(1) + "%");
			
			if(uploadCount != (fileArr.length -1)) {
				if((total - position) > fileSize) {
					var uploadPercent = Math.floor(((total - position) / fileSize) * 100);
					td.eq(2).find("div").find("div").css("width", uploadPercent.toFixed(1) + "%");
					td.eq(4).text("전송중");
				} else {
					td.eq(2).find("div").find("div").css("width", "100%");
					td.eq(4).text("완료");
					
					totalSize += fileSize;
					uploadCount++;
					
					$("#uploadContentsCnt").text(uploadCount + "/" + fileTotalCnt);
					$("#totalProgress").css("width", totalPercentage.toFixed(1) + "%");
					
					tr = $("#tr_" + fileArr[uploadCount].id);
					td = tr.children();
					
					fileSize = fileArr[uploadCount].fileSize;
					
					while(true) {
						if(uploadCount != (fileArr.length -1)) {
							var remainFileSize = total - position;
							
							if(remainFileSize > fileSize) {
								td.eq(2).find("div").find("div").css("width", "100%");
								td.eq(4).text("완료");
								
								totalSize += fileSize;
								uploadCount++;
								
								$("#uploadContentsCnt").text(uploadCount + "/" + fileTotalCnt);
								$("#totalProgress").css("width", totalPercentage.toFixed(1) + "%");

								tr = $("#tr_" + fileArr[uploadCount].id);
								td = tr.children();
								
								fileSize = fileArr[uploadCount].fileSize;
							} else {
								break;
							}
						} else {
							break;
						}
					}

					var uploadPercent = Math.floor(((total - position) / fileSize) * 100);
					td.eq(2).find("div").find("div").css("width", uploadPercent.toFixed(1) + "%");
					td.eq(4).text("전송중");
				}
			} else {
				if((total - position) > fileSize) {
					var uploadPercent = Math.floor(((total - position) / fileSize) * 100);
					td.eq(2).find("div").find("div").css("width", uploadPercent.toFixed(1) + "%");
					td.eq(4).text("전송중");
				} else {
					td.eq(2).find("div").find("div").css("width", "100%");
					td.eq(4).text("완료");
					
					totalSize += fileSize;
					
					if(uploadCount != (fileArr.length -1)) {
						uploadCount++;
					}

					$("#uploadContentsCnt").text(uploadCount + "/" + fileTotalCnt);
					$("#totalProgress").css("width", totalPercentage.toFixed(1) + "%");
				}
			}
		},
		success: function(xhr, result) {

		},
		complete: function(xhr, status) {
			uploadCount++;
			$("#uploadContentsCnt").text(uploadCount + "/" + fileTotalCnt);
			
			var resText = xhr.responseText;
			
			if(resText != null && resText != "") {
				var jsonObj = JSON.parse(resText);
				var fileList = jsonObj.fileList;
				
				for(var i = 0; i < fileList.length; i++) {
					var jsonFile = fileList[i];
					
					uploadingResultArr.push(jsonFile);
				}
			}
			
			callFunc();
		}
	}).submit();
}
