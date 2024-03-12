package net.buildtheearth.modules.generator.components.rail;

import com.sk89q.worldedit.regions.ConvexPolyhedralRegion;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import net.buildtheearth.modules.generator.GeneratorModule;
import net.buildtheearth.modules.generator.model.*;
import net.buildtheearth.modules.generator.utils.GeneratorUtils;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class RailScripts extends Script {

    public RailScripts(Player player, GeneratorComponent generatorComponent) {
        super(player, generatorComponent);

        railScript_v_1_3();
    }

    public void railScript_v_1_3() {
        List<String> commands = new ArrayList<>();
        //HashMap<Flag, String > flags = rail.getPlayerSettings().get(p.getUniqueId()).getValues();

        int xPos = getPlayer().getLocation().getBlockX();
        int zPos = getPlayer().getLocation().getBlockZ();

        int operations = 0;
        getPlayer().chat("/clearhistory");

        int railWidth = 5;


        // TODO START TEMP

        Vector[] minMax = GeneratorUtils.getMinMaxPoints(getRegion());
        GeneratorUtils.createCuboidSelection(getPlayer(), minMax[0], minMax[1]);
        getPlayer().chat("//set redstone_block");

        Block[][][] regionBlocks = GeneratorUtils.analyzeRegion(getPlayer(), getPlayer().getWorld());
        finish(operations, regionBlocks);


        // TODO END TEMP

        /*
        // Get the points of the region
        List<Vector> points = GeneratorUtils.getSelectionPointsFromRegion(getRegion());
        points = GeneratorUtils.populatePoints(points, 5);

        // ----------- PREPARATION 01 ----------
        // Replace all unnecessary blocks with air

        List<Vector> polyRegionLine = new ArrayList<>(points);
        polyRegionLine = GeneratorUtils.extendPolyLine(polyRegionLine);
        List<Vector> polyRegionPoints = GeneratorUtils.shiftPoints(polyRegionLine, railWidth + 2, true);

        // Create a region from the points
        GeneratorUtils.createPolySelection(getPlayer(), polyRegionPoints, null);

        getPlayer().chat("//expand 30 up");
        getPlayer().chat("//expand 10 down");

        // Remove non-solid blocks
        getPlayer().chat("//gmask !#solid");
        getPlayer().chat("//replace 0");
        operations++;

        // Remove all trees and pumpkins
        getPlayer().chat("//gmask");
        getPlayer().chat("//replace leaves,log,pumpkin 0");
        operations++;

        getPlayer().chat("//gmask");


        Block[][][] regionBlocks = GeneratorUtils.analyzeRegion(getPlayer(), getPlayer().getWorld());
        GeneratorUtils.adjustHeight(points, regionBlocks);


        // ----------- RAILWAY ----------

        // Draw the railway curve

        GeneratorUtils.createConvexSelection(commands, points);
        commands.add("//gmask !solid");
        commands.add("//curve 42");
        operations++;
        commands.add("//gmask");


        // Create the railway
        GeneratorUtils.createPolySelection(commands, polyRegionPoints);

        commands.add("//replace \"0 !>42 =queryRel(0,-1,-1,42,-1)||queryRel(0,-1,1,42,-1)\" 145:1");
        operations++;
        commands.add("//replace \"0 !>42 =queryRel(-1,-1,0,42,-1)||queryRel(1,-1,0,42,-1)\" 145:0");
        operations++;

        commands.add("//gmask =(sqrt((x-(" + xPos + "))^2+(z-(" + zPos + "))^2)%3)-2");
        commands.add("//replace \"0 =queryRel(0,0,1,145,-1)||queryRel(0,0,-1,145,-1)||queryRel(1,0,0,145,-1)||queryRel(-1,0,0,145,-1)\" 44:0");
        operations++;
        commands.add("//replace \"!145 !0 <145\" 43:0");
        operations++;
        commands.add("//gmask");
        commands.add("//replace 42 2");
        operations++;

        commands.add("//gmask");

        // Depending on the selection type, the selection needs to be restored correctly
        if(getRegion() instanceof Polygonal2DRegion || getRegion() instanceof ConvexPolyhedralRegion)
            GeneratorUtils.createConvexSelection(commands, points);
        else if(getRegion() instanceof CuboidRegion){
            CuboidRegion cuboidRegion = (CuboidRegion) getRegion();
            Vector pos1 = new Vector(cuboidRegion.getPos1().getX(), cuboidRegion.getPos1().getY(), cuboidRegion.getPos1().getZ());
            Vector pos2 = new Vector(cuboidRegion.getPos2().getX(), cuboidRegion.getPos2().getY(), cuboidRegion.getPos2().getZ());
            GeneratorUtils.createCuboidSelection(commands, pos1, pos2);
        }

        GeneratorModule.getInstance().getGeneratorCommands().add(new Command(getPlayer(), getGeneratorComponent(), commands, operations, regionBlocks));
        GeneratorModule.getInstance().getPlayerHistory(getPlayer()).addHistoryEntry(new History.HistoryEntry(GeneratorType.RAILWAY, operations));*/
    }
}