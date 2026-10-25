package dev.simulateddamages;

import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@EventBusSubscriber(modid = SimulatedDamages.MOD_ID, value = Dist.CLIENT, bus = Bus.MOD)
public final class ClientConfigScreen {
    private ClientConfigScreen() {
    }

    @SubscribeEvent
    public static void register(FMLClientSetupEvent event) {
        ModList.get().getModContainerById(SimulatedDamages.MOD_ID)
                .orElseThrow(() -> new IllegalStateException("Create: Simulated Damages mod container is unavailable"))
                .registerExtensionPoint(IConfigScreenFactory.class,
                        (container, parent) -> new ConfigurationScreen(container, parent));
    }
}
