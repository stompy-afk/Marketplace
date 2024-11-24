package lol.stompy.marketplace.profile;

import lol.stompy.marketplace.Marketplace;
import lol.stompy.marketplace.market.MarketItem;
import lol.stompy.marketplace.market.transaction.MarketTransaction;
import lombok.Getter;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Profile {

    private final UUID uuid;

    private final List<MarketItem> marketItemList;
    private final List<MarketTransaction> marketTransactions;

    /**
     * creates a profile
     *
     * @param uuid uuid of profile
     */

    public Profile(UUID uuid) {
        this.uuid = uuid;

        this.marketItemList = new ArrayList<>();
        this.marketTransactions = new ArrayList<>();
    }

    /**
     * creates a profile from a document
     *
     * @param document document to create profile out of
     */

    public Profile(Document document, Marketplace marketplace) {
        this.uuid = UUID.fromString(document.getString("uuid"));

        this.marketItemList = document.getList("marketItems", String.class).stream().map(s -> new MarketItem(this, s)).toList();
        this.marketTransactions = document.getList("marketTransactions", String.class).stream().map(MarketTransaction::new).toList();
    }

    /**
     * adds a market item to the list
     *
     * @param marketItem {@link MarketItem}
     */

    public final void addMarketItem(MarketItem marketItem) {
        marketItemList.add(marketItem);
    }

    /**
     * puts all the data of the profile to a document
     *
     * @return {@link Document}
     */

    public final Document toBson() {
        return new Document("_id", uuid.toString())
                .append("marketTransactions", marketTransactions.stream().map(MarketTransaction::toString).toList())
                .append("marketItems", marketItemList.stream().map(MarketItem::toString).toList());
    }

}
