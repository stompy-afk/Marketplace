package lol.stompy.marketplace;

import lol.stompy.marketplace.commands.MarketCommand;
import lol.stompy.marketplace.market.MarketItemHandler;
import lol.stompy.marketplace.mongo.MongoHandler;
import lol.stompy.marketplace.profile.Profile;
import lol.stompy.marketplace.profile.ProfileHandler;
import lol.stompy.marketplace.util.menu.MenuHandler;
import lombok.Getter;
import me.vaperion.blade.Blade;
import me.vaperion.blade.bukkit.BladeBukkitPlatform;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class Marketplace extends JavaPlugin {

    @Getter
    private static Marketplace instance;

    private ProfileHandler profileHandler;
    private MarketItemHandler marketItemHandler;
    private MongoHandler mongoHandler;
    private MenuHandler menuHandler;

    private Economy economy;

    /**
     * plugin loading concept
     */

    @Override
    public void onLoad() {
        instance = this;
        this.saveDefaultConfig();
    }

    /**
     * plugin enabling concept
     */

    @Override
    public void onEnable() {
        this.setupEconomy();

        this.marketItemHandler = new MarketItemHandler(this);
        this.profileHandler = new ProfileHandler(this);
        this.mongoHandler = new MongoHandler(this);
        this.menuHandler = new MenuHandler(this);

        Blade.forPlatform(new BladeBukkitPlatform(this)).build()
                .register(new MarketCommand(this));
    }

    /**
     * plugin disabling concept
     */

    @Override
    public void onDisable() {
        for (Profile profile : profileHandler.getProfiles())
            profileHandler.save(profile, false);
    }

    /**
     * sets up the vault economy
     *
     * @return {@link Boolean}
     */

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }

        economy = rsp.getProvider();
        return economy != null;
    }

}