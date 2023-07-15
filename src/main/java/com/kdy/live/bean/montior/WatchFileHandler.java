package com.kdy.live.bean.montior;

import java.io.File;
import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import com.kdy.live.bean.util.code.StreamType;
import com.kdy.live.dto.LiveSchedMemoryVO;
import com.kdy.live.dto.LiveSchedMemoryVO.RedisHashKeyword;
import com.kdy.live.dto.live.LiveBroadcastVO;

@Component
@RequiredArgsConstructor
public class WatchFileHandler {
	
	private Logger logger = LoggerFactory.getLogger("ffmpeg");

	@Qualifier("redisTemplate")
	private final RedisTemplate<String, Object> template;

	@Qualifier("redisTemplateObject")
	private final RedisTemplate<String, Object> recordTemplate;
	
	private final LiveSchedMemoryVO memoryVO;

	
	/**
	 * @author KDY
	 * 
	 * HLS 인코딩 파일 감지 및 Redis PUT 처리
	 * (Apache Commons IO File Monitoring)
	 * 
	 * 1) 감시할 디렉토리 경로 지정 및 감시 필터 설정
	 * 2) 파일 관찰자 객체 지정 및 리스너 추가 
	 * 3) 설정한 관찰자로 모니터링 시작 및 종료
	 *
	 */
	public void monitorHLS(LiveBroadcastVO lbvo) throws Exception {
		logger.info("Start Monitoring >> " + lbvo.getLbTitle());
		
		// 감시할 디렉토리 경로에 파일 객체 생성 (HLS 인코딩 Output 경로)
		File directory = new File(lbvo.getHlsFilePath());
		
		// 감시할 파일 필터 지정 (m3u8/ts 확장자만 감시)
		IOFileFilter m3u8_file = FileFilterUtils.and(FileFilterUtils.fileFileFilter()
									, FileFilterUtils.suffixFileFilter(".m3u8", IOCase.INSENSITIVE));
		IOFileFilter ts_files = FileFilterUtils.and(FileFilterUtils.fileFileFilter()
									, FileFilterUtils.suffixFileFilter(".ts", IOCase.INSENSITIVE));
		// m3u8, ts 확장자 감시 필터 각각 생성 후 합집합 처리
		IOFileFilter filter = FileFilterUtils.or(m3u8_file, ts_files);
		
		
		// Apache Commons IO 파일 관찰 객체 생성
		FileAlterationObserver observer = new FileAlterationObserver(directory, filter);
		// 사용자 정의 Listener 추가 (파일 변경 감지시 처리할 로직 구현)
		observer.addListener(new WatchFileListener(lbvo, template));
		
		// Apache Common IO 파일 모니터링 객체 생성 (1초 간격으로 Polling)
		FileAlterationMonitor monitor = new FileAlterationMonitor(1000L); //1초
		monitor.addObserver(observer);
		
		try {
			// 모니터링 작업 시작
			monitor.start();
			lbvo.setWatchMonitor(monitor);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error : {}", e);
			
			// 작업 도중 오류 발생시 종료 처리
			lbvo.setWatchMonitor(null);
			monitor.stop();
		} 
	}
	
	//적응형 스트리밍 적용 X
	public void monitoring(LiveBroadcastVO lbvo) {
		logger.info("Start Monitoring >> " + lbvo.getLbTitle());
		
		File directory = new File(lbvo.getHlsFilePath());
		
		logger.info("directory info >> " + lbvo.getHlsFilePath());
		
		//file filter
		IOFileFilter m3u8_file = FileFilterUtils.and(FileFilterUtils.fileFileFilter()
									, FileFilterUtils.suffixFileFilter(".m3u8", IOCase.INSENSITIVE));
		IOFileFilter ts_files = FileFilterUtils.and(FileFilterUtils.fileFileFilter()
									, FileFilterUtils.suffixFileFilter(".ts", IOCase.INSENSITIVE));

		IOFileFilter filter = FileFilterUtils.or(m3u8_file, ts_files);
		
		FileAlterationObserver observer = new FileAlterationObserver(directory, filter);
		FileAlterationMonitor monitor = new FileAlterationMonitor(1000L); //1초
		
		if(lbvo.getLbVodSaveYn() != null && lbvo.getLbVodSaveYn().equalsIgnoreCase("Y")) {
			
			ValueOperations<String, Object> valueOperation = recordTemplate.opsForValue();
			String key = RedisHashKeyword.RECENABLE.toString() + lbvo.getLbSeq();
			if(valueOperation.get(key) == null) {
				valueOperation.set(key, "N", 3, TimeUnit.DAYS);
			}
			
			if(memoryVO.getChatRecYn().equalsIgnoreCase("Y")){
				//채팅 사용할 경우
				if(lbvo.getLbChatYn().equalsIgnoreCase("Y")) {
					String key2 = RedisHashKeyword.RECSTARTDATE.toString() + lbvo.getLbSeq();
					String key3 = RedisHashKeyword.RECDURATION.toString() + lbvo.getLbSeq();
					
					if(valueOperation.get(key2) == null) {
						valueOperation.set(key2, 0, 3, TimeUnit.DAYS);
					}
					
					if(valueOperation.get(key3) == null) {
						valueOperation.set(key3, 0, 3, TimeUnit.DAYS);
					}
				}
			}
			
			observer.addListener(new WatchFileListenerRecord(memoryVO, lbvo, template, recordTemplate));
			
		} else {
			observer.addListener(new WatchFileListener(lbvo, template));
		}
		
		monitor.addObserver(observer);
		
		try {
			monitor.start();
			lbvo.setWatchMonitor(monitor);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			
			try {
				monitor.stop();
			} catch (Exception e2) {
				e2.printStackTrace();
			} finally {
				lbvo.setWatchMonitor(null);
			}
		} 
	}
	
	//적응형 스트리밍 적용 O
	public void adaptiveMonitoring(LiveBroadcastVO lbvo) {
		logger.info("Start adaptiveMonitoring >> " + lbvo.getLbTitle());
		
		//adaptive >> basic
		File highDirectory = new File(lbvo.getHlsFilePath()+ StreamType.high.ordinal());
		File lowDirectory = new File(lbvo.getHlsFilePath()+ StreamType.low.ordinal());
		File midDirectory = null;
		
		//adaptive >> advance
		if(memoryVO.getEncodingType().equals("advance")) {
			midDirectory = new File(lbvo.getHlsFilePath() + StreamType.mid.ordinal());
		}
		
		logger.info("adaptiveDirectory info >> " + lbvo.getHlsFilePath());
		
		//file filter
		IOFileFilter m3u8_file = FileFilterUtils.and(FileFilterUtils.fileFileFilter()
				, FileFilterUtils.suffixFileFilter(".m3u8", IOCase.INSENSITIVE));
		IOFileFilter ts_files = FileFilterUtils.and(FileFilterUtils.fileFileFilter()
				, FileFilterUtils.suffixFileFilter(".ts", IOCase.INSENSITIVE));
		
		IOFileFilter filter = FileFilterUtils.or(m3u8_file, ts_files);
		
		FileAlterationObserver highObserver = new FileAlterationObserver(highDirectory, filter);
		FileAlterationObserver lowObserver = new FileAlterationObserver(lowDirectory, filter);
		
		FileAlterationMonitor monitor = new FileAlterationMonitor(1000L); //1초
		
		highObserver.addListener(new AdaptiveWatchFileListener(memoryVO, lbvo, template));
		lowObserver.addListener(new AdaptiveWatchFileListener(memoryVO, lbvo, template));
		
		monitor.addObserver(highObserver);
		monitor.addObserver(lowObserver);
		
		//adaptive >> advance : basic(high, low) + mid
		if(memoryVO.getEncodingType().equals("advance")) {
			FileAlterationObserver midObserver = new FileAlterationObserver(midDirectory, filter);
			midObserver.addListener(new AdaptiveWatchFileListener(memoryVO, lbvo, template));
			monitor.addObserver(midObserver);
		}
		
		try {
			monitor.start();
			lbvo.setWatchMonitor(monitor);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			
			try {
				monitor.stop();
			} catch (Exception e2) {
				e2.printStackTrace();
			} finally {
				lbvo.setWatchMonitor(null);
			}
		} 
	}
}
