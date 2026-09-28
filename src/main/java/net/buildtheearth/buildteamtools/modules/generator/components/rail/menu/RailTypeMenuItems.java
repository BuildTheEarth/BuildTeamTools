package net.buildtheearth.buildteamtools.modules.generator.components.rail.menu;

import com.alpsbte.alpslib.utils.item.Item;
import com.cryptomorin.xseries.XMaterial;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.configuration.RailType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import net.buildtheearth.buildteamtools.modules.network.model.Permissions;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/** Builds rail type icons and their visual selection states. */
final class RailTypeMenuItems {

    private RailTypeMenuItems() {
    }

    static ItemStack createRailTypeItem(RailType railType, Player player) {
        Material icon = railType.getIcon().get();

        if (icon == null)
            icon = Objects.requireNonNull(XMaterial.RAIL.get());

        List<String> lore = createConfigurationLore(railType);

        lore.add(darkGray("Left-Click to select"));
        if (railType.isBuiltIn() && player.hasPermission(Permissions.RAIL_TYPE_CREATE))
            lore.add(darkGray("Right-Click to create an editable copy"));
        if (!railType.isBuiltIn() && player.hasPermission(Permissions.RAIL_TYPE_EDIT))
            lore.add(darkGray("Right-Click to edit"));
        if (player.hasPermission(Permissions.RAIL_TYPE_CREATE))
            lore.add(darkGray("Press your drop key (Q) to create an editable copy"));
        if (!railType.isBuiltIn() && player.hasPermission(Permissions.RAIL_TYPE_DELETE)) {
            lore.add(darkGray("Shift+Right-Click to delete"));
            lore.add(darkGray("Shift+Left-Click to select for bulk deletion"));
        }

        return Item.create(icon, yellow(railType.getDisplayName()), lore);
    }

    private static List<String> createConfigurationLore(RailType railType) {
        List<String> lore = new ArrayList<>();
        lore.add(gray("Rail Block: ") + formatMaterials(railType.getRailBlocks()));
        lore.add(gray("Blocks Below: ") + formatMaterials(railType.getBlocksBelow()));
        addSleeperLore(lore, railType);
        addTrackLore(lore, railType);
        addOverheadLore(lore, railType);
        lore.add(gray("Track Switches: ") + white(railType.isTrackSwitchesEnabled()
                ? RailMenuText.ENABLED
                : RailMenuText.DISABLED));
        return lore;
    }

    private static void addSleeperLore(List<String> lore, RailType railType) {
        if (!railType.hasSleepers()) {
            lore.add(gray("Sleepers: ") + white("None"));
            return;
        }

        lore.add(gray("Sleepers: ")
                + formatMaterials(railType.getSleeperBlocks())
                + gray(" every ")
                + white(String.valueOf(railType.getSleeperSpacing()))
                + gray(" blocks"));
    }

    private static void addTrackLore(List<String> lore, RailType railType) {
        if (railType.getTrackCount() == 1) {
            lore.add(gray("Tracks: ") + white("1"));
            return;
        }

        lore.add(gray("Tracks: ")
                + white(String.valueOf(railType.getTrackCount()))
                + gray(" with spacing ")
                + white(railType.getTrackSpacings().toString()));
    }

    private static void addOverheadLore(List<String> lore, RailType railType) {
        lore.add(gray("Overhead Poles: ") + white(railType.hasOverheadPoles()
                ? RailMenuText.ENABLED
                : RailMenuText.DISABLED));

        if (railType.hasOverheadPoles()) {
            lore.add(gray("Pole Block: ") + formatMaterials(railType.getOverheadPoleBlocks()));
            lore.add(gray("Top Support: ") + formatMaterials(railType.getOverheadSupportBlocks()));
            lore.add(gray("Pole Spacing: ") + white(String.valueOf(railType.getOverheadPoleSpacing())));
        }

        lore.add(gray("Overhead Wires: ") + white(railType.hasOverheadWires()
                ? RailMenuText.ENABLED
                : RailMenuText.DISABLED));
        if (railType.hasOverheadWires())
            lore.add(gray("Wire Blocks: ") + formatMaterials(railType.getOverheadWireBlocks()));
    }

    static ItemStack addSelectionGlow(ItemStack item) {
        ItemStack displayedItem = createGlintCompatibleDisplayItem(item);
        displayedItem.setAmount(1);
        displayedItem.addUnsafeEnchantment(Enchantment.LUCK_OF_THE_SEA, 1);

        ItemMeta meta = displayedItem.getItemMeta();

        if (meta != null) {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            forceEnchantmentGlint(meta);
            displayedItem.setItemMeta(meta);
        }

        return displayedItem;
    }

    /**
     * Vanilla does not render enchantment glint on block-entity item models,
     * even when enchantment_glint_override is true (MC-69683). Use an enchanted
     * book as the selected-state icon while retaining the rail type name, lore
     * and the original icon name.
     */
    private static ItemStack createGlintCompatibleDisplayItem(ItemStack item) {
        if (!usesBlockEntityItemRenderer(item.getType()))
            return item.clone();

        ItemMeta originalMeta = item.getItemMeta();
        Component displayName = originalMeta != null && originalMeta.hasDisplayName()
                ? Objects.requireNonNull(originalMeta.displayName())
                : Component.text("Selected Rail Type", NamedTextColor.YELLOW);
        List<Component> originalLore = originalMeta == null ? null : originalMeta.lore();
        List<Component> lore = originalLore != null
                ? new ArrayList<>(originalLore)
                : new ArrayList<>();
        lore.add(Component.text("Original icon: ", NamedTextColor.GRAY)
                .append(Component.text(formatMaterialName(item.getType()), NamedTextColor.WHITE)));
        lore.add(Component.text("Minecraft cannot render glint on this block-entity icon.", NamedTextColor.DARK_GRAY));

        ItemStack displayedItem = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta displayedMeta = displayedItem.getItemMeta();
        displayedMeta.displayName(displayName);
        displayedMeta.lore(lore);
        displayedItem.setItemMeta(displayedMeta);
        return displayedItem;
    }

    private static boolean usesBlockEntityItemRenderer(Material material) {
        String name = material.name();
        return name.endsWith("_CHEST")
                || name.endsWith("_SHULKER_BOX")
                || name.endsWith("_HEAD")
                || name.endsWith("_SKULL")
                || name.endsWith("_BANNER")
                || name.endsWith("_BED")
                || material == Material.CONDUIT
                || material == Material.DECORATED_POT;
    }

    private static String formatMaterialName(Material material) {
        return formatWords(material.name());
    }

    /** Paper 1.20.5+ exposes an explicit glint override; reflection preserves compatibility with older servers. */
    private static void forceEnchantmentGlint(ItemMeta meta) {
        try {
            ItemMeta.class
                    .getMethod("setEnchantmentGlintOverride", Boolean.class)
                    .invoke(meta, Boolean.TRUE);
        } catch (ReflectiveOperationException ignored) {
            // The hidden unsafe enchantment remains the fallback on older servers.
        }
    }

    static ItemStack addDeletionMarker(ItemStack item) {
        ItemStack displayedItem = addSelectionGlow(item);
        ItemMeta meta = displayedItem.getItemMeta();

        if (meta == null)
            return displayedItem;

        List<Component> existingLore = meta.lore();
        List<Component> lore = existingLore != null
                ? new ArrayList<>(existingLore)
                : new ArrayList<>();
        lore.add(Component.text("Selected for bulk deletion", NamedTextColor.RED));
        meta.lore(lore);
        displayedItem.setItemMeta(meta);
        return displayedItem;
    }

    static String formatMaterials(List<XMaterial> materials) {
        return String.join(gray(", "), materials.stream()
                .map(RailTypeMenuItems::formatMaterial)
                .map(RailTypeMenuItems::white)
                .toList());
    }

    static String formatMaterial(@Nullable XMaterial material) {
        if (material == null)
            return "None";

        return formatWords(material.name());
    }

    private static String formatWords(String value) {
        String[] words = value.toLowerCase(Locale.ROOT).split("_");
        List<String> capitalizedWords = new ArrayList<>();

        for (String word : words)
            capitalizedWords.add(word.isEmpty() ? word : Character.toUpperCase(word.charAt(0)) + word.substring(1));

        return String.join(" ", capitalizedWords);
    }

    private static String darkGray(String text) {
        return RailMenuText.color(NamedTextColor.DARK_GRAY, text);
    }

    private static String gray(String text) {
        return RailMenuText.color(NamedTextColor.GRAY, text);
    }

    private static String white(String text) {
        return RailMenuText.color(NamedTextColor.WHITE, text);
    }

    private static String yellow(String text) {
        return RailMenuText.color(NamedTextColor.YELLOW, text);
    }

}
