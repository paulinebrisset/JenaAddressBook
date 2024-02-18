package abook;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SparqlGUI {
    private static final AddressBook addressBook = new AddressBook();
    // Colors
    private static final Color PINK_COLOR = Color.decode("#FCDEDC");
    private static final Color LABEL_FOREGROUND_COLOR = Color.decode("#FFFFF8");
    private static final Color BUTTONS_COLOR = Color.decode("#FCFADF");

    // Fields
    private JTextArea queryTextArea;
    private JTextArea resultTextArea;
    private JButton executeButton;
    private JLabel titleLabel;

    public SparqlGUI() {
        JFrame frame = new JFrame("Requête SPARQL");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(PINK_COLOR);
        frame.add(panel);

        // Center the window on the screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - frame.getWidth()) / 2;
        int y = (screenSize.height - frame.getHeight()) / 2;
        frame.setLocation(x, y);
        placeComponents(panel);
        frame.setVisible(true);
    }

    private void placeComponents(JPanel panel) {
        // Title
        titleLabel = new JLabel("Essayer une requête Sparql");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.DARK_GRAY);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Query Input Field
        JPanel queryPanel = new JPanel(new FlowLayout());
        queryPanel.setLayout(new BoxLayout(queryPanel, BoxLayout.Y_AXIS));
        queryPanel.setBorder(BorderFactory.createEmptyBorder(30, 10, 20, 10));
        queryPanel.setBackground(PINK_COLOR);

        JLabel queryLabel = new JLabel("Essayez de faire une requête SPARQL sur Dbpedia:");
        queryLabel.setForeground(Color.DARK_GRAY);
        queryPanel.add(queryLabel);

        queryTextArea = new JTextArea(3, 40);
        JScrollPane queryScrollPane = new JScrollPane(queryTextArea);
        queryPanel.add(queryScrollPane);

        // Execute Button
        executeButton = new JButton("Exécuter la requête");
        executeButton.addActionListener(e -> executeQuery());
        executeButton.setBackground(BUTTONS_COLOR);
        executeButton.setForeground(Color.BLACK);
        queryPanel.add(executeButton);
        panel.add(queryPanel, BorderLayout.CENTER);

        // Result Display Field
        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        resultPanel.setBackground(PINK_COLOR);

        JLabel resultLabel = new JLabel("Résultat de la requête:");
        resultLabel.setForeground(Color.DARK_GRAY);
        resultPanel.add(resultLabel, BorderLayout.NORTH);

        resultTextArea = new JTextArea(10, 40);
        resultTextArea.setEditable(false);
        JScrollPane resultScrollPane = new JScrollPane(resultTextArea);
        resultPanel.add(resultScrollPane, BorderLayout.CENTER);

        panel.add(resultPanel, BorderLayout.SOUTH);
    }

    private void executeQuery() {
        String query = queryTextArea.getText();
        String result = addressBook.querySPARQLEndpoint(query);
        resultTextArea.setText(result);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SparqlGUI::new);
    }
}
