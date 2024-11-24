package lol.stompy.marketplace.market.menu;

import lol.stompy.marketplace.util.menu.buttons.Button;
import lol.stompy.marketplace.util.menu.pagination.PaginatedMenu;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class MarketMenu extends PaginatedMenu {

    /**
     * Constructor to make a new menu object
     *
     * @param player the player to create the menu for
     */

    public MarketMenu(Player player) {
        super(player, "Market Place", 27);
    }

    @Override
    public Map<Integer, Button> getButtons() {
        final Map<Integer, Button> buttonMap = new HashMap<>();

        return buttonMap;
    }

}
