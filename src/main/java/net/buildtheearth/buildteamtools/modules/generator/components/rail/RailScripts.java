package net.buildtheearth.buildteamtools.modules.generator.components.rail;

import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.modules.generator.GeneratorModule;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.path.RailPath;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.path.RailPathBuilder;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.placement.RailBlockPlacement;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.placement.RailPlacementBuilder;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.placement.RailWorldEditPlacer;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.selection.RailSelectionPointReader;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.types.DefaultRailType;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.types.RailType;
import net.buildtheearth.buildteamtools.modules.generator.model.GeneratorComponent;
import net.buildtheearth.buildteamtools.modules.generator.model.GeneratorType;
import net.buildtheearth.buildteamtools.modules.generator.model.History;
import net.buildtheearth.buildteamtools.modules.generator.model.Script;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.logging.Level;

public class RailScripts extends Script {

    private static final int MAX_PATH_POINTS = 20_000;
    private static final int MAX_BLOCK_PLACEMENTS = 100_000;

    private final RailPathBuilder pathBuilder = new RailPathBuilder();
    private final RailPlacementBuilder placementBuilder = new RailPlacementBuilder();
    private final RailWorldEditPlacer placer = new RailWorldEditPlacer();

    private final RailType railType = new DefaultRailType();

    public RailScripts(Player player, GeneratorComponent generatorComponent) {
        super(player, generatorComponent);

        Bukkit.getScheduler().runTask(BuildTeamTools.getInstance(), this::readSelectionOnMainThread);
    }

    private void readSelectionOnMainThread() {
        try {
            List<Vector> controlPoints = getControlPoints();

            if (controlPoints.size() < 2) {
                getPlayer().sendMessage("§cRail Generator needs at least two usable points in the selection.");
                return;
            }

            Bukkit.getScheduler().runTaskAsynchronously(
                    BuildTeamTools.getInstance(),
                    () -> buildRailAsync(controlPoints)
            );
        } catch (Exception exception) {
            fail("Rail Generator failed while reading the WorldEdit selection.", exception);
        }
    }

    private void buildRailAsync(List<Vector> controlPoints) {
        try {
            RailPath railPath = pathBuilder.build(controlPoints);

            if (!railPath.isValid()) {
                sendPlayerMessage("§cRail Generator could not derive a valid path from this selection.");
                return;
            }

            if (railPath.size() > MAX_PATH_POINTS) {
                sendPlayerMessage("§cRail Generator selection is too large. Please use a smaller selection.");
                return;
            }

            List<RailBlockPlacement> placements = placementBuilder.buildPlacements(railPath);

            if (placements.isEmpty()) {
                sendPlayerMessage("§cRail Generator did not create any block placements.");
                return;
            }

            if (placements.size() > MAX_BLOCK_PLACEMENTS) {
                sendPlayerMessage("§cRail Generator would place too many blocks. Please use a smaller selection.");
                return;
            }

            Bukkit.getScheduler().runTask(BuildTeamTools.getInstance(), () -> placeRail(placements));
        } catch (Exception exception) {
            fail("Rail Generator failed while generating from the WorldEdit selection.", exception);
        }
    }

    private List<Vector> getControlPoints() {
        RailSelectionPointReader reader = new RailSelectionPointReader(getPlayer(), getRegion());
        return reader.readControlPoints();
    }

    private void placeRail(List<RailBlockPlacement> placements) {
        List<History.BlockChange> changes = placer.placeWithBukkitHistory(this, placements, railType);

        if (changes.isEmpty()) {
            getPlayer().sendMessage("§eRail Generator did not change any blocks. No undo entry was added.");
            return;
        }

        GeneratorModule.getInstance()
                .getPlayerHistory(getPlayer())
                .addHistoryEntry(new History.HistoryEntry(GeneratorType.RAILWAY, this, changes));

        getGeneratorComponent().sendSuccessMessage(getPlayer());
    }

    private void fail(String message, Exception exception) {
        sendPlayerMessage("§c" + message);

        BuildTeamTools.getInstance().getLogger().log(
                Level.SEVERE,
                message,
                exception
        );
    }

    private void sendPlayerMessage(String message) {
        if (Bukkit.isPrimaryThread()) {
            getPlayer().sendMessage(message);
            return;
        }

        Bukkit.getScheduler().runTask(BuildTeamTools.getInstance(), () -> getPlayer().sendMessage(message));
    }
}