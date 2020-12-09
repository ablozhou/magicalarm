package com.abloz;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ConfigFile {
    final static String fileName = "config.txt";
    //读文件
    public static String readConfigFile() {
        List<String> configs = new ArrayList<String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line;

            while ((line = br.readLine()) != null) {
                // 一次读入一行数据
                configs.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return String.join("\n",configs);
    }

    //写文件
    public static int writeFile(String lines) {
        try {
            File writeName = new File(fileName); // 相对路径，如果没有则要建立一个新的output.txt文件
            writeName.createNewFile(); // 创建新文件,有同名的文件的话直接覆盖

            BufferedWriter out = new BufferedWriter(new FileWriter(new File(fileName)));
//            for(String l:lines){
            out.write(lines);
//            }
            out.flush(); // 把缓存区内容压入文件

        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }
}
