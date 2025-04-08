import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class viewRecipe extends JFrame {
    private JTextArea recipeTextArea;
    private JButton backButton;
    private JButton cookedButton;
    private Font bigFont;


    public viewRecipe(String FilePath) {
        setTitle("My Cookbook");
        setSize(1600,900);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        bigFont = new Font("Times New Roman", Font.BOLD, 20);

        recipeTextArea = new JTextArea();
        recipeTextArea.setBounds(50, 50, 1500, 550);
        recipeTextArea.setLineWrap(true);
        recipeTextArea.setWrapStyleWord(true);
        recipeTextArea.setEditable(false);

        String recipeContent = FileUtils.readRecipeFromFile(FilePath);
        recipeTextArea.setText(recipeContent);

        cookedButton = new JButton("Save and Mark as Cooked");
        cookedButton.setBounds(1100, 800, 200, 50);
        cookedButton.addActionListener(e -> {
            saveCookedRecipeToFile(recipeContent);
            JOptionPane.showMessageDialog(null, "Recipe Saved and Marked as Cooked");
            new RecookRecipes().setVisible(true);
            dispose();
        });

        backButton = new JButton("Return");
        backButton.setBounds(50, 800, 200, 50);
        backButton.addActionListener(e -> {
            new RecookRecipes().setVisible(true);
            dispose();
        });

        add(recipeTextArea);
        add(backButton);
        add(cookedButton);
    }

    private void saveCookedRecipeToFile(String recipeContent) {

        // Extract the title from the recipe content
        String title = FileUtils.extractTitle(recipeContent);
        if (title == null || title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Recipe title not found. Cannot save the recipe.");
            return;
        }

        String updatedContent = recipeContent.replace("State:", "State: Cooked");
        String currentDate = new SimpleDateFormat("dd.MM.yyyy").format(new Date());
        updatedContent = updatedContent.replace("Date Cooked:", "Date Cooked: " + currentDate);

        String outputDirectory = "insert_directory_here";
        String filename = title.replaceAll("[^a-zA-Z0-9\\s]", "").replaceAll("\\s+", "_") + ".txt"; // Replace non-alphanumeric characters and spaces        String outputPath = Paths.get(outputDirectory, filename).toString();
        String outputPath = Paths.get(outputDirectory, filename).toString();
        // Ensure the output directory exists
        new File(outputDirectory).mkdirs();

        try (FileWriter writer = new FileWriter(outputPath)) {
            writer.write(updatedContent);
            System.out.println("Recipe saved to " + outputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
