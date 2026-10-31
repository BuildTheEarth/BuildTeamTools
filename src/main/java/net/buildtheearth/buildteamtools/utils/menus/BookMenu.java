package net.buildtheearth.buildteamtools.utils.menus;

import com.alpsbte.alpslib.utils.item.Item;
import com.cryptomorin.xseries.XMaterial;
import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.i18n.MessageKeys;
import net.buildtheearth.buildteamtools.i18n.Messages;
import net.buildtheearth.buildteamtools.utils.ItemComponents;
import net.buildtheearth.buildteamtools.utils.MenuItems;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class BookMenu {

    private static final PlainTextComponentSerializer PLAIN = PlainTextComponentSerializer.plainText();

    private final Player player;

    private ItemStack[] cachedInventory;

    private BukkitTask task;
    private final Component bookTitle;
    private final Component bookAuthor;
    private final List<String[]> bookPages;

    private Consumer<List<String[]>> onPageComplete;

    private final long maxWaitingTime;

    private long time;

    /**
     * Creates a new book menu to edit large amounts of text
     *
     * @param player         The player that is viewing the menu
     * @param bookTitle      The title of the book
     * @param bookAuthor     The author of the book (plain name is fine)
     * @param pages          The pages of the book
     * @param maxWaitingTime The maximum time the player has to edit the book in seconds
     */
    public BookMenu(Player player, Component bookTitle, String bookAuthor, List<String[]> pages, long maxWaitingTime) {
        this(player, bookTitle, Component.text(bookAuthor), pages, maxWaitingTime);
    }

    public BookMenu(Player player, Component bookTitle, Component bookAuthor, List<String[]> pages, long maxWaitingTime) {
        this.player = player;
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.bookPages = pages;
        this.maxWaitingTime = maxWaitingTime;

        giveBookToPlayer();
    }

    private void giveBookToPlayer() {
        this.task = Bukkit.getScheduler().runTaskTimer(BuildTeamTools.getInstance(), () -> {
            checkForBookUpdate();

            if (time >= maxWaitingTime)
                cancel(Messages.component(MessageKeys.Ui.BOOK_TIMEOUT));

            time++;
        }, 20, 20);

        ItemStack item = Item.create(Objects.requireNonNull(XMaterial.WRITABLE_BOOK.get()));
        ItemComponents.displayName(item, bookTitle);
        BookMeta bookMeta = (BookMeta) item.getItemMeta();
        bookMeta.title(bookTitle);
        bookMeta.author(bookAuthor);
        List<Component> pages = new ArrayList<>();
        for (String[] page : bookPages) {
            pages.add(Component.text(String.join("\n", page)));
        }
        bookMeta.addPages(pages.toArray(Component[]::new));
        item.setItemMeta(bookMeta);

        cachedInventory = player.getInventory().getContents();
        player.closeInventory();
        player.getInventory().clear();

        for (int i = 0; i < 9; i++)
            player.getInventory().setItem(i, MenuItems.ITEM_BACKGROUND);

        player.getInventory().setHeldItemSlot(4);
        player.getInventory().setItem(4, item);

        player.sendMessage(Messages.component(MessageKeys.Ui.BOOK_INSTRUCTIONS));
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
    }

    private void checkForBookUpdate() {
        ItemStack item = player.getInventory().getItem(4);

        if (item == null)
            return;

        if (item.getType() != Objects.requireNonNull(XMaterial.WRITABLE_BOOK.get())
                && item.getType() != Objects.requireNonNull(XMaterial.WRITTEN_BOOK.get()))
            return;

        BookMeta meta = (BookMeta) item.getItemMeta();
        List<Component> metaPages = meta.pages();

        boolean hasChanged = false;
        for (int i = 0; i < bookPages.size(); i++) {
            String text = normalizeBookText(String.join("", bookPages.get(i)));
            String textBook = i < metaPages.size() ? normalizeBookText(PLAIN.serialize(metaPages.get(i))) : "";
            if (!text.equals(textBook)) {
                hasChanged = true;
            }
        }

        if (!hasChanged)
            return;

        List<String[]> newPages = new ArrayList<>();
        for (int x = 0; x < bookPages.size(); x++) {
            String text = x < metaPages.size()
                    ? PLAIN.serialize(metaPages.get(x)).replace("<br>", "").replace("\r", "")
                    : "";

            String[] lines = text.split("\n");

            for (int i = 0; i < lines.length; i++)
                if (lines[i].length() > 30)
                    lines[i] = lines[i].replaceAll("(.{30})", "$1\n");

            StringBuilder editedText = new StringBuilder(lines.length == 0 ? "" : lines[0]);
            for (int i = 1; i < lines.length; i++)
                editedText.append("\n").append(lines[i]);

            editedText = new StringBuilder(editedText.toString().replace("\n ", "<br>")
                    .replace("\n", "<br>")
                    .replace("\\n", "<br>")
                    .replace("\\\\n", "<br>"));

            text = editedText.toString();

            newPages.add(text.split("<br>"));
        }

        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
        player.getInventory().clear();

        for (int i = 0; i < cachedInventory.length; i++)
            player.getInventory().setItem(i, cachedInventory[i]);

        if (task != null)
            task.cancel();

        if (onPageComplete != null)
            onPageComplete.accept(newPages);
    }

    private static String normalizeBookText(String text) {
        return text
                .replace("\r", "")
                .replace("\\\\n", "")
                .replace("\\n", "")
                .replace("\n", "")
                .replace("<br>", "");
    }

    public void onComplete(Consumer<List<String[]>> consumer) {
        this.onPageComplete = consumer;
    }

    private void cancel(Component reason) {
        if (task != null)
            task.cancel();
        if (onPageComplete != null)
            onPageComplete.accept(null);

        player.getInventory().clear();
        for (int i = 0; i < cachedInventory.length; i++)
            player.getInventory().setItem(i, cachedInventory[i]);

        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);

        player.sendMessage(reason);
    }
}
