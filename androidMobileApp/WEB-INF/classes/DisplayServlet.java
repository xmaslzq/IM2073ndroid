//package com.example.teamtwo;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.sql.*;
import java.util.*;

@WebServlet("/display")
public class DisplayServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Retrieve questionNo from the request parameter
        String questionNoParam = request.getParameter("questionNo");

        // Default to 1 if questionNo is not provided
        int questionNo = (questionNoParam != null) ? Integer.parseInt(questionNoParam) : 1;

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ebookshop", "root", "xxxx")) {
            // Fetch the question text and options
            String questionSql = "SELECT questionText, optionA, optionB, optionC, optionD FROM questions WHERE questionNo=?";
            try (PreparedStatement questionStatement = connection.prepareStatement(questionSql)) {
                questionStatement.setInt(1, questionNo);
                ResultSet questionResult = questionStatement.executeQuery();

                if (!questionResult.next()) {
                    // No more questions, return an "end" message
                    response.getWriter().write("{\"end\": true}");
                    return;
                }

                String questionText = questionResult.getString("questionText");
                String optionA = questionResult.getString("optionA");
                String optionB = questionResult.getString("optionB");
                String optionC = questionResult.getString("optionC");
                String optionD = questionResult.getString("optionD");

                // Fetch the vote counts for choices A, B, C, D
                String voteSql = "SELECT choice, COUNT(*) AS count FROM responses WHERE questionNo=? GROUP BY choice";
                try (PreparedStatement voteStatement = connection.prepareStatement(voteSql)) {
                    voteStatement.setInt(1, questionNo);
                    ResultSet voteResultSet = voteStatement.executeQuery();

                    // Build JSON response with question text, options, and vote counts
                    StringBuilder json = new StringBuilder("{");
                    json.append("\"questionText\": \"").append(questionText).append("\",");
                    json.append("\"optionA\": \"").append(optionA).append("\",");
                    json.append("\"optionB\": \"").append(optionB).append("\",");
                    json.append("\"optionC\": \"").append(optionC).append("\",");
                    json.append("\"optionD\": \"").append(optionD).append("\",");
                    json.append("\"votes\": {");

                    while (voteResultSet.next()) {
                        String choice = voteResultSet.getString("choice").toUpperCase(); // A/B/C/D
                        int count = voteResultSet.getInt("count");
                        json.append("\"").append(choice).append("\": ").append(count).append(",");
                    }
                    if (json.charAt(json.length() - 1) == ',') {
                        json.deleteCharAt(json.length() - 1); // remove trailing comma
                    }

                    json.append("}}");

                    response.getWriter().write(json.toString());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().write("{\"error\": \"Database error.\"}");
        }
    }
}
