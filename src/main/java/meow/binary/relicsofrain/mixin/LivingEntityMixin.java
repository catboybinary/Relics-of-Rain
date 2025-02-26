package meow.binary.relicsofrain.mixin;

import it.hurts.sskirillss.relics.utils.EntityUtils;
import meow.binary.relicsofrain.item.relic.KatanaTrinketItem;
import meow.binary.relicsofrain.registry.ItemRegistry;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @ModifyArg(method = "jumpFromGround", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"))
    private Vec3 redirected(Vec3 vec3) {
        Entity self = (Entity) (Object) this;
        ItemStack stack = EntityUtils.findEquippedCurio(self, ItemRegistry.KATANA_TRINKET.get());
        if (!(stack.getItem() instanceof KatanaTrinketItem relic)
                || !(self instanceof LocalPlayer player)
        ) return vec3;

        if (player.input.forwardImpulse > 1.0E-5F
                || player.input.forwardImpulse < -1.0E-5F
        ) return vec3.scale(player.input.forwardImpulse / Math.abs(player.input.forwardImpulse));

        return self.getDeltaMovement().multiply(1, 0, 1).normalize().scale(0.2f);
    }
}
