package com.example.fengzi113.habit.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by fengzi113 on 2018/2/26.
 */

public final class HabitContract {

    private HabitContract(){}

    public static final String CONTENT_AUTHORITY = "com.example.fengzi113.habit";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_HABIT = "habit";

    public static final class HabitEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                Uri.withAppendedPath(BASE_CONTENT_URI, PATH_HABIT);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_HABIT;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_HABIT;

        public final static String TABLE_NAME = "habit";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_HABIT_THING = "thing";
        public final static String COLUMN_HABIT_DURATION = "duration";

    }


}
