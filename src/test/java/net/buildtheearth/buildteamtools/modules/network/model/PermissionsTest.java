package net.buildtheearth.buildteamtools.modules.network.model;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PermissionsTest {
    private CommandSender sender(boolean allowed, List<Component> messages) {
        return (CommandSender) Proxy.newProxyInstance(CommandSender.class.getClassLoader(),
                new Class<?>[]{CommandSender.class}, (proxy, method, args) -> {
                    if (method.getName().equals("hasPermission"))
                        return allowed;
                    if (method.getName().equals("sendMessage")) {
                        for (Object arg : args)
                            if (arg instanceof Component component)
                                messages.add(component);
                        return null;
                    }
                    throw new UnsupportedOperationException(method.getName());
                });
    }

    @Test
    void permissionCheckUsesTheExistingDenialMessageAndAcceptsAnyCommandSender() {
        List<Component> messages = new ArrayList<>();
        assertTrue(Permissions.checkPermission(sender(true, messages), Permissions.RAIL_TYPE_EDIT));
        assertTrue(messages.isEmpty());
        assertFalse(Permissions.checkPermission(sender(false, messages), Permissions.RAIL_TYPE_EDIT));
        assertEquals(1, messages.size());
        String denial = PlainTextComponentSerializer.plainText().serialize(messages.getFirst());
        assertTrue(denial.contains(Permissions.RAIL_TYPE_EDIT));
        assertTrue(denial.contains("You don't have permission to execute this command."));
    }
}
