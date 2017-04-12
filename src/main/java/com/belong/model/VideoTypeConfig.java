package com.belong.model;

public class VideoTypeConfig {
    private String videoNo;

    private String videoType;

    public String getVideoNo() {
        return videoNo;
    }

    public void setVideoNo(String videoNo) {
        this.videoNo = videoNo == null ? null : videoNo.trim();
    }

    public String getVideoType() {
        return videoType;
    }

    public void setVideoType(String videoType) {
        this.videoType = videoType == null ? null : videoType.trim();
    }
}