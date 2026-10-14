package net.buildtheearth.buildteamtools.modules.map.commands;

import com.alpsbte.alpslib.web.api.ClaimApi;
import com.alpsbte.alpslib.web.invoker.ApiClient;
import com.alpsbte.alpslib.web.invoker.ApiException;
import com.alpsbte.alpslib.web.model.Claim;
import com.alpsbte.alpslib.web.model.PublicBuildteamsTeamClaimsPostRequest;
import com.alpsbte.alpslib.web.model.PublicBuildteamsTeamClaimsPostRequestAreaInner;
import com.alpsbte.alpslib.utils.ChatHelper;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import net.buildtheearth.OutOfProjectionBoundsException;
import net.buildtheearth.Projection;
import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.modules.Module;
import net.buildtheearth.buildteamtools.modules.map.MapModule;
import net.buildtheearth.buildteamtools.modules.network.model.Permissions;
import net.buildtheearth.model.GeographicalCoordinate;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MapCreateCommand {

    public void register(Module owner) {
        BuildTeamTools.getInstance().getCommandManager().registerSubcommand(
                owner,
                "map",
                "create",
                "Create a map claim from your cuboid WorldEdit selection.",
                builder -> builder.permission(Permissions.MAP_CREATE),
                context -> {
                    CommandSender sender = context.sender().getSender();
                    if (!(sender instanceof Player player)) {
                        sender.sendMessage(ChatHelper.getErrorComponent("This command can only be used by a player!"));
                        return;
                    }

                    create(player);
                }
        );
    }

    private void create(Player player) {
        String token = MapModule.getInstance().getConfig().webApiToken();
        if (token == null || token.isBlank()) {
            player.sendMessage(ChatHelper.getErrorComponent(
                    "Map claim creation is unavailable. Configure webApiToken in modules/map/config.yml."));
            return;
        }
        String teamId = MapModule.getInstance().getConfig().buildTeamId();
        if (teamId == null || teamId.isBlank()) {
            player.sendMessage(ChatHelper.getErrorComponent(
                    "Map claim creation is unavailable. Configure buildTeamId in modules/map/config.yml."));
            return;
        }

        if (!Bukkit.getPluginManager().isPluginEnabled("WorldEdit")
                && !Bukkit.getPluginManager().isPluginEnabled("FastAsyncWorldEdit")) {
            player.sendMessage(ChatHelper.getErrorComponent("WorldEdit or FastAsyncWorldEdit must be enabled."));
            return;
        }

        World earthWorld = BuildTeamTools.getInstance().getEarthWorld();
        if (earthWorld == null) {
            player.sendMessage(ChatHelper.getErrorComponent("The configured Earth world is not loaded."));
            return;
        }
        if (!player.getWorld().equals(earthWorld)) {
            player.sendMessage(ChatHelper.getErrorComponent("Select an area in the configured Earth world."));
            return;
        }

        Region selection;
        try {
            Actor actor = BukkitAdapter.adapt(player);
            LocalSession session = WorldEdit.getInstance().getSessionManager().get(actor);
            selection = session.getSelection(BukkitAdapter.adapt(earthWorld));
        } catch (IncompleteRegionException exception) {
            player.sendMessage(ChatHelper.getErrorComponent("Make a WorldEdit selection before creating a map claim."));
            return;
        }
        if (!(selection instanceof CuboidRegion cuboidSelection)) {
            player.sendMessage(ChatHelper.getErrorComponent(
                    "Map claims currently require a cuboid WorldEdit selection."));
            return;
        }

        List<PublicBuildteamsTeamClaimsPostRequestAreaInner> area;
        try {
            area = createArea(cuboidSelection);
        } catch (OutOfProjectionBoundsException exception) {
            player.sendMessage(ChatHelper.getErrorComponent(
                    "The selection is outside the supported map projection: %s", exception.getMessage()));
            return;
        }

        PublicBuildteamsTeamClaimsPostRequest request = new PublicBuildteamsTeamClaimsPostRequest()
                .area(area)
                .active(true)
                .finished(false);
        UUID playerId = player.getUniqueId();

        player.sendMessage(ChatHelper.getStandardComponent(false, "Creating map claim from your selection..."));
        Bukkit.getScheduler().runTaskAsynchronously(BuildTeamTools.getInstance(), () -> {
            try {
                ApiClient apiClient = new ApiClient();
                apiClient.setBearerToken(token);
                Claim claim = new ClaimApi(apiClient)
                        .publicBuildteamsTeamClaimsPost(teamId, request, false, null);
                if (claim == null || claim.getId() == null) {
                    throw new IllegalStateException("The map API did not return the created claim ID.");
                }
                Bukkit.getScheduler().runTask(BuildTeamTools.getInstance(), () -> {
                    Player onlinePlayer = Bukkit.getPlayer(playerId);
                    if (onlinePlayer != null) {
                        onlinePlayer.sendMessage(ChatHelper.getSuccessComponent(
                                "Created map claim %s (ID: %s).",
                                claim.getName() == null ? "Unnamed Claim" : claim.getName(), claim.getId()));
                    }
                });
            } catch (ApiException | RuntimeException exception) {
                BuildTeamTools.getInstance().getComponentLogger().error(
                        "Failed to create map claim for player " + playerId + ".", exception);
                Bukkit.getScheduler().runTask(BuildTeamTools.getInstance(), () -> {
                    Player onlinePlayer = Bukkit.getPlayer(playerId);
                    if (onlinePlayer != null) {
                        String reason = exception.getMessage() == null
                                ? exception.getClass().getSimpleName()
                                : exception.getMessage();
                        onlinePlayer.sendMessage(ChatHelper.getErrorComponent(
                                "Failed to create map claim: %s", reason));
                    }
                });
            }
        });
    }

    private List<PublicBuildteamsTeamClaimsPostRequestAreaInner> createArea(CuboidRegion selection)
            throws OutOfProjectionBoundsException {
        int minX = selection.getMinimumPoint().x();
        int maxX = selection.getMaximumPoint().x();
        int minZ = selection.getMinimumPoint().z();
        int maxZ = selection.getMaximumPoint().z();

        double[][] minecraftCorners = {
                {minX - 0.5, minZ - 0.5},
                {maxX + 0.5, minZ - 0.5},
                {maxX + 0.5, maxZ + 0.5},
                {minX - 0.5, maxZ + 0.5}
        };

        List<PublicBuildteamsTeamClaimsPostRequestAreaInner> area = new ArrayList<>();
        for (double[] corner : minecraftCorners) {
            GeographicalCoordinate coordinate = Projection.toGeo(corner[0], corner[1]);
            area.add(new PublicBuildteamsTeamClaimsPostRequestAreaInner(List.of(
                    Double.toString(coordinate.longitude()),
                    Double.toString(coordinate.latitude())
            )));
        }
        area.add(area.getFirst());
        return area;
    }
}
