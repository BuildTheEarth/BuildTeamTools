package net.buildtheearth.buildteamtools.utils;

import com.alpsbte.alpslib.utils.item.Item;
import net.buildtheearth.buildteamtools.utils.heads.HeadColorScheme;
import net.buildtheearth.buildteamtools.utils.heads.HeadTexture;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class HeadUtil {

    public static ItemStack getCounterPlusItem(HeadColorScheme sliderColor, String name, int value, int maxValue) {
        if (value >= maxValue)
            switch (sliderColor) {
                case WHITE:
                    return Item.createCustomHeadBase64(HeadTexture.WHITE_BLANK.getBase64(), " ", null);
                case LIGHT_GRAY:
                    return Item.createCustomHeadBase64(HeadTexture.LIGHT_GRAY_BLANK.getBase64(), " ", null);
            }

        switch (sliderColor) {
            default:
            case WHITE:
                return Item.createCustomHeadBase64(HeadTexture.WHITE_PLUS.getBase64(), "§a§l+ §e" + name, null);
            case LIGHT_GRAY:
                return Item.createCustomHeadBase64(HeadTexture.LIGHT_GRAY_PLUS.getBase64(), "§a§l+ §e" + name, null);
        }
    }

    public static ItemStack getCounterMinusItem(HeadColorScheme sliderColor, String name, int value, int minValue) {
        if (value <= minValue)
            switch (sliderColor) {
                case WHITE:
                    return Item.createCustomHeadBase64(HeadTexture.WHITE_BLANK.getBase64(), " ", null);
                case LIGHT_GRAY:
                    return Item.createCustomHeadBase64(HeadTexture.LIGHT_GRAY_BLANK.getBase64(), " ", null);
            }

        switch (sliderColor) {
            default:
            case WHITE:
                return Item.createCustomHeadBase64(HeadTexture.WHITE_MINUS.getBase64(), "§c§l- §e" + name, null);
            case LIGHT_GRAY:
                return Item.createCustomHeadBase64(HeadTexture.LIGHT_GRAY_MINUS.getBase64(), "§c§l- §e" + name, null);
        }
    }

    public static ItemStack getXItem(HeadColorScheme sliderColor, String name) {
        switch (sliderColor) {
            default:
            case WHITE:
                return Item.createCustomHeadBase64(HeadTexture.WHITE_X.getBase64(), name, null);
            case LIGHT_GRAY:
                return Item.createCustomHeadBase64(HeadTexture.LIGHT_GRAY_X.getBase64(), name, null);
        }
    }

    public static ItemStack getBlankItem(HeadColorScheme sliderColor, String name) {
        switch (sliderColor) {
            default:
            case WHITE:
                return Item.createCustomHeadBase64(HeadTexture.WHITE_BLANK.getBase64(), name, null);
            case LIGHT_GRAY:
                return Item.createCustomHeadBase64(HeadTexture.LIGHT_GRAY_BLANK.getBase64(), name, null);
        }
    }
}
