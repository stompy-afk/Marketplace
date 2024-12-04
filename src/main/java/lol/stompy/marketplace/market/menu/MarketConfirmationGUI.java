package lol.stompy.marketplace.market.menu;

import lol.stompy.marketplace.market.MarketItem;
import lol.stompy.marketplace.market.MarketItemHandler;
import lol.stompy.marketplace.profile.Profile;
import lol.stompy.marketplace.util.CC;
import lol.stompy.marketplace.util.menu.Menu;
import lol.stompy.marketplace.util.menu.buttons.Button;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class MarketConfirmationGUI extends Menu {

    @Getter
    private final MarketItem marketItem;

    private final MarketItemHandler marketItemHandler;
    private final Profile profile;

    public MarketConfirmationGUI(Player player, MarketItem marketItem, Profile profile, MarketItemHandler marketItemHandler) {
        super(player, "Please confirm", 27);

        this.marketItem = marketItem;
        this.marketItemHandler = marketItemHandler;
        this.profile = profile;
    }

    /**
     * The method to get the buttons for the current inventory tick
     * <p>
     * Use {@code this.buttons[index] = Button} to assign
     * a button to a slot.
     */
    @Override
    public Map<Integer, Button> getButtons() {
        final Map<Integer, Button> buttonMap = new HashMap<>();

        final Button confirmationButton = new Button(Material.EMERALD_BLOCK)
                .setDisplayName(CC.translate("&a&lCONFIRM"))
                .setGlow(true)
                .setClickAction(action -> {
                    action.setCancelled(true);

                    final boolean blackMarket = marketItem.isBlackMarketItem();

                    marketItemHandler.purchase(marketItem, player, profile);
                    player.sendMessage(CC.translate("&aYou've purchased a market item for " + (blackMarket ? marketItem.getCost() / 2 : marketItem.getCost())));

                    player.closeInventory();
                });


        final Button denyButton = new Button(Material.REDSTONE_BLOCK)
                .setDisplayName(CC.translate("&c&lCANCEL"))
                .setGlow(true)
                .setClickAction(action -> {
                    action.setCancelled(true);
                    player.closeInventory();
                });


        for (int base = 0; base <= 18; base += 9) {
            for (int offset = 0; offset < 3; offset++) {
                buttonMap.put(base + offset, confirmationButton);
            }
        }

        buttonMap.put(13, new Button(marketItem.getStack()));

        for (int base = 6; base <= 24; base += 9) {
            for (int offset = 0; offset < 3; offset++) {
                buttonMap.put(base + offset, denyButton);
            }
        }

        return buttonMap;
    }
}
