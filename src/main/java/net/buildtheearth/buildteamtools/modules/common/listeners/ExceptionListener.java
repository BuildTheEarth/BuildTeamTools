package net.buildtheearth.buildteamtools.modules.common.listeners;

import com.destroystokyo.paper.event.server.ServerExceptionEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Arrays;

public class ExceptionListener implements Listener {

    public static boolean limiter = false;

    @EventHandler
    public void onException(ServerExceptionEvent e) {
        if(limiter) return;

        String string = Arrays.toString(e.getException().getStackTrace());

        //TODO implement Build Team Exception Handling

        limiter = true;
    }
}
