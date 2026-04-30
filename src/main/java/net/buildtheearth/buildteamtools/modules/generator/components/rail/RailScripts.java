package net.buildtheearth.buildteamtools.modules.generator.components.rail;

import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.modules.generator.GeneratorModule;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.path.RailPath;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.path.RailPathBuilder;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.placement.RailBlockPlacement;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.placement.RailPlacementBuilder;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.placement.RailWorldEditPlacer;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.selection.RailSelectionPointReader;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.types.RailType;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.types.SampleRailType;
import net.buildtheearth.buildteamtools.modules.generator.model.GeneratorComponent;
import net.buildtheearth.buildteamtools.modules.generator.model.GeneratorType;
import net.buildtheearth.buildteamtools.modules.generator.model.History;
import net.buildtheearth.buildteamtools.modules.generator.model.Script;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

public class RailScripts extends Script {

    private final RailPathBuilder pathBuilder = new RailPathBuilder();
    private final RailPlacementBuilder placementBuilder = new RailPlacementBuilder();
    private final RailWorldEditPlacer placer = new RailWorldEditPlacer();

    private final RailType railType = new SampleRailType();

    public RailScripts(Player player, GeneratorComponent generatorComponent) {
        super(player, generatorComponent);

        Thread thread = new Thread(this::generateRail);
        thread.start();
    }

    private void generateRail() {
        try {
            List<Vector> controlPoints = getControlPoints();

            if (controlPoints.size() < 2) {
                getPlayer().sendMessage("§cRail Generator needs at least two usable points in the selection.");
                return;
            }

            RailPath railPath = pathBuilder.build(controlPoints);

            if (!railPath.isValid()) {
                getPlayer().sendMessage("§cRail Generator could not derive a valid path from this selection.");
                return;
            }

            List<RailBlockPlacement> placements = placementBuilder.buildPlacements(railPath);

            if (placements.isEmpty()) {
                getPlayer().sendMessage("§cRail Generator did not create any block placements.");
                return;
            }

            Bukkit.getScheduler().runTask(BuildTeamTools.getInstance(), () -> placeRail(placements));
        } catch (Exception exception) {
            getPlayer().sendMessage("§cRail Generator failed while generating from the WorldEdit selection.");
            exception.printStackTrace();
        }
    }

    private List<Vector> getControlPoints() {
        RailSelectionPointReader reader = new RailSelectionPointReader(getPlayer(), getRegion());
        return reader.readControlPoints();
    }

    private void placeRail(List<RailBlockPlacement> placements) {
        boolean placedWithWorldEdit = placer.placeWithWorldEdit(this, placements, railType);

        if (!placedWithWorldEdit) {
            getPlayer().sendMessage("§eWorldEdit history is unavailable. Falling back to Bukkit placement.");
            getPlayer().sendMessage("§eUse §6/gen undo§e instead of §6//undo§e for this generation.");

            List<History.BlockChange> changes = placer.placeWithBukkitFallback(this, placements, railType);

            GeneratorModule.getInstance()
                    .getPlayerHistory(getPlayer())
                    .addHistoryEntry(new History.HistoryEntry(GeneratorType.RAILWAY, this, changes));

            getGeneratorComponent().sendSuccessMessage(getPlayer());
            return;
        }

        GeneratorModule.getInstance()
                .getPlayerHistory(getPlayer())
                .addHistoryEntry(new History.HistoryEntry(GeneratorType.RAILWAY, this, 1));

        getGeneratorComponent().sendSuccessMessage(getPlayer());
    }
}