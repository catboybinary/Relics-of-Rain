package meow.binary.relicsofrain.event.handler;

import meow.binary.relicsofrain.api.effect.OnKillEffect;
import meow.binary.relicsofrain.item.relic.PlatinumHorseshoeItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

public class OnKillEffectEventHandler {
    public static void register() {
        BuiltInRegistries.ITEM.stream()
                .filter(item -> item instanceof OnKillEffect)
                .forEach(item -> NeoForge.EVENT_BUS.register(new Listener((OnKillEffect) item)));
    }

    private record Listener(OnKillEffect onKillEffectItem) {
        @SubscribeEvent
        public void onLivingDamage(LivingDeathEvent event) {
            if (event.getEntity().level().isClientSide) return;
            int rolls = PlatinumHorseshoeItem.getRolls(event.getSource().getEntity());
            for (int i = 0; i < Math.max(rolls, 1); i++) {
                int killResult = onKillEffectItem.onKill(event);
                if (i >= 1 && killResult == 1) PlatinumHorseshoeItem.addExperience(event.getSource().getEntity(), 1);
                if (killResult != 0) break;
            }
        }
    }
}
