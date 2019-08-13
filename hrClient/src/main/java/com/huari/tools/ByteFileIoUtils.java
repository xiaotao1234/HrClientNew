package com.huari.tools;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.ExecutorService;

/**
 *
 */
public class ByteFileIoUtils {

    static ByteFileIoUtils byteFileIoUtils;
    public Thread thread;
    private byte[] result;
    private Thread thread1;
    private MappedByteBuffer byteBuffer;
    private FileChannel fc;
    private int position = 0;
    private RandomAccessFile randomFile;
    ExecutorService executorService;

    public static ByteFileIoUtils getInstance() {
        if (byteFileIoUtils == null) {
            byteFileIoUtils = new ByteFileIoUtils();
        }
        return byteFileIoUtils;
    }

    /**
     * @param filePath
     * @return 读出
     * @throws IOException
     */
    public byte[] getContent(String filePath) throws IOException {
        File file = new File(SysApplication.fileOs.forSaveFloder + File.separator + filePath);
        long fileSize = file.length();
        if (fileSize > Integer.MAX_VALUE) {
            System.out.println("file too big...");
            return null;
        }
        FileInputStream fi = new FileInputStream(file);
        byte[] buffer = new byte[(int) fileSize];
        int offset = 0;
        int numRead = 0;
        while (offset < buffer.length
                && (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {
            offset += numRead;
        }
        // 确保所有数据均被读取
        if (offset != buffer.length) {
            throw new IOException("Could not completely read file "
                    + file.getName());
        }
        fi.close();
        return buffer;
    }

    /**
     * 传统IO
     *
     * @param filename
     * @return
     * @throws IOException
     */
    public static byte[] toByteArray(String filename) throws IOException {

        File f = new File(SysApplication.fileOs.forSaveFloder + File.separator + filename);
        if (!f.exists()) {
            throw new FileNotFoundException(filename);
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream((int) f.length());
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(f));
            int buf_size = 1024;
            byte[] buffer = new byte[buf_size];
            int len = 0;
            while (-1 != (len = in.read(buffer, 0, buf_size))) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bos.close();
        }
    }

    /**
     * NIO 方式
     *
     * @param filename
     * @return
     * @throws IOException
     */
    public static byte[] toByteArray2(String filename) throws IOException {

        File f = new File(SysApplication.fileOs.forSaveFloder + File.separator + filename);
        if (!f.exists()) {
            throw new FileNotFoundException(filename);
        }

        FileChannel channel = null;
        FileInputStream fs = null;
        try {
            fs = new FileInputStream(f);
            channel = fs.getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate((int) channel.size());
            while ((channel.read(byteBuffer)) > 0) {
                //do something
            }
            return byteBuffer.array();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fs.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 可以在处理大文件时，提升性能
     *
     * @param filename
     * @return
     * @throws IOException
     */
    public void toByteArray3(String filename, Handler handler, int everyTimeRead) {
        Runnable runnable = () -> {
            fc = null;
            try {
                File file = new File(SysApplication.fileOs.forSaveFloder + File.separator + filename);
                SysApplication.fileOs.addRecentFile(file.getAbsolutePath(),1);
                fc = new RandomAccessFile(file, "r").getChannel();
                byteBuffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size()).load();
                result = new byte[everyTimeRead];
                if (byteBuffer.remaining() > 0 && !Thread.interrupted()) {
                    byteBuffer.get(result, position, byteBuffer.remaining() > everyTimeRead ? position + everyTimeRead : byteBuffer.remaining());
                }
                Message message = Message.obtain();
                message.what = 1;
                message.obj = result;
                handler.sendMessage(message);
                fc.close();
                byteBuffer.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        thread1 = new Thread(runnable);
        thread1.start();
    }


    /**
     * 写入
     */
    public void writeBytesToFile(String filename, byte[] bytes) {
        Runnable runnable = () -> {
            try {
                randomFile = new RandomAccessFile(SysApplication.fileOs.forSaveFloder + File.separator + filename, "rw");
                // 文件长度，字节数
                long fileLength = randomFile.length();
                //将写文件指针移到文件尾。
                randomFile.seek(fileLength);
                randomFile.write(bytes);
                SysApplication.fileOs.addRecentFile(SysApplication.fileOs.forSaveFloder +
                        File.separator + filename,1);
                randomFile.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("xiaofile", "线程停止了");
        };
        thread = new Thread(runnable);
        thread.start();
    }

    public void gc() {
        thread1.interrupt();
        result = new byte[1];
        byteBuffer = null;
        System.gc();
        try {
            fc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}