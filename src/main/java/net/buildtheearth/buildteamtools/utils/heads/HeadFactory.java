package net.buildtheearth.buildteamtools.utils.heads;

import com.alpsbte.alpslib.utils.item.Item;
import net.buildtheearth.buildteamtools.utils.ItemComponents;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Factory utility class for creating custom textured player heads.
 *
 * <p>Skull textures still come from AlpsLib {@link Item#createCustomHeadBase64}; display names and
 * lore are applied as Adventure components via {@link ItemComponents}.
 */
public class HeadFactory {

    private static ItemStack baseHead(HeadTexture headTexture) {
        return Item.createCustomHeadBase64(headTexture.getBase64(), " ", null);
    }

    public static ItemStack head(HeadTexture headTexture, Component name, @Nullable List<Component> lore) {
        return ItemComponents.named(baseHead(headTexture), name, lore);
    }

    public static ItemStack head(HeadTexture headTexture, Component name) {
        return head(headTexture, name, null);
    }

    /** Blank placeholder head (no meaningful name). */
    public static ItemStack blank(HeadTexture headTexture) {
        return baseHead(headTexture);
    }

    public static ItemStack colorizedHead(
            HeadColor color,
            HeadTexture headTexture,
            Component name,
            @Nullable List<Component> lore
    ) {
        return head(resolveColorizedTexture(color, headTexture), name, lore);
    }

    public static ItemStack colorizedHead(HeadColor color, HeadTexture headTexture, Component name) {
        return colorizedHead(color, headTexture, name, null);
    }

    public static ItemStack colorizedBlank(HeadColor color, HeadTexture headTexture) {
        return blank(resolveColorizedTexture(color, headTexture));
    }

    public static ItemStack number(HeadColor color, int value, Component name, @Nullable List<Component> lore) {
        String key = color.name() + "_" + value;
        HeadTexture texture;
        try {
            texture = HeadTexture.valueOf(key);
        } catch (IllegalArgumentException e) {
            texture = HeadTexture.valueOf(color.name() + "_BLANK");
        }
        return head(texture, name, lore);
    }

    public static ItemStack number(HeadColor color, int value, Component name) {
        return number(color, value, name, null);
    }

    public static ItemStack letter(LetterType type, char letter, Component name, @Nullable List<Component> lore) {
        String key = type.name() + "_LETTER_" + Character.toUpperCase(letter);
        HeadTexture texture;
        try {
            texture = HeadTexture.valueOf(key);
        } catch (Exception e) {
            texture = HeadTexture.valueOf(type.name() + "_LETTER_QUESTION_MARK");
        }
        return head(texture, name, lore);
    }

    public static ItemStack letter(LetterType type, char letter, Component name) {
        return letter(type, letter, name, null);
    }

    public static ItemStack getCounterPlusItem(HeadColor sliderColor, Component name, int value, int maxValue) {
        if (value >= maxValue) {
            return colorizedBlank(sliderColor, HeadTexture.WHITE_BLANK);
        }
        return colorizedHead(sliderColor, HeadTexture.WHITE_PLUS, name);
    }

    public static ItemStack getCounterMinusItem(HeadColor sliderColor, Component name, int value, int minValue) {
        if (value <= minValue) {
            return colorizedBlank(sliderColor, HeadTexture.WHITE_BLANK);
        }
        return colorizedHead(sliderColor, HeadTexture.WHITE_MINUS, name);
    }

    private static HeadTexture resolveColorizedTexture(HeadColor color, HeadTexture headTexture) {
        String textureName = headTexture.name();
        for (HeadColor c : HeadColor.values()) {
            String prefix = c.name() + "_";
            if (textureName.startsWith(prefix)) {
                String suffix = textureName.substring(prefix.length());
                String key = color.name() + "_" + suffix;
                try {
                    return HeadTexture.valueOf(key);
                } catch (IllegalArgumentException ignored) {
                    break;
                }
            }
        }
        return HeadTexture.valueOf(color.name() + "_BLANK");
    }
}
