package lol.stompy.marketplace.commands;

import lol.stompy.marketplace.Marketplace;
import lol.stompy.marketplace.market.menu.MarketMainMenu;
import lol.stompy.marketplace.market.transaction.menu.MarketTransactionMenu;
import lol.stompy.marketplace.profile.Profile;
import lol.stompy.marketplace.util.CC;
import lombok.AllArgsConstructor;
import me.vaperion.blade.annotation.argument.Name;
import me.vaperion.blade.annotation.argument.Sender;
import me.vaperion.blade.annotation.command.Command;
import me.vaperion.blade.annotation.command.Permission;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

@AllArgsConstructor
public class MarketCommand {

    private final Marketplace marketplace;

    @Command(value = "marketplace")
    @Permission(value = "marketplace.view")
    public final void marketplaceCommand(@Sender Player player) {
        new MarketMainMenu(player, marketplace, false).updateMenu();
    }

    @Command(value = "marketplace")
    @Permission(value = "marketplace.blackmarket")
    public final void blackMarketPlace(@Sender Player player) {
        new MarketMainMenu(player, marketplace, true).updateMenu();
    }

    @Command(value = "transactions")
    @Permission(value = "marketplace.history")
    public final void transactionsCommand(@Sender Player player) {
        final Optional<Profile> optionalPlayer = marketplace.getProfileHandler().getProfile(player.getUniqueId());

        if (optionalPlayer.isEmpty()) {
            player.sendMessage(CC.translate("&cYour profile is not available!"));
            return;
        }

        new MarketTransactionMenu(player, optionalPlayer.get()).updateMenu();
    }

    @Command(value = "sell")
    @Permission(value = "marketplace.sell")
    public final void sell(@Sender Player player, @Name("cost") int cost) {
        final Optional<Profile> optionalPlayer = marketplace.getProfileHandler().getProfile(player.getUniqueId());

        if (optionalPlayer.isEmpty()) {
            player.sendMessage(CC.translate("&cYour profile is not available!"));
            return;
        }

        final ItemStack stack = player.getInventory().getItemInMainHand();

        if (stack == null || stack.getType().equals(Material.AIR)) {
            player.sendMessage(CC.translate("&cYou need to be holding an item in your main hand to add it to the auction!"));
            return;
        }

        marketplace.getMarketItemHandler().add(optionalPlayer.get(), stack, cost);
        player.getInventory().setItemInMainHand(null);

        player.sendMessage(CC.translate("&aPut your item on the &lmarket place&a!"));
    }

}
