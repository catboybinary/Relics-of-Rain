package meow.binary.relicsofrain.registries;

import meow.binary.relicsofrain.handlers.OnHitEffectEventHandler;
import meow.binary.relicsofrain.handlers.OnKillEffectEventHandler;
import meow.binary.relicsofrain.items.relics.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import static meow.binary.relicsofrain.RelicsOfRain.MODID;

public class ItemRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);

    public static final DeferredItem<EnergyDrinkItem> ENERGY_DRINK = ITEMS.registerItem("energy_drink", EnergyDrinkItem::new);
    public static final DeferredItem<UkuleleItem> UKULELE = ITEMS.registerItem("ukulele", UkuleleItem::new);
    public static final DeferredItem<FrostRelicItem> FROST_RELIC = ITEMS.registerItem("frost_relic", FrostRelicItem::new);
    public static final DeferredItem<PlatinumHorseshoeItem> PLATINUM_HORSESHOE = ITEMS.registerItem("platinum_horseshoe", PlatinumHorseshoeItem::new);
    public static final DeferredItem<BrokenMirrorItem> BROKEN_MIRROR = ITEMS.registerItem("broken_mirror", BrokenMirrorItem::new);


    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }

    public static void registerEffects() {
        OnHitEffectEventHandler.register();
        OnKillEffectEventHandler.register();
    }
}
