/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */

package ch.inser.jsl.tools;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;

import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

/**
 * Classe contenant des méthodes statiques relatives au UI (user interface).
 * <p>
 * Les méthodes devront être complètement documentées avec Javadoc et respecter les formats suivants :<br>
 * -
 *
 * @author INSER SA
 * @author CONTRIBUTOR -
 * @version :
 * @version <Version> (incrémenter la version à chaque changement)
 */
public class UITools {
    /**
     * Obtient la dimension d'un texte selon une police de caractère. Cette méthode peut calculer la dimension indépendamment du contexte
     * graphique, s'il n'est pas donné, car il peut obtenir le contexte local du device écran par défaut.
     * <p>
     * Attention, cette méthode retourne une hauteur qui semble ne pas varier selon le texte donné en argument. La hauteur semble être celle
     * de la police de caractère.
     *
     * @param text
     *            Texte à afficher
     * @param font
     *            Police de caractère à utiliser pour afficher le texte
     * @param g
     *            Device graphique, ou null pour calculation par défaut
     * @param dimensionOut
     *            Dimension à retourner ou null pour créer un nouvel objet Dimension.
     * @return La dimension du texte à afficher
     *
     * @author INSER SA
     * @version 1.0
     */
    public static Dimension getTextDimension(String text, Font font, Graphics g, Dimension dimensionOut) {
        int width;
        int height;

        if (g != null) {
            height = (int) g.getFontMetrics().getLineMetrics(text, g).getHeight();
            width = g.getFontMetrics(font).stringWidth(text);
        } else {
            FontRenderContext fontRenderContext;
            AffineTransform defAfft;
            GraphicsDevice gd;

            gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            defAfft = gd.getDefaultConfiguration().getDefaultTransform();
            fontRenderContext = new FontRenderContext(defAfft, false, false);
            width = (int) font.getStringBounds(text, fontRenderContext).getWidth();
            height = (int) font.getLineMetrics(text, fontRenderContext).getHeight();
        }

        if (dimensionOut == null) {
            return new Dimension(width, height);
        }
        dimensionOut.setSize(width, height);
        return dimensionOut;
    }

    public static Dimension getTextDimension(String text, Graphics g) {
        return getTextDimension(text, g.getFont(), g, null);
    }

    /**
     * Convertit des millimètres en points de 1/72ème de inch. 1 inch (72 dots) = 2.54 cm = 25.4 mm.
     *
     * @return Points de 1/72ème de inch.
     *
     * @author INSER SA
     * @version 1.0
     */
    static public double millimeterToDot(double millimeters) {
        return millimeters * 72 / 25.4;
    }

    /**
     * Convertit des mètres en points de 1/72ème de inch. 1 inch (72 dots) = 2.54 cm = 25.4 mm.
     *
     * @return Points de 1/72ème de inch.
     * @see millimeterToDot()
     *
     * @author INSER SA
     * @version 1.0
     */
    static public double meterToDot(double meters) {
        return millimeterToDot(meters * 1000.0);
    }

    /**
     * Convertit des mètres à une échelle donnée en points de 1/72ème de inch. 1 inch (72 dots) = 2.54 cm = 25.4 mm.
     *
     * @param meters
     *            Mètres à convertir
     * @param scale
     *            Echelle appliquée aux mètres
     * @return Points de 1/72ème de inch.
     * @see millimeterToDot()
     *
     * @author INSER SA
     * @version 1.0
     */
    static public double meterToDot(double meters, double scale) {
        return millimeterToDot(meters * 1000.0 / scale);
    }

    /**
     * Convertit des points de 1/72ème de inch en mètres. On divise les dots par le nombre de dots par mètre, ce qui donne le nombre de
     * mètres pour les dots. 1 inch (72 dots) = 2.54 cm = 25.4 mm.
     *
     * @return Mètres
     *
     * @author INSER SA
     * @version 1.0
     */
    static public double dotToMeter(double dots) {
        return dots / (1000.0 * 72 / 25.4); // dots / meterToDot(1.0)
    }

    /**
     * Convertit des millimètres en inches. 1 inch = 2.54 cm = 25.4 mm.
     *
     * @return Inches
     *
     * @author INSER SA
     * @version 1.0
     */
    static public double millimeterToInch(double millimeters) {
        return millimeters / 25.4;
    }

    /**
     * Convertit des DPI écran en DPI imprimante.
     *
     * @return Points (dots) correspondant à la résolution d'imprimante
     *
     * @author INSER SA
     * @version 1.0
     */
    static public double screenToPrinterDots(double screenDots, int printerDpi) {
        int screenDpi = Toolkit.getDefaultToolkit().getScreenResolution();
        return screenDots / screenDpi * printerDpi;
    }

    /**
     * Dimensionne un Container par rapport à la taille d'un Component.
     *
     * @author INSER SA
     * @version 1.0
     */
    public static void sizeContainerToComponent(Container container, Component component) {
        if (container.isDisplayable() == false) {
            container.addNotify();
        }

        Insets insets = container.getInsets();
        Dimension size = component.getPreferredSize();

        int width = insets.left + insets.right + size.width;
        int height = insets.top + insets.bottom + size.height;

        container.setSize(width, height);
    }

    /**
     * Centre la Frame par rapport à l'écran.
     *
     * @author INSER SA
     * @version 1.0
     */
    public static void centerFrame(Frame f) {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension d = f.getSize();

        int x = (screen.width - d.width) / 2;
        int y = (screen.height - d.height) / 2;

        f.setLocation(x, y);
    }

    /**
     * Centre le dialogue par rapport à l'écran.
     *
     * @author INSER SA
     * @version 1.0
     */
    public static void centerDialog(JDialog f) {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension d = f.getSize();
        int w = d.width;
        int h = d.height;
        int x = (screen.width - w) / 2;
        int y = (screen.height - h) / 2;

        f.setBounds(x, y, w, h);
    }

    /**
     * Définit les tailles correctes pour les colonnes. Si toutes les en-têtes de colonnes sont plus larges que les contenus des cellules de
     * colonnes, alors on peut utiliser juste column.sizeWidthToFit().
     * <p>
     * Nécessite JDK 1.3
     *
     * @author INSER SA
     * @version 1.0
     */
    public static void setColumnWidthsToFitContent(JTable table) {
        TableCellRenderer headerRenderer = null;
        TableCellRenderer cellRenderer = null;
        TableColumn column = null;
        TableModel model = table.getModel();
        Component comp = null;
        int headerWidth = 0;
        int cellWidth = 0;

        headerRenderer = table.getTableHeader().getDefaultRenderer();

        for (int col = 0, n = model.getColumnCount() - 1; col < n; col++) {
            column = table.getColumnModel().getColumn(col);

            // Demande taille du contenu de l'en-tête
            comp = headerRenderer.getTableCellRendererComponent(null, column.getHeaderValue(), false, false, 0, 0);
            headerWidth = comp.getPreferredSize().width;

            // Demande taille maximum du contenu de chaque cellule/ligne de la
            // colonne
            cellWidth = 0;
            cellRenderer = table.getDefaultRenderer(model.getColumnClass(col));

            for (int row = 0, m = model.getRowCount(); row < m; row++) {
                comp = cellRenderer.getTableCellRendererComponent(table, model.getValueAt(row, col), false, false, row, col);
                cellWidth = Math.max(cellWidth, comp.getPreferredSize().width);
            }

            // Définit la taille de la colonne
            column.setPreferredWidth(Math.max(headerWidth, cellWidth));
        }
    }
}
