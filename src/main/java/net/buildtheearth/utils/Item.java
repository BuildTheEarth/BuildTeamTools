package net.buildtheearth.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Item {
	public static HashMap<String, ItemStack> nonPlayerSkulls = new HashMap<>();

	private ItemStack item;
	private Material material;
	private String displayName;
	private int amount = -1;
	private ArrayList<String> lore;
	private boolean hideAttributes;
	private boolean hideEnchantments;

	private HashMap<Enchantment, Integer> enchantments = new HashMap<>();

	public Item() {}

	public Item(Material material) {
		this.material = material;
	}

	public Item(ItemStack item) {
		this.item = item;
	}

	public Item setType(Material material) {
		this.material = material;
		return this;
	}

	public Item setDisplayName(String name) {
		this.displayName = name;
		return this;
	}

	public Item setAmount(int amount) {
		this.amount = amount;
		return this;
	}

	public Item setLore(ArrayList<String> lore) {
		this.lore = lore;
		return this;
	}

	public Item addEnchantment(Enchantment enchantment, int level) {
		this.enchantments.put(enchantment, Integer.valueOf(level));
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
		ItemStack item = new ItemStack(Material.BARRIER);

		if(this.material != null)
			item.setType(material);

		if(this.item != null)
			item = this.item.clone();

		if(this.amount != -1)
			item.setAmount(this.amount);
		else
			item.setAmount(1);

		if(item.getEnchantments().keySet().size() == 0)
		for (Enchantment en : this.enchantments.keySet())
			item.addUnsafeEnchantment(en, ((Integer)this.enchantments.get(en)).intValue());

		ItemMeta itemmeta = item.getItemMeta();

		if(!itemmeta.hasDisplayName())
		itemmeta.setDisplayName(this.displayName);

		if(!itemmeta.hasLore())
		itemmeta.setLore(this.lore);

		if (this.hideAttributes)
			itemmeta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES });
		if (this.hideEnchantments)
			itemmeta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ENCHANTS });
		item.setItemMeta(itemmeta);
		return item;
	}

	public static ItemStack create(Material material) {
		return new ItemStack(material);
	}

	public static ItemStack create(Material material, int amount) {
		ItemStack item = new ItemStack(material, amount);
		return item;
	}

	public static ItemStack create(Material material, String name) {
		ItemStack item = new ItemStack(material, 1);
		ItemMeta itemmeta = item.getItemMeta();
		itemmeta.setDisplayName(name);
		itemmeta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES });
		item.setItemMeta(itemmeta);
		return item;
	}

	public static ItemStack create(Material material, String name, int amount) {
		ItemStack item = new ItemStack(material, amount);
		ItemMeta itemmeta = item.getItemMeta();
		itemmeta.setDisplayName(name);
		itemmeta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES });
		item.setItemMeta(itemmeta);
		return item;
	}

	public static ItemStack create(Material material, String name, ArrayList<String> lore) {
		ItemStack item = new ItemStack(material, 1, (short)0);
		ItemMeta itemmeta = item.getItemMeta();
		itemmeta.setDisplayName(name);
		itemmeta.setLore(lore);
		itemmeta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES });
		item.setItemMeta(itemmeta);
		return item;
	}

	public static ItemStack create(Material material, String name, short durability, ArrayList<String> lore) {
		ItemStack item = new ItemStack(material, 1, durability);
		ItemMeta itemmeta = item.getItemMeta();
		itemmeta.setDisplayName(name);
		itemmeta.setLore(lore);
		itemmeta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES });
		item.setItemMeta(itemmeta);
		return item;
	}

	public static ItemStack create(Material material, String name, int amount, ArrayList<String> lore) {
		ItemStack item = new ItemStack(material, amount);
		ItemMeta itemmeta = item.getItemMeta();
		itemmeta.setDisplayName(name);
		itemmeta.setLore(lore);
		itemmeta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES });
		item.setItemMeta(itemmeta);
		return item;
	}

	public static ItemStack createLeatherArmor(Material material, String name, Color color, ArrayList<String> lore) {
		ItemStack item = new ItemStack(material);
		LeatherArmorMeta itemmeta = (LeatherArmorMeta)item.getItemMeta();
		itemmeta.setDisplayName(name);
		itemmeta.setLore(lore);
		itemmeta.setColor(color);
		itemmeta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES });
		item.setItemMeta((ItemMeta)itemmeta);
		return item;
	}

	public static ItemStack create(Material material, String name, ArrayList<String> lore, Enchantment enchnt1, Integer level1) {
		ItemStack item = new ItemStack(material);
		item.addUnsafeEnchantment(enchnt1, level1.intValue());
		ItemMeta itemmeta = item.getItemMeta();
		itemmeta.setDisplayName(name);
		itemmeta.setLore(lore);
		itemmeta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES });
		item.setItemMeta(itemmeta);
		return item;
	}

	public static ItemStack create(Material material, String name, ArrayList<String> lore, Enchantment enchnt1, Integer level1, Enchantment enchnt2, Integer level2) {
		ItemStack item = new ItemStack(material);
		item.addUnsafeEnchantment(enchnt1, level1.intValue());
		item.addUnsafeEnchantment(enchnt2, level2.intValue());
		ItemMeta itemmeta = item.getItemMeta();
		itemmeta.setDisplayName(name);
		itemmeta.setLore(lore);
		itemmeta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES });
		item.setItemMeta(itemmeta);
		return item;
	}

	public static ItemStack create(Material material, String name, ArrayList<String> lore, Enchantment enchnt1, Integer level1, Enchantment enchnt2, Integer level2, Enchantment enchnt3, Integer level3) {
		ItemStack item = new ItemStack(material);
		item.addUnsafeEnchantment(enchnt1, level1.intValue());
		item.addUnsafeEnchantment(enchnt2, level2.intValue());
		item.addUnsafeEnchantment(enchnt3, level3.intValue());
		ItemMeta itemmeta = item.getItemMeta();
		itemmeta.setDisplayName(name);
		itemmeta.setLore(lore);
		itemmeta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES });
		item.setItemMeta(itemmeta);
		return item;
	}

	public static ItemStack createLeatherArmor(Material material, Color color) {
		ItemStack item = new ItemStack(material);
		LeatherArmorMeta itemmeta = (LeatherArmorMeta)item.getItemMeta();
		itemmeta.setColor(color);
		item.setItemMeta((ItemMeta)itemmeta);
		itemmeta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES });
		return item;
	}

	public static ItemStack createLeatherArmor(Material material, String name, Color color, ArrayList<String> lore, Enchantment enchnt1, Integer level1) {
		ItemStack item = new ItemStack(material);
		item.addUnsafeEnchantment(enchnt1, level1.intValue());
		LeatherArmorMeta itemmeta = (LeatherArmorMeta)item.getItemMeta();
		itemmeta.setDisplayName(name);
		itemmeta.setLore(lore);
		itemmeta.setColor(color);
		itemmeta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES });
		item.setItemMeta((ItemMeta)itemmeta);
		return item;
	}

	public static ItemStack createLeatherArmor(Material material, String name, Color color, ArrayList<String> lore, Enchantment enchnt1, Integer level1, Enchantment enchnt2, Integer level2) {
		ItemStack item = new ItemStack(material);
		item.addUnsafeEnchantment(enchnt1, level1.intValue());
		item.addUnsafeEnchantment(enchnt2, level2.intValue());
		LeatherArmorMeta itemmeta = (LeatherArmorMeta)item.getItemMeta();
		itemmeta.setDisplayName(name);
		itemmeta.setLore(lore);
		itemmeta.setColor(color);
		itemmeta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES });
		item.setItemMeta((ItemMeta)itemmeta);
		return item;
	}

	public static ItemStack createLeatherArmor(Material material, String name, Color color, ArrayList<String> lore, Enchantment enchnt1, Integer level1, Enchantment enchnt2, Integer level2, Enchantment enchnt3, Integer level3) {
		ItemStack item = new ItemStack(material);
		item.addUnsafeEnchantment(enchnt1, level1.intValue());
		item.addUnsafeEnchantment(enchnt2, level2.intValue());
		item.addUnsafeEnchantment(enchnt3, level3.intValue());
		LeatherArmorMeta itemmeta = (LeatherArmorMeta)item.getItemMeta();
		itemmeta.setDisplayName(name);
		itemmeta.setLore(lore);
		itemmeta.setColor(color);
		itemmeta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES });
		item.setItemMeta((ItemMeta)itemmeta);
		return item;
	}

	public static ItemStack createPlayerHead(String name, String owner) {
		ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta itemmeta = (SkullMeta)item.getItemMeta();
		itemmeta.setDisplayName(name);
		itemmeta.setOwner(owner);
		itemmeta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES });
		item.setItemMeta((ItemMeta)itemmeta);
		return item;
	}

	public static ItemStack createPlayerHead(String name, String owner, ArrayList<String> lore) {
		ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta itemmeta = (SkullMeta)item.getItemMeta();
		itemmeta.setDisplayName(name);
		itemmeta.setOwner(owner);
		itemmeta.setLore(lore);
		itemmeta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES });
		item.setItemMeta((ItemMeta)itemmeta);
		return item;
	}

	public static ItemStack createPlayerHead(String name, String owner, int amount, ArrayList<String> lore) {
		ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta itemmeta = (SkullMeta)item.getItemMeta();
		itemmeta.setDisplayName(name);
		itemmeta.setOwner(owner);
		itemmeta.setLore(lore);
		itemmeta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES });
		item.setItemMeta((ItemMeta)itemmeta);
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
		itemmeta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES });
		item.setItemMeta(itemmeta);
		return item;
	}

	public static ItemStack edit(ItemStack item, int amount, String name) {
		item.setAmount(amount);
		ItemMeta itemmeta = item.getItemMeta();
		itemmeta.setDisplayName(name);
		itemmeta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES });
		item.setItemMeta(itemmeta);
		return item;
	}

	public static ItemStack edit(ItemStack item, ArrayList<String> lore) {
		ItemMeta itemmeta = item.getItemMeta();
		itemmeta.setLore(lore);
		itemmeta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES });
		item.setItemMeta(itemmeta);
		return item;
	}

	public static ItemStack edit(ItemStack item, int amount, String name, ArrayList<String> lore) {
		item.setAmount(amount);
		ItemMeta itemmeta = item.getItemMeta();
		itemmeta.setDisplayName(name);
		itemmeta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES });
		itemmeta.setLore(lore);
		item.setItemMeta(itemmeta);
		return item;
	}

	public static ItemStack edit(ItemStack item, int amount, int data) {
		item.setAmount(amount);
		item.getData().setData((byte)data);
		return item;
	}


	public static ItemStack createCustomHeadBase64(String base64, String name, ArrayList<String> lore) {
		ItemStack head = Item.create(Material.SKULL_ITEM, name, (short) 3,  lore);
		SkullMeta meta = (SkullMeta) head.getItemMeta();
		mutateItemMeta(meta, base64);
		meta.setDisplayName(name);
		meta.setLore(lore);
		head.setItemMeta(meta);
		return head;
	}

	public static String getUniqueMaterialString(ItemStack item){
		return item.getTypeId() + ":" + item.getDurability();
	}

	public static ItemStack fromUniqueMaterialString(String s){
		try{
			String[] split = s.split(":");
			return new ItemStack(Integer.parseInt(split[0]), 1, Short.parseShort(split[1]));
		}catch(Exception e){
			return null;
		}
	}


	public static String createStringFromItemList(ArrayList<String> items){
		String s = items.get(0);

		for(int i = 1; i < items.size(); i++)
			s += "," + items.get(i);

		return s;
	}

	private static void mutateItemMeta(SkullMeta meta, String b64) {
		Method metaSetProfileMethod = null;
		Field metaProfileField = null;
		try {
			if (metaSetProfileMethod == null) {
				metaSetProfileMethod = meta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
				metaSetProfileMethod.setAccessible(true);
			}
			metaSetProfileMethod.invoke(meta, makeProfile(b64));
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
			try {
				if (metaProfileField == null) {
					metaProfileField = meta.getClass().getDeclaredField("profile");
					metaProfileField.setAccessible(true);
				}
				metaProfileField.set(meta, makeProfile(b64));

			} catch (NoSuchFieldException | IllegalAccessException ex2) {
				ex2.printStackTrace();
			}
		}
	}

	private static GameProfile makeProfile(String b64) {
		UUID id = new UUID(
				b64.substring(b64.length() - 20).hashCode(),
				b64.substring(b64.length() - 10).hashCode()
		);
		GameProfile profile = new GameProfile(id, "aaaaa");
		profile.getProperties().put("textures", new Property("textures", b64));
		return profile;
	}
}
