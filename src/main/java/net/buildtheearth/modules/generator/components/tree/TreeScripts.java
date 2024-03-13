package net.buildtheearth.modules.generator.components.tree;

import com.cryptomorin.xseries.XMaterial;
import net.buildtheearth.modules.generator.model.Settings;
import net.buildtheearth.utils.Item;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class TreeScripts {

    public static void treescript_v_1_0(Player p, Tree tree) {
        String[] args = new String[4];

        Settings settings = tree.getPlayerSettings().get(p.getUniqueId());
        args[1] = (String) settings.getValues().get(TreeFlag.TYPE);
        args[2] = (String) settings.getValues().get(TreeFlag.HEIGHT);
        args[3] = (String) settings.getValues().get(TreeFlag.WIDTH);


        // In case the player is holding no item, give him a diamond sword
        if(p.getItemOnCursor() == null || p.getInventory().getItemInMainHand().getType() == Material.AIR)
            p.getInventory().setItem(p.getInventory().getHeldItemSlot(), Item.create(XMaterial.DIAMOND_SWORD.parseMaterial()));


        if(args.length == 1) {
            p.chat("//schbr newtrees/*@** -place:bottom -yoff:2");

        }else if(args.length >= 2) {
            String type = args[1];

            if(args.length == 2) {
                if(type.equalsIgnoreCase("any"))
                    p.chat("//schbr newtrees/*@** -place:bottom -yoff:2");
                else
                    p.chat("//schbr newtrees/" + type + "*@** -place:bottom -yoff:2");

            }else if(args.length == 3) {
                String scale = args[2];

                if(type.equalsIgnoreCase("any"))
                    p.chat("//schbr newtrees/*/" + scale + "/*@** -place:bottom -yoff:2");
                else
                    p.chat("//schbr newtrees/"+ type + "/" + scale + "/*@** -place:bottom -yoff:2");

            }else if(args.length == 4) {
                String scale = args[2];
                String option = args[3];

                if(type.equalsIgnoreCase("any")) {
                    if(scale.equalsIgnoreCase("any"))
                        p.chat("//schbr newtrees/*/*/*"+ option +"/*@** -place:bottom -yoff:2");
                    else
                        p.chat("//schbr newtrees/*/" + scale + "/*" + option + "/*@** -place:bottom -yoff:2");
                } else {
                    if(scale.equalsIgnoreCase("any"))
                        p.chat("//schbr newtrees/" + type + "/*/*" + option + "/*@** -place:bottom -yoff:2");
                    else
                        p.chat("//schbr newtrees/" + type + "/" + scale + "/*" + option + "/*@** -place:bottom -yoff:2");
                }
            }
        }
    }
}
