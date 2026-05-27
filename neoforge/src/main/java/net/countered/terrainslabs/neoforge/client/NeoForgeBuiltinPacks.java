package net.countered.terrainslabs.neoforge.client;

import net.countered.terrainslabs.TerrainSlabs;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.neoforge.event.AddPackFindersEvent;

public class NeoForgeBuiltinPacks {

    public static void addPack(AddPackFindersEvent event) {
        if (event.getPackType() != PackType.CLIENT_RESOURCES) return;

        event.addPackFinders(
                Identifier.fromNamespaceAndPath(TerrainSlabs.MOD_ID, "resourcepacks/better_grass_slabs"),
                PackType.CLIENT_RESOURCES,
                Component.literal("Better Grass Slabs"),
                PackSource.BUILT_IN,
                false,
                Pack.Position.TOP
        );
    }
}
