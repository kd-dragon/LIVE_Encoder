package com.kdy.live.bean.util;

import static com.google.common.base.Preconditions.checkNotNull;
import static net.bramp.ffmpeg.FFmpegUtils.fromTimecode;

import org.apache.commons.lang3.math.Fraction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import net.bramp.ffmpeg.FFmpegUtils;
import net.bramp.ffmpeg.progress.Progress;
import net.bramp.ffmpeg.progress.Progress.Status;

@Component
public class ProgressParseBean {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	public Progress service(String[] lines) {
		
		Progress progress = new Progress();
		
		
		for(String line : lines) {
			if (line.isEmpty()) { continue; }
			
			String[] args = line.split("=", 2);
		    if (args.length != 2) { continue; }
		    
		    String key = checkNotNull(args[0]);
		    String value = checkNotNull(args[1]);
		    
		    switch (key) {
		    
		      case "frame":
		    	  progress.frame = Long.parseLong(value);
		    	  break;
		      case "fps":
	    	  	  progress.fps = Fraction.getFraction(value);
		    	  break;
		      case "bitrate":
		          if (value.equals("N/A")) {
		        	  progress.bitrate = -1;
		          } else {
		        	  progress.bitrate = FFmpegUtils.parseBitrate(value);
		          }
		          break;
	
		      case "total_size":
		          if (value.equals("N/A")) {
		        	  progress.total_size = -1;
		          } else {
		        	  progress.total_size = Long.parseLong(value);
		          }
		          break;
	
		      case "out_time_ms":
		          // This is a duplicate of the "out_time" field, but expressed as a int instead of string.
		          // Note this value is in microseconds, not milliseconds, and is based on AV_TIME_BASE which
		          // could change.
		          // out_time_ns = Long.parseLong(value) * 1000;
		          break;
	
		      case "out_time":
		    	  progress.out_time_ns = fromTimecode(value);
		    	  break;
		    	
		      case "dup_frames":
		    	  progress.dup_frames = Long.parseLong(value);
		    	  break;
	
		      case "drop_frames":
		    	  progress.drop_frames = Long.parseLong(value);
		    	  break;
	
		      case "speed":
		          if (value.equals("N/A")) {
		        	progress.speed = -1;
		          } else {
		        	progress.speed = Float.parseFloat(value.replace("x", ""));
		          }
		          break;
	
		      case "progress":
		        // TODO After "end" stream is closed
		    	  progress.status = Status.of(value);
		          break;
	
		      default:
		          if (key.startsWith("stream_")) {
			          // TODO handle stream_0_0_q=0.0:
			          // stream_%d_%d_q= file_index, index, quality
			          // stream_%d_%d_psnr_%c=%2.2f, file_index, index, type{Y, U, V}, quality // Enable with
			          // AV_CODEC_FLAG_PSNR
			          // stream_%d_%d_psnr_all
		          } else {
		            logger.debug("Skipping unhandled key: {} = {}", key, value);
		          }
		          break;
		    }
		}
		
		return progress;
	}
}
