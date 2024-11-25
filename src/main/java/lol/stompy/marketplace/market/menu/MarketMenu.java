package lol.stompy.marketplace.market.menu;

import lol.stompy.marketplace.Marketplace;
import lol.stompy.marketplace.market.MarketItemHandler;
import lol.stompy.marketplace.profile.Profile;
import lol.stompy.marketplace.util.CC;
import lol.stompy.marketplace.util.menu.buttons.Button;
import lol.stompy.marketplace.util.menu.pagination.PaginatedMenu;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class MarketMenu extends PaginatedMenu {

    private final Marketplace marketplace;
    private final MarketItemHandler marketItemHandler;

    @Getter
    private final boolean blackMarket;

    /**
     * Constructor to make a new menu object
     *
     * @param player the player to create the menu for
     */

    public MarketMenu(Player player, Marketplace marketplace, boolean blackMarket) {
        super(player, "Market Place", 27);

        this.blackMarket = blackMarket;
        this.marketplace = marketplace;
        this.marketItemHandler = marketplace.getMarketItemHandler();
    }

    @Override
    public Map<Integer, Button> getButtons() {
        final Map<Integer, Button> buttonMap = new HashMap<>();
        final AtomicInteger i = new AtomicInteger();

        marketItemHandler.getMarketItemList().forEach(marketItem -> {
            if (blackMarket && !marketItem.isBlackMarketItem())
                return;

            buttonMap.put(i.get(), new Button(marketItem.getStack())).setClickAction(action -> {
                action.setCancelled(true);

                final Optional<Profile> optionalProfile = marketplace.getProfileHandler().getProfile(player.getUniqueId());

                if (optionalProfile.isEmpty()) {
                    player.sendMessage(CC.translate("&cYour profile does not exist!"));
                    return;
                }

                final double balance = marketplace.getEconomy().getBalance(player);

                if (balance < (blackMarket ? (double) marketItem.getCost() / 2 : marketItem.getCost())) {
                    player.sendMessage(CC.translate("&cYou cannot buy this item, as you don't have enough money."));
                    return;
                }

                marketItemHandler.purchase(marketItem, player, optionalProfile.get());
                player.sendMessage(CC.translate("&aYou've purchased a market item for " + (blackMarket ? marketItem.getCost() / 2 : marketItem.getCost())));

                this.updateMenu();
            });

            i.getAndIncrement();
        });

        return buttonMap;
    }

}
