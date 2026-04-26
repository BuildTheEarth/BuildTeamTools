package net.buildtheearth.buildteamtools.modules.navigation.components.warps.migrators;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.Warps;
import com.earth2me.essentials.commands.WarpNotFoundException;
import net.buildtheearth.buildteamtools.modules.navigation.NavUtils;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.WarpsComponent;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.model.WarpGroup;
import net.buildtheearth.buildteamtools.modules.network.NetworkModule;
import net.ess3.api.IWarps;
import net.ess3.api.InvalidWorldException;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class  EssentialsWarpMigrator implements IWarpMigrator {
    @Override
    public CompletableFuture<Void> migrate() {
        return CompletableFuture.runAsync(() -> {
            Essentials essentials = new Essentials();
            Warps warps = essentials.getWarps();
            for (String warp : warps.getList()) {
                try {
                    WarpGroup group = WarpsComponent.getOtherWarpGroup(Objects.requireNonNull(NetworkModule.getInstance().getBuildTeam()).getWarpGroups());
                    WarpsComponent.createWarp(warps.getWarp(warp), warp, group);
                } catch (Exception ignored) {
                }
            }
        });
    }
}
