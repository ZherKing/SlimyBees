package cz.martinbrom.slimybees.core.genetics;

import java.util.Comparator;

import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.SlimyBeesPlayerProfile;
import cz.martinbrom.slimybees.core.genetics.alleles.Allele;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleRegistry;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleSpecies;
import io.github.thebusybiscuit.slimefun4.utils.FireworkUtils;

@ParametersAreNonnullByDefault
public class BeeDiscoveryService {

    public void discover(Player p, Genome genome, boolean discover) {
        Validate.notNull(p, "The player cannot be null!");
        Validate.notNull(genome, "The genome cannot be null!");

        AlleleSpecies species = genome.getSpecies();
        discoverInner(p, species, discover);
    }

    public void discover(Player p, AlleleSpecies species, boolean discover) {
        Validate.notNull(p, "The player cannot be null!");
        Validate.notNull(species, "The species cannot be null!");

        discoverInner(p, species, discover);
    }

    public long discoverAll(Player p) {
        SlimyBeesPlayerProfile profile = SlimyBeesPlayerProfile.get(p);

        AlleleRegistry alleleRegistry = SlimyBeesPlugin.getAlleleRegistry();
        return alleleRegistry.getAllSpecies().stream()
                .filter(species -> !profile.hasDiscovered(species))
                .sorted(Comparator.comparing(Allele::getName))
                .peek(species -> {
                    profile.discoverBee(species, true);
                    notifyPlayer(p, species.getName(), true);
                })
                .count();
    }

    public void undiscoverAll(Player p) {
        SlimyBeesPlayerProfile profile = SlimyBeesPlayerProfile.get(p);

        profile.getDiscoveredBees().stream()
            .sorted()
            .forEach(name -> {
                profile.discoverBee(name, false);
                notifyPlayer(p, name, false);
            });
    }

    // TODO: 05.06.21 Naming
    private void discoverInner(Player p, AlleleSpecies species, boolean discover) {
        SlimyBeesPlayerProfile profile = SlimyBeesPlayerProfile.get(p);
        if (!profile.hasDiscovered(species)) {
            profile.discoverBee(species, discover);
            notifyPlayer(p, species.getName(), discover);
        }
    }

    private void notifyPlayer(Player p, String name, boolean discover) {
        if (discover) {
            FireworkUtils.launchRandom(p, 1);
            p.sendMessage(ChatColor.GREEN + "You have discovered a new species - "
                    + ChatColor.GRAY + ChatColor.BOLD + name
                    + ChatColor.RESET + ChatColor.GREEN + "!");
        }
    }

}
