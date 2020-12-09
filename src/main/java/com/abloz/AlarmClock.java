package com.abloz;

import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.FontUIResource;
//import java.awt.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.*;
import java.util.List;


public class AlarmClock extends JFrame implements ActionListener {

    private Logger logger = LoggerFactory.getLogger(AlarmClock.class);
    private static final long serialVersionUID = -7303693253632593767L;
    private final static Integer WIDTH=600;
    private final static String GROUP_NAME="AlarmGroup";
    private final static String TITLE="万能闹钟";
    final String each="*";
    final String none = "?";
    private boolean showClock = false;
    private JFrame frame = null;

    // 获得系统时间
    Calendar time = Calendar.getInstance();
    MyQuartz myQuartz =  MyQuartz.getMyQuartz();

    Thread thread;
    JTextArea jTextArea = new JTextArea("",10,40);
    JScrollPane scrollpane=new JScrollPane();//创建滚动条面板

    JPanel panelTop = new JPanel();
    JPanel panelCenter = new JPanel();
    JPanel panelBottom = new JPanel();

    JButton buttonApply = new JButton("应用");
    JButton buttonAddAlarm = new JButton("添加");

    //秒，分，时，日，月，星期几, （日期和星期几二选一,contab中不用的用?代替）
    private MsComboBox secComboBox, minuteComboBox, hourComboBox, dateComboBox,monthComboBox, dayComboBox,yearComboBox;
    JTextField txtNote = new JTextField("闹钟说明");

    JLabel timeLabel = null;
    JLabel labelYear = new JLabel("年份");
    JLabel labelDay = new JLabel("星期");
    JLabel labelMonth = new JLabel("月份");
    JLabel labelDate = new JLabel("日期");
    JLabel labelh = new JLabel("小时");
    JLabel labelm = new JLabel("分钟");
    JLabel labels = new JLabel("秒数");
    JLabel labelSet = new JLabel("设置提醒时间：");
    JLabel labelStatus = new JLabel("闹钟状态：开");

    ClockCanvas clockCanvas = new ClockCanvas();

    boolean flagOn = true;
    String hour = "", minute = "", second = "";
    String year="",month="",day="",date="";
//    int nowsecond, nowminute, nowhour;
    private Timer timer;
//    AudioData theData = null;
//    AudioDataStream nowPlaying = null;
    /**
     * 统一设置字体，父界面设置之后，所有由父界面进入的子界面都不需要再次设置字体
     */
    private static void InitGlobalFont(Font font) {
        FontUIResource fontRes = new FontUIResource(font);
        for (Enumeration<Object> keys = UIManager.getDefaults().keys(); keys.hasMoreElements();) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, fontRes);
            }
        }
    }
    public void init(){
        InitGlobalFont(new Font("宋体", Font.PLAIN, 14));
        setTitle(TITLE);
        setResizable(true);// 用户能调整大小
        setSize(WIDTH, 400);
        setLocationRelativeTo(null);
//        setLocation(450, 100);// 在屏幕位置
        JPanel jPanel = new JPanel();
        jPanel.setBorder(new EmptyBorder(5,5,5,5));
        jPanel.setLayout(new BorderLayout(10,10));
        setContentPane(jPanel);

        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// 按窗口关闭按钮时退出程序

        jTextArea.setText(ConfigFile.readConfigFile());
        if(showClock) {
            jPanel.add(clockCanvas, BorderLayout.NORTH);
        }

        dateComboBox= new MsComboBox();
        monthComboBox= new MsComboBox();
        dayComboBox= new MsComboBox();
        yearComboBox = new MsComboBox();
        hourComboBox = new MsComboBox();
        minuteComboBox = new MsComboBox();
        secComboBox = new MsComboBox();

        //时

        for (int i = 0; i < 24; i++) {
            hourComboBox.addItem(i);
        }
        hourComboBox.setSelectedIndex(-1);
        hourComboBox.setText(each);
        hourComboBox.setPreferredSize(new Dimension(60,10));

        //分秒
        for (int i = 0; i < 60; i++) {
            secComboBox.addItem(i);
            minuteComboBox.addItem(i);
        }
        secComboBox.setSelectedIndex(-1);
        secComboBox.setText(each);
        minuteComboBox.setSelectedIndex(-1);
        minuteComboBox.setText(each);

        //日期
        for (int i = 1; i <= 31; i++) {
            dateComboBox.addItem(i);
        }
        dateComboBox.setSelectedIndex(-1);
        dateComboBox.setText(none);

        //月份
        for (int i = 1; i <= 12; i++) {
            monthComboBox.addItem(i);
        }
        monthComboBox.setSelectedIndex(-1);
        monthComboBox.setText(each);
        //quartz中 星期 1表示周日，7表示周六，但combox中存储时1-7表示星期一到日
    //        String[] day={"星期日","星期一","星期二","星期三","星期四","星期五","星期六"};
        DayOfWeek dow = DayOfWeek.MONDAY;
        for (int i = 0; i < 7; i++) {
            dayComboBox.addItem(new Week(dow.plus(i)));
        }
        dayComboBox.setSelectedIndex(-1);
        dayComboBox.setText(none);
        //年


        for (int i = 2020; i <= 2030; i++) {
            yearComboBox.addItem(i);
        }
        yearComboBox.setSelectedIndex(-1);
        yearComboBox.setText(none);

        buttonAddAlarm.addActionListener(this);
        buttonApply.addActionListener(this);
        labelSet.setFont(new Font("微软雅黑", Font.BOLD, 18));

        //闹钟设置项
        panelTop.setSize(100,10);

        GridBagLayout  layOut = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
//        gbc.ipadx=10;
        gbc.insets.set(2,2,2,2);
        panelTop.setLayout(layOut);
        gbc.gridy=0;
        gbc.gridx=0;
//        panelTop.add(labelSet);
        panelTop.add(labels,gbc);
        gbc.gridx=1;
        panelTop.add(labelm,gbc);
        gbc.gridx=2;
        panelTop.add(labelh,gbc);
        gbc.gridx=3;
        panelTop.add(labelDate,gbc);
        gbc.gridx=4;
        panelTop.add(labelMonth,gbc);
        gbc.gridx=5;
        panelTop.add(labelDay,gbc);
        gbc.gridx=6;
        panelTop.add(labelYear,gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipady = 20;
        gbc.gridx=0;
        gbc.gridy=1;
        panelTop.add(secComboBox,gbc);
        gbc.gridx=1;
        panelTop.add(minuteComboBox,gbc);
        gbc.gridx=2;
        panelTop.add(hourComboBox,gbc);
        gbc.gridx=3;
        panelTop.add(dateComboBox,gbc);
        gbc.gridx=4;
        panelTop.add(monthComboBox,gbc);
        gbc.gridx=5;
        panelTop.add(dayComboBox,gbc);
        gbc.gridx=6;
        panelTop.add(yearComboBox,gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipady = 20;
        gbc.gridx=0;
        gbc.gridy=2;
        gbc.gridwidth = 5; //占5个格子

        panelTop.add(txtNote,gbc);
        gbc.gridx=6;
        panelTop.add(buttonAddAlarm,gbc);
        panelTop.setPreferredSize(new Dimension(100,120)); //真实大小
        jPanel.add(panelTop, BorderLayout.NORTH);

        //配置项
        panelCenter.setLayout(new BorderLayout(10,10));
        scrollpane.setBounds(20,20,100,10);//自定义该面板位置并设置大小为100*50
        scrollpane.setViewportView(jTextArea); //不是用add，才能出滚动条
        panelCenter.add(scrollpane,BorderLayout.CENTER);
        jPanel.add(panelCenter, BorderLayout.CENTER);

        //状态栏
        panelBottom.add(getTimelabel());
        labelStatus.setFont(new Font("微软雅黑", Font.BOLD, 18));
        labelStatus.setForeground(Color.red);
        panelBottom.add(labelStatus);
        panelBottom.add(buttonApply);
        jPanel.add(panelBottom, BorderLayout.SOUTH);

        try {
            myQuartz.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
    public AlarmClock() {
        super(TITLE);
        init();
        frame = this;
        try {
            addAlarms(jTextArea.getText());
        } catch (SchedulerException e) {
            e.printStackTrace();
            labelStatus.setText(e.getMessage());
        }

    }


    //时间显示
    private JLabel getTimelabel() {
        if (timeLabel == null) {
            timeLabel = new JLabel("");
            timeLabel.setBounds(20, 65, 200, 40);
            timeLabel.setFont(new Font("微软雅黑", Font.BOLD, 12));
            timeLabel.setForeground(new Color(0, 0, 0));
            timer = new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    String time = new SimpleDateFormat("yyyy-MM-dd EEEE hh:mm:ss").format(new Date());
                    timeLabel.setText(time);
                    frame.setTitle(TITLE+" - "+time);
                }
            });
            timer.start();
        }
        return timeLabel;
    }

    public String readConfigText(){
        return jTextArea.getText();
    }
    public void clearAlarm() {
        try {
            myQuartz.scheduler.clear();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
    public void restartAlarm(){
        clearAlarm();
        try {
            addAlarms(jTextArea.getText());
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
    //应用
    public void apply(){
        //写文件
        ConfigFile.writeFile(readConfigText());
        //重置闹钟
        restartAlarm();
    }
    public String genCronStr(){
        hour = hourComboBox.getText();
        minute = minuteComboBox.getText();
        second = secComboBox.getText();

        StringBuilder sb = new StringBuilder();
        sb.append(second).append(" ")
                .append(minute).append(" ")
                .append(hour).append(" ");


        year = yearComboBox.getText();
        month = monthComboBox.getText();
        date = dateComboBox.getText();

        //对星期单独处理，因为星期显示和值不一样
        javax.swing.JTextField jTextField = (javax.swing.JTextField)dayComboBox.getEditor().getEditorComponent();

        Set<Integer> dayIndex = dayComboBox.getIndexSet();
        if(dayIndex.isEmpty()) { //没有选中，则直接获取编辑框的值
            day=jTextField.getText();
            if(day.trim().isEmpty()) {
                day=each;
            }
        }else{
            List<Integer> dl =new ArrayList();

            dayIndex.forEach(i->{
                i=i+2;
                if(i==8) i=1;
                dl.add(i);

            });
            Collections.sort(dl);
            for(Integer d:dl){
                sb.append(d).append(",");
            }
            if(sb.length()>0 && sb.charAt(sb.length()-1)==','){
                sb.deleteCharAt(sb.length()-1);
            }
            day=sb.toString();
        }

        //处理日期和星期的冲突，只能二选一
        if(!date.equals(none) ) { //* 表示每一天，那星期就只能不选，为?
            day=none;
        }else{
            if(day.equals(none)) {
                day=each;
            }
        }

        if(!day.equals(none)) {
            date = none;
        }

        sb.append(date).append(" ");
        sb.append(month).append(" ");
        sb.append(day).append(" ");

        if(!year.equals(none)) {
            sb.append(year);
        }

        if(txtNote.getText().trim().length()>1){
            sb.append(";"+ txtNote.getText());
        }

        sb.append("\n");
        return sb.toString();
    }
    public void actionPerformed(ActionEvent ae) {

        if (ae.getSource() == buttonApply) {
            apply();
//            if (flagOn) {
//                // 闹钟开
//                buttonApply.setText("开闹钟");
//                labelStatus.setText("闹钟状态：关");
//                flagOn = false;
//                //stop();
//            } else {
//                // 闹钟关
//                buttonApply.setText("关闹钟");
//                labelStatus.setText("闹钟状态：开");
//                flagOn = true;
//            }
        }
        if (ae.getSource() == buttonAddAlarm) {
            String cron = genCronStr();
            try {
                addAlarms(cron);
            } catch (SchedulerException e) {
                e.printStackTrace();
                labelStatus.setText(e.getMessage());
                return;
            }
            jTextArea.append(cron);

            ConfigFile.writeFile(readConfigText());
        }
    }

    public void addAlarms(String cronStr) throws SchedulerException {
//        String cronStr = jTextArea.getText();
        logger.info("add alarms:"+cronStr);
        String[] cronStrs = cronStr.split("\n");

        Integer i=myQuartz.scheduler.getJobKeys(GroupMatcher.jobGroupEquals(GROUP_NAME)).size();
        for(String c :cronStrs) {
            c = c.trim();
            if(c.startsWith("#") || c.length()<=0) {
                continue;
            }

            Integer cronIndex = c.indexOf(";");
            String cronExpr = c;
            String note= "alarm "+i+":";
            if(cronIndex == -1) {
                cronExpr = c;
            }else{
                cronExpr = c.split(";")[0];
                note += c.substring(cronIndex+1);
            }

            Trigger trigger = myQuartz.addCronTrigger(cronExpr,"trig"+i,GROUP_NAME);
            JobDetail jobDetail = myQuartz.addJob(AlarmJob.class,"job"+i,GROUP_NAME);
            trigger.getJobDataMap().put("note", note);
            jobDetail.getJobDataMap().put("note", note);

            myQuartz.scheduleJob(jobDetail,trigger);

            i++;
        }

    }
    public String getTimeStr(){
        int s = time.get(Calendar.SECOND);
        int m= time.get(Calendar.MINUTE);
        int h = time.get(Calendar.HOUR_OF_DAY);
        return h + ":" + m + ":" + s;

    }
//    public void run() {
//        while (true) {
////            if (flag) {
////
////                nowsecond = time.get(Calendar.SECOND);
////                nowminute = time.get(Calendar.MINUTE);
////                nowhour = time.get(Calendar.HOUR_OF_DAY);
////                if (second == nowsecond && minute == nowminute && hour == nowhour) {
////                    // 闹钟响
////                    //play("7841.wav");
////                    logger.log(Level.INFO,"alarm");
////                }
////            }
//            try {
//                getTimeStr();
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                break;
//            }
//        }
//    }

    //	// 播放音乐
    public void play(String str) {//str音频文件路径，文件格式是wav
//        try {
            InputStream fis = this.getClass().getResourceAsStream(str);
//            AudioStream as = new AudioStream(fis);// 音频文件流
//            theData = as.getData();// 取得音乐文件的数据
//        } catch (
//                IOException e) {
//            System.err.println(e);
//        }
//        if (theData != null) {
//            stop();
//            ContinuousAudioDataStream cads = new ContinuousAudioDataStream(theData);// 不间断地循环播放
//            AudioPlayer.player.start(cads);
//            nowPlaying = cads;
//        }
    }

    // 停止播放
    public void stop() {
//        AudioPlayer.player.stop(nowPlaying);
    }

    //程序执行入口
    public static void main(String[] args) {
        new AlarmClock();
    }
    //时钟类
    class ClockCanvas extends Canvas implements Runnable {

        private static final long serialVersionUID = -8190862918726517275L;
        Thread threadClock;
        int centerX, centerY, diameter;// 圆心坐标、直径
        Image imageBuffer;

        public ClockCanvas() {
            centerX = centerY = 200;
            diameter = 300;
            setSize(400, 400);
            threadClock = new Thread(this);
            threadClock.start();
        }

        public void run() {
            while (true) {
                repaint();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }

        // 画表
        public void update(Graphics g) {
            Color backColor = Color.DARK_GRAY;
            Color hHandColor = Color.yellow;
            Color mHandColor = Color.green;
            Color sHandColor = Color.red;
            Color hPointColor = Color.red;
            Color mPointColor = Color.orange;
            // 获得系统时间
            Calendar time = Calendar.getInstance();
            int second = time.get(Calendar.SECOND);
            int minute = time.get(Calendar.MINUTE);
            int hour = time.get(Calendar.HOUR_OF_DAY);
            if (hour >= 12) {
                hour -= 12;
            }
            // 画表盘
            imageBuffer = createImage(400, 400);// 不能在外面实例化
            Graphics graphBuffer = imageBuffer.getGraphics();
            graphBuffer.setColor(backColor);
            graphBuffer.fillRect(0, 0, 400, 400);
            double radius = (diameter >> 1) * 0.9;
            // 画刻度
            for (int i = 1; i <= 12; i++) {
                double buffer = Math.PI * (0.5 - i / 6.0);
                int posX = (int) Math.floor(centerX + radius * Math.cos(buffer));
                int posY = (int) Math.floor(centerY + radius * Math.sin(buffer));
                graphBuffer.setColor(hPointColor);
                graphBuffer.fill3DRect(posX - 4, posY - 4, 8, 8, true);

            }
            for (int i = 1; i < 60; i++) {
                if ((i % 5) != 0) {
                    double buffer = Math.PI * i / 30.0;
                    int posX = (int) Math.floor(centerX + radius * Math.cos(buffer));
                    int posY = (int) Math.floor(centerY + radius * Math.sin(buffer));
                    graphBuffer.setColor(mPointColor);
                    graphBuffer.fill3DRect(posX - 4, posY - 4, 3, 3, false);
                }
            }
            // 画数字
            graphBuffer.setFont(new Font("TimesRoman", Font.PLAIN, 24));
            graphBuffer.setColor(Color.yellow);
            graphBuffer.drawString("9", centerX - 125, centerY + 8);
            graphBuffer.drawString("3", centerX + 110, centerY + 8);
            graphBuffer.drawString("12", centerX - 15, centerY - 110);
            graphBuffer.drawString("6", centerX - 8, centerY + 125);

            // 指针半径
            double Sradius = (diameter >> 1) * 0.90;
            double Mradius = (diameter >> 1) * 0.80;
            double Hradius = (diameter >> 1) * 0.70;

            double posSecond = Math.PI * second / 30.0;// 秒针走过弧度
            double posMinute = Math.PI * (minute / 30.0 + second / 1800.0);// 分针走过弧度
            double posHour = Math.PI * (hour / 6.0 + minute / 360.0);// 时针走过弧度
            // 画指针
            graphBuffer.setColor(hHandColor);
            graphBuffer.drawLine(centerX - 3, centerY - 3, (int) (Math.round(centerX + Hradius * Math.sin(posHour))),
                    (int) (Math.round(centerY - Hradius * Math.cos(posHour))));
            graphBuffer.drawLine(centerX + 3, centerY + 3, (int) (Math.round(centerX + Hradius * Math.sin(posHour))),
                    (int) (Math.round(centerY - Hradius * Math.cos(posHour))));
            graphBuffer.setColor(mHandColor);
            graphBuffer.drawLine(centerX - 2, centerY - 2, (int) (Math.round(centerX + Mradius * Math.sin(posMinute))),
                    (int) (Math.round(centerY - Mradius * Math.cos(posMinute))));
            graphBuffer.drawLine(centerX + 2, centerY + 2, (int) (Math.round(centerX + Mradius * Math.sin(posMinute))),
                    (int) (Math.round(centerY - Mradius * Math.cos(posMinute))));
            graphBuffer.setColor(sHandColor);
            graphBuffer.drawLine(centerX, centerY, (int) Math.round(centerX + Sradius * Math.sin(posSecond)),
                    (int) Math.round(centerY - Sradius * Math.cos(posSecond)));
            graphBuffer.fillOval((int) Math.round(centerX + Sradius * Math.sin(posSecond)) - 5,
                    (int) Math.round(centerY - Sradius * Math.cos(posSecond)) - 5, 10, 10);
            graphBuffer.fillOval(centerX - 5, centerY - 5, 10, 10);
            // 画数字时钟
            graphBuffer.setColor(Color.white);
            graphBuffer.drawString(time.get(Calendar.HOUR_OF_DAY) + ":" + minute + ":" + second, centerX - 50, centerY + 80);
            // 画完图片后，把图片载入画布
            paint(g);

        }

        public void paint(Graphics g) {
            g.drawImage(imageBuffer, 0, 0, null); // 在画布上加入图片
        }

    }
}


