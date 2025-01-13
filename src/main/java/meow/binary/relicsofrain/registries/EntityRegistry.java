package meow.binary.relicsofrain.registries;

import meow.binary.relicsofrain.entities.projectiles.LightningArc;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static meow.binary.relicsofrain.RelicsOfRain.MODID;

public class EntityRegistry {
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<LightningArc>> LIGHTNING_ARC = ENTITIES.register("lightning_arc", () ->
            EntityType.Builder.<LightningArc>of(LightningArc::new, MobCategory.MISC)
                    .sized(0.1F, 0.1F)
                    .build("lightning_arc")
    );

    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
    }
}
