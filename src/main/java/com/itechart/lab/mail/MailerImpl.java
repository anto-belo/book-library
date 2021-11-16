package com.itechart.lab.mail;

import lombok.extern.log4j.Log4j2;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

@Log4j2
public class MailerImpl implements Mailer {
    private static final MailerImpl instance = new MailerImpl();

    private Scheduler scheduler;

    private MailerImpl() {
    }

    public static MailerImpl getInstance() {
        return instance;
    }

    @Override
    public void init() {
        try {
            JobDetail job = JobBuilder.newJob(MailJob.class)
                    .withIdentity("mailJob", "mail")
                    .build();

            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("mailTrigger", "mail")
                    .withSchedule(CronScheduleBuilder.cronSchedule("0 0 11 ? * *"))
                    .forJob("mailJob", "mail")
                    .build();

            SchedulerFactory schFactory = new StdSchedulerFactory();
            scheduler = schFactory.getScheduler();
            scheduler.start();
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            log.warn(e.getMessage());
        }
    }

    @Override
    public void destroy() {
        try {
            scheduler.shutdown();
        } catch (SchedulerException e) {
            log.warn(e.getMessage());
        }
    }
}
