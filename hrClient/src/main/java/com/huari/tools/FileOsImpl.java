package com.huari.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.huari.client.Main3Activity;
import com.huari.dataentry.FileSearchData;
import com.huari.dataentry.FileSearchMassage;
import com.huari.dataentry.Station;
import com.huari.dataentry.recentContent;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import static android.content.Context.MODE_PRIVATE;

/**
 *
 */
/*
 *create by xiao tao
 * 2019/7/19/13:37
 */
public class FileOsImpl {
    List<File> files = new ArrayList<>();//列表内文件
    List<String> filesName = new ArrayList<>();//列表内文件名
    public File OsDicteoryPath;//文件的根目录
    public Stack<File> fileStack = new Stack<>();//文件的遍历栈
    List<File> willDeleteFiles = new ArrayList<>();//将要删除的文件列表
    List<File> selectedFiles = new ArrayList<>();//被勾选的文件列表
    public static List<recentContent> recentUseFiles = new ArrayList<>();//最近有对其进行操作的文件列表
    File currentFloder;//当前所在目录
    File currentFile;//当前正在使用的文件
    private static FileOsImpl fileOsImpl;
    public static String forSaveFloder = Environment.getExternalStorageDirectory().getAbsolutePath();
    private Thread thread;
    public int MUSIC = 0, WORD = 1, EXCEL = 2;
    private static List<recentContent> list;

    public FileOsImpl() {
    }

//    public class recentContent implements Serializable {
//        private String file;
//        private String filename;
//        private int type;
//
//        public String getFilename() {
//            return filename;
//        }
//
//        public void setFilename(String filename) {
//            this.filename = filename;
//        }
//
//        public recentContent(String file, String filename, int type) {
//            this.file = file;
//            this.type = type;
//            this.filename = filename;
//        }
//
//        public void setFile(String file) {
//            this.file = file;
//        }
//
//        public void setType(int type) {
//            this.type = type;
//        }
//
//        public String getFile() {
//
//            return file;
//        }
//
//        public int getType() {
//            return type;
//        }
//
//        @Override
//        public int hashCode() {
//            return file.hashCode();
//        }
//
//        @Override
//        public boolean equals(Object obj) {
//            if (obj instanceof recentContent) {
//                String a = ((recentContent) obj).getFile();
//
//                return file == ((recentContent) obj).getFile();
//            }
//            return false;
//        }
//
//        @Override
//        public String toString() {
//            return file + filename + type;
//        }
//    }

    public static List<recentContent> setRecentUseFiles(List<recentContent> recentUseFiles) {
        if (recentUseFiles == null) {
            File file1 = new File(forSaveFloder + File.separator + "data" + File.separator + "forsave");
            try {
                InputStream inputStream = new FileInputStream(file1);
                ObjectInputStream ois = new ObjectInputStream(inputStream);
                list = (List<recentContent>) ois.readObject();
                FileOsImpl.recentUseFiles = list;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("xiaoxiaolai", "1");
            }
            Message message = Message.obtain();
            message.obj = list;
//            Main3Activity.handler.sendMessage(message);
            return list;
        } else {
            FileOsImpl.recentUseFiles = recentUseFiles;
            return recentUseFiles;
        }
    }

    public static void SaveRecentUseFiles() {
        File filesave = new File(forSaveFloder + File.separator + "data" + File.separator + "forsave");
        if (!filesave.getParentFile().exists()) {
            filesave.getParentFile().mkdirs();
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(filesave);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(recentUseFiles);
            objectOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param filename
     * @param type
     */
    public void addRecentFile(String filename, String fileOnlyName, int type) {
//        if (!recentUseFiles.contains(new recentContent(filename, fileOnlyName, type))){
//            if (recentUseFiles.size() > 100) {
        recentUseFiles.add(0, new recentContent(filename, fileOnlyName, type));//Retrieves and removes the head (first element) of this list.
//                recentUseFiles.remove(100);
//            } else {
//                recentUseFiles.add(new recentContent(filename, fileOnlyName, type));
//            }
//        }else {
//            int i = recentUseFiles.indexOf(new recentContent(filename,fileOnlyName,type));
//            recentContent recentContent = recentUseFiles.remove(i);
//            recentUseFiles.add(0,recentContent);
//        }
        recentUseFiles.add(new recentContent(filename, fileOnlyName, type));
        SaveRecentUseFiles();
    }

    public void save(String filename, String fileOnlyName, int type) {
        GetTheRecentFirst();
        recentContent recentContent = new recentContent(filename,fileOnlyName,type);
        if (!recentUseFiles.contains(recentContent)) {
            recentUseFiles.add(0, new recentContent(filename, fileOnlyName, type));
            if (recentUseFiles.size() > 100) {
                recentUseFiles.remove(100);
            }
        } else {
            int i = recentUseFiles.indexOf(new recentContent(filename, fileOnlyName, type));
            recentUseFiles.add(0, recentUseFiles.remove(i));
        }
        File file1 = new File(forSaveFloder + File.separator + "data" + File.separator + "mimi");
        file1.delete();
        if (!file1.getParentFile().exists()) {
            file1.getParentFile().mkdirs();
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file1);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(recentUseFiles);
            objectOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void GetTheRecentFirst() { //从文件中反序列化出最近数据
        if (recentUseFiles.size() == 0) {
            try {
                File file1 = new File(forSaveFloder + File.separator + "data" + File.separator + "mimi");
                FileInputStream fileInputStream = new FileInputStream(file1);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                recentUseFiles.clear();
                recentUseFiles = (List<recentContent>) objectInputStream.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void getRecentList(Handler handler){
        if(recentUseFiles.size()!=0){
            Message message = Message.obtain();
            message.obj = recentUseFiles;
            handler.sendMessage(message);
        }
        getRecentFromFile(handler);
    }

    public static void getRecentFromFile(Handler handler) {
        try {
            File file1 = new File(forSaveFloder + File.separator + "data" + File.separator + "mimi");
            FileInputStream fileInputStream = new FileInputStream(file1);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            recentUseFiles.clear();
            recentUseFiles = (List<recentContent>) objectInputStream.readObject();
            Message message = Message.obtain();
            message.obj = recentUseFiles;
            handler.sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @return这个方法返回的是至头节点到尾节点时间依次由远到近
     */
    public static void getRecentUseFiles() {
        Thread thread = new Thread(() -> {
            if (recentUseFiles.size() == 0) {
                FileOsImpl.setRecentUseFiles(null);
            }
        });
        thread.start();
    }

    public static FileOsImpl getInstance() { //单例获取实例
        if (fileOsImpl == null) {
            fileOsImpl = new FileOsImpl();
        }
        return fileOsImpl;
    }

    public Stack<File> getFileStack() {  //获得路径记录栈
        return fileStack;
    }

    public void pushStack(File file) {  //路径记录栈的入栈
        fileStack.push(file);
        Log.d("stack_os_in", fileStack.toString());
    }


    public File popStack() {  //路径记录栈的出栈
        File file = fileStack.pop();
        Log.d("stack_os_out", fileStack.toString());
        return file;
    }


    public File getOsDicteoryPath(Context context) {  //获得初始根目录地址
        SharedPreferences sharedPreferences = context.getSharedPreferences("User", MODE_PRIVATE);
        String filepatc = sharedPreferences.getString("RootDirectory", Environment.getExternalStorageDirectory().getAbsolutePath());
        OsDicteoryPath = new File(filepatc);
        if (!OsDicteoryPath.mkdirs()) {
            Log.d("xiao", "file has exist");
        } else {
            Log.d("xiao", "file has make");
        }
        return OsDicteoryPath;
    }


    public File getCurrentFloder() {  //获得当前目录的目录名
        return currentFloder;
    }

    public void setCurrentFloder(File floder) {  //设置当前文件目录，设置后会自动设置当前目录文件列表和文件名列表
        if (floder != null) {
            currentFloder = floder;
            if (files != null) {
                files.clear();
            }
            if (filesName != null) {
                filesName.clear();
            }
            for (File file : currentFloder.listFiles()) {
                filesName.add(file.getName());
                files.add(file);
            }
        }
    }

    public List<File> getFiles() {  //获得当前目录下的所有文件
        return files;
    }

    public List<File> getWillDeleteFiles() {
        return willDeleteFiles;
    }

    public void setWillDeleteFiles(File file) {
        if (file != null) {
            willDeleteFiles.add(file);
        }
    }

    public void setOsDicteoryPath(File file) {  //设置根目录地址
        if (file != null && file.exists()) {
            OsDicteoryPath = file;
        }
    }

    public void setSelectedFiles(File file) {
        if (file != null) {
            selectedFiles.add(file);
        }
    }

    public void setCurrentFile(File currentFile) {  //获得当前被选择的文件
        this.currentFile = currentFile;
    }

    public File getCurrentFile() {
        return currentFile;
    }

    public List<File> getSelectedFiles() {  //获得被选中的文件目录
        return selectedFiles;
    }

//    public void createFloder(String name) {
//        File file = new File(currentFloder.getAbsolutePath() + File.separator + name);
//        if (file.exists() == false) {
//            if (file.mkdirs()) ;
//        }
//    }

    public void addFloder(String floderName) {          //添加文件夹
        File addFile = new File(currentFloder.getAbsolutePath() + File.separator + floderName);
        if (!addFile.exists()) {
            addFile.mkdirs();
        }
    }

    public List<String> getFilesName() {
        return filesName;
    }

    public void searh(File file, String key) {    //查找相关文件
        Log.d("xiao", key);
        Runnable runnable = () -> {
            long time = System.currentTimeMillis();
            FileSearchMassage fileSearchResult = new FileSearchMassage(searchInThisFloder(file, key));
            long timeafter = System.currentTimeMillis() - time;
            Log.d("xiaotaoni", String.valueOf(timeafter));
            EventBus.getDefault().post(fileSearchResult);
        };
        thread = new Thread(runnable);
        thread.start();
    }

    public List<FileSearchData> searchInThisFloder(File file, String key) {             //在文件夹内递归搜索相应文件
        List<FileSearchData> thisPartResult = new ArrayList<>();
        if (file.isDirectory()) {
            for (File file1 : file.listFiles()) {
                if (file1.getName().toLowerCase().contains(key) && file1.isDirectory()) {
                    thisPartResult.add(new FileSearchData(file1, file1.getName().toLowerCase().indexOf(key)));
                }
                thisPartResult.addAll(searchInThisFloder(file1, key));
            }
        } else {
            if (file.getName().toLowerCase().contains(key)) {
                thisPartResult.add(new FileSearchData(file, file.getName().toLowerCase().indexOf(key)));
            }
        }
        Log.d("xiaoxiao", String.valueOf(thisPartResult.size()));
        return thisPartResult;
    }
}
