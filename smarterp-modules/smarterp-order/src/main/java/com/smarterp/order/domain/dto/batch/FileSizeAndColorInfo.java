package com.smarterp.order.domain.dto.batch;


import java.util.List;

public class FileSizeAndColorInfo {

   private String color;
   private List<FileInfo> fileList;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<FileInfo> getFileList() {
        return fileList;
    }

    public void setFileList(List<FileInfo> fileList) {
        this.fileList = fileList;
    }
}
