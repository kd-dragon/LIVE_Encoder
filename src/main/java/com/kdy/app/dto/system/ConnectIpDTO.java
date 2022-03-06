package com.kdy.app.dto.system;

import java.util.List;

import lombok.Data;

@Data
public class ConnectIpDTO {
	
	private List<ConnectIpVO> connectIpList;
	private List<ConnectIpVO> onlyIpList;
	
	private int totalCount;
	private int blockCount;
	private int currentPage=1;
	private int startNum;
	private int totalPage;
	private String pagingHtml;
	
	private String dupChk;

	private String lscSeq; //CONCAT(DATE_FORMAT(NOW(), '%Y'),LPAD(nextval(sequence_lsc_16), 16, '0'))
	private String lscIp;
	private String lscDesc;
}
