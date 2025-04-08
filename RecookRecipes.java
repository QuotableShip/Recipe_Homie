import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RecookRecipes extends JFrame {
    private JPanel panel;
    private JButton nextPageButton;
    private JButton prevPageButton;
    private JButton exitButton;
    private List<File> recipeFiles;
    private int currentPage;
    private JLabel pageLabel;

    public RecookRecipes() {
        setTitle("Recook Recipes");
        setSize(1600, 900);
        setLayout(null); // Use null layout for pixel coordinates
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel = new JPanel();
        panel.setLayout(null);
        panel.setBounds(50, 50, 1500, 700);
        add(panel);

        exitButton = new JButton("Exit");
        exitButton.setBounds(50, 800, 100, 50);
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new MainMenu().setVisible(true);
                dispose();
            }
        });
        add(exitButton);

        nextPageButton = new JButton("Next Page");
        nextPageButton.setBounds(840, 800, 200, 50);
        nextPageButton.addActionListener(e -> {
            currentPage++;
            displayRecipes();
        });
        add(nextPageButton);

        prevPageButton = new JButton("Previous Page");
        prevPageButton.setBounds(525, 800, 200, 50);
        prevPageButton.addActionListener(e -> {
            currentPage--;
            displayRecipes();
        });
        add(prevPageButton);

        pageLabel = new JLabel();
        pageLabel.setBounds(750, 800, 100, 50);
        add(pageLabel);

        recipeFiles = loadRecipeFiles("insert_directory_here");
        Collections.sort(recipeFiles, Comparator.comparing(File::getName));
        currentPage = 0;

        displayRecipes();
    }

    private List<File> loadRecipeFiles(String directoryPath) {
        File directory = new File(directoryPath);
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".txt"));
        if (files == null) {
            return new ArrayList<>();
        }
        List<File> fileList = new ArrayList<>();
        Collections.addAll(fileList, files);
        return fileList;
    }

    private void displayRecipes() {
        panel.removeAll();
        int start = currentPage * 5;
        int end = Math.min(start + 5, recipeFiles.size());

        for (int i = start; i < end; i++) {
            File file = recipeFiles.get(i);
            String content = readRecipeFromFile(file);

            JLabel titleLabel = new JLabel(extractField(content, "Title").replace("Title:", ""));
            titleLabel.setBounds(10, (i - start) * 140, 300, 30);
            panel.add(titleLabel);

            JLabel caloriesLabel = new JLabel(extractField(content, "Estimated Calories"));
            caloriesLabel.setBounds(320, (i - start) * 140, 200, 30);
            panel.add(caloriesLabel);

            JLabel proteinLabel = new JLabel(extractField(content, "Estimated Proteins"));
            proteinLabel.setBounds(530, (i - start) * 140, 200, 30);
            panel.add(proteinLabel);

            JLabel dateCookedLabel = new JLabel(extractField(content, "Date Cooked"));
            dateCookedLabel.setBounds(740, (i - start) * 140, 200, 30);
            panel.add(dateCookedLabel);

            JButton viewButton = new JButton("View Recipe");
            viewButton.setBounds(950, (i - start) * 140, 200, 30);
            viewButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                String FilePath = file.getAbsolutePath().toString();
                new viewRecipe(FilePath).setVisible(true);
                dispose();
                }
            });
            panel.add(viewButton);
        }

        pageLabel.setText("Page " + (currentPage + 1) + " of " + ((recipeFiles.size() + 4) / 5));
        prevPageButton.setEnabled(currentPage > 0);
        nextPageButton.setEnabled(end < recipeFiles.size());
        panel.revalidate();
        panel.repaint();
    }

    private String readRecipeFromFile(File file) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    private String extractField(String content, String fieldName) {
        for (String line : content.split("\n")) {
            if (line.startsWith(fieldName)) {
                return line;
            }
        }
        return fieldName + ": N/A";
    }
}

