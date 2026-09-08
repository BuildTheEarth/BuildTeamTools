package net.buildtheearth.buildteamtools.modules.generator.components.rail.menu;

import com.alpsbte.alpslib.utils.ChatHelper;
import com.alpsbte.alpslib.utils.item.Item;
import com.cryptomorin.xseries.XMaterial;
import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.modules.generator.GeneratorModule;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.Rail;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.RailFlag;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.RailSettings;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.configuration.RailType;
import net.buildtheearth.buildteamtools.modules.generator.menu.GeneratorMenu;
import net.buildtheearth.buildteamtools.modules.generator.model.Settings;
import net.buildtheearth.buildteamtools.modules.network.model.Permissions;
import net.buildtheearth.buildteamtools.utils.heads.HeadFactory;
import net.buildtheearth.buildteamtools.utils.heads.HeadTexture;
import net.buildtheearth.buildteamtools.utils.menus.NameListMenu;
import net.wesjd.anvilgui.AnvilGUI;
import net.kyori.adventure.text.format.NamedTextColor;
import org.apache.commons.lang3.tuple.MutablePair;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

public class RailTypeMenu extends NameListMenu {

    public static final String RAIL_TYPE_INV_NAME = "Choose a Rail Type";

    // Slots 30-32 belong to the page switcher, so the extra buttons sit next to it.
    private static final int SEARCH_ITEM_SLOT = 28;
    private static final int CREATE_ITEM_SLOT = 29;
    private static final int RELOAD_ITEM_SLOT = 33;
    private static final int BULK_DELETE_ITEM_SLOT = 34;

    private final String searchQuery;
    private final Set<String> selectedForDeletion;

    public RailTypeMenu(Player player, boolean autoLoad) {
        this(player, "", Set.of(), autoLoad);
    }

    private RailTypeMenu(Player player, String searchQuery, Collection<String> selectedForDeletion, boolean autoLoad) {
        super(player, RAIL_TYPE_INV_NAME, getRailTypes(player, searchQuery), new GeneratorMenu(player, false), autoLoad);
        this.searchQuery = searchQuery;
        this.selectedForDeletion = new LinkedHashSet<>(selectedForDeletion);
        preselectCurrentRailType(player);
    }

    private void preselectCurrentRailType(Player player) {
        Rail rail = GeneratorModule.getInstance().getRail();

        if (rail == null)
            return;

        Settings settings = rail.getPlayerSettings().get(player.getUniqueId());

        if (!(settings instanceof RailSettings railSettings))
            return;

        Object value = railSettings.getValues().get(RailFlag.RAIL_TYPE);

        if (value instanceof RailType railType && RailType.byString(railType.getIdentifier()) != null)
            selectedNames.add(railType.getIdentifier());
        else if (value instanceof String identifier && RailType.byString(identifier) != null)
            selectedNames.add(identifier);
    }

    private static @NonNull List<MutablePair<ItemStack, String>> getRailTypes(Player player, String searchQuery) {
        List<MutablePair<ItemStack, String>> railTypes = new ArrayList<>();
        Rail rail = GeneratorModule.getInstance().getRail();

        if (rail == null)
            return railTypes;

        String normalizedQuery = searchQuery.trim().toLowerCase(Locale.ROOT);

        for (RailType railType : rail.getRailTypeManager().getRailTypes()) {
            if (!normalizedQuery.isEmpty()
                    && !railType.getIdentifier().toLowerCase(Locale.ROOT).contains(normalizedQuery)
                    && !railType.getDisplayName().toLowerCase(Locale.ROOT).contains(normalizedQuery))
                continue;

            railTypes.add(new MutablePair<>(RailTypeMenuItems.createRailTypeItem(railType, player), railType.getIdentifier()));
        }

        return railTypes;
    }

    static String formatMaterials(List<XMaterial> materials) {
        return RailTypeMenuItems.formatMaterials(materials);
    }

    static String formatMaterial(@Nullable XMaterial material) {
        return RailTypeMenuItems.formatMaterial(material);
    }

    @Override
    protected void setPreviewItems() {
        super.setPreviewItems();

        setRailTypePageItems();

        getMenu().getSlot(SEARCH_ITEM_SLOT).setItem(Item.create(
                Objects.requireNonNull(XMaterial.COMPASS.get()),
                yellow("Search Rail Types"),
                List.of(
                        gray("Current filter: ") + white(searchQuery.isBlank() ? "All" : searchQuery),
                        gray("Left-click to search."),
                        gray("Right-click to clear the filter.")
                )
        ));

        if (getMenuPlayer().hasPermission(Permissions.RAIL_TYPE_CREATE))
            getMenu().getSlot(CREATE_ITEM_SLOT).setItem(Item.create(
                    Objects.requireNonNull(XMaterial.NETHER_STAR.get()),
                    green("Create a Rail Type"),
                    List.of(gray("Configure and save a custom rail type."))
                ));

        if (getMenuPlayer().hasPermission(Permissions.RAIL_TYPE_EDIT))
            getMenu().getSlot(RELOAD_ITEM_SLOT).setItem(Item.create(
                    Objects.requireNonNull(XMaterial.CLOCK.get()),
                    yellow("Reload Rail Types"),
                    List.of(
                            gray("Re-reads rail-types.yml from disk,"),
                            gray("so you can test changes without a restart.")
                    )
                ));

        if (getMenuPlayer().hasPermission(Permissions.RAIL_TYPE_DELETE))
            getMenu().getSlot(BULK_DELETE_ITEM_SLOT).setItem(Item.create(
                    Objects.requireNonNull((selectedForDeletion.isEmpty() ? XMaterial.GRAY_DYE : XMaterial.RED_DYE).get()),
                    selectedForDeletion.isEmpty() ? gray("Bulk Delete") : red("Delete Selected Rail Types"),
                    List.of(
                            gray("Selected: ") + white(String.valueOf(selectedForDeletion.size())),
                            gray("Shift+Left-Click custom types to select them."),
                            selectedForDeletion.isEmpty()
                                    ? darkGray("No custom rail types selected.")
                                    : red("Click to permanently delete all selected types.")
                    )
                ));
    }

    @Override
    protected void setPaginatedPreviewItems(@NonNull List<?> source) {
        List<MutablePair<ItemStack, String>> pagItems = getPageItems(source);
        int slot = 0;

        // Render the selection glow on a copy so deselected items lose their glow again.
        for (MutablePair<ItemStack, String> item : pagItems) {
            RailType currentType = RailType.byString(item.getRight());
            ItemStack displayedItem = currentType == null ? item.getLeft()
                    : RailTypeMenuItems.createRailTypeItem(currentType, getMenuPlayer());

            if (selectedNames.contains(item.getRight()))
                displayedItem = RailTypeMenuItems.addSelectionGlow(displayedItem);

            if (getMenuPlayer().hasPermission(Permissions.RAIL_TYPE_DELETE)
                    && selectedForDeletion.contains(item.getRight()))
                displayedItem = RailTypeMenuItems.addDeletionMarker(displayedItem);

            getMenu().getSlot(slot).setItem(displayedItem);
            slot++;
        }
    }

    @Override
    protected void setPaginatedItemClickEventsAsync(@NonNull List<?> source) {
        List<MutablePair<ItemStack, String>> pagItems = getPageItems(source);
        int slot = 0;

        // Rail types are mutually exclusive, so selecting one deselects the previous one.
        for (MutablePair<ItemStack, String> item : pagItems) {
            final int _slot = slot;
            getMenu().getSlot(_slot).setClickHandler((clickPlayer, clickInformation) -> {
                String type = item.getRight().toLowerCase();
                RailType railType = RailType.byString(type);
                if (railType == null)
                    return;

                if (clickInformation.getClickType() == ClickType.SHIFT_LEFT
                        && !railType.isBuiltIn() && clickPlayer.hasPermission(Permissions.RAIL_TYPE_DELETE)) {
                    toggleBulkDeleteSelection(clickPlayer, type);
                    return;
                }

                if (clickInformation.getClickType() == ClickType.SHIFT_RIGHT
                        && !railType.isBuiltIn() && clickPlayer.hasPermission(Permissions.RAIL_TYPE_DELETE)) {
                    deleteCustomRailType(clickPlayer, type);
                    return;
                }

                if (clickInformation.getClickType() == ClickType.DROP
                        && clickPlayer.hasPermission(Permissions.RAIL_TYPE_CREATE)) {
                    openEditorForRailType(clickPlayer, type, true);
                    return;
                }

                if (clickInformation.getClickType() == ClickType.RIGHT
                        && clickPlayer.hasPermission(railType.isBuiltIn()
                        ? Permissions.RAIL_TYPE_CREATE : Permissions.RAIL_TYPE_EDIT)) {
                    openEditorForRailType(clickPlayer, type, railType.isBuiltIn());
                    return;
                }

                if (selectedNames.contains(type)) {
                    selectedNames.remove(type);
                    clickPlayer.sendMessage(ChatHelper.getStandardComponent(
                            true,
                            "Deselected rail type '%s'.",
                            getRailTypeDisplayName(type)
                    ));
                    playSound(clickPlayer, Sound.UI_BUTTON_CLICK);
                } else {
                    selectedNames.clear();
                    selectedNames.add(type);
                    clickPlayer.sendMessage(ChatHelper.getStandardComponent(
                            true,
                            "Selected rail type '%s'.",
                            getRailTypeDisplayName(type)
                    ));
                    playSound(clickPlayer, Sound.UI_BUTTON_CLICK);
                }

                reloadMenuAsync();
            });
            slot++;
        }
    }

    private List<MutablePair<ItemStack, String>> getPageItems(List<?> source) {
        List<MutablePair<ItemStack, String>> items = new ArrayList<>();

        for (Object item : source) {
            if (item instanceof MutablePair<?, ?> pair
                    && pair.getLeft() instanceof ItemStack itemStack
                    && pair.getRight() instanceof String identifier)
                items.add(new MutablePair<>(itemStack, identifier));
        }

        return items;
    }

    private String getRailTypeDisplayName(String identifier) {
        Rail rail = GeneratorModule.getInstance().getRail();

        if (rail == null)
            return identifier;

        RailType railType = rail.getRailTypeManager().byString(identifier);
        return railType == null ? identifier : railType.getDisplayName();
    }

    private void openEditorForRailType(Player clickPlayer, String identifier, boolean copy) {
        Rail rail = GeneratorModule.getInstance().getRail();

        if (rail == null)
            return;

        RailType railType = rail.getRailTypeManager().byString(identifier);

        if (railType == null) {
            playSound(clickPlayer, Sound.ENTITY_ITEM_BREAK);
            return;
        }

        String permission = copy ? Permissions.RAIL_TYPE_CREATE : Permissions.RAIL_TYPE_EDIT;

        if (!Permissions.checkPermission(clickPlayer, permission))
            return;

        clickPlayer.closeInventory();
        playSound(clickPlayer, Sound.UI_BUTTON_CLICK);
        clickPlayer.sendMessage(ChatHelper.getStandardComponent(
                true,
                copy
                        ? "Opening an editable copy of rail type '%s'."
                        : "Editing rail type '%s'.",
                railType.getIdentifier()
        ));

        // Built-in types cannot be overwritten, so editing one saves an editable copy instead.
        new RailTypeEditorMenu(clickPlayer, RailTypeDraft.from(railType, !copy), true);
    }

    private void deleteCustomRailType(Player clickPlayer, String identifier) {
        if (!Permissions.checkPermission(clickPlayer, Permissions.RAIL_TYPE_DELETE))
            return;

        Rail rail = GeneratorModule.getInstance().getRail();

        if (rail == null)
            return;

        RailType railType = rail.getRailTypeManager().byString(identifier);

        if (railType == null) {
            clickPlayer.sendMessage(ChatHelper.PREFIX_COMPONENT.append(ChatHelper.getErrorComponent(
                    "This rail type no longer exists."
            )));
            playSound(clickPlayer, Sound.ENTITY_ITEM_BREAK);
            return;
        }

        if (railType.isBuiltIn()) {
            clickPlayer.sendMessage(ChatHelper.PREFIX_COMPONENT.append(ChatHelper.getErrorComponent(
                    "Built-in rail types cannot be deleted. Right-click to create an editable copy."
            )));
            playSound(clickPlayer, Sound.ENTITY_ITEM_BREAK);
            return;
        }

        String error = rail.getRailTypeManager().deleteRailType(railType.getIdentifier());

        if (error != null) {
            clickPlayer.sendMessage(ChatHelper.PREFIX_COMPONENT.append(ChatHelper.getErrorComponent(error)));
            playSound(clickPlayer, Sound.ENTITY_ITEM_BREAK);
            return;
        }

        selectedNames.remove(railType.getIdentifier());
        clickPlayer.sendMessage(ChatHelper.getStandardComponent(true, "Deleted rail type '%s'.", railType.getIdentifier()));
        playSound(clickPlayer, Sound.UI_BUTTON_CLICK);

        new RailTypeMenu(clickPlayer, true);
    }

    private void toggleBulkDeleteSelection(Player clickPlayer, String identifier) {
        if (!clickPlayer.hasPermission(Permissions.RAIL_TYPE_DELETE))
            return;

        Rail rail = GeneratorModule.getInstance().getRail();

        if (rail == null)
            return;

        RailType railType = rail.getRailTypeManager().byString(identifier);

        if (railType == null || railType.isBuiltIn()) {
            clickPlayer.sendMessage(ChatHelper.PREFIX_COMPONENT.append(ChatHelper.getErrorComponent(
                    "Only custom rail types can be selected for deletion."
            )));
            playSound(clickPlayer, Sound.ENTITY_ITEM_BREAK);
            return;
        }

        if (selectedForDeletion.remove(identifier))
            clickPlayer.sendMessage(ChatHelper.getStandardComponent(
                    true,
                    "Removed rail type '%s' from bulk deletion.",
                    identifier
            ));
        else
            selectedForDeletion.add(identifier);

        playSound(clickPlayer, Sound.UI_BUTTON_CLICK);
        reloadMenuAsync(false);
    }

    private void deleteSelectedRailTypes(Player clickPlayer) {
        if (selectedForDeletion.isEmpty()) {
            playSound(clickPlayer, Sound.ENTITY_ITEM_BREAK);
            return;
        }

        if (!Permissions.checkPermission(clickPlayer, Permissions.RAIL_TYPE_DELETE))
            return;

        Rail rail = GeneratorModule.getInstance().getRail();

        if (rail == null)
            return;

        int amount = selectedForDeletion.size();
        String error = rail.getRailTypeManager().deleteRailTypes(selectedForDeletion);

        if (error != null) {
            clickPlayer.sendMessage(ChatHelper.PREFIX_COMPONENT.append(ChatHelper.getErrorComponent(error)));
            playSound(clickPlayer, Sound.ENTITY_ITEM_BREAK);
            return;
        }

        selectedNames.removeAll(selectedForDeletion);
        selectedForDeletion.clear();
        clickPlayer.sendMessage(ChatHelper.getStandardComponent(true, "Deleted %s custom rail types.", amount));
        playSound(clickPlayer, Sound.UI_BUTTON_CLICK);
        new RailTypeMenu(clickPlayer, searchQuery, selectedForDeletion, true);
    }

    @Override
    protected void setItemClickEventsAsync() {
        super.setItemClickEventsAsync();

        setRailTypePageClickEvents();

        getMenu().getSlot(SEARCH_ITEM_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
            if (clickInformation.getClickType() == ClickType.RIGHT) {
                playSound(clickPlayer, Sound.UI_BUTTON_CLICK);
                new RailTypeMenu(clickPlayer, "", selectedForDeletion, true);
                return;
            }

            openSearchEditor(clickPlayer);
        });

        getMenu().getSlot(CREATE_ITEM_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
            if (!clickPlayer.hasPermission(Permissions.RAIL_TYPE_CREATE))
                return;

            clickPlayer.closeInventory();
            playSound(clickPlayer, Sound.UI_BUTTON_CLICK);
            clickPlayer.sendMessage(ChatHelper.getStandardComponent(true, "Creating a new rail type."));

            new RailTypeEditorMenu(clickPlayer, new RailTypeDraft(), true);
        });

        getMenu().getSlot(RELOAD_ITEM_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
            if (!clickPlayer.hasPermission(Permissions.RAIL_TYPE_EDIT))
                return;

            Rail rail = GeneratorModule.getInstance().getRail();

            if (rail == null)
                return;

            rail.getRailTypeManager().reload();

            playSound(clickPlayer, Sound.UI_BUTTON_CLICK);
            clickPlayer.sendMessage(ChatHelper.getStandardComponent(
                    true,
                    "Reloaded %s rail types from rail-types.yml.",
                    rail.getRailTypeManager().getRailTypes().size()
            ));

            new RailTypeMenu(clickPlayer, searchQuery, selectedForDeletion, true);
        });

        getMenu().getSlot(BULK_DELETE_ITEM_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
            if (clickPlayer.hasPermission(Permissions.RAIL_TYPE_DELETE))
                deleteSelectedRailTypes(clickPlayer);
        });

        if (canProceed())
            getMenu().getSlot(NEXT_ITEM_SLOT).setClickHandler((clickPlayer, clickInformation) ->
                    generateWithSelectedType(clickPlayer));
    }

    private void generateWithSelectedType(Player clickPlayer) {
        if (selectedNames.isEmpty()) {
            clickPlayer.sendMessage(ChatHelper.PREFIX_COMPONENT.append(ChatHelper.getErrorComponent(
                    "Select a rail type before generating."
            )));
            playSound(clickPlayer, Sound.ENTITY_ITEM_BREAK);
            reloadMenuAsync();
            return;
        }

        Rail rail = GeneratorModule.getInstance().getRail();

        if (rail == null)
            return;

        Settings settings = rail.getPlayerSettings().get(clickPlayer.getUniqueId());

        if (!(settings instanceof RailSettings railSettings))
            return;

        RailType railType = RailType.byString(selectedNames.getFirst());

        if (railType == null) {
            clickPlayer.sendMessage(ChatHelper.PREFIX_COMPONENT.append(ChatHelper.getErrorComponent(
                    "This rail type no longer exists."
            )));
            playSound(clickPlayer, Sound.ENTITY_ITEM_BREAK);
            return;
        }

        railSettings.setValue(RailFlag.RAIL_TYPE, railType);
        clickPlayer.closeInventory();
        playSound(clickPlayer, Sound.UI_BUTTON_CLICK);
        rail.generate(clickPlayer);
    }

    private void openSearchEditor(Player clickPlayer) {
        playSound(clickPlayer, Sound.UI_BUTTON_CLICK);

        new AnvilGUI.Builder()
                .onClick((slot, stateSnapshot) -> {
                    if (slot != AnvilGUI.Slot.OUTPUT)
                        return List.of();

                    String query = stateSnapshot.getText().trim();
                    return List.of(
                            AnvilGUI.ResponseAction.close(),
                            AnvilGUI.ResponseAction.run(() -> new RailTypeMenu(
                                    clickPlayer,
                                    query,
                                    selectedForDeletion,
                                    true
                            ))
                    );
                })
                .text(searchQuery.isBlank() ? "Search" : searchQuery)
                .itemLeft(Item.create(Objects.requireNonNull(XMaterial.NAME_TAG.get()), yellow("Search Rail Types")))
                .title(darkGray("Search rail types"))
                .plugin(BuildTeamTools.getInstance())
                .open(clickPlayer);
    }

    private void setRailTypePageItems() {
        getMenu().getSlot(SWITCH_PAGE_ITEM_SLOT - 1).setItem(createPreviousPageItem());

        getMenu().getSlot(SWITCH_PAGE_ITEM_SLOT).setItem(Item.create(
                Objects.requireNonNull(XMaterial.PAPER.get()),
                yellow("Current Page ") + gray("- ") + white(String.valueOf(getPage())),
                List.of(gray("Use the arrows next to this item to browse rail types."))
        ));

        getMenu().getSlot(SWITCH_PAGE_ITEM_SLOT + 1).setItem(createNextPageItem());
    }

    private ItemStack createPreviousPageItem() {
        if (!hasPreviousPage())
            return Item.create(
                    Objects.requireNonNull(XMaterial.GRAY_STAINED_GLASS_PANE.get()),
                    gray("No Previous Page"),
                    List.of(darkGray("You are already on the first page."))
            );

        return HeadFactory.head(
                HeadTexture.WHITE_ARROW_LEFT,
                yellow("Previous Page ") + gray("- ") + white(String.valueOf(getPage() - 1)),
                new ArrayList<>(List.of(gray("Click to show earlier rail types.")))
        );
    }

    private ItemStack createNextPageItem() {
        if (!hasNextPage())
            return Item.create(
                    Objects.requireNonNull(XMaterial.GRAY_STAINED_GLASS_PANE.get()),
                    gray("No Next Page"),
                    List.of(darkGray("There are no more rail types."))
            );

        return HeadFactory.head(
                HeadTexture.WHITE_ARROW_RIGHT,
                yellow("Next Page ") + gray("- ") + white(String.valueOf(getPage() + 1)),
                new ArrayList<>(List.of(gray("Click to show more rail types.")))
        );
    }

    private void setRailTypePageClickEvents() {
        getMenu().getSlot(SWITCH_PAGE_ITEM_SLOT - 1).setClickHandler((clickPlayer, clickInformation) -> {
            if (!hasPreviousPage()) {
                clickPlayer.sendMessage(ChatHelper.PREFIX_COMPONENT.append(ChatHelper.getErrorComponent(
                        "You are already on the first rail type page."
                )));
                playSound(clickPlayer, Sound.ENTITY_ITEM_BREAK);
                return;
            }

            previousPage();
            clickPlayer.sendMessage(ChatHelper.getStandardComponent(true, "Opened rail type page %s.", getPage()));
            playSound(clickPlayer, Sound.UI_BUTTON_CLICK);
        });

        getMenu().getSlot(SWITCH_PAGE_ITEM_SLOT + 1).setClickHandler((clickPlayer, clickInformation) -> {
            if (!hasNextPage()) {
                clickPlayer.sendMessage(ChatHelper.PREFIX_COMPONENT.append(ChatHelper.getErrorComponent(
                        "There is no next rail type page."
                )));
                playSound(clickPlayer, Sound.ENTITY_ITEM_BREAK);
                return;
            }

            nextPage();
            clickPlayer.sendMessage(ChatHelper.getStandardComponent(true, "Opened rail type page %s.", getPage()));
            playSound(clickPlayer, Sound.UI_BUTTON_CLICK);
        });
    }

    private void playSound(Player player, Sound sound) {
        player.playSound(player, sound, 1.0F, 1.0F);
    }

    private static String darkGray(String text) {
        return RailMenuText.color(NamedTextColor.DARK_GRAY, text);
    }

    private static String gray(String text) {
        return RailMenuText.color(NamedTextColor.GRAY, text);
    }

    private static String white(String text) {
        return RailMenuText.color(NamedTextColor.WHITE, text);
    }

    private static String yellow(String text) {
        return RailMenuText.color(NamedTextColor.YELLOW, text);
    }

    private static String green(String text) {
        return RailMenuText.color(NamedTextColor.GREEN, text);
    }

    private static String red(String text) {
        return RailMenuText.color(NamedTextColor.RED, text);
    }
}
