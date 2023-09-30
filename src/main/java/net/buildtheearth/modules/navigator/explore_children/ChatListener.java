package net.buildtheearth.modules.navigator.explore_children;

import net.buildtheearth.Main;
import net.buildtheearth.buildteam.components.universal_experience.Location;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener
{
    private Location location;
    private ChatType chatType;
    private AddLocationMenu menu;

    public ChatListener(AddLocationMenu menu, Location location, ChatType chatType)
    {
        this.location = location;
        this.chatType = chatType;
        this.menu = menu;
        Bukkit.getServer().getPluginManager().registerEvents(this, Main.instance);
    }

    @EventHandler
    public void chatEvent(AsyncPlayerChatEvent event)
    {
        //Changes the details of the location
        switch (chatType)
        {
            case LocationName:
                location.setName(event.getMessage());
        }

        //Unregisters this listener
        HandlerList.unregisterAll(this);

        //Refreshes and redisplays the menu to the player
        menu.refreshMenu();
    }

    public enum ChatType
    {
        LocationName
    }
}
