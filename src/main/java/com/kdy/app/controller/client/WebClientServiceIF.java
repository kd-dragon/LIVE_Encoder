package com.kdy.app.controller.client;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

import com.kdy.app.dto.api.request.RequestBroadcastInsertDTO;
import com.kdy.app.dto.api.request.RequestBroadcastListDTO;
import com.kdy.app.dto.api.request.RequestBroadcastUpdateDTO;
import com.kdy.app.dto.api.response.ResponseBroadcastListDTO;
import com.kdy.app.dto.live.AppBroadcastVO;
import com.kdy.app.dto.live.ResultVO;

public interface WebClientServiceIF {

	public ResultVO delete(String[] seq) throws Exception;

	public AppBroadcastVO detail(String seq) throws Exception;

	public ResponseBroadcastListDTO list(RequestBroadcastListDTO dto) throws Exception;

	public ResultVO insert(RequestBroadcastInsertDTO dto, MultipartFile attachFile, MultipartFile thumbnailFile) throws Exception;

	public ResultVO update(RequestBroadcastUpdateDTO dto, MultipartFile attachFile, MultipartFile thumbnailFile) throws Exception;

	public void excel(RequestBroadcastListDTO dto, HttpServletRequest req, HttpServletResponse res) throws Exception;




}
