package com.devBackend.employeeAuth.domain.entity;

public class Employees {

    public String name;
    public String cpf;
    public String password;

    public Employees() {
    }

    public Employees(String name, String cpf, String password) {
        this.name = name;
        this.cpf = cpf;
        this.password = password;
    }
}
