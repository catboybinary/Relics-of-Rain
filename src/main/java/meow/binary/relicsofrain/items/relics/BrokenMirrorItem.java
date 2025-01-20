package meow.binary.relicsofrain.items.relics;

import it.hurts.sskirillss.relics.init.EffectRegistry;
import it.hurts.sskirillss.relics.items.relics.MagicMirrorItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.BeamsData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.StyleData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.TooltipData;
import meow.binary.relicsofrain.api.ItemDamageSource;
import meow.binary.relicsofrain.items.AbstractRORItem;
import meow.binary.relicsofrain.registries.ItemRegistry;
import meow.binary.relicsofrain.registries.KeyMappingRegistry;
import meow.binary.relicsofrain.registries.RarityRegistry;
import meow.binary.relicsofrain.util.DungeonFinder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import top.theillusivec4.curios.api.SlotContext;

public class BrokenMirrorItem extends AbstractRORItem {

    public BrokenMirrorItem(Properties props) {
        super((new Properties()).rarity(RarityRegistry.LUNAR_RARITY.getValue()).stacksTo(1));
    }

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .leveling(LevelingData.builder()
                        .maxLevel(0)
                        .initialCost(0)
                        .step(0)
                        .build())
                .style(StyleData.builder()
                        .beams(BeamsData.builder()
                                .startColor(0x0)
                                .endColor(0x0)
                                .build())
                        .tooltip(TooltipData.builder()
                                .borderBottom(0xff70cbce)
                                .borderTop(0xff007582)
                                .build())
                        .build())
                .build();
    }

    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        if (!playerIn.getCooldowns().isOnCooldown(ItemRegistry.BROKEN_MIRROR.asItem()) && !worldIn.isClientSide()) {
            ServerPlayer serverPlayer = (ServerPlayer) playerIn;
            ServerLevel serverLevel = (ServerLevel) serverPlayer.level();

            playerIn.startUsingItem(handIn);
            return InteractionResultHolder.pass(stack);
        }
        return InteractionResultHolder.fail(stack);
    }

    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entity) {
        if (!world.isClientSide() && entity instanceof ServerPlayer player) {
            ServerLevel serverLevel = (ServerLevel) player.level();
            if (!player.isCreative()) player.getCooldowns().addCooldown(stack.getItem(), 1200);
            player.hurt(ItemDamageSource.get(DamageTypes.MAGIC, serverLevel, null, null, stack), 3);
            player.addEffect(new MobEffectInstance(EffectRegistry.BLEEDING, 110, 1, false, true, true));
            BlockPos blockPos = DungeonFinder.findNearestDungeon(serverLevel, player.blockPosition(), 10);
            if (blockPos == null) return stack;
            Vec3 pos = blockPos.getBottomCenter().add(1,0,0);
            player.teleportTo(pos.x, pos.y, pos.z);
            player.addEffect(new MobEffectInstance(MobEffects.UNLUCK, 36000, 1, true, false, false));
        }
        return stack;
    }

    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.BOW;
    }

    public int getUseDuration(ItemStack pStack, LivingEntity entity) {
        return 40;
    }

    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return false;
    }

    @EventBusSubscriber
    public static class ServerEvents {
        @SubscribeEvent
        public static void input(InputEvent.Key e) {
            if (KeyMappingRegistry.FREEZE.consumeClick() && Minecraft.getInstance().player != null) {
                ClientPacketListener connection = Minecraft.getInstance().getConnection();
                if (connection == null) return;
                connection.sendCommand("tick "+(Minecraft.getInstance().player.level().tickRateManager().isFrozen() ? "unfreeze" : "freeze"));
            }
        }

        @SubscribeEvent
        public static void onEntityTick(EntityTickEvent.Post e) {
            if (!(e.getEntity() instanceof ItemEntity item) || e.getEntity().level().isClientSide || !(item.getItem().getItem() instanceof MagicMirrorItem))
                return;
            ServerLevel level = ((ServerLevel) item.level());
            Vec3 p = item.position().add(0, item.getBbHeight() / 2f, 0);

            if (item.fallDistance >= 3) level.sendParticles(ParticleTypes.WHITE_ASH, p.x, p.y, p.z, 1, 0, 0, 0, 0);
            if (item.fallDistance >= 8) level.sendParticles(ParticleTypes.FLAME, p.x, p.y, p.z, 1, 0, 0, 0, 0);
            if (item.fallDistance >= 12) item.getPersistentData().putBoolean("AboutToBreak", true);
            if (!item.onGround() || !item.getPersistentData().getBoolean("AboutToBreak")) return;

            item.level().playSound(null, item, SoundEvents.GLASS_BREAK, SoundSource.NEUTRAL, 1f, 1f);

            level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.LIGHT_BLUE_STAINED_GLASS.defaultBlockState()), p.x, p.y, p.z, 10, 0.1, 0.1, 0.1, 1);
            item.setItem(ItemRegistry.BROKEN_MIRROR.toStack());
            item.getPersistentData().remove("AboutToBreak");
        }
    }
}
