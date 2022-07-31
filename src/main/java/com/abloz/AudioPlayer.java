package com.abloz;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.media.CannotRealizeException;
import javax.media.Manager;
import javax.media.NoPlayerException;
import javax.media.Player;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AudioPlayer {
    Logger logger = LoggerFactory.getLogger(AudioPlayer.class);
    Player player = null;
    String fileName = "audio/1.wav";

    public AudioPlayer() {
    };

    public AudioPlayer(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void SimpleAudioPlayer(URL url) throws Exception {// 创建一个准备Player,准备好播放
        player = Manager.createRealizedPlayer(url);
    }

    /**
     * 播放 20 秒并结束播放
     */
    public void start() {
        play();
    }

    public void play() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File file = new File(fileName);
                    // if (file == null) {
                    // logger.error("can't open file:" + fileName);
                    // }
                    // FileInputStream fis = new FileInputStream(file);
                    // BufferedInputStream stream = new BufferedInputStream(fis);
                    player = Manager.createRealizedPlayer(file.toURI().toURL());
                    player.start();
                } catch (IOException | NoPlayerException | CannotRealizeException e) {

                    logger.error("Can't play the file:" + fileName);
                    return;
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
