package com.kosuri.stores.model.response;

import com.kosuri.stores.dao.TaskEntity;

import java.util.List;

public class GetAllTasksResponse extends GenericResponse {
    private List<TaskEntity> taskList;

    public List<TaskEntity> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<TaskEntity> taskList) {
        this.taskList = taskList;
    }
}
