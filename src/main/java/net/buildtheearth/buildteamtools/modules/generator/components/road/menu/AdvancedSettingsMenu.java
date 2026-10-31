package net.buildtheearth.buildteamtools.modules.generator.components.road.menu;

import net.buildtheearth.buildteamtools.i18n.Messages;
import net.kyori.adventure.text.Component;
import net.buildtheearth.buildteamtools.i18n.MessageKeys;
import com.cryptomorin.xseries.XMaterial;
import net.buildtheearth.buildteamtools.utils.ItemComponents;
import net.buildtheearth.buildteamtools.modules.generator.GeneratorModule;
import net.buildtheearth.buildteamtools.modules.generator.components.road.Road;
import net.buildtheearth.buildteamtools.modules.generator.components.road.RoadFlag;
import net.buildtheearth.buildteamtools.modules.generator.components.road.RoadSettings;
import net.buildtheearth.buildteamtools.modules.generator.model.Settings;
import net.buildtheearth.buildteamtools.utils.MenuItems;
import net.buildtheearth.buildteamtools.utils.heads.HeadColor;
import net.buildtheearth.buildteamtools.utils.heads.HeadFactory;
import net.buildtheearth.buildteamtools.utils.heads.HeadTexture;
import net.buildtheearth.buildteamtools.utils.menus.AbstractMenu;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class AdvancedSettingsMenu extends AbstractMenu {

    public static final int LANE_COUNT_SLOT = 2;
    public static final int LANE_WIDTH_SLOT = 11;
    public static final int SIDEWALK_WIDTH_SLOT = 20;
    public static final int STREET_LAMP_DISTANCE_SLOT = 29;

    public static final int MARKINGS_MATERIAL_SLOT = 6;
    public static final int ROAD_SLAB_SLOT = 15;
    public static final int SIDEWALK_SLAB_SLOT = 24;
    public static final int STREET_LAMP_TYPE_SLOT = 33;


    public static final int NEXT_ITEM_SLOT = 44;

    private static final int BACK_ITEM_SLOT = 36;

    public AdvancedSettingsMenu(Player player, boolean autoLoad) {
        super(5, Messages.component(MessageKeys.Generator.Road.ADVANCED_TITLE), player, autoLoad);
    }

    @Override
    protected void setPreviewItems() {
        Road road = GeneratorModule.getInstance().getRoad();
        UUID uuid = getMenuPlayer().getUniqueId();

        int laneCount = (int) road.getPlayerSettings().get(uuid).getValues().get(RoadFlag.LANE_COUNT);
        int laneWidth = (int) road.getPlayerSettings().get(uuid).getValues().get(RoadFlag.LANE_WIDTH);
        int sidewalkWidth = (int) road.getPlayerSettings().get(uuid).getValues().get(RoadFlag.SIDEWALK_WIDTH);
        int streetLampDistance = (int) road.getPlayerSettings().get(uuid).getValues().get(RoadFlag.STREET_LAMP_DISTANCE);

        XMaterial markingsMaterial = (XMaterial) road.getPlayerSettings().get(uuid).getValues().get(RoadFlag.MARKING_MATERIAL);
        XMaterial[] sidewalkSlab = (XMaterial[]) road.getPlayerSettings().get(uuid).getValues().get(RoadFlag.SIDEWALK_SLAB_COLOR);
        XMaterial[] roadSlab = (XMaterial[]) road.getPlayerSettings().get(uuid).getValues().get(RoadFlag.ROAD_SLAB_COLOR);
        ItemStack streetLampType = getStreetLampItem((String) road.getPlayerSettings().get(uuid).getValues().get(RoadFlag.STREET_LAMP_TYPE));

        // If roadSlabs are empty, show a barrier instead.
        if (roadSlab.length == 0)
            roadSlab = new XMaterial[]{XMaterial.BARRIER};

        createCounter(HeadColor.WHITE, LANE_COUNT_SLOT, Messages.plain(getMenuPlayer(), MessageKeys.Generator.Road.LANE_COUNT), laneCount, 1, 10, Messages.plain(getMenuPlayer(), MessageKeys.Generator.Road.UNIT_LANES));
        createCounter(HeadColor.LIGHT_GRAY, LANE_WIDTH_SLOT, Messages.plain(getMenuPlayer(), MessageKeys.Generator.Road.LANE_WIDTH), laneWidth, 1, 30, Messages.plain(getMenuPlayer(), MessageKeys.Generator.Road.UNIT_BLOCKS));
        createCounter(HeadColor.WHITE, SIDEWALK_WIDTH_SLOT, Messages.plain(getMenuPlayer(), MessageKeys.Generator.Road.SIDEWALK_WIDTH), sidewalkWidth, 1, 30, Messages.plain(getMenuPlayer(), MessageKeys.Generator.Road.UNIT_BLOCKS));
        createCounter(HeadColor.LIGHT_GRAY, STREET_LAMP_DISTANCE_SLOT, Messages.plain(getMenuPlayer(), MessageKeys.Generator.Road.STREET_LAMP_DISTANCE), streetLampDistance, 5, 500, Messages.plain(getMenuPlayer(), MessageKeys.Generator.Road.UNIT_BLOCKS));

        setChoiceItems(HeadColor.WHITE, MARKINGS_MATERIAL_SLOT, Messages.plain(getMenuPlayer(), MessageKeys.Generator.Road.MARKINGS_COLOR), markingsMaterial.parseItem());
        setChoiceItems(HeadColor.LIGHT_GRAY, ROAD_SLAB_SLOT, Messages.plain(getMenuPlayer(), MessageKeys.Generator.Road.ROAD_SLAB), roadSlab[0].parseItem());
        setChoiceItems(HeadColor.WHITE, SIDEWALK_SLAB_SLOT, Messages.plain(getMenuPlayer(), MessageKeys.Generator.Road.SIDEWALK_SLAB), sidewalkSlab[0].parseItem());
        setChoiceItems(HeadColor.LIGHT_GRAY, STREET_LAMP_TYPE_SLOT, Messages.plain(getMenuPlayer(), MessageKeys.Generator.Road.STREET_LAMP_TYPE), streetLampType);

        getMenu().getSlot(NEXT_ITEM_SLOT).setItem(HeadFactory.head(HeadTexture.CHECKMARK, Messages.render(getMenuPlayer(), MessageKeys.Generator.Road.NEXT)));

        setBackItem(BACK_ITEM_SLOT, new SidewalkColorMenu(getMenuPlayer(), false));

        super.setPreviewItems();
    }

    @Override
    protected void setMenuItemsAsync() {
    }

    @Override
    protected void setItemClickEventsAsync() {
        List<ItemStack> streetLampTypes = new ArrayList<>();
        for (String streetLampType : RoadSettings.streetLampTypes)
            streetLampTypes.add(getStreetLampItem(streetLampType));

        setSliderClickEvents(RoadFlag.LANE_COUNT, LANE_COUNT_SLOT, 1, 10);
        setSliderClickEvents(RoadFlag.LANE_WIDTH, LANE_WIDTH_SLOT, 1, 30);
        setSliderClickEvents(RoadFlag.SIDEWALK_WIDTH, SIDEWALK_WIDTH_SLOT, 1, 30);
        setSliderClickEvents(RoadFlag.STREET_LAMP_TYPE, STREET_LAMP_TYPE_SLOT, 5, 500);

        setChoiceClickEvents(RoadFlag.MARKING_MATERIAL, MARKINGS_MATERIAL_SLOT, Messages.plain(getMenuPlayer(), MessageKeys.Generator.Road.CHOOSE_MARKING), MenuItems.getSolidBlocks());
        setChoiceClickEvents(RoadFlag.ROAD_SLAB_COLOR, ROAD_SLAB_SLOT, Messages.plain(getMenuPlayer(), MessageKeys.Generator.Road.CHOOSE_ROAD_SLAB), MenuItems.getSlabs());
        setChoiceClickEvents(RoadFlag.SIDEWALK_SLAB_COLOR, SIDEWALK_SLAB_SLOT, Messages.plain(getMenuPlayer(), MessageKeys.Generator.Road.CHOOSE_SIDEWALK_SLAB), MenuItems.getSlabs());
        setChoiceClickEvents(RoadFlag.STREET_LAMP_TYPE, STREET_LAMP_TYPE_SLOT, Messages.plain(getMenuPlayer(), MessageKeys.Generator.Road.CHOOSE_STREET_LAMP), streetLampTypes);

        // Set click events items
        getMenu().getSlot(NEXT_ITEM_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
            clickPlayer.closeInventory();
            clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);

            GeneratorModule.getInstance().getRoad().generate(clickPlayer);
        });
    }

    @Override
    protected Mask getMask() {
        return BinaryMask.builder(getMenu())
                .item(MenuItems.ITEM_BACKGROUND)
                .pattern(BinaryMask.FULL_PATTERN)
                .pattern("100010001")
                .pattern("100010001")
                .pattern("100010001")
                .pattern("011111110")
                .build();
    }

    protected void setSliderClickEvents(RoadFlag roadFlag, int slot, int minValue, int maxValue) {
        Road road = GeneratorModule.getInstance().getRoad();

        // Set click event for previous page item
        getMenu().getSlot(slot - 1).setClickHandler((clickPlayer, clickInformation) -> {
            int value = (int) road.getPlayerSettings().get(clickPlayer.getUniqueId()).getValues().get(roadFlag);

            if (value > minValue) {
                Settings settings = road.getPlayerSettings().get(clickPlayer.getUniqueId());

                if (!(settings instanceof RoadSettings roadSettings))
                    return;

                roadSettings.setValue(roadFlag, value - 1);

                clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                reloadMenuAsync();
            } else {
                clickPlayer.playSound(clickPlayer.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
            }
        });

        // Set click event for next page item
        getMenu().getSlot(slot + 1).setClickHandler((clickPlayer, clickInformation) -> {
            int value = (int) road.getPlayerSettings().get(clickPlayer.getUniqueId()).getValues().get(roadFlag);

            if (value < maxValue) {
                Settings settings = road.getPlayerSettings().get(clickPlayer.getUniqueId());

                if (!(settings instanceof RoadSettings roadSettings))
                    return;

                roadSettings.setValue(roadFlag, value + 1);

                clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                reloadMenuAsync();
            } else {
                clickPlayer.playSound(clickPlayer.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);

            }
        });
    }


    protected void setChoiceClickEvents(RoadFlag roadFlag, int slot, String choiceInvName, List<ItemStack> choices) {
        // Set click event for X items
        getMenu().getSlot(slot - 1).setClickHandler((clickPlayer, clickInformation) -> turnOffColorChoice(clickPlayer, roadFlag));
        getMenu().getSlot(slot + 1).setClickHandler((clickPlayer, clickInformation) -> turnOffColorChoice(clickPlayer, roadFlag));

        // Set click event for color choice items
        getMenu().getSlot(slot).setClickHandler((clickPlayer, clickInformation) -> {
            clickPlayer.closeInventory();
            clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);

            new AdvancedColorMenu(clickPlayer, roadFlag, choiceInvName, choices, true);
        });
    }

    protected void turnOffColorChoice(Player clickPlayer, RoadFlag roadFlag) {
        Road road = GeneratorModule.getInstance().getRoad();
        Settings settings = road.getPlayerSettings().get(clickPlayer.getUniqueId());

        if (!(settings instanceof RoadSettings roadSettings))
            return;

        roadSettings.setValue(roadFlag, "OFF");

        clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
        reloadMenuAsync();
    }

    protected ItemStack getStreetLampItem(String id) {
        return ItemComponents.of(
                Objects.requireNonNull(XMaterial.SEA_LANTERN.get()),
                Messages.render(getMenuPlayer(), MessageKeys.Generator.Road.STREET_LAMP_NAME, Component.text(String.valueOf(id))),
                Messages.loreComponents(getMenuPlayer(), MessageKeys.Generator.Road.STREET_LAMP_LORE));
    }
}
