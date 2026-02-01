package ec.edu.espe.finvory.view;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatIntelliJLaf;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 *
 * @author Joseph B. Medina
 */
public final class ButtonStyles {

    private static final Font DEFAULT_FONT = new Font("Copperplate Gothic Light", Font.PLAIN, 12);
    private static final Color PRIMARY_GREEN = new Color(45, 106, 79);
    private static final Color SOFT_SELECTION_GREEN = new Color(216, 243, 220);
    private static final Color HOVER_GREEN = new Color(82, 183, 136);
    private static final Color TEXT_WHITE = Color.WHITE;

    private ButtonStyles() {

    }

    public static void primary(JButton button) {

        applyBase(button);
        button.setBackground(new java.awt.Color(0, 123, 0));
        button.setForeground(java.awt.Color.WHITE);
    }

    public static void danger(JButton button) {

        applyBase(button);
        button.setBackground(new java.awt.Color(180, 30, 30));
        button.setForeground(java.awt.Color.WHITE);
    }

    public static void neutral(JButton button) {

        applyBase(button);
        button.setBackground(new java.awt.Color(90, 90, 90));
        button.setForeground(java.awt.Color.WHITE);
    }

    private static void applyBase(JButton button) {

        button.setFont(DEFAULT_FONT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setMargin(new Insets(8, 14, 8, 14));

        button.setBorder(new CompoundBorder(
                new LineBorder(new java.awt.Color(0, 0, 0, 40), 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));
    }

    public static void sidebar(JButton button) {
        applyBase(button);
        button.setBackground(new java.awt.Color(50, 50, 50));
        button.setForeground(java.awt.Color.WHITE);
        button.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        button.setBorder(new javax.swing.border.EmptyBorder(10, 20, 10, 10));
    }

    public static void initializeComponents() {
        try {
            
            FlatIntelliJLaf.setup();

            UIManager.put("Button.arc", 18);
            UIManager.put("Component.focusColor", PRIMARY_GREEN);
            UIManager.put("Component.innerFocusWidth", 0);
            
            UIManager.put("Button.defaultButtonFollowsFocus", true);
            UIManager.put("Button.focusedBackground", PRIMARY_GREEN);
            UIManager.put("OptionPane.buttonFont", new Font("SansSerif", Font.BOLD, 12));

            UIManager.put("Selection.background", PRIMARY_GREEN);
            UIManager.put("Selection.foreground", TEXT_WHITE);
            UIManager.put("Table.selectionBackground", SOFT_SELECTION_GREEN);
            UIManager.put("Table.selectionForeground", new Color(27, 67, 50));
            UIManager.put("Table.focusSelectedCellHighlightBorderColor", PRIMARY_GREEN);

            UIManager.put("MenuBar.selectionBackground", HOVER_GREEN);
            UIManager.put("Menu.selectionBackground", PRIMARY_GREEN);
            UIManager.put("MenuItem.selectionBackground", PRIMARY_GREEN);
            UIManager.put("MenuItem.selectionForeground", TEXT_WHITE);
            UIManager.put("ComboBox.selectionBackground", PRIMARY_GREEN);
            UIManager.put("ComboBox.selectionForeground", TEXT_WHITE);

            UIManager.put("TabbedPane.selectedBackground", SOFT_SELECTION_GREEN);
            UIManager.put("TabbedPane.selectedForeground", PRIMARY_GREEN);
            UIManager.put("TabbedPane.underlineColor", PRIMARY_GREEN);
            UIManager.put("TabbedPane.focusColor", SOFT_SELECTION_GREEN);

        } catch (Exception ex) {
            System.err.println("Error inicializando componentes visuales: " + ex.getMessage());
        }
    }

    public static void applyPrimaryStyle(JButton button) {
        button.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        button.putClientProperty("JButton.arc", 18);
        button.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 12, 4, 12));

        button.setMargin(new Insets(4, 12, 4, 12));
        button.setFont(new Font("SansSerif", Font.BOLD, 12));

        if (button.getText() != null && !button.getText().isEmpty()) {
            button.setText(formatToTitleCase(button.getText()));
        }

        button.setBackground(PRIMARY_GREEN);
        button.setForeground(TEXT_WHITE);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
    }

    private static String formatToTitleCase(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }
    
}
