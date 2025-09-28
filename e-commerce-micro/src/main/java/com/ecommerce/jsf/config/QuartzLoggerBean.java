package com.ecommerce.jsf.config;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.DependsOn;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import java.net.InetAddress;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@DependsOn("FlywayMigrationBean")
@Startup
public class QuartzLoggerBean {
  private static final Logger logger = LoggerFactory.getLogger(QuartzLoggerBean.class);
  private Scheduler scheduler;

  @PostConstruct
  public void init() {
    try {
      StdSchedulerFactory factory = new StdSchedulerFactory();
      scheduler = factory.getScheduler();
      JobKey jobKey = new JobKey("hostnameLoggerJob", "loggerGroup");
      TriggerKey triggerKey = new TriggerKey("hostnameLoggerTrigger", "loggerGroup");

      if (scheduler.checkExists(triggerKey)) {
        scheduler.unscheduleJob(triggerKey);
      }
      if (scheduler.checkExists(jobKey)) {
        scheduler.deleteJob(jobKey);
      }

      JobDetail job =
          JobBuilder.newJob(HostnameLoggerJob.class)
              .withIdentity("hostnameLoggerJob", "loggerGroup")
              .build();
      Trigger trigger =
          TriggerBuilder.newTrigger()
              .withIdentity("hostnameLoggerTrigger", "loggerGroup")
              .withSchedule(org.quartz.CronScheduleBuilder.cronSchedule("0 * * * * ?"))
              .build();
      scheduler.scheduleJob(job, trigger);
      scheduler.start();
      logger.info(
          "QuartzLoggerBean initialized and scheduler started with JNDI datasource config.");
    } catch (SchedulerException e) {
      logger.warn("Failed to start Quartz scheduler: {}", e.getMessage());
    }
  }

  public static class HostnameLoggerJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
      try {
        String hostname = InetAddress.getLocalHost().getHostName();
        logger.info("QuartzLoggerBean: Hostname={}", hostname);
        // Ensure job takes at least 2 seconds
        Thread.sleep(2000);
      } catch (Exception e) {
        logger.warn("Failed to get hostname: {}", e.getMessage());
      }
    }
  }
}
