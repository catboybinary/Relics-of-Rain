package meow.binary.relicsofrain.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import top.theillusivec4.curios.api.CuriosApi;

import javax.annotation.Nullable;
import java.util.Optional;

public class EntityUtils {
    public static ItemStack findEquippedCurio(Entity entity, Item item) {
        if (entity instanceof LivingEntity livingEntity) {
            Optional<ImmutableTriple<String, Integer, ItemStack>> optional = CuriosApi.getCuriosHelper().findEquippedCurio(item, livingEntity);
            return optional.isEmpty() ? ItemStack.EMPTY : (ItemStack)((ImmutableTriple)optional.get()).getRight();
        } else {
            return ItemStack.EMPTY;
        }
    }

    public static boolean isAlliedTo(@Nullable Entity source, @Nullable Entity target) {
        return (source == null || target == null) || (source.isAlliedTo(target) || target.isAlliedTo(source)) || (target.getUUID().equals(source.getUUID()))
                || ((target instanceof OwnableEntity ownableTarget && ownableTarget.getOwnerUUID() != null && ownableTarget.getOwnerUUID().equals(source.getUUID()))
                || (source instanceof OwnableEntity ownableSource && ownableSource.getOwnerUUID() != null && ownableSource.getOwnerUUID().equals(target.getUUID())));
    }

}
