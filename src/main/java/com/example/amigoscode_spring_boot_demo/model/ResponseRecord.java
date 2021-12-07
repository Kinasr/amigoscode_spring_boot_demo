package com.example.amigoscode_spring_boot_demo.model;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Map;

public class ResponseRecord {
    Map<String, Object> data;
    HttpStatus status;

    public Map<String, Object> getData() {
        return data;
    }

    public ResponseRecord setData(Map<String, Object> data) {
        this.data = data;
        return this;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public ResponseRecord setStatus(HttpStatus status) {
        this.status = status;
        return this;
    }
}
