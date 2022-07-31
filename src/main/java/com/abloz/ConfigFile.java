package com.abloz;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ConfigFile {
    final static String fileName = "config.txt";
    //读文件
    public static String readConfigFile() {
        List<String> configs = new ArrayList<String>();
        Path path = Paths.get(fileName);
        try {
            //// Java 8, default UTF-8
//           BufferedReader reader = Files.newBufferedReader(path);
            // Java 11
//          String s = Files.readString(path, StandardCharsets.UTF_8);
            //java 11
//            FileReader fr = new FileReader(fileName, StandardCharsets.UTF_8);
//            BufferedReader reader = new BufferedReader(fr);

            configs = Files.readAllLines(path, StandardCharsets.UTF_8);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return String.join("\n",configs);
    }

    //写文件
    public static int writeFile(String lines) {
        Path path = Paths.get(fileName);
//        try {
//            File writeName = new File(fileName); // 相对路径，如果没有则要建立一个新的output.txt文件
//            writeName.createNewFile(); // 创建新文件,有同名的文件的话直接覆盖
//
//            BufferedWriter out = new BufferedWriter(new FileWriter(new File(fileName)));
////            for(String l:lines){
//            out.write(lines);
////            }
//            out.flush(); // 把缓存区内容压入文件
// Java 8
            try(BufferedWriter writer = Files.newBufferedWriter(path,StandardCharsets.UTF_8)){ // default UTF-8
//                for (String line : lines) {
                    writer.append(lines);
                    writer.newLine();

            // Java 11
//            new FileWriter(new File(fileName), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }
}
