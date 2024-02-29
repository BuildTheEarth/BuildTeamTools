package net.buildtheearth.modules.navigation.menu;

import net.buildtheearth.BuildTeamTools;
import net.buildtheearth.modules.utils.menus.AbstractMenu;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.ipvp.canvas.mask.Mask;

/** The Tutorials Menu for the BTE universal navigator.<br>
 * <br>
 * Accessed from the main menu, this menu will contain a list of tutorials for the player to view.<br>
 * <br>
 * The Tutorials Menu will utilise the Tutorials API once created and published.
 */
public class TutorialsMenu extends AbstractMenu {

    private static final int BACK_BUTTON_SLOT = 13;

    private static final String inventoryName = "Tutorials Menu";
    private static final FileConfiguration config = BuildTeamTools.getInstance().getConfig();

    public TutorialsMenu(Player player) {
        super(3, inventoryName, player);
    }

    @Override
    protected void setPreviewItems() {
        setBackItem(BACK_BUTTON_SLOT, true);

        super.setPreviewItems();
    }


    @Override
    protected void setMenuItemsAsync() {}

    @Override
    protected void setItemClickEventsAsync() {
        getMenu().getSlot(BACK_BUTTON_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
            clickPlayer.closeInventory();
            new MainMenu(clickPlayer);
        });
    }

    @Override
    protected Mask getMask() {
        return null;
    }


}
