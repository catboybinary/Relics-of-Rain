package meow.binary.relicsofrain.items.relics;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicSlotModifier;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.items.relics.base.data.style.BeamsData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.StyleData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.TooltipData;
import meow.binary.relicsofrain.items.AbstractRORItem;
import meow.binary.relicsofrain.registries.DataComponentRegistry;
import meow.binary.relicsofrain.registries.ItemRegistry;
import meow.binary.relicsofrain.registries.RarityRegistry;
import meow.binary.relicsofrain.util.EntityUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderArmEvent;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

import java.util.ArrayList;
import java.util.Random;

public class UnlabeledSyringeItem extends AbstractRORItem {
    public UnlabeledSyringeItem(Properties props) {
        super((new Properties()).rarity(RarityRegistry.LUNAR_RARITY.getValue()).stacksTo(1));
    }

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .leveling(LevelingData.builder()
                        .maxLevel(1)
                        .initialCost(100)
                        .step(100)
                        .build())
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("mutation")
                                .maxLevel(1)
                                .stat(StatData.builder("curio_amount")
                                        .initialValue(1, 1)
                                        .formatValue(Math::round)
                                        .upgradeModifier(UpgradeOperation.ADD, 1)
                                        .build())
                                .build())
                        .build())
                .style(StyleData.builder()
                        .beams(BeamsData.builder()
                                .startColor(0x775027d8)
                                .endColor(0x00e01062)
                                .build())
                        .tooltip(TooltipData.builder()
                                .borderTop(0xffaa95ef)
                                .borderBottom(0xffd4d0ef)
                                .build())
                        .build())
                .build();
    }


    @Override
    public @Nullable RelicSlotModifier getSlotModifiers(ItemStack stack) {
        String take = stack.getOrDefault(DataComponentRegistry.CURIO_TAKE, "");
        String give = stack.getOrDefault(DataComponentRegistry.CURIO_GIVE, "");
        if (take.isEmpty() || give.isEmpty()) return super.getSlotModifiers(stack);
        if (!(stack.getItem() instanceof IRelicItem relic)) return super.getSlotModifiers(stack);

        return RelicSlotModifier.builder()
                .modifier(take, -1)
                .modifier(give, (int) relic.getStatValue(stack, "mutation", "curio_amount"))
                .build();
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (slotContext.entity().level().isClientSide) return;
        LivingEntity livingEntity = slotContext.entity();
        int feet = getSlots(livingEntity, "feet");
        int hands = getSlots(livingEntity, "hands");
        int head = getSlots(livingEntity, "head");

        if (feet != -1) {
            if (feet == 0)
                slotContext.entity().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10, 0, true, false, false));
            else if (feet > 2)
                slotContext.entity().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 10, feet - 3, true, false, false));
        }
        if (hands != -1) {
            if (hands == 0)
                slotContext.entity().addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 10, 0, true, false, false));
            else if (hands > 2)
                slotContext.entity().addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 10, hands - 3, true, false, false));
        }
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        if (!stack.has(DataComponentRegistry.CURIO_GIVE) && !slotContext.entity().level().isClientSide) {
            ArrayList<String> slots = Lists.newArrayList("head", "feet", "hands");
            String take = slots.remove(new Random().nextInt(slots.size()));
            String give = slots.remove(new Random().nextInt(slots.size()));

            stack.set(DataComponentRegistry.CURIO_TAKE, take);
            stack.set(DataComponentRegistry.CURIO_GIVE, give);
        }

        if (CuriosApi.getCuriosInventory(slotContext.entity()).isEmpty()
                || CuriosApi.getCuriosInventory(slotContext.entity()).get().getCurios().isEmpty()
        ) return true;
        return getSlots(slotContext.entity(), stack.get(DataComponentRegistry.CURIO_TAKE)) >= 1;
    }

    public static int getSlots(LivingEntity living, String slot) {
        if (!CuriosApi.getEntitySlots(living).containsKey(slot) || CuriosApi.getCuriosInventory(living).isEmpty() || CuriosApi.getCuriosInventory(living).get().getCurios().isEmpty())
            return -1;
        return CuriosApi.getCuriosInventory(living).get().getCurios().get(slot).getSlots();
    }

    @EventBusSubscriber(Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void renderPlayer(RenderPlayerEvent.Pre e) {
            if (!(EntityUtils.findEquippedCurio(e.getEntity(), ItemRegistry.UNLABELED_SYRINGE.get()).getItem() instanceof IRelicItem))
                return;
            PlayerModel<AbstractClientPlayer> model = e.getRenderer().getModel();

            if (getSlots(e.getEntity(), "head") == 0) {
                model.head.visible = false;
                model.hat.visible = false;
            }
            if (getSlots(e.getEntity(), "hands") == 1) {
                model.leftArm.visible = false;
                model.leftSleeve.visible = false;
            } else if (getSlots(e.getEntity(), "hands") == 0) {
                model.leftArm.visible = false;
                model.leftSleeve.visible = false;
                model.rightArm.visible = false;
                model.rightSleeve.visible = false;
            }
            if (getSlots(e.getEntity(), "feet") == 1) {
                model.leftLeg.visible = false;
                model.leftPants.visible = false;
            } else if (getSlots(e.getEntity(), "feet") == 0) {
                model.leftLeg.visible = false;
                model.leftPants.visible = false;
                model.rightLeg.visible = false;
                model.rightPants.visible = false;
                e.getPoseStack().translate(0, model.crouching ? -0.5 : -0.7, 0);
            }
        }

        @SubscribeEvent
        public static void renderHand(RenderArmEvent e) {
            if (!(EntityUtils.findEquippedCurio(Minecraft.getInstance().player, ItemRegistry.UNLABELED_SYRINGE.get()).getItem() instanceof IRelicItem))
                return;
            if (getSlots(Minecraft.getInstance().player, "hands") == 0) e.setCanceled(true);
        }
    }
}
