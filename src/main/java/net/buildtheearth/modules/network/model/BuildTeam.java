package net.buildtheearth.modules.network.model;

import lombok.Getter;
import net.buildtheearth.Main;
import net.buildtheearth.modules.network.api.API;
import net.buildtheearth.modules.network.api.NetworkAPI;
import net.buildtheearth.modules.utils.ChatHelper;
import net.buildtheearth.modules.warp.model.Warp;
import net.buildtheearth.modules.warp.model.WarpGroup;
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
    private final Continent continent;
    @Getter
    private final List<Region> regions;
    @Getter
    private final List<WarpGroup> warpGroups;


    public BuildTeam(String ID, String serverIP, String name, String blankName, String serverName, Continent continent, boolean isConnected, boolean hasBTToolsInstalled) {
        this.ID = ID;
        this.name = name;
        this.blankName = blankName;
        this.serverName = serverName;
        this.continent = continent;
        this.isConnected = isConnected;
        this.hasBTToolsInstalled = hasBTToolsInstalled;

        this.regions = new ArrayList<>();
        this.warpGroups = new ArrayList<>();

        if(!isConnected)
            this.IP = serverIP;
        else
            this.IP = null;
    }

    public void createWarp(Player creator, Warp warp){
        // Check if the team owns that warp
        if(!warp.getWarpGroup().getBuildTeam().getID().equals(this.getID())){
            creator.sendMessage(ChatHelper.error("You can only create warps for your own team!"));
            return;
        }

        NetworkAPI.createWarp(warp, new API.ApiResponseCallback() {
            @Override
            public void onResponse(String response) {
                // Update the cache
                Main.buildTeamTools.getProxyModule().updateCache().thenRun(() ->
                        creator.sendMessage(ChatHelper.successful("Successfully created the warp %s!", warp.getName()))
                );
            }

            @Override
            public void onFailure(IOException e) {
                creator.sendMessage(ChatHelper.error("Something went wrong while creating the warp %s! Please take a look at the console.", warp.getName()));
                e.printStackTrace();
            }
        });
    }

    public void createWarpGroup(Player creator, WarpGroup warpGroup){
        // Check if the team owns that warp group
        if(!warpGroup.getBuildTeam().getID().equals(this.getID())){
            creator.sendMessage(ChatHelper.error("You can only create warp groups for your own team!"));
            return;
        }

        NetworkAPI.createWarpGroup(warpGroup, new API.ApiResponseCallback() {
            @Override
            public void onResponse(String response) {
                // Update the cache
                Main.buildTeamTools.getProxyModule().updateCache().thenRun(() ->
                        creator.sendMessage(ChatHelper.successful("Successfully created the warp group %s!", warpGroup.getName()))
                );
            }

            @Override
            public void onFailure(IOException e) {
                creator.sendMessage(ChatHelper.error("Something went wrong while creating the warp group %s! Please take a look at the console.", warpGroup.getName()));
                e.printStackTrace();
            }
        });
    }


    public void updateWarp(Player updater, Warp warp){
        // Check if the team owns that warp
        if(!warp.getWarpGroup().getBuildTeam().getID().equals(this.getID())){
            updater.sendMessage(ChatHelper.error("You can only update warps for your own team!"));
            return;
        }

        NetworkAPI.updateWarp(warp, new API.ApiResponseCallback() {
            @Override
            public void onResponse(String response) {
                // Update the cache
                Main.buildTeamTools.getProxyModule().updateCache().thenRun(() ->
                        updater.sendMessage(ChatHelper.successful("Successfully updated the warp %s!", warp.getName()))
                ).exceptionally(e -> {
                    updater.sendMessage(ChatHelper.error("Something went wrong while updating the warp %s! Please take a look at the console.", warp.getName()));
                    e.printStackTrace();
                    return null;
                });
            }

            @Override
            public void onFailure(IOException e) {
                updater.sendMessage(ChatHelper.error("Something went wrong while updating the warp %s! Please take a look at the console.", warp.getName()));
                e.printStackTrace();
            }
        });
    }

    public void updateWarpGroup(Player updater, WarpGroup warpGroup){
        // Check if the team owns that warp group
        if(!warpGroup.getBuildTeam().getID().equals(this.getID())){
            updater.sendMessage(ChatHelper.error("You can only update warp groups for your own team!"));
            return;
        }

        NetworkAPI.updateWarpGroup(warpGroup, new API.ApiResponseCallback() {
            @Override
            public void onResponse(String response) {
                // Update the cache
                Main.buildTeamTools.getProxyModule().updateCache().thenRun(() ->
                        updater.sendMessage(ChatHelper.successful("Successfully updated the warp group %s!", warpGroup.getName()))
                ).exceptionally(e -> {
                    updater.sendMessage(ChatHelper.error("Something went wrong while updating the warp group %s! Please take a look at the console.", warpGroup.getName()));
                    e.printStackTrace();
                    return null;
                });
            }

            @Override
            public void onFailure(IOException e) {
                updater.sendMessage(ChatHelper.error("Something went wrong while updating the warp group %s! Please take a look at the console.", warpGroup.getName()));
                e.printStackTrace();
            }
        });
    }

    public void deleteWarp(Player deleter, Warp warp){
        // Check if the team owns that warp
        if(!warp.getWarpGroup().getBuildTeam().getID().equals(this.getID())){
            deleter.sendMessage(ChatHelper.error("You can only delete warps for your own team!"));
            return;
        }

        NetworkAPI.deleteWarp(warp, new API.ApiResponseCallback() {
            @Override
            public void onResponse(String response) {
                // Update the cache
                Main.buildTeamTools.getProxyModule().updateCache().thenRun(() ->
                        deleter.sendMessage(ChatHelper.successful("Successfully deleted the warp %s!", warp.getName()))
                );
            }

            @Override
            public void onFailure(IOException e) {
                deleter.sendMessage(ChatHelper.error("Something went wrong while deleting the warp %s! Please take a look at the console.", warp.getName()));
                e.printStackTrace();
            }
        });
    }

    public void deleteWarpGroup(Player deleter, WarpGroup warpGroup){
        // Check if the team owns that warp group
        if(!warpGroup.getBuildTeam().getID().equals(this.getID())){
            deleter.sendMessage(ChatHelper.error("You can only delete warp groups for your own team!"));
            return;
        }

        NetworkAPI.deleteWarpGroup(warpGroup, new API.ApiResponseCallback() {
            @Override
            public void onResponse(String response) {
                // Update the cache
                Main.buildTeamTools.getProxyModule().updateCache().thenRun(() ->
                        deleter.sendMessage(ChatHelper.successful("Successfully deleted the warp group %s!", warpGroup.getName()))
                );
            }

            @Override
            public void onFailure(IOException e) {
                deleter.sendMessage(ChatHelper.error("Something went wrong while deleting the warp group %s! Please take a look at the console.", warpGroup.getName()));
                e.printStackTrace();
            }
        });
    }

}
