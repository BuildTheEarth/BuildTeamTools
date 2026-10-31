package net.buildtheearth.buildteamtools.modules.stats.menu;

import com.alpsbte.alpslib.utils.item.Item;
import com.cryptomorin.xseries.XMaterial;
import net.buildtheearth.buildteamtools.i18n.MessageKeys;
import net.buildtheearth.buildteamtools.i18n.Messages;
import net.buildtheearth.buildteamtools.modules.stats.StatsModule;
import net.buildtheearth.buildteamtools.modules.stats.model.StatsPlayer;
import net.buildtheearth.buildteamtools.modules.stats.model.StatsServer;
import net.buildtheearth.buildteamtools.utils.ItemComponents;
import net.buildtheearth.buildteamtools.utils.ListUtil;
import net.buildtheearth.buildteamtools.utils.MenuItems;
import net.buildtheearth.buildteamtools.utils.heads.HeadFactory;
import net.buildtheearth.buildteamtools.utils.heads.HeadTexture;
import net.buildtheearth.buildteamtools.utils.menus.AbstractMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class StatsMenu extends AbstractMenu {

    private final ItemStack GLOBAL_HEAD;
    private final ItemStack ACHIEVEMENTS_HEAD;
    private final ItemStack PLAYER_HEAD;
    private final ItemStack TEAM_HEAD;


    public StatsMenu(Player p) {
        super(4, Messages.component(MessageKeys.Stats.TITLE), p, false);
        GLOBAL_HEAD = HeadFactory.head(HeadTexture.EARTH,
                Messages.render(p, MessageKeys.Stats.GLOBAL_NAME),
                ListUtil.grayComponents("error"));
        ACHIEVEMENTS_HEAD = HeadFactory.head(HeadTexture.GOLDEN_CUP,
                Messages.render(p, MessageKeys.Stats.ACHIEVEMENTS_NAME),
                ListUtil.grayComponents("error"));
        PLAYER_HEAD = ItemComponents.named(
                Item.createPlayerHead(" ", p.getName()),
                Messages.render(p, MessageKeys.Stats.PERSONAL_NAME));
        TEAM_HEAD = ItemComponents.of(Objects.requireNonNull(XMaterial.STONE.get()),
                Messages.render(p, MessageKeys.Stats.TEAM_PLACEHOLDER),
                ListUtil.grayComponents("placeholder")); //TODO GET HEAD BASED ON COUNTRY/TEAM
        reloadMenuAsync();
    }

    /**
     * Places items synchronously in the menu and opens it afterwards
     * NOTE: This method gets called before class is loaded!
     */
    @Override
    protected void setPreviewItems() {
        setupLores();

        byte playerHeadSlot = 4;
        byte teamHeadSlot = 20;
        byte globalHeadSlot = 22;
        byte achievementHeadSlot = 24;

        getMenu().getSlot(playerHeadSlot).setItem(PLAYER_HEAD);
        getMenu().getSlot(teamHeadSlot).setItem(TEAM_HEAD);
        getMenu().getSlot(globalHeadSlot).setItem(GLOBAL_HEAD);
        getMenu().getSlot(achievementHeadSlot).setItem(ACHIEVEMENTS_HEAD);
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
                .pattern(BinaryMask.FULL_PATTERN)
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

        List<Component> playerLore = new ArrayList<>();

        for (Object key : playerStats.keySet()) {
            if (key instanceof UUID) continue;

            Object value = playerStats.get(key);
            playerLore.add(Component.text(key + ": " + value));
        }
        ItemComponents.lore(PLAYER_HEAD, playerLore);

        // Set Team Head Lore

        StatsServer statsServer = statsModule.getStatsServer();
        JSONObject serverStats = statsServer.toJSON();

        List<Component> serverLore = new ArrayList<>();

        for (Object key : serverStats.keySet()) {
            Object value = serverStats.get(key);
            serverLore.add(Component.text(key + ": " + value));
        }

        ItemComponents.lore(TEAM_HEAD, serverLore);

        // Set Global Head Lore

        //TODO: HOW TO GET GLOBAL STATS?

        ItemComponents.lore(GLOBAL_HEAD, ListUtil.grayComponents("", "", ""));

        // Set Achievements Head Lore

        //TODO WAIT UNTIL ACHIEVEMENTS HAVE MORE PROGRESS

        ItemComponents.lore(ACHIEVEMENTS_HEAD, ListUtil.grayComponents("", "", ""));
    }
}
