package com.qgnix.live.event;

public class VideoActionEvent {
    private int action;
    private boolean complete;

    public VideoActionEvent(int action) {
        this.action = action;
    }

    public VideoActionEvent(boolean complete) {
        this.complete = complete;
    }

    public boolean isComplete() {
        return complete;
    }

    public int getAction() {
        return action;
    }


}
