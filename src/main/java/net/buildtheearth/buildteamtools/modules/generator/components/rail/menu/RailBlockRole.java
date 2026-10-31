package net.buildtheearth.buildteamtools.modules.generator.components.rail.menu;

import com.cryptomorin.xseries.XMaterial;
import net.buildtheearth.buildteamtools.i18n.MessageKeys;
import lombok.Getter;
import net.buildtheearth.buildteamtools.utils.MenuItems;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.ArrayList;

/**
 * The block slots of a rail type that can be configured in the editor menu.
 * Each role provides its own predefined list of valid blocks to choose from.
 */
enum RailBlockRole {

    RAIL_BLOCK(MessageKeys.Generator.Rail.Menu.PICKER_RAIL_BLOCK) {
        @Override
        List<ItemStack> createChoices() {
            List<ItemStack> choices = new ArrayList<>();
            choices.add(new ItemStack(Material.ANVIL));
            choices.add(new ItemStack(Material.CHIPPED_ANVIL));
            choices.add(new ItemStack(Material.DAMAGED_ANVIL));
            choices.addAll(MenuItems.getBlocksByColor());
            return choices;
        }

        @Override
        void apply(RailTypeDraft draft, XMaterial material) {
            draft.setRailBlock(material);
        }
    },

    BLOCK_BELOW(MessageKeys.Generator.Rail.Menu.PICKER_BLOCK_BELOW) {
        @Override
        List<ItemStack> createChoices() {
            return MenuItems.getSolidBlocks();
        }

        @Override
        void apply(RailTypeDraft draft, XMaterial material) {
            draft.setBlocksBelow(List.of(material));
        }
    },

    SLEEPER_BLOCK(MessageKeys.Generator.Rail.Menu.PICKER_SLEEPER) {
        @Override
        List<ItemStack> createChoices() {
            return MenuItems.getSolidBlocks();
        }

        @Override
        void apply(RailTypeDraft draft, XMaterial material) {
            draft.setSleeperBlock(material);
        }
    },

    ICON(MessageKeys.Generator.Rail.Menu.PICKER_ICON) {
        @Override
        List<ItemStack> createChoices() {
            return MenuItems.getBlocksByColor();
        }

        @Override
        void apply(RailTypeDraft draft, XMaterial material) {
            draft.setIcon(material);
        }
    },

    OVERHEAD_POLE_BLOCK(MessageKeys.Generator.Rail.Menu.PICKER_POLE) {
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

    OVERHEAD_SUPPORT_BLOCK(MessageKeys.Generator.Rail.Menu.PICKER_SUPPORT) {
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

    OVERHEAD_WIRE_BLOCK(MessageKeys.Generator.Rail.Menu.PICKER_WIRE) {
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
    private final String menuTitleKey;

    RailBlockRole(String menuTitleKey) {
        this.menuTitleKey = menuTitleKey;
    }

    abstract List<ItemStack> createChoices();

    abstract void apply(RailTypeDraft draft, XMaterial material);

    List<XMaterial> getSelected(RailTypeDraft draft) {
        return switch (this) {
            case BLOCK_BELOW -> draft.getBlocksBelow();
            case RAIL_BLOCK -> draft.getRailBlocks();
            case SLEEPER_BLOCK -> draft.getSleeperBlocks();
            case ICON -> List.of(draft.getIcon());
            case OVERHEAD_POLE_BLOCK -> draft.getOverheadPoleBlocks();
            case OVERHEAD_SUPPORT_BLOCK -> draft.getOverheadSupportBlocks();
            case OVERHEAD_WIRE_BLOCK -> draft.getOverheadWireBlocks();
        };
    }

    void apply(RailTypeDraft draft, List<XMaterial> materials) {
        switch (this) {
            case BLOCK_BELOW -> draft.setBlocksBelow(List.copyOf(materials));
            case RAIL_BLOCK -> draft.setRailBlocks(List.copyOf(materials));
            case SLEEPER_BLOCK -> draft.setSleeperBlocks(List.copyOf(materials));
            case OVERHEAD_POLE_BLOCK -> draft.setOverheadPoleBlocks(List.copyOf(materials));
            case OVERHEAD_SUPPORT_BLOCK -> draft.setOverheadSupportBlocks(List.copyOf(materials));
            case OVERHEAD_WIRE_BLOCK -> draft.setOverheadWireBlocks(List.copyOf(materials));
            case ICON -> apply(draft, materials.getFirst());
        }
    }

    boolean isOverheadSetting() {
        return false;
    }

}
