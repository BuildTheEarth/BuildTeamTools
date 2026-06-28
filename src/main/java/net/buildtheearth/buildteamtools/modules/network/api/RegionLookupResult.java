package net.buildtheearth.buildteamtools.modules.network.api;

import org.jetbrains.annotations.Nullable;

public record RegionLookupResult(String regionName, @Nullable String countryCodeCCA2) {
}
