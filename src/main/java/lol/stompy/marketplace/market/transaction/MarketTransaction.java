package lol.stompy.marketplace.market.transaction;

import de.tr7zw.nbtapi.NBT;
import lol.stompy.marketplace.util.Serializer;
import lombok.SneakyThrows;
import org.bukkit.inventory.ItemStack;

import java.util.Date;
import java.util.UUID;

public class MarketTransaction {

    private final ItemStack stack;

    private int cost;
    private UUID owner;

    private final String date;

    /**
     * records a transaction done on the market
     *
     * @param stack ItemStack that got traded
     */

    public MarketTransaction(ItemStack stack) {
        this.stack = stack;

        NBT.get(stack, nbt -> {
            this.owner = UUID.fromString(nbt.getString("owner"));
            this.cost = nbt.getInteger("cost");
        });

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
