package com.abloz;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.media.bean.playerbean.*; //这个包要用到JMF
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

//import sun.audio.*;

import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaClock extends JFrame implements ActionListener, Runnable {

    Logger logger = LoggerFactory.getLogger("JavaClock.class");
    private static final long serialVersionUID = -7303693253632593767L;
    final String each = "*";
    final String none = "?";
    boolean showClock = false;
    boolean isAlarmOn = true;

    // 获得系统时间
    Calendar time = Calendar.getInstance();

    Thread thread;
    JTextArea jTextArea = new JTextArea(
            "#秒(0-59) 分(0-59) 时(0-23) 日(1-31) 月(0-11) 星期几(1-7,1为周日) 年(可选) 事项\n0/3 * * * * ?; 3秒浇一次水\n0 0/30 8-17 * * ?; 8点到17点，每30分钟喝一次水\n",
            10, 20);
    JScrollPane scrollpane = new JScrollPane();// 创建滚动条面板
    // scrollpane.(20,20,100,50);//自定义该面板位置并设置大小为100*50

    JPanel panelTop = new JPanel();
    JPanel panelCenter = new JPanel();
    JPanel panelBottom = new JPanel();

    JCheckBox buttonOn = new JCheckBox("已开闹钟", isAlarmOn);
    JButton buttonAdd = new JButton("增加");
    JButton buttonOK = new JButton("应用");

    // 秒，分，时，日，月，星期几, （日期和星期几二选一,contab中不用的用?代替）
    JComboBox<String> secComboBox, minuteComboBox, hourComboBox, dateComboBox, monthComboBox, dayComboBox, yearComboBox;
    JLabel timeLabel = null;
    JLabel labelYear = new JLabel("年");
    JLabel labelMonth = new JLabel("月");
    JLabel labelDate = new JLabel("日 星期");
    JLabel labelh = new JLabel("时:");
    JLabel labelm = new JLabel("分:");
    JLabel labels = new JLabel("秒");
    JLabel labelSet = new JLabel("设置提醒时间：");
    JLabel labelStatus = new JLabel("闹铃状态：关");

    ClockCanvas clockCanvas = new ClockCanvas();

    String hour = "", minute = "", second = "";
    String year = "", month = "", day = "", date = "";
    int nowsecond, nowminute, nowhour;

    // AudioData theData = null;
    // AudioDataStream nowPlaying = null;

    public JavaClock() {
        setTitle("万能闹钟");
        setResizable(true);// 用户不能调整大小
        setLocation(450, 100);// 在屏幕位置
        setSize(1000, 800);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);// 按窗口关闭按钮时退出程序
        setLayout(new BorderLayout(20, 20));

        if (showClock) {
            add(clockCanvas, BorderLayout.NORTH);
        }
        add(getTimelabel(), BorderLayout.SOUTH);

        dateComboBox = new JComboBox<>();
        monthComboBox = new JComboBox<>();
        dayComboBox = new JComboBox<>();
        yearComboBox = new JComboBox<>();
        hourComboBox = new JComboBox<>();
        minuteComboBox = new JComboBox<>();
        secComboBox = new JComboBox<>();
        // buttonOn.addActionListener(this);
        labelSet.setFont(new Font("楷体_GB2312", Font.BOLD, 18));

        // 闹钟设置项
        panelTop.setSize(500, 400);
        panelTop.setLayout(new FlowLayout());
        panelTop.add(labelSet);
        panelTop.add(yearComboBox);
        panelTop.add(labelYear);
        panelTop.add(monthComboBox);
        panelTop.add(labelMonth);
        panelTop.add(dateComboBox);
        panelTop.add(labelDate);
        panelTop.add(dayComboBox);

        panelTop.add(hourComboBox);
        panelTop.add(labelh);
        panelTop.add(minuteComboBox);
        panelTop.add(labelm);
        panelTop.add(secComboBox);
        panelTop.add(labels);
        panelTop.add(buttonAdd);
        panelTop.add(buttonOn);

        panelCenter.setLayout(new BorderLayout());
        panelCenter.add(scrollpane, BorderLayout.CENTER);
        panelCenter.add(buttonOK, BorderLayout.SOUTH);
        add(panelTop, BorderLayout.NORTH);
        // panelAll.add(panelCenter, BorderLayout.CENTER);
        scrollpane.setBounds(20, 20, 100, 50);// 自定义该面板位置并设置大小为100*50

        scrollpane.setViewportView(jTextArea); // 不是用add，才能出滚动条

        add(panelCenter, BorderLayout.CENTER);

        labelStatus.setFont(new Font("楷体_GB2312", Font.BOLD, 18));
        labelStatus.setForeground(Color.red);
        panelBottom.setLayout(new BorderLayout());
        panelBottom.add(labelStatus);
        add(panelBottom, BorderLayout.SOUTH);

        // 时
        hourComboBox.addItem(each);
        for (int i = 0; i < 24; i++) {
            hourComboBox.addItem(String.valueOf(i));
        }
        // 分秒
        secComboBox.addItem(each);
        minuteComboBox.addItem(each);
        for (int i = 0; i < 60; i++) {
            secComboBox.addItem(String.valueOf(i));
            minuteComboBox.addItem(String.valueOf(i));
        }

        // 日期

        dateComboBox.addItem(each);
        dateComboBox.setSelectedIndex(0);
        for (int i = 1; i <= 31; i++) {
            dateComboBox.addItem(String.valueOf(i));
        }
        dateComboBox.addItem(none);

        // 月份
        monthComboBox.addItem(each);
        for (int i = 1; i <= 12; i++) {
            monthComboBox.addItem(String.valueOf(i));
        }
        // 星期 1表示周日，7表示周六
        String[] dayStr = { none, "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", each };
        for (int i = 0; i <= 8; i++) {
            dayComboBox.addItem(dayStr[i]);
        }

        // 年
        yearComboBox.addItem(none);

        for (int i = 2020; i <= 2030; i++) {
            yearComboBox.addItem(String.valueOf(i));
        }
        // buttonAdd.addActionListener(this);
        buttonOK.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                addAlarm();
            }

        });

        buttonAdd.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Integer yearIndex = yearComboBox.getSelectedIndex();

                if (yearIndex > 0) {
                    year = yearComboBox.getSelectedItem().toString();
                }
                month = monthComboBox.getSelectedItem().toString();

                date = dateComboBox.getSelectedItem().toString();
                day = dayComboBox.getSelectedItem().toString();

                hour = hourComboBox.getSelectedItem().toString();
                minute = minuteComboBox.getSelectedItem().toString();
                second = secComboBox.getSelectedItem().toString();

                StringBuilder sb = new StringBuilder();
                sb.append(second).append(" ").append(minute).append(" ").append(hour).append(" ");
                if (!date.equals(none)) {
                    day = none;
                    sb.append(date).append(" ");
                }
                sb.append(month).append(" ");
                if (!day.equals(none)) {
                    date = none;
                    sb.append(day).append(" ");
                } else {
                    sb.append(none).append(" ");
                }
                if (!year.equals(none)) {
                    sb.append(year).append(" ");
                }
                sb.append("\n");
                jTextArea.append(sb.toString());

                addAlarm();

            }

        });

        buttonOn.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                // 获取事件源（即复选框本身）
                JCheckBox checkBox = (JCheckBox) e.getSource();
                isAlarmOn = checkBox.isSelected();
                labelStatus.setText(checkBox.getText() + ":" + isAlarmOn);
                logger.debug(checkBox.getText() + " : " + isAlarmOn);
            }
        });

        // pack();// 自动调整窗口大小
        thread = new Thread(this);
        thread.start();

    }

    private Timer timer;

    // 时间显示
    private JLabel getTimelabel() {
        if (timeLabel == null) {
            timeLabel = new JLabel("");
            timeLabel.setBounds(20, 65, 200, 40);
            timeLabel.setFont(new Font("微软雅黑", Font.BOLD, 12));
            timeLabel.setForeground(new Color(0, 0, 0));
            timer = new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    timeLabel.setText(new SimpleDateFormat("yyyy-MM-dd EEEE hh:mm:ss").format(new Date()));
                }
            });
            timer.start();
        }
        return timeLabel;
    }

    public void actionPerformed(ActionEvent ae) {

    }

    public void addAlarm() {
        String cronStr = jTextArea.getText();
        logger.info(cronStr);
        String[] cronStrs = cronStr.split("\n");
        MyQuartz myQuartz = MyQuartz.getMyQuartz();
        // myQuartz.stop();
        try {
            myQuartz.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        int n = 0;
        for (String c : cronStrs) {
            if (c.startsWith("#")) {
                continue;

            }
            try {
                String[] items = c.split(";");
                // 秒，分，时，日，月，星期几，年（可选）, 事项
                if (items[0].split(" ").length < 6 || items[0].trim().length() < 10) {
                    logger.error("error cron string:" + c);
                    labelStatus.setText("error cron string:" + c);
                    continue;
                }

                Trigger trigger = myQuartz.addCronTrigger(items[0].trim(), "alarmt " + n, "mygroup");
                logger.info("cron " + n + " :" + c + " cron:" + items[1]);

                if (items.length > 1) {
                    trigger.getJobDataMap().put("tkey", items[1]);
                    logger.info("cron " + n + " :" + c + " comment:" + items[1]);
                }
                JobDetail jobDetail = myQuartz.addJob(AlarmJob.class, "alarmjob " + n, "mygroup");

                myQuartz.scheduleJob(jobDetail, trigger);

                n++;
            } catch (SchedulerException e) {
                e.printStackTrace();
            }

        }

    }

    public String getTimeStr() {
        int s = time.get(Calendar.SECOND);
        int m = time.get(Calendar.MINUTE);
        int h = time.get(Calendar.HOUR_OF_DAY);
        return h + ":" + m + ":" + s;

    }

    public void run() {
        while (true) {

            try {
                getTimeStr();
                Thread.sleep(500);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    // // 播放音乐
    public void play(String str) {// str音频文件路径，文件格式是wav

        // InputStream fis = this.getClass().getResourceAsStream(str);
        stop();
        AudioPlayer player = new AudioPlayer();
        player.play();
    }

    // 停止播放
    public void stop() {
        // AudioPlayer.player.stop(nowPlaying);
    }

    // 程序执行入口
    public static void main(String[] args) {
        new JavaClock();
    }

}

// 时钟类
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
        graphBuffer.drawString(time.get(Calendar.HOUR_OF_DAY) + ":" + minute + ":" + second, centerX - 50,
                centerY + 80);
        // 画完图片后，把图片载入画布
        paint(g);

    }

    public void paint(Graphics g) {
        g.drawImage(imageBuffer, 0, 0, null); // 在画布上加入图片
    }

}
