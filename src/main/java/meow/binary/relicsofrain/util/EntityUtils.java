//package meow.binary.relicsofrain.util;
//
//import net.minecraft.core.BlockPos;
//import net.minecraft.server.level.ServerLevel;
//import net.minecraft.world.level.block.state.BlockState;
//
//public class EntityUtils {
//    public static BlockPos findNearestSafePos(ServerLevel level, BlockPos startPos, int searchRadius) {
//        for (int radius = 0; radius <= searchRadius; radius++) {
//            for (int dx = -radius; dx <= radius; dx++) {
//                for (int dz = -radius; dz <= radius; dz++) {
//                    BlockPos checkPos = startPos.offset(dx, 0, dz);
//
//                    // Adjust the Y position to ensure it's on solid ground
//                    BlockPos safePos = findSafeYPosition(level, checkPos);
//
//                    if (safePos != null) {
//                        return safePos;
//                    }
//                }
//            }
//        }
//        return null; // No safe position found
//    }
//
//    /**
//     * Finds a safe Y position at a given horizontal position.
//     *
//     * @param level The server level (world).
//     * @param pos The position to check (horizontal coordinates).
//     * @return A safe BlockPos, or null if no safe Y position is found.
//     */
//    private static BlockPos findSafeYPosition(ServerLevel level, BlockPos pos) {
//        int minY = pos.getY()-10;
//        int maxY = pos.getY()+10;
//
//        for (int y = minY; y < maxY; y++) {
//            BlockPos currentPos = pos.atY(y);
//            BlockState below = level.getBlockState(currentPos.below());
//            BlockState base = level.getBlockState(currentPos);
//            BlockState above = level.getBlockState(currentPos.above());
//
//            // Check if the position is safe:
//            // 1. The block below must be solid
//            // 2. The current and above blocks must be air
//            if (below.isSolidRender(level, currentPos.below()) &&
//                    base.isAir() &&
//                    above.isAir()) {
//                return currentPos;
//            }
//        }
//
//        return null; // No safe Y position found
//    }
//}
