package lol.stompy.marketplace.market.transaction;

import lol.stompy.marketplace.profile.Profile;
import lol.stompy.marketplace.util.menu.buttons.Button;
import lol.stompy.marketplace.util.menu.pagination.PaginatedMenu;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MarketTransactionMenu extends PaginatedMenu {

    private final Profile profile;

    /**
     * Constructor to make a new menu object
     *
     * @param player the player to create the menu for
     */
    public MarketTransactionMenu(Player player, Profile profile) {
        super(player, "Your transactions", 27);

        this.profile = profile;
    }


    @Override
    public Map<Integer, Button> getButtons() {
        final Map<Integer, Button> buttonMap = new HashMap<>();
        final AtomicInteger atomicInteger = new AtomicInteger(0);

        profile.getMarketTransactions().forEach(marketTransaction -> {
            buttonMap.put(atomicInteger.get(), new Button(marketTransaction.getStack()));
            atomicInteger.getAndIncrement();
        });

        return buttonMap;
    }
}
