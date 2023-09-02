package net.buildtheearth.utils;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;


public class Permissions {
	public static final String PermOwner = "server.owner";
	public static final String PermAdmin = "server.admin";
	public static final String PermModerator = "server.moderator";
	public static final String PermSupporter = "server.supporter";
	public static final String PermPremium = "server.premium";
	public static final String PermYoutuber = "server.youtuber";
	public static final String PermBuilder = "server.builder";
	public static final String PermDeveloper = "server.developer";
	public static final String PermDonator = "server.donator";
	public static final String PermAdvanced = "server.advanced";
	public static final String PermProfessional = "server.professional";
	
	public static boolean isMember(Player p) {
		return (
				 !p.hasPermission(Permissions.PermPremium)
				&!p.hasPermission(Permissions.PermAdmin) 
				&!p.hasPermission(Permissions.PermDeveloper) 
				&!p.hasPermission(Permissions.PermSupporter) 
				&!p.hasPermission(Permissions.PermOwner) 
				&!p.hasPermission(Permissions.PermBuilder)
				&!p.hasPermission(Permissions.PermYoutuber)
				&!p.hasPermission(Permissions.PermModerator)
		);
	}
	
	public static boolean isTeamMember(Player p) {
		return (
				 p.hasPermission(Permissions.PermAdmin) 
				|p.hasPermission(Permissions.PermDeveloper) 
				|p.hasPermission(Permissions.PermSupporter) 
				|p.hasPermission(Permissions.PermOwner) 
				|p.hasPermission(Permissions.PermBuilder)
				|p.hasPermission(Permissions.PermYoutuber)
				|p.hasPermission(Permissions.PermModerator)
		);
	}
	
	public static String getPrefixColorString(String permission){
        switch (permission) {
            case PermOwner:
                return "§4";
            case PermAdmin:
                return "§c";
            case PermModerator:
                return "§3";
            case PermDeveloper:
                return "§b";
            case PermSupporter:
                return "§9";
            case PermBuilder:
                return "§1";
            case PermYoutuber:
                return "§5";
            case PermPremium:
                return "§6";
            default:
                return "§a";
        }
	}
	
	public static String getPrefixColorString(Player p){
		if(p.hasPermission(PermOwner)){
			return "§4";
		}else if(p.hasPermission(PermAdmin)){
			return "§c";
		}else if(p.hasPermission(PermModerator)){
			return "§3";
		}else if(p.hasPermission(PermDeveloper)){
			return "§b";
		}else if(p.hasPermission(PermSupporter)){
			return "§9";
		}else if(p.hasPermission(PermBuilder)){
			return "§1";
		}else if(p.hasPermission(PermYoutuber)){
			return "§5";
		}else if(p.hasPermission(PermPremium)){
			return "§6";
		}else{
			return "§a";
		}
	}
	
	public static ChatColor getPrefixChatColor(Player p){
		if(p.hasPermission(PermOwner)){
			return ChatColor.DARK_RED;
		}else if(p.hasPermission(PermAdmin)){
			return ChatColor.RED;
		}else if(p.hasPermission(PermModerator)){
			return ChatColor.DARK_AQUA;
		}else if(p.hasPermission(PermDeveloper)){
			return ChatColor.AQUA;
		}else if(p.hasPermission(PermSupporter)){
			return ChatColor.BLUE;
		}else if(p.hasPermission(PermBuilder)){
			return ChatColor.DARK_BLUE;
		}else if(p.hasPermission(PermYoutuber)){
			return ChatColor.DARK_PURPLE;
		}else if(p.hasPermission(PermPremium)){
			return ChatColor.GOLD;
		}else{
			return ChatColor.GREEN;
		}
	}
	
	public static Color getPrefixColor(Player p){
		if(p.hasPermission(PermOwner)){
			return Color.MAROON;
		}else if(p.hasPermission(PermAdmin)){
			return Color.RED;
		}else if(p.hasPermission(PermModerator)){
			return Color.TEAL;
		}else if(p.hasPermission(PermDeveloper)){
			return Color.AQUA;
		}else if(p.hasPermission(PermSupporter)){
			return Color.BLUE;
		}else if(p.hasPermission(PermBuilder)){
			return Color.NAVY;
		}else if(p.hasPermission(PermYoutuber)){
			return Color.PURPLE;
		}else if(p.hasPermission(PermPremium)){
			return Color.ORANGE;
		}else{
			return Color.LIME;
		}
	}
}
