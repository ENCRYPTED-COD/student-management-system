import java.awt.*;
import java.io.*;
import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class SMS {

    // ================== CREDENTIALS ==================
    static String USERNAME = "admin";
    static String PASSWORD = "admin";

    static void loadCredentials() {
        try {
            File f = new File("credentials.txt");
            if (!f.exists()) {
                FileWriter fw = new FileWriter(f);
                fw.write("admin\nadmin");
                fw.close();
            }
            BufferedReader br = new BufferedReader(new FileReader(f));
            USERNAME = br.readLine();
            PASSWORD = br.readLine();
            br.close();
        } catch (Exception ignored) {}
    }

    static void saveCredentials(String u, String p) {
        try (FileWriter fw = new FileWriter("credentials.txt")) {
            fw.write(u + "\n" + p);
            USERNAME = u;
            PASSWORD = p;
        } catch (Exception ignored) {}
    }

    // ================== SOUND ==================
    static void playSound() {
        try {
            File f = new File("pop.wav");
            if (!f.exists()) {
                Toolkit.getDefaultToolkit().beep();
                return;
            }
            AudioInputStream a = AudioSystem.getAudioInputStream(f);
            Clip c = AudioSystem.getClip();
            c.open(a);
            c.start();
        } catch (Exception e) {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    // ================== POPUP ==================
    static void popup(JFrame frame, String msg) {
        playSound();
        JLabel pop = new JLabel("🎉 " + msg, SwingConstants.CENTER);
        pop.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pop.setOpaque(true);
        pop.setBackground(new Color(76, 175, 80));
        pop.setForeground(Color.WHITE);
        pop.setBounds(250, -50, 400, 45);

        JLayeredPane lp = frame.getLayeredPane();
        lp.add(pop, JLayeredPane.POPUP_LAYER);

        Timer slide = new Timer(10, null);
        slide.addActionListener(e -> {
            if (pop.getY() < 70) {
                pop.setLocation(pop.getX(), pop.getY() + 5);
            } else {
                slide.stop();
                new Timer(1200, ev -> {
                    lp.remove(pop);
                    lp.repaint();
                }).start();
            }
        });
        slide.start();
    }

    // ================== LOGIN ==================
    static void loginScreen() {
        loadCredentials();

        JFrame f = new JFrame("Login");
        f.setSize(360, 230);
        f.setLayout(null);
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel t = new JLabel("🔐 Login", SwingConstants.CENTER);
        t.setFont(new Font("Segoe UI", Font.BOLD, 20));
        t.setBounds(0, 20, 360, 30);
        f.add(t);

        JTextField user = new JTextField();
        JPasswordField pass = new JPasswordField();

        user.setBounds(90, 70, 180, 28);
        pass.setBounds(90, 110, 180, 28);

        JButton btn = new JButton("Login");
        btn.setBounds(130, 155, 100, 30);

        f.add(user);
        f.add(pass);
        f.add(btn);

        btn.addActionListener(e -> {
            if (user.getText().equals(USERNAME) &&
                new String(pass.getPassword()).equals(PASSWORD)) {
                f.dispose();
                smsApp();
            } else {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(f, "Invalid credentials");
            }
        });

        f.setVisible(true);
    }

    // ================== CHANGE CREDENTIALS ==================
    static void changeCredentials(JFrame parent) {
        JDialog d = new JDialog(parent, "Change Login", true);
        d.setSize(360, 270);
        d.setLayout(null);
        d.setLocationRelativeTo(parent);

        JTextField oldU = new JTextField();
        JPasswordField oldP = new JPasswordField();
        JTextField newU = new JTextField();
        JPasswordField newP = new JPasswordField();

        JLabel[] labels = {
            new JLabel("Old Username:"),
            new JLabel("Old Password:"),
            new JLabel("New Username:"),
            new JLabel("New Password:")
        };

        int y = 20;
        for (JLabel l : labels) {
            l.setBounds(20, y, 120, 25);
            d.add(l);
            y += 35;
        }

        oldU.setBounds(150, 20, 170, 25);
        oldP.setBounds(150, 55, 170, 25);
        newU.setBounds(150, 90, 170, 25);
        newP.setBounds(150, 125, 170, 25);

        JButton save = new JButton("Update");
        save.setBounds(120, 180, 120, 30);

        d.add(oldU); d.add(oldP);
        d.add(newU); d.add(newP);
        d.add(save);

        save.addActionListener(e -> {
            if (oldU.getText().equals(USERNAME) &&
                new String(oldP.getPassword()).equals(PASSWORD)) {
                saveCredentials(newU.getText(), new String(newP.getPassword()));
                popup(parent, "Login Updated");
                d.dispose();
            } else {
                JOptionPane.showMessageDialog(d, "Old credentials incorrect");
            }
        });

        d.setVisible(true);
    }

    // ================== MAIN SMS APP ==================
    static void smsApp() {

        JFrame frame = new JFrame("Student Management System");
        frame.setSize(950, 560);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container c = frame.getContentPane();
        c.setBackground(new Color(245, 247, 250));

        JLabel title = new JLabel("🎓 Student Management System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setBounds(260, 20, 500, 35);
        frame.add(title);

        JTextField name = new JTextField();
        JTextField roll = new JTextField();
        JTextField course = new JTextField();

        name.setBounds(140, 90, 200, 28);
        roll.setBounds(140, 130, 200, 28);
        course.setBounds(140, 170, 200, 28);

        frame.add(name); frame.add(roll); frame.add(course);

        name.addActionListener(e -> roll.requestFocus());
        roll.addActionListener(e -> course.requestFocus());

        JButton add = new JButton("Add");
        JButton edit = new JButton("Edit");
        JButton del = new JButton("Delete");
        JButton save = new JButton("Save CSV");
        JButton change = new JButton("Change Login");

        JButton[] btns = {add, edit, del, save, change};
        int y = 220;
        for (JButton b : btns) {
            b.setBounds(40, y, 300, 32);
            frame.add(b);
            y += 40;
        }

        String[] cols = {"Name", "Roll", "Course"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        table.setRowHeight(24);

        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(380, 90, 520, 360);
        frame.add(sp);

        add.addActionListener(e -> {
            model.addRow(new Object[]{name.getText(), roll.getText(), course.getText()});
            popup(frame, "Student Added");
            name.setText(""); roll.setText(""); course.setText("");
            name.requestFocus();
        });

        edit.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r != -1) {
                model.setValueAt(name.getText(), r, 0);
                model.setValueAt(roll.getText(), r, 1);
                model.setValueAt(course.getText(), r, 2);
                popup(frame, "Student Updated");
            }
        });

        del.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r != -1) {
                model.removeRow(r);
                popup(frame, "Student Deleted");
            }
        });

        save.addActionListener(e -> {
            try (FileWriter fw = new FileWriter("students.csv")) {
                for (int i = 0; i < model.getRowCount(); i++) {
                    fw.write(model.getValueAt(i, 0) + "," +
                             model.getValueAt(i, 1) + "," +
                             model.getValueAt(i, 2) + "\n");
                }
                popup(frame, "Saved to File");
            } catch (Exception ignored) {}
        });

        change.addActionListener(e -> changeCredentials(frame));

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        loginScreen();
    }
}
