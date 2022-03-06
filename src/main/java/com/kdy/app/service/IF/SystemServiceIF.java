package com.kdy.app.service.IF;


import java.util.Map;

import com.kdy.app.dto.system.ConnectIpDTO;
import com.kdy.app.dto.system.ConnectIpVO;
import com.kdy.app.dto.system.LoginIpLogDTO;
import com.kdy.app.dto.system.StreamingDTO;
import com.kdy.app.dto.system.StreamingVO;
import com.kdy.app.dto.system.UserListDTO;
import com.kdy.app.dto.system.UserVO;
import com.kdy.live.dto.system.SystemConfigVO;

public interface SystemServiceIF {

	//관리자 리스트
	public UserListDTO getUserList(UserListDTO dto) throws Exception;
	
	//관리자 삭제
	public String removeAdmin(String luIds) throws Exception;
	
	//관리자 등록
	public String registerAdmin(UserVO vo) throws Exception;
	
	//ID 중복 체크
	public String idDupCheck(String luId) throws Exception;
	
	//관리자 수정
	public String modifyAdmin(UserVO vo) throws Exception;
	
	//시스템 모니터링
	public Map<String, Object> getSystemStatus() throws Exception;
	
	//로그인 ip 로그 리스트
	public LoginIpLogDTO getLoginIpLogList(LoginIpLogDTO dto) throws Exception;
	
	//접속 IP 리스트
	public ConnectIpDTO getConnectIpList(ConnectIpDTO dto) throws Exception;
	
	//접속 IP 추가
	public String insertConnectIp(ConnectIpVO vo) throws Exception;
	
	//접속 IP 수정
	public String modifyConnectIp(ConnectIpVO vo) throws Exception;
	
	//접속 IP 삭제
	public String delConnectIp(String lscIps) throws Exception;
	
	//스트리밍 서버 리스트
	public StreamingDTO getStreamingList(StreamingDTO dto) throws Exception;
	
	//스트리밍 정보 추가
	public String insertStreaming(StreamingVO vo) throws Exception;
	
	//스트리밍 정보 수정
	public String modifyStreaming(StreamingVO vo) throws Exception;
	
	//접속 IP 삭제
	public String delStreaming(String streamingIps) throws Exception;
	
	//시스템 관리 > 모니터링 폴더 정보
	public SystemConfigVO getUploadRootPath() throws Exception;
}
