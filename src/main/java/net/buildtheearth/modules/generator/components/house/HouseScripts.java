package net.buildtheearth.modules.generator.components.house;

import com.alpsbte.alpslib.utils.GeneratorUtils;
import com.alpsbte.alpslib.utils.item.Item;
import com.cryptomorin.xseries.XMaterial;
import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import net.buildtheearth.BuildTeamTools;
import net.buildtheearth.modules.generator.model.Flag;
import net.buildtheearth.modules.generator.model.GeneratorComponent;
import net.buildtheearth.modules.generator.model.Script;
import net.buildtheearth.utils.MenuItems;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;

public class HouseScripts extends Script {

    public HouseScripts(Player player, GeneratorComponent generatorComponent) {
        super(player, generatorComponent);
        Bukkit.getScheduler().runTaskAsynchronously(BuildTeamTools.getInstance(), this::buildscript_v_1_2);
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

        // Prepare the script session
        Block[][][] blocks = GeneratorUtils.prepareScriptSession(localSession, actor, getPlayer(),weWorld, 10, true, true, false);

        if(blocks == null){
            getPlayer().sendMessage("§c§lERROR: §cRegion not readable. Please report this to the developers of the BuildTeamTool plugin.");
            return;
        }

        int highestBlock = GeneratorUtils.getMaxHeight(blocks, MenuItems.getIgnoredMaterials());
        boolean containsRedWool = GeneratorUtils.containsBlock(blocks, XMaterial.RED_WOOL);
        boolean containsOrangeWool = GeneratorUtils.containsBlock(blocks, XMaterial.ORANGE_WOOL);


        // Disable the global mask



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

        // Replace all sponges with bricks that have bricks below them
        for(int i = 0; i < 20; i++)
            replaceBlocksWithMask(">45", XMaterial.SPONGE, XMaterial.BRICKS);

        // Replace all left sponges with air
        replaceBlocks(XMaterial.SPONGE, XMaterial.AIR);



        // ----------- PREPARATION 03 ----------
        // Bring the orange, yellow, green, blue and red wool blocks to the same height

        XMaterial[] woolColors = {XMaterial.ORANGE_WOOL, XMaterial.YELLOW_WOOL, XMaterial.BLUE_WOOL, XMaterial.LIME_WOOL};

        for(XMaterial wool : woolColors){
            // Replace all blocks above the wool
            replaceBlocksWithMask(">" + Item.getUniqueMaterialString(wool), XMaterial.AIR, wool, 20);

            // Select highest wool block and replace it with sponge
            replaceBlocksWithMask("<0", wool, XMaterial.SPONGE);

            // Replace wool with brick
            replaceBlocks(wool, XMaterial.BRICKS);

            // Replace all sponges with yellow wool
            replaceBlocks(XMaterial.SPONGE, wool);
        }

        // ----------- PREPARATION 05 ----------
        // Move blue, red and lime wool one block up and replace block below with brick

        expandSelection(new Vector(0, 1, 0));

        XMaterial[] woolColorsNoYellow = {XMaterial.BLUE_WOOL, XMaterial.BLUE_WOOL, XMaterial.RED_WOOL, XMaterial.LIME_WOOL};
        for(XMaterial wool : woolColorsNoYellow) {
            replaceBlocksWithMask("=queryRel(1,0,0,45,0)||queryRel(-1,0,0,45,0)||queryRel(0,0,1,45,0)||queryRel(0,0,-1,45,0)||queryRel(1,0,1,45,0)||queryRel(-1,0,1,45,0)||queryRel(1,0,-1,45,0)||queryRel(-1,0,-1,45,0)",
                wool, XMaterial.SPONGE);

            replaceBlocksWithMask("=queryRel(1,0,0,19,0)||queryRel(-1,0,0,19,0)||queryRel(0,0,1,19,0)||queryRel(0,0,-1,19,0)||queryRel(1,0,1,19,0)||queryRel(-1,0,1,19,0)||queryRel(1,0,-1,19,0)||queryRel(-1,0,-1,19,0)",
                    wool, XMaterial.SPONGE, 10);

            setBlocksWithMask(">19", wool);
            replaceBlocks(XMaterial.SPONGE, XMaterial.BRICKS);
        }



        // ----------- PREPARATION 06 ----------
        // Replace all bricks underground with grass and replace wool with ore blocks

        // Replace all bricks with sponge
        replaceBlocks(XMaterial.BRICKS, XMaterial.SPONGE);

        // Replace all sponges with bricks that have air or red wool or blue wool or lime wool or yellow wool above them
        replaceBlocksWithMask("=(queryRel(0,1,0,0,0)||queryRel(0,1,0,35,11)||queryRel(0,1,0,35,14)||queryRel(0,1,0,35,5)||queryRel(0,1,0,35,4))"
                , XMaterial.SPONGE, XMaterial.BRICKS);

        // Replace all left sponges with grass
        replaceBlocks(XMaterial.SPONGE, XMaterial.GRASS_BLOCK);



        // ----------- PREPARATION 07 ----------
        // Expand the blue wool until it reaches the lime wool

        // Replace all air blocks above bricks, next to blue wool and not next to lime wool with blue wool
        setBlocksWithMask("=queryRel(0,-1,0,45,0)" +
                        "&&(queryRel(1,0,0,35,11)||queryRel(-1,0,0,35,11)||queryRel(0,0,1,35,11)||queryRel(0,0,-1,35,11)||queryRel(1,0,1,35,11)||queryRel(-1,0,-1,35,11)||queryRel(-1,0,1,35,11)||queryRel(1,0,-1,35,11))" +
                        "&&!(queryRel(1,0,0,35,5)||queryRel(-1,0,0,35,5)||queryRel(0,0,1,35,5)||queryRel(0,0,-1,35,5)||queryRel(1,0,1,35,5)||queryRel(-1,0,-1,35,5)||queryRel(-1,0,1,35,5)||queryRel(1,0,-1,35,5))",
                XMaterial.BLUE_WOOL, 20);


        // Place the last blue wool between the lime wool and the blue wool
        setBlocksWithMask("=queryRel(0,-1,0,45,0)&&(queryRel(1,0,0,35,11)||queryRel(-1,0,0,35,11)||queryRel(0,0,1,35,11)||queryRel(0,0,-1,35,11)||queryRel(1,0,1,35,11)||queryRel(-1,0,-1,35,11)||queryRel(-1,0,1,35,11)||queryRel(1,0,-1,35,11))",
                XMaterial.BLUE_WOOL);


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

        // Replace all blocks that are not bricks around the yellow wool with yellow wool
        setBlocksWithMask("=!queryRel(0,0,0,45,0)&&(queryRel(-1,0,0,35,4)||queryRel(1,0,0,35,4)||queryRel(0,0,1,35,4)||queryRel(0,0,-1,35,4))",
                XMaterial.YELLOW_WOOL, 20);

        // Make the outline as thin as possible and fill all inner corners with yellow wool that are too thick
        String[] queries = {
            "=queryRel(-1,0,0,35,4)&&queryRel(0,0,-1,35,4)&&queryRel(0,0,1,45,-1)&&queryRel(1,0,0,45,-1)",
            "=queryRel(-1,0,0,35,4)&&queryRel(0,0,1,35,4)&&queryRel(0,0,-1,45,-1)&&queryRel(1,0,0,45,-1)",
            "=queryRel(1,0,0,35,4)&&queryRel(0,0,-1,35,4)&&queryRel(0,0,1,45,-1)&&queryRel(-1,0,0,45,-1)",
            "=queryRel(1,0,0,35,4)&&queryRel(0,0,1,35,4)&&queryRel(0,0,-1,45,-1)&&queryRel(-1,0,0,45,-1)",
            "=queryRel(-1,0,0,35,4)&&queryRel(0,0,-1,45,-1)&&queryRel(0,0,1,45,-1)&&queryRel(1,0,0,45,-1)",
            "=queryRel(1,0,0,35,4)&&queryRel(0,0,1,45,-1)&&queryRel(0,0,-1,45,-1)&&queryRel(-1,0,0,45,-1)",
            "=queryRel(1,0,0,45,-1)&&queryRel(0,0,-1,35,4)&&queryRel(0,0,1,45,-1)&&queryRel(-1,0,0,45,-1)",
            "=queryRel(1,0,0,45,-1)&&queryRel(0,0,1,35,4)&&queryRel(0,0,-1,45,-1)&&queryRel(-1,0,0,45,-1)"
        };
        replaceBlocksWithMask(Arrays.asList(queries), XMaterial.RED_WOOL, XMaterial.YELLOW_WOOL, 2);



        // ----------- BASE ----------
        int currentHeight = 0;

        if(baseHeight > 0)
            for(int i = 0; i < baseHeight; i++) {
                currentHeight++;

                // Move wool one block up
                moveWoolUp(0);

                // Select everything x blocks above bricks. Then replace that with lapislazuli
                setBlocksWithMask("=queryRel(0," + (-currentHeight) + ",0,45,-1)", XMaterial.LAPIS_BLOCK);

                // Raise the yellow wool layer by one block
                raiseGoldFloor();
            }





        // ----------- FLOORS ----------
        int heightDifference = 0;
        for(int i = 0; i < floorCount; i++) {

            currentHeight++;

            // Move wool one block up
            moveWoolUp(i+1);

            // Select everything x blocks above bricks. Then replace that with lapislazuli block
            setBlocksWithMask("=queryRel(0," + (-currentHeight) + ",0,45,-1)", XMaterial.LAPIS_ORE);

            // Raise the yellow wool layer by one block
            raiseGoldFloor();

            // Windows
            for(int i2 = 0; i2 < windowHeight; i2++) {
                currentHeight++;

                // Move wool one block up
                moveWoolUp(0);


                if (!containsRedWool) {
                    // Replace everything with white glass
                    setBlocksWithMask("=queryRel(0," + (-currentHeight) + ",0,45,-1)", XMaterial.WHITE_STAINED_GLASS);
                }else {
                    // Replace red wool with gray glass
                    replaceBlocksWithMask("=queryRel(0," + (-currentHeight) + ",0,45,-1)", XMaterial.RED_WOOL, XMaterial.GRAY_STAINED_GLASS);

                    // Replace air with lapislazuli ore
                    replaceBlocksWithMask("=queryRel(0," + (-currentHeight) + ",0,45,-1)", XMaterial.AIR, XMaterial.LAPIS_ORE);

                    // Replace lapislazuli ore with lapislazuli ore
                    replaceBlocksWithMask("=queryRel(0," + (-currentHeight) + ",0,45,-1)", XMaterial.LAPIS_ORE, XMaterial.LAPIS_ORE);

                    // Replace lime wool with lapislazuli ore
                    replaceBlocksWithMask("=queryRel(0," + (-currentHeight) + ",0,45,-1)", XMaterial.LIME_WOOL, XMaterial.LAPIS_ORE);
                }

                // Raise the yellow wool layer by one block
                raiseGoldFloor();

            }

            heightDifference = floorHeight - (windowHeight + 1);

            if(heightDifference > 0)
                for(int i2 = 0; i2 < heightDifference; i2++) {
                    currentHeight++;

                    // Move wool one block up
                    moveWoolUp(-1);

                    // Select everything x blocks above bricks. Then replace that with lapislazuli ore
                    setBlocksWithMask("=queryRel(0," + (-currentHeight) + ",0,45,-1)", XMaterial.LAPIS_ORE);

                    // Raise the yellow wool layer by one block
                    raiseGoldFloor();
                }

        }
        if(heightDifference == 0){
            currentHeight++;

            // Move wool one block up
            moveWoolUp(-1);

            // Select everything x blocks above bricks. Then replace that with lapislazuli ore
            setBlocksWithMask("=queryRel(0," + (-currentHeight) + ",0,45,-1)", XMaterial.LAPIS_ORE);

            // Raise the yellow wool layer by one block
            raiseGoldFloor();
        }

        // Remove the red wool
        replaceBlocks(XMaterial.RED_WOOL, XMaterial.AIR);


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

        // Replace any white glass with lapislazuli ore
        replaceBlocks(XMaterial.WHITE_STAINED_GLASS, XMaterial.LAPIS_ORE);

        // Replace any gray glass with the window color
        replaceBlocks(XMaterial.GRAY_STAINED_GLASS, windowColor);




        // ----------- BALCONY 2/2 ----------

        if(containsOrangeWool){

            // Replace all blocks that have green wool below them and have air next to them with the balcony color
            setBlocksWithMask("=queryRel(0,-1,0,35,13)&&(queryRel(-1,-1,0,0,0)||queryRel(1,-1,0,0,0)||queryRel(0,-1,-1,0,0)||queryRel(0,-1,1,0,0))",
                    balconyFenceColor);

            // If the balcony fence color is enabled, replace green wool with the balcony color
            if(balconyColor != null)
                replaceBlocks(XMaterial.GREEN_WOOL, balconyColor);
        }


        // ----------- ROOF ----------



        if(roofType == RoofType.FLATTER_SLABS || roofType == RoofType.STEEP_SLABS|| roofType == RoofType.MEDIUM_SLABS){

            // Replace the blue wool next to lime wool with lime wool
            replaceBlocksWithMask("=queryRel(1,0,0,35,5)||queryRel(-1,0,0,35,5)||queryRel(0,0,1,35,5)||queryRel(0,0,-1,35,5)||queryRel(1,0,1,35,5)||queryRel(-1,0,-1,35,5)||queryRel(-1,0,1,35,5)||queryRel(1,0,-1,35,5)",
                    XMaterial.BLUE_WOOL, XMaterial.LIME_WOOL);

            // Create the roof house wall staircase
            for(int i = 0; i < maxRoofHeight; i++){
                // Select all air blocks that are above blue wool and above & next to lime wool
                replaceBlocksWithMask("=queryRel(0,-1,0,35,11)&&" +
                            "(" +
                                "queryRel(1,-1,0,35,5)||queryRel(-1,-1,0,35,5)||queryRel(0,-1,1,35,5)||queryRel(0,-1,-1,35,5)||queryRel(1,-1,1,35,5)||queryRel(-1,-1,-1,35,5)||queryRel(-1,-1,1,35,5)||queryRel(1,-1,-1,35,5)" +
                            ")",
                        XMaterial.BLUE_WOOL, XMaterial.LIME_WOOL);

                // Select all air blocks that are above blue wool and next to lime wool
                replaceBlocksWithMask("=queryRel(0,-1,0,35,11)&&" +
                                "(" +
                                    "queryRel(1,0,0,35,5)||queryRel(-1,0,0,35,5)||queryRel(0,0,1,35,5)||queryRel(0,0,-1,35,5)||queryRel(1,0,1,35,5)||queryRel(-1,0,-1,35,5)||queryRel(-1,0,1,35,5)||queryRel(1,0,-1,35,5)" +
                                ")",
                        XMaterial.BLUE_WOOL, XMaterial.LIME_WOOL);

                // Select all air blocks that are above blue wool and replace them with blue wool
                setBlocksWithMask(">35:11", XMaterial.BLUE_WOOL);
            }


            // (One more yellow wool layer) Replace everything above yellow wool with one layer yellow wool
            setBlocksWithMask(">35:4", XMaterial.YELLOW_WOOL);

            // (First Roof Layer) Select only air blocks that are next to yellow wool in any direction and above lapislazuli ore. Then replace them with stone slabs
            replaceBlocksWithMask("=queryRel(0,-1,0,21,0)&&queryRel(0,0,0,0,0)&&(queryRel(1,0,0,35,4)||queryRel(1,0,0,35,4)||queryRel(-1,0,0,35,4)||queryRel(0,0,1,35,4)||queryRel(0,0,-1,35,4))",
                XMaterial.LAPIS_ORE, XMaterial.STONE_SLAB);

            // (Fix First Roof Layer) Select only air blocks that are next to stone slabs and lime wool in any direction and above lapislazuli ore. Then replace them with stone slabs
            replaceBlocksWithMask("=queryRel(0,-1,0,21,0)&&queryRel(0,0,0,0,0)&&(queryRel(1,0,0,44,0)||queryRel(-1,0,0,44,0)||queryRel(0,0,1,44,0)||queryRel(0,0,-1,44,0))",
                    XMaterial.LAPIS_ORE, XMaterial.STONE_SLAB, 2);

            // (Overhang Roof Layer 1) Select all air blocks next to lapislazuli ores that have a stone slab above them. Then replace them with and upside down stone slab
            setBlocksWithMask("=queryRel(0,0,0,0,0)&&" +
                    "(" +
                        "(queryRel(1,0,0,21,-1)&&queryRel(1,1,0,44,0))||" +
                        "(queryRel(-1,0,0,21,-1)&&queryRel(-1,1,0,44,0))||" +
                        "(queryRel(0,0,1,21,-1)&&queryRel(0,1,1,44,0))||" +
                        "(queryRel(0,0,-1,21,-1)&&queryRel(0,1,-1,44,0))" +
                    ")",
                    getSlab(BlockTypes.STONE_SLAB, "top"));

            // (Overhang Roof Layer 2) Select all air blocks next to upside down stone slab and lapislazuli. Then replace them with and upside down stone slab
            setBlocksWithMask("=queryRel(0,0,0,0,0)&&" +
                    "(" +
                        "(queryRel(1,0,0,44,8)&&(queryRel(0,0,1,21,0)||queryRel(0,0,-1,21,0)))||" +
                        "(queryRel(-1,0,0,44,8)&&(queryRel(0,0,1,21,0)||queryRel(0,0,-1,21,0)))||" +
                        "(queryRel(0,0,1,44,8)&&(queryRel(1,0,0,21,0)||queryRel(-1,0,0,21,0)))||" +
                        "(queryRel(0,0,-1,44,8)&&(queryRel(1,0,0,21,0)||queryRel(-1,0,0,21,0)))" +
                    ")",
                    getSlab(BlockTypes.STONE_SLAB, "top"));


            // Replace the highest yellow wool layer with double slabs
            replaceBlocksWithMask("<0", XMaterial.YELLOW_WOOL, XMaterial.STONE);


            maxRoofHeight = maxRoofHeight - 1;
            if(maxRoofHeight > 0)
                for(int i = 0; i < maxRoofHeight; i++) {
                    if(roofType == RoofType.FLATTER_SLABS || roofType == RoofType.MEDIUM_SLABS)
                        //Only select air block that are surrounded by other stone slabs below or which are directly neighbors to lime wool or blue wool
                        replaceBlocksWithMask("=queryRel(0,-1,0,43,0)&&" +
                            "(" +
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
                                "||queryRel(0,0,-1,35,5)||queryRel(0,0,-1,35,11)" +
                            ")",
                            getSlab(BlockTypes.STONE_SLAB, "double"), getSlab(BlockTypes.STONE_SLAB, "bottom")
                        );
                    else
                        replaceBlocksWithMask("=!(queryRel(1,-1,0,44,-1)||queryRel(-1,-1,0,44,-1)||queryRel(0,-1,1,44,-1)||queryRel(0,-1,-1,44,-1))",
                                getSlab(BlockTypes.STONE_SLAB, "double"), getSlab(BlockTypes.STONE_SLAB, "bottom"));


                    if(roofType == RoofType.FLATTER_SLABS)

                        //Only select air block that are surrounded by other stone slabs or which are directly neighbors to lime wool or blue wool
                        replaceBlocksWithMask(
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
                                "||queryRel(0,0,-1,35,5)||queryRel(0,0,-1,35,11)",
                            getSlab(BlockTypes.STONE_SLAB, "bottom"), getSlab(BlockTypes.STONE_SLAB, "double")
                        );
                    else
                        replaceBlocksWithMask("=!(queryRel(1,0,0,0,-1)||queryRel(-1,0,0,0,-1)||queryRel(0,0,1,0,-1)||queryRel(0,0,-1,0,-1))"
                            ,getSlab(BlockTypes.STONE_SLAB, "bottom"), getSlab(BlockTypes.STONE_SLAB, "double"));
                }

            // Replace everything above upside down stone slabs with purple wool
            setBlocksWithMask(">44:8", XMaterial.RED_WOOL);

            expandEmeraldOre();

            // (Overhang Roof Layer 3) Select all air blocks next to two upside down stone slabs. Then replace them with and upside down stone slab
            replaceBlocksWithMask("=(queryRel(1,0,0,44,8)&&queryRel(2,0,0,44,8))||(queryRel(-1,0,0,44,8)&&queryRel(-2,0,0,44,8))||(queryRel(0,0,1,44,8)&&queryRel(0,0,2,44,8))||(queryRel(0,0,-1,44,8)&&queryRel(0,0,-2,44,8))",
                getAir(), getSlab(BlockTypes.STONE_SLAB, "top"));

            // Select all lime wool that are above & next to lime wool and replace it with stone slabs
            replaceBlocksWithMask("=queryRel(1,-1,0,35,5)||queryRel(-1,-1,0,35,5)||queryRel(0,-1,1,35,5)||queryRel(0,-1,-1,35,5)||queryRel(1,-1,1,35,5)||queryRel(-1,-1,-1,35,5)||queryRel(-1,-1,1,35,5)||queryRel(1,-1,-1,35,5)",
                XMaterial.LIME_WOOL, XMaterial.STONE_SLAB);

            // Select all lime wool that are above & next to upside down stone slabs and replace it with stone slabs
            replaceBlocksWithMask("=queryRel(1,-1,0,44,8)||queryRel(-1,-1,0,44,8)||queryRel(0,-1,1,44,8)||queryRel(0,-1,-1,44,8)||queryRel(1,-1,1,44,8)||queryRel(-1,-1,-1,44,8)||queryRel(-1,-1,1,44,8)||queryRel(1,-1,-1,44,8)",
                XMaterial.LIME_WOOL, XMaterial.STONE_SLAB);


            // Select all air blocks that are below & next to lime wool and under a stone slab and replace it with upside down stone slabs
            replaceBlocksWithMask("=queryRel(0,1,0,44,0)&&(queryRel(1,1,0,35,5)||queryRel(-1,1,0,35,5)||queryRel(0,1,1,35,5)||queryRel(0,1,-1,35,5)||queryRel(1,1,1,35,5)||queryRel(-1,1,-1,35,5)||queryRel(-1,1,1,35,5)||queryRel(1,1,-1,35,5))",
                getAir(), getSlab(BlockTypes.STONE_SLAB, "top"));

            // Select all air blocks that are next to lime wool and under a stone slab and replace it with upside down stone slabs
            replaceBlocksWithMask("=queryRel(0,1,0,44,0)&&(queryRel(1,0,0,35,5)||queryRel(-1,0,0,35,5)||queryRel(0,0,1,35,5)||queryRel(0,0,-1,35,5))",
                getAir(), getSlab(BlockTypes.STONE_SLAB, "top"));


            // Select all leftover lime wool replace it with double stone slabs
            replaceBlocks(getEmeraldOre(), getSlab(BlockTypes.STONE_SLAB, "double"));

            // Replace blue wool with lapislazuli ore
            replaceBlocks(XMaterial.BLUE_WOOL, XMaterial.LAPIS_ORE);


            BlockState[] bottomSlabs = new BlockState[roofColor.length];
            BlockState[] topSlabs = new BlockState[roofColor.length];
            BlockState[] doubleSlabs = new BlockState[roofColor.length];

            for(int i = 0; i < roofColor.length; i++){
                BlockType bt = Item.convertXMaterialToWEBlockType(roofColor[i]);

                if(bt == null)
                    continue;

                bottomSlabs[i] = getSlab(bt, "bottom");
                topSlabs[i] = getSlab(bt, "top");
                doubleSlabs[i] = getSlab(bt, "double");
            }

            replaceBlocks(getSlab(BlockTypes.STONE_SLAB, "bottom"), bottomSlabs);
            replaceBlocks(getSlab(BlockTypes.STONE_SLAB, "top"), topSlabs);
            replaceBlocks(getSlab(BlockTypes.STONE_SLAB, "double"), doubleSlabs);

        } else if(roofType == RoofType.FLAT){
            replaceBlocksWithMask(">21", XMaterial.AIR, XMaterial.GRAY_CARPET);
            replaceBlocksWithMask("<0", XMaterial.YELLOW_WOOL, XMaterial.DISPENSER);

        } else if(roofType == RoofType.STAIRS){

            // Create the roof house wall staircase
            for(int i = 0; i < maxRoofHeight; i++){
                // Select all air blocks that are above blue wool and next to lime wool
                setBlocksWithMask("=queryRel(0,-1,0,35,11)&&" +
                        "(" +
                            "queryRel(1,-1,0,35,5)||queryRel(-1,-1,0,35,5)||queryRel(0,-1,1,35,5)||queryRel(0,-1,-1,35,5)||queryRel(1,-1,1,35,5)||queryRel(-1,-1,-1,35,5)||queryRel(-1,-1,1,35,5)||queryRel(1,-1,-1,35,5)" +
                        ")",
                    XMaterial.LIME_WOOL);

                // Select all air blocks that are above blue wool and replace them with blue wool
                replaceBlocksWithMask(">35:11", XMaterial.AIR, XMaterial.BLUE_WOOL);
            }

            // (One more yellow wool layer) Replace everything above yellow wool with one layer yellow wool
            setBlocksWithMask(">35:4", XMaterial.YELLOW_WOOL);

            // (First Roof Layer) Select only air blocks that are next to yellow wool in any direction and above lapislazuli ore. Then replace them with stone bricks
            setBlocksWithMask("=queryRel(0,-1,0,21,0)&&queryRel(0,0,0,0,0)&&(queryRel(1,0,0,35,4)||queryRel(1,0,0,35,4)||queryRel(-1,0,0,35,4)||queryRel(0,0,1,35,4)||queryRel(0,0,-1,35,4))",
                XMaterial.STONE_BRICKS);

            // (Fix First Roof Layer) Select only air blocks that are next to stone slabs in any direction and above lapislazuli ore. Then replace them with stone bricks
            setBlocksWithMask("=queryRel(0,-1,0,21,0)&&queryRel(0,0,0,0,0)&&(queryRel(1,0,0,98,0)||queryRel(1,0,0,98,0)||queryRel(-1,0,0,98,0)||queryRel(0,0,1,98,0)||queryRel(0,0,-1,98,0))",
                XMaterial.STONE_BRICKS, 2);

            // Replace everything around stone bricks with sponge
            replaceBlocksWithMask("=queryRel(1,0,0,98,0)||queryRel(-1,0,0,98,0)||queryRel(0,0,1,98,0)||queryRel(0,0,-1,98,0)",
                    XMaterial.AIR, XMaterial.SPONGE);

            // (Overhang Roof Layer) Select all air blocks next to lapislazuli ores that have sponge above them. Then replace them with stone bricks
            replaceBlocksWithMask("<19",
                XMaterial.AIR, XMaterial.STONE_BRICKS);

            // Replace sponge with air
            replaceBlocks(XMaterial.SPONGE, XMaterial.AIR);

            // Replace all air blocks that are next to lapislazuli ores and next to stone bricks with lime wool
            replaceBlocksWithMask("=(queryRel(1,0,0,98,0)&&queryRel(0,0,1,21,0))" +
                            "||(queryRel(1,0,0,98,0)&&queryRel(0,0,-1,21,0))" +
                            "||(queryRel(-1,0,0,98,0)&&queryRel(0,0,-1,21,0))" +
                            "||(queryRel(-1,0,0,98,0)&&queryRel(0,0,1,21,0))" +
                            "||(queryRel(0,0,1,98,0)&&queryRel(1,0,0,21,0))" +
                            "||(queryRel(0,0,-1,98,0)&&queryRel(1,0,0,21,0))" +
                            "||(queryRel(0,0,-1,98,0)&&queryRel(-1,0,0,21,0))" +
                            "||(queryRel(0,0,1,98,0)&&queryRel(-1,0,0,21,0))",
                    XMaterial.AIR, XMaterial.SPONGE);

            // Replace all air blocks that have sponge with lapislazuli ore behind them with red wool
            replaceBlocksWithMask("=(queryRel(1,0,0,19,0)&&queryRel(2,0,0,21,0))||(queryRel(-1,0,0,19,0)&&queryRel(-2,0,0,21,0))||(queryRel(0,0,1,19,0)&&queryRel(0,0,2,21,0))||(queryRel(0,0,-1,19,0)&&queryRel(0,0,-2,21,0))",
                    XMaterial.AIR, XMaterial.RED_WOOL);

            // Replace sponge with lime wool
            replaceBlocks(XMaterial.SPONGE, XMaterial.LIME_WOOL);



            maxRoofHeight = maxRoofHeight - 1;

            if(maxRoofHeight > 0)
                for(int i = 0; i < maxRoofHeight; i++) {
                    // Every 2nd layer
                    if(i % 2 == 0)
                        //Only select air block that have yellow wool below them which are surrounded by other stone bricks
                        setBlocksWithMask("=queryRel(0,-1,0,35,4)&&(queryRel(1,-1,0,98,-1)||queryRel(-1,-1,0,98,-1)||queryRel(0,-1,1,98,-1)||queryRel(0,-1,-1,98,-1))",
                                XMaterial.STONE_BRICKS);
                    else
                        // Only select air block that have yellow wool below them which are completely surrounded by other stone bricks
                        setBlocksWithMask("=queryRel(0,-1,0,35,4)&&(queryRel(1,-1,0,98,-1)||queryRel(-1,-1,0,98,-1)||queryRel(0,-1,1,98,-1)||queryRel(0,-1,-1,98,-1)||queryRel(1,-1,1,98,-1)||queryRel(-1,-1,1,98,-1)||queryRel(-1,-1,-1,98,-1)||queryRel(1,-1,-1,98,-1))",
                                XMaterial.STONE_BRICKS);


                    //Only select yellow wool with air blocks above them and put yellow wool above them
                    replaceBlocksWithMask(">35:4", XMaterial.AIR, XMaterial.YELLOW_WOOL);
                }

            // ROOF OVERHANG

            expandEmeraldOre();


            // Replace lime wool with stone bricks
            replaceBlocks(XMaterial.LIME_WOOL, XMaterial.STONE_BRICKS);

            // Replace blue wool with lapislazuli ore
            replaceBlocks(XMaterial.BLUE_WOOL, XMaterial.LAPIS_ORE);

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
            replaceBlocksWithMask("=queryRel(0,1,0,98,0)&&queryRel(-1,0,0,98,0)",
                    getAir(), getStair(BlockTypes.STONE_STAIRS, "west", "top", "straight"));
            replaceBlocksWithMask("=queryRel(0,1,0,98,0)&&queryRel(0,0,1,98,0)",
                    getAir(), getStair(BlockTypes.STONE_STAIRS, "south", "top", "straight"));
            replaceBlocksWithMask("=queryRel(0,1,0,98,0)&&queryRel(0,0,-1,98,0)",
                    getAir(), getStair(BlockTypes.STONE_STAIRS, "north", "top", "straight"));


            // Fill the remaining overhang with upside-down stairs which are surrounded by 1 stone brick and one stone brick above them
            replaceBlocksWithMask("=queryRel(0,1,0,98,0)&&(queryRel(1,0,0,98,0)||queryRel(1,0,1,98,0)||queryRel(1,0,-1,98,0))",
                    getAir(), getStair(BlockTypes.STONE_STAIRS, "east", "top", "straight"));
            replaceBlocksWithMask("=queryRel(0,1,0,98,0)&&(queryRel(-1,0,0,98,0)||queryRel(-1,0,1,98,0)||queryRel(-1,0,-1,98,0))",
                    getAir(), getStair(BlockTypes.STONE_STAIRS, "west", "top", "straight"));
            replaceBlocksWithMask("=queryRel(0,1,0,98,0)&&(queryRel(0,0,1,98,0)||queryRel(1,0,1,98,0)||queryRel(-1,0,1,98,0))",
                    getAir(), getStair(BlockTypes.STONE_STAIRS, "south", "top", "straight"));
            replaceBlocksWithMask("=queryRel(0,1,0,98,0)&&(queryRel(0,0,-1,98,0)||queryRel(1,0,-1,98,0)||queryRel(-1,0,-1,98,0))",
                    getAir(), getStair(BlockTypes.STONE_STAIRS, "north", "top", "straight"));


            // (Normal Stair Roof Layer) Replace all air blocks that have a stone brick on one side, 3 air sides and one stone brick below them with stairs
            replaceBlocksWithMask("=queryRel(0,-1,0,98,0)&&(queryRel(1,0,0,98,-1)&&queryRel(-1,0,0,0,-1)&&queryRel(0,0,1,0,-1)&&queryRel(0,0,-1,0,-1))",
                    getAir(), getStair(BlockTypes.STONE_STAIRS, "east", "bottom", "straight"));
            replaceBlocksWithMask("=queryRel(0,-1,0,98,0)&&(queryRel(1,0,0,0,-1)&&queryRel(-1,0,0,98,-1)&&queryRel(0,0,1,0,-1)&&queryRel(0,0,-1,0,-1))",
                    getAir(), getStair(BlockTypes.STONE_STAIRS, "west", "bottom", "straight"));
            replaceBlocksWithMask("=queryRel(0,-1,0,98,0)&&(queryRel(1,0,0,0,-1)&&queryRel(-1,0,0,0,-1)&&queryRel(0,0,1,98,-1)&&queryRel(0,0,-1,0,-1))",
                    getAir(), getStair(BlockTypes.STONE_STAIRS, "south", "bottom", "straight"));
            replaceBlocksWithMask("=queryRel(0,-1,0,98,0)&&(queryRel(1,0,0,0,-1)&&queryRel(-1,0,0,0,-1)&&queryRel(0,0,1,0,-1)&&queryRel(0,0,-1,98,-1))",
                    getAir(), getStair(BlockTypes.STONE_STAIRS, "north", "bottom", "straight"));


            // (Corner Stair Roof Layer) Replace all air blocks that have stone bricks on 2 sides, 2 air or stair sides and one stone brick below them with stairs
            replaceBlocksWithMask("=queryRel(0,-1,0,98,0)&&(queryRel(1,0,0,98,-1)&&queryRel(0,0,1,98,-1)&&(queryRel(-1,0,0,0,-1)||queryRel(-1,0,0,109,-1))&&(queryRel(0,0,-1,0,-1)||queryRel(0,0,-1,109,-1)))",
                    getAir(), getStair(BlockTypes.STONE_STAIRS, "east", "bottom", "inner_right"));
            replaceBlocksWithMask("=queryRel(0,-1,0,98,0)&&(queryRel(-1,0,0,98,-1)&&queryRel(0,0,-1,98,-1)&&(queryRel(1,0,0,0,-1)||queryRel(1,0,0,109,-1))&&(queryRel(0,0,1,0,-1)||queryRel(0,0,1,109,-1)))",
                    getAir(), getStair(BlockTypes.STONE_STAIRS, "west", "bottom", "inner_right"));
            replaceBlocksWithMask("=queryRel(0,-1,0,98,0)&&(queryRel(-1,0,0,98,-1)&&queryRel(0,0,1,98,-1)&&(queryRel(1,0,0,0,-1)||queryRel(1,0,0,109,-1))&&(queryRel(0,0,-1,0,-1)||queryRel(0,0,-1,109,-1)))",
                    getAir(), getStair(BlockTypes.STONE_STAIRS, "south", "bottom", "inner_right"));
            replaceBlocksWithMask("=queryRel(0,-1,0,98,0)&&(queryRel(1,0,0,98,-1)&&queryRel(0,0,-1,98,-1)&&(queryRel(-1,0,0,0,-1)||queryRel(-1,0,0,109,-1))&&(queryRel(0,0,1,0,-1)||queryRel(0,0,1,109,-1)))",
                    getAir(), getStair(BlockTypes.STONE_STAIRS, "north", "bottom", "inner_right"));


            // (Corner Stair 2 Roof Layer) Replace all air blocks that have stairs on 2 sides, 2 air sides and one stone brick below them with stairs
            replaceBlocksWithMask("=queryRel(0,-1,0,98,0)&&(queryRel(1,0,0,109,-1)&&queryRel(0,0,1,109,-1)&&queryRel(-1,0,0,0,-1)&&queryRel(0,0,-1,0,-1))",
                    getAir(), getStair(BlockTypes.STONE_STAIRS, "south", "bottom", "inner_left"));
            replaceBlocksWithMask("=queryRel(0,-1,0,98,0)&&(queryRel(1,0,0,109,-1)&&queryRel(0,0,-1,109,-1)&&queryRel(-1,0,0,0,-1)&&queryRel(0,0,1,0,-1))",
                    getAir(), getStair(BlockTypes.STONE_STAIRS, "north", "bottom", "inner_right"));
            replaceBlocksWithMask("=queryRel(0,-1,0,98,0)&&(queryRel(-1,0,0,109,-1)&&queryRel(0,0,1,109,-1)&&queryRel(1,0,0,0,-1)&&queryRel(0,0,-1,0,-1))",
                    getAir(), getStair(BlockTypes.STONE_STAIRS, "south", "bottom", "inner_right"));
            replaceBlocksWithMask("=queryRel(0,-1,0,98,0)&&(queryRel(-1,0,0,109,-1)&&queryRel(0,0,-1,109,-1)&&queryRel(1,0,0,0,-1)&&queryRel(0,0,1,0,-1))",
                    getAir(), getStair(BlockTypes.STONE_STAIRS, "north", "bottom", "inner_left"));


            // Cover leaking yellow wool blocks with stone bricks
            replaceBlocksWithMask("=queryRel(1,0,0,35,4)||queryRel(-1,0,0,35,4)||queryRel(0,0,1,35,4)||queryRel(0,0,-1,35,4)",
                XMaterial.AIR, XMaterial.STONE_BRICKS);


            XMaterial[] stairBlocks = new XMaterial[roofColor.length];
            for(int i = 0; i < roofColor.length; i++)
                stairBlocks[i] = MenuItems.convertStairToBlock(roofColor[i]);

            // Replace stone bricks with the correct color
            replaceBlocks(XMaterial.STONE_BRICKS, stairBlocks);


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
                        BlockType bt = Item.convertXMaterialToWEBlockType(roofColor[i]);

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
        setGmask(null);

        replaceBlocks(XMaterial.LAPIS_ORE, wallColor);
        replaceBlocks(XMaterial.LAPIS_BLOCK, baseColor);
        replaceBlocks(XMaterial.YELLOW_WOOL, XMaterial.GRAY_WOOL);
        replaceBlocks(XMaterial.STONE_SLAB, XMaterial.GRAY_WOOL);


        BlockState[] doubleSlabs = new BlockState[roofColor.length];

        for(int i = 0; i < roofColor.length; i++){
            BlockType bt = Item.convertXMaterialToWEBlockType(roofColor[i]);

            if(bt == null)
                continue;

            doubleSlabs[i] = getSlab(bt, "double");
        }

        replaceBlocks(Item.convertXMaterialToWEBlockType(XMaterial.DISPENSER).getDefaultState(), doubleSlabs);

        // Finish the script
        finish(blocks, selectionPoints);
    }

    // Move lapislazuli ore, lime wool nad red wool one block up
    private void moveWoolUp(int floor) {

        setBlocksWithMask(">35:11", XMaterial.BLUE_WOOL);
        setBlocksWithMask(">35:14", XMaterial.RED_WOOL);
        setBlocksWithMask(">35:5", XMaterial.LIME_WOOL);

        if(floor >= 0) {
            if (floor == 0)
                replaceBlocksWithMask(">35:1,35:13", XMaterial.AIR, XMaterial.ORANGE_WOOL);
            else
                replaceBlocksWithMask(">35:1", XMaterial.AIR, XMaterial.GREEN_WOOL);
        }
    }

    private void raiseGoldFloor() {
        setBlocksWithMask(">35:4", XMaterial.YELLOW_WOOL);
    }

    private void expandEmeraldOre(){

        // Replace everything above green wool with temporary purple wool
        setBlocksWithMask(">35:5", XMaterial.RED_WOOL);

        // Replace air next to red wool with redstone
        replaceBlocksWithMask("=queryRel(1,0,0,35,14)||queryRel(-1,0,0,35,14)||queryRel(0,0,1,35,14)||queryRel(0,0,-1,35,14)"
                , XMaterial.AIR, XMaterial.RED_WOOL);

        // Replace air next to lime wool with lime wool
        replaceBlocksWithMask("=queryRel(1,0,0,35,5)||queryRel(-1,0,0,35,5)||queryRel(0,0,1,35,5)||queryRel(0,0,-1,35,5)"
                , XMaterial.AIR, XMaterial.LIME_WOOL);

        // Replace red wool with air
        replaceBlocks(XMaterial.RED_WOOL, XMaterial.AIR);
    }



    private BlockState getAir(){
        BlockType blockType = BlockTypes.AIR;

        if(blockType == null)
            return null;

        return blockType.getDefaultState();
    }

    private BlockState getEmeraldOre(){
        BlockType blockType = BlockTypes.LIME_WOOL;

        if(blockType == null)
            return null;

        return blockType.getDefaultState();
    }
}
