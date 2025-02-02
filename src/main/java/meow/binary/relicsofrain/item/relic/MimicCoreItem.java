package meow.binary.relicsofrain.item.relic;

import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.BeamsData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.StyleData;
import meow.binary.relicsofrain.item.AbstractRORItem;
import meow.binary.relicsofrain.registry.RarityRegistry;

public class MimicCoreItem extends AbstractRORItem {
    public MimicCoreItem(Properties props) {
        super((new Properties()).rarity(RarityRegistry.UNCOMMON_RARITY.getValue()).stacksTo(1));
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
                                .startColor(0xFF0a70dd)
                                .endColor(0x00002042)
                                .build())
                        .build())
                .build();
    }
}
