package com.sandbox.voxel.mesh;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import com.sandbox.voxel.block.BlockType;
import com.sandbox.voxel.block.BlockType.BlockFace;
import com.sandbox.voxel.chunk.Chunk;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Generates meshes for chunks using naive face culling.
 * Only generates faces for blocks that are adjacent to air or chunk boundaries.
 */
public class ChunkMeshGenerator {
    private static final float BLOCK_SIZE = 1.0f;
    private static final int TEXTURE_ATLAS_SIZE = 4; // 4x4 texture atlas
    private static final float TEXTURE_UNIT = 1.0f / TEXTURE_ATLAS_SIZE;
    
    /**
     * Generate mesh for a chunk
     */
    public static Mesh generateMesh(Chunk chunk, Chunk northChunk, Chunk southChunk, 
                                   Chunk eastChunk, Chunk westChunk) {
        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2f> texCoords = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        
        for (int x = 0; x < Chunk.CHUNK_SIZE_X; x++) {
            for (int y = 0; y < Chunk.CHUNK_SIZE_Y; y++) {
                for (int z = 0; z < Chunk.CHUNK_SIZE_Z; z++) {
                    BlockType blockType = chunk.getBlock(x, y, z);
                    if (blockType == BlockType.AIR || !blockType.isSolid()) {
                        continue;
                    }
                    
                    // Check each face
                    // Top face (Y+)
                    if (shouldRenderFace(chunk, x, y + 1, z, null, null, null, null)) {
                        addFace(vertices, texCoords, normals, indices, 
                               x, y, z, BlockFace.TOP, blockType);
                    }
                    
                    // Bottom face (Y-)
                    if (shouldRenderFace(chunk, x, y - 1, z, null, null, null, null)) {
                        addFace(vertices, texCoords, normals, indices, 
                               x, y, z, BlockFace.BOTTOM, blockType);
                    }
                    
                    // North face (Z-)
                    if (shouldRenderFace(chunk, x, y, z - 1, northChunk, null, null, null)) {
                        addFace(vertices, texCoords, normals, indices, 
                               x, y, z, BlockFace.NORTH, blockType);
                    }
                    
                    // South face (Z+)
                    if (shouldRenderFace(chunk, x, y, z + 1, null, southChunk, null, null)) {
                        addFace(vertices, texCoords, normals, indices, 
                               x, y, z, BlockFace.SOUTH, blockType);
                    }
                    
                    // East face (X+)
                    if (shouldRenderFace(chunk, x + 1, y, z, null, null, eastChunk, null)) {
                        addFace(vertices, texCoords, normals, indices, 
                               x, y, z, BlockFace.EAST, blockType);
                    }
                    
                    // West face (X-)
                    if (shouldRenderFace(chunk, x - 1, y, z, null, null, null, westChunk)) {
                        addFace(vertices, texCoords, normals, indices, 
                               x, y, z, BlockFace.WEST, blockType);
                    }
                }
            }
        }
        
        if (vertices.isEmpty()) {
            return null;
        }
        
        // Create mesh
        Mesh mesh = new Mesh();
        
        // Convert lists to buffers
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.toArray(new Vector3f[0]));
        FloatBuffer texCoordBuffer = BufferUtils.createFloatBuffer(texCoords.toArray(new Vector2f[0]));
        FloatBuffer normalBuffer = BufferUtils.createFloatBuffer(normals.toArray(new Vector3f[0]));
        IntBuffer indexBuffer = BufferUtils.createIntBuffer(indices.stream().mapToInt(i -> i).toArray());
        
        mesh.setBuffer(VertexBuffer.Type.Position, 3, vertexBuffer);
        mesh.setBuffer(VertexBuffer.Type.TexCoord, 2, texCoordBuffer);
        mesh.setBuffer(VertexBuffer.Type.Normal, 3, normalBuffer);
        mesh.setBuffer(VertexBuffer.Type.Index, 3, indexBuffer);
        
        mesh.updateBound();
        
        return mesh;
    }
    
    private static boolean shouldRenderFace(Chunk chunk, int x, int y, int z, 
                                           Chunk northChunk, Chunk southChunk,
                                           Chunk eastChunk, Chunk westChunk) {
        // Check within chunk
        if (x >= 0 && x < Chunk.CHUNK_SIZE_X && 
            y >= 0 && y < Chunk.CHUNK_SIZE_Y && 
            z >= 0 && z < Chunk.CHUNK_SIZE_Z) {
            BlockType neighbor = chunk.getBlock(x, y, z);
            return neighbor == BlockType.AIR || !neighbor.isSolid();
        }
        
        // Check neighboring chunks
        if (z < 0 && northChunk != null) {
            BlockType neighbor = northChunk.getBlock(x, y, Chunk.CHUNK_SIZE_Z - 1);
            return neighbor == BlockType.AIR || !neighbor.isSolid();
        }
        if (z >= Chunk.CHUNK_SIZE_Z && southChunk != null) {
            BlockType neighbor = southChunk.getBlock(x, y, 0);
            return neighbor == BlockType.AIR || !neighbor.isSolid();
        }
        if (x >= Chunk.CHUNK_SIZE_X && eastChunk != null) {
            BlockType neighbor = eastChunk.getBlock(0, y, z);
            return neighbor == BlockType.AIR || !neighbor.isSolid();
        }
        if (x < 0 && westChunk != null) {
            BlockType neighbor = westChunk.getBlock(Chunk.CHUNK_SIZE_X - 1, y, z);
            return neighbor == BlockType.AIR || !neighbor.isSolid();
        }
        
        // Render face at chunk boundary if no neighbor
        return true;
    }
    
    private static void addFace(List<Vector3f> vertices, List<Vector2f> texCoords, 
                               List<Vector3f> normals, List<Integer> indices,
                               int x, int y, int z, BlockFace face, BlockType blockType) {
        int startIndex = vertices.size();
        float x0 = x * BLOCK_SIZE;
        float y0 = y * BLOCK_SIZE;
        float z0 = z * BLOCK_SIZE;
        float x1 = (x + 1) * BLOCK_SIZE;
        float y1 = (y + 1) * BLOCK_SIZE;
        float z1 = (z + 1) * BLOCK_SIZE;
        
        // Get texture coordinates
        int texIndex = blockType.getTextureIndex(face);
        int texX = texIndex % TEXTURE_ATLAS_SIZE;
        int texY = texIndex / TEXTURE_ATLAS_SIZE;
        float u0 = texX * TEXTURE_UNIT;
        float v0 = texY * TEXTURE_UNIT;
        float u1 = u0 + TEXTURE_UNIT;
        float v1 = v0 + TEXTURE_UNIT;
        
        switch (face) {
            case TOP:
                vertices.add(new Vector3f(x0, y1, z0));
                vertices.add(new Vector3f(x1, y1, z0));
                vertices.add(new Vector3f(x1, y1, z1));
                vertices.add(new Vector3f(x0, y1, z1));
                normals.add(new Vector3f(0, 1, 0));
                normals.add(new Vector3f(0, 1, 0));
                normals.add(new Vector3f(0, 1, 0));
                normals.add(new Vector3f(0, 1, 0));
                break;
                
            case BOTTOM:
                vertices.add(new Vector3f(x0, y0, z1));
                vertices.add(new Vector3f(x1, y0, z1));
                vertices.add(new Vector3f(x1, y0, z0));
                vertices.add(new Vector3f(x0, y0, z0));
                normals.add(new Vector3f(0, -1, 0));
                normals.add(new Vector3f(0, -1, 0));
                normals.add(new Vector3f(0, -1, 0));
                normals.add(new Vector3f(0, -1, 0));
                break;
                
            case NORTH:
                vertices.add(new Vector3f(x1, y0, z0));
                vertices.add(new Vector3f(x0, y0, z0));
                vertices.add(new Vector3f(x0, y1, z0));
                vertices.add(new Vector3f(x1, y1, z0));
                normals.add(new Vector3f(0, 0, -1));
                normals.add(new Vector3f(0, 0, -1));
                normals.add(new Vector3f(0, 0, -1));
                normals.add(new Vector3f(0, 0, -1));
                break;
                
            case SOUTH:
                vertices.add(new Vector3f(x0, y0, z1));
                vertices.add(new Vector3f(x1, y0, z1));
                vertices.add(new Vector3f(x1, y1, z1));
                vertices.add(new Vector3f(x0, y1, z1));
                normals.add(new Vector3f(0, 0, 1));
                normals.add(new Vector3f(0, 0, 1));
                normals.add(new Vector3f(0, 0, 1));
                normals.add(new Vector3f(0, 0, 1));
                break;
                
            case EAST:
                vertices.add(new Vector3f(x1, y0, z1));
                vertices.add(new Vector3f(x1, y0, z0));
                vertices.add(new Vector3f(x1, y1, z0));
                vertices.add(new Vector3f(x1, y1, z1));
                normals.add(new Vector3f(1, 0, 0));
                normals.add(new Vector3f(1, 0, 0));
                normals.add(new Vector3f(1, 0, 0));
                normals.add(new Vector3f(1, 0, 0));
                break;
                
            case WEST:
                vertices.add(new Vector3f(x0, y0, z0));
                vertices.add(new Vector3f(x0, y0, z1));
                vertices.add(new Vector3f(x0, y1, z1));
                vertices.add(new Vector3f(x0, y1, z0));
                normals.add(new Vector3f(-1, 0, 0));
                normals.add(new Vector3f(-1, 0, 0));
                normals.add(new Vector3f(-1, 0, 0));
                normals.add(new Vector3f(-1, 0, 0));
                break;
        }
        
        // Add texture coordinates
        texCoords.add(new Vector2f(u0, v1));
        texCoords.add(new Vector2f(u1, v1));
        texCoords.add(new Vector2f(u1, v0));
        texCoords.add(new Vector2f(u0, v0));
        
        // Add indices (two triangles per face)
        indices.add(startIndex);
        indices.add(startIndex + 1);
        indices.add(startIndex + 2);
        
        indices.add(startIndex);
        indices.add(startIndex + 2);
        indices.add(startIndex + 3);
    }
}
