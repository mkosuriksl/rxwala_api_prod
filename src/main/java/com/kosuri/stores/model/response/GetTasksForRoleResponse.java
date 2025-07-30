package com.kosuri.stores.model.response;

import com.kosuri.stores.model.role.Task;

import java.util.List;

public class GetTasksForRoleResponse extends GenericResponse {
    private List<Task> taskList;

    public List<Task> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
    }
}
