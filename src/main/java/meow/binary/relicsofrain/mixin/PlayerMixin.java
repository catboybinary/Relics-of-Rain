package meow.binary.relicsofrain.mixin;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Player.class)
public class PlayerMixin {
    @ModifyArg(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;setSprinting(Z)V"))
    private boolean sprinting(boolean par1) {
//        Player player = (Player) (Object) this;
//        ItemStack stack = EntityUtils.findEquippedCurio(player, ItemRegistry.KATANA_TRINKET.get());
//        if (stack.getItem() instanceof KatanaTrinketItem) {
//            return player.isSprinting();
//        }

        return par1;
    }

    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isSprinting()Z"))
    private boolean injected(Player instance) {
        Player player = (Player) (Object) this;
//        ItemStack stack = EntityUtils.findEquippedCurio(player, ItemRegistry.KATANA_TRINKET.get());
//        if (stack.getItem() instanceof KatanaTrinketItem) {
//            return false;
//        }

        return player.isSprinting();
    }
}
