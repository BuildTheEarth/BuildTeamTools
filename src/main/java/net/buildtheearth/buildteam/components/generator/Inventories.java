package net.buildtheearth.buildteam.components.generator;

import net.buildtheearth.utils.Item;
import net.buildtheearth.utils.Liste;
import net.buildtheearth.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import java.util.*;


public class Inventories implements Listener {

    public static int COLORED_INV_SIZE = 36;
    public static int GENERATOR_INV_SIZE = 27;
    public static int ROOF_TYPE_INV_SIZE = 27;
    public static int FLAT_ROOF_INV_SIZE = 45;
    public static int FLAT_GABLE_ROOF_INV_SIZE = 27;
    public static int GABLE_ROOF_INV_SIZE = 27;

    public static String WALL_BLOCK_INV_NAME = "Choose a Wall Block";
    public static String COLOR_PALETTE_INV_NAME = "Select a Block";
    public static String GENERATOR_INV_NAME = "What do you want to generate?";
    public static String ROOF_TYPE_INV_NAME = "Choose a Roof Type";
    public static String FLAT_ROOF_COLOR_INV_NAME = "Choose a Flat Roof Color";
    public static String FLAT_GABLE_ROOF_COLOR_INV_NAME = "Choose a Flat Roof Color";
    public static String GABLE_ROOF_COLOR_INV_NAME = "Choose a Flat Gable Roof Color";

    public static String[] colorPalateInvNames = {COLOR_PALETTE_INV_NAME, WALL_BLOCK_INV_NAME};
    public static String[] roofColorInvNames = {FLAT_ROOF_COLOR_INV_NAME, FLAT_GABLE_ROOF_COLOR_INV_NAME, GABLE_ROOF_COLOR_INV_NAME};

    public static HashMap<UUID, List<ItemStack>> colorInvSelectedItems = new HashMap<>();
    public static HashMap<UUID, List<ItemStack>> roofInvSelectedItems = new HashMap<>();

    @EventHandler
    public void onClick(InventoryClickEvent e){
        Player p = (Player) e.getWhoClicked();

        if(e.getCurrentItem() == null)
            return;
        if(e.getClickedInventory() == null)
            return;
        if(e.getClickedInventory().getName() == null)
            return;
        if(e.getCurrentItem().getType() == null)
            return;


        String itemName = null;
        if(e.getCurrentItem().getItemMeta() != null)
        if(e.getCurrentItem().getItemMeta().getDisplayName() != null)
            itemName = e.getCurrentItem().getItemMeta().getDisplayName();

        String inventoryName = e.getClickedInventory().getName();
        Material itemType = e.getCurrentItem().getType();

        if(itemType == Material.STAINED_GLASS_PANE) {
            e.setCancelled(true);
            return;
        }

        if(Arrays.asList(colorPalateInvNames).contains(inventoryName)){
            e.setCancelled(true);

            List<ItemStack> list = null;
            if(colorInvSelectedItems.containsKey(p.getUniqueId()))
                list = colorInvSelectedItems.get(p.getUniqueId());

            if(e.getCurrentItem().getEnchantments() != null && e.getCurrentItem().getEnchantments().size() > 0) {
                for (Enchantment enchantment : e.getCurrentItem().getEnchantments().keySet())
                    e.getCurrentItem().removeEnchantment(enchantment);
                e.getCurrentItem().setAmount(1);

                list.remove(contains2(e.getCurrentItem(), list));
            }

            if(itemName != null && itemName.contains("§ePrevious Page"))
                openColoredInventory(p, WALL_BLOCK_INV_NAME, Integer.parseInt(itemName.replace("§ePrevious Page §7- §f", "")), list);
            else if(itemName != null && itemName.contains("§eCurrent Page"))
                openColoredInventory(p, WALL_BLOCK_INV_NAME, Integer.parseInt(itemName.replace("§eCurrent Page §7- §f", "")), list);
            else if(itemName != null && itemName.contains("§eNext Page"))
                openColoredInventory(p, WALL_BLOCK_INV_NAME, Integer.parseInt(itemName.replace("§eNext Page §7- §f", "")), list);
            else if(itemName != null && itemName.equals("§eNext")){
                openRoofTypeInventory(p);
            }else if(colorInvSelectedItems.containsKey(p.getUniqueId())) {
                if(!contains(e.getCurrentItem(), list))
                    list.add(e.getCurrentItem());

                int page = Integer.parseInt(e.getClickedInventory().getItem(COLORED_INV_SIZE - 5).getItemMeta().getDisplayName().replace("§eCurrent Page §7- §f", ""));
                openColoredInventory(p, WALL_BLOCK_INV_NAME, page, list);

            }else
                e.setCancelled(false);


        }else if(inventoryName.equals(GENERATOR_INV_NAME)) {
            e.setCancelled(true);

            if(itemName.contains("Generate House"))
                Inventories.openWallBlockInventory(p);

        }else if(inventoryName.equals(ROOF_TYPE_INV_NAME)){
            e.setCancelled(true);

            if(itemName.contains("Flat Roof"))
                openFlatRoofInventory(p);
            else if(itemName.contains("Flat Gable Roof"))
                openFlatGableRoofInventory(p);
            else if(itemName.contains("Gable Roof"))
                openGableRoofInventory(p);

        }else if(Arrays.asList(roofColorInvNames).contains(inventoryName)){
            e.setCancelled(true);

            if(!roofInvSelectedItems.containsKey(p.getUniqueId()))
                return;


            List<ItemStack> list = roofInvSelectedItems.get(p.getUniqueId());
            if(!contains(e.getCurrentItem(), list))
                list.add(e.getCurrentItem());

            if(e.getCurrentItem().getEnchantments() != null && e.getCurrentItem().getEnchantments().size() > 0) {
                for (Enchantment enchantment : e.getCurrentItem().getEnchantments().keySet())
                    e.getCurrentItem().removeEnchantment(enchantment);
                e.getCurrentItem().setAmount(1);

                list.remove(contains2(e.getCurrentItem(), list));
            }

            if(inventoryName.equals(FLAT_GABLE_ROOF_COLOR_INV_NAME))
                openFlatGableRoofInventory(p);
            else if(inventoryName.equals(GABLE_ROOF_COLOR_INV_NAME))
                openGableRoofInventory(p);
            else if(inventoryName.equals(FLAT_ROOF_COLOR_INV_NAME))
                openFlatRoofInventory(p);


            if(itemName != null && itemName.equals("§eNext"))
                ;
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e){
        if(Arrays.asList(colorPalateInvNames).contains(e.getInventory().getName()))
            colorInvSelectedItems.remove(e.getPlayer().getUniqueId());
    }



    public static void openWallBlockInventory(Player p){
        if(colorInvSelectedItems.containsKey(p.getUniqueId()))
            openColoredInventory(p, WALL_BLOCK_INV_NAME, 1, colorInvSelectedItems.get(p.getUniqueId()));
        else
            openColoredInventory(p, WALL_BLOCK_INV_NAME, 1, new ArrayList<>());
    }

    public static void opeColorPaletteInventory(Player p){
        openColoredInventory(p, COLOR_PALETTE_INV_NAME, 1,null);
    }

    /** Opens a rainbow colored block inventory.
     *
     * @param selectedBlocks:
     *  - Null, if you want to make it possible to pick up the blocks from the inv
     *  - Emtpy list, if you want to make it selectable
     *  - Filled list, if you want to make some blocks to appear selected
     */
    private static void openColoredInventory(Player p, String invName, int page, List<ItemStack> selectedBlocks){
        Inventory inv = createNewInventory(p, invName, COLORED_INV_SIZE);

        colorInvSelectedItems.remove(p.getUniqueId());
        if(selectedBlocks != null)
            colorInvSelectedItems.put(p.getUniqueId(), selectedBlocks);

        int blocksSize = COLORED_INV_SIZE - 9;

        for(int i = (page-1)*blocksSize; i < page*blocksSize; i++)
            if(Utils.BLOCKS_BY_COLOR_1_12.length > i) {
                int index = (i % 3) * 9 + i / 3 - (page - 1) * 9;

                if(contains(Utils.BLOCKS_BY_COLOR_1_12[i], selectedBlocks)) {
                    inv.setItem(index, new Item(Utils.BLOCKS_BY_COLOR_1_12[i]).setAmount(1).addEnchantment(Enchantment.LUCK, 1).hideEnchantments(true).build());
                }else
                    inv.setItem(index, Utils.BLOCKS_BY_COLOR_1_12[i]);
            }


        if(page > 1)
            inv.setItem(COLORED_INV_SIZE - 6, Item.createCustomHeadBase64(Utils.WHITE_ARROW_LEFT, "§ePrevious Page §7- §f" + (page - 1) , null));
        else
            inv.setItem(COLORED_INV_SIZE - 6, Item.createCustomHeadBase64(Utils.WHITE_BLANK, "§8Previous Page", null));


        inv.setItem(COLORED_INV_SIZE - 5,  Utils.getWhiteNumberHead(page,  "§eCurrent Page §7- §f" + (page), null));

        if(page <= Utils.BLOCKS_BY_COLOR_1_12.length/27)
            inv.setItem(COLORED_INV_SIZE - 4, Item.createCustomHeadBase64(Utils.WHITE_ARROW_RIGHT, "§eNext Page §7- §f" + (page + 1) , null));
        else
            inv.setItem(COLORED_INV_SIZE - 4, Item.createCustomHeadBase64(Utils.WHITE_BLANK, "§8Next Page", null));

        if(selectedBlocks != null && selectedBlocks.size() > 0)
        inv.setItem(COLORED_INV_SIZE - 1, Item.createCustomHeadBase64(Utils.CHECKMARK, "§eNext", null));

        updateInv(p, inv, invName);
    }

    public static void openGeneratorInventory(Player p){
        Inventory inv = createNewInventory(p, GENERATOR_INV_NAME, GENERATOR_INV_SIZE);

        inv.setItem(13, Item.create(Material.BIRCH_DOOR_ITEM, "§cGenerate House", Liste.createList("", "§eDescription:", "Create building shells", "automatically with this", "generator", "", "§eFeatures:", "- 4 House Types", "- 3 Roof Types", "- Custom Wall Color", "- Custom Windows", "", "§8Leftclick to generate", "§8Rightclick for Tutorial")));

        updateInv(p, inv, GENERATOR_INV_NAME);
    }

    public static void openRoofTypeInventory(Player p){
        Inventory inv = createNewInventory(p, ROOF_TYPE_INV_NAME, ROOF_TYPE_INV_SIZE);

        inv.setItem(11, Item.create(Material.CARPET, "§bFlat Roof", Liste.createList("", "§eDescription:", "A flat roof for", "skyscrapers using carpets", "or slabs")));
        inv.setItem(13, Item.create(Material.COBBLESTONE_STAIRS, "§bGable Roof", Liste.createList("", "§eDescription:", "A gable roof for", "residential houses using stairs")));
        inv.setItem(15, Item.create(Material.STEP, "§bFlat Gable Roof", (short) 3, Liste.createList("", "§eDescription:", "A flat gable roof for", "residential houses using slabs")));

        updateInv(p, inv, ROOF_TYPE_INV_NAME);
    }

    public static void openFlatRoofInventory(Player p){
        Inventory inv = createNewInventory(p, FLAT_ROOF_COLOR_INV_NAME, FLAT_ROOF_INV_SIZE);

        if(!roofInvSelectedItems.containsKey(p.getUniqueId()))
            roofInvSelectedItems.put(p.getUniqueId(), new ArrayList<>());

        int x = 0;

        for(int i = 0; i <= 15; i++) {
            ItemStack item = Item.create(Material.CARPET,null, (short) i, null);

            if (contains(item, roofInvSelectedItems.get(p.getUniqueId())))
                inv.setItem(x, new Item(item).addEnchantment(Enchantment.LUCK, 1).hideEnchantments(true).build());
            else
                inv.setItem(x, item);
            x++;
        }

        for(ItemStack item : Utils.SLABS) {
            if (contains(item, roofInvSelectedItems.get(p.getUniqueId())))
                inv.setItem(x, new Item(item).addEnchantment(Enchantment.LUCK, 1).hideEnchantments(true).build());
            else
                inv.setItem(x, item);
            x++;
        }

        if(roofInvSelectedItems.get(p.getUniqueId()).size() > 0)
        inv.setItem(FLAT_ROOF_INV_SIZE - 1, Item.createCustomHeadBase64(Utils.CHECKMARK, "§eNext", null));


        updateInv(p, inv, FLAT_ROOF_COLOR_INV_NAME);
    }

    public static void openFlatGableRoofInventory(Player p){
        Inventory inv = createNewInventory(p, FLAT_GABLE_ROOF_COLOR_INV_NAME, FLAT_GABLE_ROOF_INV_SIZE);

        if(!roofInvSelectedItems.containsKey(p.getUniqueId()))
            roofInvSelectedItems.put(p.getUniqueId(), new ArrayList<>());

        int x = 0;
        for(ItemStack item : Utils.SLABS) {
            if (contains(item, roofInvSelectedItems.get(p.getUniqueId())))
                inv.setItem(x, new Item(item).addEnchantment(Enchantment.LUCK, 1).hideEnchantments(true).build());
            else
                inv.setItem(x, item);
            x++;
        }

        if(roofInvSelectedItems.get(p.getUniqueId()).size() > 0)
        inv.setItem(FLAT_GABLE_ROOF_INV_SIZE - 1, Item.createCustomHeadBase64(Utils.CHECKMARK, "§eNext", null));

        updateInv(p, inv, FLAT_GABLE_ROOF_COLOR_INV_NAME);
    }

    public static void openGableRoofInventory(Player p){
        Inventory inv = createNewInventory(p, GABLE_ROOF_COLOR_INV_NAME, GABLE_ROOF_INV_SIZE);

        if(!roofInvSelectedItems.containsKey(p.getUniqueId()))
            roofInvSelectedItems.put(p.getUniqueId(), new ArrayList<>());

        int x = 0;
        for(ItemStack item : Utils.STAIRS) {
            if (contains(item, roofInvSelectedItems.get(p.getUniqueId())))
                inv.setItem(x, new Item(item).addEnchantment(Enchantment.LUCK, 1).hideEnchantments(true).build());
            else
                inv.setItem(x, item);
            x++;
        }

        if(roofInvSelectedItems.get(p.getUniqueId()).size() > 0)
        inv.setItem(GABLE_ROOF_INV_SIZE - 1, Item.createCustomHeadBase64(Utils.CHECKMARK, "§eNext", null));

        updateInv(p, inv, GABLE_ROOF_COLOR_INV_NAME);
    }






    public static Inventory createNewInventory(Player p, String invName, int size){
        Inventory inv =  p.getOpenInventory().getTopInventory();
        if(!p.getOpenInventory().getTitle().equals(invName))
            inv = Bukkit.createInventory(null, size, invName);

        for(int i = 0; i < size; i++)
            inv.setItem(i, Item.create(Material.STAINED_GLASS_PANE, " ", (short)15, null));

        return inv;
    }

    public static void updateInv(Player p, Inventory inv, String invName){
        if(!p.getOpenInventory().getTitle().equals(invName))
            p.openInventory(inv);
        else
            p.updateInventory();
    }

    public static boolean contains(ItemStack item, List<ItemStack> list){
        if(list != null)
        for(ItemStack item2 : list)
            if(item2.getType() == item.getType() && item2.getDurability() == item.getDurability())
                return true;

        return false;
    }

    public static ItemStack contains2(ItemStack item, List<ItemStack> list){
        if(list != null)
            for(ItemStack item2 : list)
                if(item2.getType() == item.getType() && item2.getDurability() == item.getDurability())
                    return item2;

        return null;
    }
}
