package com.abloz;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MusicPlayer {
    Logger logger = LoggerFactory.getLogger(MusicPlayer.class);
    FileInputStream inputStream = null;
    BufferedInputStream bufferedInputStream = null;
    String fileName = "a1.mp3";
    Thread playThread = null;
    Player player = null;
    public MusicPlayer() {
    }

    public MusicPlayer(String fileName) {
        this.fileName = fileName;
    }

    public void stop() {
        if(playThread!=null && playThread.isAlive()) {
            playThread.interrupt();
        }

        if(player!=null){
            player.close();
            player = null;
        }

    }
    public void play() {

        // 声明一个File对象
        File file = new File(fileName);

        playThread = new Thread(() -> {
            // 调用播放方法进行播放
            try {
                // 创建一个输入流
                inputStream = new FileInputStream(file);
                // 创建一个缓冲流
                bufferedInputStream = new BufferedInputStream(inputStream);
                // 创建播放器对象，把文件的缓冲流传入进去
                player = new Player(bufferedInputStream);
                player.play();
                if(Thread.currentThread().isInterrupted()){
                    //处理中断逻辑
                    player.close();
                    player = null;
                    return;
                }
            } catch (JavaLayerException | FileNotFoundException e) {
                e.printStackTrace();
            }
            finally
             {
                try {
                    inputStream.close();
                    bufferedInputStream.close();
                    if(player!=null){
                        player.close();
                        player = null;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        playThread.start();

//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        player.close();

    }
}
