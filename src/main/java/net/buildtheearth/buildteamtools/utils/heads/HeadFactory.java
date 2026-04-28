package net.buildtheearth.buildteamtools.utils.heads;

import com.alpsbte.alpslib.utils.item.Item;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

/**
 * Factory utility class for creating custom textured player heads.
 *
 * <p>This class provides a centralized API for generating {@link ItemStack} instances
 * based on predefined {@link HeadTexture} values. It supports:
 * <ul>
 *     <li>Direct texture-based head creation</li>
 *     <li>Colorized variants of heads (based on {@link HeadColor})</li>
 *     <li>Number-based heads</li>
 *     <li>Letter-based heads</li>
 *     <li>UI helper heads (plus/minus counters)</li>
 * </ul>
 *
 * <p>All methods delegate to {@link Item#createCustomHeadBase64(String, String, java.util.List)}
 * using base64 texture data stored in {@link HeadTexture}.
 */
public class HeadFactory {

    /**
     * Creates a custom head using a raw {@link HeadTexture}.
     *
     * @param headTexture the texture to use
     * @param name        display name of the item
     * @param lore        optional lore (can be null)
     * @return the created {@link ItemStack}
     */
    public static ItemStack head(HeadTexture headTexture, String name, ArrayList<String> lore) {
        return Item.createCustomHeadBase64(headTexture.getBase64(), name, lore);
    }

    /**
     * Creates a custom head using a raw {@link HeadTexture} without lore.
     *
     * @param headTexture the texture to use
     * @param name        display name of the item
     * @return the created {@link ItemStack}
     */
    public static ItemStack head(HeadTexture headTexture, String name) {
        return Item.createCustomHeadBase64(headTexture.getBase64(), name, null);
    }

    /**
     * Creates a colorized variant of a head texture.
     *
     * <p>This method attempts to resolve a texture by combining the provided
     * {@link HeadColor} and the base texture name. If the resolved texture
     * does not exist, it falls back to the corresponding BLANK texture.
     *
     * @param color       color scheme to apply
     * @param headTexture base texture to colorize
     * @param name        display name
     * @param lore        optional lore (can be null)
     * @return the created {@link ItemStack}
     */
    public static ItemStack colorizedHead(HeadColor color, HeadTexture headTexture, String name, ArrayList<String> lore) {
        String key = color.name() + "_" + headTexture.name().substring(color.name().length());
        HeadTexture texture;

        try {
            texture = HeadTexture.valueOf(key);
        } catch (IllegalArgumentException e) {
            texture = HeadTexture.valueOf(color.name() + "_BLANK");
        }

        return Item.createCustomHeadBase64(texture.getBase64(), name, lore);
    }

    /**
     * Creates a colorized head without lore.
     *
     * @param color       color scheme
     * @param headTexture base texture
     * @param name        display name
     * @return the created {@link ItemStack}
     */
    public static ItemStack colorizedHead(HeadColor color, HeadTexture headTexture, String name) {
        return colorizedHead(color, headTexture, name, null);
    }

    /**
     * Creates a numbered head based on a color scheme.
     *
     * <p>Resolves a texture using the pattern:
     * {@code COLOR_VALUE} (e.g. WHITE_5).
     * Falls back to a blank texture if the value is invalid.
     *
     * @param color color scheme
     * @param value numeric value (e.g. 0–20)
     * @param name  display name
     * @param lore  optional lore (can be null)
     * @return the created {@link ItemStack}
     */
    public static ItemStack number(HeadColor color, int value, String name, ArrayList<String> lore) {
        String key = color.name() + "_" + value;
        HeadTexture texture;

        try {
            texture = HeadTexture.valueOf(key);
        } catch (IllegalArgumentException e) {
            texture = HeadTexture.valueOf(color.name() + "_BLANK");
        }

        return Item.createCustomHeadBase64(texture.getBase64(), name, lore);
    }

    /**
     * Creates a numbered head without lore.
     *
     * @param color color scheme
     * @param value numeric value
     * @param name  display name
     * @return the created {@link ItemStack}
     */
    public static ItemStack number(HeadColor color, int value, String name) {
        return number(color, value, name, null);
    }

    /**
     * Creates a letter-based head (A–Z) using a given style.
     *
     * <p>Resolves textures using the pattern:
     * {@code TYPE_LETTER_X}. If the letter is invalid,
     * a QUESTION_MARK fallback texture is used.
     *
     * @param type  letter style/type (e.g. WOODEN, STONE)
     * @param letter character input (A–Z)
     * @param name   display name
     * @param lore   optional lore (can be null)
     * @return the created {@link ItemStack}
     */
    public static ItemStack letter(LetterType type, char letter, String name, ArrayList<String> lore) {
        String key = type.name() + "_LETTER_" + Character.toUpperCase(letter);
        HeadTexture texture;

        try {
            texture = HeadTexture.valueOf(key);
        } catch (Exception e) {
            texture = HeadTexture.valueOf(type.name() + "_LETTER_QUESTION_MARK");
        }

        return Item.createCustomHeadBase64(texture.getBase64(), name, lore);
    }

    /**
     * Creates a letter-based head without lore.
     *
     * @param type   letter style/type
     * @param letter character input
     * @param name   display name
     * @return the created {@link ItemStack}
     */
    public static ItemStack letter(LetterType type, char letter, String name) {
        return letter(type, letter, name, null);
    }

    /**
     * Creates a "+" counter button item.
     *
     * <p>If the value is already at or above the maximum,
     * a blank head is returned instead.
     *
     * @param sliderColor color scheme
     * @param name        display name base
     * @param value       current value
     * @param maxValue    maximum allowed value
     * @return plus button or blank item
     */
    public static ItemStack getCounterPlusItem(HeadColor sliderColor, String name, int value, int maxValue) {
        if (value >= maxValue)
            return colorizedHead(sliderColor, HeadTexture.WHITE_BLANK, " ");

        return colorizedHead(sliderColor, HeadTexture.WHITE_PLUS, "§a§l+ §e" + name);
    }

    /**
     * Creates a "-" counter button item.
     *
     * <p>If the value is already at or below the minimum,
     * a blank head is returned instead.
     *
     * @param sliderColor color scheme
     * @param name        display name base
     * @param value       current value
     * @param minValue    minimum allowed value
     * @return minus button or blank item
     */
    public static ItemStack getCounterMinusItem(HeadColor sliderColor, String name, int value, int minValue) {
        if (value <= minValue)
            return colorizedHead(sliderColor, HeadTexture.WHITE_BLANK, " ");

        return colorizedHead(sliderColor, HeadTexture.WHITE_MINUS, "§c§l- §e" + name);
    }
}