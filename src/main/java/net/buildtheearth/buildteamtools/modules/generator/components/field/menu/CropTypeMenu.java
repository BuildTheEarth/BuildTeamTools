package net.buildtheearth.buildteamtools.modules.generator.components.field.menu;

import net.buildtheearth.buildteamtools.i18n.Messages;
import net.buildtheearth.buildteamtools.i18n.MessageKeys;
import com.cryptomorin.xseries.XMaterial;
import net.buildtheearth.buildteamtools.utils.ItemComponents;
import net.buildtheearth.buildteamtools.modules.generator.GeneratorModule;
import net.buildtheearth.buildteamtools.modules.generator.components.field.CropType;
import net.buildtheearth.buildteamtools.modules.generator.components.field.FieldFlag;
import net.buildtheearth.buildteamtools.modules.generator.components.field.FieldSettings;
import net.buildtheearth.buildteamtools.modules.generator.menu.GeneratorMenu;
import net.buildtheearth.buildteamtools.modules.generator.model.Settings;
import net.buildtheearth.buildteamtools.utils.MenuItems;
import net.buildtheearth.buildteamtools.utils.menus.AbstractMenu;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

public class CropTypeMenu extends AbstractMenu {

    private final byte POTATO_CROP_SLOT = 9;
    private final byte WHEAT_CROP_SLOT = 10;
    private final byte CORN_CROP_SLOT = 11;
    private final byte VINEYARD_CROP_SLOT = 12;
    private final byte PEAR_CROP_SLOT = 13;
    private final byte CATTLE_CROP_SLOT = 14;
    private final byte MEADOW_CROP_SLOT = 15;
    private final byte HARVESTED_CROP_SLOT = 16;
    private final byte OTHER_CROP_SLOT = 17;

    public CropTypeMenu(Player player, boolean autoLoad) {
        super(3, Messages.component(MessageKeys.Generator.Field.CROP_TYPE_TITLE), player, autoLoad);
    }


    @Override
    protected void setPreviewItems() {

        Player player = getMenuPlayer();
        var cropLore = Messages.loreComponents(player, MessageKeys.Generator.Field.CROP_ITEM_LORE);
        ItemStack potatoItem = ItemComponents.of(Objects.requireNonNull(XMaterial.POTATO.get()), Messages.render(player, MessageKeys.Generator.Field.CROP_POTATO), cropLore);
        ItemStack wheatItem = ItemComponents.of(Objects.requireNonNull(XMaterial.WHEAT.get()), Messages.render(player, MessageKeys.Generator.Field.CROP_WHEAT), cropLore);
        ItemStack cornItem = ItemComponents.of(Objects.requireNonNull(XMaterial.PUMPKIN_SEEDS.get()), Messages.render(player, MessageKeys.Generator.Field.CROP_CORN), cropLore);
        ItemStack vineyardItem = ItemComponents.of(Objects.requireNonNull(XMaterial.VINE.get()), Messages.render(player, MessageKeys.Generator.Field.CROP_VINEYARD), cropLore);
        ItemStack pearItem = ItemComponents.of(Objects.requireNonNull(XMaterial.SLIME_BALL.get()), Messages.render(player, MessageKeys.Generator.Field.CROP_PEAR), cropLore);
        ItemStack cattleItem = ItemComponents.of(Objects.requireNonNull(XMaterial.SPRUCE_FENCE.get()), Messages.render(player, MessageKeys.Generator.Field.CROP_CATTLE), cropLore);
        ItemStack meadowItem = ItemComponents.of(Objects.requireNonNull(XMaterial.SHORT_GRASS.get()), Messages.render(player, MessageKeys.Generator.Field.CROP_MEADOW), cropLore);
        ItemStack harvestedItem = ItemComponents.of(Objects.requireNonNull(XMaterial.HAY_BLOCK.get()), Messages.render(player, MessageKeys.Generator.Field.CROP_HARVESTED), cropLore);
        ItemStack otherItem = ItemComponents.of(Objects.requireNonNull(XMaterial.DEAD_BUSH.get()), Messages.render(player, MessageKeys.Generator.Field.CROP_OTHER), cropLore);

        // Set items
        getMenu().getSlot(POTATO_CROP_SLOT).setItem(potatoItem);
        getMenu().getSlot(WHEAT_CROP_SLOT).setItem(wheatItem);
        getMenu().getSlot(CORN_CROP_SLOT).setItem(cornItem);
        getMenu().getSlot(VINEYARD_CROP_SLOT).setItem(vineyardItem);
        getMenu().getSlot(PEAR_CROP_SLOT).setItem(pearItem);
        getMenu().getSlot(CATTLE_CROP_SLOT).setItem(cattleItem);
        getMenu().getSlot(MEADOW_CROP_SLOT).setItem(meadowItem);
        getMenu().getSlot(HARVESTED_CROP_SLOT).setItem(harvestedItem);
        getMenu().getSlot(OTHER_CROP_SLOT).setItem(otherItem);

        int backItemSlot = 18;
        setBackItem(backItemSlot, new GeneratorMenu(getMenuPlayer(), false));

        super.setPreviewItems();
    }

    @Override
    protected void setMenuItemsAsync() {
    }

    @Override
    protected void setItemClickEventsAsync() {
        // Set click events for the crop type items
        getMenu().getSlot(POTATO_CROP_SLOT).setClickHandler(((clickPlayer, clickInformation) -> {
            if (clickInformation.getClickType().equals(ClickType.RIGHT)) {
                CropStageMenu.sendMoreInformation(clickPlayer, CropType.POTATO);
                return;
            }
            performClickAction(clickPlayer, CropType.POTATO);
        }));

        getMenu().getSlot(WHEAT_CROP_SLOT).setClickHandler(((clickPlayer, clickInformation) -> {
            if (clickInformation.getClickType().equals(ClickType.RIGHT)) {
                CropStageMenu.sendMoreInformation(clickPlayer, CropType.WHEAT);
                return;
            }
            performClickAction(clickPlayer, CropType.WHEAT);
        }));

        getMenu().getSlot(CORN_CROP_SLOT).setClickHandler(((clickPlayer, clickInformation) -> {
            if (clickInformation.getClickType().equals(ClickType.RIGHT)) {
                CropStageMenu.sendMoreInformation(clickPlayer, CropType.CORN);
                return;
            }
            performClickAction(clickPlayer, CropType.CORN);
        }));

        getMenu().getSlot(VINEYARD_CROP_SLOT).setClickHandler(((clickPlayer, clickInformation) -> {
            if (clickInformation.getClickType().equals(ClickType.RIGHT)) {
                CropStageMenu.sendMoreInformation(clickPlayer, CropType.VINEYARD);
                return;
            }
            performClickAction(clickPlayer, CropType.VINEYARD);
        }));

        getMenu().getSlot(PEAR_CROP_SLOT).setClickHandler(((clickPlayer, clickInformation) -> {
            if (clickInformation.getClickType().equals(ClickType.RIGHT)) {
                CropStageMenu.sendMoreInformation(clickPlayer, CropType.PEAR);
                return;
            }
            performClickAction(clickPlayer, CropType.PEAR);
        }));

        getMenu().getSlot(CATTLE_CROP_SLOT).setClickHandler(((clickPlayer, clickInformation) -> {
            if (clickInformation.getClickType().equals(ClickType.RIGHT)) {
                CropStageMenu.sendMoreInformation(clickPlayer, CropType.CATTLE);
                return;
            }
            performClickAction(clickPlayer, CropType.CATTLE);
        }));

        getMenu().getSlot(MEADOW_CROP_SLOT).setClickHandler(((clickPlayer, clickInformation) -> {
            if (clickInformation.getClickType().equals(ClickType.RIGHT)) {
                CropStageMenu.sendMoreInformation(clickPlayer, CropType.MEADOW);
                return;
            }
            performClickAction(clickPlayer, CropType.MEADOW);
        }));

        getMenu().getSlot(HARVESTED_CROP_SLOT).setClickHandler(((clickPlayer, clickInformation) -> {
            if (clickInformation.getClickType().equals(ClickType.RIGHT)) {
                CropStageMenu.sendMoreInformation(clickPlayer, CropType.HARVESTED);
                return;
            }
            performClickAction(clickPlayer, CropType.HARVESTED);
        }));

        getMenu().getSlot(OTHER_CROP_SLOT).setClickHandler(((clickPlayer, clickInformation) -> {
            if (clickInformation.getClickType().equals(ClickType.RIGHT)) {
                CropStageMenu.sendMoreInformation(clickPlayer, CropType.OTHER);
                return;
            }
            performClickAction(clickPlayer, CropType.OTHER);
        }));


    }

    private void performClickAction(@NonNull Player p, CropType cropType) {
        Settings settings = GeneratorModule.getInstance().getField().getPlayerSettings().get(p.getUniqueId());

        if (!(settings instanceof FieldSettings fieldSettings))
            return;

        fieldSettings.setValue(FieldFlag.CROP_TYPE, cropType.getIdentifier());

        p.closeInventory();
        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
        if (cropType.hasStages()) {
            new CropStageMenu(p, cropType);
            return;
        }
        if (cropType.equals(CropType.CATTLE) || cropType.equals(CropType.MEADOW)) {
            new FenceTypeMenu(p, true);
            return;
        }

        GeneratorModule.getInstance().getField().generate(p);
    }

    @Override
    protected Mask getMask() {
        return BinaryMask.builder(getMenu())
                .item(MenuItems.ITEM_BACKGROUND)
                .pattern(BinaryMask.FULL_PATTERN)
                .pattern(BinaryMask.EMPTY_PATTERN)
                .pattern("011111111")
                .build();
    }
}
