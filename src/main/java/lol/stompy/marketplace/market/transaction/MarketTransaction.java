package lol.stompy.marketplace.market.transaction;

import lol.stompy.marketplace.market.MarketItem;
import lol.stompy.marketplace.util.Serializer;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MarketTransaction {

    private final ItemStack stack;

    private final int cost;
    private final UUID owner;

    private final String date;

    /**
     * records a transaction done on the market
     *
     * @param marketItem market item that got traded
     */

    public MarketTransaction(MarketItem marketItem) {
        this.stack = marketItem.getStack();

        this.owner = marketItem.getOwner();
        int cost = marketItem.getCost();

        if (marketItem.isBlackMarketItem())
            this.cost = cost / 2;
        else
            this.cost = cost;

        this.date = new Date().toString();
    }

    /**
     * de-serializes a market transaction from a string
     *
     * @param s string to deserialize
     */

    @SneakyThrows
    public MarketTransaction(String s) {
        final String[] args = s.split("//");

        this.stack = Serializer.itemStackFromBase64(args[0]);
        this.cost = Integer.parseInt(args[1]);
        this.owner = UUID.fromString(args[2]);
        this.date = args[3];
    }

    /**
     * gets the item stack
     *
     * @return {@link ItemStack}
     */

    public final ItemStack getStack() {
        final ItemStack clone = stack.clone();

        ItemMeta meta = clone.getItemMeta();

        if (meta == null)
            meta = Bukkit.getItemFactory().getItemMeta(clone.getType());

        final List<String> lore = new ArrayList<>();

        if (meta.getLore() != null && !meta.getLore().isEmpty())
            lore.addAll(meta.getLore());

        lore.add("&eSeller&7: " + Bukkit.getOfflinePlayer(owner).getName());
        lore.add("&eCost&7: " + cost);

        meta.setLore(lore);
        clone.setItemMeta(meta);
        return clone;
    }

    /**
     * serializes the class to a string
     *
     * @return {@link String}
     */

    @Override
    public String toString() {
        return Serializer.itemStackToBase64(stack) + "//" + cost + "//" + owner.toString() + "//" + date;
    }
}
