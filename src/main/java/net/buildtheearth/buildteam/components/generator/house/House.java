package net.buildtheearth.buildteam.components.generator.house;

import com.sk89q.worldedit.regions.Region;
import net.buildtheearth.buildteam.BuildTeamTools;
import net.buildtheearth.buildteam.components.generator.Generator;
import net.buildtheearth.buildteam.components.generator.GeneratorModule;
import net.buildtheearth.buildteam.components.generator.GeneratorType;
import net.buildtheearth.utils.Item;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class House extends GeneratorModule {

    public House() {
        super(GeneratorType.HOUSE);
        WIKI_PAGE = "https://github.com/BuildTheEarth/BuildTeamTools/wiki/House-Command";
    }

    @Override
    public void analyzeCommand(Player p, String[] args){

        if(args.length == 2)
            if(args[1].equals("info") || args[1].equals("help") || args[1].equals("?")) {
                sendHelp(p);
                return;
            }


        /** Conversion:
         *
         * Command: /gen house -w 123:12 -r 456:78
         * args: ["-w", "123:12", "-r", "456:78"]
         * HouseSettings:
         *  WALL_COLOR: 123:12
         *  ROOF_TYPE:  456:78
         */

        getPlayerSettings().put(p.getUniqueId(), new HouseSettings(p));


        for(String flag : Generator.convertArgsToFlags(args)){
            String[] flagAndValue = Generator.convertToFlagAndValue(flag, p);
            String flagName = flagAndValue[0];
            String flagValue = flagAndValue[1];

            if(flagName == null)
                continue;

            HouseFlag houseFlag = HouseFlag.byString(flagName);

            if(houseFlag == null)
                continue;
            if(!(getPlayerSettings().get(p.getUniqueId()) instanceof HouseSettings))
                continue;

            HouseSettings settings = (HouseSettings) getPlayerSettings().get(p.getUniqueId());
            settings.setValue(houseFlag, flagValue);
        }

        if(getPlayerSettings().get(p.getUniqueId()).getValues().size() == 0 && args.length > 1){
            sendHelp(p);
            return;
        }

        generate(p);
    }

    @Override
    public boolean checkPlayer(Player p){
        if(!Generator.checkForWorldEditSelection(p))
            return false;

        if(getPlayerSettings().get(p.getUniqueId()).getBlocks() == null)
            getPlayerSettings().get(p.getUniqueId()).setBlocks(Generator.analyzeRegion(p, p.getWorld()));

        Block[][][] blocks = getPlayerSettings().get(p.getUniqueId()).getBlocks();

        if(!Generator.checkForBrickOutline(blocks, p))
            return false;
        if(!Generator.checkForWoolBlock(blocks, p))
            return false;

        return true;
    }

    @Override
    public void generate(Player p){
        if(!checkPlayer(p))
            return;

        Region polyRegion = Generator.getWorldEditSelection(p);

        HouseScripts.buildscript_v_1_2(p, this, polyRegion);

        sendSuccessMessage(p);
    }
}
