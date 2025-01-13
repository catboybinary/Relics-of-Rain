package meow.binary.relicsofrain.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.client.renderer.RenderStateShard.EQUAL_DEPTH_TEST;
import static net.minecraft.client.renderer.RenderStateShard.OUTLINE_TARGET;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    @Inject(method = "render", at = @At("HEAD"))
    public void changeColor(ItemStack itemStack, ItemDisplayContext displayContext, boolean leftHand, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, BakedModel p_model, CallbackInfo ci) {
        TextColor color = itemStack.getRarity().getStyleModifier().apply(Style.EMPTY).getColor();
        int icolor = 0xffffff;
        if (color != null) icolor = color.getValue();
        Minecraft.getInstance().renderBuffers().outlineBufferSource().setColor(FastColor.ARGB32.red(icolor), FastColor.ARGB32.green(icolor), FastColor.ARGB32.blue(icolor), 127);
        //Minecraft.getInstance().levelRenderer.requestOutlineEffect();

    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V"))
    public void stopBlend(ItemStack itemStack, ItemDisplayContext displayContext, boolean leftHand, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, BakedModel p_model, CallbackInfo ci) {
        //Minecraft.getInstance().renderBuffers().outlineBufferSource().endOutlineBatch();
        Minecraft.getInstance().renderBuffers().outlineBufferSource().setColor(255, 255, 255, 255);
    }

    @ModifyVariable(method = "render", at = @At(value = "HEAD"), ordinal = 0, argsOnly = true)
    public MultiBufferSource test(MultiBufferSource value) {
        return value;
    }
}


