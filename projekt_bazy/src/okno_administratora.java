import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class okno_administratora extends JFrame implements ActionListener {
    Connection_Class connection;
    private JLabel app_title, welcome_label, logs_label, log_id_label, result_label;
    private JTable logsTable;
    private DefaultTableModel tableModel;
    private JButton logout_button, remove_order_button;
    private JTextField log_id_field;

    okno_administratora() {
        connection = new Connection_Class();

        app_title = new JLabel("Serwis Elektroniczny");
        app_title.setFont(new Font("Calibre", Font.ITALIC, 40));
        app_title.setIcon(new ImageIcon("logo.png"));
        app_title.setBounds(270, 25, 600, 50);
        this.add(app_title);

        welcome_label = new JLabel("Witaj!");
        welcome_label.setFont(new Font("Calibre", Font.PLAIN, 40));
        welcome_label.setBounds(470, 100, 600, 45);
        this.add(welcome_label);

        logs_label = new JLabel("Logi:");
        logs_label.setFont(new Font("Calibre", Font.PLAIN, 40));
        logs_label.setBounds(100, 200, 200, 30);
        this.add(logs_label);

        String[] columnNames = {"log_id", "repair_id", "user_id", "user_name", "employee_id", "log_time"};
        tableModel = new DefaultTableModel(columnNames, 0);

        logsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(logsTable);
        scrollPane.setBounds(50, 300, 900, 200);
        this.add(scrollPane);
        List<String[]> data = connection.get_repair_logs();
        for (String[] row : data) {
            tableModel.addRow(row);
        }

        remove_order_button = new JButton("Usuń zamówienie");
        remove_order_button.setFont(new Font("Calibre", Font.PLAIN, 16));
        remove_order_button.setBounds(20, 650, 200, 45);
        remove_order_button.addActionListener(this);
        this.add(remove_order_button);

        log_id_label = new JLabel("Log ID:");
        log_id_label.setFont(new Font("Calibre", Font.PLAIN, 16));
        log_id_label.setBounds(240, 650, 60, 30);
        this.add(log_id_label);

        log_id_field = new JTextField("");
        log_id_field.setFont(new Font("Calibre", Font.PLAIN, 16));
        log_id_field.setBounds(305, 650, 100, 30);
        this.add(log_id_field);

        result_label = new JLabel("");
        result_label.setFont(new Font("Calibre", Font.PLAIN, 16));
        result_label.setBounds(305, 690, 200, 30);
        this.add(result_label);

        logout_button = new JButton("Wyloguj");
        logout_button.setFont(new Font("Calibre", Font.PLAIN, 16));
        logout_button.setBounds(800, 650, 150, 45);
        logout_button.addActionListener(this);
        this.add(logout_button);

        this.setSize(1000, 800);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setLayout(null);
        this.setTitle("Panel administratora");
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == logout_button) {
            this.dispose();
            new Window();
        } else if(e.getSource() == remove_order_button) {
            int log_id = Integer.parseInt(log_id_field.getText());
            boolean query_result = connection.remove_log_record(log_id);

            if(query_result) {
                result_label.setText("Sukces!");
                result_label.setForeground(Color.GREEN.darker());

                // Wyczyść tabelę i załaduj dane ponownie
                tableModel.setRowCount(0);
                List<String[]> data = connection.get_repair_logs();
                for (String[] row : data) {
                    tableModel.addRow(row);
                }

                this.revalidate();
                this.repaint();
            } else {
                result_label.setText("Wystąpił błąd!");
                result_label.setForeground(Color.RED);

                this.revalidate();
                this.repaint();
            }
        }
    }
}
