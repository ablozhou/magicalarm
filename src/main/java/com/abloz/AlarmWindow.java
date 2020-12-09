package com.abloz;

import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.time.LocalDateTime;

public class AlarmWindow extends JFrame {
    Logger logger = LoggerFactory.getLogger(AlarmWindow.class);
    JButton stop = new JButton("停止本闹钟");
    JButton close = new JButton("关闭窗口");
    JFrame frame = null;
    public AlarmWindow(String alarm, JobExecutionContext context)
    {
        super(alarm);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocation(450, 100);
        setSize(300,200);
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout(5,5));
        jPanel.add(new JLabel(""+LocalDateTime.now()),BorderLayout.NORTH);

        jPanel.add(new JLabel(alarm),BorderLayout.CENTER);
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout());
        buttonsPanel.add(stop);
        buttonsPanel.add(close);
        jPanel.add(buttonsPanel,BorderLayout.SOUTH);
        setContentPane(jPanel);

        frame = this;

        stop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    context.getScheduler().unscheduleJob(context.getTrigger().getKey());
                    logger.info("Job stopped:"+context.getJobDetail().getKey().getName());
                    frame.dispose();
                } catch (SchedulerException ex) {
                    ex.printStackTrace();
                }
            }
        });
        close.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
//                frame.dispose();
                //添加在frame上的windowListener的windowClosing操作响应，如果需要在窗口关闭的时候对窗口中的一些数据进行处理，用下面的方法。
                frame.dispatchEvent(new WindowEvent(frame,WindowEvent.WINDOW_CLOSING) );

            }
        });

//        add(new JButton("停止"));

        setVisible(true);
    }
}
