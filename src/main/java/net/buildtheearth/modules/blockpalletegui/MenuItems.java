package net.buildtheearth.modules.blockpalletegui;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MenuItems {

    private static final List<ItemStack> BLOCKS_BY_COLOR = Arrays.asList(
            XMaterial.CHISELED_POLISHED_BLACKSTONE.parseItem(),
            XMaterial.CRACKED_POLISHED_BLACKSTONE_BRICKS.parseItem(),
            XMaterial.POLISHED_BLACKSTONE_BRICKS.parseItem(),
            XMaterial.POLISHED_BLACKSTONE_BRICK_STAIRS.parseItem(),
            XMaterial.POLISHED_BLACKSTONE_BRICK_SLAB.parseItem(),
            XMaterial.POLISHED_BLACKSTONE_BRICK_WALL.parseItem(),
            XMaterial.POLISHED_BLACKSTONE.parseItem(),
            XMaterial.POLISHED_BLACKSTONE_STAIRS.parseItem(),
            XMaterial.POLISHED_BLACKSTONE_SLAB.parseItem(),
            XMaterial.POLISHED_BLACKSTONE_WALL.parseItem(),
            XMaterial.POLISHED_BLACKSTONE_PRESSURE_PLATE.parseItem(),
            XMaterial.POLISHED_BLACKSTONE_BUTTON.parseItem(),
            XMaterial.BLACKSTONE.parseItem(),
            XMaterial.BLACKSTONE_STAIRS.parseItem(),
            XMaterial.BLACKSTONE_SLAB.parseItem(),
            XMaterial.BLACKSTONE_WALL.parseItem(),
            XMaterial.GILDED_BLACKSTONE.parseItem(),
            XMaterial.BLACK_CONCRETE_POWDER.parseItem(),
            XMaterial.BLACK_WOOL.parseItem(),
            XMaterial.BLACK_CARPET.parseItem(),
            XMaterial.OBSIDIAN.parseItem(),
            XMaterial.GRAY_STAINED_GLASS.parseItem(),
            XMaterial.NETHERITE_BLOCK.parseItem(),
            XMaterial.MUD.parseItem(),
            XMaterial.CHISELED_DEEPSLATE.parseItem(),
            XMaterial.CRACKED_DEEPSLATE_TILES.parseItem(),
            XMaterial.DEEPSLATE_TILES.parseItem(),
            XMaterial.DEEPSLATE_TILE_STAIRS.parseItem(),
            XMaterial.DEEPSLATE_TILE_SLAB.parseItem(),
            XMaterial.DEEPSLATE_TILE_WALL.parseItem(),
            XMaterial.CRACKED_DEEPSLATE_BRICKS.parseItem(),
            XMaterial.DEEPSLATE_BRICKS.parseItem(),
            XMaterial.DEEPSLATE_BRICK_STAIRS.parseItem(),
            XMaterial.DEEPSLATE_BRICK_SLAB.parseItem(),
            XMaterial.DEEPSLATE_BRICK_WALL.parseItem(),
            XMaterial.POLISHED_DEEPSLATE.parseItem(),
            XMaterial.POLISHED_DEEPSLATE_STAIRS.parseItem(),
            XMaterial.POLISHED_DEEPSLATE_SLAB.parseItem(),
            XMaterial.POLISHED_DEEPSLATE_WALL.parseItem(),
            XMaterial.COBBLED_DEEPSLATE.parseItem(),
            XMaterial.COBBLED_DEEPSLATE_STAIRS.parseItem(),
            XMaterial.COBBLED_DEEPSLATE_SLAB.parseItem(),
            XMaterial.COBBLED_DEEPSLATE_WALL.parseItem(),
            XMaterial.DEEPSLATE.parseItem(),
            XMaterial.SMOOTH_BASALT.parseItem(),
            XMaterial.POLISHED_BASALT.parseItem(),
            XMaterial.BASALT.parseItem(),
            XMaterial.GRAY_CONCRETE.parseItem(),
            XMaterial.GRAY_WOOL.parseItem(),
            XMaterial.GRAY_CARPET.parseItem(),
            XMaterial.GRAY_CONCRETE_POWDER.parseItem(),
            XMaterial.CYAN_TERRACOTTA.parseItem(),
            XMaterial.GRAY_GLAZED_TERRACOTTA.parseItem(),
            XMaterial.TUFF.parseItem(),
            XMaterial.LIGHT_GRAY_CONCRETE.parseItem(),
            XMaterial.COBBLESTONE.parseItem(),
            XMaterial.COBBLESTONE_STAIRS.parseItem(),
            XMaterial.COBBLESTONE_SLAB.parseItem(),
            XMaterial.COBBLESTONE_WALL.parseItem(),
            XMaterial.CRACKED_STONE_BRICKS.parseItem(),
            XMaterial.STONE_BRICKS.parseItem(),
            XMaterial.STONE_BRICK_STAIRS.parseItem(),
            XMaterial.STONE_BRICK_SLAB.parseItem(),
            XMaterial.STONE_BRICK_WALL.parseItem(),
            XMaterial.LIGHT_GRAY_STAINED_GLASS.parseItem(),
            XMaterial.POLISHED_ANDESITE.parseItem(),
            XMaterial.POLISHED_ANDESITE_STAIRS.parseItem(),
            XMaterial.POLISHED_ANDESITE_SLAB.parseItem(),
            XMaterial.STONE.parseItem(),
            XMaterial.STONE_STAIRS.parseItem(),
            XMaterial.STONE_SLAB.parseItem(),
            XMaterial.STONE_PRESSURE_PLATE.parseItem(),
            XMaterial.STONE_BUTTON.parseItem(),
            XMaterial.ANDESITE.parseItem(),
            XMaterial.ANDESITE_STAIRS.parseItem(),
            XMaterial.ANDESITE_SLAB.parseItem(),
            XMaterial.ANDESITE_WALL.parseItem(),
            XMaterial.LIGHT_GRAY_WOOL.parseItem(),
            XMaterial.LIGHT_GRAY_CARPET.parseItem(),
            XMaterial.LIGHT_GRAY_CONCRETE_POWDER.parseItem(),
            XMaterial.CLAY.parseItem(),
            XMaterial.LIGHT_GRAY_GLAZED_TERRACOTTA.parseItem(),
            XMaterial.SEA_LANTERN.parseItem(),
            XMaterial.LODESTONE.parseItem(),
            XMaterial.WHITE_GLAZED_TERRACOTTA.parseItem(),
            XMaterial.BONE_BLOCK.parseItem(),
            XMaterial.BIRCH_LOG.parseItem(),
            XMaterial.POLISHED_DIORITE.parseItem(),
            XMaterial.POLISHED_DIORITE_STAIRS.parseItem(),
            XMaterial.POLISHED_DIORITE_SLAB.parseItem(),
            XMaterial.DIORITE.parseItem(),
            XMaterial.DIORITE_STAIRS.parseItem(),
            XMaterial.DIORITE_SLAB.parseItem(),
            XMaterial.DIORITE_WALL.parseItem(),
            XMaterial.CALCITE.parseItem(),
            XMaterial.WHITE_CONCRETE.parseItem(),
            XMaterial.QUARTZ_BRICKS.parseItem(),
            XMaterial.CHISELED_QUARTZ_BLOCK.parseItem(),
            XMaterial.QUARTZ_PILLAR.parseItem(),
            XMaterial.QUARTZ_BLOCK.parseItem(),
            XMaterial.QUARTZ_STAIRS.parseItem(),
            XMaterial.QUARTZ_SLAB.parseItem(),
            XMaterial.SMOOTH_QUARTZ.parseItem(),
            XMaterial.SMOOTH_QUARTZ_STAIRS.parseItem(),
            XMaterial.SMOOTH_QUARTZ_SLAB.parseItem(),
            XMaterial.IRON_BLOCK.parseItem(),
            XMaterial.WHITE_CONCRETE_POWDER.parseItem(),
            XMaterial.WHITE_WOOL.parseItem(),
            XMaterial.WHITE_CARPET.parseItem(),
            XMaterial.SNOW_BLOCK.parseItem(),
            XMaterial.WHITE_STAINED_GLASS.parseItem(),
            XMaterial.PINK_STAINED_GLASS.parseItem(),
            XMaterial.CHERRY_LEAVES.parseItem(),
            XMaterial.CHERRY_PLANKS.parseItem(),
            XMaterial.CHERRY_DOOR.parseItem(),
            XMaterial.CHERRY_STAIRS.parseItem(),
            XMaterial.CHERRY_FENCE.parseItem(),
            XMaterial.CHERRY_FENCE_GATE.parseItem(),
            XMaterial.CHERRY_SLAB.parseItem(),
            XMaterial.CHERRY_TRAPDOOR.parseItem(),
            XMaterial.CHERRY_PRESSURE_PLATE.parseItem(),
            XMaterial.CHERRY_HANGING_SIGN.parseItem(),
            XMaterial.CHERRY_SIGN.parseItem(),
            XMaterial.CHERRY_BUTTON.parseItem(),
            XMaterial.PINK_CONCRETE_POWDER.parseItem(),
            XMaterial.PINK_GLAZED_TERRACOTTA.parseItem(),
            XMaterial.PINK_WOOL.parseItem(),
            XMaterial.PINK_CARPET.parseItem(),
            XMaterial.PINK_CONCRETE.parseItem(),
            XMaterial.MAGENTA_TERRACOTTA.parseItem(),
            XMaterial.MAGENTA_CONCRETE_POWDER.parseItem(),
            XMaterial.MAGENTA_WOOL.parseItem(),
            XMaterial.MAGENTA_CARPET.parseItem(),
            XMaterial.MAGENTA_GLAZED_TERRACOTTA.parseItem(),
            XMaterial.MAGENTA_CONCRETE.parseItem(),
            XMaterial.PURPUR_BLOCK.parseItem(),
            XMaterial.PURPUR_STAIRS.parseItem(),
            XMaterial.PURPUR_SLAB.parseItem(),
            XMaterial.PURPUR_PILLAR.parseItem(),
            XMaterial.MAGENTA_STAINED_GLASS.parseItem(),
            XMaterial.PURPLE_STAINED_GLASS.parseItem(),
            XMaterial.PURPLE_CONCRETE_POWDER.parseItem(),
            XMaterial.PURPLE_WOOL.parseItem(),
            XMaterial.PURPLE_CARPET.parseItem(),
            XMaterial.PURPLE_CONCRETE.parseItem(),
            XMaterial.PURPLE_GLAZED_TERRACOTTA.parseItem(),
            XMaterial.CRYING_OBSIDIAN.parseItem(),
            XMaterial.BUDDING_AMETHYST.parseItem(),
            XMaterial.AMETHYST_BLOCK.parseItem(),
            XMaterial.BLUE_TERRACOTTA.parseItem(),
            XMaterial.LIGHT_BLUE_TERRACOTTA.parseItem(),
            XMaterial.BLUE_STAINED_GLASS.parseItem(),
            XMaterial.BLUE_CONCRETE.parseItem(),
            XMaterial.BLUE_GLAZED_TERRACOTTA.parseItem(),
            XMaterial.LAPIS_BLOCK.parseItem(),
            XMaterial.BLUE_WOOL.parseItem(),
            XMaterial.BLUE_CARPET.parseItem(),
            XMaterial.BLUE_CONCRETE_POWDER.parseItem(),
            XMaterial.BLUE_ICE.parseItem(),
            XMaterial.PACKED_ICE.parseItem(),
            XMaterial.ICE.parseItem(),
            XMaterial.LIGHT_BLUE_STAINED_GLASS.parseItem(),
            XMaterial.LIGHT_BLUE_GLAZED_TERRACOTTA.parseItem(),
            XMaterial.LIGHT_BLUE_CONCRETE.parseItem(),
            XMaterial.LIGHT_BLUE_WOOL.parseItem(),
            XMaterial.LIGHT_BLUE_CARPET.parseItem(),
            XMaterial.LIGHT_BLUE_CONCRETE_POWDER.parseItem(),
            XMaterial.DIAMOND_BLOCK.parseItem(),
            XMaterial.CYAN_CONCRETE_POWDER.parseItem(),
            XMaterial.CYAN_WOOL.parseItem(),
            XMaterial.CYAN_CARPET.parseItem(),
            XMaterial.STRIPPED_WARPED_HYPHAE.parseItem(),
            XMaterial.STRIPPED_WARPED_STEM.parseItem(),
            XMaterial.WARPED_WART_BLOCK.parseItem(),
            XMaterial.CYAN_GLAZED_TERRACOTTA.parseItem(),
            XMaterial.CYAN_CONCRETE.parseItem(),
            XMaterial.WARPED_PLANKS.parseItem(),
            XMaterial.WARPED_DOOR.parseItem(),
            XMaterial.WARPED_STAIRS.parseItem(),
            XMaterial.WARPED_FENCE.parseItem(),
            XMaterial.WARPED_FENCE_GATE.parseItem(),
            XMaterial.WARPED_SLAB.parseItem(),
            XMaterial.WARPED_TRAPDOOR.parseItem(),
            XMaterial.WARPED_PRESSURE_PLATE.parseItem(),
            XMaterial.WARPED_HANGING_SIGN.parseItem(),
            XMaterial.WARPED_SIGN.parseItem(),
            XMaterial.WARPED_BUTTON.parseItem(),
            XMaterial.DARK_PRISMARINE.parseItem(),
            XMaterial.DARK_PRISMARINE_STAIRS.parseItem(),
            XMaterial.DARK_PRISMARINE_SLAB.parseItem(),
            XMaterial.OXIDIZED_COPPER.parseItem(),
            XMaterial.OXIDIZED_CUT_COPPER.parseItem(),
            XMaterial.OXIDIZED_CUT_COPPER_STAIRS.parseItem(),
            XMaterial.OXIDIZED_CUT_COPPER_SLAB.parseItem(),
            XMaterial.PRISMARINE_BRICKS.parseItem(),
            XMaterial.PRISMARINE_BRICK_STAIRS.parseItem(),
            XMaterial.PRISMARINE_BRICK_SLAB.parseItem(),
            XMaterial.PRISMARINE.parseItem(),
            XMaterial.PRISMARINE_STAIRS.parseItem(),
            XMaterial.PRISMARINE_SLAB.parseItem(),
            XMaterial.PRISMARINE_WALL.parseItem(),
            XMaterial.WEATHERED_CUT_COPPER.parseItem(),
            XMaterial.WEATHERED_CUT_COPPER_STAIRS.parseItem(),
            XMaterial.WEATHERED_CUT_COPPER_SLAB.parseItem(),
            XMaterial.WEATHERED_COPPER.parseItem(),
            XMaterial.ORANGE_GLAZED_TERRACOTTA.parseItem(),
            XMaterial.BAMBOO_BLOCK.parseItem(),
            XMaterial.LIME_GLAZED_TERRACOTTA.parseItem(),
            XMaterial.LIME_CONCRETE_POWDER.parseItem(),
            XMaterial.LIME_WOOL.parseItem(),
            XMaterial.LIME_CARPET.parseItem(),
            XMaterial.LIME_CONCRETE.parseItem(),
            XMaterial.MELON.parseItem(),
            XMaterial.EMERALD_BLOCK.parseItem(),
            XMaterial.SLIME_BLOCK.parseItem(),
            XMaterial.GREEN_GLAZED_TERRACOTTA.parseItem(),
            XMaterial.GREEN_STAINED_GLASS.parseItem(),
            XMaterial.GREEN_CONCRETE_POWDER.parseItem(),
            XMaterial.MOSS_BLOCK.parseItem(),
            XMaterial.GREEN_WOOL.parseItem(),
            XMaterial.GREEN_CARPET.parseItem(),
            XMaterial.LIME_TERRACOTTA.parseItem(),
            XMaterial.GREEN_CONCRETE.parseItem(),
            XMaterial.GREEN_TERRACOTTA.parseItem(),
            XMaterial.GRASS_BLOCK.parseItem(),
            XMaterial.JUNGLE_LEAVES.parseItem(),
            XMaterial.OAK_LEAVES.parseItem(),
            XMaterial.DARK_OAK_LEAVES.parseItem(),
            XMaterial.ACACIA_LEAVES.parseItem(),
            XMaterial.BIRCH_LEAVES.parseItem(),
            XMaterial.SPRUCE_LEAVES.parseItem(),
            XMaterial.MOSSY_COBBLESTONE.parseItem(),
            XMaterial.MOSSY_COBBLESTONE_STAIRS.parseItem(),
            XMaterial.MOSSY_COBBLESTONE_SLAB.parseItem(),
            XMaterial.MOSSY_COBBLESTONE_WALL.parseItem(),
            XMaterial.MOSSY_STONE_BRICKS.parseItem(),
            XMaterial.MOSSY_STONE_BRICK_STAIRS.parseItem(),
            XMaterial.MOSSY_STONE_BRICK_SLAB.parseItem(),
            XMaterial.MOSSY_STONE_BRICK_WALL.parseItem(),
            XMaterial.ACACIA_LOG.parseItem(),
            XMaterial.TERRACOTTA.parseItem(),
            XMaterial.EXPOSED_CUT_COPPER.parseItem(),
            XMaterial.EXPOSED_CUT_COPPER_STAIRS.parseItem(),
            XMaterial.EXPOSED_CUT_COPPER_SLAB.parseItem(),
            XMaterial.POLISHED_GRANITE.parseItem(),
            XMaterial.POLISHED_GRANITE_STAIRS.parseItem(),
            XMaterial.POLISHED_GRANITE_SLAB.parseItem(),
            XMaterial.GRANITE.parseItem(),
            XMaterial.GRANITE_STAIRS.parseItem(),
            XMaterial.GRANITE_SLAB.parseItem(),
            XMaterial.GRANITE_WALL.parseItem(),
            XMaterial.JUNGLE_PLANKS.parseItem(),
            XMaterial.JUNGLE_DOOR.parseItem(),
            XMaterial.JUNGLE_STAIRS.parseItem(),
            XMaterial.JUNGLE_FENCE.parseItem(),
            XMaterial.JUNGLE_FENCE_GATE.parseItem(),
            XMaterial.JUNGLE_SLAB.parseItem(),
            XMaterial.JUNGLE_TRAPDOOR.parseItem(),
            XMaterial.JUNGLE_PRESSURE_PLATE.parseItem(),
            XMaterial.JUNGLE_HANGING_SIGN.parseItem(),
            XMaterial.JUNGLE_SIGN.parseItem(),
            XMaterial.JUNGLE_BUTTON.parseItem(),
            XMaterial.MUD_BRICKS.parseItem(),
            XMaterial.MUD_BRICK_STAIRS.parseItem(),
            XMaterial.MUD_BRICK_SLAB.parseItem(),
            XMaterial.MUD_BRICK_WALL.parseItem(),
            XMaterial.PACKED_MUD.parseItem(),
            XMaterial.WHITE_TERRACOTTA.parseItem(),
            XMaterial.BIRCH_PLANKS.parseItem(),
            XMaterial.BIRCH_DOOR.parseItem(),
            XMaterial.BIRCH_STAIRS.parseItem(),
            XMaterial.BIRCH_FENCE.parseItem(),
            XMaterial.BIRCH_FENCE_GATE.parseItem(),
            XMaterial.BIRCH_SLAB.parseItem(),
            XMaterial.BIRCH_TRAPDOOR.parseItem(),
            XMaterial.BIRCH_PRESSURE_PLATE.parseItem(),
            XMaterial.BIRCH_HANGING_SIGN.parseItem(),
            XMaterial.BIRCH_SIGN.parseItem(),
            XMaterial.BIRCH_BUTTON.parseItem(),
            XMaterial.END_STONE_BRICKS.parseItem(),
            XMaterial.END_STONE_BRICK_STAIRS.parseItem(),
            XMaterial.END_STONE_BRICK_SLAB.parseItem(),
            XMaterial.END_STONE_BRICK_WALL.parseItem(),
            XMaterial.END_STONE.parseItem(),
            XMaterial.SANDSTONE.parseItem(),
            XMaterial.SANDSTONE_STAIRS.parseItem(),
            XMaterial.SANDSTONE_SLAB.parseItem(),
            XMaterial.SANDSTONE_WALL.parseItem(),
            XMaterial.CHISELED_SANDSTONE.parseItem(),
            XMaterial.SMOOTH_SANDSTONE.parseItem(),
            XMaterial.SMOOTH_SANDSTONE_STAIRS.parseItem(),
            XMaterial.SMOOTH_SANDSTONE_SLAB.parseItem(),
            XMaterial.SAND.parseItem(),
            XMaterial.STRIPPED_BAMBOO_BLOCK.parseItem(),
            XMaterial.BAMBOO_MOSAIC.parseItem(),
            XMaterial.BAMBOO_PLANKS.parseItem(),
            XMaterial.BAMBOO_DOOR.parseItem(),
            XMaterial.BAMBOO_MOSAIC_STAIRS.parseItem(),
            XMaterial.BAMBOO_STAIRS.parseItem(),
            XMaterial.BAMBOO_FENCE.parseItem(),
            XMaterial.BAMBOO_FENCE_GATE.parseItem(),
            XMaterial.BAMBOO_MOSAIC_SLAB.parseItem(),
            XMaterial.BAMBOO_SLAB.parseItem(),
            XMaterial.BAMBOO_TRAPDOOR.parseItem(),
            XMaterial.BAMBOO_PRESSURE_PLATE.parseItem(),
            XMaterial.BAMBOO_HANGING_SIGN.parseItem(),
            XMaterial.BAMBOO_SIGN.parseItem(),
            XMaterial.BAMBOO_BUTTON.parseItem(),
            XMaterial.GOLD_BLOCK.parseItem(),
            XMaterial.YELLOW_CONCRETE_POWDER.parseItem(),
            XMaterial.YELLOW_GLAZED_TERRACOTTA.parseItem(),
            XMaterial.YELLOW_WOOL.parseItem(),
            XMaterial.YELLOW_CARPET.parseItem(),
            XMaterial.YELLOW_CONCRETE.parseItem(),
            XMaterial.YELLOW_TERRACOTTA.parseItem(),
            XMaterial.RAW_GOLD_BLOCK.parseItem(),
            XMaterial.GLOWSTONE.parseItem(),
            XMaterial.HAY_BLOCK.parseItem(),
            XMaterial.SPONGE.parseItem(),
            XMaterial.WET_SPONGE.parseItem(),
            XMaterial.YELLOW_STAINED_GLASS.parseItem(),
            XMaterial.ORANGE_STAINED_GLASS.parseItem(),
            XMaterial.PUMPKIN.parseItem(),
            XMaterial.ORANGE_CONCRETE_POWDER.parseItem(),
            XMaterial.ORANGE_WOOL.parseItem(),
            XMaterial.ORANGE_CARPET.parseItem(),
            XMaterial.ORANGE_CONCRETE.parseItem(),
            XMaterial.MAGMA_BLOCK.parseItem(),
            XMaterial.RED_SAND.parseItem(),
            XMaterial.RED_SANDSTONE.parseItem(),
            XMaterial.RED_SANDSTONE_STAIRS.parseItem(),
            XMaterial.RED_SANDSTONE_SLAB.parseItem(),
            XMaterial.RED_SANDSTONE_WALL.parseItem(),
            XMaterial.CHISELED_RED_SANDSTONE.parseItem(),
            XMaterial.CUT_RED_SANDSTONE.parseItem(),
            XMaterial.SMOOTH_RED_SANDSTONE.parseItem(),
            XMaterial.SMOOTH_RED_SANDSTONE_STAIRS.parseItem(),
            XMaterial.SMOOTH_RED_SANDSTONE_SLAB.parseItem(),
            XMaterial.ORANGE_TERRACOTTA.parseItem(),
            XMaterial.ACACIA_PLANKS.parseItem(),
            XMaterial.ACACIA_DOOR.parseItem(),
            XMaterial.ACACIA_STAIRS.parseItem(),
            XMaterial.ACACIA_FENCE.parseItem(),
            XMaterial.ACACIA_FENCE_GATE.parseItem(),
            XMaterial.ACACIA_SLAB.parseItem(),
            XMaterial.ACACIA_TRAPDOOR.parseItem(),
            XMaterial.ACACIA_PRESSURE_PLATE.parseItem(),
            XMaterial.ACACIA_HANGING_SIGN.parseItem(),
            XMaterial.ACACIA_SIGN.parseItem(),
            XMaterial.ACACIA_BUTTON.parseItem(),
            XMaterial.STRIPPED_ACACIA_LOG.parseItem(),
            XMaterial.STRIPPED_ACACIA_WOOD.parseItem(),
            XMaterial.BRICKS.parseItem(),
            XMaterial.BRICK_STAIRS.parseItem(),
            XMaterial.BRICK_SLAB.parseItem(),
            XMaterial.BRICK_WALL.parseItem(),
            XMaterial.CUT_COPPER.parseItem(),
            XMaterial.CUT_COPPER_STAIRS.parseItem(),
            XMaterial.CUT_COPPER_SLAB.parseItem(),
            XMaterial.COPPER_BLOCK.parseItem(),
            XMaterial.RED_TERRACOTTA.parseItem(),
            XMaterial.PINK_TERRACOTTA.parseItem(),
            XMaterial.RED_GLAZED_TERRACOTTA.parseItem(),
            XMaterial.REDSTONE_BLOCK.parseItem(),
            XMaterial.RED_MUSHROOM_BLOCK.parseItem(),
            XMaterial.RED_WOOL.parseItem(),
            XMaterial.RED_CARPET.parseItem(),
            XMaterial.RED_CONCRETE_POWDER.parseItem(),
            XMaterial.RED_CONCRETE.parseItem(),
            XMaterial.RED_STAINED_GLASS.parseItem(),
            XMaterial.RED_NETHER_BRICKS.parseItem(),
            XMaterial.RED_NETHER_BRICK_STAIRS.parseItem(),
            XMaterial.RED_NETHER_BRICK_SLAB.parseItem(),
            XMaterial.RED_NETHER_BRICK_WALL.parseItem(),
            XMaterial.NETHER_WART_BLOCK.parseItem(),
            XMaterial.MANGROVE_PLANKS.parseItem(),
            XMaterial.MANGROVE_DOOR.parseItem(),
            XMaterial.MANGROVE_STAIRS.parseItem(),
            XMaterial.MANGROVE_FENCE.parseItem(),
            XMaterial.MANGROVE_FENCE_GATE.parseItem(),
            XMaterial.MANGROVE_SLAB.parseItem(),
            XMaterial.MANGROVE_TRAPDOOR.parseItem(),
            XMaterial.MANGROVE_PRESSURE_PLATE.parseItem(),
            XMaterial.MANGROVE_HANGING_SIGN.parseItem(),
            XMaterial.MANGROVE_SIGN.parseItem(),
            XMaterial.MANGROVE_BUTTON.parseItem(),
            XMaterial.CRIMSON_HYPHAE.parseItem(),
            XMaterial.CRIMSON_STEM.parseItem(),
            XMaterial.PURPLE_TERRACOTTA.parseItem(),
            XMaterial.STRIPPED_CRIMSON_STEM.parseItem(),
            XMaterial.STRIPPED_CRIMSON_HYPHAE.parseItem(),
            XMaterial.CRIMSON_PLANKS.parseItem(),
            XMaterial.CRIMSON_DOOR.parseItem(),
            XMaterial.CRIMSON_STAIRS.parseItem(),
            XMaterial.CRIMSON_FENCE.parseItem(),
            XMaterial.CRIMSON_FENCE_GATE.parseItem(),
            XMaterial.CRIMSON_SLAB.parseItem(),
            XMaterial.CRIMSON_TRAPDOOR.parseItem(),
            XMaterial.CRIMSON_PRESSURE_PLATE.parseItem(),
            XMaterial.CRIMSON_HANGING_SIGN.parseItem(),
            XMaterial.CRIMSON_SIGN.parseItem(),
            XMaterial.CRIMSON_BUTTON.parseItem(),
            XMaterial.CRIMSON_NYLIUM.parseItem(),
            XMaterial.NETHERRACK.parseItem(),
            XMaterial.NETHER_QUARTZ_ORE.parseItem(),
            XMaterial.BLACK_GLAZED_TERRACOTTA.parseItem(),
            XMaterial.CHERRY_LOG.parseItem(),
            XMaterial.NETHER_BRICKS.parseItem(),
            XMaterial.NETHER_BRICK_STAIRS.parseItem(),
            XMaterial.NETHER_BRICK_SLAB.parseItem(),
            XMaterial.NETHER_BRICK_WALL.parseItem(),
            XMaterial.CHISELED_NETHER_BRICKS.parseItem(),
            XMaterial.CRACKED_NETHER_BRICKS.parseItem(),
            XMaterial.BLACK_TERRACOTTA.parseItem(),
            XMaterial.GRAY_TERRACOTTA.parseItem(),
            XMaterial.BROWN_TERRACOTTA.parseItem(),
            XMaterial.STRIPPED_DARK_OAK_LOG.parseItem(),
            XMaterial.STRIPPED_DARK_OAK_WOOD.parseItem(),
            XMaterial.SPRUCE_LOG.parseItem(),
            XMaterial.SPRUCE_WOOD.parseItem(),
            XMaterial.DARK_OAK_PLANKS.parseItem(),
            XMaterial.DARK_OAK_DOOR.parseItem(),
            XMaterial.DARK_OAK_STAIRS.parseItem(),
            XMaterial.DARK_OAK_FENCE.parseItem(),
            XMaterial.DARK_OAK_FENCE_GATE.parseItem(),
            XMaterial.DARK_OAK_SLAB.parseItem(),
            XMaterial.DARK_OAK_TRAPDOOR.parseItem(),
            XMaterial.DARK_OAK_PRESSURE_PLATE.parseItem(),
            XMaterial.DARK_OAK_HANGING_SIGN.parseItem(),
            XMaterial.DARK_OAK_SIGN.parseItem(),
            XMaterial.DARK_OAK_BUTTON.parseItem(),
            XMaterial.DARK_OAK_LOG.parseItem(),
            XMaterial.DARK_OAK_WOOD.parseItem(),
            XMaterial.MANGROVE_ROOTS.parseItem(),
            XMaterial.MUDDY_MANGROVE_ROOTS.parseItem(),
            XMaterial.SOUL_SAND.parseItem(),
            XMaterial.ANCIENT_DEBRIS.parseItem(),
            XMaterial.REDSTONE_LAMP.parseItem(),
            XMaterial.NOTE_BLOCK.parseItem(),
            XMaterial.JUKEBOX.parseItem(),
            XMaterial.JUNGLE_LOG.parseItem(),
            XMaterial.JUNGLE_WOOD.parseItem(),
            XMaterial.BROWN_STAINED_GLASS.parseItem(),
            XMaterial.PODZOL.parseItem(),
            XMaterial.OAK_WOOD.parseItem(),
            XMaterial.OAK_LOG.parseItem(),
            XMaterial.SPRUCE_PLANKS.parseItem(),
            XMaterial.SPRUCE_DOOR.parseItem(),
            XMaterial.SPRUCE_STAIRS.parseItem(),
            XMaterial.SPRUCE_FENCE.parseItem(),
            XMaterial.SPRUCE_FENCE_GATE.parseItem(),
            XMaterial.SPRUCE_SLAB.parseItem(),
            XMaterial.SPRUCE_TRAPDOOR.parseItem(),
            XMaterial.SPRUCE_PRESSURE_PLATE.parseItem(),
            XMaterial.SPRUCE_HANGING_SIGN.parseItem(),
            XMaterial.SPRUCE_SIGN.parseItem(),
            XMaterial.SPRUCE_BUTTON.parseItem(),
            XMaterial.BROWN_CONCRETE.parseItem(),
            XMaterial.BROWN_WOOL.parseItem(),
            XMaterial.BROWN_CARPET.parseItem(),
            XMaterial.BROWN_CONCRETE_POWDER.parseItem(),
            XMaterial.COARSE_DIRT.parseItem(),
            XMaterial.ROOTED_DIRT.parseItem(),
            XMaterial.MYCELIUM.parseItem(),
            XMaterial.LIGHT_GRAY_TERRACOTTA.parseItem(),
            XMaterial.OAK_PLANKS.parseItem(),
            XMaterial.OAK_DOOR.parseItem(),
            XMaterial.OAK_STAIRS.parseItem(),
            XMaterial.OAK_FENCE.parseItem(),
            XMaterial.OAK_FENCE_GATE.parseItem(),
            XMaterial.OAK_SLAB.parseItem(),
            XMaterial.OAK_TRAPDOOR.parseItem(),
            XMaterial.OAK_PRESSURE_PLATE.parseItem(),
            XMaterial.OAK_HANGING_SIGN.parseItem(),
            XMaterial.OAK_SIGN.parseItem(),
            XMaterial.OAK_BUTTON.parseItem(),
            XMaterial.STRIPPED_JUNGLE_LOG.parseItem(),
            XMaterial.STRIPPED_JUNGLE_WOOD.parseItem(),
            XMaterial.BROWN_GLAZED_TERRACOTTA.parseItem(),
            XMaterial.BLACK_CANDLE.parseItem(),
            XMaterial.BLUE_CANDLE.parseItem(),
            XMaterial.BROWN_CANDLE.parseItem(),
            XMaterial.CYAN_CANDLE.parseItem(),
            XMaterial.GRAY_CANDLE.parseItem(),
            XMaterial.GREEN_CANDLE.parseItem(),
            XMaterial.LIGHT_BLUE_CANDLE.parseItem(),
            XMaterial.LIGHT_GRAY_CANDLE.parseItem(),
            XMaterial.LIME_CANDLE.parseItem(),
            XMaterial.MAGENTA_CANDLE.parseItem(),
            XMaterial.ORANGE_CANDLE.parseItem(),
            XMaterial.PINK_CANDLE.parseItem(),
            XMaterial.PURPLE_CANDLE.parseItem(),
            XMaterial.RED_CANDLE.parseItem(),
            XMaterial.WHITE_CANDLE.parseItem(),
            XMaterial.YELLOW_CANDLE.parseItem(),
            XMaterial.BLACK_BED.parseItem(),
            XMaterial.BLUE_BED.parseItem(),
            XMaterial.BROWN_BED.parseItem(),
            XMaterial.CYAN_BED.parseItem(),
            XMaterial.GRAY_BED.parseItem(),
            XMaterial.GREEN_BED.parseItem(),
            XMaterial.LIGHT_BLUE_BED.parseItem(),
            XMaterial.LIGHT_GRAY_BED.parseItem(),
            XMaterial.LIME_BED.parseItem(),
            XMaterial.MAGENTA_BED.parseItem(),
            XMaterial.ORANGE_BED.parseItem(),
            XMaterial.PINK_BED.parseItem(),
            XMaterial.PURPLE_BED.parseItem(),
            XMaterial.RED_BED.parseItem(),
            XMaterial.WHITE_BED.parseItem(),
            XMaterial.YELLOW_BED.parseItem(),
            XMaterial.BLACK_BANNER.parseItem(),
            XMaterial.BLUE_BANNER.parseItem(),
            XMaterial.BROWN_BANNER.parseItem(),
            XMaterial.CYAN_BANNER.parseItem(),
            XMaterial.GRAY_BANNER.parseItem(),
            XMaterial.GREEN_BANNER.parseItem(),
            XMaterial.LIGHT_BLUE_BANNER.parseItem(),
            XMaterial.LIGHT_GRAY_BANNER.parseItem(),
            XMaterial.LIME_BANNER.parseItem(),
            XMaterial.MAGENTA_BANNER.parseItem(),
            XMaterial.ORANGE_BANNER.parseItem(),
            XMaterial.PINK_BANNER.parseItem(),
            XMaterial.PURPLE_BANNER.parseItem(),
            XMaterial.RED_BANNER.parseItem(),
            XMaterial.WHITE_BANNER.parseItem(),
            XMaterial.YELLOW_BANNER.parseItem(),
            XMaterial.BLACK_STAINED_GLASS_PANE.parseItem(),
            XMaterial.BLUE_STAINED_GLASS_PANE.parseItem(),
            XMaterial.BROWN_STAINED_GLASS_PANE.parseItem(),
            XMaterial.CYAN_STAINED_GLASS_PANE.parseItem(),
            XMaterial.GRAY_STAINED_GLASS_PANE.parseItem(),
            XMaterial.GREEN_STAINED_GLASS_PANE.parseItem(),
            XMaterial.LIGHT_BLUE_STAINED_GLASS_PANE.parseItem(),
            XMaterial.LIGHT_GRAY_STAINED_GLASS_PANE.parseItem(),
            XMaterial.LIME_STAINED_GLASS_PANE.parseItem(),
            XMaterial.MAGENTA_STAINED_GLASS_PANE.parseItem(),
            XMaterial.ORANGE_STAINED_GLASS_PANE.parseItem(),
            XMaterial.PINK_STAINED_GLASS_PANE.parseItem(),
            XMaterial.PURPLE_STAINED_GLASS_PANE.parseItem(),
            XMaterial.RED_STAINED_GLASS_PANE.parseItem(),
            XMaterial.WHITE_STAINED_GLASS_PANE.parseItem(),
            XMaterial.YELLOW_STAINED_GLASS_PANE.parseItem(),
            XMaterial.BLACK_SHULKER_BOX.parseItem(),
            XMaterial.BLUE_SHULKER_BOX.parseItem(),
            XMaterial.BROWN_SHULKER_BOX.parseItem(),
            XMaterial.CYAN_SHULKER_BOX.parseItem(),
            XMaterial.GRAY_SHULKER_BOX.parseItem(),
            XMaterial.GREEN_SHULKER_BOX.parseItem(),
            XMaterial.LIGHT_BLUE_SHULKER_BOX.parseItem(),
            XMaterial.LIGHT_GRAY_SHULKER_BOX.parseItem(),
            XMaterial.LIME_SHULKER_BOX.parseItem(),
            XMaterial.MAGENTA_SHULKER_BOX.parseItem(),
            XMaterial.ORANGE_SHULKER_BOX.parseItem(),
            XMaterial.PINK_SHULKER_BOX.parseItem(),
            XMaterial.PURPLE_SHULKER_BOX.parseItem(),
            XMaterial.RED_SHULKER_BOX.parseItem(),
            XMaterial.WHITE_SHULKER_BOX.parseItem(),
            XMaterial.YELLOW_SHULKER_BOX.parseItem()

    );

    public static ItemStack[] getBlocksByColor() {
        return BLOCKS_BY_COLOR.toArray(new ItemStack[0]);
    }

    public static ItemStack[] getSlabs() {
        List<ItemStack> slabs = BLOCKS_BY_COLOR.stream()
                .filter(item -> item != null && item.getType().name().endsWith("_SLAB"))
                .collect(Collectors.toList());
        return slabs.toArray(new ItemStack[0]);
    }

    public static ItemStack[] getStairs() {
        List<ItemStack> stairs = BLOCKS_BY_COLOR.stream()
                .filter(item -> item != null && item.getType().name().endsWith("_STAIRS"))
                .collect(Collectors.toList());
        return stairs.toArray(new ItemStack[0]);
    }

    public static ItemStack[] getWalls() {
        List<ItemStack> walls = BLOCKS_BY_COLOR.stream()
                .filter(item -> item != null && item.getType().name().endsWith("_WALL"))
                .collect(Collectors.toList());
        return walls.toArray(new ItemStack[0]);
    }
    public static ItemStack[] getLogs() {
        List<ItemStack> logs = BLOCKS_BY_COLOR.stream()
                .filter(item -> item != null && (item.getType().name().endsWith("_LOG") || item.getType().name().endsWith("_WOOD")))
                .collect(Collectors.toList());

        return logs.toArray(new ItemStack[0]);
    }
    public static ItemStack[] getLeaves() {
        return BLOCKS_BY_COLOR.stream()
                .filter(item -> item != null && item.getType().name().endsWith("_LEAVES"))
                .collect(Collectors.toList())
                .toArray(new ItemStack[0]);
    }

    public static ItemStack[] getFences() {
        return BLOCKS_BY_COLOR.stream()
                .filter(item -> item != null && item.getType().name().endsWith("_FENCE"))
                .collect(Collectors.toList())
                .toArray(new ItemStack[0]);
    }
    public static ItemStack[] getGlass() {
        return BLOCKS_BY_COLOR.stream()
                .filter(item -> item != null && item.getType().name().endsWith("_GLASS") || item.getType().name().endsWith("_STAINED_GLASS"))
                .collect(Collectors.toList())
                .toArray(new ItemStack[0]);
    }

    public static ItemStack[] getCarpet() {
        return BLOCKS_BY_COLOR.stream()
                .filter(item -> item != null && item.getType().name().endsWith("_CARPET"))
                .collect(Collectors.toList())
                .toArray(new ItemStack[0]);
    }

    public static ItemStack[] getWool() {
        return BLOCKS_BY_COLOR.stream()
                .filter(item -> item != null && item.getType().name().endsWith("_WOOL"))
                .collect(Collectors.toList())
                .toArray(new ItemStack[0]);
    }
    public static ItemStack[] getTerracotta() {
        return BLOCKS_BY_COLOR.stream()
                .filter(item -> item != null && item.getType().name().endsWith("_TERRACOTTA"))
                .collect(Collectors.toList())
                .toArray(new ItemStack[0]);
    }

    public static ItemStack[] getConcrete() {
        return BLOCKS_BY_COLOR.stream()
                .filter(item -> item != null && item.getType().name().endsWith("_CONCRETE"))
                .collect(Collectors.toList())
                .toArray(new ItemStack[0]);
    }

    public static ItemStack[] getConcretePowder() {
        return BLOCKS_BY_COLOR.stream()
                .filter(item -> item != null && item.getType().name().endsWith("_CONCRETE_POWDER"))
                .collect(Collectors.toList())
                .toArray(new ItemStack[0]);
    }
    public static ItemStack[] getBeds() {
        return BLOCKS_BY_COLOR.stream()
                .filter(item -> item != null && item.getType().name().endsWith("_BED"))
                .collect(Collectors.toList())
                .toArray(new ItemStack[0]);
    }

    public static ItemStack[] getCandles() {
        return BLOCKS_BY_COLOR.stream()
                .filter(item -> item != null && item.getType().name().endsWith("_CANDLE"))
                .collect(Collectors.toList())
                .toArray(new ItemStack[0]);
    }

    public static ItemStack[] getBanners() {
        return BLOCKS_BY_COLOR.stream()
                .filter(item -> item != null && item.getType().name().endsWith("_BANNER"))
                .collect(Collectors.toList())
                .toArray(new ItemStack[0]);
    }

    public static ItemStack[] getGlassPanes() {
        return BLOCKS_BY_COLOR.stream()
                .filter(item -> item != null && item.getType().name().endsWith("_GLASS_PANE"))
                .collect(Collectors.toList())
                .toArray(new ItemStack[0]);
    }
    public static ItemStack[] getSigns() {
        return BLOCKS_BY_COLOR.stream()
                .filter(item -> item != null && item.getType().name().endsWith("_SIGN"))
                .collect(Collectors.toList())
                .toArray(new ItemStack[0]);
    }
    public static ItemStack[] getShulkerBoxes() {
        return BLOCKS_BY_COLOR.stream()
                .filter(item -> item != null && item.getType().name().endsWith("_SHULKER_BOX"))
                .collect(Collectors.toList())
                .toArray(new ItemStack[0]);
    }
    public static ItemStack[] getGates() {
        return BLOCKS_BY_COLOR.stream()
                .filter(item -> item != null && item.getType().name().endsWith("_GATE"))
                .collect(Collectors.toList())
                .toArray(new ItemStack[0]);
    }
    public static ItemStack[] getItemsByFilter(String filter) {
        switch (filter.toLowerCase()) {
            case "slabs": return getSlabs();
            case "stairs": return getStairs();
            case "walls": return getWalls();
            case "logs": return getLogs();
            case "leaves": return getLeaves();
            case "fences": return getFences();
            case "carpet": return getCarpet();
            case "wool": return getWool();
            case "terracotta": return getTerracotta();
            case "concrete": return getConcrete();
            case "concrete_powder": return getConcretePowder();
            case "bed": return getBeds();
            case "candle": return getCandles();
            case "banner": return getBanners();
            case "glass_pane": return getGlassPanes();
            case "signs": return getSigns();
            case "shulker_boxes": return getShulkerBoxes();
            case "gates": return getGates();
            case "glass": return getGlass();
            default: return getBlocksByColor();
        }
    }


}