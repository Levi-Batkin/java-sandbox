package com.sandbox.voxel.input;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.*;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.sandbox.voxel.block.BlockType;
import com.sandbox.voxel.world.World;

/**
 * Handles player input for movement and block interaction.
 */
public class InputHandler implements ActionListener, AnalogListener {
    private final InputManager inputManager;
    private final Camera camera;
    private final World world;
    private boolean forward, backward, left, right, up, down;
    private boolean debugMode = false;
    private final float moveSpeed = 10.0f;
    
    // Block interaction
    private BlockType selectedBlock = BlockType.DIRT;
    private final float maxReachDistance = 10.0f;
    
    public InputHandler(InputManager inputManager, Camera camera, World world) {
        this.inputManager = inputManager;
        this.camera = camera;
        this.world = world;
        
        setupKeys();
    }
    
    private void setupKeys() {
        // Movement keys
        inputManager.addMapping("Forward", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Backward", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_LSHIFT));
        
        // Block interaction
        inputManager.addMapping("PlaceBlock", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        inputManager.addMapping("RemoveBlock", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        
        // Debug toggle
        inputManager.addMapping("ToggleDebug", new KeyTrigger(KeyInput.KEY_F3));
        
        // Block selection (1-3 keys)
        inputManager.addMapping("SelectDirt", new KeyTrigger(KeyInput.KEY_1));
        inputManager.addMapping("SelectGrass", new KeyTrigger(KeyInput.KEY_2));
        inputManager.addMapping("SelectStone", new KeyTrigger(KeyInput.KEY_3));
        
        // Add listeners
        inputManager.addListener(this, "Forward", "Backward", "Left", "Right", "Up", "Down",
                                "PlaceBlock", "RemoveBlock", "ToggleDebug",
                                "SelectDirt", "SelectGrass", "SelectStone");
    }
    
    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        switch (name) {
            case "Forward":
                forward = isPressed;
                break;
            case "Backward":
                backward = isPressed;
                break;
            case "Left":
                left = isPressed;
                break;
            case "Right":
                right = isPressed;
                break;
            case "Up":
                up = isPressed;
                break;
            case "Down":
                down = isPressed;
                break;
            case "PlaceBlock":
                if (isPressed) {
                    placeBlock();
                }
                break;
            case "RemoveBlock":
                if (isPressed) {
                    removeBlock();
                }
                break;
            case "ToggleDebug":
                if (isPressed) {
                    debugMode = !debugMode;
                    System.out.println("Debug mode: " + debugMode);
                }
                break;
            case "SelectDirt":
                if (isPressed) {
                    selectedBlock = BlockType.DIRT;
                    System.out.println("Selected: DIRT");
                }
                break;
            case "SelectGrass":
                if (isPressed) {
                    selectedBlock = BlockType.GRASS;
                    System.out.println("Selected: GRASS");
                }
                break;
            case "SelectStone":
                if (isPressed) {
                    selectedBlock = BlockType.STONE;
                    System.out.println("Selected: STONE");
                }
                break;
        }
    }
    
    @Override
    public void onAnalog(String name, float value, float tpf) {
        // Not used for digital inputs
    }
    
    /**
     * Update player movement
     */
    public void update(float tpf) {
        Vector3f camDir = camera.getDirection().clone();
        Vector3f camLeft = camera.getLeft().clone();
        
        Vector3f walkDirection = new Vector3f();
        
        if (forward) {
            walkDirection.addLocal(camDir.mult(moveSpeed * tpf));
        }
        if (backward) {
            walkDirection.addLocal(camDir.mult(-moveSpeed * tpf));
        }
        if (left) {
            walkDirection.addLocal(camLeft.mult(moveSpeed * tpf));
        }
        if (right) {
            walkDirection.addLocal(camLeft.mult(-moveSpeed * tpf));
        }
        if (up) {
            walkDirection.addLocal(0, moveSpeed * tpf, 0);
        }
        if (down) {
            walkDirection.addLocal(0, -moveSpeed * tpf, 0);
        }
        
        // Apply movement with collision check
        Vector3f newPos = camera.getLocation().add(walkDirection);
        
        // Simple collision detection
        if (isPositionValid(newPos)) {
            camera.setLocation(newPos);
        }
    }
    
    /**
     * Check if position is valid (not inside solid block)
     */
    private boolean isPositionValid(Vector3f pos) {
        // Check player bounding box (simplified)
        int x = (int) Math.floor(pos.x);
        int y = (int) Math.floor(pos.y);
        int z = (int) Math.floor(pos.z);
        
        // Check block at foot level and head level
        BlockType blockFoot = world.getBlock(x, y, z);
        BlockType blockHead = world.getBlock(x, y + 1, z);
        
        return !blockFoot.isSolid() && !blockHead.isSolid();
    }
    
    /**
     * Place a block in front of the player
     */
    private void placeBlock() {
        Ray ray = new Ray(camera.getLocation(), camera.getDirection());
        CollisionResults results = new CollisionResults();
        world.getWorldNode().collideWith(ray, results);
        
        if (results.size() > 0) {
            CollisionResult closest = results.getClosestCollision();
            if (closest.getDistance() <= maxReachDistance) {
                Vector3f hitPoint = closest.getContactPoint();
                Vector3f normal = closest.getContactNormal();
                
                // Place block adjacent to hit surface
                Vector3f placePos = hitPoint.add(normal.mult(0.5f));
                int x = (int) Math.floor(placePos.x);
                int y = (int) Math.floor(placePos.y);
                int z = (int) Math.floor(placePos.z);
                
                world.setBlock(x, y, z, selectedBlock);
            }
        }
    }
    
    /**
     * Remove block the player is looking at
     */
    private void removeBlock() {
        Ray ray = new Ray(camera.getLocation(), camera.getDirection());
        CollisionResults results = new CollisionResults();
        world.getWorldNode().collideWith(ray, results);
        
        if (results.size() > 0) {
            CollisionResult closest = results.getClosestCollision();
            if (closest.getDistance() <= maxReachDistance) {
                Vector3f hitPoint = closest.getContactPoint();
                Vector3f normal = closest.getContactNormal();
                
                // Remove block at hit surface
                Vector3f blockPos = hitPoint.subtract(normal.mult(0.5f));
                int x = (int) Math.floor(blockPos.x);
                int y = (int) Math.floor(blockPos.y);
                int z = (int) Math.floor(blockPos.z);
                
                world.setBlock(x, y, z, BlockType.AIR);
            }
        }
    }
    
    public boolean isDebugMode() {
        return debugMode;
    }
}
