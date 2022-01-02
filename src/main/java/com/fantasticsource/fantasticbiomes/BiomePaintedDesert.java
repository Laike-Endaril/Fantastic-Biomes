package com.fantasticsource.fantasticbiomes;

import net.minecraft.block.BlockColored;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.NoiseGeneratorSimplex;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.Random;

import static com.fantasticsource.fantasticbiomes.FantasticBiomes.MODID;

public class BiomePaintedDesert extends Biome
{
    //NOTE: The decorator boolean and the event for lakes BOTH generate lakes; you need to disable them both to remove lakes from a biome

    public static final HashMap<World, NoiseGeneratorSimplex> SIMPLEX_GENERATORS = new HashMap<>();

    protected static final IBlockState
            STAINED_HARDENED_CLAY = Blocks.STAINED_HARDENED_CLAY.getDefaultState(),
            RED_HARDENED_CLAY = STAINED_HARDENED_CLAY.withProperty(BlockColored.COLOR, EnumDyeColor.RED),
            ORANGE_HARDENED_CLAY = STAINED_HARDENED_CLAY.withProperty(BlockColored.COLOR, EnumDyeColor.ORANGE),
            YELLOW_HARDENED_CLAY = STAINED_HARDENED_CLAY.withProperty(BlockColored.COLOR, EnumDyeColor.YELLOW),
            BROWN_HARDENED_CLAY = STAINED_HARDENED_CLAY.withProperty(BlockColored.COLOR, EnumDyeColor.BROWN);

    BiomePaintedDesert()
    {
        super(new Biome.BiomeProperties("Painted Desert").setTemperature(2).setRainfall(0).setRainDisabled().setBaseHeight(1).setHeightVariation(0.5f));
        setRegistryName(new ResourceLocation(MODID, "painted_desert"));
        BiomeManager.addBiome(BiomeManager.BiomeType.DESERT, new BiomeManager.BiomeEntry(this, 1000));
        BiomeManager.addSpawnBiome(this);

        fillerBlock = RED_HARDENED_CLAY;

        decorator.generateFalls = false; //Should be named "generateLakes"
        decorator.flowersPerChunk = -999;
        decorator.treesPerChunk = -999;
        decorator.bigMushroomsPerChunk = -999;
        decorator.reedsPerChunk = -999;
        decorator.deadBushPerChunk = -999;
        decorator.cactiPerChunk = -999;

        spawnableWaterCreatureList.clear();
    }

    @Override
    public float getSpawningChance()
    {
        return 0;
    }

    @Override
    public void genTerrainBlocks(World world, Random rand, ChunkPrimer chunkPrimer, int x, int z, double noiseVal)
    {
        x &= 15;
        z &= 15;
        int seaLevel = world.getSeaLevel();
        for (int y = (int) (seaLevel - 10 + noiseVal); y < 256; y++)
        {
            IBlockState primedBlockstate = chunkPrimer.getBlockState(x, y, z);
            if (primedBlockstate.getMaterial() != Material.AIR)
            {
                chunkPrimer.setBlockState(x, y, z, getClayForHeightOffset(world, y - seaLevel));
            }
        }
    }

    public IBlockState getClayForHeightOffset(World world, int yFromSeaLevel)
    {
        if (yFromSeaLevel <= 2) return BROWN_HARDENED_CLAY;
        if (yFromSeaLevel == 3) return ORANGE_HARDENED_CLAY;

        NoiseGeneratorSimplex simplex = SIMPLEX_GENERATORS.computeIfAbsent(world, o -> new NoiseGeneratorSimplex(new Random(world.getSeed())));
        double d = (1 + simplex.getValue(0, yFromSeaLevel)) * 0.5;
        if (d < 0.2) return YELLOW_HARDENED_CLAY;
        if (d < 0.5) return ORANGE_HARDENED_CLAY;
        return RED_HARDENED_CLAY;
    }

    @Override
    public void decorate(World worldIn, Random rand, BlockPos pos)
    {
    }

    @Override
    public BiomeDecorator createBiomeDecorator()
    {
        return getModdedBiomeDecorator(new BiomeDecorator());
    }

    @SubscribeEvent
    public static void removeLakes(PopulateChunkEvent.Populate event)
    {
        PopulateChunkEvent.Populate.EventType type = event.getType();
        if (type == PopulateChunkEvent.Populate.EventType.LAKE || type == PopulateChunkEvent.Populate.EventType.ICE)
        {
            World world = event.getWorld();
            BlockPos
                    x0z0 = new BlockPos(event.getChunkX() << 4, 0, event.getChunkZ() << 4),
                    x15z0 = x0z0.east(15),
                    x0z15 = x0z0.south(15),
                    x15z15 = x15z0.south(15),
                    x7z7 = x0z0.add(7, 0, 7);

            if (world.getBiome(x0z0) instanceof BiomePaintedDesert
                    || world.getBiome(x15z0) instanceof BiomePaintedDesert
                    || world.getBiome(x0z15) instanceof BiomePaintedDesert
                    || world.getBiome(x15z15) instanceof BiomePaintedDesert
                    || world.getBiome(x7z7) instanceof BiomePaintedDesert)
                event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public static void removeLakes2(DecorateBiomeEvent.Decorate event)
    {
        DecorateBiomeEvent.Decorate.EventType type = event.getType();
        if (type == DecorateBiomeEvent.Decorate.EventType.LAKE_WATER || type == DecorateBiomeEvent.Decorate.EventType.LAKE_LAVA)
        {
            World world = event.getWorld();
            ChunkPos chunkPos = event.getChunkPos();
            BlockPos
                    x0z0 = new BlockPos(chunkPos.x << 4, 0, chunkPos.z << 4),
                    x15z0 = x0z0.east(15),
                    x0z15 = x0z0.south(15),
                    x15z15 = x15z0.south(15),
                    x7z7 = x0z0.add(7, 0, 7);

            if (world.getBiome(x0z0) instanceof BiomePaintedDesert
                    || world.getBiome(x15z0) instanceof BiomePaintedDesert
                    || world.getBiome(x0z15) instanceof BiomePaintedDesert
                    || world.getBiome(x15z15) instanceof BiomePaintedDesert
                    || world.getBiome(x7z7) instanceof BiomePaintedDesert)
                event.setResult(Event.Result.DENY);
        }
    }
}
