package net.buildtheearth.modules.generator.components.house;

import com.cryptomorin.xseries.XMaterial;
import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import net.buildtheearth.modules.generator.model.Flag;
import net.buildtheearth.modules.generator.model.GeneratorComponent;
import net.buildtheearth.modules.generator.model.Script;
import net.buildtheearth.modules.generator.utils.GeneratorUtils;
import net.buildtheearth.utils.Item;
import net.buildtheearth.utils.MenuItems;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;

public class HouseScripts extends Script {

    public HouseScripts(Player player, GeneratorComponent generatorComponent) {
        super(player, generatorComponent);

        buildscript_v_1_2();
    }

    public void buildscript_v_1_2(){
        HashMap<Flag, Object> flags = getGeneratorComponent().getPlayerSettings().get(getPlayer().getUniqueId()).getValues();

        XMaterial[] wallColor = (XMaterial[]) flags.get(HouseFlag.WALL_COLOR);
        XMaterial[] roofColor = (XMaterial[]) flags.get(HouseFlag.ROOF_COLOR);
        XMaterial[] baseColor = (XMaterial[]) flags.get(HouseFlag.BASE_COLOR);
        XMaterial[] windowColor = (XMaterial[]) flags.get(HouseFlag.WINDOW_COLOR);
        XMaterial[] balconyColor = (XMaterial[]) flags.get(HouseFlag.BALCONY_COLOR);
        XMaterial[] balconyFenceColor = (XMaterial[]) flags.get(HouseFlag.BALCONY_FENCE_COLOR);
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




        // ----------- PREPARATION 01 ----------
        // Replace all non-solid blocks with air

        Block[][][] blocks = GeneratorUtils.prepareScriptSession(localSession, actor, getPlayer(),weWorld, 10);
        changes++;

        if(blocks == null){
            getPlayer().sendMessage("§c§lERROR: §cRegion not readable. Please report this to the developers of the BuildTeamTool plugin.");
            return;
        }

        int highestBlock = GeneratorUtils.getMaxHeight(blocks, MenuItems.getIgnoredMaterials());
        boolean containsRedWool = GeneratorUtils.containsBlock(blocks, XMaterial.RED_WOOL);
        boolean containsOrangeWool = GeneratorUtils.containsBlock(blocks, XMaterial.ORANGE_WOOL);


        // Disable the global mask
        disableGmask();


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
            replaceBlocksWithMask(">45", XMaterial.SPONGE, XMaterial.BRICKS);
            changes++;
        }

        // Replace all left sponges with air
        replaceBlocks(XMaterial.SPONGE, XMaterial.AIR);
        changes++;


        // ----------- PREPARATION 03 ----------
        // Bring the orange, yellow, green, blue and red wool blocks to the same height

        XMaterial[] woolColors = {XMaterial.ORANGE_WOOL, XMaterial.YELLOW_WOOL, XMaterial.BLUE_WOOL, XMaterial.LIME_WOOL};

        for(XMaterial wool : woolColors){
            // Replace all blocks above the wool
            replaceBlocksWithMask(">" + Item.getUniqueMaterialString(wool), XMaterial.AIR, wool, 20);

            // Select highest wool block and replace it with sponge
            replaceBlocksWithMask("<0", wool, XMaterial.SPONGE);
            changes++;

            // Replace wool with brick
            replaceBlocks(wool, XMaterial.BRICKS);
            changes++;

            // Replace all sponges with yellow wool
            replaceBlocks(XMaterial.SPONGE, wool);
            changes++;
        }

        // ----------- PREPARATION 05 ----------
        // Move blue, red and lime wool one block up and replace block below with brick

        expandSelection(new Vector(0, 1, 0));

        XMaterial[] woolColorsNoYellow = {XMaterial.BLUE_WOOL, XMaterial.BLUE_WOOL, XMaterial.RED_WOOL, XMaterial.LIME_WOOL};
        for(XMaterial wool : woolColorsNoYellow) {
            replaceBlocksWithMask("=queryRel(1,0,0,45,0)||queryRel(-1,0,0,45,0)||queryRel(0,0,1,45,0)||queryRel(0,0,-1,45,0)||queryRel(1,0,1,45,0)||queryRel(-1,0,1,45,0)||queryRel(1,0,-1,45,0)||queryRel(-1,0,-1,45,0)",
                wool, XMaterial.SPONGE);
            changes++;


            replaceBlocksWithMask("=queryRel(1,0,0,19,0)||queryRel(-1,0,0,19,0)||queryRel(0,0,1,19,0)||queryRel(0,0,-1,19,0)||queryRel(1,0,1,19,0)||queryRel(-1,0,1,19,0)||queryRel(1,0,-1,19,0)||queryRel(-1,0,-1,19,0)",
                    wool, XMaterial.SPONGE, 10);
            changes+=10;

            setBlocksWithMask(">19", wool);
            changes++;

            replaceBlocks(XMaterial.SPONGE, XMaterial.BRICKS);
            changes++;
        }



        // ----------- PREPARATION 06 ----------
        // Replace all bricks underground with grass and replace wool with ore blocks

        // Replace all bricks with sponge
        replaceBlocks(XMaterial.BRICKS, XMaterial.SPONGE);
        changes++;

        // Replace the lime wool with emerald ore because FAWE doesn't support data values in queryRels in 2.9.1
        replaceBlocks(XMaterial.LIME_WOOL, XMaterial.EMERALD_ORE);

        // Replace the blue wool with diamond ore because FAWE doesn't support data values in queryRels in 2.9.1
        replaceBlocks(XMaterial.BLUE_WOOL, XMaterial.DIAMOND_ORE);

        // Replace the yellow wool with gold ore because FAWE doesn't support data values in queryRels in 2.9.1
        replaceBlocks(XMaterial.YELLOW_WOOL, XMaterial.GOLD_ORE);

        // Replace the red wool with redstone ore because FAWE doesn't support data values in queryRels in 2.9.1
        replaceBlocks(XMaterial.RED_WOOL, XMaterial.REDSTONE_ORE);


        // Replace all sponges with bricks that have air or redstone ore or diamond ore or emerald ore or gold ore above them
        replaceBlocksWithMask("=(queryRel(0,1,0,0,0)||queryRel(0,1,0,56,0)||queryRel(0,1,0,73,0)||queryRel(0,1,0,129,0)||queryRel(0,1,0,14,0))"
                , XMaterial.SPONGE, XMaterial.BRICKS);
        changes++;

        // Replace all left sponges with grass
        replaceBlocks(XMaterial.SPONGE, XMaterial.GRASS_BLOCK);
        changes++;


        // ----------- PREPARATION 07 ----------
        // Expand the diamond ore until it reaches the emerald ore

        // Replace all air blocks above bricks, next to diamond ore and not next to emerald ore with diamond ore
        setBlocksWithMask("=queryRel(0,-1,0,45,0)" +
                        "&&(queryRel(1,0,0,56,0)||queryRel(-1,0,0,56,0)||queryRel(0,0,1,56,0)||queryRel(0,0,-1,56,0)||queryRel(1,0,1,56,0)||queryRel(-1,0,-1,56,0)||queryRel(-1,0,1,56,0)||queryRel(1,0,-1,56,0))" +
                        "&&!(queryRel(1,0,0,129,0)||queryRel(-1,0,0,129,0)||queryRel(0,0,1,129,0)||queryRel(0,0,-1,129,0)||queryRel(1,0,1,129,0)||queryRel(-1,0,-1,129,0)||queryRel(-1,0,1,129,0)||queryRel(1,0,-1,129,0))",
                XMaterial.DIAMOND_ORE, 20);
        changes+=20;


        // Place the last diamond ore between the emerald ore and the diamond ore
        setBlocksWithMask("=queryRel(0,-1,0,45,0)&&(queryRel(1,0,0,56,0)||queryRel(-1,0,0,56,0)||queryRel(0,0,1,56,0)||queryRel(0,0,-1,56,0)||queryRel(1,0,1,56,0)||queryRel(-1,0,-1,56,0)||queryRel(-1,0,1,56,0)||queryRel(1,0,-1,56,0))",
                XMaterial.DIAMOND_ORE);
        changes++;


        // ----------- GROUND ----------

        // Set pos1 and pos2
        outlinePoints = new ArrayList<>(selectionPoints);
        outlinePoints.get(0).setY(maxY);
        for(Vector point : outlinePoints)
            point.setY(minY);
        createSelection(outlinePoints);

        // Expand the current selection down by 10 blocks
        expandSelection(new Vector(0, -10, 0));

        // Expand the current selection up by "up_expand" blocks
        int up_expand = 5 + maxRoofHeight + baseHeight + (floorCount * floorHeight);
        expandSelection(new Vector(0, up_expand, 0));

        // Replace all blocks that are not bricks around the gold ore with gold ore
        setBlocksWithMask("=!queryRel(0,0,0,45,0)&&(queryRel(-1,0,0,14,0)||queryRel(1,0,0,14,0)||queryRel(0,0,1,14,0)||queryRel(0,0,-1,14,0))",
                XMaterial.GOLD_ORE, 20);
        changes+=20;


        // Make the outline as thin as possible and fill all inner corners with gold ore that are too thick
        String[] queries = {
            "=queryRel(-1,0,0,14,0)&&queryRel(0,0,-1,14,0)&&queryRel(0,0,1,45,-1)&&queryRel(1,0,0,45,-1)",
            "=queryRel(-1,0,0,14,0)&&queryRel(0,0,1,14,0)&&queryRel(0,0,-1,45,-1)&&queryRel(1,0,0,45,-1)",
            "=queryRel(1,0,0,14,0)&&queryRel(0,0,-1,14,0)&&queryRel(0,0,1,45,-1)&&queryRel(-1,0,0,45,-1)",
            "=queryRel(1,0,0,14,0)&&queryRel(0,0,1,14,0)&&queryRel(0,0,-1,45,-1)&&queryRel(-1,0,0,45,-1)",
            "=queryRel(-1,0,0,14,0)&&queryRel(0,0,-1,45,-1)&&queryRel(0,0,1,45,-1)&&queryRel(1,0,0,45,-1)",
            "=queryRel(1,0,0,14,0)&&queryRel(0,0,1,45,-1)&&queryRel(0,0,-1,45,-1)&&queryRel(-1,0,0,45,-1)",
            "=queryRel(1,0,0,45,-1)&&queryRel(0,0,-1,14,0)&&queryRel(0,0,1,45,-1)&&queryRel(-1,0,0,45,-1)",
            "=queryRel(1,0,0,45,-1)&&queryRel(0,0,1,14,0)&&queryRel(0,0,-1,45,-1)&&queryRel(-1,0,0,45,-1)"
        };
        replaceBlocksWithMask(Arrays.asList(queries), XMaterial.REDSTONE_ORE, XMaterial.GOLD_ORE, 2);



        // ----------- BASE ----------
        int currentHeight = 0;

        if(baseHeight > 0)
            for(int i = 0; i < baseHeight; i++) {
                currentHeight++;

                // Move wool one block up
                moveOresUp(0);

                // Select everything x blocks above bricks. Then replace that with lapislazuli
                setBlocksWithMask("=queryRel(0," + (-currentHeight) + ",0,45,-1)", XMaterial.LAPIS_BLOCK);
                changes++;

                // Raise the yellow wool layer by one block
                raiseGoldFloor();
            }





        // ----------- FLOORS ----------
        int heightDifference = 0;
        for(int i = 0; i < floorCount; i++) {

            currentHeight++;

            // Move wool one block up
            moveOresUp(i+1);

            // Select everything x blocks above bricks. Then replace that with lapislazuli block
            setBlocksWithMask("=queryRel(0," + (-currentHeight) + ",0,45,-1)", XMaterial.LAPIS_ORE);
            changes++;

            // Raise the yellow wool layer by one block
            raiseGoldFloor();

            // Windows
            for(int i2 = 0; i2 < windowHeight; i2++) {
                currentHeight++;

                // Move wool one block up
                moveOresUp(0);


                if (!containsRedWool) {
                    // Replace everything with white glass
                    setBlocksWithMask("=queryRel(0," + (-currentHeight) + ",0,45,-1)", XMaterial.WHITE_STAINED_GLASS);
                }else {
                    // Replace redstone ore with gray glass
                    replaceBlocksWithMask("=queryRel(0," + (-currentHeight) + ",0,45,-1)", XMaterial.REDSTONE_ORE, XMaterial.GRAY_STAINED_GLASS);
                    changes++;

                    // Replace air with lapislazuli ore
                    replaceBlocksWithMask("=queryRel(0," + (-currentHeight) + ",0,45,-1)", XMaterial.AIR, XMaterial.LAPIS_ORE);
                    changes++;

                    // Replace lapislazuli ore with lapislazuli ore
                    replaceBlocksWithMask("=queryRel(0," + (-currentHeight) + ",0,45,-1)", XMaterial.LAPIS_ORE, XMaterial.LAPIS_ORE);
                    changes++;

                    // Replace emerald ore with lapislazuli ore
                    replaceBlocksWithMask("=queryRel(0," + (-currentHeight) + ",0,45,-1)", XMaterial.EMERALD_ORE, XMaterial.LAPIS_ORE);
                }
                changes++;


                // Raise the yellow wool layer by one block
                raiseGoldFloor();

            }

            heightDifference = floorHeight - (windowHeight + 1);

            if(heightDifference > 0)
                for(int i2 = 0; i2 < heightDifference; i2++) {
                    currentHeight++;

                    // Move wool one block up
                    moveOresUp(-1);

                    // Select everything x blocks above bricks. Then replace that with lapislazuli ore
                    setBlocksWithMask("=queryRel(0," + (-currentHeight) + ",0,45,-1)", XMaterial.LAPIS_ORE);
                    changes++;

                    // Raise the yellow wool layer by one block
                    raiseGoldFloor();
                }

        }
        if(heightDifference == 0){
            currentHeight++;

            // Move wool one block up
            moveOresUp(-1);

            // Select everything x blocks above bricks. Then replace that with lapislazuli ore
            setBlocksWithMask("=queryRel(0," + (-currentHeight) + ",0,45,-1)", XMaterial.LAPIS_ORE);
            changes++;

            // Raise the yellow wool layer by one block
            raiseGoldFloor();
        }

        // Remove the red wool
        replaceBlocks(XMaterial.RED_WOOL, XMaterial.AIR);
        changes++;


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
            setBlocksWithMask("=queryRel(0,-1,0,19)&&(queryRel(-1,-1,0,0,0)||queryRel(1,-1,0,0,0)||queryRel(0,-1,-1,0,0)||queryRel(0,-1,1,0,0))",
                    balconyFenceColor);

            // If the balcony fence color is enabled, replace sponge with the balcony color
            if(balconyColor != null)
                replaceBlocks(XMaterial.SPONGE, balconyColor);
        }


        // ----------- ROOF ----------



        if(roofType == RoofType.FLATTER_SLABS || roofType == RoofType.STEEP_SLABS|| roofType == RoofType.MEDIUM_SLABS){

            // Replace the diamond ore next to emerald ore with emerald ore
            replaceBlocksWithMask("=queryRel(1,0,0,129,0)||queryRel(-1,0,0,129,0)||queryRel(0,0,1,129,0)||queryRel(0,0,-1,129,0)||queryRel(1,0,1,129,0)||queryRel(-1,0,-1,129,0)||queryRel(-1,0,1,129,0)||queryRel(1,0,-1,129,0)",
                    XMaterial.DIAMOND_ORE, XMaterial.EMERALD_ORE);
            changes++;

            // Create the roof house wall staircase
            for(int i = 0; i < maxRoofHeight; i++){
                // Select all air blocks that are above diamond ore and above & next to emerald ore
                replaceBlocksWithMask("=queryRel(0,-1,0,56,0)&&" +
                            "(" +
                                "queryRel(1,-1,0,129,0)||queryRel(-1,-1,0,129,0)||queryRel(0,-1,1,129,0)||queryRel(0,-1,-1,129,0)||queryRel(1,-1,1,129,0)||queryRel(-1,-1,-1,129,0)||queryRel(-1,-1,1,129,0)||queryRel(1,-1,-1,129,0)" +
                            ")",
                        XMaterial.DIAMOND_ORE, XMaterial.EMERALD_ORE);
                changes++;

                // Select all air blocks that are above diamond ore and next to emerald ore
                replaceBlocksWithMask("=queryRel(0,-1,0,56,0)&&" +
                                "(" +
                                    "queryRel(1,0,0,129,0)||queryRel(-1,0,0,129,0)||queryRel(0,0,1,129,0)||queryRel(0,0,-1,129,0)||queryRel(1,0,1,129,0)||queryRel(-1,0,-1,129,0)||queryRel(-1,0,1,129,0)||queryRel(1,0,-1,129,0)" +
                                ")",
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
                    getSlab(BlockTypes.STONE_SLAB, "top"));
            changes++;

            // (Overhang Roof Layer 2) Select all air blocks next to upside down stone slab and lapislazuli. Then replace them with and upside down stone slab
            setBlocksWithMask("=queryRel(0,0,0,0,0)&&" +
                    "(" +
                        "(queryRel(1,0,0,44,8)&&(queryRel(0,0,1,21,0)||queryRel(0,0,-1,21,0)))||" +
                        "(queryRel(-1,0,0,44,8)&&(queryRel(0,0,1,21,0)||queryRel(0,0,-1,21,0)))||" +
                        "(queryRel(0,0,1,44,8)&&(queryRel(1,0,0,21,0)||queryRel(-1,0,0,21,0)))||" +
                        "(queryRel(0,0,-1,44,8)&&(queryRel(1,0,0,21,0)||queryRel(-1,0,0,21,0)))" +
                    ")",
                    getSlab(BlockTypes.STONE_SLAB, "top"));
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
                            getSlab(BlockTypes.STONE_SLAB, "double"), getSlab(BlockTypes.STONE_SLAB, "bottom")
                        );
                    else
                        replaceBlocksWithMask("=!(queryRel(1,-1,0,44,-1)||queryRel(-1,-1,0,44,-1)||queryRel(0,-1,1,44,-1)||queryRel(0,-1,-1,44,-1))",
                                getSlab(BlockTypes.STONE_SLAB, "double"), getSlab(BlockTypes.STONE_SLAB, "bottom"));

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
                            getSlab(BlockTypes.STONE_SLAB, "bottom"), getSlab(BlockTypes.STONE_SLAB, "double")
                        );
                    else
                        replaceBlocksWithMask("=!(queryRel(1,0,0,0,-1)||queryRel(-1,0,0,0,-1)||queryRel(0,0,1,0,-1)||queryRel(0,0,-1,0,-1))"
                            ,getSlab(BlockTypes.STONE_SLAB, "bottom"), getSlab(BlockTypes.STONE_SLAB, "double"));

                    changes++;
                }

            // Replace everything above upside down stone slabs with purple wool
            setBlocksWithMask(">44:8", XMaterial.REDSTONE_ORE);
            changes++;

            expandEmeraldOre();

            // (Overhang Roof Layer 3) Select all air blocks next to two upside down stone slabs. Then replace them with and upside down stone slab
            replaceBlocksWithMask("=(queryRel(1,0,0,44,8)&&queryRel(2,0,0,44,8))||(queryRel(-1,0,0,44,8)&&queryRel(-2,0,0,44,8))||(queryRel(0,0,1,44,8)&&queryRel(0,0,2,44,8))||(queryRel(0,0,-1,44,8)&&queryRel(0,0,-2,44,8))",
                getAir(), getSlab(BlockTypes.STONE_SLAB, "top"));
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
                getAir(), getSlab(BlockTypes.STONE_SLAB, "top"));
            changes++;

            // Select all air blocks that are next to emerald ore and under a stone slab and replace it with upside down stone slabs
            replaceBlocksWithMask("=queryRel(0,1,0,44,0)&&(queryRel(1,0,0,129,0)||queryRel(-1,0,0,129,0)||queryRel(0,0,1,129,0)||queryRel(0,0,-1,129,0))",
                getAir(), getSlab(BlockTypes.STONE_SLAB, "top"));
            changes++;


            // Select all leftover emerald ore replace it with double stone slabs
            replaceBlocks(getEmeraldOre(), getSlab(BlockTypes.STONE_SLAB, "double"));
            changes++;

            // Replace diamond ore with lapislazuli ore
            replaceBlocks(XMaterial.DIAMOND_ORE, XMaterial.LAPIS_ORE);
            changes++;

            BlockState[] bottomSlabs = new BlockState[roofColor.length];
            BlockState[] topSlabs = new BlockState[roofColor.length];
            BlockState[] doubleSlabs = new BlockState[roofColor.length];

            for(int i = 0; i < roofColor.length; i++){
                BlockType bt = Item.convertXMaterialToBlockType(roofColor[i]);

                if(bt == null)
                    continue;

                bottomSlabs[i] = getSlab(bt, "bottom");
                topSlabs[i] = getSlab(bt, "top");
                doubleSlabs[i] = getSlab(bt, "double");
            }

            replaceBlocks(getSlab(BlockTypes.STONE_SLAB, "bottom"), bottomSlabs);
            changes++;
            replaceBlocks(getSlab(BlockTypes.STONE_SLAB, "top"), topSlabs);
            changes++;
            replaceBlocks(getSlab(BlockTypes.STONE_SLAB, "double"), doubleSlabs);
            changes++;

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
                            "queryRel(1,-1,0,129,0)||queryRel(-1,-1,0,129,0)||queryRel(0,-1,1,129,0)||queryRel(0,-1,-1,129,0)||queryRel(1,-1,1,129,0)||queryRel(-1,-1,-1,129,0)||queryRel(-1,-1,1,129,0)||queryRel(1,-1,-1,129,0)" +
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

            // Replace everything around stone bricks with sponge
            replaceBlocksWithMask("=queryRel(1,0,0,98,0)||queryRel(-1,0,0,98,0)||queryRel(0,0,1,98,0)||queryRel(0,0,-1,98,0)",
                    XMaterial.AIR, XMaterial.SPONGE);

            // (Overhang Roof Layer) Select all air blocks next to lapislazuli ores that have sponge above them. Then replace them with stone bricks
            replaceBlocksWithMask("<19",
                XMaterial.AIR, XMaterial.STONE_BRICKS);
            changes++;

            // Replace sponge with air
            replaceBlocks(XMaterial.SPONGE, XMaterial.AIR);

            // Replace all air blocks that are next to lapislazuli ores and next to stone bricks with emerald ore
            replaceBlocksWithMask("=(queryRel(1,0,0,98,0)&&queryRel(0,0,1,21,0))" +
                            "||(queryRel(1,0,0,98,0)&&queryRel(0,0,-1,21,0))" +
                            "||(queryRel(-1,0,0,98,0)&&queryRel(0,0,-1,21,0))" +
                            "||(queryRel(-1,0,0,98,0)&&queryRel(0,0,1,21,0))" +
                            "||(queryRel(0,0,1,98,0)&&queryRel(1,0,0,21,0))" +
                            "||(queryRel(0,0,-1,98,0)&&queryRel(1,0,0,21,0))" +
                            "||(queryRel(0,0,-1,98,0)&&queryRel(-1,0,0,21,0))" +
                            "||(queryRel(0,0,1,98,0)&&queryRel(-1,0,0,21,0))",
                    XMaterial.AIR, XMaterial.SPONGE);

            // Replace all air blocks that have sponge with lapislazuli ore behind them with redstone ore
            replaceBlocksWithMask("=(queryRel(1,0,0,19,0)&&queryRel(2,0,0,21,0))||(queryRel(-1,0,0,19,0)&&queryRel(-2,0,0,21,0))||(queryRel(0,0,1,19,0)&&queryRel(0,0,2,21,0))||(queryRel(0,0,-1,19,0)&&queryRel(0,0,-2,21,0))",
                    XMaterial.AIR, XMaterial.REDSTONE_ORE);

            // Replace sponge with emerald ore
            replaceBlocks(XMaterial.SPONGE, XMaterial.EMERALD_ORE);



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
                    "=queryRel(0,1,0,98,0)&&(queryRel(-1,0,0,98,0)||queryRel(-1,0,1,98,0)||queryRel(-1,0,-1,98,0))&&(queryRel(1,0,0,98,0)||queryRel(1,0,1,98,0)||queryRel(1,0,-1,98,0))",
                    "=queryRel(0,1,0,98,0)&&(queryRel(0,0,-1,98,0)||queryRel(1,0,-1,98,0)||queryRel(-1,0,-1,98,0))&&(queryRel(0,0,1,98,0)||queryRel(1,0,1,98,0)||queryRel(-1,0,1,98,0))"
            };
            replaceBlocksWithMask(Arrays.asList(queries), XMaterial.AIR, XMaterial.STONE_BRICKS, 1);


            // ROOF STAIRS

            // Fill the overhang with upside-down stairs which are surrounded by 1 stone brick and one stone brick above them
            replaceBlocksWithMask("=queryRel(0,1,0,98,0)&&queryRel(1,0,0,98,0)",
                    getAir(), getStair(BlockTypes.STONE_STAIRS, "east", "top", "straight"));
            changes++;
            replaceBlocksWithMask("=queryRel(0,1,0,98,0)&&queryRel(-1,0,0,98,0)",
                    getAir(), getStair(BlockTypes.STONE_STAIRS, "west", "top", "straight"));
            changes++;
            replaceBlocksWithMask("=queryRel(0,1,0,98,0)&&queryRel(0,0,1,98,0)",
                    getAir(), getStair(BlockTypes.STONE_STAIRS, "south", "top", "straight"));
            changes++;
            replaceBlocksWithMask("=queryRel(0,1,0,98,0)&&queryRel(0,0,-1,98,0)",
                    getAir(), getStair(BlockTypes.STONE_STAIRS, "north", "top", "straight"));
            changes++;

            // Fill the remaining overhang with upside-down stairs which are surrounded by 1 stone brick and one stone brick above them
            replaceBlocksWithMask("=queryRel(0,1,0,98,0)&&(queryRel(1,0,0,98,0)||queryRel(1,0,1,98,0)||queryRel(1,0,-1,98,0))",
                    getAir(), getStair(BlockTypes.STONE_STAIRS, "east", "top", "straight"));
            changes++;
            replaceBlocksWithMask("=queryRel(0,1,0,98,0)&&(queryRel(-1,0,0,98,0)||queryRel(-1,0,1,98,0)||queryRel(-1,0,-1,98,0))",
                    getAir(), getStair(BlockTypes.STONE_STAIRS, "west", "top", "straight"));
            changes++;
            replaceBlocksWithMask("=queryRel(0,1,0,98,0)&&(queryRel(0,0,1,98,0)||queryRel(1,0,1,98,0)||queryRel(-1,0,1,98,0))",
                    getAir(), getStair(BlockTypes.STONE_STAIRS, "south", "top", "straight"));
            changes++;
            replaceBlocksWithMask("=queryRel(0,1,0,98,0)&&(queryRel(0,0,-1,98,0)||queryRel(1,0,-1,98,0)||queryRel(-1,0,-1,98,0))",
                    getAir(), getStair(BlockTypes.STONE_STAIRS, "north", "top", "straight"));
            changes++;


            // (Normal Stair Roof Layer) Replace all air blocks that have a stone brick on one side, 3 air sides and one stone brick below them with stairs
            replaceBlocksWithMask("=queryRel(0,-1,0,98,0)&&(queryRel(1,0,0,98,-1)&&queryRel(-1,0,0,0,-1)&&queryRel(0,0,1,0,-1)&&queryRel(0,0,-1,0,-1))",
                    getAir(), getStair(BlockTypes.STONE_STAIRS, "east", "bottom", "straight"));
            changes++;
            replaceBlocksWithMask("=queryRel(0,-1,0,98,0)&&(queryRel(1,0,0,0,-1)&&queryRel(-1,0,0,98,-1)&&queryRel(0,0,1,0,-1)&&queryRel(0,0,-1,0,-1))",
                    getAir(), getStair(BlockTypes.STONE_STAIRS, "west", "bottom", "straight"));
            changes++;
            replaceBlocksWithMask("=queryRel(0,-1,0,98,0)&&(queryRel(1,0,0,0,-1)&&queryRel(-1,0,0,0,-1)&&queryRel(0,0,1,98,-1)&&queryRel(0,0,-1,0,-1))",
                    getAir(), getStair(BlockTypes.STONE_STAIRS, "south", "bottom", "straight"));
            changes++;
            replaceBlocksWithMask("=queryRel(0,-1,0,98,0)&&(queryRel(1,0,0,0,-1)&&queryRel(-1,0,0,0,-1)&&queryRel(0,0,1,0,-1)&&queryRel(0,0,-1,98,-1))",
                    getAir(), getStair(BlockTypes.STONE_STAIRS, "north", "bottom", "straight"));
            changes++;

            // (Corner Stair Roof Layer) Replace all air blocks that have stone bricks on 2 sides, 2 air or stair sides and one stone brick below them with stairs
            replaceBlocksWithMask("=queryRel(0,-1,0,98,0)&&(queryRel(1,0,0,98,-1)&&queryRel(0,0,1,98,-1)&&(queryRel(-1,0,0,0,-1)||queryRel(-1,0,0,109,-1))&&(queryRel(0,0,-1,0,-1)||queryRel(0,0,-1,109,-1)))",
                    getAir(), getStair(BlockTypes.STONE_STAIRS, "east", "bottom", "inner_right"));
            changes++;
            replaceBlocksWithMask("=queryRel(0,-1,0,98,0)&&(queryRel(-1,0,0,98,-1)&&queryRel(0,0,-1,98,-1)&&(queryRel(1,0,0,0,-1)||queryRel(1,0,0,109,-1))&&(queryRel(0,0,1,0,-1)||queryRel(0,0,1,109,-1)))",
                    getAir(), getStair(BlockTypes.STONE_STAIRS, "west", "bottom", "inner_right"));
            changes++;
            replaceBlocksWithMask("=queryRel(0,-1,0,98,0)&&(queryRel(-1,0,0,98,-1)&&queryRel(0,0,1,98,-1)&&(queryRel(1,0,0,0,-1)||queryRel(1,0,0,109,-1))&&(queryRel(0,0,-1,0,-1)||queryRel(0,0,-1,109,-1)))",
                    getAir(), getStair(BlockTypes.STONE_STAIRS, "south", "bottom", "inner_right"));
            changes++;
            replaceBlocksWithMask("=queryRel(0,-1,0,98,0)&&(queryRel(1,0,0,98,-1)&&queryRel(0,0,-1,98,-1)&&(queryRel(-1,0,0,0,-1)||queryRel(-1,0,0,109,-1))&&(queryRel(0,0,1,0,-1)||queryRel(0,0,1,109,-1)))",
                    getAir(), getStair(BlockTypes.STONE_STAIRS, "north", "bottom", "inner_right"));
            changes++;


            // (Corner Stair 2 Roof Layer) Replace all air blocks that have stairs on 2 sides, 2 air sides and one stone brick below them with stairs
            replaceBlocksWithMask("=queryRel(0,-1,0,98,0)&&(queryRel(1,0,0,109,-1)&&queryRel(0,0,1,109,-1)&&queryRel(-1,0,0,0,-1)&&queryRel(0,0,-1,0,-1))",
                    getAir(), getStair(BlockTypes.STONE_STAIRS, "south", "bottom", "inner_left"));
            changes++;
            replaceBlocksWithMask("=queryRel(0,-1,0,98,0)&&(queryRel(1,0,0,109,-1)&&queryRel(0,0,-1,109,-1)&&queryRel(-1,0,0,0,-1)&&queryRel(0,0,1,0,-1))",
                    getAir(), getStair(BlockTypes.STONE_STAIRS, "north", "bottom", "inner_right"));
            changes++;
            replaceBlocksWithMask("=queryRel(0,-1,0,98,0)&&(queryRel(-1,0,0,109,-1)&&queryRel(0,0,1,109,-1)&&queryRel(1,0,0,0,-1)&&queryRel(0,0,-1,0,-1))",
                    getAir(), getStair(BlockTypes.STONE_STAIRS, "south", "bottom", "inner_right"));
            changes++;
            replaceBlocksWithMask("=queryRel(0,-1,0,98,0)&&(queryRel(-1,0,0,109,-1)&&queryRel(0,0,-1,109,-1)&&queryRel(1,0,0,0,-1)&&queryRel(0,0,1,0,-1))",
                    getAir(), getStair(BlockTypes.STONE_STAIRS, "north", "bottom", "inner_left"));
            changes++;


            // Cover leaking gold ore blocks with stone bricks
            replaceBlocksWithMask("=queryRel(1,0,0,14,0)||queryRel(-1,0,0,14,0)||queryRel(0,0,1,14,0)||queryRel(0,0,-1,14,0)",
                XMaterial.AIR, XMaterial.STONE_BRICKS);
            changes++;


            XMaterial[] stairBlocks = new XMaterial[roofColor.length];
            for(int i = 0; i < roofColor.length; i++)
                stairBlocks[i] = MenuItems.convertStairToBlock(roofColor[i]);

            // Replace stone bricks with the correct color
            replaceBlocks(XMaterial.STONE_BRICKS, stairBlocks);
            changes++;


            // Iterate through all stair block states and replace them with the correct color
            BlockType blockType = BlockTypes.STONE_STAIRS;
            if(blockType != null)
                for(BlockState blockState : blockType.getAllStates()) {
                    Map<Property<?>, Object> properties = blockState.getStates();
                    String facing = "";
                    String half = "";
                    String shape = "";

                    boolean shouldSkip = false;
                    // Skip waterlogged = true stairs
                    for(Property<?> property : properties.keySet()){
                        if (property.getName().equalsIgnoreCase("waterlogged")
                                && properties.get(property) instanceof Boolean
                                && (Boolean) properties.get(property))
                            shouldSkip = true;
                    }

                    if(shouldSkip)
                        continue;

                    // Get the properties of the stair
                    for(Property<?> property : properties.keySet()){
                        if(property.getName().equalsIgnoreCase("facing"))
                            facing = properties.get(property).toString();
                        else if(property.getName().equalsIgnoreCase("half"))
                            half = properties.get(property).toString();
                        else if(property.getName().equalsIgnoreCase("shape"))
                            shape = properties.get(property).toString();
                    }

                    // Convert all stair blocks to the correct orientation
                    BlockState[] stairsWithOrientation = new BlockState[roofColor.length];
                    for(int i = 0; i < roofColor.length; i++){
                        BlockType bt = Item.convertXMaterialToBlockType(roofColor[i]);

                        if(bt == null)
                            continue;

                        stairsWithOrientation[i] = getStair(bt, facing, half, shape);
                    }

                    BlockState stair = getStair(BlockTypes.STONE_STAIRS, facing, half, shape);

                    // Replace all stairs with the correct color
                    replaceBlocks(stair, stairsWithOrientation);
                }
        }


        // ----------- FINAL FINISH ----------

        setGmask("0,45,31,37,38,39,40,175");
        setBlocksWithMask("<22", XMaterial.LAPIS_BLOCK, 5);
        disableGmask();
        changes+=5;

        replaceBlocks(XMaterial.LAPIS_ORE, wallColor);
        changes++;
        replaceBlocks(XMaterial.LAPIS_BLOCK, baseColor);
        changes++;
        replaceBlocks(XMaterial.GOLD_ORE, XMaterial.GRAY_WOOL);
        changes++;
        replaceBlocks(XMaterial.STONE_SLAB, XMaterial.GRAY_WOOL);


        BlockState[] doubleSlabs = new BlockState[roofColor.length];

        for(int i = 0; i < roofColor.length; i++){
            BlockType bt = Item.convertXMaterialToBlockType(roofColor[i]);

            if(bt == null)
                continue;

            doubleSlabs[i] = getSlab(bt, "double");
        }

        replaceBlocks(Item.convertXMaterialToBlockType(XMaterial.DISPENSER).getDefaultState(), doubleSlabs);
        changes++;

        // Finish the script
        finish(blocks, selectionPoints);
    }

    // Move lapislazuli ore, emerald ore nad redstone ore one block up
    private void moveOresUp(int floor) {

        setBlocksWithMask(">56", XMaterial.DIAMOND_ORE);
        setBlocksWithMask(">73", XMaterial.REDSTONE_ORE);
        setBlocksWithMask(">129", XMaterial.EMERALD_ORE);
        changes+=3;

        if(floor >= 0) {
            if (floor == 0)
                setBlocksWithMask(">35:1", XMaterial.ORANGE_WOOL);
            else
                setBlocksWithMask(">35:1", XMaterial.GREEN_WOOL);
            changes++;
        }
    }

    private void raiseGoldFloor() {
        setBlocksWithMask(">14", XMaterial.GOLD_ORE);
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



    private BlockState getAir(){
        BlockType blockType = BlockTypes.AIR;

        if(blockType == null)
            return null;

        return blockType.getDefaultState();
    }

    private BlockState getEmeraldOre(){
        BlockType blockType = BlockTypes.EMERALD_ORE;

        if(blockType == null)
            return null;

        return blockType.getDefaultState();
    }
}
