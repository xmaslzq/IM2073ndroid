//package com.example.teamtwo;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.sql.*;
import java.util.*;

@WebServlet("/display")
public class DisplayServlet extends HttpServlet {

    private static final int QUESTION_DURATION_SECONDS = 60;
    private static final Map<Integer, Long> questionStartTimes = new HashMap<>();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String questionNoParam = request.getParameter("questionNo");
        int questionNo = (questionNoParam != null) ? Integer.parseInt(questionNoParam) : 1;

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ebookshop", "root", "xxxx")) {

            String questionSql = "SELECT questionText, optionA, optionB, optionC, optionD FROM questions WHERE questionNo=?";
            try (PreparedStatement questionStatement = connection.prepareStatement(questionSql)) {
                questionStatement.setInt(1, questionNo);
                ResultSet questionResult = questionStatement.executeQuery();

                if (!questionResult.next()) {
                    response.getWriter().write("{\"end\": true}");
                    return;
                }

                String questionText = questionResult.getString("questionText");
                String optionA = questionResult.getString("optionA");
                String optionB = questionResult.getString("optionB");
                String optionC = questionResult.getString("optionC");
                String optionD = questionResult.getString("optionD");

                // Track and compute time remaining
                long currentTimeMillis = System.currentTimeMillis();
                questionStartTimes.putIfAbsent(questionNo, currentTimeMillis);
                long elapsedMillis = currentTimeMillis - questionStartTimes.get(questionNo);
                int timeRemaining = Math.max(0, QUESTION_DURATION_SECONDS - (int) (elapsedMillis / 1000));

                String voteSql = "SELECT choice, COUNT(*) AS count FROM responses WHERE questionNo=? GROUP BY choice";
                try (PreparedStatement voteStatement = connection.prepareStatement(voteSql)) {
                    voteStatement.setInt(1, questionNo);
                    ResultSet voteResultSet = voteStatement.executeQuery();

                    StringBuilder json = new StringBuilder("{");
                    json.append("\"questionText\": \"").append(questionText).append("\",");
                    json.append("\"optionA\": \"").append(optionA).append("\",");
                    json.append("\"optionB\": \"").append(optionB).append("\",");
                    json.append("\"optionC\": \"").append(optionC).append("\",");
                    json.append("\"optionD\": \"").append(optionD).append("\",");
                    json.append("\"timeRemaining\": ").append(timeRemaining).append(",");
                    json.append("\"votes\": {");

                    while (voteResultSet.next()) {
                        String choice = voteResultSet.getString("choice").toUpperCase();
                        int count = voteResultSet.getInt("count");
                        json.append("\"").append(choice).append("\": ").append(count).append(",");
                    }

                    if (json.charAt(json.length() - 1) == ',') {
                        json.deleteCharAt(json.length() - 1);
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
