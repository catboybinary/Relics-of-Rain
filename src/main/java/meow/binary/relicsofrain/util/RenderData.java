package meow.binary.relicsofrain.util;

import com.mojang.blaze3d.vertex.PoseStack;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@Data
@AllArgsConstructor
public class RenderData {
    PoseStack.Pose pose;
    Player player;
    ItemStack stack;
    float partialTick;
}
