package lol.stompy.marketplace.util.menu;

import lol.stompy.marketplace.util.menu.listener.MenuListener;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MenuHandler {

    /**
     * -- GETTER --
     *  Get the instance of the menu handler
     *
     * @return the instance
     */
    @Getter
    private static MenuHandler instance;
    private final Map<Player, Menu> menus = new HashMap<>();

    /**
     * Constructor to create a new menu handler
     *
     * @param plugin the plugin to register the handler to
     */
    public MenuHandler(JavaPlugin plugin) {
        instance = this;

        plugin.getServer().getPluginManager().registerEvents(new MenuListener(this), plugin);
    }

    /**
     * Register a new menu to a player.
     *
     * @param player the player to register the menu for
     * @param menu   the menu to register
     */
    public void register(Player player, Menu menu) {
        this.menus.put(player, menu);
    }

    /**
     * Remove an player entry from the menus map.
     *
     * @param player the player to remove
     */
    public void unregister(Player player) {
        this.menus.remove(player);
    }

    /**
     * Find a {@link Menu} by a player
     *
     * @param player the player to get the menu by
     * @return the optional of the menu
     */
    public Optional<Menu> findMenu(Player player) {
        return Optional.ofNullable(this.menus.getOrDefault(player, null));
    }

    /**
     * Updates menus with a specific title
     *
     * @param title of the menu
     */

    public void updateMenus(String title) {
        menus.values().stream().filter(menu -> menu.getTitle().equalsIgnoreCase(title)).forEach(Menu::updateMenu);
    }

    /**
     * Updates all menus
     */

    public void updateMenus() {
        menus.values().forEach(Menu::updateMenu);
    }

    public final Collection<Menu> getMenus() {
        return this.menus.values();
    }

}