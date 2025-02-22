package com.company;

import java.io.PrintWriter;
public class Student {
    private int id;
    private String firstName;
    private String lastName;
    private String groupName;
    private int age;
    private String subject;

    // Геттеры
    public int getId() {
        return id;
    }

    public String getFirstName() {
        System.out.println("Пользователь ввел: " + firstName);
        return firstName;
    }

    public String getLastName() {
        System.out.println("Пользователь ввел: " + lastName);
        return lastName;
    }

    public String getGroupName() {
        System.out.println("Пользователь ввел: " + groupName);
        return groupName;
    }

    public int getAge() {
        System.out.println("Пользователь ввел: " + age);
        return age;
    }

    public String getSubject() {
        System.out.println("Пользователь ввел: " + subject);
        return subject;
    }
    public Student(int id, String firstName, String lastName, String groupName, int age, String subject) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.groupName = groupName;
        this.age = age;
        this.subject = subject;
    }
}
