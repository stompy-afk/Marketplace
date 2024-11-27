package lol.stompy.marketplace.market;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lol.stompy.marketplace.Marketplace;
import lol.stompy.marketplace.market.menu.MarketMainMenu;
import lol.stompy.marketplace.market.transaction.MarketTransaction;
import lol.stompy.marketplace.profile.Profile;
import lol.stompy.marketplace.util.menu.Menu;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class MarketItemHandler {

    @Getter
    private final List<MarketItem> marketItemList;
    private final List<UUID> marketItemRemovedList;

    private final Random random;

    private final Marketplace marketplace;

    /**
     * handles all market items
     *
     * @param marketplace instance of main
     */

    public MarketItemHandler(Marketplace marketplace) {
        this.marketplace = marketplace;

        this.marketItemList = new ArrayList<>();

        this.marketItemRemovedList = new ArrayList<>();
        this.random = new Random();

        this.load();
    }

    /**
     * loads all market items
     */

    private void load() {
        marketplace.getServer().getScheduler().runTaskAsynchronously(marketplace, () -> {
            for (Document document : marketplace.getMongoHandler().getMarketItems().find()) {
                marketItemList.add(new MarketItem(document));
            }
        });

        marketplace.getServer().getScheduler().runTaskTimer(marketplace, () -> {
            for (MarketItem marketItem : marketItemList) {
                if (marketItem.isBlackMarketItem())
                    marketItem.setBlackMarketItem(false);

                if (random.nextDouble() <= 0.35)
                    marketItem.setBlackMarketItem(true);
            }
        }, 20L * 5 * 60, 20L * 5 * 60);
    }

    /**
     * saves a market item to the database
     *
     * @param marketItem market item to save
     * @param async      to do task async or not
     */

    private void save(MarketItem marketItem, boolean async) {

        if (async) {
            marketplace.getServer().getScheduler().runTaskAsynchronously(marketplace, () -> save(marketItem, false));
            return;
        }

        final Document document = marketplace.getMongoHandler().getMarketItems().find(Filters.eq("_id", marketItem.getUuid().toString())).first();

        if (document == null) {
            marketplace.getMongoHandler().getMarketItems().insertOne(marketItem.toBson());
            return;
        }

        marketplace.getMongoHandler().getMarketItems().replaceOne(document, marketItem.toBson(), new ReplaceOptions().upsert(true));
    }

    /**
     * adds a market item to the market
     *
     * @param profile profile of player
     * @param stack   itemstack to add
     * @param cost    cost to be added to market
     */

    public final void add(Profile profile, ItemStack stack, int cost) {
        final MarketItem marketItem = new MarketItem(profile, stack, cost);

        marketItemList.add(marketItem);
        this.save(marketItem, true);
    }

    /**
     * handles the purchase of a market item
     *
     * @param marketItem market item to handle purchase off
     * @param player     player of purchaser
     * @param purchaser  profile of purchaser
     */

    public final void purchase(MarketItem marketItem, Player player, Profile purchaser) {
        final boolean blackMarket = marketItem.isBlackMarketItem();

        marketItemList.remove(marketItem);
        marketItemRemovedList.add(marketItem.getUuid());

        for (Menu menu : marketplace.getMenuHandler().getMenus()) {
            if (menu.getPlayer().getUniqueId().equals(player.getUniqueId()) || !(menu instanceof MarketMainMenu))
                return;

            final MarketMainMenu marketMenu = (MarketMainMenu) menu;

            if (marketMenu.isBlackMarket() && blackMarket) {
                menu.updateMenu();
                return;
            }

            menu.updateMenu();
        }

        if (player.getInventory().firstEmpty() == -1)
            player.getWorld().dropItem(player.getLocation(), marketItem.getStack());
        else
            player.getInventory().addItem(marketItem.getStack());

        if (blackMarket) {
            marketplace.getEconomy().bankWithdraw(player.getName(), (double) marketItem.getCost() / 2);
            marketplace.getEconomy().bankDeposit(marketplace.getServer().getOfflinePlayer(marketItem.getOwner()).getName(), marketItem.getCost() * 2);
        } else {
            marketplace.getEconomy().bankWithdraw(player.getName(), marketItem.getCost());
            marketplace.getEconomy().bankDeposit(marketplace.getServer().getOfflinePlayer(marketItem.getOwner()).getName(), marketItem.getCost());
        }

        purchaser.addMarketTransaction(new MarketTransaction(marketItem));
    }

}
