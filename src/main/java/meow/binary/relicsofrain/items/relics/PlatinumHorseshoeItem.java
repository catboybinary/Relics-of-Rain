package meow.binary.relicsofrain.items.relics;

import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import meow.binary.relicsofrain.items.AbstractRORItem;
import meow.binary.relicsofrain.registries.ItemRegistry;
import meow.binary.relicsofrain.registries.RarityRegistry;
import meow.binary.relicsofrain.util.EntityUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class PlatinumHorseshoeItem extends AbstractRORItem {
    public PlatinumHorseshoeItem(Properties props) {
        super((new Properties()).rarity(RarityRegistry.LEGENDARY_RARITY.getValue()).stacksTo(1));
    }

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("try_again")
                                .maxLevel(5)
                                .stat(StatData.builder("extra_rolls")
                                        .initialValue(1, 1)
                                        .formatValue(Math::round)
                                        .upgradeModifier(UpgradeOperation.ADD, 1)
                                        .build())
                                .build())
                        .build())
                .leveling(LevelingData.builder()
                        .maxLevel(5)
                        .initialCost(100)
                        .step(100)
                        .build())
                .build();
    }

    public static int getRolls(Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity)) return 1;
        ItemStack stack = EntityUtils.findEquippedCurio(livingEntity, ItemRegistry.PLATINUM_HORSESHOE.get());
        if (!(stack.getItem() instanceof IRelicItem relic)) return 1;
        return (int) relic.getStatValue(stack, "try_again", "extra_rolls") + 1;
    }
}
