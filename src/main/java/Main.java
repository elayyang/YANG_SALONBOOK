
import dao.DatabaseConnection;
import ui.Menu;

public class Main {

    public static void main(String[] args) {

        // Create all required database tables.
        DatabaseConnection.createTable();
        DatabaseConnection.createClientTable();
        DatabaseConnection.createServiceTable();
        DatabaseConnection.createAppointmentTable();
        DatabaseConnection.createActivityLogTable();

        // Start the application.
        Menu menu = new Menu();
        menu.start();
    }
}
