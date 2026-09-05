package net.buildtheearth.buildteamtools.modules.generator.components.rail.menu;

import com.alpsbte.alpslib.utils.item.Item;
import com.cryptomorin.xseries.XMaterial;
import net.buildtheearth.buildteamtools.utils.menus.AbstractMenu;
import net.buildtheearth.buildteamtools.utils.menus.BlockListMenu;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Lets the player pick a single block for one of the {@link RailBlockRole} slots
 * of a rail type draft, then returns to the editor menu.
 */
public class RailBlockPickerMenu extends BlockListMenu {

    private final RailTypeDraft draft;
    private final RailBlockRole role;

    RailBlockPickerMenu(Player player, RailTypeDraft draft, RailBlockRole role, boolean autoLoad) {
        super(player, role.getMenuTitle(), role.createChoices(), createParentMenu(player, draft, role), autoLoad);

        this.draft = draft;
        this.role = role;
    }

    @Override
    protected void setPaginatedItemClickEventsAsync(List<?> source) {
        List<ItemStack> itemStacks = source.stream().map(l -> (ItemStack) l).toList();
        int slot = 0;

        // Only one block can be picked per role, so selecting a block deselects the previous one.
        for (ItemStack ignored : itemStacks) {
            final int _slot = slot;
            getMenu().getSlot(_slot).setClickHandler((clickPlayer, clickInformation) -> {
                String type = Item.getUppercaseMaterialString(getMenu().getSlot(_slot).getItem(getMenuPlayer()));

                if (selectedMaterials.contains(type)) {
                    selectedMaterials.remove(type);
                } else {
                    selectedMaterials.clear();
                    selectedMaterials.add(type);
                }

                reloadMenuAsync();
            });
            slot++;
        }
    }

    @Override
    protected void setItemClickEventsAsync() {
        super.setItemClickEventsAsync();

        if (canProceed())
            getMenu().getSlot(NEXT_ITEM_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
                XMaterial material = Item.convertStringToXMaterial(selectedMaterials.getFirst());

                if (material == null)
                    return;

                role.apply(draft, material);

                clickPlayer.closeInventory();
                playSound(clickPlayer, Sound.UI_BUTTON_CLICK);

                if (role.isOverheadSetting())
                    new RailOverheadWireMenu(clickPlayer, draft, true);
                else
                    new RailTypeEditorMenu(clickPlayer, draft, true);
            });
    }

    private static AbstractMenu createParentMenu(Player player, RailTypeDraft draft, RailBlockRole role) {
        if (role.isOverheadSetting())
            return new RailOverheadWireMenu(player, draft, false);

        return new RailTypeEditorMenu(player, draft, false);
    }

    private void playSound(Player player, Sound sound) {
        player.playSound(player, sound, 1.0F, 1.0F);
    }
}
