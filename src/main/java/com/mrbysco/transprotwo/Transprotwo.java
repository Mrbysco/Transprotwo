package com.mrbysco.transprotwo;

import com.mrbysco.transprotwo.client.ClientHandler;
import com.mrbysco.transprotwo.client.particles.TransprotwoParticles;
import com.mrbysco.transprotwo.config.TransprotConfig;
import com.mrbysco.transprotwo.network.PacketHandler;
import com.mrbysco.transprotwo.registry.TransprotwoContainers;
import com.mrbysco.transprotwo.registry.TransprotwoRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Transprotwo.MOD_ID)
public class Transprotwo {
    public static final String MOD_ID = "transprotwo";

    public static final Logger LOGGER = LogManager.getLogger();

    public Transprotwo() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, TransprotConfig.clientSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, TransprotConfig.serverSpec);
        eventBus.register(TransprotConfig.class);

        eventBus.addListener(this::setup);

        TransprotwoRegistry.ITEMS.register(eventBus);
        TransprotwoRegistry.BLOCKS.register(eventBus);
        TransprotwoRegistry.BLOCK_ENTITIES.register(eventBus);
        TransprotwoContainers.CONTAINERS.register(eventBus);

        TransprotwoParticles.PARTICLE_TYPES.register(eventBus);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            eventBus.addListener(ClientHandler::onClientSetup);
            eventBus.addListener(ClientHandler::registerEntityRenders);
        });
    }

    private void setup(final FMLCommonSetupEvent event) {
        PacketHandler.init();
    }
}
