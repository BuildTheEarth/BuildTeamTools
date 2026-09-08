package net.buildtheearth.buildteamtools.modules.network.model;

import lombok.experimental.UtilityClass;
import org.bukkit.command.CommandSender;
import com.alpsbte.alpslib.utils.ChatHelper;

@UtilityClass
public class Permissions {

    public static final String BUILD_TEAM_TOOLS = "btt.command.use";
    public static final String BUILD_TEAM_TOOLS_CACHE = "btt.command.cache";
    public static final String BUILD_TEAM_TOOLS_CHECK_FOR_UPDATES = "btt.command.checkForUpdates";
    public static final String BUILD_TEAM_TOOLS_COMMUNICATORS = "btt.command.communicators";
    public static final String BUILD_TEAM_TOOLS_DEBUG = "btt.command.debug";
    public static final String BUILD_TEAM_TOOLS_RELOAD = "btt.command.reload";
    public static final String BUILD_TEAM_TOOLS_UPDATE = "btt.command.update";


    public static final String GENERATOR_USE = "btt.generator.use";
    public static final String RAIL_GENERATOR_USE = "btt.generator.rail.use";
    public static final String RAIL_TYPE_MENU = "btt.generator.rail.menu";
    public static final String RAIL_TYPE_CREATE = "btt.generator.rail.create";
    public static final String RAIL_TYPE_EDIT = "btt.generator.rail.edit";
    public static final String RAIL_TYPE_DELETE = "btt.generator.rail.delete";
    public static final String RAIL_MULTIPLE_TRACKS = "btt.generator.rail.multiple";


    public static final String BLOCK_PALETTE_EDIT = "btt.bp.edit";


    public static final String NAVIGATOR_USE = "btt.navigator.use";


    public static final String WARP_USE = "btt.warp.use";
    public static final String WARP_CREATE = "btt.warp.create";
    public static final String WARP_EDIT = "btt.warp.edit";
    public static final String WARP_DELETE = "btt.warp.delete";
    public static final String WARP_MIGRATE = "btt.warp.migrate";
    public static final String WARP_RANDOM = "btt.warp.random";

    public static final String WARP_GROUP_CREATE = "btt.warp.group.create";
    public static final String WARP_GROUP_EDIT = "btt.warp.group.edit";
    public static final String WARP_GROUP_DELETE = "btt.warp.group.delete";

    public static final String NOTIFY_UPDATE = "btt.notify.update";

    public static final String AUTO_TPLL = "btt.global.autotpll";

    /** Checks permission and sends the standard denial message when access is denied. */
    public static boolean checkPermission(CommandSender sender, String permission) {
        if (sender.hasPermission(permission))
            return true;

        sendNoPermissionMessage(sender, permission);
        return false;
    }

    public static void sendNoPermissionMessage(CommandSender sender, String permission) {
        sender.sendMessage(ChatHelper.getErrorComponent("You don't have permission to execute this command. Required " +
                "permission: " + permission));
    }
}
