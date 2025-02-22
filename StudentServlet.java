package com.company;
import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.io.FileWriter;
@WebServlet(name = "StudentServlet", urlPatterns = "/servlet")
public class StudentServlet extends HttpServlet {
    private StudentDAO studentDAO;
    private Gson gson;
    private static final Logger LOGGER = Logger.getLogger(StudentDAO.class.getName());
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
    @Override

    public void init() {
        studentDAO = new StudentDAO();
        gson = new Gson();

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = request.getParameter("action");
        if (action == null) {
            getAllStudents(request, response);
        } else if (action.equals("get")) {
            getStudent(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String action = request.getParameter("action");
        if (action != null && action.equals("create")) {
            createStudent(request, response);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        updateStudent(request, response);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        deleteStudent(request, response);
    }

    private void getAllStudents(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        out.println(gson.toJson(studentDAO.getAllStudents()));
    }

    private void getStudent(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int studentId = Integer.parseInt(request.getParameter("id"));
        PrintWriter out = response.getWriter();
        out.println(gson.toJson(studentDAO.getStudentById(studentId)));
    }

    private void createStudent(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String requestBody = request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
        Student student = gson.fromJson(requestBody, Student.class);
        response.setContentType("text/plain");
        response.setHeader("Content-Disposition", "attachment; filename=\"student_info.txt\"");
        try (PrintWriter writer = response.getWriter()) {
            // Записываем данные о студенте в файл txt
            writer.println("First Name: " + student.getFirstName());
            writer.println("Last Name: " + student.getLastName());
            writer.println("Group: " + student.getGroupName());
            writer.println("Age: " + student.getAge());
            writer.println("Subject: " + student.getSubject());
        }
        if (requestBody.isEmpty()) {
            LOGGER.log(Level.INFO, "Request body is empty");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        LOGGER.log(Level.INFO, "Received student: "+student);
        studentDAO.insertStudent(student);
        response.setStatus(HttpServletResponse.SC_CREATED);

    }

    private void updateStudent(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String requestBody = request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
        Student student = gson.fromJson(requestBody, Student.class);
        studentDAO.updateStudent(student);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private void deleteStudent(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int studentId = Integer.parseInt(request.getParameter("id"));
        studentDAO.deleteStudent(studentId);
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}
