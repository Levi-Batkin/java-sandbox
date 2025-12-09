package com.sandbox.voxel.chunk;

import com.sandbox.voxel.block.BlockType;

/**
 * Represents a chunk of blocks in the world.
 * Default size is 16x64x16 blocks.
 */
public class Chunk {
    public static final int CHUNK_SIZE_X = 16;
    public static final int CHUNK_SIZE_Y = 64;
    public static final int CHUNK_SIZE_Z = 16;
    
    private final int chunkX;
    private final int chunkZ;
    private final BlockType[][][] blocks;
    private boolean isModified;
    
    public Chunk(int chunkX, int chunkZ) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.blocks = new BlockType[CHUNK_SIZE_X][CHUNK_SIZE_Y][CHUNK_SIZE_Z];
        this.isModified = false;
        
        // Initialize with air
        for (int x = 0; x < CHUNK_SIZE_X; x++) {
            for (int y = 0; y < CHUNK_SIZE_Y; y++) {
                for (int z = 0; z < CHUNK_SIZE_Z; z++) {
                    blocks[x][y][z] = BlockType.AIR;
                }
            }
        }
    }
    
    public BlockType getBlock(int x, int y, int z) {
        if (x < 0 || x >= CHUNK_SIZE_X || y < 0 || y >= CHUNK_SIZE_Y || z < 0 || z >= CHUNK_SIZE_Z) {
            return BlockType.AIR;
        }
        return blocks[x][y][z];
    }
    
    public void setBlock(int x, int y, int z, BlockType type) {
        if (x < 0 || x >= CHUNK_SIZE_X || y < 0 || y >= CHUNK_SIZE_Y || z < 0 || z >= CHUNK_SIZE_Z) {
            return;
        }
        blocks[x][y][z] = type;
        isModified = true;
    }
    
    public int getChunkX() {
        return chunkX;
    }
    
    public int getChunkZ() {
        return chunkZ;
    }
    
    public boolean isModified() {
        return isModified;
    }
    
    public void setModified(boolean modified) {
        this.isModified = modified;
    }
    
    public BlockType[][][] getBlocks() {
        return blocks;
    }
    
    /**
     * Get world position X from chunk coordinates
     */
    public int getWorldX() {
        return chunkX * CHUNK_SIZE_X;
    }
    
    /**
     * Get world position Z from chunk coordinates
     */
    public int getWorldZ() {
        return chunkZ * CHUNK_SIZE_Z;
    }
}
