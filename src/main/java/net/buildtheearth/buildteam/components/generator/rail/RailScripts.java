package net.buildtheearth.buildteam.components.generator.rail;

import org.bukkit.entity.Player;

public class RailScripts {

    public static void railscript_1_2_beta(Player p) {

        int xPos = p.getLocation().getBlockX();
        int zPos = p.getLocation().getBlockZ();

        p.chat("//expand 10 u");
        p.chat("//expand 5 d");

        p.chat("//gmask");
        p.chat("//replace \"0 !>42 =queryRel(0,-1,-1,42,-1)||queryRel(0,-1,1,42,-1)\" 145:1");
        p.chat("//replace \"0 !>42 =queryRel(-1,-1,0,42,-1)||queryRel(1,-1,0,42,-1)\" 145:0");

        p.chat("//gmask =(sqrt((x-(" + xPos + "))^2+(z-(" + zPos + "))^2)%3)-2");
        p.chat("//replace \"0 =queryRel(0,0,1,145,-1)||queryRel(0,0,-1,145,-1)||queryRel(1,0,0,145,-1)||queryRel(-1,0,0,145,-1)\" 44:0");
        p.chat("//replace \"!145 !0 <145\" 43:0");
        p.chat("//gmask");
        p.chat("//replace 42 2");
    }
}
