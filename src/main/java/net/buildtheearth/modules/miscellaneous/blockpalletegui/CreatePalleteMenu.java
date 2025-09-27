package net.buildtheearth.modules.miscellaneous.blockpalletegui;

import com.cryptomorin.xseries.XMaterial;
import net.buildtheearth.utils.Item;
import net.buildtheearth.utils.menus.AbstractMenu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;

import java.util.*;

public class CreatePalleteMenu extends AbstractMenu {

    private static final int NAME_SLOT        = 11;
    private static final int DESCRIPTION_SLOT = 13;
    private static final int BLOCKS_SLOT      = 15;
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

    private final BlockPalletManager manager;
    private final JavaPlugin plugin;
    private final Player player;
    private final java.util.logging.Logger logger;

    private String name = "Unnamed";
    private String description = "";
    private final List<String> blocks = new ArrayList<>();
    private InputMode inputMode = InputMode.NONE;
    private ChatInputListener chatListener;

    private enum InputMode { NONE, NAME, DESCRIPTION }

    public CreatePalleteMenu(BlockPalletManager manager, Player player, JavaPlugin plugin) {
        super(3, "Create Palette", player);
        this.manager = manager;
        this.plugin = plugin;
        this.player = player;
        this.logger = plugin.getLogger();
    }

    public Player getPlayer() { return player; }

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
        logger.info("Setting menu items for CreatePalleteMenu for player " + player.getName() + ". Blocks: " + blocks);

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
            p.closeInventory();
            inputMode = InputMode.NAME;
            registerChatListener();
            p.sendMessage("§ePlease type the palette name in chat (max " + MAX_NAME_LENGTH + " characters). Type 'cancel' to abort.");
        });

        // Description
        getMenu().getSlot(DESCRIPTION_SLOT).setClickHandler((p, i) -> {
            p.closeInventory();
            inputMode = InputMode.DESCRIPTION;
            registerChatListener();
            p.sendMessage("§ePlease type the palette description in chat (max " + MAX_DESCRIPTION_LENGTH + " characters). Type 'cancel' to abort.");
        });

        // Blocks
        getMenu().getSlot(BLOCKS_SLOT).setClickHandler((p, i) -> {
            p.closeInventory();
            logger.info("Opening ChoosePalleteBlocksMenu from CreatePalleteMenu for player " + p.getName() + ". Current blocks: " + blocks);
            new ChoosePalleteBlocksMenu(manager, p, plugin, blocks, updatedBlocks -> {
                logger.info("Received updated blocks for player " + p.getName() + ": " + updatedBlocks);
                blocks.clear();
                blocks.addAll(updatedBlocks);
                setMenuItemsAsync();
                getMenu().open(p);
            }).open();
        });

        // Back
        getMenu().getSlot(BACK_SLOT).setClickHandler((p, i) -> {
            unregisterChatListener();
            new ChoosePalleteMenu(manager, p, plugin).open();
        });

        // Apply
        getMenu().getSlot(APPLY_SLOT).setClickHandler((p, i) -> {
            if (!p.hasPermission(EDIT_PERMISSION)) {
                p.sendMessage("§cYou do not have permission to create palettes.");
                return;
            }
            unregisterChatListener();
            logger.info("Apply clicked in CreatePalleteMenu by " + p.getName() + ". Name: " + name + ", Blocks: " + blocks);
            if (name.equals("Unnamed")) {
                p.sendMessage("§cPlease set a valid name for the palette.");
                return;
            }
            String paletteKey = UUID.randomUUID().toString();
            manager.createPalette(paletteKey, name, description, new ArrayList<>(blocks));
            Set<String> filters = new HashSet<>(manager.getPlayerFilters(p));
            filters.add(paletteKey);
            manager.updatePlayerFilters(p, filters);
            p.sendMessage("§aPalette created: §f" + name);
            new ChoosePalleteMenu(manager, p, plugin).open();
        });
    }

    private void registerChatListener() {
        if (chatListener != null) {
            HandlerList.unregisterAll(chatListener);
        }
        chatListener = new ChatInputListener();
        plugin.getServer().getPluginManager().registerEvents(chatListener, plugin);
    }

    private void unregisterChatListener() {
        if (chatListener != null) {
            HandlerList.unregisterAll(chatListener);
            chatListener = null;
            inputMode = InputMode.NONE;
        }
    }

    private class ChatInputListener implements Listener {
        @EventHandler
        public void onPlayerChat(AsyncPlayerChatEvent event) {
            if (!event.getPlayer().getUniqueId().equals(player.getUniqueId())) return;

            event.setCancelled(true);
            String message = event.getMessage().trim();

            if (message.equalsIgnoreCase("cancel")) {
                if (inputMode == InputMode.NAME) name = "Unnamed";
                else if (inputMode == InputMode.DESCRIPTION) description = "";
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
        logger.info("Opening CreatePalleteMenu for player " + player.getName() + ". Current blocks: " + blocks);
        setMenuItemsAsync();
        getMenu().open(player);
    }
}
