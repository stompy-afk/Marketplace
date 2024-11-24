package lol.stompy.marketplace.profile;

import lombok.AllArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

@AllArgsConstructor
public class ProfileListener implements Listener {

    private final ProfileHandler profileHandler;

    @EventHandler
    public final void onAsyncPrePlayerJoinEvent(AsyncPlayerPreLoginEvent event) {
        final UUID uuid = event.getUniqueId();

        if (!event.getLoginResult().equals(AsyncPlayerPreLoginEvent.Result.ALLOWED))
            return;

        profileHandler.load(uuid, true);
    }

    @EventHandler
    public final void onPlayerQuitEvent(PlayerQuitEvent event) {
        profileHandler.getProfile(event.getPlayer().getUniqueId()).ifPresent(profile -> profileHandler.handleRemoval(profile, true));
    }

}
