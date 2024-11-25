package lol.stompy.marketplace.market.transaction;

import lol.stompy.marketplace.market.MarketItem;
import lol.stompy.marketplace.util.Serializer;
import lombok.SneakyThrows;
import org.bukkit.inventory.ItemStack;

import java.util.Date;
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
     * serializes the class to a string
     *
     * @return {@link String}
     */

    @Override
    public String toString() {
        return Serializer.itemStackToBase64(stack) + "//" + cost + "//" + owner.toString() + "//" + date;
    }
}
