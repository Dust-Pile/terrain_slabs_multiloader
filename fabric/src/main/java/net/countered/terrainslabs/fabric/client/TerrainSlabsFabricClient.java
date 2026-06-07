package net.countered.terrainslabs.fabric.client;

import net.countered.terrainslabs.TerrainSlabs;
import net.countered.terrainslabs.registries.ModBlocksRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.color.block.BlockTintSources;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;

public final class TerrainSlabsFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        registerBlockColorProviders();
        registerBuiltinResourcePacks();
    }

    private void registerBlockColorProviders() {
        BlockColorRegistry.register(List.of(BlockTintSources.grass()), ModBlocksRegistry.GRASS_SLAB.get());
    }

    private void registerBuiltinResourcePacks() {
        ModContainer mod = FabricLoader.getInstance().getModContainer(TerrainSlabs.MOD_ID).orElseThrow();
        ResourceManagerHelper.registerBuiltinResourcePack(
                Identifier.fromNamespaceAndPath(TerrainSlabs.MOD_ID, "better_grass_slabs"),
                mod,
                Component.literal("Better Grass Slabs"),
                ResourcePackActivationType.NORMAL
        );
    }
}
