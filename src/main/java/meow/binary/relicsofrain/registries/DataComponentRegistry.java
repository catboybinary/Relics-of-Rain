package meow.binary.relicsofrain.registries;

import com.mojang.serialization.Codec;
import meow.binary.relicsofrain.RelicsOfRain;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class DataComponentRegistry {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, RelicsOfRain.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<Integer>>> TIMER_LIST = DATA_COMPONENTS.register("timer_list",
            () -> DataComponentType.<List<Integer>>builder()
                    .persistent(Codec.list(Codec.INT))
                    .build()
    );

    public static void register(IEventBus modEventBus) {
        DATA_COMPONENTS.register(modEventBus);
    }
}
