package net.buildtheearth.utils;

import com.cryptomorin.xseries.XMaterial;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import net.buildtheearth.modules.common.CommonModule;
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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Item {
    public static final Map<String, ItemStack> nonPlayerSkulls = new HashMap<>();

	private ItemStack item;
	private Material material;
	private String displayName;
	private int amount = -1;
    private List<String> lore;
	private boolean hideAttributes;
	private boolean hideEnchantments;
	private final List<String> canDestroyItems = new ArrayList<>();
	private final List<String> canPlaceItems = new ArrayList<>();

	private final HashMap<Enchantment, Integer> enchantments = new HashMap<>();

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

	public Item addCanDestroyItem(String itemName){
		this.canDestroyItems.add(itemName);
		return this;
	}

	public Item addCanPlaceItem(String itemName){
		this.canPlaceItems.add(itemName);
		return this;
	}

	public ItemStack build() {
		ItemStack item = XMaterial.BARRIER.parseItem();

		if(this.material != null)
			item.setType(material);

		if(this.item != null)
			item = this.item.clone();

		if(this.amount != -1)
			item.setAmount(this.amount);
		else
			item.setAmount(1);

        if (item.getEnchantments().isEmpty())
            for (Map.Entry<Enchantment, Integer> en : this.enchantments.entrySet())
                item.addUnsafeEnchantment(en.getKey(), en.getValue());



		ItemMeta itemmeta = item.getItemMeta();

		if(this.displayName != null)
			itemmeta.setDisplayName(this.displayName);

		if(this.lore != null)
			itemmeta.setLore(this.lore);

		if (this.hideAttributes)
			itemmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		if (this.hideEnchantments)
			itemmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

		/*if(!canDestroyItems.isEmpty()){
			// TODO Marked for removal in 1.20.6, not fixed yet.
			//Set<Namespaced> nameSpacedKeySet = new HashSet<>();
			//for(String itemName : canDestroyItems)
			//	nameSpacedKeySet.add(NamespacedKey.minecraft(itemName));
			//itemmeta.setDestroyableKeys(nameSpacedKeySet);
		}

		if(!canPlaceItems.isEmpty()){
			// TODO Marked for removal in 1.20.6, not fixed yet.
			//Set<Namespaced> nameSpacedKeySet = new HashSet<>();
			//for(String itemName : canPlaceItems)
			//	nameSpacedKeySet.add(NamespacedKey.minecraft(itemName));
			//itemmeta.setPlaceableKeys(nameSpacedKeySet);
		}*/

		item.setItemMeta(itemmeta);
		return item;
	}

	public static ItemStack create(Material material) {
		return new ItemStack(material);
	}

	public static ItemStack create(Material material, int amount) {
		return new ItemStack(material, amount);
	}

	public static ItemStack create(Material material, String name) {
		ItemStack item = new ItemStack(material, 1);
        return edit(item, name);
	}

	public static ItemStack create(Material material, String name, int amount) {
		ItemStack item = new ItemStack(material, amount);
		ItemMeta itemmeta = item.getItemMeta();
		itemmeta.setDisplayName(name);
		itemmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(itemmeta);
		return item;
	}

    public static @NotNull ItemStack create(Material material, String name, List<String> lore) {
		ItemStack item = new ItemStack(material, 1, (short)0);
		ItemMeta itemmeta = item.getItemMeta();
		itemmeta.setDisplayName(name);
		itemmeta.setLore(lore);
		itemmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(itemmeta);
		return item;
	}

    public static @NotNull ItemStack create(Material material, String name, short durability, List<String> lore) {
		ItemStack item = new ItemStack(material, 1, durability);
		ItemMeta itemmeta = item.getItemMeta();
		itemmeta.setDisplayName(name);
		itemmeta.setLore(lore);
		itemmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(itemmeta);
		return item;
	}

    public static @NotNull ItemStack create(Material material, String name, int amount, List<String> lore) {
		ItemStack item = new ItemStack(material, amount);
		ItemMeta itemmeta = item.getItemMeta();
		itemmeta.setDisplayName(name);
		itemmeta.setLore(lore);
		itemmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(itemmeta);
		return item;
	}

    public static @NotNull ItemStack createLeatherArmor(Material material, String name, Color color, List<String> lore) {
		ItemStack item = new ItemStack(material);
		LeatherArmorMeta itemmeta = (LeatherArmorMeta)item.getItemMeta();
		itemmeta.setDisplayName(name);
		itemmeta.setLore(lore);
		itemmeta.setColor(color);
		itemmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(itemmeta);
		return item;
	}

    public static @NotNull ItemStack create(Material material, String name, List<String> lore, Enchantment enchnt1, Integer level1) {
		ItemStack item = new ItemStack(material);
		item.addUnsafeEnchantment(enchnt1, level1);
		ItemMeta itemmeta = item.getItemMeta();
		itemmeta.setDisplayName(name);
		itemmeta.setLore(lore);
		itemmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(itemmeta);
		return item;
	}

    public static @NotNull ItemStack create(Material material, String name, List<String> lore, Enchantment enchnt1, Integer level1, Enchantment enchnt2, Integer level2) {
		ItemStack item = new ItemStack(material);
		item.addUnsafeEnchantment(enchnt1, level1);
		item.addUnsafeEnchantment(enchnt2, level2);
		ItemMeta itemmeta = item.getItemMeta();
		itemmeta.setDisplayName(name);
		itemmeta.setLore(lore);
		itemmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(itemmeta);
		return item;
	}

    public static @NotNull ItemStack create(Material material, String name, List<String> lore, Enchantment enchnt1, Integer level1, Enchantment enchnt2, Integer level2, Enchantment enchnt3, Integer level3) {
		ItemStack item = new ItemStack(material);
		item.addUnsafeEnchantment(enchnt1, level1);
		item.addUnsafeEnchantment(enchnt2, level2);
		item.addUnsafeEnchantment(enchnt3, level3);
		ItemMeta itemmeta = item.getItemMeta();
		itemmeta.setDisplayName(name);
		itemmeta.setLore(lore);
		itemmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(itemmeta);
		return item;
	}

    public static @NotNull ItemStack createLeatherArmor(Material material, Color color) {
		ItemStack item = new ItemStack(material);
		LeatherArmorMeta itemmeta = (LeatherArmorMeta)item.getItemMeta();
		itemmeta.setColor(color);
		item.setItemMeta(itemmeta);
		itemmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		return item;
	}

    public static @NotNull ItemStack createLeatherArmor(Material material, String name, Color color, List<String> lore, Enchantment enchnt1, Integer level1) {
		ItemStack item = new ItemStack(material);
		item.addUnsafeEnchantment(enchnt1, level1);
		LeatherArmorMeta itemmeta = (LeatherArmorMeta)item.getItemMeta();
		itemmeta.setDisplayName(name);
		itemmeta.setLore(lore);
		itemmeta.setColor(color);
		itemmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(itemmeta);
		return item;
	}

    public static @NotNull ItemStack createLeatherArmor(Material material, String name, Color color, List<String> lore, Enchantment enchnt1, Integer level1, Enchantment enchnt2, Integer level2) {
		ItemStack item = new ItemStack(material);
		item.addUnsafeEnchantment(enchnt1, level1);
		item.addUnsafeEnchantment(enchnt2, level2);
		LeatherArmorMeta itemmeta = (LeatherArmorMeta)item.getItemMeta();
		itemmeta.setDisplayName(name);
		itemmeta.setLore(lore);
		itemmeta.setColor(color);
		itemmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(itemmeta);
		return item;
	}

    public static @NotNull ItemStack createLeatherArmor(Material material, String name, Color color, List<String> lore, Enchantment enchnt1, Integer level1, Enchantment enchnt2, Integer level2, Enchantment enchnt3, Integer level3) {
		ItemStack item = new ItemStack(material);
		item.addUnsafeEnchantment(enchnt1, level1);
		item.addUnsafeEnchantment(enchnt2, level2);
		item.addUnsafeEnchantment(enchnt3, level3);
		LeatherArmorMeta itemmeta = (LeatherArmorMeta)item.getItemMeta();
		itemmeta.setDisplayName(name);
		itemmeta.setLore(lore);
		itemmeta.setColor(color);
		itemmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(itemmeta);
		return item;
	}

    public static @Nullable ItemStack createPlayerHead(String name, String owner) {
		ItemStack item = XMaterial.PLAYER_HEAD.parseItem();

		if(item == null)
			return null;

		SkullMeta itemmeta = (SkullMeta)item.getItemMeta();
		itemmeta.setDisplayName(name);
		itemmeta.setOwner(owner);
		itemmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(itemmeta);
		return item;
	}

    public static @Nullable ItemStack createPlayerHead(String name, String owner, List<String> lore) {
		ItemStack item = XMaterial.PLAYER_HEAD.parseItem();

		if(item == null)
			return null;

		SkullMeta itemmeta = (SkullMeta)item.getItemMeta();
		itemmeta.setDisplayName(name);
		itemmeta.setOwner(owner);
		itemmeta.setLore(lore);
		itemmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(itemmeta);
		return item;
	}

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

    public static @Nullable ItemStack fromUniqueMaterialString(String materialString) {
		Material material = Material.matchMaterial(materialString);
		if(material != null)
			return XMaterial.matchXMaterial(material).parseItem();


		if(XMaterial.matchXMaterial(materialString).isPresent())
			return XMaterial.matchXMaterial(materialString).get().parseItem();

		return null;
	}

    public static @NotNull String getUniqueMaterialString(ItemStack item) {
		if(CommonModule.getInstance().getVersionComponent().is_1_12())
			return XMaterial.matchXMaterial(item).getId() + ":" + XMaterial.matchXMaterial(item).getData();
		else
			return item.getType().getKey().asString();

	}

    public static @NotNull String getUniqueMaterialString(@NotNull XMaterial material) {
		return getUniqueMaterialString(material.parseItem());
	}

	public static String getUniqueMaterialString(XMaterial[] materials) {
		if(materials == null || materials.length == 0)
			return null;

		StringBuilder s = new StringBuilder(getUniqueMaterialString(materials[0]));

		for (int i = 1; i < materials.length; i++)
			s.append(",").append(getUniqueMaterialString(materials[i]));

		return s.toString();
	}

    public static @Nullable XMaterial convertStringToXMaterial(String materialString) {
		XMaterial material;

		if(XMaterial.matchXMaterial(materialString).isPresent())
			material = XMaterial.matchXMaterial(materialString).get();
		else {
			Material mat = Material.matchMaterial(materialString);

			if(mat != null)
				material = XMaterial.matchXMaterial(mat);
			else
				return null;
		}

		return material;
	}

	public static BlockType convertXMaterialToBlockType(XMaterial material) {
		String mat = getUniqueMaterialString(material);
		BlockType bt;

		if(mat.contains("minecraft:"))
			bt = BlockTypes.parse(mat);
		else
			bt = BlockTypes.get(mat);

		return bt;
	}

    public static @NotNull String createStringFromItemList(@NotNull List<String> items) throws IllegalArgumentException {
        StringBuilder s = new StringBuilder(items.getFirst());

		for (int i = 1; i < items.size(); i++)
			if(XMaterial.matchXMaterial(items.get(i)).isPresent()) {
				XMaterial xMaterial = XMaterial.matchXMaterial(items.get(i)).get();

				s.append(",").append(getUniqueMaterialString(xMaterial.parseItem()));
			}

		return s.toString();
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


    private static @NotNull GameProfile makeProfile(@NotNull String b64) {
		UUID id = new UUID(
				b64.substring(b64.length() - 20).hashCode(),
				b64.substring(b64.length() - 10).hashCode()
		);
		GameProfile profile = new GameProfile(id, "bte");
		profile.getProperties().put("textures", new Property("textures", b64));
		return profile;
	}
}
