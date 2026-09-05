package net.buildtheearth.buildteamtools.modules.generator.components.rail;

import com.alpsbte.alpslib.utils.ChatHelper;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;

@UtilityClass
public class RailPermissionGuard {

    public static boolean check(Player player, String permission) {
        if (player.hasPermission(permission))
            return true;

        player.sendMessage(ChatHelper.PREFIX_COMPONENT.append(ChatHelper.getErrorComponent(
                "You don't have permission to do this. Required permission: %s",
                permission
        )));
        return false;
    }
}
