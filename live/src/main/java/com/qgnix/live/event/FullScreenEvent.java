package com.qgnix.live.event;

public class FullScreenEvent {
    private boolean isLand;

    public FullScreenEvent(boolean isLand) {
        this.isLand = isLand;
    }

    public boolean isLand() {
        return isLand;
    }
}
