package com.sandbox.voxel.block;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for BlockType
 */
public class BlockTypeTest {
    
    @Test
    public void testBlockTypeIds() {
        assertEquals(0, BlockType.AIR.getId());
        assertEquals(1, BlockType.DIRT.getId());
        assertEquals(2, BlockType.GRASS.getId());
        assertEquals(3, BlockType.STONE.getId());
    }
    
    @Test
    public void testBlockSolidity() {
        assertFalse(BlockType.AIR.isSolid());
        assertTrue(BlockType.DIRT.isSolid());
        assertTrue(BlockType.GRASS.isSolid());
        assertTrue(BlockType.STONE.isSolid());
    }
    
    @Test
    public void testFromId() {
        assertEquals(BlockType.AIR, BlockType.fromId(0));
        assertEquals(BlockType.DIRT, BlockType.fromId(1));
        assertEquals(BlockType.GRASS, BlockType.fromId(2));
        assertEquals(BlockType.STONE, BlockType.fromId(3));
        assertEquals(BlockType.AIR, BlockType.fromId(999)); // Invalid ID
    }
    
    @Test
    public void testTextureIndices() {
        // Dirt should have same texture on all sides
        assertEquals(0, BlockType.DIRT.getTextureIndex(BlockType.BlockFace.TOP));
        assertEquals(0, BlockType.DIRT.getTextureIndex(BlockType.BlockFace.BOTTOM));
        
        // Grass should have different textures for top/bottom/sides
        assertEquals(1, BlockType.GRASS.getTextureIndex(BlockType.BlockFace.TOP));
        assertEquals(0, BlockType.GRASS.getTextureIndex(BlockType.BlockFace.BOTTOM));
        assertEquals(2, BlockType.GRASS.getTextureIndex(BlockType.BlockFace.NORTH));
    }
}
