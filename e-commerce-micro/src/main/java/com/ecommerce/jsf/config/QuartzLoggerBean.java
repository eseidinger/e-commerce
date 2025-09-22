package com.ecommerce.jsf.config;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

import java.net.InetAddress;
import java.util.logging.Logger;

import jakarta.ejb.DependsOn;

@Singleton
@DependsOn("FlywayMigrationBean")
@Startup
public class QuartzLoggerBean {
    private static final Logger logger = Logger.getLogger(QuartzLoggerBean.class.getName());
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

            JobDetail job = JobBuilder.newJob(HostnameLoggerJob.class)
                    .withIdentity("hostnameLoggerJob", "loggerGroup")
                    .build();
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("hostnameLoggerTrigger", "loggerGroup")
                    .withSchedule(org.quartz.CronScheduleBuilder.cronSchedule("0 * * * * ?"))
                    .build();
            scheduler.scheduleJob(job, trigger);
            scheduler.start();
            logger.info("QuartzLoggerBean initialized and scheduler started with JNDI datasource config.");
        } catch (SchedulerException e) {
            logger.severe("Failed to start Quartz scheduler: " + e.getMessage());
        }
    }

    public static class HostnameLoggerJob implements Job {
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            try {
                String hostname = InetAddress.getLocalHost().getHostName();
                logger.info("QuartzLoggerBean: Hostname=" + hostname);
                // Ensure job takes at least 2 seconds
                Thread.sleep(2000);
            } catch (Exception e) {
                logger.warning("Failed to get hostname: " + e.getMessage());
            }
        }
    }
}
