package org.VVC;

public class FileAttributes {
    private String name;
    private String size;
    private String creationDate;
    private String updateTime;
    private String filePlath;

    public FileAttributes(String name, String size, String creationDate, String updateTime,String filePath) {
        this.name = name;
        this.size = size;
        this.creationDate = creationDate;
        this.updateTime = updateTime;
        this.filePlath=filePath;
    }

    public String getName() {
        return name;
    }

    public String getSize() {
        return size;
    }

    public String getCreationDate() {
        return creationDate;
    }


    public String getUpdateTime() {
        return updateTime;
    }

    public String getFilePlath() {
        return filePlath;
    }
}
