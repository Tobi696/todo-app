package com.example.todo;

import android.os.Build;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.O)
public class TodoRepository {
    public final static BaseTodoRepository instance = new TodoRepositoryCloud();
}
