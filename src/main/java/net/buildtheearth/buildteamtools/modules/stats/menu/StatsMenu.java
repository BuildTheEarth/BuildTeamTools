package net.buildtheearth.buildteamtools.modules.stats.menu;

import com.alpsbte.alpslib.utils.item.Item;
import com.cryptomorin.xseries.XMaterial;
import net.buildtheearth.buildteamtools.modules.stats.StatsModule;
import net.buildtheearth.buildteamtools.modules.stats.model.StatsPlayer;
import net.buildtheearth.buildteamtools.modules.stats.model.StatsServer;
import net.buildtheearth.buildteamtools.utils.CustomHeads;
import net.buildtheearth.buildteamtools.utils.ListUtil;
import net.buildtheearth.buildteamtools.utils.MenuItems;
import net.buildtheearth.buildteamtools.utils.menus.AbstractMenu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

public class StatsMenu extends AbstractMenu {

    private final static ItemStack GLOBAL_HEAD = Item.createCustomHeadBase64(CustomHeads.EARTH, "§eGlobal Statistics", ListUtil.createList("error"));
    private final static ItemStack ACHIEVEMENTS_HEAD = Item.createCustomHeadBase64(CustomHeads.GOLDEN_CUP, "§eAchievements", ListUtil.createList("error"));
    private final byte PLAYER_HEAD_SLOT = 4;
    private final byte TEAM_HEAD_SLOT = 20;
    private final byte GLOBAL_HEAD_SLOT = 22;
    private final byte ACHIEVEMENT_HEAD_SLOT = 24;
    private final ItemStack PLAYER_HEAD = Item.createPlayerHead("&ePersonal Statistics", getMenuPlayer().getName());
    private final ItemStack TEAM_HEAD = Item.create(XMaterial.STONE.parseMaterial(), "Placeholder", ListUtil.createList("placeholder")); //TODO GET HEAD BASED ON COUNTRY/TEAM


    public StatsMenu(Player p) {
        super(4, "Statistics", p);
    }

    /**
     * Places items synchronously in the menu and opens it afterwards
     * NOTE: This method gets called before class is loaded!
     */
    @Override
    protected void setPreviewItems() {
        setupLores();


        getMenu().getSlot(PLAYER_HEAD_SLOT).setItem(PLAYER_HEAD);
        getMenu().getSlot(TEAM_HEAD_SLOT).setItem(TEAM_HEAD);
        getMenu().getSlot(GLOBAL_HEAD_SLOT).setItem(GLOBAL_HEAD);
        getMenu().getSlot(ACHIEVEMENT_HEAD_SLOT).setItem(ACHIEVEMENTS_HEAD);
        super.setPreviewItems();
    }

    /**
     * Places items asynchronously in the menu after it is opened
     */
    @Override
    protected void setMenuItemsAsync() {

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
                .item(MenuItems.ITEM_BACKGROUND)
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
        StatsModule statsModule = StatsModule.getInstance();


        // Set Player Head Lore

        StatsPlayer statsPlayer = statsModule.getStatsPlayer(getMenuPlayer().getUniqueId());
        JSONObject playerStats = statsPlayer.toJSON();

        ArrayList<String> playerLore = new ArrayList<>();

        for (Object key : playerStats.keySet()) {
            if (key instanceof UUID) continue;

            Object value = playerStats.get(key);
            playerLore.add(key + ": " + value);
        }
        PLAYER_HEAD.setLore(playerLore);

        // Set Team Head Lore

        StatsServer statsServer = statsModule.getStatsServer();
        JSONObject serverStats = statsServer.toJSON();

        ArrayList<String> serverLore = new ArrayList<>();

        for (Object key : serverStats.keySet()) {
            Object value = serverStats.get(key);
            serverLore.add(key + ": " + value);
        }

        TEAM_HEAD.setLore(serverLore);

        // Set Global Head Lore

        //TODO: HOW TO GET GLOBAL STATS?

        GLOBAL_HEAD.setLore(ListUtil.createList(
                "",
                "",
                ""
        ));

        // Set Achievements Head Lore

        //TODO WAIT UNTIL ACHIEVEMENTS HAVE MORE PROGRESS

        ACHIEVEMENTS_HEAD.setLore(ListUtil.createList(
                "",
                "",
                ""
        ));
    }
}
