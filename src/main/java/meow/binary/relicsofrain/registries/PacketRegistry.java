package meow.binary.relicsofrain.registries;

import meow.binary.relicsofrain.RelicsOfRain;
import meow.binary.relicsofrain.network.S2CBustlingFungusUpdate;
import meow.binary.relicsofrain.network.S2CFrostRelicUpdate;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class PacketRegistry {
    @SubscribeEvent
    public static void onRegisterPayloadHandler(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(RelicsOfRain.MODID)
                .versioned("1.0")
                .optional();

        registrar.playToClient(S2CFrostRelicUpdate.TYPE, S2CFrostRelicUpdate.STREAM_CODEC, S2CFrostRelicUpdate::handle);
        registrar.playToClient(S2CBustlingFungusUpdate.TYPE, S2CBustlingFungusUpdate.STREAM_CODEC, S2CBustlingFungusUpdate::handle);
    }
}
