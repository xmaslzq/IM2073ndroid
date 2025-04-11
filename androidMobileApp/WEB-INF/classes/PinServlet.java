import java.io.*;
import java.sql.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.util.Random;

@WebServlet("/pinServlet")
public class PinServlet extends HttpServlet {

    // Handle GET requests for pin generation and pin validation
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("generate".equals(action)) {
            // Generate and save pin
            String pin = generatePin();
            savePin(pin);

            // Send the generated pin as the response
            response.getWriter().write(pin);
        } else if ("validate".equals(action)) {
            // Validate pin and delete it after successful validation
            String pin = request.getParameter("pin");
            if (pin != null && !pin.isEmpty()) {
                boolean isValid = validatePin(pin);
                if (isValid) {
                    deletePin(pin); // Delete the pin after it's validated
                    response.getWriter().write("valid");
                } else {
                    response.getWriter().write("invalid");
                }
            } else {
                response.getWriter().write("invalid");
            }
        }
    }

    // Method to generate a 6-digit PIN
    private String generatePin() {
        Random random = new Random();
        String pin = String.format("%06d", random.nextInt(1000000));
        return pin;
    }

    // Method to save the generated pin into the database
    private void savePin(String pin) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/ebookshop", "root", "xxxx")) {
            String query = "INSERT INTO pins (pin) VALUES (?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, pin);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to validate the pin from the database
    private boolean validatePin(String pin) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/ebookshop", "root", "xxxx")) {
            String query = "SELECT * FROM pins WHERE pin = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, pin);
                ResultSet rs = stmt.executeQuery();
                return rs.next(); // Returns true if the pin exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method to delete the pin from the database after successful validation
    private void deletePin(String pin) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/ebookshop", "root", "xxxx")) {
            String deleteQuery = "DELETE FROM pins WHERE pin = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                deleteStmt.setString(1, pin);
                deleteStmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}