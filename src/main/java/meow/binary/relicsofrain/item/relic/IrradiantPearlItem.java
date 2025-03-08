package meow.binary.relicsofrain.item.relic;

import it.hurts.sskirillss.relics.items.relics.base.data.RelicAttributeModifier;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import meow.binary.relicsofrain.RelicsOfRain;
import meow.binary.relicsofrain.item.AbstractRORItem;
import meow.binary.relicsofrain.registry.ItemRegistry;
import meow.binary.relicsofrain.registry.RarityRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber
public class IrradiantPearlItem extends AbstractRORItem {
    public IrradiantPearlItem(Properties props) {
        super(new Properties()
                .rarity(RarityRegistry.SPECIAL_RARITY.getValue())
                .stacksTo(1)
        );
    }

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("1up")
                                .stat(StatData.builder("attribute_modifier")
                                        .initialValue(0.01d, 0.02d)
                                        .upgradeModifier(UpgradeOperation.ADD, 0.018)
                                        .formatValue(value -> MathUtils.round(value * 100, 1))
                                        .build())
                                .maxLevel(10)
                                .build())
                        .build())
                .leveling(LevelingData.builder()
                        .maxLevel(10)
                        .build())
                .build();
    }

    @SubscribeEvent
    public static void modifyAttributes(EntityTickEvent.Pre e) {
        if (!(e.getEntity() instanceof LivingEntity livingEntity)
            || livingEntity.level().isClientSide
        ) return;

        float multiplier = (float) EntityUtils.findEquippedCurios(livingEntity, ItemRegistry.IRRADIANT_PEARL.get())
                .stream().mapToDouble(stack -> {
                    IrradiantPearlItem item = (IrradiantPearlItem) stack.getItem();
                    return item.getStatValue(stack, "1up", "attribute_modifier");
                }).sum();

        RelicAttributeModifier modifiers = RelicAttributeModifier.builder()
                .attribute(new RelicAttributeModifier.Modifier(Attributes.ARMOR, multiplier))
                .attribute(new RelicAttributeModifier.Modifier(Attributes.ARMOR_TOUGHNESS, multiplier))
                .attribute(new RelicAttributeModifier.Modifier(Attributes.ATTACK_DAMAGE, multiplier))
                .attribute(new RelicAttributeModifier.Modifier(Attributes.ATTACK_KNOCKBACK, multiplier))
                .attribute(new RelicAttributeModifier.Modifier(Attributes.ATTACK_SPEED, multiplier))
                .attribute(new RelicAttributeModifier.Modifier(Attributes.BLOCK_BREAK_SPEED, multiplier))
                .attribute(new RelicAttributeModifier.Modifier(Attributes.BLOCK_INTERACTION_RANGE, multiplier))
                .attribute(new RelicAttributeModifier.Modifier(Attributes.ENTITY_INTERACTION_RANGE, multiplier))
                .attribute(new RelicAttributeModifier.Modifier(Attributes.FLYING_SPEED, multiplier))
                .attribute(new RelicAttributeModifier.Modifier(Attributes.JUMP_STRENGTH, multiplier))
                .attribute(new RelicAttributeModifier.Modifier(Attributes.KNOCKBACK_RESISTANCE, multiplier))
                .attribute(new RelicAttributeModifier.Modifier(Attributes.LUCK, multiplier))
                .attribute(new RelicAttributeModifier.Modifier(Attributes.MAX_ABSORPTION, multiplier))
                .attribute(new RelicAttributeModifier.Modifier(Attributes.MAX_HEALTH, multiplier))
                .attribute(new RelicAttributeModifier.Modifier(Attributes.MINING_EFFICIENCY, multiplier))
                .attribute(new RelicAttributeModifier.Modifier(Attributes.MOVEMENT_EFFICIENCY, multiplier))
                .attribute(new RelicAttributeModifier.Modifier(Attributes.OXYGEN_BONUS, multiplier))
                .attribute(new RelicAttributeModifier.Modifier(Attributes.SNEAKING_SPEED, multiplier))
                .attribute(new RelicAttributeModifier.Modifier(Attributes.MOVEMENT_SPEED, multiplier))
                .attribute(new RelicAttributeModifier.Modifier(Attributes.STEP_HEIGHT, multiplier))
                .attribute(new RelicAttributeModifier.Modifier(Attributes.SUBMERGED_MINING_SPEED, multiplier))
                .attribute(new RelicAttributeModifier.Modifier(Attributes.SWEEPING_DAMAGE_RATIO, multiplier))
                .attribute(new RelicAttributeModifier.Modifier(Attributes.SAFE_FALL_DISTANCE, multiplier*1.5f))
                .attribute(new RelicAttributeModifier.Modifier(Attributes.WATER_MOVEMENT_EFFICIENCY, multiplier))
                .build();

        modifiers.getAttributes().forEach(
                modifier -> {
                    if (!(livingEntity.getAttribute(modifier.getAttribute()) instanceof AttributeInstance attribute)) {
                        return;
                    }

                    AttributeModifier attributeModifier = new AttributeModifier(
                            ResourceLocation.fromNamespaceAndPath(RelicsOfRain.MODID, modifier.getAttribute().getKey().location().getPath()),
                            modifier.getMultiplier(),
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    );

                    if (multiplier == 0) {
                        attribute.removeModifier(attributeModifier.id());
                    } else {
                        attribute.addOrUpdateTransientModifier(attributeModifier);
                    }
                }
        );
    }

    @Override
    public List<Component> getAttributesTooltip(List<Component> tooltips, TooltipContext context, ItemStack stack) {
        if (!(stack.getItem() instanceof IrradiantPearlItem relic)) {
            return tooltips;
        }

        ArrayList<Component> list = new ArrayList<>();
        list.add(Component.empty());
        list.add(Component.translatable("curios.modifiers.charm").withStyle(ChatFormatting.GOLD));
        list.add(Component.translatable("neoforge.modifier.plus", Component.translatable("neoforge.value.percent",
                Math.round(relic.getStatValue(stack, "1up", "attribute_modifier")*100)),
                Component.translatable("tooltip.relicsofrain.irradiant_pearl.all_stats")
        ).withStyle(ChatFormatting.BLUE));
        return list;
    }
}
