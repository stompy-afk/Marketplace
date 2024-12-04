package lol.stompy.marketplace.market;

import com.eduardomcb.discord.webhook.WebhookClient;
import com.eduardomcb.discord.webhook.WebhookManager;
import com.eduardomcb.discord.webhook.models.Message;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lol.stompy.marketplace.Marketplace;
import lol.stompy.marketplace.market.menu.MarketConfirmationGUI;
import lol.stompy.marketplace.market.menu.MarketMainMenu;
import lol.stompy.marketplace.market.transaction.MarketTransaction;
import lol.stompy.marketplace.profile.Profile;
import lol.stompy.marketplace.util.CC;
import lol.stompy.marketplace.util.menu.Menu;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;

public class MarketItemHandler {

    @Getter
    private final List<MarketItem> marketItemList;
    private final List<UUID> marketItemRemovedList;

    private final Random random;
    private final WebhookManager webhookManager;

    private final int blackMarketTiming;
    private final double blackMarketChance;
    private final List<String> blackMarketMessage;

    private final Marketplace marketplace;

    /**
     * handles all market items
     *
     * @param marketplace instance of main
     */

    public MarketItemHandler(Marketplace marketplace) {
        this.marketplace = marketplace;

        this.marketItemList = new ArrayList<>();

        this.marketItemRemovedList = new ArrayList<>();
        this.random = new Random();

        this.blackMarketTiming = marketplace.getConfig().getInt("black-market.reset");
        this.blackMarketChance = marketplace.getConfig().getDouble("black-market.chance");
        this.blackMarketMessage = marketplace.getConfig().getStringList("black-market.message");

        this.webhookManager = new WebhookManager().setChannelUrl(marketplace.getConfig().getString("web-hook-link")).setListener(new WebhookClient.Callback() {
            @Override
            public void onSuccess(String s) {

            }

            @Override
            public void onFailure(int i, String s) {
                Bukkit.getServer().getLogger().log(Level.WARNING, "This webhook has failed to send!");
            }
        });

        this.load();
    }

    /**
     * loads all market items
     */

    private void load() {
        marketplace.getServer().getScheduler().runTaskAsynchronously(marketplace, () -> {
            for (Document document : marketplace.getMongoHandler().getMarketItems().find()) {
                marketItemList.add(new MarketItem(document));
            }
        });

        marketplace.getServer().getScheduler().runTaskTimer(marketplace, () -> {
            if (marketItemList.isEmpty())
                return;

            for (MarketItem marketItem : marketItemList) {
                if (marketItem.isBlackMarketItem()) {
                    marketItem.setBlackMarketItem(false);
                }

                if (random.nextDouble() <= blackMarketChance) {
                    marketItem.setBlackMarketItem(true);
                }
            }

            marketplace.getMenuHandler().getMenus().forEach(menu -> {
                if (menu instanceof MarketMainMenu)
                    menu.updateMenu();
            });

            blackMarketMessage.forEach(s -> marketplace.getServer().broadcastMessage(CC.translate(s)));
        }, 20L * blackMarketTiming, 20L * blackMarketTiming);
    }

    /**
     * saves a market item to the database
     *
     * @param marketItem market item to save
     * @param async      to do task async or not
     */

    private void save(MarketItem marketItem, boolean async) {

        if (async) {
            marketplace.getServer().getScheduler().runTaskAsynchronously(marketplace, () -> save(marketItem, false));
            return;
        }

        final Document document = marketplace.getMongoHandler().getMarketItems().find(Filters.eq("_id", marketItem.getUuid().toString())).first();

        if (document == null) {
            marketplace.getMongoHandler().getMarketItems().insertOne(marketItem.toBson());
            return;
        }

        marketplace.getMongoHandler().getMarketItems().replaceOne(document, marketItem.toBson(), new ReplaceOptions().upsert(true));
    }

    public final void handleRemoval() {
        marketItemRemovedList.forEach(uuid -> {
            final Document document = marketplace.getMongoHandler().getMarketItems().find(Filters.eq("_id", uuid.toString())).first();

            if (document == null)
                return;

            marketplace.getMongoHandler().getMarketItems().deleteOne(document);
        });
    }

    /**
     * adds a market item to the market
     *
     * @param profile profile of player
     * @param stack   itemstack to add
     * @param cost    cost to be added to market
     */

    public final void add(Profile profile, ItemStack stack, int cost) {
        final MarketItem marketItem = new MarketItem(profile, stack, cost);

        marketItemList.add(marketItem);
        this.save(marketItem, true);
    }

    /**
     * handles the purchase of a market item
     *
     * @param marketItem market item to handle purchase off
     * @param player     player of purchaser
     * @param purchaser  profile of purchaser
     */

    public final void purchase(MarketItem marketItem, Player player, Profile purchaser) {
        final boolean blackMarket = marketItem.isBlackMarketItem();

        marketItemList.remove(marketItem);
        marketItemRemovedList.add(marketItem.getUuid());

        for (Menu menu : marketplace.getMenuHandler().getMenus()) {
            if (menu.getPlayer().getUniqueId().equals(player.getUniqueId()))
                continue;

            if (menu instanceof MarketMainMenu)
                menu.updateMenu();

            if (menu instanceof MarketConfirmationGUI) {
                final MarketConfirmationGUI marketConfirmationGUI = (MarketConfirmationGUI) menu;

                if (marketConfirmationGUI.getMarketItem().getUuid().equals(marketItem.getUuid())) {
                    player.closeInventory();
                    player.sendMessage(CC.translate("&cSorry someone bought this item before you!"));
                }
            }
        }

        if (player.getInventory().firstEmpty() == -1)
            player.getWorld().dropItem(player.getLocation(), marketItem.getItem());
        else
            player.getInventory().addItem(marketItem.getItem());

        final MarketTransaction marketTransaction = new MarketTransaction(marketItem);
        purchaser.addMarketTransaction(marketTransaction);

        final StringBuilder stringBuilder = new StringBuilder();
        final OfflinePlayer seller = marketplace.getServer().getOfflinePlayer(marketItem.getOwner());

        stringBuilder.append("New Purchase!").append("\n");
        stringBuilder.append("Seller: ").append(seller.getName()).append("\n");
        stringBuilder.append("Purchaser: ").append(player.getName()).append("\n");
        stringBuilder.append("Date: ").append(marketTransaction.getDate()).append("\n");
        stringBuilder.append("Cost:").append(marketTransaction.getCost()).append("\n");

        webhookManager.setMessage(new Message().setContent(stringBuilder.toString())).exec();

        if (blackMarket) {
            marketplace.getEconomy().bankWithdraw(player.getName(), (double) marketItem.getCost() / 2);
            marketplace.getEconomy().bankDeposit(seller.getName(), marketItem.getCost() * 2);
            return;
        }

        marketplace.getEconomy().bankWithdraw(player.getName(), marketItem.getCost());
        marketplace.getEconomy().bankDeposit(seller.getName(), marketItem.getCost());
    }

}
