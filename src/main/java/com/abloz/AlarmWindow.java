package com.abloz;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.time.LocalDateTime;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlarmWindow extends JFrame {
    Logger logger = LoggerFactory.getLogger(AlarmWindow.class);
    JButton close = new JButton("关闭窗口");
    JButton stop = new JButton("停止本闹钟");

    JFrame frame = null;
    MusicPlayer p = new MusicPlayer("audio/a1.mp3");
    public AlarmWindow(String alarm, JobExecutionContext context)
    {
        super(alarm);
        //specify the image that you want to display on the title bar
        Image icon = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("alarm-clock.png"));  
        if(icon != null){
            setIconImage(icon);
        }
        
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
        stop.setDefaultCapable(false);

        this.getRootPane().setDefaultButton(close);

        jPanel.add(buttonsPanel,BorderLayout.SOUTH);
        setContentPane(jPanel);

        frame = this;

        // ESC和Enter都关闭闹钟窗口
        ActionListener actionListener =new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }

        };
        frame.getRootPane().registerKeyboardAction(actionListener, "close",
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        stop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    context.getScheduler().unscheduleJob(context.getTrigger().getKey());
                    logger.info("Job stopped:"+context.getJobDetail().getKey().getName());
                    frame.dispose();
                } catch (SchedulerException ex) {
                    ex.printStackTrace();
                }
                p.stop();
            }
        });
        close.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
//                frame.dispose();
                //添加在frame上的windowListener的windowClosing操作响应，如果需要在窗口关闭的时候对窗口中的一些数据进行处理，用下面的方法。
                frame.dispatchEvent(new WindowEvent(frame,WindowEvent.WINDOW_CLOSING) );
                p.stop();
            }
        });

//        add(new JButton("停止"));

        p.play();
        setAlwaysOnTop(true);
        setVisible(true);
        close.requestFocus();
    }
}

