package net.buildtheearth.buildteamtools.modules.network.api;

import com.alpsbte.alpslib.utils.ChatHelper;
import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.model.GeographicalCoordinate;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class NominatimAPI {
    private static final String BASE_URL = "https://nominatim.openstreetmap.org/";

    public static @NotNull CompletableFuture<String> getAddressFromCoordinatesAsync(
            @NotNull GeographicalCoordinate coordinates
    ) {
        CompletableFuture<String> future = new CompletableFuture<>();

        String url = BASE_URL + "reverse?format=jsonv2"
                + "&lat=" + coordinates.latitude()
                + "&lon=" + coordinates.longitude()
                + "&addressdetails=1"
                + "&accept-language=en";
        ChatHelper.getStandardComponent(false, url);
        String userAgent = BuildTeamTools.getInstance().getMainConfig().userAgent();
        API.getAsync(url, userAgent, new API.ApiResponseCallback() {
            @Override
            public void onResponse(String response) {
                try {
                    String address = formatAddressFromResponse(response);
                    future.complete(address);
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            }

            @Override
            public void onFailure(IOException e) {
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    private static String formatAddressFromResponse(String response) {
        JSONObject jsonObject = API.createJSONObject(response);

        ChatHelper.logDebug(
                "Response from Nominatim: %s",
                jsonObject
        );

        Object displayName = jsonObject.get("display_name");

        if (displayName == null || displayName.toString().isBlank()) {
            ChatHelper.logError(
                    "No address data found for these coordinates."
            );
            return "";
        }

        return displayName.toString();
    }
}
