package net.buildtheearth.modules.generator.components.house;

import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.ConvexPolyhedralRegion;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import net.buildtheearth.modules.generator.GeneratorModule;
import net.buildtheearth.modules.generator.model.Command;
import net.buildtheearth.modules.generator.model.Flag;
import net.buildtheearth.modules.generator.model.GeneratorType;
import net.buildtheearth.modules.generator.model.History;
import net.buildtheearth.modules.generator.utils.GeneratorUtils;
import net.buildtheearth.utils.MenuItems;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HouseScripts {

    public static void buildscript_v_1_2(Player p, House house, Region region){
        List<String> commands = new ArrayList<>();
        HashMap<Flag, String> flags = house.getPlayerSettings().get(p.getUniqueId()).getValues();

        String wallColor = flags.get(HouseFlag.WALL_COLOR);
        String roofColor = flags.get(HouseFlag.ROOF_COLOR);
        String baseColor = flags.get(HouseFlag.BASE_COLOR);
        String windowColor = flags.get(HouseFlag.WINDOW_COLOR);
        String balconyColor = flags.get(HouseFlag.BALCONY_COLOR);
        String balconyFenceColor = flags.get(HouseFlag.BALCONY_FENCE_COLOR);
        RoofType roofType = RoofType.byString(flags.get(HouseFlag.ROOF_TYPE));

        int floorCount = Integer.parseInt(flags.get(HouseFlag.FLOOR_COUNT));
        int floorHeight = Integer.parseInt(flags.get(HouseFlag.FLOOR_HEIGHT));
        int baseHeight = Integer.parseInt(flags.get(HouseFlag.BASE_HEIGHT));
        int windowHeight = Integer.parseInt(flags.get(HouseFlag.WINDOW_HEIGHT));
        int windowWidth = Integer.parseInt(flags.get(HouseFlag.WINDOW_WIDTH));
        int windowDistance = Integer.parseInt(flags.get(HouseFlag.WINDOW_DISTANCE));
        int maxRoofHeight = Integer.parseInt(flags.get(HouseFlag.MAX_ROOF_HEIGHT));

        List<BlockVector2D> selectionPoints = new ArrayList<>();
        int minY = region.getMinimumPoint().getBlockY();
        int maxY = region.getMaximumPoint().getBlockY();


        if(region instanceof Polygonal2DRegion){
            Polygonal2DRegion polyRegion = (Polygonal2DRegion) region;
            selectionPoints.addAll(polyRegion.getPoints());

        } else if (region instanceof ConvexPolyhedralRegion) {
            ConvexPolyhedralRegion convexRegion = (ConvexPolyhedralRegion) region;

            for (Vector vector : convexRegion.getVertices())
                selectionPoints.add(new BlockVector2D(vector.getBlockX(), vector.getBlockZ()));

        }else if(region instanceof CuboidRegion){
            CuboidRegion cuboidRegion = (CuboidRegion) region;
            Vector min = cuboidRegion.getMinimumPoint();
            Vector max = cuboidRegion.getMaximumPoint();

            selectionPoints.add(new BlockVector2D(min.getBlockX(), min.getBlockZ()));
            selectionPoints.add(new BlockVector2D(max.getBlockX(), max.getBlockZ()));
        }else{
            p.sendMessage("§c§lERROR: §cRegion type not supported!");
            return;
        }




        int operations = 0;
        p.chat("/clearhistory");

        // Disable the current global mask
        p.chat("//gmask");


        // ----------- PREPARATION 01 ----------
        // Replace all non-solid blocks with air

        p.chat("//expand 10 up");
        p.chat("//expand 10 down");

        p.chat("//gmask !#solid");
        p.chat("//replace 0");
        operations++;

        Block[][][] blocks = GeneratorUtils.analyzeRegion(p, p.getWorld());

        if(blocks == null){
            p.sendMessage("§c§lERROR: §cRegion not readable. Please report this to the developers of the BuildTeamTool plugin.");
            return;
        }

        int highestBlock = GeneratorUtils.getMaxHeight(blocks, Material.LOG, Material.LOG_2, Material.LEAVES, Material.LEAVES_2, Material.WOOL);
        boolean containsRedWool = GeneratorUtils.containsBlock(blocks, Material.WOOL, (byte) 14);
        boolean containsOrangeWool = GeneratorUtils.containsBlock(blocks, Material.WOOL, (byte) 1);


        // ----------- PREPARATION 02 ----------
        // Bring the outline on the same height

        // Set pos1 and pos2
        commands.add("//pos1 " + selectionPoints.get(0).getBlockX() + "," + highestBlock + "," + selectionPoints.get(0).getBlockZ());
        for(int i = 1; i < selectionPoints.size(); i++)
            commands.add("//pos2 " + selectionPoints.get(i).getBlockX() + "," + minY + "," + selectionPoints.get(i).getBlockZ());

        commands.add("//expand 10 down");

        // Replace air with sponge
        commands.add("//replace 0 19");
        operations++;

        // Replace all sponges with bricks that have bricks below them
        commands.add("//gmask >45");

        for(int i = 0; i < 20; i++) {
            commands.add("//replace 19 45");
            operations++;
        }

        // Replace all left sponges with air
        commands.add("//gmask");
        commands.add("//replace 19 0");
        operations++;


        // ----------- PREPARATION 03 ----------
        // Bring the orange, yellow, green, blue and red wool blocks to the same height

        String[] woolColors = {"35:1", "35:4", "35:11", "35:14", "35:5"};

        for(String wool : woolColors){
            // Replace all blocks above the wool
            commands.add("//gmask >" + wool);
            for(int i = 0; i < 20; i++){
                commands.add("//replace 0 " + wool);
                operations++;
            }

            // Select highest yellow wool block and replace it with sponge
            commands.add("//gmask <0");
            commands.add("//replace " + wool + " 19");
            operations++;

            // Replace yellow wool with brick
            commands.add("//gmask");
            commands.add("//replace " + wool + " 45");
            operations++;

            // Replace all sponges with yellow wool
            commands.add("//replace 19 " + wool);
            operations++;
        }

        // ----------- PREPARATION 05 ----------
        // Move blue, red and lime wool one block up and replace block below with brick

        commands.add("//expand 1 up");

        String[] woolColorsNoYellow = {"35:11", "35:14", "35:5"};
        for(String wool : woolColorsNoYellow) {
            commands.add("//gmask =queryRel(1,0,0,45,0)||queryRel(-1,0,0,45,0)||queryRel(0,0,1,45,0)||queryRel(0,0,-1,45,0)||queryRel(1,0,1,45,0)||queryRel(-1,0,1,45,0)||queryRel(1,0,-1,45,0)||queryRel(-1,0,-1,45,0)");
            commands.add("//replace " + wool + " 19");
            operations++;

            for(int i = 0; i < 10; i++){
                commands.add("//gmask =queryRel(1,0,0,19,0)||queryRel(-1,0,0,19,0)||queryRel(0,0,1,19,0)||queryRel(0,0,-1,19,0)||queryRel(1,0,1,19,0)||queryRel(-1,0,1,19,0)||queryRel(1,0,-1,19,0)||queryRel(-1,0,-1,19,0)");
                commands.add("//replace " + wool + " 19");
                operations++;
            }

            commands.add("//gmask");
            commands.add("//replace >19 " + wool);
            operations++;
            commands.add("//replace 19 45");
            operations++;
        }



        // ----------- PREPARATION 06 ----------
        // Replace all bricks underground with grass

        // Replace all bricks with sponge
        commands.add("//replace 45 19");
        operations++;

        // Replace all sponges with bricks that have air or red wool or blue wool or green wool above them
        commands.add("//gmask =(queryRel(0,1,0,0,0)||queryRel(0,1,0,35,11)||queryRel(0,1,0,35,14)||queryRel(0,1,0,35,5))");
        commands.add("//replace 19 45");
        operations++;

        // Disable the global mask
        commands.add("//gmask");

        // Replace all left sponges with grass
        commands.add("//replace 19 2");
        operations++;


        // ----------- PREPARATION 07 ----------
        // Expand the blue wool until it reaches the green wool

        // Select all blocks that are next to blue wool
        commands.add("//gmask =(queryRel(1,0,0,35,11)||queryRel(-1,0,0,35,11)||queryRel(0,0,1,35,11)||queryRel(0,0,-1,35,11)||queryRel(1,0,1,35,11)||queryRel(-1,0,-1,35,11)||queryRel(-1,0,1,35,11)||queryRel(1,0,-1,35,11))&&!(queryRel(1,0,0,35,5)||queryRel(-1,0,0,35,5)||queryRel(0,0,1,35,5)||queryRel(0,0,-1,35,5)||queryRel(1,0,1,35,5)||queryRel(-1,0,-1,35,5)||queryRel(-1,0,1,35,5)||queryRel(1,0,-1,35,5))");

        for(int i = 0; i < 20; i++) {
            // Replace all blocks above lapislazuli with blue wool
            commands.add("//replace >45 35:11");
            operations++;
        }

        // Place the last blue wool between the green and the blue wool
        commands.add("//gmask =queryRel(1,0,0,35,11)||queryRel(-1,0,0,35,11)||queryRel(0,0,1,35,11)||queryRel(0,0,-1,35,11)||queryRel(1,0,1,35,11)||queryRel(-1,0,-1,35,11)||queryRel(-1,0,1,35,11)||queryRel(1,0,-1,35,11)");
        commands.add("//replace >45 35:11");
        operations++;


        // ----------- GROUND ----------

        // Set pos1 and pos2
        commands.add("//pos1 " + selectionPoints.get(0).getBlockX() + "," + maxY + "," + selectionPoints.get(0).getBlockZ());
        for(int i = 1; i < selectionPoints.size(); i++)
            commands.add("//pos2 " + selectionPoints.get(i).getBlockX() + "," + minY + "," + selectionPoints.get(i).getBlockZ());

        // Expand the current selection down by 10 blocks
        commands.add("//expand 10 down");

        int up_expand = 5 + maxRoofHeight + baseHeight + (floorCount * floorHeight);

        // Expand the current selection up by "up_expand" blocks
        commands.add("//expand " + up_expand + " up");

        // Select all blocks around the yellow wool block
        commands.add("//gmask =queryRel(-1,0,0,35,4)||queryRel(1,0,0,35,4)||queryRel(0,0,1,35,4)||queryRel(0,0,-1,35,4)");

        for(int i = 0; i < 20; i++) {
            // Replace all blocks that are not bricks with yellow wool
            commands.add("//replace !45 35:4");
            operations++;
        }


        // Make the outline as thin as possible and fill all inner corners with yellow wool that are too thick
        for(int i = 0; i < 2; i++) {
            commands.add("//gmask =queryRel(-1,0,0,35,4)&&queryRel(0,0,-1,35,4)&&queryRel(0,0,1,45,-1)&&queryRel(1,0,0,45,-1)");
            commands.add("//replace 45 35:4");
            operations++;
            commands.add("//gmask =queryRel(-1,0,0,35,4)&&queryRel(0,0,1,35,4)&&queryRel(0,0,-1,45,-1)&&queryRel(1,0,0,45,-1)");
            commands.add("//replace 45 35:4");
            operations++;
            commands.add("//gmask =queryRel(1,0,0,35,4)&&queryRel(0,0,-1,35,4)&&queryRel(0,0,1,45,-1)&&queryRel(-1,0,0,45,-1)");
            commands.add("//replace 45 35:4");
            operations++;
            commands.add("//gmask =queryRel(1,0,0,35,4)&&queryRel(0,0,1,35,4)&&queryRel(0,0,-1,45,-1)&&queryRel(-1,0,0,45,-1)");
            commands.add("//replace 45 35:4");
            operations++;
            commands.add("//gmask =queryRel(-1,0,0,35,4)&&queryRel(0,0,-1,45,-1)&&queryRel(0,0,1,45,-1)&&queryRel(1,0,0,45,-1)");
            commands.add("//replace 45 35:4");
            operations++;
            commands.add("//gmask =queryRel(1,0,0,35,4)&&queryRel(0,0,1,45,-1)&&queryRel(0,0,-1,45,-1)&&queryRel(-1,0,0,45,-1)");
            commands.add("//replace 45 35:4");
            operations++;
            commands.add("//gmask =queryRel(1,0,0,45,-1)&&queryRel(0,0,-1,35,4)&&queryRel(0,0,1,45,-1)&&queryRel(-1,0,0,45,-1)");
            commands.add("//replace 45 35:4");
            operations++;
            commands.add("//gmask =queryRel(1,0,0,45,-1)&&queryRel(0,0,1,35,4)&&queryRel(0,0,-1,45,-1)&&queryRel(-1,0,0,45,-1)");
            commands.add("//replace 45 35:4");
            operations++;
        }



        // ----------- BASE ----------
        int currentHeight = 0;

        if(baseHeight > 0)
            for(int i = 0; i < baseHeight; i++) {
                currentHeight++;

                // Move wool one block up
                operations = moveWoolUp(commands, operations, 0);

                // Select everything x blocks above bricks. Then replace that with lapislazuli
                commands.add("//gmask =queryRel(0," + (-currentHeight) + ",0,45,-1)");
                commands.add("//set 22");
                operations++;

                // Raise the yellow wool layer by one block
                operations = raiseYellowWoolFloor(commands, operations);
            }



        // ----------- FLOORS ----------
        int heightDifference = 0;
        for(int i = 0; i < floorCount; i++) {
            currentHeight++;

            // Move wool one block up
            operations = moveWoolUp(commands, operations, i+1);

            // Select everything x blocks above bricks. Then replace that with lapislazuli ore
            commands.add("//gmask =queryRel(0," + (-currentHeight) + ",0,45,-1)");
            commands.add("//set 21");
            operations++;

            // Raise the yellow wool layer by one block
            operations = raiseYellowWoolFloor(commands, operations);

            // Windows
            for(int i2 = 0; i2 < windowHeight; i2++) {
                currentHeight++;

                // Move wool one block up
                operations = moveWoolUp(commands, operations, 0);

                // Select everything x blocks above bricks. Then replace that with white glass
                commands.add("//gmask =queryRel(0," + (-currentHeight) + ",0,45,-1)");

                if (!containsRedWool) {
                    // Replace everything with white glass
                    commands.add("//set 95:0");
                    operations++;
                }else {
                    // Replace red wool with gray glass
                    commands.add("//replace 35:14 95:7");
                    operations++;

                    // Replace air with lapislazuli ore
                    commands.add("//replace 0 21");
                    operations++;

                    // Replace blue and green wool with lapislazuli ore
                    commands.add("//replace 35:11,35:5 21");
                }


                // Raise the yellow wool layer by one block
                operations = raiseYellowWoolFloor(commands, operations);
            }

            heightDifference = floorHeight - (windowHeight + 1);

            if(heightDifference > 0)
                for(int i2 = 0; i2 < heightDifference; i2++) {
                    currentHeight++;

                    // Move wool one block up
                    operations = moveWoolUp(commands, operations, -1);

                    // Select everything x blocks above bricks. Then replace that with lapislazuli ore
                    commands.add("//gmask =queryRel(0," + (-currentHeight) + ",0,45,-1)");
                    commands.add("//set 21");
                    operations++;

                    // Raise the yellow wool layer by one block
                    operations = raiseYellowWoolFloor(commands, operations);
                }
        }
        if(heightDifference == 0){
            currentHeight++;

            // Move wool one block up
            operations = moveWoolUp(commands, operations, -1);

            // Select everything x blocks above bricks. Then replace that with lapislazuli ore
            commands.add("//gmask =queryRel(0," + (-currentHeight) + ",0,45,-1)");
            commands.add("//set 21");
            operations++;

            // Raise the yellow wool layer by one block
            operations = raiseYellowWoolFloor(commands, operations);
        }

        // Remove the red wool
        commands.add("//gmask");
        commands.add("//replace 35:14 0");
        operations++;


        // ----------- BALCONY 1/2 ----------

        if(containsOrangeWool){
            // Disable the gmask
            commands.add("//gmask");

            // Replace all orange wool with air
            commands.add("//replace 35:1 0");
        }


        // ----------- WINDOWS ----------
        int windowSum = windowWidth + windowDistance;
        int wd2 = windowDistance - 1;

        // If windowSum=6 & wd=2:    Any white glass block whose z%6 remainder > 1, and which has air in the x+1 direction, and has no air in the x-1 direction, and has no white glass in the x-1 direction, is replaced with gray glass.
        commands.add("//gmask =(abs(z%" + windowSum + ")-" + wd2 + ")&&queryRel(1,0,0,0,-1)&&!queryRel(-1,0,0,0,-1)&&!queryRel(-1,0,0,95,-1)");
        commands.add("//replace 95:0 95:7");
        operations++;

        // If windowSum=6 & wd=2:    Any white glass block whose z%6 remainder > 1, and which has air in the x-1 direction, and has no air in the x+1 direction, and has no white glass in the x+1 direction, is replaced with gray glass.
        commands.add("//gmask =(abs(z%" + windowSum + ")-" + wd2 + ")&&queryRel(-1,0,0,0,-1)&&!queryRel(1,0,0,0,-1)&&!queryRel(1,0,0,95,-1)");
        commands.add("//replace 95:0 95:7");
        operations++;

        // If windowSum=6 & wd=2:    Any white glass block whose x%6 remainder > 1, and which has air in the z+1 direction, and has no air in the z-1 direction, and has no white glass in the z-1 direction, is replaced with gray glass.
        commands.add("//gmask =(abs(x%" + windowSum + ")-" + wd2 + ")&&queryRel(0,0,1,0,-1)&&!queryRel(0,0,-1,0,-1)&&!queryRel(0,0,-1,95,-1)");
        commands.add("//replace 95:0 95:7");
        operations++;

        // If windowSum=6 & wd=2:    Any white glass block whose x%6 remainder > 1, and which has air in the z-1 direction, and has no air in the z+1 direction, and has no white glass in the z+1 direction, is replaced with gray glass.
        commands.add("//gmask =(abs(x%" + windowSum + ")-" + wd2 + ")&&queryRel(0,0,-1,0,-1)&&!queryRel(0,0,1,0,-1)&&!queryRel(0,0,1,95,-1)");
        commands.add("//replace 95:0 95:7");
        operations++;

        // Disable the global mask
        commands.add("//gmask");


        // Replace any white glass with lapislazuli ore
        commands.add("//replace 95:0 21");
        operations++;

        // Replace any gray glass with the window color
        commands.add("//replace 95:7 " + windowColor);
        operations++;



        // ----------- BALCONY 2/2 ----------

        if(containsOrangeWool){

            // Select all blocks that have orange wool below them and have air next to them
            commands.add("//gmask =queryRel(0,-1,0,35,13)&&(queryRel(-1,-1,0,0,0)||queryRel(1,-1,0,0,0)||queryRel(0,-1,-1,0,0)||queryRel(0,-1,1,0,0))");

            // Replace all blocks above green wool with the balcony fence color
            commands.add("//replace >35:13 " + balconyFenceColor);


            // If the balcony fence color is enabled, replace all blocks above green wool with the balcony fence color
            if(!balconyFenceColor.equalsIgnoreCase(Flag.DISABLED)) {
                // Disable the global mask
                commands.add("//gmask");

                // Replace all green wool with the balcony color
                commands.add("//replace 35:13 " + balconyColor);
            }
        }


        // ----------- ROOF ----------

        String rm1 = roofColor;
        String rm2 = roofColor;
        String rm3 = roofColor;



        if(roofType == RoofType.FLATTER_SLABS || roofType == RoofType.STEEP_SLABS|| roofType == RoofType.MEDIUM_SLABS){

            // Replace the blue wool next to green wool with green wool
            commands.add("//gmask =queryRel(1,0,0,35,5)||queryRel(-1,0,0,35,5)||queryRel(0,0,1,35,5)||queryRel(0,0,-1,35,5)||queryRel(1,0,1,35,5)||queryRel(-1,0,-1,35,5)||queryRel(-1,0,1,35,5)||queryRel(1,0,-1,35,5)");
            commands.add("//replace 35:11 35:5");
            operations++;

            // Create the roof house wall staircase
            for(int i = 0; i < maxRoofHeight; i++){
                // Select all air blocks that are above blue wool and above & next to green wool
                commands.add("//gmask =queryRel(1,-1,0,35,5)||queryRel(-1,-1,0,35,5)||queryRel(0,-1,1,35,5)||queryRel(0,-1,-1,35,5)||queryRel(1,-1,1,35,5)||queryRel(-1,-1,-1,35,5)||queryRel(-1,-1,1,35,5)||queryRel(1,-1,-1,35,5)");
                commands.add("//replace >35:11 35:5");
                operations++;

                // Select all air blocks that are above blue wool and next to green wool
                commands.add("//gmask =queryRel(1,0,0,35,5)||queryRel(-1,0,0,35,5)||queryRel(0,0,1,35,5)||queryRel(0,0,-1,35,5)||queryRel(1,0,1,35,5)||queryRel(-1,0,-1,35,5)||queryRel(-1,0,1,35,5)||queryRel(1,0,-1,35,5)");
                commands.add("//replace >35:11 35:5");
                operations++;

                // Select all air blocks that are above blue wool and replace them with blue wool
                commands.add("//gmask 0");
                commands.add("//replace >35:11 35:11");
                operations++;
            }


            // (One more yellow wool layer) Replace everything above yellow wool with one layer yellow wool
            commands.add("//replace >35:4 35:4");
            operations++;

            // (First Roof Layer) Select only air blocks that are next to yellow wool in any direction and above lapislazuli ore. Then replace them with stone slabs
            commands.add("//gmask =queryRel(0,0,0,0,0)&&(queryRel(1,0,0,35,4)||queryRel(1,0,0,35,4)||queryRel(-1,0,0,35,4)||queryRel(0,0,1,35,4)||queryRel(0,0,-1,35,4))");
            commands.add("//replace >21 44");
            operations++;

            // (Fix First Roof Layer) Select only air blocks that are next to stone slabs and green wool in any direction and above lapislazuli ore. Then replace them with stone slabs
            commands.add("//gmask =queryRel(0,0,0,0,0)&&(queryRel(1,0,0,44,0)||queryRel(-1,0,0,44,0)||queryRel(0,0,1,44,0)||queryRel(0,0,-1,44,0))");
            commands.add("//replace >21 44");
            commands.add("//replace >21 44");
            operations++;
            operations++;

            // (Overhang Roof Layer 1) Select all air blocks next to lapislazuli ores that have a stone slab above them. Then replace them with and upside down stone slab
            commands.add("//gmask =(queryRel(1,0,0,21,-1)&&queryRel(1,1,0,44,0))||(queryRel(-1,0,0,21,-1)&&queryRel(-1,1,0,44,0))||(queryRel(0,0,1,21,-1)&&queryRel(0,1,1,44,0))||(queryRel(0,0,-1,21,-1)&&queryRel(0,1,-1,44,0))");
            commands.add("//replace 0 44:8");
            operations++;

            // (Overhang Roof Layer 2) Select all air blocks next to upside down stone slab and lapislazuli. Then replace them with and upside down stone slab
            commands.add("//gmask =(" +
                    "(queryRel(1,0,0,44,8)&&(queryRel(0,0,1,21,0)||queryRel(0,0,-1,21,0)))" +
                    "||(queryRel(-1,0,0,44,8)&&(queryRel(0,0,1,21,0)||queryRel(0,0,-1,21,0)))" +
                    "||(queryRel(0,0,1,44,8)&&(queryRel(1,0,0,21,0)||queryRel(-1,0,0,21,0)))" +
                    "||(queryRel(0,0,-1,44,8)&&(queryRel(1,0,0,21,0)||queryRel(-1,0,0,21,0)))" +
                    ")");
            commands.add("//replace 0 44:8");
            operations++;



            // Replace the highest yellow wool layer with double slabs
            commands.add("//gmask <0");
            commands.add("//replace 35:4 43");
            operations++;

            maxRoofHeight = maxRoofHeight - 1;

            if(maxRoofHeight > 0)
                for(int i = 0; i < maxRoofHeight; i++) {
                    if(roofType == RoofType.FLATTER_SLABS || roofType == RoofType.MEDIUM_SLABS)
                        //Only select air block that are surrounded by other stone slabs below or which are directly neighbors to green wool or blue wool
                        commands.add("//gmask =" +
                                "(" +
                                    "queryRel(1,-1,0,43,-1)&&queryRel(-1,-1,0,43,-1)&&queryRel(0,-1,1,43,-1)&&queryRel(0,-1,-1,43,-1)" +
                                    "&&(queryRel(-1,-1,1,43,-1)||queryRel(-1,0,1,35,5)||queryRel(-1,0,1,35,11))" +
                                    "&&(queryRel(1,-1,-1,43,-1)||queryRel(1,0,-1,35,5)||queryRel(1,0,-1,35,11))" +
                                    "&&(queryRel(1,-1,1,43,-1)||queryRel(1,0,1,35,5)||queryRel(1,0,1,35,11))" +
                                    "&&(queryRel(-1,-1,-1,43,-1)||queryRel(-1,0,-1,35,5)||queryRel(-1,0,-1,35,11))" +
                                ")" +
                                "||queryRel(1,0,0,35,5)||queryRel(1,0,0,35,11)" +
                                "||queryRel(-1,0,0,35,5)||queryRel(-1,0,0,35,11)" +
                                "||queryRel(0,0,1,35,5)||queryRel(0,0,1,35,11)" +
                                "||queryRel(0,0,-1,35,5)||queryRel(0,0,-1,35,11)"
                        );
                    else
                        commands.add("//gmask =!(queryRel(1,-1,0,44,-1)||queryRel(-1,-1,0,44,-1)||queryRel(0,-1,1,44,-1)||queryRel(0,-1,-1,44,-1))");

                    commands.add("//replace >43 44");
                    operations++;

                    if(roofType == RoofType.FLATTER_SLABS)

                        //Only select air block that are surrounded by other stone slabs or which are directly neighbors to green wool or blue wool
                        commands.add("//gmask =" +
                                "(" +
                                    "queryRel(1,0,0,44,-1)&&queryRel(-1,0,0,44,-1)&&queryRel(0,0,1,44,-1)&&queryRel(0,0,-1,44,-1)" +
                                    "&&(queryRel(-1,0,1,44,-1)||queryRel(-1,0,1,35,5)||queryRel(-1,0,1,35,11))" +
                                    "&&(queryRel(1,0,-1,44,-1)||queryRel(1,0,-1,35,5)||queryRel(1,0,-1,35,11))" +
                                    "&&(queryRel(1,0,1,44,-1)||queryRel(1,0,1,35,5)||queryRel(1,0,1,35,11))" +
                                    "&&(queryRel(-1,0,-1,44,-1)||queryRel(-1,0,-1,35,5)||queryRel(-1,0,-1,35,11))" +
                                ")" +
                                "||queryRel(1,0,0,35,5)||queryRel(1,0,0,35,11)" +
                                "||queryRel(-1,0,0,35,5)||queryRel(-1,0,0,35,11)" +
                                "||queryRel(0,0,1,35,5)||queryRel(0,0,1,35,11)" +
                                "||queryRel(0,0,-1,35,5)||queryRel(0,0,-1,35,11)"

                        );
                    else
                        commands.add("//gmask =!(queryRel(1,0,0,0,-1)||queryRel(-1,0,0,0,-1)||queryRel(0,0,1,0,-1)||queryRel(0,0,-1,0,-1))");

                    commands.add("//replace 44 43");
                    operations++;
                }

            // Replace everything above upside down stone slabs with purple wool
            commands.add("//gmask");
            commands.add("//replace >44:8 35:10");
            operations++;

            operations += expandGreenWool(commands);

            // (Overhang Roof Layer 3) Select all air blocks next to two upside down stone slabs. Then replace them with and upside down stone slab
            commands.add("//gmask =(queryRel(1,0,0,44,8)&&queryRel(2,0,0,44,8))||(queryRel(-1,0,0,44,8)&&queryRel(-2,0,0,44,8))||(queryRel(0,0,1,44,8)&&queryRel(0,0,2,44,8))||(queryRel(0,0,-1,44,8)&&queryRel(0,0,-2,44,8))");
            commands.add("//replace 0 44:8");
            operations++;

            // Select all green wool that are above & next to green wool and replace it with stone slabs
            commands.add("//gmask =queryRel(1,-1,0,35,5)||queryRel(-1,-1,0,35,5)||queryRel(0,-1,1,35,5)||queryRel(0,-1,-1,35,5)||queryRel(1,-1,1,35,5)||queryRel(-1,-1,-1,35,5)||queryRel(-1,-1,1,35,5)||queryRel(1,-1,-1,35,5)");
            commands.add("//replace 35:5 44");
            operations++;

            // Select all green wool that are above & next to upside down stone slabs and replace it with stone slabs
            commands.add("//gmask =queryRel(1,-1,0,44,8)||queryRel(-1,-1,0,44,8)||queryRel(0,-1,1,44,8)||queryRel(0,-1,-1,44,8)||queryRel(1,-1,1,44,8)||queryRel(-1,-1,-1,44,8)||queryRel(-1,-1,1,44,8)||queryRel(1,-1,-1,44,8)");
            commands.add("//replace 35:5 44");
            operations++;

            // Select all air blocks that are below & next to green wool and under a stone slab and replace it with upside down stone slabs
            commands.add("//gmask =queryRel(0,1,0,44,0)&&(queryRel(1,1,0,35,5)||queryRel(-1,1,0,35,5)||queryRel(0,1,1,35,5)||queryRel(0,1,-1,35,5)||queryRel(1,1,1,35,5)||queryRel(-1,1,-1,35,5)||queryRel(-1,1,1,35,5)||queryRel(1,1,-1,35,5))");
            commands.add("//replace 0 44:8");
            operations++;

            // Select all air blocks that are next to green wool and under a stone slab and replace it with upside down stone slabs
            commands.add("//gmask =queryRel(0,1,0,44,0)&&(queryRel(1,0,0,35,5)||queryRel(-1,0,0,35,5)||queryRel(0,0,1,35,5)||queryRel(0,0,-1,35,5))");
            commands.add("//replace 0 44:8");
            operations++;

            // Select all left over green wool replace it with double stone slabs
            commands.add("//gmask");
            commands.add("//replace 35:5 43");
            operations++;

            // Replace blue wool with lapislazuli ore
            commands.add("//replace 35:11 21");
            operations++;


            // Create the flipped steps
            String[] roofColors = roofColor.split(",");
            String[] roofColors2 = new String[roofColors.length];
            String[] roofColors3 = new String[roofColors.length];

            for(int i = 0; i < roofColors.length; i++){
                String[] values = roofColors[i].split(":");
                String material = values[0];
                int data = 0;
                if(values.length > 1)
                    data = Integer.parseInt(values[1]);
                data += 8;

                roofColors2[i] = material + ":" + data;
                roofColors3[i] = (Integer.parseInt(material)-1) + ":" + data;
            }

            rm1 = StringUtils.join(roofColors, ",");
            rm2 = StringUtils.join(roofColors2, ",");
            rm3 = StringUtils.join(roofColors3, ",");

        } else if(roofType == RoofType.FLAT){
            commands.add("//gmask 0");
            commands.add("//replace >21 171:7");
            operations++;
            commands.add("//gmask <0");
            commands.add("//replace 35:4 23");
            operations++;

        } else if(roofType == RoofType.STAIRS){

            // Create the roof house wall staircase
            for(int i = 0; i < maxRoofHeight; i++){
                // Select all air blocks that are above blue wool and next to green wool
                commands.add("//gmask =queryRel(1,-1,0,35,5)||queryRel(-1,-1,0,35,5)||queryRel(0,-1,1,35,5)||queryRel(0,-1,-1,35,5)||queryRel(1,-1,1,35,5)||queryRel(-1,-1,-1,35,5)||queryRel(-1,-1,1,35,5)||queryRel(1,-1,-1,35,5)");
                commands.add("//replace >35:11 35:5");
                operations++;

                // Select all air blocks that are above blue wool and replace them with blue wool
                commands.add("//gmask 0");
                commands.add("//replace >35:11 35:11");
                operations++;
            }

            // (One more yellow wool layer) Replace everything above yellow wool with one layer yellow wool
            commands.add("//replace >35:4 35:4");
            operations++;

            // (First Roof Layer) Select only air blocks that are next to yellow wool in any direction and above lapislazuli ore. Then replace them with stone bricks
            commands.add("//gmask =queryRel(0,0,0,0,0)&&(queryRel(1,0,0,35,4)||queryRel(1,0,0,35,4)||queryRel(-1,0,0,35,4)||queryRel(0,0,1,35,4)||queryRel(0,0,-1,35,4))");
            commands.add("//replace >21 98");
            operations++;

            // (Fix First Roof Layer) Select only air blocks that are next to stone slabs in any direction and above lapislazuli ore. Then replace them with stone bricks
            commands.add("//gmask =queryRel(0,0,0,0,0)&&(queryRel(1,0,0,98,0)||queryRel(1,0,0,98,0)||queryRel(-1,0,0,98,0)||queryRel(0,0,1,98,0)||queryRel(0,0,-1,98,0))");
            commands.add("//replace >21 98");
            commands.add("//replace >21 98");
            operations++;
            operations++;

            // (Overhang Roof Layer) Select all air blocks next to lapislazuli ores that have a stone slab above them. Then replace them with stone bricks
            commands.add("//gmask =(queryRel(1,0,0,21,-1)&&queryRel(1,1,0,98,0))||(queryRel(-1,0,0,21,-1)&&queryRel(-1,1,0,98,0))||(queryRel(0,0,1,21,-1)&&queryRel(0,1,1,98,0))||(queryRel(0,0,-1,21,-1)&&queryRel(0,1,-1,98,0))");
            commands.add("//replace 0 98:8");
            operations++;

            maxRoofHeight = maxRoofHeight - 1;

            if(maxRoofHeight > 0)
                for(int i = 0; i < maxRoofHeight; i++) {
                    // Every 2nd layer
                    if(i % 2 == 0)
                        //Only select air block that have yellow wool below them which are surrounded by other stone bricks
                        commands.add("//gmask =(queryRel(1,-1,0,98,-1)||queryRel(-1,-1,0,98,-1)||queryRel(0,-1,1,98,-1)||queryRel(0,-1,-1,98,-1))");
                    else
                        // Only select air block that have yellow wool below them which are completely surrounded by other stone bricks
                        commands.add("//gmask =(queryRel(1,-1,0,98,-1)||queryRel(-1,-1,0,98,-1)||queryRel(0,-1,1,98,-1)||queryRel(0,-1,-1,98,-1)||queryRel(1,-1,1,98,-1)||queryRel(-1,-1,1,98,-1)||queryRel(-1,-1,-1,98,-1)||queryRel(1,-1,-1,98,-1))");

                    commands.add("//replace >35:4 98");
                    operations++;

                    //Only select yellow wool with air blocks above them and put yellow wool above them
                    commands.add("//gmask air");
                    commands.add("//replace >35:4 35:4");
                    operations++;
                }

            // ROOF OVERHANG

            operations += expandGreenWool(commands);


            // Replace green wool with stone bricks
            commands.add("//gmask");
            commands.add("//replace 35:5 98");
            operations++;

            // Replace blue wool with lapislazuli ore
            commands.add("//replace 35:11 21");
            operations++;

            // Fill up air blocks surrounded by 3 stone bricks with stone bricks
            commands.add("//gmask =queryRel(1,0,0,98,0)&&queryRel(-1,0,0,98,0)&&queryRel(0,0,1,98,0)&&queryRel(0,0,-1,0,0)");
            commands.add("//replace 0 98");
            operations++;
            commands.add("//gmask =queryRel(1,0,0,98,0)&&queryRel(-1,0,0,98,0)&&queryRel(0,0,1,0,0)&&queryRel(0,0,-1,98,0)");
            commands.add("//replace 0 98");
            operations++;
            commands.add("//gmask =queryRel(1,0,0,98,0)&&queryRel(-1,0,0,0,0)&&queryRel(0,0,1,98,0)&&queryRel(0,0,-1,98,0)");
            commands.add("//replace 0 98");
            operations++;
            commands.add("//gmask =queryRel(1,0,0,0,0)&&queryRel(-1,0,0,98,0)&&queryRel(0,0,1,98,0)&&queryRel(0,0,-1,98,0)");
            commands.add("//replace 0 98");
            operations++;


            // ROOF STAIRS

            // Fill the top roof gable that is surrounded by 2 stone bricks and one stone brick on top with stone bricks
            commands.add("//gmask =queryRel(0,1,0,98,0)&&(queryRel(-1,0,0,98,0)||queryRel(-1,0,1,98,0)||queryRel(-1,0,-1,98,0))&&(queryRel(1,0,0,98,0)||queryRel(1,0,1,98,0)||queryRel(1,0,-1,98,0))");
            commands.add("//replace 0 98");
            operations++;
            commands.add("//gmask =queryRel(0,1,0,98,0)&&(queryRel(0,0,-1,98,0)||queryRel(1,0,-1,98,0)||queryRel(-1,0,-1,98,0))&&(queryRel(0,0,1,98,0)||queryRel(1,0,1,98,0)||queryRel(-1,0,1,98,0))");
            commands.add("//replace 0 98");
            operations++;

            // Fill the overhang with upside-down stairs which are surrounded by 1 stone brick and one stone brick above them
            commands.add("//gmask =queryRel(0,1,0,98,0)&&queryRel(-1,0,0,98,0)");
            commands.add("//replace 0 109:5");
            operations++;
            commands.add("//gmask =queryRel(0,1,0,98,0)&&queryRel(1,0,0,98,0)");
            commands.add("//replace 0 109:4");
            operations++;
            commands.add("//gmask =queryRel(0,1,0,98,0)&&queryRel(0,0,1,98,0)");
            commands.add("//replace 0 109:6");
            operations++;
            commands.add("//gmask =queryRel(0,1,0,98,0)&&queryRel(0,0,-1,98,0)");
            commands.add("//replace 0 109:7");
            operations++;

            // Fill the remaining overhang with upside-down stairs which are surrounded by 1 stone brick and one stone brick above them
            commands.add("//gmask =queryRel(0,1,0,98,0)&&(queryRel(-1,0,0,98,0)||queryRel(-1,0,1,98,0)||queryRel(-1,0,-1,98,0))");
            commands.add("//replace 0 109:5");
            operations++;
            commands.add("//gmask =queryRel(0,1,0,98,0)&&(queryRel(1,0,0,98,0)||queryRel(1,0,1,98,0)||queryRel(1,0,-1,98,0))");
            commands.add("//replace 0 109:4");
            operations++;
            commands.add("//gmask =queryRel(0,1,0,98,0)&&(queryRel(0,0,1,98,0)||queryRel(1,0,1,98,0)||queryRel(-1,0,1,98,0))");
            commands.add("//replace 0 109:6");
            operations++;
            commands.add("//gmask =queryRel(0,1,0,98,0)&&(queryRel(0,0,-1,98,0)||queryRel(1,0,-1,98,0)||queryRel(-1,0,-1,98,0))");
            commands.add("//replace 0 109:7");
            operations++;


            // (Normal Stair Roof Layer) Replace all air blocks that have a stone brick on one side, 3 air sides and one stone brick below them with stairs
            commands.add("//gmask =(queryRel(1,0,0,98,-1)&&queryRel(-1,0,0,0,-1)&&queryRel(0,0,1,0,-1)&&queryRel(0,0,-1,0,-1))");
            commands.add("//replace >98 109:0");
            operations++;
            commands.add("//gmask =(queryRel(1,0,0,0,-1)&&queryRel(-1,0,0,98,-1)&&queryRel(0,0,1,0,-1)&&queryRel(0,0,-1,0,-1))");
            commands.add("//replace >98 109:1");
            operations++;
            commands.add("//gmask =(queryRel(1,0,0,0,-1)&&queryRel(-1,0,0,0,-1)&&queryRel(0,0,1,98,-1)&&queryRel(0,0,-1,0,-1))");
            commands.add("//replace >98 109:2");
            operations++;
            commands.add("//gmask =(queryRel(1,0,0,0,-1)&&queryRel(-1,0,0,0,-1)&&queryRel(0,0,1,0,-1)&&queryRel(0,0,-1,98,-1))");
            commands.add("//replace >98 109:3");
            operations++;

            // (Corner Stair Roof Layer) Replace all air blocks that have stone bricks on 2 sides, 2 air or stair sides and one stone brick below them with stairs
            commands.add("//gmask =(queryRel(1,0,0,98,-1)&&queryRel(0,0,1,98,-1)&&(queryRel(-1,0,0,0,-1)||queryRel(-1,0,0,109,-1))&&(queryRel(0,0,-1,0,-1)||queryRel(0,0,-1,109,-1)))");
            commands.add("//replace >98 109:8");
            operations++;
            commands.add("//gmask =(queryRel(1,0,0,98,-1)&&queryRel(0,0,-1,98,-1)&&(queryRel(-1,0,0,0,-1)||queryRel(-1,0,0,109,-1))&&(queryRel(0,0,1,0,-1)||queryRel(0,0,1,109,-1)))");
            commands.add("//replace >98 109:11");
            operations++;
            commands.add("//gmask =(queryRel(-1,0,0,98,-1)&&queryRel(0,0,1,98,-1)&&(queryRel(1,0,0,0,-1)||queryRel(1,0,0,109,-1))&&(queryRel(0,0,-1,0,-1)||queryRel(0,0,-1,109,-1)))");
            commands.add("//replace >98 109:10");
            operations++;
            commands.add("//gmask =(queryRel(-1,0,0,98,-1)&&queryRel(0,0,-1,98,-1)&&(queryRel(1,0,0,0,-1)||queryRel(1,0,0,109,-1))&&(queryRel(0,0,1,0,-1)||queryRel(0,0,1,109,-1)))");
            commands.add("//replace >98 109:9");
            operations++;


            // (Corner Stair 2 Roof Layer) Replace all air blocks that have stairs on 2 sides, 2 air sides and one stone brick below them with stairs
            commands.add("//gmask =(queryRel(1,0,0,109,-1)&&queryRel(0,0,1,109,-1)&&queryRel(-1,0,0,0,-1)&&queryRel(0,0,-1,0,-1))");
            commands.add("//replace >98 109:10");
            operations++;
            commands.add("//gmask =(queryRel(1,0,0,109,-1)&&queryRel(0,0,-1,109,-1)&&queryRel(-1,0,0,0,-1)&&queryRel(0,0,1,0,-1))");
            commands.add("//replace >98 109:11");
            operations++;
            commands.add("//gmask =(queryRel(-1,0,0,109,-1)&&queryRel(0,0,1,109,-1)&&queryRel(1,0,0,0,-1)&&queryRel(0,0,-1,0,-1))");
            commands.add("//replace >98 109:9");
            operations++;
            commands.add("//gmask =(queryRel(-1,0,0,109,-1)&&queryRel(0,0,-1,109,-1)&&queryRel(1,0,0,0,-1)&&queryRel(0,0,1,0,-1))");
            commands.add("//replace >98 109:9");
            operations++;

            // Cover leaking yellow wool blocks with stone bricks
            commands.add("//gmask =queryRel(1,0,0,35,4)||queryRel(-1,0,0,35,4)||queryRel(0,0,1,35,4)||queryRel(0,0,-1,35,4)");
            commands.add("//replace 0 98");
            operations++;

            // Disable the gmask
            commands.add("//gmask");

            String[] colors = roofColor.split(",");

            // Remove :X from colors
            for(int i = 0; i < colors.length; i++)
                colors[i] = colors[i].split(":")[0];

            String[] blockColors = new String[colors.length];
            for(int i = 0; i < colors.length; i++)
                blockColors[i] = MenuItems.convertStairToBlock(colors[i]);


            // Replace stone bricks with the correct color
            commands.add("//replace 98 " + StringUtils.join(blockColors, ","));
            operations++;


            // Replace all stairs with the correct color
            for(int i = 0; i < 12; i++) {
                if(colors.length == 1)
                    commands.add("//replace 109:" + i + " " + colors[0] + ":" + i);
                else
                    commands.add("//replace 109:" + i + " " + StringUtils.join(colors, ":" + i + ","));

                operations++;
            }
        }


        // ----------- FINAL FINISH ----------

        commands.add("//gmask 0,45,31,37,38,39,40,175");

        for(int i = 0; i < 5; i++) {
            commands.add("//replace <22 22");
            operations++;
        }

        commands.add("//gmask");
        commands.add("//replace 21 " + wallColor);
        operations++;
        commands.add("//replace 22 " + baseColor);
        operations++;
        commands.add("//replace 35:4 35:7");
        operations++;


        commands.add("//gmask");



        commands.add("//replace 44:0 " + rm1);
        operations++;
        commands.add("//replace 44:8 " + rm2);
        operations++;
        commands.add("//replace 43:0 " + rm3);
        operations++;
        commands.add("//replace 23 " + rm3);
        operations++;


        // Reset pos1 and pos2
        commands.add("//pos1 " + selectionPoints.get(0).getBlockX() + "," + maxY + "," + selectionPoints.get(0).getBlockZ());
        for(int i = 1; i < selectionPoints.size(); i++)
            commands.add("//pos2 " + selectionPoints.get(i).getBlockX() + "," + minY + "," + selectionPoints.get(i).getBlockZ());

        GeneratorModule.getInstance().getGeneratorCommands().add(new Command(p, house, commands, operations, blocks));
        GeneratorModule.getInstance().getPlayerHistory(p).addHistoryEntry(new History.HistoryEntry(GeneratorType.HOUSE, operations));
    }

    // Move blue, green and red wool one block up
    private static int moveWoolUp(List<String> commands, int operations, int floor) {
        commands.add("//gmask");
        commands.add("//replace >35:11 35:11");
        operations++;
        commands.add("//replace >35:14 35:14");
        operations++;
        commands.add("//replace >35:5 35:5");
        operations++;

        if(floor >= 0) {
            if (floor == 0)
                commands.add("//replace >35:1,35:13 35:1");
            else
                commands.add("//replace >35:1,35:13 35:13");
            operations++;
        }

        return operations;
    }

    private static int raiseYellowWoolFloor(List<String> commands, int operations) {
        commands.add("//gmask");
        commands.add("//replace >35:4 35:4");
        operations++;
        return operations;
    }

    private static int expandGreenWool(List<String> commands){
        int operations = 0;

        // Replace everything above green wool with temporary purple wool
        commands.add("//gmask");
        commands.add("//replace >35:5 35:10");
        operations++;

        // Replace air next to purple wool with purple wool
        commands.add("//gmask =queryRel(1,0,0,35,10)||queryRel(-1,0,0,35,10)||queryRel(0,0,1,35,10)||queryRel(0,0,-1,35,10)");
        commands.add("//replace 0 35:10");
        operations++;

        // Replace air next to green wool with green wool
        commands.add("//gmask =queryRel(1,0,0,35,5)||queryRel(-1,0,0,35,5)||queryRel(0,0,1,35,5)||queryRel(0,0,-1,35,5)");
        commands.add("//replace 0 35:5");
        operations++;

        // Replace purple wool with air
        commands.add("//gmask");
        commands.add("//replace 35:10 0");

        return operations;
    }
}
