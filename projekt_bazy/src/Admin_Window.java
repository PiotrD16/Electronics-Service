import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.security.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Admin_Window extends JFrame implements ActionListener
{
    Connection_Class connection;
    // --- Dane administratorów ---
    String[] admin_data_array;
    String admin_id, admin_name, admin_job, admin_login;

    // --- Tabela ---
    private JTable order_history_table;
    private JScrollPane order_history_scroll;
    private String[] order_history_labels;

    private JLabel app_title, welcome_label, employee_label, zlecenia_label;

    // --- Dane z bazy MongoDB ---
    private String connection_mongodb ;
    private MongoOrderRepository read_data;
    List<MongoResults> data_from_db_for_employee_id;

    // --- Obsługiwanie zamówień ---
    private JLabel handle_orders, cost_label, datetime_label, status_label, id_label, send_button_status, date_format;
    private JTextField cost_field, datetime_field, id_field;
    private JRadioButton in_progress,completed, cancelled;
    private ButtonGroup status_group;
    private JButton send_button, cancel_button, logout_button;


    Admin_Window (String admin_login_db)
    {
        // --- Parametry okna ---
        this.setSize(1000, 800);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setLayout(null);
        this.setTitle("Panel administratora");

        // --- Połączenie z bazą danych ---
        connection = new Connection_Class();
        admin_data_array = new String[4];
        boolean get_admin_data_result = connection.get_admin_data(admin_login_db, admin_data_array);
        if(get_admin_data_result)
        {
            admin_id = admin_data_array[0];
            admin_name = admin_data_array[1];
            admin_login = admin_login_db;
            admin_job = admin_data_array[3];
        }
        else
        {
            admin_id = "0";
            admin_name = "admin";
            admin_login = "login";
            admin_job = "engineer";
        }

        // --- Elementy ekranu ---

        app_title = new JLabel("Serwis Elektroniczny");
        app_title.setFont(new Font("Calibre", Font.ITALIC, 40));
        app_title.setIcon(new ImageIcon("logo.png"));
        app_title.setBounds(270, 25, 600, 50);
        this.add(app_title);

        welcome_label = new JLabel("Witaj, " + admin_login + "!");
        welcome_label.setFont(new Font("Calibre", Font.PLAIN, 40));
        welcome_label.setBounds(320, 100, 600, 45);
        this.add(welcome_label);

        employee_label = new JLabel("Zalogowano jako: " + admin_name + " - " + admin_job + ".");
        employee_label.setFont(new Font("Calibre", Font.PLAIN, 25));
        employee_label.setBounds(50, 200, 800, 45);
        this.add(employee_label);

        zlecenia_label = new JLabel("Zlecenia:");
        zlecenia_label.setFont(new Font("Calibre", Font.PLAIN, 25));
        zlecenia_label.setBounds(100, 250, 800, 45);
        this.add(zlecenia_label);

        // --- Parametry do tabeli ---

        order_history_labels = new String[]{"Klient", "Sprzęt", "Koszt", "Data", "Status", "ID"};

        connection_mongodb = "mongodb://localhost:27017";
        read_data = new MongoOrderRepository(connection_mongodb, "electronic_service", "images");
        data_from_db_for_employee_id = read_data.get_mongo_data(Integer.parseInt(admin_id));

        load_order_table();

        // --- Obsługa zamówień ---
        handle_orders = new JLabel("Obsłuż zamówienie:");
        handle_orders.setFont(new Font("Calibre", Font.PLAIN, 22));
        handle_orders.setBounds(50, 620, 250, 30);
        this.add(handle_orders);

        id_label = new JLabel("ID:");
        id_label.setFont(new Font("Calibre", Font.PLAIN, 18));
        id_label.setBounds(50, 660, 100, 25);
        this.add(id_label);

        cost_label = new JLabel("Koszt:");
        cost_label.setFont(new Font("Calibre", Font.PLAIN, 18));
        cost_label.setBounds(170, 660, 100, 25);
        this.add(cost_label);

        datetime_label = new JLabel("Data:");
        datetime_label.setFont(new Font("Calibre", Font.PLAIN, 18));
        datetime_label.setBounds(320, 660, 100, 25);
        this.add(datetime_label);

        status_label = new JLabel("Status:");
        status_label.setFont(new Font("Calibre", Font.PLAIN, 18));
        status_label.setBounds(470, 660, 100, 25);
        this.add(status_label);

        id_field = new JTextField();
        id_field.setFont(new Font("Calibre", Font.PLAIN, 16));
        id_field.setBounds(50, 690, 100, 30);
        this.add(id_field);

        cost_field = new JTextField();
        cost_field.setFont(new Font("Calibre", Font.PLAIN, 16));
        cost_field.setBounds(170, 690, 100, 30);
        this.add(cost_field);

        datetime_field = new JTextField();
        datetime_field.setFont(new Font("Calibre", Font.PLAIN, 16));
        datetime_field.setBounds(320, 690, 100, 30);
        this.add(datetime_field);

        in_progress = new JRadioButton("W trakcie");
        completed = new JRadioButton("Zakończone");
        cancelled = new JRadioButton("Anulowane");

        in_progress.setFont(new Font("Calibre", Font.PLAIN, 16));
        completed.setFont(new Font("Calibre", Font.PLAIN, 16));
        cancelled.setFont(new Font("Calibre", Font.PLAIN, 16));

        in_progress.setBounds(470, 690, 120, 25);
        completed.setBounds(470, 715, 120, 25);
        cancelled.setBounds(470, 740, 120, 25);

        status_group = new ButtonGroup();
        status_group.add(in_progress);
        status_group.add(completed);
        status_group.add(cancelled);

        date_format = new JLabel("yyyy-MM-dd");
        date_format.setFont(new Font("Calibre", Font.PLAIN, 16));
        date_format.setBounds(320, 715, 200, 30);
        this.add(date_format);

        this.add(in_progress);
        this.add(completed);
        this.add(cancelled);

        // --- Wysyłanie zrealizowanych zamowien ---

        send_button = new JButton("Wyślij");
        send_button.setFont(new Font("Calibre", Font.PLAIN, 16));
        send_button.setBounds(600, 660, 150, 30);
        send_button.addActionListener(this);
        this.add(send_button);

        cancel_button = new JButton("Anuluj");
        cancel_button.setFont(new Font("Calibre", Font.PLAIN, 16));
        cancel_button.setBounds(600, 690, 150, 30);
        cancel_button.addActionListener(this);
        this.add(cancel_button);

        send_button_status = new JLabel("");
        send_button_status.setFont(new Font("Calibre", Font.PLAIN, 16));
        send_button_status.setBounds(800, 660, 150, 30);
        this.add(send_button_status);

        logout_button = new JButton("Wyloguj");
        logout_button.setFont(new Font("Calibre", Font.PLAIN, 16));
        logout_button.setBounds(700, 200, 150, 45);
        logout_button.addActionListener(this);
        this.add(logout_button);

        this.setVisible(true);
    }

    private void load_order_table()
    {
        if (order_history_scroll != null) {
            this.remove(order_history_scroll);
        }

        List<String[]> all_orders = connection.get_order_history_admin(admin_id);
        List<String[]> order_history = new ArrayList<>();

        for (String[] order : all_orders) {
            String status = order[4];
            if (!status.trim().equalsIgnoreCase("Ukończone")) {
                order_history.add(order);
            }
        }

        String[] columnNames = {"Klient", "Sprzęt", "Koszt", "Data", "Status", "ID", "Opis", "Zdjęcie"};
        Object[][] data = new Object[order_history.size()][columnNames.length];

        for (int i = 0; i < order_history.size(); i++) {
            String[] row = order_history.get(i);
            System.arraycopy(row, 0, data[i], 0, row.length);

            int repairId = Integer.parseInt(row[5]); // zakładamy, że ID naprawy to ostatnia kolumna
            MongoResults match = null;
            for (MongoResults m : data_from_db_for_employee_id) {
                if (m.getRepairId() == repairId) {
                    match = m;
                    break;
                }
            }

            if (match != null) {
                JTextArea descArea = new JTextArea(match.getDescription());
                descArea.setLineWrap(true);
                descArea.setWrapStyleWord(true);
                descArea.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(descArea);
                scrollPane.setPreferredSize(new Dimension(200, 50));
                data[i][6] = scrollPane;

                try {
                    BufferedImage img = ImageIO.read(new ByteArrayInputStream(match.getImageBytes()));
                    Image scaledImg = img.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                    data[i][7] = new ImageIcon(scaledImg);
                } catch (Exception e) {
                    data[i][7] = null;
                }
            } else {
                data[i][6] = new JScrollPane(new JTextArea("Brak opisu"));
                data[i][7] = null;
            }
        }

        // Tworzymy tabelę z własnym modelem
        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 7) return ImageIcon.class;
                return Object.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(70);
        table.getTableHeader().setFont(new Font("Calibre", Font.BOLD, 16));
        table.setFont(new Font("Calibre", Font.PLAIN, 14));

        // Ustaw renderer dla kolumny z opisem (scrollowany tekst)
        table.getColumnModel().getColumn(6).setCellRenderer((tbl, value, isSelected, hasFocus, row, col) -> (JScrollPane) value);

        order_history_scroll = new JScrollPane(table);
        order_history_scroll.setBounds(50, 300, 900, 300);
        this.add(order_history_scroll);

        // --- Powiększenie obrazu ---

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());

                // Sprawdź czy kliknięto kolumnę "Zdjęcie"
                if (col == 7) {
                    MongoResults match = null;
                    try {
                        int repairId = Integer.parseInt(table.getValueAt(row, 5).toString());
                        for (MongoResults m : data_from_db_for_employee_id) {
                            if (m.getRepairId() == repairId) {
                                match = m;
                                break;
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    if (match != null && match.getImageBytes() != null) {
                        try {
                            BufferedImage img = ImageIO.read(new ByteArrayInputStream(match.getImageBytes()));

                            // Nowe okno z pełnym obrazem
                            JFrame imageFrame = new JFrame("Podgląd obrazu - naprawa ID " + match.getRepairId());
                            imageFrame.setSize(600, 600);
                            imageFrame.setLocationRelativeTo(null);
                            imageFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                            JLabel fullImageLabel = new JLabel(new ImageIcon(img));
                            JScrollPane scrollPane = new JScrollPane(fullImageLabel);
                            imageFrame.add(scrollPane);

                            imageFrame.setVisible(true);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });

        this.revalidate();
        this.repaint();
    }


    @Override
    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource() == send_button)
        {
            int repair_id = Integer.parseInt(id_field.getText());
            double cost = Double.parseDouble(cost_field.getText());

            String input = datetime_field.getText();

            LocalDate localDate = LocalDate.parse(input);
            java.sql.Date sqlDate = java.sql.Date.valueOf(localDate);

            String status = "";
            ButtonModel selectedModel = status_group.getSelection();

            if (selectedModel != null)
            {
                if (selectedModel == in_progress.getModel())
                {
                    status = "W trakcie";
                }
                else if (selectedModel == completed.getModel())
                {
                    status = "Ukończone";
                }
                else if (selectedModel == cancelled.getModel()) {

                    status = "Anulowane";
                }
            }
            else
            {
                status = "W trakcie";
            }

            boolean result = connection.update_repair(repair_id,sqlDate,cost,status);
            if(result)
            {
                send_button_status.setText("Sukces!");
                send_button_status.setForeground(Color.GREEN.darker());
                load_order_table();
                this.revalidate();
                this.repaint();
            }
            else
            {
                send_button_status.setText("Wystąpił błąd!");
                send_button_status.setForeground(Color.RED);
                this.revalidate();
                this.repaint();
            }

        }

        else if(e.getSource() == cancel_button)
        {
            id_field.setText("");
            cost_field.setText("");
            datetime_field.setText("");
            in_progress.setSelected(false);
            completed.setSelected(false);
            cancelled.setSelected(false);
            this.revalidate();
            this.repaint();
        }

        else if(e.getSource() == logout_button)
        {
            this.dispose();
            new Window();
        }
    }
}
