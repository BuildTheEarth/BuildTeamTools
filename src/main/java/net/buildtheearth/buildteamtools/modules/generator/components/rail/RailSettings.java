package net.buildtheearth.buildteamtools.modules.generator.components.rail;

import net.buildtheearth.buildteamtools.modules.generator.model.Settings;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RailSettings extends Settings {

    private List<Vector> customControlPoints;

    public RailSettings(Player player) {
        super(player);
    }

    @Override
    public void setDefaultValues() {
        if (customControlPoints == null) {
            customControlPoints = new ArrayList<>();
            return;
        }

        customControlPoints.clear();
    }

    public void addCustomControlPoint(Vector point) {
        ensureCustomControlPoints();

        customControlPoints.add(new Vector(
                point.getBlockX(),
                point.getBlockY(),
                point.getBlockZ()
        ));
    }

    public void clearCustomControlPoints() {
        ensureCustomControlPoints();
        customControlPoints.clear();
    }

    public List<Vector> getCustomControlPoints() {
        ensureCustomControlPoints();
        return Collections.unmodifiableList(customControlPoints);
    }

    public boolean hasEnoughCustomControlPoints() {
        ensureCustomControlPoints();
        return customControlPoints.size() >= 2;
    }

    private void ensureCustomControlPoints() {
        if (customControlPoints == null)
            customControlPoints = new ArrayList<>();
    }
}