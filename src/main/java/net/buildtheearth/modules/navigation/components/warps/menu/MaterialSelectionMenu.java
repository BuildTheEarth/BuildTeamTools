package net.buildtheearth.modules.navigation.components.warps.menu;

import net.buildtheearth.Main;
import net.buildtheearth.modules.utils.ChatHelper;
import net.buildtheearth.modules.utils.Item;
import net.buildtheearth.modules.utils.ListUtil;
import net.buildtheearth.modules.utils.MenuItems;
import net.buildtheearth.modules.utils.menus.AbstractMenu;
import net.buildtheearth.modules.navigation.components.warps.model.Warp;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;

import java.util.Arrays;
import java.util.Collections;

public class MaterialSelectionMenu extends AbstractMenu {

    public static String MATERIAL_INV_NAME = "Select a Warp Material";
    public final int MATERIAL_SLOT = 11;
    public final int CUSTOM_HEAD_SLOT = 15;
    public final int BACK_ITEM_SLOT = 27;

    private final Warp warp;
    private final boolean alreadyExists;

    /** In this menu the player can select a material for a warp.
     * This can be used for example to change the material of a warp in the {@link WarpMenu}.
     *
     * @param menuPlayer  The player that is viewing the menu.
     * @param warp The warp that is being updated with the selected warp group.
     * @param alreadyExists Whether the warp already exists.
     */
    public MaterialSelectionMenu(Player menuPlayer, Warp warp, boolean alreadyExists) {
        super(4, MATERIAL_INV_NAME, menuPlayer);
        this.warp = warp;
        this.alreadyExists = alreadyExists;
    }

    @Override
    protected void setMenuItemsAsync() {
        getMenu().getSlot(MATERIAL_SLOT).setItem(Item.create(Material.STONE, "§6§lItem", ListUtil.createList("", "Change the material of the warp", "to a minecraft item.", "", "§eExample:", "Stone")));
        getMenu().getSlot(CUSTOM_HEAD_SLOT).setItem(MenuItems.getLetterHead("?", MenuItems.LetterType.WOODEN, "§6§lCustom Head", ListUtil.createList("", "Change the material of the warp", "to a custom head texture URL.", "", "§eExample:", "https://textures.minecraft.net/texture/...")));

        setBackItem(BACK_ITEM_SLOT, true);
    }

    @Override
    protected void setItemClickEventsAsync() {
        getMenu().getSlot(BACK_ITEM_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
            clickPlayer.closeInventory();
            new WarpEditMenu(clickPlayer, warp, alreadyExists);
        });

        getMenu().getSlot(MATERIAL_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
            clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);

            new AnvilGUI.Builder()
                    .onClose(player -> {
                        player.getPlayer().playSound(clickPlayer.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);

                        new MaterialSelectionMenu(clickPlayer, warp, alreadyExists);
                    })
                    .onClick((slot, stateSnapshot) -> {
                        if (slot != AnvilGUI.Slot.OUTPUT)
                            return Collections.emptyList();

                        // Make sure that the material is valid
                        if(Material.matchMaterial(stateSnapshot.getText().toUpperCase().split(":")[0]) == null){
                            return Arrays.asList(
                                    AnvilGUI.ResponseAction.close(),
                                    AnvilGUI.ResponseAction.run(() -> {
                                        clickPlayer.closeInventory();
                                        stateSnapshot.getPlayer().playSound(clickPlayer.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
                                        clickPlayer.sendMessage(ChatHelper.error("§cThis is not a valid material."));
                                        new WarpEditMenu(clickPlayer, warp, alreadyExists);
                                    })
                            );
                        }

                        stateSnapshot.getPlayer().playSound(clickPlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
                        return Arrays.asList(
                                AnvilGUI.ResponseAction.close(),
                                AnvilGUI.ResponseAction.run(() -> {
                                    warp.setMaterial(stateSnapshot.getText().toUpperCase());
                                    new WarpEditMenu(clickPlayer, warp, alreadyExists);
                                })
                        );
                    })
                    .text("Material")
                    .itemLeft(Item.create(Material.NAME_TAG, "§6§lEnter Material Name"))
                    .title("§8Enter Material Name")
                    .plugin(Main.instance)
                    .open(clickPlayer);
        });

        // Set click event for the name item
        getMenu().getSlot(CUSTOM_HEAD_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
            clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);

            new AnvilGUI.Builder()
                    .onClose(player -> {
                        player.getPlayer().playSound(clickPlayer.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);

                        new MaterialSelectionMenu(clickPlayer, warp, alreadyExists);
                    })
                    .onClick((slot, stateSnapshot) -> {
                        if (slot != AnvilGUI.Slot.OUTPUT)
                            return Collections.emptyList();

                        // Make sure that the texture url starts with http://textures.minecraft.net/texture/
                        if(!stateSnapshot.getText().startsWith("http://textures.minecraft.net/texture/")){
                            return Arrays.asList(
                                    AnvilGUI.ResponseAction.close(),
                                    AnvilGUI.ResponseAction.run(() -> {
                                        clickPlayer.closeInventory();
                                        stateSnapshot.getPlayer().playSound(clickPlayer.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
                                        clickPlayer.sendMessage(ChatHelper.error("§cThe URL must start with http://textures.minecraft.net/texture/"));
                                        new WarpEditMenu(clickPlayer, warp, alreadyExists);
                                    })
                            );
                        }

                        stateSnapshot.getPlayer().playSound(clickPlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
                        return Arrays.asList(
                                AnvilGUI.ResponseAction.close(),
                                AnvilGUI.ResponseAction.run(() -> {
                                    warp.setMaterial(stateSnapshot.getText());
                                    new WarpEditMenu(clickPlayer, warp, alreadyExists);
                                })
                        );
                    })
                    .text("URL")
                    .itemLeft(Item.create(Material.NAME_TAG, "§6§lEnter Custom Head Texture URL"))
                    .title("§8Enter Custom Head Texture URL")
                    .plugin(Main.instance)
                    .open(clickPlayer);
        });
    }

    @Override
    protected Mask getMask() {
        return BinaryMask.builder(getMenu())
                .item(Item.create(Material.STAINED_GLASS_PANE, " ", (short) 15, null))
                .pattern("000000000")
                .pattern("000000000")
                .pattern("000000000")
                .pattern("011111111")
                .build();
    }
}
