package net.buildtheearth.utils;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.profiles.builder.XSkull;
import com.cryptomorin.xseries.profiles.objects.Profileable;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class provides static utility methods and a fluent builder API for creating and modifying {@link ItemStack} objects in Bukkit.<br>
 * It supports the customization of items with names, lore, enchantments, attributes, and skull textures.
 * <br><br>
 * The following dependencies are required:
 * <ol>
 *     <li><b>Bukkit API</b></li>
 *     <li><b>HeadDatabaseAPI</b></li> (removed for BuildTeamTools)
 *     <li><b>XMaterial</b> from <a href="https://github.com/CryptoMorin/XSeries">XSeries</a></li>
 * </ol>
 *
 * <br>
 * The functions are categorized into the following sections:
 *
 * <br><br><b>Builder Pattern Methods</b>:
 * <br>These methods allow chaining item properties using a fluent API:
 * <br>• {@link #setDisplayName(String)}
 * <br>• {@link #setAmount(int)}
 * <br>• {@link #setLore(List)}
 * <br>• {@link #addEnchantment(Enchantment, int)}
 * <br>• {@link #hideAttributes(boolean)}
 * <br>• {@link #hideEnchantments(boolean)}
 * <br>• {@link #build()}
 *
 * <br><br><b>Item Creation Methods</b>:
 * <br>Static methods to quickly create customized {@link ItemStack} instances:
 * <br>• {@link #create(Material)}
 * <br>• {@link #create(Material, int)}
 * <br>• {@link #create(Material, String)}
 * <br>• {@link #create(Material, String, int)}
 * <br>• {@link #create(Material, String, List)}
 * <br>• {@link #create(Material, String, short, List)}
 * <br>• {@link #create(Material, String, int, List)}
 * <br>• {@link #create(Material, String, List, Enchantment, Integer)}
 * <br>• {@link #create(Material, String, List, Enchantment, Integer, Enchantment, Integer)}
 * <br>• {@link #create(Material, String, List, Enchantment, Integer, Enchantment, Integer, Enchantment, Integer)}
 *
 * <br><br><b>Leather Armor Creation Methods</b>:
 * <br>Special methods for creating and coloring leather armor:
 * <br>• {@link #createLeatherArmor(Material, Color)}
 * <br>• {@link #createLeatherArmor(Material, String, Color, List)}
 * <br>• {@link #createLeatherArmor(Material, String, Color, List, Enchantment, Integer)}
 * <br>• {@link #createLeatherArmor(Material, String, Color, List, Enchantment, Integer, Enchantment, Integer)}
 * <br>• {@link #createLeatherArmor(Material, String, Color, List, Enchantment, Integer, Enchantment, Integer, Enchantment, Integer)}
 *
 * <br><br><b>Item Editing Methods</b>:
 * <br>Modify existing {@link ItemStack} objects:
 * <br>• {@link #edit(ItemStack, Material)}
 * <br>• {@link #edit(ItemStack, int)}
 * <br>• {@link #edit(ItemStack, String)}
 * <br>• {@link #edit(ItemStack, int, String)}
 * <br>• {@link #edit(ItemStack, List)}
 * <br>• {@link #edit(ItemStack, int, String, List)}
 *
 * <br><br><b>Material Conversion Utilities</b>:
 * <br>Utility methods to convert between {@link Material}, {@link XMaterial}, and {@link ItemStack} representations:
 * <br>• {@link #fromUniqueMaterialString(String)} – Converts a namespaced string to an {@link ItemStack}.
 * <br>• {@link #getUniqueMaterialString(ItemStack)} – Gets the namespaced key of an {@link ItemStack}'s material.
 * <br>• {@link #getUniqueMaterialString(XMaterial)} – Gets the namespaced key of an {@link XMaterial}.
 * <br>• {@link #getUniqueMaterialString(XMaterial[])} – Gets a comma-separated list of namespaced keys from multiple {@link XMaterial}s.
 * <br>• {@link #convertStringToXMaterial(String)} – Converts a string to an {@link XMaterial}, fallback to Bukkit {@link Material} if needed.
 * <br>• {@link #convertXMaterialToWEBlockType(XMaterial)} – Converts an {@link XMaterial} to a WorldEdit {@link BlockType}.
 * <br>• {@link #createStringFromItemStringList(ArrayList)} – Converts a list of material strings to a comma-separated namespaced material string.
 * <br>• {@link #createStringFromItemStringList(String...)} – Converts an array of material strings to a comma-separated namespaced material string.
 *
 * <br><br><b>Skull Creation Methods</b>:
 * <br>Utility methods to create player and custom skulls:
 * <br>• {@link #createPlayerHead(String, String)}
 * <br>• {@link #createPlayerHead(String, String, List)}
 * <br>• {@link #createPlayerHead(String, String, int, List)}
 *
 * @version 1.3.4
 * @author MineFact, Zoriot
 */
@SuppressWarnings({"deprecation", "unused"})
public class Item {
	public static final Map<String, ItemStack> nonPlayerSkulls = new ConcurrentHashMap<>();

	private ItemStack item;

	private Material material;

	private String displayName;

	private int amount = 1;

	private List<String> lore;

	private boolean hideAttributes;

	private boolean hideEnchantments;

	private final Map<Enchantment, Integer> enchantments = new HashMap<>();

	public Item(Material material) {
		this.material = material;
	}

	public Item(ItemStack item) {
		this.item = item;
	}

	public Item setDisplayName(String name) {
		this.displayName = name;
		return this;
	}

	public Item setAmount(int amount) {
		this.amount = amount;
		return this;
	}

    public Item setLore(List<String> lore) {
		this.lore = lore;
		return this;
	}

	public Item addEnchantment(Enchantment enchantment, int level) {
		this.enchantments.put(enchantment, level);
		return this;
	}

	public Item hideAttributes(boolean hide) {
		this.hideAttributes = hide;
		return this;
	}

	public Item hideEnchantments(boolean enchants) {
		this.hideEnchantments = enchants;
		return this;
	}

	public ItemStack build() {
		ItemStack item;
		if (this.item != null)
			item = this.item.clone();
		else
			item = new ItemStack(this.material);

		item.setAmount(this.amount);

		for (Enchantment en : this.enchantments.keySet())
			item.addUnsafeEnchantment(en, this.enchantments.get(en));

		ItemMeta itemmeta = item.getItemMeta();
		itemmeta.setDisplayName(this.displayName);
		itemmeta.setLore(this.lore);
		if (this.hideAttributes) itemmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		if (this.hideEnchantments) itemmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(itemmeta);
		return item;
	}


	// ==============================
	// SECTION: Item Creation Methods
	// ==============================


	private static @NonNull ItemStack createItem(Material material, String name, int amount, List<String> lore,
												 Map<Enchantment, Integer> enchantments) {
		ItemStack item = new ItemStack(material, amount);
		if (enchantments != null)
			for (Map.Entry<Enchantment, Integer> e : enchantments.entrySet())
				item.addUnsafeEnchantment(e.getKey(), e.getValue());

		ItemMeta meta = item.getItemMeta();
		if (name != null)
			meta.setDisplayName(name);
		if (lore != null)
			meta.setLore(lore);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(meta);
		return item;
	}

	private static @NonNull ItemStack createLeatherArmorItem(Material material, String name, Color color,
															 List<String> lore, Map<Enchantment, Integer> enchantments) {
		ItemStack item = new ItemStack(material);
		if (enchantments != null)
			for (Map.Entry<Enchantment, Integer> e : enchantments.entrySet())
				item.addUnsafeEnchantment(e.getKey(), e.getValue());


		LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
		if (name != null)
			meta.setDisplayName(name);
		if (lore != null)
			meta.setLore(lore);
		meta.setColor(color);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(meta);
		return item;
	}

	@Contract("_ -> new")
	public static @NonNull ItemStack create(Material material) {
		return new ItemStack(material);
	}

	@Contract("_, _ -> new")
	public static @NonNull ItemStack create(Material material, int amount) {
		return new ItemStack(material, amount);
	}

	public static @NonNull ItemStack create(Material material, String name) {
		return createItem(material, name, 1, null, null);
	}

	public static @NonNull ItemStack create(Material material, String name, int amount) {
		return createItem(material, name, amount, null, null);
	}

	public static @NonNull ItemStack create(Material material, String name, List<String> lore) {
		return createItem(material, name, 1, lore, null);
	}

	public static @NonNull ItemStack create(Material material, String name, short durability, List<String> lore) {
		ItemStack item = createItem(material, name, 1, lore, null);
		item.setDurability(durability);
		return item;
	}

	public static @NonNull ItemStack create(Material material, String name, int amount, List<String> lore) {
		return createItem(material, name, amount, lore, null);
	}

	public static @NonNull ItemStack createLeatherArmor(Material material, String name, Color color, List<String> lore) {
		return createLeatherArmorItem(material, name, color, lore, null);
	}

	public static @NonNull ItemStack create(Material material, String name, List<String> lore, Enchantment enchantment1, Integer level1) {
		Map<Enchantment, Integer> enchantments = new HashMap<>();
		enchantments.put(enchantment1, level1);
		return createItem(material, name, 1, lore, enchantments);
	}

	public static @NonNull ItemStack create(Material material, String name, List<String> lore, Enchantment enchantment, Integer level1, Enchantment enchantment2, Integer level2) {
		Map<Enchantment, Integer> enchantments = new HashMap<>();
		enchantments.put(enchantment, level1);
		enchantments.put(enchantment2, level2);
		return createItem(material, name, 1, lore, enchantments);
	}

	public static @NonNull ItemStack create(Material material, String name, List<String> lore, Enchantment enchantment, Integer level1, Enchantment enchantment2, Integer level2, Enchantment enchantment3, Integer level3) {
		Map<Enchantment, Integer> enchantments = new HashMap<>();
		enchantments.put(enchantment, level1);
		enchantments.put(enchantment2, level2);
		enchantments.put(enchantment3, level3);
		return createItem(material, name, 1, lore, enchantments);
	}

	public static @NonNull ItemStack createLeatherArmor(Material material, Color color) {
		LeatherArmorMeta itemMeta = (LeatherArmorMeta) new ItemStack(material).getItemMeta();
		itemMeta.setColor(color);
		itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		ItemStack item = new ItemStack(material);
		item.setItemMeta(itemMeta);
		return item;
	}

	public static @NonNull ItemStack createLeatherArmor(Material material, String name, Color color, List<String> lore, Enchantment enchantment, Integer level1) {
		Map<Enchantment, Integer> enchantments = new HashMap<>();
		enchantments.put(enchantment, level1);
		return createLeatherArmorItem(material, name, color, lore, enchantments);
	}

	public static @NonNull ItemStack createLeatherArmor(Material material, String name, Color color, List<String> lore, Enchantment enchantment, Integer level1, Enchantment enchantment2, Integer level2) {
		Map<Enchantment, Integer> enchantments = new HashMap<>();
		enchantments.put(enchantment, level1);
		enchantments.put(enchantment2, level2);
		return createLeatherArmorItem(material, name, color, lore, enchantments);
	}

	public static @NonNull ItemStack createLeatherArmor(Material material, String name, Color color, List<String> lore, Enchantment enchantment, Integer level1, Enchantment enchantment2, Integer level2, Enchantment enchantment3, Integer level3) {
		Map<Enchantment, Integer> enchantments = new HashMap<>();
		enchantments.put(enchantment, level1);
		enchantments.put(enchantment2, level2);
		enchantments.put(enchantment3, level3);
		return createLeatherArmorItem(material, name, color, lore, enchantments);
	}

	// ==============================
	// SECTION: Skull Creation Methods
	// ==============================

	public static @NonNull ItemStack createPlayerHead(String name, String owner) {
		ItemStack item = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta itemMeta = (SkullMeta) item.getItemMeta();
		itemMeta.setDisplayName(name);
		itemMeta.setOwningPlayer(Bukkit.getOfflinePlayer(owner));
		itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(itemMeta);
		return item;
	}

	public static @NonNull ItemStack createPlayerHead(String name, String owner, List<String> lore) {
		ItemStack item = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta itemMeta = (SkullMeta) item.getItemMeta();
		itemMeta.setDisplayName(name);
		itemMeta.setOwningPlayer(Bukkit.getOfflinePlayer(owner));
		itemMeta.setLore(lore);
		itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(itemMeta);
		return item;
	}

	public static @NonNull ItemStack createPlayerHead(String name, String owner, int amount, List<String> lore) {
		var item = createPlayerHead(name, owner, lore);
		return edit(item, amount);
	}

	// ==============================
	// SECTION: Item Editing Methods
	// ==============================

    @Contract("_, _ -> param1")
	public static @NotNull ItemStack edit(@NotNull ItemStack item, Material material) {
		item.setType(material);
		return item;
	}

    @Contract("_, _ -> param1")
    public static @NotNull ItemStack edit(@NotNull ItemStack item, int amount) {
		item.setAmount(amount);
		return item;
	}

    @Contract("_, _ -> param1")
    public static @NotNull ItemStack edit(@NotNull ItemStack item, String name) {
		ItemMeta itemmeta = item.getItemMeta();
		itemmeta.setDisplayName(name);
		itemmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(itemmeta);
		return item;
	}

    @Contract("_, _, _ -> param1")
    public static @NotNull ItemStack edit(@NotNull ItemStack item, int amount, String name) {
		item.setAmount(amount);
		ItemMeta itemmeta = item.getItemMeta();
		itemmeta.setDisplayName(name);
		itemmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(itemmeta);
		return item;
	}

    @Contract("_, _ -> param1")
    public static @NotNull ItemStack edit(@NotNull ItemStack item, List<String> lore) {
		ItemMeta itemmeta = item.getItemMeta();
		itemmeta.setLore(lore);
		itemmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(itemmeta);
		return item;
	}

    @Contract("_, _, _, _ -> param1")
    public static @NotNull ItemStack edit(@NotNull ItemStack item, int amount, String name, List<String> lore) {
		item.setAmount(amount);
		ItemMeta itemmeta = item.getItemMeta();
		itemmeta.setDisplayName(name);
		itemmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		itemmeta.setLore(lore);
		item.setItemMeta(itemmeta);
		return item;
	}

	// ======================================
	// SECTION: Material Conversion Utilities
	// ======================================

    public static @Nullable ItemStack fromUniqueMaterialString(String materialString) {
		Material material = Material.matchMaterial(materialString);
		if(material != null)
			return XMaterial.matchXMaterial(material).parseItem();


		if(XMaterial.matchXMaterial(materialString).isPresent())
			return XMaterial.matchXMaterial(materialString).get().parseItem();

		return null;
	}

	public static @NonNull String getUniqueMaterialString(@NonNull ItemStack item) {
		return item.getType().name();
	}


	public static String getUniqueMaterialString(XMaterial material) {
		if (material == null)
			return null;

		ItemStack item = material.parseItem();
		if (item == null)
			return null;

		return getUniqueMaterialString(item);
	}

	public static String getUniqueMaterialString(XMaterial[] materials) {
		if (materials == null || materials.length == 0)
			return null;

		StringBuilder s = new StringBuilder(getUniqueMaterialString(materials[0]));

		for (int i = 1; i < materials.length; i++)
			s.append(",").append(getUniqueMaterialString(materials[i]));

		return s.toString();
	}

	public static @org.jspecify.annotations.Nullable XMaterial convertStringToXMaterial(String materialString) {
		XMaterial material;

		if (XMaterial.matchXMaterial(materialString).isPresent())
			material = XMaterial.matchXMaterial(materialString).get();
		else {
			Material mat = Material.matchMaterial(materialString);

			if (mat != null)
				material = XMaterial.matchXMaterial(mat);
			else
				return null;
		}

		return material;
	}

	public static BlockType convertXMaterialToWEBlockType(XMaterial material) {
		String mat = getUniqueMaterialString(material);
		BlockType bt;

		if (mat.contains("minecraft:"))
			bt = BlockTypes.parse(mat);
		else
			bt = BlockTypes.get(mat);

		return bt;
	}

	public static @NonNull String createStringFromItemStringList(@NonNull List<String> items) throws IllegalArgumentException {
		StringBuilder s = new StringBuilder();

		for (int i = 0; i < items.size(); i++)
			if (XMaterial.matchXMaterial(items.get(i)).isPresent()) {
				var curItem = items.get(i);
				var material = XMaterial.matchXMaterial(curItem);
				XMaterial xMaterial = material.get();
				ItemStack item = xMaterial.parseItem();

				if (item == null)
					continue;

				if (i > 0) s.append(",");
				s.append(getUniqueMaterialString(item));
			}
		return s.toString();
	}

	public static @NonNull String createStringFromItemStringList(String... items) throws IllegalArgumentException {
		return createStringFromItemStringList(new ArrayList<>(Arrays.asList(items)));
	}

    public static ItemStack createCustomHeadTextureURL(String url, String name, List<String> lore) {
		byte[] encodedByteData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
		String encodedData = new String(encodedByteData);

		return createCustomHeadBase64(encodedData, name, lore);
	}

    public static @Nullable ItemStack createCustomHeadBase64(String base64, String name, List<String> lore) {
		if (nonPlayerSkulls.containsKey(base64 + name + lore))
			return nonPlayerSkulls.get(base64 + name + lore);

		ItemStack head = XMaterial.PLAYER_HEAD.parseItem();

		if(head == null)
			return null;

        ItemMeta meta = XSkull.of(head).profile(Profileable.detect(base64)).apply().getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(lore);
		head.setItemMeta(meta);

		nonPlayerSkulls.put(base64 + name + lore, head);

		return head;
	}
}
