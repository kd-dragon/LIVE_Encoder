package com.kdy.live.dto.vod;

import lombok.Data;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;

@Data
public class VodMetaVO {
	
	private Double metaDuration        = 0.0;	//비디오 재생시간
	private long   metaFileSize        = 0;		//파일크기
	private int    metaWidth           = 0;  	//이미지 넓이
	private int    metaHeight          = 0;  	//이미지 높이
	private Double metaFps             = 0.0;	//비디오 프레임율
	private String metaDisplayRatio    = "";	//비디오 화면비
	private String metaVideoCodec      = ""; 	//비디오 코덱
	private long   metaVideoBps        = 0;  	//비디오 대역폭
	private String metaAudioCodec      = ""; 	//오디오 코덱
	private long   metaAudioBps        = 0;  	//오디오 대역폭
	private int    metaAudioSampleRate = 0;  	//오디오 샘플속도
	private int    metaAudioSampleSize = 0;  	//오디오 샘플크기
	private int    metaAudioChannels   = 0;  	//오디오 채널
	private String codecType           = "";
	private String formatName          = "";
	
	private FFmpegProbeResult fpResult;
	
	private String lbSeq; //VOD 시퀀스 (mediaId)

	private String vodFileSeq;
	private String vodSavePath;
	private String encodeFileName;
	private String Quality;
	private String preset;
	private String vodSeq;
}
