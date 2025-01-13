package meow.binary.relicsofrain.util;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import meow.binary.relicsofrain.RelicsOfRain;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.NeoForgeRenderTypes;
import net.neoforged.neoforge.common.NeoForge;
import org.joml.Vector3f;

import java.util.List;
import java.util.function.Function;

public class RenderUtils {
    public static final Function<ResourceLocation, RenderType> TYPE = Util.memoize(rl -> RenderType.create("icosahedron",
            DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
            VertexFormat.Mode.TRIANGLES,
            1536, false, false,
            RenderType.CompositeState.builder()
                    .setTransparencyState(RenderStateShard.ADDITIVE_TRANSPARENCY)
                    .setShaderState(RenderStateShard.POSITION_COLOR_TEX_LIGHTMAP_SHADER)
                    .setOutputState(RenderStateShard.MAIN_TARGET)
                    .setColorLogicState(RenderStateShard.NO_COLOR_LOGIC)
                    .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                    .setCullState(RenderStateShard.NO_CULL)
                    .setLightmapState(RenderStateShard.LIGHTMAP)
                    .setOverlayState(RenderStateShard.OVERLAY)
                    .setWriteMaskState(new RenderStateShard.WriteMaskStateShard(true, false))
                    .setTextureState(new RenderStateShard.TextureStateShard(rl, false, false))
                    .createCompositeState(false))
    );

    public static RenderType getRenderType(ResourceLocation rl) {
        return TYPE.apply(rl);
    }


    public static final List<Vector3f> icosahedronVertices = List.of(
            new Vector3f(0.8506508f, 0.5257311f, 0f),            // 0
            new Vector3f(0.000000101405476f, 0.8506507f, -0.525731f),     // 1
            new Vector3f(0.000000101405476f, 0.8506506f, 0.525731f),     // 2
            new Vector3f(0.5257309f, -0.00000006267203f, -0.85065067f),   // 3
            new Vector3f(0.52573115f, -0.00000006267203f, 0.85065067f),   // 4
            new Vector3f(0.8506508f, -0.5257311f, 0f),            // 5
            new Vector3f(-0.52573115f, 0.00000006267203f, -0.85065067f),   // 6
            new Vector3f(-0.8506508f, 0.5257311f, 0f),            // 7
            new Vector3f(-0.5257309f, 0.00000006267203f, 0.85065067f),   // 8
            new Vector3f(-0.000000101405476f, -0.8506506f, -0.525731f),     // 9
            new Vector3f(-0.000000101405476f, -0.8506507f, 0.525731f),     // 10
            new Vector3f(-0.8506508f, -0.5257311f, 0f)             // 11
    );

    public static final int[] icosahedronTriangleIndicies = {
            0, 1, 2,
            0, 3, 1,
            0, 2, 4,
            3, 0, 5,
            0, 4, 5,
            1, 3, 6,
            1, 7, 2,
            7, 1, 6,
            4, 2, 8,
            7, 8, 2,
            9, 3, 5,
            6, 3, 9,
            5, 4, 10,
            4, 8, 10,
            9, 5, 10,
            7, 6, 11,
            7, 11, 8,
            11, 6, 9,
            8, 11, 10,
            10, 11, 9
    };

    /**
     * Calculates the normal vector of a triangle given its three vertices.
     *
     * @param v1 The first vertex of the triangle.
     * @param v2 The second vertex of the triangle.
     * @param v3 The third vertex of the triangle.
     * @return A normalized Vector3f representing the triangle's normal vector.
     */
    public static Vector3f calculateNormal(Vector3f v1, Vector3f v2, Vector3f v3) {
        // Compute the vectors representing two edges of the triangle
        Vector3f edge1 = new Vector3f(v2);
        edge1.sub(v1); // edge1 = v2 - v1

        Vector3f edge2 = new Vector3f(v3);
        edge2.sub(v1); // edge2 = v3 - v1

        // Compute the cross product of the two edges to get the normal vector
        Vector3f normal = new Vector3f(edge1);
        normal.cross(edge2); // normal = edge1 x edge2

        // Normalize the resulting vector to ensure it has a length of 1
        normal.normalize();

        return normal;
    }
}
