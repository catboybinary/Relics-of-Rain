package meow.binary.relicsofrain.handlers;

import meow.binary.relicsofrain.effects.OnHitEffect;
import meow.binary.relicsofrain.items.relics.PlatinumHorseshoeItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

public class OnHitEffectEventHandler {
    public static void register() {
        BuiltInRegistries.ITEM.stream()
                .filter(item -> item instanceof OnHitEffect onHit)
                .forEach(item -> NeoForge.EVENT_BUS.register(new Listener((OnHitEffect) item)));

    }

    private record Listener(OnHitEffect onHitEffectItem) {
        @SubscribeEvent
        public void onHitEvent(LivingDamageEvent.Post event) {
            if (event.getEntity().level().isClientSide) return;
            for (int i = 0; i < PlatinumHorseshoeItem.getRolls(event.getSource().getEntity()); i++)
                if (onHitEffectItem.onHit(event) != 0) break;
        }
    }
}
