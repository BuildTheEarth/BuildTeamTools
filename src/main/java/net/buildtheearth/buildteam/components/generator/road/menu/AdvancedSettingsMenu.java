package net.buildtheearth.buildteam.components.generator.road.menu;

import net.buildtheearth.Main;
import net.buildtheearth.buildteam.components.generator.Settings;
import net.buildtheearth.buildteam.components.generator.road.Road;
import net.buildtheearth.buildteam.components.generator.road.RoadFlag;
import net.buildtheearth.buildteam.components.generator.road.RoadSettings;
import net.buildtheearth.utils.menus.AbstractMenu;
import net.buildtheearth.utils.Item;
import net.buildtheearth.utils.MenuItems;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;

import java.util.List;
import java.util.UUID;

public class AdvancedSettingsMenu extends AbstractMenu {

    public static final String ADVANCED_SETTINGS_INV_NAME = "Adjust some Advanced Settings";

    public static final int LANE_COUNT_SLOT = 11;
    public static final int LANE_WIDTH_SLOT = 20;
    public static final int SIDEWALK_WIDTH_SLOT = 29;
    public static final int MARKINGS_MATERIAL_SLOT = 15;

    public static final int ROAD_SLAB_SLOT = 24;
    public static final int SIDEWALK_SLAB_SLOT = 33;


    public static final int NEXT_ITEM_SLOT = 44;

    private int laneCount, laneWidth, sidewalkWidth;
    private ItemStack markingsMaterial, sidewalkSlab, roadSlab;

    public AdvancedSettingsMenu(Player player) {
        super(5, ADVANCED_SETTINGS_INV_NAME, player);
    }

    @Override
    protected void setPreviewItems() {
        Road road = Main.getBuildTeam().getGenerator().getRoad();
        UUID uuid = getMenuPlayer().getUniqueId();

        this.laneCount = Integer.parseInt(road.getPlayerSettings().get(uuid).getValues().get(RoadFlag.LANE_COUNT));
        this.laneWidth = Integer.parseInt(road.getPlayerSettings().get(uuid).getValues().get(RoadFlag.LANE_WIDTH));
        this.sidewalkWidth = Integer.parseInt(road.getPlayerSettings().get(uuid).getValues().get(RoadFlag.SIDEWALK_WIDTH));

        this.markingsMaterial = Item.fromUniqueMaterialString(road.getPlayerSettings().get(uuid).getValues().get(RoadFlag.MARKING_MATERIAL));
        this.sidewalkSlab = Item.fromUniqueMaterialString(road.getPlayerSettings().get(uuid).getValues().get(RoadFlag.SIDEWALK_SLAB_COLOR));
        this.roadSlab = Item.fromUniqueMaterialString(road.getPlayerSettings().get(uuid).getValues().get(RoadFlag.ROAD_SLAB_COLOR));

        createCounter(MenuItems.SliderColor.WHITE, LANE_COUNT_SLOT, "Number of Lanes", laneCount, 1, 10, "Lanes");
        createCounter(MenuItems.SliderColor.LIGHT_GRAY, LANE_WIDTH_SLOT, "Lane Width", laneWidth, 1, 30, "Blocks");
        createCounter(MenuItems.SliderColor.WHITE, SIDEWALK_WIDTH_SLOT, "Sidewalk Width", sidewalkWidth, 1, 30, "Blocks");

        setColorChoiceItems(MenuItems.SliderColor.WHITE, MARKINGS_MATERIAL_SLOT, "Line Markings Color", markingsMaterial);
        setColorChoiceItems(MenuItems.SliderColor.LIGHT_GRAY, ROAD_SLAB_SLOT, "Road Elevation Slab", roadSlab);
        setColorChoiceItems(MenuItems.SliderColor.WHITE, SIDEWALK_SLAB_SLOT, "Sidewalk Elevation Slab", sidewalkSlab);

        getMenu().getSlot(NEXT_ITEM_SLOT).setItem(MenuItems.getNextItem());

        super.setPreviewItems();
    }

    @Override
    protected void setMenuItemsAsync() {}

    @Override
    protected void setItemClickEventsAsync() {
        setSliderClickEvents(RoadFlag.LANE_COUNT, LANE_COUNT_SLOT,  1, 10);
        setSliderClickEvents(RoadFlag.LANE_WIDTH, LANE_WIDTH_SLOT,  1, 30);
        setSliderClickEvents(RoadFlag.SIDEWALK_WIDTH, SIDEWALK_WIDTH_SLOT,  1, 30);

        setColorChoiceClickEvents(RoadFlag.MARKING_MATERIAL, MARKINGS_MATERIAL_SLOT, "Choose a Line Marking Block", MenuItems.getBlocksByColor());
        setColorChoiceClickEvents(RoadFlag.ROAD_SLAB_COLOR, ROAD_SLAB_SLOT, "Choose a Road Elevation Slab", MenuItems.getSlabs());
        setColorChoiceClickEvents(RoadFlag.SIDEWALK_SLAB_COLOR, SIDEWALK_SLAB_SLOT, "Choose a Sidewalk Elevation Slab", MenuItems.getSlabs());

        // Set click events items
        getMenu().getSlot(NEXT_ITEM_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
            clickPlayer.closeInventory();
            clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);

            Main.getBuildTeam().getGenerator().getRoad().generate(clickPlayer);
        });
    }

    @Override
    protected Mask getMask() {
        return BinaryMask.builder(getMenu())
                .item(Item.create(Material.STAINED_GLASS_PANE, " ", (short)15, null))
                .pattern("111111111")
                .pattern("100010001")
                .pattern("100010001")
                .pattern("100010001")
                .pattern("111111110")
                .build();
    }

    protected void setSliderClickEvents(RoadFlag roadFlag, int slot, int minValue, int maxValue){
        Road road = Main.getBuildTeam().getGenerator().getRoad();

        // Set click event for previous page item
        getMenu().getSlot(slot - 1).setClickHandler((clickPlayer, clickInformation) -> {
            int value = Integer.parseInt(road.getPlayerSettings().get(clickPlayer.getUniqueId()).getValues().get(roadFlag));

            if (value > minValue) {
                Settings settings = road.getPlayerSettings().get(clickPlayer.getUniqueId());

                if(!(settings instanceof RoadSettings))
                    return;

                RoadSettings roadSettings = (RoadSettings) settings;
                roadSettings.setValue(roadFlag, "" + (value - 1));

                clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                reloadMenuAsync();
            }else{
                clickPlayer.playSound(clickPlayer.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
            }
        });

        // Set click event for next page item
        getMenu().getSlot(slot + 1).setClickHandler((clickPlayer, clickInformation) -> {
            int value = Integer.parseInt(road.getPlayerSettings().get(clickPlayer.getUniqueId()).getValues().get(roadFlag));

            if (value < maxValue) {
                Settings settings = road.getPlayerSettings().get(clickPlayer.getUniqueId());

                if(!(settings instanceof RoadSettings))
                    return;

                RoadSettings roadSettings = (RoadSettings) settings;
                roadSettings.setValue(roadFlag, "" + (value + 1));

                clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                reloadMenuAsync();
            }else{
                clickPlayer.playSound(clickPlayer.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);

            }
        });
    }


    protected void setColorChoiceClickEvents(RoadFlag roadFlag, int slot, String choiceInvName, List<ItemStack> choices){
        Road road = Main.getBuildTeam().getGenerator().getRoad();

        // Set click event for X items
        getMenu().getSlot(slot - 1).setClickHandler((clickPlayer, clickInformation) -> turnOffColorChoice(clickPlayer, roadFlag));
        getMenu().getSlot(slot + 1).setClickHandler((clickPlayer, clickInformation) -> turnOffColorChoice(clickPlayer, roadFlag));

        // Set click event for color choice items
        getMenu().getSlot(slot).setClickHandler((clickPlayer, clickInformation) -> {
            clickPlayer.closeInventory();
            clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);

            new AdvancedColorMenu(clickPlayer, roadFlag, choiceInvName, choices);
        });
    }

    protected void turnOffColorChoice(Player clickPlayer, RoadFlag roadFlag){
        Road road = Main.getBuildTeam().getGenerator().getRoad();
        Settings settings = road.getPlayerSettings().get(clickPlayer.getUniqueId());

        if(!(settings instanceof RoadSettings))
            return;

        RoadSettings roadSettings = (RoadSettings) settings;
        roadSettings.setValue(roadFlag, "OFF");

        clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
        reloadMenuAsync();
    }
}
