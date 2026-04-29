package net.buildtheearth.buildteamtools.modules.generator.components.rail.path;

import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RailPath {

    private final List<Vector> centerPath;

    public RailPath(List<Vector> centerPath) {
        this.centerPath = new ArrayList<>(centerPath);
    }

    public List<Vector> getCenterPath() {
        return Collections.unmodifiableList(centerPath);
    }

    public int size() {
        return centerPath.size();
    }

    public boolean isValid() {
        return centerPath.size() >= 2;
    }
}