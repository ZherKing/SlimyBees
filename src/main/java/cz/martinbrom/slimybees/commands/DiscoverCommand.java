package cz.martinbrom.slimybees.commands;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import cz.martinbrom.slimybees.core.BeeDiscoveryService;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleRegistry;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleSpecies;
import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType;
import cz.martinbrom.slimybees.utils.StringUtils;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

@ParametersAreNonnullByDefault
public class DiscoverCommand extends AbstractCommand {

    private final BeeDiscoveryService beeDiscoveryService;
    private final AlleleRegistry alleleRegistry;

    public DiscoverCommand(BeeDiscoveryService beeDiscoveryService, AlleleRegistry alleleRegistry) {
        super("发现", "发现的蜜蜂物种标记", "slimybees.command.discover");

        this.beeDiscoveryService = beeDiscoveryService;
        this.alleleRegistry = alleleRegistry;
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            Slimefun.getLocalization().sendMessage(sender, "messages.only-players");
            return;
        }

        if (args.length != 2) {
            // TODO: 04.06.21 Common method to print usage with colors and stuff
            sender.sendMessage("用法: /slimybees discover <species | all | reset>");
            return;
        }

        // TODO: 05.06.21 Add an argument to execute this for someone else (like /sf research)
        Player p = (Player) sender;
        if (args[1].equals("all")) {
            discoverAll(p);
        } else if (args[1].equals("reset")) {
            beeDiscoveryService.undiscoverAll(p);
            p.sendMessage(ChatColor.GREEN + "成功地将所有蜜蜂标记为未被发现!");
        } else {
            discoverSpecies(p, args[1]);
        }
    }

    private void discoverAll(Player p) {
        long discoveredCount = beeDiscoveryService.discoverAll(p);
        if (discoveredCount > 0) {
            String countSuffix = discoveredCount == 1 ? "" : "s";
            p.sendMessage(ChatColor.GREEN + "成功标记 "
                    + ChatColor.BOLD + discoveredCount
                    + ChatColor.RESET + ChatColor.GREEN + " 蜜蜂" + countSuffix + " 发现了!");
        } else {
            p.sendMessage(ChatColor.DARK_GRAY + "没有更多的蜜蜂可以发现!");
        }
    }

    private void discoverSpecies(Player p, String speciesName) {
        String uid = StringUtils.nameToUid(ChromosomeType.SPECIES, speciesName);
        AlleleSpecies species = ((AlleleSpecies) alleleRegistry.get(ChromosomeType.SPECIES, uid));
        if (species != null) {
            if (!beeDiscoveryService.discover(p, species)) {
                p.sendMessage(ChatColor.DARK_GRAY + "你已经发现了这个物种!");
            }
        } else {
            p.sendMessage(ChatColor.RED + "没有找到任何有名字的蜜蜂物种: "
                    + ChatColor.BOLD + speciesName
                    + ChatColor.RESET + ChatColor.RED + "!");
        }
    }

    @Nonnull
    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        if (args.length != 2) {
            return Collections.emptyList();
        }

        List<String> names = alleleRegistry.getAllNamesByChromosomeType(ChromosomeType.SPECIES);
        names.add("all");
        names.add("reset");

        return names;
    }

}
