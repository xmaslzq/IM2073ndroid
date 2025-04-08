//package com.example.teamtwo;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.sql.*;

@WebServlet("/select")
public class SelectServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String choice = request.getParameter("choice");
        String questionNoParam = request.getParameter("questionNo");

        response.setContentType("text/plain");

        if (choice == null || questionNoParam == null) {
            response.getWriter().write("Missing parameters");
            return;
        }

        try {
            int questionNo = Integer.parseInt(questionNoParam);

            // Fetch valid question numbers from the database
            boolean isValidQuestionNo = false;
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ebookshop", "root", "xxxx")) {
                String validQuestionSql = "SELECT questionNo FROM questions";
                try (PreparedStatement stmt = connection.prepareStatement(validQuestionSql);
                     ResultSet rs = stmt.executeQuery()) {

                    // Check if the provided questionNo exists in the database
                    while (rs.next()) {
                        int validQuestionNo = rs.getInt("questionNo");
                        if (validQuestionNo == questionNo) {
                            isValidQuestionNo = true;
                            break;
                        }
                    }
                }
            }

            // If the questionNo is invalid, send an error message
            if (!isValidQuestionNo) {
                response.getWriter().write("Invalid question number.");
                return;
            }

            // Proceed with storing the vote
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ebookshop", "root", "xxxx")) {
                String sql = "INSERT INTO responses (questionNo, choice) VALUES (?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setInt(1, questionNo);
                    statement.setString(2, choice);
                    statement.executeUpdate();
                }
            }

            response.getWriter().write("Choice recorded: " + choice + " for Question #" + questionNo);
        } catch (NumberFormatException e) {
            response.getWriter().write("Invalid question number.");
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().write("Database error.");
        }
    }
}
