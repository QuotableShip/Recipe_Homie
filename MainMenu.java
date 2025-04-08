import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu extends JFrame {

    public MainMenu() {
        setTitle("Meal Prep App - Main Menu");
        setSize(400, 300);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton cookNewRecipeButton = new JButton("Cook New Recipe");
        cookNewRecipeButton.setBounds(50, 50, 300, 50);
        cookNewRecipeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new CookNewRecipe().setVisible(true);
                dispose();
            }
        });

        JButton recookRecipeButton = new JButton("CookBook");
        recookRecipeButton.setBounds(50, 110, 300, 50);
        recookRecipeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new RecookRecipes().setVisible(true);
                dispose();
            }
        });

        JButton cookingHistoryButton = new JButton("Cooking History");
        cookingHistoryButton.setBounds(50, 170, 300, 50);
        cookingHistoryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new CookingHistory().setVisible(true);
                dispose();
            }
        });

        add(cookNewRecipeButton);
        add(recookRecipeButton);
        add(cookingHistoryButton);
    }

    public static void main(String[] args) {
        new MainMenu().setVisible(true);
    }
}


