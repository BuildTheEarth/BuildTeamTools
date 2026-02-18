package net.buildtheearth.buildteamtools.modules.navigation.components.warps.menu;

import net.buildtheearth.buildteamtools.modules.navigation.NavUtils;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.WarpsComponent;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.model.Warp;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.model.WarpGroup;
import net.buildtheearth.buildteamtools.modules.network.NetworkModule;
import net.buildtheearth.buildteamtools.modules.network.model.BuildTeam;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class WarpGroupSelectionMenu extends WarpGroupMenu {

    private final Warp warp;
    private final boolean alreadyExists;

    /** In this menu the player can select a warp group.
     * This can be used for example to change the warp group of a warp in the {@link WarpGroupMenu}.
     * The menu is child of the {@link WarpGroupMenu}.
     *
     * @param menuPlayer  The player that is viewing the menu.
     * @param buildTeam   The build team that the menu is for.
     * @param warp The warp that is being updated with the selected warp group.
     * @param alreadyExists Whether the warp already exists.
     */
    public WarpGroupSelectionMenu(Player menuPlayer, BuildTeam buildTeam, Warp warp, boolean alreadyExists, boolean autoLoad) {
        super(menuPlayer, buildTeam, alreadyExists, autoLoad);
        this.warp = warp;
        this.alreadyExists = alreadyExists;
    }

    @Override
    protected void setItemClickEventsAsync() {
        getMenu().getSlot(BACK_ITEM_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
            clickPlayer.closeInventory();
            new WarpEditMenu(clickPlayer, warp, alreadyExists, true);
        });
    }

    @Override
    protected List<?> getSource() {
        var source = super.getSource().stream().map(l -> (WarpGroup) l).collect(Collectors.toList());

        var otherWarp = WarpsComponent.getOtherWarpGroup(source);
        if (otherWarp == null) source.add(NavUtils.createOtherWarpGroup(NetworkModule.getInstance().getBuildTeam()));

        return source;
    }

    @Override
    protected void leftClickAction(@NotNull Player clickPlayer, @NotNull WarpGroup warpGroup) {
        clickPlayer.playSound(clickPlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
        warp.setWarpGroup(warpGroup);
        new WarpEditMenu(clickPlayer, warp, alreadyExists, true);    }
}
