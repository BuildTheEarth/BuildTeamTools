package net.buildtheearth.buildteamtools.modules.generator.components.field.menu;

import net.buildtheearth.buildteamtools.i18n.Messages;
import net.buildtheearth.buildteamtools.i18n.MessageKeys;
import com.cryptomorin.xseries.XMaterial;
import net.buildtheearth.buildteamtools.utils.ItemComponents;
import net.buildtheearth.buildteamtools.modules.generator.GeneratorModule;
import net.buildtheearth.buildteamtools.modules.generator.components.field.*;
import net.buildtheearth.buildteamtools.modules.generator.model.Settings;
import net.buildtheearth.buildteamtools.utils.MenuItems;
import net.buildtheearth.buildteamtools.utils.WikiLinks;
import net.buildtheearth.buildteamtools.utils.menus.AbstractMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Objects;

public class CropStageMenu extends AbstractMenu {

    private final CropType cropType;
    private static final byte STAGE_ONE_SLOT = 11;
    private static final byte STAGE_TWO_SLOT = 15;
    private static final int BACK_ITEM_SLOT = 18;

    public CropStageMenu(Player player, CropType cropType) {
        super(3, Messages.component(MessageKeys.Generator.Field.CROP_STAGE_TITLE), player);
        this.cropType = cropType;
    }


    @Override
    protected void setPreviewItems() {
        Field field = GeneratorModule.getInstance().getField();
        CropType type = (CropType) field.getPlayerSettings().get(getMenuPlayer().getUniqueId()).getValues().get(FieldFlag.CROP_TYPE);

        ItemStack itemOne = new ItemStack(Objects.requireNonNull(XMaterial.BARRIER.get()));
        ItemStack itemTwo = new ItemStack(Objects.requireNonNull(XMaterial.BARRIER.get()));

        Player player = getMenuPlayer();
        List<Component> typeLore = Messages.loreComponents(player, MessageKeys.Generator.Field.STAGE_ITEM_LORE);
        itemTwo = switch (type) {
            case POTATO -> {
                itemOne = ItemComponents.named(
                        Objects.requireNonNull(XMaterial.SHORT_GRASS.parseItem()),
                        Messages.render(player, MessageKeys.Generator.Field.STAGE_LOW),
                        typeLore);
                yield ItemComponents.named(
                        Objects.requireNonNull(XMaterial.TALL_GRASS.parseItem()),
                        Messages.render(player, MessageKeys.Generator.Field.STAGE_TALL),
                        typeLore);
            }
            case WHEAT -> {
                itemOne = ItemComponents.of(
                        Objects.requireNonNull(XMaterial.BIRCH_FENCE.get()),
                        Messages.render(player, MessageKeys.Generator.Field.STAGE_LIGHT),
                        typeLore);
                yield ItemComponents.of(
                        Objects.requireNonNull(XMaterial.DARK_OAK_FENCE.get()),
                        Messages.render(player, MessageKeys.Generator.Field.STAGE_DARK),
                        typeLore);
            }
            case CORN -> {
                itemOne = ItemComponents.named(
                        Objects.requireNonNull(XMaterial.SHORT_GRASS.parseItem()),
                        Messages.render(player, MessageKeys.Generator.Field.STAGE_HARVESTED),
                        typeLore);
                yield ItemComponents.named(
                        Objects.requireNonNull(XMaterial.TALL_GRASS.parseItem()),
                        Messages.render(player, MessageKeys.Generator.Field.STAGE_TALL),
                        typeLore);
            }
            case OTHER -> {
                itemOne = ItemComponents.of(
                        Objects.requireNonNull(XMaterial.DEAD_BUSH.get()),
                        Messages.render(player, MessageKeys.Generator.Field.STAGE_DRY),
                        typeLore);
                yield ItemComponents.of(
                        Objects.requireNonNull(XMaterial.WATER_BUCKET.get()),
                        Messages.render(player, MessageKeys.Generator.Field.STAGE_WET),
                        typeLore);
            }
            default -> itemTwo;
        };


        // Set items
        getMenu().getSlot(STAGE_ONE_SLOT).setItem(itemOne);
        getMenu().getSlot(STAGE_TWO_SLOT).setItem(itemTwo);

        setBackItem(BACK_ITEM_SLOT, new CropTypeMenu(getMenuPlayer(), false));


        super.setPreviewItems();
    }

    @Override
    protected void setMenuItemsAsync() {
    }

    @Override
    protected void setItemClickEventsAsync() {
        // Set click events for the crop type items
        getMenu().getSlot(STAGE_ONE_SLOT).setClickHandler(((clickPlayer, clickInformation) -> {
            if (clickInformation.getClickType().equals(ClickType.RIGHT)) {
                sendMoreInformation(clickPlayer, CropType.POTATO);
                return;
            }

            CropStage cropstage = CropStage.FALLBACK;
            cropstage = switch (cropType) {
                case POTATO -> CropStage.LOW;
                case WHEAT -> CropStage.LIGHT;
                case CORN -> CropStage.HARVESTED;
                case OTHER -> CropStage.DRY;
                default -> cropstage;
            };

            performClickAction(clickPlayer, cropstage);
        }));

        getMenu().getSlot(STAGE_TWO_SLOT).setClickHandler(((clickPlayer, clickInformation) -> {
            if (clickInformation.getClickType().equals(ClickType.RIGHT)) {
                sendMoreInformation(clickPlayer, CropType.WHEAT);
                return;
            }

            CropStage cropstage = CropStage.FALLBACK;
            cropstage = switch (cropType) {
                case POTATO, CORN -> CropStage.TALL;
                case WHEAT -> CropStage.DARK;
                case HARVESTED, OTHER -> CropStage.WET;
                default -> cropstage;
            };

            performClickAction(clickPlayer, cropstage);
        }));
    }

    private void performClickAction(Player p, CropStage cropStage) {
        Settings settings = GeneratorModule.getInstance().getField().getPlayerSettings().get(p.getUniqueId());

        if (!(settings instanceof FieldSettings fieldSettings))
            return;

        fieldSettings.setValue(FieldFlag.CROP_STAGE, cropStage);

        p.closeInventory();
        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
        GeneratorModule.getInstance().getField().generate(p);
    }

    public static void sendMoreInformation(Player p, @NonNull CropType cropType) {
        switch (cropType) {
            case POTATO:
                p.sendMessage(Messages.component(MessageKeys.Generator.Field.WIKI_LINK, Component.text(WikiLinks.Gen.Field.CROP_POTATO)));
                break;
            case CORN:
                p.sendMessage(Messages.component(MessageKeys.Generator.Field.WIKI_LINK, Component.text(WikiLinks.Gen.Field.CROP_CORN)));
                break;
            case WHEAT:
                p.sendMessage(Messages.component(MessageKeys.Generator.Field.WIKI_LINK, Component.text(WikiLinks.Gen.Field.CROP_WHEAT)));
                break;
            case HARVESTED:
                p.sendMessage(Messages.component(MessageKeys.Generator.Field.WIKI_LINK, Component.text(WikiLinks.Gen.Field.CROP_HARVESTED)));
                break;
            case OTHER:
                p.sendMessage(Messages.component(MessageKeys.Generator.Field.WIKI_LINK, Component.text(WikiLinks.Gen.Field.CROP_OTHER)));
                break;
            case VINEYARD:
                p.sendMessage(Messages.component(MessageKeys.Generator.Field.WIKI_LINK, Component.text(WikiLinks.Gen.Field.CROP_VINEYARD)));
                break;
            case PEAR:
                p.sendMessage(Messages.component(MessageKeys.Generator.Field.WIKI_LINK, Component.text(WikiLinks.Gen.Field.CROP_PEAR)));
                break;
            case CATTLE:
                p.sendMessage(Messages.component(MessageKeys.Generator.Field.WIKI_LINK, Component.text(WikiLinks.Gen.Field.CROP_CATTLE)));
                break;
            case MEADOW:
                p.sendMessage(Messages.component(MessageKeys.Generator.Field.WIKI_LINK, Component.text(WikiLinks.Gen.Field.CROP_MEADOW)));
                break;
        }

        p.closeInventory();
        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
    }

    @Override
    protected Mask getMask() {
        return BinaryMask.builder(getMenu())
                .item(MenuItems.ITEM_BACKGROUND)
                .pattern(BinaryMask.FULL_PATTERN)
                .pattern("110111011")
                .pattern("011111111")
                .build();
    }
}
