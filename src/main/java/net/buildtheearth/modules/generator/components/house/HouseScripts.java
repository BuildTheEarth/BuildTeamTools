package net.buildtheearth.modules.generator.components.house;

import com.cryptomorin.xseries.XMaterial;
import net.buildtheearth.modules.generator.model.*;
import net.buildtheearth.modules.generator.utils.GeneratorUtils;
import net.buildtheearth.utils.MenuItems;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;

public class HouseScripts extends Script {

    public HouseScripts(Player player, GeneratorComponent generatorComponent) {
        super(player, generatorComponent);

        buildscript_v_1_2();
    }

    public void buildscript_v_1_2(){
        HashMap<Flag, String> flags = getGeneratorComponent().getPlayerSettings().get(getPlayer().getUniqueId()).getValues();

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

        List<Vector> selectionPoints = GeneratorUtils.getSelectionPointsFromRegion(getRegion());
        Vector[] minMax = GeneratorUtils.getMinMaxPoints(getRegion());
        int minY = minMax[0].getBlockY();
        int maxY = minMax[1].getBlockY();


        getPlayer().chat("/clearhistory");

        // Disable the current global mask
        getPlayer().chat("//gmask");


        // ----------- PREPARATION 01 ----------
        // Replace all non-solid blocks with air

        getPlayer().chat("//expand 10 up");
        getPlayer().chat("//expand 10 down");

        getPlayer().chat("//gmask !#solid");
        getPlayer().chat("//replace 0");
        changes++;

        Block[][][] blocks = GeneratorUtils.analyzeRegion(getPlayer(), getPlayer().getWorld());

        if(blocks == null){
            getPlayer().sendMessage("§c§lERROR: §cRegion not readable. Please report this to the developers of the BuildTeamTool plugin.");
            return;
        }

        int highestBlock = GeneratorUtils.getMaxHeight(blocks, MenuItems.getIgnoredMaterials());
        boolean containsRedWool = GeneratorUtils.containsBlock(blocks, XMaterial.RED_WOOL);
        boolean containsOrangeWool = GeneratorUtils.containsBlock(blocks, XMaterial.ORANGE_WOOL);


        // ----------- PREPARATION 02 ----------
        // Bring the outline on the same height

        // Set pos1 and pos2
        this.operations.add(new Operation( "//pos1 " + selectionPoints.get(0).getBlockX() + "," + highestBlock + "," + selectionPoints.get(0).getBlockZ()));
        for(int i = 1; i < selectionPoints.size(); i++)
            this.operations.add(new Operation( "//pos2 " + selectionPoints.get(i).getBlockX() + "," + minY + "," + selectionPoints.get(i).getBlockZ()));

        this.operations.add(new Operation( "//expand 10 down"));

        // Replace air with sponge
        this.operations.add(new Operation( "//replace 0 19"));
        createBreakPointOperation();
        changes++;

        // Replace all sponges with bricks that have bricks below them
        this.operations.add(new Operation( "//gmask >45"));

        for(int i = 0; i < 20; i++) {
            this.operations.add(new Operation( "//replace 19 45"));
            changes++;
        }
        createBreakPointOperation();

        // Replace all left sponges with air
        this.operations.add(new Operation( "//gmask"));
        this.operations.add(new Operation( "//replace 19 0"));
        createBreakPointOperation();
        changes++;


        // ----------- PREPARATION 03 ----------
        // Bring the orange, yellow, green, blue and red wool blocks to the same height

        String[] woolColors = {"35:1", "35:4", "35:11", "35:14", "35:5"};

        for(String wool : woolColors){
            // Replace all blocks above the wool
            this.operations.add(new Operation( "//gmask >" + wool));
            for(int i = 0; i < 20; i++){
                this.operations.add(new Operation( "//replace 0 " + wool));
                changes++;
            }
            createBreakPointOperation();

            // Select highest yellow wool block and replace it with sponge
            this.operations.add(new Operation( "//gmask <0"));
            this.operations.add(new Operation( "//replace " + wool + " 19"));
            changes++;

            // Replace yellow wool with brick
            this.operations.add(new Operation( "//gmask"));
            this.operations.add(new Operation( "//replace " + wool + " 45"));
            changes++;

            // Replace all sponges with yellow wool
            this.operations.add(new Operation( "//replace 19 " + wool));
            createBreakPointOperation();
            changes++;
        }

        // ----------- PREPARATION 05 ----------
        // Move blue, red and lime wool one block up and replace block below with brick

        this.operations.add(new Operation( "//expand 1 up"));

        String[] woolColorsNoYellow = {"35:11", "35:14", "35:5"};
        for(String wool : woolColorsNoYellow) {
            this.operations.add(new Operation( "//gmask =queryRel(1,0,0,45,0)||queryRel(-1,0,0,45,0)||queryRel(0,0,1,45,0)||queryRel(0,0,-1,45,0)||queryRel(1,0,1,45,0)||queryRel(-1,0,1,45,0)||queryRel(1,0,-1,45,0)||queryRel(-1,0,-1,45,0)"));
            this.operations.add(new Operation( "//replace " + wool + " 19"));
            createBreakPointOperation();
            changes++;

            for(int i = 0; i < 10; i++){
                this.operations.add(new Operation( "//gmask =queryRel(1,0,0,19,0)||queryRel(-1,0,0,19,0)||queryRel(0,0,1,19,0)||queryRel(0,0,-1,19,0)||queryRel(1,0,1,19,0)||queryRel(-1,0,1,19,0)||queryRel(1,0,-1,19,0)||queryRel(-1,0,-1,19,0)"));
                this.operations.add(new Operation( "//replace " + wool + " 19"));
                changes++;
            }
            createBreakPointOperation();

            this.operations.add(new Operation( "//gmask"));
            this.operations.add(new Operation( "//replace >19 " + wool));
            changes++;
            this.operations.add(new Operation( "//replace 19 45"));
            createBreakPointOperation();
            changes++;
        }



        // ----------- PREPARATION 06 ----------
        // Replace all bricks underground with grass

        // Replace all bricks with sponge
        this.operations.add(new Operation( "//replace 45 19"));
        createBreakPointOperation();
        changes++;


        // Replace all sponges with bricks that have air or red wool or blue wool or green wool above them
        this.operations.add(new Operation( "//gmask =(queryRel(0,1,0,0,0)||queryRel(0,1,0,35,11)||queryRel(0,1,0,35,14)||queryRel(0,1,0,35,5))"));
        this.operations.add(new Operation( "//replace 19 45"));
        createBreakPointOperation();

        changes++;

        // Disable the global mask
        this.operations.add(new Operation( "//gmask"));

        // Replace all left sponges with grass
        this.operations.add(new Operation( "//replace 19 2"));
        createBreakPointOperation();
        changes++;


        // ----------- PREPARATION 07 ----------
        // Expand the blue wool until it reaches the green wool

        // Select all blocks that are next to blue wool
        this.operations.add(new Operation( "//gmask =(queryRel(1,0,0,35,11)||queryRel(-1,0,0,35,11)||queryRel(0,0,1,35,11)||queryRel(0,0,-1,35,11)||queryRel(1,0,1,35,11)||queryRel(-1,0,-1,35,11)||queryRel(-1,0,1,35,11)||queryRel(1,0,-1,35,11))&&!(queryRel(1,0,0,35,5)||queryRel(-1,0,0,35,5)||queryRel(0,0,1,35,5)||queryRel(0,0,-1,35,5)||queryRel(1,0,1,35,5)||queryRel(-1,0,-1,35,5)||queryRel(-1,0,1,35,5)||queryRel(1,0,-1,35,5))"));

        for(int i = 0; i < 20; i++) {
            // Replace all blocks above lapislazuli with blue wool
            this.operations.add(new Operation( "//replace >45 35:11"));
            changes++;
        }
        createBreakPointOperation();

        // Place the last blue wool between the green and the blue wool
        this.operations.add(new Operation( "//gmask =queryRel(1,0,0,35,11)||queryRel(-1,0,0,35,11)||queryRel(0,0,1,35,11)||queryRel(0,0,-1,35,11)||queryRel(1,0,1,35,11)||queryRel(-1,0,-1,35,11)||queryRel(-1,0,1,35,11)||queryRel(1,0,-1,35,11)"));
        this.operations.add(new Operation( "//replace >45 35:11"));
        createBreakPointOperation();
        changes++;


        // ----------- GROUND ----------

        // Set pos1 and pos2
        this.operations.add(new Operation( "//pos1 " + selectionPoints.get(0).getBlockX() + "," + maxY + "," + selectionPoints.get(0).getBlockZ()));
        for(int i = 1; i < selectionPoints.size(); i++)
            this.operations.add(new Operation( "//pos2 " + selectionPoints.get(i).getBlockX() + "," + minY + "," + selectionPoints.get(i).getBlockZ()));

        // Expand the current selection down by 10 blocks
        this.operations.add(new Operation( "//expand 10 down"));

        int up_expand = 5 + maxRoofHeight + baseHeight + (floorCount * floorHeight);

        // Expand the current selection up by "up_expand" blocks
        this.operations.add(new Operation( "//expand " + up_expand + " up"));

        // Select all blocks around the yellow wool block
        this.operations.add(new Operation( "//gmask =queryRel(-1,0,0,35,4)||queryRel(1,0,0,35,4)||queryRel(0,0,1,35,4)||queryRel(0,0,-1,35,4)"));

        for(int i = 0; i < 20; i++) {
            // Replace all blocks that are not bricks with yellow wool
            this.operations.add(new Operation( "//replace !45 35:4"));
            changes++;
        }
        createBreakPointOperation();


        // Make the outline as thin as possible and fill all inner corners with yellow wool that are too thick
        for(int i = 0; i < 2; i++) {
            this.operations.add(new Operation( "//gmask =queryRel(-1,0,0,35,4)&&queryRel(0,0,-1,35,4)&&queryRel(0,0,1,45,-1)&&queryRel(1,0,0,45,-1)"));
            this.operations.add(new Operation( "//replace 45 35:4"));
            changes++;
            this.operations.add(new Operation( "//gmask =queryRel(-1,0,0,35,4)&&queryRel(0,0,1,35,4)&&queryRel(0,0,-1,45,-1)&&queryRel(1,0,0,45,-1)"));
            this.operations.add(new Operation( "//replace 45 35:4"));
            changes++;
            this.operations.add(new Operation( "//gmask =queryRel(1,0,0,35,4)&&queryRel(0,0,-1,35,4)&&queryRel(0,0,1,45,-1)&&queryRel(-1,0,0,45,-1)"));
            this.operations.add(new Operation( "//replace 45 35:4"));
            changes++;
            this.operations.add(new Operation( "//gmask =queryRel(1,0,0,35,4)&&queryRel(0,0,1,35,4)&&queryRel(0,0,-1,45,-1)&&queryRel(-1,0,0,45,-1)"));
            this.operations.add(new Operation( "//replace 45 35:4"));
            changes++;
            this.operations.add(new Operation( "//gmask =queryRel(-1,0,0,35,4)&&queryRel(0,0,-1,45,-1)&&queryRel(0,0,1,45,-1)&&queryRel(1,0,0,45,-1)"));
            this.operations.add(new Operation( "//replace 45 35:4"));
            changes++;
            this.operations.add(new Operation( "//gmask =queryRel(1,0,0,35,4)&&queryRel(0,0,1,45,-1)&&queryRel(0,0,-1,45,-1)&&queryRel(-1,0,0,45,-1)"));
            this.operations.add(new Operation( "//replace 45 35:4"));
            changes++;
            this.operations.add(new Operation( "//gmask =queryRel(1,0,0,45,-1)&&queryRel(0,0,-1,35,4)&&queryRel(0,0,1,45,-1)&&queryRel(-1,0,0,45,-1)"));
            this.operations.add(new Operation( "//replace 45 35:4"));
            changes++;
            this.operations.add(new Operation( "//gmask =queryRel(1,0,0,45,-1)&&queryRel(0,0,1,35,4)&&queryRel(0,0,-1,45,-1)&&queryRel(-1,0,0,45,-1)"));
            this.operations.add(new Operation( "//replace 45 35:4"));
            changes++;
        }
        createBreakPointOperation();



        // ----------- BASE ----------
        int currentHeight = 0;

        if(baseHeight > 0)
            for(int i = 0; i < baseHeight; i++) {
                currentHeight++;

                // Move wool one block up
                moveWoolUp(0);

                // Select everything x blocks above bricks. Then replace that with lapislazuli
                this.operations.add(new Operation( "//gmask =queryRel(0," + (-currentHeight) + ",0,45,-1)"));
                this.operations.add(new Operation( "//set 22"));
                createBreakPointOperation();
                changes++;

                // Raise the yellow wool layer by one block
                raiseYellowWoolFloor();
            }



        // ----------- FLOORS ----------
        int heightDifference = 0;
        for(int i = 0; i < floorCount; i++) {
            currentHeight++;

            // Move wool one block up
            moveWoolUp(i+1);

            // Select everything x blocks above bricks. Then replace that with lapislazuli ore
            this.operations.add(new Operation( "//gmask =queryRel(0," + (-currentHeight) + ",0,45,-1)"));
            this.operations.add(new Operation( "//set 21"));
            createBreakPointOperation();
            changes++;

            // Raise the yellow wool layer by one block
            raiseYellowWoolFloor();

            // Windows
            for(int i2 = 0; i2 < windowHeight; i2++) {
                currentHeight++;

                // Move wool one block up
                moveWoolUp(0);

                // Select everything x blocks above bricks. Then replace that with white glass
                this.operations.add(new Operation( "//gmask =queryRel(0," + (-currentHeight) + ",0,45,-1)"));

                if (!containsRedWool) {
                    // Replace everything with white glass
                    this.operations.add(new Operation( "//set 95:0"));
                    changes++;
                }else {
                    // Replace red wool with gray glass
                    this.operations.add(new Operation( "//replace 35:14 95:7"));
                    changes++;

                    // Replace air with lapislazuli ore
                    this.operations.add(new Operation( "//replace 0 21"));
                    changes++;

                    // Replace blue and green wool with lapislazuli ore
                    this.operations.add(new Operation( "//replace 35:11,35:5 21"));
                }


                // Raise the yellow wool layer by one block
                raiseYellowWoolFloor();
            }

            heightDifference = floorHeight - (windowHeight + 1);

            if(heightDifference > 0)
                for(int i2 = 0; i2 < heightDifference; i2++) {
                    currentHeight++;

                    // Move wool one block up
                    moveWoolUp(-1);

                    // Select everything x blocks above bricks. Then replace that with lapislazuli ore
                    this.operations.add(new Operation( "//gmask =queryRel(0," + (-currentHeight) + ",0,45,-1)"));
                    this.operations.add(new Operation( "//set 21"));
                    createBreakPointOperation();
                    changes++;

                    // Raise the yellow wool layer by one block
                    raiseYellowWoolFloor();
                }
        }
        if(heightDifference == 0){
            currentHeight++;

            // Move wool one block up
            moveWoolUp(-1);

            // Select everything x blocks above bricks. Then replace that with lapislazuli ore
            this.operations.add(new Operation( "//gmask =queryRel(0," + (-currentHeight) + ",0,45,-1)"));
            this.operations.add(new Operation( "//set 21"));
            changes++;

            // Raise the yellow wool layer by one block
            raiseYellowWoolFloor();
        }

        // Remove the red wool
        this.operations.add(new Operation( "//gmask"));
        this.operations.add(new Operation( "//replace 35:14 0"));
        createBreakPointOperation();
        changes++;


        // ----------- BALCONY 1/2 ----------

        if(containsOrangeWool){
            // Disable the gmask
            this.operations.add(new Operation( "//gmask"));

            // Replace all orange wool with air
            this.operations.add(new Operation( "//replace 35:1 0"));
            createBreakPointOperation();
        }


        // ----------- WINDOWS ----------
        int windowSum = windowWidth + windowDistance;
        int wd2 = windowDistance - 1;

        // If windowSum=6 & wd=2:    Any white glass block whose z%6 remainder > 1, and which has air in the x+1 direction, and has no air in the x-1 direction, and has no white glass in the x-1 direction, is replaced with gray glass.
        this.operations.add(new Operation( "//gmask =(abs(z%" + windowSum + ")-" + wd2 + ")&&queryRel(1,0,0,0,-1)&&!queryRel(-1,0,0,0,-1)&&!queryRel(-1,0,0,95,-1)"));
        this.operations.add(new Operation( "//replace 95:0 95:7"));
        changes++;

        // If windowSum=6 & wd=2:    Any white glass block whose z%6 remainder > 1, and which has air in the x-1 direction, and has no air in the x+1 direction, and has no white glass in the x+1 direction, is replaced with gray glass.
        this.operations.add(new Operation( "//gmask =(abs(z%" + windowSum + ")-" + wd2 + ")&&queryRel(-1,0,0,0,-1)&&!queryRel(1,0,0,0,-1)&&!queryRel(1,0,0,95,-1)"));
        this.operations.add(new Operation( "//replace 95:0 95:7"));
        changes++;

        // If windowSum=6 & wd=2:    Any white glass block whose x%6 remainder > 1, and which has air in the z+1 direction, and has no air in the z-1 direction, and has no white glass in the z-1 direction, is replaced with gray glass.
        this.operations.add(new Operation( "//gmask =(abs(x%" + windowSum + ")-" + wd2 + ")&&queryRel(0,0,1,0,-1)&&!queryRel(0,0,-1,0,-1)&&!queryRel(0,0,-1,95,-1)"));
        this.operations.add(new Operation( "//replace 95:0 95:7"));
        changes++;

        // If windowSum=6 & wd=2:    Any white glass block whose x%6 remainder > 1, and which has air in the z-1 direction, and has no air in the z+1 direction, and has no white glass in the z+1 direction, is replaced with gray glass.
        this.operations.add(new Operation( "//gmask =(abs(x%" + windowSum + ")-" + wd2 + ")&&queryRel(0,0,-1,0,-1)&&!queryRel(0,0,1,0,-1)&&!queryRel(0,0,1,95,-1)"));
        this.operations.add(new Operation( "//replace 95:0 95:7"));
        changes++;

        createBreakPointOperation();


        // Disable the global mask
        this.operations.add(new Operation( "//gmask"));


        // Replace any white glass with lapislazuli ore
        this.operations.add(new Operation( "//replace 95:0 21"));
        changes++;

        // Replace any gray glass with the window color
        this.operations.add(new Operation( "//replace 95:7 " + windowColor));
        changes++;

        createBreakPointOperation();



        // ----------- BALCONY 2/2 ----------

        if(containsOrangeWool){

            // Select all blocks that have orange wool below them and have air next to them
            this.operations.add(new Operation( "//gmask =queryRel(0,-1,0,35,13)&&(queryRel(-1,-1,0,0,0)||queryRel(1,-1,0,0,0)||queryRel(0,-1,-1,0,0)||queryRel(0,-1,1,0,0))"));

            // Replace all blocks above green wool with the balcony fence color
            this.operations.add(new Operation( "//replace >35:13 " + balconyFenceColor));


            // If the balcony fence color is enabled, replace all blocks above green wool with the balcony fence color
            if(!balconyFenceColor.equalsIgnoreCase(Flag.DISABLED)) {
                // Disable the global mask
                this.operations.add(new Operation( "//gmask"));

                // Replace all green wool with the balcony color
                this.operations.add(new Operation( "//replace 35:13 " + balconyColor));
            }

            createBreakPointOperation();
        }


        // ----------- ROOF ----------

        String rm1 = roofColor;
        String rm2 = roofColor;
        String rm3 = roofColor;



        if(roofType == RoofType.FLATTER_SLABS || roofType == RoofType.STEEP_SLABS|| roofType == RoofType.MEDIUM_SLABS){

            // Replace the blue wool next to green wool with green wool
            this.operations.add(new Operation( "//gmask =queryRel(1,0,0,35,5)||queryRel(-1,0,0,35,5)||queryRel(0,0,1,35,5)||queryRel(0,0,-1,35,5)||queryRel(1,0,1,35,5)||queryRel(-1,0,-1,35,5)||queryRel(-1,0,1,35,5)||queryRel(1,0,-1,35,5)"));
            this.operations.add(new Operation( "//replace 35:11 35:5"));
            createBreakPointOperation();
            changes++;

            // Create the roof house wall staircase
            for(int i = 0; i < maxRoofHeight; i++){
                // Select all air blocks that are above blue wool and above & next to green wool
                this.operations.add(new Operation( "//gmask =queryRel(1,-1,0,35,5)||queryRel(-1,-1,0,35,5)||queryRel(0,-1,1,35,5)||queryRel(0,-1,-1,35,5)||queryRel(1,-1,1,35,5)||queryRel(-1,-1,-1,35,5)||queryRel(-1,-1,1,35,5)||queryRel(1,-1,-1,35,5)"));
                this.operations.add(new Operation( "//replace >35:11 35:5"));
                changes++;

                // Select all air blocks that are above blue wool and next to green wool
                this.operations.add(new Operation( "//gmask =queryRel(1,0,0,35,5)||queryRel(-1,0,0,35,5)||queryRel(0,0,1,35,5)||queryRel(0,0,-1,35,5)||queryRel(1,0,1,35,5)||queryRel(-1,0,-1,35,5)||queryRel(-1,0,1,35,5)||queryRel(1,0,-1,35,5)"));
                this.operations.add(new Operation( "//replace >35:11 35:5"));
                changes++;

                // Select all air blocks that are above blue wool and replace them with blue wool
                this.operations.add(new Operation( "//gmask 0"));
                this.operations.add(new Operation( "//replace >35:11 35:11"));
                changes++;

                createBreakPointOperation();
            }


            // (One more yellow wool layer) Replace everything above yellow wool with one layer yellow wool
            this.operations.add(new Operation( "//replace >35:4 35:4"));
            createBreakPointOperation();
            changes++;

            // (First Roof Layer) Select only air blocks that are next to yellow wool in any direction and above lapislazuli ore. Then replace them with stone slabs
            this.operations.add(new Operation( "//gmask =queryRel(0,0,0,0,0)&&(queryRel(1,0,0,35,4)||queryRel(1,0,0,35,4)||queryRel(-1,0,0,35,4)||queryRel(0,0,1,35,4)||queryRel(0,0,-1,35,4))"));
            this.operations.add(new Operation( "//replace >21 44"));
            createBreakPointOperation();
            changes++;

            // (Fix First Roof Layer) Select only air blocks that are next to stone slabs and green wool in any direction and above lapislazuli ore. Then replace them with stone slabs
            this.operations.add(new Operation( "//gmask =queryRel(0,0,0,0,0)&&(queryRel(1,0,0,44,0)||queryRel(-1,0,0,44,0)||queryRel(0,0,1,44,0)||queryRel(0,0,-1,44,0))"));
            this.operations.add(new Operation( "//replace >21 44"));
            this.operations.add(new Operation( "//replace >21 44"));
            createBreakPointOperation();
            changes++;
            changes++;

            // (Overhang Roof Layer 1) Select all air blocks next to lapislazuli ores that have a stone slab above them. Then replace them with and upside down stone slab
            this.operations.add(new Operation( "//gmask =(queryRel(1,0,0,21,-1)&&queryRel(1,1,0,44,0))||(queryRel(-1,0,0,21,-1)&&queryRel(-1,1,0,44,0))||(queryRel(0,0,1,21,-1)&&queryRel(0,1,1,44,0))||(queryRel(0,0,-1,21,-1)&&queryRel(0,1,-1,44,0))"));
            this.operations.add(new Operation( "//replace 0 44:8"));
            createBreakPointOperation();
            changes++;

            // (Overhang Roof Layer 2) Select all air blocks next to upside down stone slab and lapislazuli. Then replace them with and upside down stone slab
            this.operations.add(new Operation( "//gmask =(" +
                    "(queryRel(1,0,0,44,8)&&(queryRel(0,0,1,21,0)||queryRel(0,0,-1,21,0)))" +
                    "||(queryRel(-1,0,0,44,8)&&(queryRel(0,0,1,21,0)||queryRel(0,0,-1,21,0)))" +
                    "||(queryRel(0,0,1,44,8)&&(queryRel(1,0,0,21,0)||queryRel(-1,0,0,21,0)))" +
                    "||(queryRel(0,0,-1,44,8)&&(queryRel(1,0,0,21,0)||queryRel(-1,0,0,21,0)))" +
                    ")"));
            this.operations.add(new Operation( "//replace 0 44:8"));
            createBreakPointOperation();
            changes++;



            // Replace the highest yellow wool layer with double slabs
            this.operations.add(new Operation( "//gmask <0"));
            this.operations.add(new Operation( "//replace 35:4 43"));
            createBreakPointOperation();
            changes++;

            maxRoofHeight = maxRoofHeight - 1;

            if(maxRoofHeight > 0)
                for(int i = 0; i < maxRoofHeight; i++) {
                    if(roofType == RoofType.FLATTER_SLABS || roofType == RoofType.MEDIUM_SLABS)
                        //Only select air block that are surrounded by other stone slabs below or which are directly neighbors to green wool or blue wool
                        this.operations.add(new Operation( "//gmask =" +
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
                        ));
                    else
                        this.operations.add(new Operation( "//gmask =!(queryRel(1,-1,0,44,-1)||queryRel(-1,-1,0,44,-1)||queryRel(0,-1,1,44,-1)||queryRel(0,-1,-1,44,-1))"));

                    this.operations.add(new Operation( "//replace >43 44"));
                    createBreakPointOperation();
                    changes++;

                    if(roofType == RoofType.FLATTER_SLABS)

                        //Only select air block that are surrounded by other stone slabs or which are directly neighbors to green wool or blue wool
                        this.operations.add(new Operation( "//gmask =" +
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

                        ));
                    else
                        this.operations.add(new Operation( "//gmask =!(queryRel(1,0,0,0,-1)||queryRel(-1,0,0,0,-1)||queryRel(0,0,1,0,-1)||queryRel(0,0,-1,0,-1))"));

                    this.operations.add(new Operation( "//replace 44 43"));
                    createBreakPointOperation();
                    changes++;
                }

            // Replace everything above upside down stone slabs with purple wool
            this.operations.add(new Operation( "//gmask"));
            this.operations.add(new Operation( "//replace >44:8 35:10"));
            createBreakPointOperation();
            changes++;

            expandGreenWool();

            // (Overhang Roof Layer 3) Select all air blocks next to two upside down stone slabs. Then replace them with and upside down stone slab
            this.operations.add(new Operation( "//gmask =(queryRel(1,0,0,44,8)&&queryRel(2,0,0,44,8))||(queryRel(-1,0,0,44,8)&&queryRel(-2,0,0,44,8))||(queryRel(0,0,1,44,8)&&queryRel(0,0,2,44,8))||(queryRel(0,0,-1,44,8)&&queryRel(0,0,-2,44,8))"));
            this.operations.add(new Operation( "//replace 0 44:8"));
            changes++;

            // Select all green wool that are above & next to green wool and replace it with stone slabs
            this.operations.add(new Operation( "//gmask =queryRel(1,-1,0,35,5)||queryRel(-1,-1,0,35,5)||queryRel(0,-1,1,35,5)||queryRel(0,-1,-1,35,5)||queryRel(1,-1,1,35,5)||queryRel(-1,-1,-1,35,5)||queryRel(-1,-1,1,35,5)||queryRel(1,-1,-1,35,5)"));
            this.operations.add(new Operation( "//replace 35:5 44"));
            changes++;

            // Select all green wool that are above & next to upside down stone slabs and replace it with stone slabs
            this.operations.add(new Operation( "//gmask =queryRel(1,-1,0,44,8)||queryRel(-1,-1,0,44,8)||queryRel(0,-1,1,44,8)||queryRel(0,-1,-1,44,8)||queryRel(1,-1,1,44,8)||queryRel(-1,-1,-1,44,8)||queryRel(-1,-1,1,44,8)||queryRel(1,-1,-1,44,8)"));
            this.operations.add(new Operation( "//replace 35:5 44"));

            changes++;

            // Select all air blocks that are below & next to green wool and under a stone slab and replace it with upside down stone slabs
            this.operations.add(new Operation( "//gmask =queryRel(0,1,0,44,0)&&(queryRel(1,1,0,35,5)||queryRel(-1,1,0,35,5)||queryRel(0,1,1,35,5)||queryRel(0,1,-1,35,5)||queryRel(1,1,1,35,5)||queryRel(-1,1,-1,35,5)||queryRel(-1,1,1,35,5)||queryRel(1,1,-1,35,5))"));
            this.operations.add(new Operation( "//replace 0 44:8"));
            changes++;

            // Select all air blocks that are next to green wool and under a stone slab and replace it with upside down stone slabs
            this.operations.add(new Operation( "//gmask =queryRel(0,1,0,44,0)&&(queryRel(1,0,0,35,5)||queryRel(-1,0,0,35,5)||queryRel(0,0,1,35,5)||queryRel(0,0,-1,35,5))"));
            this.operations.add(new Operation( "//replace 0 44:8"));
            changes++;

            createBreakPointOperation();

            // Select all left over green wool replace it with double stone slabs
            this.operations.add(new Operation( "//gmask"));
            this.operations.add(new Operation( "//replace 35:5 43"));
            changes++;

            // Replace blue wool with lapislazuli ore
            this.operations.add(new Operation( "//replace 35:11 21"));
            changes++;

            createBreakPointOperation();



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
            this.operations.add(new Operation( "//gmask 0"));
            this.operations.add(new Operation( "//replace >21 171:7"));
            changes++;
            this.operations.add(new Operation( "//gmask <0"));
            this.operations.add(new Operation( "//replace 35:4 23"));
            changes++;

            createBreakPointOperation();

        } else if(roofType == RoofType.STAIRS){

            // Create the roof house wall staircase
            for(int i = 0; i < maxRoofHeight; i++){
                // Select all air blocks that are above blue wool and next to green wool
                this.operations.add(new Operation( "//gmask =queryRel(1,-1,0,35,5)||queryRel(-1,-1,0,35,5)||queryRel(0,-1,1,35,5)||queryRel(0,-1,-1,35,5)||queryRel(1,-1,1,35,5)||queryRel(-1,-1,-1,35,5)||queryRel(-1,-1,1,35,5)||queryRel(1,-1,-1,35,5)"));
                this.operations.add(new Operation( "//replace >35:11 35:5"));
                createBreakPointOperation();
                changes++;

                // Select all air blocks that are above blue wool and replace them with blue wool
                this.operations.add(new Operation( "//gmask 0"));
                this.operations.add(new Operation( "//replace >35:11 35:11"));
                createBreakPointOperation();
                changes++;
            }

            // (One more yellow wool layer) Replace everything above yellow wool with one layer yellow wool
            this.operations.add(new Operation( "//replace >35:4 35:4"));
            changes++;

            // (First Roof Layer) Select only air blocks that are next to yellow wool in any direction and above lapislazuli ore. Then replace them with stone bricks
            this.operations.add(new Operation( "//gmask =queryRel(0,0,0,0,0)&&(queryRel(1,0,0,35,4)||queryRel(1,0,0,35,4)||queryRel(-1,0,0,35,4)||queryRel(0,0,1,35,4)||queryRel(0,0,-1,35,4))"));
            this.operations.add(new Operation( "//replace >21 98"));
            changes++;

            // (Fix First Roof Layer) Select only air blocks that are next to stone slabs in any direction and above lapislazuli ore. Then replace them with stone bricks
            this.operations.add(new Operation( "//gmask =queryRel(0,0,0,0,0)&&(queryRel(1,0,0,98,0)||queryRel(1,0,0,98,0)||queryRel(-1,0,0,98,0)||queryRel(0,0,1,98,0)||queryRel(0,0,-1,98,0))"));
            this.operations.add(new Operation( "//replace >21 98"));
            this.operations.add(new Operation( "//replace >21 98"));
            changes++;
            changes++;

            // (Overhang Roof Layer) Select all air blocks next to lapislazuli ores that have a stone slab above them. Then replace them with stone bricks
            this.operations.add(new Operation( "//gmask =(queryRel(1,0,0,21,-1)&&queryRel(1,1,0,98,0))||(queryRel(-1,0,0,21,-1)&&queryRel(-1,1,0,98,0))||(queryRel(0,0,1,21,-1)&&queryRel(0,1,1,98,0))||(queryRel(0,0,-1,21,-1)&&queryRel(0,1,-1,98,0))"));
            this.operations.add(new Operation( "//replace 0 98:8"));
            changes++;

            createBreakPointOperation();


            maxRoofHeight = maxRoofHeight - 1;

            if(maxRoofHeight > 0)
                for(int i = 0; i < maxRoofHeight; i++) {
                    // Every 2nd layer
                    if(i % 2 == 0)
                        //Only select air block that have yellow wool below them which are surrounded by other stone bricks
                        this.operations.add(new Operation( "//gmask =(queryRel(1,-1,0,98,-1)||queryRel(-1,-1,0,98,-1)||queryRel(0,-1,1,98,-1)||queryRel(0,-1,-1,98,-1))"));
                    else
                        // Only select air block that have yellow wool below them which are completely surrounded by other stone bricks
                        this.operations.add(new Operation( "//gmask =(queryRel(1,-1,0,98,-1)||queryRel(-1,-1,0,98,-1)||queryRel(0,-1,1,98,-1)||queryRel(0,-1,-1,98,-1)||queryRel(1,-1,1,98,-1)||queryRel(-1,-1,1,98,-1)||queryRel(-1,-1,-1,98,-1)||queryRel(1,-1,-1,98,-1))"));

                    this.operations.add(new Operation( "//replace >35:4 98"));
                    createBreakPointOperation();
                    changes++;

                    //Only select yellow wool with air blocks above them and put yellow wool above them
                    this.operations.add(new Operation( "//gmask air"));
                    this.operations.add(new Operation( "//replace >35:4 35:4"));
                    createBreakPointOperation();
                    changes++;
                }

            // ROOF OVERHANG

            expandGreenWool();


            // Replace green wool with stone bricks
            this.operations.add(new Operation( "//gmask"));
            this.operations.add(new Operation( "//replace 35:5 98"));
            changes++;

            // Replace blue wool with lapislazuli ore
            this.operations.add(new Operation( "//replace 35:11 21"));
            createBreakPointOperation();
            changes++;

            // Fill up air blocks surrounded by 3 stone bricks with stone bricks
            this.operations.add(new Operation( "//gmask =queryRel(1,0,0,98,0)&&queryRel(-1,0,0,98,0)&&queryRel(0,0,1,98,0)&&queryRel(0,0,-1,0,0)"));
            this.operations.add(new Operation( "//replace 0 98"));
            changes++;
            this.operations.add(new Operation( "//gmask =queryRel(1,0,0,98,0)&&queryRel(-1,0,0,98,0)&&queryRel(0,0,1,0,0)&&queryRel(0,0,-1,98,0)"));
            this.operations.add(new Operation( "//replace 0 98"));
            changes++;
            this.operations.add(new Operation( "//gmask =queryRel(1,0,0,98,0)&&queryRel(-1,0,0,0,0)&&queryRel(0,0,1,98,0)&&queryRel(0,0,-1,98,0)"));
            this.operations.add(new Operation( "//replace 0 98"));
            changes++;
            this.operations.add(new Operation( "//gmask =queryRel(1,0,0,0,0)&&queryRel(-1,0,0,98,0)&&queryRel(0,0,1,98,0)&&queryRel(0,0,-1,98,0)"));
            this.operations.add(new Operation( "//replace 0 98"));
            changes++;
            createBreakPointOperation();


            // ROOF STAIRS

            // Fill the top roof gable that is surrounded by 2 stone bricks and one stone brick on top with stone bricks
            this.operations.add(new Operation( "//gmask =queryRel(0,1,0,98,0)&&(queryRel(-1,0,0,98,0)||queryRel(-1,0,1,98,0)||queryRel(-1,0,-1,98,0))&&(queryRel(1,0,0,98,0)||queryRel(1,0,1,98,0)||queryRel(1,0,-1,98,0))"));
            this.operations.add(new Operation( "//replace 0 98"));
            changes++;
            this.operations.add(new Operation( "//gmask =queryRel(0,1,0,98,0)&&(queryRel(0,0,-1,98,0)||queryRel(1,0,-1,98,0)||queryRel(-1,0,-1,98,0))&&(queryRel(0,0,1,98,0)||queryRel(1,0,1,98,0)||queryRel(-1,0,1,98,0))"));
            this.operations.add(new Operation( "//replace 0 98"));
            changes++;

            // Fill the overhang with upside-down stairs which are surrounded by 1 stone brick and one stone brick above them
            this.operations.add(new Operation( "//gmask =queryRel(0,1,0,98,0)&&queryRel(-1,0,0,98,0)"));
            this.operations.add(new Operation( "//replace 0 109:5"));
            changes++;
            this.operations.add(new Operation( "//gmask =queryRel(0,1,0,98,0)&&queryRel(1,0,0,98,0)"));
            this.operations.add(new Operation( "//replace 0 109:4"));
            changes++;
            this.operations.add(new Operation( "//gmask =queryRel(0,1,0,98,0)&&queryRel(0,0,1,98,0)"));
            this.operations.add(new Operation( "//replace 0 109:6"));
            changes++;
            this.operations.add(new Operation( "//gmask =queryRel(0,1,0,98,0)&&queryRel(0,0,-1,98,0)"));
            this.operations.add(new Operation( "//replace 0 109:7"));
            changes++;

            // Fill the remaining overhang with upside-down stairs which are surrounded by 1 stone brick and one stone brick above them
            this.operations.add(new Operation( "//gmask =queryRel(0,1,0,98,0)&&(queryRel(-1,0,0,98,0)||queryRel(-1,0,1,98,0)||queryRel(-1,0,-1,98,0))"));
            this.operations.add(new Operation( "//replace 0 109:5"));
            changes++;
            this.operations.add(new Operation( "//gmask =queryRel(0,1,0,98,0)&&(queryRel(1,0,0,98,0)||queryRel(1,0,1,98,0)||queryRel(1,0,-1,98,0))"));
            this.operations.add(new Operation( "//replace 0 109:4"));
            changes++;
            this.operations.add(new Operation( "//gmask =queryRel(0,1,0,98,0)&&(queryRel(0,0,1,98,0)||queryRel(1,0,1,98,0)||queryRel(-1,0,1,98,0))"));
            this.operations.add(new Operation( "//replace 0 109:6"));
            changes++;
            this.operations.add(new Operation( "//gmask =queryRel(0,1,0,98,0)&&(queryRel(0,0,-1,98,0)||queryRel(1,0,-1,98,0)||queryRel(-1,0,-1,98,0))"));
            this.operations.add(new Operation( "//replace 0 109:7"));
            changes++;


            // (Normal Stair Roof Layer) Replace all air blocks that have a stone brick on one side, 3 air sides and one stone brick below them with stairs
            this.operations.add(new Operation( "//gmask =(queryRel(1,0,0,98,-1)&&queryRel(-1,0,0,0,-1)&&queryRel(0,0,1,0,-1)&&queryRel(0,0,-1,0,-1))"));
            this.operations.add(new Operation( "//replace >98 109:0"));
            changes++;
            this.operations.add(new Operation( "//gmask =(queryRel(1,0,0,0,-1)&&queryRel(-1,0,0,98,-1)&&queryRel(0,0,1,0,-1)&&queryRel(0,0,-1,0,-1))"));
            this.operations.add(new Operation( "//replace >98 109:1"));
            changes++;
            this.operations.add(new Operation( "//gmask =(queryRel(1,0,0,0,-1)&&queryRel(-1,0,0,0,-1)&&queryRel(0,0,1,98,-1)&&queryRel(0,0,-1,0,-1))"));
            this.operations.add(new Operation( "//replace >98 109:2"));
            changes++;
            this.operations.add(new Operation( "//gmask =(queryRel(1,0,0,0,-1)&&queryRel(-1,0,0,0,-1)&&queryRel(0,0,1,0,-1)&&queryRel(0,0,-1,98,-1))"));
            this.operations.add(new Operation( "//replace >98 109:3"));
            changes++;

            // (Corner Stair Roof Layer) Replace all air blocks that have stone bricks on 2 sides, 2 air or stair sides and one stone brick below them with stairs
            this.operations.add(new Operation( "//gmask =(queryRel(1,0,0,98,-1)&&queryRel(0,0,1,98,-1)&&(queryRel(-1,0,0,0,-1)||queryRel(-1,0,0,109,-1))&&(queryRel(0,0,-1,0,-1)||queryRel(0,0,-1,109,-1)))"));
            this.operations.add(new Operation( "//replace >98 109:8"));
            changes++;
            this.operations.add(new Operation( "//gmask =(queryRel(1,0,0,98,-1)&&queryRel(0,0,-1,98,-1)&&(queryRel(-1,0,0,0,-1)||queryRel(-1,0,0,109,-1))&&(queryRel(0,0,1,0,-1)||queryRel(0,0,1,109,-1)))"));
            this.operations.add(new Operation( "//replace >98 109:11"));
            changes++;
            this.operations.add(new Operation( "//gmask =(queryRel(-1,0,0,98,-1)&&queryRel(0,0,1,98,-1)&&(queryRel(1,0,0,0,-1)||queryRel(1,0,0,109,-1))&&(queryRel(0,0,-1,0,-1)||queryRel(0,0,-1,109,-1)))"));
            this.operations.add(new Operation( "//replace >98 109:10"));
            changes++;
            this.operations.add(new Operation( "//gmask =(queryRel(-1,0,0,98,-1)&&queryRel(0,0,-1,98,-1)&&(queryRel(1,0,0,0,-1)||queryRel(1,0,0,109,-1))&&(queryRel(0,0,1,0,-1)||queryRel(0,0,1,109,-1)))"));
            this.operations.add(new Operation( "//replace >98 109:9"));
            changes++;


            // (Corner Stair 2 Roof Layer) Replace all air blocks that have stairs on 2 sides, 2 air sides and one stone brick below them with stairs
            this.operations.add(new Operation( "//gmask =(queryRel(1,0,0,109,-1)&&queryRel(0,0,1,109,-1)&&queryRel(-1,0,0,0,-1)&&queryRel(0,0,-1,0,-1))"));
            this.operations.add(new Operation( "//replace >98 109:10"));
            changes++;
            this.operations.add(new Operation( "//gmask =(queryRel(1,0,0,109,-1)&&queryRel(0,0,-1,109,-1)&&queryRel(-1,0,0,0,-1)&&queryRel(0,0,1,0,-1))"));
            this.operations.add(new Operation( "//replace >98 109:11"));
            changes++;
            this.operations.add(new Operation( "//gmask =(queryRel(-1,0,0,109,-1)&&queryRel(0,0,1,109,-1)&&queryRel(1,0,0,0,-1)&&queryRel(0,0,-1,0,-1))"));
            this.operations.add(new Operation( "//replace >98 109:9"));
            changes++;
            this.operations.add(new Operation( "//gmask =(queryRel(-1,0,0,109,-1)&&queryRel(0,0,-1,109,-1)&&queryRel(1,0,0,0,-1)&&queryRel(0,0,1,0,-1))"));
            this.operations.add(new Operation( "//replace >98 109:9"));
            changes++;

            // Cover leaking yellow wool blocks with stone bricks
            this.operations.add(new Operation( "//gmask =queryRel(1,0,0,35,4)||queryRel(-1,0,0,35,4)||queryRel(0,0,1,35,4)||queryRel(0,0,-1,35,4)"));
            this.operations.add(new Operation( "//replace 0 98"));
            changes++;
            createBreakPointOperation();


            // Disable the gmask
            this.operations.add(new Operation( "//gmask"));

            String[] colors = roofColor.split(",");

            // Remove :X from colors
            for(int i = 0; i < colors.length; i++)
                colors[i] = colors[i].split(":")[0];

            String[] blockColors = new String[colors.length];
            for(int i = 0; i < colors.length; i++)
                blockColors[i] = MenuItems.convertStairToBlock(colors[i]);


            // Replace stone bricks with the correct color
            this.operations.add(new Operation( "//replace 98 " + StringUtils.join(blockColors, ",")));
            createBreakPointOperation();
            changes++;


            // Replace all stairs with the correct color
            for(int i = 0; i < 12; i++) {
                if(colors.length == 1)
                    this.operations.add(new Operation( "//replace 109:" + i + " " + colors[0] + ":" + i));
                else
                    this.operations.add(new Operation( "//replace 109:" + i + " " + StringUtils.join(colors, ":" + i + ",")));

                changes++;
            }
            createBreakPointOperation();
        }


        // ----------- FINAL FINISH ----------

        this.operations.add(new Operation( "//gmask 0,45,31,37,38,39,40,175"));

        for(int i = 0; i < 5; i++) {
            this.operations.add(new Operation( "//replace <22 22"));
            changes++;
        }

        this.operations.add(new Operation( "//gmask"));
        this.operations.add(new Operation( "//replace 21 " + wallColor));
        changes++;
        this.operations.add(new Operation( "//replace 22 " + baseColor));
        changes++;
        this.operations.add(new Operation( "//replace 35:4 35:7"));
        changes++;


        this.operations.add(new Operation( "//gmask"));



        this.operations.add(new Operation( "//replace 44:0 " + rm1));
        changes++;
        this.operations.add(new Operation( "//replace 44:8 " + rm2));
        changes++;
        this.operations.add(new Operation( "//replace 43:0 " + rm3));
        changes++;
        this.operations.add(new Operation( "//replace 23 " + rm3));
        changes++;


        // Reset pos1 and pos2
        this.operations.add(new Operation( "//pos1 " + selectionPoints.get(0).getBlockX() + "," + maxY + "," + selectionPoints.get(0).getBlockZ()));
        for(int i = 1; i < selectionPoints.size(); i++)
            this.operations.add(new Operation( "//pos2 " + selectionPoints.get(i).getBlockX() + "," + minY + "," + selectionPoints.get(i).getBlockZ()));


        // Finish the script
        finish(blocks);
    }

    // Move blue, green and red wool one block up
    private void moveWoolUp(int floor) {
        this.operations.add(new Operation("//gmask"));
        this.operations.add(new Operation("//replace >35:11 35:11"));
        changes++;
        this.operations.add(new Operation("//replace >35:14 35:14"));
        changes++;
        this.operations.add(new Operation("//replace >35:5 35:5"));
        changes++;

        if(floor >= 0) {
            if (floor == 0)
                this.operations.add(new Operation("//replace >35:1,35:13 35:1"));
            else
                this.operations.add(new Operation("//replace >35:1,35:13 35:13"));
            changes++;
        }
        createBreakPointOperation();
    }

    private void raiseYellowWoolFloor() {
        this.operations.add(new Operation("//gmask"));
        this.operations.add(new Operation("//replace >35:4 35:4"));
        createBreakPointOperation();
        changes++;
    }

    private void expandGreenWool(){

        // Replace everything above green wool with temporary purple wool
        this.operations.add(new Operation("//gmask"));
        this.operations.add(new Operation("//replace >35:5 35:10"));
        changes++;

        // Replace air next to purple wool with purple wool
        this.operations.add(new Operation("//gmask =queryRel(1,0,0,35,10)||queryRel(-1,0,0,35,10)||queryRel(0,0,1,35,10)||queryRel(0,0,-1,35,10)"));
        this.operations.add(new Operation("//replace 0 35:10"));
        changes++;

        // Replace air next to green wool with green wool
        this.operations.add(new Operation("//gmask =queryRel(1,0,0,35,5)||queryRel(-1,0,0,35,5)||queryRel(0,0,1,35,5)||queryRel(0,0,-1,35,5)"));
        this.operations.add(new Operation("//replace 0 35:5"));
        changes++;

        // Replace purple wool with air
        this.operations.add(new Operation("//gmask"));
        this.operations.add(new Operation("//replace 35:10 0"));
    }
}
