package com.abloz;
import javazoom.jl.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

//import javax.media.Manager;
//import javax.media.NoPlayerException;
//import javax.media.Player;

public class MusicPlayer {
    Logger logger = LoggerFactory.getLogger(MusicPlayer.class);
    Player player = null;
    String fileName = "src/main/resources/alarm.wav";

    public MusicPlayer(){};
    public MusicPlayer(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * 播放 20 秒并结束播放
     */
    public void play() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File file = new File(fileName);
                    FileInputStream fis = new FileInputStream(file);
                    BufferedInputStream stream = new BufferedInputStream(fis);
                    player = new Player(stream);
                    player.play();
                } catch (Exception e) {

                    logger.error("Can't play the file:"+fileName);
                }
            }
        }).start();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        player.close();
    }
}
