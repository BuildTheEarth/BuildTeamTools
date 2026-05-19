package net.buildtheearth.buildteamtools.modules.generator.components.rail;

import com.alpsbte.alpslib.utils.GeneratorUtils;
import net.buildtheearth.buildteamtools.modules.generator.GeneratorModule;
import net.buildtheearth.buildteamtools.modules.generator.model.GeneratorComponent;
import net.buildtheearth.buildteamtools.modules.generator.model.GeneratorType;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class Rail extends GeneratorComponent {

    private static final int TARGET_BLOCK_RANGE = 200;

    public Rail() {
        super(GeneratorType.RAILWAY);
    }

    @Override
    public void analyzeCommand(Player player, String[] args) {
        addPlayerSetting(player);

        if (args.length >= 2) {
            String subCommand = args[1].toLowerCase();

            switch (subCommand) {
                case "help", "info", "?" -> {
                    sendHelp(player);
                    return;
                }

                case "add", "point" -> {
                    addPoint(player);
                    return;
                }

                case "clear", "reset" -> {
                    clearPoints(player);
                    return;
                }

                case "points", "list" -> {
                    listPoints(player);
                    return;
                }

                default -> {
                    player.sendMessage("§cUnknown rail command: §7" + args[1]);
                    sendHelp(player);
                    return;
                }
            }
        }

        generate(player);
    }

    @Override
    public boolean checkForPlayer(Player player) {
        RailSettings settings = getRailSettings(player);

        if (settings != null && settings.hasEnoughCustomControlPoints())
            return true;

        return !GeneratorUtils.checkForNoWorldEditSelection(player);
    }

    @Override
    public void generate(Player player) {
        if (!GeneratorModule.getInstance().getRail().checkForPlayer(player))
            return;

        RailSettings settings = getRailSettings(player);

        if (settings != null && settings.hasEnoughCustomControlPoints()) {
            new RailScripts(player, this, new ArrayList<>(settings.getCustomControlPoints()));
            return;
        }

        new RailScripts(player, this);
    }

    private void addPoint(Player player) {
        RailSettings settings = getRailSettings(player);

        if (settings == null) {
            player.sendMessage("§cRail settings could not be loaded.");
            return;
        }

        Block targetBlock = player.getTargetBlockExact(TARGET_BLOCK_RANGE);

        if (targetBlock == null) {
            player.sendMessage("§cLook at a block first to add a rail point.");
            return;
        }

        Vector point = new Vector(
                targetBlock.getX(),
                targetBlock.getY() + 1,
                targetBlock.getZ()
        );

        settings.addCustomControlPoint(point);

        player.sendMessage("§aAdded rail point §7#" + settings.getCustomControlPoints().size()
                + " §8(" + point.getBlockX() + ", " + point.getBlockY() + ", " + point.getBlockZ() + ")");
    }

    private void clearPoints(Player player) {
        RailSettings settings = getRailSettings(player);

        if (settings == null) {
            player.sendMessage("§cRail settings could not be loaded.");
            return;
        }

        settings.clearCustomControlPoints();
        player.sendMessage("§aCleared all custom rail points.");
    }

    private void listPoints(Player player) {
        RailSettings settings = getRailSettings(player);

        if (settings == null) {
            player.sendMessage("§cRail settings could not be loaded.");
            return;
        }

        List<Vector> points = settings.getCustomControlPoints();

        if (points.isEmpty()) {
            player.sendMessage("§eNo custom rail points saved.");
            return;
        }

        player.sendMessage("§aSaved rail points:");

        for (int index = 0; index < points.size(); index++) {
            Vector point = points.get(index);

            player.sendMessage("§7#" + (index + 1)
                    + " §8(" + point.getBlockX() + ", " + point.getBlockY() + ", " + point.getBlockZ() + ")");
        }
    }

    private RailSettings getRailSettings(Player player) {
        return (RailSettings) getPlayerSettings().get(player.getUniqueId());
    }
}