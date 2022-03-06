package com.kdy.live.dto.live;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LiveBroadcastEvent extends ApplicationEvent {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final LiveBroadcastVO lbvo;
	
	public LiveBroadcastEvent(Object source, LiveBroadcastVO lbvo) {
		super(source);
		this.lbvo = lbvo;
	}
}
