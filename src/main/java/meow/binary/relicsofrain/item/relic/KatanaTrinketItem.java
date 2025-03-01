package meow.binary.relicsofrain.item.relic;

import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.BeamsData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.StyleData;
import meow.binary.relicsofrain.item.AbstractRORItem;
import meow.binary.relicsofrain.registry.RarityRegistry;

public class KatanaTrinketItem extends AbstractRORItem {
    public KatanaTrinketItem(Properties props) {
        super((new Properties()).rarity(RarityRegistry.LEGENDARY_RARITY.getValue()).stacksTo(1));
    }

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("agility").build())
                        .build())
                .leveling(LevelingData.builder()
                        .maxLevel(0)
                        .initialCost(0)
                        .step(0)
                        .build())
                .style(StyleData.builder()
                        .beams(BeamsData.builder()
                                .startColor(0x90909090)
                                .endColor(0x00ffffff)
                                .build())
                        .build())
                .build();
    }


}
