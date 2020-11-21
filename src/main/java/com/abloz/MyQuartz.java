package com.abloz;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

//CronTrigger通常比Simple Trigger更有用，如果您需要基于日历的概念而不是按照SimpleTrigger的精确指定间隔进行重新启动的作业启动计划。
//
//使用CronTrigger，您可以指定号时间表，例如“每周五中午”或“每个工作日和上午9:30”，甚至“每周一至周五上午9:00至10点之间每5分钟”和1月份的星期五“。
//
//即使如此，和SimpleTrigger一样，CronTrigger有一个startTime，它指定何时生效，以及一个（可选的）endTime，用于指定何时停止计划。
//定时任务的每段为：秒，分，时，日，月，星期几，年（可选）, 事项
// 通配符（'*'字符）可用于说明该字段的“每个”可能的值。
// '/'字符可用于指定值的增量。例如，如果在“分钟”字段中输入“0/15”，则表示“每隔15分钟，从零开始”。如果您在“分钟”字段中使用“3/20”，
// 则意味着“每隔20分钟，从三分钟开始” 换句话说，它与“分钟”中的“3,23,43”相同领域。请注意“ / 35”的细微之处并不代表“每35分钟”
// 这意味着“每隔35分钟，从零开始” 或者换句话说，与指定“0,35”相同。'？' 字符表示该字段无特定值。“L”字符允许用于月日和星期几字段。
// 表示最后的。如“月”字段中的“L”表示“月的最后一天。指定从该月最后一天的偏移量，例如“L-3”。
//“W”用于指定最近给定日期的工作日（星期一至星期五）.'＃'用于指定本月的“第n个”工作日。“星期几”字段中的“6＃3”表示“本月的第三个星期五”。
//      第1列表示秒0～59 范围，每秒钟用*或者 */1表示
//        第2列表示分钟0～59 每分钟用*或者 */1表示
//        第3列表示小时0～23（0表示0点）
//        第4列表示日期1～31
//        第5列表示月份0～11
//        第6列标识号星期1～7（1表示星期天）或者使用字符串SUN，MON，TUE，WED，THU，FRI和SAT
//        第7列年 可选
//
//        *：表示任意时间都，实际上就是“每”的意思。可以代表00-23小时或者00-12每月或者00-59分
//        -：表示区间，是一个范围，00 17-19 * * * cmd，就是每天17,18,19点的整点执行命令
//        ,：是分割时段，30 3,19,21 * * * cmd，就是每天凌晨3和晚上19,21点的半点时刻执行命令
//        /n：表示分割，可以看成除法，*/5 * * * * cmd，每隔五分钟执行一次
public class MyQuartz {
    Scheduler scheduler = null;
    private static MyQuartz myQuartz = null;
    public static MyQuartz getMyQuartz() {
        if(myQuartz == null) {
            myQuartz = new MyQuartz();
        }
        return myQuartz;
    }
    private MyQuartz()  {
        // Grab the Scheduler instance from the Factory

    }

    public JobDetail addJob(Class<? extends Job> jobClass, String name, String group) {
        JobDetail jobDetail = newJob(jobClass)
                .withIdentity(name, group)
                .build();
        return jobDetail;
    }

    /**
     * simpleSchedule()
     *                         .withIntervalInSeconds(40)
     *                         .repeatForever()
     * @param simpleSchedule
     * @param name
     * @param group
     * @return
     */
    public Trigger addSimpleTrigger(SimpleScheduleBuilder simpleSchedule, String name,String group){
        Trigger trigger = newTrigger()
                .withIdentity(name,group)
                .startNow()
                .withSchedule(simpleSchedule)
                .build();
        return trigger;
    }
    public Trigger addCronTrigger(String cronExpr,String name,String group) {
        Trigger trigger = newTrigger()
                .withIdentity(name,group)
                .startNow()
                .withSchedule(cronSchedule(cronExpr))
                .build();
        return trigger;
    }
    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }
    public Scheduler start() throws SchedulerException {
        if(scheduler == null) {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
        }
        // and start it
        scheduler.start();
        return scheduler;
    }
    public void scheduleJob(JobDetail jobDetail,Trigger trigger) throws SchedulerException {
        scheduler.scheduleJob(jobDetail,trigger);
    }
    public void stop()  {
        if(scheduler != null) {
            try {
                if(scheduler.isStarted()) {
                    scheduler.shutdown();
                }
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) {
        MyQuartz myQuartz = new MyQuartz();
        try {
            Scheduler scheduler = myQuartz.start();
            scheduler.getContext().put("skey","喝水schedule");
            // define the job and tie it to our HelloJob class
            JobDetail jobDetail = myQuartz.addJob(AlarmJob.class,"alarmJob","agroup");
            jobDetail.getJobDataMap().put("jkey","喝水Job");
            // Trigger the job to run now, and then repeat every 3 seconds
            String cronExpr = "0/3 * * * * ?";
            Trigger trigger = myQuartz.addCronTrigger(cronExpr,"alrmtrigger", "agroup");

            trigger.getJobDataMap().put("tkey","喝水trigger");
            // Tell quartz to schedule the job using our trigger
            myQuartz.scheduleJob(jobDetail, trigger);

        } catch (SchedulerException se) {
            se.printStackTrace();
        }
    }
}
