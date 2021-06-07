package com.example.schedular;

public class Model {
    private String task , Description , id , date;

    public Model(String task, String description, String id, String date) {
        this.task = task; //constructor
        Description = description;
        this.id = id;
        this.date = date;
    }

    public Model(){

    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}