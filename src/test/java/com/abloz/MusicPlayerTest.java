package com.abloz;

import org.junit.Test;

public class MusicPlayerTest {
    @Test
    public void testAudioPlay() {
        AudioPlayer player = new AudioPlayer();
        player.play();
    }

    @Test
    public void testMusicPlay() {
        MusicPlayer player = new MusicPlayer();
        player.play();
        int i = 0;
        while (i < 10) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            i++;
        }
    }
}
