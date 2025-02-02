package meow.binary.relicsofrain.item.relic;

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
import meow.binary.relicsofrain.item.AbstractRORItem;
import meow.binary.relicsofrain.registry.ItemRegistry;
import meow.binary.relicsofrain.registry.RarityRegistry;
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
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

import java.util.List;

public class PlatinumHorseshoeItem extends AbstractRORItem implements IRenderableCurio {
    public PlatinumHorseshoeItem(Properties props) {
        super((new Properties()).rarity(RarityRegistry.LEGENDARY_RARITY.getValue()).stacksTo(1));
    }

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("try_again")
                                .maxLevel(5)
                                .stat(StatData.builder("extra_rolls")
                                        .initialValue(1, 1)
                                        .formatValue(Math::round)
                                        .upgradeModifier(UpgradeOperation.ADD, 1)
                                        .build())
                                .build())
                        .build())
                .leveling(LevelingData.builder()
                        .maxLevel(5)
                        .initialCost(100)
                        .step(100)
                        .sources(LevelingSourcesData.builder()
                                .source(LevelingSourceData.abilityBuilder("try_again")
                                        .gem(GemShape.SQUARE, GemColor.ORANGE)
                                        .build())
                                .build())
                        .build())
                .style(StyleData.builder()
                        .beams(BeamsData.builder()
                                .build())
                        .tooltip(TooltipData.builder()
                                .borderBottom(0xff939c94)
                                .borderTop(0xffbfc9bf)
                                .build())
                        .build())
                .build();
    }

    public static ItemStack findStack(Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity)) return ItemStack.EMPTY;
        return EntityUtils.findEquippedCurio(livingEntity, ItemRegistry.PLATINUM_HORSESHOE.get());
    }

    public static int getRolls(Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity)) return 1;
        ItemStack stack = PlatinumHorseshoeItem.findStack(livingEntity);
        if (!(stack.getItem() instanceof IRelicItem relic)) return 1;
        return (int) relic.getStatValue(stack, "try_again", "extra_rolls") + 1;
    }

    public static void addExperience(Entity entity, int exp) {
        if (!(entity instanceof LivingEntity livingEntity)) return;
        ItemStack stack = PlatinumHorseshoeItem.findStack(livingEntity);
        if (!(stack.getItem() instanceof IRelicItem relic)) return;
        relic.spreadRelicExperience(livingEntity, stack, exp);
    }

    @Override
    public List<String> headParts() {
        return Lists.newArrayList("left_leg");
    }

    @Override
    public LayerDefinition constructLayerDefinition() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(new CubeDeformation(0.4f), 0);
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 1).addBox(2.61F, 0.0F, -2.5F, 0.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 16, 16);
    }

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext, PoseStack matrixStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource renderTypeBuffer, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        CurioModel model = this.getModel(stack);
        matrixStack.pushPose();
        LivingEntity entity = slotContext.entity();
        ICurioRenderer.followBodyRotations(entity, model);
        model.prepareMobModel(entity, limbSwing * 0.5f, limbSwingAmount / 6f, partialTicks);
        model.setupAnim(entity, limbSwing * 0.5f, limbSwingAmount / 6f, ageInTicks, netHeadYaw, headPitch);
        VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(renderTypeBuffer, RenderType.armorCutoutNoCull(this.getTexture(stack)), stack.hasFoil());
        matrixStack.translate(0.01, -0.15, 0.025);
        model.renderToBuffer(matrixStack, vertexconsumer, light, OverlayTexture.NO_OVERLAY);
        matrixStack.popPose();
    }
}
