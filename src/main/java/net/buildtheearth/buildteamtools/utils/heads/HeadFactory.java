package net.buildtheearth.buildteamtools.utils.heads;

import com.alpsbte.alpslib.utils.item.Item;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class HeadFactory {

    public static ItemStack head(HeadTexture headTexture, String name, ArrayList<String> lore) {
        return Item.createCustomHeadBase64(headTexture.getBase64(), name, lore);
    }

    public static ItemStack head(HeadTexture headTexture, String name) {
        return Item.createCustomHeadBase64(headTexture.getBase64(), name, null);
    }

    public static ItemStack number(HeadColorScheme color, int value, String name, ArrayList<String> lore) {
        String key = color.name() + "_" + value;
        HeadTexture texture;

        try {
            texture = HeadTexture.valueOf(key);
        } catch (IllegalArgumentException e) {
            texture = HeadTexture.valueOf(color.name() + "_BLANK");
        }
        return Item.createCustomHeadBase64(texture.getBase64(), name, lore);
    }

    public static ItemStack number(HeadColorScheme color, int value, String name) {
        return number(color, value, name, null);
    }

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

    public static ItemStack letter(LetterType type, char letter, String name) {
        return letter(type, letter, name, null);
    }
}
