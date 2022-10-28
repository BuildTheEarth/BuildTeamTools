package net.buildtheearth.buildteam.components.generator.house;

import net.buildtheearth.buildteam.components.generator.Generator;
import net.buildtheearth.buildteam.components.generator.GeneratorType;
import net.buildtheearth.buildteam.components.generator.History;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

public class HouseScripts {

    public static void buildscript_v_1_2(Player p,
                                         String wallColor,
                                         String roofColor,
                                         String baseColor,
                                         String windowColor,
                                         RoofType roofType,
                                         int floorCount,
                                         int floorHeight,
                                         int baseHeight,
                                         int windowHeight,
                                         int windowWidth,
                                         int windowDistance,
                                         int maxRoofHeight){



        int operations = 0;
        p.chat("/clearhistory");


        // ----------- PREPARATION ----------

        // Disable the current global mask
        p.chat("//gmask");

        // Expand the current selection down by 10 blocks
        p.chat("//expand 10 down");

        int up_expand = 5 + maxRoofHeight + baseHeight + (floorCount * floorHeight);

        // Expand the current selection up by "up_expand" blocks
        p.chat("//expand " + up_expand + " up");

        // Replace all bricks with sponge
        p.chat("//replace 45 19");
        operations++;

        // Replace all sponges with bricks that have air above them
        p.chat("//gmask <0");
        p.chat("//replace 19 45");
        operations++;

        // Disable the global mask
        p.chat("//gmask");

        // Replace all left sponges with grass
        p.chat("//replace 19 2");
        operations++;





        // ----------- GROUND ----------

        // Select all blocks around the yellow wool block
        p.chat("//gmask =queryRel(-1,0,0,35,4)||queryRel(1,0,0,35,4)||queryRel(0,0,1,35,4)||queryRel(0,0,-1,35,4)");

        for(int i = 0; i < 20; i++) {
            // Replace all blocks that are not bricks with yellow wool
            p.chat("//replace !45 35:4");
            operations++;
        }



        // Make the outline as thin as possible and fill all inner corners with yellow wool that are too thick
        for(int i = 0; i < 2; i++) {
            p.chat("//gmask =queryRel(-1,0,0,35,4)&&queryRel(0,0,-1,35,4)&&queryRel(0,0,1,45,-1)&&queryRel(1,0,0,45,-1)");
            p.chat("//replace 45 35:4");
            operations++;
            p.chat("//gmask =queryRel(-1,0,0,35,4)&&queryRel(0,0,1,35,4)&&queryRel(0,0,-1,45,-1)&&queryRel(1,0,0,45,-1)");
            p.chat("//replace 45 35:4");
            operations++;
            p.chat("//gmask =queryRel(1,0,0,35,4)&&queryRel(0,0,-1,35,4)&&queryRel(0,0,1,45,-1)&&queryRel(-1,0,0,45,-1)");
            p.chat("//replace 45 35:4");
            operations++;
            p.chat("//gmask =queryRel(1,0,0,35,4)&&queryRel(0,0,1,35,4)&&queryRel(0,0,-1,45,-1)&&queryRel(-1,0,0,45,-1)");
            p.chat("//replace 45 35:4");
            operations++;
            p.chat("//gmask =queryRel(-1,0,0,35,4)&&queryRel(0,0,-1,45,-1)&&queryRel(0,0,1,45,-1)&&queryRel(1,0,0,45,-1)");
            p.chat("//replace 45 35:4");
            operations++;
            p.chat("//gmask =queryRel(1,0,0,35,4)&&queryRel(0,0,1,45,-1)&&queryRel(0,0,-1,45,-1)&&queryRel(-1,0,0,45,-1)");
            p.chat("//replace 45 35:4");
            operations++;
            p.chat("//gmask =queryRel(1,0,0,45,-1)&&queryRel(0,0,-1,35,4)&&queryRel(0,0,1,45,-1)&&queryRel(-1,0,0,45,-1)");
            p.chat("//replace 45 35:4");
            operations++;
            p.chat("//gmask =queryRel(1,0,0,45,-1)&&queryRel(0,0,1,35,4)&&queryRel(0,0,-1,45,-1)&&queryRel(-1,0,0,45,-1)");
            p.chat("//replace 45 35:4");
            operations++;
        }


        // ----------- BASE ----------
        int currentheight = 0;

        if(baseHeight > 0)
        for(int i = 0; i < baseHeight; i++) {
            currentheight++;

            // Select everything x blocks above bricks. Then replace that with lapizlazuli
            p.chat("//gmask =queryRel(0," + (-currentheight) + ",0,45,-1)");
            p.chat("//set 22");
            operations++;

            // Disable the global mask
            p.chat("//gmask");

            // Raise the yellow wool layer by one block
            p.chat("//replace >35:4 35:4");
            operations++;
        }



        // ----------- FLOORS ----------
        int heightdifference = 0;
        for(int i = 0; i < floorCount; i++) {
            currentheight++;

            // Select everything x blocks above bricks. Then replace that with lapizlazuli ore
            p.chat("//gmask =queryRel(0," + (-currentheight) + ",0,45,-1)");
            p.chat("//set 21");
            operations++;

            // Raise the yellow wool layer by one block
            p.chat("//gmask");
            p.chat("//replace >35:4 35:4");
            operations++;

            for(int i2 = 0; i2 < windowHeight; i2++) {
                currentheight++;

                // Select everything x blocks above bricks. Then replace that with white glass
                p.chat("//gmask =queryRel(0," + (-currentheight) + ",0,45,-1)");
                p.chat("//set 95:0");
                operations++;

                // Raise the yellow wool layer by one block
                p.chat("//gmask");
                p.chat("//replace >35:4 35:4");
                operations++;
            }

            heightdifference = floorHeight - (windowHeight + 1);

            if(heightdifference > 0)
            for(int i2 = 0; i2 < heightdifference; i2++) {
                currentheight++;

                // Select everything x blocks above bricks. Then replace that with lapizlazuli ore
                p.chat("//gmask =queryRel(0," + (-currentheight) + ",0,45,-1)");
                p.chat("//set 21");
                operations++;

                // Disable the global mask, raise the yellow wool layer by one block
                p.chat("//gmask");
                p.chat("//replace >35:4 35:4");
                operations++;
            }
        }
        if(heightdifference == 0){
            currentheight++;
            // Select everything x blocks above bricks. Then replace that with white glass
            p.chat("//gmask =queryRel(0," + (-currentheight) + ",0,45,-1)");
            p.chat("//set 21");
            operations++;

            // Disable the global mask, raise the yellow wool layer by one block
            p.chat("//gmask");
            p.chat("//replace >35:4 35:4");
            operations++;
        }



        // ----------- WINDOWS ----------
        int winsum = windowWidth + windowDistance;
        int wd2 = windowDistance - 1;

        // If winsum=6 & wd=2:    Any white glass block whose z%6 remainder > 1, and which has air in the x+1 direction, and has no air in the x-1 direction, and has no white glass in the x-1 direction, is replaced with gray glass.
        p.chat("//gmask =(abs(z%" + winsum + ")-" + wd2 + ")&&queryRel(1,0,0,0,-1)&&!queryRel(-1,0,0,0,-1)&&!queryRel(-1,0,0,95,-1)");
        p.chat("//replace 95:0 95:7");
        operations++;

        // If winsum=6 & wd=2:    Any white glass block whose z%6 remainder > 1, and which has air in the x-1 direction, and has no air in the x+1 direction, and has no white glass in the x+1 direction, is replaced with gray glass.
        p.chat("//gmask =(abs(z%" + winsum + ")-" + wd2 + ")&&queryRel(-1,0,0,0,-1)&&!queryRel(1,0,0,0,-1)&&!queryRel(1,0,0,95,-1)");
        p.chat("//replace 95:0 95:7");
        operations++;

        // If winsum=6 & wd=2:    Any white glass block whose x%6 remainder > 1, and which has air in the z+1 direction, and has no air in the z-1 direction, and has no white glass in the z-1 direction, is replaced with gray glass.
        p.chat("//gmask =(abs(x%" + winsum + ")-" + wd2 + ")&&queryRel(0,0,1,0,-1)&&!queryRel(0,0,-1,0,-1)&&!queryRel(0,0,-1,95,-1)");
        p.chat("//replace 95:0 95:7");
        operations++;

        // If winsum=6 & wd=2:    Any white glass block whose x%6 remainder > 1, and which has air in the z-1 direction, and has no air in the z+1 direction, and has no white glass in the z+1 direction, is replaced with gray glass.
        p.chat("//gmask =(abs(x%" + winsum + ")-" + wd2 + ")&&queryRel(0,0,-1,0,-1)&&!queryRel(0,0,1,0,-1)&&!queryRel(0,0,1,95,-1)");
        p.chat("//replace 95:0 95:7");
        operations++;

        // Disable the global mask
        p.chat("//gmask");


        // Replace any white glass with lapiz ore
        p.chat("//replace 95:0 21");
        operations++;

        // Replace any gray glass with the window color
        p.chat("//replace 95:7 " + windowColor);
        operations++;


        //roof
        String rm1 = roofColor;
        String rm2 = roofColor;
        String rm3 = roofColor;



        if(roofType == RoofType.SLABS){
            // (One more yellow wool layer) Replace everything above yellow wool with one layer yellow wool
            p.chat("//replace >35:4 35:4");
            operations++;

            // (First Roof Layer) Select only air blocks that are next to yellow wool in any direction and above lapizlazuli ore. Then replace them with stone slabs
            p.chat("//gmask =queryRel(0,0,0,0,0)&&(queryRel(1,0,0,35,4)||queryRel(1,0,0,35,4)||queryRel(-1,0,0,35,4)||queryRel(0,0,1,35,4)||queryRel(0,0,-1,35,4))");
            p.chat("//replace >21 44");
            operations++;

            // (Fix First Roof Layer) Select only air blocks that are next to stone slabs in any direction and above lapizlazuli ore. Then replace them with stone slabs
            p.chat("//gmask =queryRel(0,0,0,0,0)&&(queryRel(1,0,0,44,0)||queryRel(1,0,0,44,0)||queryRel(-1,0,0,44,0)||queryRel(0,0,1,44,0)||queryRel(0,0,-1,44,0))");
            p.chat("//replace >21 44");
            p.chat("//replace >21 44");
            operations++;
            operations++;

            // (Overhang Roof Layer) Select all air blocks next to lapizlazuli ores that have a stone slab above them. Then replace them with and upside down stone slab
            p.chat("//gmask =(queryRel(1,0,0,21,-1)&&queryRel(1,1,0,44,0))||(queryRel(-1,0,0,21,-1)&&queryRel(-1,1,0,44,0))||(queryRel(0,0,1,21,-1)&&queryRel(0,1,1,44,0))||(queryRel(0,0,-1,21,-1)&&queryRel(0,1,-1,44,0))");
            p.chat("//replace 0 44:8");
            operations++;

            // Replace highest yellow wool layer with double slabs
            p.chat("//gmask <0");
            p.chat("//replace 35:4 43");
            operations++;

            maxRoofHeight = maxRoofHeight - 1;

            if(maxRoofHeight > 0)
            for(int i = 0; i < maxRoofHeight; i++) {
                //Only select air block that have stone slabs below them which are surrounded by other stone slabs
                p.chat("//gmask =!(queryRel(1,-1,0,44,-1)||queryRel(-1,-1,0,44,-1)||queryRel(0,-1,1,44,-1)||queryRel(0,-1,-1,4,-1)||(queryRel(-1,-1,1,44,-1)||queryRel(1,-1,-1,44,-1)||queryRel(1,-1,1,44,-1)||queryRel(-1,-1,-1,4,-1)))");
                p.chat("//replace >43 44");
                operations++;
                p.chat("//gmask =!(queryRel(1,0,0,0,-1)||queryRel(-1,0,0,0,-1)||queryRel(0,0,1,0,-1)||queryRel(0,0,-1,0,-1))");
                p.chat("//replace 44 43");
                operations++;
            }

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
        }

        if(roofType == RoofType.FLAT){
            p.chat("//gmask 0");
            p.chat("//replace >21 171:7");
            operations++;
            p.chat("//gmask <0");
            p.chat("//replace 35:4 23");
            operations++;
        }

        p.chat("//gmask 0,45,31,37,38,39,40,175");

        for(int i = 0; i < 5; i++) {
            p.chat("//replace <22 22");
            operations++;
        }

        p.chat("//gmask");
        p.chat("//replace 21 " + wallColor);
        operations++;
        p.chat("//replace 22 " + baseColor);
        operations++;
        p.chat("//replace 35:4 35:7");
        operations++;


        p.chat("//gmask");



        p.chat("//replace 44:0 " + rm1);
        operations++;
        p.chat("//replace 44:8 " + rm2);
        operations++;
        p.chat("//replace 43:0 " + rm3);
        operations++;
        p.chat("//replace 23 " + rm3);
        operations++;


        Generator.getPlayerHistory(p).addHistoryEntry(new History.HistoryEntry(GeneratorType.HOUSE, operations));

        /*
        Bukkit.broadcastMessage("wallColor: " + wallColor);
        Bukkit.broadcastMessage("roofColor: " + roofColor);
        Bukkit.broadcastMessage("baseColor: " + baseColor);
        Bukkit.broadcastMessage("windowColor: " + windowColor);
        Bukkit.broadcastMessage("roofType: " + roofType);
        Bukkit.broadcastMessage("floorCount: " + floorCount);
        Bukkit.broadcastMessage("floorHeight: " + floorHeight);
        Bukkit.broadcastMessage("baseHeight: " + baseHeight);
        Bukkit.broadcastMessage("windowHeight: " + windowHeight);
        Bukkit.broadcastMessage("windowWidth: " + windowWidth);
        Bukkit.broadcastMessage("windowDistance: " + windowDistance);
        Bukkit.broadcastMessage("maxRoofHeight: " + maxRoofHeight);
        Bukkit.broadcastMessage(rm1);
        Bukkit.broadcastMessage(rm2);
        Bukkit.broadcastMessage(rm3);*/
    }
}
