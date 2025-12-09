package com.sandbox.voxel.save;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sandbox.voxel.block.BlockType;
import com.sandbox.voxel.chunk.Chunk;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Manages saving and loading chunks to/from disk in JSON format.
 */
public class WorldSaveManager {
    private final String worldDir;
    private final Gson gson;
    
    public WorldSaveManager(String worldDir) {
        this.worldDir = worldDir;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        
        // Create world directory if it doesn't exist
        try {
            Files.createDirectories(Paths.get(worldDir));
        } catch (IOException e) {
            System.err.println("Failed to create world directory: " + e.getMessage());
        }
    }
    
    /**
     * Save a chunk to disk
     */
    public void saveChunk(Chunk chunk) {
        String filename = getChunkFilename(chunk.getChunkX(), chunk.getChunkZ());
        Path filePath = Paths.get(worldDir, filename);
        
        try {
            ChunkData data = new ChunkData();
            data.chunkX = chunk.getChunkX();
            data.chunkZ = chunk.getChunkZ();
            data.blocks = new int[Chunk.CHUNK_SIZE_X][Chunk.CHUNK_SIZE_Y][Chunk.CHUNK_SIZE_Z];
            
            // Convert blocks to IDs
            for (int x = 0; x < Chunk.CHUNK_SIZE_X; x++) {
                for (int y = 0; y < Chunk.CHUNK_SIZE_Y; y++) {
                    for (int z = 0; z < Chunk.CHUNK_SIZE_Z; z++) {
                        data.blocks[x][y][z] = chunk.getBlock(x, y, z).getId();
                    }
                }
            }
            
            String json = gson.toJson(data);
            Files.writeString(filePath, json);
            
        } catch (IOException e) {
            System.err.println("Failed to save chunk " + chunk.getChunkX() + "," + 
                             chunk.getChunkZ() + ": " + e.getMessage());
        }
    }
    
    /**
     * Load a chunk from disk
     */
    public Chunk loadChunk(int chunkX, int chunkZ) {
        String filename = getChunkFilename(chunkX, chunkZ);
        Path filePath = Paths.get(worldDir, filename);
        
        if (!Files.exists(filePath)) {
            return null;
        }
        
        try {
            String json = Files.readString(filePath);
            ChunkData data = gson.fromJson(json, ChunkData.class);
            
            Chunk chunk = new Chunk(chunkX, chunkZ);
            
            // Convert IDs back to blocks
            for (int x = 0; x < Chunk.CHUNK_SIZE_X; x++) {
                for (int y = 0; y < Chunk.CHUNK_SIZE_Y; y++) {
                    for (int z = 0; z < Chunk.CHUNK_SIZE_Z; z++) {
                        BlockType type = BlockType.fromId(data.blocks[x][y][z]);
                        chunk.setBlock(x, y, z, type);
                    }
                }
            }
            
            chunk.setModified(false);
            return chunk;
            
        } catch (IOException e) {
            System.err.println("Failed to load chunk " + chunkX + "," + chunkZ + ": " + e.getMessage());
            return null;
        }
    }
    
    private String getChunkFilename(int chunkX, int chunkZ) {
        return "chunk_" + chunkX + "_" + chunkZ + ".json";
    }
    
    /**
     * Data class for chunk serialization
     */
    private static class ChunkData {
        int chunkX;
        int chunkZ;
        int[][][] blocks;
    }
}
