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

// JavaFX imports
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.stage.Stage;

// File/Image IO imports
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

/**
 * A JavaFX frontend for the image transformation program I wrote
 * in April 2022.
 *
 * @author Hayden Walker
 * @version 2022-09-14
 */
public class MatrixGUI extends Application
{
    // Store the images
    private WritableImage originalImage;
    private WritableImage transformedImage;
    
    // Message/error label at bottom of screen
    private Label messageLabel;
    
    // Store text fields for path and matrix
    private TextField loadPath;
    private TextField[][] matrixField;
    
    // Image viewpanes
    private ImageView originalImageView;
    private ImageView transformedImageView;
    
    // The matrix to use for the linear transformation
    private double[][] matrix;
    
    /**
     * JavaFX application entry point
     *
     * @param stage the primary stage for this application.
     */
    @Override
    public void start(Stage stage)
    {
        /*
         * Initialize instance variables
         */
        
        // Path field
        loadPath = new TextField("C:\\Users\\Hayden Walker\\Desktop\\cat.jpg");
        
        // Matrix field
        matrixField = new TextField[3][3];
        
        // Initialize matrix boxes
        for(int row = 0; row < 3; row++)
            for(int col = 0; col < 3; col++)
                matrixField[row][col] = new TextField();
    
        // Images start blank
        originalImage = null;
        transformedImage = null;

        // Create image views
        originalImageView = new ImageView();
        transformedImageView = new ImageView();   
        
        /*
         * Set image sizes
         */
        
        int imageWidth = 175;
        int imageHeight = 200;
        
        /*
         * Create and configure nodes
         */
        
        // Image label
        Label loadLabel = new Label("Enter path to JPEG image:");
        
        // Image-loading button
        Button loadButton = new Button("Load");
        loadButton.setOnAction(this::loadImage);
        loadButton.setMaxWidth(1000);
        
        // Matrix label
        Label matrixLabel = new Label("Enter a 3x3 matrix:");

        // Create "transform" button
        Button transformButton = new Button("Transform");
        transformButton.setOnAction(this::begin);
        transformButton.setMaxWidth(1000);
       
        // Bottom Label
        messageLabel = new Label("By Hayden Walker, 2022. www.haywalk.ca");
        
        /*
         * Configure images, put them in their own GridPane
         */
        
        // Make abels for images
        Label originalImageLabel = new Label("Original:");
        Label transformedImageLabel = new Label("Transformed:");
        
        // Set image sizes
        originalImageView.setFitWidth(imageWidth);
        originalImageView.setFitHeight(imageHeight);
        originalImageView.setPreserveRatio(true);
        transformedImageView.setFitWidth(imageWidth);
        transformedImageView.setFitHeight(imageHeight);
        transformedImageView.setPreserveRatio(true);
        
        // Add GridPane for images
        GridPane imagePane = new GridPane();
        imagePane.setPadding(new Insets(10, 10, 10, 10));
        imagePane.setMinSize(350, 245);
        imagePane.setMaxSize(350, 245);
        imagePane.setVgap(10);
        imagePane.setHgap(10);
        
        // Make both columns in the image GridPane of equal width
        ColumnConstraints columnConstraint = new ColumnConstraints();
        columnConstraint.setPercentWidth(50);
        imagePane.getColumnConstraints().addAll(columnConstraint, columnConstraint);
        
        // Add images to image GridPane
        imagePane.add(originalImageLabel, 0, 0);
        imagePane.add(transformedImageLabel, 1, 0);
        imagePane.add(originalImageView, 0, 1);
        imagePane.add(transformedImageView, 1, 1);
        
        /*
         * Create GridPane for whole program and add
         * nodes to it
         */
        
        // Create a new GridPane to store everything
        GridPane pane = new GridPane();
        pane.setPadding(new Insets(10, 10, 10, 10));
        pane.setMinSize(300, 300);
        pane.setVgap(10);
        pane.setHgap(10);       

        // Add the file stuff into the pane
        pane.add(loadLabel, 0, 0, 3, 1);
        pane.add(loadButton, 0, 1);
        pane.add(loadPath, 1, 1, 2, 1);
        
        // Add matrix label to pane
        pane.add(matrixLabel, 0, 2, 3, 1);
        
        // Add matrix fields
        for(int row = 0; row < 3; row++)
            for(int col = 0; col < 3; col++)
                pane.add(matrixField[row][col], col, row + 3);
      
        // Add "transform" button
        pane.add(transformButton, 0, 6, 3, 1);
        
        // Add image GridPane to main GridPane
        pane.add(imagePane, 0, 7, 3, 1);
        
        // Add bottom label
        pane.add(messageLabel, 0, 8, 3, 1);
        
        // Create and set up scene and stage
        Scene scene = new Scene(pane, 400, 520);
        stage.setTitle("Linear Transformations on Colours");
        stage.setScene(scene);
        
        // Make window non-resizable
        stage.setResizable(false);

        // Show the Stage (window)
        stage.show();
    }
    
    /**
     * Load matrix entries from text fields and store them in the
     * matrix array
     */
    private void loadMatrix()
    {
        // Create an empty matrix
        matrix = new double[3][3];        
        
        // Load in data from the text fields
        for(int row = 0; row < 3; row++) {
            for(int col = 0; col < 3; col++) {
                double thisEntry = 0;
                
                // Leave as 0 if blank
                try {
                    thisEntry= Double.parseDouble(matrixField[row][col].getCharacters().toString());
                } catch(Exception e) {
                    
                }
                
                matrix[row][col] = thisEntry;
            }
        }
    }
    
    /**
     * Attempt to load the matrix and carry out the transformation
     */
    private void begin(ActionEvent event)
    {        
        // Load the matrix
        loadMatrix();
        
        // Begin the transformation
        transform();
    }
    
    /**
     * Attempt to perform the transformation
     */
    private void transform()
    {
        // Check that image has been loaded
        if(originalImage != null) {
            // Store the images as BufferedImages
            BufferedImage image, transformed;
            
            // Convert original image to a BufferedImage
            image = SwingFXUtils.fromFXImage(originalImage, null);
          
            // Perform transformation
            ColourTransformation transformation = new ColourTransformation(image, matrix);
            transformed = transformation.getImage();
            
            // Convert back to FX image
            transformedImage = SwingFXUtils.toFXImage(transformed, null);
            transformedImageView.setImage(transformedImage);
            
            // Display success message
            messageLabel.setText("Transformation complete.");
        } else {
            // Display error message
            messageLabel.setText("Error: No image loaded.");
        }
    }
    
    /**
     * Load in an image
     */
    private void loadImage(ActionEvent event)
    {
        // Store file and image
        File inputFile;
        BufferedImage image;
        
        // Get path
        String path = loadPath.getCharacters().toString();
        
        // Open the input file
        inputFile = new File(path);

        try {
            // Read the image from the file
            image = ImageIO.read(inputFile);
            
            // Store the loaded image as an FX image
            originalImage = SwingFXUtils.toFXImage(image, null);
            originalImageView.setImage(originalImage);
            
            // Display success message
            messageLabel.setText("Image loaded.");
        } catch (IOException e) {
            // Show error message
            messageLabel.setText("Error: File not found.");
        }
    }
}
