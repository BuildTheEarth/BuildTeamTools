package net.buildtheearth.buildteamtools.modules.generator.model;

import com.alpsbte.alpslib.utils.GeneratorUtils;
import com.alpsbte.alpslib.utils.ChatHelper;
import com.alpsbte.alpslib.utils.WikiDocumented;
import lombok.Getter;
import net.buildtheearth.buildteamtools.modules.ModuleComponent;
import net.buildtheearth.buildteamtools.modules.generator.components.field.FieldSettings;
import net.buildtheearth.buildteamtools.modules.generator.components.house.HouseSettings;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.RailSettings;
import net.buildtheearth.buildteamtools.modules.generator.components.road.RoadSettings;
import net.buildtheearth.buildteamtools.modules.generator.components.tree.TreeSettings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.UUID;

public abstract class GeneratorComponent extends ModuleComponent implements WikiDocumented {
    @Getter
    private final GeneratorType generatorType;

    @Getter
    private final HashMap<UUID, Settings> playerSettings = new HashMap<>();

    protected GeneratorComponent(@NonNull GeneratorType type) {
        super(type.getName());
        generatorType = type;
    }

    public abstract boolean checkForPlayer(Player p);

    public abstract void generate(Player p);

    public void analyzeCommand(Player p, String[] args) {
        if (isHelpCommand(args)) {
            sendHelp(p);
            return;
        }

        addPlayerSetting(p);
        if (!convertArgsToSettings(p, args))
            return;

        generate(p);
    }

    private boolean isHelpCommand(String[] args) {
        return args.length == 2
                && (args[1].equalsIgnoreCase("info")
                || args[1].equalsIgnoreCase("help")
                || args[1].equalsIgnoreCase("?"));
    }

    public void addPlayerSetting(UUID uuid, Settings settings) {
        playerSettings.put(uuid, settings);
    }

    public void addPlayerSetting(Player p) {
        switch (generatorType) {
            case HOUSE:
                addPlayerSetting(p.getUniqueId(), new HouseSettings(p));
                break;
            case ROAD:
                addPlayerSetting(p.getUniqueId(), new RoadSettings(p));
                break;
            case RAIL:
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

    public void sendHelp(@NonNull Player p) {
        String wikiPage = getWikiPage();
        Component wikiLink = Component.text(wikiPage, NamedTextColor.YELLOW)
                .clickEvent(ClickEvent.openUrl(wikiPage))
                .hoverEvent(HoverEvent.showText(Component.text("Click to open this page", NamedTextColor.GRAY)));

        p.sendMessage(ChatHelper.PREFIX_COMPONENT.append(wikiLink));
    }

    public void sendError(Player p) {
        p.sendMessage(ChatHelper.PREFIX_COMPONENT.append(ChatHelper.getErrorComponent(
                "There was an error while generating the %s. Please contact the admins.",
                generatorType.getName().toLowerCase()
        )));
    }

    public String getCommand(@NonNull Player p) {
        HashMap<Flag, String> flags = getPlayerSettings().get(p.getUniqueId()).getValuesAsString();

        String type = switch (generatorType) {
            case HOUSE -> "house";
            case ROAD -> "road";
            case RAIL -> "rail";
            case TREE -> "tree";
            case FIELD -> "field";
        };

        StringBuilder command = new StringBuilder("/gen " + type);

        for (var flag : flags.entrySet())
            command.append(" -").append(flag.getKey().getFlag()).append(" ").append(flag.getValue());

        return command.toString();
    }

    public void sendSuccessMessage(Player p) {
        TextComponent copyCommand = Component.text("[COPY]", NamedTextColor.YELLOW, TextDecoration.BOLD)
                .clickEvent(ClickEvent.suggestCommand(getCommand(p)))
                .hoverEvent(HoverEvent.showText(Component.text("Click to copy command", NamedTextColor.GRAY)));

        TextComponent undo = Component.text("[UNDO]", NamedTextColor.RED, TextDecoration.BOLD)
                .clickEvent(ClickEvent.runCommand("/gen undo"))
                .hoverEvent(HoverEvent.showText(Component.text("Click to undo last generation", NamedTextColor.GRAY)));

        TextComponent message = getMessage()
                .append(Component.text(" "))
                .append(copyCommand)
                .append(Component.text(" "))
                .append(undo);

        p.sendMessage(message);
        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
    }

    private @NonNull TextComponent getMessage() {
        String type = switch (generatorType) {
            case HOUSE -> "House";
            case ROAD -> "Road";
            case RAIL -> "Rail";
            case TREE -> "Tree";
            case FIELD -> "Field";
        };

        return ChatHelper.PREFIX_COMPONENT
                .append(Component.text(type, NamedTextColor.GRAY))
                .append(Component.text(" successfully ", NamedTextColor.GREEN))
                .append(Component.text("generated.", NamedTextColor.GRAY));
    }

    /**
     * Conversion:
     * Command: /gen house -w 123:12 -r 456:78
     * args: ["-w", "123:12", "-r", "456:78"]
     * HouseSettings:
     * WALL_COLOR: 123:12
     * ROOF_TYPE: 456:78
     */
    protected boolean convertArgsToSettings(Player p, String[] args) {
        boolean convertedFlag = false;

        for (String flag : GeneratorUtils.convertArgsToFlags(args)) {
            String[] flagAndValue = GeneratorUtils.convertToFlagAndValue(flag, p);

            if (flagAndValue == null)
                continue;

            String flagName = flagAndValue[0];

            if (flagName == null)
                continue;

            Flag finalFlag = Flag.byString(generatorType, flagName);

            if (finalFlag == null)
                continue;

            convertedFlag = true;
            Object flagValue = FlagType.convertToFlagType(finalFlag, flagAndValue[1]);

            String errorMessage = FlagType.validateFlagType(finalFlag, flagValue);

            if (errorMessage != null) {
                p.sendMessage(errorMessage);
                continue;
            }

            getPlayerSettings().get(p.getUniqueId()).setValue(finalFlag, flagValue);
        }

        if (!convertedFlag && args.length > 1) {
            sendHelp(p);
            return false;
        }

        return true;
    }

    @Override
    public String getWikiPage() {
        return generatorType.getWikiPage();
    }
}
