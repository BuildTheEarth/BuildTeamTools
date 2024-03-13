package net.buildtheearth.modules.generator.components.house;

import com.cryptomorin.xseries.XMaterial;
import com.sk89q.worldedit.registry.state.EnumProperty;
import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import net.buildtheearth.modules.generator.model.Flag;
import net.buildtheearth.modules.generator.model.GeneratorComponent;
import net.buildtheearth.modules.generator.model.Script;
import net.buildtheearth.modules.generator.utils.GeneratorUtils;
import net.buildtheearth.utils.MenuItems;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class HouseScripts extends Script {

    public HouseScripts(Player player, GeneratorComponent generatorComponent) {
        super(player, generatorComponent);

        buildscript_v_1_2();
    }

    public void buildscript_v_1_2(){
        HashMap<Flag, Object> flags = getGeneratorComponent().getPlayerSettings().get(getPlayer().getUniqueId()).getValues();

        XMaterial wallColor = (XMaterial) flags.get(HouseFlag.WALL_COLOR);
        XMaterial roofColor = (XMaterial) flags.get(HouseFlag.ROOF_COLOR);
        XMaterial baseColor = (XMaterial) flags.get(HouseFlag.BASE_COLOR);
        XMaterial windowColor = (XMaterial) flags.get(HouseFlag.WINDOW_COLOR);
        XMaterial balconyColor = (XMaterial) flags.get(HouseFlag.BALCONY_COLOR);
        XMaterial balconyFenceColor = (XMaterial) flags.get(HouseFlag.BALCONY_FENCE_COLOR);
        RoofType roofType = (RoofType) flags.get(HouseFlag.ROOF_TYPE);

        int floorCount = (int) flags.get(HouseFlag.FLOOR_COUNT);
        int floorHeight = (int) flags.get(HouseFlag.FLOOR_HEIGHT);
        int baseHeight = (int) flags.get(HouseFlag.BASE_HEIGHT);
        int windowHeight = (int) flags.get(HouseFlag.WINDOW_HEIGHT);
        int windowWidth = (int) flags.get(HouseFlag.WINDOW_WIDTH);
        int windowDistance = (int) flags.get(HouseFlag.WINDOW_DISTANCE);
        int maxRoofHeight = (int) flags.get(HouseFlag.MAX_ROOF_HEIGHT);

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


        // Disable the global mask
        disableGmask();

        /*
        // ----------- PREPARATION 02 ----------
        // Bring the outline on the same height

        // Set pos1 and pos2
        List<Vector> outlinePoints = new ArrayList<>(selectionPoints);
        outlinePoints.get(0).setY(highestBlock);
        for(Vector point : outlinePoints)
            point.setY(minY);
        createSelection(outlinePoints);

        // Expand the current selection down by 10 blocks
        expandSelection(new Vector(0, -10, 0));

        // Replace air with sponge
        replaceBlocks(XMaterial.AIR, XMaterial.SPONGE);
        changes++;

        // Replace all sponges with bricks that have bricks below them
        for(int i = 0; i < 20; i++) {
            replaceBlocksWithExpressionMask(">45", XMaterial.SPONGE, XMaterial.BRICK);
            changes++;
        }

        // Replace all left sponges with air
        createCommand("//gmask");
        createCommand("//replace 19 0");
        changes++;


        // ----------- PREPARATION 03 ----------
        // Bring the orange, yellow, green, blue and red wool blocks to the same height

        String[] woolColors = {"35:1", "35:4", "35:11", "35:14", "35:5"};

        for(String wool : woolColors){
            // Replace all blocks above the wool
            createCommand("//gmask >" + wool);
            for(int i = 0; i < 20; i++){
                createCommand("//replace 0 " + wool);
                changes++;
            }

            // Select highest yellow wool block and replace it with sponge
            createCommand("//gmask <0");
            createCommand("//replace " + wool + " 19");
            changes++;

            // Replace yellow wool with brick
            createCommand("//gmask");
            createCommand("//replace " + wool + " 45");
            changes++;

            // Replace all sponges with yellow wool
            createCommand("//replace 19 " + wool);
            changes++;
        }

        // ----------- PREPARATION 05 ----------
        // Move blue, red and lime wool one block up and replace block below with brick

        createCommand("//expand 1 up");

        String[] woolColorsNoYellow = {"35:11", "35:14", "35:5"};
        for(String wool : woolColorsNoYellow) {
            createCommand("//gmask =queryRel(1,0,0,45,0)||queryRel(-1,0,0,45,0)||queryRel(0,0,1,45,0)||queryRel(0,0,-1,45,0)||queryRel(1,0,1,45,0)||queryRel(-1,0,1,45,0)||queryRel(1,0,-1,45,0)||queryRel(-1,0,-1,45,0)");
            createCommand("//replace " + wool + " 19");
            changes++;

            for(int i = 0; i < 10; i++){
                createCommand("//gmask =queryRel(1,0,0,19,0)||queryRel(-1,0,0,19,0)||queryRel(0,0,1,19,0)||queryRel(0,0,-1,19,0)||queryRel(1,0,1,19,0)||queryRel(-1,0,1,19,0)||queryRel(1,0,-1,19,0)||queryRel(-1,0,-1,19,0)");
                createCommand("//replace " + wool + " 19");
                changes++;
            }

            createCommand("//gmask");
            createCommand("//replace >19 " + wool);
            changes++;
            createCommand("//replace 19 45");
            changes++;
        }



        // ----------- PREPARATION 06 ----------
        // Replace all bricks underground with grass

        // Replace all bricks with sponge
        createCommand("//replace 45 19");
        changes++;


        // Replace all sponges with bricks that have air or red wool or blue wool or green wool above them
        createCommand("//gmask =(queryRel(0,1,0,0,0)||queryRel(0,1,0,35,11)||queryRel(0,1,0,35,14)||queryRel(0,1,0,35,5))");
        createCommand("//replace 19 45");

        changes++;

        // Disable the global mask
        createCommand("//gmask");

        // Replace all left sponges with grass
        createCommand("//replace 19 2");
        changes++;


        // ----------- PREPARATION 07 ----------
        // Expand the blue wool until it reaches the green wool

        // Select all blocks that are next to blue wool
        createCommand("//gmask =(queryRel(1,0,0,35,11)||queryRel(-1,0,0,35,11)||queryRel(0,0,1,35,11)||queryRel(0,0,-1,35,11)||queryRel(1,0,1,35,11)||queryRel(-1,0,-1,35,11)||queryRel(-1,0,1,35,11)||queryRel(1,0,-1,35,11))&&!(queryRel(1,0,0,35,5)||queryRel(-1,0,0,35,5)||queryRel(0,0,1,35,5)||queryRel(0,0,-1,35,5)||queryRel(1,0,1,35,5)||queryRel(-1,0,-1,35,5)||queryRel(-1,0,1,35,5)||queryRel(1,0,-1,35,5))");

        for(int i = 0; i < 20; i++) {
            // Replace all blocks above lapislazuli with blue wool
            createCommand("//replace >45 35:11");
            changes++;
        }

        // Place the last blue wool between the green and the blue wool
        createCommand("//gmask =queryRel(1,0,0,35,11)||queryRel(-1,0,0,35,11)||queryRel(0,0,1,35,11)||queryRel(0,0,-1,35,11)||queryRel(1,0,1,35,11)||queryRel(-1,0,-1,35,11)||queryRel(-1,0,1,35,11)||queryRel(1,0,-1,35,11)");
        createCommand("//replace >45 35:11");
        changes++;
        */

        // ----------- GROUND ----------

        // Set pos1 and pos2
        List<Vector> outlinePoints = new ArrayList<>(selectionPoints);
        outlinePoints.get(0).setY(maxY);
        for(Vector point : outlinePoints)
            point.setY(minY);
        createSelection(outlinePoints);

        // Expand the current selection down by 10 blocks
        expandSelection(new Vector(0, -10, 0));

        // Expand the current selection up by "up_expand" blocks
        int up_expand = 5 + maxRoofHeight + baseHeight + (floorCount * floorHeight);
        expandSelection(new Vector(0, up_expand, 0));

        // Replace yellow wool with sponge
        replaceBlocks(XMaterial.YELLOW_WOOL, XMaterial.SPONGE);

        // Replace all blocks that are not bricks around the sponge block with sponge
        setBlocksWithMask("=!queryRel(0,0,0,45,0)&&(queryRel(-1,0,0,19,0)||queryRel(1,0,0,19,0)||queryRel(0,0,1,19,0)||queryRel(0,0,-1,19,0))",
                XMaterial.SPONGE, 20);
        changes+=20;


        // Make the outline as thin as possible and fill all inner corners with yellow wool that are too thick
        String[] queries = {
            "=queryRel(-1,0,0,19,0)&&queryRel(0,0,-1,19,0)&&queryRel(0,0,1,45,-1)&&queryRel(1,0,0,45,-1)",
            "=queryRel(-1,0,0,19,0)&&queryRel(0,0,1,19,0)&&queryRel(0,0,-1,45,-1)&&queryRel(1,0,0,45,-1)",
            "=queryRel(1,0,0,19,0)&&queryRel(0,0,-1,19,0)&&queryRel(0,0,1,45,-1)&&queryRel(-1,0,0,45,-1)",
            "=queryRel(1,0,0,19,0)&&queryRel(0,0,1,19,0)&&queryRel(0,0,-1,45,-1)&&queryRel(-1,0,0,45,-1)",
            "=queryRel(-1,0,0,19,0)&&queryRel(0,0,-1,45,-1)&&queryRel(0,0,1,45,-1)&&queryRel(1,0,0,45,-1)",
            "=queryRel(1,0,0,19,0)&&queryRel(0,0,1,45,-1)&&queryRel(0,0,-1,45,-1)&&queryRel(-1,0,0,45,-1)",
            "=queryRel(1,0,0,45,-1)&&queryRel(0,0,-1,19,0)&&queryRel(0,0,1,45,-1)&&queryRel(-1,0,0,45,-1)",
            "=queryRel(1,0,0,45,-1)&&queryRel(0,0,1,19,0)&&queryRel(0,0,-1,45,-1)&&queryRel(-1,0,0,45,-1)"
        };
        replaceBlocksWithMask(Arrays.asList(queries), XMaterial.RED_WOOL, XMaterial.YELLOW_WOOL, 2);


        // Replace sponge with yellow wool
        replaceBlocks(XMaterial.SPONGE, XMaterial.YELLOW_WOOL);



        // ----------- BASE ----------
        int currentHeight = 0;

        if(baseHeight > 0)
            for(int i = 0; i < baseHeight; i++) {
                currentHeight++;

                // Move wool one block up
                moveWoolUp(0);

                // Select everything x blocks above bricks. Then replace that with lapislazuli
                setBlocksWithMask("=queryRel(0," + (-currentHeight) + ",0,45,-1)", XMaterial.LAPIS_ORE);
                changes++;

                // Raise the yellow wool layer by one block
                raiseYellowWoolFloor();
            }





        // ----------- FLOORS ----------
        int heightDifference = 0;
        for(int i = 0; i < floorCount; i++) {
            Bukkit.broadcastMessage("Floor " + i + " of " + floorCount);

            currentHeight++;

            // Move wool one block up
            moveWoolUp(i+1);

            // Select everything x blocks above bricks. Then replace that with lapislazuli ore
            setBlocksWithMask("=queryRel(0," + (-currentHeight) + ",0,45,-1)", XMaterial.LAPIS_ORE);
            changes++;

            // Raise the yellow wool layer by one block
            raiseYellowWoolFloor();

            // Windows
            for(int i2 = 0; i2 < windowHeight; i2++) {
                currentHeight++;

                // Move wool one block up
                moveWoolUp(0);


                if (!containsRedWool) {
                    // Replace everything with white glass
                    setBlocksWithMask("=queryRel(0," + (-currentHeight) + ",0,45,-1)", XMaterial.WHITE_STAINED_GLASS);
                    changes++;
                }else {
                    // Replace red wool with gray glass
                    replaceBlocksWithMask("=queryRel(0," + (-currentHeight) + ",0,45,-1)", XMaterial.RED_WOOL, XMaterial.GRAY_STAINED_GLASS);
                    changes++;

                    // Replace air with lapislazuli ore
                    replaceBlocksWithMask("=queryRel(0," + (-currentHeight) + ",0,45,-1)", XMaterial.AIR, XMaterial.LAPIS_ORE);
                    changes++;

                    // Replace blue wool with lapislazuli ore
                    replaceBlocksWithMask("=queryRel(0," + (-currentHeight) + ",0,45,-1)", XMaterial.BLUE_WOOL, XMaterial.LAPIS_ORE);
                    changes++;

                    // Replace green wool with lapislazuli ore
                    replaceBlocksWithMask("=queryRel(0," + (-currentHeight) + ",0,45,-1)", XMaterial.GREEN_WOOL, XMaterial.LAPIS_ORE);
                    changes++;
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
                    setBlocksWithMask("=queryRel(0," + (-currentHeight) + ",0,45,-1)", XMaterial.LAPIS_ORE);
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
            setBlocksWithMask("=queryRel(0," + (-currentHeight) + ",0,45,-1)", XMaterial.LAPIS_ORE);
            changes++;

            // Raise the yellow wool layer by one block
            raiseYellowWoolFloor();
        }

        // Remove the red wool
        replaceBlocks(XMaterial.RED_WOOL, XMaterial.AIR);
        changes++;


        finish(blocks);


        // ----------- BALCONY 1/2 ----------

        if(containsOrangeWool){
            // Replace all orange wool with air
            replaceBlocks(XMaterial.ORANGE_WOOL, XMaterial.AIR);
        }


        // ----------- WINDOWS ----------
        int windowSum = windowWidth + windowDistance;
        int wd2 = windowDistance - 1;

        queries = new String[]{
            // Any white glass block whose z%6 remainder > 1, and which has air in the x+1 direction, and has no air in the x-1 direction, and has no white glass in the x-1 direction, is replaced with gray glass.
            "=(abs(z%" + windowSum + ")-" + wd2 + ")&&queryRel(1,0,0,0,-1)&&!queryRel(-1,0,0,0,-1)&&!queryRel(-1,0,0,95,-1)",

            // Any white glass block whose z%6 remainder > 1, and which has air in the x-1 direction, and has no air in the x+1 direction, and has no white glass in the x+1 direction, is replaced with gray glass.
            "=(abs(z%" + windowSum + ")-" + wd2 + ")&&queryRel(-1,0,0,0,-1)&&!queryRel(1,0,0,0,-1)&&!queryRel(1,0,0,95,-1)",

            // Any white glass block whose x%6 remainder > 1, and which has air in the z+1 direction, and has no air in the z-1 direction, and has no white glass in the z-1 direction, is replaced with gray glass.
            "=(abs(x%" + windowSum + ")-" + wd2 + ")&&queryRel(0,0,1,0,-1)&&!queryRel(0,0,-1,0,-1)&&!queryRel(0,0,-1,95,-1)",

            // Any white glass block whose x%6 remainder > 1, and which has air in the z-1 direction, and has no air in the z+1 direction, and has no white glass in the z+1 direction, is replaced with gray glass.
            "=(abs(x%" + windowSum + ")-" + wd2 + ")&&queryRel(0,0,-1,0,-1)&&!queryRel(0,0,1,0,-1)&&!queryRel(0,0,1,95,-1)"
        };

        replaceBlocksWithMask(Arrays.asList(queries), XMaterial.WHITE_STAINED_GLASS, XMaterial.GRAY_STAINED_GLASS, 1);
        changes++;


        // Replace any white glass with lapislazuli ore
        replaceBlocks(XMaterial.WHITE_STAINED_GLASS, XMaterial.LAPIS_ORE);
        changes++;

        // Replace any gray glass with the window color
        replaceBlocks(XMaterial.GRAY_STAINED_GLASS, windowColor);
        changes++;




        // ----------- BALCONY 2/2 ----------

        if(containsOrangeWool){

            // Replace all green wool with sponge
            replaceBlocks(XMaterial.GREEN_WOOL, XMaterial.SPONGE);

            // Replace all blocks that have sponge below them and have air next to them with the balcony color
            setBlocksWithMask("//gmask =queryRel(0,-1,0,19)&&(queryRel(-1,-1,0,0,0)||queryRel(1,-1,0,0,0)||queryRel(0,-1,-1,0,0)||queryRel(0,-1,1,0,0))",
                    balconyFenceColor);

            // If the balcony fence color is enabled, replace sponge with the balcony color
            if(balconyColor != null)
                replaceBlocks(XMaterial.SPONGE, balconyColor);
        }


        // ----------- ROOF ----------

        XMaterial rm1 = roofColor;
        XMaterial rm2 = roofColor;
        XMaterial rm3 = roofColor;

        // Replace the lime wool with emerald ore because FAWE doesn't support data values in queryRels in 2.9.1
        replaceBlocks(XMaterial.LIME_WOOL, XMaterial.EMERALD_ORE);

        // Replace the blue wool with diamond ore because FAWE doesn't support data values in queryRels in 2.9.1
        replaceBlocks(XMaterial.BLUE_WOOL, XMaterial.DIAMOND_ORE);

        // Replace the yellow wool with gold ore because FAWE doesn't support data values in queryRels in 2.9.1
        replaceBlocks(XMaterial.YELLOW_WOOL, XMaterial.GOLD_ORE);


        if(roofType == RoofType.FLATTER_SLABS || roofType == RoofType.STEEP_SLABS|| roofType == RoofType.MEDIUM_SLABS){
            
            // Replace the diamond ore next to emerald ore with emerald ore
            replaceBlocksWithMask("=queryRel(1,0,0,129,0)||queryRel(-1,0,0,129,0)||queryRel(0,0,1,129,0)||queryRel(0,0,-1,129,0)||queryRel(1,0,1,129,0)||queryRel(-1,0,-1,129,0)||queryRel(-1,0,1,129,0)||queryRel(1,0,-1,129,0)",
                    XMaterial.DIAMOND_ORE, XMaterial.EMERALD_ORE);
            changes++;

            // Create the roof house wall staircase
            for(int i = 0; i < maxRoofHeight; i++){
                // Select all air blocks that are above diamond ore and above & next to emerald ore
                replaceBlocksWithMask("=queryRel(0,-1,0,56,0)&&(queryRel(1,-1,0,129,0)||queryRel(-1,-1,0,129,0)||queryRel(0,-1,1,129,0)||queryRel(0,-1,-1,129,0)||queryRel(1,-1,1,129,0)||queryRel(-1,-1,-1,129,0)||queryRel(-1,-1,1,129,0)||queryRel(1,-1,-1,129,0))",
                        XMaterial.DIAMOND_ORE, XMaterial.EMERALD_ORE);
                changes++;

                // Select all air blocks that are above diamond ore and next to emerald ore
                replaceBlocksWithMask("=queryRel(0,-1,0,56,0)&&(queryRel(1,0,0,129,0)||queryRel(-1,0,0,129,0)||queryRel(0,0,1,129,0)||queryRel(0,0,-1,129,0)||queryRel(1,0,1,129,0)||queryRel(-1,0,-1,129,0)||queryRel(-1,0,1,129,0)||queryRel(1,0,-1,129,0))",
                        XMaterial.DIAMOND_ORE, XMaterial.EMERALD_ORE);
                changes++;

                // Select all air blocks that are above diamond ore and replace them with diamond ore
                setBlocksWithMask(">56", XMaterial.DIAMOND_ORE);
                changes++;
            }


            // (One more gold ore layer) Replace everything above gold ore with one layer gold ore
            setBlocksWithMask(">14", XMaterial.GOLD_ORE);
            changes++;

            // (First Roof Layer) Select only air blocks that are next to gold ore in any direction and above lapislazuli ore. Then replace them with stone slabs
            replaceBlocksWithMask("=queryRel(0,-1,0,21,0)&&queryRel(0,0,0,0,0)&&(queryRel(1,0,0,14,0)||queryRel(1,0,0,14,0)||queryRel(-1,0,0,14,0)||queryRel(0,0,1,14,0)||queryRel(0,0,-1,14,0))",
                XMaterial.LAPIS_ORE, XMaterial.STONE_SLAB);
            changes++;

            // (Fix First Roof Layer) Select only air blocks that are next to stone slabs and emerald ore in any direction and above lapislazuli ore. Then replace them with stone slabs
            replaceBlocksWithMask("=queryRel(0,-1,0,21,0)&&queryRel(0,0,0,0,0)&&(queryRel(1,0,0,44,0)||queryRel(-1,0,0,44,0)||queryRel(0,0,1,44,0)||queryRel(0,0,-1,44,0))",
                    XMaterial.LAPIS_ORE, XMaterial.STONE_SLAB, 2);
            changes+=2;

            // (Overhang Roof Layer 1) Select all air blocks next to lapislazuli ores that have a stone slab above them. Then replace them with and upside down stone slab
            setBlocksWithMask("=queryRel(0,0,0,0,0)&&" +
                    "(" +
                        "(queryRel(1,0,0,21,-1)&&queryRel(1,1,0,44,0))||" +
                        "(queryRel(-1,0,0,21,-1)&&queryRel(-1,1,0,44,0))||" +
                        "(queryRel(0,0,1,21,-1)&&queryRel(0,1,1,44,0))||" +
                        "(queryRel(0,0,-1,21,-1)&&queryRel(0,1,-1,44,0))" +
                    ")",
                    getStoneSlab("top"));
            changes++;

            // (Overhang Roof Layer 2) Select all air blocks next to upside down stone slab and lapislazuli. Then replace them with and upside down stone slab
            setBlocksWithMask("=queryRel(0,0,0,0,0)&&" +
                    "(" +
                        "(queryRel(1,0,0,44,8)&&(queryRel(0,0,1,21,0)||queryRel(0,0,-1,21,0)))||" +
                        "(queryRel(-1,0,0,44,8)&&(queryRel(0,0,1,21,0)||queryRel(0,0,-1,21,0)))||" +
                        "(queryRel(0,0,1,44,8)&&(queryRel(1,0,0,21,0)||queryRel(-1,0,0,21,0)))||" +
                        "(queryRel(0,0,-1,44,8)&&(queryRel(1,0,0,21,0)||queryRel(-1,0,0,21,0)))" +
                    ")",
                    getStoneSlab("top"));
            changes++;



            // Replace the highest gold ore layer with double slabs
            replaceBlocksWithMask("<0", XMaterial.GOLD_ORE, XMaterial.STONE);
            changes++;

            maxRoofHeight = maxRoofHeight - 1;

            if(maxRoofHeight > 0)
                for(int i = 0; i < maxRoofHeight; i++) {
                    if(roofType == RoofType.FLATTER_SLABS || roofType == RoofType.MEDIUM_SLABS)
                        //Only select air block that are surrounded by other stone slabs below or which are directly neighbors to emerald ore or diamond ore
                        replaceBlocksWithMask("=queryRel(0,-1,0,43)&&" +
                            "(" +
                                "(" +
                                    "queryRel(1,-1,0,43,-1)&&queryRel(-1,-1,0,43,-1)&&queryRel(0,-1,1,43,-1)&&queryRel(0,-1,-1,43,-1)" +
                                    "&&(queryRel(-1,-1,1,43,-1)||queryRel(-1,0,1,129,0)||queryRel(-1,0,1,56,0))" +
                                    "&&(queryRel(1,-1,-1,43,-1)||queryRel(1,0,-1,129,0)||queryRel(1,0,-1,56,0))" +
                                    "&&(queryRel(1,-1,1,43,-1)||queryRel(1,0,1,129,0)||queryRel(1,0,1,56,0))" +
                                    "&&(queryRel(-1,-1,-1,43,-1)||queryRel(-1,0,-1,129,0)||queryRel(-1,0,-1,56,0))" +
                                ")" +
                                "||queryRel(1,0,0,129,0)||queryRel(1,0,0,56,0)" +
                                "||queryRel(-1,0,0,129,0)||queryRel(-1,0,0,56,0)" +
                                "||queryRel(0,0,1,129,0)||queryRel(0,0,1,56,0)" +
                                "||queryRel(0,0,-1,129,0)||queryRel(0,0,-1,56,0)" +
                            ")",
                            getStoneSlab("double"), getStoneSlab("bottom")
                        );
                    else
                        replaceBlocksWithMask("=!(queryRel(1,-1,0,44,-1)||queryRel(-1,-1,0,44,-1)||queryRel(0,-1,1,44,-1)||queryRel(0,-1,-1,44,-1))",
                                getStoneSlab("double"), getStoneSlab("bottom"));

                    changes++;

                    if(roofType == RoofType.FLATTER_SLABS)

                        //Only select air block that are surrounded by other stone slabs or which are directly neighbors to emerald ore or diamond ore
                        replaceBlocksWithMask(
                            "(" +
                                    "queryRel(1,0,0,44,-1)&&queryRel(-1,0,0,44,-1)&&queryRel(0,0,1,44,-1)&&queryRel(0,0,-1,44,-1)" +
                                    "&&(queryRel(-1,0,1,44,-1)||queryRel(-1,0,1,129,0)||queryRel(-1,0,1,56,0))" +
                                    "&&(queryRel(1,0,-1,44,-1)||queryRel(1,0,-1,129,0)||queryRel(1,0,-1,56,0))" +
                                    "&&(queryRel(1,0,1,44,-1)||queryRel(1,0,1,129,0)||queryRel(1,0,1,56,0))" +
                                    "&&(queryRel(-1,0,-1,44,-1)||queryRel(-1,0,-1,129,0)||queryRel(-1,0,-1,56,0))" +
                                ")" +
                                "||queryRel(1,0,0,129,0)||queryRel(1,0,0,56,0)" +
                                "||queryRel(-1,0,0,129,0)||queryRel(-1,0,0,56,0)" +
                                "||queryRel(0,0,1,129,0)||queryRel(0,0,1,56,0)" +
                                "||queryRel(0,0,-1,129,0)||queryRel(0,0,-1,56,0)",
                            getStoneSlab("bottom"), getStoneSlab("double")
                        );
                    else
                        replaceBlocksWithMask("=!(queryRel(1,0,0,0,-1)||queryRel(-1,0,0,0,-1)||queryRel(0,0,1,0,-1)||queryRel(0,0,-1,0,-1))"
                            ,getStoneSlab("bottom"), getStoneSlab("double"));

                    changes++;
                }

            // Replace everything above upside down stone slabs with purple wool
            setBlocksWithMask(">44:8", XMaterial.REDSTONE_ORE);
            changes++;

            expandEmeraldOre();

            // (Overhang Roof Layer 3) Select all air blocks next to two upside down stone slabs. Then replace them with and upside down stone slab
            replaceBlocksWithMask("=(queryRel(1,0,0,44,8)&&queryRel(2,0,0,44,8))||(queryRel(-1,0,0,44,8)&&queryRel(-2,0,0,44,8))||(queryRel(0,0,1,44,8)&&queryRel(0,0,2,44,8))||(queryRel(0,0,-1,44,8)&&queryRel(0,0,-2,44,8))",
                getAir(), getStoneSlab("top"));
            changes++;

            // Select all emerald ore that are above & next to emerald ore and replace it with stone slabs
            replaceBlocksWithMask("=queryRel(1,-1,0,129,0)||queryRel(-1,-1,0,129,0)||queryRel(0,-1,1,129,0)||queryRel(0,-1,-1,129,0)||queryRel(1,-1,1,129,0)||queryRel(-1,-1,-1,129,0)||queryRel(-1,-1,1,129,0)||queryRel(1,-1,-1,129,0)",
                XMaterial.EMERALD_ORE, XMaterial.STONE_SLAB);
            changes++;

            // Select all emerald ore that are above & next to upside down stone slabs and replace it with stone slabs
            replaceBlocksWithMask("=queryRel(1,-1,0,44,8)||queryRel(-1,-1,0,44,8)||queryRel(0,-1,1,44,8)||queryRel(0,-1,-1,44,8)||queryRel(1,-1,1,44,8)||queryRel(-1,-1,-1,44,8)||queryRel(-1,-1,1,44,8)||queryRel(1,-1,-1,44,8)",
                XMaterial.EMERALD_ORE, XMaterial.STONE_SLAB);

            changes++;

            // Select all air blocks that are below & next to emerald ore and under a stone slab and replace it with upside down stone slabs
            replaceBlocksWithMask("=queryRel(0,1,0,44,0)&&(queryRel(1,1,0,129,0)||queryRel(-1,1,0,129,0)||queryRel(0,1,1,129,0)||queryRel(0,1,-1,129,0)||queryRel(1,1,1,129,0)||queryRel(-1,1,-1,129,0)||queryRel(-1,1,1,129,0)||queryRel(1,1,-1,129,0))",
                getAir(), getStoneSlab("top"));
            changes++;

            // Select all air blocks that are next to emerald ore and under a stone slab and replace it with upside down stone slabs
            replaceBlocksWithMask("=queryRel(0,1,0,44,0)&&(queryRel(1,0,0,129,0)||queryRel(-1,0,0,129,0)||queryRel(0,0,1,129,0)||queryRel(0,0,-1,129,0))",
                getAir(), getStoneSlab("top"));
            changes++;


            // Select all left over emerald ore replace it with double stone slabs
            replaceBlocks(getEmeraldOre(), getStoneSlab("double"));
            changes++;

            // Replace diamond ore with lapislazuli ore
            replaceBlocks(XMaterial.DIAMOND_ORE, XMaterial.LAPIS_ORE);
            changes++;




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
            replaceBlocksWithMask(">21", XMaterial.AIR, XMaterial.GRAY_CARPET);
            changes++;

            replaceBlocksWithMask("<0", XMaterial.GOLD_ORE, XMaterial.DISPENSER);
            changes++;

        } else if(roofType == RoofType.STAIRS){

            // Create the roof house wall staircase
            for(int i = 0; i < maxRoofHeight; i++){
                // Select all air blocks that are above diamond ore and next to emerald ore
                setBlocksWithMask("=queryRel(0,-1,0,56,0)&&" +
                        "(" +
                            "queryRel(1,-1,0,129,0)||queryRel(-1,-1,0,129,0)||queryRel(0,-1,1,129,0)||queryRel(0,-1,-1,129,0)||queryRel(1,-1,1,129,0)||queryRel(-1,-1,-1,129,0)||queryRel(-1,-1,1,129,0)||queryRel(1,-1,-1,129,0" +
                        ")",
                    XMaterial.EMERALD_ORE);
                changes++;

                // Select all air blocks that are above diamond ore and replace them with diamond ore
                replaceBlocksWithMask(">56", XMaterial.AIR, XMaterial.DIAMOND_ORE);
                changes++;
            }

            // (One more gold ore layer) Replace everything above gold ore with one layer gold ore
            setBlocksWithMask(">14", XMaterial.GOLD_ORE);
            changes++;

            // (First Roof Layer) Select only air blocks that are next to gold ore in any direction and above lapislazuli ore. Then replace them with stone bricks
            setBlocksWithMask("=queryRel(0,-1,0,21,0)&&queryRel(0,0,0,0,0)&&(queryRel(1,0,0,14,0)||queryRel(1,0,0,14,0)||queryRel(-1,0,0,14,0)||queryRel(0,0,1,14,0)||queryRel(0,0,-1,14,0))",
                XMaterial.STONE_BRICKS);
            changes++;

            // (Fix First Roof Layer) Select only air blocks that are next to stone slabs in any direction and above lapislazuli ore. Then replace them with stone bricks
            setBlocksWithMask("=queryRel(0,-1,0,21,0)&&queryRel(0,0,0,0,0)&&(queryRel(1,0,0,98,0)||queryRel(1,0,0,98,0)||queryRel(-1,0,0,98,0)||queryRel(0,0,1,98,0)||queryRel(0,0,-1,98,0))",
                XMaterial.STONE_BRICKS, 2);
            changes++;
            changes++;

            // (Overhang Roof Layer) Select all air blocks next to lapislazuli ores that have a stone slab above them. Then replace them with stone bricks
            replaceBlocksWithMask("=(queryRel(1,0,0,21,-1)&&queryRel(1,1,0,98,0))||(queryRel(-1,0,0,21,-1)&&queryRel(-1,1,0,98,0))||(queryRel(0,0,1,21,-1)&&queryRel(0,1,1,98,0))||(queryRel(0,0,-1,21,-1)&&queryRel(0,1,-1,98,0))",
                XMaterial.AIR, XMaterial.STONE_BRICKS);
            changes++;


            maxRoofHeight = maxRoofHeight - 1;

            if(maxRoofHeight > 0)
                for(int i = 0; i < maxRoofHeight; i++) {
                    // Every 2nd layer
                    if(i % 2 == 0)
                        //Only select air block that have gold ore below them which are surrounded by other stone bricks
                        setBlocksWithMask("=queryRel(0,-1,0,14,0)&&(queryRel(1,-1,0,98,-1)||queryRel(-1,-1,0,98,-1)||queryRel(0,-1,1,98,-1)||queryRel(0,-1,-1,98,-1))",
                                XMaterial.STONE_BRICKS);
                    else
                        // Only select air block that have gold ore below them which are completely surrounded by other stone bricks
                        setBlocksWithMask("=queryRel(0,-1,0,14,0)&&(queryRel(1,-1,0,98,-1)||queryRel(-1,-1,0,98,-1)||queryRel(0,-1,1,98,-1)||queryRel(0,-1,-1,98,-1)||queryRel(1,-1,1,98,-1)||queryRel(-1,-1,1,98,-1)||queryRel(-1,-1,-1,98,-1)||queryRel(1,-1,-1,98,-1))",
                                XMaterial.STONE_BRICKS);

                    changes++;

                    //Only select gold ore with air blocks above them and put gold ore above them
                    replaceBlocksWithMask(">14", XMaterial.AIR, XMaterial.GOLD_ORE);
                    changes++;
                }

            // ROOF OVERHANG

            expandEmeraldOre();


            // Replace emerald ore with stone bricks
            replaceBlocks(XMaterial.EMERALD_ORE, XMaterial.STONE_BRICKS);
            changes++;

            // Replace diamond ore with lapislazuli ore
            replaceBlocks(XMaterial.DIAMOND_ORE, XMaterial.LAPIS_ORE);
            changes++;

            queries = new String[]{
                    // Fill up air blocks surrounded by 3 stone bricks with stone bricks
                    "=queryRel(1,0,0,98,0)&&queryRel(-1,0,0,98,0)&&queryRel(0,0,1,98,0)&&queryRel(0,0,-1,0,0)",
                    "=queryRel(1,0,0,98,0)&&queryRel(-1,0,0,98,0)&&queryRel(0,0,1,0,0)&&queryRel(0,0,-1,98,0)",
                    "=queryRel(1,0,0,98,0)&&queryRel(-1,0,0,0,0)&&queryRel(0,0,1,98,0)&&queryRel(0,0,-1,98,0)",
                    "=queryRel(1,0,0,0,0)&&queryRel(-1,0,0,98,0)&&queryRel(0,0,1,98,0)&&queryRel(0,0,-1,98,0)",

                    // Fill the top roof gable that is surrounded by 2 stone bricks and one stone brick on top with stone bricks
                    "//gmask =queryRel(0,1,0,98,0)&&(queryRel(-1,0,0,98,0)||queryRel(-1,0,1,98,0)||queryRel(-1,0,-1,98,0))&&(queryRel(1,0,0,98,0)||queryRel(1,0,1,98,0)||queryRel(1,0,-1,98,0))",
                    "//gmask =queryRel(0,1,0,98,0)&&(queryRel(0,0,-1,98,0)||queryRel(1,0,-1,98,0)||queryRel(-1,0,-1,98,0))&&(queryRel(0,0,1,98,0)||queryRel(1,0,1,98,0)||queryRel(-1,0,1,98,0))"
            };
            replaceBlocksWithMask(Arrays.asList(queries), XMaterial.AIR, XMaterial.STONE_BRICKS, 1);


            // ROOF STAIRS

            // Fill the overhang with upside-down stairs which are surrounded by 1 stone brick and one stone brick above them
            replaceBlocksWithMask("=queryRel(0,1,0,98,0)&&queryRel(1,0,0,98,0)",
                    getAir(), getStoneStair("east", "top", "straight"));
            changes++;
            replaceBlocksWithMask("=queryRel(0,1,0,98,0)&&queryRel(-1,0,0,98,0)",
                    getAir(), getStoneStair("west", "top", "straight"));
            changes++;
            replaceBlocksWithMask("=queryRel(0,1,0,98,0)&&queryRel(0,0,1,98,0)",
                    getAir(), getStoneStair("south", "top", "straight"));
            changes++;
            replaceBlocksWithMask("=queryRel(0,1,0,98,0)&&queryRel(0,0,-1,98,0)",
                    getAir(), getStoneStair("north", "top", "straight"));
            changes++;

            // Fill the remaining overhang with upside-down stairs which are surrounded by 1 stone brick and one stone brick above them
            replaceBlocksWithMask("=queryRel(0,1,0,98,0)&&(queryRel(1,0,0,98,0)||queryRel(1,0,1,98,0)||queryRel(1,0,-1,98,0))",
                    getAir(), getStoneStair("east", "top", "straight"));
            changes++;
            replaceBlocksWithMask("=queryRel(0,1,0,98,0)&&(queryRel(-1,0,0,98,0)||queryRel(-1,0,1,98,0)||queryRel(-1,0,-1,98,0))",
                    getAir(), getStoneStair("west", "top", "straight"));
            changes++;
            replaceBlocksWithMask("=queryRel(0,1,0,98,0)&&(queryRel(0,0,1,98,0)||queryRel(1,0,1,98,0)||queryRel(-1,0,1,98,0))",
                    getAir(), getStoneStair("south", "top", "straight"));
            changes++;
            replaceBlocksWithMask("=queryRel(0,1,0,98,0)&&(queryRel(0,0,-1,98,0)||queryRel(1,0,-1,98,0)||queryRel(-1,0,-1,98,0))",
                    getAir(), getStoneStair("north", "top", "straight"));
            changes++;


            // (Normal Stair Roof Layer) Replace all air blocks that have a stone brick on one side, 3 air sides and one stone brick below them with stairs
            replaceBlocksWithMask("//gmask =queryRel(0,-1,0,98,0)&&(queryRel(1,0,0,98,-1)&&queryRel(-1,0,0,0,-1)&&queryRel(0,0,1,0,-1)&&queryRel(0,0,-1,0,-1))",
                    getAir(), getStoneStair("east", "bottom", "straight"));
            changes++;
            replaceBlocksWithMask("//gmask =queryRel(0,-1,0,98,0)&&(queryRel(1,0,0,0,-1)&&queryRel(-1,0,0,98,-1)&&queryRel(0,0,1,0,-1)&&queryRel(0,0,-1,0,-1))",
                    getAir(), getStoneStair("west", "bottom", "straight"));
            changes++;
            replaceBlocksWithMask("//gmask =queryRel(0,-1,0,98,0)&&(queryRel(1,0,0,0,-1)&&queryRel(-1,0,0,0,-1)&&queryRel(0,0,1,98,-1)&&queryRel(0,0,-1,0,-1))",
                    getAir(), getStoneStair("south", "bottom", "straight"));
            changes++;
            replaceBlocksWithMask("//gmask =queryRel(0,-1,0,98,0)&&(queryRel(1,0,0,0,-1)&&queryRel(-1,0,0,0,-1)&&queryRel(0,0,1,0,-1)&&queryRel(0,0,-1,98,-1))",
                    getAir(), getStoneStair("north", "bottom", "straight"));
            changes++;

            // (Corner Stair Roof Layer) Replace all air blocks that have stone bricks on 2 sides, 2 air or stair sides and one stone brick below them with stairs
            replaceBlocksWithMask("//gmask =(queryRel(1,0,0,98,-1)&&queryRel(0,0,1,98,-1)&&(queryRel(-1,0,0,0,-1)||queryRel(-1,0,0,109,-1))&&(queryRel(0,0,-1,0,-1)||queryRel(0,0,-1,109,-1)))",
                    getAir(), getStoneStair("east", "bottom", "straight"));
            changes++;
            replaceBlocksWithMask("//gmask =(queryRel(-1,0,0,98,-1)&&queryRel(0,0,-1,98,-1)&&(queryRel(1,0,0,0,-1)||queryRel(1,0,0,109,-1))&&(queryRel(0,0,1,0,-1)||queryRel(0,0,1,109,-1)))",
                    getAir(), getStoneStair("west", "bottom", "straight"));
            changes++;
            replaceBlocksWithMask("//gmask =(queryRel(-1,0,0,98,-1)&&queryRel(0,0,1,98,-1)&&(queryRel(1,0,0,0,-1)||queryRel(1,0,0,109,-1))&&(queryRel(0,0,-1,0,-1)||queryRel(0,0,-1,109,-1)))",
                    getAir(), getStoneStair("south", "bottom", "straight"));
            changes++;
            replaceBlocksWithMask("//gmask =(queryRel(1,0,0,98,-1)&&queryRel(0,0,-1,98,-1)&&(queryRel(-1,0,0,0,-1)||queryRel(-1,0,0,109,-1))&&(queryRel(0,0,1,0,-1)||queryRel(0,0,1,109,-1)))",
                    getAir(), getStoneStair("north", "bottom", "straight"));
            changes++;


            // (Corner Stair 2 Roof Layer) Replace all air blocks that have stairs on 2 sides, 2 air sides and one stone brick below them with stairs
            replaceBlocksWithMask("=(queryRel(1,0,0,109,-1)&&queryRel(0,0,1,109,-1)&&queryRel(-1,0,0,0,-1)&&queryRel(0,0,-1,0,-1))",
                    getAir(), getStoneStair("south", "bottom", "inner_left"));
            changes++;
            replaceBlocksWithMask("//gmask =(queryRel(1,0,0,109,-1)&&queryRel(0,0,-1,109,-1)&&queryRel(-1,0,0,0,-1)&&queryRel(0,0,1,0,-1))",
                    getAir(), getStoneStair("north", "bottom", "inner_right"));
            changes++;
            replaceBlocksWithMask("//gmask =(queryRel(-1,0,0,109,-1)&&queryRel(0,0,1,109,-1)&&queryRel(1,0,0,0,-1)&&queryRel(0,0,-1,0,-1))",
                    getAir(), getStoneStair("south", "bottom", "inner_right"));
            changes++;
            replaceBlocksWithMask("//gmask =(queryRel(-1,0,0,109,-1)&&queryRel(0,0,-1,109,-1)&&queryRel(1,0,0,0,-1)&&queryRel(0,0,1,0,-1))",
                    getAir(), getStoneStair("north", "bottom", "inner_left"));
            changes++;


            // Cover leaking gold ore blocks with stone bricks
            replaceBlocksWithMask("=queryRel(1,0,0,14,0)||queryRel(-1,0,0,14,0)||queryRel(0,0,1,14,0)||queryRel(0,0,-1,14,0)",
                XMaterial.AIR, XMaterial.STONE_BRICKS);
            changes++;


            String[] colors = roofColor.split(",");

            // Remove :X from colors
            for(int i = 0; i < colors.length; i++)
                colors[i] = colors[i].split(":")[0];

            String[] blockColors = new String[colors.length];
            for(int i = 0; i < colors.length; i++)
                blockColors[i] = MenuItems.convertStairToBlock(colors[i]);


            // Replace stone bricks with the correct color
            createCommand("//replace 98 " + StringUtils.join(blockColors, ","));
            changes++;


            // Replace all stairs with the correct color
            for(int i = 0; i < 12; i++) {
                if(colors.length == 1)
                    createCommand("//replace 109:" + i + " " + colors[0] + ":" + i);
                else
                    createCommand("//replace 109:" + i + " " + StringUtils.join(colors, ":" + i + ","));

                changes++;
            }
        }


        // ----------- FINAL FINISH ----------

        createCommand("//gmask 0,45,31,37,38,39,40,175");

        for(int i = 0; i < 5; i++) {
            createCommand("//replace <22 22");
            changes++;
        }

        createCommand("//gmask");
        createCommand("//replace 21 " + wallColor);
        changes++;
        createCommand("//replace 22 " + baseColor);
        changes++;
        createCommand("//replace 14 35:7");
        changes++;


        createCommand("//gmask");



        createCommand("//replace 44:0 " + rm1);
        changes++;
        createCommand("//replace 44:8 " + rm2);
        changes++;
        createCommand("//replace 43:0 " + rm3);
        changes++;
        createCommand("//replace 23 " + rm3);
        changes++;


        // Reset pos1 and pos2
        createCommand("//pos1 " + selectionPoints.get(0).getBlockX() + "," + maxY + "," + selectionPoints.get(0).getBlockZ());
        for(int i = 1; i < selectionPoints.size(); i++)
            createCommand("//pos2 " + selectionPoints.get(i).getBlockX() + "," + minY + "," + selectionPoints.get(i).getBlockZ());


        // Finish the script
        finish(blocks);

        */
    }

    // Move blue, green and red wool one block up
    private void moveWoolUp(int floor) {

        setBlocksWithMask(">35:11", XMaterial.BLUE_WOOL);
        setBlocksWithMask(">35:14", XMaterial.RED_WOOL);
        setBlocksWithMask(">35:5", XMaterial.LIME_WOOL);
        changes+=3;

        if(floor >= 0) {
            if (floor == 0)
                setBlocksWithMask(">35:1", XMaterial.ORANGE_WOOL);
            else
                setBlocksWithMask(">35:1", XMaterial.GREEN_WOOL);
            changes++;
        }
    }

    private void raiseYellowWoolFloor() {
        setBlocksWithMask(">35:4", XMaterial.YELLOW_WOOL);
        changes++;
    }

    private void expandEmeraldOre(){

        // Replace everything above green wool with temporary purple wool
        setBlocksWithMask(">129", XMaterial.REDSTONE_ORE);
        changes++;

        // Replace air next to redstone ore with redstone
        replaceBlocksWithMask("=queryRel(1,0,0,73,0)||queryRel(-1,0,0,73,0)||queryRel(0,0,1,73,0)||queryRel(0,0,-1,73,0)"
                , XMaterial.AIR, XMaterial.REDSTONE_ORE);
        changes++;

        // Replace air next to emerald ore with emerald ore
        replaceBlocksWithMask("=queryRel(1,0,0,129,0)||queryRel(-1,0,0,129,0)||queryRel(0,0,1,129,0)||queryRel(0,0,-1,129,0)"
                , XMaterial.AIR, XMaterial.EMERALD_ORE);
        changes++;

        // Replace redstone ore with air
        replaceBlocks(XMaterial.REDSTONE_ORE, XMaterial.AIR);
    }

    private BlockState getStoneSlab(String type){
        BlockType blockType = BlockTypes.STONE_SLAB;

        if(blockType == null)
            return null;

        EnumProperty property = (EnumProperty) (Property<?>) blockType.getProperty("type");
        BlockState blockState = blockType.getDefaultState();
        return blockState.with(property, type);
    }

    /**
     * Get the block state for a stone stair in the given direction, half and shape
     *
     * @param facing The direction the stair is facing (north, east, south, west)
     * @param half The site of the bigger part of the stair (top, bottom)
     * @param shape The shape of the stair (straight, inner_left, inner_right, outer_left, outer_right)
     * @return The block state for the stair
     */
    private BlockState getStoneStair(String facing, String half, String shape){
        BlockType blockType = BlockTypes.STONE_SLAB;

        if(blockType == null)
            return null;

        EnumProperty facingProperty = (EnumProperty) (Property<?>) blockType.getProperty("facing");
        EnumProperty halfProperty = (EnumProperty) (Property<?>) blockType.getProperty("half");
        EnumProperty shapeProperty = (EnumProperty) (Property<?>) blockType.getProperty("shape");
        BlockState blockState = blockType.getDefaultState();
        return blockState.with(facingProperty, facing).with(halfProperty, half).with(shapeProperty, shape);
    }

    private BlockState getAir(){
        return BlockTypes.AIR.getDefaultState();
    }

    private BlockState getEmeraldOre(){
        return BlockTypes.EMERALD_ORE.getDefaultState();
    }
}
