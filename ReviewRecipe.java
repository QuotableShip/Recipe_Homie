import org.apache.http.io.SessionOutputBuffer;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReviewRecipe extends JFrame {
    private JLabel titleField;
    private JTextArea recipeTextArea;
    private JTextField enterChanges;
    private JButton saveButton;
    private JButton backButton;
    private JButton cookedButton;
    private JButton updateRecipeButton;
    private Font bigFont;
    private Font arialBigFont;

    public ReviewRecipe() {
        setTitle("Review Recipe");
        setSize(1600, 900);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        bigFont = new Font("Times New Roman", Font.BOLD, 20);

        arialBigFont = new Font("Arial", Font.PLAIN, 36);
        titleField = new JLabel("Enter Changes to Update Recipe Here:");
        titleField.setBounds(50, 600, 1000, 75);
        titleField.setFont(bigFont);
        add(titleField);

        enterChanges = new JTextField();
        enterChanges.setBounds(50, 650, 1500, 75);
        enterChanges.setEditable(true);
        enterChanges.setFont(bigFont);

        updateRecipeButton = new JButton("Update Recipe");
        updateRecipeButton.setBounds(700, 800, 200, 50);
        updateRecipeButton.addActionListener(e -> {
            String LLMPrompt = enterChanges.getText();
            String secondaryScriptPath = "insert_directory_here";

            if (!LLMPrompt.isEmpty()) {
                runBackgroundTask(() -> {
                    PythonScripts.runPythonScript(LLMPrompt, secondaryScriptPath);
                    SwingUtilities.invokeLater(() -> new ReviewRecipe().setVisible(true));
                    dispose();
                });
            } else {
                JOptionPane.showMessageDialog(this, "Please enter any changes into the designated text field");
            }
        });

        cookedButton = new JButton("Save and Mark as Cooked");
        cookedButton.setBounds(1100, 800, 200, 50);
        cookedButton.addActionListener(e -> {
            runBackgroundTask(() -> {
                saveRecipeToFile(recipeTextArea.getText(), CookingStatus.COOKED);
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, "Recipe Saved and Marked as Cooked"));
            });
        });

        saveButton = new JButton("Save for Later");
        saveButton.setBounds(1350, 800, 200, 50);
        saveButton.addActionListener(e -> {
            runBackgroundTask(() -> {
                saveRecipeToFile(recipeTextArea.getText(), CookingStatus.UNCOOKED);
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Recipe Saved");
                    new MainMenu().setVisible(true);
                    dispose();
                });
            });
        });

        backButton = new JButton("Return");
        backButton.setBounds(50, 800, 200, 50);
        backButton.addActionListener(e -> {
            new CookNewRecipe().setVisible(true);
            dispose();
        });

        add(enterChanges);
        add(updateRecipeButton);
        add(cookedButton);
        add(saveButton);
        add(backButton);

        runBackgroundTask(() -> {
            String recipeFilePath = FileUtils.findLatestFileInDirectory("insert_directory_here");
            String recipeContent = FileUtils.readRecipeFromFile(recipeFilePath);

            if (recipeContent.equals("generation_error")) {
                SwingUtilities.invokeLater(() -> {
                    new CookNewRecipe().setVisible(true);
                    dispose();
                });
            } else {
                SwingUtilities.invokeLater(() -> {
                    recipeTextArea = new JTextArea(recipeContent);
                    recipeTextArea.setBounds(50, 50, 1500, 550);
                    recipeTextArea.setLineWrap(true);
                    recipeTextArea.setWrapStyleWord(true);
                    recipeTextArea.setEditable(false);
                    add(recipeTextArea);
                    revalidate();
                    repaint();
                });
            }
        });
    }

    private void runBackgroundTask(Runnable task) {
        JFrame parentFrame = this;
        LoadingScreen loadingScreen = new LoadingScreen(parentFrame);

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                SwingUtilities.invokeLater(() -> loadingScreen.setVisible(true));
                task.run();
                return null;
            }

            @Override
            protected void done() {
                SwingUtilities.invokeLater(() -> loadingScreen.setVisible(false));
            }
        };

        worker.execute();
    }

    enum CookingStatus {UNCOOKED, COOKED}

    private void saveRecipeToFile(String recipeContent, CookingStatus status) {
        String title = extractTitle(recipeContent);
        if (title == null || title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Recipe title not found. Cannot save the recipe.");
            return;
        }

        String updatedContent = recipeContent;
        if (status == CookingStatus.UNCOOKED) {
            updatedContent = recipeContent.replace("State: ", "State: Uncooked");
        } else if (status == CookingStatus.COOKED) {
            updatedContent = recipeContent.replace("State:", "State: Cooked");
            String currentDate = new SimpleDateFormat("dd.MM.yyyy").format(new Date());
            updatedContent = updatedContent.replace("Date Cooked:", "Date Cooked: " + currentDate);
        }

        saveContentToFile(title, updatedContent);
    }

    private void saveContentToFile(String title, String updatedContent) {
        String outputDirectory = "insert_directory_here";
        String fileName = title.replaceAll("[^a-zA-Z0-9\\s]", "").replaceAll("\\s+", "_") + ".txt";
        String outputPath = Paths.get(outputDirectory, fileName).toString();
        new File(outputDirectory).mkdirs();

        try (FileWriter writer = new FileWriter(outputPath)) {
            writer.write(updatedContent);
            System.out.println("Recipe saved to " + outputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String extractTitle(String recipeContent) {
        for (String line : recipeContent.split("\n")) {
            if (line.startsWith("Title:")) {
                return line.substring(7).trim();
            }
        }
        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ReviewRecipe().setVisible(true));
    }
}



