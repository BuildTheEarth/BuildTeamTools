package net.buildtheearth.modules.utils.menus;

import net.buildtheearth.Main;
import net.buildtheearth.modules.utils.Item;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BookMenu {

    private final Player player;

    private ItemStack[] cachedInventory;

    private BukkitTask task;
    private final String bookTitle;
    private final String bookAuthor;
    private final List<String[]> bookPages;

    private Consumer<List<String[]>> onPageComplete;

    private final long maxWaitingTime;

    private long time;

    /** Creates a new book menu to edit large amounts of text
     *
     * @param player The player that is viewing the menu
     * @param bookTitle The title of the book
     * @param bookAuthor The author of the book
     * @param pages The pages of the book
     * @param maxWaitingTime The maximum time the player has to edit the book in seconds
     */
    public BookMenu(Player player, String bookTitle, String bookAuthor, List<String[]> pages, long maxWaitingTime) {
        this.player = player;
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.bookPages = pages;
        this.maxWaitingTime = maxWaitingTime;

        giveBookToPlayer();
    }

    private void giveBookToPlayer() {
        this.task = Bukkit.getScheduler().runTaskTimer(Main.instance, () -> {
            checkForBookUpdate();

            if(time >= maxWaitingTime)
                cancel("§cYou took too long to edit the book. The book has been closed. Please try again.");

            time++;
        }, 20, 20);

        ItemStack item = Item.create(Material.BOOK_AND_QUILL, bookTitle);
        BookMeta bookMeta = (BookMeta) item.getItemMeta();
        bookMeta.setTitle(bookTitle);
        bookMeta.setAuthor(bookAuthor);
        for(String[] page : bookPages)
            bookMeta.addPage(String.join("\n", page));

        item.setItemMeta(bookMeta);

        cachedInventory = player.getInventory().getContents();
        player.closeInventory();
        player.getInventory().clear();

        for(int i = 0; i < 9; i++)
            player.getInventory().setItem(i, Item.create(Material.STAINED_GLASS_PANE, " ", (short) 15, null));

        player.getInventory().setHeldItemSlot(4);
        player.getInventory().setItem(4, item);

        player.sendMessage("§8§l§m============================================");
        player.sendMessage(" ");
        player.sendMessage("§7Please §eopen the book§7 in your inventory to edit the §etext§7.");
        player.sendMessage(" ");
        player.sendMessage("§8§l§m============================================");
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
    }

    private void checkForBookUpdate(){
        if(player.getInventory().getItem(4) == null)
            return;

        if(player.getInventory().getItem(4).getType() != Material.BOOK_AND_QUILL
        && player.getInventory().getItem(4).getType() != Material.WRITTEN_BOOK)
            return;

        ItemStack item = player.getInventory().getItem(4);
        BookMeta meta = (BookMeta) item.getItemMeta();

        boolean hasChanged = false;
        for(int i = 0; i < bookPages.size(); i++){
            // Join the page array to a string
            String text = String.join("", bookPages.get(i))
                    .replace("§0", "")
                    .replace("\r", "")
                    .replace("\\\\n", "")
                    .replace("\\n", "")
                    .replace("\n", "")
                    .replace("<br>", "");

            String textBook = meta.getPage(i + 1)
                    .replace("§0", "")
                    .replace("\r", "")
                    .replace("\\\\n", "")
                    .replace("\\n", "")
                    .replace("\n", "")
                    .replace("<br>", "");

            if(!text.equals(textBook))
                hasChanged = true;
        }

        if (!hasChanged)
            return;

        List<String[]> newPages = new ArrayList<>();
        for(int x = 0; x < bookPages.size(); x++){
            String text = meta.getPage(x + 1)
                    .replace("<br>", "")
                    .replace("\r", "")
                    .replace("§0", "");

            String[] lines = text.split("\n");

            for(int i = 0; i < lines.length; i++)
                if(lines[i].toCharArray().length > 30)
                    lines[i] = lines[i].replaceAll("(.{30})", "$1\n");

            StringBuilder editedText = new StringBuilder(lines[0]);
            for(int i = 1; i < lines.length; i++)
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

        for(int i = 0; i < cachedInventory.length; i++)
            player.getInventory().setItem(i, cachedInventory[i]);

        if(task != null)
            task.cancel();

        if (onPageComplete != null)
            onPageComplete.accept(newPages);
    }

    public void onComplete(Consumer<List<String[]>> consumer) {
        this.onPageComplete = consumer;
    }

    private void cancel(String reason){
        if(task != null)
            task.cancel();
        if (onPageComplete != null)
            onPageComplete.accept(null);

        player.getInventory().clear();
        for(int i = 0; i < cachedInventory.length; i++)
            player.getInventory().setItem(i, cachedInventory[i]);

        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);

        player.sendMessage(reason);
    }
}
