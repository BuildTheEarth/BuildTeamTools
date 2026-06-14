package net.buildtheearth.buildteamtools.modules.network.model;

import com.alpsbte.alpslib.utils.ChatHelper;
import lombok.Getter;
import lombok.Setter;
import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.modules.navigation.NavigationModule;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.model.Warp;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.model.WarpGroup;
import net.buildtheearth.buildteamtools.modules.network.NetworkModule;
import net.buildtheearth.buildteamtools.modules.network.api.API;
import net.buildtheearth.buildteamtools.modules.network.api.NetworkAPI;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BuildTeam {

    @Getter
    private final String ID;
    @Getter
    private final String IP;
    @Getter
    private final String name;
    @Getter
    private final String blankName;
    @Getter
    private final String serverName;
    @Getter
    @Setter
    private boolean isConnected;
    @Getter
    private final boolean hasBTToolsInstalled;
    @Getter
    private final List<Region> regions;
    @Getter
    private final List<WarpGroup> warpGroups;
    @Getter
    private final boolean allowsTransfers;
    @Getter
    private final String tag;


    public BuildTeam(String ID, String serverIP, String name, String blankName, String serverName,
                     boolean isConnected, boolean hasBTToolsInstalled, boolean allowsTransfers, String tag) {
        this.ID = ID;
        this.name = name;
        this.blankName = blankName;
        this.serverName = serverName;
        // We need to verify the Data manually, because NwApi is quite scuffed
        this.isConnected = isConnected && (serverName != null && !serverName.isEmpty());
        this.hasBTToolsInstalled = hasBTToolsInstalled;

        this.regions = new ArrayList<>();
        this.warpGroups = new ArrayList<>();
        this.allowsTransfers = allowsTransfers;
        this.tag = tag;

        if (isConnected && NetworkModule.getInstance().getBuildTeam() != null && NetworkModule.getInstance().getBuildTeam().isConnected())
            this.IP = null;
        else
            this.IP = (!isConnected) ? serverIP : tag.toLowerCase() + ".buildtheearth.net";
    }

    public void createWarp(Player creator, Warp warp) {
        // Check if the team owns that warp
        if (!warp.getWarpGroup().getBuildTeam().getID().equals(this.getID())) {
            if (creator != null) {
            creator.sendMessage(ChatHelper.getErrorString("You can only create warps for your own team!"));
            }
            return;
        }

        warp.getWarpGroup().getWarps().add(warp);
        ChatHelper.sendSuccessfulMessage(creator, "Successfully created the warp %s!", warp.getName());

        NetworkAPI.createWarp(warp, new API.ApiResponseCallback() {
            @Override
            public void onResponse(String response) {
                NetworkModule.getInstance().updateCache().thenRun(() -> refreshBluemapMarkers()).exceptionally(e -> {
                    BuildTeamTools.getInstance().getComponentLogger().warn("Failed to update cache after warp creation.", e);
                    return null;
                });
            }

            @Override
            public void onFailure(IOException e) {
                warp.getWarpGroup().getWarps().remove(warp);
                if (creator != null) {
                    creator.sendMessage(ChatHelper.getErrorString("Failed to sync warp %s to the network! It has been removed " +
                        "locally. Please try again.", warp.getName()));
                }
                BuildTeamTools.getInstance().getComponentLogger().error("Failed to create warp via API", e);
            }
        });
    }

    public void createWarpGroup(Player creator, WarpGroup warpGroup) {
        // Check if the team owns that warp group
        if (!warpGroup.getBuildTeam().getID().equals(this.getID())) {
            creator.sendMessage(ChatHelper.getErrorString("You can only create warp groups for your own team!"));
            return;
        }

        this.warpGroups.add(warpGroup);
        ChatHelper.sendSuccessfulMessage(creator, "Successfully created the warp group %s!", warpGroup.getName());

        NetworkAPI.createWarpGroup(warpGroup, new API.ApiResponseCallback() {
            @Override
            public void onResponse(String response) {
                NetworkModule.getInstance().updateCache()
                        .thenRun(() -> refreshBluemapMarkers())
                        .exceptionally(e -> {
                            BuildTeamTools.getInstance().getComponentLogger()
                                    .warn("Failed to update cache after warp group creation.", e);
                    return null;
                });
            }

            @Override
            public void onFailure(IOException e) {
                warpGroups.remove(warpGroup);
                creator.sendMessage(ChatHelper.getErrorString("Failed to sync warp group %s to the network! It has been removed" +
                        " locally. Please try again.", warpGroup.getName()));
                BuildTeamTools.getInstance().getComponentLogger().error("Failed to create warp group via API", e);
            }
        });
    }


    public void updateWarp(Player updater, Warp warp) {
        // Check if the team owns that warp
        if (!warp.getWarpGroup().getBuildTeam().getID().equals(this.getID())) {
            updater.sendMessage(ChatHelper.getErrorString("You can only update warps for your own team!"));
            return;
        }

        // Local state is already updated through direct object modification
        // Send success message immediately
        ChatHelper.sendSuccessfulMessage(updater, "Successfully updated the warp %s!", warp.getName());

        NetworkAPI.updateWarp(warp, new API.ApiResponseCallback() {
            @Override
            public void onResponse(String response) {
                NetworkModule.getInstance().updateCache().thenRun(() -> refreshBluemapMarkers()).exceptionally(e -> {
                    BuildTeamTools.getInstance().getComponentLogger().warn("Failed to update cache after warp update.", e);
                    return null;
                });
            }

            @Override
            public void onFailure(IOException e) {
                updater.sendMessage(ChatHelper.getErrorString("Failed to sync warp %s changes to the network! Your changes may " +
                        "be lost on reload. Please try editing again.", warp.getName()));
                BuildTeamTools.getInstance().getComponentLogger().error("Failed to update warp via API", e);
            }
        });
    }

    public void updateWarpGroup(Player updater, WarpGroup warpGroup) {
        // Check if the team owns that warp group
        if (!warpGroup.getBuildTeam().getID().equals(this.getID())) {
            updater.sendMessage(ChatHelper.getErrorString("You can only update warp groups for your own team!"));
            return;
        }

        // Local state is already updated through direct object modification
        // Send success message immediately
        ChatHelper.sendSuccessfulMessage(updater, "Successfully updated the warp group %s!", warpGroup.getName());

        NetworkAPI.updateWarpGroup(warpGroup, new API.ApiResponseCallback() {
            @Override
            public void onResponse(String response) {
                NetworkModule.getInstance().updateCache().thenRun(() -> refreshBluemapMarkers()).exceptionally(e -> {
                    BuildTeamTools.getInstance().getComponentLogger().warn("Failed to update cache after warp group update.", e);
                    return null;
                });
            }

            @Override
            public void onFailure(IOException e) {
                updater.sendMessage(ChatHelper.getErrorString("Failed to sync warp group %s changes to the network! Your " +
                        "changes may be lost on reload. Please try editing again.", warpGroup.getName()));
                BuildTeamTools.getInstance().getComponentLogger().error("Failed to update warp group via API", e);
            }
        });
    }

    public void deleteWarp(Player deleter, Warp warp) {
        // Check if the team owns that warp
        if (!warp.getWarpGroup().getBuildTeam().getID().equals(this.getID())) {
            deleter.sendMessage(ChatHelper.getErrorString("You can only delete warps for your own team!"));
            return;
        }

        warp.getWarpGroup().getWarps().remove(warp);
        ChatHelper.sendSuccessfulMessage(deleter, "Successfully deleted the warp %s!", warp.getName());

        NetworkAPI.deleteWarp(warp, new API.ApiResponseCallback() {
            @Override
            public void onResponse(String response) {
                NetworkModule.getInstance().updateCache().thenRun(() -> refreshBluemapMarkers()).exceptionally(e -> {
                    BuildTeamTools.getInstance().getComponentLogger().warn("Failed to update cache after warp deletion.", e);
                    return null;
                });
            }

            @Override
            public void onFailure(IOException e) {
                warp.getWarpGroup().getWarps().add(warp);
                deleter.sendMessage(ChatHelper.getErrorString("Failed to delete warp %s from the network! It has been restored " +
                        "locally. Please try again.", warp.getName()));
                BuildTeamTools.getInstance().getComponentLogger().error("Failed to delete warp via API", e);
            }
        });
    }

    public void deleteWarpGroup(Player deleter, WarpGroup warpGroup) {
        // Check if the team owns that warp group
        if (!warpGroup.getBuildTeam().getID().equals(this.getID())) {
            deleter.sendMessage(ChatHelper.getErrorString("You can only delete warp groups for your own team!"));
            return;
        }

        this.warpGroups.remove(warpGroup);
        ChatHelper.sendSuccessfulMessage(deleter, "Successfully deleted the warp group %s!", warpGroup.getName());

        NetworkAPI.deleteWarpGroup(warpGroup, new API.ApiResponseCallback() {
            @Override
            public void onResponse(String response) {
                NetworkModule.getInstance().updateCache().thenRun(() -> refreshBluemapMarkers()).exceptionally(e -> {
                    BuildTeamTools.getInstance().getComponentLogger().warn("Failed to update cache after warp group deletion.",
                            e);
                    return null;
                });
            }

            @Override
            public void onFailure(IOException e) {
                warpGroups.add(warpGroup);
                deleter.sendMessage(ChatHelper.getErrorString("Failed to delete warp group %s from the network! It has been " +
                        "restored locally. Please try again.", warpGroup.getName()));
                BuildTeamTools.getInstance().getComponentLogger().error("Failed to delete warp group via API", e);
            }
        });
    }

    /**
     * Refreshes BlueMap markers for all warp groups.
     * <p>
     * This method is called after any warp or warp group is created, updated, or deleted
     * to ensure the BlueMap display stays in sync with the current state.
     * </p>
     */
    private void refreshBluemapMarkers() {
        if (NavigationModule.getInstance().getBluemapComponent() != null
                && NavigationModule.getInstance().getBluemapComponent().isEnabled()) {
            NavigationModule.getInstance().getBluemapComponent().refreshAllMarkers();
        }
    }

}
