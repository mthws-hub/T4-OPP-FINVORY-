package ec.edu.espe.finvory.utils;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

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

    public static ImageIcon getTransparentIcon(ImageIcon icon, float opacity) {
        if (icon == null) {
            return null;
        }
        Image img = icon.getImage();
        int w = img.getWidth(null);
        int h = img.getHeight(null);
        java.awt.image.BufferedImage bi = new java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bi.createGraphics();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        g2.drawImage(img, 0, 0, null);
        g2.dispose();
        return new ImageIcon(bi);
    }

    public static void startClock(javax.swing.JLabel label) {
        javax.swing.Timer timer = new javax.swing.Timer(1000, e -> {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy | HH:mm:ss");
            label.setText(sdf.format(new java.util.Date()));
        });
        timer.start();
    }

    public static void applyWatermark(JLabel label, java.net.URL imgUrl, int width, int height, float opacity) {
        ImageIcon scaled = getScaledIcon(imgUrl, width, height);
        ImageIcon transparent = getTransparentIcon(scaled, opacity);
        label.setIcon(transparent);
        label.setText("");
        label.setHorizontalAlignment(SwingConstants.CENTER);
    }

    public static void setStatusText(javax.swing.JLabel label, String companyName) {
        label.setText(" Conectado: " + companyName + " | Sistema Finvory v1.0");
        label.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 11));
        label.setForeground(new java.awt.Color(100, 100, 100));
    }

    public static void applyHeaderStyle(javax.swing.JPanel pnlHeader, javax.swing.JLabel lblWelcome, String user, String company) {
        lblWelcome.setText("<html>Bienvenido: <b>" + user + "</b> | " + company + "</html>");
        if (pnlHeader != null) {
        }
    }
}
