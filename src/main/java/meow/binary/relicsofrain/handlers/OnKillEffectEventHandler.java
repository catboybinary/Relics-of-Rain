package meow.binary.relicsofrain.handlers;

import meow.binary.relicsofrain.effects.OnKillEffect;
import meow.binary.relicsofrain.items.relics.PlatinumHorseshoeItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

public class OnKillEffectEventHandler {
    public static void register() {
        BuiltInRegistries.ITEM.stream()
                .filter(item -> item instanceof OnKillEffect onHit)
                .forEach(item -> NeoForge.EVENT_BUS.register(new Listener((OnKillEffect) item)));
    }

    private record Listener(OnKillEffect onKillEffectItem) {
        @SubscribeEvent
        public void onLivingDamage(LivingDeathEvent event) {
            if (event.getEntity().level().isClientSide) return;
            for (int i = 0; i < PlatinumHorseshoeItem.getRolls(event.getSource().getEntity()); i++)
                if (onKillEffectItem.onKill(event) != 0) break;
        }
    }
}
