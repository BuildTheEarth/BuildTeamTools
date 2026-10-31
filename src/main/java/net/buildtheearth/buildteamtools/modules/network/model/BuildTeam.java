package net.buildtheearth.buildteamtools.modules.network.model;

import lombok.Getter;
import lombok.Setter;
import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.i18n.MessageKeys;
import net.buildtheearth.buildteamtools.i18n.Messages;
import net.buildtheearth.buildteamtools.modules.navigation.NavigationModule;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.model.Warp;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.model.WarpGroup;
import net.buildtheearth.buildteamtools.modules.network.NetworkModule;
import net.buildtheearth.buildteamtools.modules.network.api.API;
import net.buildtheearth.buildteamtools.modules.network.api.NetworkAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BuildTeam {

    @Getter
    private final String ID;
    @Getter
    private final String IP;
    @Getter
    private final String configuredMainServerIP;
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
    @Getter
    private final boolean networkMappingMismatch;


    public BuildTeam(String ID, String serverIP, String name, String blankName, String serverName,
                     boolean isConnected, boolean hasBTToolsInstalled, boolean allowsTransfers, String tag) {
        this.ID = ID;
        this.name = name;
        this.blankName = blankName;
        this.serverName = serverName;
        this.configuredMainServerIP = serverIP;
        this.networkMappingMismatch = isConnected && serverIP != null && serverName == null;
        // We need to verify the Data manually, because NwApi is quite scuffed
        this.isConnected = isConnected && (serverName != null && !serverName.isEmpty());
        this.hasBTToolsInstalled = hasBTToolsInstalled;

        this.regions = new ArrayList<>();
        this.warpGroups = new ArrayList<>();
        this.allowsTransfers = allowsTransfers;
        this.tag = tag;

        if (isConnected && (NetworkModule.getInstance() == null ||
                (NetworkModule.getInstance().getBuildTeam() != null && NetworkModule.getInstance().getBuildTeam().isConnected())))
            this.IP = null;
        else
            this.IP = (!isConnected) ? serverIP : tag.toLowerCase() + ".buildtheearth.net";
    }

    public void createWarp(Player creator, @NonNull Warp warp) {
        createWarp(creator, warp, false);
    }

    public void createWarp(@NonNull Player creator, @NonNull Warp warp, boolean reducedMessages) {
        if (!warp.getWarpGroup().getBuildTeam().getID().equals(this.getID())) {
            creator.sendMessage(Messages.component(
                    MessageKeys.Network.WARP_CREATE_OWN_TEAM, Component.text(warp.getName())));
            return;
        }

        warp.getWarpGroup().getWarps().add(warp);
        if (!reducedMessages) {
            creator.sendMessage(Messages.component(
                    MessageKeys.Network.WARP_CREATE_SUCCESS, Component.text(warp.getName())));
        }

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
                creator.sendMessage(Messages.component(
                        MessageKeys.Network.WARP_CREATE_SYNC_FAILED, Component.text(warp.getName())));
                BuildTeamTools.getInstance().getComponentLogger().error("Failed to create warp via API", e);
            }
        });
    }

    public void createWarpGroup(Player creator, WarpGroup warpGroup) {
        // Check if the team owns that warp group
        if (!warpGroup.getBuildTeam().getID().equals(this.getID())) {
            creator.sendMessage(Messages.component(MessageKeys.Network.WARP_GROUP_CREATE_OWN_TEAM));
            return;
        }

        this.warpGroups.add(warpGroup);
        creator.sendMessage(Messages.component(
                MessageKeys.Network.WARP_GROUP_CREATE_SUCCESS, Component.text(warpGroup.getName())));

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
                creator.sendMessage(Messages.component(
                        MessageKeys.Network.WARP_GROUP_CREATE_SYNC_FAILED, Component.text(warpGroup.getName())));
                BuildTeamTools.getInstance().getComponentLogger().error("Failed to create warp group via API", e);
            }
        });
    }


    public void updateWarp(Player updater, Warp warp) {
        // Check if the team owns that warp
        if (!warp.getWarpGroup().getBuildTeam().getID().equals(this.getID())) {
            updater.sendMessage(Messages.component(MessageKeys.Network.WARP_UPDATE_OWN_TEAM));
            return;
        }

        // Local state is already updated through direct object modification
        // Send success message immediately
        updater.sendMessage(Messages.component(
                MessageKeys.Network.WARP_UPDATE_SUCCESS, Component.text(warp.getName())));

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
                updater.sendMessage(Messages.component(
                        MessageKeys.Network.WARP_UPDATE_SYNC_FAILED, Component.text(warp.getName())));
                BuildTeamTools.getInstance().getComponentLogger().error("Failed to update warp via API", e);
            }
        });
    }

    public void updateWarpGroup(Player updater, WarpGroup warpGroup) {
        // Check if the team owns that warp group
        if (!warpGroup.getBuildTeam().getID().equals(this.getID())) {
            updater.sendMessage(Messages.component(MessageKeys.Network.WARP_GROUP_UPDATE_OWN_TEAM));
            return;
        }

        // Local state is already updated through direct object modification
        // Send success message immediately
        updater.sendMessage(Messages.component(
                MessageKeys.Network.WARP_GROUP_UPDATE_SUCCESS, Component.text(warpGroup.getName())));

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
                updater.sendMessage(Messages.component(
                        MessageKeys.Network.WARP_GROUP_UPDATE_SYNC_FAILED, Component.text(warpGroup.getName())));
                BuildTeamTools.getInstance().getComponentLogger().error("Failed to update warp group via API", e);
            }
        });
    }

    public void deleteWarp(Player deleter, Warp warp) {
        // Check if the team owns that warp
        if (!warp.getWarpGroup().getBuildTeam().getID().equals(this.getID())) {
            deleter.sendMessage(Messages.component(MessageKeys.Network.WARP_DELETE_OWN_TEAM));
            return;
        }

        warp.getWarpGroup().getWarps().remove(warp);
        deleter.sendMessage(Messages.component(
                MessageKeys.Network.WARP_DELETE_SUCCESS, Component.text(warp.getName())));

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
                deleter.sendMessage(Messages.component(
                        MessageKeys.Network.WARP_DELETE_SYNC_FAILED, Component.text(warp.getName())));
                BuildTeamTools.getInstance().getComponentLogger().error("Failed to delete warp via API", e);
            }
        });
    }

    public void deleteWarpGroup(Player deleter, WarpGroup warpGroup, boolean deleteWarps) {
        if (!warpGroup.getBuildTeam().getID().equals(this.getID())) {
            deleter.sendMessage(Messages.component(MessageKeys.Network.WARP_GROUP_DELETE_OWN_TEAM));
            return;
        }

        if (deleteWarps) {
            for (Warp warp : warpGroup.getWarps()) {
                deleteWarp(deleter, warp);
            }
        }

        this.warpGroups.remove(warpGroup);
        deleter.sendMessage(Messages.component(
                MessageKeys.Network.WARP_GROUP_DELETE_SUCCESS, Component.text(warpGroup.getName())));

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
                deleter.sendMessage(Messages.component(
                        MessageKeys.Network.WARP_GROUP_DELETE_SYNC_FAILED, Component.text(warpGroup.getName())));
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
