package net.buildtheearth.buildteamtools.modules.generator.components.rail.menu;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import net.buildtheearth.buildteamtools.utils.MenuItems;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * The block slots of a rail type that can be configured in the editor menu.
 * Each role provides its own predefined list of valid blocks to choose from.
 */
enum RailBlockRole {

    RAIL_BLOCK("Choose a Rail Block") {
        @Override
        List<ItemStack> createChoices() {
            return MenuItems.getBlocksByColor();
        }

        @Override
        void apply(RailTypeDraft draft, XMaterial material) {
            draft.setRailBlock(material);
        }
    },

    BLOCK_BELOW("Choose a Block Below the Rails") {
        @Override
        List<ItemStack> createChoices() {
            return MenuItems.getSolidBlocks();
        }

        @Override
        void apply(RailTypeDraft draft, XMaterial material) {
            draft.setBlockBelow(material);
        }
    },

    SLEEPER_BLOCK("Choose a Sleeper Block") {
        @Override
        List<ItemStack> createChoices() {
            return MenuItems.getSolidBlocks();
        }

        @Override
        void apply(RailTypeDraft draft, XMaterial material) {
            draft.setSleeperBlock(material);
        }
    },

    ICON("Choose a Rail Type Icon") {
        @Override
        List<ItemStack> createChoices() {
            return MenuItems.getBlocksByColor();
        }

        @Override
        void apply(RailTypeDraft draft, XMaterial material) {
            draft.setIcon(material);
        }
    },

    OVERHEAD_POLE_BLOCK("Choose an Overhead Pole Block") {
        @Override
        List<ItemStack> createChoices() {
            return MenuItems.getBlocksByColor();
        }

        @Override
        void apply(RailTypeDraft draft, XMaterial material) {
            draft.setOverheadPoleBlock(material);
        }

        @Override
        boolean isOverheadSetting() {
            return true;
        }
    },

    OVERHEAD_SUPPORT_BLOCK("Choose a Top Support Block") {
        @Override
        List<ItemStack> createChoices() {
            return MenuItems.getBlocksByColor();
        }

        @Override
        void apply(RailTypeDraft draft, XMaterial material) {
            draft.setOverheadSupportBlock(material);
        }

        @Override
        boolean isOverheadSetting() {
            return true;
        }
    },

    OVERHEAD_WIRE_BLOCK("Choose an Overhead Wire Block") {
        @Override
        List<ItemStack> createChoices() {
            return MenuItems.getBlocksByColor();
        }

        @Override
        void apply(RailTypeDraft draft, XMaterial material) {
            draft.setOverheadWireBlock(material);
        }

        @Override
        boolean isOverheadSetting() {
            return true;
        }
    };

    @Getter
    private final String menuTitle;

    RailBlockRole(String menuTitle) {
        this.menuTitle = menuTitle;
    }

    abstract List<ItemStack> createChoices();

    abstract void apply(RailTypeDraft draft, XMaterial material);

    boolean isOverheadSetting() {
        return false;
    }

}
