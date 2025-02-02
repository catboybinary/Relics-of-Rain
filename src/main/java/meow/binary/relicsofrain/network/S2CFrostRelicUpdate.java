package meow.binary.relicsofrain.network;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import meow.binary.relicsofrain.RelicsOfRain;
import meow.binary.relicsofrain.item.relic.FrostRelicItem;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

@Data
@AllArgsConstructor
public class S2CFrostRelicUpdate implements CustomPacketPayload {
    private int id;

    public static final CustomPacketPayload.Type<S2CFrostRelicUpdate> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(RelicsOfRain.MODID, "update_frost_relic")
    );
    public static final StreamCodec<ByteBuf, S2CFrostRelicUpdate> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.INT, S2CFrostRelicUpdate::getId, S2CFrostRelicUpdate::new);

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!FrostRelicItem.lerpedRadius.containsKey(id)) FrostRelicItem.lerpedRadius.put(id, 0f);
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }


}
