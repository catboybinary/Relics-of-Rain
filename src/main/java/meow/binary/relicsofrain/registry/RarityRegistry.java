package meow.binary.relicsofrain.registry;

import meow.binary.relicsofrain.RelicsOfRain;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Rarity;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

import java.util.function.UnaryOperator;

public class RarityRegistry {
    public static final EnumProxy<Rarity> UNCOMMON_RARITY = new EnumProxy<>(
            Rarity.class, -1, RelicsOfRain.MODID + ":uncommon", (UnaryOperator<Style>) style -> style.withColor(ChatFormatting.GREEN)
    );
    public static final EnumProxy<Rarity> LEGENDARY_RARITY = new EnumProxy<>(
            Rarity.class, -1, RelicsOfRain.MODID + ":legendary", (UnaryOperator<Style>) style -> style.withColor(0xFF2538)
    );
    public static final EnumProxy<Rarity> LUNAR_RARITY = new EnumProxy<>(
            Rarity.class, -1, RelicsOfRain.MODID + ":lunar", (UnaryOperator<Style>) style -> style.withColor(ChatFormatting.BLUE)
    );
}
