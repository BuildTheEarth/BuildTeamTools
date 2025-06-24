package net.buildtheearth.modules.common.commands;

import li.cinnazeyy.langlibs.core.LangLibAPI;
import net.buildtheearth.utils.lang.LangUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LanguageCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {

        if (args.length < 1) {
            commandSender.sendMessage("Available Languages:");
            for (int i = 0; i < LangUtil.getInstance().languageFiles.length; i++) {
                commandSender.sendMessage(LangUtil.getInstance().languageFiles[i].getLanguage().toString());
            }
            commandSender.sendMessage("To set a Language Use:");
            return false;
        }

        if (args.length > 1) {
            commandSender.sendMessage("Illegal Usage");
            return false;
        } else {
            LangLibAPI.setPlayerLang((Player) commandSender, args[0]);
            commandSender.sendMessage(LangLibAPI.getPlayerLang(((Player) commandSender).getUniqueId()));
            return true;
        }
    }

}
