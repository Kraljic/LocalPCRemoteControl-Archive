package com.example.localpcremotecontrol.communication;

public enum Controls {
    POWER_OFF(1),
    LOCK_PC(2),
    VOLUME_UP(3),
    VOLUME_DOWN(4),
    PLAY_TOGGLE(5),
    MUTE(6),
    PREV(7),
    NEXT(8),
    CHANGE_SUBTITLE(9);

    private int code;

    Controls(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }

    public static Controls getEControlFromInt(int code) {
        Controls selected = null;
        for (Controls ec : Controls.values()) {
            if (ec.getCode() == code) {
                selected = ec;
            }
        }
        return selected;
    }
}
