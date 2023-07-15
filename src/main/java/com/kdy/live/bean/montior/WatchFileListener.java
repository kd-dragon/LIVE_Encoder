package com.kdy.live.bean.montior;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.kdy.live.dto.live.LiveBroadcastVO;

public class WatchFileListener implements FileAlterationListener {
	
	private Logger logger = LoggerFactory.getLogger("ffmpeg");
	
	private final LiveBroadcastVO lbvo;
	private final RedisTemplate<String, Object> template;
	
	public WatchFileListener(LiveBroadcastVO lbvo, RedisTemplate<String, Object> template) {
		this.lbvo = lbvo;
		this.template = template;
	}
	
	/**
	 * In-Memory Key/Value DB Redis 에 .m3u8, .ts파일 저장
	 */
	
	// 파일 변화 감지시 .m3u8, .ts 파일에 대한 처리 구현
	@Override
	public void onFileChange(File file) {
		// key 지정 ( 고유 시퀀스값 + 파일명 )
		String key = lbvo.getLbSeq() + "_" + file.getName();
		//logger.info("########### onFileChange >> key :" + key);
		
		// file 객체를 redis 에 할당
		assignFileToMemoryReplication(key, file);
	}
	
	private void assignFileToMemoryReplication(String key, File file) {
		// Redis의 ValueOperation 사용하여 지정한 key에 byte array 로 변환된 file 저장 ( 60초 후에 만료되도록 설정 )
		ValueOperations<String, Object> valueOperations = template.opsForValue();
		valueOperations.set(key, convertFileToByteArray(file), 60, TimeUnit.SECONDS);
	}
	
	// Apache Commons IO 의 FileUtils 활용하여 file -> byte[]로 변환
	private byte[] convertFileToByteArray(File file) {
		byte[] fileBytes = null;
		
		try {
			fileBytes = FileUtils.readFileToByteArray(file);
		} catch (IOException e) {
			logger.error("error : {}", e);
			e.printStackTrace();
		}
		return fileBytes;
	}
	
	@Override
	public void onDirectoryCreate(File directory) {
	}

	@Override
	public void onDirectoryChange(File directory) {
	}

	@Override
	public void onDirectoryDelete(File directory) {
	}

	@Override
	public void onFileCreate(File file) {
	}


	@Override
	public void onFileDelete(File file) {
	}
	
	/**
	 *  start, stop은 매번 반복되는 함수
	 */
	@Override
	public void onStart(FileAlterationObserver observer) {
	}
	
	@Override
	public void onStop(FileAlterationObserver observer) {
	}
	
}
