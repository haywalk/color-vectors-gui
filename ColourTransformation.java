/*
 * ColourTransformation.java
 * 
 * Copyright 2022 Hayden D. Walker <haydenwalker@live.com>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 * 
 * 
 */

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.util.Scanner;

/**
 * Given an image, for each pixel, treat its colour as a vector and
 * apply linear transformations to it.
 * 
 * @author Hayden Walker
 * @version 2022-04-16
 */
public class ColourTransformation
{
    // Instance variables
    private BufferedImage image;
    private double[][] transformation;
    private double[] col1, col2, col3;
    
    /**
     * Create a new ColourVect object.
     * 
     * @param image Image to transform.
     * @param transformation Matrix to apply.
     */
    public ColourTransformation(BufferedImage image, double[][] transformation)
    {
        this.image = image;
        this.transformation = transformation;
        makeColumnVectors();
        
        for(int y = 0; y < image.getHeight(); y++)
            for(int x = 0; x < image.getWidth(); x++)
                apply(x, y);
    }
    
    /**
     * Return array [R, G, B] for a pixel in the image.
     * 
     * @param x X-coordinate of pixel.
     * @param y Y-coordinate of pixel.
     * @return Array of RGB values for the pixel.
     */
    private double[] getRGBVector(int x, int y)
    {
        // Get this pixel's colour
        Color thisColor = new Color(image.getRGB(x, y));
        
        // Split colour into RGB values
        double r = thisColor.getRed() * 1.0;
        double g = thisColor.getGreen() * 1.0;
        double b = thisColor.getBlue() * 1.0;
        
        // Return array of values
        return new double[]{r, g, b};
    }
    
    /**
     * Split 2D array of values into column vectors.
     */
    private void makeColumnVectors()
    {
        col1 = new double[]{
            transformation[0][0], transformation[1][0], transformation[2][0]
        };
        
        col2 = new double[]{
            transformation[0][1], transformation[1][1], transformation[2][1]
        };
        
        col3 = new double[]{
            transformation[0][2], transformation[1][2], transformation[2][2]
        };
    }
    
    /**
     * Apply transformation to a pixel.
     * 
     * @param x X-coordinate of pixel.
     * @param y Y-coordinate of pixel.
     */
    private void apply(int x, int y)
    {
        // Get new RGB values
        int newR = (int) dot(getRGBVector(x, y), col1) % 255; 
        int newG = (int) dot(getRGBVector(x, y), col2) % 255; 
        int newB = (int) dot(getRGBVector(x, y), col3) % 255; 
        
        // Wrap around negative numbers
        
        if(newR < 0)
            newR += 255;
        
        if(newG < 0)
            newG += 255;
        
        if(newB < 0)
            newB += 255;
        
        /* Apply transformation to the colour vector at this pixel
        // (wrap around 255)
        int[] new_color = new int[]{
            Math.abs((int) dot(getRGBVector(x, y), col1)) % 255,        
            Math.abs((int) dot(getRGBVector(x, y), col2)) % 255,        
            Math.abs((int) dot(getRGBVector(x, y), col3)) % 255
        };*/
    
        // Create new colour
        Color newColor = new Color(newR, newG, newB);
        int rgbValue = newColor.getRGB();
        
        // Change colour of pixel to new colour
        image.setRGB(x, y, rgbValue);
    }
    
    /**
     * Return dot products of arrays a and b.
     * 
     * @param a First vector.
     * @param b Second vector.
     */
    private double dot(double[] a, double[] b)
    {
        return (a[0] * b[0]) + (a[1] * b[1]) + (a[2] * b[2]);
    }
    
    /**
     * Return the image.
     * 
     * @return The transformed image.
     */
    public BufferedImage getImage()
    {
        return image;
    }
    
    /**
     * Starts the program.
     * 
     * @param args Command-line arguments.
     */
    public static void main (String[] args) throws IOException
    {
        // Create file and image objects, and scanner
        File outputFile, inputFile;
        BufferedImage image;
        ColourTransformation transformation;
        Scanner sc;
        
        // Initialize output file and scanner
        outputFile = new File("out.jpg");
        sc = new Scanner(System.in);
        
        // Attempt to read input file name from arguments
        try {
            inputFile = new File(args[0]);
        } catch(IndexOutOfBoundsException e) {
            System.out.println("Please specify an input file.");
            inputFile = null;
            System.exit(1);
        }
        
        // Attempt to load input file
        try {
            image = ImageIO.read(inputFile);
        } catch (IOException e) {
            System.out.println("Input file does not exist.");
            image = null;
            System.exit(1);
        }
    
        // Create and fill matrix
        double[][] matrix = new double[3][3];
        
        for(int r = 0; r < 3; r++)
            for(int c = 0; c < 3; c++)
                matrix[r][c] = sc.nextDouble();
    
        // Create new transformation
        transformation = new ColourTransformation(image, matrix);
        
        // Write to output file
        ImageIO.write(transformation.getImage(), "jpg", outputFile);
    }
}

