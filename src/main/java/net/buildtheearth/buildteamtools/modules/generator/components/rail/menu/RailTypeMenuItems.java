package net.buildtheearth.buildteamtools.modules.generator.components.rail.menu;

import com.cryptomorin.xseries.XMaterial;
import net.buildtheearth.buildteamtools.utils.ItemComponents;
import net.buildtheearth.buildteamtools.i18n.MessageKeys;
import net.buildtheearth.buildteamtools.i18n.Messages;
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

        List<Component> lore = createConfigurationLore(railType, player);

        lore.add(Messages.render(player, MessageKeys.Generator.Rail.Menu.LORE_LEFT_SELECT));
        if (railType.isBuiltIn() && player.hasPermission(Permissions.RAIL_TYPE_CREATE))
            lore.add(Messages.render(player, MessageKeys.Generator.Rail.Menu.LORE_RIGHT_COPY));
        if (!railType.isBuiltIn() && player.hasPermission(Permissions.RAIL_TYPE_EDIT))
            lore.add(Messages.render(player, MessageKeys.Generator.Rail.Menu.LORE_RIGHT_EDIT));
        if (player.hasPermission(Permissions.RAIL_TYPE_CREATE))
            lore.add(Messages.render(player, MessageKeys.Generator.Rail.Menu.LORE_DROP_COPY));
        if (!railType.isBuiltIn() && player.hasPermission(Permissions.RAIL_TYPE_DELETE)) {
            lore.add(Messages.render(player, MessageKeys.Generator.Rail.Menu.LORE_SHIFT_RIGHT_DELETE));
            lore.add(Messages.render(player, MessageKeys.Generator.Rail.Menu.LORE_SHIFT_LEFT_BULK));
        }

        return ItemComponents.of(icon, yellow(railType.getDisplayName()), lore);
    }

    private static List<Component> createConfigurationLore(RailType railType, Player player) {
        List<Component> lore = new ArrayList<>();
        String enabled = Messages.plain(player, MessageKeys.Generator.Rail.Menu.ENABLED);
        String disabled = Messages.plain(player, MessageKeys.Generator.Rail.Menu.DISABLED);
        lore.add(Messages.render(player, MessageKeys.Generator.Rail.Menu.LORE_RAIL_BLOCK, Component.text(formatMaterialsPlain(railType.getRailBlocks()))));
        lore.add(Messages.render(player, MessageKeys.Generator.Rail.Menu.LORE_BLOCKS_BELOW, Component.text(formatMaterialsPlain(railType.getBlocksBelow()))));
        addSleeperLore(lore, railType, player);
        addTrackLore(lore, railType, player);
        addOverheadLore(lore, railType, player, enabled, disabled);
        lore.add(Messages.render(player, MessageKeys.Generator.Rail.Menu.LORE_TRACK_SWITCHES,
                Component.text(railType.isTrackSwitchesEnabled() ? enabled : disabled)));
        return lore;
    }

    private static void addSleeperLore(List<Component> lore, RailType railType, Player player) {
        if (!railType.hasSleepers()) {
            lore.add(Messages.render(player, MessageKeys.Generator.Rail.Menu.LORE_SLEEPERS_NONE));
            return;
        }

        lore.add(Messages.render(player, MessageKeys.Generator.Rail.Menu.LORE_SLEEPERS,
                Component.text(formatMaterialsPlain(railType.getSleeperBlocks())),
                Component.text(String.valueOf(railType.getSleeperSpacing()))));
    }

    private static void addTrackLore(List<Component> lore, RailType railType, Player player) {
        if (railType.getTrackCount() == 1) {
            lore.add(Messages.render(player, MessageKeys.Generator.Rail.Menu.LORE_TRACKS_ONE));
            return;
        }

        lore.add(Messages.render(player, MessageKeys.Generator.Rail.Menu.LORE_TRACKS,
                Component.text(String.valueOf(railType.getTrackCount())),
                Component.text(railType.getTrackSpacings().toString())));
    }

    private static void addOverheadLore(List<Component> lore, RailType railType, Player player, String enabled, String disabled) {
        lore.add(Messages.render(player, MessageKeys.Generator.Rail.Menu.LORE_OVERHEAD_POLES,
                Component.text(railType.hasOverheadPoles() ? enabled : disabled)));

        if (railType.hasOverheadPoles()) {
            lore.add(Messages.render(player, MessageKeys.Generator.Rail.Menu.LORE_POLE_BLOCK, Component.text(formatMaterialsPlain(railType.getOverheadPoleBlocks()))));
            lore.add(Messages.render(player, MessageKeys.Generator.Rail.Menu.LORE_TOP_SUPPORT, Component.text(formatMaterialsPlain(railType.getOverheadSupportBlocks()))));
            lore.add(Messages.render(player, MessageKeys.Generator.Rail.Menu.LORE_POLE_SPACING, Component.text(String.valueOf(railType.getOverheadPoleSpacing()))));
        }

        lore.add(Messages.render(player, MessageKeys.Generator.Rail.Menu.LORE_OVERHEAD_WIRES,
                Component.text(railType.hasOverheadWires() ? enabled : disabled)));
        if (railType.hasOverheadWires())
            lore.add(Messages.render(player, MessageKeys.Generator.Rail.Menu.LORE_WIRE_BLOCKS, Component.text(formatMaterialsPlain(railType.getOverheadWireBlocks()))));
    }

    private static String formatMaterialsPlain(List<XMaterial> materials) {
        return String.join(", ", materials.stream().map(RailTypeMenuItems::formatMaterial).toList());
    }

    static ItemStack addSelectionGlow(ItemStack item, Player player) {
        ItemStack displayedItem = createGlintCompatibleDisplayItem(item, player);
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
    private static ItemStack createGlintCompatibleDisplayItem(ItemStack item, Player player) {
        if (!usesBlockEntityItemRenderer(item.getType()))
            return item.clone();

        ItemMeta originalMeta = item.getItemMeta();
        Component displayName = originalMeta != null && originalMeta.hasDisplayName()
                ? Objects.requireNonNull(originalMeta.displayName())
                : Messages.render(player, MessageKeys.Generator.Rail.Menu.SELECTED_FALLBACK_NAME);
        List<Component> originalLore = originalMeta == null ? null : originalMeta.lore();
        List<Component> lore = originalLore != null
                ? new ArrayList<>(originalLore)
                : new ArrayList<>();
        lore.add(Messages.render(player, MessageKeys.Generator.Rail.Menu.ORIGINAL_ICON,
                Component.text(formatMaterialName(item.getType()))));
        lore.add(Messages.render(player, MessageKeys.Generator.Rail.Menu.GLINT_HINT));

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

    static ItemStack addDeletionMarker(ItemStack item, Player player) {
        ItemStack displayedItem = addSelectionGlow(item, player);
        ItemMeta meta = displayedItem.getItemMeta();

        if (meta == null)
            return displayedItem;

        List<Component> existingLore = meta.lore();
        List<Component> lore = existingLore != null
                ? new ArrayList<>(existingLore)
                : new ArrayList<>();
        lore.add(Messages.render(player, MessageKeys.Generator.Rail.Menu.SELECTED_FOR_BULK));
        meta.lore(lore);
        displayedItem.setItemMeta(meta);
        return displayedItem;
    }

    static Component formatMaterialsComponent(List<XMaterial> materials) {
        Component result = Component.empty();
        boolean first = true;
        for (XMaterial material : materials) {
            if (!first)
                result = result.append(Component.text(", ", NamedTextColor.GRAY));
            result = result.append(Component.text(formatMaterial(material), NamedTextColor.WHITE));
            first = false;
        }
        return result;
    }

    static String formatMaterial(@Nullable XMaterial material) {
        if (material == null)
            return "None"; // display data; localized via LORE_SLEEPERS_NONE when unused

        return formatWords(material.name());
    }

    private static String formatWords(String value) {
        String[] words = value.toLowerCase(Locale.ROOT).split("_");
        List<String> capitalizedWords = new ArrayList<>();

        for (String word : words)
            capitalizedWords.add(word.isEmpty() ? word : Character.toUpperCase(word.charAt(0)) + word.substring(1));

        return String.join(" ", capitalizedWords);
    }

    private static Component yellow(String text) {
        return RailMenuText.colored(NamedTextColor.YELLOW, text);
    }

}
