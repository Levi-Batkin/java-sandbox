package com.sandbox.voxel.chunk;

import com.sandbox.voxel.block.BlockType;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for Chunk
 */
public class ChunkTest {
    
    @Test
    public void testChunkCreation() {
        Chunk chunk = new Chunk(0, 0);
        assertEquals(0, chunk.getChunkX());
        assertEquals(0, chunk.getChunkZ());
        assertFalse(chunk.isModified());
    }
    
    @Test
    public void testChunkInitializedWithAir() {
        Chunk chunk = new Chunk(0, 0);
        for (int x = 0; x < Chunk.CHUNK_SIZE_X; x++) {
            for (int y = 0; y < Chunk.CHUNK_SIZE_Y; y++) {
                for (int z = 0; z < Chunk.CHUNK_SIZE_Z; z++) {
                    assertEquals(BlockType.AIR, chunk.getBlock(x, y, z));
                }
            }
        }
    }
    
    @Test
    public void testSetAndGetBlock() {
        Chunk chunk = new Chunk(0, 0);
        chunk.setBlock(5, 10, 5, BlockType.STONE);
        assertEquals(BlockType.STONE, chunk.getBlock(5, 10, 5));
        assertTrue(chunk.isModified());
    }
    
    @Test
    public void testOutOfBoundsAccess() {
        Chunk chunk = new Chunk(0, 0);
        // Should return AIR for out of bounds
        assertEquals(BlockType.AIR, chunk.getBlock(-1, 0, 0));
        assertEquals(BlockType.AIR, chunk.getBlock(0, -1, 0));
        assertEquals(BlockType.AIR, chunk.getBlock(Chunk.CHUNK_SIZE_X, 0, 0));
        assertEquals(BlockType.AIR, chunk.getBlock(0, Chunk.CHUNK_SIZE_Y, 0));
        
        // Setting out of bounds should not crash
        chunk.setBlock(-1, 0, 0, BlockType.STONE);
        chunk.setBlock(Chunk.CHUNK_SIZE_X, 0, 0, BlockType.STONE);
    }
    
    @Test
    public void testWorldPosition() {
        Chunk chunk = new Chunk(2, 3);
        assertEquals(2 * Chunk.CHUNK_SIZE_X, chunk.getWorldX());
        assertEquals(3 * Chunk.CHUNK_SIZE_Z, chunk.getWorldZ());
    }
}
