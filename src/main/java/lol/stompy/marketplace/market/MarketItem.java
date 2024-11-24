package lol.stompy.marketplace.market;

import de.tr7zw.nbtapi.NBT;
import lol.stompy.marketplace.profile.Profile;
import lol.stompy.marketplace.util.Serializer;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bson.Document;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@Getter
public class MarketItem {

    private final UUID uuid;

    private final UUID owner;
    private final ItemStack stack;
    private final int cost;

    /**
     * creates a market item
     *
     * @param profile owner of item
     * @param stack   stack
     * @param cost    cost of item
     */

    public MarketItem(Profile profile, ItemStack stack, int cost) {
        this.uuid = UUID.randomUUID();

        this.owner = profile.getUuid();
        this.cost = cost;
        this.stack = stack;

        NBT.modify(stack, nbt -> {
            nbt.setInteger("cost", cost);
            nbt.setString("owner", profile.getUuid().toString());
        });
    }

    /**
     * deserializes a market item
     *
     * @param document document to get data from
     */

    @SneakyThrows
    public MarketItem(Document document) {
        this.uuid = UUID.fromString(document.getString("_id"));

        this.stack = Serializer.itemStackFromBase64(document.getString("stack"));
        this.cost = document.getInteger("cost");
        this.owner = UUID.fromString(document.getString("owner"));

        if (stack != null)
            NBT.modify(stack, nbt -> {
                nbt.setInteger("cost", cost);
                nbt.setString("owner", owner.toString());
            });
    }

    /**
     * puts all info of the market item into a document
     *
     * @return {@link Document}
     */

    public final Document toBson() {
        return new Document("_id", uuid.toString())
                .append("stack", Serializer.itemStackToBase64(stack))
                .append("cost", cost)
                .append("owner", owner.toString());
    }

}
