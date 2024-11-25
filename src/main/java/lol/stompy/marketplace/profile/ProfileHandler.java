package lol.stompy.marketplace.profile;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lol.stompy.marketplace.Marketplace;
import org.bson.Document;

import java.util.*;

public class ProfileHandler {

    private final Map<UUID, Profile> profileMap;
    private final Marketplace marketplace;

    /**
     * profile handler handles all profile related stuff
     *
     * @param marketplace instance of main
     */

    public ProfileHandler(Marketplace marketplace) {
        this.profileMap = new HashMap<>();
        this.marketplace = marketplace;

        marketplace.getServer().getPluginManager().registerEvents(new ProfileListener(this), marketplace);
    }

    /**
     * loads a profile
     *
     * @param uuid uuid of profile to save
     * @param async to do task async or not
     */

    public final void load(UUID uuid, boolean async) {

        if (async) {
            marketplace.getServer().getScheduler().runTaskAsynchronously(marketplace, () -> load(uuid, false));
            return;
        }

        final Document document = marketplace.getMongoHandler().getProfiles().find(Filters.eq("_id", uuid.toString())).first();

        if (document == null) {
            this.save(profileMap.put(uuid, new Profile(uuid)), false);
            return;
        }

        profileMap.put(uuid, new Profile(document));
    }

    /**
     * handles the removal of a profile
     *
     * @param profile profile to remove
     * @param async to do task async or not
     */

    public final void handleRemoval(Profile profile, boolean async) {
        this.save(profile, async);
        profileMap.remove(profile.getUuid());
    }

    /**
     * saves a profile to the mongo db
     *
     * @param profile profile to save
     * @param async to do task async or not
     */

    public final void save(Profile profile, boolean async) {

        if (async) {
            marketplace.getServer().getScheduler().runTaskAsynchronously(marketplace, () -> save(profile, false));
            return;
        }

        final Document document = marketplace.getMongoHandler().getProfiles().find(Filters.eq("_id", profile.getUuid().toString())).first();

        if (document == null) {
            marketplace.getMongoHandler().getProfiles().insertOne(profile.toBson());
            return;
        }

        marketplace.getMongoHandler().getProfiles().replaceOne(document, profile.toBson(), new ReplaceOptions().upsert(true));
    }

    /**
     * gets profiles
     *
     * @return {@link Collection<Profile>}
     */

    public final Collection<Profile> getProfiles() {
        return profileMap.values();
    }

    /**
     * gets a profile by the uuid
     *
     * @param uuid uuid to get profile off
     * @return {@link Optional<Profile>}
     */

    public final Optional<Profile> getProfile(UUID uuid) {
        return Optional.ofNullable(profileMap.get(uuid));
    }

}
