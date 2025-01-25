package meow.binary.relicsofrain.items.relics;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.hurts.sskirillss.relics.client.models.items.CurioModel;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.IRenderableCurio;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.*;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemColor;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemShape;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.items.relics.base.data.style.BeamsData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.StyleData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.TooltipData;
import meow.binary.relicsofrain.api.IProcCoefficient;
import meow.binary.relicsofrain.api.ItemDamageSource;
import meow.binary.relicsofrain.api.effects.OnHitEffect;
import meow.binary.relicsofrain.entities.projectiles.LightningArc;
import meow.binary.relicsofrain.items.AbstractRORItem;
import meow.binary.relicsofrain.registries.EntityRegistry;
import meow.binary.relicsofrain.registries.ItemRegistry;
import meow.binary.relicsofrain.registries.RarityRegistry;
import meow.binary.relicsofrain.util.EntityUtils;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

import java.util.List;

public class UkuleleItem extends AbstractRORItem implements IRenderableCurio, OnHitEffect, IProcCoefficient {
    public static final int PROC_COOLDOWN = 40;

    public UkuleleItem(Properties props) {
        super((new Item.Properties()).rarity(RarityRegistry.UNCOMMON_RARITY.getValue()).stacksTo(1));
    }

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("lightning_chain")
                                .stat(StatData.builder("proc_chance")
                                        .initialValue(0.15d, 0.18d)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.175)
                                        .thresholdValue(0, 1)
                                        .formatValue(value -> (int) Math.round(value * 100))
                                        .build())
                                .stat(StatData.builder("max_targets")
                                        .initialValue(2, 4)
                                        .upgradeModifier(UpgradeOperation.ADD, 1.5)
                                        .formatValue(Math::round)
                                        .build())
                                .stat(StatData.builder("max_distance")
                                        .initialValue(4, 7)
                                        .upgradeModifier(UpgradeOperation.ADD, 0.65)
                                        .formatValue(Math::round)
                                        .build())
                                .stat(StatData.builder("damage_percentage")
                                        .initialValue(0.25d, 0.5d)
                                        .upgradeModifier(UpgradeOperation.ADD, 0.05)
                                        .thresholdValue(0, 1)
                                        .formatValue(value -> (int) Math.round(value * 100))
                                        .build())
                                .build())
                        .build())
                .leveling(LevelingData.builder()
                        .initialCost(100)
                        .maxLevel(10)
                        .step(175)
                        .sources(LevelingSourcesData.builder()
                                .source(LevelingSourceData.abilityBuilder("lightning_chain")
                                        .gem(GemShape.SQUARE, GemColor.CYAN)
                                        .build())
                                .build())
                        .build())
                .style(StyleData.builder()
                        .beams(BeamsData.builder()
                                .build())
                        .tooltip(TooltipData.builder()
                                .borderBottom(0xff4b1f16)
                                .borderTop(0xff965740)
                                .build())
                        .build())
                .build();
    }

    @Override
    public float getProcCoefficient() {
        return 0.2f;
    }

    @Override
    public int onHit(LivingDamageEvent.Post e) {
        int exitValue = OnHitEffect.super.onHit(e);
        if (exitValue != 1) return exitValue;

        Entity causingEntity = e.getSource().getEntity();
        Entity source = e.getSource().getDirectEntity();
        LivingEntity target = e.getEntity();
        if (EntityUtils.isAlliedTo(causingEntity, target)) return -1;
        if (e.getSource() instanceof ItemDamageSource proc && proc.itemUsed.getItem() instanceof UkuleleItem) return -1;

        ItemStack stack = EntityUtils.findEquippedCurio(causingEntity, ItemRegistry.UKULELE.get());
        if (!(stack.getItem() instanceof IRelicItem relic)) return -1;
        if (target.level().random.nextFloat() > relic.getStatValue(stack, "lightning_chain", "proc_chance")) return 0;

        LightningArc arc = new LightningArc(EntityRegistry.LIGHTNING_ARC.get(), target.level());
        arc.setOwner(causingEntity);
        arc.setDamage(relic.getStatValue(stack, "lightning_chain", "damage_percentage") * e.getOriginalDamage());
        arc.setMaxDistance(relic.getStatValue(stack, "lightning_chain", "max_distance"));
        arc.setTarget(target);
        arc.getBouncedTargets().add(target.getStringUUID());
        arc.setTargetsLeft((int) relic.getStatValue(stack, "lightning_chain", "max_targets"));
        arc.setPos(target.getEyePosition());
        arc.setItemStack(stack);

        target.level().addFreshEntity(arc);

        return 1;
    }

    @Override
    public List<String> headParts() {
        return Lists.newArrayList("body");
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public LayerDefinition constructLayerDefinition() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(new CubeDeformation(0.4F), 0F);
        PartDefinition partdefinition = meshdefinition.getRoot();


        PartDefinition root = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition body = root.addOrReplaceChild("body2", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -3.0F, -0.5F, 6.0F, 11.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 14).addBox(-4.0F, 1.0F, 0.495F, 8.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(18, 0).addBox(-1.0F, -11.0F, 1.5F, 2.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 5.0F, 3.505F, 0.0F, 0.0F, -0.3927F));
        body.addOrReplaceChild("cube_5_r1", CubeListBuilder.create().texOffs(20, 14).addBox(-0.995F, -2.85F, 0.0F, 1.99F, 2.85F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.6173F, 0.4239F, -0.3927F, 0.0F, 0.0F));
        body.addOrReplaceChild("cube_5_r2", CubeListBuilder.create().texOffs(20, 18).addBox(-2.5F, -3.5F, -0.5F, 5.0F, 3.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(18, 9).addBox(-1.5F, -4.0F, -1.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -11.0F, 2.5F, 0.3054F, 0.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 32, 32);

    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext, PoseStack matrixStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource renderTypeBuffer, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        CurioModel model = getModel(stack);

        LivingEntity entity = slotContext.entity();

        matrixStack.pushPose();

        ICurioRenderer.followBodyRotations(entity, model);

        //ICurioRenderer.translateIfSneaking(matrixStack, entity);
        //ICurioRenderer.rotateIfSneaking(matrixStack,entity);


        model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
        model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(renderTypeBuffer, RenderType.armorCutoutNoCull(getTexture(stack)), stack.hasFoil());
        model.renderToBuffer(matrixStack, vertexconsumer, light, OverlayTexture.NO_OVERLAY);

        matrixStack.popPose();
    }
}
