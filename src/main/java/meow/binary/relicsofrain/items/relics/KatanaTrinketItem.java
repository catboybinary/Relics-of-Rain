package meow.binary.relicsofrain.items.relics;

import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.BeamsData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.StyleData;
import meow.binary.relicsofrain.items.AbstractRORItem;
import meow.binary.relicsofrain.registries.RarityRegistry;

public class KatanaTrinketItem extends AbstractRORItem {
    public KatanaTrinketItem(Properties props) {
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
