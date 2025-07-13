package net.buildtheearth.modules.generator.components.road.menu;

import com.cryptomorin.xseries.XMaterial;
import net.buildtheearth.modules.generator.GeneratorModule;
import net.buildtheearth.modules.generator.model.Settings;
import net.buildtheearth.modules.generator.components.road.Road;
import net.buildtheearth.modules.generator.components.road.RoadFlag;
import net.buildtheearth.modules.generator.components.road.RoadSettings;
import net.buildtheearth.utils.CustomHeads;
import net.buildtheearth.utils.Item;
import net.buildtheearth.utils.ListUtil;
import net.buildtheearth.utils.MenuItems;
import net.buildtheearth.utils.menus.AbstractMenu;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AdvancedSettingsMenu extends AbstractMenu {

    public static final String ADVANCED_SETTINGS_INV_NAME = "Adjust some Advanced Settings";

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
        super(5, ADVANCED_SETTINGS_INV_NAME, player, autoLoad);
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

        createCounter(CustomHeads.SliderColor.WHITE, LANE_COUNT_SLOT, "Number of Lanes", laneCount, 1, 10, "Lanes");
        createCounter(CustomHeads.SliderColor.LIGHT_GRAY, LANE_WIDTH_SLOT, "Lane Width", laneWidth, 1, 30, "Blocks");
        createCounter(CustomHeads.SliderColor.WHITE, SIDEWALK_WIDTH_SLOT, "Sidewalk Width", sidewalkWidth, 1, 30, "Blocks");
        createCounter(CustomHeads.SliderColor.LIGHT_GRAY, STREET_LAMP_DISTANCE_SLOT, "Street Lamp Distance", streetLampDistance, 5, 500, "Blocks");

        setChoiceItems(CustomHeads.SliderColor.WHITE, MARKINGS_MATERIAL_SLOT, "Line Markings Color", markingsMaterial.parseItem());
        setChoiceItems(CustomHeads.SliderColor.LIGHT_GRAY, ROAD_SLAB_SLOT, "Road Elevation Slab", roadSlab[0].parseItem());
        setChoiceItems(CustomHeads.SliderColor.WHITE, SIDEWALK_SLAB_SLOT, "Sidewalk Elevation Slab", sidewalkSlab[0].parseItem());
        setChoiceItems(CustomHeads.SliderColor.LIGHT_GRAY, STREET_LAMP_TYPE_SLOT, "Street Lamp Type", streetLampType);

        getMenu().getSlot(NEXT_ITEM_SLOT).setItem(CustomHeads.getCheckmarkItem("§eNext"));

        setBackItem(BACK_ITEM_SLOT, new SidewalkColorMenu(getMenuPlayer(), false));

        super.setPreviewItems();
    }

    @Override
    protected void setMenuItemsAsync() {}

    @Override
    protected void setItemClickEventsAsync() {
        List<ItemStack> streetLampTypes = new ArrayList<>();
        for(String streetLampType : RoadSettings.streetLampTypes)
            streetLampTypes.add(getStreetLampItem(streetLampType));

        setSliderClickEvents(RoadFlag.LANE_COUNT, LANE_COUNT_SLOT, 1, 10);
        setSliderClickEvents(RoadFlag.LANE_WIDTH, LANE_WIDTH_SLOT, 1, 30);
        setSliderClickEvents(RoadFlag.SIDEWALK_WIDTH, SIDEWALK_WIDTH_SLOT, 1, 30);
        setSliderClickEvents(RoadFlag.STREET_LAMP_TYPE, STREET_LAMP_TYPE_SLOT, 5, 500);

        setChoiceClickEvents(RoadFlag.MARKING_MATERIAL, MARKINGS_MATERIAL_SLOT, "Choose a Line Marking Block", MenuItems.getSolidBlocks());
        setChoiceClickEvents(RoadFlag.ROAD_SLAB_COLOR, ROAD_SLAB_SLOT, "Choose a Road Elevation Slab", MenuItems.getSlabs());
        setChoiceClickEvents(RoadFlag.SIDEWALK_SLAB_COLOR, SIDEWALK_SLAB_SLOT, "Choose a Sidewalk Elevation Slab", MenuItems.getSlabs());
        setChoiceClickEvents(RoadFlag.STREET_LAMP_TYPE, STREET_LAMP_TYPE_SLOT, "Choose a Street Lamp Type", streetLampTypes);

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
                .pattern("111111111")
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

                if(!(settings instanceof RoadSettings))
                    return;

                RoadSettings roadSettings = (RoadSettings) settings;
                roadSettings.setValue(roadFlag, value - 1);

                clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                reloadMenuAsync();
            }else{
                clickPlayer.playSound(clickPlayer.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
            }
        });

        // Set click event for next page item
        getMenu().getSlot(slot + 1).setClickHandler((clickPlayer, clickInformation) -> {
            int value = (int) road.getPlayerSettings().get(clickPlayer.getUniqueId()).getValues().get(roadFlag);

            if (value < maxValue) {
                Settings settings = road.getPlayerSettings().get(clickPlayer.getUniqueId());

                if(!(settings instanceof RoadSettings))
                    return;

                RoadSettings roadSettings = (RoadSettings) settings;
                roadSettings.setValue(roadFlag, value + 1);

                clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                reloadMenuAsync();
            }else{
                clickPlayer.playSound(clickPlayer.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);

            }
        });
    }


    protected void setChoiceClickEvents(RoadFlag roadFlag, int slot, String choiceInvName, List<ItemStack> choices){
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

        if(!(settings instanceof RoadSettings))
            return;

        RoadSettings roadSettings = (RoadSettings) settings;
        roadSettings.setValue(roadFlag, "OFF");

        clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
        reloadMenuAsync();
    }

    protected ItemStack getStreetLampItem(String id){
        return Item.create(XMaterial.SEA_LANTERN.parseMaterial(), "§eStreet Lamp #" + id, ListUtil.createList("§7Click to select this street lamp type."));
    }
}
