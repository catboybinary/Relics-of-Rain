package meow.binary.relicsofrain.registry;

import meow.binary.relicsofrain.event.handler.OnHitEffectEventHandler;
import meow.binary.relicsofrain.event.handler.OnKillEffectEventHandler;
import meow.binary.relicsofrain.item.relic.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import static meow.binary.relicsofrain.RelicsOfRain.MODID;

public class ItemRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);

    public static final DeferredItem<BustlingFungusItem> BUSTLING_FUNGUS = ITEMS.registerItem("bustling_fungus", BustlingFungusItem::new);
    public static final DeferredItem<SoldierSyringeItem> SOLDIER_SYRINGE = ITEMS.registerItem("soldier_syringe", SoldierSyringeItem::new);
    public static final DeferredItem<EnergyDrinkItem> ENERGY_DRINK = ITEMS.registerItem("energy_drink", EnergyDrinkItem::new);
    public static final DeferredItem<CrowbarItem> CROWBAR = ITEMS.registerItem("crowbar", CrowbarItem::new);
    public static final DeferredItem<UkuleleItem> UKULELE = ITEMS.registerItem("ukulele", UkuleleItem::new);
    public static final DeferredItem<WaxQuailItem> WAX_QUAIL = ITEMS.registerItem("wax_quail", WaxQuailItem::new);
    public static final DeferredItem<FrostRelicItem> FROST_RELIC = ITEMS.registerItem("frost_relic", FrostRelicItem::new);
    public static final DeferredItem<KatanaTrinketItem> KATANA_TRINKET = ITEMS.registerItem("katana_trinket", KatanaTrinketItem::new);
    public static final DeferredItem<PlatinumHorseshoeItem> PLATINUM_HORSESHOE = ITEMS.registerItem("platinum_horseshoe", PlatinumHorseshoeItem::new);
    public static final DeferredItem<BrokenMirrorItem> BROKEN_MIRROR = ITEMS.registerItem("broken_mirror", BrokenMirrorItem::new);
    public static final DeferredItem<UnlabeledSyringeItem> UNLABELED_SYRINGE = ITEMS.registerItem("unlabeled_syringe", UnlabeledSyringeItem::new);
    public static final DeferredItem<IrradiantPearlItem> IRRADIANT_PEARL = ITEMS.registerItem("irradiant_pearl", IrradiantPearlItem::new);

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }

    public static void registerEffects() {
        OnHitEffectEventHandler.register();
        OnKillEffectEventHandler.register();
    }
}
