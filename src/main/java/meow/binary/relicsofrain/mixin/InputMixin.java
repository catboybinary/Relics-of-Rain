package meow.binary.relicsofrain.mixin;

import net.minecraft.client.player.Input;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Input.class)
public class InputMixin {
    @Shadow
    public float forwardImpulse;

    @Shadow
    public float leftImpulse;

    @Inject(method = "hasForwardImpulse", at = @At("HEAD"), cancellable = true)
    private void hasEnoughImpulseToStartSprinting(CallbackInfoReturnable<Boolean> cir) {
//        if (!(Minecraft.getInstance().player instanceof LocalPlayer player)) {
//            return;
//        }
//        ItemStack stack = EntityUtils.findEquippedCurio(player, ItemRegistry.KATANA_TRINKET.get());
//        if (stack.getItem() instanceof KatanaTrinketItem) {
//            cir.setReturnValue(Math.abs(forwardImpulse) > 1.0E-5F || Math.abs(leftImpulse) > 1.0E-5F);
//            cir.cancel();
//        }
    }
}
