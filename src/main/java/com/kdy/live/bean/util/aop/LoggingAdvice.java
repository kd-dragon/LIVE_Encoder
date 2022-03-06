package com.kdy.live.bean.util.aop;

import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Component
public class LoggingAdvice {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	//@Around("within(com.kdy.live.sched.*) or @annotation(com.kdy.live.bean.util.annotation.StopWatch)")
	@Around("@annotation(com.kdy.live.bean.util.annotation.StopWatch)")
	public Object schedStopWatch(ProceedingJoinPoint jointPoint) throws Throwable {
		StopWatch stopWatch = new StopWatch();
		Object proceed = null;
		//String type = jointPoint.getSignature().getDeclaringTypeName();
		
		try {
			stopWatch.start();
			
			proceed = jointPoint.proceed();
			
		} finally {
			stopWatch.stop();
			logger.debug(String.format("%s Duration Time [%s]", jointPoint.getSignature().getName(), TimeUnit.SECONDS.convert(stopWatch.getTotalTimeNanos(), TimeUnit.NANOSECONDS) + "s"));
			//logger.info(type + "." + jointPoint.getSignature().getName() + " Schedule Elapsed Time [" + TimeUnit.SECONDS.convert(stopWatch.getTotalTimeNanos(), TimeUnit.NANOSECONDS) + "s");
			//logger.info(" #######getTotalTimeMillis######## " + stopWatch.getTotalTimeMillis() + "s");
			//logger.info(" #######getTotalTimeNanos######## " + stopWatch.getTotalTimeNanos() + "s");
			//logger.info(" #######getLastTaskName######## " + stopWatch.getLastTaskName() + "s");
			//logger.info(" #######prettyPrintshortSummary######## " + stopWatch.prettyPrint());
			//logger.info(" #######shortSummary######## " + stopWatch.shortSummary());
			//logger.info(" #######currentTaskName######## " + stopWatch.currentTaskName());
		}
		return proceed;
	}
}


