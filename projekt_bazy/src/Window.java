import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Window extends JFrame implements ActionListener {
    private JLabel login_label, password_label, app_title_label, login_result_label, reg_result_label;
    private JTextField login_field;
    private JPasswordField password_field;
    private JButton log_button, reset_button, register_button;

    private JLabel name_label, surname_label;
    private JTextField name_field, surname_field;

    private boolean is_registration_form_open = false;

    public Window() {
        // --- Komunikaty ---
        login_result_label = new JLabel("");
        login_result_label.setFont(new Font("Times New Roman", Font.ITALIC, 15));
        login_result_label.setBounds(125, 250, 250, 35);
        this.add(login_result_label);

        reg_result_label = new JLabel("");
        reg_result_label.setFont(new Font("Times New Roman", Font.ITALIC, 15));
        reg_result_label.setBounds(100, 380, 500, 35);
        reg_result_label.setVisible(false);
        this.add(reg_result_label);

        // --- Tytuł aplikacji ---
        app_title_label = new JLabel("Serwis Elektroniczny");
        app_title_label.setFont(new Font("Times New Roman", Font.ITALIC, 20));
        app_title_label.setIcon(new ImageIcon("logo.png"));
        app_title_label.setBounds(90, 25, 600, 50);

        // --- Pola logowania ---
        login_label = new JLabel("Login: ");
        login_label.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        login_label.setBounds(50, 100, 75, 25);

        password_label = new JLabel("Hasło: ");
        password_label.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        password_label.setBounds(50, 150, 75, 25);

        login_field = new JTextField();
        login_field.setBounds(125, 100, 200, 25);
        login_field.setFont(new Font("Times New Roman", Font.PLAIN, 15));

        password_field = new JPasswordField();
        password_field.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        password_field.setBounds(125, 150, 200, 25);

        // --- Przyciski ---
        log_button = new JButton("Zaloguj się");
        log_button.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        log_button.setBounds(125, 200, 100, 25);
        log_button.addActionListener(this);

        reset_button = new JButton("Resetuj");
        reset_button.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        reset_button.setBounds(225, 200, 100, 25);
        reset_button.addActionListener(this);

        register_button = new JButton("Zarejestruj");
        register_button.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        register_button.setBounds(175, 230, 100, 25);
        register_button.addActionListener(this);

        // --- Okno ---
        this.setTitle("Serwis elektroniczny");
        this.setSize(420, 450);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setLayout(null);

        this.add(app_title_label);
        this.add(login_label);
        this.add(password_label);
        this.add(login_field);
        this.add(password_field);
        this.add(log_button);
        this.add(reset_button);
        this.add(register_button);
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == log_button) {
            if (is_registration_form_open) {
                login_page_params();
                log_button.setText("Zaloguj się");
            } else {
                String login = login_field.getText();
                String password = new String(password_field.getPassword());

                Connection_Class login_connection = new Connection_Class();
                boolean login_result = login_connection.login(login, password);
                boolean login_result_admin = login_connection.login_admin(login, password);
                boolean login_result_administrator = login_connection.login_administrator(login, password);

                if (login_result)
                {
                    login_result_label.setText("Zalogowano pomyślnie!");
                    login_result_label.setForeground(Color.green);
                    User_Window user_window = new User_Window(login);
                    this.dispose();
                }

                else if(login_result_admin)
                {
                    login_result_label.setText("Zalogowano pomyślnie!");
                    login_result_label.setForeground(Color.green);
                    Admin_Window admin_window = new Admin_Window(login);
                    this.dispose();
                }

                else if(login_result_administrator)
                {
                    login_result_label.setText("Zalogowano pomyślnie!");
                    login_result_label.setForeground(Color.green);
                    okno_administratora admin_window = new okno_administratora();
                    this.dispose();
                }

                else
                {
                    login_result_label.setText("Podano błędne dane!");
                    login_result_label.setForeground(Color.red);
                    login_field.setText("");
                    password_field.setText("");
                }
            }
            this.revalidate();
            this.repaint();
        }

        else if (e.getSource() == reset_button)
        {
            login_field.setText("");
            password_field.setText("");
            login_result_label.setText("");
            reg_result_label.setText("");
            if (name_field != null) name_field.setText("");
            if (surname_field != null) surname_field.setText("");
            this.revalidate();
        }

        else if (e.getSource() == register_button) {
            if (!is_registration_form_open) {
                register_page_params();
                log_button.setText("Powrót");
            } else {
                String name = name_field.getText();
                String surname = surname_field.getText();
                String login = login_field.getText();
                String password = new String(password_field.getPassword());

                Connection_Class register_connection = new Connection_Class();
                boolean reg_query_result = register_connection.registration(name, surname, login, password);

                reg_result_label.setVisible(true);
                if (reg_query_result) {
                    reg_result_label.setText("Zarejestrowano pomyślnie! Wróć do logowania.");
                    reg_result_label.setForeground(Color.green.darker());
                } else {
                    reg_result_label.setText("Nie udało się zarejestrować.");
                    reg_result_label.setForeground(Color.red);
                }
            }
            this.revalidate();
            this.repaint();
        }
    }

    void login_page_params() {
        login_label.setBounds(50, 100, 75, 25);
        password_label.setBounds(50, 150, 75, 25);
        login_field.setBounds(125, 100, 200, 25);
        password_field.setBounds(125, 150, 200, 25);
        reset_button.setBounds(225, 200, 100, 25);
        log_button.setBounds(125, 200, 100, 25);
        register_button.setBounds(175, 230, 100, 25);

        if (name_label != null) this.remove(name_label);
        if (name_field != null) this.remove(name_field);
        if (surname_label != null) this.remove(surname_label);
        if (surname_field != null) this.remove(surname_field);

        is_registration_form_open = false;
        reg_result_label.setVisible(false);
        login_field.setText("");
        password_field.setText("");
        this.revalidate();
        this.repaint();
    }

    void register_page_params() {
        int gap = 50;
        int button_gap = 30;

        name_label = new JLabel("Imię: ");
        name_label.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        name_label.setBounds(50, 100, 75, 25);

        name_field = new JTextField();
        name_field.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        name_field.setBounds(125, 100, 200, 25);

        surname_label = new JLabel("Nazwisko: ");
        surname_label.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        surname_label.setBounds(50, 150, 75, 25);

        surname_field = new JTextField();
        surname_field.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        surname_field.setBounds(125, 150, 200, 25);

        login_label.setBounds(50, 200, 75, 25);
        login_field.setBounds(125, 200, 200, 25);
        password_label.setBounds(50, 250, 75, 25);
        password_field.setBounds(125, 250, 200, 25);

        reset_button.setBounds(225, 300, 100, 25);
        log_button.setBounds(125, 300, 100, 25);
        register_button.setBounds(175, 330, 100, 25);

        this.add(name_label);
        this.add(name_field);
        this.add(surname_label);
        this.add(surname_field);

        is_registration_form_open = true;
        this.revalidate();
        this.repaint();
    }
}
