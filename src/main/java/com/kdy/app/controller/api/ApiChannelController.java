package com.kdy.app.controller.api;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kdy.app.dto.api.request.RequestChannelDeleteDTO;
import com.kdy.app.dto.api.request.RequestChannelInsertDTO;
import com.kdy.app.dto.api.request.RequestChannelUpdateDTO;
import com.kdy.app.dto.api.response.ResponseChannelListDTO;
import com.kdy.app.dto.api.response.ResponseCommonDTO;
import com.kdy.app.dto.live.ResultVO;
import com.kdy.app.service.api.IF.ApiChannelServiceIF;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/v2.0/channels")
public class ApiChannelController extends ApiExceptionController{
	
	private final Logger logger = LoggerFactory.getLogger(ApiChannelController.class);
	
	private ApiChannelServiceIF channelService;
	
	@Autowired
	public ApiChannelController(ApiChannelServiceIF channelService) {
		this.channelService = channelService;
	}
	
	/**
	 * API 채널 목록 가져오기
	 * @param requestDTO
	 * @return
	 * @throws Exception
	 */
	@ApiOperation(value="Live Channel List", notes="라이브 채널 조회")
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getChannels() throws Exception {
		
		ResponseChannelListDTO responseDto = new ResponseChannelListDTO();
		ResultVO result = new ResultVO();
		
		try {
			
			responseDto.setChannelList(channelService.channelList());
			
			result.setRslt(true);
			result.setRsltMsg("SUCCESS");
			
		}catch (Exception e) {
			result.setRslt(false);
			result.setRsltMsg("GET_CHANNEL_FAIL");
			result.setRsltDesc("Error causes while select channel in Database");
			logger.error(e.getMessage());
			
		} finally {
			responseDto.setResult(result);
		}
		
		return ResponseEntity.ok(responseDto);
	}
	
	/**
	 * 채널 등록하기
	 * @param requestDto
	 * @return
	 * @throws Exception
	 */
	@ApiOperation(value="Live Channel Insert", notes="라이브 채널 추가")
	@PostMapping
	public ResponseEntity<?> addChannels(@RequestBody @Valid RequestChannelInsertDTO requestDto) throws Exception {
		logger.info(requestDto.toString());
		
		ResponseCommonDTO responseDTO = null;
		ResultVO result = new ResultVO();
		
		try {
			
			result = channelService.addChannel(requestDto);
			
		} catch (Exception e) {
			result.setRslt(false);
			result.setRsltMsg("ADD_CHANNEL_FAIL");
			result.setRsltDesc("Error causes while insert channel in Database");
			logger.error(e.getMessage());
			
		} finally {
			responseDTO = new ResponseCommonDTO();
			responseDTO.setRequest(requestDto);
			responseDTO.setResult(result);
		}
		
		return ResponseEntity.ok(responseDTO);
	}
	
	/**
	 * 채널 수정하기
	 * @param requestDto
	 * @return
	 * @throws Exception
	 */
	@ApiOperation(value="Live Channel Update", notes="라이브 채널 수정")
	@PutMapping
	public ResponseEntity<?> modChannels(@RequestBody @Valid RequestChannelUpdateDTO requestDto) throws Exception{
		logger.info(requestDto.toString());
		
		ResponseCommonDTO responseDTO = null;
		ResultVO result = new ResultVO();
		
		try {
			
			result = channelService.modChannel(requestDto);
			
		} catch (Exception e) {
			result.setRslt(false);
			result.setRsltMsg("MOD_CHANNEL_FAIL");
			result.setRsltDesc("Error causes while Update channel in Database");
			logger.error(e.getMessage());
			
		} finally {
			responseDTO = new ResponseCommonDTO();
			responseDTO.setRequest(requestDto);
			responseDTO.setResult(result);
		}
		
		return ResponseEntity.ok(responseDTO);
	}
	
	@ApiOperation(value="Live Channel Delete", notes="라이브 채널 삭제")
	@DeleteMapping
	public ResponseEntity<?> delChannels(@RequestBody @Valid RequestChannelDeleteDTO requestDto) throws Exception{
		logger.info(requestDto.toString());
		
		ResponseCommonDTO responseDTO = null;
		ResultVO result = new ResultVO();
		
		try {
			result = channelService.delChannel(requestDto);
			
		} catch (Exception e) {
			result.setRslt(false);
			result.setRsltMsg("DEL_CHANNEL_FAIL");
			result.setRsltDesc("Error causes while Delete channel in Database");
			logger.error(e.getMessage());
			
		} finally {
			responseDTO = new ResponseCommonDTO();
			responseDTO.setRequest(requestDto);
			responseDTO.setResult(result);
		}
		return ResponseEntity.ok(responseDTO);
	}
	
	
}
