package meow.binary.relicsofrain;

import com.mojang.logging.LogUtils;
import it.hurts.sskirillss.relics.client.renderer.entities.NullRenderer;
import meow.binary.relicsofrain.registries.DataComponentRegistry;
import meow.binary.relicsofrain.registries.EntityRegistry;
import meow.binary.relicsofrain.registries.ItemRegistry;
import meow.binary.relicsofrain.registries.KeyMappingRegistry;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.slf4j.Logger;

@Mod(RelicsOfRain.MODID)
public class RelicsOfRain {
    public static final String MODID = "relicsofrain";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String START_WORLD = "Structures";


    public RelicsOfRain(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        ItemRegistry.register(modEventBus);
        EntityRegistry.register(modEventBus);
        DataComponentRegistry.register(modEventBus);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        ItemRegistry.registerEffects();
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void keyMappings(RegisterKeyMappingsEvent event) {
            event.register(KeyMappingRegistry.FREEZE);
        }

        @SubscribeEvent
        public static void entityRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(EntityRegistry.LIGHTNING_ARC.get(), NullRenderer::new);
        }
    }
}
