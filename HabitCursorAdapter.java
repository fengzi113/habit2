package com.example.fengzi113.habit;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.fengzi113.habit.data.HabitContract;

/**
 * Created by fengzi113 on 2018/2/27.
 */

public class HabitCursorAdapter extends CursorAdapter {

    public HabitCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor,0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context)
                .inflate(R.layout.list_item,viewGroup,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameTextView = (TextView)view.findViewById(R.id.tv_thing);
        TextView summaryTextView = (TextView)view.findViewById(R.id.tv_duration);

        int nameColumnIndex = cursor.getColumnIndex(HabitContract.HabitEntry.COLUMN_HABIT_THING);
        int durationColumnIndex = cursor.getColumnIndex(HabitContract.HabitEntry.COLUMN_HABIT_DURATION);

        String name = cursor.getString(nameColumnIndex);
        String duration = cursor.getString(durationColumnIndex);

        nameTextView.setText(name);
        summaryTextView.setText(context.getString(R.string.summary_yijianchi)+ duration + context.getString(R.string.summary_tian));

        }
}
