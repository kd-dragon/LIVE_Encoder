package com.kdy.app.controller.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kdy.app.dto.api.response.ResponeseMonitorDTO;
import com.kdy.app.dto.live.ResultVO;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/v2.0/monitors")
public class ApiMonitorController {
	
	private final Logger logger = LoggerFactory.getLogger(ApiMonitorController.class);
	
	@ApiOperation(value="Live Channel List", notes="라이브 채널 조회")
	@GetMapping
	public ResponseEntity<?> getMonitorData() throws Exception {
		
		ResponeseMonitorDTO responseDto = new ResponeseMonitorDTO();
		ResultVO result = new ResultVO();
		
		try {
			
		} catch (Exception e) {
			
			logger.error(e.getMessage());
		} finally {
			
		}
		
		return ResponseEntity.ok(responseDto);
	}
	
}
