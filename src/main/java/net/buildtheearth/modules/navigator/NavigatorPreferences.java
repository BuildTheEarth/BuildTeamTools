package net.buildtheearth.modules.navigator;

public class NavigatorPreferences {

    /**
     * Creates each preference list and adds it to the big list of everyone's preferences
     *//*
    private void initialisePreferences()
    {
        this.userPreferences = new HashMap<>(PreferenceType.values().length+1, 1);
        HashMap<UUID, Boolean> navigatorEnabled = new HashMap<>();
        this.userPreferences.put(PreferenceType.NavigatorEnabled, navigatorEnabled);
    }

    public void fetchAndLoadPreferences(UUID uuid)
    {
        User user = User.fetchUser(uuid);
        userPreferences.get(PreferenceType.NavigatorEnabled).put(uuid, user.bNavigatorEnabled);
    }

    //Toggles a boolean preference
    public void updatePreference(PreferenceType preferenceType, UUID uuid)
    {
        if (userPreferences.get(preferenceType).get(uuid) instanceof Boolean)
        {
            Boolean bValue = (Boolean) userPreferences.get(preferenceType).get(uuid);
            userPreferences.get(preferenceType).replace(uuid, Boolean.valueOf(!bValue.booleanValue()));
        }

        //Todo: Make the change in the DB as well
    }*/
}
