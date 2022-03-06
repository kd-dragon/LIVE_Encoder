package com.kdy.app.controller.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kdy.app.dto.api.request.RequestVodListDTO;
import com.kdy.app.dto.api.response.ResponseVodListDTO;
import com.kdy.app.dto.live.ResultVO;
import com.kdy.app.service.api.IF.ApiVodServiceIF;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/v2.0")
public class ApiVodController {
	
private final Logger logger = LoggerFactory.getLogger(ApiVodController.class);
	
	private final ApiVodServiceIF vodService;
	
	@Autowired
	public ApiVodController(ApiVodServiceIF vodService) {
		this.vodService = vodService;
	}
	
	/**
	 * API VOD 목록 가져오기
	 */
	@GetMapping( value = "/vods", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value="VOD List", notes="VOD 목록 조회")
	public ResponseEntity<?> getVods(RequestVodListDTO requestDTO) throws Exception {
		 
		ResponseVodListDTO responseDTO = new ResponseVodListDTO();
		ResultVO result = new ResultVO();
		
		try {
			
			responseDTO = vodService.getVodList(requestDTO);
			result.setRslt(true);
			result.setRsltMsg("SUCCESS");
			
		} catch(Exception e) {
			result.setRslt(false);
			result.setRsltMsg("GET_VODS_FAIL");
			result.setRsltDesc("Error causes while select vod in Database");
			logger.error(e.getMessage());
			e.printStackTrace();
			
		} finally {
			if(responseDTO == null) {
				responseDTO  = new ResponseVodListDTO();
			}
			responseDTO.setResult(result);
		}
		return ResponseEntity.ok(responseDTO);
	}

}
