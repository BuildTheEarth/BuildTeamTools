package net.buildtheearth.utils;

import net.buildtheearth.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.mask.Mask;
import org.ipvp.canvas.type.ChestMenu;

public abstract class AbstractMenu {

    public static int MAX_CHARS_PER_LINE = 30;
    public static char LINE_BAKER = '\n';



    private final Menu menu;
    private final Player menuPlayer;

    public AbstractMenu(int rows, String title, Player menuPlayer) {
        this(rows, title, menuPlayer, true);
    }

    public AbstractMenu(int rows, String title, Player menuPlayer, boolean reload) {
        this.menuPlayer = menuPlayer;
        this.menu = ChestMenu.builder(rows).title(title).redraw(true).build();

        if (reload) reloadMenuAsync();
    }

    /**
     * Places items asynchronously in the menu after it is opened
     */
    protected abstract void setMenuItemsAsync();

    /**
     * Sets click events for the items placed in the menu async after it is opened
     */
    protected abstract void setItemClickEventsAsync();

    /**
     * Places pre-defined items in the menu before it is opened
     * @return Pre-defined mask
     * @see <a href=https://github.com/IPVP-MC/canvas#masks</a>
     */
    protected abstract Mask getMask();

    /**
     * Places items synchronously in the menu and opens it afterwards
     * NOTE: This method gets called before class is loaded!
     */
    protected void setPreviewItems() {
        if(getMask() != null) getMask().apply(getMenu());
        getMenu().open(getMenuPlayer());
    }

    /**
     * Reloads all menu items and click events in the menu asynchronously
     * {@link #setPreviewItems()}.{@link #setMenuItemsAsync()}.{@link #setItemClickEventsAsync()}
     */
    protected void reloadMenuAsync() {
        setPreviewItems();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
            setMenuItemsAsync();
            setItemClickEventsAsync();
        });
    }

    /**
     * @return Inventory
     */
    protected Menu getMenu() {
        return menu;
    }

    /**
     * @return Inventory player
     */
    protected Player getMenuPlayer() {
        return menuPlayer;
    }
}
