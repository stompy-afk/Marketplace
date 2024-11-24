package lol.stompy.marketplace.market;

import de.tr7zw.nbtapi.NBT;
import lol.stompy.marketplace.profile.Profile;
import lol.stompy.marketplace.util.Serializer;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.inventory.ItemStack;

@Getter
public class MarketItem {

    private final Profile profile;
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
        this.profile = profile;
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
     * @param profile owner of the item
     * @param s string to deserialize from
     */

    @SneakyThrows
    public MarketItem(Profile profile, String s) {
        this.profile = profile;

        final String[] args = s.split("//");

        this.stack = Serializer.itemStackFromBase64(args[0]);
        this.cost = Integer.parseInt(args[1]);

        if (stack != null)
            NBT.modify(stack, nbt -> {
                nbt.setInteger("cost", cost);
                nbt.setString("owner", profile.getUuid().toString());
            });
    }

    /**
     * serializes the market item
     *
     * @return {@link String}
     */

    @Override
    public String toString() {
        return Serializer.itemStackToBase64(stack) + "//" + cost;
    }
}
