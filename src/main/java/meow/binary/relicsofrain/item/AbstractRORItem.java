package meow.binary.relicsofrain.item;

import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import meow.binary.relicsofrain.RelicsOfRain;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

public abstract class AbstractRORItem extends RelicItem {
    public AbstractRORItem(Item.Properties properties) {
        super(properties);
    }

    public AbstractRORItem() {
        super((new Item.Properties()).rarity(Rarity.RARE).stacksTo(1));
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        entity.setGlowingTag(true);
        return super.onEntityItemUpdate(stack, entity);
    }

    @Override
    public String getConfigRoute() {
        return RelicsOfRain.MODID;
    }
}
