package com.kdy.app.bean;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.kdy.app.bean.dao.SystemDAO;
import com.kdy.app.dto.live.CategoryVO;
import com.kdy.app.dto.system.AuthVO;
import com.kdy.app.dto.system.ConnectIpDTO;
import com.kdy.app.dto.system.ConnectIpVO;
import com.kdy.app.dto.system.LoginIpLogDTO;
import com.kdy.app.dto.system.LoginIpLogVO;
import com.kdy.app.dto.system.StreamingDTO;
import com.kdy.app.dto.system.StreamingVO;
import com.kdy.app.dto.system.UserListDTO;
import com.kdy.app.dto.system.UserVO;
import com.kdy.live.bean.util.OkHttpClientPool;

import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Component
@RequiredArgsConstructor
public class SystemBean {

	private final Logger logger = LoggerFactory.getLogger(SystemBean.class);
	
	private final SystemDAO dao;
	
	private final OkHttpClientPool pool;
	
	
	//관리자 리스트
	public List<UserVO> getUserList(UserListDTO dto) throws Exception {
		return dao.selectUserList(dto);
	}
	
	//관리자 리스트 카운트
	public int getUserListCount(UserListDTO dto) throws Exception {
		return dao.selectUserListCount(dto);
	}
	
	//관리자 삭제
	public String removeAdmin(String[] luIds) throws Exception {
		if(dao.delAdmin(luIds) > 0) {
			return "SUCCESS";
		} else {
			return "FAIL";
		}
	}
	
	//관리자 등록
	public String registerAdmin(UserVO vo) throws Exception {
		if(dao.insertAdmin(vo) > 0) {
			return "SUCCESS";
		} else {
			return "FAIL";
		}
	}
	
	//관리자 등록 > 아이디 중복 체크
	public int idDupCheck(String luId) throws Exception {
		return dao.idDupCheck(luId);
	}
	
	//관리자 수정
	public String modifyAdmin(UserVO vo) throws Exception {
		if(dao.updateAdmin(vo) > 0) {
			return "SUCCESS";
		} else {
			return "FAIL";
		}
	}
	
	//로그인 ip 로그 리스트
	public List<LoginIpLogVO> getLoginIpLogList(LoginIpLogDTO dto) throws Exception {
		return dao.selectLoginIpLogList(dto);
	}
	
	//로그인 ip 로그 리스트 카운트
	public int getLoginIpLogListCount(LoginIpLogDTO dto) throws Exception {
		return dao.selectLoginIpLogListCount(dto);
	}
	
	//접속 IP 리스트
	public List<ConnectIpVO> getConnectIpList(ConnectIpDTO dto) throws Exception {
		return dao.selectConnectIpList(dto);
	}
	
	//접속 IP 리스트 count
	public int getConnectIpCount(ConnectIpDTO dto) throws Exception {
		return dao.selectConnectIpListCount(dto);
	}
	
	//접속 IP 추가
	public String insertConnectIp(ConnectIpVO vo) throws Exception {
		if(dao.insertConnectIp(vo) > 0) {
			return "SUCCESS";
		} else {
			return "FAIL";
		}
	}
	
	//접속 IP 추가 > 중복 체크
	public String ipDupCkeck(ConnectIpVO vo) throws Exception {

		String type = "";
		int result = dao.ipDukCheck(vo);
		if(result == 0) {
			type = "SUCCESS";
		} else {
			type = "DUPLICATION";
		}
		
		return type;
	}
	
	//접속 IP 수정
	public String modifyConnectIp(ConnectIpVO vo) throws Exception {
		if(dao.updateConnectIp(vo) > 0) {
			return "SUCCESS";
		} else {
			return "FAIL";
		}
	}
	
	//접속 IP 삭제
	public String delConnectIp(String[] lscIps) throws Exception {
		if(dao.delConnectIp(lscIps) > 0) {
			return "SUCCESS";
		} else {
			return "FAIL";
		}
	}
	
	//접속 IP 리스트
	public List<StreamingVO> getStreamingList(StreamingDTO dto) throws Exception {
		return dao.selectStreamingList(dto);
	}
	
	//접속 IP 리스트 count
	public int getStreamingCount(StreamingDTO dto) throws Exception {
		return dao.selectStreamingListCount(dto);
	}
	
	public void setStreamingStatus(StreamingDTO dto) throws Exception {
		
		List<StreamingVO> list = dto.getStreamingList();
		
		for(StreamingVO vo : list) {
			OkHttpClient client = pool.getStreamingMonitorClient();
			
			Request request = new Request.Builder()
					.url("http://"+ vo.getStreamingIp() + ":" + vo.getStreamingPort() + "/monitor").get()
					.build();
			try {
				
				Response response = client.newCall(request).execute();
				if(response.isSuccessful()) {
					vo.setStreamingStatus(true);
				} else {
					vo.setStreamingStatus(false);
				}
			} catch (IOException e) {
				logger.error("Streaming Server Connect Fail -> IP:{}, Port:{}", vo.getStreamingIp(), vo.getStreamingPort());
				vo.setStreamingStatus(false);
			}
		}
	}
	
	//접속 IP 추가
	public String insertStreaming(StreamingVO vo) throws Exception {
		if(dao.insertStreaming(vo) > 0) {
			return "SUCCESS";
		} else {
			return "FAIL";
		}
	}
	
	//접속 IP 추가 > 중복 체크
	public String streamingDupCheck(StreamingVO vo) throws Exception {

		String type = "";
		int result = dao.streamingDupCheck(vo);
		if(result == 0) {
			type = "SUCCESS";
		} else {
			type = "DUPLICATION";
		}
		
		return type;
	}
	
	//접속 IP 수정
	public String modifyStreaming(StreamingVO vo) throws Exception {
		if(dao.updateStreaming(vo) > 0) {
			return "SUCCESS";
		} else {
			return "FAIL";
		}
	}
	
	//접속 IP 삭제
	public String delStreaming(String[] streamingIps) throws Exception {
		if(dao.delStreaming(streamingIps) > 0) {
			return "SUCCESS";
		} else {
			return "FAIL";
		}
	}
	
	//시스템 관리 > 모니터링 폴더 정보
	public String getUploadRootPath() throws Exception {
		return dao.selectUploadRootPath();
	}
	
	//권한 리스트 가져오기
	public List<AuthVO> getAuthList() throws Exception{
		return dao.getAuthList();
	}
	
	//카테고리 리스트 가져오기(부서 리스트)
	public List<CategoryVO> getCategoryList(int rootUpCategoryCode) throws Exception {
		return dao.getCategoryList(rootUpCategoryCode);
	}
}
