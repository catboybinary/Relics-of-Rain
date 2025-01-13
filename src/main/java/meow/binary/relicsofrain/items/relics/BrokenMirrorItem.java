package meow.binary.relicsofrain.items.relics;

import it.hurts.sskirillss.relics.items.relics.MagicMirrorItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import meow.binary.relicsofrain.items.AbstractRORItem;
import meow.binary.relicsofrain.registries.ItemRegistry;
import meow.binary.relicsofrain.registries.RarityRegistry;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

public class BrokenMirrorItem extends AbstractRORItem {

    public BrokenMirrorItem(Properties props) {
        super((new Properties()).rarity(RarityRegistry.LUNAR_RARITY.getValue()).stacksTo(1));
    }

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .build();
    }

    @EventBusSubscriber
    public static class ServerEvents {

        @SubscribeEvent
        public static void onEntityTick(EntityTickEvent.Post e) {
            if (!(e.getEntity() instanceof ItemEntity item) || e.getEntity().level().isClientSide || !(item.getItem().getItem() instanceof MagicMirrorItem)) return;
            ServerLevel level = ((ServerLevel) item.level());
            Vec3 p = item.position().add(0, item.getBbHeight()/2f, 0);

            if (item.fallDistance >= 3) level.sendParticles(ParticleTypes.WHITE_ASH, p.x, p.y, p.z, 1, 0, 0, 0, 0);
            if (item.fallDistance >= 8) level.sendParticles(ParticleTypes.FLAME, p.x, p.y, p.z, 1, 0, 0, 0, 0);
            if (item.fallDistance >= 12) item.getPersistentData().putBoolean("AboutToBreak", true);
            if (!item.onGround() || !item.getPersistentData().getBoolean("AboutToBreak")) return;

            item.level().playSound(null, item, SoundEvents.GLASS_BREAK, SoundSource.NEUTRAL, 1f, 1f);

            level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.LIGHT_BLUE_STAINED_GLASS.defaultBlockState()), p.x, p.y, p.z, 10, 0.1,0.1,0.1, 1);
            item.setItem(ItemRegistry.BROKEN_MIRROR.toStack());
            item.getPersistentData().remove("AboutToBreak");
        }
    }
}
