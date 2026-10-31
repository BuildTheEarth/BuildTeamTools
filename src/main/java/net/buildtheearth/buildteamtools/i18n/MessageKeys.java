package net.buildtheearth.buildteamtools.i18n;

/**
 * Stable translation keys for user-facing strings.
 *
 * <p>Prefer these constants over string literals so renames and missing keys stay discoverable.
 * Keys use the {@code btt.<area>.…} namespace and should not change once shipped.
 */
public final class MessageKeys {

    private MessageKeys() {
    }

    public static final class Common {
        private Common() {
        }

        public static final String PLAYER_ONLY = "btt.common.player-only";
        public static final String NO_PERMISSION = "btt.common.no-permission";
        /** Args: {@code 0}=module name, {@code 1}=optional reason */
        public static final String MODULE_DISABLED = "btt.common.module-disabled";
        /** Args: {@code 0}=error detail (used as module-disabled arg 1) */
        public static final String MODULE_DISABLED_REASON = "btt.common.module-disabled-reason";
        /** Args: {@code 0}=correct syntax */
        public static final String INVALID_SYNTAX = "btt.common.invalid-syntax";
        /** Args: {@code 0}=parse error */
        public static final String INVALID_ARGUMENT = "btt.common.invalid-argument";
        public static final String NO_PERMISSION_GENERIC = "btt.common.no-permission-generic";
        public static final String INVALID_SENDER = "btt.common.invalid-sender";
        public static final String COMMAND_ERROR = "btt.common.command-error";
        /** Args: {@code 0}=ON/OFF */
        public static final String DEBUG_CURRENT = "btt.common.debug-current";
        /** Args: {@code 0}=true/false */
        public static final String DEBUG_SET = "btt.common.debug-set";
        public static final String CACHE_UPDATED = "btt.common.cache-updated";
        public static final String CACHE_STATS_DISABLED = "btt.common.cache-stats-disabled";
        public static final String RELOADING_CONFIGS = "btt.common.reloading-configs";
        public static final String CONFIGS_RELOADED = "btt.common.configs-reloaded";
        public static final String INFO_TITLE = "btt.common.info.title";
        public static final String COMMUNICATORS_TITLE = "btt.common.communicators.title";
        public static final String CACHE_TITLE = "btt.common.cache.title";
        /** Args: {@code 0}=uuid */
        public static final String COMMUNICATOR_ENTRY = "btt.common.communicators.entry";
        /** Args: {@code 0}=status component */
        public static final String INFO_STATUS = "btt.common.info.status";
        public static final String INFO_STATUS_CONNECTED = "btt.common.info.status-connected";
        public static final String INFO_STATUS_DISCONNECTED = "btt.common.info.status-disconnected";
        public static final String INFO_STATUS_STANDBY = "btt.common.info.status-standby";
        /** Args: {@code 0}=version */
        public static final String INFO_VERSION = "btt.common.info.version";
        public static final String INFO_DEBUG_ON = "btt.common.info.debug-on";
        public static final String INFO_MODULES = "btt.common.info.modules";
        /** Args: {@code 0}=module name, {@code 1}=status mark */
        public static final String INFO_MODULE_LINE = "btt.common.info.module-line";
        public static final String INFO_MODULE_MARK_ON = "btt.common.info.module-mark-on";
        public static final String INFO_MODULE_MARK_OFF = "btt.common.info.module-mark-off";
        public static final String INFO_MODULE_HOVER_ENABLED = "btt.common.info.module-hover-enabled";
        public static final String INFO_MODULE_HOVER_DISABLED = "btt.common.info.module-hover-disabled";
        /** Args: {@code 0}=error */
        public static final String INFO_MODULE_HOVER_ERROR = "btt.common.info.module-hover-error";
        /** Args: {@code 0}=id */
        public static final String INFO_BUILD_TEAM_ID = "btt.common.info.build-team-id";
        /** Args: {@code 0}=server name */
        public static final String INFO_SERVER_NAME = "btt.common.info.server-name";
        /** Args: {@code 0}=continents json */
        public static final String INFO_CONTINENTS = "btt.common.info.continents";
        /** Args: {@code 0}=regions json */
        public static final String INFO_REGIONS = "btt.common.info.regions";
        public static final String INFO_HELP_HINT = "btt.common.info.help-hint";
        public static final String DEBUG_ON = "btt.common.debug-on";
        public static final String DEBUG_OFF = "btt.common.debug-off";
    }

    public static final class Navigation {
        private Navigation() {
        }

        public static final class MainMenu {
            private MainMenu() {
            }

            public static final String TITLE = "btt.nav.menu.main.title";
            public static final String BUILD_NAME = "btt.nav.menu.main.build.name";
            public static final String BUILD_LORE = "btt.nav.menu.main.build.lore";
            public static final String PLOTSYSTEM_NAME = "btt.nav.menu.main.plotsystem.name";
            public static final String PLOTSYSTEM_LORE = "btt.nav.menu.main.plotsystem.lore";
            public static final String EXPLORE_NAME = "btt.nav.menu.main.explore.name";
            public static final String EXPLORE_LORE_WARPS = "btt.nav.menu.main.explore.lore-warps";
            public static final String EXPLORE_LORE_TEAMS = "btt.nav.menu.main.explore.lore-teams";
            public static final String TUTORIALS_NAME = "btt.nav.menu.main.tutorials.name";
            public static final String TUTORIALS_LORE = "btt.nav.menu.main.tutorials.lore";
            /** Args: {@code 0}=item type */
            public static final String ITEM_DISABLED = "btt.nav.menu.main.item-disabled";
            /** Args: {@code 0}=item type, {@code 1}=contact role */
            public static final String NO_ACTION = "btt.nav.menu.main.no-action";
            /** Args: {@code 0}=item type label, {@code 1}=contact role */
            public static final String NO_ACTION_GENERIC = "btt.nav.menu.main.no-action-generic";
        }

        public static final class ExploreMenu {
            private ExploreMenu() {
            }

            public static final String TITLE = "btt.nav.menu.explore.title";
            /** Args: {@code 0}=continent label */
            public static final String CONTINENT_NAME = "btt.nav.menu.explore.continent.name";
            /** Args: {@code 0}=continent label */
            public static final String CONTINENT_LORE = "btt.nav.menu.explore.continent.lore";
        }

        public static final class TutorialsMenu {
            private TutorialsMenu() {
            }

            public static final String TITLE = "btt.nav.menu.tutorials.title";
        }

        public static final class CountryMenu {
            private CountryMenu() {
            }

            /** Args: {@code 0}=continent label */
            public static final String TITLE = "btt.nav.menu.country.title";
            /** Args: {@code 0}=country name */
            public static final String ITEM_NAME = "btt.nav.menu.country.item.name";
            /** Args: {@code 0}=build team name, {@code 1}=formatted area */
            public static final String ITEM_LORE = "btt.nav.menu.country.item.lore";
            public static final String ITEM_LORE_WARPS = "btt.nav.menu.country.item.lore-warps";
        }

        public static final class StateMenu {
            private StateMenu() {
            }

            public static final String TITLE = "btt.nav.menu.state.title";
            /** Args: {@code 0}=state name */
            public static final String ITEM_NAME = "btt.nav.menu.state.item.name";
            public static final String BUILD_TEAM_LABEL = "btt.nav.menu.state.build-team-label";
            public static final String LORE_JOIN = "btt.nav.menu.state.lore-join";
            public static final String LORE_VIEW_CITIES = "btt.nav.menu.state.lore-view-cities";
        }

        public static final class CityMenu {
            private CityMenu() {
            }

            /** Args: {@code 0}=state name */
            public static final String TITLE = "btt.nav.menu.city.title";
            /** Args: {@code 0}=city/region name */
            public static final String ITEM_NAME = "btt.nav.menu.city.item.name";
            /** Args: {@code 0}=build team name */
            public static final String ITEM_LORE_JOIN_CITY = "btt.nav.menu.city.item.lore-join-city";
            /** Args: {@code 0}=build team name */
            public static final String ITEM_LORE_JOIN_STATE = "btt.nav.menu.city.item.lore-join-state";
            public static final String ITEM_LORE_WARPS = "btt.nav.menu.city.item.lore-warps";
            /** Args: {@code 0}=state name */
            public static final String REST_OF_STATE = "btt.nav.menu.city.rest-of-state";
        }

        /** Args: {@code 0}=team name, {@code 1}=server IP */
        public static final String NOT_CONNECTED = "btt.nav.not-connected";
        /** Args: {@code 0}=server IP */
        public static final String NOT_CONNECTED_IP = "btt.nav.not-connected-ip";
        public static final String NOT_CONNECTED_HOVER = "btt.nav.not-connected-hover";
        public static final String NOT_CONNECTED_HINT = "btt.nav.not-connected-hint";
        /** Args: {@code 0}=build team name */
        public static final String NO_IP = "btt.nav.no-ip";
        /** Args: {@code 0}=build team name */
        public static final String INVALID_NETWORK_MAPPING = "btt.nav.invalid-network-mapping";
        public static final String COUNTRY_NOT_FOUND = "btt.nav.country-not-found";
    }

    public static final class Navigator {
        private Navigator() {
        }

        public static final String ITEM_NAME = "btt.navigator.item.name";
        /** Args: {@code 0}=on/off state */
        public static final String TOGGLED = "btt.navigator.toggled";
        public static final String STATE_ON = "btt.navigator.state.on";
        public static final String STATE_OFF = "btt.navigator.state.off";
        /** Args: {@code 0}=available continents */
        public static final String UNKNOWN_CONTINENT = "btt.navigator.unknown-continent";
        /** Args: {@code 0}=available states */
        public static final String UNKNOWN_CITY_STATE = "btt.navigator.unknown-city-state";
        /** Args: {@code 0}=build team name */
        public static final String BUILD_TEAM_NOT_FOUND = "btt.navigator.build-team-not-found";
    }

    public static final class Address {
        private Address() {
        }

        public static final String USAGE = "btt.address.usage";
        public static final String GETTING_CLOSEST = "btt.address.getting-closest";
        public static final String CONVERT_FAILED = "btt.address.convert-failed";
        public static final String NOT_FOUND = "btt.address.not-found";
        public static final String LABEL = "btt.address.label";
        public static final String CLICK_TO_COPY = "btt.address.click-to-copy";
        public static final String RETRIEVE_FAILED = "btt.address.retrieve-failed";
        public static final String TOO_MANY_REQUESTS = "btt.address.too-many-requests";
        public static final String FOUND_TELEPORTING = "btt.address.found-teleporting";
        /** Args: {@code 0}=address */
        public static final String FOUND = "btt.address.found";
        public static final String TELEPORTING = "btt.address.teleporting";
        /** Args: {@code 0}=error message */
        public static final String ERROR = "btt.address.error";
    }

    public static final class Warp {
        private Warp() {
        }

        public static final String MODULE_DISABLED = "btt.warp.module-disabled";
        /** Args: {@code 0}=action verb (create/migrate/use) */
        public static final String NO_PERMISSION_ACTION = "btt.warp.no-permission-action";
        public static final String CREATING = "btt.warp.creating";
        public static final String MIGRATING = "btt.warp.migrating";
        public static final String NO_WARP_FOUND = "btt.warp.no-warp-found";
        /** Args: {@code 0}=error detail */
        public static final String MIGRATE_ERROR = "btt.warp.migrate-error";
        /** Args: {@code 0}=error detail */
        public static final String MIGRATE_FAILED = "btt.warp.migrate-failed";
        public static final String MIGRATE_NONE = "btt.warp.migrate-none";
        /** Args: {@code 0}=migrated count */
        public static final String MIGRATE_SUCCESS = "btt.warp.migrate-success";
        /** Args: {@code 0}=failed count */
        public static final String MIGRATE_ALL_FAILED = "btt.warp.migrate-all-failed";
        /** Args: {@code 0}=migrated count, {@code 1}=failed count */
        public static final String MIGRATE_PARTIAL = "btt.warp.migrate-partial";
        /** Args: {@code 0}=warp name */
        public static final String NOT_EXISTS = "btt.warp.not-exists";
        public static final String GONE = "btt.warp.gone";
        public static final String TELEPORT_SUCCESS = "btt.warp.teleport-success";
        public static final String TELEPORT_FAILED = "btt.warp.teleport-failed";
        /** Args: {@code 0}=warp name */
        public static final String TELEPORT_SUCCESS_NAMED = "btt.warp.teleport-success-named";
        /** Args: {@code 0}=error detail */
        public static final String CREATE_FAILED = "btt.warp.create-failed";
        /** Args: {@code 0}=error detail */
        public static final String CREATE_HERE_FAILED = "btt.warp.create-here-failed";
        /** Args: {@code 0}=warp name, {@code 1}=error detail */
        public static final String CREATE_NAMED_FAILED = "btt.warp.create-named-failed";
        public static final String NO_WARPS_YET = "btt.warp.no-warps-yet";
        public static final String NO_WARP_AVAILABLE = "btt.warp.no-warp-available";
        /** Args: {@code 0}=player name */
        public static final String DEFAULT_NAME = "btt.warp.default-name";
        /** Args: {@code 0}=player name */
        public static final String DEFAULT_GROUP_NAME = "btt.warp.default-group-name";
        public static final String DEFAULT_GROUP_DESCRIPTION = "btt.warp.default-group-description";
        public static final String OTHER_GROUP_DESCRIPTION = "btt.warp.other-group-description";
        /** Args: {@code 0}=region name */
        public static final String TEAM_DOES_NOT_OWN_COUNTRY = "btt.warp.team-does-not-own-country";
        /** Args: {@code 0}=warp name */
        public static final String MIGRATE_ALREADY_EXISTS = "btt.warp.migrate-already-exists";
        public static final String MIGRATE_ESSENTIALS_ACCESS_FAILED = "btt.warp.migrate-essentials-access-failed";

        public static final class Menu {
            private Menu() {
            }

            public static final String TITLE = "btt.warp.menu.title";
            public static final String RANDOM_NAME = "btt.warp.menu.random.name";
            public static final String RANDOM_LORE = "btt.warp.menu.random.lore";
            public static final String CREATE_NAME = "btt.warp.menu.create.name";
            public static final String CREATE_LORE = "btt.warp.menu.create.lore";
            public static final String CREATE_GROUP_NAME = "btt.warp.menu.create-group.name";
            public static final String CREATE_GROUP_LORE = "btt.warp.menu.create-group.lore";
            /** Args: {@code 0}=warp/group name */
            public static final String ITEM_NAME = "btt.warp.menu.item.name";
            public static final String ADDRESS_LABEL = "btt.warp.menu.address-label";
            public static final String DESCRIPTION_LABEL = "btt.warp.menu.description-label";
        }

        public static final class Edit {
            private Edit() {
            }

            public static final String TITLE = "btt.warp.edit.title";
            public static final String UPDATE = "btt.warp.edit.update";
            public static final String CREATE = "btt.warp.edit.create";
            public static final String CHANGE_LOCATION = "btt.warp.edit.change-location";
            /** Args: {@code 0}=world, {@code 1}=lat, {@code 2}=lon, {@code 3}=elevation */
            public static final String LOCATION_LORE = "btt.warp.edit.location.lore";
            public static final String CHANGE_NAME = "btt.warp.edit.change-name";
            /** Args: {@code 0}=current name */
            public static final String NAME_LORE = "btt.warp.edit.name.lore";
            public static final String CHANGE_GROUP = "btt.warp.edit.change-group";
            /** Args: {@code 0}=current group */
            public static final String GROUP_LORE = "btt.warp.edit.group.lore";
            public static final String CHANGE_ADDRESS_TYPE = "btt.warp.edit.change-address-type";
            /** Args: {@code 0}=address type */
            public static final String ADDRESS_TYPE_LORE = "btt.warp.edit.address-type.lore";
            public static final String ADDRESS_TYPE_CITY_DEFAULT = "btt.warp.edit.address-type.city-default";
            public static final String CHANGE_MATERIAL = "btt.warp.edit.change-material";
            /** Args: {@code 0}=material */
            public static final String MATERIAL_LORE = "btt.warp.edit.material.lore";
            public static final String MATERIAL_DEFAULT = "btt.warp.edit.material.default";
            public static final String MAKE_NORMAL = "btt.warp.edit.make-normal";
            public static final String MAKE_HIGHLIGHT = "btt.warp.edit.make-highlight";
            /** Args: {@code 0}=true/false */
            public static final String HIGHLIGHT_LORE = "btt.warp.edit.highlight.lore";
            public static final String DELETE = "btt.warp.edit.delete";
            public static final String DELETE_LORE = "btt.warp.edit.delete.lore";
            public static final String ANVIL_TEXT = "btt.warp.edit.anvil.text";
            public static final String ANVIL_TITLE = "btt.warp.edit.anvil.title";
            /** Args: {@code 0}=error detail */
            public static final String LOCATION_CHANGE_FAILED = "btt.warp.edit.location-change-failed";
            /** Args: {@code 0}=error detail */
            public static final String LOCATION_SET_HERE_FAILED = "btt.warp.edit.location-set-here-failed";
            public static final String DELETE_NO_PERMISSION = "btt.warp.edit.delete-no-permission";
        }

        public static final class GroupEdit {
            private GroupEdit() {
            }

            public static final String TITLE = "btt.warp.group-edit.title";
            public static final String CHANGE_DESCRIPTION = "btt.warp.group-edit.change-description";
            public static final String CHANGE_SLOT = "btt.warp.group-edit.change-slot";
            /** Args: {@code 0}=slot value */
            public static final String SLOT_LORE = "btt.warp.group-edit.slot.lore";
            public static final String SLOT_AUTO = "btt.warp.group-edit.slot.auto";
            public static final String DELETE = "btt.warp.group-edit.delete";
            public static final String DELETE_LORE = "btt.warp.group-edit.delete.lore";
            public static final String ANVIL_NAME_TEXT = "btt.warp.group-edit.anvil.name-text";
            public static final String ANVIL_NAME_TITLE = "btt.warp.group-edit.anvil.name-title";
            public static final String BOOK_DESCRIPTION_TITLE = "btt.warp.group-edit.book.description-title";
            public static final String DESCRIPTION_SAVE_FAILED = "btt.warp.group-edit.description-save-failed";
            public static final String INVALID_SLOT = "btt.warp.group-edit.invalid-slot";
            /** Args: {@code 0}=group name, {@code 1}=slot */
            public static final String SLOT_OCCUPIED = "btt.warp.group-edit.slot-occupied";
            public static final String ANVIL_SLOT_TEXT = "btt.warp.group-edit.anvil.slot-text";
            public static final String ANVIL_SLOT_TITLE = "btt.warp.group-edit.anvil.slot-title";
            public static final String DELETE_NO_PERMISSION = "btt.warp.group-edit.delete-no-permission";
        }

        public static final class DeleteGroup {
            private DeleteGroup() {
            }

            public static final String TITLE = "btt.warp.delete-group.title";
            public static final String INFO_NAME = "btt.warp.delete-group.info.name";
            /** Args: {@code 0}=group name, {@code 1}=warp count */
            public static final String INFO_LORE_SINGULAR = "btt.warp.delete-group.info.lore-singular";
            /** Args: {@code 0}=group name, {@code 1}=warp count */
            public static final String INFO_LORE_PLURAL = "btt.warp.delete-group.info.lore-plural";
            public static final String MOVE_NAME = "btt.warp.delete-group.move.name";
            public static final String MOVE_LORE = "btt.warp.delete-group.move.lore";
            public static final String DELETE_ALL_NAME = "btt.warp.delete-group.delete-all.name";
            public static final String DELETE_ALL_LORE = "btt.warp.delete-group.delete-all.lore";
        }

        public static final class AddressType {
            private AddressType() {
            }

            public static final String TITLE = "btt.warp.address-type.title";
            public static final String EXAMPLE_LABEL = "btt.warp.address-type.example-label";
            public static final String BUILDING = "btt.warp.address-type.building";
            public static final String STREET = "btt.warp.address-type.street";
            public static final String CITY = "btt.warp.address-type.city";
            public static final String STATE = "btt.warp.address-type.state";
            public static final String COUNTRY = "btt.warp.address-type.country";
            public static final String CUSTOM = "btt.warp.address-type.custom";
            public static final String CUSTOM_LORE = "btt.warp.address-type.custom.lore";
            public static final String ANVIL_TEXT = "btt.warp.address-type.anvil.text";
            public static final String ANVIL_ITEM = "btt.warp.address-type.anvil.item";
            public static final String ANVIL_TITLE = "btt.warp.address-type.anvil.title";
        }

        public static final class Material {
            private Material() {
            }

            public static final String TITLE = "btt.warp.material.title";
            public static final String ITEM_NAME = "btt.warp.material.item.name";
            public static final String ITEM_LORE = "btt.warp.material.item.lore";
            public static final String HEAD_NAME = "btt.warp.material.head.name";
            public static final String HEAD_LORE = "btt.warp.material.head.lore";
            public static final String INVALID = "btt.warp.material.invalid";
            public static final String ANVIL_TEXT = "btt.warp.material.anvil.text";
            public static final String ANVIL_ITEM = "btt.warp.material.anvil.item";
            public static final String ANVIL_TITLE = "btt.warp.material.anvil.title";
            public static final String BOOK_TITLE = "btt.warp.material.book.title";
            public static final String URL_SAVE_FAILED = "btt.warp.material.url-save-failed";
            public static final String URL_INVALID = "btt.warp.material.url-invalid";
        }
    }

    public static final class Map {
        private Map() {
        }

        public static final String USAGE_CREATE = "btt.map.usage-create";
        public static final String OPTIONAL_NAME = "btt.map.optional-name";
        public static final String CREATING = "btt.map.creating";
        public static final String UNAVAILABLE_TOKEN = "btt.map.unavailable-token";
        public static final String UNAVAILABLE_TEAM_ID = "btt.map.unavailable-team-id";
        public static final String UNAVAILABLE_BASE_PATH = "btt.map.unavailable-base-path";
        public static final String WORLDEDIT_REQUIRED = "btt.map.worldedit-required";
        public static final String EARTH_WORLD_NOT_LOADED = "btt.map.earth-world-not-loaded";
        public static final String SELECT_IN_EARTH_WORLD = "btt.map.select-in-earth-world";
        public static final String NEED_POLY_SELECTION = "btt.map.need-poly-selection";
        public static final String REQUIRE_POLY_SELECTION = "btt.map.require-poly-selection";
        /** Args: {@code 0}=minimum point count */
        public static final String MIN_POLYGON_POINTS = "btt.map.min-polygon-points";
        /** Args: {@code 0}=projection error */
        public static final String OUTSIDE_PROJECTION = "btt.map.outside-projection";
        /** Args: {@code 0}=claim name; ends before the clickable ID */
        public static final String CREATED_PREFIX = "btt.map.created-prefix";
        public static final String CREATED_SUFFIX = "btt.map.created-suffix";
        public static final String OPEN_CLAIM_HOVER = "btt.map.open-claim-hover";
        public static final String UNNAMED_CLAIM = "btt.map.unnamed-claim";
        public static final String ACCOUNT_NOT_REGISTERED = "btt.map.account-not-registered";
        /** Args: {@code 0}=failure reason */
        public static final String CREATE_FAILED = "btt.map.create-failed";
    }

    public static final class Network {
        private Network() {
        }

        /** Args: {@code 0}=warp name */
        public static final String WARP_CREATE_OWN_TEAM = "btt.network.warp-create-own-team";
        /** Args: {@code 0}=warp name */
        public static final String WARP_CREATE_SUCCESS = "btt.network.warp-create-success";
        /** Args: {@code 0}=warp name */
        public static final String WARP_CREATE_SYNC_FAILED = "btt.network.warp-create-sync-failed";
        public static final String WARP_GROUP_CREATE_OWN_TEAM = "btt.network.warp-group-create-own-team";
        /** Args: {@code 0}=group name */
        public static final String WARP_GROUP_CREATE_SUCCESS = "btt.network.warp-group-create-success";
        /** Args: {@code 0}=group name */
        public static final String WARP_GROUP_CREATE_SYNC_FAILED = "btt.network.warp-group-create-sync-failed";
        public static final String WARP_UPDATE_OWN_TEAM = "btt.network.warp-update-own-team";
        /** Args: {@code 0}=warp name */
        public static final String WARP_UPDATE_SUCCESS = "btt.network.warp-update-success";
        /** Args: {@code 0}=warp name */
        public static final String WARP_UPDATE_SYNC_FAILED = "btt.network.warp-update-sync-failed";
        public static final String WARP_GROUP_UPDATE_OWN_TEAM = "btt.network.warp-group-update-own-team";
        /** Args: {@code 0}=group name */
        public static final String WARP_GROUP_UPDATE_SUCCESS = "btt.network.warp-group-update-success";
        /** Args: {@code 0}=group name */
        public static final String WARP_GROUP_UPDATE_SYNC_FAILED = "btt.network.warp-group-update-sync-failed";
        public static final String WARP_DELETE_OWN_TEAM = "btt.network.warp-delete-own-team";
        /** Args: {@code 0}=warp name */
        public static final String WARP_DELETE_SUCCESS = "btt.network.warp-delete-success";
        /** Args: {@code 0}=warp name */
        public static final String WARP_DELETE_SYNC_FAILED = "btt.network.warp-delete-sync-failed";
        public static final String WARP_GROUP_DELETE_OWN_TEAM = "btt.network.warp-group-delete-own-team";
        /** Args: {@code 0}=group name */
        public static final String WARP_GROUP_DELETE_SUCCESS = "btt.network.warp-group-delete-success";
        /** Args: {@code 0}=group name */
        public static final String WARP_GROUP_DELETE_SYNC_FAILED = "btt.network.warp-group-delete-sync-failed";
    }

    public static final class Updater {
        private Updater() {
        }

        public static final String NO_UPDATE = "btt.updater.no-update";
        /** Args: {@code 0}=version */
        public static final String UPDATED = "btt.updater.updated";
        /** Args: {@code 0}=error detail */
        public static final String CHECK_FAILED = "btt.updater.check-failed";
        public static final String UP_TO_DATE = "btt.updater.up-to-date";
        /** Args: {@code 0}=permission */
        public static final String AVAILABLE_NO_PERM = "btt.updater.available-no-perm";
        /** Args: {@code 0}=version */
        public static final String AVAILABLE = "btt.updater.available";
        /** Args: {@code 0}=version */
        public static final String INSTALLED_NOTIFY = "btt.updater.installed-notify";
        public static final String RESTART_TO_ACTIVATE = "btt.updater.restart-to-activate";
    }

    public static final class Module {
        private Module() {
        }

        /** Args: {@code 0}=module name */
        public static final String ENABLE_SUCCESS = "btt.module.enable-success";
        /** Args: {@code 0}=module name, {@code 1}=optional reason */
        public static final String ENABLE_FAILED = "btt.module.enable-failed";
        /** Args: {@code 0}=module name */
        public static final String DISABLE_SUCCESS = "btt.module.disable-success";
        /** Args: {@code 0}=module name, {@code 1}=optional reason */
        public static final String DISABLE_FAILED = "btt.module.disable-failed";
        /** Args: {@code 0}=error detail */
        public static final String FAILURE_REASON = "btt.module.failure-reason";
    }

    public static final class Tpll {
        private Tpll() {
        }

        public static final String EARTH_WORLD_UNKNOWN = "btt.tpll.earth-world-unknown";
    }

    /** Shared UI chrome used across menus (back, pagination, sliders, books). */
    public static final class Ui {
        private Ui() {
        }

        public static final String BACK = "btt.ui.back";
        public static final String BACK_RED = "btt.ui.back-red";
        public static final String NEXT = "btt.ui.next";
        public static final String APPLY = "btt.ui.apply";
        /** Args: {@code 0}=page number */
        public static final String CURRENT_PAGE = "btt.ui.current-page";
        /** Args: {@code 0}=page number */
        public static final String PREVIOUS_PAGE = "btt.ui.previous-page";
        /** Args: {@code 0}=page number */
        public static final String NEXT_PAGE = "btt.ui.next-page";
        public static final String PREVIOUS_PAGE_PLAIN = "btt.ui.previous-page-plain";
        public static final String NEXT_PAGE_PLAIN = "btt.ui.next-page-plain";
        public static final String NO_PREVIOUS_PAGE = "btt.ui.no-previous-page";
        public static final String NO_NEXT_PAGE = "btt.ui.no-next-page";
        /** Args: {@code 0}=page text e.g. {@code 1/3} */
        public static final String PAGE_INDICATOR = "btt.ui.page-indicator";
        /** Args: {@code 0}=slider name (uncolored) */
        public static final String SLIDER_NAME = "btt.ui.slider-name";
        /** Args: {@code 0}=slider name (uncolored) */
        public static final String SLIDER_OFF = "btt.ui.slider-off";
        /** Args: {@code 0}=slider name (uncolored) */
        public static final String SLIDER_DISABLE = "btt.ui.slider-disable";
        /** Args: {@code 0}=slider name, {@code 1}=current value */
        public static final String COUNTER_VALUE = "btt.ui.counter-value";
        /** Args: {@code 0}=slider name, {@code 1}=current value, {@code 2}=unit */
        public static final String COUNTER_VALUE_TYPED = "btt.ui.counter-value-typed";
        /** Args: {@code 0}=slider name */
        public static final String COUNTER_PLUS = "btt.ui.counter-plus";
        /** Args: {@code 0}=slider name */
        public static final String COUNTER_MINUS = "btt.ui.counter-minus";
        public static final String BOOK_TIMEOUT = "btt.ui.book-timeout";
        public static final String BOOK_INSTRUCTIONS = "btt.ui.book-instructions";
    }

    public static final class BlockPalette {
        private BlockPalette() {
        }

        public static final String USAGE = "btt.bp.usage";

        public static final String CHOOSE_TITLE = "btt.bp.menu.choose.title";
        public static final String CREATE_TITLE = "btt.bp.menu.create.title";
        /** Args: {@code 0}=palette name */
        public static final String EDIT_TITLE = "btt.bp.menu.edit.title";
        public static final String CHOOSE_BLOCKS_TITLE = "btt.bp.menu.choose-blocks.title";
        public static final String VIEW_TITLE = "btt.bp.menu.view.title";

        /** Args: {@code 0}=filter/palette display name */
        public static final String FILTER_ON = "btt.bp.filter.on";
        /** Args: {@code 0}=filter/palette display name */
        public static final String FILTER_OFF = "btt.bp.filter.off";
        /** Args: {@code 0}=block count */
        public static final String LORE_BLOCKS = "btt.bp.lore.blocks";
        /** Args: {@code 0}=description or none */
        public static final String LORE_DESCRIPTION = "btt.bp.lore.description";
        public static final String NONE = "btt.bp.none";
        public static final String ADD_NEW = "btt.bp.add-new";
        public static final String CHOOSE_PALETTE_ITEM = "btt.bp.choose-palette-item";

        public static final String SET_NAME = "btt.bp.set-name";
        /** Args: {@code 0}=current name */
        public static final String SET_NAME_LORE = "btt.bp.set-name.lore";
        public static final String SET_DESCRIPTION = "btt.bp.set-description";
        /** Args: {@code 0}=current description */
        public static final String SET_DESCRIPTION_LORE = "btt.bp.set-description.lore";
        public static final String SET_BLOCKS = "btt.bp.set-blocks";
        /** Args: {@code 0}=selected count */
        public static final String SET_BLOCKS_LORE = "btt.bp.set-blocks.lore";
        public static final String DELETE = "btt.bp.delete";
        public static final String DELETE_LORE = "btt.bp.delete.lore";
        public static final String DELETE_LORE_PREDEFINED = "btt.bp.delete.lore-predefined";

        /** Args: {@code 0}=max length */
        public static final String PROMPT_NAME = "btt.bp.prompt.name";
        /** Args: {@code 0}=max length */
        public static final String PROMPT_DESCRIPTION = "btt.bp.prompt.description";
        /** Args: {@code 0}=max length */
        public static final String PROMPT_NAME_EDIT = "btt.bp.prompt.name-edit";
        /** Args: {@code 0}=max length */
        public static final String PROMPT_DESCRIPTION_EDIT = "btt.bp.prompt.description-edit";

        public static final String NO_PERM_CREATE = "btt.bp.no-perm.create";
        public static final String NO_PERM_EDIT = "btt.bp.no-perm.edit";
        public static final String NO_PERM_DELETE = "btt.bp.no-perm.delete";
        public static final String INVALID_NAME = "btt.bp.invalid-name";
        /** Args: {@code 0}=palette name */
        public static final String CREATED = "btt.bp.created";
        /** Args: {@code 0}=palette name */
        public static final String UPDATED = "btt.bp.updated";
        public static final String DELETED = "btt.bp.deleted";
        public static final String CANNOT_DELETE_PREDEFINED = "btt.bp.cannot-delete-predefined";
        public static final String FILTER_NOT_FOUND = "btt.bp.filter-not-found";
        public static final String INPUT_CANCELLED = "btt.bp.input-cancelled";
        /** Args: {@code 0}=max length */
        public static final String NAME_TOO_LONG = "btt.bp.name-too-long";
        public static final String NAME_EMPTY = "btt.bp.name-empty";
        /** Args: {@code 0}=name */
        public static final String NAME_SET = "btt.bp.name-set";
        /** Args: {@code 0}=max length */
        public static final String DESCRIPTION_TOO_LONG = "btt.bp.description-too-long";
        /** Args: {@code 0}=description or none */
        public static final String DESCRIPTION_SET = "btt.bp.description-set";
        public static final String MENU_BUILD_FAILED = "btt.bp.menu-build-failed";
        public static final String CLICK_ERROR = "btt.bp.click-error";
        public static final String HANDLERS_FAILED = "btt.bp.handlers-failed";

        public static final String SELECTION = "btt.bp.selection";
        /** Args: {@code 0}=count */
        public static final String SELECTION_COUNT = "btt.bp.selection.count";
        public static final String SELECTION_MORE = "btt.bp.selection.more";
        /** Args: {@code 0}=block pretty name */
        public static final String SELECTION_ITEM = "btt.bp.selection.item";
        public static final String BLOCK_SELECTED = "btt.bp.block.selected";
        public static final String BLOCK_SELECT_HINT = "btt.bp.block.select-hint";
        /** Args: {@code 0}=block pretty name */
        public static final String BLOCK_ON = "btt.bp.block.on";
        /** Args: {@code 0}=block pretty name */
        public static final String BLOCK_OFF = "btt.bp.block.off";
    }

    public static final class Stats {
        private Stats() {
        }

        public static final String TITLE = "btt.stats.menu.title";
        public static final String GLOBAL_NAME = "btt.stats.global.name";
        public static final String ACHIEVEMENTS_NAME = "btt.stats.achievements.name";
        public static final String PERSONAL_NAME = "btt.stats.personal.name";
        public static final String TEAM_PLACEHOLDER = "btt.stats.team.placeholder";
    }

    public static final class Generator {
        private Generator() {
        }

        public static final class History {
            private History() {
            }

            public static final String NOTHING_TO_UNDO = "btt.generator.history.nothing-to-undo";
            public static final String NOTHING_TO_REDO = "btt.generator.history.nothing-to-redo";
            public static final String UNDO_SUCCESS = "btt.generator.history.undo-success";
            public static final String REDO_SUCCESS = "btt.generator.history.redo-success";
            public static final String USE_REDO = "btt.generator.history.use-redo";
            public static final String USE_UNDO = "btt.generator.history.use-undo";
            public static final String HOVER_REDO = "btt.generator.history.hover-redo";
            public static final String HOVER_UNDO = "btt.generator.history.hover-undo";
        }

        public static final class Command {
            private Command() {
            }

            public static final String HELP_BOX_TITLE = "btt.generator.command.help-box-title";
            public static final String HELP_GENERATORS = "btt.generator.command.help-generators";
            public static final String HELP_HISTORY = "btt.generator.command.help-history";
            public static final String HISTORY_EMPTY = "btt.generator.command.history-empty";
            public static final String HISTORY_BOX_TITLE = "btt.generator.command.history-box-title";
            public static final String HISTORY_ENTRY = "btt.generator.command.history-entry";
            public static final String FIELD_DISABLED = "btt.generator.command.field-disabled";
        }

        public static final class Component {
            private Component() {
            }

            public static final String OPEN_DOCS = "btt.generator.component.open-docs";
            public static final String OPEN_DOCS_HOVER = "btt.generator.component.open-docs-hover";
            public static final String GENERATE_ERROR = "btt.generator.component.generate-error";
            public static final String GENERATE_SUCCESS = "btt.generator.component.generate-success";
            public static final String COPY_BUTTON = "btt.generator.component.copy-button";
            public static final String COPY_HOVER = "btt.generator.component.copy-hover";
            public static final String UNDO_BUTTON = "btt.generator.component.undo-button";
            public static final String UNDO_HOVER = "btt.generator.component.undo-hover";
        }

        public static final class Flag {
            private Flag() {
            }

            public static final String INVALID_VALUE = "btt.generator.flag.invalid-value";
        }

        public static final class Progress {
            private Progress() {
            }

            public static final String ACTION_BAR = "btt.generator.progress.action-bar";
            public static final String FAILED = "btt.generator.progress.failed";
        }

        public static final class Listener {
            private Listener() {
            }

            public static final String NO_WE_COMMANDS = "btt.generator.listener.no-we-commands";
            public static final String NO_WE_WHILE_GENERATING = "btt.generator.listener.no-we-while-generating";
        }

        public static final class Collections {
            private Collections() {
            }

            public static final String INSTALL_ERROR = "btt.generator.collections.install-error";
            public static final String OUTDATED = "btt.generator.collections.outdated";
            public static final String NOT_FOUND = "btt.generator.collections.not-found";
            public static final String INSTALLED = "btt.generator.collections.installed";
        }

        public static final class Scripts {
            private Scripts() {
            }

            public static final String REGION_NOT_READABLE = "btt.generator.scripts.region-not-readable";
            public static final String NO_SCHEMATICS = "btt.generator.scripts.no-schematics";
        }

        public static final class Menu {
            private Menu() {
            }

            public static final String TITLE = "btt.generator.menu.title";
            public static final String HOUSE_NAME = "btt.generator.menu.house.name";
            public static final String HOUSE_LORE = "btt.generator.menu.house.lore";
            public static final String ROAD_NAME = "btt.generator.menu.road.name";
            public static final String ROAD_LORE = "btt.generator.menu.road.lore";
            public static final String RAIL_NAME = "btt.generator.menu.rail.name";
            public static final String RAIL_LORE = "btt.generator.menu.rail.lore";
            public static final String TREE_NAME = "btt.generator.menu.tree.name";
            public static final String TREE_LORE = "btt.generator.menu.tree.lore";
            public static final String TREE_DISABLED_SCHEMATIC_LORE = "btt.generator.menu.tree.disabled-schematic-lore";
            public static final String TREE_DISABLED_PACK_LORE = "btt.generator.menu.tree.disabled-pack-lore";
            public static final String TREE_DISABLED_SUFFIX = "btt.generator.menu.tree.disabled-suffix";
            public static final String FIELD_NAME = "btt.generator.menu.field.name";
            public static final String FIELD_LORE = "btt.generator.menu.field.lore";
        }

        public static final class House {
            private House() {
            }

            public static final String WALL_COLOR_TITLE = "btt.generator.house.menu.wall-color.title";
            public static final String ROOF_TYPE_TITLE = "btt.generator.house.menu.roof-type.title";
            public static final String ROOF_COLOR_TITLE = "btt.generator.house.menu.roof-color.title";
            public static final String BASE_COLOR_TITLE = "btt.generator.house.menu.base-color.title";
            public static final String ADVANCED_TITLE = "btt.generator.house.menu.advanced.title";
            public static final String FLOOR_COUNT = "btt.generator.house.menu.advanced.floor-count";
            public static final String FLOOR_HEIGHT = "btt.generator.house.menu.advanced.floor-height";
            public static final String BASE_HEIGHT = "btt.generator.house.menu.advanced.base-height";
            public static final String WINDOW_WIDTH = "btt.generator.house.menu.advanced.window-width";
            public static final String WINDOW_HEIGHT = "btt.generator.house.menu.advanced.window-height";
            public static final String WINDOW_DISTANCE = "btt.generator.house.menu.advanced.window-distance";
            public static final String UNIT_FLOORS = "btt.generator.house.menu.advanced.unit-floors";
            public static final String UNIT_BLOCKS = "btt.generator.house.menu.advanced.unit-blocks";
            public static final String NEXT = "btt.generator.house.menu.next";

            public static final String ROOF_FLAT_NAME = "btt.generator.house.menu.roof-type.flat.name";
            public static final String ROOF_FLAT_LORE = "btt.generator.house.menu.roof-type.flat.lore";
            public static final String ROOF_GABLE_NAME = "btt.generator.house.menu.roof-type.gable.name";
            public static final String ROOF_GABLE_LORE = "btt.generator.house.menu.roof-type.gable.lore";
            public static final String ROOF_STEEP_NAME = "btt.generator.house.menu.roof-type.steep.name";
            public static final String ROOF_STEEP_LORE = "btt.generator.house.menu.roof-type.steep.lore";
            public static final String ROOF_MEDIUM_NAME = "btt.generator.house.menu.roof-type.medium.name";
            public static final String ROOF_MEDIUM_LORE = "btt.generator.house.menu.roof-type.medium.lore";
            public static final String ROOF_FLATTER_NAME = "btt.generator.house.menu.roof-type.flatter.name";
            public static final String ROOF_FLATTER_LORE = "btt.generator.house.menu.roof-type.flatter.lore";
        }

        public static final class Road {
            private Road() {
            }

            public static final String COLOR_TITLE = "btt.generator.road.menu.color.title";
            public static final String SIDEWALK_TITLE = "btt.generator.road.menu.sidewalk.title";
            public static final String ADVANCED_TITLE = "btt.generator.road.menu.advanced.title";
            public static final String LANE_COUNT = "btt.generator.road.menu.advanced.lane-count";
            public static final String LANE_WIDTH = "btt.generator.road.menu.advanced.lane-width";
            public static final String SIDEWALK_WIDTH = "btt.generator.road.menu.advanced.sidewalk-width";
            public static final String STREET_LAMP_DISTANCE = "btt.generator.road.menu.advanced.street-lamp-distance";
            public static final String MARKINGS_COLOR = "btt.generator.road.menu.advanced.markings-color";
            public static final String ROAD_SLAB = "btt.generator.road.menu.advanced.road-slab";
            public static final String SIDEWALK_SLAB = "btt.generator.road.menu.advanced.sidewalk-slab";
            public static final String STREET_LAMP_TYPE = "btt.generator.road.menu.advanced.street-lamp-type";
            public static final String UNIT_LANES = "btt.generator.road.menu.advanced.unit-lanes";
            public static final String UNIT_BLOCKS = "btt.generator.road.menu.advanced.unit-blocks";
            public static final String NEXT = "btt.generator.road.menu.next";
            public static final String CHOOSE_MARKING = "btt.generator.road.menu.choose.marking";
            public static final String CHOOSE_ROAD_SLAB = "btt.generator.road.menu.choose.road-slab";
            public static final String CHOOSE_SIDEWALK_SLAB = "btt.generator.road.menu.choose.sidewalk-slab";
            public static final String CHOOSE_STREET_LAMP = "btt.generator.road.menu.choose.street-lamp";
            public static final String STREET_LAMP_NAME = "btt.generator.road.menu.street-lamp-name";
            public static final String STREET_LAMP_LORE = "btt.generator.road.menu.street-lamp-lore";
        }

        public static final class Tree {
            private Tree() {
            }

            public static final String TYPE_TITLE = "btt.generator.tree.menu.type.title";
            public static final String WIDTH_TITLE = "btt.generator.tree.menu.width.title";
            public static final String HEIGHT_TITLE = "btt.generator.tree.menu.height.title";
        }

        public static final class Field {
            private Field() {
            }

            public static final String CROP_TYPE_TITLE = "btt.generator.field.menu.crop-type.title";
            public static final String CROP_STAGE_TITLE = "btt.generator.field.menu.crop-stage.title";
            public static final String FENCE_TYPE_TITLE = "btt.generator.field.menu.fence-type.title";
            public static final String CROP_ITEM_LORE = "btt.generator.field.menu.crop-type.item-lore";
            public static final String CROP_POTATO = "btt.generator.field.menu.crop-type.potato";
            public static final String CROP_WHEAT = "btt.generator.field.menu.crop-type.wheat";
            public static final String CROP_CORN = "btt.generator.field.menu.crop-type.corn";
            public static final String CROP_VINEYARD = "btt.generator.field.menu.crop-type.vineyard";
            public static final String CROP_PEAR = "btt.generator.field.menu.crop-type.pear";
            public static final String CROP_CATTLE = "btt.generator.field.menu.crop-type.cattle";
            public static final String CROP_MEADOW = "btt.generator.field.menu.crop-type.meadow";
            public static final String CROP_HARVESTED = "btt.generator.field.menu.crop-type.harvested";
            public static final String CROP_OTHER = "btt.generator.field.menu.crop-type.other";
            public static final String WIKI_LINK = "btt.generator.field.menu.wiki-link";
            public static final String STAGE_ITEM_LORE = "btt.generator.field.menu.crop-stage.item-lore";
            public static final String STAGE_LOW = "btt.generator.field.menu.crop-stage.low";
            public static final String STAGE_TALL = "btt.generator.field.menu.crop-stage.tall";
            public static final String STAGE_LIGHT = "btt.generator.field.menu.crop-stage.light";
            public static final String STAGE_DARK = "btt.generator.field.menu.crop-stage.dark";
            public static final String STAGE_HARVESTED = "btt.generator.field.menu.crop-stage.harvested";
            public static final String STAGE_DRY = "btt.generator.field.menu.crop-stage.dry";
            public static final String STAGE_WET = "btt.generator.field.menu.crop-stage.wet";
        }

        public static final class Rail {
            private Rail() {
            }

            public static final String UNSUPPORTED_SELECTION = "btt.generator.rail.unsupported-selection";
            public static final String ALREADY_RUNNING = "btt.generator.rail.already-running";

            public static final String VALIDATING = "btt.generator.rail.validating";
            public static final String PREPARING_TERRAIN = "btt.generator.rail.preparing-terrain";
            public static final String REGION_NOT_READABLE = "btt.generator.rail.region-not-readable";
            public static final String PARALLEL_TRACKS_FAILED = "btt.generator.rail.parallel-tracks-failed";
            public static final String TRACKS_OVERLAP = "btt.generator.rail.tracks-overlap";
            public static final String TOO_MANY_BLOCKS = "btt.generator.rail.too-many-blocks";
            public static final String QUEUED = "btt.generator.rail.queued";
            public static final String OUTSIDE_SAFE_AREA = "btt.generator.rail.outside-safe-area";
            public static final String NEED_TWO_POINTS = "btt.generator.rail.need-two-points";
            public static final String TOO_MANY_POINTS = "btt.generator.rail.too-many-points";
            public static final String INVALID_PATH = "btt.generator.rail.invalid-path";
            public static final String PATH_TOO_LONG = "btt.generator.rail.path-too-long";
            public static final String UNSAFE_PREP = "btt.generator.rail.unsafe-prep";
            public static final String SELECTION_TOO_WIDE = "btt.generator.rail.selection-too-wide";
            public static final String PREPARE_TOO_MANY_BLOCKS = "btt.generator.rail.prepare-too-many-blocks";
            public static final String TRACK_COUNT_RANGE = "btt.generator.rail.track-count-range";
            public static final String TRACK_SPACING_RANGE = "btt.generator.rail.track-spacing-range";
            public static final String EVERY_TRACK_SPACING_RANGE = "btt.generator.rail.every-track-spacing-range";

            public static final class Menu {
                private Menu() {
                }

                public static final String TYPE_TITLE = "btt.generator.rail.menu.type.title";
                public static final String EDITOR_TITLE = "btt.generator.rail.menu.editor.title";
                public static final String OVERHEAD_TITLE = "btt.generator.rail.menu.overhead.title";
                public static final String TRACK_SPACING_TITLE = "btt.generator.rail.menu.track-spacing.title";
                public static final String ENABLED = "btt.generator.rail.menu.enabled";
                public static final String DISABLED = "btt.generator.rail.menu.disabled";
                public static final String NONE = "btt.generator.rail.menu.none";

                public static final String SEARCH_NAME = "btt.generator.rail.menu.type.search.name";
                public static final String SEARCH_LORE = "btt.generator.rail.menu.type.search.lore";
                public static final String SEARCH_FILTER_ALL = "btt.generator.rail.menu.type.search.filter-all";
                public static final String CREATE_NAME = "btt.generator.rail.menu.type.create.name";
                public static final String CREATE_LORE = "btt.generator.rail.menu.type.create.lore";
                public static final String RELOAD_NAME = "btt.generator.rail.menu.type.reload.name";
                public static final String RELOAD_LORE = "btt.generator.rail.menu.type.reload.lore";
                public static final String BULK_DELETE_NAME = "btt.generator.rail.menu.type.bulk-delete.name";
                public static final String BULK_DELETE_NAME_ACTIVE = "btt.generator.rail.menu.type.bulk-delete.name-active";
                public static final String BULK_DELETE_LORE = "btt.generator.rail.menu.type.bulk-delete.lore";
                public static final String BULK_DELETE_LORE_EMPTY = "btt.generator.rail.menu.type.bulk-delete.lore-empty";
                public static final String BULK_DELETE_LORE_CONFIRM = "btt.generator.rail.menu.type.bulk-delete.lore-confirm";
                public static final String PAGE_CURRENT = "btt.generator.rail.menu.type.page.current";
                public static final String PAGE_CURRENT_LORE = "btt.generator.rail.menu.type.page.current-lore";
                public static final String PAGE_PREV_NONE = "btt.generator.rail.menu.type.page.prev-none";
                public static final String PAGE_PREV_NONE_LORE = "btt.generator.rail.menu.type.page.prev-none-lore";
                public static final String PAGE_PREV = "btt.generator.rail.menu.type.page.prev";
                public static final String PAGE_PREV_LORE = "btt.generator.rail.menu.type.page.prev-lore";
                public static final String PAGE_NEXT_NONE = "btt.generator.rail.menu.type.page.next-none";
                public static final String PAGE_NEXT_NONE_LORE = "btt.generator.rail.menu.type.page.next-none-lore";
                public static final String PAGE_NEXT = "btt.generator.rail.menu.type.page.next";
                public static final String PAGE_NEXT_LORE = "btt.generator.rail.menu.type.page.next-lore";
                public static final String SEARCH_ANVIL_TITLE = "btt.generator.rail.menu.type.search.anvil-title";
                public static final String SEARCH_ANVIL_ITEM = "btt.generator.rail.menu.type.search.anvil-item";

                public static final String DESELECTED = "btt.generator.rail.menu.type.deselected";
                public static final String SELECTED = "btt.generator.rail.menu.type.selected";
                public static final String OPENING_COPY = "btt.generator.rail.menu.type.opening-copy";
                public static final String EDITING = "btt.generator.rail.menu.type.editing";
                public static final String NO_LONGER_EXISTS = "btt.generator.rail.menu.type.no-longer-exists";
                public static final String BUILTIN_NO_DELETE = "btt.generator.rail.menu.type.builtin-no-delete";
                public static final String DELETED = "btt.generator.rail.menu.type.deleted";
                public static final String ONLY_CUSTOM_BULK = "btt.generator.rail.menu.type.only-custom-bulk";
                public static final String REMOVED_FROM_BULK = "btt.generator.rail.menu.type.removed-from-bulk";
                public static final String BULK_DELETED = "btt.generator.rail.menu.type.bulk-deleted";
                public static final String CREATING_NEW = "btt.generator.rail.menu.type.creating-new";
                public static final String RELOADED = "btt.generator.rail.menu.type.reloaded";
                public static final String SELECT_BEFORE_GENERATE = "btt.generator.rail.menu.type.select-before-generate";
                public static final String ALREADY_FIRST_PAGE = "btt.generator.rail.menu.type.already-first-page";
                public static final String NO_NEXT_PAGE = "btt.generator.rail.menu.type.no-next-page";
                public static final String OPENED_PAGE = "btt.generator.rail.menu.type.opened-page";

                public static final String LORE_LEFT_SELECT = "btt.generator.rail.menu.type.lore.left-select";
                public static final String LORE_RIGHT_COPY = "btt.generator.rail.menu.type.lore.right-copy";
                public static final String LORE_RIGHT_EDIT = "btt.generator.rail.menu.type.lore.right-edit";
                public static final String LORE_DROP_COPY = "btt.generator.rail.menu.type.lore.drop-copy";
                public static final String LORE_SHIFT_RIGHT_DELETE = "btt.generator.rail.menu.type.lore.shift-right-delete";
                public static final String LORE_SHIFT_LEFT_BULK = "btt.generator.rail.menu.type.lore.shift-left-bulk";
                public static final String LORE_RAIL_BLOCK = "btt.generator.rail.menu.type.lore.rail-block";
                public static final String LORE_BLOCKS_BELOW = "btt.generator.rail.menu.type.lore.blocks-below";
                public static final String LORE_TRACK_SWITCHES = "btt.generator.rail.menu.type.lore.track-switches";
                public static final String LORE_SLEEPERS_NONE = "btt.generator.rail.menu.type.lore.sleepers-none";
                public static final String LORE_SLEEPERS = "btt.generator.rail.menu.type.lore.sleepers";
                public static final String LORE_TRACKS_ONE = "btt.generator.rail.menu.type.lore.tracks-one";
                public static final String LORE_TRACKS = "btt.generator.rail.menu.type.lore.tracks";
                public static final String LORE_OVERHEAD_POLES = "btt.generator.rail.menu.type.lore.overhead-poles";
                public static final String LORE_POLE_BLOCK = "btt.generator.rail.menu.type.lore.pole-block";
                public static final String LORE_TOP_SUPPORT = "btt.generator.rail.menu.type.lore.top-support";
                public static final String LORE_POLE_SPACING = "btt.generator.rail.menu.type.lore.pole-spacing";
                public static final String LORE_OVERHEAD_WIRES = "btt.generator.rail.menu.type.lore.overhead-wires";
                public static final String LORE_WIRE_BLOCKS = "btt.generator.rail.menu.type.lore.wire-blocks";
                public static final String SELECTED_FALLBACK_NAME = "btt.generator.rail.menu.type.selected-fallback-name";
                public static final String ORIGINAL_ICON = "btt.generator.rail.menu.type.original-icon";
                public static final String GLINT_HINT = "btt.generator.rail.menu.type.glint-hint";
                public static final String SELECTED_FOR_BULK = "btt.generator.rail.menu.type.selected-for-bulk";

                public static final String EDITOR_NAME_LABEL = "btt.generator.rail.menu.editor.name-label";
                public static final String EDITOR_NAME_LORE = "btt.generator.rail.menu.editor.name-lore";
                public static final String EDITOR_TRACK_COUNT = "btt.generator.rail.menu.editor.track-count";
                public static final String EDITOR_TRACK_SPACING = "btt.generator.rail.menu.editor.track-spacing";
                public static final String EDITOR_TRACK_SPACINGS = "btt.generator.rail.menu.editor.track-spacings";
                public static final String EDITOR_TRACK_SPACINGS_LORE = "btt.generator.rail.menu.editor.track-spacings-lore";
                public static final String EDITOR_SLEEPER_SPACING = "btt.generator.rail.menu.editor.sleeper-spacing";
                public static final String EDITOR_RAIL_BLOCK = "btt.generator.rail.menu.editor.rail-block";
                public static final String EDITOR_RAIL_BLOCK_DESC = "btt.generator.rail.menu.editor.rail-block-desc";
                public static final String EDITOR_BLOCKS_BELOW = "btt.generator.rail.menu.editor.blocks-below";
                public static final String EDITOR_BLOCKS_BELOW_DESC = "btt.generator.rail.menu.editor.blocks-below-desc";
                public static final String EDITOR_SLEEPER_BLOCK = "btt.generator.rail.menu.editor.sleeper-block";
                public static final String EDITOR_SLEEPER_BLOCK_DESC = "btt.generator.rail.menu.editor.sleeper-block-desc";
                public static final String EDITOR_MENU_ICON = "btt.generator.rail.menu.editor.menu-icon";
                public static final String EDITOR_MENU_ICON_DESC = "btt.generator.rail.menu.editor.menu-icon-desc";
                public static final String EDITOR_OVERHEAD = "btt.generator.rail.menu.editor.overhead";
                public static final String EDITOR_OVERHEAD_LORE = "btt.generator.rail.menu.editor.overhead-lore";
                public static final String EDITOR_TRACK_SWITCHES = "btt.generator.rail.menu.editor.track-switches";
                public static final String EDITOR_TRACK_SWITCHES_LORE = "btt.generator.rail.menu.editor.track-switches-lore";
                public static final String EDITOR_SAVE = "btt.generator.rail.menu.editor.save";
                public static final String EDITOR_UPDATE = "btt.generator.rail.menu.editor.update";
                public static final String EDITOR_NAME_EMPTY = "btt.generator.rail.menu.editor.name-empty";
                public static final String EDITOR_NAME_SET = "btt.generator.rail.menu.editor.name-set";
                public static final String EDITOR_ANVIL_TITLE = "btt.generator.rail.menu.editor.anvil-title";
                public static final String EDITOR_ANVIL_ITEM = "btt.generator.rail.menu.editor.anvil-item";
                public static final String EDITOR_SAVED = "btt.generator.rail.menu.editor.saved";
                public static final String EDITOR_CLICK_MATERIALS = "btt.generator.rail.menu.editor.click-materials";
                public static final String EDITOR_CLICK_BLOCK = "btt.generator.rail.menu.editor.click-block";
                public static final String EDITOR_DEFAULT_NAME = "btt.generator.rail.menu.editor.default-name";

                public static final String OVERHEAD_POLES = "btt.generator.rail.menu.overhead.poles";
                public static final String OVERHEAD_POLES_DESC = "btt.generator.rail.menu.overhead.poles-desc";
                public static final String OVERHEAD_WIRES = "btt.generator.rail.menu.overhead.wires";
                public static final String OVERHEAD_WIRES_DESC = "btt.generator.rail.menu.overhead.wires-desc";
                public static final String OVERHEAD_POLE_BLOCK = "btt.generator.rail.menu.overhead.pole-block";
                public static final String OVERHEAD_SUPPORT_BLOCK = "btt.generator.rail.menu.overhead.support-block";
                public static final String OVERHEAD_WIRE_BLOCK = "btt.generator.rail.menu.overhead.wire-block";
                public static final String OVERHEAD_POLE_OFFSET = "btt.generator.rail.menu.overhead.pole-offset";
                public static final String OVERHEAD_POLE_HEIGHT = "btt.generator.rail.menu.overhead.pole-height";
                public static final String OVERHEAD_TOGGLE_LORE = "btt.generator.rail.menu.overhead.toggle-lore";
                public static final String OVERHEAD_MATERIALS_LORE = "btt.generator.rail.menu.overhead.materials-lore";
                public static final String OVERHEAD_POLE_SPACING = "btt.generator.rail.menu.overhead.pole-spacing";
                public static final String OVERHEAD_POLE_SPACING_LORE = "btt.generator.rail.menu.overhead.pole-spacing-lore";
                public static final String OVERHEAD_SPACING_INVALID = "btt.generator.rail.menu.overhead.spacing-invalid";
                public static final String OVERHEAD_SPACING_SET = "btt.generator.rail.menu.overhead.spacing-set";
                public static final String OVERHEAD_ANVIL_TITLE = "btt.generator.rail.menu.overhead.anvil-title";
                public static final String OVERHEAD_ANVIL_ITEM = "btt.generator.rail.menu.overhead.anvil-item";
                public static final String TRACK_GAP_NAME = "btt.generator.rail.menu.track-spacing.gap-name";

                public static final String PICKER_RAIL_BLOCK = "btt.generator.rail.menu.picker.rail-block";
                public static final String PICKER_BLOCK_BELOW = "btt.generator.rail.menu.picker.block-below";
                public static final String PICKER_SLEEPER = "btt.generator.rail.menu.picker.sleeper";
                public static final String PICKER_ICON = "btt.generator.rail.menu.picker.icon";
                public static final String PICKER_POLE = "btt.generator.rail.menu.picker.pole";
                public static final String PICKER_SUPPORT = "btt.generator.rail.menu.picker.support";
                public static final String PICKER_WIRE = "btt.generator.rail.menu.picker.wire";
                public static final String PICKER_NEED_ITEM = "btt.generator.rail.menu.picker.need-item";
                public static final String PICKER_NEED_BLOCK = "btt.generator.rail.menu.picker.need-block";
                public static final String VALIDATION_ERROR = "btt.generator.rail.menu.validation-error";
                public static final String PICKER_USE_OWN = "btt.generator.rail.menu.picker.use-own";
                public static final String PICKER_USE_OWN_LORE = "btt.generator.rail.menu.picker.use-own-lore";
                public static final String PICKER_SELECTED = "btt.generator.rail.menu.picker.selected";
                public static final String PICKER_SELECTED_LORE = "btt.generator.rail.menu.picker.selected-lore";
                public static final String PICKER_SELECTED_HINT_MULTI = "btt.generator.rail.menu.picker.selected-hint-multi";
                public static final String PICKER_SELECTED_HINT_SINGLE = "btt.generator.rail.menu.picker.selected-hint-single";
                public static final String EDITOR_UNIT_TRACKS = "btt.generator.rail.menu.editor.unit-tracks";
                public static final String EDITOR_UNIT_BLOCKS = "btt.generator.rail.menu.editor.unit-blocks";
                public static final String EDITOR_OVERHEAD_POLES_LINE = "btt.generator.rail.menu.editor.overhead-poles-line";
                public static final String EDITOR_OVERHEAD_WIRES_LINE = "btt.generator.rail.menu.editor.overhead-wires-line";
                public static final String TRACK_SPACING_DONE = "btt.generator.rail.menu.track-spacing.done";
            }
        }

        public static final class Kml {
            private Kml() {
            }

            public static final String PLAYER_ONLY = "btt.generator.kml.player-only";
            public static final String USE_GEO_ALIAS = "btt.generator.kml.use-geo-alias";
            public static final String COMMANDBLOCK_NO_METADATA = "btt.generator.kml.commandblock-no-metadata";
            public static final String INVALID_BLOCK_METADATA = "btt.generator.kml.invalid-block-metadata";
            public static final String TIMING = "btt.generator.kml.timing";
            public static final String CHANGED_BLOCKS = "btt.generator.kml.changed-blocks";
            public static final String UNLOADED_CHUNKS = "btt.generator.kml.unloaded-chunks";
            public static final String TOO_FAR = "btt.generator.kml.too-far";
            public static final String INVALID_EXTEND_BLOCK = "btt.generator.kml.invalid-extend-block";
            public static final String INVALID_BLOCK_TYPE = "btt.generator.kml.invalid-block-type";
            public static final String COMMANDBLOCK_CREATED = "btt.generator.kml.commandblock-created";
            public static final String UNDO_NO_HISTORY = "btt.generator.kml.undo-no-history";
            public static final String UNDO_NOTHING = "btt.generator.kml.undo-nothing";
            public static final String UNDO_SUCCESS = "btt.generator.kml.undo-success";
            public static final String DEBUG_SUMMARY = "btt.generator.kml.debug-summary";
            public static final String PARSE_ERROR = "btt.generator.kml.parse-error";
            public static final String UNSUPPORTED_GEOMETRY = "btt.generator.kml.unsupported-geometry";
            public static final String NESTED_MULTIGEOMETRY = "btt.generator.kml.nested-multigeometry";
            public static final String DEBUG_NO_CHANGES = "btt.generator.kml.debug-no-changes";
        }
    }
}
