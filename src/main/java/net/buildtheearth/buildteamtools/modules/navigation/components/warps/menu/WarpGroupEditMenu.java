package net.buildtheearth.buildteamtools.modules.navigation.components.warps.menu;

import com.alpsbte.alpslib.utils.AlpsUtils;
import com.cryptomorin.xseries.XMaterial;
import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.i18n.MessageKeys;
import net.buildtheearth.buildteamtools.i18n.Messages;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.model.WarpGroup;
import net.buildtheearth.buildteamtools.modules.network.NetworkModule;
import net.buildtheearth.buildteamtools.modules.network.model.BuildTeam;
import net.buildtheearth.buildteamtools.modules.network.model.Permissions;
import net.buildtheearth.buildteamtools.utils.ItemComponents;
import net.buildtheearth.buildteamtools.utils.MenuItems;
import net.buildtheearth.buildteamtools.utils.heads.HeadFactory;
import net.buildtheearth.buildteamtools.utils.heads.HeadTexture;
import net.buildtheearth.buildteamtools.utils.heads.LetterType;
import net.buildtheearth.buildteamtools.utils.menus.AbstractMenu;
import net.buildtheearth.buildteamtools.utils.menus.BookMenu;
import net.kyori.adventure.text.Component;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;

import java.util.*;

import static net.buildtheearth.buildteamtools.modules.navigation.components.warps.menu.WarpEditMenu.CONFIRM_SLOT;

public class WarpGroupEditMenu extends AbstractMenu {

    private static final int WARP_GROUP = 4;
    private static final int NAME_SLOT = 18;
    private static final int DESCRIPTION_SLOT = 20;

    private static final int SLOT_SLOT = 22;

    private static final int MATERIAL_SLOT = 24;

    private static final int DELETE_SLOT = 26;


    private final WarpGroup warpGroup;
    private final boolean alreadyExists;

    /**
     * In this menu the player can update a warp.
     * This can be used for example to change the name of a warp in the {@link WarpMenu}.
     *
     * @param player    The player that is viewing the menu.
     * @param warpGroup The warp that is being updated.
     */
    public WarpGroupEditMenu(Player player, WarpGroup warpGroup, boolean alreadyExists, boolean autoLoad) {
        super(4, Messages.component(MessageKeys.Warp.GroupEdit.TITLE), player, autoLoad);

        this.warpGroup = warpGroup;
        this.alreadyExists = alreadyExists;
    }

    @Override
    protected void setMenuItemsAsync() {
        Player player = getMenuPlayer();

        // Set the confirmation item
        getMenu().getSlot(CONFIRM_SLOT).setItem(HeadFactory.head(
                HeadTexture.CHECKMARK,
                Messages.render(player, alreadyExists ? MessageKeys.Warp.Edit.UPDATE : MessageKeys.Warp.Edit.CREATE)));

        // Set the warp group item
        getMenu().getSlot(WARP_GROUP).setItem(
                HeadFactory.letter(
                        LetterType.WOODEN,
                        warpGroup.getName().charAt(0),
                        Messages.render(player, MessageKeys.Warp.Menu.ITEM_NAME, Component.text(warpGroup.getName())),
                        warpGroup.getDescriptionLore(player)
                )
        );

        // Set the name item
        List<Component> nameLore = new ArrayList<>(Messages.loreComponents(
                player, MessageKeys.Warp.Edit.NAME_LORE, Component.text(warpGroup.getName())));
        getMenu().getSlot(NAME_SLOT).setItem(ItemComponents.of(
                Objects.requireNonNull(XMaterial.NAME_TAG.get()),
                Messages.render(player, MessageKeys.Warp.Edit.CHANGE_NAME),
                nameLore));

        // Set the Description item
        getMenu().getSlot(DESCRIPTION_SLOT).setItem(ItemComponents.of(
                Objects.requireNonNull(XMaterial.BOOK.get()),
                Messages.render(player, MessageKeys.Warp.GroupEdit.CHANGE_DESCRIPTION),
                warpGroup.getDescriptionLore(player)));

        // Set the Slot item
        int slot = warpGroup.getSlot();
        String slotValue = slot < 0 || slot >= 27
                ? Messages.plain(player, MessageKeys.Warp.GroupEdit.SLOT_AUTO)
                : String.valueOf(slot);
        List<Component> slotLore = new ArrayList<>(Messages.loreComponents(
                player, MessageKeys.Warp.GroupEdit.SLOT_LORE, Component.text(slotValue)));
        getMenu().getSlot(SLOT_SLOT).setItem(ItemComponents.of(
                Objects.requireNonNull(XMaterial.ITEM_FRAME.get()),
                Messages.render(player, MessageKeys.Warp.GroupEdit.CHANGE_SLOT),
                slotLore));

        // Set the Material item
        String materialName = warpGroup.getMaterial() == null
                ? Messages.plain(player, MessageKeys.Warp.Edit.MATERIAL_DEFAULT)
                : warpGroup.getMaterial();
        List<Component> materialLore = new ArrayList<>(Messages.loreComponents(
                player, MessageKeys.Warp.Edit.MATERIAL_LORE, Component.text(materialName)));
        getMenu().getSlot(MATERIAL_SLOT).setItem(ItemComponents.named(
                warpGroup.getMaterialItem(player),
                Messages.render(player, MessageKeys.Warp.Edit.CHANGE_MATERIAL),
                materialLore));

        // Set the delete item
        getMenu().getSlot(DELETE_SLOT).setItem(ItemComponents.of(
                Objects.requireNonNull(XMaterial.BARRIER.get()),
                Messages.render(player, MessageKeys.Warp.GroupEdit.DELETE),
                Messages.loreComponents(player, MessageKeys.Warp.GroupEdit.DELETE_LORE)));
    }

    @Override
    protected void setItemClickEventsAsync() {
        // Set click event for the confirmation item
        getMenu().getSlot(CONFIRM_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
            clickPlayer.closeInventory();
            clickPlayer.playSound(clickPlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);

            BuildTeam buildTeam = NetworkModule.getInstance().getBuildTeam();
            if (buildTeam != null) {
                if (alreadyExists)
                    buildTeam.updateWarpGroup(clickPlayer, warpGroup);
                else
                    buildTeam.createWarpGroup(clickPlayer, warpGroup);
            }
        });

        // Set click event for the name item
        getMenu().getSlot(NAME_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
            clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);

            new AnvilGUI.Builder()
                    .onClose(player -> {
                        player.getPlayer().playSound(clickPlayer.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);

                        new WarpGroupEditMenu(clickPlayer, warpGroup, alreadyExists, true);
                    })
                    .onClick((slot, stateSnapshot) -> {
                        if (slot != AnvilGUI.Slot.OUTPUT)
                            return Collections.emptyList();

                        stateSnapshot.getPlayer().playSound(clickPlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
                        return Arrays.asList(
                                AnvilGUI.ResponseAction.close(),
                                AnvilGUI.ResponseAction.run(() -> {
                                    warpGroup.setName(stateSnapshot.getText());
                                    new WarpGroupEditMenu(clickPlayer, warpGroup, alreadyExists, true);
                                })
                        );
                    })
                    .text(Messages.plain(clickPlayer, MessageKeys.Warp.GroupEdit.ANVIL_NAME_TEXT))
                    .itemLeft(ItemComponents.of(
                            Objects.requireNonNull(XMaterial.NAME_TAG.get()),
                            Messages.render(clickPlayer, MessageKeys.Warp.Edit.CHANGE_NAME)))
                    .title(Messages.legacy(clickPlayer, MessageKeys.Warp.GroupEdit.ANVIL_NAME_TITLE))
                    .plugin(BuildTeamTools.getInstance())
                    .open(clickPlayer);
        });

        // Set click event for the description item
        getMenu().getSlot(DESCRIPTION_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
            clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);

            List<String[]> description = new ArrayList<>();
            description.add(warpGroup.getDescription().split("<br>"));

            BookMenu bookMenu = new BookMenu(
                    clickPlayer,
                    Messages.render(clickPlayer, MessageKeys.Warp.GroupEdit.BOOK_DESCRIPTION_TITLE),
                    clickPlayer.getName(),
                    description,
                    240);

            bookMenu.onComplete(text -> {
                if (text == null) {
                    clickPlayer.sendMessage(Messages.component(MessageKeys.Warp.GroupEdit.DESCRIPTION_SAVE_FAILED));
                    new WarpGroupEditMenu(clickPlayer, warpGroup, alreadyExists, true);
                    return;
                }

                // Combine the first page to a single string
                StringBuilder finalText = new StringBuilder(text.getFirst()[0]);
                for (int i = 1; i < text.getFirst().length; i++)
                    finalText.append("<br>").append(text.getFirst()[i]);

                warpGroup.setDescription(finalText.toString());
                new WarpGroupEditMenu(clickPlayer, warpGroup, alreadyExists, true);
            });
        });

        // Set click event for the slot item
        getMenu().getSlot(SLOT_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
            clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);

            new AnvilGUI.Builder()
                    .onClose(player -> {
                        player.getPlayer().playSound(clickPlayer.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
                        new WarpGroupEditMenu(clickPlayer, warpGroup, alreadyExists, true);
                    })
                    .onClick((slot, stateSnapshot) -> {
                        if (slot != AnvilGUI.Slot.OUTPUT)
                            return Collections.emptyList();

                        Integer selectedSlot = AlpsUtils.tryParseInt(stateSnapshot.getText());

                        // Make sure that the slot is a valid number and between 0 and 26
                        if (!stateSnapshot.getText().matches("-?\\d+")
                                || selectedSlot == null
                                || selectedSlot < -1
                                || selectedSlot > 26) {
                            return Arrays.asList(AnvilGUI.ResponseAction.close(),
                                    AnvilGUI.ResponseAction.run(() -> {
                                        clickPlayer.closeInventory();
                                        stateSnapshot.getPlayer().playSound(clickPlayer.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
                                        clickPlayer.sendMessage(Messages.component(MessageKeys.Warp.GroupEdit.INVALID_SLOT));
                                        new WarpGroupEditMenu(clickPlayer, warpGroup, alreadyExists, true);
                                    })
                            );
                        }

                        var warpGroups = warpGroup.getBuildTeam().getWarpGroups().stream()
                                .filter(g -> g.getSlot() == selectedSlot)
                                .map(WarpGroup::getName)
                                .findFirst();

                        if (selectedSlot != -1 && warpGroups.isPresent()) {
                            return Arrays.asList(AnvilGUI.ResponseAction.close(),
                                    AnvilGUI.ResponseAction.run(() -> {
                                        clickPlayer.closeInventory();
                                        stateSnapshot.getPlayer().playSound(clickPlayer.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
                                        clickPlayer.sendMessage(Messages.component(
                                                MessageKeys.Warp.GroupEdit.SLOT_OCCUPIED,
                                                Component.text(warpGroups.get()),
                                                Component.text(String.valueOf(selectedSlot))));
                                        new WarpGroupEditMenu(clickPlayer, warpGroup, alreadyExists, true);
                                    })
                            );
                        }

                        stateSnapshot.getPlayer().playSound(clickPlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
                        return Arrays.asList(
                                AnvilGUI.ResponseAction.close(),
                                AnvilGUI.ResponseAction.run(() -> {
                                    warpGroup.setSlot(selectedSlot);
                                    new WarpGroupEditMenu(clickPlayer, warpGroup, alreadyExists, true);
                                })
                        );
                    })
                    .text(Messages.plain(clickPlayer, MessageKeys.Warp.GroupEdit.ANVIL_SLOT_TEXT))
                    .itemLeft(ItemComponents.of(
                            Objects.requireNonNull(XMaterial.ITEM_FRAME.get()),
                            Messages.render(clickPlayer, MessageKeys.Warp.GroupEdit.CHANGE_SLOT)))
                    .title(Messages.legacy(clickPlayer, MessageKeys.Warp.GroupEdit.ANVIL_SLOT_TITLE))
                    .plugin(BuildTeamTools.getInstance())
                    .open(clickPlayer);
        });

        // Set click event for the material item
        getMenu().getSlot(MATERIAL_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
            clickPlayer.closeInventory();
            clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);

            new MaterialSelectionMenu(clickPlayer, warpGroup, alreadyExists);
        });

        // Set click event for the delete item
        getMenu().getSlot(DELETE_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
            clickPlayer.closeInventory();
            clickPlayer.playSound(clickPlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);

            if (clickPlayer.hasPermission(Permissions.WARP_GROUP_DELETE)) {
                BuildTeam buildTeam = NetworkModule.getInstance().getBuildTeam();
                if (buildTeam != null) {
                    if (!warpGroup.getWarps().isEmpty()) {
                        new WarpGroupDeletePromptMenu(clickPlayer, warpGroup, this);
                    } else buildTeam.deleteWarpGroup(clickPlayer, warpGroup, false);
                }
            } else {
                clickPlayer.sendMessage(Messages.component(MessageKeys.Warp.GroupEdit.DELETE_NO_PERMISSION));
            }

        });
    }

    @Override
    protected Mask getMask() {
        return BinaryMask.builder(getMenu())
                .item(MenuItems.ITEM_BACKGROUND)
                .pattern(BinaryMask.EMPTY_PATTERN)
                .pattern(BinaryMask.EMPTY_PATTERN)
                .pattern(BinaryMask.EMPTY_PATTERN)
                .pattern("111111110")
                .build();
    }
}
