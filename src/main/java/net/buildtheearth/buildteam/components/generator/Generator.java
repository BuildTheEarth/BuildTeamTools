package net.buildtheearth.buildteam.components.generator;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.regions.ConvexPolyhedralRegion;
import com.sk89q.worldedit.regions.Region;
import lombok.Getter;
import net.buildtheearth.buildteam.BuildTeamTools;
import net.buildtheearth.buildteam.components.generator.house.House;
import net.buildtheearth.buildteam.components.generator.rail.Rail;
import net.buildtheearth.buildteam.components.generator.road.Road;
import net.buildtheearth.utils.Item;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Generator {

    public static String WIKI_PAGE = "https://github.com/BuildTheEarth/BuildTeamTools/wiki/Generator";
    public static String INSTALL_WIKI = "https://github.com/BuildTheEarth/BuildTeamTools/wiki/Installation";

    private static HashMap<UUID, History> playerHistory = new HashMap<>();

    @Getter
    private List<Command> commands = new ArrayList<>();

    @Getter
    private House house;
    @Getter
    private Road road;
    @Getter
    private Rail rail;

    public Generator(){
        house = new House();
        road = new Road();
        rail = new Rail();
    }

    public void tick(){
        if(commands.size() == 0)
            return;

        if(commands.get(0).getCommands().size() == 0){
            commands.remove(0);
            return;
        }

        for(int i = 1; i < commands.size(); i++) {
            boolean isInQueue = false;

            for(int j = i-1; j > 0; j--)
                if(commands.get(i).getPlayer().getUniqueId().equals(commands.get(j).getPlayer().getUniqueId()))
                    isInQueue = true;

            if(commands.get(0).getPlayer().getUniqueId().equals(commands.get(i).getPlayer().getUniqueId()))
               isInQueue = true;

            if(isInQueue)
                continue;

            commands.get(i).getPlayer().sendActionBar("§c§lOther Generation in Progress. Position: §e" + i + "/" + commands.size() + " (" + commands.get(0).getPercentage() + "%)");
        }

        commands.get(0).tick();
    }


    public static Region getWorldEditSelection(Player p){
        Region plotRegion;

        try {
            plotRegion = Objects.requireNonNull(WorldEdit.getInstance().getSessionManager().findByName(p.getName())).getSelection(
                    Objects.requireNonNull(WorldEdit.getInstance().getSessionManager().findByName(p.getName())).getSelectionWorld());
        } catch (NullPointerException | IncompleteRegionException ex) {
            return null;
        }

        return plotRegion;
    }

    public static History getPlayerHistory(Player p){
        if(!playerHistory.containsKey(p.getUniqueId()))
            playerHistory.put(p.getUniqueId(), new History(p));

        return playerHistory.get(p.getUniqueId());
    }

    public static void sendMoreInfo(Player p){
        p.sendMessage(" ");
        p.sendMessage("§cFor more information take a look here:");
        p.sendMessage("§c" + WIKI_PAGE);
    }

    public static String[] convertArgsToFlags(String[] args){
        String argsString = " " + StringUtils.join(Arrays.copyOfRange(args, 1, args.length), " ");
        String[] argsArray = argsString.split(" -");
        String[] flagsArray = Arrays.copyOfRange(argsArray, 1, argsArray.length);

        return flagsArray;
    }

    public static String[] convertToFlagAndValue(String flagAndValue, Player p){
        String[] values = flagAndValue.split(" ");
        String flagName = values[0];
        String flagValue;

        try {
            flagValue = StringUtils.join(Arrays.copyOfRange(values, 1, values.length), " ");
        } catch (ArrayIndexOutOfBoundsException e) {
            p.sendMessage("§cInvalid flag value: -" + flagName + "§n§c ???");
            return null;
        }

        return new String[]{flagName, flagValue};
    }

    public static Block[][][] analyzeRegion(Player p, World world) {
        // Get WorldEdit selection of player
        Region polyRegion = Generator.getWorldEditSelection(p);

        Block[][][] blocks = new Block[polyRegion.getWidth()][polyRegion.getHeight()][polyRegion.getLength()];

        for (int i = polyRegion.getMinimumPoint().getBlockX(); i <= polyRegion.getMaximumPoint().getBlockX(); i++)
            for (int j = polyRegion.getMinimumPoint().getBlockY(); j <= polyRegion.getMaximumPoint().getBlockY(); j++)
                for (int k = polyRegion.getMinimumPoint().getBlockZ(); k <= polyRegion.getMaximumPoint().getBlockZ(); k++)
                    if (polyRegion.contains(new Vector(i, j, k))) {
                        Block block = world.getBlockAt(i, j, k);
                        blocks[i - polyRegion.getMinimumPoint().getBlockX()][j - polyRegion.getMinimumPoint().getBlockY()][k - polyRegion.getMinimumPoint().getBlockZ()] = block;
                    }

        return blocks;
    }

    /**
     * Checks the maximum height of a polygon region
     * @param blocks List of blocks in polygon region
     * @return Maximum height of polygon region
     */
    public static int getMaxHeight(Block[][][] blocks, Material... ignoreMaterials){
        int maxHeight = 0;
        List<Material> ignoreMaterialsList = Arrays.asList(ignoreMaterials);

        for (Block[][] block2D : blocks)
            for (Block[] block1D : block2D)
                for (Block block : block1D)
                    if (block != null &&! ignoreMaterialsList.contains(block.getType()) && block.getType().isSolid() &&! block.isLiquid() && block.getY() > maxHeight)
                        maxHeight = block.getY();

        return maxHeight;
    }

    /**
     * Checks the max height of a polygon region given a certain x and z coordinate
     * @param blocks List of blocks in polygon region
     * @return Maximum height of polygon region
     */
    public static int getMaxHeight(Block[][][] blocks, int x, int z, Material... ignoreMaterials){
        int maxHeight = 0;
        List<Material> ignoreMaterialsList = Arrays.asList(ignoreMaterials);

        for (Block[][] block2D : blocks)
            for (Block[] block1D : block2D)
                for (Block block : block1D)
                    if (block != null && block.getX() == x && block.getZ() == z && block.getY() > maxHeight &&! ignoreMaterialsList.contains(block.getType()) && block.getType().isSolid() &&! block.isLiquid())
                        maxHeight = block.getY();

        return maxHeight;
    }

    /**
     * Checks if polygon region contains a block of a certain type
     * @param blocks List of blocks in polygon region
     * @param material Material to check for (e.g. Material.WALL_SIGN)
     * @param data Data value of material to check for (0-15)
     * @return true if polygon region contains a sign, false otherwise
     */
    public static boolean containsBlock(Block[][][] blocks, Material material, byte data){
        for (Block[][] block2D : blocks)
            for (Block[] block1D : block2D)
                for (Block block : block1D)
                    if (block != null && block.getType() == material && block.getData() == data)
                        return true;

        return false;
    }

    public static boolean checkIfWorldEditIsInstalled(Player p){
        // Check if WorldEdit is enabled
        if(!BuildTeamTools.DependencyManager.isWorldEditEnabled()){
            p.sendMessage("§cPlease install WorldEdit to use this tool. You can ask the server administrator to install it.");
            p.sendMessage(" ");
            p.sendMessage("§cFor more installation help, please see the wiki:");
            p.sendMessage("§c" + INSTALL_WIKI);
            sendMoreInfo(p);
            return false;
        }
        return true;
    }

    public static boolean checkForWorldEditSelection(Player p){
        // Get WorldEdit selection of player
        Region polyRegion = Generator.getWorldEditSelection(p);

        if(polyRegion == null){
            p.sendMessage("§cPlease make a WorldEdit Selection first.");
            p.closeInventory();
            p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
            sendMoreInfo(p);
            return false;
        }

        return true;
    }

    public static boolean checkForConvexSelection(Player p){
        // Get WorldEdit selection of player
        Region polyRegion = Generator.getWorldEditSelection(p);

        if(!(polyRegion instanceof ConvexPolyhedralRegion)){
            p.sendMessage("§cPlease make a WorldEdit Convex Selection first (//sel convex).");
            p.closeInventory();
            p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
            sendMoreInfo(p);
            return false;
        }

        return true;
    }

    public static boolean checkForBrickOutline(Block[][][] blocks, Player p){
        if(!containsBlock(blocks, Material.BRICK, (byte) 0)){
            p.sendMessage("§cPlease make a selection around an outline.");
            p.closeInventory();
            p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
            sendMoreInfo(p);

            return false;
        }

        return true;
    }

    public static boolean checkForWoolBlock(Block[][][] blocks, Player p){
        if(!containsBlock(blocks, Material.WOOL, (byte) 4)){
            p.sendMessage("§cPlease place a yellow wool block inside the outline.");
            p.closeInventory();
            p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
            sendMoreInfo(p);

            ItemStack yellowWool = Item.create(Material.WOOL, null, (short) 4, null);
            if(!p.getInventory().contains(yellowWool)) {
                p.getInventory().setItem(4, yellowWool);
                p.playSound(p.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);
            }

            return false;
        }
        return true;
    }
}
