package meow.binary.relicsofrain.client.trail;

import it.hurts.octostudios.octolib.module.particle.trail.EntityTrailProvider;
import meow.binary.relicsofrain.entity.projectile.LightningArc;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class LightningTrail extends EntityTrailProvider<LightningArc> {
    public LightningTrail(LightningArc entity) {
        super(entity);
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
    public Vec3 getTrailPosition(float partialTicks) {
        return entity.getPosition(1f);
    }

    @Override
    public int getTrailUpdateFrequency() {
        return 1;
    }

    @Override
    public boolean isTrailAlive() {
        return entity.isAlive();
    }

    @Override
    public boolean isTrailGrowing() {
        return entity.tickCount > 0;
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