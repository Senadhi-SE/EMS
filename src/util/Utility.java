/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 *
 * @author Pavithra
 */
public class Utility {

    public static void customizeTable(JTable table) {
        /*
            This method is for re-stlying JTables for a
            defined theme.
         */

        JTableHeader header = table.getTableHeader();
        header.setFont(new java.awt.Font("Segoe UI", 1, 18));
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

        table.setFont(new java.awt.Font("Segoe UI", 0, 16));
        table.setRowHeight(28);

        // Center-align all cell contents
        DefaultTableCellRenderer render = new DefaultTableCellRenderer();
        render.setHorizontalAlignment(DefaultTableCellRenderer.LEFT);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(render);
        }
    }

    public static void customizeTable(JTable table, int alignment) {
        
        /*
            This method is for re-stlying JTables for a
            defined theme.
            
          * @param alignment
            LEFT = 2, CENTER = 0
        
            render.setHorizontalAlignment(DefaultTableCellRenderer.LEFT);
            ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);
         */

        JTableHeader header = table.getTableHeader();
        header.setFont(new java.awt.Font("Segoe UI", 1, 18));
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

        table.setFont(new java.awt.Font("Segoe UI", 0, 16));
        table.setRowHeight(28);

        // Center-align all cell contents
        DefaultTableCellRenderer render = new DefaultTableCellRenderer();
        render.setHorizontalAlignment(DefaultTableCellRenderer.LEFT);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(render);
        }
    }

}
