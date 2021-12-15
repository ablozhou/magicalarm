package com.abloz;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
//        第4列表示每月日期1～31 如果要指定星期，则该列为?
//        第5列表示月份 0～11
//        第6列标识号星期1～7（1表示星期天）或者使用字符串SUN，MON，TUE，WED，THU，FRI和SAT。如果日期为*，或指定日期，则该列为?
//        第7列年 可选
//
//        *：表示任意时间都，实际上就是“每”的意思。可以代表00-23小时或者00-12每月或者00-59分
//        -：表示区间，是一个范围，00 17-19 * * * cmd，就是每天17,18,19点的整点执行命令
//        ,：是分割时段，30 3,19,21 * * * cmd，就是每天凌晨3和晚上19,21点的半点时刻执行命令
//        /n：表示分割，可以看成除法，*/5 * * * * cmd，每隔五分钟执行一次
public class MyQuartz {
    Logger logger = LoggerFactory.getLogger(MyQuartz.class);
    Scheduler scheduler = null;
    private static MyQuartz myQuartz = null;

    public static MyQuartz getMyQuartz() {
        if (myQuartz == null) {
            myQuartz = new MyQuartz();
        }
        return myQuartz;
    }

    private MyQuartz() {
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
     * .withIntervalInSeconds(40)
     * .repeatForever()
     * 
     * @param simpleSchedule
     * @param name
     * @param group
     * @return
     */
    public Trigger addSimpleTrigger(SimpleScheduleBuilder simpleSchedule, String name, String group) {
        Trigger trigger = newTrigger()
                .withIdentity(name, group)
                .startNow()
                .withSchedule(simpleSchedule)
                .build();
        return trigger;
    }

    /**
     * 将星期的第一天从1开始。为了和crontab兼容，暂未使用
     */
    public String firstDayOfWeek(String cronExpr){
        //处理表达式星期，因为quartz的星期日为1，不符合中国人习惯。将数字处理为1-7分别表示周一到周日。
        Calendar now = Calendar.getInstance();
        //一周第一天是否为星期天
        boolean isFirstSunday = (now.getFirstDayOfWeek() == Calendar.SUNDAY);
        
        now.setFirstDayOfWeek(Calendar.MONDAY);
        String[] cronList = cronExpr.split(" ");
        String weekStr = cronList[5];

        char[] weeks = weekStr.toCharArray();
        //若一周第一天为星期天，则-1
        StringBuilder sb = new StringBuilder();
        int weekDay=0;
        char last = 'a';//any char but not #
        for(char c :weeks){
            weekDay = c-'0';
            //对数字进行处理，因为星期值范围0-7，所以直接处理
            if( weekDay >=0 && weekDay<=9){
                //3#2 表示当月第2周的第3天，所以2不应该处理。
                if(last!='#'){ 
                    if(isFirstSunday){
                        weekDay = weekDay - 1;
                        if(weekDay == 0){
                            weekDay = 7;
                        }

                        sb.append(weekDay);
                    }
                }
            }else{
                sb.append(c);
            }
            last=c;
        }

        cronList[5]=sb.toString();

        //将字符数字转为字符串
        return Arrays.stream(cronList).map(s->s+" ").reduce("",String::concat);
        
    }
        
    
    public Trigger addCronTrigger(String cronExpr, String name, String group) {
        logger.info("add cron:" + cronExpr);

        Trigger trigger = newTrigger()
                .withIdentity(name, group)
                .startNow()
                .withSchedule(cronSchedule(cronExpr))
                .build();
        return trigger;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public Scheduler start() throws SchedulerException {
        if (scheduler == null) {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
        }
        // and start it
        scheduler.start();
        return scheduler;
    }

    public void scheduleJob(JobDetail jobDetail, Trigger trigger) throws SchedulerException {
        scheduler.scheduleJob(jobDetail, trigger);
    }

    public void stop() {
        if (scheduler != null) {
            try {
                if (scheduler.isStarted()) {
                    scheduler.shutdown();
                }
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        }
    }

    public Set<JobKey> getJobs(String groupName) throws SchedulerException {
        Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName));
        for (JobKey jobKey : jobKeys) {

            String jobName = jobKey.getName();
            String jobGroup = jobKey.getGroup();

            // get job's trigger
            List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
            Date nextFireTime = triggers.get(0).getNextFireTime();

            logger.info("[jobName] : " + jobName + " [groupName] : "
                    + jobGroup + " ,nextFireTime: " + nextFireTime);

        }
        return jobKeys;
    }

    public Set<JobKey> getJobs() throws SchedulerException {
        Set<JobKey> jobKeys = new HashSet<>();
        for (String groupName : scheduler.getJobGroupNames()) {
            jobKeys.addAll(getJobs(groupName));
        }
        return jobKeys;
    }
    
    // public static void main(String[] args) {
    //     MyQuartz myQuartz = new MyQuartz();
    // try {
    // Scheduler scheduler = myQuartz.start();
    // scheduler.getContext().put("note","喝水schedule");
    // // define the job and tie it to our HelloJob class
    // JobDetail jobDetail = myQuartz.addJob(AlarmJob.class,"alarmJob","agroup");
    // jobDetail.getJobDataMap().put("note","喝水Job");
    // // Trigger the job to run now, and then repeat every 3 seconds
    // String cronExpr = "0/5 * * * * ?";
    // Trigger trigger = myQuartz.addCronTrigger(cronExpr,"alrmtrigger", "agroup");
    //
    // trigger.getJobDataMap().put("note","喝水trigger");
    // // Tell quartz to schedule the job using our trigger
    // myQuartz.scheduleJob(jobDetail, trigger);
    // myQuartz.getJobs();
    // } catch (SchedulerException se) {
    // se.printStackTrace();
    // }
    //----
    // 2. 测试将星期转成周一到周日为1-7
    //     String cronExpr = "0 30 18 ? * 1-7 ; 周一到周五(2-6，)，18点半下班打卡";
    //     System.out.println(cronExpr);
    //     String cron = myQuartz.firstDayOfWeek(cronExpr);
    //     //should be “0 30 18 ? * 7-6 ; 周一到周五(2-6，)，18点半下班打卡”
    //     System.out.println(cron);
    // }
}
