package net.buildtheearth.buildteam.components.universal_experience;

import net.buildtheearth.utils.ProxyUtil;
import org.bukkit.entity.Player;

public class BuildTeam
{
    private String szName;
    private String szCode;
    private boolean bOnNetwork;
    private String szServerNameOrIP;
    private String szDescription;

    public String getName()
    {
        return szName;
    }

    public String getCode()
    {
        return szCode;
    }

    public String getDescription()
    {
        return szDescription;
    }

    public boolean isOnNetwork()
    {
        return bOnNetwork;
    }

    /**
     * @return `bte.net` if they are on the BTE network or the minecraft server IP of the team if they are not on the BTE network
     */
    public String getServerIP()
    {
        if (bOnNetwork)
            return "bte.net";
        else
            return szServerNameOrIP;
    }

    /**
     * Connects a player to this build team
     */
    public void connectPlayer(Player player)
    {
        ProxyUtil.SwitchServer(player, this.szServerNameOrIP);
    }
}
