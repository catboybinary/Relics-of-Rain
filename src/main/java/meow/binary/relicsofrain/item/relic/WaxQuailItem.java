package meow.binary.relicsofrain.item.relic;

import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.items.relics.base.data.research.ResearchData;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import meow.binary.relicsofrain.item.AbstractRORItem;
import meow.binary.relicsofrain.registry.ItemRegistry;
import meow.binary.relicsofrain.registry.RarityRegistry;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

@EventBusSubscriber
public class WaxQuailItem extends AbstractRORItem {
    public WaxQuailItem(Properties props) {
        super((new Properties()).rarity(RarityRegistry.UNCOMMON_RARITY.getValue()).stacksTo(1));
    }

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("woah")
                                .stat(StatData.builder("boost_amount")
                                        .initialValue(0.75, 1)
                                        .upgradeModifier(UpgradeOperation.ADD, 0.15)
                                        .formatValue(value -> MathUtils.round(value, 2))
                                        .build())
                                .research(ResearchData.builder()
                                        .star(0,4,4)
                                        .star(1,13,8)
                                        .star(2,18,3)
                                        .star(3,21,8)
                                        .star(4,13,17)
                                        .star(5,2,22)
                                        .star(6,12,23)
                                        .link(0, 1)
                                        .link(1, 2)
                                        .link(1, 3)
                                        .link(3, 4)
                                        .link(0, 4)
                                        .link(4, 5)
                                        .link(5, 6)
                                        .build())
                                .build())
                        .build())
                .leveling(LevelingData.builder()
                        .maxLevel(10)
                        .build())
                .build();
    }


    @SubscribeEvent
    public static void onJump(LivingEvent.LivingJumpEvent e) {
        LivingEntity entity = e.getEntity();
        ItemStack stack = EntityUtils.findEquippedCurio(entity, ItemRegistry.WAX_QUAIL.get());
        if ((entity.level().isClientSide && !(entity instanceof LocalPlayer))
                || !entity.isSprinting()
                || !entity.onGround()
                || !(stack.getItem() instanceof WaxQuailItem relic)
        ) return;

        relic.boost(entity, relic.getStatValue(stack, "woah", "boost_amount"));
        if (!entity.level().isClientSide) {
            relic.spreadRelicExperience(entity, stack, 1);
        }
    }

    public void boost(LivingEntity entity, double scale) {
        Vec3 originalDt = entity.getDeltaMovement();
        Vec3 dt = originalDt.multiply(1,0,1).normalize().scale(scale * Mth.clamp(originalDt.length()*1.5f, 0, 1));
        entity.setDeltaMovement(dt.x, entity.getDeltaMovement().y, dt.z);
    }
}
