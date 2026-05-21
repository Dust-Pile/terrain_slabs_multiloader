package net.countered.terrainslabs.registries;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.countered.terrainslabs.TerrainSlabs;
import net.countered.terrainslabs.block.customslabs.soilslabs.GrassSlab;
import net.countered.terrainslabs.block.customslabs.soilslabs.MyceliumSlab;
import net.countered.terrainslabs.block.customslabs.soilslabs.PathSlab;
import net.countered.terrainslabs.block.customslabs.soilslabs.PodzolSlab;
import net.countered.terrainslabs.block.customslabs.specialslabs.CustomSlab;
import net.countered.terrainslabs.block.customslabs.specialslabs.GravityAffectedSlab;
import net.countered.terrainslabs.block.customslabs.specialslabs.MudSlab;
import net.countered.terrainslabs.block.customslabs.specialslabs.dimensions.NetherrackSlab;
import net.countered.terrainslabs.block.customslabs.specialslabs.dimensions.NyliumSlab;
import net.countered.terrainslabs.block.customslabs.specialslabs.dimensions.SoulSandSlab;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Function;

@SuppressWarnings("UnstableApiUsage")
public class ModBlocksRegistry {
    private static final Logger LOGGER = LogManager.getLogger(TerrainSlabs.MOD_ID);
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(TerrainSlabs.MOD_ID, Registries.BLOCK);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(TerrainSlabs.MOD_ID, Registries.ITEM);

    private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(TerrainSlabs.MOD_ID, Registries.CREATIVE_MODE_TAB);

    public static final RegistrySupplier<CreativeModeTab> TERRAIN_SLABS_TAB = TABS.register(
            "terrain_slabs",
            () -> CreativeTabRegistry.create(
                    Component.translatable("itemGroup.terrain_slabs"),
                    () -> new ItemStack(ModBlocksRegistry.GRASS_SLAB_ITEM.get())
            )
    );

    private static <T extends Block> RegistrySupplier<T> registerBlock(
            String name,
            BlockBehaviour.Properties props,
            Function<BlockBehaviour.Properties, T> factory) {

        ResourceKey<Block> key = ResourceKey.create(
                Registries.BLOCK,
                Identifier.parse(TerrainSlabs.MOD_ID + ":" + name)
        );
        return BLOCKS.register(name, () -> factory.apply(props.setId(key)));
    }

    private static <T extends Item> RegistrySupplier<T> registerItem(
            String name,
            Function<Item.Properties, T> factory) {

        ResourceKey<Item> key = ResourceKey.create(
                Registries.ITEM,
                Identifier.parse(TerrainSlabs.MOD_ID + ":" + name)
        );
        return ITEMS.register(name, () -> factory.apply(
                new Item.Properties()
                        .setId(key)
                        .arch$tab(TERRAIN_SLABS_TAB)
        ));
    }


    // BLOCKS
    public static final RegistrySupplier<Block> DIRT_SLAB = registerBlock(
            "dirt_slab",
            BlockBehaviour.Properties.ofFullCopy(Blocks.DIRT),
            CustomSlab::new
    );;
    public static final RegistrySupplier<Block> MUD_SLAB = registerBlock(
            "mud_slab",
            BlockBehaviour.Properties.ofFullCopy(Blocks.MUD).noOcclusion(),
            MudSlab::new
    );
    public static final RegistrySupplier<Block> COARSE_SLAB = registerBlock(
            "coarse_slab",
            BlockBehaviour.Properties.ofFullCopy(Blocks.COARSE_DIRT),
            CustomSlab::new
    );
    public static final RegistrySupplier<Block> SNOW_SLAB = registerBlock(
            "snow_slab",
            BlockBehaviour.Properties.ofFullCopy(Blocks.SNOW_BLOCK),
            CustomSlab::new
    );
    public static final RegistrySupplier<Block> PACKED_ICE_SLAB = registerBlock(
            "packed_ice_slab",
            BlockBehaviour.Properties.ofFullCopy(Blocks.PACKED_ICE),
            CustomSlab::new
    );
    public static final RegistrySupplier<Block> DEEPSLATE_SLAB = registerBlock(
            "deepslate_slab",
            BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE),
            CustomSlab::new
    );
    public static final RegistrySupplier<Block> CLAY_SLAB = registerBlock(
            "clay_slab",
            BlockBehaviour.Properties.ofFullCopy(Blocks.CLAY),
            CustomSlab::new
    );
    public static final RegistrySupplier<Block> MOSS_SLAB = registerBlock(
            "moss_slab",
            BlockBehaviour.Properties.ofFullCopy(Blocks.MOSS_BLOCK),
            CustomSlab::new
    );
    public static final RegistrySupplier<Block> CUSTOM_TUFF_SLAB = registerBlock(
            "terrain_tuff_slab",
            BlockBehaviour.Properties.ofFullCopy(Blocks.TUFF),
            CustomSlab::new
    );

    public static final RegistrySupplier<Block> GRASS_SLAB = registerBlock(
            "grass_slab",
            BlockBehaviour.Properties.ofFullCopy(Blocks.GRASS_BLOCK),
            GrassSlab::new
    );
    public static final RegistrySupplier<Block> MYCELIUM_SLAB = registerBlock(
            "mycelium_slab",
            BlockBehaviour.Properties.ofFullCopy(Blocks.MYCELIUM),
            MyceliumSlab::new
    );
    public static final RegistrySupplier<Block> PODZOL_SLAB = registerBlock(
            "podzol_slab",
            BlockBehaviour.Properties.ofFullCopy(Blocks.PODZOL),
            PodzolSlab::new
    );
    public static final RegistrySupplier<Block> PATH_SLAB = registerBlock(
            "path_slab",
            BlockBehaviour.Properties.ofFullCopy(Blocks.DIRT_PATH).noOcclusion(),
            PathSlab::new
    );

    public static final RegistrySupplier<Block> GRAVEL_SLAB = registerBlock(
            "gravel_slab",
            BlockBehaviour.Properties.ofFullCopy(Blocks.GRAVEL),
            GravityAffectedSlab::new
    );
    public static final RegistrySupplier<Block> SAND_SLAB = registerBlock(
            "sand_slab",
            BlockBehaviour.Properties.ofFullCopy(Blocks.SAND),
            GravityAffectedSlab::new
    );
    public static final RegistrySupplier<Block> RED_SAND_SLAB = registerBlock(
            "red_sand_slab",
            BlockBehaviour.Properties.ofFullCopy(Blocks.RED_SAND),
            GravityAffectedSlab::new
    );

    public static final RegistrySupplier<Block> TERRACOTTA_SLAB = registerBlock(
            "terracotta_slab",
            BlockBehaviour.Properties.ofFullCopy(Blocks.TERRACOTTA),
            CustomSlab::new
    );
    public static final RegistrySupplier<Block> RED_TERRACOTTA_SLAB = registerBlock(
            "red_terracotta_slab",
            BlockBehaviour.Properties.ofFullCopy(Blocks.RED_TERRACOTTA),
            CustomSlab::new
    );
    public static final RegistrySupplier<Block> ORANGE_TERRACOTTA_SLAB = registerBlock(
            "orange_terracotta_slab",
            BlockBehaviour.Properties.ofFullCopy(Blocks.ORANGE_TERRACOTTA),
            CustomSlab::new
    );
    public static final RegistrySupplier<Block> LIGHT_GRAY_TERRACOTTA_SLAB = registerBlock(
            "light_gray_terracotta_slab",
            BlockBehaviour.Properties.ofFullCopy(Blocks.LIGHT_GRAY_TERRACOTTA),
            CustomSlab::new
    );
    public static final RegistrySupplier<Block> WHITE_TERRACOTTA_SLAB = registerBlock(
            "white_terracotta_slab",
            BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_TERRACOTTA),
            CustomSlab::new
    );
    public static final RegistrySupplier<Block> BROWN_TERRACOTTA_SLAB = registerBlock(
            "brown_terracotta_slab",
            BlockBehaviour.Properties.ofFullCopy(Blocks.BROWN_TERRACOTTA),
            CustomSlab::new
    );
    public static final RegistrySupplier<Block> YELLOW_TERRACOTTA_SLAB = registerBlock(
            "yellow_terracotta_slab",
            BlockBehaviour.Properties.ofFullCopy(Blocks.YELLOW_TERRACOTTA),
            CustomSlab::new
    );

    public static final RegistrySupplier<Block> CUSTOM_STONE_SLAB = registerBlock(
            "terrain_stone_slab",
            BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_SLAB),
            CustomSlab::new
    );
    public static final RegistrySupplier<Block> CUSTOM_SANDSTONE_SLAB = registerBlock(
            "terrain_sandstone_slab",
            BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE_SLAB),
            CustomSlab::new
    );
    public static final RegistrySupplier<Block> CUSTOM_RED_SANDSTONE_SLAB = registerBlock(
            "terrain_red_sandstone_slab",
            BlockBehaviour.Properties.ofFullCopy(Blocks.RED_SANDSTONE_SLAB),
            CustomSlab::new
    );
    public static final RegistrySupplier<Block> CUSTOM_ANDESITE_SLAB = registerBlock(
            "terrain_andesite_slab",
            BlockBehaviour.Properties.ofFullCopy(Blocks.ANDESITE_SLAB),
            CustomSlab::new
    );
    public static final RegistrySupplier<Block> CUSTOM_DIORITE_SLAB = registerBlock(
            "terrain_diorite_slab",
            BlockBehaviour.Properties.ofFullCopy(Blocks.DIORITE_SLAB),
            CustomSlab::new
    );
    public static final RegistrySupplier<Block> CUSTOM_GRANITE_SLAB = registerBlock(
            "terrain_granite_slab",
            BlockBehaviour.Properties.ofFullCopy(Blocks.GRANITE_SLAB),
            CustomSlab::new
    );

    public static final RegistrySupplier<Block> SOUL_SAND_SLAB = registerBlock(
            "soul_sand_slab",
            BlockBehaviour.Properties.ofFullCopy(Blocks.SOUL_SAND).noOcclusion(),
            SoulSandSlab::new
    );
    public static final RegistrySupplier<Block> SOUL_SOIL_SLAB = registerBlock(
            "soul_soil_slab",
            BlockBehaviour.Properties.ofFullCopy(Blocks.SOUL_SOIL),
            CustomSlab::new
    );
    public static final RegistrySupplier<Block> NETHERRACK_SLAB = registerBlock(
            "netherrack_slab",
            BlockBehaviour.Properties.ofFullCopy(Blocks.NETHERRACK),
            NetherrackSlab::new
    );
    public static final RegistrySupplier<Block> WARPED_NYLIUM_SLAB = registerBlock(
            "warped_nylium_slab",
            BlockBehaviour.Properties.ofFullCopy(Blocks.WARPED_NYLIUM),
            NyliumSlab::new
    );
    public static final RegistrySupplier<Block> CRIMSON_NYLIUM_SLAB = registerBlock(
            "crimson_nylium_slab",
            BlockBehaviour.Properties.ofFullCopy(Blocks.CRIMSON_NYLIUM),
            NyliumSlab::new
    );
    public static final RegistrySupplier<Block> BASALT_SLAB = registerBlock(
            "basalt_slab",
            BlockBehaviour.Properties.ofFullCopy(Blocks.BASALT),
            CustomSlab::new
    );
    public static final RegistrySupplier<Block> CUSTOM_BLACKSTONE_SLAB = registerBlock(
            "terrain_blackstone_slab",
            BlockBehaviour.Properties.ofFullCopy(Blocks.BLACKSTONE_SLAB),
            CustomSlab::new
    );
    public static final RegistrySupplier<Block> ENDSTONE_SLAB = registerBlock(
            "endstone_slab",
            BlockBehaviour.Properties.ofFullCopy(Blocks.END_STONE),
            CustomSlab::new
    );

    // VANILLA BLOCKS USED BY TERRALITH
    public static final RegistrySupplier<Block> CALCITE_SLAB = registerBlock(
            "calcite_slab",
            BlockBehaviour.Properties.ofFullCopy(Blocks.CALCITE),
            CustomSlab::new
    );
    public static final RegistrySupplier<Block> SMOOTH_BASALT_SLAB = registerBlock(
            "smooth_basalt_slab",
            BlockBehaviour.Properties.ofFullCopy(Blocks.SMOOTH_BASALT),
            CustomSlab::new
    );
    public static final RegistrySupplier<Block> LIGHT_BLUE_TERRACOTTA_SLAB = registerBlock(
            "light_blue_terracotta_slab",
            BlockBehaviour.Properties.ofFullCopy(Blocks.LIGHT_BLUE_TERRACOTTA),
            CustomSlab::new
    );
    public static final RegistrySupplier<Block> CYAN_TERRACOTTA_SLAB = registerBlock(
            "cyan_terracotta_slab",
            BlockBehaviour.Properties.ofFullCopy(Blocks.CYAN_TERRACOTTA),
            CustomSlab::new
    );
    public static final RegistrySupplier<Block> CUSTOM_COBBLESTONE_SLAB = registerBlock(
            "terrain_cobblestone_slab",
            BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE_SLAB),
            CustomSlab::new
    );
    public static final RegistrySupplier<Block> CUSTOM_MOSSY_COBBLESTONE_SLAB = registerBlock(
            "terrain_mossy_cobblestone_slab",
            BlockBehaviour.Properties.ofFullCopy(Blocks.MOSSY_COBBLESTONE_SLAB),
            CustomSlab::new
    );
    public static final RegistrySupplier<Block> CUSTOM_COBBLED_DEEPSLATE_SLAB = registerBlock(
            "terrain_cobbled_deepslate_slab",
            BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLED_DEEPSLATE_SLAB),
            CustomSlab::new
    );
    public static final RegistrySupplier<Block> ICE_SLAB = registerBlock(
            "ice_slab",
            BlockBehaviour.Properties.ofFullCopy(Blocks.ICE),
            CustomSlab::new
    );
    public static final RegistrySupplier<Block> ROOTED_DIRT_SLAB = registerBlock(
            "rooted_dirt_slab",
            BlockBehaviour.Properties.ofFullCopy(Blocks.ROOTED_DIRT),
            CustomSlab::new
    );
    public static final RegistrySupplier<Block> PACKED_MUD_SLAB = registerBlock(
            "packed_mud_slab",
            BlockBehaviour.Properties.ofFullCopy(Blocks.PACKED_MUD),
            CustomSlab::new
    );
    public static final RegistrySupplier<Block> BLUE_ICE_SLAB = registerBlock(
            "blue_ice_slab",
            BlockBehaviour.Properties.ofFullCopy(Blocks.BLUE_ICE),
            CustomSlab::new
    );
    public static final RegistrySupplier<Block> BLACK_TERRACOTTA_SLAB = registerBlock(
            "black_terracotta_slab",
            BlockBehaviour.Properties.ofFullCopy(Blocks.BLACK_TERRACOTTA),
            CustomSlab::new
    );
    public static final RegistrySupplier<Block> CUSTOM_PRISMARINE_SLAB = registerBlock(
            "terrain_prismarine_slab",
            BlockBehaviour.Properties.ofFullCopy(Blocks.PRISMARINE_SLAB),
            CustomSlab::new
    );

    // ITEMS
    public static final RegistrySupplier<Item> DIRT_SLAB_ITEM = registerItem(
            "dirt_slab",
            props -> new BlockItem(DIRT_SLAB.get(), props)
    );
    public static final RegistrySupplier<Item> MUD_SLAB_ITEM = registerItem(
            "mud_slab",
            props -> new BlockItem(MUD_SLAB.get(), props)
    );
    public static final RegistrySupplier<Item> COARSE_SLAB_ITEM = registerItem(
            "coarse_slab",
            props -> new BlockItem(COARSE_SLAB.get(), props)
    );
    public static final RegistrySupplier<Item> SNOW_SLAB_ITEM = registerItem(
            "snow_slab",
            props -> new BlockItem(SNOW_SLAB.get(), props)
    );
    public static final RegistrySupplier<Item> PACKED_ICE_SLAB_ITEM = registerItem(
            "packed_ice_slab",
            props -> new BlockItem(PACKED_ICE_SLAB.get(), props)
    );
    public static final RegistrySupplier<Item> DEEPSLATE_SLAB_ITEM = registerItem(
            "deepslate_slab",
            props -> new BlockItem(DEEPSLATE_SLAB.get(), props)
    );
    public static final RegistrySupplier<Item> CLAY_SLAB_ITEM = registerItem(
            "clay_slab",
            props -> new BlockItem(CLAY_SLAB.get(), props)
    );
    public static final RegistrySupplier<Item> MOSS_SLAB_ITEM = registerItem(
            "moss_slab",
            props -> new BlockItem(MOSS_SLAB.get(), props)
    );
    public static final RegistrySupplier<Item> CUSTOM_TUFF_SLAB_ITEM = registerItem(
            "terrain_tuff_slab",
            props -> new BlockItem(CUSTOM_TUFF_SLAB.get(), props)
    );

    public static final RegistrySupplier<Item> GRASS_SLAB_ITEM = registerItem(
            "grass_slab",
            props -> new BlockItem(GRASS_SLAB.get(), props)
    );
    public static final RegistrySupplier<Item> MYCELIUM_SLAB_ITEM = registerItem(
            "mycelium_slab",
            props -> new BlockItem(MYCELIUM_SLAB.get(), props)
    );
    public static final RegistrySupplier<Item> PODZOL_SLAB_ITEM = registerItem(
            "podzol_slab",
            props -> new BlockItem(PODZOL_SLAB.get(), props)
    );
    public static final RegistrySupplier<Item> PATH_SLAB_ITEM = registerItem(
            "path_slab",
            props -> new BlockItem(PATH_SLAB.get(), props)
    );
    public static final RegistrySupplier<Item> GRAVEL_SLAB_ITEM = registerItem(
            "gravel_slab",
            props -> new BlockItem(GRAVEL_SLAB.get(), props)
    );
    public static final RegistrySupplier<Item> SAND_SLAB_ITEM = registerItem(
            "sand_slab",
            props -> new BlockItem(SAND_SLAB.get(), props)
    );
    public static final RegistrySupplier<Item> RED_SAND_SLAB_ITEM = registerItem(
            "red_sand_slab",
            props -> new BlockItem(RED_SAND_SLAB.get(), props)
    );

    public static final RegistrySupplier<Item> TERRACOTTA_SLAB_ITEM = registerItem(
            "terracotta_slab",
            props -> new BlockItem(TERRACOTTA_SLAB.get(), props)
    );
    public static final RegistrySupplier<Item> RED_TERRACOTTA_SLAB_ITEM = registerItem(
            "red_terracotta_slab",
            props -> new BlockItem(RED_TERRACOTTA_SLAB.get(), props)
    );
    public static final RegistrySupplier<Item> ORANGE_TERRACOTTA_SLAB_ITEM = registerItem(
            "orange_terracotta_slab",
            props -> new BlockItem(ORANGE_TERRACOTTA_SLAB.get(), props)
    );
    public static final RegistrySupplier<Item> LIGHT_GRAY_TERRACOTTA_SLAB_ITEM = registerItem(
            "light_gray_terracotta_slab",
            props -> new BlockItem(LIGHT_GRAY_TERRACOTTA_SLAB.get(), props)
    );
    public static final RegistrySupplier<Item> WHITE_TERRACOTTA_SLAB_ITEM = registerItem(
            "white_terracotta_slab",
            props -> new BlockItem(WHITE_TERRACOTTA_SLAB.get(), props)
    );
    public static final RegistrySupplier<Item> BROWN_TERRACOTTA_SLAB_ITEM = registerItem(
            "brown_terracotta_slab",
            props -> new BlockItem(BROWN_TERRACOTTA_SLAB.get(), props)
    );
    public static final RegistrySupplier<Item> YELLOW_TERRACOTTA_SLAB_ITEM = registerItem(
            "yellow_terracotta_slab",
            props -> new BlockItem(YELLOW_TERRACOTTA_SLAB.get(), props)
    );

    public static final RegistrySupplier<Item> CUSTOM_STONE_SLAB_ITEM = registerItem(
            "terrain_stone_slab",
            props -> new BlockItem(CUSTOM_STONE_SLAB.get(), props)
    );
    public static final RegistrySupplier<Item> CUSTOM_SANDSTONE_SLAB_ITEM = registerItem(
            "terrain_sandstone_slab",
            props -> new BlockItem(CUSTOM_SANDSTONE_SLAB.get(), props)
    );
    public static final RegistrySupplier<Item> CUSTOM_RED_SANDSTONE_SLAB_ITEM = registerItem(
            "terrain_red_sandstone_slab",
            props -> new BlockItem(CUSTOM_RED_SANDSTONE_SLAB.get(), props)
    );
    public static final RegistrySupplier<Item> CUSTOM_ANDESITE_SLAB_ITEM = registerItem(
            "terrain_andesite_slab",
            props -> new BlockItem(CUSTOM_ANDESITE_SLAB.get(), props)
    );
    public static final RegistrySupplier<Item> CUSTOM_DIORITE_SLAB_ITEM = registerItem(
            "terrain_diorite_slab",
            props -> new BlockItem(CUSTOM_DIORITE_SLAB.get(), props)
    );
    public static final RegistrySupplier<Item> CUSTOM_GRANITE_SLAB_ITEM = registerItem(
            "terrain_granite_slab",
            props -> new BlockItem(CUSTOM_GRANITE_SLAB.get(), props)
    );

    public static final RegistrySupplier<Item> SOUL_SAND_SLAB_ITEM = registerItem(
            "soul_sand_slab",
            props -> new BlockItem(SOUL_SAND_SLAB.get(), props)
    );
    public static final RegistrySupplier<Item> SOUL_SOIL_SLAB_ITEM = registerItem(
            "soul_soil_slab",
            props -> new BlockItem(SOUL_SOIL_SLAB.get(), props)
    );
    public static final RegistrySupplier<Item> NETHERRACK_SLAB_ITEM = registerItem(
            "netherrack_slab",
            props -> new BlockItem(NETHERRACK_SLAB.get(), props)
    );
    public static final RegistrySupplier<Item> WARPED_NYLIUM_SLAB_ITEM = registerItem(
            "warped_nylium_slab",
            props -> new BlockItem(WARPED_NYLIUM_SLAB.get(), props)
    );
    public static final RegistrySupplier<Item> CRIMSON_NYLIUM_SLAB_ITEM = registerItem(
            "crimson_nylium_slab",
            props -> new BlockItem(CRIMSON_NYLIUM_SLAB.get(), props)
    );
    public static final RegistrySupplier<Item> BASALT_SLAB_ITEM = registerItem(
            "basalt_slab",
            props -> new BlockItem(BASALT_SLAB.get(), props)
    );
    public static final RegistrySupplier<Item> CUSTOM_BLACKSTONE_SLAB_ITEM = registerItem(
            "terrain_blackstone_slab",
            props -> new BlockItem(CUSTOM_BLACKSTONE_SLAB.get(), props)
    );
    public static final RegistrySupplier<Item> ENDSTONE_SLAB_ITEM = registerItem(
            "endstone_slab",
            props -> new BlockItem(ENDSTONE_SLAB.get(), props)
    );

    public static final RegistrySupplier<Item> CALCITE_SLAB_ITEM = registerItem(
            "calcite_slab",
            props -> new BlockItem(CALCITE_SLAB.get(), props)
    );
    public static final RegistrySupplier<Item> SMOOTH_BASALT_SLAB_ITEM = registerItem(
            "smooth_basalt_slab",
            props -> new BlockItem(SMOOTH_BASALT_SLAB.get(), props)
    );
    public static final RegistrySupplier<Item> LIGHT_BLUE_TERRACOTTA_SLAB_ITEM = registerItem(
            "light_blue_terracotta_slab",
            props -> new BlockItem(LIGHT_BLUE_TERRACOTTA_SLAB.get(), props)
    );
    public static final RegistrySupplier<Item> CYAN_TERRACOTTA_SLAB_ITEM = registerItem(
            "cyan_terracotta_slab",
            props -> new BlockItem(CYAN_TERRACOTTA_SLAB.get(), props)
    );
    public static final RegistrySupplier<Item> CUSTOM_COBBLESTONE_SLAB_ITEM = registerItem(
            "terrain_cobblestone_slab",
            props -> new BlockItem(CUSTOM_COBBLESTONE_SLAB.get(), props)
    );
    public static final RegistrySupplier<Item> CUSTOM_MOSSY_COBBLESTONE_SLAB_ITEM = registerItem(
            "terrain_mossy_cobblestone_slab",
            props -> new BlockItem(CUSTOM_MOSSY_COBBLESTONE_SLAB.get(), props)
    );
    public static final RegistrySupplier<Item> CUSTOM_COBBLED_DEEPSLATE_SLAB_ITEM = registerItem(
            "terrain_cobbled_deepslate_slab",
            props -> new BlockItem(CUSTOM_COBBLED_DEEPSLATE_SLAB.get(), props)
    );
    public static final RegistrySupplier<Item> ICE_SLAB_ITEM = registerItem(
            "ice_slab",
            props -> new BlockItem(ICE_SLAB.get(), props)
    );
    public static final RegistrySupplier<Item> ROOTED_DIRT_SLAB_ITEM = registerItem(
            "rooted_dirt_slab",
            props -> new BlockItem(ROOTED_DIRT_SLAB.get(), props)
    );
    public static final RegistrySupplier<Item> PACKED_MUD_SLAB_ITEM = registerItem(
            "packed_mud_slab",
            props -> new BlockItem(PACKED_MUD_SLAB.get(), props)
    );
    public static final RegistrySupplier<Item> BLUE_ICE_SLAB_ITEM = registerItem(
            "blue_ice_slab",
            props -> new BlockItem(BLUE_ICE_SLAB.get(), props)
    );
    public static final RegistrySupplier<Item> BLACK_TERRACOTTA_SLAB_ITEM = registerItem(
            "black_terracotta_slab",
            props -> new BlockItem(BLACK_TERRACOTTA_SLAB.get(), props)
    );
    public static final RegistrySupplier<Item> CUSTOM_PRISMARINE_SLAB_ITEM = registerItem(
            "terrain_prismarine_slab",
            props -> new BlockItem(CUSTOM_PRISMARINE_SLAB.get(), props)
    );

    public static void registerModBlocks() {
        LOGGER.info("Registering Mod Blocks & Items for " + TerrainSlabs.MOD_ID);
        TABS.register();
        BLOCKS.register();
        ITEMS.register();
    }
}
