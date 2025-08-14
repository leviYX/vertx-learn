package com.levi.database;

public class Result {

    private int id;
    private String response;
    private String status;

    public Result() {}

    public Result(int id, String response, String status) {
        this.id = id;
        this.response = response;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Result{" +
                "id=" + id +
                ", response='" + response + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
