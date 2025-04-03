import java.io.*;
import java.sql.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet("/login")  // Configure the request URL for this servlet
public class LoginServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Set response content type
        response.setContentType("text/html");

        // Retrieve input from the request
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            response.sendRedirect("login.html?error=Username and Password are required");
            return;
        }

        try (
            // Step 1: Allocate a database connection
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/ebookshop?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
                "myuser", "xxxx");  // Replace with your database credentials

            //Prepare SQL statement to check credentials
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM users WHERE username = ? AND password = ?")
        ) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Valid credentials, create a session
                HttpSession session = request.getSession();
                session.setAttribute("username", username);
                response.sendRedirect("Home.html");  // Redirect to homepage
            } else {
                // Invalid credentials, redirect with an error message
                response.sendRedirect("login.html?error=Invalid username or password");
            }
        } catch (SQLException ex) {
            response.sendRedirect("login.html?error=Database error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
