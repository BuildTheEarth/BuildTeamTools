package net.buildtheearth.buildteamtools.modules.navigation.menu;

import com.alpsbte.alpslib.utils.ChatHelper;
import com.alpsbte.alpslib.utils.item.Item;
import lombok.NonNull;
import net.buildtheearth.buildteamtools.i18n.MessageKeys;
import net.buildtheearth.buildteamtools.i18n.Messages;
import net.buildtheearth.buildteamtools.modules.navigation.NavUtils;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.WarpsComponent;
import net.buildtheearth.buildteamtools.modules.network.NetworkModule;
import net.buildtheearth.buildteamtools.modules.network.model.BuildTeam;
import net.buildtheearth.buildteamtools.modules.network.model.Continent;
import net.buildtheearth.buildteamtools.modules.network.model.Permissions;
import net.buildtheearth.buildteamtools.modules.network.model.Region;
import net.buildtheearth.buildteamtools.utils.ItemComponents;
import net.buildtheearth.buildteamtools.utils.MenuItems;
import net.buildtheearth.buildteamtools.utils.menus.AbstractPaginatedMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CountrySelectorMenu extends AbstractPaginatedMenu {

    private final List<Region> regions;

    public static final int BACK_ITEM_SLOT = 27;
    public static final int SWITCH_PAGE_ITEM_SLOT = 34;

    public CountrySelectorMenu(Player menuPlayer, @NonNull Continent continent, boolean autoLoad) {
        super(4, 3, Messages.component(MessageKeys.Navigation.CountryMenu.TITLE, Component.text(continent.getLabel())),
                menuPlayer, autoLoad);
        this.regions = new ArrayList<>(continent.getCountries());

        // Add USA region to North America because it is being built by multiple teams
        if (continent == Continent.NORTH_AMERICA)
            regions.add(
                    new Region("USA",
                            Continent.NORTH_AMERICA,
                            new BuildTeam("USA", null, null, "3 Teams", null, false, false, false, ""),
                            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGNhYzk3NzRkYTEyMTcyNDg1MzJjZTE0N2Y3ODMxZjY3YTEyZmRjY2ExY2YwY2I0YjM4NDhkZTZiYzk0YjQifX19"
                            , 9372610, "US", "USA"
                    )
            );

        if (!regions.isEmpty()) {
            // Sort countries by area
            regions.sort(Comparator.comparing(Region::getArea).reversed());

            // Remove all regions that don't have a build team
            regions.removeAll(regions.stream().filter(region ->
                    region.getBuildTeam() == null
                            || region.getHeadBase64() == null
                            || region.getBuildTeam().getID() == null
                            || (
                            NetworkModule.getInstance().getBuildTeam() != null
                                    && region.getBuildTeam().getID().equals(NetworkModule.getInstance().getBuildTeam().getID())
                    )
            ).toList());
        }

        ChatHelper.logDebug("Continent in constructor: %s", continent);
    }

    @Override
    protected void setPreviewItems() {
        setBackItem(BACK_ITEM_SLOT, new ExploreMenu(getMenuPlayer(), false));

        // If there are more than 27 countries, add the switch page items, otherwise add glass panes
        if (regions.size() > 27)
            setSwitchPageItems(SWITCH_PAGE_ITEM_SLOT);
        else
            for (int i = -1; i < 2; i++)
                getMenu().getSlot(SWITCH_PAGE_ITEM_SLOT + i).setItem(MenuItems.ITEM_BACKGROUND);

        super.setPreviewItems();
    }

    @Override
    protected void setPaginatedPreviewItems(@NotNull List<?> source) {
        List<Region> countries = source.stream().map(l -> (Region) l).toList();
        Player player = getMenuPlayer();

        // Create the country items
        int slot = 0;

        for (Region region : countries) {
            List<Component> countryLore = new ArrayList<>(Messages.loreComponents(
                    player,
                    MessageKeys.Navigation.CountryMenu.ITEM_LORE,
                    Component.text(region.getBuildTeam().getBlankName()),
                    Component.text(formatArea(region.getArea()))));
            if (!region.getBuildTeam().getWarpGroups().isEmpty()) {
                countryLore.addAll(Messages.loreComponents(player, MessageKeys.Navigation.CountryMenu.ITEM_LORE_WARPS));
            }
            String headBase64 = region.getHeadBase64() == null
                    ? "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmFkYzA0OGE3Y2U3OGY3ZGFkNzJhMDdkYTI3ZDg1YzA5MTY4ODFlNTUyMmVlZWQxZTNkYWYyMTdhMzhjMWEifX19"
                    : region.getHeadBase64();
            getMenu().getSlot(slot).setItem(
                    ItemComponents.named(
                            Item.createCustomHeadBase64(headBase64, " ", null),
                            Messages.render(player, MessageKeys.Navigation.CountryMenu.ITEM_NAME, Component.text(region.getName())),
                            countryLore)
            );
            slot++;
        }
    }

    @Override
    protected void setMenuItemsAsync(/* No async Items set */) {
    }

    @Override
    protected void setItemClickEventsAsync() {
        if (regions.size() > 27)
            setSwitchPageItemClickEvents(SWITCH_PAGE_ITEM_SLOT);
    }

    @Override
    protected void setPaginatedItemClickEventsAsync(@NotNull List<?> source) {
        List<Region> countries = source.stream().map(l -> (Region) l).toList();

        int slot = 0;
        for (Region clickedRegion : countries) {
            final int _slot = slot;
            getMenu().getSlot(_slot).setClickHandler((clickPlayer, clickInformation) -> {
                clickPlayer.closeInventory();

                ChatHelper.logDebug("Clicked Region: %s",
                        clickedRegion.getName() + " (" + clickedRegion.getCountryCodeCca3() + ")");

                if (clickedRegion.getCountryCodeCca3().equalsIgnoreCase("USA")) {
                    new StateSelectorMenu(clickPlayer, clickedRegion.getContinent(), true);
                    return;
                }

                if (clickInformation.getClickType().isRightClick() &&
                        clickPlayer.hasPermission(Permissions.WARP_USE) &&
                        !clickedRegion.getBuildTeam().getWarpGroups().isEmpty()) {
                    WarpsComponent.openWarpMenu(clickPlayer, clickedRegion.getBuildTeam(), this);
                } else {
                    NavUtils.switchToTeam(clickedRegion.getBuildTeam(), clickPlayer);
                }
            });
            slot++;
        }
    }

    @Override
    protected Mask getMask() {
        return BinaryMask.builder(getMenu())
                .item(MenuItems.ITEM_BACKGROUND)
                .pattern(BinaryMask.EMPTY_PATTERN)
                .pattern(BinaryMask.EMPTY_PATTERN)
                .pattern(BinaryMask.EMPTY_PATTERN)
                .pattern("011111000")
                .build();
    }

    @Override
    protected List<?> getSource() {
        return regions;
    }

    @Override
    protected void setPaginatedMenuItemsAsync(List<?> source) {

    }

    /**
     * Converts an area in square meters to a string with dot notation starting from the right every 3 digits.
     */
    public static String formatArea(double area) {
        String areaStr = String.valueOf((int) area);
        StringBuilder formattedArea = new StringBuilder();

        int length = areaStr.length();
        for (int i = 0; i < length; i++) {
            if (i > 0 && (length - i) % 3 == 0) {
                formattedArea.append(".");
            }
            formattedArea.append(areaStr.charAt(i));
        }

        return formattedArea.toString();
    }
}
