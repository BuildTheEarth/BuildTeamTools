package net.buildtheearth.buildteamtools.modules.miscellaneous.blockpalettegui;

import com.alpsbte.alpslib.utils.item.Item;
import com.cryptomorin.xseries.XMaterial;
import net.buildtheearth.buildteamtools.utils.menus.AbstractMenu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;

import java.util.*;

public class EditPaletteMenu extends AbstractMenu {

    private static final int NAME_SLOT        = 10;
    private static final int DESCRIPTION_SLOT = 12;
    private static final int BLOCKS_SLOT      = 14;
    private static final int DELETE_SLOT      = 16;
    private static final int BACK_SLOT        = 18;
    private static final int APPLY_SLOT       = 26;

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
    private static final String EDIT_PERMISSION = "btt.bp.edit";

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

    private enum InputMode { NONE, NAME, DESCRIPTION }

    public EditPaletteMenu(BlockPaletteManager manager, Player player, JavaPlugin plugin,
                           String paletteKey, String name, String description, List<String> blocks) {
        super(3, "Edit Palette: " + name, player);
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
        ItemStack filler = Item.create(XMaterial.GRAY_STAINED_GLASS_PANE.parseMaterial(), " ");
        return BinaryMask.builder(getMenu())
                .item(filler)
                .pattern("111111111")
                .pattern("111111111")
                .pattern("111111111")
                .build();
    }

    @Override
    protected void setMenuItemsAsync() {
        logger.info("Setting menu items for EditPalleteMenu for player " + player.getName() + ". Blocks: " + blocks);

        getMenu().getSlot(NAME_SLOT).setItem(
                Item.create(XMaterial.NAME_TAG.parseMaterial(), "§eSet Name",
                        new ArrayList<>(List.of("§7Current: §f" + name)))
        );

        getMenu().getSlot(DESCRIPTION_SLOT).setItem(
                Item.create(XMaterial.WRITABLE_BOOK.parseMaterial(), "§eSet Description",
                        new ArrayList<>(List.of("§7Current: §f" + (description.isEmpty() ? "None" : description))))
        );

        getMenu().getSlot(BLOCKS_SLOT).setItem(
                Item.create(XMaterial.STONE.parseMaterial(), "§eSet Blocks",
                        new ArrayList<>(List.of("§7Selected: §f" + blocks.size())))
        );

        getMenu().getSlot(DELETE_SLOT).setItem(
                Item.create(XMaterial.BARRIER.parseMaterial(), "§cDelete Palette",
                        new ArrayList<>(List.of("§7Click to delete this palette" + (isPredefinedFilter() ? " (Cannot delete predefined filter)" : ""))))
        );

        getMenu().getSlot(BACK_SLOT).setItem(
                Item.createCustomHeadBase64(BACK_HEAD, "§cBack", null)
        );

        getMenu().getSlot(APPLY_SLOT).setItem(
                Item.createCustomHeadBase64(GREEN_CHECK_HEAD, "§aApply", null)
        );
    }

    @Override
    protected void setItemClickEventsAsync() {
        // Name
        getMenu().getSlot(NAME_SLOT).setClickHandler((p, i) -> {
            if (!p.hasPermission(EDIT_PERMISSION)) {
                p.sendMessage("§cYou do not have permission to edit palettes.");
                return;
            }
            if (i.getClickType() != ClickType.LEFT) return;
            if (awaitingInput || inputMode != InputMode.NONE) return;
            awaitingInput = true;

            p.closeInventory();
            inputMode = InputMode.NAME;
            registerChatListener();
            Bukkit.getScheduler().runTask(plugin, () ->
                    p.sendMessage("§ePlease type the new palette name in chat (max " + MAX_NAME_LENGTH + " characters). Type 'cancel' to abort.")
            );
        });

        // Description
        getMenu().getSlot(DESCRIPTION_SLOT).setClickHandler((p, i) -> {
            if (!p.hasPermission(EDIT_PERMISSION)) {
                p.sendMessage("§cYou do not have permission to edit palettes.");
                return;
            }
            if (i.getClickType() != ClickType.LEFT) return;
            if (awaitingInput || inputMode != InputMode.NONE) return;
            awaitingInput = true;

            p.closeInventory();
            inputMode = InputMode.DESCRIPTION;
            registerChatListener();
            Bukkit.getScheduler().runTask(plugin, () ->
                    p.sendMessage("§ePlease type the new palette description in chat (max " + MAX_DESCRIPTION_LENGTH + " characters). Type 'cancel' to abort.")
            );
        });

        // Blocks
        getMenu().getSlot(BLOCKS_SLOT).setClickHandler((p, i) -> {
            if (!p.hasPermission(EDIT_PERMISSION)) {
                p.sendMessage("§cYou do not have permission to edit palettes.");
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
            if (!p.hasPermission(EDIT_PERMISSION)) {
                p.sendMessage("§cYou do not have permission to delete palettes.");
                return;
            }
            if (isPredefinedFilter()) {
                p.sendMessage("§cCannot delete predefined filters. Only custom palettes can be deleted.");
                return;
            }
            unregisterChatListener();
            manager.deletePalette(paletteKey);
            Set<String> filters = new HashSet<>(manager.getPlayerFilters(p));
            filters.remove(paletteKey);
            manager.updatePlayerFilters(p, filters);
            p.sendMessage("§aPalette deleted.");
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
            if (!p.hasPermission(EDIT_PERMISSION)) {
                p.sendMessage("§cYou do not have permission to edit palettes.");
                return;
            }
            unregisterChatListener();
            logger.info("Apply clicked in EditPalleteMenu by " + p.getName() + ". Name: " + name + ", Blocks: " + blocks);
            if (name.isEmpty()) {
                p.sendMessage("§cPlease set a valid name for the palette.");
                return;
            }
            manager.updatePalette(paletteKey, name, description, new ArrayList<>(blocks));
            Set<String> filters = new HashSet<>(manager.getPlayerFilters(p));
            filters.add(paletteKey);
            manager.updatePlayerFilters(p, filters);
            p.sendMessage("§aPalette updated: §f" + name);
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
        public void onPlayerChat(AsyncPlayerChatEvent event) {
            if (!event.getPlayer().getUniqueId().equals(player.getUniqueId())) return;

            // Alleen als we echt in input-modus zijn
            if (inputMode == InputMode.NONE) return;

            event.setCancelled(true);
            String message = event.getMessage().trim();

            if (message.equalsIgnoreCase("cancel")) {
                player.sendMessage("§cInput cancelled.");
                unregisterChatListener();
                Bukkit.getScheduler().runTask(plugin, () -> {
                    setMenuItemsAsync();
                    getMenu().open(player);
                });
                return;
            }

            if (inputMode == InputMode.NAME) {
                if (message.length() > MAX_NAME_LENGTH) {
                    player.sendMessage("§cName is too long! Max " + MAX_NAME_LENGTH + " characters.");
                } else if (message.isEmpty()) {
                    player.sendMessage("§cName cannot be empty!");
                } else {
                    name = message;
                    player.sendMessage("§aPalette name set to: §f" + name);
                    unregisterChatListener();
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        setMenuItemsAsync();
                        getMenu().open(player);
                    });
                }
            } else if (inputMode == InputMode.DESCRIPTION) {
                if (message.length() > MAX_DESCRIPTION_LENGTH) {
                    player.sendMessage("§cDescription is too long! Max " + MAX_DESCRIPTION_LENGTH + " characters.");
                } else {
                    description = message.isEmpty() ? "" : message;
                    player.sendMessage("§aPalette description set to: §f" + (description.isEmpty() ? "None" : description));
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
