package net.buildtheearth.buildteam.components.generator.house;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class HouseScripts {

    public static void houseScript_v_1_2(Player p,
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
                                         int maxRoofHeight)
    {

        p.chat("//gmask");
        p.chat("//expand 10 down");

        int up_expand = 5 + maxRoofHeight + baseHeight + (floorCount * floorHeight);

        p.chat("//expand " + up_expand + " up");
        p.chat("//replace 45 19");
        p.chat("//gmask <0");
        p.chat("//replace 19 45");
        p.chat("//gmask");
        p.chat("//replace 19 2");


        //ground
        p.chat("//gmask =queryRel(-1,0,0,35,4)||queryRel(1,0,0,35,4)||queryRel(0,0,1,35,4)||queryRel(0,0,-1,35,4)");

        for(int i = 0; i < 20; i++)
            p.chat("//replace !45 35:4");

        for(int i = 0; i < 2; i++) {
            p.chat("//gmask =queryRel(-1,0,0,35,4)&&queryRel(0,0,-1,35,4)&&queryRel(0,0,1,45,-1)&&queryRel(1,0,0,45,-1)");
            p.chat("//replace 45 35:4");
            p.chat("//gmask =queryRel(-1,0,0,35,4)&&queryRel(0,0,1,35,4)&&queryRel(0,0,-1,45,-1)&&queryRel(1,0,0,45,-1)");
            p.chat("//replace 45 35:4");
            p.chat("//gmask =queryRel(1,0,0,35,4)&&queryRel(0,0,-1,35,4)&&queryRel(0,0,1,45,-1)&&queryRel(-1,0,0,45,-1)");
            p.chat("//replace 45 35:4");
            p.chat("//gmask =queryRel(1,0,0,35,4)&&queryRel(0,0,1,35,4)&&queryRel(0,0,-1,45,-1)&&queryRel(-1,0,0,45,-1)");
            p.chat("//replace 45 35:4");
            p.chat("//gmask =queryRel(-1,0,0,35,4)&&queryRel(0,0,-1,45,-1)&&queryRel(0,0,1,45,-1)&&queryRel(1,0,0,45,-1)");
            p.chat("//replace 45 35:4");
            p.chat("//gmask =queryRel(1,0,0,35,4)&&queryRel(0,0,1,45,-1)&&queryRel(0,0,-1,45,-1)&&queryRel(-1,0,0,45,-1)");
            p.chat("//replace 45 35:4");
            p.chat("//gmask =queryRel(1,0,0,45,-1)&&queryRel(0,0,-1,35,4)&&queryRel(0,0,1,45,-1)&&queryRel(-1,0,0,45,-1)");
            p.chat("//replace 45 35:4");
            p.chat("//gmask =queryRel(1,0,0,45,-1)&&queryRel(0,0,1,35,4)&&queryRel(0,0,-1,45,-1)&&queryRel(-1,0,0,45,-1)");
            p.chat("//replace 45 35:4");
        }


        //base
        int currentheight = 0;

        if(baseHeight > 0)
        for(int i = 0; i < baseHeight; i++) {
            currentheight++;

            p.chat("//gmask =queryRel(0," + (-currentheight) + ",0,45,-1)");
            p.chat("//set 22");
            p.chat("//gmask");
            p.chat("//replace >35:4 35:4");
        }

        //floors
        int heightdifference = 0;
        for(int i = 0; i < floorCount; i++) {
            currentheight++;

            p.chat("//gmask =queryRel(0," + (-currentheight) + ",0,45,-1)");
            p.chat("//set 21");
            p.chat("//gmask");
            p.chat("//replace >35:4 35:4");

            for(int i2 = 0; i2 < windowHeight; i2++) {
                currentheight++;

                p.chat("//gmask =queryRel(0," + (-currentheight) + ",0,45,-1)");
                p.chat("//set 95:0");
                p.chat("//gmask");
                p.chat("//replace >35:4 35:4");
            }

            heightdifference = floorHeight - (windowHeight + 1);

            if(heightdifference > 0)
            for(int i2 = 0; i2 < heightdifference; i2++) {
                currentheight++;

                p.chat("//gmask =queryRel(0," + (-currentheight) + ",0,45,-1)");
                p.chat("//set 21");
                p.chat("//gmask");
                p.chat("//replace >35:4 35:4");
            }
        }
        if(heightdifference == 0){
            currentheight++;
            p.chat("//gmask =queryRel(0," + (-currentheight) + ",0,45,-1)");
            p.chat("//set 21");
            p.chat("//gmask");
            p.chat("//replace >35:4 35:4");
        }


        //windows
        int winsum = windowWidth + windowDistance;
        int wd2 = windowDistance - 1;

        p.chat("//gmask =(abs(z%" + winsum + ")-" + wd2 + ")&&queryRel(1,0,0,0,-1)&&!queryRel(-1,0,0,0,-1)&&!queryRel(-1,0,0,95,-1)");
        p.chat("//replace 95:0 95:7");
        p.chat("//gmask =(abs(z%" + winsum + ")-" + wd2 + ")&&queryRel(-1,0,0,0,-1)&&!queryRel(1,0,0,0,-1)&&!queryRel(1,0,0,95,-1)");
        p.chat("//replace 95:0 95:7");
        p.chat("//gmask =(abs(x%" + winsum + ")-" + wd2 + ")&&queryRel(0,0,1,0,-1)&&!queryRel(0,0,-1,0,-1)&&!queryRel(0,0,-1,95,-1)");
        p.chat("//replace 95:0 95:7");
        p.chat("//gmask =(abs(x%" + winsum + ")-" + wd2 + ")&&queryRel(0,0,-1,0,-1)&&!queryRel(0,0,1,0,-1)&&!queryRel(0,0,1,95,-1)");
        p.chat("//replace 95:0 95:7");
        p.chat("//gmask");
        p.chat("//replace 95:0 21");
        p.chat("//replace 95:7 " + windowColor);


        //roof
        String rm1 = roofColor;
        String rm2 = roofColor;
        String rm3 = roofColor;

        if(roofType == RoofType.SLABS){
            p.chat("//replace >35:4 35:4");
            p.chat("//gmask =queryRel(0,0,0,0,0)&&(queryRel(1,0,0,35,4)||queryRel(1,0,0,35,4)||queryRel(-1,0,0,35,4)||queryRel(0,0,1,35,4)||queryRel(0,0,-1,35,4))");
            p.chat("//replace >21 44");
            p.chat("//gmask =queryRel(0,0,0,0,0)&&(queryRel(1,0,0,44,0)||queryRel(1,0,0,44,0)||queryRel(-1,0,0,44,0)||queryRel(0,0,1,44,0)||queryRel(0,0,-1,44,0))");
            p.chat("//replace >21 44");
            p.chat("//replace >21 44");
            p.chat("//gmask =(queryRel(1,0,0,21,-1)&&queryRel(1,1,0,44,0))||(queryRel(-1,0,0,21,-1)&&queryRel(-1,1,0,44,0))||(queryRel(0,0,1,21,-1)&&queryRel(0,1,1,44,0))||(queryRel(0,0,-1,21,-1)&&queryRel(0,1,-1,44,0))");
            p.chat("//replace 0 44:8");
            p.chat("//gmask <0");
            p.chat("//replace 35:4 43");

            maxRoofHeight = maxRoofHeight - 1;

            if(maxRoofHeight > 0)
            for(int i = 0; i < maxRoofHeight; i++) {
                p.chat("//gmask =!(queryRel(1,-1,0,44,-1)||queryRel(-1,-1,0,44,-1)||queryRel(0,-1,1,44,-1)||queryRel(0,-1,-1,4,-1)||(queryRel(-1,-1,1,44,-1)||queryRel(1,-1,-1,44,-1)||queryRel(1,-1,1,44,-1)||queryRel(-1,-1,-1,4,-1)))");
                p.chat("//replace >43 44");
                p.chat("//gmask =!(queryRel(1,0,0,0,-1)||queryRel(-1,0,0,0,-1)||queryRel(0,0,1,0,-1)||queryRel(0,0,-1,0,-1))");
                p.chat("//replace 44 43");
            }

            // Create the flipped steps
            String[] roofColors = roofColor.split(",");
            String[] roofColors2 = new String[roofColors.length];

            for(int i = 0; i < roofColors.length; i++){
                String[] values = roofColors[i].split(":");
                String material = values[0];
                int data = 0;
                if(values.length > 1)
                    data = Integer.parseInt(values[1]);
                data += 8;

                roofColors2[i] = material + ":" + data;
            }

            rm1 = StringUtils.join(roofColors, ",");
            rm2 = StringUtils.join(roofColors2, ",");
            rm3 = rm1 + "," + rm2;
        }

        if(roofType == RoofType.FLAT){
            p.chat("//gmask 0");
            p.chat("//replace >21 171:7");
            p.chat("//gmask <0");
            p.chat("//replace 35:4 23");
        }

        p.chat("//gmask 0,45,31,37,38,39,40,175");

        for(int i = 0; i < 5; i++)
            p.chat("//replace <22 22");

        p.chat("//gmask");
        p.chat("//replace 21 " + wallColor);
        p.chat("//replace 22 " + baseColor);
        p.chat("//replace 35:4 35:7");


        p.chat("//gmask");



        p.chat("//replace 44:0 " + rm1);
        p.chat("//replace 44:8 " + rm2);
        p.chat("//replace 43:0 " + rm3);
        p.chat("//replace 23 " + rm3);


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
        Bukkit.broadcastMessage(rm3);
    }
}
