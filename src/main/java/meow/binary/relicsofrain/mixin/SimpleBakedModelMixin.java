package meow.binary.relicsofrain.mixin;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(SimpleBakedModel.class)
public class SimpleBakedModelMixin {
    @Inject(method = "getRenderTypes(Lnet/minecraft/world/item/ItemStack;Z)Ljava/util/List;", at = @At("RETURN"), cancellable = true)
    public void injectCustomRenderType(ItemStack itemStack, boolean fabulous, CallbackInfoReturnable<List<RenderType>> cir) {

    }
}
