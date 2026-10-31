package net.buildtheearth.buildteamtools.utils;

import com.alpsbte.alpslib.utils.item.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Builds inventory items with Adventure names/lore via AlpsLib {@link ItemBuilder}.
 *
 * <p>Prefer this over {@code Item.create(..., String, List&lt;String&gt;)} so menus never need
 * legacy section serialization for display text.
 */
public final class ItemComponents {

    private ItemComponents() {
    }

    public static ItemStack of(Material material, Component name) {
        return new ItemBuilder(material).setName(name).build();
    }

    public static ItemStack of(Material material, Component name, @Nullable List<Component> lore) {
        ItemBuilder builder = new ItemBuilder(material).setName(name);
        if (lore != null) {
            builder.setLore(lore);
        }
        return builder.build();
    }

    public static ItemStack of(Material material, int amount, Component name, @Nullable List<Component> lore) {
        ItemBuilder builder = new ItemBuilder(material, amount).setName(name);
        if (lore != null) {
            builder.setLore(lore);
        }
        return builder.build();
    }

    public static ItemStack displayName(ItemStack item, Component name) {
        return new ItemBuilder(item).setName(name).build();
    }

    public static ItemStack lore(ItemStack item, @Nullable List<Component> lore) {
        ItemBuilder builder = new ItemBuilder(item);
        if (lore == null) {
            return builder.build();
        }
        return builder.setLore(lore).build();
    }

    public static ItemStack named(ItemStack item, Component name) {
        return displayName(item, name);
    }

    public static ItemStack named(ItemStack item, Component name, @Nullable List<Component> lore) {
        ItemBuilder builder = new ItemBuilder(item).setName(name);
        if (lore != null) {
            builder.setLore(lore);
        }
        return builder.build();
    }

    /** Applies italic-off to a list so Paper does not force italic item text. */
    public static List<Component> nonItalic(List<Component> lines) {
        return lines.stream()
                .map(line -> line.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE))
                .toList();
    }
}
