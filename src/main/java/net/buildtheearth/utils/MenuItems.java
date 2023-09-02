package net.buildtheearth.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MenuItems {

    public static ItemStack closeMenuItem() {
        return Item.create(Material.BARRIER, "§c§lClose");
    }

    public static ItemStack getCurrentPageItem(int currentPage) {
        return getWhiteNumberHead(currentPage, "§eCurrent Page §7- §f" + (currentPage), null);
    }

    public static ItemStack getPreviousPageItem(int currentPage) {
        if(currentPage <= 1)
            return Item.createCustomHeadBase64(WHITE_BLANK, " ", null);

        return Item.createCustomHeadBase64(MenuItems.WHITE_ARROW_LEFT, "§ePrevious Page §7- §f" + (currentPage - 1), null);
    }

    public static ItemStack getNextPageItem(int currentPage, boolean hasNextPage) {
        if(!hasNextPage)
            return Item.createCustomHeadBase64(WHITE_BLANK, " ", null);

        return Item.createCustomHeadBase64(MenuItems.WHITE_ARROW_RIGHT, "§eNext Page §7- §f" + (currentPage + 1), null);
    }



    public static ItemStack getCounterCurrentValueItem(SliderColor sliderColor, String name, int value, String valueType) {
        String sliderName = "§e" + name + ": §7§l" + value;
        if(valueType != null)
            sliderName += " " + valueType;
        
        switch (sliderColor) {
            default:
            case WHITE: return getWhiteNumberHead(value, sliderName, null);
            case LIGHT_GRAY: return getLightGrayNumberHead(value, sliderName, null);
        }
    }

    public static ItemStack getCounterPlusItem(SliderColor sliderColor, String name, int value, int maxValue) {
        if(value >= maxValue)
        switch (sliderColor) {
            case WHITE: return Item.createCustomHeadBase64(WHITE_BLANK, " ", null);
            case LIGHT_GRAY: return Item.createCustomHeadBase64(LIGHT_GRAY_BLANK, " ", null);
        }

        switch (sliderColor) {
            default:
            case WHITE: return Item.createCustomHeadBase64(MenuItems.WHITE_PLUS, "§a§l+ §e" + name, null);
            case LIGHT_GRAY: return Item.createCustomHeadBase64(MenuItems.LIGHT_GRAY_PLUS, "§a§l+ §e" + name, null);
        }
    }

    public static ItemStack getCounterMinusItem(SliderColor sliderColor, String name, int value, int minValue) {
        if(value <= minValue)
        switch (sliderColor) {
            case WHITE: return Item.createCustomHeadBase64(WHITE_BLANK, " ", null);
            case LIGHT_GRAY: return Item.createCustomHeadBase64(LIGHT_GRAY_BLANK, " ", null);
        }

        switch (sliderColor) {
            default:
            case WHITE: return Item.createCustomHeadBase64(MenuItems.WHITE_MINUS, "§c§l- §e" + name, null);
            case LIGHT_GRAY: return Item.createCustomHeadBase64(MenuItems.LIGHT_GRAY_MINUS, "§c§l- §e" + name, null);
        }
    }

    public static ItemStack getXItem(SliderColor sliderColor, String name) {
        switch (sliderColor) {
            default:
            case WHITE: return Item.createCustomHeadBase64(MenuItems.WHITE_X,  name, null);
            case LIGHT_GRAY: return Item.createCustomHeadBase64(MenuItems.LIGHT_GRAY_X, name, null);
        }
    }

    public static ItemStack getBlankItem(SliderColor sliderColor, String name) {
        switch (sliderColor) {
            default:
            case WHITE: return Item.createCustomHeadBase64(MenuItems.WHITE_BLANK,  name, null);
            case LIGHT_GRAY: return Item.createCustomHeadBase64(MenuItems.LIGHT_GRAY_BLANK, name, null);
        }
    }


    public static ItemStack getNextItem(){
        return Item.createCustomHeadBase64(MenuItems.CHECKMARK, "§eNext", null);
    }


    public static final ItemStack[] BLOCKS_BY_COLOR_1_12 = {
            Item.create(Material.STAINED_GLASS, null, (short)15, null),
            Item.create(Material.CONCRETE, null, (short)15, null),
            Item.create(Material.COAL_BLOCK),
            Item.create(Material.CONCRETE_POWDER, null, (short)15, null),
            Item.create(Material.WOOL, null, (short)15, null),
            Item.create(Material.OBSIDIAN),
            Item.create(Material.STAINED_GLASS, null, (short)7, null),
            Item.create(Material.CONCRETE, null, (short)7, null),
            Item.create(Material.CONCRETE_POWDER, null, (short)7, null),
            Item.create(Material.STAINED_CLAY, null, (short)9, null),
            Item.create(Material.CONCRETE, null, (short)8, null),
            Item.create(Material.COBBLESTONE),
            Item.create(Material.SMOOTH_BRICK, null, (short)2, null),
            Item.create(Material.SMOOTH_BRICK),
            Item.create(Material.STAINED_GLASS, null, (short)8, null),
            Item.create(Material.STONE, null, (short)6, null),
            Item.create(Material.STONE),
            Item.create(Material.STONE, null, (short)5, null),
            Item.create(Material.WOOL, null, (short)8, null),
            Item.create(Material.CONCRETE_POWDER, null, (short)8, null),
            Item.create(Material.CLAY),
            Item.create(Material.BONE_BLOCK),
            Item.create(Material.STONE, null, (short)3, null),
            Item.create(Material.STONE, null, (short)4, null),
            Item.create(Material.CONCRETE, null, (short)0, null),
            Item.create(Material.QUARTZ_BLOCK, null, (short)1, null),
            Item.create(Material.QUARTZ_BLOCK, null, (short)2, null),
            Item.create(Material.QUARTZ_BLOCK, null, (short)0, null),
            Item.create(Material.LOG, null, (short)2, null),
            Item.create(Material.SEA_LANTERN),
            Item.create(Material.IRON_BLOCK),
            Item.create(Material.CONCRETE_POWDER, null, (short)0, null),
            Item.create(Material.WOOL),
            Item.create(Material.SNOW_BLOCK),
            Item.create(Material.STAINED_GLASS),
            Item.create(Material.CONCRETE_POWDER, null, (short)6, null),
            Item.create(Material.PINK_GLAZED_TERRACOTTA),
            Item.create(Material.WOOL, null, (short)6, null),
            Item.create(Material.WOOL, null, (short)2, null),
            Item.create(Material.MAGENTA_GLAZED_TERRACOTTA),
            Item.create(Material.CONCRETE_POWDER, null, (short)2, null),
            Item.create(Material.PURPUR_BLOCK),
            Item.create(Material.PURPUR_PILLAR),
            Item.create(Material.STAINED_GLASS, null, (short)2, null),
            Item.create(Material.STAINED_GLASS, null, (short)10, null),
            Item.create(Material.CONCRETE_POWDER, null, (short)10, null),
            Item.create(Material.WOOL, null, (short)10, null),
            Item.create(Material.CONCRETE, null, (short)10, null),
            Item.create(Material.STAINED_CLAY, null, (short)11, null),
            Item.create(Material.STAINED_CLAY, null, (short)3, null),
            Item.create(Material.STAINED_GLASS, null, (short)11, null),
            Item.create(Material.STAINED_GLASS, null, (short)3, null),
            Item.create(Material.CONCRETE, null, (short)11, null),
            Item.create(Material.LAPIS_BLOCK),
            Item.create(Material.WOOL, null, (short)11, null),
            Item.create(Material.CONCRETE_POWDER, null, (short)11, null),
            Item.create(Material.PRISMARINE, null, (short)2, null),
            Item.create(Material.WOOL, null, (short)9, null),
            Item.create(Material.PRISMARINE),
            Item.create(Material.PRISMARINE, null, (short)1, null),
            Item.create(Material.PACKED_ICE),
            Item.create(Material.ICE),
            Item.create(Material.CONCRETE, null, (short)3, null),
            Item.create(Material.WOOL, null, (short)3, null),
            Item.create(Material.CONCRETE_POWDER, null, (short)3, null),
            Item.create(Material.DIAMOND_BLOCK),
            Item.create(Material.LIME_GLAZED_TERRACOTTA),
            Item.create(Material.CONCRETE_POWDER, null, (short)5, null),
            Item.create(Material.WOOL, null, (short)5, null),
            Item.create(Material.CONCRETE, null, (short)5, null),
            Item.create(Material.MELON_BLOCK),
            Item.create(Material.STAINED_GLASS, null, (short)5, null),
            Item.create(Material.EMERALD_BLOCK),
            Item.create(Material.GREEN_GLAZED_TERRACOTTA),
            Item.create(Material.CONCRETE_POWDER, null, (short)13, null),
            Item.create(Material.WOOL, null, (short)13, null),
            Item.create(Material.CONCRETE, null, (short)13, null),
            Item.create(Material.GRASS),
            Item.create(Material.STAINED_CLAY, null, (short)5, null),
            Item.create(Material.STAINED_CLAY, null, (short)13, null),
            Item.create(Material.LEAVES, null, (short)3, null),
            Item.create(Material.LEAVES, null, (short)0, null),
            Item.create(Material.LEAVES_2, null, (short)1, null),
            Item.create(Material.LEAVES_2, null, (short)0, null),
            Item.create(Material.LEAVES, null, (short)2, null),
            Item.create(Material.LEAVES, null, (short)1, null),
            Item.create(Material.MOSSY_COBBLESTONE),
            Item.create(Material.LOG_2),
            Item.create(Material.HARD_CLAY),
            Item.create(Material.STONE, null, (short)2, null),
            Item.create(Material.STONE, null, (short)1, null),
            Item.create(Material.WOOD, null, (short)3, null),
            Item.create(Material.WOOD, null, (short)2, null),
            Item.create(Material.END_BRICKS),
            Item.create(Material.ENDER_STONE),
            Item.create(Material.SANDSTONE),
            Item.create(Material.SANDSTONE, null, (short)1, null),
            Item.create(Material.SANDSTONE, null, (short)2, null),
            Item.create(Material.SAND),
            Item.create(Material.GOLD_BLOCK),
            Item.create(Material.CONCRETE_POWDER, null, (short)4, null),
            Item.create(Material.YELLOW_GLAZED_TERRACOTTA),
            Item.create(Material.CONCRETE, null, (short)4, null),
            Item.create(Material.STAINED_CLAY, null, (short)4, null),
            Item.create(Material.GLOWSTONE),
            Item.create(Material.HAY_BLOCK),
            Item.create(Material.SPONGE),
            Item.create(Material.SPONGE, null, (short)1, null),
            Item.create(Material.STAINED_GLASS, null, (short)4, null),
            Item.create(Material.STAINED_GLASS, null, (short)1, null),
            Item.create(Material.CONCRETE_POWDER, null, (short)1, null),
            Item.create(Material.WOOL, null, (short)1, null),
            Item.create(Material.CONCRETE, null, (short)1, null),
            Item.create(Material.SAND, null, (short)1, null),
            Item.create(Material.RED_SANDSTONE),
            Item.create(Material.RED_SANDSTONE, null, (short)1, null),
            Item.create(Material.RED_SANDSTONE, null, (short)2, null),
            Item.create(Material.PUMPKIN),
            Item.create(Material.WOOD, null, (short)4, null),
            Item.create(Material.BRICK),
            Item.create(Material.STAINED_CLAY, null, (short)1, null),
            Item.create(Material.MAGMA),
            Item.create(Material.REDSTONE_BLOCK),
            Item.create(Material.HUGE_MUSHROOM_2),
            Item.create(Material.WOOL, null, (short)14, null),
            Item.create(Material.STAINED_CLAY, null, (short)14, null),
            Item.create(Material.CONCRETE, null, (short)14, null),
            Item.create(Material.RED_NETHER_BRICK),
            Item.create(Material.STAINED_GLASS, null, (short)14, null),
            Item.create(Material.NETHERRACK),
            Item.create(Material.QUARTZ_ORE),
            Item.create(Material.BLACK_GLAZED_TERRACOTTA),
            Item.create(Material.NETHER_BRICK),
            Item.create(Material.STAINED_CLAY, null, (short)15, null),
            Item.create(Material.LOG, null, (short)1, null),
            Item.create(Material.WOOD, null, (short)5, null),
            Item.create(Material.LOG_2, null, (short)1, null),
            Item.create(Material.STAINED_CLAY, null, (short)7, null),
            Item.create(Material.LOG, null, (short)3, null),
            Item.create(Material.DIRT, null, (short)2, null),
            Item.create(Material.STAINED_CLAY, null, (short)12, null),
            Item.create(Material.SOUL_SAND),
            Item.create(Material.REDSTONE_LAMP_OFF),
            Item.create(Material.NOTE_BLOCK),
            Item.create(Material.JUKEBOX),
            Item.create(Material.STAINED_GLASS, null, (short)12, null),
            Item.create(Material.LOG, null, (short)0, null),
            Item.create(Material.CONCRETE, null, (short)12, null),
            Item.create(Material.WOOD, null, (short)1, null),
            Item.create(Material.WOOL, null, (short)12, null),
            Item.create(Material.CONCRETE_POWDER, null, (short)12, null),
            Item.create(Material.DIRT, null, (short)1, null),
            Item.create(Material.MYCEL),
            Item.create(Material.WOOD)
    };

    public static List<ItemStack> getBlocksByColor(){
        return Arrays.asList(BLOCKS_BY_COLOR_1_12);
    }

    public static final ItemStack[] WALL_BLOCKS = {
            Item.create(Material.CONCRETE, null, (short)15, null),
            Item.create(Material.COAL_BLOCK),
            Item.create(Material.CONCRETE_POWDER, null, (short)15, null),
            Item.create(Material.WOOL, null, (short)15, null),
            Item.create(Material.OBSIDIAN),
            Item.create(Material.CONCRETE, null, (short)7, null),
            Item.create(Material.CONCRETE_POWDER, null, (short)7, null),
            Item.create(Material.STAINED_CLAY, null, (short)9, null),
            Item.create(Material.CONCRETE, null, (short)8, null),
            Item.create(Material.COBBLESTONE),
            Item.create(Material.SMOOTH_BRICK, null, (short)2, null),
            Item.create(Material.SMOOTH_BRICK),
            Item.create(Material.STONE, null, (short)6, null),
            Item.create(Material.STONE),
            Item.create(Material.STONE, null, (short)5, null),
            Item.create(Material.WOOL, null, (short)8, null),
            Item.create(Material.CONCRETE_POWDER, null, (short)8, null),
            Item.create(Material.CLAY),
            Item.create(Material.BONE_BLOCK),
            Item.create(Material.STONE, null, (short)3, null),
            Item.create(Material.STONE, null, (short)4, null),
            Item.create(Material.CONCRETE, null, (short)0, null),
            Item.create(Material.QUARTZ_BLOCK, null, (short)1, null),
            Item.create(Material.QUARTZ_BLOCK, null, (short)2, null),
            Item.create(Material.QUARTZ_BLOCK, null, (short)0, null),
            Item.create(Material.LOG, null, (short)2, null),
            Item.create(Material.IRON_BLOCK),
            Item.create(Material.CONCRETE_POWDER, null, (short)0, null),
            Item.create(Material.WOOL),
            Item.create(Material.SNOW_BLOCK),
            Item.create(Material.CONCRETE_POWDER, null, (short)6, null),
            Item.create(Material.WOOL, null, (short)6, null),
            Item.create(Material.WOOL, null, (short)2, null),
            Item.create(Material.CONCRETE_POWDER, null, (short)2, null),
            Item.create(Material.CONCRETE_POWDER, null, (short)10, null),
            Item.create(Material.WOOL, null, (short)10, null),
            Item.create(Material.CONCRETE, null, (short)10, null),
            Item.create(Material.STAINED_CLAY, null, (short)11, null),
            Item.create(Material.STAINED_CLAY, null, (short)3, null),
            Item.create(Material.CONCRETE, null, (short)11, null),
            Item.create(Material.LAPIS_BLOCK),
            Item.create(Material.WOOL, null, (short)11, null),
            Item.create(Material.CONCRETE_POWDER, null, (short)11, null),
            Item.create(Material.PRISMARINE, null, (short)2, null),
            Item.create(Material.WOOL, null, (short)9, null),
            Item.create(Material.PRISMARINE),
            Item.create(Material.PRISMARINE, null, (short)1, null),
            Item.create(Material.PACKED_ICE),
            Item.create(Material.CONCRETE, null, (short)3, null),
            Item.create(Material.WOOL, null, (short)3, null),
            Item.create(Material.CONCRETE_POWDER, null, (short)3, null),
            Item.create(Material.CONCRETE_POWDER, null, (short)5, null),
            Item.create(Material.WOOL, null, (short)5, null),
            Item.create(Material.CONCRETE, null, (short)5, null),
            Item.create(Material.CONCRETE_POWDER, null, (short)13, null),
            Item.create(Material.WOOL, null, (short)13, null),
            Item.create(Material.CONCRETE, null, (short)13, null),
            Item.create(Material.STAINED_CLAY, null, (short)5, null),
            Item.create(Material.STAINED_CLAY, null, (short)13, null),
            Item.create(Material.LOG_2),
            Item.create(Material.HARD_CLAY),
            Item.create(Material.STONE, null, (short)2, null),
            Item.create(Material.STONE, null, (short)1, null),
            Item.create(Material.WOOD, null, (short)3, null),
            Item.create(Material.WOOD, null, (short)2, null),
            Item.create(Material.END_BRICKS),
            Item.create(Material.ENDER_STONE),
            Item.create(Material.SANDSTONE),
            Item.create(Material.SANDSTONE, null, (short)1, null),
            Item.create(Material.SANDSTONE, null, (short)2, null),
            Item.create(Material.SAND),
            Item.create(Material.CONCRETE_POWDER, null, (short)4, null),
            Item.create(Material.CONCRETE, null, (short)4, null),
            Item.create(Material.STAINED_CLAY, null, (short)4, null),
            Item.create(Material.CONCRETE_POWDER, null, (short)1, null),
            Item.create(Material.WOOL, null, (short)1, null),
            Item.create(Material.CONCRETE, null, (short)1, null),
            Item.create(Material.SAND, null, (short)1, null),
            Item.create(Material.RED_SANDSTONE),
            Item.create(Material.RED_SANDSTONE, null, (short)1, null),
            Item.create(Material.RED_SANDSTONE, null, (short)2, null),
            Item.create(Material.PUMPKIN),
            Item.create(Material.WOOD, null, (short)4, null),
            Item.create(Material.BRICK),
            Item.create(Material.STAINED_CLAY, null, (short)1, null),
            Item.create(Material.HUGE_MUSHROOM_2),
            Item.create(Material.WOOL, null, (short)14, null),
            Item.create(Material.STAINED_CLAY, null, (short)14, null),
            Item.create(Material.CONCRETE, null, (short)14, null),
            Item.create(Material.RED_NETHER_BRICK),
            Item.create(Material.NETHERRACK),
            Item.create(Material.NETHER_BRICK),
            Item.create(Material.STAINED_CLAY, null, (short)15, null),
            Item.create(Material.LOG, null, (short)1, null),
            Item.create(Material.WOOD, null, (short)5, null),
            Item.create(Material.LOG_2, null, (short)1, null),
            Item.create(Material.STAINED_CLAY, null, (short)7, null),
            Item.create(Material.LOG, null, (short)3, null),
            Item.create(Material.STAINED_CLAY, null, (short)12, null),
            Item.create(Material.SOUL_SAND),
            Item.create(Material.NOTE_BLOCK),
            Item.create(Material.LOG, null, (short)0, null),
            Item.create(Material.CONCRETE, null, (short)12, null),
            Item.create(Material.WOOD, null, (short)1, null),
            Item.create(Material.WOOL, null, (short)12, null),
            Item.create(Material.CONCRETE_POWDER, null, (short)12, null),
            Item.create(Material.WOOD)
    };

    public static final ItemStack[] SLABS = {
            Item.create(Material.STEP, null, (short) 0, null),
            Item.create(Material.STEP, null, (short) 1, null),
            Item.create(Material.STEP, null, (short) 3, null),
            Item.create(Material.STEP, null, (short) 4, null),
            Item.create(Material.STEP, null, (short) 5, null),
            Item.create(Material.STEP, null, (short) 6, null),
            Item.create(Material.STEP, null, (short) 7, null),
            Item.create(Material.STONE_SLAB2),
            Item.create(Material.PURPUR_SLAB),
            Item.create(Material.WOOD_STEP, null, (short) 0, null),
            Item.create(Material.WOOD_STEP, null, (short) 1, null),
            Item.create(Material.WOOD_STEP, null, (short) 2, null),
            Item.create(Material.WOOD_STEP, null, (short) 3, null),
            Item.create(Material.WOOD_STEP, null, (short) 4, null),
            Item.create(Material.WOOD_STEP, null, (short) 5, null),
    };

    public static List<ItemStack> getSlabs(){
        return Arrays.asList(SLABS);
    }

    public static final ItemStack[] STAIRS = {
            Item.create(Material.COBBLESTONE_STAIRS),
            Item.create(Material.SANDSTONE_STAIRS),
            Item.create(Material.SMOOTH_STAIRS),
            Item.create(Material.SPRUCE_WOOD_STAIRS),
            Item.create(Material.ACACIA_STAIRS),
            Item.create(Material.BIRCH_WOOD_STAIRS),
            Item.create(Material.BRICK_STAIRS),
            Item.create(Material.DARK_OAK_STAIRS),
            Item.create(Material.JUNGLE_WOOD_STAIRS),
            Item.create(Material.NETHER_BRICK_STAIRS),
            Item.create(Material.PURPUR_STAIRS),
            Item.create(Material.QUARTZ_STAIRS),
            Item.create(Material.RED_SANDSTONE_STAIRS),
            Item.create(Material.WOOD_STAIRS)
    };

    public static List<ItemStack> getStairs(){
        return Arrays.asList(STAIRS);
    }

    public static final String WHITE_PLUS = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjBiNTVmNzQ2ODFjNjgyODNhMWMxY2U1MWYxYzgzYjUyZTI5NzFjOTFlZTM0ZWZjYjU5OGRmMzk5MGE3ZTcifX19";
    public static final String WHITE_MINUS = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzNlNGI1MzNlNGJhMmRmZjdjMGZhOTBmNjdlOGJlZjM2NDI4YjZjYjA2YzQ1MjYyNjMxYjBiMjVkYjg1YiJ9fX0=";
    public static final String WHITE_BLANK = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTdjMjE0NGZkY2I1NWMzZmMxYmYxZGU1MWNhYmRmNTJjMzg4M2JjYjU3ODkyMzIyNmJlYjBkODVjYjJkOTgwIn19fQ==";

    public static final String WHITE_X = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWQxYTNjOTY1NjIzNDg1MjdkNTc5OGYyOTE2MDkyODFmNzJlMTZkNjExZjFhNzZjMGZhN2FiZTA0MzY2NSJ9fX0=";


    public static final String WHITE_0_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2YwOTAxOGY0NmYzNDllNTUzNDQ2OTQ2YTM4NjQ5ZmNmY2Y5ZmRmZDYyOTE2YWVjMzNlYmNhOTZiYjIxYjUifX19";
    public static final String WHITE_1_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2E1MTZmYmFlMTYwNThmMjUxYWVmOWE2OGQzMDc4NTQ5ZjQ4ZjZkNWI2ODNmMTljZjVhMTc0NTIxN2Q3MmNjIn19fQ==";
    public static final String WHITE_2_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDY5OGFkZDM5Y2Y5ZTRlYTkyZDQyZmFkZWZkZWMzYmU4YTdkYWZhMTFmYjM1OWRlNzUyZTlmNTRhZWNlZGM5YSJ9fX0=";
    public static final String WHITE_3_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmQ5ZTRjZDVlMWI5ZjNjOGQ2Y2E1YTFiZjQ1ZDg2ZWRkMWQ1MWU1MzVkYmY4NTVmZTlkMmY1ZDRjZmZjZDIifX19";
    public static final String WHITE_4_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjJhM2Q1Mzg5ODE0MWM1OGQ1YWNiY2ZjODc0NjlhODdkNDhjNWMxZmM4MmZiNGU3MmY3MDE1YTM2NDgwNTgifX19";
    public static final String WHITE_5_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDFmZTM2YzQxMDQyNDdjODdlYmZkMzU4YWU2Y2E3ODA5YjYxYWZmZDYyNDVmYTk4NDA2OTI3NWQxY2JhNzYzIn19fQ==";
    public static final String WHITE_6_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2FiNGRhMjM1OGI3YjBlODk4MGQwM2JkYjY0Mzk5ZWZiNDQxODc2M2FhZjg5YWZiMDQzNDUzNTYzN2YwYTEifX19";
    public static final String WHITE_7_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjk3NzEyYmEzMjQ5NmM5ZTgyYjIwY2M3ZDE2ZTE2OGIwMzViNmY4OWYzZGYwMTQzMjRlNGQ3YzM2NWRiM2ZiIn19fQ==";
    public static final String WHITE_8_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWJjMGZkYTlmYTFkOTg0N2EzYjE0NjQ1NGFkNjczN2FkMWJlNDhiZGFhOTQzMjQ0MjZlY2EwOTE4NTEyZCJ9fX0=";
    public static final String WHITE_9_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDZhYmM2MWRjYWVmYmQ1MmQ5Njg5YzA2OTdjMjRjN2VjNGJjMWFmYjU2YjhiMzc1NWU2MTU0YjI0YTVkOGJhIn19fQ==";
    public static final String WHITE_10_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2FmM2ZkNDczYTY0OGI4NDdjY2RhMWQyMDc0NDc5YmI3NjcyNzcxZGM0MzUyMjM0NjhlZDlmZjdiNzZjYjMifX19";
    public static final String WHITE_11_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDhjYWI1M2IwMjA5OGU2ODFhNDZkMWQ3ZjVmZjY5MTc0NmFkZjRlMWZiM2FmZTM1MTZkZDJhZjk0NDU2OSJ9fX0=";
    public static final String WHITE_12_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmZkODNiNWJhYWU0Y2I4NTY5NGExNGQ2ZDEzMzQxZWY3MWFhM2Q5MmQzN2RlMDdiZWE3N2IyYzlkYzUzZSJ9fX0=";
    public static final String WHITE_13_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjFlNTk4NWJlNDg4NmY5ZjE2ZTI0NDdjM2Y0NjEwNTNiNDUxMzQyZDRmYjAxNjZmYjJmODhkZjc0MjIxMzZiNCJ9fX0=";
    public static final String WHITE_14_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTY4MTQ1NjQzOGFlOWIyZDRkMmJmYWI5Y2YzZmZhOTM1NGVlYmRiM2YwMmNlMjk1NzkyOTM0OGU1Yjg1ZmY5NSJ9fX0=";
    public static final String WHITE_15_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzE5YzRkYjczNjViMWI4OGIxMjllNzA0MTg0MjEzZmUwNzhkODhiYzNkNGFlM2Q1MjI5MGY2MWQ5NTVkNTEifX19";
    public static final String WHITE_16_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWY1ZGQwNzliOThmZGFjNDNhMTlhNzk1YmE0NmZkOTdmMjNlYTc3NTdkOTJhZDBhNjlhZGM5NzMyODllNWEifX19";
    public static final String WHITE_17_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmU1MWJmZThlYmZhYTU4NWE3ODdlMWNiNzcyYzdmZDdkOWE5Mjg2ZDk1ZWZhNTRkNjZmYTgyNzRmMTg4ZiJ9fX0=";
    public static final String WHITE_18_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjZkZTlhNWEyZDhhMjM3MDcwMTliOWVmNjFkMTY2Mjg2MGUwYjE2NTNkZjZjMjc2MTZiZTJjNzZmY2QxODc1In19fQ==";
    public static final String WHITE_19_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGJkNDU5MDRkMzRiNjM2YjJmNjQyNjFiM2Q4YmNlZDI1ODI4YzJiOGM0ODIzYjdlMTgzZWU4YTZmMWEyODRkIn19fQ==";
    public static final String WHITE_20_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzRiOTNjNzIxNTE5ZTE0OTY0OWI3ZTRhZmI2ZDc2Y2ZjODE0NjA4YWU5Yzk1ZTdjM2RiNGJmNGJkYWFjZjMxZSJ9fX0=";

    public static final String WHITE_ARROW_LEFT = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RjOWU0ZGNmYTQyMjFhMWZhZGMxYjViMmIxMWQ4YmVlYjU3ODc5YWYxYzQyMzYyMTQyYmFlMWVkZDUifX19";
    public static final String WHITE_ARROW_RIGHT = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTU2YTM2MTg0NTllNDNiMjg3YjIyYjdlMjM1ZWM2OTk1OTQ1NDZjNmZjZDZkYzg0YmZjYTRjZjMwYWI5MzExIn19fQ==";
    
    
    public static final String LIGHT_GRAY_PLUS = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjMyZmZmMTYzZTIzNTYzMmY0MDQ3ZjQ4NDE1OTJkNDZmODVjYmJmZGU4OWZjM2RmNjg3NzFiZmY2OWE2NjIifX19";
    public static final String LIGHT_GRAY_MINUS = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGE1NmRhYjUzZDRlYTFhNzlhOGU1ZWQ2MzIyYzJkNTZjYjcxNGRkMzVlZGY0Nzg3NjNhZDFhODRhODMxMCJ9fX0=";
    public static final String LIGHT_GRAY_BLANK = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODFmYjhjZTY0MDhhNTg1MTM4NGUxYzJlZjc1Mzg1MWVhYzE4YmE0MDE4MjY2Y2RkNjY5ZGM5NDQ4NzNkNDIifX19";

    public static final String LIGHT_GRAY_X = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjVmM2VhN2M3YjI2YTA1NGE5ZmJiYjI4Yjk3YTYwODk5OWMyYzczZGY3NWJmNmIyMzQ4ZDdmYjFlNTllODU1In19fQ==";
    public static final String LIGHT_GRAY_0_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmZhNDU5MTFiMTYyOThjZmNhNGIyMjkxZWVkYTY2NjExM2JjNmYyYTM3ZGNiMmVjZDhjMjc1NGQyNGVmNiJ9fX0=";
    public static final String LIGHT_GRAY_1_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2FmMWIyODBjYWI1OWY0NDY5ZGFiOWYxYTJhZjc5MjdlZDk2YTgxZGYxZTI0ZDUwYThlMzk4NGFiZmU0MDQ0In19fQ==";
    public static final String LIGHT_GRAY_2_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTRiMWUxZDQyNjEyM2NlNDBjZDZhNTRiMGY4NzZhZDMwYzA4NTM5Y2Y1YTZlYTYzZTg0N2RjNTA3OTUwZmYifX19";
    public static final String LIGHT_GRAY_3_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTA0Y2NmOGI1MzMyYzE5NmM5ZWEwMmIyMmIzOWI5OWZhY2QxY2M4MmJmZTNmN2Q3YWVlZGMzYzMzMjkwMzkifX19";
    public static final String LIGHT_GRAY_4_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmI0ZmMxOGU5NzVmNGYyMjJkODg1MjE2ZTM2M2FkYzllNmQ0NTZhYTI5MDgwZTQ4ZWI0NzE0NGRkYTQzNmY3In19fQ==";
    public static final String LIGHT_GRAY_5_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWQ4YjIyMjM5NzEyZTBhZDU3OWE2MmFlNGMxMTUxMDNlNzcyODgyNWUxNzUwOGFjZDZjYzg5MTc0ZWU4MzgifX19";
    public static final String LIGHT_GRAY_6_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWVlZmJhZDE2NzEyYTA1Zjk4ZTRmMGRlNWI0NDg2YWYzOTg3YjQ2ZWE2YWI0ZTNiZTkzZDE0YTgzMmM1NmUifX19";
    public static final String LIGHT_GRAY_7_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTNlNjlmYTk0MmRmM2Q1ZWE1M2EzYTk3NDkxNjE3NTEwOTI0YzZiOGQ3YzQzNzExOTczNzhhMWNmMmRlZjI3In19fQ==";
    public static final String LIGHT_GRAY_8_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2QxODRmZDRhYjUxZDQ2MjJmNDliNTRjZTdhMTM5NWMyOWYwMmFkMzVjZTVhYmQ1ZDNjMjU2MzhmM2E4MiJ9fX0=";
    public static final String LIGHT_GRAY_9_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWIyNDU0YTVmYWEyNWY3YzRmNTc3MWQ1MmJiNGY1NWRlYjE5MzlmNzVlZmQ4ZTBhYzQyMTgxMmJhM2RjNyJ9fX0=";
    public static final String LIGHT_GRAY_10_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzZiYWY3ODZjYmI2NmZkOTQzY2I0NWIxZmE1MmYzNjI4OWEzOWYyZDk4NThkOWJlYmUxZTFhMzcwZDdkZmNjIn19fQ==";
    public static final String LIGHT_GRAY_11_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmRlNDI5YmM3MmIyY2M3ZmY3MGMxZDZjOWYxMTE2ZWMwNzExMmYxZjY0YzU4YmQ4YjljNDgwODNmZTIwMSJ9fX0=";
    public static final String LIGHT_GRAY_12_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWNlMDg0MWYwNjExZDg1NWQ1MGUxNmRkYzNkODM0MDZhN2MwNjRhZDYyNzFkNWM2MzE3M2MwNWQzZDNjYjAifX19";
    public static final String LIGHT_GRAY_13_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2ViYTZlOTBkZDg4MTYyNjE2MWRlYjllMWE4NzY1YTFhMzRiZjc3MTE3OTMxYWJlYjU3NzhiNTQ5ZmQyYiJ9fX0=";
    public static final String LIGHT_GRAY_14_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDdjYjU4MjAxNGM2YTFhOTYwYjE3MDQ5NDliMTYyYTk4ZDdmNjU1ZmI2NmM2ZjE4MTU0ZWNjZGE2YTVmYTY1In19fQ==";
    public static final String LIGHT_GRAY_15_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGM0OTYwMWVmNjlkNjNkY2YxZDlmOGVkMThhMzhiNGI3ZjM0NGY3Njc4YjM2NTU1OWE0NzM5ZmNmYmZkOTYyIn19fQ==";
    public static final String LIGHT_GRAY_16_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjkwZmVmY2YyZGNhMTlhNjcyYzYxNTllMjg3YzRlZGVmNGU5MWY0NTllODNmZmRhZjYxNmI4ZTg4Y2QyYTgifX19";
    public static final String LIGHT_GRAY_17_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTRlZTU4MTg5ODMyOGMzMTZkMjY2ZWQ2NjRiY2M0ZDI5MzNkOTNiZTc5Mjk4Yzc5OGI3ODlkODNkMzRiM2FlIn19fQ==";
    public static final String LIGHT_GRAY_18_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjhkOTc3ODhiOTRjMzU2YzJmZjQ0NWY0NGY5NTM0ZmU0OGNiNWQyMTU0N2Q2NGZkYmI5OGFjYzFjNWRlZmUifX19";
    public static final String LIGHT_GRAY_19_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmM1YjU5ZDk4YzkyNzRmOWI4NDMzNmNmMWFjYWUxNWIxYmU2OWY0MzQ4OGQ2NDI2YzhlMzQzZWMzN2FiMTI2In19fQ==";
    public static final String LIGHT_GRAY_20_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTRhOTJlZGVkY2FmMTlmYmRjYjUwNWIyMGQ2NzQ3MmJkYTc4MWYyZGQzY2Y3MzcyZmFmOTcyOWQ5NzMxYiJ9fX0=";


    public static final String CHECKMARK = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTkyZTMxZmZiNTljOTBhYjA4ZmM5ZGMxZmUyNjgwMjAzNWEzYTQ3YzQyZmVlNjM0MjNiY2RiNDI2MmVjYjliNiJ9fX0=";

    public static final ItemStack ITEM_BACKGROUND = Item.create(Material.STAINED_GLASS_PANE, " ", (short)15, null);


    public static ItemStack getWhiteNumberHead(int number, String headName, ArrayList<String> lore){
        String b64;
        switch (number){
            case 0: b64 = WHITE_0_B64; break;
            case 1: b64 = WHITE_1_B64; break;
            case 2: b64 = WHITE_2_B64; break;
            case 3: b64 = WHITE_3_B64; break;
            case 4: b64 = WHITE_4_B64; break;
            case 5: b64 = WHITE_5_B64; break;
            case 6: b64 = WHITE_6_B64; break;
            case 7: b64 = WHITE_7_B64; break;
            case 8: b64 = WHITE_8_B64; break;
            case 9: b64 = WHITE_9_B64; break;
            case 10: b64 = WHITE_10_B64; break;
            case 11: b64 = WHITE_11_B64; break;
            case 12: b64 = WHITE_12_B64; break;
            case 13: b64 = WHITE_13_B64; break;
            case 14: b64 = WHITE_14_B64; break;
            case 15: b64 = WHITE_15_B64; break;
            case 16: b64 = WHITE_16_B64; break;
            case 17: b64 = WHITE_17_B64; break;
            case 18: b64 = WHITE_18_B64; break;
            case 19: b64 = WHITE_19_B64; break;
            case 20: b64 = WHITE_20_B64; break;

            default: b64 = WHITE_BLANK; break;
        }
        return Item.createCustomHeadBase64(b64, headName, lore);
    }

    public static ItemStack getLightGrayNumberHead(int number, String headName, ArrayList<String> lore){
        String b64;
        switch (number){
            case 0: b64 = LIGHT_GRAY_0_B64; break;
            case 1: b64 = LIGHT_GRAY_1_B64; break;
            case 2: b64 = LIGHT_GRAY_2_B64; break;
            case 3: b64 = LIGHT_GRAY_3_B64; break;
            case 4: b64 = LIGHT_GRAY_4_B64; break;
            case 5: b64 = LIGHT_GRAY_5_B64; break;
            case 6: b64 = LIGHT_GRAY_6_B64; break;
            case 7: b64 = LIGHT_GRAY_7_B64; break;
            case 8: b64 = LIGHT_GRAY_8_B64; break;
            case 9: b64 = LIGHT_GRAY_9_B64; break;
            case 10: b64 = LIGHT_GRAY_10_B64; break;
            case 11: b64 = LIGHT_GRAY_11_B64; break;
            case 12: b64 = LIGHT_GRAY_12_B64; break;
            case 13: b64 = LIGHT_GRAY_13_B64; break;
            case 14: b64 = LIGHT_GRAY_14_B64; break;
            case 15: b64 = LIGHT_GRAY_15_B64; break;
            case 16: b64 = LIGHT_GRAY_16_B64; break;
            case 17: b64 = LIGHT_GRAY_17_B64; break;
            case 18: b64 = LIGHT_GRAY_18_B64; break;
            case 19: b64 = LIGHT_GRAY_19_B64; break;
            case 20: b64 = LIGHT_GRAY_20_B64; break;

            default: b64 = LIGHT_GRAY_BLANK; break;
        }
        return Item.createCustomHeadBase64(b64, headName, lore);
    }

    public static String convertStairToBlock(String stair){
        stair = stair.split(":")[0];

        switch (stair){
            //COBBLESTONE_STAIRS
            case "67": return "4";

            //SANDSTONE_STAIRS
            case "128": return "24";

            //SMOOTH_STAIRS
            case "109": return "98";

            //SPRUCE_WOOD_STAIRS
            case "134": return "5:1";

            //ACACIA_STAIRS
            case "163": return "5:4";

            //BIRCH_WOOD_STAIRS
            case "135": return "5:2";

            //BRICK_STAIRS
            case "108": return "45";

            //DARK_OAK_STAIRS
            case "164": return "5:5";

            //JUNGLE_WOOD_STAIRS
            case "136": return "5:3";

            //NETHER_BRICK_STAIRS
            case "114": return "112";

            //PURPUR_STAIRS
            case "203": return "201";

            //QUARTZ_STAIRS
            case "156": return "155";

            //RED_SANDSTONE_STAIRS
            case "180": return "179";

            //WOOD_STAIRS
            case "53": return "5";
        }

        return "67";
    }


    public enum SliderColor{
        WHITE, LIGHT_GRAY
    }
}
