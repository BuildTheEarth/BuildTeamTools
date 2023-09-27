//This is noahs

package net.buildtheearth.buildteam.components.universal_experience.universal_tpll;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import net.buildtheearth.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ServerLocator
{
    /**
     * Gets a server from geographical coordinates
     *
     * @param lat Latitude
     * @param lon Longitude
     * @return Returns the name of the server if a valid region is found, or null if not
     */
    public static String getServerFromLocation(double lat, double lon) {
        return getServerFromLocation(lat, lon, Main.instance.getConfig().getBoolean("universal_tpll.geography.use_offline_mode"));
    }

    /**
     * Gets a server from geographical coordinates
     *
     * @param lat     Latitude
     * @param lon     Longitude
     * @param offline True to use offline database
     * @return Returns the server name if a valid region is found, or null if not
     */
    public static String getServerFromLocation(double lat, double lon, boolean offline)
    {
        //Fetches the location details of the coordinate
        Location location = offline ? getOfflineLocation(lat, lon) : getLocation(lat, lon);
        if (location == null) {
            return null;
        }
        Map<Location.Detail, String> serverInfoMap = Maps.newHashMap();

        //------------------------------------------------
        //---Finds the correct server for this location---
        //------------------------------------------------

        for (SledgehammerServer s : ServerHandler.getInstance().getServers().values()) {
            if (!s.isEarthServer()) {
                continue;
            }
            if (s.getLocations() == null || s.getLocations().isEmpty()) {
                continue;
            }
            for (Location l : s.getLocations()) {
                switch (l.detailType) {
                    case city:
                        if (l.compare(location, Location.Detail.city)) {
                            serverInfoMap.put(l.detailType, s.getInfo());
                            continue;
                        }
                        break;
                    case county:
                        if (l.compare(location, Location.Detail.county)) {
                            serverInfoMap.put(l.detailType, s.getInfo());
                            continue;
                        }
                        break;
                    case state:
                        if (l.compare(location, Location.Detail.state)) {
                            serverInfoMap.put(l.detailType, s.getInfo());
                            continue;
                        }
                        break;
                    case country:
                        if (l.compare(location, Location.Detail.country)) {
                            serverInfoMap.put(l.detailType, s.getInfo());
                            continue;
                        }
                        break;
                }
            }
        }

        if (serverInfoMap.get(Location.Detail.city) != null) {
            return serverInfoMap.get(Location.Detail.city);
        }

        if (serverInfoMap.get(Location.Detail.county) != null) {
            return serverInfoMap.get(Location.Detail.county);
        }

        if (serverInfoMap.get(Location.Detail.state) != null) {
            return serverInfoMap.get(Location.Detail.state);
        }

        if (serverInfoMap.get(Location.Detail.country) != null) {
            return serverInfoMap.get(Location.Detail.country);
        }

        return null;
    }

    /**
     * Generates {@link Location} from geographical coordinates
     *
     * @param lat Latitude
     * @param lon Longitude
     * @return {@link Location}
     */
    public static Location getLocation(double lat, double lon) {
        return getLocation(lat, lon, Main.instance.getConfig().getInt("universal_tpll.geography.zoom"));
    }

    /**
     * Generates {@link Location} from geographical coordinates
     *
     * @param lat  Latitude
     * @param lon  Longitude
     * @param zoom Zoom level
     * @return {@link Location}
     */
    public static Location getLocation(double lat, double lon, int zoom) {
        try {
            //Gets the request from the API
            String szNominatimAPI = Main.instance.getConfig().getString("universal_tpll.geography.API");
            String fullRequest = szNominatimAPI.replace("{zoom}", String.valueOf(zoom)) + "&lat=" + lat + "&accept-language=en&lon=" + lon;

            URL url = new URL(fullRequest);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("User-Agent", Main.instance.getName() + "/" + Main.instance.getDescription().getVersion());
            con.setRequestProperty("Accept", "application/json");

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                //Handles the data returned from the request
                JsonObject geocode = JsonUtils.parseString(response.toString()).getAsJsonObject();
                JsonObject address = geocode.getAsJsonObject("address");

                //Decompiles the address into city/town, county state and country
                String city = null;
                if (address.has("city")) {
                    city = address.get("city").getAsString();
                } else if (address.has("town")) {
                    city = address.get("town").getAsString();
                }
                String county = null;
                if (address.has("county")) {
                    county = address.get("county").getAsString();
                }
                String state = null;
                if (address.has("state")) {
                    state = address.get("state").getAsString();
                } else if (address.has("territory")) {
                    state = address.get("territory").getAsString();
                }
                String country = address.get("country").getAsString();

                return new Location(Location.Detail.none, city, county, state, country);
            }
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Get location from geographical coordinates from an offline bin
     *
     * @param lat Latitude
     * @param lon Longitude
     * @return {@link Location}
     */
    public static Location getOfflineLocation(double lat, double lon) {
        String[] data = offlineGeocoder.lookup((float) lon, (float) lat);
        String city = null;
        String county = null;
        String state = null;
        String country = null;
        for (String datum : data) {
            OfflineDataField o = getDataField(datum);
            if (o.type.equalsIgnoreCase("city")) {
                city = o.data;
            } else if (o.type.equalsIgnoreCase("county")) {
                county = o.data;
            } else if (o.type.equalsIgnoreCase("state")) {
                state = o.data;
            } else if (o.type.equalsIgnoreCase("country")) {
                country = o.data;
            } else if (o.admin.equals("8") && city == null) {
                city = o.data;
            } else if (o.admin.equals("6") && county == null) {
                county = o.data;
            } else if (o.admin.equals("4") && state == null) {
                state = o.data;
            } else if (o.admin.equals("2") && country == null) {
                country = o.data;
            }
        }

        return new Location(Location.Detail.none, city, county, state, country);
    }
}
