package com.hiroshi.cimoc.rx;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Hiroshi on 2016/8/21.
 */
public class RxEvent {

    public static final int EVENT_COMIC_FAVORITE = 1;
    public static final int EVENT_COMIC_UNFAVORITE = 2;
    public static final int EVENT_COMIC_READ = 3;
    public static final int EVENT_COMIC_CHAPTER_CHANGE = 4;
    public static final int EVENT_COMIC_PAGE_CHANGE = 5;
    public static final int EVENT_COMIC_FAVORITE_RESTORE = 6;

    public static final int EVENT_TASK_STATE_CHANGE = 21;
    public static final int EVENT_TASK_PROCESS = 22;
    public static final int EVENT_TASK_INSERT = 23;

    public static final int EVENT_DOWNLOAD_REMOVE = 41;
    public static final int EVENT_DOWNLOAD_START = 42;
    public static final int EVENT_DOWNLOAD_STOP = 43;

    public static final int EVENT_TAG_UPDATE = 81;
    public static final int EVENT_TAG_RESTORE = 82;

    public static final int EVENT_THEME_CHANGE = 101;

    public static final int EVENT_DIALOG_PROGRESS = 121;

    @IntDef({EVENT_COMIC_FAVORITE, EVENT_COMIC_UNFAVORITE, EVENT_COMIC_READ, EVENT_COMIC_CHAPTER_CHANGE, EVENT_COMIC_FAVORITE_RESTORE,
            EVENT_TASK_STATE_CHANGE, EVENT_TASK_PROCESS, EVENT_TASK_INSERT, EVENT_DOWNLOAD_REMOVE, EVENT_DOWNLOAD_START,
            EVENT_DOWNLOAD_STOP, EVENT_THEME_CHANGE, EVENT_TAG_UPDATE, EVENT_TAG_RESTORE, EVENT_DIALOG_PROGRESS, EVENT_COMIC_PAGE_CHANGE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EventType {}

    private int type;
    private Object[] data;

    public RxEvent(@EventType int type, Object... data) {
        this.type = type;
        this.data = data;
    }

    public @EventType int getType() {
        return type;
    }

    public Object getData() {
        return getData(0);
    }

    public Object getData(int index) {
        return index < data.length ? data[index] : null;
    }

}
