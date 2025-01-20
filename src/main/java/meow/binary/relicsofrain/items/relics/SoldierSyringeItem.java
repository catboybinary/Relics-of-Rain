package meow.binary.relicsofrain.items.relics;

import it.hurts.sskirillss.relics.items.relics.base.data.RelicAttributeModifier;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.items.relics.base.data.style.BeamsData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.StyleData;
import it.hurts.sskirillss.relics.utils.MathUtils;
import meow.binary.relicsofrain.items.AbstractRORItem;
import meow.binary.relicsofrain.registries.ItemRegistry;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber
public class SoldierSyringeItem extends AbstractRORItem {
    public SoldierSyringeItem(Properties props) {
        super((new Properties()).rarity(Rarity.COMMON).stacksTo(1));
    }


    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("attack_speed")
                                .maxLevel(20)
                                .stat(StatData.builder("attack_speed")
                                        .initialValue(0.075, 0.15)
                                        .upgradeModifier(UpgradeOperation.ADD, 0.075)
                                        .formatValue(value -> MathUtils.round(value*100, 1))
                                        .build())
                                .build())
                        .build())
                .leveling(LevelingData.builder()
                        .maxLevel(20)
                        .initialCost(100)
                        .step(150)
                        .build())
                .style(StyleData.builder()
                        .beams(BeamsData.builder()
                                .startColor(0xFF0a70dd)
                                .endColor(0x00002042)
                                .build())
                        .build())
                .build();
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand usedHand) {
        if (!((stack.getItem() instanceof SoldierSyringeItem))) return super.interactLivingEntity(stack, player, interactionTarget, usedHand);;
        if (!(interactionTarget instanceof Zombie zombie)) return super.interactLivingEntity(stack, player, interactionTarget, usedHand);;

        player.setItemInHand(usedHand, new ItemStack(ItemRegistry.UNLABELED_SYRINGE.get()));
        player.level().playSound(null, player.blockPosition(), SoundEvents.VEX_HURT, SoundSource.HOSTILE, 1f, 1f);
        zombie.invulnerableTime = 0;
        zombie.hurt(zombie.level().damageSources().playerAttack(player), 10f);

        return InteractionResult.CONSUME;
    }

    @Override
    public @Nullable RelicAttributeModifier getRelicAttributeModifiers(ItemStack stack) {
        return RelicAttributeModifier.builder()
                .attribute(new RelicAttributeModifier.Modifier(
                        Attributes.ATTACK_SPEED,
                        (float) this.getStatValue(stack, "attack_speed", "attack_speed"),
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                ))
                .build();
    }

    @SubscribeEvent
    public static void convertSyringe(LivingIncomingDamageEvent e) {
        if (e.getEntity().level().isClientSide) return;
        if (!(e.getSource().getDirectEntity() instanceof Player player)) return;
        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (!((stack.getItem() instanceof SoldierSyringeItem))) return;
        if (!(e.getEntity() instanceof Zombie zombie)) return;

        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ItemRegistry.UNLABELED_SYRINGE.get()));
        player.level().playSound(null, player.blockPosition(), SoundEvents.VEX_HURT, SoundSource.HOSTILE, 1f, 1f);
        zombie.invulnerableTime = 0;
        zombie.hurt(e.getSource(), 10f);
    }
}
