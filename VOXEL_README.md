# Voxel Sandbox MVP

A 3D voxel sandbox game (Minecraft-like) built with jMonkeyEngine. Features procedurally generated terrain, block placement/removal, first-person camera controls, and world persistence.

![Voxel Sandbox](docs/screenshot.png)

## Features

### Core Features (MVP)
- ✅ **First-person camera** with mouse look controls
- ✅ **Chunk-based world** (configurable, default 16×64×16 blocks per chunk)
- ✅ **Procedural terrain generation** using Perlin noise
- ✅ **Block types**: Air, Dirt, Grass, Stone
- ✅ **Optimized rendering**: Only visible block faces are rendered (naive face culling)
- ✅ **Texture atlas** with placeholder CC0 textures
- ✅ **Block interaction**: Raycast-based selection with mouse clicks
- ✅ **Collision detection**: AABB collision prevents falling through blocks
- ✅ **World persistence**: Chunks are saved to disk in JSON format

### Controls
- **W/A/S/D**: Move forward/left/backward/right
- **Space**: Move up
- **Left Shift**: Move down
- **Mouse**: Look around
- **Left Click**: Remove block
- **Right Click**: Place block
- **1/2/3**: Select block type (Dirt/Grass/Stone)
- **F3**: Toggle debug mode (shows chunk boundaries)

## Requirements

- **Java 17 or higher** - Required to build and run the application
- **Operating System**: Windows, macOS, or Linux (any OS with Java support)

## Quick Start

### 1. Build the Project

```bash
./gradlew build
```

On Windows, use:
```cmd
gradlew.bat build
```

### 2. Run the Application

```bash
./gradlew run
```

On Windows:
```cmd
gradlew.bat run
```

The application will open a window showing the voxel world. Use the controls listed above to navigate and interact with blocks.

## Project Structure

```
src/main/java/com/sandbox/voxel/
├── Main.java                    # Application entry point
├── block/
│   └── BlockType.java          # Block type definitions
├── chunk/
│   └── Chunk.java              # Chunk data structure
├── world/
│   ├── World.java              # World manager
│   └── TerrainGenerator.java  # Procedural terrain generation
├── mesh/
│   └── ChunkMeshGenerator.java # Mesh generation with face culling
├── input/
│   └── InputHandler.java       # Input handling (keyboard/mouse)
├── save/
│   └── WorldSaveManager.java   # Save/load system
└── util/
    └── TextureGenerator.java   # Texture atlas generator

src/main/resources/
└── textures/
    └── blocks.png              # Block texture atlas (64×64, 4×4 grid)

src/test/java/                   # Unit tests
```

## World Persistence

Modified chunks are automatically saved to the `world/` directory when you exit the application. The save format is JSON for easy inspection and debugging.

To start a new world, simply delete the `world/` directory.

## Customizing Textures

The game uses a texture atlas located at `src/main/resources/textures/blocks.png`. This is a 64×64 PNG containing a 4×4 grid of 16×16 block textures.

### Texture Layout (indices 0-3):
- **0**: Dirt (brown)
- **1**: Grass top (green)
- **2**: Grass side (green-gray transition)
- **3**: Stone (gray)

### Replacing Textures:
1. Create your own 64×64 PNG texture atlas with 16×16 tiles
2. Replace `src/main/resources/textures/blocks.png`
3. Rebuild and run: `./gradlew run`

**Where to Find Textures:**
- OpenGameArt.org (CC0 and free licenses)
- Kenney.nl (CC0 textures and game assets)
- Create your own using GIMP, Aseprite, or any image editor

**Note:** Keep textures small (16×16 or 32×32) for that classic voxel aesthetic!

## Configuration

You can modify these constants in the code:

### Chunk Size
In `Chunk.java`:
```java
public static final int CHUNK_SIZE_X = 16;
public static final int CHUNK_SIZE_Y = 64;
public static final int CHUNK_SIZE_Z = 16;
```

### Render Distance
In `Main.java` (simpleInitApp method):
```java
world = new World(assetManager, rootNode, 3); // 3 = render distance in chunks
```

### Terrain Generation
In `TerrainGenerator.java`:
```java
private static final int SEA_LEVEL = 32;
private static final double TERRAIN_SCALE = 0.02;
private static final int TERRAIN_HEIGHT_MULTIPLIER = 20;
```

## Development

### Running Tests

```bash
./gradlew test
```

Test reports are generated in `build/reports/tests/test/index.html`.

### Building a Distribution

```bash
./gradlew build
```

The built JAR will be in `build/libs/voxel-sandbox-1.0.0-SNAPSHOT.jar`.

### IDE Setup

The project uses Gradle, so most IDEs can import it directly:

- **IntelliJ IDEA**: File → Open → select build.gradle
- **Eclipse**: File → Import → Existing Gradle Project
- **VS Code**: Open the folder (Java Extension Pack recommended)

## Architecture

### Chunk System
The world is divided into chunks for efficient rendering and loading. Each chunk is a 16×64×16 grid of blocks. Only chunks near the player are loaded and rendered.

### Mesh Generation
Chunks are converted to meshes using naive face culling - only block faces adjacent to air or chunk boundaries are added to the mesh. This significantly reduces the number of triangles rendered.

### Collision Detection
Simple AABB (Axis-Aligned Bounding Box) collision detection prevents the camera from entering solid blocks. The player's bounding box is checked against blocks at foot and head level.

### Terrain Generation
Terrain is generated using Perlin noise (via the Joise library). The same seed produces the same terrain, making worlds deterministic and reproducible.

## Next Steps & Improvements

This is an MVP (Minimum Viable Product). Potential improvements include:

### Performance
- [ ] Greedy meshing for fewer triangles
- [ ] Chunk loading/unloading based on distance
- [ ] Multithreaded chunk generation and meshing
- [ ] Frustum culling for chunks

### Features
- [ ] More block types (wood, sand, water, ore, etc.)
- [ ] Inventory system
- [ ] Breaking animation
- [ ] Block physics (gravity for sand/gravel)
- [ ] Lighting system (sunlight and block light)
- [ ] Day/night cycle
- [ ] Caves and underground structures
- [ ] Trees and vegetation

### Graphics
- [ ] Ambient occlusion
- [ ] Better textures and texture packs
- [ ] Particle effects
- [ ] Water rendering with transparency
- [ ] Sky rendering with clouds

### Gameplay
- [ ] Creative/Survival modes
- [ ] Health and hunger
- [ ] Mobs and entities
- [ ] Crafting system
- [ ] Multiplayer support

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Dependencies

- **jMonkeyEngine 3.6.1** - 3D game engine (BSD license)
- **Joise 1.1.0** - Noise generation library (Apache 2.0)
- **Gson 2.10.1** - JSON serialization (Apache 2.0)
- **JUnit 4.13.2** - Unit testing (EPL 1.0)

## Credits

- Built with [jMonkeyEngine](https://jmonkeyengine.org/)
- Noise generation using [Joise](https://github.com/SudoPlayGames/Joise)
- Placeholder textures are CC0 (public domain)

## Troubleshooting

### "Java version not supported"
Make sure you have Java 17 or higher installed. Check with:
```bash
java -version
```

### Black screen / No rendering
This may be a graphics driver issue. Try:
1. Updating your graphics drivers
2. Running in windowed mode (default)
3. Disabling anti-aliasing in `Main.java`

### Performance issues
- Reduce render distance in `Main.java`
- Close other applications
- Ensure you're using a dedicated GPU (if available)

### World not saving
- Check that the application has write permissions
- Ensure the `world/` directory isn't read-only
- Check console output for error messages

## Contributing

This is a sandbox/learning project, but contributions are welcome! Feel free to:
- Report bugs via GitHub issues
- Submit pull requests with improvements
- Share your texture packs or mods
- Fork and extend for your own projects

---

**Have fun building!** 🎮⛏️
