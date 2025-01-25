package meow.binary.relicsofrain.items.relics;

import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.*;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemColor;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemShape;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.items.relics.base.data.style.BeamsData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.StyleData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.TooltipData;
import it.hurts.sskirillss.relics.utils.MathUtils;
import meow.binary.relicsofrain.RelicsOfRain;
import meow.binary.relicsofrain.items.AbstractRORItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import top.theillusivec4.curios.api.SlotContext;

public class EnergyDrinkItem extends AbstractRORItem {
    public EnergyDrinkItem(Properties props) {
        super((new Properties()).rarity(Rarity.COMMON).stacksTo(1));
    }

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("speed_boost")
                                .maxLevel(20)
                                .stat(StatData.builder("sprint_multiplier")
                                        .initialValue(0.1875d, 0.1918d) // 119.23% = 125% of sprinting speed
                                        .upgradeModifier(UpgradeOperation.ADD, 0.09615)
                                        .formatValue(value -> (MathUtils.round(value * 100 * 1.3f, 1)) + 0.1) //sprinting speed calculation tomfoolery
                                        .build())
                                .build())
                        .build())
                .leveling(LevelingData.builder()
                        .initialCost(100)
                        .maxLevel(20)
                        .step(150)
                        .sources(LevelingSourcesData.builder()
                                .source(LevelingSourceData.abilityBuilder("speed_boost")
                                        .gem(GemShape.OVAL, GemColor.PURPLE)
                                        .build())
                                .build())
                        .build())
                .style(StyleData.builder()
                        .beams(BeamsData.builder()
                                .startColor(-65281).endColor(255)
                                .build())
                        .tooltip(TooltipData.builder()
                                .borderBottom(0xff633ee0)
                                .borderTop(0xffb277f8)
                                .build())
                        .build())
                .build();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(stack.getItem() instanceof IRelicItem relicItem)) return;
        AttributeInstance attr = slotContext.entity().getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr == null) return;
        attr.addOrUpdateTransientModifier(
                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(RelicsOfRain.MODID, "sprint_bonus"),
                        slotContext.entity().isSprinting() ? relicItem.getStatValue(stack, "speed_boost", "sprint_multiplier") : 0,
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE));

        if (slotContext.entity().level().isClientSide) return;
        if (slotContext.entity().isSprinting() && slotContext.entity().tickCount % 20 == 0)
            relicItem.spreadRelicExperience(slotContext.entity(), stack, 1);
    }
}
