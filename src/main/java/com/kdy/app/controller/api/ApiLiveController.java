package com.kdy.app.controller.api;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kdy.app.bean.util.ExcelDownBean;
import com.kdy.app.dto.api.request.RequestBroadcastInsertDTO;
import com.kdy.app.dto.api.request.RequestBroadcastListDTO;
import com.kdy.app.dto.api.request.RequestBroadcastUpdateDTO;
import com.kdy.app.dto.api.request.RequestUserBroadcastListDTO;
import com.kdy.app.dto.api.response.ResponseBroadcastDetailDTO;
import com.kdy.app.dto.api.response.ResponseBroadcastListDTO;
import com.kdy.app.dto.api.response.ResponseBroadcastListExcelDTO;
import com.kdy.app.dto.api.response.ResponseCommonDTO;
import com.kdy.app.dto.api.response.ResponseUserBroadcastListDTO;
import com.kdy.app.dto.live.AppBroadcastVO;
import com.kdy.app.dto.live.ResultVO;
import com.kdy.app.service.IF.ChannelServiceIF;
import com.kdy.app.service.api.IF.ApiLiveServiceIF;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/api/v2.0/broadcasts")
public class ApiLiveController {
	
	private final Logger logger = LoggerFactory.getLogger(ApiLiveController.class);
	
	private final ApiLiveServiceIF liveService;
	private final ChannelServiceIF channelService;
	private final ExcelDownBean excelDownBean;
	
	@Autowired
	public ApiLiveController(ApiLiveServiceIF liveService, ChannelServiceIF channelService, ExcelDownBean excelDownBean) {
		this.liveService = liveService;
		this.channelService = channelService;
		this.excelDownBean = excelDownBean;
	}
	
	/**
	 * API ?????? ?????? ????????????
	 * @param RequestBroadcastListDTO
	 * @return ResponseBroadcastListDTO
	 * @throws Exception
	 */
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value="Live Broadcast List", notes="????????? ?????? ?????? ??????")
	public ResponseEntity<?> getBroadcasts(RequestBroadcastListDTO requestDTO) throws Exception {
		 
		ResponseBroadcastListDTO responseDTO = new ResponseBroadcastListDTO();
		ResultVO result = new ResultVO();
		
		try {
			responseDTO = liveService.getBroadcasts(requestDTO);
			result.setRslt(true);
			result.setRsltMsg("SUCCESS");
			
		} catch(Exception e) {
			result.setRslt(false);
			result.setRsltMsg("GET_BROADCASTS_FAIL");
			result.setRsltDesc("Error causes while select broadcasts in Database");
			logger.error(e.getMessage());
			
		} finally {
			if(responseDTO == null) {
				responseDTO  = new ResponseBroadcastListDTO();
			}
			responseDTO.setResult(result);
		}
		return ResponseEntity.ok(responseDTO);
	}
	
	/**
	 * API ?????? ?????? ????????? ????????????
	 * @param requestDTO
	 * @return ResponseBroadcastDetailDTO
	 * @throws Exception
	 */
	@GetMapping(value = "/{seq}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value="Live Broadcast Detail", notes="?????? ????????? ?????? ?????? ??????")
	public ResponseEntity<?> getBroadcast(@ApiParam(value="????????? ?????? ?????????") @PathVariable("seq") String seq) throws Exception {
		ResponseBroadcastDetailDTO responseDTO = new ResponseBroadcastDetailDTO();
		ResultVO result = new ResultVO();
		
		try {
			
			if(seq == null || seq.equalsIgnoreCase("undefined")) {
				result.setRslt(false);
				result.setRsltMsg("NOT_FOUND_ID_VALUE");
				result.setRsltDesc("null pointer exception because of null value in ID");
				
			} else {
				responseDTO = liveService.getBroadcast(seq);
				result.setRslt(true);
				result.setRsltMsg("SUCCESS");
			}
			
		} catch(Exception e) {
			result.setRslt(false);
			result.setRsltMsg("GET_BROADCAST_FAIL");
			result.setRsltDesc("Error causes while select broadcasts in Database");
			logger.error(e.getMessage());
			
		} finally {
			if(responseDTO == null) {
				responseDTO = new ResponseBroadcastDetailDTO();
			}
			responseDTO.setResult(result);
		}
		
		return ResponseEntity.ok(responseDTO);
	}
	
	/**
	 * API ?????? ?????? ??????????????????
	 * @param RequestBroadcastExcelDTO
	 * @return void
	 * @throws Exception
	 */
//	@GetMapping(value="/excel", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
//	@ApiOperation(value="Live Broadcasts Excel Download", notes="????????? ?????? ?????? ??????")
//	public void downloadExcel(RequestBroadcastExcelDTO requestDTO, HttpServletRequest req, HttpServletResponse res) throws Exception {
//		excelDownBean.liveBroadcast(liveService.getBroadcastsExcel(requestDTO), "broadcast_list", req, res);
//	}
	
	/**
	 * API ????????? ?????? ???????????? [MultipartFile: ?????????(??????), ????????????(??????)]
	 * @param RequestBroadcastInsertDTO
	 * @return ResponseCommonDTO
	 * @throws Exception
	 */
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value="Live Broadcast Regist", notes="????????? ????????? ?????? ??????" )
	public ResponseEntity<?> addBroadcast(@Valid RequestBroadcastInsertDTO requestDTO,
			@ApiParam(value="????????????") @RequestPart(value="attach", required =false ) MultipartFile attachFile,
			@ApiParam(value="????????? ???????????????", required=true) @RequestPart("thumbnail") MultipartFile thumbnailFile) throws Exception {
		
		//ResponseCommonDTO responseDTO = null;
		ResultVO result = new ResultVO();
		
		try {
			result = liveService.addBroadcasts(requestDTO, attachFile, thumbnailFile);
			
		} catch(Exception e) {
			result.setRslt(false);
			result.setRsltMsg("ADD_BROADCAST_FAIL");
			result.setRsltDesc("Error causes while insert broadcasts in Database");
			logger.error(e.getMessage());
		}
		
		//responseDTO = new ResponseCommonDTO();
		//responseDTO.setRequest(requestDTO);
		//responseDTO.setResult(result);
		
		return ResponseEntity.ok(result);
	}
	
	/**
	 * API ?????? ??????
	 * @param RequestBroadcastDeleteDTO
	 * @return ResultVO
	 * @throws Exception
	 */
	@DeleteMapping(value="/{seq}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value="Live Broadcast Delete", notes="????????? ?????? ??????")
	public ResponseEntity<?> deleteBroadcast(@ApiParam(value="????????? ?????? ?????????" , required = true) @PathVariable List<String> seq) throws Exception {
		
		ResultVO result = new ResultVO();
		
		try {
			
			if(seq == null && seq.size() < 0) {
				result.setRslt(false);
				result.setRsltMsg("NOT_FOUND_IDS_VALUE");
				result.setRsltDesc("null pointer exception because of null value in seqs");
			} else {
				result = liveService.deleteBroadcast(seq);
			}
			
		} catch(Exception e) {
			result.setRslt(false);
			result.setRsltMsg("DELETE_BROADCAST_FAIL");
			result.setRsltDesc("Error causes while delete broadcast in Database");
			logger.error(e.getMessage());
		}
		
		return ResponseEntity.ok(result);
	}
	
	/**
	 * API ?????? ??????
	 * @param RequestBroadcastUpdateDTO
	 * @return ResultVO
	 * @throws Exception
	 */
	@PutMapping(value="/{seq}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value="Live Broadcast Update", notes="????????? ?????? ??????")
	public ResponseEntity<?> updateBroadcast(@Valid RequestBroadcastUpdateDTO requestDTO,
			@ApiParam(value="????????????") @RequestPart(value="attach", required = false) MultipartFile attachFile,
			@ApiParam(value="????????? ????????? ??????????????? [????????? ????????? ????????? removeThumbnailFlag??? Y??? ???????????????.]") @RequestPart(value="thumbnail", required = false) MultipartFile thumbnailFile) throws Exception {

		ResultVO result = new ResultVO();
		
		try {
			result = liveService.updateBroadcast(requestDTO, attachFile, thumbnailFile);
		} 
		catch(Exception e) {
			result.setRslt(false);
			result.setRsltMsg("UPDATE_BROADCAST_FAIL");
			result.setRsltDesc("Error causes while update broadcast in Database");
			logger.error(e.getMessage());
		}
		
		return ResponseEntity.ok(result);
	}

	//????????? ?????? ??????
	@PutMapping(value="/status/{lbSeq}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value="Live Broadcast Status End Update", notes = "????????? ?????? ??????")
	public ResponseEntity<?> liveBroadcastStatusEnd(@ApiParam(value="????????? ?????? ?????????" , required = true) @PathVariable String lbSeq) throws Exception {
		
		ResultVO result = new ResultVO();
		
		try {
			result = liveService.liveBroadcastStatusEnd(lbSeq);
		} catch (Exception e) {
			result.setRslt(false);
			result.setRsltMsg("UPDATE_LIVE_STATUS_FAIL");
			result.setRsltDesc("Error causes while update live status in Database");
			logger.error(e.getMessage());
			
		}
		return ResponseEntity.ok(result);
	}
	
	@GetMapping(value="/chat/{lbSeq}")
	@ApiOperation(value="Live Chat txt Download", notes = "????????? ?????? ????????????")
	public AppBroadcastVO getLiveInfo(@PathVariable String lbSeq) throws Exception {
		return liveService.getLiveInfo(lbSeq);
	}
	
	@PutMapping
	@ApiOperation(value="Live Broadcast Status Pause, Restart", notes = "????????? ???????????? ??? ?????????")
	public ResponseEntity<?> livePauseAndStart(@RequestBody AppBroadcastVO vo) throws Exception {
		ResponseCommonDTO responseDTO = null;
		ResultVO result = new ResultVO();
		
		try {
			result = liveService.livePauseAndStart(vo);
		} catch (Exception e) {
			result.setRslt(false);
			result.setRsltMsg("UPDATE_LIVE_STATUS_FAIL");
			result.setRsltDesc("Error causes wule update live status in Database");
			logger.error(e.getMessage());
		} finally {
			responseDTO = new ResponseCommonDTO();
			responseDTO.setRequest(vo);
			responseDTO.setResult(result);
		}
		
		return ResponseEntity.ok(responseDTO);
	}
	
	
	//USER LIVE ????????? ????????????
	@GetMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value="User Live Broadcast List", notes="????????? ????????? ?????? ?????? ??????")
	public ResponseEntity<?> getUserBroadcasts(RequestUserBroadcastListDTO requestDTO) throws Exception {
		ResponseUserBroadcastListDTO responseDTO = null; 
		ResultVO result = new ResultVO();
		
		try {
			
			responseDTO = liveService.getUserBroadcasts(requestDTO);
			result.setRslt(true);
			result.setRsltMsg("SUCCESS");
			
		}catch (Exception e) {
			result.setRslt(false);
			result.setRsltMsg("GET_USER_BROADCASTS_FAIL");
			result.setRsltDesc("Error causes while select broadcasts in Database");
			logger.error(e.getMessage());
		} finally {
			if(responseDTO == null) {
				responseDTO = new ResponseUserBroadcastListDTO();
			}
			responseDTO.setResult(result);
		}
		
		return ResponseEntity.ok(responseDTO);
	}
	
	//LIVE ?????? ?????? (1:?????????/2:??????/3:????????????)
	@GetMapping(value = "/status-check")
	@ApiOperation(value = "LIVE STATUS CHECK")
	public ResponseEntity<?> liveStatusCheck(String lbSeq) throws Exception{
		String rtn = null;
		
		try {
			rtn = liveService.liveStatusCheck(lbSeq);
		} catch (Exception e) {
			rtn = null;
			logger.error(e.getMessage());
		}
		
		return ResponseEntity.ok(rtn);
	}
	
	/**
	 * API ?????? ?????? ???????????? (excel ???)
	 * @param RequestBroadcastListDTO
	 * @return ResponseBroadcastListExcelDTO
	 * @throws Exception
	 */
	@GetMapping(value = "/excel" , produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value="Live Broadcast Excel List", notes="????????? ?????? ???????????? ?????? ?????? ??????")
	public ResponseEntity<?> getBroadcastsExcel(RequestBroadcastListDTO requestDTO) throws Exception {
		
		ResponseBroadcastListExcelDTO responseDTO = new ResponseBroadcastListExcelDTO();
		ResultVO result = new ResultVO();
		
		try {
			responseDTO = liveService.getBroadcastListExcel(requestDTO);
			result.setRslt(true);
			result.setRsltMsg("SUCCESS");
			
		} catch(Exception e) {
			result.setRslt(false);
			result.setRsltMsg("GET_BROADCASTS_FAIL");
			result.setRsltDesc("Error causes while select broadcasts in Database");
			logger.error(e.getMessage());
			
		} finally {
			if(responseDTO == null) {
				responseDTO  = new ResponseBroadcastListExcelDTO();
			}
			responseDTO.setResult(result);
		}
		return ResponseEntity.ok(responseDTO);
	}
	
}
