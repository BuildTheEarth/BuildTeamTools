package net.buildtheearth.buildteam.components.generator;

import com.sk89q.worldedit.regions.Region;
import net.buildtheearth.buildteam.components.generator.house.House;
import net.buildtheearth.buildteam.components.generator.house.HouseSettings;
import net.buildtheearth.buildteam.components.generator.house.WallColorMenu;
import net.buildtheearth.utils.AbstractMenu;
import net.buildtheearth.utils.Item;
import net.buildtheearth.utils.Liste;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;
import java.util.ArrayList;

public class GeneratorMenu extends AbstractMenu {

    public static String GENERATOR_INV_NAME = "What do you want to generate?";

    public static int HOUSE_ITEM_SLOT = 13;



    public GeneratorMenu(Player player) {
        super(3, GENERATOR_INV_NAME, player);
    }

    @Override
    protected void setPreviewItems() {

        ArrayList<String> houseLore = Liste.createList("",
                "§eDescription:",
                "Create building shells",
                "automatically with this",
                "generator",
                "",
                "§eFeatures:",
                "- 4 House Types",
                "- 3 Roof Types",
                "- Custom Wall Color",
                "- Custom Windows",
                "",
                "§8Leftclick to generate",
                "§8Rightclick for Tutorial");

        ItemStack houseItem = Item.create(Material.BIRCH_DOOR_ITEM, "§cGenerate House", houseLore);

        // Set navigator item
        getMenu().getSlot(HOUSE_ITEM_SLOT).setItem(houseItem);

        super.setPreviewItems();
    }

    @Override
    protected void setMenuItemsAsync() {}

    @Override
    protected void setItemClickEventsAsync() {
        // Set click event for house item
        getMenu().getSlot(HOUSE_ITEM_SLOT).setClickHandler(((clickPlayer, clickInformation) -> {
            if(!checkPlayer(clickPlayer))
                return;

            House.playerHouseSettings.remove(clickPlayer.getUniqueId());
            House.playerHouseSettings.put(clickPlayer.getUniqueId(), new HouseSettings(clickPlayer));

            clickPlayer.closeInventory();
            clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
            new WallColorMenu(clickPlayer);
        }));
    }

    @Override
    protected Mask getMask() {
        return BinaryMask.builder(getMenu())
                .item(Item.create(Material.STAINED_GLASS_PANE, " ", (short)15, null))
                .pattern("111111111")
                .pattern("111101111")
                .pattern("111111111")
                .build();
    }

    public boolean checkPlayer(Player p){
        // Get WorldEdit selection of player
        Region plotRegion = Generator.getWorldEditSelction(p);

        if(plotRegion == null){
            p.sendMessage("§cPlease make a WorldEdit Selection first.");
            p.closeInventory();
            p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
            House.sendMoreInfo(p);
            return false;
        }

        if(!House.containsBlock(plotRegion, p.getWorld(), Material.BRICK, (byte) 0)){
            p.sendMessage("§cPlease make a selection around an outline.");
            p.closeInventory();
            p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
            House.sendMoreInfo(p);

            return false;
        }

        if(!House.containsBlock(plotRegion, p.getWorld(), Material.WOOL, (byte) 4)){
            p.sendMessage("§cPlease place a yellow wool block inside the outline.");
            p.closeInventory();
            p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
            House.sendMoreInfo(p);

            ItemStack yellowWool = Item.create(Material.WOOL, null, (short) 4, null);
            if(!p.getInventory().contains(yellowWool)) {
                p.getInventory().setItem(4, yellowWool);
                p.playSound(p.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);
            }

            return false;
        }


        return true;
    }
}