package meow.binary.relicsofrain.mixin;

import meow.binary.relicsofrain.RelicsOfRain;
import net.minecraft.client.Minecraft;
import net.minecraft.server.WorldStem;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.neoforged.fml.loading.FMLLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.CompletableFuture;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow public abstract CompletableFuture<Void> reloadResourcePacks();

    @Inject(method = "onResourceLoadFinished", at = @At(value = "TAIL"))
    public void startWorld(CallbackInfo ci) throws Exception {
        if (FMLLoader.isProduction()) return;

        LevelStorageSource.LevelStorageAccess level = Minecraft.getInstance().getLevelSource().createAccess(RelicsOfRain.START_WORLD);
        PackRepository pack = ServerPacksSource.createPackRepository(level);
        WorldStem stem = Minecraft.getInstance().createWorldOpenFlows().loadWorldStem(level.getDataTag(), false, pack);
        Minecraft.getInstance().doWorldLoad(level, pack, stem, false);
    }
}
