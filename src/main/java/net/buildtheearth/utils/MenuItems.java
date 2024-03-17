package net.buildtheearth.utils;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.cryptomorin.xseries.XMaterial.*;

public class MenuItems {

    // ----------------- BLOCKS BY COLOR -----------------

    private static final ItemStack[] BLOCKS_BY_COLOR = {
            BLACK_STAINED_GLASS.parseItem(),
            BLACK_CONCRETE.parseItem(),
            COAL_BLOCK.parseItem(),
            CHISELED_POLISHED_BLACKSTONE.parseItem(),
            CRACKED_POLISHED_BLACKSTONE_BRICKS.parseItem(),
            POLISHED_BLACKSTONE_BRICKS.parseItem(),
            POLISHED_BLACKSTONE_BRICK_STAIRS.parseItem(),
            POLISHED_BLACKSTONE_BRICK_SLAB.parseItem(),
            POLISHED_BLACKSTONE_BRICK_WALL.parseItem(),
            POLISHED_BLACKSTONE.parseItem(),
            POLISHED_BLACKSTONE_STAIRS.parseItem(),
            POLISHED_BLACKSTONE_SLAB.parseItem(),
            POLISHED_BLACKSTONE_WALL.parseItem(),
            POLISHED_BLACKSTONE_PRESSURE_PLATE.parseItem(),
            POLISHED_BLACKSTONE_BUTTON.parseItem(),
            BLACKSTONE.parseItem(),
            BLACKSTONE_STAIRS.parseItem(),
            BLACKSTONE_SLAB.parseItem(),
            BLACKSTONE_WALL.parseItem(),
            GILDED_BLACKSTONE.parseItem(),
            BLACK_CONCRETE_POWDER.parseItem(),
            BLACK_WOOL.parseItem(),
            BLACK_CARPET.parseItem(),
            OBSIDIAN.parseItem(),
            GRAY_STAINED_GLASS.parseItem(),
            NETHERITE_BLOCK.parseItem(),
            MUD.parseItem(),
            CHISELED_DEEPSLATE.parseItem(),
            CRACKED_DEEPSLATE_TILES.parseItem(),
            DEEPSLATE_TILES.parseItem(),
            DEEPSLATE_TILE_STAIRS.parseItem(),
            DEEPSLATE_TILE_SLAB.parseItem(),
            DEEPSLATE_TILE_WALL.parseItem(),
            CRACKED_DEEPSLATE_BRICKS.parseItem(),
            DEEPSLATE_BRICKS.parseItem(),
            DEEPSLATE_BRICK_STAIRS.parseItem(),
            DEEPSLATE_BRICK_SLAB.parseItem(),
            DEEPSLATE_BRICK_WALL.parseItem(),
            POLISHED_DEEPSLATE.parseItem(),
            POLISHED_DEEPSLATE_STAIRS.parseItem(),
            POLISHED_DEEPSLATE_SLAB.parseItem(),
            POLISHED_DEEPSLATE_WALL.parseItem(),
            COBBLED_DEEPSLATE.parseItem(),
            COBBLED_DEEPSLATE_STAIRS.parseItem(),
            COBBLED_DEEPSLATE_SLAB.parseItem(),
            COBBLED_DEEPSLATE_WALL.parseItem(),
            DEEPSLATE.parseItem(),
            SMOOTH_BASALT.parseItem(),
            POLISHED_BASALT.parseItem(),
            BASALT.parseItem(),
            GRAY_CONCRETE.parseItem(),
            GRAY_WOOL.parseItem(),
            GRAY_CARPET.parseItem(),
            GRAY_CONCRETE_POWDER.parseItem(),
            CYAN_TERRACOTTA.parseItem(),
            GRAY_GLAZED_TERRACOTTA.parseItem(),
            TUFF.parseItem(),
            LIGHT_GRAY_CONCRETE.parseItem(),
            COBBLESTONE.parseItem(),
            COBBLESTONE_STAIRS.parseItem(),
            COBBLESTONE_SLAB.parseItem(),
            COBBLESTONE_WALL.parseItem(),
            CRACKED_STONE_BRICKS.parseItem(),
            STONE_BRICKS.parseItem(),
            STONE_BRICK_STAIRS.parseItem(),
            STONE_BRICK_SLAB.parseItem(),
            STONE_BRICK_WALL.parseItem(),
            LIGHT_GRAY_STAINED_GLASS.parseItem(),
            POLISHED_ANDESITE.parseItem(),
            POLISHED_ANDESITE_STAIRS.parseItem(),
            POLISHED_ANDESITE_SLAB.parseItem(),
            STONE.parseItem(),
            STONE_STAIRS.parseItem(),
            STONE_SLAB.parseItem(),
            STONE_PRESSURE_PLATE.parseItem(),
            STONE_BUTTON.parseItem(),
            ANDESITE.parseItem(),
            ANDESITE_STAIRS.parseItem(),
            ANDESITE_SLAB.parseItem(),
            ANDESITE_WALL.parseItem(),
            LIGHT_GRAY_WOOL.parseItem(),
            LIGHT_GRAY_CARPET.parseItem(),
            LIGHT_GRAY_CONCRETE_POWDER.parseItem(),
            CLAY.parseItem(),
            LIGHT_GRAY_GLAZED_TERRACOTTA.parseItem(),
            SEA_LANTERN.parseItem(),
            LODESTONE.parseItem(),
            WHITE_GLAZED_TERRACOTTA.parseItem(),
            BONE_BLOCK.parseItem(),
            BIRCH_LOG.parseItem(),
            POLISHED_DIORITE.parseItem(),
            POLISHED_DIORITE_STAIRS.parseItem(),
            POLISHED_DIORITE_SLAB.parseItem(),
            DIORITE.parseItem(),
            DIORITE_STAIRS.parseItem(),
            DIORITE_SLAB.parseItem(),
            DIORITE_WALL.parseItem(),
            CALCITE.parseItem(),
            WHITE_CONCRETE.parseItem(),
            QUARTZ_BRICKS.parseItem(),
            CHISELED_QUARTZ_BLOCK.parseItem(),
            QUARTZ_PILLAR.parseItem(),
            QUARTZ_BLOCK.parseItem(),
            QUARTZ_STAIRS.parseItem(),
            QUARTZ_SLAB.parseItem(),
            SMOOTH_QUARTZ.parseItem(),
            SMOOTH_QUARTZ_STAIRS.parseItem(),
            SMOOTH_QUARTZ_SLAB.parseItem(),
            IRON_BLOCK.parseItem(),
            WHITE_CONCRETE_POWDER.parseItem(),
            WHITE_WOOL.parseItem(),
            WHITE_CARPET.parseItem(),
            SNOW_BLOCK.parseItem(),
            WHITE_STAINED_GLASS.parseItem(),
            PINK_STAINED_GLASS.parseItem(),
            CHERRY_PLANKS.parseItem(),
            CHERRY_DOOR.parseItem(),
            CHERRY_STAIRS.parseItem(),
            CHERRY_FENCE.parseItem(),
            CHERRY_FENCE_GATE.parseItem(),
            CHERRY_SLAB.parseItem(),
            CHERRY_TRAPDOOR.parseItem(),
            CHERRY_PRESSURE_PLATE.parseItem(),
            CHERRY_HANGING_SIGN.parseItem(),
            CHERRY_SIGN.parseItem(),
            CHERRY_BUTTON.parseItem(),
            PINK_CONCRETE_POWDER.parseItem(),
            PINK_GLAZED_TERRACOTTA.parseItem(),
            PINK_WOOL.parseItem(),
            PINK_CARPET.parseItem(),
            PINK_CONCRETE.parseItem(),
            MAGENTA_TERRACOTTA.parseItem(),
            MAGENTA_CONCRETE_POWDER.parseItem(),
            MAGENTA_WOOL.parseItem(),
            MAGENTA_CARPET.parseItem(),
            MAGENTA_GLAZED_TERRACOTTA.parseItem(),
            MAGENTA_CONCRETE.parseItem(),
            PURPUR_BLOCK.parseItem(),
            PURPUR_STAIRS.parseItem(),
            PURPUR_SLAB.parseItem(),
            PURPUR_PILLAR.parseItem(),
            MAGENTA_STAINED_GLASS.parseItem(),
            PURPLE_STAINED_GLASS.parseItem(),
            PURPLE_CONCRETE_POWDER.parseItem(),
            PURPLE_WOOL.parseItem(),
            PURPLE_CARPET.parseItem(),
            PURPLE_CONCRETE.parseItem(),
            PURPLE_GLAZED_TERRACOTTA.parseItem(),
            CRYING_OBSIDIAN.parseItem(),
            BUDDING_AMETHYST.parseItem(),
            AMETHYST_BLOCK.parseItem(),
            BLUE_TERRACOTTA.parseItem(),
            LIGHT_BLUE_TERRACOTTA.parseItem(),
            BLUE_STAINED_GLASS.parseItem(),
            BLUE_CONCRETE.parseItem(),
            BLUE_GLAZED_TERRACOTTA.parseItem(),
            LAPIS_BLOCK.parseItem(),
            BLUE_WOOL.parseItem(),
            BLUE_CARPET.parseItem(),
            BLUE_CONCRETE_POWDER.parseItem(),
            BLUE_ICE.parseItem(),
            PACKED_ICE.parseItem(),
            ICE.parseItem(),
            LIGHT_BLUE_STAINED_GLASS.parseItem(),
            LIGHT_BLUE_GLAZED_TERRACOTTA.parseItem(),
            LIGHT_BLUE_CONCRETE.parseItem(),
            LIGHT_BLUE_WOOL.parseItem(),
            LIGHT_BLUE_CARPET.parseItem(),
            LIGHT_BLUE_CONCRETE_POWDER.parseItem(),
            DIAMOND_BLOCK.parseItem(),
            CYAN_CONCRETE_POWDER.parseItem(),
            CYAN_WOOL.parseItem(),
            CYAN_CARPET.parseItem(),
            STRIPPED_WARPED_HYPHAE.parseItem(),
            STRIPPED_WARPED_STEM.parseItem(),
            WARPED_WART_BLOCK.parseItem(),
            CYAN_GLAZED_TERRACOTTA.parseItem(),
            CYAN_CONCRETE.parseItem(),
            WARPED_PLANKS.parseItem(),
            WARPED_DOOR.parseItem(),
            WARPED_STAIRS.parseItem(),
            WARPED_FENCE.parseItem(),
            WARPED_FENCE_GATE.parseItem(),
            WARPED_SLAB.parseItem(),
            WARPED_TRAPDOOR.parseItem(),
            WARPED_PRESSURE_PLATE.parseItem(),
            WARPED_HANGING_SIGN.parseItem(),
            WARPED_SIGN.parseItem(),
            WARPED_BUTTON.parseItem(),
            DARK_PRISMARINE.parseItem(),
            DARK_PRISMARINE_STAIRS.parseItem(),
            DARK_PRISMARINE_SLAB.parseItem(),
            OXIDIZED_COPPER.parseItem(),
            OXIDIZED_CUT_COPPER.parseItem(),
            OXIDIZED_CUT_COPPER_STAIRS.parseItem(),
            OXIDIZED_CUT_COPPER_SLAB.parseItem(),
            PRISMARINE_BRICKS.parseItem(),
            PRISMARINE_BRICK_STAIRS.parseItem(),
            PRISMARINE_BRICK_SLAB.parseItem(),
            PRISMARINE.parseItem(),
            PRISMARINE_STAIRS.parseItem(),
            PRISMARINE_SLAB.parseItem(),
            PRISMARINE_WALL.parseItem(),
            WEATHERED_CUT_COPPER.parseItem(),
            WEATHERED_CUT_COPPER_STAIRS.parseItem(),
            WEATHERED_CUT_COPPER_SLAB.parseItem(),
            WEATHERED_COPPER.parseItem(),
            ORANGE_GLAZED_TERRACOTTA.parseItem(),
            BAMBOO_BLOCK.parseItem(),
            LIME_GLAZED_TERRACOTTA.parseItem(),
            LIME_CONCRETE_POWDER.parseItem(),
            LIME_WOOL.parseItem(),
            LIME_CARPET.parseItem(),
            LIME_CONCRETE.parseItem(),
            MELON.parseItem(),
            EMERALD_BLOCK.parseItem(),
            SLIME_BLOCK.parseItem(),
            GREEN_GLAZED_TERRACOTTA.parseItem(),
            GREEN_STAINED_GLASS.parseItem(),
            GREEN_CONCRETE_POWDER.parseItem(),
            MOSS_BLOCK.parseItem(),
            GREEN_WOOL.parseItem(),
            GREEN_CARPET.parseItem(),
            LIME_TERRACOTTA.parseItem(),
            GREEN_CONCRETE.parseItem(),
            GREEN_TERRACOTTA.parseItem(),
            GRASS_BLOCK.parseItem(),
            JUNGLE_LEAVES.parseItem(),
            OAK_LEAVES.parseItem(),
            DARK_OAK_LEAVES.parseItem(),
            ACACIA_LEAVES.parseItem(),
            BIRCH_LEAVES.parseItem(),
            SPRUCE_LEAVES.parseItem(),
            MOSSY_COBBLESTONE.parseItem(),
            MOSSY_COBBLESTONE_STAIRS.parseItem(),
            MOSSY_COBBLESTONE_SLAB.parseItem(),
            MOSSY_COBBLESTONE_WALL.parseItem(),
            MOSSY_STONE_BRICKS.parseItem(),
            MOSSY_STONE_BRICK_STAIRS.parseItem(),
            MOSSY_STONE_BRICK_SLAB.parseItem(),
            MOSSY_STONE_BRICK_WALL.parseItem(),
            ACACIA_LOG.parseItem(),
            TERRACOTTA.parseItem(),
            EXPOSED_CUT_COPPER.parseItem(),
            EXPOSED_CUT_COPPER_STAIRS.parseItem(),
            EXPOSED_CUT_COPPER_SLAB.parseItem(),
            POLISHED_GRANITE.parseItem(),
            POLISHED_GRANITE_STAIRS.parseItem(),
            POLISHED_GRANITE_SLAB.parseItem(),
            GRANITE.parseItem(),
            GRANITE_STAIRS.parseItem(),
            GRANITE_SLAB.parseItem(),
            GRANITE_WALL.parseItem(),
            JUNGLE_PLANKS.parseItem(),
            JUNGLE_DOOR.parseItem(),
            JUNGLE_STAIRS.parseItem(),
            JUNGLE_FENCE.parseItem(),
            JUNGLE_FENCE_GATE.parseItem(),
            JUNGLE_SLAB.parseItem(),
            JUNGLE_TRAPDOOR.parseItem(),
            JUNGLE_PRESSURE_PLATE.parseItem(),
            JUNGLE_HANGING_SIGN.parseItem(),
            JUNGLE_SIGN.parseItem(),
            JUNGLE_BUTTON.parseItem(),
            MUD_BRICKS.parseItem(),
            MUD_BRICK_STAIRS.parseItem(),
            MUD_BRICK_SLAB.parseItem(),
            MUD_BRICK_WALL.parseItem(),
            PACKED_MUD.parseItem(),
            WHITE_TERRACOTTA.parseItem(),
            BIRCH_PLANKS.parseItem(),
            BIRCH_DOOR.parseItem(),
            BIRCH_STAIRS.parseItem(),
            BIRCH_FENCE.parseItem(),
            BIRCH_FENCE_GATE.parseItem(),
            BIRCH_SLAB.parseItem(),
            BIRCH_TRAPDOOR.parseItem(),
            BIRCH_PRESSURE_PLATE.parseItem(),
            BIRCH_HANGING_SIGN.parseItem(),
            BIRCH_SIGN.parseItem(),
            BIRCH_BUTTON.parseItem(),
            END_STONE_BRICKS.parseItem(),
            END_STONE_BRICK_STAIRS.parseItem(),
            END_STONE_BRICK_SLAB.parseItem(),
            END_STONE_BRICK_WALL.parseItem(),
            END_STONE.parseItem(),
            SANDSTONE.parseItem(),
            SANDSTONE_STAIRS.parseItem(),
            SANDSTONE_SLAB.parseItem(),
            SANDSTONE_WALL.parseItem(),
            CHISELED_SANDSTONE.parseItem(),
            SMOOTH_SANDSTONE.parseItem(),
            SMOOTH_SANDSTONE_STAIRS.parseItem(),
            SMOOTH_SANDSTONE_SLAB.parseItem(),
            SAND.parseItem(),
            STRIPPED_BAMBOO_BLOCK.parseItem(),
            BAMBOO_MOSAIC.parseItem(),
            BAMBOO_PLANKS.parseItem(),
            BAMBOO_DOOR.parseItem(),
            BAMBOO_MOSAIC_STAIRS.parseItem(),
            BAMBOO_STAIRS.parseItem(),
            BAMBOO_FENCE.parseItem(),
            BAMBOO_FENCE_GATE.parseItem(),
            BAMBOO_MOSAIC_SLAB.parseItem(),
            BAMBOO_SLAB.parseItem(),
            BAMBOO_TRAPDOOR.parseItem(),
            BAMBOO_PRESSURE_PLATE.parseItem(),
            BAMBOO_HANGING_SIGN.parseItem(),
            BAMBOO_SIGN.parseItem(),
            BAMBOO_BUTTON.parseItem(),
            GOLD_BLOCK.parseItem(),
            YELLOW_CONCRETE_POWDER.parseItem(),
            YELLOW_GLAZED_TERRACOTTA.parseItem(),
            YELLOW_WOOL.parseItem(),
            YELLOW_CARPET.parseItem(),
            YELLOW_CONCRETE.parseItem(),
            YELLOW_TERRACOTTA.parseItem(),
            RAW_GOLD_BLOCK.parseItem(),
            GLOWSTONE.parseItem(),
            HAY_BLOCK.parseItem(),
            SPONGE.parseItem(),
            WET_SPONGE.parseItem(),
            YELLOW_STAINED_GLASS.parseItem(),
            ORANGE_STAINED_GLASS.parseItem(),
            PUMPKIN.parseItem(),
            ORANGE_CONCRETE_POWDER.parseItem(),
            ORANGE_WOOL.parseItem(),
            ORANGE_CARPET.parseItem(),
            ORANGE_CONCRETE.parseItem(),
            MAGMA_BLOCK.parseItem(),
            RED_SAND.parseItem(),
            RED_SANDSTONE.parseItem(),
            RED_SANDSTONE_STAIRS.parseItem(),
            RED_SANDSTONE_SLAB.parseItem(),
            RED_SANDSTONE_WALL.parseItem(),
            CHISELED_RED_SANDSTONE.parseItem(),
            CUT_RED_SANDSTONE.parseItem(),
            SMOOTH_RED_SANDSTONE.parseItem(),
            SMOOTH_RED_SANDSTONE_STAIRS.parseItem(),
            SMOOTH_RED_SANDSTONE_SLAB.parseItem(),
            ORANGE_TERRACOTTA.parseItem(),
            ACACIA_PLANKS.parseItem(),
            ACACIA_DOOR.parseItem(),
            ACACIA_STAIRS.parseItem(),
            ACACIA_FENCE.parseItem(),
            ACACIA_FENCE_GATE.parseItem(),
            ACACIA_SLAB.parseItem(),
            ACACIA_TRAPDOOR.parseItem(),
            ACACIA_PRESSURE_PLATE.parseItem(),
            ACACIA_HANGING_SIGN.parseItem(),
            ACACIA_SIGN.parseItem(),
            ACACIA_BUTTON.parseItem(),
            STRIPPED_ACACIA_LOG.parseItem(),
            STRIPPED_ACACIA_WOOD.parseItem(),
            BRICKS.parseItem(),
            BRICK_STAIRS.parseItem(),
            BRICK_SLAB.parseItem(),
            BRICK_WALL.parseItem(),
            CUT_COPPER.parseItem(),
            CUT_COPPER_STAIRS.parseItem(),
            CUT_COPPER_SLAB.parseItem(),
            COPPER_BLOCK.parseItem(),
            RED_TERRACOTTA.parseItem(),
            PINK_TERRACOTTA.parseItem(),
            RED_GLAZED_TERRACOTTA.parseItem(),
            REDSTONE_BLOCK.parseItem(),
            RED_MUSHROOM_BLOCK.parseItem(),
            RED_WOOL.parseItem(),
            RED_CARPET.parseItem(),
            RED_CONCRETE_POWDER.parseItem(),
            RED_CONCRETE.parseItem(),
            RED_STAINED_GLASS.parseItem(),
            RED_NETHER_BRICKS.parseItem(),
            RED_NETHER_BRICK_STAIRS.parseItem(),
            RED_NETHER_BRICK_SLAB.parseItem(),
            RED_NETHER_BRICK_WALL.parseItem(),
            NETHER_WART_BLOCK.parseItem(),
            MANGROVE_PLANKS.parseItem(),
            MANGROVE_DOOR.parseItem(),
            MANGROVE_STAIRS.parseItem(),
            MANGROVE_FENCE.parseItem(),
            MANGROVE_FENCE_GATE.parseItem(),
            MANGROVE_SLAB.parseItem(),
            MANGROVE_TRAPDOOR.parseItem(),
            MANGROVE_PRESSURE_PLATE.parseItem(),
            MANGROVE_HANGING_SIGN.parseItem(),
            MANGROVE_SIGN.parseItem(),
            MANGROVE_BUTTON.parseItem(),
            CRIMSON_HYPHAE.parseItem(),
            CRIMSON_STEM.parseItem(),
            PURPLE_TERRACOTTA.parseItem(),
            STRIPPED_CRIMSON_STEM.parseItem(),
            STRIPPED_CRIMSON_HYPHAE.parseItem(),
            CRIMSON_PLANKS.parseItem(),
            CRIMSON_DOOR.parseItem(),
            CRIMSON_STAIRS.parseItem(),
            CRIMSON_FENCE.parseItem(),
            CRIMSON_FENCE_GATE.parseItem(),
            CRIMSON_SLAB.parseItem(),
            CRIMSON_TRAPDOOR.parseItem(),
            CRIMSON_PRESSURE_PLATE.parseItem(),
            CRIMSON_HANGING_SIGN.parseItem(),
            CRIMSON_SIGN.parseItem(),
            CRIMSON_BUTTON.parseItem(),
            CRIMSON_NYLIUM.parseItem(),
            NETHERRACK.parseItem(),
            NETHER_QUARTZ_ORE.parseItem(),
            BLACK_GLAZED_TERRACOTTA.parseItem(),
            NETHER_BRICKS.parseItem(),
            NETHER_BRICK_STAIRS.parseItem(),
            NETHER_BRICK_SLAB.parseItem(),
            NETHER_BRICK_WALL.parseItem(),
            CHISELED_NETHER_BRICKS.parseItem(),
            CRACKED_NETHER_BRICKS.parseItem(),
            BLACK_TERRACOTTA.parseItem(),
            GRAY_TERRACOTTA.parseItem(),
            BROWN_TERRACOTTA.parseItem(),
            STRIPPED_DARK_OAK_LOG.parseItem(),
            STRIPPED_DARK_OAK_WOOD.parseItem(),
            SPRUCE_LOG.parseItem(),
            SPRUCE_WOOD.parseItem(),
            DARK_OAK_PLANKS.parseItem(),
            DARK_OAK_DOOR.parseItem(),
            DARK_OAK_STAIRS.parseItem(),
            DARK_OAK_FENCE.parseItem(),
            DARK_OAK_FENCE_GATE.parseItem(),
            DARK_OAK_SLAB.parseItem(),
            DARK_OAK_TRAPDOOR.parseItem(),
            DARK_OAK_PRESSURE_PLATE.parseItem(),
            DARK_OAK_HANGING_SIGN.parseItem(),
            DARK_OAK_SIGN.parseItem(),
            DARK_OAK_BUTTON.parseItem(),
            DARK_OAK_LOG.parseItem(),
            DARK_OAK_WOOD.parseItem(),
            MANGROVE_ROOTS.parseItem(),
            MUDDY_MANGROVE_ROOTS.parseItem(),
            SOUL_SAND.parseItem(),
            ANCIENT_DEBRIS.parseItem(),
            REDSTONE_LAMP.parseItem(),
            NOTE_BLOCK.parseItem(),
            JUKEBOX.parseItem(),
            JUNGLE_LOG.parseItem(),
            JUNGLE_WOOD.parseItem(),
            BROWN_STAINED_GLASS.parseItem(),
            PODZOL.parseItem(),
            OAK_WOOD.parseItem(),
            SPRUCE_PLANKS.parseItem(),
            SPRUCE_DOOR.parseItem(),
            SPRUCE_STAIRS.parseItem(),
            SPRUCE_FENCE.parseItem(),
            SPRUCE_FENCE_GATE.parseItem(),
            SPRUCE_SLAB.parseItem(),
            SPRUCE_TRAPDOOR.parseItem(),
            SPRUCE_PRESSURE_PLATE.parseItem(),
            SPRUCE_HANGING_SIGN.parseItem(),
            SPRUCE_SIGN.parseItem(),
            SPRUCE_BUTTON.parseItem(),
            BROWN_CONCRETE.parseItem(),
            BROWN_WOOL.parseItem(),
            BROWN_CARPET.parseItem(),
            BROWN_CONCRETE_POWDER.parseItem(),
            COARSE_DIRT.parseItem(),
            ROOTED_DIRT.parseItem(),
            MYCELIUM.parseItem(),
            LIGHT_GRAY_TERRACOTTA.parseItem(),
            OAK_PLANKS.parseItem(),
            OAK_DOOR.parseItem(),
            OAK_STAIRS.parseItem(),
            OAK_FENCE.parseItem(),
            OAK_FENCE_GATE.parseItem(),
            OAK_SLAB.parseItem(),
            OAK_TRAPDOOR.parseItem(),
            OAK_PRESSURE_PLATE.parseItem(),
            OAK_HANGING_SIGN.parseItem(),
            OAK_SIGN.parseItem(),
            OAK_BUTTON.parseItem(),
            STRIPPED_JUNGLE_LOG.parseItem(),
            STRIPPED_JUNGLE_WOOD.parseItem(),
            BROWN_GLAZED_TERRACOTTA.parseItem(),
    };

    /**
     * Get all blocks by color that are supported by the server version sorted by color
     * @return a list of all blocks
     */
    public static List<ItemStack> getBlocksByColor() {
        return Arrays.stream(BLOCKS_BY_COLOR).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * Get all solid blocks that are supported by the server version sorted by color
     * Solid blocks are blocks that are not transparent and do not allow light to pass through.
     * @return a list of all solid blocks
     */
    public static List<ItemStack> getSolidBlocks() {
        List<ItemStack> items = getBlocksByColor();

        new ArrayList<>(items).stream().filter(item -> !item.getType().isOccluding()).forEach(items::remove);

        return items;
    }

    /**
     * Get all wall blocks by color that are supported by the server version sorted by color.
     * Wall Blocks are solid blocks + glass blocks
     * @return a list of all wall blocks
     */
    public static List<ItemStack> getWallBlocks(){
        List<ItemStack> items = getBlocksByColor();

        new ArrayList<>(items).stream().filter(item ->
                !item.getType().isOccluding() &&! item.getType().toString().endsWith("STAINED_GLASS"))
                .forEach(items::remove);

        return items;
    }




    // ----------------- SLABS -----------------

    /**
     * Get all slabs that are supported by the server version sorted by color
     * @return a list of all slabs
     */
    public static List<ItemStack> getSlabs() {
        return getBlocksByColor().stream().filter(item -> item != null && item.getType().toString().endsWith("_SLAB")).collect(Collectors.toList());
    }




    // ----------------- STAIRS -----------------

    /**
     * Get all stairs that are supported by the server version sorted by color
     * @return a list of all stairs
     */
    public static List<ItemStack> getStairs() {
        return getBlocksByColor().stream().filter(item -> item != null && item.getType().toString().endsWith("_STAIRS")).collect(Collectors.toList());
    }




    // ----------------- FENCES -----------------

    /**
     * Get all fences that are supported by the server version sorted by color
     * @return a list of all fences
     */
    public static List<ItemStack> getFences() {
        return getBlocksByColor().stream().filter(item -> item != null && item.getType().toString().endsWith("_FENCE")).collect(Collectors.toList());
    }




    // ----------------- LOGS -----------------

    /**
     * Get all logs that are supported by the server version sorted by color
     * @return a list of all logs
     */
    public static List<ItemStack> getLogs() {
        return getBlocksByColor().stream().filter(item -> item != null && item.getType().toString().endsWith("_LOG")).collect(Collectors.toList());
    }

    /**
     * Get all woods that are supported by the server version sorted by color
     * @return a list of all logs
     */
    public static List<ItemStack> getWoods() {
        return getBlocksByColor().stream().filter(item -> item != null && item.getType().toString().endsWith("_WOOD")).collect(Collectors.toList());
    }




    // ----------------- LEAVES -----------------

    /**
     * Get all leaves that are supported by the server version  sorted by color
     * @return a list of all leaves
     */

    public static List<ItemStack> getLeaves() {
        return getBlocksByColor().stream().filter(item -> item != null && item.getType().toString().endsWith("_LEAVES")).collect(Collectors.toList());
    }




    // ----------------- GLASS -----------------

    /**
     * Get all glass blocks that are supported by the server version  sorted by color
     * @return a list of all glass blocks
     */
    public static List<ItemStack> getGlass() {
        return getBlocksByColor().stream().filter(item -> item != null && item.getType().toString().endsWith("_STAINED_GLASS")).collect(Collectors.toList());
    }




    // ----------------- WOOLS -----------------

    /**
     * Get all wool blocks that are supported by the server version sorted by color
     * @return a list of all wool blocks
     */

    public static List<ItemStack> getWools() {
        return getBlocksByColor().stream().filter(item -> item != null && item.getType().toString().endsWith("_WOOL")).collect(Collectors.toList());
    }




    // ----------------- CARPETS -----------------

    /**
     * Get all carpets that are supported by the server version sorted by color
     * @return a list of all carpets
     */
    public static List<ItemStack> getCarpets() {
        return getBlocksByColor().stream().filter(item -> item != null && item.getType().toString().endsWith("_CARPET")).collect(Collectors.toList());
    }




    // ----------------- OTHER -----------------


    public static ItemStack ITEM_BACKGROUND = new Item(GRAY_STAINED_GLASS_PANE.parseItem()).setDisplayName(" ").build();


    public static Material[] getIgnoredMaterials(){
        List<ItemStack> items = new ArrayList<>();

        items.addAll(getLogs());
        items.addAll(getWoods());
        items.addAll(getLeaves());
        items.addAll(getWools());
        items.add(SNOW.parseItem());

        return items.stream().map(ItemStack::getType).toArray(Material[]::new);
    }



    public static XMaterial convertStairToBlock(XMaterial stair) {

        switch (stair) {
            case QUARTZ_STAIRS: return QUARTZ_BLOCK;
            case POLISHED_BLACKSTONE_BRICK_STAIRS: return POLISHED_BLACKSTONE_BRICKS;
            case POLISHED_BLACKSTONE_STAIRS: return POLISHED_BLACKSTONE;
            case BLACKSTONE_STAIRS: return BLACKSTONE;
            case DEEPSLATE_TILE_STAIRS: return DEEPSLATE_TILES;
            case DEEPSLATE_BRICK_STAIRS: return DEEPSLATE_BRICKS;
            case POLISHED_DEEPSLATE_STAIRS: return POLISHED_DEEPSLATE;
            case COBBLED_DEEPSLATE_STAIRS: return COBBLED_DEEPSLATE;
            case STONE_BRICK_STAIRS: return STONE_BRICKS;
            case POLISHED_ANDESITE_STAIRS: return POLISHED_ANDESITE;
            case STONE_STAIRS: return STONE;
            case ANDESITE_STAIRS: return ANDESITE;
            case POLISHED_DIORITE_STAIRS: return POLISHED_DIORITE;
            case DIORITE_STAIRS: return DIORITE;
            case SMOOTH_QUARTZ_STAIRS: return SMOOTH_QUARTZ;
            case CHERRY_STAIRS: return CHERRY_PLANKS;
            case PURPUR_STAIRS: return PURPUR_BLOCK;
            case WARPED_STAIRS: return WARPED_PLANKS;
            case DARK_PRISMARINE_STAIRS: return DARK_PRISMARINE;
            case OXIDIZED_CUT_COPPER_STAIRS: return OXIDIZED_CUT_COPPER;
            case PRISMARINE_BRICK_STAIRS: return PRISMARINE_BRICKS;
            case PRISMARINE_STAIRS: return PRISMARINE;
            case WEATHERED_CUT_COPPER_STAIRS: return WEATHERED_CUT_COPPER;
            case MOSSY_COBBLESTONE_STAIRS: return MOSSY_COBBLESTONE;
            case MOSSY_STONE_BRICK_STAIRS: return MOSSY_STONE_BRICKS;
            case EXPOSED_CUT_COPPER_STAIRS: return EXPOSED_CUT_COPPER;
            case POLISHED_GRANITE_STAIRS: return POLISHED_GRANITE;
            case GRANITE_STAIRS: return GRANITE;
            case JUNGLE_STAIRS: return JUNGLE_PLANKS;
            case MUD_BRICK_STAIRS: return MUD_BRICKS;
            case BIRCH_STAIRS: return BIRCH_PLANKS;
            case END_STONE_BRICK_STAIRS: return END_STONE_BRICKS;
            case SANDSTONE_STAIRS: return SANDSTONE;
            case SMOOTH_SANDSTONE_STAIRS: return SMOOTH_SANDSTONE;
            case BAMBOO_MOSAIC_STAIRS: return BAMBOO_MOSAIC;
            case BAMBOO_STAIRS: return BAMBOO_PLANKS;
            case RED_SANDSTONE_STAIRS: return RED_SANDSTONE;
            case SMOOTH_RED_SANDSTONE_STAIRS: return SMOOTH_RED_SANDSTONE;
            case ACACIA_STAIRS: return ACACIA_PLANKS;
            case BRICK_STAIRS: return BRICKS;
            case CUT_COPPER_STAIRS: return CUT_COPPER;
            case RED_NETHER_BRICK_STAIRS: return RED_NETHER_BRICKS;
            case MANGROVE_STAIRS: return MANGROVE_PLANKS;
            case CRIMSON_STAIRS: return CRIMSON_PLANKS;
            case NETHER_BRICK_STAIRS: return NETHER_BRICKS;
            case DARK_OAK_STAIRS: return DARK_OAK_PLANKS;
            case SPRUCE_STAIRS: return SPRUCE_PLANKS;
            case OAK_STAIRS: return OAK_PLANKS;

            case COBBLESTONE_STAIRS:
            default: return COBBLESTONE;
        }
    }
}
