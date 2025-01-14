package meow.binary.relicsofrain.api;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemDamageSource extends DamageSource {
    public final ItemStack itemUsed;

    public ItemDamageSource(Holder<DamageType> type, @Nullable Entity directEntity, @Nullable Entity causingEntity, @Nullable Vec3 damageSourcePosition, @Nullable ItemStack itemUsed) {
        super(type, directEntity, causingEntity, damageSourcePosition);
        this.itemUsed = itemUsed == null ? ItemStack.EMPTY : itemUsed;
    }

    public static ItemDamageSource get(Holder<DamageType> type, Entity directEntity, @Nullable Entity causingEntity, @Nullable Vec3 damageSourcePosition, @Nullable ItemStack itemUsed) {
        return new ItemDamageSource(type, directEntity, causingEntity, damageSourcePosition, itemUsed);
    }

    public static ItemDamageSource get(ResourceKey<DamageType> type, Level level, Entity directEntity, @Nullable Entity causingEntity, @Nullable ItemStack itemUsed) {
        return get(level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(type), directEntity, causingEntity, null, itemUsed);
    }

    public static ItemDamageSource get(Level level, Entity directEntity, @Nullable Entity causingEntity, @Nullable ItemStack itemUsed) {
        return get(DamageTypes.GENERIC, level, directEntity, causingEntity, itemUsed);
    }

    @Override
    public @NotNull Component getLocalizedDeathMessage(@NotNull LivingEntity livingEntity) {
        String s = "death.attack." + this.type().msgId();
        if (this.getEntity() == null && this.getDirectEntity() == null) {
            LivingEntity livingentity1 = livingEntity.getKillCredit();
            String s1 = s + ".player";
            return livingentity1 != null
                    ? Component.translatable(s1, livingEntity.getDisplayName(), livingentity1.getDisplayName())
                    : Component.translatable(s, livingEntity.getDisplayName());
        } else {
            Component component = this.getEntity() != null ? this.getEntity().getDisplayName() : this.getDirectEntity() != null ? this.getDirectEntity().getDisplayName() : null;
            return !itemUsed.isEmpty() && itemUsed.has(DataComponents.CUSTOM_NAME)
                    ? Component.translatable(s + ".item", livingEntity.getDisplayName(), component, itemUsed.getDisplayName())
                    : component != null ? Component.translatable(s + ".player", livingEntity.getDisplayName(), component)
                    : Component.translatable(s, livingEntity.getDisplayName());
        }
    }
}
