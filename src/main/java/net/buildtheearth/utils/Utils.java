package net.buildtheearth.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Utils {

	public static ItemStack[] BLOCKS_BY_COLOR_1_12 = {
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

	public static ItemStack[] SLABS = {
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

	public static ItemStack[] STAIRS = {
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


	public static String WHITE_BLANK = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTdjMjE0NGZkY2I1NWMzZmMxYmYxZGU1MWNhYmRmNTJjMzg4M2JjYjU3ODkyMzIyNmJlYjBkODVjYjJkOTgwIn19fQ==";
	public static String WHITE_1_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2E1MTZmYmFlMTYwNThmMjUxYWVmOWE2OGQzMDc4NTQ5ZjQ4ZjZkNWI2ODNmMTljZjVhMTc0NTIxN2Q3MmNjIn19fQ==";
	public static String WHITE_2_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDY5OGFkZDM5Y2Y5ZTRlYTkyZDQyZmFkZWZkZWMzYmU4YTdkYWZhMTFmYjM1OWRlNzUyZTlmNTRhZWNlZGM5YSJ9fX0=";
	public static String WHITE_3_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmQ5ZTRjZDVlMWI5ZjNjOGQ2Y2E1YTFiZjQ1ZDg2ZWRkMWQ1MWU1MzVkYmY4NTVmZTlkMmY1ZDRjZmZjZDIifX19";
	public static String WHITE_4_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjJhM2Q1Mzg5ODE0MWM1OGQ1YWNiY2ZjODc0NjlhODdkNDhjNWMxZmM4MmZiNGU3MmY3MDE1YTM2NDgwNTgifX19";
	public static String WHITE_5_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDFmZTM2YzQxMDQyNDdjODdlYmZkMzU4YWU2Y2E3ODA5YjYxYWZmZDYyNDVmYTk4NDA2OTI3NWQxY2JhNzYzIn19fQ==";
	public static String WHITE_6_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2FiNGRhMjM1OGI3YjBlODk4MGQwM2JkYjY0Mzk5ZWZiNDQxODc2M2FhZjg5YWZiMDQzNDUzNTYzN2YwYTEifX19";
	public static String WHITE_7_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjk3NzEyYmEzMjQ5NmM5ZTgyYjIwY2M3ZDE2ZTE2OGIwMzViNmY4OWYzZGYwMTQzMjRlNGQ3YzM2NWRiM2ZiIn19fQ==";
	public static String WHITE_8_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWJjMGZkYTlmYTFkOTg0N2EzYjE0NjQ1NGFkNjczN2FkMWJlNDhiZGFhOTQzMjQ0MjZlY2EwOTE4NTEyZCJ9fX0=";
	public static String WHITE_9_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDZhYmM2MWRjYWVmYmQ1MmQ5Njg5YzA2OTdjMjRjN2VjNGJjMWFmYjU2YjhiMzc1NWU2MTU0YjI0YTVkOGJhIn19fQ==";
	public static String WHITE_10_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2FmM2ZkNDczYTY0OGI4NDdjY2RhMWQyMDc0NDc5YmI3NjcyNzcxZGM0MzUyMjM0NjhlZDlmZjdiNzZjYjMifX19";
	public static String WHITE_11_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDhjYWI1M2IwMjA5OGU2ODFhNDZkMWQ3ZjVmZjY5MTc0NmFkZjRlMWZiM2FmZTM1MTZkZDJhZjk0NDU2OSJ9fX0=";
	public static String WHITE_12_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmZkODNiNWJhYWU0Y2I4NTY5NGExNGQ2ZDEzMzQxZWY3MWFhM2Q5MmQzN2RlMDdiZWE3N2IyYzlkYzUzZSJ9fX0=";
	public static String WHITE_13_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjFlNTk4NWJlNDg4NmY5ZjE2ZTI0NDdjM2Y0NjEwNTNiNDUxMzQyZDRmYjAxNjZmYjJmODhkZjc0MjIxMzZiNCJ9fX0=";
	public static String WHITE_14_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTY4MTQ1NjQzOGFlOWIyZDRkMmJmYWI5Y2YzZmZhOTM1NGVlYmRiM2YwMmNlMjk1NzkyOTM0OGU1Yjg1ZmY5NSJ9fX0=";
	public static String WHITE_15_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzE5YzRkYjczNjViMWI4OGIxMjllNzA0MTg0MjEzZmUwNzhkODhiYzNkNGFlM2Q1MjI5MGY2MWQ5NTVkNTEifX19";
	public static String WHITE_16_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWY1ZGQwNzliOThmZGFjNDNhMTlhNzk1YmE0NmZkOTdmMjNlYTc3NTdkOTJhZDBhNjlhZGM5NzMyODllNWEifX19";
	public static String WHITE_17_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmU1MWJmZThlYmZhYTU4NWE3ODdlMWNiNzcyYzdmZDdkOWE5Mjg2ZDk1ZWZhNTRkNjZmYTgyNzRmMTg4ZiJ9fX0=";
	public static String WHITE_18_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjZkZTlhNWEyZDhhMjM3MDcwMTliOWVmNjFkMTY2Mjg2MGUwYjE2NTNkZjZjMjc2MTZiZTJjNzZmY2QxODc1In19fQ==";
	public static String WHITE_19_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGJkNDU5MDRkMzRiNjM2YjJmNjQyNjFiM2Q4YmNlZDI1ODI4YzJiOGM0ODIzYjdlMTgzZWU4YTZmMWEyODRkIn19fQ==";
	public static String WHITE_20_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzRiOTNjNzIxNTE5ZTE0OTY0OWI3ZTRhZmI2ZDc2Y2ZjODE0NjA4YWU5Yzk1ZTdjM2RiNGJmNGJkYWFjZjMxZSJ9fX0=";

	public static String WHITE_ARROW_LEFT = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RjOWU0ZGNmYTQyMjFhMWZhZGMxYjViMmIxMWQ4YmVlYjU3ODc5YWYxYzQyMzYyMTQyYmFlMWVkZDUifX19";
	public static String WHITE_ARROW_RIGHT = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTU2YTM2MTg0NTllNDNiMjg3YjIyYjdlMjM1ZWM2OTk1OTQ1NDZjNmZjZDZkYzg0YmZjYTRjZjMwYWI5MzExIn19fQ==";

	public static String CHECKMARK = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTkyZTMxZmZiNTljOTBhYjA4ZmM5ZGMxZmUyNjgwMjAzNWEzYTQ3YzQyZmVlNjM0MjNiY2RiNDI2MmVjYjliNiJ9fX0=";



	public static ItemStack getWhiteNumberHead(int number, String headName, ArrayList<String> lore){
		String b64;
		switch (number){
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

	public static boolean containsStringFromList(String string, List<String> list) {
		for(String s : list)
		if(string.contains(s))
			return true;
		
		return false;
	}
	
	public static int getHighestY(World world, int x, int z) {
	    int y = 255;
	    while(world.getBlockAt(x, y, z).getType() == Material.AIR || world.getBlockAt(x, y, z).getType() == Material.AIR) {
	    	y--; 
	    	if(y == 0)
	    		return 0;
	    }
	    return y;
	}

	public static Player getRandomPlayer(){
		for(Player player : Bukkit.getOnlinePlayers())
			return player;

		return null;
	}
}
