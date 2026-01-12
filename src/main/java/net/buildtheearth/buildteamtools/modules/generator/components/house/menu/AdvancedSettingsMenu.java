package net.buildtheearth.buildteamtools.modules.generator.components.house.menu;

import net.buildtheearth.buildteamtools.modules.generator.GeneratorModule;
import net.buildtheearth.buildteamtools.modules.generator.components.house.House;
import net.buildtheearth.buildteamtools.modules.generator.components.house.HouseFlag;
import net.buildtheearth.buildteamtools.modules.generator.components.house.HouseSettings;
import net.buildtheearth.buildteamtools.modules.generator.model.Settings;
import net.buildtheearth.buildteamtools.utils.CustomHeads;
import net.buildtheearth.buildteamtools.utils.MenuItems;
import net.buildtheearth.buildteamtools.utils.menus.AbstractMenu;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;

import java.util.UUID;

public class AdvancedSettingsMenu extends AbstractMenu {

    public static final String ADVANCED_SETTINGS_INV_NAME = "Adjust some Advanced Settings";

    public static final int FLOOR_COUNT_SLOT = 11;
    public static final int FLOOR_HEIGHT_SLOT = 20;
    public static final int BASE_HEIGHT_SLOT = 29;
    public static final int WINDOW_WIDTH_SLOT = 15;
    public static final int WINDOW_HEIGHT_SLOT = 24;
    public static final int WINDOW_DISTANCE_SLOT = 33;

    public static final int NEXT_ITEM_SLOT = 44;
    public static final int BACK_ITEM_SLOT = 36;

    public int floorCount, floorHeight, baseHeight, windowWidth, windowHeight, windowDistance;

    public AdvancedSettingsMenu(Player player) {
        super(5, ADVANCED_SETTINGS_INV_NAME, player);
    }

    @Override
    protected void setPreviewItems() {
        House house = GeneratorModule.getInstance().getHouse();
        UUID uuid = getMenuPlayer().getUniqueId();

        this.floorCount = (int) house.getPlayerSettings().get(uuid).getValues().get(HouseFlag.FLOOR_COUNT);
        this.floorHeight = (int) house.getPlayerSettings().get(uuid).getValues().get(HouseFlag.FLOOR_HEIGHT);
        this.baseHeight = (int) house.getPlayerSettings().get(uuid).getValues().get(HouseFlag.BASE_HEIGHT);
        this.windowWidth = (int) house.getPlayerSettings().get(uuid).getValues().get(HouseFlag.WINDOW_WIDTH);
        this.windowHeight = (int) house.getPlayerSettings().get(uuid).getValues().get(HouseFlag.WINDOW_HEIGHT);
        this.windowDistance = (int) house.getPlayerSettings().get(uuid).getValues().get(HouseFlag.WINDOW_DISTANCE);

        createCounter(CustomHeads.SliderColor.WHITE, FLOOR_COUNT_SLOT, "Number of Floors", floorCount, 1, 10, "Floors");
        createCounter(CustomHeads.SliderColor.LIGHT_GRAY, FLOOR_HEIGHT_SLOT, "Floors Height", floorHeight, 1, 10, "Blocks");
        createCounter(CustomHeads.SliderColor.WHITE, BASE_HEIGHT_SLOT, "Basement Height", baseHeight, 0, 10, "Blocks");
        createCounter(CustomHeads.SliderColor.WHITE, WINDOW_WIDTH_SLOT, "Window Width", windowWidth, 1, 5, "Blocks");
        createCounter(CustomHeads.SliderColor.LIGHT_GRAY, WINDOW_HEIGHT_SLOT, "Window Height", windowHeight, 1, 5, "Blocks");
        createCounter(CustomHeads.SliderColor.WHITE, WINDOW_DISTANCE_SLOT, "Window Distance", windowDistance, 1, 6, "Blocks");


        getMenu().getSlot(NEXT_ITEM_SLOT).setItem(CustomHeads.getCheckmarkItem("§eNext"));
        setBackItem(BACK_ITEM_SLOT, new BaseColorMenu(getMenuPlayer(), false));

        super.setPreviewItems();
    }

    @Override
    protected void setMenuItemsAsync() {}

    @Override
    protected void setItemClickEventsAsync() {
        setSliderClickEvents(HouseFlag.FLOOR_COUNT, FLOOR_COUNT_SLOT,  1, 10);
        setSliderClickEvents(HouseFlag.FLOOR_HEIGHT, FLOOR_HEIGHT_SLOT,  1, 10);
        setSliderClickEvents(HouseFlag.BASE_HEIGHT, BASE_HEIGHT_SLOT,  0, 10);
        setSliderClickEvents(HouseFlag.WINDOW_WIDTH, WINDOW_WIDTH_SLOT,  1, 5);
        setSliderClickEvents(HouseFlag.WINDOW_HEIGHT, WINDOW_HEIGHT_SLOT,  1, 5);
        setSliderClickEvents(HouseFlag.WINDOW_DISTANCE, WINDOW_DISTANCE_SLOT,  1, 6);


        // Set click events items
        getMenu().getSlot(NEXT_ITEM_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
            clickPlayer.closeInventory();
            clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);

            GeneratorModule.getInstance().getHouse().generate(clickPlayer);
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

    protected void setSliderClickEvents(HouseFlag houseFlag, int slot, int minValue, int maxValue) {
        House house = GeneratorModule.getInstance().getHouse();

        // Set click event for previous page item
        getMenu().getSlot(slot - 1).setClickHandler((clickPlayer, clickInformation) -> {
            int value = (int) house.getPlayerSettings().get(clickPlayer.getUniqueId()).getValues().get(houseFlag);

            if (value > minValue) {
                Settings settings = house.getPlayerSettings().get(clickPlayer.getUniqueId());

                if(!(settings instanceof HouseSettings))
                    return;

                HouseSettings houseSettings = (HouseSettings) settings;
                houseSettings.setValue(houseFlag, value - 1);

                clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                reloadMenuAsync();
            }else{
                clickPlayer.playSound(clickPlayer.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
            }
        });

        // Set click event for next page item
        getMenu().getSlot(slot + 1).setClickHandler((clickPlayer, clickInformation) -> {
            int value = (int) house.getPlayerSettings().get(clickPlayer.getUniqueId()).getValues().get(houseFlag);

            if (value < maxValue) {
                Settings settings = house.getPlayerSettings().get(clickPlayer.getUniqueId());

                if(!(settings instanceof HouseSettings))
                    return;

                HouseSettings houseSettings = (HouseSettings) settings;
                houseSettings.setValue(houseFlag, value + 1);

                clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                reloadMenuAsync();
            }else{
                clickPlayer.playSound(clickPlayer.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);

            }
        });
    }
}
