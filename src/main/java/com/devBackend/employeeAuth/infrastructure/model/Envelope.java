package com.devBackend.employeeAuth.infrastructure.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Document(collection = "${mongodb.collections.envelopes}")
public class Envelope<T> {

    @Id
    private String id;

    private LocalDateTime createdAt;

    private LocalDateTime lastUpdatedAt;

    @Valid
    @NotNull
    private T body;

    public Envelope() {
    }

    public Envelope(String id, LocalDateTime createdAt, LocalDateTime updatedAt, T body) {
        this.id = id;
        this.createdAt = createdAt;
        this.lastUpdatedAt = updatedAt;
        this.body = body;
    }

    public static <T> Envelope<T> of(T body) {
        LocalDateTime now = LocalDateTime.now();
        return new Envelope<>(null, now, now, body);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.lastUpdatedAt = updatedAt;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public T body() {
        return body;
    }
}
