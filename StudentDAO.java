package com.company;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class StudentDAO {
    private static final Logger LOGGER = Logger.getLogger(StudentDAO.class.getName());
    private static final String SELECT_STUDENT_BY_ID = "SELECT * FROM students WHERE id = ?";
    private String jdbcURL = "jdbc:mysql://localhost:3306/studentdb";
    private String jdbcUsername = "root";
    private String jdbcPassword = "12345";
    static {
        try {
            // Создаем FileHandler для записи логов в файл
            FileHandler fileHandler = new FileHandler("application.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);

            // Устанавливаем уровень логирования
            LOGGER.setLevel(Level.ALL);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to set up file handler for logger.", e);
        }
    }

    private static final String INSERT_STUDENT_SQL = "INSERT INTO students (first_name, last_name, group_name, age, subject) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_ALL_STUDENTS = "SELECT * FROM students";
    private static final String UPDATE_STUDENT_SQL = "UPDATE students SET first_name = ?, last_name = ?, group_name = ?, age = ?, subject = ? WHERE id = ?";
    private static final String DELETE_STUDENT_SQL = "DELETE FROM students WHERE id = ?";

    public Student getStudentById(int id) {
        Student student = null;
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_STUDENT_BY_ID)) {
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String groupName = rs.getString("group_name");
                int age = rs.getInt("age");
                String subject = rs.getString("subject");
                student = new Student(id, firstName, lastName, groupName, age, subject);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return student;
    }

    public StudentDAO() {}

    protected Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
            LOGGER.log(Level.INFO, "Successfully connected to the database.");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, "Failed to connect to the database.", e);
        }
        return connection;
    }

    public void insertStudent(Student student) {
        try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(INSERT_STUDENT_SQL)) {
            preparedStatement.setString(1, student.getFirstName());
            preparedStatement.setString(2, student.getLastName());
            preparedStatement.setString(3, student.getGroupName());
            preparedStatement.setInt(4, student.getAge());
            preparedStatement.setString(5, student.getSubject());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_STUDENTS)) {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String groupName = rs.getString("group_name");
                int age = rs.getInt("age");
                String subject = rs.getString("subject");
                students.add(new Student(id, firstName, lastName, groupName, age, subject));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    public boolean updateStudent(Student student) {
        boolean rowUpdated = false;
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(UPDATE_STUDENT_SQL)) {
            statement.setString(1, student.getFirstName());
            statement.setString(2, student.getLastName());
            statement.setString(3, student.getGroupName());
            statement.setInt(4, student.getAge());
            statement.setString(5, student.getSubject());
            statement.setInt(6, student.getId());

            rowUpdated = statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowUpdated;
    }

    public boolean deleteStudent(int id) {
        boolean rowDeleted = false;
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(DELETE_STUDENT_SQL)) {
            statement.setInt(1, id);
            rowDeleted = statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowDeleted;
    }
}
