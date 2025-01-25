package meow.binary.relicsofrain.api.effects;

import meow.binary.relicsofrain.api.IProcCoefficient;
import meow.binary.relicsofrain.api.ItemDamageSource;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

public interface OnHitEffect {

    /**
     *
     * @param e - event
     * @return 0 - rng failure, 1 - success, -1 other failure (shouldn't reroll)
     */
    default int onHit(LivingDamageEvent.Post e) {
        float procCoefficient = e.getSource() instanceof ItemDamageSource src ? src.itemUsed.getItem() instanceof IProcCoefficient proc ? proc.getProcCoefficient() : 1f : 1f;
        return (e.getEntity().level().random.nextFloat() < procCoefficient) ? 1 : 0;
    }
}
