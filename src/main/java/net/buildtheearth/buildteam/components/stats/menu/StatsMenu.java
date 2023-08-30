package net.buildtheearth.buildteam.components.stats.menu;

import net.buildtheearth.utils.Item;
import net.buildtheearth.utils.Liste;
import net.buildtheearth.utils.MenuItems;
import net.buildtheearth.utils.menus.AbstractMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;

public class StatsMenu extends AbstractMenu {

    private final byte PLAYER_HEAD_SLOT = 4;

    private final byte TEAM_HEAD_SLOT = 20;
    private final byte GLOBAL_HEAD_SLOT = 22;
    private final byte ACHIEVEMENT_HEAD_SLOT = 24;

    private final static ItemStack GLOBAL_HEAD = Item.createCustomHeadBase64(MenuItems.EARTH, "§eGlobal Statistics", Liste.createList("error"));
    private final static ItemStack ACHIEVEMENTS_HEAD = Item.createCustomHeadBase64(MenuItems.GOLDEN_CUP, "§eAchievements", Liste.createList("error"));

    private final ItemStack PLAYER_HEAD = Item.createPlayerHead("&ePersonal Statistics", getMenuPlayer().getName());
    private final ItemStack TEAM_HEAD = Item.create(Material.STONE, "Placeholder", Liste.createList("placeholder")); //TODO GET HEAD BASED ON COUNTRY/TEAM



    public StatsMenu(Player p) {
        super(4, "Statistics", p);
    }

    /**
     * Places items synchronously in the menu and opens it afterwards
     * NOTE: This method gets called before class is loaded!
     */
    @Override
    protected void setPreviewItems() {
        super.setPreviewItems();
    }


    /**
     * Places items asynchronously in the menu after it is opened
     */
    @Override
    protected void setMenuItemsAsync() {
        setupLores();


        getMenu().getSlot(PLAYER_HEAD_SLOT).setItem(PLAYER_HEAD);
        getMenu().getSlot(TEAM_HEAD_SLOT).setItem(TEAM_HEAD);
        getMenu().getSlot(GLOBAL_HEAD_SLOT).setItem(GLOBAL_HEAD);
        getMenu().getSlot(ACHIEVEMENT_HEAD_SLOT).setItem(ACHIEVEMENTS_HEAD);
    }

    /**
     * Sets click events for the items placed in the menu async after it is opened
     */
    @Override
    protected void setItemClickEventsAsync() {

    }

    /**
     * Places pre-defined items in the menu before it is opened
     *
     * @return Pre-defined mask
     * @see <a href=https://github.com/IPVP-MC/canvas#masks</a>
     */
    @Override
    protected Mask getMask() {
        return BinaryMask.builder(getMenu())
                .item(Item.create(Material.STAINED_GLASS_PANE, " ", (short)15, null))
                .pattern("111101111")
                .pattern("110101011")
                .pattern("111111111")
                .pattern("111101111")
                .build();
    }

    /**
     * Sets up the correct lores for all items
     */
    private void setupLores() {
        PLAYER_HEAD.setLore(Liste.createList(
                "",
                "",
                ""
        ));

        TEAM_HEAD.setLore(Liste.createList(
                "",
                "",
                ""
        ));

        GLOBAL_HEAD.setLore(Liste.createList(
                "",
                "",
                ""
        ));

        ACHIEVEMENTS_HEAD.setLore(Liste.createList(
                "",
                "",
                ""
        ));
    }
}
