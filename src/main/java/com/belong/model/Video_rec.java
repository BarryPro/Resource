package com.belong.model;

public class Video_rec {
    private String videoName;

    private String videoPic;

    private String videoSrc;

    private String actionList;

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName == null ? null : videoName.trim();
    }

    public String getVideoPic() {
        return videoPic;
    }

    public void setVideoPic(String videoPic) {
        this.videoPic = videoPic == null ? null : videoPic.trim();
    }

    public String getVideoSrc() {
        return videoSrc;
    }

    public void setVideoSrc(String videoSrc) {
        this.videoSrc = videoSrc == null ? null : videoSrc.trim();
    }

    public String getActionList() {
        return actionList;
    }

    public void setActionList(String actionList) {
        this.actionList = actionList == null ? null : actionList.trim();
    }
}