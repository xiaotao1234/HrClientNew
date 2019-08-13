package com.huari.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;
import com.huari.dataentry.FileSearchData;
import com.huari.dataentry.FileSearchMassage;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

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
    List<File> searchResults = new ArrayList<>();//搜索结果列表
    List<recentContent> recentUseFiles = new LinkedList<>();//最近有对其进行操作的文件列表
    File currentFloder;//当前所在目录
    File currentFile;//当前正在使用的文件
    private static FileOsImpl fileOsImpl;
    String forSaveFloder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
    private Thread thread;
    public int MUSIC = 0,WORD = 1,EXCEL = 2;

    public FileOsImpl() {
    }

    class recentContent{
        String file;
        int type;

        public recentContent(String file, int type) {
            this.file = file;
            this.type = type;
        }

        public void setFile(String file) {
            this.file = file;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getFile() {

            return file;
        }

        public int getType() {
            return type;
        }
    }


    /**
     * @param filename
     * @param type
     */
    public void addRecentFile(String filename,int type){
        if(recentUseFiles.size()>500){
            recentUseFiles.remove(0);//Retrieves and removes the head (first element) of this list.
            recentUseFiles.add(new recentContent(filename,type));
        }
    }

    /**
     * @return这个方法返回的是至头节点到尾节点时间依次由远到近
     */
    public List<recentContent> getRecentUseFiles(){
        return recentUseFiles;
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
        SharedPreferences sharedPreferences = context.getSharedPreferences("User",Context.MODE_PRIVATE);
        String filepatc = sharedPreferences.getString("RootDirectory",Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath());
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
            long timeafter = System.currentTimeMillis()-time;
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
                if (file1.getName().toLowerCase().contains(key)&&file1.isDirectory()){
                    thisPartResult.add(new FileSearchData(file1,file1.getName().toLowerCase().indexOf(key)));
                }
                thisPartResult.addAll(searchInThisFloder(file1, key));
            }
        } else {
            if (file.getName().toLowerCase().contains(key)){
                thisPartResult.add(new FileSearchData(file,file.getName().toLowerCase().indexOf(key)));
            }
        }
        Log.d("xiaoxiao", String.valueOf(thisPartResult.size()));
        return thisPartResult;
    }
}
