package com.kdy.app.bean.dao;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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

@Repository
public class SystemDAO {

	@Autowired
	SqlSessionTemplate sqlSessionTemplate;
	
	//관리자 리스트
	public List<UserVO> selectUserList(UserListDTO dto) throws Exception {
		return sqlSessionTemplate.selectList("app_system.adminList", dto);
	}
	
	//관리자 리스트 카운트
	public int selectUserListCount(UserListDTO dto) throws Exception {
		return sqlSessionTemplate.selectOne("app_system.adminListCount", dto);
	}
	
	//관리자 삭제
	public int delAdmin(String[] luIds) throws Exception {
		return sqlSessionTemplate.delete("app_system.delAdmin", luIds);
	}
	
	//관리자 등록
	public int insertAdmin(UserVO vo) throws Exception {
		return sqlSessionTemplate.insert("app_system.insertAdmin", vo);
	}
	
	//관리자 등록 > 아이디 중복 체크
	public int idDupCheck(String luId) throws Exception {
		return sqlSessionTemplate.selectOne("app_system.idDupCheck", luId);
	}
	
	//관리자 수정
	public int updateAdmin(UserVO vo) throws Exception {
		return sqlSessionTemplate.update("app_system.updateAdmin", vo);
	}
	
	//로그인 IP 로그 리스트
	public List<LoginIpLogVO> selectLoginIpLogList(LoginIpLogDTO dto) throws Exception {
		return sqlSessionTemplate.selectList("app_system.loginIpLogList", dto);
	}
	
	//로그인 IP 로그 리스트 카운트
	public int selectLoginIpLogListCount(LoginIpLogDTO dto) throws Exception {
		return sqlSessionTemplate.selectOne("app_system.loginIpLogListCount", dto);
	}
	
	//접속 IP 관리 리스트
	public List<ConnectIpVO> selectConnectIpList (ConnectIpDTO dto) throws Exception {
		return sqlSessionTemplate.selectList("app_system.connectIpList", dto);
	}
	
	//접속 IP 관리 리스트 카운트
	public int selectConnectIpListCount (ConnectIpDTO dto) throws Exception {
		return sqlSessionTemplate.selectOne ("app_system.connectIpListCount", dto);
	}
	
	// 접속 IP 추가
	public int insertConnectIp (ConnectIpVO vo) throws Exception {
		return sqlSessionTemplate.insert("app_system.insertConnectIp", vo);
	}
	
	//접속 IP 등록 > 중복 체크
	public int ipDukCheck (ConnectIpVO vo) throws Exception {
		return sqlSessionTemplate.selectOne("app_system.ipDupCheck", vo);
	}
	
	//접속 IP 수정
	public int updateConnectIp(ConnectIpVO vo) throws Exception {
		return sqlSessionTemplate.update("app_system.updateConnectIp", vo);
	}

	//접속 IP 삭제
	public int delConnectIp (String[] lscIps) throws Exception {
		return sqlSessionTemplate.update("app_system.delConnectIp", lscIps);
	}
	
	//접속 IP 관리 리스트
	public List<StreamingVO> selectStreamingList (StreamingDTO dto) throws Exception {
		return sqlSessionTemplate.selectList("app_system.streamingList", dto);
	}
	
	//접속 IP 관리 리스트 카운트
	public int selectStreamingListCount (StreamingDTO dto) throws Exception {
		return sqlSessionTemplate.selectOne ("app_system.streamingListCount", dto);
	}
	
	// 접속 IP 추가
	public int insertStreaming (StreamingVO vo) throws Exception {
		return sqlSessionTemplate.insert("app_system.insertStreaming", vo);
	}
	
	//접속 IP 등록 > 중복 체크
	public int streamingDupCheck (StreamingVO vo) throws Exception {
		return sqlSessionTemplate.selectOne("app_system.streamingDupCheck", vo);
	}
	
	//접속 IP 수정
	public int updateStreaming(StreamingVO vo) throws Exception {
		return sqlSessionTemplate.update("app_system.updateStreaming", vo);
	}

	//접속 IP 삭제
	public int delStreaming (String[] streamingIps) throws Exception {
		return sqlSessionTemplate.update("app_system.delStreaming", streamingIps);
	}
	
	//시스템 관리 > 모니터링 폴더 정보
	public String selectUploadRootPath() throws Exception {
		return sqlSessionTemplate.selectOne("app_system.selectUploadRootPath");
	}
	
	//권한 리스트 가져오기
	public List<AuthVO> getAuthList() throws Exception{
		return sqlSessionTemplate.selectList("app_system.getAuthList");
	}
	
	//카테고리 리스트 가져오기(부서 리스트)
	public List<CategoryVO> getCategoryList(int rootUpCategoryCode) throws Exception{
		return sqlSessionTemplate.selectList("app_system.getCategoryList", rootUpCategoryCode);
	}
}
