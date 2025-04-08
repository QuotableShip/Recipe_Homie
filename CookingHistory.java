import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class CookingHistory extends JFrame {
    private final JPanel panel;
    private final JButton nextPageButton;
    private final JButton prevPageButton;
    private JButton exitButton;
    private final List<File> recipeFiles;
    private int currentPage;
    private final JLabel pageLabel;
    private Calendar calendar;
    private final Date oldestDateCooked;


    public CookingHistory() {
        setTitle("Cooking History");
        setSize(1600, 900);
        setLayout(null);
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

        nextPageButton = new JButton("Next Week");
        nextPageButton.setBounds(880, 800, 200, 50);
        nextPageButton.addActionListener(e -> {
            calendar.add(Calendar.WEEK_OF_YEAR, 1);
            currentPage++;
            displayRecipes();
        });
        add(nextPageButton);

        prevPageButton = new JButton("Previous Week");
        prevPageButton.setBounds(525, 800, 200, 50);
        prevPageButton.addActionListener(e -> {
            calendar.add(Calendar.WEEK_OF_YEAR, -1);
            currentPage--;
            displayRecipes();
        });
        add(prevPageButton);

        pageLabel = new JLabel();
        pageLabel.setBounds(750, 800, 300, 50);
        add(pageLabel);

        recipeFiles = loadRecipeFiles("insert_directory_here");
        Collections.sort(recipeFiles, Comparator.comparing(File::getName));

        calendar = Calendar.getInstance();
        currentPage = 0;

        oldestDateCooked = findOldestDateCooked();
        calendar = Calendar.getInstance(); // Start with the current week

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
        List<File> weeklyRecipes = getWeeklyRecipes();

        int totalCalories = 0;
        int totalProtein = 0;
        int totalFat = 0;
        int totalCarbs = 0;
        int totalSugar = 0;

        for (int i = 0; i < weeklyRecipes.size(); i++) {
            File file = weeklyRecipes.get(i);
            String content = FileUtils.readRecipeFromFile(String.valueOf(file));

            JLabel titleLabel = new JLabel(extractField(content, "Title").replace("Title: ", ""));
            titleLabel.setBounds(10, i * 100, 300, 30);
            panel.add(titleLabel);

            JLabel caloriesLabel = new JLabel(extractField(content, "Estimated Calories"));
            caloriesLabel.setBounds(320, i * 100, 200, 30);
            panel.add(caloriesLabel);

            JLabel proteinLabel = new JLabel(extractField(content, "Estimated Proteins"));
            proteinLabel.setBounds(530, i * 100, 200, 30);
            panel.add(proteinLabel);

            JLabel fatLabel = new JLabel(extractField(content, "Estimated Fat"));
            fatLabel.setBounds(740, i * 100, 200, 30);
            panel.add(fatLabel);

            JLabel carbsLabel = new JLabel(extractField(content, "Estimated Carbs"));
            carbsLabel.setBounds(950, i * 100, 200, 30);
            panel.add(carbsLabel);

            JLabel sugarLabel = new JLabel(extractField(content, "of which sugar"));
            sugarLabel.setBounds(1160, i * 100, 200, 30);
            panel.add(sugarLabel);

            totalCalories += extractNutritionalValue(content, "Estimated Calories:");
            totalProtein += extractNutritionalValue(content, "Estimated Proteins:");
            totalFat += extractNutritionalValue(content, "Estimated Fat:");
            totalCarbs += extractNutritionalValue(content, "Estimated Carbs:");
            totalSugar += extractNutritionalValue(content, "of which sugar:");


            pageLabel.setText("Week of " + new SimpleDateFormat("dd.MM.yyyy").format(calendar.getTime()));
            prevPageButton.setEnabled(!calendar.getTime().before(oldestDateCooked));
            nextPageButton.setEnabled(calendar.getTime().before(Calendar.getInstance().getTime()));
            panel.revalidate();
            panel.repaint();
        }

        JLabel totalLabel = new JLabel("Weekly Total:");
        totalLabel.setBounds(10, 650, 300, 30);
        panel.add(totalLabel);

        JLabel totalCaloriesLabel = new JLabel("Total Calories: " + totalCalories + " kcal");
        totalCaloriesLabel.setBounds(320, 650, 200, 30);
        panel.add(totalCaloriesLabel);

        System.out.println(totalCalories);

        JLabel totalProteinLabel = new JLabel("Total Proteins: " + totalProtein + " g");
        totalProteinLabel.setBounds(530,650, 200, 30);
        panel.add(totalProteinLabel);

        JLabel totalFatLabel = new JLabel("Total Fat: " + totalFat + " g");
        totalFatLabel.setBounds(740, 650, 200, 30);
        panel.add(totalFatLabel);

        JLabel totalCarbsLabel = new JLabel("Total Carbs: " + totalCarbs + " g");
        totalCarbsLabel.setBounds(950, 650, 200, 30);
        panel.add(totalCarbsLabel);

        JLabel totalSugarLabel = new JLabel("of which Sugar: " + totalSugar + " g");
        totalSugarLabel.setBounds(1160, 650, 200, 30);
        panel.add(totalSugarLabel);

        pageLabel.setText(STR."Week of \{new SimpleDateFormat("dd.MM.yyyy").format(calendar.getTime())}");
        prevPageButton.setEnabled(!calendar.getTime().before(oldestDateCooked));
        nextPageButton.setEnabled(calendar.getTime().before(Calendar.getInstance().getTime()));
        panel.revalidate();
        panel.repaint();
    }

    private List<File> getWeeklyRecipes() {
        List<File> weeklyRecipes = new ArrayList<>();
        Calendar startOfWeek = (Calendar) calendar.clone();
        startOfWeek.set(Calendar.DAY_OF_WEEK, startOfWeek.getFirstDayOfWeek());
        startOfWeek.set(Calendar.HOUR_OF_DAY, 0);
        startOfWeek.set(Calendar.MINUTE, 0);
        startOfWeek.set(Calendar.SECOND, 0);
        startOfWeek.set(Calendar.MILLISECOND, 0);

        Calendar endOfWeek = (Calendar) startOfWeek.clone();
        endOfWeek.add(Calendar.DAY_OF_WEEK, 6);

        for (File file : recipeFiles) {
            String content = FileUtils.readRecipeFromFile(String.valueOf(file));
            String dateCookedStr = extractField(content, "Date Cooked").replace("Date Cooked: ", "").trim();

            if (dateCookedStr.equals("Date Cooked:")) {
                continue;  // Skip recipes without a "Date Cooked"
            }

            try {
                Date dateCooked = new SimpleDateFormat("dd.MM.yyyy").parse(dateCookedStr);
                Calendar dateCookedCal = Calendar.getInstance();
                dateCookedCal.setTime(dateCooked);

                if (!dateCookedCal.before(startOfWeek) && !dateCookedCal.after(endOfWeek)) {
                    weeklyRecipes.add(file);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        weeklyRecipes.sort(Comparator.comparing(file -> {
            String content = FileUtils.readRecipeFromFile(String.valueOf(file));
            String dateCookedStr = extractField(content, "Date Cooked").replace("Date Cooked: ", "").trim();
            if (!dateCookedStr.equals("Date Cooked:")) {
                try {
                    Date dateCooked = new SimpleDateFormat("dd.MM.yyyy").parse(dateCookedStr);
                    return dateCooked;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            return new Date();
        }));

        return weeklyRecipes;
    }


    private String extractField(String content, String fieldName) {
        for (String line : content.split("\n")) {
            if (line.startsWith(fieldName)) {
                return line;
            }
        }
        return "";
    }

    private int extractNutritionalValue(String content, String fieldName) {
        String field = extractField(content, fieldName);
        try {
            return Integer.parseInt(field.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private Date findOldestDateCooked() {
        Date oldestDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

        for (File file : recipeFiles) {
            String content = FileUtils.readRecipeFromFile(String.valueOf(file));
            String dateCookedStr = extractField(content, "Date Cooked").replace("Date Cooked: ", "").trim();

            if (dateCookedStr.isEmpty()) {
                continue;
            }

            try {
                Date dateCooked = sdf.parse(dateCookedStr);
                if (dateCooked.before(oldestDate)) {
                    oldestDate = dateCooked;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return oldestDate;
    }
}
