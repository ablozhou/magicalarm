package com.abloz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

import static org.quartz.CronScheduleBuilder.*;
import static org.quartz.CalendarIntervalScheduleBuilder.*;

import static org.quartz.DateBuilder.*;

/**
 * Quartz API的关键接口是：
 *
 * Scheduler - 与调度程序交互的主要API。 Job - 你想要调度器执行的任务组件需要实现的接口 JobDetail - 用于定义作业的实例。
 * Trigger（即触发器） - 定义执行给定作业的计划的组件。 JobBuilder - 用于定义/构建 JobDetail 实例，用于定义作业的实例。
 * TriggerBuilder - 用于定义/构建触发器实例。 Scheduler 的生命期，从 SchedulerFactory 创建它时开始，到
 * Scheduler 调用shutdown() 方法时结束；Scheduler 被创建后，可以增加、删除和列举 Job 和
 * Trigger，以及执行其它与调度相关的操作（如暂停 Trigger）。但是，Scheduler 只有在调用 start() 方法后，才会真正地触发
 * trigger（即执行 job） Trigger 用于触发 Job 的执行。当你准备调度一个 job 时，你创建一个 Trigger
 * 的实例，然后设置调度相关的属性。Trigger 也有一个相关联的 JobDataMap，用于给 Job 传递一些触发相关的参数。Quartz
 * 自带了各种不同类型的 Trigger，最常用的主要是 SimpleTrigger 和 CronTrigger。
 *
 * SimpleTrigger 主要用于一次性执行的 Job（只在某个特定的时间点执行一次），或者 Job 在特定的时间点执行，重复执行 N
 * 次，每次执行间隔T个时间单位。CronTrigger 在基于日历的调度上非常有用，如“每个星期五的正午”，或者“每月的第十天的上午 10:15”等。
 */
public class AlarmJob implements Job {
    Logger logger = LoggerFactory.getLogger(AlarmJob.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Object tv1 = context.getTrigger().getJobDataMap().get("tkey");
        // Object tv2 = context.getTrigger().getJobDataMap().get("t2");
        Object jv1 = context.getJobDetail().getJobDataMap().get("jkey");
        // Object jv2 = context.getJobDetail().getJobDataMap().get("j2");
        Object sv = null;
        try {
            sv = context.getScheduler().getContext().get("skey");
            AudioPlayer player = new AudioPlayer();
            player.play();

        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        logger.info("schedule value:" + sv);
        logger.info("job value:" + jv1);
        logger.info("trigger value" + tv1);
        logger.info("time:" + LocalDateTime.now());
    }

}
