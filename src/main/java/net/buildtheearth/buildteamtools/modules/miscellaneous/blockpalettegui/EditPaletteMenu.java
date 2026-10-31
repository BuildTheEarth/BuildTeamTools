package net.buildtheearth.buildteamtools.modules.miscellaneous.blockpalettegui;

import com.alpsbte.alpslib.utils.item.Item;
import com.cryptomorin.xseries.XMaterial;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.buildtheearth.buildteamtools.i18n.MessageKeys;
import net.buildtheearth.buildteamtools.i18n.Messages;
import net.buildtheearth.buildteamtools.modules.network.model.Permissions;
import net.buildtheearth.buildteamtools.utils.ItemComponents;
import net.buildtheearth.buildteamtools.utils.menus.AbstractMenu;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;
import org.jspecify.annotations.NonNull;

import java.util.*;

public class EditPaletteMenu extends AbstractMenu {

    private static final int NAME_SLOT = 10;
    private static final int DESCRIPTION_SLOT = 12;
    private static final int BLOCKS_SLOT = 14;
    private static final int DELETE_SLOT = 16;
    private static final int BACK_SLOT = 18;
    private static final int APPLY_SLOT = 26;

    private static final String BACK_HEAD =
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90"
                    + "ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RjOWU0"
                    + "ZGNmYTQyMjFhMWZhZGMxYjViMmIxMWQ4YmVlYjU3ODc5YWYx"
                    + "YzQyMzYyMTQyYmFlMWVkZDUifX19";

    private static final String GREEN_CHECK_HEAD =
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90"
                    + "ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDMxMmNh"
                    + "NDYzMmRlZjVmZmFmMmViMGQ5ZDdjYzdiNTVhNTBjNGUzOTIw"
                    + "ZDkwMzcyYWFiMTQwNzgxZjVkZmJjNCJ9fX0=";

    private static final int MAX_NAME_LENGTH = 32;
    private static final int MAX_DESCRIPTION_LENGTH = 256;

    private final BlockPaletteManager manager;
    private final JavaPlugin plugin;
    private final Player player;
    private final String paletteKey;
    private String name;
    private String description;
    private final List<String> blocks;
    private InputMode inputMode = InputMode.NONE;
    private ChatInputListener chatListener;
    private final java.util.logging.Logger logger;

    // --- NEW: singleton listener per speler + debounce ---
    private static final Map<UUID, Listener> ACTIVE_CHAT = new HashMap<>();
    private boolean awaitingInput = false;

    private enum InputMode {NONE, NAME, DESCRIPTION}

    public EditPaletteMenu(BlockPaletteManager manager, Player player, @NonNull JavaPlugin plugin,
                           String paletteKey, String name, String description, List<String> blocks) {
        super(3, Messages.component(MessageKeys.BlockPalette.EDIT_TITLE, Component.text(name)), player);
        this.manager = manager;
        this.plugin = plugin;
        this.player = player;
        this.paletteKey = paletteKey;
        this.name = name;
        this.description = description != null ? description : "";
        this.blocks = new ArrayList<>(blocks != null ? blocks : new ArrayList<>());
        this.logger = plugin.getLogger();
    }

    @Override
    protected Mask getMask() {
        ItemStack filler = ItemComponents.of(
                Objects.requireNonNull(XMaterial.GRAY_STAINED_GLASS_PANE.get()),
                Component.text(" "));
        return BinaryMask.builder(getMenu())
                .item(filler)
                .pattern(BinaryMask.FULL_PATTERN)
                .pattern(BinaryMask.FULL_PATTERN)
                .pattern(BinaryMask.FULL_PATTERN)
                .build();
    }

    @Override
    protected void setMenuItemsAsync() {
        logger.info("Setting menu items for EditPalleteMenu for player " + player.getName() + ". Blocks: " + blocks);

        getMenu().getSlot(NAME_SLOT).setItem(ItemComponents.of(
                Objects.requireNonNull(XMaterial.NAME_TAG.get()),
                Messages.render(player, MessageKeys.BlockPalette.SET_NAME),
                Messages.loreComponents(player, MessageKeys.BlockPalette.SET_NAME_LORE, Component.text(name))));

        String descDisplay = description.isEmpty()
                ? Messages.plain(player, MessageKeys.BlockPalette.NONE)
                : description;
        getMenu().getSlot(DESCRIPTION_SLOT).setItem(ItemComponents.of(
                Objects.requireNonNull(XMaterial.WRITABLE_BOOK.get()),
                Messages.render(player, MessageKeys.BlockPalette.SET_DESCRIPTION),
                Messages.loreComponents(player, MessageKeys.BlockPalette.SET_DESCRIPTION_LORE, Component.text(descDisplay))));

        getMenu().getSlot(BLOCKS_SLOT).setItem(ItemComponents.of(
                Objects.requireNonNull(XMaterial.STONE.get()),
                Messages.render(player, MessageKeys.BlockPalette.SET_BLOCKS),
                Messages.loreComponents(player, MessageKeys.BlockPalette.SET_BLOCKS_LORE, Component.text(blocks.size()))));

        getMenu().getSlot(DELETE_SLOT).setItem(ItemComponents.of(
                Objects.requireNonNull(XMaterial.BARRIER.get()),
                Messages.render(player, MessageKeys.BlockPalette.DELETE),
                Messages.loreComponents(player,
                        isPredefinedFilter()
                                ? MessageKeys.BlockPalette.DELETE_LORE_PREDEFINED
                                : MessageKeys.BlockPalette.DELETE_LORE)));

        getMenu().getSlot(BACK_SLOT).setItem(ItemComponents.named(
                Item.createCustomHeadBase64(BACK_HEAD, " ", null),
                Messages.render(player, MessageKeys.Ui.BACK_RED)));

        getMenu().getSlot(APPLY_SLOT).setItem(ItemComponents.named(
                Item.createCustomHeadBase64(GREEN_CHECK_HEAD, " ", null),
                Messages.render(player, MessageKeys.Ui.APPLY)));
    }

    @Override
    protected void setItemClickEventsAsync() {
        // Name
        getMenu().getSlot(NAME_SLOT).setClickHandler((p, i) -> {
            if (!p.hasPermission(Permissions.BLOCK_PALETTE_EDIT)) {
                p.sendMessage(Messages.component(MessageKeys.BlockPalette.NO_PERM_EDIT));
                return;
            }
            if (i.getClickType() != ClickType.LEFT) return;
            if (awaitingInput || inputMode != InputMode.NONE) return;
            awaitingInput = true;

            p.closeInventory();
            inputMode = InputMode.NAME;
            registerChatListener();
            Bukkit.getScheduler().runTask(plugin, () ->
                    p.sendMessage(Messages.component(MessageKeys.BlockPalette.PROMPT_NAME_EDIT, Component.text(MAX_NAME_LENGTH)))
            );
        });

        // Description
        getMenu().getSlot(DESCRIPTION_SLOT).setClickHandler((p, i) -> {
            if (!p.hasPermission(Permissions.BLOCK_PALETTE_EDIT)) {
                p.sendMessage(Messages.component(MessageKeys.BlockPalette.NO_PERM_EDIT));
                return;
            }
            if (i.getClickType() != ClickType.LEFT) return;
            if (awaitingInput || inputMode != InputMode.NONE) return;
            awaitingInput = true;

            p.closeInventory();
            inputMode = InputMode.DESCRIPTION;
            registerChatListener();
            Bukkit.getScheduler().runTask(plugin, () ->
                    p.sendMessage(Messages.component(MessageKeys.BlockPalette.PROMPT_DESCRIPTION_EDIT, Component.text(MAX_DESCRIPTION_LENGTH)))
            );
        });

        // Blocks
        getMenu().getSlot(BLOCKS_SLOT).setClickHandler((p, i) -> {
            if (!p.hasPermission(Permissions.BLOCK_PALETTE_EDIT)) {
                p.sendMessage(Messages.component(MessageKeys.BlockPalette.NO_PERM_EDIT));
                return;
            }
            p.closeInventory();
            logger.info("Opening ChoosePalleteBlocksMenu from EditPalleteMenu for player " + p.getName() + ". Current blocks: " + blocks);
            new ChoosePaletteBlocksMenu(manager, p, plugin, blocks, updatedBlocks -> {
                logger.info("Received updated blocks for player " + p.getName() + ": " + updatedBlocks);
                blocks.clear();
                blocks.addAll(updatedBlocks);
                setMenuItemsAsync();
                getMenu().open(p);
            }).open();
        });

        // Delete
        getMenu().getSlot(DELETE_SLOT).setClickHandler((p, i) -> {
            if (!p.hasPermission(Permissions.BLOCK_PALETTE_EDIT)) {
                p.sendMessage(Messages.component(MessageKeys.BlockPalette.NO_PERM_DELETE));
                return;
            }
            if (isPredefinedFilter()) {
                p.sendMessage(Messages.component(MessageKeys.BlockPalette.CANNOT_DELETE_PREDEFINED));
                return;
            }
            unregisterChatListener();
            manager.deletePalette(paletteKey);
            Set<String> filters = new HashSet<>(manager.getPlayerFilters(p));
            filters.remove(paletteKey);
            manager.updatePlayerFilters(p, filters);
            p.sendMessage(Messages.component(MessageKeys.BlockPalette.DELETED));
            p.closeInventory();
            new ChoosePaletteMenu(manager, p, plugin).open();
        });

        // Back
        getMenu().getSlot(BACK_SLOT).setClickHandler((p, i) -> {
            unregisterChatListener();
            new ChoosePaletteMenu(manager, p, plugin).open();
        });

        // Apply
        getMenu().getSlot(APPLY_SLOT).setClickHandler((p, i) -> {
            if (!p.hasPermission(Permissions.BLOCK_PALETTE_EDIT)) {
                p.sendMessage(Messages.component(MessageKeys.BlockPalette.NO_PERM_EDIT));
                return;
            }
            unregisterChatListener();
            logger.info("Apply clicked in EditPalleteMenu by " + p.getName() + ". Name: " + name + ", Blocks: " + blocks);
            if (name.isEmpty()) {
                p.sendMessage(Messages.component(MessageKeys.BlockPalette.INVALID_NAME));
                return;
            }
            manager.updatePalette(paletteKey, name, description, new ArrayList<>(blocks));
            Set<String> filters = new HashSet<>(manager.getPlayerFilters(p));
            filters.add(paletteKey);
            manager.updatePlayerFilters(p, filters);
            p.sendMessage(Messages.component(MessageKeys.BlockPalette.UPDATED, Component.text(name)));
            new ChoosePaletteMenu(manager, p, plugin).open();
        });
    }

    // --- NEW/REPLACED ---
    private void registerChatListener() {
        Listener prev = ACTIVE_CHAT.remove(player.getUniqueId());
        if (prev != null) {
            HandlerList.unregisterAll(prev);
        }
        chatListener = new ChatInputListener();
        ACTIVE_CHAT.put(player.getUniqueId(), chatListener);
        plugin.getServer().getPluginManager().registerEvents(chatListener, plugin);
    }

    // --- NEW/REPLACED ---
    private void unregisterChatListener() {
        Listener prev = ACTIVE_CHAT.remove(player.getUniqueId());
        if (prev != null) {
            HandlerList.unregisterAll(prev);
        }
        chatListener = null;
        inputMode = InputMode.NONE;
        awaitingInput = false; // reset debounce
    }

    private class ChatInputListener implements Listener {
        @EventHandler
        public void onPlayerChat(@NonNull AsyncChatEvent event) {
            if (!event.getPlayer().getUniqueId().equals(player.getUniqueId())) return;

            if (inputMode == InputMode.NONE) return;

            event.setCancelled(true);
            String message = PlainTextComponentSerializer.plainText().serialize(event.message());

            if (message.equalsIgnoreCase("cancel")) {
                player.sendMessage(Messages.component(MessageKeys.BlockPalette.INPUT_CANCELLED));
                unregisterChatListener();
                Bukkit.getScheduler().runTask(plugin, () -> {
                    setMenuItemsAsync();
                    getMenu().open(player);
                });
                return;
            }

            if (inputMode == InputMode.NAME) {
                if (message.length() > MAX_NAME_LENGTH) {
                    player.sendMessage(Messages.component(MessageKeys.BlockPalette.NAME_TOO_LONG, Component.text(MAX_NAME_LENGTH)));
                } else if (message.isEmpty()) {
                    player.sendMessage(Messages.component(MessageKeys.BlockPalette.NAME_EMPTY));
                } else {
                    name = message;
                    player.sendMessage(Messages.component(MessageKeys.BlockPalette.NAME_SET, Component.text(name)));
                    unregisterChatListener();
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        setMenuItemsAsync();
                        getMenu().open(player);
                    });
                }
            } else if (inputMode == InputMode.DESCRIPTION) {
                if (message.length() > MAX_DESCRIPTION_LENGTH) {
                    player.sendMessage(Messages.component(MessageKeys.BlockPalette.DESCRIPTION_TOO_LONG, Component.text(MAX_DESCRIPTION_LENGTH)));
                } else {
                    description = message.isEmpty() ? "" : message;
                    String descDisplay = description.isEmpty()
                            ? Messages.plain(player, MessageKeys.BlockPalette.NONE)
                            : description;
                    player.sendMessage(Messages.component(MessageKeys.BlockPalette.DESCRIPTION_SET, Component.text(descDisplay)));
                    unregisterChatListener();
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        setMenuItemsAsync();
                        getMenu().open(player);
                    });
                }
            }
        }
    }

    public void open() {
        logger.info("Opening EditPalleteMenu for player " + player.getName() + ". Current blocks: " + blocks);
        setMenuItemsAsync();
        getMenu().open(player);
    }

    private boolean isPredefinedFilter() {
        for (BlockPaletteMenuType type : BlockPaletteMenuType.values()) {
            String typeKey = type.getReadableName().toLowerCase().replace(' ', '_');
            if (typeKey.equals(paletteKey)) {
                return true;
            }
        }
        return false;
    }
}
