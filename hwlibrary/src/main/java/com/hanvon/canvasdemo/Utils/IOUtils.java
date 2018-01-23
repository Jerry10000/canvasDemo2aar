package com.hanvon.canvasdemo.Utils;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by pc on 2017/11/16.
 */

public class IOUtils {
    private static final String TAG = "IOUtils";
    /**
     * 写文件
     * @param data  将要写文件的数据
     * @param path  文件所在的完整路径
     * @param fileName 文件名
     */
    public static void writeStringToFile(String data, String path, String fileName){
//        FileOutputStream out = null;
        FileOutputStream outSTr = null;
        BufferedOutputStream Buff=null;
//        FileWriter fw = null;

        File file = new File(path);
        if (!file.exists()){
            file.mkdirs();
        }
        try {
//            //方法一， 较方法二稍慢些
//            file = new File(path + "stroke0.txt");
//            if (!file.exists()){
//                file.createNewFile();
//            }
//            out = new FileOutputStream(file);
//            long begin = System.currentTimeMillis();
//            out.write(data.getBytes());
//            out.close();
//            long end = System.currentTimeMillis();
//            System.out.println("FileOutputStream执行耗时:" + (end - begin) + "豪秒");

            //方法二， 最快
            file = new File(path + fileName);
            if (!file.exists()){
                file.createNewFile();
            }
            outSTr = new FileOutputStream(file);
            Buff=new BufferedOutputStream(outSTr);
//            long begin0 = System.currentTimeMillis();
            Buff.write(data.getBytes());
            Buff.flush();
            Buff.close();
//            long end0 = System.currentTimeMillis();
//            System.out.println("BufferedOutputStream执行耗时:" + (end0 - begin0) + " 豪秒");


//            //方法三, 太耗时
//            file = new File(path + "stroke2.txt");
//            if (!file.exists()){
//                file.createNewFile();
//            }
//            fw = new FileWriter(file);
//            long begin3 = System.currentTimeMillis();
//            fw.write(data);
//            fw.close();
//            long end3 = System.currentTimeMillis();
//            System.out.println("FileWriter执行耗时:" + (end3 - begin3) + " 豪秒");

        } catch (Exception e) {
            e.printStackTrace();
        }

        finally {
            try {
//                fw.close();
                Buff.close();
                outSTr.close();
//                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void writeIntArrayToFile(int[] array, String path, String fileName) throws IOException {
        File file = new File(path);
        if (!file.exists()){
            file.mkdirs();
        }
        file = new File(path + fileName);
        if (!file.exists()){
            file.createNewFile();
        }
        FileChannel fc = new RandomAccessFile(path + fileName, "rw").getChannel();
        ByteBuffer buff = ByteBuffer.allocate(array.length*4);
        IntBuffer ib = buff.asIntBuffer();
        ib.put(array);
        fc.write(buff);

        //关闭通道
        fc.close();
    }


    public static void readIntArrayFromFile(int[] array, String fileName) throws IOException {
        FileChannel fc = new FileInputStream(fileName).getChannel();
        ByteBuffer buff = ByteBuffer.allocate(array.length*4);
        fc.read(buff);
        buff.flip();
        IntBuffer ib = buff.asIntBuffer();
//        array = ib.array().clone();
        for (int i = 0; i < array.length; i++){
            array[i] = ib.get(i);
        }

        //关闭通道
        fc.close();
    }




    /**
     * 以行为单位读取文件，常用于读面向行的格式化文件
     */
    public static String readFileByLines(String fileName) {
        long begin0 = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                sb.append(tempString);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        long end0 = System.currentTimeMillis();
        System.out.println("readFileByLines执行耗时:" + (end0 - begin0) + " 豪秒");
        return sb.toString();
    }


    /**
     * 以字符为单位读取文件，常用于读文本，数字等类型的文件
     */
    public static String readFileByChars(String fileName) {
//        long begin0 = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();
        File file = new File(fileName);
        if (!file.exists()){
            return "";
        }
        Reader reader = null;
        try {
            char[] tempchars = new char[1024*10];
            int length = 0;
            reader = new InputStreamReader(new FileInputStream(fileName));
            while ((length = reader.read(tempchars)) != -1) {
                sb.append(tempchars, 0, length);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
//        long end0 = System.currentTimeMillis();
//        System.out.println("readFileByLines执行耗时:" + (end0 - begin0) + " 豪秒");
        return sb.toString();
    }

    /**
     * 以字节为单位读取文件，常用于读二进制文件，如图片、声音、影像等文件。
     */
    public static String readFileByBytes(String fileName) {
        long begin0 = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();
        FileInputStream fileInput = null;
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            byte[] buffer = new byte[1024];
            fileInput = new FileInputStream(file);
            while (fileInput.read(buffer) != -1) {
                sb.append(buffer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInput != null) {
                    fileInput.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        long end0 = System.currentTimeMillis();
        System.out.println("readFileByLines执行耗时:" + (end0 - begin0) + " 豪秒");
        return sb.toString();
    }


    public static String readFileByMapped(String fileName) throws IOException {
        StringBuilder sb = new StringBuilder();
        FileChannel fc = new RandomAccessFile(new File(fileName), "rw").getChannel();
        IntBuffer ib = fc.map(FileChannel.MapMode.READ_WRITE, 0, fc.size()).asIntBuffer();
        ib.put(0);
        long num = fc.size();
        for (int i = 1; i < 1400; i++){
//            ib.put(ib.get(i - 1));
            sb.append(ib.get(i - 1));
        }
        fc.close();
        return sb.toString();
    }













    //删除文件和目录
    public static void clearFiles(String workspaceRootPath){
        File file = new File(workspaceRootPath);
        if(file.exists()){
            deleteFile(file);
        }
    }
    public static void deleteFile(File file){
        if(file.isDirectory()){
            File[] files = file.listFiles();
            for(int i=0; i<files.length; i++){
                deleteFile(files[i]);
            }
        }
        file.delete();
    }

    public static byte[] compress(String str, String encoding) {
        if (str == null || str.length() == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip;
        try {
            gzip = new GZIPOutputStream(out);
            gzip.write(str.getBytes(encoding));
            gzip.close();
        } catch (IOException e) {
            Log.e(TAG, "gzip compress error.", e);
        }
        return out.toByteArray();
    }

    public static byte[] uncompress(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        try {
            GZIPInputStream ungzip = new GZIPInputStream(in);
            byte[] buffer = new byte[256];
            int n;
            while ((n = ungzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
        } catch (IOException e) {
            Log.e(TAG, "gzip uncompress error.", e);
        }

        return out.toByteArray();
    }

}
