package com.abloz;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class MusicPlayer {
    FileInputStream inputStream = null;
    BufferedInputStream bufferedInputStream = null;
    String fileName = "src/main/resources/a1.mp3";

    public MusicPlayer() {
    }

    public MusicPlayer(String fileName) {
        this.fileName = fileName;
    }

    public void play() {

        // 声明一个File对象
        File file = new File(fileName);

        new Thread(() -> {
            // 调用播放方法进行播放
            try {
                // 创建一个输入流
                inputStream = new FileInputStream(file);
                // 创建一个缓冲流
                bufferedInputStream = new BufferedInputStream(inputStream);
                // 创建播放器对象，把文件的缓冲流传入进去
                final Player player = new Player(bufferedInputStream);
                player.play();

            } catch (JavaLayerException | FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                    bufferedInputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
