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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        getMenu().getSlot(4).setItem(playerHeadWithLore());
        getMenu().getSlot(20).setItem(teamHeadWithLore());
        getMenu().getSlot(22).setItem(ItemComponents.lore(GLOBAL_HEAD, ListUtil.grayComponents("", "", "")));
        getMenu().getSlot(24).setItem(ItemComponents.lore(ACHIEVEMENTS_HEAD, ListUtil.grayComponents("", "", "")));
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

    private ItemStack playerHeadWithLore() {
        StatsPlayer statsPlayer = StatsModule.getInstance().getStatsPlayer(getMenuPlayer().getUniqueId());
        JSONObject playerStats = statsPlayer.toJSON();

        List<Component> playerLore = new ArrayList<>();
        for (String key : playerStats.keySet()) {
            if ("UUID".equals(key)) {
                continue;
            }
            playerLore.add(Component.text(key + ": " + playerStats.get(key)));
        }
        return ItemComponents.lore(PLAYER_HEAD, playerLore);
    }

    private ItemStack teamHeadWithLore() {
        StatsServer statsServer = StatsModule.getInstance().getStatsServer();
        JSONObject serverStats = statsServer.toJSON();

        List<Component> serverLore = new ArrayList<>();
        for (String key : serverStats.keySet()) {
            serverLore.add(Component.text(key + ": " + serverStats.get(key)));
        }
        return ItemComponents.lore(TEAM_HEAD, serverLore);
    }
}
