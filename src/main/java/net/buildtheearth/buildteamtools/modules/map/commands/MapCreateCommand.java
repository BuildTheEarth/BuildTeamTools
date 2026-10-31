package net.buildtheearth.buildteamtools.modules.map.commands;

import com.alpsbte.alpslib.utils.ChatHelper;
import com.alpsbte.alpslib.web.api.ClaimApi;
import com.alpsbte.alpslib.web.invoker.ApiClient;
import com.alpsbte.alpslib.web.invoker.ApiException;
import com.alpsbte.alpslib.web.model.Claim;
import com.alpsbte.alpslib.web.model.PublicBuildteamsTeamClaimsPostRequest;
import com.alpsbte.alpslib.web.model.PublicBuildteamsTeamClaimsPostRequestAreaInner;
import com.alpsbte.alpslib.web.model.PublicBuildteamsTeamClaimsPostRequestOwner;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import net.buildtheearth.OutOfProjectionBoundsException;
import net.buildtheearth.Projection;
import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.modules.Module;
import net.buildtheearth.buildteamtools.modules.common.commands.BttCommandManager;
import net.buildtheearth.buildteamtools.modules.common.metrics.FeatureUsage;
import net.buildtheearth.buildteamtools.modules.common.metrics.UsageFeature;
import net.buildtheearth.buildteamtools.modules.map.MapModule;
import net.buildtheearth.buildteamtools.modules.network.model.Permissions;
import net.buildtheearth.buildteamtools.utils.io.MapConfig;
import net.buildtheearth.model.GeographicalCoordinate;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.minecraft.extras.RichDescription;
import org.incendo.cloud.parser.standard.StringParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MapCreateCommand {

    private static final String COMMAND_NAME = "map";
    private static final int MIN_POLYGON_VERTICES = 3;
    /** Claim creation may wait on Overpass (retries); OkHttp's default 10s read timeout is too short. */
    private static final int API_READ_TIMEOUT_MS = 120_000;
    private static final String CLAIM_MAP_URL = "https://buildtheearth.net/map?claim=";

    private final MapModule module;

    public MapCreateCommand(MapModule module) {
        this.module = module;
    }

    private MapConfig config() {
        // Config is assigned after Module.super() finishes; registerCommands() runs during super().
        return module.getConfig();
    }

    public void register(Module owner) {
        var commandManager = BuildTeamTools.getInstance().getCommandManager();
        commandManager.register(owner, COMMAND_NAME, Permissions.MAP_CREATE,
                (sender, args) -> sender.sendMessage(ChatHelper.getErrorComponent("Usage: /map create [name]")),
                (sender, input) -> BttCommandManager.matchingSuggestions(List.of("create"), input));
        commandManager.registerSubcommand(
                owner,
                COMMAND_NAME,
                "create",
                "Create a map claim from your polygonal WorldEdit selection.",
                builder -> builder
                        .permission(Permissions.MAP_CREATE)
                        .optional("name", StringParser.greedyStringParser(),
                                RichDescription.of(Component.text("Optional claim name"))),
                context -> {
                    CommandSender sender = context.sender().getSender();
                    if (!(sender instanceof Player player)) {
                        sender.sendMessage(ChatHelper.getErrorComponent("This command can only be used by a player!"));
                        return;
                    }
                    create(player, context.<String>optional("name").orElse("").trim());
                }
        );
    }

    private void create(Player player, String claimName) {
        if (!hasApiConfig(player) || !hasWorldEdit(player)) return;

        Polygonal2DRegion selection = requirePolygonalSelection(player);
        if (selection == null) return;

        List<PublicBuildteamsTeamClaimsPostRequestAreaInner> area = buildArea(player, selection);
        if (area == null) return;

        PublicBuildteamsTeamClaimsPostRequest request = new PublicBuildteamsTeamClaimsPostRequest()
                .owner(new PublicBuildteamsTeamClaimsPostRequestOwner().minecraft(player.getName()))
                .area(area)
                .active(true)
                .finished(false);
        if (!claimName.isEmpty()) {
            request.name(claimName);
        }

        FeatureUsage.record(UsageFeature.MAP);
        player.sendMessage(ChatHelper.getStandardComponent(false, "Creating map claim from your selection..."));
        Bukkit.getScheduler().runTaskAsynchronously(BuildTeamTools.getInstance(),
                () -> submitClaim(player, request));
    }

    private boolean hasApiConfig(Player player) {
        MapConfig config = config();
        if (config.webApiToken() == null || config.webApiToken().isBlank()) {
            player.sendMessage(ChatHelper.getErrorComponent(
                    "Map claim creation is unavailable. Configure webApiToken in modules/map/config.yml."));
            return false;
        }
        if (config.buildTeamId() == null || config.buildTeamId().isBlank()) {
            player.sendMessage(ChatHelper.getErrorComponent(
                    "Map claim creation is unavailable. Configure buildTeamId in modules/map/config.yml."));
            return false;
        }
        if (config.webApiBasePath() == null || config.webApiBasePath().isBlank()) {
            player.sendMessage(ChatHelper.getErrorComponent(
                    "Map claim creation is unavailable. Configure webApiBasePath in modules/map/config.yml."));
            return false;
        }
        return true;
    }

    private boolean hasWorldEdit(Player player) {
        if (Bukkit.getPluginManager().isPluginEnabled("WorldEdit")
                || Bukkit.getPluginManager().isPluginEnabled("FastAsyncWorldEdit")) {
            return true;
        }
        player.sendMessage(ChatHelper.getErrorComponent("WorldEdit or FastAsyncWorldEdit must be enabled."));
        return false;
    }

    private Polygonal2DRegion requirePolygonalSelection(Player player) {
        World earthWorld = BuildTeamTools.getInstance().getEarthWorld();
        if (earthWorld == null) {
            player.sendMessage(ChatHelper.getErrorComponent("The configured Earth world is not loaded."));
            return null;
        }
        if (!player.getWorld().equals(earthWorld)) {
            player.sendMessage(ChatHelper.getErrorComponent("Select an area in the configured Earth world."));
            return null;
        }

        Region selection;
        try {
            Actor actor = BukkitAdapter.adapt(player);
            LocalSession session = WorldEdit.getInstance().getSessionManager().get(actor);
            selection = session.getSelection(BukkitAdapter.adapt(earthWorld));
        } catch (IncompleteRegionException exception) {
            player.sendMessage(ChatHelper.getErrorComponent(
                    "Make a polygonal WorldEdit selection (//sel poly) before creating a map claim."));
            return null;
        }
        if (!(selection instanceof Polygonal2DRegion polygonalSelection)) {
            player.sendMessage(ChatHelper.getErrorComponent(
                    "Map claims require a polygonal WorldEdit selection. Use //sel poly."));
            return null;
        }
        if (polygonalSelection.getPoints().size() < MIN_POLYGON_VERTICES) {
            player.sendMessage(ChatHelper.getErrorComponent(
                    "A map claim needs at least %s distinct polygon points.", MIN_POLYGON_VERTICES));
            return null;
        }
        return polygonalSelection;
    }

    private List<PublicBuildteamsTeamClaimsPostRequestAreaInner> buildArea(
            Player player, Polygonal2DRegion selection) {
        try {
            return createArea(selection);
        } catch (OutOfProjectionBoundsException exception) {
            player.sendMessage(ChatHelper.getErrorComponent(
                    "The selection is outside the supported map projection: %s", exception.getMessage()));
            return null;
        }
    }

    private void submitClaim(Player player, PublicBuildteamsTeamClaimsPostRequest request) {
        try {
            MapConfig config = config();
            ApiClient apiClient = new ApiClient();
            apiClient.setBearerToken(config.webApiToken());
            apiClient.setBasePath(config.webApiBasePath());
            apiClient.setReadTimeout(API_READ_TIMEOUT_MS);
            // null, not false: Express treats ?slug=false as truthy and looks up the UUID as a slug.
            Claim claim = new ClaimApi(apiClient)
                    .publicBuildteamsTeamClaimsPost(config.buildTeamId(), request, null, null);
            if (claim == null || claim.getId() == null) {
                throw new IllegalStateException("The map API did not return the created claim ID.");
            }
            sendSuccess(player, claim);
        } catch (ApiException | RuntimeException exception) {
            BuildTeamTools.getInstance().getComponentLogger().error(
                    "Failed to create map claim for player {}.", player.getUniqueId(), exception);
            sendFailure(player, exception);
        }
    }

    private void sendSuccess(Player player, Claim claim) {
        String claimName = claim.getName() == null || claim.getName().isBlank()
                ? "Unnamed Claim"
                : claim.getName();
        String claimUrl = CLAIM_MAP_URL + claim.getId();
        Component idLink = Component.text(Objects.requireNonNull(claim.getId()).toString(), NamedTextColor.AQUA)
                .clickEvent(ClickEvent.openUrl(claimUrl))
                .hoverEvent(HoverEvent.showText(Component.text("Open claim on the map", NamedTextColor.GRAY)));
        player.sendMessage(ChatHelper.getSuccessComponent("Created map claim %s (ID: ", claimName)
                .append(idLink)
                .append(Component.text(").")));
    }

    private void sendFailure(Player player, Exception exception) {
        if (exception instanceof ApiException apiException && apiException.getCode() == 404) {
            player.sendMessage(ChatHelper.getErrorComponent(
                    "Your Minecraft account is not registered on buildtheearth.net. Link it on the website first."));
            return;
        }
        String reason = exception.getMessage() == null
                ? exception.getClass().getSimpleName()
                : exception.getMessage();
        player.sendMessage(ChatHelper.getErrorComponent("Failed to create map claim: %s", reason));
    }

    /** API default coordType expects {@code "lng, lat"} strings; the API closes the ring. */
    private List<PublicBuildteamsTeamClaimsPostRequestAreaInner> createArea(Polygonal2DRegion selection)
            throws OutOfProjectionBoundsException {
        List<PublicBuildteamsTeamClaimsPostRequestAreaInner> area = new ArrayList<>();
        for (BlockVector2 point : selection.getPoints()) {
            GeographicalCoordinate coordinate = Projection.toGeo(point.x(), point.z());
            area.add(new PublicBuildteamsTeamClaimsPostRequestAreaInner(
                    coordinate.longitude() + ", " + coordinate.latitude()));
        }
        return area;
    }
}
