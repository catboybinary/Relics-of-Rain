package meow.binary.relicsofrain.mixin;

import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {
    @Shadow public Input input;

    @Inject(method = "hasEnoughImpulseToStartSprinting", at = @At("RETURN"), cancellable = true)
    public void hasImpulse(CallbackInfoReturnable<Boolean> cir) {
//        ItemStack stack = EntityUtils.findEquippedCurio((Entity) (Object) this, ItemRegistry.KATANA_TRINKET.get());
//        if (stack.getItem() instanceof KatanaTrinketItem relic) {
//            cir.setReturnValue(input.hasForwardImpulse());
//        }
    }
}
