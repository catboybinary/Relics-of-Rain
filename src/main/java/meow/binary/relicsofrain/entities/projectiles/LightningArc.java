package meow.binary.relicsofrain.entities.projectiles;

import it.hurts.octostudios.octolib.modules.particles.OctoRenderManager;
import it.hurts.octostudios.octolib.modules.particles.trail.TrailProvider;
import it.hurts.sskirillss.relics.entities.misc.ITargetableEntity;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.network.NetworkHandler;
import it.hurts.sskirillss.relics.network.packets.sync.S2CEntityTargetPacket;
import lombok.Getter;
import lombok.Setter;
import meow.binary.relicsofrain.api.ItemDamageSource;
import meow.binary.relicsofrain.registries.DamageTypeRegistry;
import meow.binary.relicsofrain.util.EntityUtils;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class LightningArc extends ThrowableProjectile implements ITargetableEntity, TrailProvider {
    @Setter
    private double damage = 0d;
    @Setter
    private double maxDistance = 0;
    @Setter
    private int targetsLeft = 1;

    @Nullable
    private LivingEntity currentTarget = null;
    @Nullable
    private LivingEntity lastTarget = null;
    @Getter
    private Set<String> bouncedTargets = new LinkedHashSet<>();
    @Setter
    @Getter
    private ItemStack itemStack = ItemStack.EMPTY;

    public LightningArc(EntityType<? extends LightningArc> entityType, Level level) {
        super(entityType, level);
    }

    public @Nullable LivingEntity findClosestEligibleEntity() {
        List<LivingEntity> entities = level().getEntitiesOfClass(LivingEntity.class, new AABB(position(), position()).inflate(maxDistance), e ->
                !EntityUtils.isAlliedTo(getOwner(), e) &&
                        e != currentTarget &&
                        !bouncedTargets.contains(e.getStringUUID())
                        && e.isAlive());

        return entities.stream().min(Comparator.comparing(e -> e.position().distanceTo(position()))).orElse(null);
    }

    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();
        OctoRenderManager.registerProvider(this);
    }

    @Override
    public int getTrailInterpolationPoints() {
        return 1;
    }

    @Override
    public List<Vec3> getTrailRenderPositions(List<Vec3> points, float pTicks) {
        return points;
    }

    @Override
    public void tick() {
        if (this.level().isClientSide || tickCount <= 1) return;
        //if (this.tickCount % 2 != 0) return;
        if (targetsLeft <= 0) {
            discard();
            return;
        }

        setTarget(findClosestEligibleEntity());
        if (getTarget() == null) {
            discard();
            return;
        }

        setPos(getTarget().getEyePosition());
        getTarget().invulnerableTime = 0;
        LivingEntity owner = getOwner() instanceof LivingEntity Lowner ? Lowner : null;
        if (getTarget().hurt(ItemDamageSource.get(DamageTypeRegistry.ELECTRICITY, this, owner, itemStack), (float) damage)) {
            if (itemStack.getItem() instanceof IRelicItem relic) {
                relic.spreadRelicExperience(owner, itemStack, 1);
            }
            bouncedTargets.add(getTarget().getStringUUID());
            targetsLeft -= 1;
            getTarget().invulnerableTime = 0;
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    public @Nullable LivingEntity getTarget() {
        return currentTarget;
    }

    @Override
    public void setTarget(LivingEntity livingEntity) {
        lastTarget = currentTarget;
        currentTarget = livingEntity;
        if (!this.level().isClientSide && livingEntity != null) NetworkHandler.sendToClientsTrackingEntity(new S2CEntityTargetPacket(getId(), livingEntity.getId()), this);
    }

    @Override
    public Vec3 getTrailPosition(float partialTicks) {
        return getPosition(1f);
    }

    @Override
    public int getTrailUpdateFrequency() {
        return 1;
    }

    @Override
    public boolean isTrailAlive() {
        return isAlive();
    }

    @Override
    public boolean isTrailGrowing() {
        return this.tickCount > 0;
    }

    @Override
    public int getTrailMaxLength() {
        return 3;
    }

    @Override
    public int getTrailFadeInColor() {
        return 0xFF00FFFF;
    }

    @Override
    public int getTrailFadeOutColor() {
        return 0x300000FF;
    }

    @Override
    public double getTrailScale() {
        return 0.025F;
    }
}
