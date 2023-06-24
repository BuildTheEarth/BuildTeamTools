package net.buildtheearth.buildteam.components.generator;

import lombok.Getter;
import net.buildtheearth.buildteam.BuildTeamTools;
import net.buildtheearth.buildteam.components.generator.house.HouseSettings;
import net.buildtheearth.buildteam.components.generator.road.RoadFlag;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public abstract class GeneratorModule {


    public String WIKI_PAGE;
    GeneratorType GENERATOR_TYPE;

    @Getter
    private HashMap<UUID, Settings> playerSettings = new HashMap<>();

    public GeneratorModule(GeneratorType type) {
        GENERATOR_TYPE = type;
    }

    public abstract void analyzeCommand(Player p, String[] args);
    public abstract boolean checkPlayer(Player p);
    public abstract void generate(Player p);

    public void sendHelp(Player p){
        //TODO send houses help
        p.sendMessage("TODO send Houses Help");
    }

    public void sendMoreInfo(Player p){
        p.sendMessage(" ");
        p.sendMessage("§cFor more information take a look at the wiki:");
        p.sendMessage("§c" + WIKI_PAGE);
    }

    public void sendError(Player p){
        p.sendMessage("§cThere was an error while generating the house. Please contact the admins");
    }

    public String getCommand(Player p){
        HashMap<Object, String> flags = getPlayerSettings().get(p.getUniqueId()).getValues();

        String type = "house";

        switch (GENERATOR_TYPE){
            case HOUSE:
                type = "house";
                break;
            case ROAD:
                type = "road";
                break;
            case RAILWAY:
                type = "railway";
                break;
            case TREE:
                type = "tree";
                break;
        }

        String command = "/gen " + type;
        for(Object object : flags.keySet()) {
            if (!(object instanceof RoadFlag))
                continue;

            RoadFlag roadFlag = (RoadFlag) object;
            command += " -" + roadFlag.getFlag() + " " + flags.get(roadFlag);
        }

        return command;
    }

    public void sendSuccessMessage(Player p){
        p.sendMessage(" ");
        p.sendMessage(" ");
        p.sendMessage(" ");

        String type = "Building";

        switch (GENERATOR_TYPE){
            case HOUSE:
                type = "House";
                break;
            case ROAD:
                type = "Road";
                break;
            case RAILWAY:
                type = "Railway";
                break;
            case TREE:
                type = "Tree";
                break;
        }

        TextComponent tc = new TextComponent(BuildTeamTools.PREFIX + type + " §asuccessfully §7generated. §e[Copy Command]");
        tc.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, getCommand(p)));
        tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click to copy command").create()));

        p.spigot().sendMessage(tc);

        p.sendMessage(" ");
        p.sendMessage("§cNote: You can undo the edit with /gen undo.");
    }
}
