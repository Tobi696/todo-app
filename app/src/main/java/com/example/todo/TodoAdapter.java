package com.example.todo;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import org.w3c.dom.Text;

import java.util.List;

public class TodoAdapter extends BaseAdapter {
    private int layoutId;
    private List<Todo> todos;
    private LayoutInflater inflater;
    private OverviewActivity overviewActivity;

    public TodoAdapter(Context ctx, int layoutId, List<Todo> todos, OverviewActivity overviewActivity) {
        this.todos = todos;
        this.layoutId = layoutId;
        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.overviewActivity = overviewActivity;
    }


    @Override
    public int getCount() {
        return todos.size();
    }

    @Override
    public Object getItem(int i) {
        return todos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Todo todo = todos.get(i);
        View listViewItem = (view == null) ? inflater.inflate(layoutId, null) : view;
        CheckBox checkBox = listViewItem.findViewById(R.id.todoItemCheckBox);
        checkBox.setChecked(todo.isCompleted());
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                todo.setCompleted(checkBox.isChecked());
                TodoRepository.instance.update(todo);
            }
        });

        listViewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overviewActivity.startEditTodoIntent(todo);
            }
        });

        ((TextView) listViewItem.findViewById(R.id.todoText)).setText(todo.getText());
        ((TextView) listViewItem.findViewById(R.id.todoUntil)).setText(((todo.getDate() == null ? "" : todo.getDate().format(OverviewActivity.dateFormatter)) + " " + (todo.getTime() == null ? "" : todo.getTime().format(OverviewActivity.timeFormatter)).trim()));
        ((TextView) listViewItem.findViewById(R.id.todoLocation)).setText(todo.getAddress() + " (" + todo.getLatitude() + ", " + todo.getLongitude() + ")");
        return listViewItem;
    }
}
