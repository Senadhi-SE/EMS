/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author senad
 */
public class BarcodeGenerator {

    public static String generateEmployeeBarcode(String employeeNIC) {
        String filePath = null;
        try {
            // Generate the barcode matrix
            BitMatrix bitMatrix = new MultiFormatWriter().encode(
                    employeeNIC, BarcodeFormat.CODE_128, 300, 150
            );

            // Convert matrix to image
            BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);

            // Ensure the output directory exists
            String directoryPath = "src/barcodes/";
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Build the output file path
            filePath = directoryPath + employeeNIC + ".png";
            File outputFile = new File(filePath);

            // Write the image to file
            ImageIO.write(image, "png", outputFile);
            System.out.println("Barcode generated successfully: " + outputFile.getAbsolutePath());            
        } catch (WriterException | IOException e) {
            e.printStackTrace();
            System.err.println("Error generating barcode: " + e.getMessage());
        }
        return filePath;
    }
}
