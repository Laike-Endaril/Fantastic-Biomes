package com.fantasticsource.fantasticbiomes.biome;

import com.fantasticsource.tools.datastructures.DecimalWeightedPool;
import net.minecraft.block.BlockColored;
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
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.Random;

import static com.fantasticsource.fantasticbiomes.FantasticBiomes.MODID;

public class BiomePaintedDesert extends Biome
{
    public static final DecimalWeightedPool<IBlockState> STAINED_CLAY_POOL = new DecimalWeightedPool<>();
    public static final HashMap<World, NoiseGeneratorSimplex> SIMPLEX_GENERATORS = new HashMap<>();

    protected static final IBlockState
            STAINED_HARDENED_CLAY = Blocks.STAINED_HARDENED_CLAY.getDefaultState(),
            LIGHT_BLUE_HARDENED_CLAY = STAINED_HARDENED_CLAY.withProperty(BlockColored.COLOR, EnumDyeColor.LIGHT_BLUE),
            MAGENTA_HARDENED_CLAY = STAINED_HARDENED_CLAY.withProperty(BlockColored.COLOR, EnumDyeColor.MAGENTA),
            RED_HARDENED_CLAY = STAINED_HARDENED_CLAY.withProperty(BlockColored.COLOR, EnumDyeColor.RED),
            ORANGE_HARDENED_CLAY = STAINED_HARDENED_CLAY.withProperty(BlockColored.COLOR, EnumDyeColor.ORANGE),
            YELLOW_HARDENED_CLAY = STAINED_HARDENED_CLAY.withProperty(BlockColored.COLOR, EnumDyeColor.YELLOW),
            SILVER_HARDENED_CLAY = STAINED_HARDENED_CLAY.withProperty(BlockColored.COLOR, EnumDyeColor.SILVER),
            BROWN_HARDENED_CLAY = STAINED_HARDENED_CLAY.withProperty(BlockColored.COLOR, EnumDyeColor.BROWN);

    static
    {
        STAINED_CLAY_POOL.addWeight(SILVER_HARDENED_CLAY, 1);
        STAINED_CLAY_POOL.addWeight(YELLOW_HARDENED_CLAY, 0.75);
        STAINED_CLAY_POOL.addWeight(ORANGE_HARDENED_CLAY, 1);
        STAINED_CLAY_POOL.addWeight(RED_HARDENED_CLAY, 3);
        STAINED_CLAY_POOL.addWeight(MAGENTA_HARDENED_CLAY, 1);
        STAINED_CLAY_POOL.addWeight(LIGHT_BLUE_HARDENED_CLAY, 1);
    }

    public BiomePaintedDesert()
    {
        super(new Biome.BiomeProperties("Painted Desert").setTemperature(2).setRainfall(0).setRainDisabled().setBaseHeight(1).setHeightVariation(0.5f));
        setRegistryName(new ResourceLocation(MODID, "painted_desert"));
        BiomeManager.addBiome(BiomeManager.BiomeType.DESERT, new BiomeManager.BiomeEntry(this, 20));

        fillerBlock = RED_HARDENED_CLAY;

        decorator.generateFalls = false; //Should be named "generateLakes"
        decorator.flowersPerChunk = -999;
        decorator.treesPerChunk = -999;
        decorator.bigMushroomsPerChunk = -999;
        decorator.reedsPerChunk = -999;
        decorator.deadBushPerChunk = -999;
        decorator.cactiPerChunk = -999;

        spawnableCreatureList.clear();
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
        //Somewhere, something weird is going on, and I had to flip the x and z values here to fix it (vanilla also flips them in BiomeMesa.genTerrainBlocks())
        int xx = z & 15, zz = x & 15;
        int seaLevel = world.getSeaLevel();
        for (int y = (int) (seaLevel - 10 + noiseVal); y < 256; y++)
        {
            IBlockState primedBlockstate = chunkPrimer.getBlockState(xx, y, zz);
            if (primedBlockstate.getMaterial().isSolid()) chunkPrimer.setBlockState(xx, y, zz, getClayForHeightOffset(world, y - seaLevel));
        }
    }

    public IBlockState getClayForHeightOffset(World world, int yFromSeaLevel)
    {
        if (yFromSeaLevel <= 2) return BROWN_HARDENED_CLAY;
        if (yFromSeaLevel == 3) return ORANGE_HARDENED_CLAY;

        NoiseGeneratorSimplex simplex = SIMPLEX_GENERATORS.computeIfAbsent(world, o -> new NoiseGeneratorSimplex(new Random(world.getSeed())));
        return STAINED_CLAY_POOL.getRandom((1 + simplex.getValue(0, yFromSeaLevel)) * 0.5);
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

    @SubscribeEvent
    public static void cleanup(WorldEvent.Unload event)
    {
        SIMPLEX_GENERATORS.remove(event.getWorld());
    }
}
