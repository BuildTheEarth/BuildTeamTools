package net.buildtheearth.modules.network.model;

import lombok.Getter;
import net.buildtheearth.modules.navigation.components.warps.model.Warp;
import net.buildtheearth.modules.navigation.components.warps.model.WarpGroup;
import net.buildtheearth.modules.network.NetworkModule;
import net.buildtheearth.modules.network.api.API;
import net.buildtheearth.modules.network.api.NetworkAPI;
import net.buildtheearth.utils.ChatHelper;
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
    private final boolean isConnected;
    @Getter
    private final boolean hasBTToolsInstalled;
    @Getter
    private final List<Region> regions;
    @Getter
    private final List<WarpGroup> warpGroups;
    @Getter
    private final boolean allowsTransfers;


    public BuildTeam(String ID, String serverIP, String name, String blankName, String serverName, boolean isConnected, boolean hasBTToolsInstalled, boolean allowsTransfers) {
        this.ID = ID;
        this.name = name;
        this.blankName = blankName;
        this.serverName = serverName;
        this.isConnected = isConnected;
        this.hasBTToolsInstalled = hasBTToolsInstalled;

        this.regions = new ArrayList<>();
        this.warpGroups = new ArrayList<>();
        this.allowsTransfers = allowsTransfers;

        if(!isConnected)
            this.IP = serverIP;
        else
            this.IP = null;
    }

    public void createWarp(Player creator, Warp warp){
        // Check if the team owns that warp
        if(!warp.getWarpGroup().getBuildTeam().getID().equals(this.getID())){
            creator.sendMessage(ChatHelper.getErrorString("You can only create warps for your own team!"));
            return;
        }

        NetworkAPI.createWarp(warp, new API.ApiResponseCallback() {
            @Override
            public void onResponse(String response) {
                // Update the cache
                NetworkModule.getInstance().updateCache().thenRun(() ->
                        ChatHelper.sendSuccessfulMessage(creator, "Successfully created the warp %s!", warp.getName())
                );
            }

            @Override
            public void onFailure(IOException e) {
                creator.sendMessage(ChatHelper.getErrorString("Something went wrong while creating the warp %s! Please take a look at the console.", warp.getName()));
                e.printStackTrace();
            }
        });
    }

    public void createWarpGroup(Player creator, WarpGroup warpGroup){
        // Check if the team owns that warp group
        if(!warpGroup.getBuildTeam().getID().equals(this.getID())){
            creator.sendMessage(ChatHelper.getErrorString("You can only create warp groups for your own team!"));
            return;
        }

        NetworkAPI.createWarpGroup(warpGroup, new API.ApiResponseCallback() {
            @Override
            public void onResponse(String response) {
                // Update the cache
                NetworkModule.getInstance().updateCache().thenRun(() ->
                        ChatHelper.sendSuccessfulMessage(creator, "Successfully created the warp group %s!", warpGroup.getName())
                );
            }

            @Override
            public void onFailure(IOException e) {
                creator.sendMessage(ChatHelper.getErrorString("Something went wrong while creating the warp group %s! Please take a look at the console.", warpGroup.getName()));
                e.printStackTrace();
            }
        });
    }


    public void updateWarp(Player updater, Warp warp){
        // Check if the team owns that warp
        if(!warp.getWarpGroup().getBuildTeam().getID().equals(this.getID())){
            updater.sendMessage(ChatHelper.getErrorString("You can only update warps for your own team!"));
            return;
        }

        NetworkAPI.updateWarp(warp, new API.ApiResponseCallback() {
            @Override
            public void onResponse(String response) {
                // Update the cache
                NetworkModule.getInstance().updateCache().thenRun(() ->
                        ChatHelper.sendSuccessfulMessage(updater, "Successfully updated the warp %s!", warp.getName())
                ).exceptionally(e -> {
                    updater.sendMessage(ChatHelper.getErrorString("Something went wrong while updating the warp %s! Please take a look at the console.", warp.getName()));
                    e.printStackTrace();
                    return null;
                });
            }

            @Override
            public void onFailure(IOException e) {
                updater.sendMessage(ChatHelper.getErrorString("Something went wrong while updating the warp %s! Please take a look at the console.", warp.getName()));
                e.printStackTrace();
            }
        });
    }

    public void updateWarpGroup(Player updater, WarpGroup warpGroup){
        // Check if the team owns that warp group
        if(!warpGroup.getBuildTeam().getID().equals(this.getID())){
            updater.sendMessage(ChatHelper.getErrorString("You can only update warp groups for your own team!"));
            return;
        }

        NetworkAPI.updateWarpGroup(warpGroup, new API.ApiResponseCallback() {
            @Override
            public void onResponse(String response) {
                // Update the cache
                NetworkModule.getInstance().updateCache().thenRun(() ->
                        ChatHelper.sendSuccessfulMessage(updater, "Successfully updated the warp group %s!", warpGroup.getName())
                ).exceptionally(e -> {
                    updater.sendMessage(ChatHelper.getErrorString("Something went wrong while updating the warp group %s! Please take a look at the console.", warpGroup.getName()));
                    e.printStackTrace();
                    return null;
                });
            }

            @Override
            public void onFailure(IOException e) {
                updater.sendMessage(ChatHelper.getErrorString("Something went wrong while updating the warp group %s! Please take a look at the console.", warpGroup.getName()));
                e.printStackTrace();
            }
        });
    }

    public void deleteWarp(Player deleter, Warp warp){
        // Check if the team owns that warp
        if(!warp.getWarpGroup().getBuildTeam().getID().equals(this.getID())){
            deleter.sendMessage(ChatHelper.getErrorString("You can only delete warps for your own team!"));
            return;
        }

        NetworkAPI.deleteWarp(warp, new API.ApiResponseCallback() {
            @Override
            public void onResponse(String response) {
                // Update the cache
                NetworkModule.getInstance().updateCache().thenRun(() ->
                        ChatHelper.sendSuccessfulMessage(deleter, "Successfully deleted the warp %s!", warp.getName())
                );
            }

            @Override
            public void onFailure(IOException e) {
                deleter.sendMessage(ChatHelper.getErrorString("Something went wrong while deleting the warp %s! Please take a look at the console.", warp.getName()));
                e.printStackTrace();
            }
        });
    }

    public void deleteWarpGroup(Player deleter, WarpGroup warpGroup){
        // Check if the team owns that warp group
        if(!warpGroup.getBuildTeam().getID().equals(this.getID())){
            deleter.sendMessage(ChatHelper.getErrorString("You can only delete warp groups for your own team!"));
            return;
        }

        NetworkAPI.deleteWarpGroup(warpGroup, new API.ApiResponseCallback() {
            @Override
            public void onResponse(String response) {
                // Update the cache
                NetworkModule.getInstance().updateCache().thenRun(() ->
                        ChatHelper.sendSuccessfulMessage(deleter, "Successfully deleted the warp group %s!", warpGroup.getName())
                );
            }

            @Override
            public void onFailure(IOException e) {
                deleter.sendMessage(ChatHelper.getErrorString("Something went wrong while deleting the warp group %s! Please take a look at the console.", warpGroup.getName()));
                e.printStackTrace();
            }
        });
    }

}
