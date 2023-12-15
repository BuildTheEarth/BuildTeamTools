package net.buildtheearth.modules.navigator.menu;

import com.alpsbte.alpslib.utils.item.ItemBuilder;
import net.buildtheearth.modules.network.model.Continent;
import net.buildtheearth.modules.network.model.Country;
import net.buildtheearth.modules.utils.ChatHelper;
import net.buildtheearth.modules.utils.Item;
import net.buildtheearth.modules.utils.Utils;
import net.buildtheearth.modules.utils.menus.AbstractMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CountrySelectorMenu extends AbstractMenu {

    private final Continent continent;
    private int page;

    public CountrySelectorMenu(Continent continent, Player menuPlayer, int page) {
        super(5, continent.getLabel() + " - countries", menuPlayer);
        this.continent = continent;
        this.page = page;

        ChatHelper.logDebug("Continent in constructor: %s", continent);
    }

    @Override
    protected void setMenuItemsAsync() {
        if (continent == null) {
            ChatHelper.logDebug("Continent is null in setPreviewItems");
            return;
        }

        // Create the country items
        int slot = 9;
        int offset = (page == 1) ? 0 : 27 * (page - 1);

        List<Country> countries = this.continent.getCountries();
        if (countries == null) {
            ChatHelper.logDebug("Countries list is null in setPreviewItems");
            return;
        }

        countries.sort(Comparator.comparing(Country::isConnected).reversed().thenComparing(Country::getName));

        System.out.println("Continent in setPreviewItems: " + continent);

        int currentIndex = 0;
        for (Country country : countries) {
            if (currentIndex >= offset && slot < 36) {
                ArrayList<String> countryLore = new ArrayList<>(Collections.singletonList(ChatHelper.colorize(ChatColor.GRAY, ChatColor.GRAY, "Visit countries in %s", continent.getLabel())));
                getMenu().getSlot(slot).setItem(Item.createCustomHeadBase64(country.getHeadBase64(), country.getName(), countryLore));
                slot++;
            }
            currentIndex++;
        }

        if(!getMenu().getSlot(35).getItem(getMenuPlayer()).getType().equals(Material.AIR))
            getMenu().getSlot(44).setItem(Item.create(Material.ARROW, "Next Page"));
        if(page > 1) getMenu().getSlot(36).setItem(Item.create(Material.ARROW, "Previous Page"));

    }

    @Override
    protected void setItemClickEventsAsync() {
        int offset = (page == 1) ? 0 : 27 * (page - 1);
        int currentIndex = 0;

        for(int slot = 9; currentIndex >= offset && slot < 36;) {
            int finalCurrentIndex = currentIndex;
            getMenu().getSlot(slot).setClickHandler((clickPlayer, clickInformation) -> {
                ChatHelper.logDebug("Country clicked");
                clickPlayer.closeInventory();

                Country clickedCountry = continent.getCountries().get(finalCurrentIndex);
                ChatHelper.logDebug("%s", clickedCountry.getName());
                if(clickedCountry.isConnected()) {
                    Utils.sendPlayerToServer(clickPlayer, clickedCountry.getServerName());
                } else {
                    clickPlayer.sendMessage(ChatHelper.highlight("This country isn't connected to the network! Connect to %s instead.", clickedCountry.getIP()));
                }
            });
            currentIndex++;
            slot++;
        }

        getMenu().getSlot(36).setClickHandler((clickPlayer, clickInformation) -> {
            ChatHelper.logDebug("Prev page clicked");
            if(clickInformation.getClickedSlot().getItem(clickPlayer).getType().equals(Material.ARROW)) {
                clickPlayer.closeInventory();
                new CountrySelectorMenu(continent, clickPlayer, page--);
            }
        });

        getMenu().getSlot(44).setClickHandler((clickPlayer, clickInformation) -> {
            ChatHelper.logDebug("Next page clicked");
            if(clickInformation.getClickedSlot().getItem(clickPlayer).getType().equals(Material.ARROW)) {
                clickPlayer.closeInventory();
                new CountrySelectorMenu(continent, clickPlayer, page++);
            }
        });
    }



    @Override
    protected Mask getMask() {
        return BinaryMask.builder(getMenu())
                .item(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 7).setName(" ").build())
                .pattern("111111111")
                .pattern("000000000")
                .pattern("000000000")
                .pattern("000000000")
                .pattern("111111111")
                .build();
    }
}
