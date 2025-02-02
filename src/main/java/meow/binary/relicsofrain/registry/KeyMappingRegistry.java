package meow.binary.relicsofrain.registry;

import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class KeyMappingRegistry {
    public static KeyMapping FREEZE = new KeyMapping("Freeze the Game", GLFW.GLFW_KEY_C, KeyMapping.CATEGORY_MISC);
}
