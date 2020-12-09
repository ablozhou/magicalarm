package com.abloz;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import static org.junit.Assert.*;


public class AlarmJobTest {

    @Test
    public void execute() throws SchedulerException, InterruptedException {


            //创建一个scheduler
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.getContext().put("skey", "svalue");

            //创建一个Trigger
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("trigger1", "group1")
                    .usingJobData("t1", "tv1")
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(5)
                            .repeatForever()).build();
            trigger.getJobDataMap().put("t2", "tv2");

            //创建一个job
            JobDetail job = JobBuilder.newJob(AlarmJob.class)
                    .usingJobData("j1", "jv1")
                    .withIdentity("myjob", "mygroup").build();
            job.getJobDataMap().put("j2", "jv2");

            //注册trigger并启动scheduler
            scheduler.scheduleJob(job,trigger);
            scheduler.start();
            int i=0;
            while(i<1) {
                Thread.sleep(5000);
                i++;
            }
            scheduler.shutdown();
    }
}
