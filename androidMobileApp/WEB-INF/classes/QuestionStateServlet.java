import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.sql.*;
import java.util.*;
@WebServlet("/questionState")
public class QuestionStateServlet extends HttpServlet {
    private int currentQuestionNo = 1;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.getWriter().write("{\"currentQuestionNo\": " + currentQuestionNo + "}");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        currentQuestionNo++;
        response.setContentType("application/json");
        response.getWriter().write("{\"message\": \"Question number updated\", \"currentQuestionNo\": " + currentQuestionNo + "}");
    }
}
