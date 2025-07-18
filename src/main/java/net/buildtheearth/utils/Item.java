package net.buildtheearth.utils;

import com.cryptomorin.xseries.XMaterial;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
 * <br><br><b>Skull Creation Methods</b>:
 * <br>Utility methods to create player and custom skulls:
 * <br>• {@link #createPlayerHead(String, String)}
 * <br>• {@link #createPlayerHead(String, String, List)}
 * <br>• {@link #createPlayerHead(String, String, int, List)}
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
 * @version 1.3.2
 * @author MineFact
 */
@SuppressWarnings({"deprecation", "unused"})
public class Item {
	public static Map<String, ItemStack> nonPlayerSkulls = new ConcurrentHashMap<>();

	private final Material material;

	private String displayName;

	private int amount = 1;

	private List<String> lore;

	private boolean hideAttributes;

	private boolean hideEnchantments;

	private final Map<Enchantment, Integer> enchantments = new HashMap<>();

	public Item(Material material) {
		this.material = material;
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
		ItemStack item = new ItemStack(this.material);
		item.setAmount(this.amount);

		for (Enchantment en : this.enchantments.keySet())
			item.addUnsafeEnchantment(en, this.enchantments.get(en));

		ItemMeta itemmeta = item.getItemMeta();
		itemmeta.setDisplayName(this.displayName);
		itemmeta.setLore(this.lore);
		if (this.hideAttributes) itemmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		if (this.hideEnchantments)	itemmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(itemmeta);
		return item;
	}

	private static ItemStack createItem(Material material, String name, int amount, List<String> lore,
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

	private static ItemStack createLeatherArmorItem(Material material, String name, Color color,
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

	public static ItemStack create(Material material) {
		return new ItemStack(material);
	}

	public static ItemStack create(Material material, int amount) {
		return new ItemStack(material, amount);
	}

	public static ItemStack create(Material material, String name) {
		return createItem(material, name, 1, null, null);
	}

	public static ItemStack create(Material material, String name, int amount) {
		return createItem(material, name, amount, null, null);
	}

	public static ItemStack create(Material material, String name, List<String> lore) {
		return createItem(material, name, 1, lore, null);
	}

	public static ItemStack create(Material material, String name, short durability, List<String> lore) {
		ItemStack item = createItem(material, name, 1, lore, null);
		item.setDurability(durability);
		return item;
	}

	public static ItemStack create(Material material, String name, int amount, List<String> lore) {
		return createItem(material, name, amount, lore, null);
	}

	public static ItemStack createLeatherArmor(Material material, String name, Color color, List<String> lore) {
		return createLeatherArmorItem(material, name, color, lore, null);
	}

	public static ItemStack create(Material material, String name, List<String> lore, Enchantment enchantment1, Integer level1) {
		Map<Enchantment, Integer> enchantments = new HashMap<>();
		enchantments.put(enchantment1, level1);
		return createItem(material, name, 1, lore, enchantments);
	}

	public static ItemStack create(Material material, String name, List<String> lore, Enchantment enchantment, Integer level1, Enchantment enchantment2, Integer level2) {
		Map<Enchantment, Integer> enchantments = new HashMap<>();
		enchantments.put(enchantment, level1);
		enchantments.put(enchantment2, level2);
		return createItem(material, name, 1, lore, enchantments);
	}

	public static ItemStack create(Material material, String name, List<String> lore, Enchantment enchantment, Integer level1, Enchantment enchantment2, Integer level2, Enchantment enchantment3, Integer level3) {
		Map<Enchantment, Integer> enchantments = new HashMap<>();
		enchantments.put(enchantment, level1);
		enchantments.put(enchantment2, level2);
		enchantments.put(enchantment3, level3);
		return createItem(material, name, 1, lore, enchantments);
	}

	public static ItemStack createLeatherArmor(Material material, Color color) {
		LeatherArmorMeta itemMeta = (LeatherArmorMeta) new ItemStack(material).getItemMeta();
		itemMeta.setColor(color);
		itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		ItemStack item = new ItemStack(material);
		item.setItemMeta(itemMeta);
		return item;
	}

	public static ItemStack createLeatherArmor(Material material, String name, Color color, List<String> lore, Enchantment enchantment, Integer level1) {
		Map<Enchantment, Integer> enchantments = new HashMap<>();
		enchantments.put(enchantment, level1);
		return createLeatherArmorItem(material, name, color, lore, enchantments);
	}

	public static ItemStack createLeatherArmor(Material material, String name, Color color, List<String> lore, Enchantment enchantment, Integer level1, Enchantment enchantment2, Integer level2) {
		Map<Enchantment, Integer> enchantments = new HashMap<>();
		enchantments.put(enchantment, level1);
		enchantments.put(enchantment2, level2);
		return createLeatherArmorItem(material, name, color, lore, enchantments);
	}

	public static ItemStack createLeatherArmor(Material material, String name, Color color, List<String> lore, Enchantment enchantment, Integer level1, Enchantment enchantment2, Integer level2, Enchantment enchantment3, Integer level3) {
		Map<Enchantment, Integer> enchantments = new HashMap<>();
		enchantments.put(enchantment, level1);
		enchantments.put(enchantment2, level2);
		enchantments.put(enchantment3, level3);
		return createLeatherArmorItem(material, name, color, lore, enchantments);
	}

	public static ItemStack createPlayerHead(String name, String owner) {
		ItemStack item = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta itemMeta = (SkullMeta)item.getItemMeta();
		itemMeta.setDisplayName(name);
		itemMeta.setOwningPlayer(Bukkit.getOfflinePlayer(owner));
		itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(itemMeta);
		return item;
	}

	public static ItemStack createPlayerHead(String name, String owner, List<String> lore) {
		ItemStack item = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta itemMeta = (SkullMeta)item.getItemMeta();
		itemMeta.setDisplayName(name);
		itemMeta.setOwningPlayer(Bukkit.getOfflinePlayer(owner));
		itemMeta.setLore(lore);
		itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(itemMeta);
		return item;
	}

	public static ItemStack createPlayerHead(String name, String owner, int amount, List<String> lore) {
		ItemStack item = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta itemMeta = (SkullMeta)item.getItemMeta();
		itemMeta.setDisplayName(name);
		itemMeta.setOwningPlayer(Bukkit.getOfflinePlayer(owner));
		itemMeta.setLore(lore);
		itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(itemMeta);
		return item;
	}

	public static ItemStack edit(ItemStack item, Material material) {
		item.setType(material);
		return item;
	}

	public static ItemStack edit(ItemStack item, int amount) {
		item.setAmount(amount);
		return item;
	}

	public static ItemStack edit(ItemStack item, String name) {
		ItemMeta itemmeta = item.getItemMeta();
		itemmeta.setDisplayName(name);
		itemmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(itemmeta);
		return item;
	}

	public static ItemStack edit(ItemStack item, int amount, String name) {
		item.setAmount(amount);
		ItemMeta itemmeta = item.getItemMeta();
		itemmeta.setDisplayName(name);
		itemmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(itemmeta);
		return item;
	}

	public static ItemStack edit(ItemStack item, List<String> lore) {
		ItemMeta itemmeta = item.getItemMeta();
		itemmeta.setLore(lore);
		itemmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(itemmeta);
		return item;
	}

	public static ItemStack edit(ItemStack item, int amount, String name, List<String> lore) {
		item.setAmount(amount);
		ItemMeta itemmeta = item.getItemMeta();
		itemmeta.setDisplayName(name);
		itemmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		itemmeta.setLore(lore);
		item.setItemMeta(itemmeta);
		return item;
	}

	/*
	public static ItemStack createNonPlayerSkull(String url, String name, List<String> lore) {
		StringBuilder loreString = new StringBuilder();
		if(lore != null)
			for (String s : lore)
				loreString.append(s);
		try {
			if (nonPlayerSkulls.containsKey(url + name + loreString))
				return nonPlayerSkulls.get(url + name + loreString);
			byte[] encodedByteData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
			String encodedData = new String(encodedByteData);
			HeadDatabaseAPI api = new HeadDatabaseAPI();
			if (api.getItemHead(url) == null)
				api.addHead(CategoryEnum.CUSTOM, url, encodedData);
			ItemStack item = api.getItemHead(url);
			SkullMeta im = (SkullMeta)item.getItemMeta();
			im.setDisplayName(name);
			im.setLore(lore);
			item.setItemMeta(im);
			nonPlayerSkulls.remove(url + name + loreString);
			nonPlayerSkulls.put(url + name + loreString, item);
			return item;
		} catch (NoClassDefFoundError ex) {
			return create(Material.PLAYER_HEAD, name, lore);
		}
	}

	public static ItemStack createCustomHead(String id, String name, List<String> lore) {
		StringBuilder loreString = new StringBuilder();
		if(lore != null)
			for (String s : lore)
				loreString.append(s);
		try {
			if (nonPlayerSkulls.containsKey(id + name + loreString))
				return nonPlayerSkulls.get(id + name + loreString);
			HeadDatabaseAPI api = new HeadDatabaseAPI();
			if (api.getItemHead(id) == null)
				return null;
			ItemStack item = api.getItemHead(id);
			SkullMeta im = (SkullMeta)item.getItemMeta();
			im.setDisplayName(name);
			im.setLore(lore);
			item.setItemMeta(im);
			nonPlayerSkulls.remove(id + name + loreString);
			nonPlayerSkulls.put(id + name + loreString, item);
			return item;
		} catch (NoClassDefFoundError ex) {
			return create(Material.PLAYER_HEAD, name, lore);
		}
	}*/

	public static ItemStack createCustomHeadTextureURL(String url, String name, List<String> lore) {
		byte[] encodedByteData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
		String encodedData = new String(encodedByteData);

		return createCustomHeadBase64(encodedData, name, lore);
	}

	public static ItemStack createCustomHeadBase64(String base64, String name, List<String> lore) {
		if (nonPlayerSkulls.containsKey(base64 + name + lore))
			return nonPlayerSkulls.get(base64 + name + lore);

		ItemStack head = XMaterial.PLAYER_HEAD.parseItem();

		if(head == null)
			return null;

		SkullMeta meta = (SkullMeta) head.getItemMeta();
		mutateItemMeta(meta, base64);
		meta.setDisplayName(name);
		meta.setLore(lore);
		head.setItemMeta(meta);

		nonPlayerSkulls.put(base64 + name + lore, head);

		return head;
	}

	private static void mutateItemMeta(SkullMeta meta, String b64) {
		GameProfile profile = makeProfile(b64);

		// Try Paper API (Minecraft Version 1.20+)
		try {
			Method setPlayerProfile = SkullMeta.class.getMethod("setPlayerProfile", com.destroystokyo.paper.profile.PlayerProfile.class);
			com.destroystokyo.paper.profile.PlayerProfile paperProfile = Bukkit.createProfile(profile.getId(), profile.getName());
			paperProfile.getProperties().add(new com.destroystokyo.paper.profile.ProfileProperty("textures", b64));
			setPlayerProfile.invoke(meta, paperProfile);
			return;
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {}

		// Try setProfile(GameProfile) (Minecraft Version 1.15 – 1.19.4)
		try {
			Method metaSetProfileMethod = meta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
			metaSetProfileMethod.setAccessible(true);
			metaSetProfileMethod.invoke(meta, profile);
			return;
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {}

		// Try direct profile field (Minecraft Version 1.12 – 1.14)
		try {
			Field metaProfileField = meta.getClass().getDeclaredField("profile");
			metaProfileField.setAccessible(true);
			metaProfileField.set(meta, profile);
		} catch (NoSuchFieldException | IllegalAccessException ignored) {
			System.err.println("Failed to set custom skull texture: unsupported server version or method change.");
		}
	}

	private static GameProfile makeProfile(String b64) {
		UUID id = new UUID(
				b64.substring(b64.length() - 20).hashCode(),
				b64.substring(b64.length() - 10).hashCode()
		);
		GameProfile profile = new GameProfile(id, "bte");
		profile.getProperties().put("textures", new Property("textures", b64));
		return profile;
	}
}
