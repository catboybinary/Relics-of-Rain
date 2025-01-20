package meow.binary.relicsofrain.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.SpawnerBlock;
import net.minecraft.world.level.chunk.ChunkAccess;

public class DungeonFinder {
    /**
     * Finds the nearest dungeon (monster room) to a given position.
     *
     * @param level        The server level (world).
     * @param startPos     The starting position.
     * @param searchRadius The radius (in chunks) to search for dungeons.
     * @return The position of the nearest dungeon, or null if none are found.
     */
    public static BlockPos findNearestDungeon(ServerLevel level, BlockPos startPos, int searchRadius) {
        int chunkX = startPos.getX() >> 4;
        int chunkZ = startPos.getZ() >> 4;

        for (int dx = 0; dx <= searchRadius; dx = dx > 0 ? dx * -1 : dx * -1 + 1) {
            for (int dz = 0; dz <= searchRadius; dz = dz > 0 ? dz * -1 : dz * -1 + 1) {
                int currentChunkX = chunkX + dx;
                int currentChunkZ = chunkZ + dz;

                System.out.println(currentChunkX + "; " + currentChunkZ);
                ChunkAccess chunk = level.getChunk(currentChunkX, currentChunkZ);
                BlockPos dungeonPos = findDungeonInChunk(level, chunk);

                if (dungeonPos != null) {
                    return dungeonPos;
                }
            }
        }

        return null; // No dungeon found within the search radius
    }

    /**
     * Scans a chunk for the presence of a dungeon.
     *
     * @param level The server level (world).
     * @param chunk The chunk to scan.
     * @return The position of a dungeon, or null if none is found.
     */
    private static BlockPos findDungeonInChunk(ServerLevel level, ChunkAccess chunk) {
        for (BlockPos pos : chunk.getBlockEntitiesPos()) {
            // Check if the block at this position is a dungeon spawner
            if (isDungeon(level, pos)) {
                return pos;
            }
        }
        return null;
    }

    /**
     * Determines if a position corresponds to a dungeon (monster room).
     *
     * @param level The server level (world).
     * @param pos   The position to check.
     * @return True if the position is part of a dungeon, false otherwise.
     */
    private static boolean isDungeon(ServerLevel level, BlockPos pos) {
        // Check for a mob spawner block
        return level.getBlockState(pos).getBlock() instanceof SpawnerBlock;
    }
}