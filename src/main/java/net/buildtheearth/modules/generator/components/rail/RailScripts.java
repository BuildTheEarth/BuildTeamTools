package net.buildtheearth.modules.generator.components.rail;

import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.ConvexPolyhedralRegion;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import net.buildtheearth.modules.generator.GeneratorModule;
import net.buildtheearth.modules.generator.model.Command;
import net.buildtheearth.modules.generator.model.GeneratorType;
import net.buildtheearth.modules.generator.model.History;
import net.buildtheearth.modules.generator.utils.GeneratorUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class RailScripts {

    public static void railScript_v_1_3(Player p, Rail rail, Region region) {
        List<String> commands = new ArrayList<>();
        //HashMap<Flag, String > flags = rail.getPlayerSettings().get(p.getUniqueId()).getValues();

        int xPos = p.getLocation().getBlockX();
        int zPos = p.getLocation().getBlockZ();

        int operations = 0;
        p.chat("/clearhistory");

        int railWidth = 5;

        // Get the points of the region
        List<Vector> points = GeneratorUtils.getSelectionPointsFromRegion(region);
        points = GeneratorUtils.populatePoints(points, 5);

        // ----------- PREPARATION 01 ----------
        // Replace all unnecessary blocks with air

        List<Vector> polyRegionLine = new ArrayList<>(points);
        polyRegionLine = GeneratorUtils.extendPolyLine(polyRegionLine);
        List<Vector> polyRegionPoints = GeneratorUtils.shiftPoints(polyRegionLine, railWidth + 2, true);

        // Create a region from the points
        GeneratorUtils.createPolySelection(p, polyRegionPoints, null);

        p.chat("//expand 30 up");
        p.chat("//expand 10 down");

        // Remove non-solid blocks
        p.chat("//gmask !#solid");
        p.chat("//replace 0");
        operations++;

        // Remove all trees and pumpkins
        p.chat("//gmask");
        p.chat("//replace leaves,log,pumpkin 0");
        operations++;

        p.chat("//gmask");


        Block[][][] regionBlocks = GeneratorUtils.analyzeRegion(p, p.getWorld());
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
        if(region instanceof Polygonal2DRegion || region instanceof ConvexPolyhedralRegion)
            GeneratorUtils.createConvexSelection(commands, points);
        else if(region instanceof CuboidRegion){
            CuboidRegion cuboidRegion = (CuboidRegion) region;
            Vector pos1 = new Vector(cuboidRegion.getPos1().getX(), cuboidRegion.getPos1().getY(), cuboidRegion.getPos1().getZ());
            Vector pos2 = new Vector(cuboidRegion.getPos2().getX(), cuboidRegion.getPos2().getY(), cuboidRegion.getPos2().getZ());
            GeneratorUtils.createCuboidSelection(commands, pos1, pos2);
        }

        GeneratorModule.getInstance().getGeneratorCommands().add(new Command(p, rail, commands, operations, regionBlocks));
        GeneratorModule.getInstance().getPlayerHistory(p).addHistoryEntry(new History.HistoryEntry(GeneratorType.RAILWAY, operations));
    }
}