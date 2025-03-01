package meow.binary.relicsofrain.event.handler;

import meow.binary.relicsofrain.api.effect.OnHitEffect;
import meow.binary.relicsofrain.item.relic.PlatinumHorseshoeItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

public class OnHitEffectEventHandler {
    public static void register() {
        BuiltInRegistries.ITEM.stream()
                .filter(item -> item instanceof OnHitEffect)
                .forEach(item -> NeoForge.EVENT_BUS.register(new Listener((OnHitEffect) item)));
    }

    private record Listener(OnHitEffect onHitEffectItem) {
        @SubscribeEvent
        public void onHitEvent(LivingDamageEvent.Post event) {
            if (event.getEntity().level().isClientSide) return;
            int rolls = PlatinumHorseshoeItem.getRolls(event.getSource().getEntity());
            for (int i = 0; i < Math.max(rolls, 1); i++) {
                int hitResult = onHitEffectItem.onHit(event);
                if (i >= 1 && hitResult == 1) PlatinumHorseshoeItem.addExperience(event.getSource().getEntity(), 1);
                if (hitResult != 0) break;
            }
        }
    }
}
