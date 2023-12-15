package net.buildtheearth.modules.navigator.menu;

import net.buildtheearth.modules.network.model.Continent;
import net.buildtheearth.modules.utils.menus.AbstractMenu;
import org.bukkit.entity.Player;
import org.ipvp.canvas.mask.Mask;

public class CountrySelectorMenu extends AbstractMenu {
    public CountrySelectorMenu(Continent continent, Player menuPlayer) {
        super(5, continent.getLabel() + " - countries", menuPlayer);
    }

    @Override
    protected void setMenuItemsAsync() {

    }

    @Override
    protected void setItemClickEventsAsync() {

    }

    @Override
    protected Mask getMask() {
        return null;
    }
}
