package com.fantasticsource.fantasticbiomes.biome;

import net.minecraft.block.BlockFalling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;

import java.util.Random;

public class BiomeBetterBeach extends Biome
{
    public static final IBlockState
            AIR = Blocks.AIR.getDefaultState(),
            SAND = Blocks.SAND.getDefaultState(),
            SANDSTONE = Blocks.SANDSTONE.getDefaultState(),
            WATER = Blocks.WATER.getDefaultState();

    public BiomeBetterBeach()
    {
        super(new Biome.BiomeProperties("Beach").setBaseHeight(0.0F).setHeightVariation(0.01F).setTemperature(0.8F).setRainfall(0.4F));
        setRegistryName(new ResourceLocation("minecraft", "beaches"));

        spawnableCreatureList.clear();
        topBlock = Blocks.SAND.getDefaultState();
        fillerBlock = Blocks.SAND.getDefaultState();
        decorator.treesPerChunk = -999;
        decorator.deadBushPerChunk = 0;
        decorator.reedsPerChunk = 0;
        decorator.cactiPerChunk = 0;
    }

    @Override
    public void genTerrainBlocks(World world, Random rand, ChunkPrimer chunkPrimer, int x, int z, double noiseVal)
    {
        //Somewhere, something weird is going on, and I had to flip the x and z values here to fix it (vanilla also flips them in BiomeMesa.genTerrainBlocks())
        int xx = z & 15, zz = x & 15, seaLevel = world.getSeaLevel(), y = 255;

        //Find first solid block (from top down)
        IBlockState primedBlockstate = chunkPrimer.getBlockState(xx, y, zz);
        while (!primedBlockstate.getMaterial().isSolid()) primedBlockstate = chunkPrimer.getBlockState(xx, --y, zz);

        //If the terrain height is above our sand threshold, either let the inland biome handle this column
        int topSandThreshold = (int) (seaLevel + Math.abs(noiseVal) * 0.5);
        if (y > topSandThreshold)
        {
            Biome higherBiome;
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, 0, z);
            do
            {
                pos.move(EnumFacing.SOUTH, 4);
                higherBiome = world.getBiome(pos);
            }
            while (higherBiome instanceof BiomeBetterBeach);

            if (higherBiome.getBaseHeight() <= getBaseHeight())
            {
                pos.setPos(x, 0, z);
                do
                {
                    pos.move(EnumFacing.NORTH, 4);
                    higherBiome = world.getBiome(pos);
                }
                while (higherBiome instanceof BiomeBetterBeach);
            }

            if (higherBiome.getBaseHeight() <= getBaseHeight())
            {
                pos.setPos(x, 0, z);
                do
                {
                    pos.move(EnumFacing.EAST, 4);
                    higherBiome = world.getBiome(pos);
                }
                while (higherBiome instanceof BiomeBetterBeach);
            }

            if (higherBiome.getBaseHeight() <= getBaseHeight())
            {
                pos.setPos(x, 0, z);
                do
                {
                    pos.move(EnumFacing.WEST, 4);
                    higherBiome = world.getBiome(pos);
                }
                while (higherBiome instanceof BiomeBetterBeach);
            }

            if (higherBiome.getBaseHeight() > getBaseHeight())
            {
                higherBiome.genTerrainBlocks(world, rand, chunkPrimer, x, z, noiseVal);
                return;
            }


            //If there is no higher neighboring biome, replace blocks above threshold with air
            for (; y > topSandThreshold; y--) chunkPrimer.setBlockState(xx, y, zz, AIR);
        }


        //For blocks from the top sand height (above) to sea level, make sure solids are sand and non-solids are air
        for (; y >= seaLevel; y--)
        {
            primedBlockstate = chunkPrimer.getBlockState(xx, y, zz);
            if (primedBlockstate.getMaterial().isSolid()) chunkPrimer.setBlockState(xx, y, zz, SAND);
            else chunkPrimer.setBlockState(xx, y, zz, AIR);
        }

        //Below sea level; make sure all non-solid blocks are water until we find a solid block
        primedBlockstate = chunkPrimer.getBlockState(xx, y, zz);
        while (!primedBlockstate.getMaterial().isSolid())
        {
            chunkPrimer.setBlockState(xx, y, zz, WATER);
            primedBlockstate = chunkPrimer.getBlockState(xx, --y, zz);
        }

        //Make sure first few solid underwater blocks are sand (sand depth slightly randomized)
        for (int i = 3 + (int) (Math.abs(noiseVal) * 0.5); i > 0 && y > 0; i--) chunkPrimer.setBlockState(xx, y--, zz, SAND);

        //Find the first non-falling block below the sand we just placed
        primedBlockstate = chunkPrimer.getBlockState(xx, y, zz);
        while (primedBlockstate.getBlock() instanceof BlockFalling) primedBlockstate = chunkPrimer.getBlockState(xx, --y, zz);

        //If the first non-falling block is not solid, replace it with sandstone to hold up the falling blocks
        if (!primedBlockstate.getMaterial().isSolid()) chunkPrimer.setBlockState(xx, y, zz, SANDSTONE);
    }
}
