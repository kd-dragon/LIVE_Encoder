package com.kdy.live.bean.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableList;
import com.kdy.live.bean.encoding.command.FfmpegCommandBuilderIF;
import com.kdy.live.bean.encoding.command.concat.ConcatVod;
import com.kdy.live.bean.util.annotation.StopWatch;
import com.kdy.live.bean.util.code.StreamType;
import com.kdy.live.dto.LiveSchedMemoryVO;
import com.kdy.live.dto.live.LiveBroadcastVO;
import com.kdy.live.dto.netty.NettyVO;

import net.bramp.ffmpeg.builder.FFmpegBuilder;

@Component
public class RecordingFileHandler {
	private Logger logger = LoggerFactory.getLogger("ffmpeg");
	
	private final LiveSchedMemoryVO memoryVO;
	private final NettyVO nettyVO;
	
	@Autowired
	public RecordingFileHandler(LiveSchedMemoryVO memoryVO, NettyVO nettyVO) {
		this.memoryVO = memoryVO;
		this.nettyVO = nettyVO;
	}
	
	/** ## 사용안함 ##
	 * live 방송 종료 후 녹화 파일 인코딩 (AVI -> MP4 방식) 
	 * : 라이브 중 동시에 AVI 파일 생성 방법
	 * @param file
	 * @param lbvo
	 * @return
	 */
	@StopWatch
	public void serviceAVI(LiveBroadcastVO lbvo) throws Exception {
		/*
		File liveTempDir = new File(lbvo.getVodTempSavePath());
		File[] liveFileList = liveTempDir.listFiles();
		String liveListTxtPath = lbvo.getVodTempSavePath() + lbvo.getLbSeq() + "_list.txt";
		FileWriter fileWriter = new FileWriter(liveListTxtPath, true);
		StringBuilder concatBuilder = new StringBuilder();
		
		if(liveFileList != null) {
			int count = liveFileList.length;
			
			//Total Duration 구하기
			calculateTotalDuration(lbvo);
			
			for(int i=0; i<count; i++) {
				// convert avi file to Mp4
				lbvo.setVodFileNum(i);
				convertAviToMp4(liveFileList[i], lbvo);
				
				concatBuilder.append("file \'" + liveFileList[i].getName().replace(".avi", ".mp4") + "\'\n");
			}
			
			fileWriter.write(concatBuilder.toString());
			fileWriter.flush();
			fileWriter.close();
			fileWriter = null;
			
			// 분할 작업 완료 후 전체 처리 작업 체크
			lbvo.setIsTotalProcess(true);
			
			// Concat MP4 Files
			concatVodFiles(lbvo, liveListTxtPath, lbvo.getVodSavePath() + lbvo.getLbTitle() + ".mp4");
				
		} else {
			logger.error("Recording LiveFiles Doesn't Exist");
		}
		*/
	}
	
	/**
	 * 적응형 스트리밍 X
	 * live 방송 종료 후 녹화 처리 (HLS -> MP4 방식)
	 * : 라이브 중에 녹화 X, 종료 후 TS파일 -> MP4
	 * @param lbvo
	 * @return
	 */
	@StopWatch
	public void serviceHLS(LiveBroadcastVO lbvo) throws Exception {
		
		File liveTempDir = new File(lbvo.getHlsFilePath());
		File[] liveFileList = liveTempDir.listFiles();
		String liveListTxtPath = lbvo.getHlsFilePath() + lbvo.getLbSeq() + "_list.txt";
		FileWriter fileWriter = new FileWriter(liveListTxtPath, true);
		StringBuilder concatBuilder = new StringBuilder();
		
		if(liveFileList != null) {
			int count = liveFileList.length;
			
			//Total Duration 구하기
			calculateTotalDuration(lbvo);
			
			// count-2 => m3u8 과 마지막 손상된 ts파일 제외
			for(int i=0; i < count-2 ; i++) {
				// create concat ts.files list
				concatBuilder.append("file \'" + liveFileList[i].getName() + "\'\n");
			}
			fileWriter.write(concatBuilder.toString());
			fileWriter.flush();
			fileWriter.close();
			fileWriter = null;
			
			lbvo.setIsTotalProcess(true);
			
			// Concat MP4 Files
			concatVodFiles(lbvo, liveListTxtPath, lbvo.getVodSavePath() + lbvo.getLbSeq() + "/" + lbvo.getRecordCopyName() + "high.mp4");
				
		} else {
			logger.error("Recording LiveFiles Doesn't Exist");
		}
	}
	
	/**
	 * [Adaptive]
	 * 적응형 스트리밍 O
	 * live 방송 종료 후 녹화 처리 (HLS -> MP4 방식) 
	 * : 라이브 중에 녹화 X, 종료 후 TS파일 -> MP4
	 * @param lbvo
	 * @return
	 */
	@StopWatch
	public void serviceAdaptiveHLS(LiveBroadcastVO lbvo, StreamType streamType) throws Exception {
		
		File liveTempDir = null;
		File[] liveFileList = null;
		FileWriter fileWriter = null;
		String liveListTxtPath = null;
		StringBuilder concatBuilder = new StringBuilder();
		
		
		liveTempDir = new File(lbvo.getHlsFilePath() + streamType.ordinal());
		liveFileList = liveTempDir.listFiles();
		liveListTxtPath = lbvo.getHlsFilePath() + streamType.ordinal() + "/" + lbvo.getLbSeq() + "_list.txt";
		
		if(liveFileList != null) {
			
			fileWriter = new FileWriter(liveListTxtPath, true);
			
			int count = liveFileList.length;
			
			//Total Duration 구하기
			calculateTotalDuration(lbvo);
			
			// count-2 => m3u8 과 마지막 손상된 ts파일 제외
			for(int i=0; i < count-2 ; i++) {
				// create concat ts.files list
				concatBuilder.append("file \'" + liveFileList[i].getName() + "\'\n");
			}
			fileWriter.write(concatBuilder.toString());
			fileWriter.flush();
			fileWriter.close();
			fileWriter = null;
			
			lbvo.setIsTotalProcess(true);
			
			// Concat MP4 Files
			concatVodFiles(lbvo, liveListTxtPath, lbvo.getVodSavePath() + lbvo.getLbSeq() + "/" + lbvo.getRecordCopyName() + streamType + ".mp4");
				
		} else {
			logger.error("Recording LiveFiles Doesn't Exist");
		}
		
	}
	
	public void recordFfmpegLog(RunConsoleRunnable runConsole, String sequence) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String dateString = sdf.format(date);
		
		String line = null;
		BufferedReader stdoutReader = null;
		FileWriter fileWriter = null;
		
		File liveLogDir = new File(memoryVO.getFfmpegLogPath() + "live" + sequence);
		
		if(!liveLogDir.isDirectory()) {
			liveLogDir.mkdirs();
		}
		
		File logFile = new File(liveLogDir + "/" + sequence + "_" + dateString +".log");
		
		stdoutReader = runConsole.getStdout();
		
		while((line = stdoutReader.readLine()) != null) {
			fileWriter = new FileWriter(logFile, true);
			fileWriter.write(line + "\n");
			fileWriter.flush();
			
			fileWriter.close();
			fileWriter = null;
		}
		
	}
	
	private void calculateTotalDuration(LiveBroadcastVO lbvo) {
		
		double totalDuration=0.0;
		
		for(int i=0; i<lbvo.getRecordDurations().size(); i++) {
			totalDuration += lbvo.getRecordDurations().get(i);
		}
		logger.debug("#### calculateTotalDuration ## totalDuration :: " + totalDuration);
		lbvo.setTotalDuration(totalDuration);
	}
	
	/*
	private int convertAviToMp4(File file, LiveBroadcastVO lbvo) {

		FfmpegCommandBuilderIF recordCommand = new ConvertAviToMp4(nettyVO.getFfmpegPort(), file.getAbsolutePath());
		FFmpegBuilder builder = recordCommand.getBuilder();
		List<String> cmdList = ImmutableList.<String>builder().add(memoryVO.getFfmpegPath()).addAll(builder.build()).build();
		StringBuilder strbuild = new StringBuilder();
		for(String str: cmdList) {
			strbuild.append(str);
			strbuild.append(" ");
		}
		// command log
		logger.info("[convertAviToMp4] " + strbuild.toString());
		
		// ffmpeg work queue offer
		nettyVO.getLiveInfoQueue().offer(lbvo);
		
		//ffmpeg 실행
		RunConsoleRunnable runConsole = new RunConsoleRunnable(cmdList, "vodRecording");
		
		long pid = runConsole.getProcessID();
		lbvo.setLbjProcessId(pid + "");
		
		try {
			recordFfmpegLog(runConsole, lbvo.getLbSeq());
		} catch(Exception e) {
			logger.error("### Fail to recording ffmpeg runConsole Logging ###");
			logger.error(e.getMessage());
		}
		
		int retval = runConsole.waitFor();
		
		if(retval != 0) {
			logger.warn("[ERROR] FFMpeg Recording AVI file Convert MP4 File >> " + file.getName());
		}
		
		return retval;
	}
	*/
	
	private int concatVodFiles(LiveBroadcastVO lbvo, String input, String output) {
		FfmpegCommandBuilderIF concatCommand = new ConcatVod(nettyVO.getFfmpegPort(), input, output);
		FFmpegBuilder builder = concatCommand.getBuilder();
		
		List<String> cmdList = ImmutableList.<String>builder().add(memoryVO.getFfmpegPath()).addAll(builder.build()).build();
		StringBuilder strbuild = new StringBuilder();
		for(String str: cmdList) {
			strbuild.append(str);
			strbuild.append(" ");
		}
		// command log
		logger.info("[concatVodFiles] " + strbuild.toString());
		
		String vodSavePath = lbvo.getVodSavePath() + lbvo.getLbSeq();
		
		//VOD 파일 저장 폴더 없을 경우 만들기
		File vodFileDirectory = new File(vodSavePath);
		if(!vodFileDirectory.isDirectory()) {
			vodFileDirectory.mkdirs();
		}
		
		// ffmpeg work queue offer
		nettyVO.getLiveInfoQueue().offer(lbvo);
		
		//ffmpeg 실행
		RunConsoleRunnable runConsole = new RunConsoleRunnable(cmdList, "vodConcating");
		long pid = runConsole.getProcessID();
		lbvo.setLbjProcessId(pid + "");
		
		try {
			recordFfmpegLog(runConsole, lbvo.getLbSeq());
		} catch(Exception e) {
			logger.error("### Fail to recording ffmpeg runConsole Logging ###");
			logger.error(e.getMessage());
		}
		
		int retval = runConsole.waitFor();
		
		if(retval != 0) {
			logger.warn("[ERROR] FFMpeg Concat Recording files >> " + lbvo.getLbSeq());
		}
		
		return retval;
	}
}
