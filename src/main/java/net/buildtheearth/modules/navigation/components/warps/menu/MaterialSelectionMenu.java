package net.buildtheearth.modules.navigation.components.warps.menu;

import com.cryptomorin.xseries.XMaterial;
import net.buildtheearth.BuildTeamTools;
import net.buildtheearth.modules.navigation.components.warps.model.WarpGroup;
import net.buildtheearth.utils.*;
import net.buildtheearth.utils.menus.AbstractMenu;
import net.buildtheearth.modules.navigation.components.warps.model.Warp;
import net.buildtheearth.utils.menus.BookMenu;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MaterialSelectionMenu extends AbstractMenu {

    public static String MATERIAL_INV_NAME = "Select a Warp Material";
    public final int MATERIAL_SLOT = 11;
    public final int CUSTOM_HEAD_SLOT = 15;
    public final int BACK_ITEM_SLOT = 27;

    private final Object object;
    private final boolean alreadyExists;

    /** In this menu the player can select a material for a warp.
     * This can be used for example to change the material of a warp in the {@link WarpMenu}.
     *
     * @param menuPlayer  The player that is viewing the menu.
     * @param object The Warp or WarpGroup that is being updated with the selected material.
     * @param alreadyExists Whether the warp already exists.
     */
    public MaterialSelectionMenu(Player menuPlayer, Object object, boolean alreadyExists) {
        super(4, MATERIAL_INV_NAME, menuPlayer);
        this.object = object;
        this.alreadyExists = alreadyExists;
    }

    @Override
    protected void setMenuItemsAsync() {
        getMenu().getSlot(MATERIAL_SLOT).setItem(Item.create(XMaterial.STONE.parseMaterial(), "§6§lItem", ListUtil.createList("", "Change the material of the warp", "to a minecraft item.", "", "§eExample:", "Stone")));
        getMenu().getSlot(CUSTOM_HEAD_SLOT).setItem(CustomHeads.getLetterHead("?", CustomHeads.LetterType.WOODEN, "§6§lCustom Head", ListUtil.createList("", "Change the material of the warp", "to a custom head texture URL.", "", "§eExample:", "https://textures.minecraft.net/texture/...")));

        if(object instanceof Warp)
            setBackItem(BACK_ITEM_SLOT, new WarpEditMenu(getMenuPlayer(), (Warp) object, alreadyExists, false));
        else if(object instanceof WarpGroup)
            setBackItem(BACK_ITEM_SLOT, new WarpGroupEditMenu(getMenuPlayer(), (WarpGroup) object, alreadyExists, false));
    }

    @Override
    protected void setItemClickEventsAsync() {
        getMenu().getSlot(MATERIAL_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
            clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);

            new AnvilGUI.Builder()
                    .onClose(player -> {
                        player.getPlayer().playSound(clickPlayer.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);

                        openObjectMenu();
                    })
                    .onClick((slot, stateSnapshot) -> {
                        if (slot != AnvilGUI.Slot.OUTPUT)
                            return Collections.emptyList();

                        // Make sure that the material is valid
                        if(Item.fromUniqueMaterialString(stateSnapshot.getText()) == null){
                            return Arrays.asList(
                                    AnvilGUI.ResponseAction.close(),
                                    AnvilGUI.ResponseAction.run(() -> {
                                        clickPlayer.closeInventory();
                                        stateSnapshot.getPlayer().playSound(clickPlayer.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
                                        clickPlayer.sendMessage(ChatHelper.getErrorString("§cThis is not a valid material."));
                                        openObjectMenu();
                                    })
                            );
                        }

                        stateSnapshot.getPlayer().playSound(clickPlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
                        return Arrays.asList(
                                AnvilGUI.ResponseAction.close(),
                                AnvilGUI.ResponseAction.run(() -> {
                                    if(object instanceof Warp)
                                        ((Warp) object).setMaterial(stateSnapshot.getText().toUpperCase());
                                    else if(object instanceof WarpGroup)
                                        ((WarpGroup) object).setMaterial(stateSnapshot.getText().toUpperCase());

                                    openObjectMenu();
                                })
                        );
                    })
                    .text("Material")
                    .itemLeft(Item.create(XMaterial.NAME_TAG.parseMaterial(), "§6§lEnter Material Name"))
                    .title("§8Enter Material Name")
                    .plugin(BuildTeamTools.getInstance())
                    .open(clickPlayer);
        });

        // Set click event for the custom head item
        getMenu().getSlot(CUSTOM_HEAD_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
            clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);

            String[] url = {"http://textures.minecraft.net/texture/..."};
            List<String[]> content = new ArrayList<>();
            content.add(url);

            BookMenu bookMenu = new BookMenu(clickPlayer, "§6§lChange Texture URL", clickPlayer.getName(), content, 240);

            bookMenu.onComplete((text) -> {
                if(text == null) {
                    clickPlayer.playSound(clickPlayer.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
                    clickPlayer.sendMessage("§cA problem occurred while saving the url.");
                    openObjectMenu();
                    return;
                }

                // Combine the first page to a single string
                StringBuilder finalText = new StringBuilder(text.get(0)[0]);
                for(int i = 1; i < text.get(0).length; i++)
                    finalText.append(text.get(0)[i]);

                if(!finalText.toString().startsWith("http://textures.minecraft.net/texture/")){
                    clickPlayer.playSound(clickPlayer.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
                    clickPlayer.sendMessage("§cThe URL must start with http://textures.minecraft.net/texture/");
                    openObjectMenu();
                }

                if(object instanceof Warp)
                    ((Warp) object).setMaterial(finalText.toString());
                else if(object instanceof WarpGroup)
                    ((WarpGroup) object).setMaterial(finalText.toString());

                openObjectMenu();
            });
        });
    }

    @Override
    protected Mask getMask() {
        return BinaryMask.builder(getMenu())
                .item(MenuItems.ITEM_BACKGROUND)
                .pattern("000000000")
                .pattern("000000000")
                .pattern("000000000")
                .pattern("011111111")
                .build();
    }

    private void openObjectMenu(){
        if(object instanceof Warp)
            new WarpEditMenu(getMenuPlayer(), (Warp) object, alreadyExists, true);
        else if(object instanceof WarpGroup)
            new WarpGroupEditMenu(getMenuPlayer(), (WarpGroup) object, alreadyExists, true);
    }
}
