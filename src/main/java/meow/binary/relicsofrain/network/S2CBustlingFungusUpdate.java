package meow.binary.relicsofrain.network;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import meow.binary.relicsofrain.RelicsOfRain;
import meow.binary.relicsofrain.item.relic.BustlingFungusItem;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

@Data
@AllArgsConstructor
public class S2CBustlingFungusUpdate implements CustomPacketPayload {
    private int id;

    public static final Type<S2CBustlingFungusUpdate> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(RelicsOfRain.MODID, "update_bustling_fungus")
    );
    public static final StreamCodec<ByteBuf, S2CBustlingFungusUpdate> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.INT, S2CBustlingFungusUpdate::getId, S2CBustlingFungusUpdate::new);

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!BustlingFungusItem.lerpedRadius.containsKey(id)) BustlingFungusItem.lerpedRadius.put(id, 0f);
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }


}
