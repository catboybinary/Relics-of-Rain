package meow.binary.relicsofrain.mixin;

import meow.binary.relicsofrain.RelicsOfRain;
import net.minecraft.client.Minecraft;
import net.minecraft.server.WorldStem;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Inject(method = "onResourceLoadFinished", at = @At(value = "TAIL"))
    public void startWorld(CallbackInfo ci) throws Exception {
        LevelStorageSource.LevelStorageAccess level = Minecraft.getInstance().getLevelSource().createAccess(RelicsOfRain.START_WORLD);
        PackRepository pack = ServerPacksSource.createPackRepository(level);
        WorldStem stem = Minecraft.getInstance().createWorldOpenFlows().loadWorldStem(level.getDataTag(), false, pack);
        Minecraft.getInstance().doWorldLoad(level, pack, stem, false);
    }
}
