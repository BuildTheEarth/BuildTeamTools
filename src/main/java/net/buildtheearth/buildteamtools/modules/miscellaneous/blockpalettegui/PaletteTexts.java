package net.buildtheearth.buildteamtools.modules.miscellaneous.blockpalettegui;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;

import java.util.Locale;

/**
 * Shared string/material helpers for block-palette menus.
 */
final class PaletteTexts {

    private PaletteTexts() {
    }

    static String toTitleCase(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        String[] parts = input.trim().toLowerCase(Locale.ROOT).replace('_', ' ').split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) {
                continue;
            }
            sb.append(Character.toUpperCase(part.charAt(0)))
                    .append(part.length() > 1 ? part.substring(1) : "")
                    .append(' ');
        }
        return sb.toString().trim();
    }

    static Material safeMat(XMaterial xmat, Material fallback) {
        try {
            Material material = xmat.get();
            return material != null ? material : fallback;
        } catch (Exception ignored) {
            return fallback;
        }
    }
}
