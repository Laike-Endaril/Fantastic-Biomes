package com.fantasticsource.fantasticbiomes;

import com.fantasticsource.fantasticbiomes.biome.BiomePaintedDesert;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.layer.*;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.terraingen.WorldTypeEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistry;

import static net.minecraft.world.gen.layer.GenLayer.getModdedBiomeSize;

@Mod(modid = FantasticBiomes.MODID, name = FantasticBiomes.NAME, version = FantasticBiomes.VERSION, dependencies = "required-after:fantasticlib@[1.12.2.047a,)")
public class FantasticBiomes
{
    public static final String MODID = "fantasticbiomes";
    public static final String NAME = "Fantastic Biomes";
    public static final String VERSION = "1.12.2.000";

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(FantasticBiomes.class);

        MinecraftForge.TERRAIN_GEN_BUS.register(FantasticBiomes.class);
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
        ForgeRegistry<Biome> registry = (ForgeRegistry<Biome>) event.getRegistry();

        Biome biome = new BiomePaintedDesert();
        registry.register(biome);
        BiomeDictionary.addTypes(biome, BiomeDictionary.Type.HOT, BiomeDictionary.Type.DRY, BiomeDictionary.Type.DEAD, BiomeDictionary.Type.MOUNTAIN, BiomeDictionary.Type.MESA, BiomeDictionary.Type.SANDY, BiomeDictionary.Type.WASTELAND);
    }


    @SubscribeEvent
    public static void removeVanillaRivers(WorldTypeEvent.InitBiomeGens event)
    {
        if (event.getWorldType().getName().equals("default")) event.setNewBiomeGens(initializeAllBiomeGeneratorsWithoutRivers(event.getSeed(), event.getWorldType()));
    }

    public static GenLayer[] initializeAllBiomeGeneratorsWithoutRivers(long seed, WorldType worldType)
    {
        GenLayer genlayer = new GenLayerIsland(1L);
        genlayer = new GenLayerFuzzyZoom(2000L, genlayer);
        GenLayer genlayeraddisland = new GenLayerAddIsland(1L, genlayer);
        GenLayer genlayerzoom = new GenLayerZoom(2001L, genlayeraddisland);
        GenLayer genlayeraddisland1 = new GenLayerAddIsland(2L, genlayerzoom);
        genlayeraddisland1 = new GenLayerAddIsland(50L, genlayeraddisland1);
        genlayeraddisland1 = new GenLayerAddIsland(70L, genlayeraddisland1);
        GenLayer genlayerremovetoomuchocean = new GenLayerRemoveTooMuchOcean(2L, genlayeraddisland1);
        GenLayer genlayeraddsnow = new GenLayerAddSnow(2L, genlayerremovetoomuchocean);
        GenLayer genlayeraddisland2 = new GenLayerAddIsland(3L, genlayeraddsnow);
        GenLayer genlayeredge = new GenLayerEdge(2L, genlayeraddisland2, GenLayerEdge.Mode.COOL_WARM);
        genlayeredge = new GenLayerEdge(2L, genlayeredge, GenLayerEdge.Mode.HEAT_ICE);
        genlayeredge = new GenLayerEdge(3L, genlayeredge, GenLayerEdge.Mode.SPECIAL);
        GenLayer genlayerzoom1 = new GenLayerZoom(2002L, genlayeredge);
        genlayerzoom1 = new GenLayerZoom(2003L, genlayerzoom1);
        GenLayer genlayeraddisland3 = new GenLayerAddIsland(4L, genlayerzoom1);
        GenLayer genlayeraddmushroomisland = new GenLayerAddMushroomIsland(5L, genlayeraddisland3);
        GenLayer genlayerdeepocean = new GenLayerDeepOcean(4L, genlayeraddmushroomisland);
        GenLayer genlayer4 = GenLayerZoom.magnify(1000L, genlayerdeepocean, 0);


        int biomeSize = getModdedBiomeSize(worldType, worldType == WorldType.LARGE_BIOMES ? 6 : 4);

        GenLayer genlayerbiomeedge = worldType.getBiomeLayer(seed, genlayer4, null);
        GenLayer genlayerhills = new GenLayerHills(1000L, genlayerbiomeedge, genlayerbiomeedge);
        GenLayer genlayersmooth = new GenLayerSmooth(1000L, genlayerbiomeedge);
        genlayerhills = new GenLayerRareBiome(1001L, genlayerhills);

        for (int k = 0; k < biomeSize; ++k)
        {
            genlayerhills = new GenLayerZoom((long) (1000 + k), genlayerhills);

            if (k == 0)
            {
                genlayerhills = new GenLayerAddIsland(3L, genlayerhills);
            }

            if (k == 1 || biomeSize == 1)
            {
                genlayerhills = new GenLayerShore(1000L, genlayerhills);
            }
        }

        GenLayer genlayersmooth1 = new GenLayerSmooth(1000L, genlayerhills);
        GenLayer genlayerTransition = new GenLayerRiverMix(100L, genlayersmooth1, genlayersmooth);
        GenLayer genlayer3 = new GenLayerVoronoiZoom(10L, genlayerTransition);
        genlayerTransition.initWorldGenSeed(seed);
        genlayer3.initWorldGenSeed(seed);
        return new GenLayer[]{genlayerTransition, genlayer3, genlayerTransition};
    }
}
