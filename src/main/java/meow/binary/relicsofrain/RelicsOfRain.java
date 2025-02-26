package meow.binary.relicsofrain;

import com.mojang.logging.LogUtils;
import it.hurts.sskirillss.relics.client.renderer.entities.NullRenderer;
import meow.binary.relicsofrain.item.relic.CrowbarItem;
import meow.binary.relicsofrain.registry.DataComponentRegistry;
import meow.binary.relicsofrain.registry.EntityRegistry;
import meow.binary.relicsofrain.registry.ItemRegistry;
import meow.binary.relicsofrain.registry.KeyMappingRegistry;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLLoader;
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

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> { // ItemProperties#register is not threadsafe, so we need to call it on the main thread
                ItemProperties.register(
                        // The item to apply the property to.
                        ItemRegistry.CROWBAR.get(),
                        // The id of the property.
                        ResourceLocation.withDefaultNamespace("broken"),
                        // A reference to a method that calculates the override value.
                        // Parameters are the used item stack, the level context, the player using the item,
                        // and a random seed you can use.
                        (stack, level, player, seed) -> CrowbarItem.isBroken(stack) ? 1 : 0
                );
            });
        }

        @SubscribeEvent
        public static void keyMappings(RegisterKeyMappingsEvent event) {
            if (!FMLLoader.isProduction()) event.register(KeyMappingRegistry.FREEZE);
        }

        @SubscribeEvent
        public static void entityRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(EntityRegistry.LIGHTNING_ARC.get(), NullRenderer::new);
//            event.registerEntityRenderer(EntityRegistry.MIMIC_CORE.get(), NullRenderer::new);
        }
    }
}
