package com.kdy.live.sched.conf;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;

@Configuration
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "10s")
public class SchedulerConfig implements SchedulingConfigurer {
	
	private final int POOL_SIZE = 5;
	Logger logger = LoggerFactory.getLogger(SchedulerConfig.class);
	
	@Bean
	public LockProvider lockProvider(DataSource dataSource) {
		return new JdbcTemplateLockProvider(
			JdbcTemplateLockProvider.Configuration.builder()
			.withJdbcTemplate(new JdbcTemplate(dataSource))
			.usingDbTime()
			.build()
		);
	}
	
	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		logger.info("** POOL_SIZE : "+POOL_SIZE);
		ThreadPoolTaskScheduler threadPool = new ThreadPoolTaskScheduler();
		threadPool.setPoolSize(POOL_SIZE);
		threadPool.initialize();
		taskRegistrar.setTaskScheduler(threadPool);
	}
	
	
}
