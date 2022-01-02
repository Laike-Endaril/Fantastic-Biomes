package com.fantasticsource.fantasticbiomes;

import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

@Mod(modid = FantasticBiomes.MODID, name = FantasticBiomes.NAME, version = FantasticBiomes.VERSION)
public class FantasticBiomes
{
    public static final String MODID = "fantasticbiomes";
    public static final String NAME = "Fantastic Biomes";
    public static final String VERSION = "1.12.2.000";

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(FantasticBiomes.class);
        MinecraftForge.TERRAIN_GEN_BUS.register(BiomePaintedDesert.class);
    }

    @SubscribeEvent
    public static void saveConfig(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(MODID)) ConfigManager.sync(MODID, Config.Type.INSTANCE);
    }

    @SubscribeEvent
    public static void registerBiome(RegistryEvent.Register<Biome> event)
    {
        IForgeRegistry<Biome> registry = event.getRegistry();

        Biome biome = new BiomePaintedDesert();
        registry.register(biome);
        BiomeDictionary.addTypes(biome, BiomeDictionary.Type.HOT, BiomeDictionary.Type.DRY, BiomeDictionary.Type.DEAD, BiomeDictionary.Type.MOUNTAIN, BiomeDictionary.Type.MESA, BiomeDictionary.Type.SANDY, BiomeDictionary.Type.WASTELAND);
    }
}
