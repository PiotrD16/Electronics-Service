import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class User_Window extends JFrame implements ActionListener
{
    private JLabel user_surname_label, user_name_label, user_login_label, user_greeting_label, title_label, simple_text;
    private JLabel old_password_label, new_password_label, new_password_confirm_label, change_result;
    private JButton logout_button, change_password_button, confirm_password_change_button, cancel_password_change_button, delete_account_button,make_order_button;
    private String user_ID, user_name, user_surname, user_login, user_password;
    private String[] user_data_array;
    private Connection_Class connection;
    private JPasswordField new_password_field, new_password_field_confirm, old_password_field;

    // --- Elementy po prawej
    private JButton add_new_order_button, send_order_button, cancel_order_button;
    private JLabel product_selection_label, order_result_label;
    private String[] products;
    private JComboBox<String> product_combo_box;

    private JTable order_history_table;
    private JScrollPane order_history_scroll;
    private String[] order_history_labels;

    private JButton choose_image_button;
    private JLabel image_preview_label, issue_description_label;
    private File selectedImageFile;
    private JTextArea issue_description_field;
    private JScrollPane issue_description_scroll;

    User_Window(String user_login_db)
    {
        user_login = new String(user_login_db);

        // --- Parametry dla okna ---
        this.setTitle("Panel użytkownika");
        this.setSize(1000, 800);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setLayout(null);

        // --- Połączenie z bazą danych
        connection = new Connection_Class();
        user_data_array = new String[5];
        boolean get_user_data_result = connection.get_user_data(user_login, user_data_array);
        System.out.println(get_user_data_result);
        if(get_user_data_result)
        {
            user_ID = user_data_array[0];
            user_name = user_data_array[1];
            user_surname = user_data_array[2];
            user_login = user_data_array[3];
            user_password = user_data_array[4];
        }
        else
        {
            JOptionPane.showMessageDialog(this, "Nie udało się pobrać danych użytkownika!");
            this.dispose();
            return;
        }

        // --- Parametry dla labeli ---

        title_label = new JLabel("Serwis Elektroniczny");
        title_label.setFont(new Font("Calibre", Font.ITALIC, 40));
        title_label.setIcon(new ImageIcon("logo.png"));
        title_label.setBounds(270, 25, 600, 50);
        this.add(title_label);

        user_greeting_label = new JLabel("Witaj, " + user_name + "!");
        user_greeting_label.setFont(new Font("Calibre", Font.PLAIN, 40));
        user_greeting_label.setBounds(400, 100, 600, 45);
        this.add(user_greeting_label);

        simple_text = new JLabel("Twoje dane: ");
        simple_text.setFont(new Font("Calibre", Font.PLAIN, 30));
        simple_text.setBounds(100, 200, 200, 35);
        this.add(simple_text);

        user_name_label = new JLabel("Imię: " + user_name);
        user_name_label.setFont(new Font("Calibre", Font.PLAIN, 20));
        user_name_label.setBounds(50, 250, 200, 35);
        this.add(user_name_label);

        user_surname_label = new JLabel("Nazwisko: " + user_surname);
        user_surname_label.setFont(new Font("Calibre", Font.PLAIN, 20));
        user_surname_label.setBounds(50, 300, 200, 35);
        this.add(user_surname_label);

        user_login_label = new JLabel("Login: " + user_login);
        user_login_label.setFont(new Font("Calibre", Font.PLAIN, 20));
        user_login_label.setBounds(50, 350, 200, 35);
        this.add(user_login_label);

        change_result = new JLabel("");
        change_result.setFont(new Font("Calibre", Font.PLAIN, 20));
        change_result.setBounds(320, 450, 400, 35);
        change_result.setVisible(false);
        this.add(change_result);

        // --- Przyciski ---

        change_password_button = new JButton("Zmień hasło");
        change_password_button.setFont(new Font("Calibre", Font.PLAIN, 20));
        change_password_button.setBounds(50, 450, 150, 35);
        change_password_button.addActionListener(this);
        this.add(change_password_button);

        logout_button = new JButton("Wyloguj się");
        logout_button.setFont(new Font("Calibre", Font.PLAIN, 20));
        logout_button.setBounds(50, 710, 150, 35);
        logout_button.addActionListener(this);
        this.add(logout_button);

        add_new_order_button = new JButton("Złóż zamówienie");
        add_new_order_button.setFont(new Font("Calibre", Font.PLAIN, 20));
        add_new_order_button.setBounds(570, 450, 200, 35);
        add_new_order_button.addActionListener(this);
        this.add(add_new_order_button);

        // --- Obsługa zamówień ---

        products = new String[]{"Laptop", "Smartfon", "Tablet", "Monitor", "Telewizor"};
        product_combo_box = new JComboBox<>(products);
        product_combo_box.setFont(new Font("Calibre", Font.PLAIN, 20));
        product_combo_box.setBounds(520, 500, 200, 35);
        product_combo_box.setVisible(false);

        product_selection_label = new JLabel("Produkt: ");
        product_selection_label.setFont(new Font("Calibre", Font.PLAIN, 20));
        product_selection_label.setBounds(420, 500, 200, 35);
        product_selection_label.setVisible(false);

        choose_image_button = new JButton("Dodaj zdjęcie");
        choose_image_button.setFont(new Font("Calibre", Font.PLAIN, 18));
        choose_image_button.setBounds(520, 550, 200, 35);
        choose_image_button.addActionListener(this);
        choose_image_button.setVisible(false);

        image_preview_label = new JLabel("Nie wybrano załącznika");
        image_preview_label.setFont(new Font("Calibre", Font.ITALIC, 18));
        image_preview_label.setBounds(740, 550, 300, 35);
        image_preview_label.setVisible(false);

        issue_description_field = new JTextArea("");
        issue_description_field.setFont(new Font("Calibre", Font.PLAIN, 15));
        issue_description_field.setLineWrap(true);
        issue_description_field.setWrapStyleWord(true);
        issue_description_field.setVisible(false);

        issue_description_scroll = new JScrollPane(issue_description_field);
        issue_description_scroll.setBounds(520, 600, 200, 150);
        issue_description_scroll.setVisible(false);

        issue_description_label = new JLabel("Dodaj opis ");
        issue_description_label.setFont(new Font("Calibre", Font.PLAIN, 20));
        issue_description_label.setBounds(420, 600, 200, 35);
        issue_description_label.setVisible(false);

        send_order_button = new JButton("Wyślij");
        send_order_button.setFont(new Font("Calibre", Font.PLAIN, 20));
        send_order_button.setBounds(740, 600, 200, 35);
        send_order_button.addActionListener(this);
        send_order_button.setVisible(false);

        order_result_label = new JLabel("");
        order_result_label.setFont(new Font("Calibre", Font.PLAIN, 20));
        order_result_label.setBounds(740, 700, 200, 35);
        order_result_label.setVisible(false);

        cancel_order_button = new JButton("Anuluj");
        cancel_order_button.setFont(new Font("Calibre", Font.PLAIN, 20));
        cancel_order_button.setBounds(740, 650, 200, 35);
        cancel_order_button.addActionListener(this);
        cancel_order_button.setVisible(false);

        this.add(product_combo_box);
        this.add(product_selection_label);
        this.add(choose_image_button);
        this.add(image_preview_label);
        this.add(issue_description_scroll);
        this.add(issue_description_label);
        this.add(send_order_button);
        this.add(cancel_order_button);
        this.add(order_result_label);

        // --- Parametry do tabeli historii zamówień ---
        order_history_labels = new String[]{"Imię i nazwisko", "Urządzenie", "Inżynier", "Koszt", "Data odbioru"};
        loadOrderHistoryTable();
        this.setVisible(true);
    }

    private void loadOrderHistoryTable() {
        if (order_history_scroll != null) {
            this.remove(order_history_scroll);
        }

        List<String[]> order_history = connection.get_order_history(user_ID);
        String[][] data = new String[order_history.size()][order_history_labels.length];

        for (int i = 0; i < order_history.size(); i++) {
            data[i] = order_history.get(i);
        }

        order_history_table = new JTable(data, order_history_labels);
        order_history_table.setFont(new Font("Calibre", Font.PLAIN, 16));
        order_history_table.setRowHeight(24);
        order_history_table.getTableHeader().setFont(new Font("Calibre", Font.BOLD, 16));
        order_history_scroll = new JScrollPane(order_history_table);
        order_history_scroll.setBounds(350, 180, 600, 250);
        this.add(order_history_scroll);

        this.revalidate();
        this.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource() == change_password_button)
        {
            old_password_field = new JPasswordField();
            old_password_field.setFont(new Font("Calibre", Font.PLAIN, 20));
            old_password_field.setBounds(170, 500, 200, 35);
            old_password_label = new JLabel("Stare hasło: ");
            old_password_label.setFont(new Font("Calibre", Font.PLAIN, 20));
            old_password_label.setBounds(30, 500, 200, 35);
            this.add(old_password_label);
            this.add(old_password_field);

            new_password_field = new JPasswordField();
            new_password_field.setFont(new Font("Calibre", Font.PLAIN, 20));
            new_password_field.setBounds(170, 550, 200, 35);
            new_password_label = new JLabel("Nowe hasło: ");
            new_password_label.setFont(new Font("Calibre", Font.PLAIN, 20));
            new_password_label.setBounds(30, 550, 200, 35);
            this.add(new_password_label);
            this.add(new_password_field);

            new_password_field_confirm = new JPasswordField ();
            new_password_field_confirm.setFont(new Font("Calibre", Font.PLAIN, 20));
            new_password_field_confirm.setBounds(170, 600, 200, 35);
            new_password_confirm_label = new JLabel("Powtórz hasło: ");
            new_password_confirm_label.setFont(new Font("Calibre", Font.PLAIN, 20));
            new_password_confirm_label.setBounds(30, 600, 200, 35);
            this.add(new_password_confirm_label);
            this.add(new_password_field_confirm);

            confirm_password_change_button = new JButton("Potwierdź");
            confirm_password_change_button.setFont(new Font("Calibre", Font.PLAIN, 20));
            confirm_password_change_button.setBounds(40, 650, 150, 35);
            confirm_password_change_button.addActionListener(this);
            this.add(confirm_password_change_button);

            cancel_password_change_button = new JButton("Anuluj");
            cancel_password_change_button.setFont(new Font("Calibre", Font.PLAIN, 20));
            cancel_password_change_button.setBounds(200, 650, 100, 35);
            cancel_password_change_button.addActionListener(this);
            this.add(cancel_password_change_button);

            this.revalidate();
            this.repaint();
        }

        else if(e.getSource() == confirm_password_change_button)
        {
            String old_password = new String(old_password_field.getPassword());
            String new_password = new String(new_password_field.getPassword());
            String new_password_confirm = new String(new_password_field_confirm.getPassword());

            if (new_password.trim().isEmpty() || new_password_confirm.trim().isEmpty())
            {
                change_result.setText("Żadne pole nie może być puste!");
                change_result.setForeground(Color.red);
                change_result.setVisible(true);
                this.revalidate();
                this.repaint();
                return;
            }

            if((old_password.equals(user_password) && new_password.equals(new_password_confirm)))
            {
                boolean change_password = connection.change_password(user_ID, new_password);
                if(change_password)
                {
                    user_password = new_password;
                    change_result.setText("Udało się zmienić hasło!");
                    change_result.setForeground(Color.green);
                    change_result.setVisible(true);
                    old_password_field.setText("");
                    new_password_field.setText("");
                    new_password_field_confirm.setText("");
                    this.remove(new_password_field);
                    this.remove(new_password_label);
                    this.remove(new_password_field_confirm);
                    this.remove(new_password_confirm_label);
                    this.remove(old_password_field);
                    this.remove(old_password_label);
                    this.remove(confirm_password_change_button);
                    this.remove(cancel_password_change_button);
                    this.revalidate();
                    this.repaint();
                }
                else
                {
                    change_result.setText("Wystąpił błąd!");
                    change_result.setForeground(Color.red);
                    change_result.setVisible(true);
                    new_password_field.setText("");
                    old_password_field.setText("");
                    new_password_field_confirm.setText("");
                    this.revalidate();
                    this.repaint();
                }
            }
            else
            {
                change_result.setText("Wystąpił błąd!");
                change_result.setForeground(Color.red);
                change_result.setVisible(true);
                this.revalidate();
                this.repaint();
            }
        }

        else if(e.getSource() == logout_button)
        {
            this.dispose();
            new Window();
        }

        else if(e.getSource() == cancel_password_change_button)
        {
            this.remove(new_password_field);
            this.remove(new_password_label);
            this.remove(new_password_field_confirm);
            this.remove(new_password_confirm_label);
            this.remove(old_password_field);
            this.remove(old_password_label);
            this.remove(confirm_password_change_button);
            this.remove(cancel_password_change_button);
            this.revalidate();
            this.repaint();
        }

        else if (e.getSource() == choose_image_button)
        {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Wybierz zdjęcie");
            int result = fileChooser.showOpenDialog(this);

            if (result == JFileChooser.APPROVE_OPTION)
            {
                selectedImageFile = fileChooser.getSelectedFile();
                image_preview_label.setText(selectedImageFile.getName());
                this.revalidate();
                this.repaint();
            }
        }

        else if(e.getSource() == add_new_order_button)
        {
            product_selection_label.setVisible(true);
            product_combo_box.setVisible(true);
            choose_image_button.setVisible(true);
            image_preview_label.setVisible(true);
            issue_description_scroll.setVisible(true);
            issue_description_label.setVisible(true);
            issue_description_field.setVisible(true);
            send_order_button.setVisible(true);
            cancel_order_button.setVisible(true);

            this.revalidate();
            this.repaint();
        }

        else if(e.getSource() == cancel_order_button)
        {
            product_selection_label.setVisible(false);
            product_combo_box.setVisible(false);
            choose_image_button.setVisible(false);
            image_preview_label.setVisible(false);
            issue_description_scroll.setVisible(false);
            issue_description_label.setVisible(false);
            issue_description_field.setVisible(false);
            send_order_button.setVisible(false);
            cancel_order_button.setVisible(false);

            this.revalidate();
            this.repaint();
        }

        else if(e.getSource() == send_order_button)
        {
            String product_name = product_combo_box.getSelectedItem().toString();
            String issue_description = issue_description_field.getText();
            String device_type = product_combo_box.getSelectedItem().toString();
            boolean order_result = connection.send_new_order_(user_ID,device_type, issue_description, selectedImageFile);

            if(order_result)
            {
                order_result_label.setText("Zlecenie wysłane!");
                order_result_label.setForeground(Color.green);
                order_result_label.setVisible(true);
                loadOrderHistoryTable();
                this.revalidate();
                this.repaint();
            }
            else
            {
                order_result_label.setText("Wystąpił błąd!");
                order_result_label.setForeground(Color.red);
                order_result_label.setVisible(true);
                this.revalidate();
                this.repaint();
            }
        }
    }
}
