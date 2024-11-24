package lol.stompy.marketplace.profile;

import lol.stompy.marketplace.market.MarketItem;
import lombok.Getter;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Profile {

    private final UUID uuid;

    private final List<MarketItem> marketItemList;

    /**
     * creates a profile
     *
     * @param uuid uuid of profile
     */

    public Profile(UUID uuid) {
        this.uuid = uuid;
        this.marketItemList = new ArrayList<>();
    }

    /**
     * puts all the data of the profile to a document
     *
     * @return {@link Document}
     */

    public final Document toBson() {
        return new Document("_id", uuid.toString())
                .append("marketItems", marketItemList.stream().map(MarketItem::toString).toList());
    }

}
