package abook;

import javax.swing.*;
import java.awt.*;

public class AddressBookGUI {
    // Colors
    private static final Color PINK_COLOR = Color.decode("#FCDEDC");
    private static final Color LABEL_FOREGROUND_COLOR = Color.decode("#FFFFF8");
    private static final Color BUTTONS_COLOR = Color.decode("#FCFADF");

    // Fields
    private JTextField uriField;
    private JTextField firstNameField;
    private JTextField familyNameField;
    private JTextField addressField;
    private JTextField countryField;
    private JTextField nicknameField;
    private JTextArea contactsTextArea;
    private static final AddressBook ab = new AddressBook();
    private JLabel newContactLabel;

    public AddressBookGUI() {
        JFrame frame = new JFrame("Mon carnet d'adresses");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 750);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setPreferredSize(new Dimension(400, 100));
        frame.add(panel);

        // Center the window on the screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - frame.getWidth()) / 2;
        int y = (screenSize.height - frame.getHeight()) / 2;
        frame.setLocation(x, y);
        placeComponents(panel);
        frame.setVisible(true);
    }

    // Get all my components placed on the interface
    private void placeComponents(JPanel panel) {
        panel.setBackground(PINK_COLOR);

        // Add second title
        newContactLabel = new JLabel("Mon carnet de contacts");
        newContactLabel.setFont(new Font("Arial", Font.BOLD, 18));
        newContactLabel.setForeground(Color.DARK_GRAY);
        Dimension size = newContactLabel.getPreferredSize();
        int y = 10;

        newContactLabel.setBounds(220, y, size.width, size.height + 10);
        panel.add(newContactLabel);

        // fields to create my new contacts
        int fieldWidth = 200;
        int labelX = 90;
        int fieldX = 300;

        createField(panel, "URI :", labelX, 50, fieldX, uriField = new JTextField(20), fieldWidth);
        createField(panel, "Prénom :", labelX, 80, fieldX, firstNameField = new JTextField(20), fieldWidth);
        createField(panel, "Nom de famille :", labelX, 110, fieldX, familyNameField = new JTextField(20), fieldWidth);
        createField(panel, "Surnom :", labelX, 140,
                fieldX, nicknameField = new JTextField(20), fieldWidth);
        createField(panel, "Adresse :", labelX, 170, fieldX, addressField = new JTextField(20), fieldWidth);
        createField(panel, "Pays :", labelX, 200, fieldX, countryField = new JTextField(20), fieldWidth);

        // Buttons
        JButton submitButton = new JButton("Créer");
        submitButton.setBounds(90, 250, 80, 25);
        submitButton.setBackground(BUTTONS_COLOR);
        panel.add(submitButton);

        JButton displayButton = new JButton("Montrer tous les contacts");
        displayButton.setBounds(190, 250, 270, 25);
        displayButton.setBackground(BUTTONS_COLOR);
        panel.add(displayButton);

        // Display contacts notebook
        contactsTextArea = new JTextArea();
        contactsTextArea.setEditable(false);
        contactsTextArea.setBackground(LABEL_FOREGROUND_COLOR);

        JScrollPane scrollPane = new JScrollPane(contactsTextArea);
        scrollPane.setBounds(10, 280, 560, 400); // Set the bounds for the scroll pane
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); // Show vertical scroll bar always

        panel.add(scrollPane);

        // Call AddressBook.java with error control
        displayButton.addActionListener(e -> {
            try {
                contactsTextArea.setText(ab.getModelAsString());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Erreur pour afficher les contacts : " + ex.getMessage());
            }
        });

        // Register new contacts through AddressBook
        submitButton.addActionListener(e -> {
            String uri = uriField.getText();
            String firstName = firstNameField.getText();
            String familyName = familyNameField.getText();
            String address = addressField.getText();
            String country = countryField.getText();
            String nickname = nicknameField.getText();

            if (uri.isEmpty() || firstName.isEmpty() || familyName.isEmpty() || address.isEmpty() || country.isEmpty()
                    || nickname.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Veuillez renseigner tous les champs svp.");
                return;
            }
            ab.newContact(uri, firstName, familyName, address, country, nickname);

            // Success message and fields cleaning
            JOptionPane.showMessageDialog(null, "Nouveau contact créé avec succès!");
            uriField.setText("");
            firstNameField.setText("");
            familyNameField.setText("");
            addressField.setText("");
            countryField.setText("");
            nicknameField.setText("");
        });
    }

    // Normalize labals
    private void createField(JPanel panel, String label, int labelX, int labelY, int fieldX, JTextField textField,
            int fieldWidth) {
        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setBounds(labelX, labelY, 190, 25);
        panel.add(fieldLabel);
        textField.setBounds(fieldX, labelY, fieldWidth, 25);
        textField.setBackground(LABEL_FOREGROUND_COLOR);

        panel.add(textField);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AddressBookGUI::new);
    }
}
