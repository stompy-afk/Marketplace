package lol.stompy.marketplace.commands;

import lol.stompy.marketplace.Marketplace;
import lol.stompy.marketplace.market.menu.MarketMenu;
import lol.stompy.marketplace.util.CC;
import lombok.AllArgsConstructor;
import me.vaperion.blade.annotation.argument.Sender;
import me.vaperion.blade.annotation.command.Command;
import me.vaperion.blade.annotation.command.Permission;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class MarketCommand {

    private final Marketplace marketplace;

    @Command(value = "marketplace")
    @Permission(value = "marketplace.view")
    public final void marketplaceCommand(@Sender Player player) {
        new MarketMenu(player, marketplace, false);
    }

    @Command(value = "marketplace")
    @Permission(value = "marketplace.blackmarket")
    public final void blackMarketPlace(@Sender Player player) {
        new MarketMenu(player, marketplace, true);
    }

}
