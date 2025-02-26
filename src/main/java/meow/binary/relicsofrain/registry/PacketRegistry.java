package meow.binary.relicsofrain.registry;

import meow.binary.relicsofrain.RelicsOfRain;
import meow.binary.relicsofrain.network.BustlingFungusUpdatePacket;
import meow.binary.relicsofrain.network.FrostRelicUpdatePacket;
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

        registrar.playToClient(FrostRelicUpdatePacket.TYPE, FrostRelicUpdatePacket.STREAM_CODEC, FrostRelicUpdatePacket::handle);
        registrar.playToClient(BustlingFungusUpdatePacket.TYPE, BustlingFungusUpdatePacket.STREAM_CODEC, BustlingFungusUpdatePacket::handle);
    }
}
