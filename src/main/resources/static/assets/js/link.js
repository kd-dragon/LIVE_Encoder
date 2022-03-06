/*  LINK  */
function getContextPath() {
	
	var isUseable = false;
	var path = "";
	if(isUseable) {
		var hostIndex = location.href.indexOf( location.host ) + location.host.length;
		path = location.href.substring( hostIndex, location.href.indexOf('/', hostIndex + 1) );
	}
	return path;
};

/*LOGO*/
function goUrl_main() {window.open( getContextPath() + "/","_self")};

/* 라이브 방송 관리 */
function goUrl_1_1() {window.open( getContextPath() + "/live/liveOnAirList.do","_self")};
function goUrl_1_2() {window.open( getContextPath() + "/channel/channelList.do","_self")};
function goUrl_1_3() {window.open( getContextPath() + "/monitor/monitorMain.do","_self")};

/* 라이브 방송 관리 */
function goUrl_2() {window.open( getContextPath() + "/vod/vodList.do","_self")};

/* 인코딩 설정 */
function goUrl_3_1() {window.open( getContextPath() + "/setting/watermarkSet.do","_self")};
function goUrl_3_2() {window.open( getContextPath() + "/setting/mediaSet.do","_self")};
function goUrl_3_3() {window.open( getContextPath() + "/setting/categorySet.do","_self")};

/* 시스템관리 */
function goUrl_4_1() {window.open( getContextPath() + "/system/adminManage.do","_self")};
function goUrl_4_2() {window.open( getContextPath() + "/system/setConnectIpList.do","_self")};
function goUrl_4_3() {window.open( getContextPath() + "/system/loginIpLogList.do","_self")};
function goUrl_4_4() {window.open( getContextPath() + "/system/systemMonitor.do","_self")};
function goUrl_4_5() {window.open( getContextPath() + "/system/setStreamingList.do","_self")};

/* 사용자 홈페이지 관리 */
function goUrl_5_1() {window.open( getContextPath() + "/userHome/mainBanner.do", "_self")};
function goUrl_5_2() {window.open( getContextPath() + "/userHome/noticeLink.do", "_self")};
