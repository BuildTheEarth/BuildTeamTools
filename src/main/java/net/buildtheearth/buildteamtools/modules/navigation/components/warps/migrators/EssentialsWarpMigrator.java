package net.buildtheearth.buildteamtools.modules.navigation.components.warps.migrators;

import com.alpsbte.alpslib.utils.ChatHelper;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.Warps;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.WarpsComponent;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.model.WarpGroup;
import net.buildtheearth.buildteamtools.modules.network.NetworkModule;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class  EssentialsWarpMigrator implements IWarpMigrator {
    @Override
    public CompletableFuture<Void> migrate() {
        return CompletableFuture.runAsync(() -> {
            try {
                Essentials essentials = (Essentials) Essentials.getProvidingPlugin(Essentials.class);
                Warps warps = essentials.getWarps();
                for (String warp : warps.getList()) {
                    WarpGroup group = WarpsComponent.getOtherWarpGroup(Objects.requireNonNull(NetworkModule.getInstance().getBuildTeam()).getWarpGroups());
                    WarpsComponent.createWarp(warps.getWarp(warp), warp, group);
                }
            } catch (Exception e) {
                ChatHelper.logError("An error occurred while migrating the essentials warps!\n(this probably means essentials isn't installed)\n %s", e);
            }

        });
    }
}
