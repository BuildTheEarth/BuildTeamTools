package net.buildtheearth.buildteam.components.generator.road;

import net.buildtheearth.buildteam.components.generator.house.RoofType;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class RoadScripts {

    public static void roadscript_v_1_3(Player p,
                                         String roadMaterial,
                                         String markingsMaterial,
                                         String sidewalkMaterial,
                                         int laneCount,
                                         int laneWidth,
                                         int separationRadius,
                                         int markingsLength,
                                         int markingsDistance,
                                         int sidewalkWidth) {

        // Is there a sidewalk?
        boolean isSidewalk = false;
        if(sidewalkWidth>0)
            isSidewalk = true;

        // Calculate current width from centre of road
        int current_width = ((laneWidth + 1)*laneCount) + sidewalkWidth + (isSidewalk ? 1 : 0) + separationRadius;

        // Clear the surrounding area
        p.chat("//gmask 31,32,37,38,39,40,83,86,175");
        p.chat("//curve 0 " + current_width);


        // Draw sidewalk
        p.chat("//gmask <0");
        p.chat("//curve 43:0 " + current_width);

        //TODO: fix excessively wide sidewalk bug
        current_width -=(isSidewalk ? 1 : 0);
        p.chat("//curve 35:1 " + current_width);
        current_width -= sidewalkWidth;
        p.chat("//curve 43:0 " + current_width);


        // Draw road
        for(int i=0;i<laneCount;i++) {
            current_width--;
            p.chat("//curve 35:2 " + current_width);
            current_width -= laneWidth;
            p.chat("//curve 35:3 " + current_width);
        }

        if(laneCount>1)
            p.chat("//curve " + markingsMaterial);

        // Separation
        if(separationRadius > 0) {
            p.chat("//curve 43:8 " + current_width);
            current_width--;
            p.chat("//curve 2 " + current_width);
        }

        // Markings
        int xPos = p.getLocation().getBlockX();
        int zPos = p.getLocation().getBlockZ();
        int markingSum = markingsLength + markingsDistance;
        p.chat("//gmask \"=(sqrt((x-(" + xPos + "))^2+(z-(" + zPos + "))^2)%" + markingSum + ")-" + markingsDistance + " 35:3\"");
        int roadWidth = ((laneWidth+1)*laneCount)+sidewalkWidth+1+separationRadius;


        p.chat("//curve " + markingsMaterial + " " + roadWidth);
        p.chat("//gmask 35:2,35:3");
        p.chat("//curve " + roadMaterial + " " + roadWidth);
        p.chat("//gmask 35:1");
        p.chat("//curve " + sidewalkMaterial + " " + roadWidth);


    }
}
