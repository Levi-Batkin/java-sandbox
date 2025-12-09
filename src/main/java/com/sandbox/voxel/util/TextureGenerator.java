package com.sandbox.voxel.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Utility to generate a simple texture atlas for blocks.
 * This creates a 64x64 texture with 4x4 grid of 16x16 block textures.
 */
public class TextureGenerator {
    private static final int ATLAS_SIZE = 64;
    private static final int TILE_SIZE = 16;
    
    public static void main(String[] args) {
        generateTextureAtlas();
    }
    
    public static void generateTextureAtlas() {
        BufferedImage atlas = new BufferedImage(ATLAS_SIZE, ATLAS_SIZE, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = atlas.createGraphics();
        
        // Block colors
        Color[] colors = {
            new Color(139, 90, 43),   // 0: Dirt (brown)
            new Color(106, 168, 79),  // 1: Grass top (green)
            new Color(89, 89, 89),    // 2: Grass side (gray-green with dirt)
            new Color(128, 128, 128), // 3: Stone (gray)
        };
        
        // Generate each tile
        for (int i = 0; i < colors.length; i++) {
            int tileX = (i % 4) * TILE_SIZE;
            int tileY = (i / 4) * TILE_SIZE;
            
            drawTile(g, tileX, tileY, colors[i]);
        }
        
        g.dispose();
        
        // Save the texture
        try {
            File outputDir = new File("src/main/resources/textures");
            outputDir.mkdirs();
            File outputFile = new File(outputDir, "blocks.png");
            ImageIO.write(atlas, "PNG", outputFile);
            System.out.println("Texture atlas generated: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to save texture atlas: " + e.getMessage());
        }
    }
    
    private static void drawTile(Graphics2D g, int x, int y, Color baseColor) {
        // Fill base color
        g.setColor(baseColor);
        g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
        
        // Add some texture variation
        for (int px = 0; px < TILE_SIZE; px++) {
            for (int py = 0; py < TILE_SIZE; py++) {
                if ((px + py) % 4 == 0) {
                    int variation = (px * py) % 20 - 10;
                    Color variedColor = new Color(
                        clamp(baseColor.getRed() + variation),
                        clamp(baseColor.getGreen() + variation),
                        clamp(baseColor.getBlue() + variation)
                    );
                    g.setColor(variedColor);
                    g.fillRect(x + px, y + py, 1, 1);
                }
            }
        }
    }
    
    private static int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }
}
