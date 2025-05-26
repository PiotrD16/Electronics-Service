import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Connection_Class
{
    Connection db_connection;
    ResultSet result_set;

    Connection_Class()
    {
        try {
            db_connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_project","root","P10tru1234!");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public boolean login(String login, String password)
    {
        String login_query = "SELECT * FROM users WHERE user_login = ? AND user_password = ?";

        try (PreparedStatement statement = db_connection.prepareStatement(login_query)) {
            statement.setString(1, login);
            statement.setString(2, password);
            ResultSet result_set = statement.executeQuery();

            return result_set.next();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean login_admin(String login, String password)
    {
        String login_query = "SELECT * FROM employees WHERE employee_login = ? AND employee_password = ?";

        try(PreparedStatement statement = db_connection.prepareStatement(login_query))
        {
            statement.setString(1,login);
            statement.setString(2,password);
            ResultSet result_set = statement.executeQuery();

            if(result_set.next())
                return true;
            else
                return false;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public boolean login_administrator(String login, String password)
    {
        String login_query = "SELECT * FROM admin WHERE admin_login = ? AND admin_password = ?";

        try(PreparedStatement statement = db_connection.prepareStatement(login_query))
        {
            statement.setString(1,login);
            statement.setString(2,password);
            ResultSet result_set = statement.executeQuery();
            return result_set.next();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public boolean registration(String name, String surname, String login, String password)
    {
        String registration_query = "INSERT INTO users (user_name, user_surname, user_login, user_password) VALUES (?,?,?,?)";
        String check_duplicates_query = "SELECT COUNT(*) FROM users WHERE user_login = ?";
        try{
            try(PreparedStatement check_duplicates_statement = db_connection.prepareStatement(check_duplicates_query)){
                check_duplicates_statement.setString(1,login);
                result_set = check_duplicates_statement.executeQuery();
                result_set.next();
                if(result_set.getInt(1) > 0)
                {
                    return false;
                }
            }

            try (PreparedStatement reg_statement = db_connection.prepareStatement(registration_query)) {
                reg_statement.setString(1,name);
                reg_statement.setString(2,surname);
                reg_statement.setString(3,login);
                reg_statement.setString(4,password);
                int rowsAffected = reg_statement.executeUpdate();
                System.out.println("Dodano wierszy: " + rowsAffected);
                return rowsAffected > 0;
            }

            }
        catch (Exception e) {
            System.out.println("Wystąpił wyjątek w rejestracji:");
            e.printStackTrace();
        }

        return false;
    }

    public boolean change_password(String user_ID, String new_password)
    {
        String change_password_query = "UPDATE users SET user_password = ? WHERE user_id = ?";
        try(PreparedStatement statement = db_connection.prepareStatement(change_password_query))
        {
            statement.setString(1,new_password);
            statement.setInt(2,Integer.parseInt(user_ID));
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public boolean get_user_data(String user_login, String[] user_data_array)
    {
        String get_user_data_query = "SELECT * FROM users WHERE user_login = ?";
        try(PreparedStatement statement = db_connection.prepareStatement(get_user_data_query))
        {
            statement.setString(1,user_login);
            ResultSet data_set = statement.executeQuery();

            if(data_set.next())
            {
                user_data_array[0] = String.valueOf(data_set.getInt("user_id"));
                user_data_array[1] = data_set.getString("user_name");
                user_data_array[2] = data_set.getString("user_surname");
                user_data_array[3] = data_set.getString("user_login");
                user_data_array[4] = data_set.getString("user_password");
                return true;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public boolean get_admin_data(String admin_login, String[] admin_data_array)
    {
        String get_admin_data_query = "SELECT * FROM employees WHERE employee_login = ?";

        try(PreparedStatement statement = db_connection.prepareStatement(get_admin_data_query))
        {
            statement.setString(1,admin_login);
            ResultSet data_set = statement.executeQuery();
            while(data_set.next())
            {
                admin_data_array[0] = String.valueOf(data_set.getInt("employee_id"));
                admin_data_array[1] = data_set.getString("employee_name");
                admin_data_array[2] = data_set.getString("employee_login");
                admin_data_array[3] = data_set.getString("employee_job");
                return true;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public List<String[]> get_order_history(String user_ID)
    {
        List<String[]> orderList = new ArrayList<String[]>();

        String query = """
            SELECT
                CONCAT(users.user_name,' ',users.user_surname) AS imie_i_nazwisko,
                devices.device_name AS nazwa_urzadzenia,
                CONCAT(employees.employee_name, ' - ', employees.employee_job) AS inzynier,
                repairs.repair_cost AS koszt,
                repairs.repair_finish_date AS data_zakonczenia
            FROM repairs
            LEFT JOIN users ON repairs.user_id = users.user_id
            LEFT JOIN devices ON repairs.device_id = devices.device_id
            LEFT JOIN employees ON repairs.employee_id = employees.employee_id
            WHERE repairs.user_id = ?
        """;

        try (PreparedStatement statement = db_connection.prepareStatement(query)) {
            statement.setInt(1, Integer.parseInt(user_ID));
            ResultSet data_set = statement.executeQuery();

            while (data_set.next()) {
                String[] row = new String[5];
                row[0] = data_set.getString("imie_i_nazwisko");
                row[1] = data_set.getString("nazwa_urzadzenia");
                row[2] = data_set.getString("inzynier");
                row[3] = String.valueOf(data_set.getDouble("koszt"));
                row[4] = String.valueOf(data_set.getDate("data_zakonczenia"));
                orderList.add(row);
                System.out.println(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return orderList;
    }

    public List<String[]> get_order_history_admin(String admin_ID)
    {
        List<String[]> orderList = new ArrayList<String[]>();

        String query = """
            select
                    CONCAT(users.user_name, ' ', users.user_surname) AS Klient,
                    devices.device_name AS Sprzet,
                    repairs.repair_cost AS Koszt,
                    repairs.repair_finish_date AS Data,
                    repairs.is_finished AS Status,
                    repairs.repair_id AS ID
                    from repairs
                    LEFT JOIN users on repairs.user_id=users.user_id
                    LEFT JOIN devices on repairs.device_id=devices.device_id
                    WHERE repairs.employee_id = ?;
        """;

        try (PreparedStatement statement = db_connection.prepareStatement(query)) {
            statement.setInt(1, Integer.parseInt(admin_ID));
            ResultSet data_set = statement.executeQuery();

            while (data_set.next()) {
                String[] row = new String[6];
                row[0] = data_set.getString("Klient");
                row[1] = data_set.getString("Sprzet");
                row[2] = String.valueOf(data_set.getDouble("Koszt"));
                row[3] = String.valueOf(data_set.getDate("Data"));
                row[4] = data_set.getString("Status");
                row[5] = String.valueOf(data_set.getInt("ID"));
                orderList.add(row);
                System.out.println(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return orderList;
    }

    public boolean send_new_order_(String user_ID, String device_type, String issue_description, File image_file)
    {
        int employee_ID;
        int user_ID_int = Integer.parseInt(user_ID);
        int device_ID;

        switch (device_type)
        {
            case "Laptop", "Tablet":
                employee_ID = 1;
                break;
            case "Smartfon":
                employee_ID = 2;
                break;
            case "Monitor", "Telewizor":
                employee_ID = 3;
                break;
            default:
                employee_ID = 2;
        }

        // Dodawanie do devices
        String query_devices = "INSERT INTO devices (device_name, user_id) VALUES (?, ?)";

        try (PreparedStatement statement = db_connection.prepareStatement(query_devices, Statement.RETURN_GENERATED_KEYS))
        {
            statement.setString(1, device_type);
            statement.setInt(2, user_ID_int);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 0) {
                System.out.println("Nie udało się dodać urządzenia.");
                return false;
            }

            ResultSet keys = statement.getGeneratedKeys();
            if (keys.next()) {
                device_ID = keys.getInt(1);
            } else {
                System.out.println("Nie udało się pobrać device_id.");
                return false;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }

        // Dodanie do repairs
        String query_repairs = "INSERT INTO repairs (user_id, employee_id, device_id, is_finished) VALUES (?, ?, ?, 'Oczekuje')";

        try (PreparedStatement statement = db_connection.prepareStatement(query_repairs, Statement.RETURN_GENERATED_KEYS))
        {
            statement.setInt(1, user_ID_int);
            statement.setInt(2, employee_ID);
            statement.setInt(3, device_ID);
            int rowsAffected = statement.executeUpdate();

            int repair_ID = 0;
            ResultSet keys = statement.getGeneratedKeys();
            if (keys.next()) {
                repair_ID = keys.getInt(1);
            }

            String connection_string = "mongodb://localhost:27017";
            MongoOrderRepository mongodb_send_data = new MongoOrderRepository(
                    connection_string,"electronic_service","images");

            mongodb_send_data.insertOrder(Integer.parseInt(user_ID),repair_ID,employee_ID,issue_description,image_file);

            return rowsAffected > 0;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update_repair(int repair_id, java.sql.Date finish_date, double cost, String status)
    {
        String query = "UPDATE repairs SET repair_finish_date = ?, repair_cost = ?, is_finished = ? WHERE repair_id = ?";

        try(PreparedStatement statement = db_connection.prepareStatement(query))
        {
            statement.setDate(1,finish_date);
            statement.setDouble(2,cost);
            statement.setString(3,status);
            statement.setInt(4,repair_id);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public List<String[]> get_repair_logs() {
        String query = "SELECT * FROM repair_log";
        List<String[]> data = new ArrayList<>();
        try (PreparedStatement statement = db_connection.prepareStatement(query)) {
            ResultSet result_set = statement.executeQuery();
            while (result_set.next()) {
                String[] repair_logs_array = new String[6]; // utwórz nową tablicę na każdy wiersz
                repair_logs_array[0] = String.valueOf(result_set.getInt("log_id"));
                repair_logs_array[1] = String.valueOf(result_set.getInt("repair_id"));
                repair_logs_array[2] = String.valueOf(result_set.getInt("user_id"));
                repair_logs_array[3] = result_set.getString("user_name");  // poprawka na getString
                repair_logs_array[4] = String.valueOf(result_set.getInt("employee_id")); // zakładam, że employee_id to int
                repair_logs_array[5] = String.valueOf(result_set.getDate("log_time"));
                data.add(repair_logs_array);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public boolean remove_log_record(int log_id)
    {
        String query = "DELETE FROM repair_log WHERE log_id = ?";

        try(PreparedStatement statement = db_connection.prepareStatement(query))
        {
            statement.setInt(1, log_id);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

}
