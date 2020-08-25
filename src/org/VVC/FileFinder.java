package org.VVC;
import org.apache.commons.collections4.ListUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;


public class FileFinder {

    public static List<File> listAllFiles(String directoryName) {
        File directory = new File(directoryName);

        List<File> resultList = new ArrayList<File>();

        // get all the files from a directory
        File[] fList = directory.listFiles();
        resultList.addAll(Arrays.asList(fList));
        for (File file : fList) {
            if (file.isFile()) {
            } else if (file.isDirectory()&&!file.getName().equals(".vvc")) {
                resultList.addAll(listAllFiles(file.getAbsolutePath()));
            }
        }
        //System.out.println(fList);
        return resultList;
    }

    public static boolean CompareFiles(FileAttributes fileAtt,File file2){
        BasicFileAttributes attr2 = null;
        try {
            attr2 = Files.readAttributes(file2.toPath(), BasicFileAttributes.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(attr2==null)return false;
        if(!fileAtt.getName().equals(file2.getName())){
            //System.out.println("diferent Name"+fileAtt.getName()+" "+file2.getName());
            return false;
        }
        if(!fileAtt.getSize().equals(Long.toString(attr2.size()))){
            //System.out.println("diferent size"+fileAtt.getSize()+" "+ Long.toString(attr2.size()));
            return false;
        }
        if(!fileAtt.getCreationDate().equals(attr2.creationTime().toString())){
            //System.out.println("diferent create"+fileAtt.getCreationDate()+" "+attr2.creationTime().toString());
            return false;
        }
        if(!fileAtt.getUpdateTime().equals(attr2.lastModifiedTime().toString())){
            //System.out.println("diferent update"+fileAtt.getUpdateTime()+" "+attr2.lastModifiedTime().toString());
            return false;
        }

        return true;

    }

    public static List<File> listAllUnIgnoredFiles(){
       List<File>ignoredList =  XmlController.getIgnoreList();
       if(ignoredList==null){
           return listAllFiles("./");
       }
       List<File>allFiles= ListUtils.subtract(listAllFiles("./"),ignoredList);
       return allFiles;
    }

    public static void backupFiles(List<File>fileList,String dirName){
        File dir=new File("./.vvc/"+dirName);
        dir.mkdir();
        for (File f:fileList){
            String[]dirs= getDirFromPath(XmlController.localPath(f.getAbsolutePath()));
            StringBuilder createdPath= new StringBuilder("./.vvc/" + dirName + "/");

            for (int i = 0; i <dirs.length ; i++) {
                File nDir=new File(createdPath.toString()+dirs[i]);
                if(nDir.mkdir()){
                    createdPath.append(dirs[i]).append("/");
                }
            }

            try {
                File destination=new File(createdPath.toString()+f.getName());
                Files.copy(f.toPath(),destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String[]getDirFromPath(String path){
        String[]split=path.split("\\\\");
        String[]result=Arrays.copyOf(split, split.length-1);
        return result;
    }

    public void restoreFiles(List<File>fileList,List<FileAttributes>fileAttributes){

    }
}
