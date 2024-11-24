package lol.stompy.marketplace.market;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lol.stompy.marketplace.Marketplace;
import lol.stompy.marketplace.profile.Profile;
import org.bson.Document;
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
    }

    /**
     * saves a market item to the database
     *
     * @param marketItem market item to save
     * @param async to do task async or not
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
     * @param stack itemstack to add
     * @param cost cost to be added to market
     */

    public final void add(Profile profile, ItemStack stack, int cost) {
        final MarketItem marketItem = new MarketItem(profile, stack, cost);

        marketItemList.add(marketItem);
        this.save(marketItem, true);
    }

}
