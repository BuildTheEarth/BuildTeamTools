package net.buildtheearth.utils;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MenuItems {

    public static ItemStack[] BLOCKS_BY_COLOR_1_12 = {
            XMaterial.BLACK_STAINED_GLASS_PANE.parseItem(),
            /* TODO fix this
            Item.create(XMaterial.STAINED_GLASS, null, (short) 15, null),
            Item.create(XMaterial.CONCRETE, null, (short) 15, null),
            Item.create(XMaterial.COAL_BLOCK),
            Item.create(XMaterial.CONCRETE_POWDER, null, (short) 15, null),
            Item.create(XMaterial.WOOL, null, (short) 15, null),
            Item.create(XMaterial.OBSIDIAN),
            Item.create(XMaterial.STAINED_GLASS, null, (short) 7, null),
            Item.create(XMaterial.CONCRETE, null, (short) 7, null),
            Item.create(XMaterial.CONCRETE_POWDER, null, (short) 7, null),
            Item.create(XMaterial.STAINED_CLAY, null, (short) 9, null),
            Item.create(XMaterial.CONCRETE, null, (short) 8, null),
            Item.create(XMaterial.COBBLESTONE),
            Item.create(XMaterial.SMOOTH_BRICK, null, (short) 2, null),
            Item.create(XMaterial.SMOOTH_BRICK),
            Item.create(XMaterial.STAINED_GLASS, null, (short) 8, null),
            Item.create(XMaterial.STONE, null, (short) 6, null),
            Item.create(XMaterial.STONE),
            Item.create(XMaterial.STONE, null, (short) 5, null),
            Item.create(XMaterial.WOOL, null, (short) 8, null),
            Item.create(XMaterial.CONCRETE_POWDER, null, (short) 8, null),
            Item.create(XMaterial.CLAY),
            Item.create(XMaterial.BONE_BLOCK),
            Item.create(XMaterial.STONE, null, (short) 3, null),
            Item.create(XMaterial.STONE, null, (short) 4, null),
            Item.create(XMaterial.CONCRETE, null, (short) 0, null),
            Item.create(XMaterial.QUARTZ_BLOCK, null, (short) 1, null),
            Item.create(XMaterial.QUARTZ_BLOCK, null, (short) 2, null),
            Item.create(XMaterial.QUARTZ_BLOCK, null, (short) 0, null),
            Item.create(XMaterial.LOG, null, (short) 2, null),
            Item.create(XMaterial.SEA_LANTERN),
            Item.create(XMaterial.IRON_BLOCK),
            Item.create(XMaterial.CONCRETE_POWDER, null, (short) 0, null),
            Item.create(XMaterial.WOOL),
            Item.create(XMaterial.SNOW_BLOCK),
            Item.create(XMaterial.STAINED_GLASS),
            Item.create(XMaterial.CONCRETE_POWDER, null, (short) 6, null),
            Item.create(XMaterial.PINK_GLAZED_TERRACOTTA),
            Item.create(XMaterial.WOOL, null, (short) 6, null),
            Item.create(XMaterial.WOOL, null, (short) 2, null),
            Item.create(XMaterial.MAGENTA_GLAZED_TERRACOTTA),
            Item.create(XMaterial.CONCRETE_POWDER, null, (short) 2, null),
            Item.create(XMaterial.PURPUR_BLOCK),
            Item.create(XMaterial.PURPUR_PILLAR),
            Item.create(XMaterial.STAINED_GLASS, null, (short) 2, null),
            Item.create(XMaterial.STAINED_GLASS, null, (short) 10, null),
            Item.create(XMaterial.CONCRETE_POWDER, null, (short) 10, null),
            Item.create(XMaterial.WOOL, null, (short) 10, null),
            Item.create(XMaterial.CONCRETE, null, (short) 10, null),
            Item.create(XMaterial.STAINED_CLAY, null, (short) 11, null),
            Item.create(XMaterial.STAINED_CLAY, null, (short) 3, null),
            Item.create(XMaterial.STAINED_GLASS, null, (short) 11, null),
            Item.create(XMaterial.STAINED_GLASS, null, (short) 3, null),
            Item.create(XMaterial.CONCRETE, null, (short) 11, null),
            Item.create(XMaterial.LAPIS_BLOCK),
            Item.create(XMaterial.WOOL, null, (short) 11, null),
            Item.create(XMaterial.CONCRETE_POWDER, null, (short) 11, null),
            Item.create(XMaterial.PRISMARINE, null, (short) 2, null),
            Item.create(XMaterial.WOOL, null, (short) 9, null),
            Item.create(XMaterial.PRISMARINE),
            Item.create(XMaterial.PRISMARINE, null, (short) 1, null),
            Item.create(XMaterial.PACKED_ICE),
            Item.create(XMaterial.ICE),
            Item.create(XMaterial.CONCRETE, null, (short) 3, null),
            Item.create(XMaterial.WOOL, null, (short) 3, null),
            Item.create(XMaterial.CONCRETE_POWDER, null, (short) 3, null),
            Item.create(XMaterial.DIAMOND_BLOCK),
            Item.create(XMaterial.LIME_GLAZED_TERRACOTTA),
            Item.create(XMaterial.CONCRETE_POWDER, null, (short) 5, null),
            Item.create(XMaterial.WOOL, null, (short) 5, null),
            Item.create(XMaterial.CONCRETE, null, (short) 5, null),
            Item.create(XMaterial.MELON_BLOCK),
            Item.create(XMaterial.STAINED_GLASS, null, (short) 5, null),
            Item.create(XMaterial.EMERALD_BLOCK),
            Item.create(XMaterial.GREEN_GLAZED_TERRACOTTA),
            Item.create(XMaterial.CONCRETE_POWDER, null, (short) 13, null),
            Item.create(XMaterial.WOOL, null, (short) 13, null),
            Item.create(XMaterial.CONCRETE, null, (short) 13, null),
            Item.create(XMaterial.GRASS),
            Item.create(XMaterial.STAINED_CLAY, null, (short) 5, null),
            Item.create(XMaterial.STAINED_CLAY, null, (short) 13, null),
            Item.create(XMaterial.LEAVES, null, (short) 3, null),
            Item.create(XMaterial.LEAVES, null, (short) 0, null),
            Item.create(XMaterial.LEAVES_2, null, (short) 1, null),
            Item.create(XMaterial.LEAVES_2, null, (short) 0, null),
            Item.create(XMaterial.LEAVES, null, (short) 2, null),
            Item.create(XMaterial.LEAVES, null, (short) 1, null),
            Item.create(XMaterial.MOSSY_COBBLESTONE),
            Item.create(XMaterial.LOG_2),
            Item.create(XMaterial.HARD_CLAY),
            Item.create(XMaterial.STONE, null, (short) 2, null),
            Item.create(XMaterial.STONE, null, (short) 1, null),
            Item.create(XMaterial.WOOD, null, (short) 3, null),
            Item.create(XMaterial.WOOD, null, (short) 2, null),
            Item.create(XMaterial.END_BRICKS),
            Item.create(XMaterial.ENDER_STONE),
            Item.create(XMaterial.SANDSTONE),
            Item.create(XMaterial.SANDSTONE, null, (short) 1, null),
            Item.create(XMaterial.SANDSTONE, null, (short) 2, null),
            Item.create(XMaterial.SAND),
            Item.create(XMaterial.GOLD_BLOCK),
            Item.create(XMaterial.CONCRETE_POWDER, null, (short) 4, null),
            Item.create(XMaterial.YELLOW_GLAZED_TERRACOTTA),
            Item.create(XMaterial.CONCRETE, null, (short) 4, null),
            Item.create(XMaterial.STAINED_CLAY, null, (short) 4, null),
            Item.create(XMaterial.GLOWSTONE),
            Item.create(XMaterial.HAY_BLOCK),
            Item.create(XMaterial.SPONGE),
            Item.create(XMaterial.SPONGE, null, (short) 1, null),
            Item.create(XMaterial.STAINED_GLASS, null, (short) 4, null),
            Item.create(XMaterial.STAINED_GLASS, null, (short) 1, null),
            Item.create(XMaterial.CONCRETE_POWDER, null, (short) 1, null),
            Item.create(XMaterial.WOOL, null, (short) 1, null),
            Item.create(XMaterial.CONCRETE, null, (short) 1, null),
            Item.create(XMaterial.SAND, null, (short) 1, null),
            Item.create(XMaterial.RED_SANDSTONE),
            Item.create(XMaterial.RED_SANDSTONE, null, (short) 1, null),
            Item.create(XMaterial.RED_SANDSTONE, null, (short) 2, null),
            Item.create(XMaterial.PUMPKIN),
            Item.create(XMaterial.WOOD, null, (short) 4, null),
            Item.create(XMaterial.BRICK),
            Item.create(XMaterial.STAINED_CLAY, null, (short) 1, null),
            Item.create(XMaterial.MAGMA),
            Item.create(XMaterial.REDSTONE_BLOCK),
            Item.create(XMaterial.HUGE_MUSHROOM_2),
            Item.create(XMaterial.WOOL, null, (short) 14, null),
            Item.create(XMaterial.STAINED_CLAY, null, (short) 14, null),
            Item.create(XMaterial.CONCRETE, null, (short) 14, null),
            Item.create(XMaterial.RED_NETHER_BRICK),
            Item.create(XMaterial.STAINED_GLASS, null, (short) 14, null),
            Item.create(XMaterial.NETHERRACK),
            Item.create(XMaterial.QUARTZ_ORE),
            Item.create(XMaterial.BLACK_GLAZED_TERRACOTTA),
            Item.create(XMaterial.NETHER_BRICK),
            Item.create(XMaterial.STAINED_CLAY, null, (short) 15, null),
            Item.create(XMaterial.LOG, null, (short) 1, null),
            Item.create(XMaterial.WOOD, null, (short) 5, null),
            Item.create(XMaterial.LOG_2, null, (short) 1, null),
            Item.create(XMaterial.STAINED_CLAY, null, (short) 7, null),
            Item.create(XMaterial.LOG, null, (short) 3, null),
            Item.create(XMaterial.DIRT, null, (short) 2, null),
            Item.create(XMaterial.STAINED_CLAY, null, (short) 12, null),
            Item.create(XMaterial.SOUL_SAND),
            Item.create(XMaterial.REDSTONE_LAMP_OFF),
            Item.create(XMaterial.NOTE_BLOCK),
            Item.create(XMaterial.JUKEBOX),
            Item.create(XMaterial.STAINED_GLASS, null, (short) 12, null),
            Item.create(XMaterial.LOG, null, (short) 0, null),
            Item.create(XMaterial.CONCRETE, null, (short) 12, null),
            Item.create(XMaterial.WOOD, null, (short) 1, null),
            Item.create(XMaterial.WOOL, null, (short) 12, null),
            Item.create(XMaterial.CONCRETE_POWDER, null, (short) 12, null),
            Item.create(XMaterial.DIRT, null, (short) 1, null),
            Item.create(XMaterial.MYCEL),
            Item.create(XMaterial.WOOD)*/
    };
    public static ItemStack[] WALL_BLOCKS = {
            XMaterial.BLACK_CONCRETE.parseItem()

            /* TODO fix this
            Item.create(XMaterial.CONCRETE, null, (short) 15, null),
            Item.create(XMaterial.COAL_BLOCK),
            Item.create(XMaterial.CONCRETE_POWDER, null, (short) 15, null),
            Item.create(XMaterial.WOOL, null, (short) 15, null),
            Item.create(XMaterial.OBSIDIAN),
            Item.create(XMaterial.CONCRETE, null, (short) 7, null),
            Item.create(XMaterial.CONCRETE_POWDER, null, (short) 7, null),
            Item.create(XMaterial.STAINED_CLAY, null, (short) 9, null),
            Item.create(XMaterial.CONCRETE, null, (short) 8, null),
            Item.create(XMaterial.COBBLESTONE),
            Item.create(XMaterial.SMOOTH_BRICK, null, (short) 2, null),
            Item.create(XMaterial.SMOOTH_BRICK),
            Item.create(XMaterial.STONE, null, (short) 6, null),
            Item.create(XMaterial.STONE),
            Item.create(XMaterial.STONE, null, (short) 5, null),
            Item.create(XMaterial.WOOL, null, (short) 8, null),
            Item.create(XMaterial.CONCRETE_POWDER, null, (short) 8, null),
            Item.create(XMaterial.CLAY),
            Item.create(XMaterial.BONE_BLOCK),
            Item.create(XMaterial.STONE, null, (short) 3, null),
            Item.create(XMaterial.STONE, null, (short) 4, null),
            Item.create(XMaterial.CONCRETE, null, (short) 0, null),
            Item.create(XMaterial.QUARTZ_BLOCK, null, (short) 1, null),
            Item.create(XMaterial.QUARTZ_BLOCK, null, (short) 2, null),
            Item.create(XMaterial.QUARTZ_BLOCK, null, (short) 0, null),
            Item.create(XMaterial.LOG, null, (short) 2, null),
            Item.create(XMaterial.IRON_BLOCK),
            Item.create(XMaterial.CONCRETE_POWDER, null, (short) 0, null),
            Item.create(XMaterial.WOOL),
            Item.create(XMaterial.SNOW_BLOCK),
            Item.create(XMaterial.CONCRETE_POWDER, null, (short) 6, null),
            Item.create(XMaterial.WOOL, null, (short) 6, null),
            Item.create(XMaterial.WOOL, null, (short) 2, null),
            Item.create(XMaterial.CONCRETE_POWDER, null, (short) 2, null),
            Item.create(XMaterial.CONCRETE_POWDER, null, (short) 10, null),
            Item.create(XMaterial.WOOL, null, (short) 10, null),
            Item.create(XMaterial.CONCRETE, null, (short) 10, null),
            Item.create(XMaterial.STAINED_CLAY, null, (short) 11, null),
            Item.create(XMaterial.STAINED_CLAY, null, (short) 3, null),
            Item.create(XMaterial.CONCRETE, null, (short) 11, null),
            Item.create(XMaterial.LAPIS_BLOCK),
            Item.create(XMaterial.WOOL, null, (short) 11, null),
            Item.create(XMaterial.CONCRETE_POWDER, null, (short) 11, null),
            Item.create(XMaterial.PRISMARINE, null, (short) 2, null),
            Item.create(XMaterial.WOOL, null, (short) 9, null),
            Item.create(XMaterial.PRISMARINE),
            Item.create(XMaterial.PRISMARINE, null, (short) 1, null),
            Item.create(XMaterial.PACKED_ICE),
            Item.create(XMaterial.CONCRETE, null, (short) 3, null),
            Item.create(XMaterial.WOOL, null, (short) 3, null),
            Item.create(XMaterial.CONCRETE_POWDER, null, (short) 3, null),
            Item.create(XMaterial.CONCRETE_POWDER, null, (short) 5, null),
            Item.create(XMaterial.WOOL, null, (short) 5, null),
            Item.create(XMaterial.CONCRETE, null, (short) 5, null),
            Item.create(XMaterial.CONCRETE_POWDER, null, (short) 13, null),
            Item.create(XMaterial.WOOL, null, (short) 13, null),
            Item.create(XMaterial.CONCRETE, null, (short) 13, null),
            Item.create(XMaterial.STAINED_CLAY, null, (short) 5, null),
            Item.create(XMaterial.STAINED_CLAY, null, (short) 13, null),
            Item.create(XMaterial.LOG_2),
            Item.create(XMaterial.HARD_CLAY),
            Item.create(XMaterial.STONE, null, (short) 2, null),
            Item.create(XMaterial.STONE, null, (short) 1, null),
            Item.create(XMaterial.WOOD, null, (short) 3, null),
            Item.create(XMaterial.WOOD, null, (short) 2, null),
            Item.create(XMaterial.END_BRICKS),
            Item.create(XMaterial.ENDER_STONE),
            Item.create(XMaterial.SANDSTONE),
            Item.create(XMaterial.SANDSTONE, null, (short) 1, null),
            Item.create(XMaterial.SANDSTONE, null, (short) 2, null),
            Item.create(XMaterial.SAND),
            Item.create(XMaterial.CONCRETE_POWDER, null, (short) 4, null),
            Item.create(XMaterial.CONCRETE, null, (short) 4, null),
            Item.create(XMaterial.STAINED_CLAY, null, (short) 4, null),
            Item.create(XMaterial.CONCRETE_POWDER, null, (short) 1, null),
            Item.create(XMaterial.WOOL, null, (short) 1, null),
            Item.create(XMaterial.CONCRETE, null, (short) 1, null),
            Item.create(XMaterial.SAND, null, (short) 1, null),
            Item.create(XMaterial.RED_SANDSTONE),
            Item.create(XMaterial.RED_SANDSTONE, null, (short) 1, null),
            Item.create(XMaterial.RED_SANDSTONE, null, (short) 2, null),
            Item.create(XMaterial.PUMPKIN),
            Item.create(XMaterial.WOOD, null, (short) 4, null),
            Item.create(XMaterial.BRICK),
            Item.create(XMaterial.STAINED_CLAY, null, (short) 1, null),
            Item.create(XMaterial.HUGE_MUSHROOM_2),
            Item.create(XMaterial.WOOL, null, (short) 14, null),
            Item.create(XMaterial.STAINED_CLAY, null, (short) 14, null),
            Item.create(XMaterial.CONCRETE, null, (short) 14, null),
            Item.create(XMaterial.RED_NETHER_BRICK),
            Item.create(XMaterial.NETHERRACK),
            Item.create(XMaterial.NETHER_BRICK),
            Item.create(XMaterial.STAINED_CLAY, null, (short) 15, null),
            Item.create(XMaterial.LOG, null, (short) 1, null),
            Item.create(XMaterial.WOOD, null, (short) 5, null),
            Item.create(XMaterial.LOG_2, null, (short) 1, null),
            Item.create(XMaterial.STAINED_CLAY, null, (short) 7, null),
            Item.create(XMaterial.LOG, null, (short) 3, null),
            Item.create(XMaterial.STAINED_CLAY, null, (short) 12, null),
            Item.create(XMaterial.SOUL_SAND),
            Item.create(XMaterial.NOTE_BLOCK),
            Item.create(XMaterial.LOG, null, (short) 0, null),
            Item.create(XMaterial.CONCRETE, null, (short) 12, null),
            Item.create(XMaterial.WOOD, null, (short) 1, null),
            Item.create(XMaterial.WOOL, null, (short) 12, null),
            Item.create(XMaterial.CONCRETE_POWDER, null, (short) 12, null),
            Item.create(XMaterial.WOOD)
            */
    };
    public static ItemStack[] SLABS = {
            XMaterial.STONE_SLAB.parseItem()
            /* TODO fix this
            Item.create(XMaterial.STEP, null, (short) 0, null),
            Item.create(XMaterial.STEP, null, (short) 1, null),
            Item.create(XMaterial.STEP, null, (short) 3, null),
            Item.create(XMaterial.STEP, null, (short) 4, null),
            Item.create(XMaterial.STEP, null, (short) 5, null),
            Item.create(XMaterial.STEP, null, (short) 6, null),
            Item.create(XMaterial.STEP, null, (short) 7, null),
            Item.create(XMaterial.STONE_SLAB2),
            Item.create(XMaterial.PURPUR_SLAB),
            Item.create(XMaterial.WOOD_STEP, null, (short) 0, null),
            Item.create(XMaterial.WOOD_STEP, null, (short) 1, null),
            Item.create(XMaterial.WOOD_STEP, null, (short) 2, null),
            Item.create(XMaterial.WOOD_STEP, null, (short) 3, null),
            Item.create(XMaterial.WOOD_STEP, null, (short) 4, null),
            Item.create(XMaterial.WOOD_STEP, null, (short) 5, null),
            */
    };
    public static ItemStack[] STAIRS = {
            XMaterial.COBBLESTONE_STAIRS.parseItem()
            /* TODO fix this
            Item.create(XMaterial.COBBLESTONE_STAIRS),
            Item.create(XMaterial.SANDSTONE_STAIRS),
            Item.create(XMaterial.SMOOTH_STAIRS),
            Item.create(XMaterial.SPRUCE_WOOD_STAIRS),
            Item.create(XMaterial.ACACIA_STAIRS),
            Item.create(XMaterial.BIRCH_WOOD_STAIRS),
            Item.create(XMaterial.BRICK_STAIRS),
            Item.create(XMaterial.DARK_OAK_STAIRS),
            Item.create(XMaterial.JUNGLE_WOOD_STAIRS),
            Item.create(XMaterial.NETHER_BRICK_STAIRS),
            Item.create(XMaterial.PURPUR_STAIRS),
            Item.create(XMaterial.QUARTZ_STAIRS),
            Item.create(XMaterial.RED_SANDSTONE_STAIRS),
            Item.create(XMaterial.WOOD_STAIRS)
             */
    };
    public static ItemStack[] FENCES = {
            XMaterial.OAK_FENCE.parseItem()
            /* TODO fix this
            Item.create(XMaterial.FENCE),
            Item.create(XMaterial.BIRCH_FENCE),
            Item.create(XMaterial.ACACIA_FENCE),
            Item.create(XMaterial.JUNGLE_FENCE),
            Item.create(XMaterial.SPRUCE_FENCE),
            Item.create(XMaterial.DARK_OAK_FENCE),
            Item.create(XMaterial.NETHER_FENCE)
             */
    };

    public static ItemStack[] LOGS = {
            XMaterial.ACACIA_LOG.parseItem(),
            XMaterial.BIRCH_LOG.parseItem(),
            XMaterial.CHERRY_LOG.parseItem(),
            XMaterial.DARK_OAK_LOG.parseItem(),
            XMaterial.JUNGLE_LOG.parseItem(),
            XMaterial.MANGROVE_LOG.parseItem(),
            XMaterial.OAK_LOG.parseItem(),
            XMaterial.SPRUCE_LOG.parseItem(),
            XMaterial.STRIPPED_ACACIA_LOG.parseItem(),
            XMaterial.STRIPPED_BIRCH_LOG.parseItem(),
            XMaterial.STRIPPED_CHERRY_LOG.parseItem(),
            XMaterial.STRIPPED_DARK_OAK_LOG.parseItem(),
            XMaterial.STRIPPED_JUNGLE_LOG.parseItem(),
            XMaterial.STRIPPED_MANGROVE_LOG.parseItem(),
            XMaterial.STRIPPED_OAK_LOG.parseItem(),
            XMaterial.STRIPPED_SPRUCE_LOG.parseItem()
    };

    public static ItemStack[] LEAVES = {
            XMaterial.ACACIA_LEAVES.parseItem(),
            XMaterial.AZALEA_LEAVES.parseItem(),
            XMaterial.BIRCH_LEAVES.parseItem(),
            XMaterial.CHERRY_LEAVES.parseItem(),
            XMaterial.DARK_OAK_LEAVES.parseItem(),
            XMaterial.FLOWERING_AZALEA_LEAVES.parseItem(),
            XMaterial.JUNGLE_LEAVES.parseItem(),
            XMaterial.MANGROVE_LEAVES.parseItem(),
            XMaterial.OAK_LEAVES.parseItem(),
            XMaterial.SPRUCE_LEAVES.parseItem()
    };

    public static ItemStack[] WOOLS = {
            XMaterial.BLACK_WOOL.parseItem(),
            XMaterial.BLUE_WOOL.parseItem(),
            XMaterial.BROWN_WOOL.parseItem(),
            XMaterial.CYAN_WOOL.parseItem(),
            XMaterial.GRAY_WOOL.parseItem(),
            XMaterial.GREEN_WOOL.parseItem(),
            XMaterial.LIGHT_BLUE_WOOL.parseItem(),
            XMaterial.LIGHT_GRAY_WOOL.parseItem(),
            XMaterial.LIME_WOOL.parseItem(),
            XMaterial.MAGENTA_WOOL.parseItem(),
            XMaterial.ORANGE_WOOL.parseItem(),
            XMaterial.PINK_WOOL.parseItem(),
            XMaterial.PURPLE_WOOL.parseItem(),
            XMaterial.RED_WOOL.parseItem(),
            XMaterial.WHITE_WOOL.parseItem(),
            XMaterial.YELLOW_WOOL.parseItem()
    };

    public static ItemStack[] CARPETS = {
            XMaterial.BLACK_CARPET.parseItem(),
            XMaterial.BLUE_CARPET.parseItem(),
            XMaterial.BROWN_CARPET.parseItem(),
            XMaterial.CYAN_CARPET.parseItem(),
            XMaterial.GRAY_CARPET.parseItem(),
            XMaterial.GREEN_CARPET.parseItem(),
            XMaterial.LIGHT_BLUE_CARPET.parseItem(),
            XMaterial.LIGHT_GRAY_CARPET.parseItem(),
            XMaterial.LIME_CARPET.parseItem(),
            XMaterial.MAGENTA_CARPET.parseItem(),
            XMaterial.ORANGE_CARPET.parseItem(),
            XMaterial.PINK_CARPET.parseItem(),
            XMaterial.PURPLE_CARPET.parseItem(),
            XMaterial.RED_CARPET.parseItem(),
            XMaterial.WHITE_CARPET.parseItem(),
            XMaterial.YELLOW_CARPET.parseItem()
    };


    public static ItemStack ITEM_BACKGROUND = new Item(XMaterial.GRAY_STAINED_GLASS_PANE.parseItem()).setDisplayName(" ").build();


    public static Material[] getIgnoredMaterials(){
        List<Material> materials = new ArrayList<>();

        materials.addAll(Arrays.asList(Item.asMaterialArray(LOGS)));
        materials.addAll(Arrays.asList(Item.asMaterialArray(LEAVES)));
        materials.addAll(Arrays.asList(Item.asMaterialArray(WOOLS)));
        materials.add(Material.SNOW);

        return materials.toArray(new Material[0]);
    }


    public static ItemStack closeMenuItem() {
        return Item.create(XMaterial.BARRIER.parseMaterial(), "§c§lClose");
    }


    public static List<ItemStack> getBlocksByColor() {
        return Arrays.asList(BLOCKS_BY_COLOR_1_12);
    }

    public static List<ItemStack> getSlabs() {
        return Arrays.asList(SLABS);
    }

    public static List<ItemStack> getStairs() {
        return Arrays.asList(STAIRS);
    }

    public static List<ItemStack> getFences() {
        return Arrays.asList(FENCES);
    }


    public static String convertStairToBlock(String stair) {
        stair = stair.split(":")[0];

        switch (stair) {
            //COBBLESTONE_STAIRS
            case "67":
                return "4";

            //SANDSTONE_STAIRS
            case "128":
                return "24";

            //SMOOTH_STAIRS
            case "109":
                return "98";

            //SPRUCE_WOOD_STAIRS
            case "134":
                return "5:1";

            //ACACIA_STAIRS
            case "163":
                return "5:4";

            //BIRCH_WOOD_STAIRS
            case "135":
                return "5:2";

            //BRICK_STAIRS
            case "108":
                return "45";

            //DARK_OAK_STAIRS
            case "164":
                return "5:5";

            //JUNGLE_WOOD_STAIRS
            case "136":
                return "5:3";

            //NETHER_BRICK_STAIRS
            case "114":
                return "112";

            //PURPUR_STAIRS
            case "203":
                return "201";

            //QUARTZ_STAIRS
            case "156":
                return "155";

            //RED_SANDSTONE_STAIRS
            case "180":
                return "179";

            //WOOD_STAIRS
            case "53":
                return "5";
        }

        return "67";
    }
}
