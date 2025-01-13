package meow.binary.relicsofrain.api;

import net.minecraft.world.item.Item;

public interface IProcCoefficient {
    default float getProcCoefficient() {
        return 1f;
    }

    static float getProcCoefficient(Item item) {
        return item instanceof IProcCoefficient proc ? proc.getProcCoefficient() : 1f;
    }
}
