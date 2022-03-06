package com.kdy.app.controller.client;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.kdy.app.bean.util.ExcelDownBean;
import com.kdy.app.dto.api.request.RequestBroadcastInsertDTO;
import com.kdy.app.dto.api.request.RequestBroadcastListDTO;
import com.kdy.app.dto.api.request.RequestBroadcastUpdateDTO;
import com.kdy.app.dto.api.response.ResponseBroadcastListDTO;
import com.kdy.app.dto.live.AppBroadcastVO;
import com.kdy.app.dto.live.ResultVO;


@Service
public class WebClientService implements WebClientServiceIF {

	private final WebClient client;
	private final ExcelDownBean excelDownBean;
	
	public WebClientService(WebClient.Builder webClientBuilder, ExcelDownBean excelDownBean) {
		this.client = webClientBuilder.baseUrl("http://localhost:8090").build();
		this.excelDownBean = excelDownBean;
		
	}

	
	
	@Override
	public ResultVO delete(String[] seq) throws Exception {
		
		String uri = "/api/v2.0/broadcasts/{seq}";
		
		return client.delete()
			 	     .uri(uri, String.join(",", seq))
				     .retrieve() 
				     .bodyToMono(ResultVO.class)
				     .block();
	}


	@Override
	public AppBroadcastVO detail(String seq) throws Exception {
		String uri = "/api/v2.0/broadcasts/{seq}";
		
		return client.get() //요청방식: GET
					 .uri(uri, seq) //api 요청 uri 정의
					 .retrieve() //response body를 받아 디코딩하는 메소드
					 .bodyToMono(AppBroadcastVO.class) //reponse를 객체로 디코딩 (한개의 값을 return 할 때: bodyTOMono / 복수의 값을 리털할 때: bodyToFlux)
					 .block(); //마지막에 결과를 플로킹하여 동기 결과를 가져옴 
	}
	
	
	@Override
	public ResponseBroadcastListDTO list(RequestBroadcastListDTO dto) throws Exception {
		String uri = "/api/v2.0/broadcasts";

		return client.get() //요청방식: GET
					 .uri(it -> it.path(uri)
							      .queryParam("blockCount", dto.getBlockCount())
							      .queryParam("currentPage", dto.getCurrentPage())
							      .queryParam("searchEnDate", dto.getSearchEnDate())
							      .queryParam("orderByType", dto.getOrderByType())
							      .queryParam("searchText", dto.getSearchText())
							      .queryParam("searchOpen", dto.getSearchOpen())
							      .queryParam("searchStDate", dto.getSearchStDate())
							      .build()) //api 요청 uri 정의
					 .retrieve() //response body를 받아 디코딩하는 메소드
					 .bodyToMono(ResponseBroadcastListDTO.class) //reponse를 객체로 디코딩 (한개의 값을 return 할 때: bodyTOMono / 복수의 값을 리털할 때: bodyToFlux)
					 .block(); //마지막에 결과를 플로킹하여 동기 결과를 가져옴 
	}


	@Override
	public ResultVO insert(RequestBroadcastInsertDTO dto, MultipartFile attachFile, MultipartFile thumbnailFile)
			throws Exception {
		
		String uri = "/api/v2.0/broadcasts";
		
		MultipartBodyBuilder builder = new MultipartBodyBuilder();
		builder.part("thumbnail", thumbnailFile.getResource());
		builder.part("lbTitle", dto.getLbTitle());
		builder.part("lbRegUserId", dto.getLbRegUserId());
		builder.part("lbStartDate", dto.getLbStartDate());
		builder.part("lbEndDate", dto.getLbEndDate());
		builder.part("lcSeq", dto.getLcSeq());
		builder.part("lbChatYn", dto.getLbChatYn());
		builder.part("lbOpenYn", dto.getLbOpenYn());
		builder.part("lbVodSaveYn", dto.getLbVodSaveYn());
		builder.part("lbVodDownYn", dto.getLbVodDownYn());
		builder.part("lbPresetCd", dto.getLbPresetCd());

		
		//null 가능
		if(dto.getLbDesc() != null) { builder.part("lbDesc", dto.getLbDesc()); }
		if(attachFile != null && !attachFile.isEmpty()) {builder.part("attach", attachFile.getResource());}
		if(dto.getLbCategorySeq() != null ) {builder.part("lbDesc", dto.getLbCategorySeq());}
		if(dto.getLhTagNames() != null ) {builder.part("lhTagNames",String.join(",", dto.getLhTagNames()));}
		
		return client.post()
				.uri(uri)
				.body(BodyInserters.fromMultipartData(builder.build()))
				.retrieve()
				.bodyToMono(ResultVO.class)
				.block();
	}


	@Override
	public ResultVO update(RequestBroadcastUpdateDTO dto, MultipartFile attachFile, MultipartFile thumbnailFile) throws Exception {
		String uri ="/api/v2.0/broadcasts/{seq}";
		
		MultipartBodyBuilder builder = new MultipartBodyBuilder();
		builder.part("lbSeq", dto.getLbSeq());
		builder.part("lbTitle", dto.getLbTitle());
		builder.part("lbOpenYn", dto.getLbOpenYn());
		builder.part("lbStartDate", dto.getLbStartDate());
		builder.part("lbEndDate", dto.getLbEndDate());
		builder.part("lbVodSaveYn", dto.getLbVodSaveYn());
		builder.part("lbVodDownYn", dto.getLbVodDownYn());
		builder.part("lbChatYn", dto.getLbChatYn());
		builder.part("lbPresetCd", dto.getLbPresetCd());
		builder.part("lcSeq", dto.getLcSeq());
		builder.part("removeThumbnailFlag", dto.getRemoveThumbnailFlag());
		builder.part("removeAttachFlag", dto.getRemoveAttachFlag());
		
		//null 가능
		if(dto.getLbDesc() != null) { builder.part("lbDesc", dto.getLbDesc()); }
		if(dto.getLhTagNames() != null ) { builder.part("lhTagNames",String.join(",", dto.getLhTagNames())); }
		if(dto.getLbCategorySeq() != null ) { builder.part("lbDesc", dto.getLbCategorySeq()); }
		if(attachFile != null && !attachFile.isEmpty()) { builder.part("attach", attachFile.getResource()); }
		if(thumbnailFile != null && !thumbnailFile.isEmpty()) { builder.part("thumbnail", thumbnailFile.getResource()); }
		
		return client.put()
				     .uri(uri,dto.getLbSeq())
				     .body(BodyInserters.fromMultipartData(builder.build()))
				     .retrieve()
				     .bodyToMono(ResultVO.class)
				     .block();
		
	}


	@Override
	public void excel(RequestBroadcastListDTO dto, HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		String uri = "/api/v2.0/broadcasts";

		ResponseBroadcastListDTO broadcasts = client.get() //요청방식: GET
												    .uri(it -> it.path(uri)
												   		         .queryParam("blockCount", dto.getBlockCount())
												 		         .queryParam("currentPage", dto.getCurrentPage())
														         .queryParam("searchEnDate", dto.getSearchEnDate())
														         .queryParam("orderByType", dto.getOrderByType())
														         .queryParam("searchText", dto.getSearchText())
														         .queryParam("searchOpen", dto.getSearchOpen())
														         .queryParam("searchType", dto.getSearchType())
														         .queryParam("searchStDate", dto.getSearchStDate())
														         .build())
												   .retrieve() 
												   .bodyToMono(ResponseBroadcastListDTO.class)
												   .block();
		
		excelDownBean.liveBroadcast(broadcasts.getBroadcasts(), "broadcast_list", req, res);
	}

}


