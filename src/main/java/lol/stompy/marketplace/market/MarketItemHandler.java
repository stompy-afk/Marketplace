package lol.stompy.marketplace.market;

import lol.stompy.marketplace.Marketplace;
import lol.stompy.marketplace.profile.Profile;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MarketItemHandler {

    private final List<MarketItem> marketItemList;
    private final Marketplace marketplace;

    /**
     * handles all market items
     *
     * @param marketplace instance of main
     */

    public MarketItemHandler(Marketplace marketplace) {
        this.marketplace = marketplace;
        this.marketItemList = new ArrayList<>();
    }

    /**
     * adds a market item to the market
     *
     * @param player player to get the market from
     * @param profile profile of player
     * @param stack itemstack to add
     * @param cost cost to be added to market
     */

    public final void add(Player player, Profile profile, ItemStack stack, int cost) {
        final MarketItem marketItem = new MarketItem(profile, stack, cost);

        profile.addMarketItem(marketItem);
        player.getInventory().setItemInMainHand(null);
    }

}
