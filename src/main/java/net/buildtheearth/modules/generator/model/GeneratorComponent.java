package net.buildtheearth.modules.generator.model;

import lombok.Getter;
import net.buildtheearth.BuildTeamTools;
import net.buildtheearth.modules.ModuleComponent;
import net.buildtheearth.modules.WikiDocumented;
import net.buildtheearth.modules.generator.components.field.FieldSettings;
import net.buildtheearth.modules.generator.components.house.HouseSettings;
import net.buildtheearth.modules.generator.components.rail.RailSettings;
import net.buildtheearth.modules.generator.components.road.RoadSettings;
import net.buildtheearth.modules.generator.components.tree.TreeSettings;
import net.buildtheearth.modules.generator.utils.GeneratorUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public abstract class GeneratorComponent extends ModuleComponent implements WikiDocumented {

    @Getter
    public String wikiPage;

    @Getter
    private final GeneratorType generatorType;

    @Getter
    private final HashMap<UUID, Settings> playerSettings = new HashMap<>();

    public GeneratorComponent(GeneratorType type) {
        super(type.getName());
        generatorType = type;
    }

    public abstract boolean checkForPlayer(Player p);
    public abstract void generate(Player p);



    public void analyzeCommand(Player p, String[] args){
        sendHelp(p, args);
        addPlayerSetting(p);
        convertArgsToSettings(p, args);
        generate(p);
    }

    public void addPlayerSetting(UUID uuid, Settings settings){
        playerSettings.put(uuid, settings);
    }

    public void addPlayerSetting(Player p){
        switch (generatorType){
            case HOUSE:
                addPlayerSetting(p.getUniqueId(), new HouseSettings(p));
                break;
            case ROAD:
                addPlayerSetting(p.getUniqueId(), new RoadSettings(p));
                break;
            case RAILWAY:
                addPlayerSetting(p.getUniqueId(), new RailSettings(p));
                break;
            case TREE:
                addPlayerSetting(p.getUniqueId(), new TreeSettings(p));
                break;
            case FIELD:
                addPlayerSetting(p.getUniqueId(), new FieldSettings(p));
                break;
        }
    }

    public void sendHelp(Player p, String[] args){
        if (args.length == 2)
            if (args[1].equals("info") || args[1].equals("help") || args[1].equals("?"))
                sendHelp(p);
    }

    public void sendHelp(Player p){
        //TODO send houses help
        p.sendMessage("TODO send Houses Help");
    }

    public void sendMoreInfo(Player p) {
        p.sendMessage(" ");
        p.sendMessage("§cFor more information take a look at the wiki:");
        p.sendMessage("§c" + wikiPage);
    }

    public void sendError(Player p) {
        p.sendMessage("§cThere was an error while generating the house. Please contact the admins");
    }

    public String getCommand(Player p) {
        HashMap<Flag, String> flags = getPlayerSettings().get(p.getUniqueId()).getValuesAsString();

        String type = "house";

        switch (generatorType) {
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

        StringBuilder command = new StringBuilder("/gen " + type);
        for(Flag flag : flags.keySet())
            command.append(" -").append(flag.getFlag()).append(" ").append(flags.get(flag));

        return command.toString();
    }

    public void sendSuccessMessage(Player p){
        TextComponent copyCommand = Component.text("[COPY]", NamedTextColor.YELLOW, TextDecoration.BOLD)
                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, getCommand(p)))
                .hoverEvent(HoverEvent.showText(Component.text("Click to copy command", NamedTextColor.GRAY)));

        TextComponent undo = Component.text("[UNDO]", NamedTextColor.RED, TextDecoration.BOLD)
                .clickEvent(ClickEvent.runCommand("/gen undo"))
                .hoverEvent(HoverEvent.showText(Component.text("Click to undo last generation", NamedTextColor.GRAY)));

        TextComponent message = getMessage().append(Component.text(" ")).append(copyCommand).append(Component.text(" ")).append(undo);

        p.sendMessage(message);
        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
    }

    private TextComponent getMessage() {
        String type = "Building";

        switch (generatorType){
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

        return LegacyComponentSerializer.legacyAmpersand().deserialize(BuildTeamTools.PREFIX + type + "§a successfully §7generated.");
    }

    /** Conversion:
     * Command: /gen house -w 123:12 -r 456:78
     * args: ["-w", "123:12", "-r", "456:78"]
     * HouseSettings:
     * WALL_COLOR: 123:12
     * ROOF_TYPE:  456:78
     */
    protected void convertArgsToSettings(Player p, String[] args){
        for(String flag : GeneratorUtils.convertArgsToFlags(args)){
            String[] flagAndValue = GeneratorUtils.convertToFlagAndValue(flag, p);

            if(flagAndValue == null) continue;

            String flagName = flagAndValue[0];

            if(flagName == null) continue;

            Flag finalFlag = Flag.byString(generatorType, flagName);

            if(finalFlag == null) continue;

            Object flagValue = FlagType.convertToFlagType(finalFlag, flagAndValue[1]);

            String errorMessage = FlagType.validateFlagType(finalFlag, flagValue);

            if(errorMessage != null){
                p.sendMessage(errorMessage);
                continue;
            }

            getPlayerSettings().get(p.getUniqueId()).setValue(finalFlag, flagValue);
        }

        if(getPlayerSettings().get(p.getUniqueId()).getValues().isEmpty() && args.length > 1)
            sendHelp(p);
    }
}
