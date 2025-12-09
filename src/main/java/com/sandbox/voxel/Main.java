package com.sandbox.voxel;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.system.AppSettings;
import com.sandbox.voxel.input.InputHandler;
import com.sandbox.voxel.world.World;

/**
 * Main application entry point for the voxel sandbox.
 * 
 * Controls:
 * - WASD: Movement
 * - Space: Move up
 * - Left Shift: Move down
 * - Mouse: Look around
 * - Left Click: Remove block
 * - Right Click: Place block
 * - 1/2/3: Select block type (Dirt/Grass/Stone)
 * - F3: Toggle debug mode
 */
public class Main extends SimpleApplication {
    private World world;
    private InputHandler inputHandler;
    
    public static void main(String[] args) {
        Main app = new Main();
        
        // Configure application settings
        AppSettings settings = new AppSettings(true);
        settings.setTitle("Voxel Sandbox MVP");
        settings.setResolution(1280, 720);
        settings.setVSync(true);
        settings.setSamples(4); // Anti-aliasing
        
        app.setSettings(settings);
        app.setShowSettings(false); // Skip settings dialog
        app.start();
    }
    
    @Override
    public void simpleInitApp() {
        // Configure camera
        flyCam.setMoveSpeed(20f);
        cam.setLocation(new Vector3f(8, 40, 8));
        cam.lookAt(new Vector3f(0, 35, 0), Vector3f.UNIT_Y);
        
        // Add lighting
        setupLighting();
        
        // Create world
        world = new World(assetManager, rootNode, 3); // Render distance of 3 chunks
        
        // Setup input handling
        inputHandler = new InputHandler(inputManager, cam, world);
        
        // Generate initial chunks around spawn
        world.updateChunks(cam.getLocation().x, cam.getLocation().z);
        
        System.out.println("=== Voxel Sandbox MVP ===");
        System.out.println("Controls:");
        System.out.println("  WASD - Move");
        System.out.println("  Space - Move up");
        System.out.println("  Left Shift - Move down");
        System.out.println("  Mouse - Look around");
        System.out.println("  Left Click - Remove block");
        System.out.println("  Right Click - Place block");
        System.out.println("  1/2/3 - Select block (Dirt/Grass/Stone)");
        System.out.println("  F3 - Toggle debug mode");
        System.out.println("========================");
    }
    
    private void setupLighting() {
        // Directional light (sun)
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.5f, -1f, -0.5f).normalizeLocal());
        sun.setColor(ColorRGBA.White.mult(1.2f));
        rootNode.addLight(sun);
        
        // Ambient light
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White.mult(0.5f));
        rootNode.addLight(ambient);
        
        // Sky color
        viewPort.setBackgroundColor(new ColorRGBA(0.5f, 0.7f, 1.0f, 1.0f));
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        // Update input
        inputHandler.update(tpf);
        
        // Update chunks based on camera position
        world.updateChunks(cam.getLocation().x, cam.getLocation().z);
    }
    
    @Override
    public void stop() {
        // Save world before closing
        if (world != null) {
            System.out.println("Saving world...");
            world.saveModifiedChunks();
            System.out.println("World saved.");
        }
        super.stop();
    }
}
