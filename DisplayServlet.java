import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

public class DisplayServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/clickerDB", "root", "your_password");
            Statement stmt = conn.createStatement();
            String sqlStr = "SELECT choice, COUNT(*) AS count FROM responses WHERE questionNo=8 GROUP BY choice";
            ResultSet rs = stmt.executeQuery(sqlStr);

            out.println("<html><body><h2>Response Statistics</h2><table border='1'><tr><th>Choice</th><th>Count</th></tr>");
            while (rs.next()) {
                out.println("<tr><td>" + rs.getString("choice") + "</td><td>" + rs.getInt("count") + "</td></tr>");
            }
            out.println("</table></body></html>");
        } catch (Exception e) {
            e.printStackTrace();
            out.println("Error: " + e.getMessage());
        }
    }
}
