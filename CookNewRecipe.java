import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;


public class CookNewRecipe extends JFrame {
    private JTextField[] ingredientFields;
    private JLabel titleField;
    private JButton backButton;
    private JButton confirmButton;
    private Font bigFont;

    public CookNewRecipe() {
        setTitle("Cook New Recipe");
        setSize(1600, 900);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        bigFont = new Font("Arial", Font.PLAIN, 36);
        titleField = new JLabel("Enter Ingredients to Cook a New Recipe", JLabel.CENTER);
        titleField.setBounds(150, 50, 1000, 75);
        titleField.setFont(bigFont);
        add(titleField);

        ingredientFields = new JTextField[8];
        for (int i = 0; i < 8; i++) {
            ingredientFields[i] = new JTextField();
            ingredientFields[i].setBounds(150 + (i % 4) * 300, 150 + (i / 4) * 100, 250, 75);
            add(ingredientFields[i]);
        }

        backButton = new JButton("Exit");
        backButton.setBounds(50, 800, 200, 50);
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new MainMenu().setVisible(true);
                dispose();
            }
        });

        confirmButton = new JButton("Confirm Ingredients");
        confirmButton.setBounds(650, 800, 300, 50);
        confirmButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                generateRecipe();
            }
        });

        add(backButton);
        add(confirmButton);
    }

    private void generateRecipe() {
        // Get the ingredients
        String[] ingredients = new String[8];
        for (int i = 0; i < this.ingredientFields.length; i++) {
            ingredients[i] = this.ingredientFields[i].getText();
        }

        // Create the loading screen
        JFrame parentFrame = this;
        LoadingScreen loadingScreen = new LoadingScreen(parentFrame);

        // Use SwingWorker to run the Python script in a background thread
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                try {
                    // Show the loading screen
                    SwingUtilities.invokeLater(() -> loadingScreen.setVisible(true));

                    // Define script path and error directory
                    String scriptPath = "insert_directory_here";
                    String errorDirectory = "insert_directory_here";

                    // Run the Python script
                    PythonScripts.runPythonScript(Arrays.toString(ingredients), scriptPath);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    // Hide the loading screen
                    SwingUtilities.invokeLater(() -> loadingScreen.setVisible(false));
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    // Determine if the recipe generation succeeded or failed
                    String errorFile = FileUtils.findLatestFileInDirectory("insert_directory_here");
                    // Add a sample file of a failed generation
                    if (errorFile.equals("insert_directory_here")) {
                        JOptionPane.showMessageDialog(null, "The Assistant Failed to Generate a Recipe, Please try Different Ingredients");
                        new CookNewRecipe().setVisible(true);
                    } else {
                        new ReviewRecipe().setVisible(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    // Dispose of the parent frame when done
                    parentFrame.dispose();
                }
            }
        };

        // Start the background task
        worker.execute();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CookNewRecipe().setVisible(true));
    }
}


class LoadingScreen extends JDialog {

    public LoadingScreen(JFrame parent) {
        super(parent, "Loading", true);

        JLabel loadingLabel = new JLabel("Retrieving Recipe, please wait...", JLabel.CENTER);
        loadingLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        add(loadingLabel, BorderLayout.CENTER);

        setSize(300, 150);
        setLocationRelativeTo(parent);  // Center the dialog relative to the parent
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);  // Prevent closing the dialog
    }
}


