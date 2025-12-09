package com.sandbox.voxel.block;

/**
 * Represents different block types in the voxel world.
 * Each block has a unique ID and texture information.
 */
public enum BlockType {
    AIR(0, false, -1, -1, -1, -1, -1, -1),
    DIRT(1, true, 0, 0, 0, 0, 0, 0),
    GRASS(2, true, 1, 0, 2, 2, 2, 2),
    STONE(3, true, 3, 3, 3, 3, 3, 3);
    
    private final int id;
    private final boolean isSolid;
    private final int topTexture;
    private final int bottomTexture;
    private final int northTexture;
    private final int southTexture;
    private final int eastTexture;
    private final int westTexture;
    
    BlockType(int id, boolean isSolid, int topTexture, int bottomTexture, 
              int northTexture, int southTexture, int eastTexture, int westTexture) {
        this.id = id;
        this.isSolid = isSolid;
        this.topTexture = topTexture;
        this.bottomTexture = bottomTexture;
        this.northTexture = northTexture;
        this.southTexture = southTexture;
        this.eastTexture = eastTexture;
        this.westTexture = westTexture;
    }
    
    public int getId() {
        return id;
    }
    
    public boolean isSolid() {
        return isSolid;
    }
    
    public int getTextureIndex(BlockFace face) {
        switch (face) {
            case TOP: return topTexture;
            case BOTTOM: return bottomTexture;
            case NORTH: return northTexture;
            case SOUTH: return southTexture;
            case EAST: return eastTexture;
            case WEST: return westTexture;
            default: return 0;
        }
    }
    
    public static BlockType fromId(int id) {
        for (BlockType type : values()) {
            if (type.id == id) {
                return type;
            }
        }
        return AIR;
    }
    
    public enum BlockFace {
        TOP, BOTTOM, NORTH, SOUTH, EAST, WEST
    }
}
