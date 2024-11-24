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
    private final List<MarketTransaction> marketTransactions;

    /**
     * creates a profile
     *
     * @param uuid uuid of profile
     */

    public Profile(UUID uuid) {
        this.uuid = uuid;
        this.marketTransactions = new ArrayList<>();
    }

    /**
     * creates a profile from a document
     *
     * @param document document to create profile out of
     */

    public Profile(Document document, Marketplace marketplace) {
        this.uuid = UUID.fromString(document.getString("uuid"));
        this.marketTransactions = document.getList("marketTransactions", String.class).stream().map(MarketTransaction::new).toList();
    }

    /**
     * puts all the data of the profile to a document
     *
     * @return {@link Document}
     */

    public final Document toBson() {
        return new Document("_id", uuid.toString())
                .append("marketTransactions", marketTransactions.stream().map(MarketTransaction::toString).toList());
    }

}
