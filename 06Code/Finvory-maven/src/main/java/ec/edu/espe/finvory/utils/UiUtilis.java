package ec.edu.espe.finvory.utils;

import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;

/**
 *
 * @author Mathews Pastor, The POOwer Rangers Of Programming
 */
public class UiUtilis {
    
    public static javax.swing.ImageIcon getScaledIcon(java.net.URL url, int containerWidth, int containerHeight) {
        if (url == null) {
            return null;
        }

        javax.swing.ImageIcon original = new javax.swing.ImageIcon(url);
        int imgW = original.getIconWidth();
        int imgH = original.getIconHeight();
        double ratio = Math.min((double) containerWidth / imgW, (double) containerHeight / imgH);
        int newWidth = (int) (imgW * ratio);
        int newHeight = (int) (imgH * ratio);
        java.awt.Image scaled = original.getImage().getScaledInstance(newWidth, newHeight, java.awt.Image.SCALE_SMOOTH);
        return new javax.swing.ImageIcon(scaled);
    }
}
