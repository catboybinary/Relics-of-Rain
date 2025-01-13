package meow.binary.relicsofrain.effects;

import meow.binary.relicsofrain.api.IProcCoefficient;
import meow.binary.relicsofrain.api.ItemDamageSource;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

public interface OnKillEffect {

    /**
     *
     * @param e - event
     * @return 0 - rng failure, 1 - success, -1 other failure (shouldn't reroll)
     */
    default int onKill(LivingDeathEvent e) {
        float procCoefficient = e.getSource() instanceof ItemDamageSource src ? src.itemUsed.getItem() instanceof IProcCoefficient proc ? proc.getProcCoefficient() : 1f : 1f;
        return (e.getEntity().level().random.nextFloat() < procCoefficient) ? 1 : 0;
    }
}
