package com.kdy.app.bean.util;

import java.awt.Color;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.kdy.app.dto.live.AppBroadcastListDTO;
import com.kdy.app.dto.live.AppBroadcastVO;
import com.kdy.app.dto.vod.VodVO;
import com.kdy.live.dto.live.ChatDTO;

@Component
public class ExcelDownBean {
	private final Logger logger = LoggerFactory.getLogger(ExcelDownBean.class);

	public void liveBroadcast(AppBroadcastListDTO dto, String sheetName, HttpServletRequest req, HttpServletResponse res) throws IOException {
		
		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
		Date cur_date = new Date();
		String cur_date_str = fmt.format(cur_date);
		
		// 파일명
		String fileName = sheetName + "_" + cur_date_str + ".xlsx";
		
		encodeFileNameForBrowser(req, res, fileName);
		
		Workbook workbook = null;
		
		try {
			// 엑셀 파일 생성
			workbook = new SXSSFWorkbook();
			
			// 엑셀 파일 내부에 Sheet 생성
			Sheet sheet = workbook.createSheet(sheetName);
			
			// 엑셀 랜더링에 필요한 DTO 
			List<AppBroadcastVO> list = dto.getLiveList();
			
			//CellStyle 설정
			CellStyle greyCellStyle = workbook.createCellStyle();
			applyCellStyle(greyCellStyle, new Color(231, 234, 236));
			
			CellStyle numberCellStyle = workbook.createCellStyle();
			applyCellStyle(applyCellFormat(workbook, numberCellStyle), new Color(231, 234, 236));
			
			CellStyle bodyCellStyle = workbook.createCellStyle();
			applyCellStyle(bodyCellStyle, new Color(255,255,255));
			
			// 헤더 생성
			int rowIndex = 0;
			Row headerRow = sheet.createRow(rowIndex ++);
			
			Cell headerCell0 = headerRow.createCell(0);
			headerCell0.setCellStyle(numberCellStyle);
			headerCell0.setCellValue("순번");
			sheet.setColumnWidth(0, 15*256);
			
			Cell headerCell1 = headerRow.createCell(1);
			headerCell1.setCellStyle(greyCellStyle);
			headerCell1.setCellValue("채널");
			sheet.setColumnWidth(1, 25*256);
			
			Cell headerCell2 = headerRow.createCell(2);
			headerCell2.setCellStyle(greyCellStyle);
			headerCell2.setCellValue("방송제목");
			sheet.setColumnWidth(2, 80*256);
			
			Cell headerCell3 = headerRow.createCell(3);
			headerCell3.setCellStyle(greyCellStyle);
			headerCell3.setCellValue("상태");
			sheet.setColumnWidth(3, 10*256);
			
			Cell headerCell4 = headerRow.createCell(4);
			headerCell4.setCellStyle(greyCellStyle);
			headerCell4.setCellValue("공개구분");
			sheet.setColumnWidth(4, 10*256);
			
			Cell headerCell5 = headerRow.createCell(5);
			headerCell5.setCellStyle(greyCellStyle);
			headerCell5.setCellValue("방송 시작 일시");
			sheet.setColumnWidth(5, 20*256);
			
			Cell headerCell6 = headerRow.createCell(6);
			headerCell6.setCellStyle(greyCellStyle);
			headerCell6.setCellValue("방송 종료 일시");
			sheet.setColumnWidth(6, 20*256);
			
			Cell headerCell7 = headerRow.createCell(7);
			headerCell7.setCellStyle(greyCellStyle);
			headerCell7.setCellValue("실제 방송 시간");
			sheet.setColumnWidth(7, 20*256);
			
			Cell headerCell8 = headerRow.createCell(8);
			headerCell8.setCellStyle(greyCellStyle);
			headerCell8.setCellValue("예상 방송 시간");
			sheet.setColumnWidth(8, 15*256);
			
			Cell headerCell9 = headerRow.createCell(9);
			headerCell9.setCellStyle(greyCellStyle);
			headerCell9.setCellValue("등록자");
			sheet.setColumnWidth(9, 20*256);
			
			Cell headerCell10 = headerRow.createCell(10);
			headerCell10.setCellStyle(greyCellStyle);
			headerCell10.setCellValue("등록일");
			sheet.setColumnWidth(10, 20*256);
			
			int list_size = list.size();
			for(AppBroadcastVO vo : list) {
				Row bodyRow = sheet.createRow(rowIndex ++);
				Cell bodyCell0 = bodyRow.createCell(0);
				Cell bodyCell1 = bodyRow.createCell(1);
				Cell bodyCell2 = bodyRow.createCell(2);
				Cell bodyCell3 = bodyRow.createCell(3);
				Cell bodyCell4 = bodyRow.createCell(4);
				Cell bodyCell5 = bodyRow.createCell(5);
				Cell bodyCell6 = bodyRow.createCell(6);
				Cell bodyCell7 = bodyRow.createCell(7);
				Cell bodyCell8 = bodyRow.createCell(8);
				Cell bodyCell9 = bodyRow.createCell(9);
				Cell bodyCell10 = bodyRow.createCell(10);
				
				bodyCell0.setCellValue(list_size - (rowIndex-2));
				bodyCell1.setCellValue(vo.getLcName() == null ? "VOD" : vo.getLcName());
				bodyCell2.setCellValue(vo.getLbTitle());
				String statusName = "";
				switch (vo.getLbStatus()) {
					case "0":
						statusName="대기"; break;
					case "1":
						statusName="방송중"; break;
					case "2":
						statusName="종료"; break;
					case "3":
						statusName="일시정지"; break;
					case "4":
						statusName="재시작"; break;
					case "5":
						statusName="녹화"; break;
					case "9":
						statusName="오류"; break;
				}
				bodyCell3.setCellValue(statusName);
				bodyCell4.setCellValue(vo.getLbOpenYn().equalsIgnoreCase("Y") == true ? "공개" : "비공개");
				bodyCell5.setCellValue(vo.getLbStartDate());
				bodyCell6.setCellValue(vo.getLbEndDate());
				bodyCell7.setCellValue(vo.getPassDuration());
				bodyCell8.setCellValue(vo.getTotalDuration());
				bodyCell9.setCellValue(vo.getRegUserName());
				bodyCell10.setCellValue(vo.getLbRegDate());
				
				bodyCell0.setCellStyle(bodyCellStyle);
				bodyCell1.setCellStyle(bodyCellStyle);
				bodyCell2.setCellStyle(bodyCellStyle);
				bodyCell3.setCellStyle(bodyCellStyle);
				bodyCell4.setCellStyle(bodyCellStyle);
				bodyCell5.setCellStyle(bodyCellStyle);
				bodyCell6.setCellStyle(bodyCellStyle);
				bodyCell7.setCellStyle(bodyCellStyle);
				bodyCell8.setCellStyle(bodyCellStyle);
				bodyCell9.setCellStyle(bodyCellStyle);
				bodyCell10.setCellStyle(bodyCellStyle);
			}
			
			res.setHeader("Set-Cookie", "fileDownload=true; path=/");
			//res.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\";");
			res.setHeader("Content-Transfer-Encoding", "binary");
			
			workbook.write(res.getOutputStream());
			workbook.close();
			
		} catch(IOException ioe) {
			logger.error(ioe.getMessage());
			for(StackTraceElement st : ioe.getStackTrace()) {
				if(st.toString().startsWith("com.kdy.app")) {
					logger.error(st.toString());
				}
			}
		} finally {
			if(workbook != null) {
				workbook.close();
			}
		}
	}
	
	public void liveBroadcast(List<AppBroadcastVO> excelList, String sheetName, HttpServletRequest req, HttpServletResponse res) throws IOException {
		
		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
		Date cur_date = new Date();
		String cur_date_str = fmt.format(cur_date);
		
		// 파일명
		String fileName = sheetName + "_" + cur_date_str + ".xlsx";
		
		// 헤더설정
		/*
		String userAgent = req.getHeader("User-Agent");
		if (userAgent.indexOf("MSIE") > -1) {
			fileName = URLEncoder.encode(fileName, "euc-kr");
		} else {
			fileName = new String(fileName.getBytes("euc-kr"), "iso-8859-1");
		}
		*/
		Workbook workbook = null;
		
		try {
			// 엑셀 파일 생성
			workbook = new SXSSFWorkbook();
			
			// 엑셀 파일 내부에 Sheet 생성
			Sheet sheet = workbook.createSheet(sheetName);
			
			// 엑셀 랜더링에 필요한 DTO 
			List<AppBroadcastVO> list = excelList;
			
			//CellStyle 설정
			CellStyle greyCellStyle = workbook.createCellStyle();
			applyCellStyle(greyCellStyle, new Color(231, 234, 236));
			
			CellStyle numberCellStyle = workbook.createCellStyle();
			applyCellStyle(applyCellFormat(workbook, numberCellStyle), new Color(231, 234, 236));
			
			CellStyle bodyCellStyle = workbook.createCellStyle();
			applyCellStyle(bodyCellStyle, new Color(255,255,255));
			
			// 헤더 생성
			int rowIndex = 0;
			Row headerRow = sheet.createRow(rowIndex ++);
			
			Cell headerCell0 = headerRow.createCell(0);
			headerCell0.setCellStyle(numberCellStyle);
			headerCell0.setCellValue("순번");
			sheet.setColumnWidth(0, 15*256);
			
			Cell headerCell1 = headerRow.createCell(1);
			headerCell1.setCellStyle(greyCellStyle);
			headerCell1.setCellValue("채널");
			sheet.setColumnWidth(1, 25*256);
			
			Cell headerCell2 = headerRow.createCell(2);
			headerCell2.setCellStyle(greyCellStyle);
			headerCell2.setCellValue("방송제목");
			sheet.setColumnWidth(2, 80*256);
			
			Cell headerCell3 = headerRow.createCell(3);
			headerCell3.setCellStyle(greyCellStyle);
			headerCell3.setCellValue("상태");
			sheet.setColumnWidth(3, 10*256);
			
			Cell headerCell4 = headerRow.createCell(4);
			headerCell4.setCellStyle(greyCellStyle);
			headerCell4.setCellValue("공개구분");
			sheet.setColumnWidth(4, 10*256);
			
			Cell headerCell5 = headerRow.createCell(5);
			headerCell5.setCellStyle(greyCellStyle);
			headerCell5.setCellValue("방송 시작 일시");
			sheet.setColumnWidth(5, 20*256);
			
			Cell headerCell6 = headerRow.createCell(6);
			headerCell6.setCellStyle(greyCellStyle);
			headerCell6.setCellValue("방송 종료 일시");
			sheet.setColumnWidth(6, 20*256);
			
			Cell headerCell7 = headerRow.createCell(7);
			headerCell7.setCellStyle(greyCellStyle);
			headerCell7.setCellValue("실제 방송 시간");
			sheet.setColumnWidth(7, 20*256);
			
			Cell headerCell8 = headerRow.createCell(8);
			headerCell8.setCellStyle(greyCellStyle);
			headerCell8.setCellValue("예상 방송 시간");
			sheet.setColumnWidth(8, 15*256);
			
			Cell headerCell9 = headerRow.createCell(9);
			headerCell9.setCellStyle(greyCellStyle);
			headerCell9.setCellValue("등록자");
			sheet.setColumnWidth(9, 20*256);
			
			Cell headerCell10 = headerRow.createCell(10);
			headerCell10.setCellStyle(greyCellStyle);
			headerCell10.setCellValue("등록일");
			sheet.setColumnWidth(10, 20*256);
			
			int list_size = list.size();
			for(AppBroadcastVO vo : list) {
				Row bodyRow = sheet.createRow(rowIndex ++);
				Cell bodyCell0 = bodyRow.createCell(0);
				Cell bodyCell1 = bodyRow.createCell(1);
				Cell bodyCell2 = bodyRow.createCell(2);
				Cell bodyCell3 = bodyRow.createCell(3);
				Cell bodyCell4 = bodyRow.createCell(4);
				Cell bodyCell5 = bodyRow.createCell(5);
				Cell bodyCell6 = bodyRow.createCell(6);
				Cell bodyCell7 = bodyRow.createCell(7);
				Cell bodyCell8 = bodyRow.createCell(8);
				Cell bodyCell9 = bodyRow.createCell(9);
				Cell bodyCell10 = bodyRow.createCell(10);
				
				bodyCell0.setCellValue(list_size - (rowIndex-2));
				bodyCell1.setCellValue(vo.getLcName() == null ? "VOD" : vo.getLcName());
				bodyCell2.setCellValue(vo.getLbTitle());
				String statusName = "";
				switch (vo.getLbStatus()) {
					case "0":
						statusName="대기"; break;
					case "1":
						statusName="방송중"; break;
					case "2":
						statusName="종료"; break;
					case "3":
						statusName="일시정지"; break;
					case "4":
						statusName="재시작"; break;
					case "5":
						statusName="녹화"; break;
					case "9":
						statusName="오류"; break;
				}
				bodyCell3.setCellValue(statusName);
				bodyCell4.setCellValue(vo.getLbOpenYn().equalsIgnoreCase("Y") == true ? "공개" : "비공개");
				bodyCell5.setCellValue(vo.getLbStartDate());
				bodyCell6.setCellValue(vo.getLbEndDate());
				bodyCell7.setCellValue(vo.getPassDuration());
				bodyCell8.setCellValue(vo.getTotalDuration());
				bodyCell9.setCellValue(vo.getRegUserName());
				bodyCell10.setCellValue(vo.getLbRegDate());
				
				bodyCell0.setCellStyle(bodyCellStyle);
				bodyCell1.setCellStyle(bodyCellStyle);
				bodyCell2.setCellStyle(bodyCellStyle);
				bodyCell3.setCellStyle(bodyCellStyle);
				bodyCell4.setCellStyle(bodyCellStyle);
				bodyCell5.setCellStyle(bodyCellStyle);
				bodyCell6.setCellStyle(bodyCellStyle);
				bodyCell7.setCellStyle(bodyCellStyle);
				bodyCell8.setCellStyle(bodyCellStyle);
				bodyCell9.setCellStyle(bodyCellStyle);
				bodyCell10.setCellStyle(bodyCellStyle);
			}
			
			//브라우저별 파일명 처리
			encodeFileNameForBrowser(req, res, fileName);
			res.setHeader("Set-Cookie", "fileDownload=true; path=/");
			//res.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\";");
			res.setHeader("Content-Transfer-Encoding", "binary");
			res.setHeader("Content-Description", "excel download");
			
			workbook.write(res.getOutputStream());
			workbook.close();
			
		} catch(IOException ioe) {
			logger.error(ioe.getMessage());
			for(StackTraceElement st : ioe.getStackTrace()) {
				if(st.toString().startsWith("com.kdy.app")) {
					logger.error(st.toString());
				}
			}
		} finally {
			if(workbook != null) {
				workbook.close();
			}
		}
	}
	
	
	//VOD list Excel Download
	public void vodExcelDown(List<VodVO> vodList, HttpServletRequest req, HttpServletResponse res) throws Exception{
		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
		Date cur_date = new Date();
		String cur_date_str = fmt.format(cur_date);
		String sheetName = "VOD_리스트";
		
		// 파일명
		String fileName = sheetName + "_" + cur_date_str + ".xlsx";
		
		Workbook workbook = null;
		
		try {
			// 엑셀 파일 생성
			workbook = new SXSSFWorkbook();
			
			// 엑셀 파일 내부에 Sheet 생성
			Sheet sheet = workbook.createSheet(sheetName);
			
			//CellStyle 설정
			CellStyle greyCellStyle = workbook.createCellStyle();
			applyCellStyle(greyCellStyle, new Color(231, 234, 236));
			
			CellStyle numberCellStyle = workbook.createCellStyle();
			applyCellStyle(applyCellFormat(workbook, numberCellStyle), new Color(231, 234, 236));
			
			CellStyle bodyCellStyle = workbook.createCellStyle();
			applyCellStyle(bodyCellStyle, new Color(255,255,255));
			
			// 헤더 생성
			int rowIndex = 0;
			Row headerRow = sheet.createRow(rowIndex ++);
			
			Cell headerCell0 = headerRow.createCell(0);
			headerCell0.setCellStyle(numberCellStyle);
			headerCell0.setCellValue("순번");
			sheet.setColumnWidth(0, 10*256);
			
			Cell headerCell1 = headerRow.createCell(1);
			headerCell1.setCellStyle(greyCellStyle);
			headerCell1.setCellValue("카테고리");
			sheet.setColumnWidth(1, 40*256);
			
			Cell headerCell2 = headerRow.createCell(2);
			headerCell2.setCellStyle(greyCellStyle);
			headerCell2.setCellValue("제목");
			sheet.setColumnWidth(2, 50*256);
			
			Cell headerCell3 = headerRow.createCell(3);
			headerCell3.setCellStyle(greyCellStyle);
			headerCell3.setCellValue("원본파일명");
			sheet.setColumnWidth(3, 40*256);
			
			Cell headerCell4 = headerRow.createCell(4);
			headerCell4.setCellStyle(greyCellStyle);
			headerCell4.setCellValue("인코딩파일명");
			sheet.setColumnWidth(4, 40*256);
			
			Cell headerCell5 = headerRow.createCell(5);
			headerCell5.setCellStyle(greyCellStyle);
			headerCell5.setCellValue("원본용량");
			sheet.setColumnWidth(5, 15*256);
			
			Cell headerCell6 = headerRow.createCell(6);
			headerCell6.setCellStyle(greyCellStyle);
			headerCell6.setCellValue("인코딩용량");
			sheet.setColumnWidth(6, 15*256);
			
			Cell headerCell7 = headerRow.createCell(7);
			headerCell7.setCellStyle(greyCellStyle);
			headerCell7.setCellValue("변환상태");
			sheet.setColumnWidth(7, 20*256);
			
			Cell headerCell8 = headerRow.createCell(8);
			headerCell8.setCellStyle(greyCellStyle);
			headerCell8.setCellValue("등록자");
			sheet.setColumnWidth(8, 20*256);
			
			Cell headerCell9 = headerRow.createCell(9);
			headerCell9.setCellStyle(greyCellStyle);
			headerCell9.setCellValue("등록일시");
			sheet.setColumnWidth(9, 30*256);
			
			int list_size = vodList.size();
			for(VodVO vo : vodList) {
				Row bodyRow = sheet.createRow(rowIndex ++);
				Cell bodyCell0 = bodyRow.createCell(0);
				Cell bodyCell1 = bodyRow.createCell(1);
				Cell bodyCell2 = bodyRow.createCell(2);
				Cell bodyCell3 = bodyRow.createCell(3);
				Cell bodyCell4 = bodyRow.createCell(4);
				Cell bodyCell5 = bodyRow.createCell(5);
				Cell bodyCell6 = bodyRow.createCell(6);
				Cell bodyCell7 = bodyRow.createCell(7);
				Cell bodyCell8 = bodyRow.createCell(8);
				Cell bodyCell9 = bodyRow.createCell(9);
				
				bodyCell0.setCellValue(list_size - (rowIndex-2));
				bodyCell1.setCellValue(vo.getFullCategory());
				bodyCell2.setCellValue(vo.getVodTitle());
				bodyCell3.setCellValue(vo.getOriginalFileName());
				bodyCell4.setCellValue(vo.getEncodingFileName());
				bodyCell5.setCellValue(vo.getOriginalFileSize());
				bodyCell6.setCellValue(vo.getEncodingFileSize());
				
				String status = "";
				switch (vo.getVodStatus()) {
				case "0":	status="대기"; break;
				case "1":	status="성공"; break;
				case "2":	status="임시"; break;
				case "3":	status="인코딩중"; break;
				case "4":	status="실패"; break;
				}
				bodyCell7.setCellValue(status);
				bodyCell8.setCellValue(vo.getRegUserName());
				bodyCell9.setCellValue(vo.getRegDate());
				
				bodyCell0.setCellStyle(bodyCellStyle);
				bodyCell1.setCellStyle(bodyCellStyle);
				bodyCell2.setCellStyle(bodyCellStyle);
				bodyCell3.setCellStyle(bodyCellStyle);
				bodyCell4.setCellStyle(bodyCellStyle);
				bodyCell5.setCellStyle(bodyCellStyle);
				bodyCell6.setCellStyle(bodyCellStyle);
				bodyCell7.setCellStyle(bodyCellStyle);
				bodyCell8.setCellStyle(bodyCellStyle);
				bodyCell9.setCellStyle(bodyCellStyle);
			}
			
			//브라우저별 파일명 처리
			encodeFileNameForBrowser(req, res, fileName);
			res.setHeader("Set-Cookie", "fileDownload=true; path=/");
			//res.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\";");
			res.setHeader("Content-Transfer-Encoding", "binary");
			res.setHeader("Content-Description", "excel download");
			
			workbook.write(res.getOutputStream());
			workbook.close();
			
		} catch(IOException ioe) {
			logger.error(ioe.getMessage());
			for(StackTraceElement st : ioe.getStackTrace()) {
				if(st.toString().startsWith("com.kdy.app")) {
					logger.error(st.toString());
				}
			}
		} finally {
			if(workbook != null) {
				workbook.close();
			}
		}
		
	}
	
	public void liveChat(List<ChatDTO> chatList, String sheetName, HttpServletRequest req, HttpServletResponse res) throws IOException {
		
		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
		Date cur_date = new Date();
		String cur_date_str = fmt.format(cur_date);
		
		String fileName = sheetName + "_" + cur_date_str + ".xlsx";
		if(chatList != null && chatList.size() > 0) {
			fileName = chatList.get(0).getLbTitle() + "_채팅기록_" + cur_date_str + ".xlsx";
		}
		
		// 헤더설정
		/*
		String userAgent = req.getHeader("User-Agent");
		if (userAgent.indexOf("MSIE") > -1) {
			fileName = URLEncoder.encode(fileName, "euc-kr");
		} else {
			fileName = new String(fileName.getBytes("euc-kr"), "iso-8859-1");
		}
		*/
		Workbook workbook = null;
		
		try {
			// 엑셀 파일 생성
			workbook = new SXSSFWorkbook();
			
			// 엑셀 파일 내부에 Sheet 생성
			Sheet sheet = workbook.createSheet(sheetName);
			
			// 엑셀 랜더링에 필요한 DTO 
			List<ChatDTO> list = chatList;
			
			//CellStyle 설정
			CellStyle greyCellStyle = workbook.createCellStyle();
			applyCellStyle(greyCellStyle, new Color(231, 234, 236));
			
			CellStyle numberCellStyle = workbook.createCellStyle();
			applyCellStyle(applyCellFormat(workbook, numberCellStyle), new Color(231, 234, 236));
			
			CellStyle bodyCellStyle = workbook.createCellStyle();
			applyCellStyle(bodyCellStyle, new Color(255,255,255));
			
			// 헤더 생성
			int rowIndex = 0;
			Row headerRow = sheet.createRow(rowIndex ++);
			
			Cell headerCell0 = headerRow.createCell(0);
			headerCell0.setCellStyle(numberCellStyle);
			headerCell0.setCellValue("순번");
			sheet.setColumnWidth(0, 15*256);
			
			Cell headerCell1 = headerRow.createCell(1);
			headerCell1.setCellStyle(greyCellStyle);
			headerCell1.setCellValue("아이디");
			sheet.setColumnWidth(1, 20*256);
			
			Cell headerCell2 = headerRow.createCell(2);
			headerCell2.setCellStyle(greyCellStyle);
			headerCell2.setCellValue("이름");
			sheet.setColumnWidth(2, 20*256);
			
			Cell headerCell3 = headerRow.createCell(3);
			headerCell3.setCellStyle(greyCellStyle);
			headerCell3.setCellValue("채팅일시");
			sheet.setColumnWidth(3, 25*256);
			
			Cell headerCell4 = headerRow.createCell(4);
			headerCell4.setCellStyle(greyCellStyle);
			headerCell4.setCellValue("채팅내용");
			sheet.setColumnWidth(4, 100*256);
			
			for(ChatDTO vo : list) {
				Row bodyRow = sheet.createRow(rowIndex ++);
				Cell bodyCell0 = bodyRow.createCell(0);
				Cell bodyCell1 = bodyRow.createCell(1);
				Cell bodyCell2 = bodyRow.createCell(2);
				Cell bodyCell3 = bodyRow.createCell(3);
				Cell bodyCell4 = bodyRow.createCell(4);
				
				bodyCell0.setCellValue(rowIndex - 1);
				bodyCell1.setCellValue(vo.getUserId());
				bodyCell2.setCellValue(vo.getUserName());
				bodyCell3.setCellValue(vo.getDate());
				bodyCell4.setCellValue(vo.getContent());
				
				bodyCell0.setCellStyle(bodyCellStyle);
				bodyCell1.setCellStyle(bodyCellStyle);
				bodyCell2.setCellStyle(bodyCellStyle);
				bodyCell3.setCellStyle(bodyCellStyle);
				bodyCell4.setCellStyle(bodyCellStyle);
			}
			
			//브라우저별 파일명 처리
			encodeFileNameForBrowser(req, res, fileName);
			res.setHeader("Set-Cookie", "fileDownload=true; path=/");
			//res.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\";");
			res.setHeader("Content-Transfer-Encoding", "binary");
			res.setHeader("Content-Description", "excel download");
			
			workbook.write(res.getOutputStream());
			workbook.close();
			
		} catch(IOException ioe) {
			logger.error(ioe.getMessage());
			for(StackTraceElement st : ioe.getStackTrace()) {
				if(st.toString().startsWith("com.kdy.app")) {
					logger.error(st.toString());
				}
			}
		} finally {
			if(workbook != null) {
				workbook.close();
			}
		}
	}

	
	private void applyCellStyle(CellStyle cellStyle, Color color) {
	  XSSFCellStyle xssfCellStyle = (XSSFCellStyle) cellStyle;
	  xssfCellStyle.setFillForegroundColor(new XSSFColor(color, new DefaultIndexedColorMap()));
	  cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	  cellStyle.setAlignment(HorizontalAlignment.CENTER);
	  cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
	  cellStyle.setBorderLeft(BorderStyle.THIN);
	  cellStyle.setBorderTop(BorderStyle.THIN);
	  cellStyle.setBorderRight(BorderStyle.THIN);
	  cellStyle.setBorderBottom(BorderStyle.THIN);
	}
	
	private XSSFCellStyle applyCellFormat(Workbook workbook, CellStyle cellStyle) {
		XSSFDataFormat format = (XSSFDataFormat) workbook.createDataFormat();
		XSSFCellStyle numberStyle = (XSSFCellStyle) cellStyle;
		numberStyle.setDataFormat(format.getFormat("#,###"));
		
		return numberStyle;
	}
	
	private void encodeFileNameForBrowser(HttpServletRequest request, HttpServletResponse response, String name) throws UnsupportedEncodingException {
		// 브라우저 별 한글 인코딩
		String header = request.getHeader("User-Agent");
		if (header.contains("Edge")){
		    name = URLEncoder.encode(name, "UTF-8").replaceAll("\\+", "%20");
		} else if (header.contains("MSIE") || header.contains("Trident")) { // IE 11버전부터 Trident로 변경되었기때문에 추가해준다.
		    name = URLEncoder.encode(name, "UTF-8").replaceAll("\\+", "%20");
		} else if (header.contains("Chrome")) {
			StringBuffer sb = new StringBuffer();
			for(int i=0; i<name.length(); i++) {
				char c = name.charAt(i);
				if (c > '~') {
					sb.append(URLEncoder.encode(Character.toString(c), "UTF-8"));
				}else {
					sb.append(c);
				}
			}
			name = sb.toString();
		} else if (header.contains("Opera")) {
		    name = new String(name.getBytes("UTF-8"), "ISO-8859-1");
		} else if (header.contains("Firefox")) {
		    name = new String(name.getBytes("UTF-8"), "ISO-8859-1");
		} else {
			name = new String(name.getBytes("UTF-8"), "iso-8859-1");
		}
		response.setHeader("Content-Disposition", "attachment; filename=\"" + name + "\"");
	}
}
