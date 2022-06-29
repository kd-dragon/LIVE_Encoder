package com.kdy.app.service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;

import com.kdy.app.bean.SystemBean;
import com.kdy.app.bean.util.PagingHtmlBean;
import com.kdy.app.dto.system.ConnectIpDTO;
import com.kdy.app.dto.system.ConnectIpVO;
import com.kdy.app.dto.system.LoginIpLogDTO;
import com.kdy.app.dto.system.StreamingDTO;
import com.kdy.app.dto.system.StreamingVO;
import com.kdy.app.dto.system.UserListDTO;
import com.kdy.app.dto.system.UserVO;
import com.kdy.app.service.IF.SystemServiceIF;
import com.kdy.live.dto.system.SystemConfigVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SystemService implements SystemServiceIF {

	private final Logger logger = LoggerFactory.getLogger(SystemService.class);

	private final SystemBean systemBean;
	private final PagingHtmlBean pagingHtmlBean;
	private final DataSourceTransactionManager transactionManager;
	private final PasswordEncoderService passwordEncoderService;
	
	//시스템 상황을 리턴하기 위해 전역변수 선언
	//private OperatingSystemMXBean osBean = (OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();		
	
	private File file = new File(System.getProperty("user.dir"));
	
	@Value("${server.block-count}")
	private int blockCount;
	
	@Value("${live.rootPath}")
	private String rootPath;
	
	@Value("${server.root-up-category-code}")
	private int rootUpCategoryCode;
	
	//관리자 리스트
	@Override
	public UserListDTO getUserList(UserListDTO dto) throws Exception {
		dto.setBlockCount(blockCount);
		dto.setStartNum(dto.getBlockCount() * (dto.getCurrentPage() - 1));
		//관리자 리스트
		dto.setUserList(systemBean.getUserList(dto));
		//리스트 개수
		dto.setTotalCount(systemBean.getUserListCount(dto));
		//페이징
		dto.setPagingHtml(pagingHtmlBean.Paging(dto.getCurrentPage(), dto.getTotalCount(), dto.getBlockCount()));
		
		//권한 리스트
		dto.setAuthList(systemBean.getAuthList());
		
		//카테고리(부서) 리스트
//		dto.setCategoryList(systemBean.getCategoryList(rootUpCategoryCode));
		
		return dto;
	}

	//관리자 삭제
	@Override
	public String removeAdmin(String luIds) throws Exception {
		String userIds[] = luIds.split(",");
		return systemBean.removeAdmin(userIds);
	}

	//관리자 등록
	@Override
	public String registerAdmin(UserVO vo) throws Exception {
		//1. userPw RSA 복호화
		String decryptPw = passwordEncoderService.decryptRsa(vo.getPrivateKey(), vo.getLuPwd().toString());
		//2. 복호화한 암호를 다시 암호화
		String encodePw = passwordEncoderService.encode(decryptPw);
		vo.setLuPwd(encodePw);
		return systemBean.registerAdmin(vo);
	}

	//관리자 등록 > 아이디 중복 체크
	@Override
	public String idDupCheck(String luId) throws Exception {
		String type = "";
		int result = systemBean.idDupCheck(luId);
		if(result == 0) {
			type = "SUCCESS";
		} else {
			type = "DUPLICATION";
		}
		return type;
	}

	//관리자 수정
	@Override
	public String modifyAdmin(UserVO vo) throws Exception {
		
		if(vo.getLuPwd() != null && !vo.getLuPwd().equals("")) {
			//1. 수정할 비밀번호 RSA 복호화
			String decryptPw = passwordEncoderService.decryptRsa(vo.getPrivateKey(), vo.getLuPwd().toString());
			//2. 복호화한 암호를 다시 암호화
			String encodePw = passwordEncoderService.encode(decryptPw);
			vo.setLuPwd(encodePw);
		}
		return systemBean.modifyAdmin(vo);
	}

	//시스템 관리 > 모니터링
	@Override
	public Map<String, Object> getSystemStatus() throws Exception {
		Map<String, Object> dataMap = new HashMap<>();
		
		//dataMap.put("cpuStatus", osBean.getProcessCpuLoad());
		dataMap.put("cpuStatus", "");
		dataMap.put("totalDiskMem", file.getTotalSpace());
		dataMap.put("freeDiskMem", file.getFreeSpace());
		
		//총 메모리
		dataMap.put("totalMemory", getSize(Runtime.getRuntime().totalMemory()));
		//사용한 메모리
		dataMap.put("useMemory", getSize(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
		//메모리 사용량 퍼센트
		dataMap.put("useMemoryPersent", String.format("%.1f",(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/(double)Runtime.getRuntime().totalMemory()*100));
				
		
		return dataMap;
	}
	
	//메모리 단위 변환
    public static String getSize(long size) {
        long kilo = 1024;
        long mega = kilo * kilo;
        long giga = mega * kilo;
        long tera = giga * kilo;
        
        String s = "";
        double kb = (double)size / kilo;
        double mb = kb / kilo;
        double gb = mb / kilo;
        double tb = gb / kilo;
        if(size < kilo) {
            s = size + " Bytes";
        } else if(size >= kilo && size < mega) {
            s =  String.format("%.2f", kb) + " KB";
        } else if(size >= mega && size < giga) {
            s = String.format("%.2f", mb) + " MB";
        } else if(size >= giga && size < tera) {
            s = String.format("%.2f", gb) + " GB";
        } else if(size >= tera) {
            s = String.format("%.2f", tb) + " TB";
        }
        return s;
    }
	
	//시스템 관리 > 모니터링 폴더 정보
	@Override
	public SystemConfigVO getUploadRootPath() throws Exception {
		SystemConfigVO vo = new SystemConfigVO();
		
		vo.setEtcFileUploadPath(systemBean.getUploadRootPath());
		vo.setRootPath(rootPath);
		
		//업로드 폴더 사이즈 구하기 (Byte -> GB 소수점 버리기)
		File uploadFolder = new File(vo.getEtcFileUploadPath());
		double uploadFolderSize = (double)FileUtils.sizeOfDirectory(uploadFolder) / 1024 / 1024 / 1024;
		vo.setUploadFolderSize(Math.floor(uploadFolderSize * 100) / 100.0);
		
		//루트 폴더 사이즈 구하기 (Byte -> GB 소수점 버리기)
		File rootFolder = new File(vo.getRootPath());
		double rootFolderSize = (double)FileUtils.sizeOfDirectory(rootFolder) / 1024 / 1024 / 1024;
		vo.setRootFolderSize(Math.floor(rootFolderSize * 100) / 100.0);
		
		return vo;
	}

	@Override
	public LoginIpLogDTO getLoginIpLogList(LoginIpLogDTO dto) throws Exception {
		
		//default paging
		if(dto.getBlockCount() == 0) {
			dto.setBlockCount(blockCount);
		}
		
		dto.setStartNum(dto.getBlockCount() * (dto.getCurrentPage() - 1));
		
		//리스트
		dto.setIpLogList(systemBean.getLoginIpLogList(dto));
		
		//리스트 카운트
		dto.setTotalCount(systemBean.getLoginIpLogListCount(dto));
				
		//페이징
		dto.setPagingHtml(pagingHtmlBean.Paging(dto.getCurrentPage(), dto.getTotalCount(), dto.getBlockCount()));
		
		int totalPage = dto.getTotalCount() / dto.getBlockCount();
		if(dto.getTotalCount() % dto.getBlockCount() != 0) {
			totalPage++;
		}
		if(totalPage == 0) {
			totalPage++;
		}
		dto.setTotalPage(totalPage);
		
		return dto;
	}
	
	//접속 IP 리스트
	@Override
	public ConnectIpDTO getConnectIpList (ConnectIpDTO dto) throws Exception {
		
		if(dto.getBlockCount() == 0) {
			dto.setBlockCount(blockCount);
		}
		
		dto.setStartNum(dto.getBlockCount() * (dto.getCurrentPage() - 1));
		
		//접속 IP 리스트
		dto.setConnectIpList(systemBean.getConnectIpList(dto));
		//접속 IP 리스트 count
		dto.setTotalCount(systemBean.getConnectIpCount(dto));
		//페이징
		dto.setPagingHtml(pagingHtmlBean.Paging(dto.getCurrentPage(), dto.getTotalCount(), dto.getBlockCount()));
		
		return dto;
		
	}
	
	//접속 IP 추가
	@Override
	public String insertConnectIp(ConnectIpVO vo) throws Exception{
		
		//IP 중복체크
		String type = "";
		type = systemBean.ipDupCkeck(vo);
		if(type == "SUCCESS") {
			return systemBean.insertConnectIp(vo);
		} else {
			return type;
		}
		
	}
		
	//접속 IP 수정
	@Override
	public String modifyConnectIp(ConnectIpVO vo) throws Exception{
		
		//IP 중복체크
		String type = "";
		type = systemBean.ipDupCkeck(vo);
		if(type == "SUCCESS") {
			return systemBean.modifyConnectIp(vo);
		} else {
			return type;
		}
				
	}
	
	//접속 IP 삭제
	@Override
	public String delConnectIp(String lscIps) throws Exception {
		String delConnectIps[] = lscIps.split(",");
		return systemBean.delConnectIp(delConnectIps);
	}

	@Override
	public StreamingDTO getStreamingList(StreamingDTO dto) throws Exception {
		
		if(dto.getBlockCount() == 0) {
			dto.setBlockCount(blockCount);
		}
		
		dto.setStartNum(dto.getBlockCount() * (dto.getCurrentPage() - 1));
		
		//스트리밍 서버 리스트
		dto.setStreamingList(systemBean.getStreamingList(dto));
		//스트리밍 서버 리스트 count
		dto.setTotalCount(systemBean.getStreamingCount(dto));
		//페이징
		dto.setPagingHtml(pagingHtmlBean.Paging(dto.getCurrentPage(), dto.getTotalCount(), dto.getBlockCount()));
		
		//스트리밍 상태 확인
		systemBean.setStreamingStatus(dto);
		
		return dto;
	}

	@Override
	public String insertStreaming(StreamingVO vo) throws Exception {
		String type = "";
		type = systemBean.streamingDupCheck(vo);
		if(type == "SUCCESS") {
			return systemBean.insertStreaming(vo);
		} else {
			return type;
		}
	}

	@Override
	public String modifyStreaming(StreamingVO vo) throws Exception {
		return systemBean.modifyStreaming(vo);
	}

	@Override
	public String delStreaming(String streamingIps) throws Exception {
		String delConnectIps[] = streamingIps.split(",");
		return systemBean.delStreaming(delConnectIps);
	}
}
