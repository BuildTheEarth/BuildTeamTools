package net.buildtheearth.buildteamtools.modules.miscellaneous.blockpalettegui;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.buildtheearth.buildteamtools.i18n.MessageKeys;
import net.buildtheearth.buildteamtools.i18n.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.UUID;

/**
 * Shared chat-prompt handling for create/edit palette name and description fields.
 */
final class PaletteChatInput {

    public enum Mode {NONE, NAME, DESCRIPTION}

    interface Session {
        Player player();

        JavaPlugin plugin();

        Mode mode();

        void setMode(Mode mode);

        void setAwaitingInput(boolean awaiting);

        void setName(String name);

        void setDescription(String description);

        void onCancel();

        void reopenMenu();
    }

    private PaletteChatInput() {
    }

    static void register(
            Map<UUID, Listener> activeChat,
            Session session,
            int maxNameLength,
            int maxDescriptionLength
    ) {
        Listener prev = activeChat.remove(session.player().getUniqueId());
        if (prev != null) {
            HandlerList.unregisterAll(prev);
        }
        Listener listener = new ChatListener(session, maxNameLength, maxDescriptionLength, activeChat);
        activeChat.put(session.player().getUniqueId(), listener);
        session.plugin().getServer().getPluginManager().registerEvents(listener, session.plugin());
    }

    static void unregister(Map<UUID, Listener> activeChat, Session session) {
        Listener prev = activeChat.remove(session.player().getUniqueId());
        if (prev != null) {
            HandlerList.unregisterAll(prev);
        }
        session.setMode(Mode.NONE);
        session.setAwaitingInput(false);
    }

    private record ChatListener(
            Session session,
            int maxNameLength,
            int maxDescriptionLength,
            Map<UUID, Listener> activeChat
    ) implements Listener {

        @EventHandler
        public void onPlayerChat(@NonNull AsyncChatEvent event) {
            if (!event.getPlayer().getUniqueId().equals(session.player().getUniqueId())) {
                return;
            }
            if (session.mode() == Mode.NONE) {
                return;
            }

            event.setCancelled(true);
            String message = PlainTextComponentSerializer.plainText().serialize(event.message());
            Player player = session.player();

            if (message.equalsIgnoreCase("cancel")) {
                session.onCancel();
                player.sendMessage(Messages.component(MessageKeys.BlockPalette.INPUT_CANCELLED));
                finish();
                return;
            }

            if (session.mode() == Mode.NAME) {
                if (message.length() > maxNameLength) {
                    player.sendMessage(Messages.component(
                            MessageKeys.BlockPalette.NAME_TOO_LONG, Component.text(maxNameLength)));
                } else if (message.isEmpty()) {
                    player.sendMessage(Messages.component(MessageKeys.BlockPalette.NAME_EMPTY));
                } else {
                    session.setName(message);
                    player.sendMessage(Messages.component(
                            MessageKeys.BlockPalette.NAME_SET, Component.text(message)));
                    finish();
                }
                return;
            }

            if (session.mode() == Mode.DESCRIPTION) {
                if (message.length() > maxDescriptionLength) {
                    player.sendMessage(Messages.component(
                            MessageKeys.BlockPalette.DESCRIPTION_TOO_LONG,
                            Component.text(maxDescriptionLength)));
                    return;
                }
                String description = message.isEmpty() ? "" : message;
                session.setDescription(description);
                String descDisplay = description.isEmpty()
                        ? Messages.plain(player, MessageKeys.BlockPalette.NONE)
                        : description;
                player.sendMessage(Messages.component(
                        MessageKeys.BlockPalette.DESCRIPTION_SET, Component.text(descDisplay)));
                finish();
            }
        }

        private void finish() {
            unregister(activeChat, session);
            Bukkit.getScheduler().runTask(session.plugin(), session::reopenMenu);
        }
    }
}
