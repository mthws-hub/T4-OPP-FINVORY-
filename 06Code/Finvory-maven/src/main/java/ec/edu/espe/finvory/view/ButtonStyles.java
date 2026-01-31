package ec.edu.espe.finvory.view;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 *
 * @author Joseph B. Medina
 */
public final class ButtonStyles {

    private static final Font DEFAULT_FONT = new Font("Copperplate Gothic Light", Font.PLAIN, 12);

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
}
