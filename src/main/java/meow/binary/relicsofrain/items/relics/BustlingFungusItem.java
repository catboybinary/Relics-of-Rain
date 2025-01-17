package meow.binary.relicsofrain.items.relics;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.BeamsData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.StyleData;
import it.hurts.sskirillss.relics.network.NetworkHandler;
import meow.binary.relicsofrain.items.AbstractRORItem;
import meow.binary.relicsofrain.network.S2CBustlingFungusUpdate;
import meow.binary.relicsofrain.registries.DataComponentRegistry;
import meow.binary.relicsofrain.registries.ItemRegistry;
import meow.binary.relicsofrain.util.EntityUtils;
import meow.binary.relicsofrain.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Vector3f;
import top.theillusivec4.curios.api.SlotContext;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber
public class BustlingFungusItem extends AbstractRORItem {
    public BustlingFungusItem(Properties props) {
        super((new Properties()).rarity(Rarity.COMMON).stacksTo(1));
    }

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .style(StyleData.builder()
                        .beams(BeamsData.builder()
                                .startColor(0xFF58f99f)
                                .endColor(0x00005346)
                                .build())
                        .build())
                .build();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        int ticksImmobile = stack.getOrDefault(DataComponentRegistry.TICKS_IMMOBILE, 0);

        if (slotContext.entity().level().isClientSide) return;

        boolean isMoving = slotContext.entity().getKnownMovement().multiply(1, slotContext.entity().onGround() ? 0 : 1, 1).lengthSqr() > 0.0005;

        if (!isMoving) {
            if (ticksImmobile < 20)
                stack.set(DataComponentRegistry.TICKS_IMMOBILE, Mth.clamp(ticksImmobile + 1, 0, 20));
        } else if (ticksImmobile > 0) stack.set(DataComponentRegistry.TICKS_IMMOBILE, 0);

        if (slotContext.entity().tickCount % 5 == 0 && ticksImmobile == 20)
            NetworkHandler.sendToClientsTrackingEntityAndSelf(new S2CBustlingFungusUpdate(slotContext.entity().getId()), slotContext.entity());
    }

    public static Map<Integer, Float> lerpedRadius = new HashMap<>();

    private static void renderFungi(PoseStack p, LivingEntity livingEntity, ItemStack stack, float partialTick) {
        int id = livingEntity.getId();
        if (!(stack.getItem() instanceof BustlingFungusItem relic)) return;
        int ticksImmobile = stack.getOrDefault(DataComponentRegistry.TICKS_IMMOBILE, 0);

        float radius = ticksImmobile >= 20 ? 2.5f : 0f;
        float tickMultiple = Mth.clamp(livingEntity.level().tickRateManager().tickrate() / 20f, 0, 1);

        lerpedRadius.put(id, Mth.lerp(Mth.clamp(Minecraft.getInstance().getTimer().getRealtimeDeltaTicks() / 3f * tickMultiple, 0, 1), lerpedRadius.getOrDefault(id, 0f), radius));
        float r = lerpedRadius.getOrDefault(id, 0f);
        float ii = Mth.clamp(r - 0.15f, 0f, 1.35f) / 1.5f;

        if (r < 0.025f) return;
        RenderType type = RenderUtils.getRenderType(RenderUtils.WHITE, VertexFormat.Mode.QUADS);
        p.pushPose();
        p.mulPose(Axis.YP.rotationDegrees(livingEntity.tickCount+partialTick));
        p.scale(r, 1f, r);

        VertexConsumer builder = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(type);

        for (int i = 0; i < 8; i++) {
            builder.addVertex(p.last(), new Vector3f(0.4142f , 0.0f, 1)).setColor((int)(150*ii), (int)(255*ii), (int)(110*ii), 100).setUv(0, 0).setLight(LightTexture.pack(15, 15));
            builder.addVertex(p.last(), new Vector3f(0.4142f , 0.1f, 1)).setColor((int)(150*ii), (int)(255*ii), (int)(110*ii), 100).setUv(0, 0).setLight(LightTexture.pack(15, 15));
            builder.addVertex(p.last(), new Vector3f(-0.4142f, 0.1f, 1)).setColor((int)(150*ii), (int)(255*ii), (int)(110*ii), 100).setUv(0, 0).setLight(LightTexture.pack(15, 15));
            builder.addVertex(p.last(), new Vector3f(-0.4142f, 0.0f, 1)).setColor((int)(150*ii), (int)(255*ii), (int)(110*ii), 100).setUv(0, 0).setLight(LightTexture.pack(15, 15));
            p.mulPose(Axis.YP.rotationDegrees(45));
        }


        p.popPose();
    }

    @SubscribeEvent
    public static void renderLevel(RenderLevelStageEvent e) {
        if (e.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            for (int id : lerpedRadius.keySet()) {
                if (!(e.getCamera().getEntity().level().getEntity(id) instanceof LivingEntity livingEntity)) continue;
                ItemStack stack = EntityUtils.findEquippedCurio(livingEntity, ItemRegistry.BUSTLING_FUNGUS.get());
                if (!(stack.getItem() instanceof IRelicItem relic)) continue;

                PoseStack p = e.getPoseStack();
                Vec3 cp = e.getCamera().getPosition();
                float partialTick = e.getPartialTick().getGameTimeDeltaPartialTick(true);
                Vec3 pp = livingEntity.getPosition(partialTick);

                p.pushPose();
                p.translate(pp.x - cp.x, pp.y - cp.y, pp.z - cp.z);
                renderFungi(p, livingEntity, stack, partialTick);
                p.popPose();
            }

            Minecraft.getInstance().renderBuffers().bufferSource().endLastBatch();
        }
    }
}
