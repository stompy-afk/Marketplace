package lol.stompy.marketplace.market;

import lol.stompy.marketplace.profile.Profile;
import lol.stompy.marketplace.util.CC;
import lol.stompy.marketplace.util.Serializer;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

@Getter
public class MarketItem {

    private final UUID uuid;

    private final UUID owner;
    private final ItemStack stack;
    private final int cost;

    @Setter
    private boolean blackMarketItem;

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

        //save nbt tags
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

        //save nbt tags
    }

    /**
     * gets the item stack
     *
     * @return {@link ItemStack}
     */

    public ItemStack getStack() {
        final ItemStack clone = stack.clone();

        ItemMeta meta = clone.getItemMeta();

        if (meta == null)
            meta = Bukkit.getItemFactory().getItemMeta(clone.getType());

        if (meta.getLore() == null || meta.getLore().isEmpty()) {
            meta.setLore(List.of(CC.translate("&eCost&7: " + (blackMarketItem ? cost / 2 : cost))));
            clone.setItemMeta(meta);
            return clone;
        }

        final List<String> lore = meta.getLore();
        lore.add("&eCost&7: " + (blackMarketItem ? cost / 2 : cost));

        meta.setLore(lore);
        clone.setItemMeta(meta);
        return clone;
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
