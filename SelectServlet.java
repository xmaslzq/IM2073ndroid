import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

public class SelectServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String choice = request.getParameter("choice");
        int questionNo = 8; // Hardcoded for now

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/clickerDB", "root", "your_password");
            Statement stmt = conn.createStatement();
            String sqlStr = "INSERT INTO responses (questionNo, choice) VALUES (" + questionNo + ", '" + choice + "')";
            stmt.executeUpdate(sqlStr);

            response.getWriter().println("Response recorded successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error: " + e.getMessage());
        }
    }
}
