package cz.martinbrom.slimybees.commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import cz.martinbrom.slimybees.core.genetics.BeeGeneticService;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleRegistry;
import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

@ParametersAreNonnullByDefault
public class AlterCommand extends AbstractCommand {

    private final AlleleRegistry alleleRegistry;
    private final BeeGeneticService geneticService;

    // exclude SPECIES because we don't support changing the species directly, could definitely lead to bugs
    private final List<String> chromosomeTypeNames = Arrays.stream(ChromosomeType.values())
            .filter(t -> t != ChromosomeType.SPECIES)
            .map(ChromosomeType::name)
            .collect(Collectors.toList());

    public AlterCommand(AlleleRegistry alleleRegistry, BeeGeneticService geneticService) {
        super("修改", "改变蜜蜂的染色体", "slimybees.command.alter");

        this.alleleRegistry = alleleRegistry;
        this.geneticService = geneticService;
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            Slimefun.getLocalization().sendMessage(sender, "messages.only-players");
            return;
        }

        Player p = (Player) sender;
        if (args.length != 3 && args.length != 4) {
            p.sendMessage("用法: /slimybees alter <chromosome> <value uid> [primary | secondary | both]");
            return;
        }

        ChromosomeType type = ChromosomeType.parse(args[1]);
        if (type == null) {
            p.sendMessage(ChatColor.RED + "没有找到任何带有名称的染色体: " + args[1] + "!");
            return;
        }

        if (type == ChromosomeType.SPECIES) {
            p.sendMessage(ChatColor.RED + "不能直接改变蜜蜂的种类!");
            return;
        }

        List<String> alleleUids = alleleRegistry.getAllUidsByChromosomeType(type);
        if (!alleleUids.contains(args[2])) {
            p.sendMessage(ChatColor.RED + "Did not find any allele value with the uid: " + args[2] + "!");
            return;
        }

        // if the last argument is missing, we assume the player means "both" as that is the most common use-case
        boolean primary = true;
        boolean secondary = true;
        if (args.length == 4) {
            if (args[3].equalsIgnoreCase("主要")) {
                secondary = false;
            } else if (args[3].equalsIgnoreCase("次要")) {
                primary = false;
            } else if (!args[3].equalsIgnoreCase("两者")) {
                p.sendMessage(ChatColor.RED + "无法确定要改变哪个等位基因. " +
                        "请使用 either \"primary\", \"secondary\", or \"both\"!");
                return;
            }
        }

        PlayerInventory inv = p.getInventory();
        ItemStack result = geneticService.alterItemGenome(inv.getItemInMainHand(), type, args[2], primary, secondary);
        if (result == null) {
            p.sendMessage(ChatColor.DARK_GRAY + "您手中的物品不是有效的蜜蜂!");
        } else {
            inv.setItemInMainHand(result);
            p.sendMessage(ChatColor.GREEN + "成功更新蜜蜂等位基因" + ((primary && secondary) ? "s" : "") + "!");
        }
    }

    @Nonnull
    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return chromosomeTypeNames;
        } else if (args.length == 3) {
            ChromosomeType type = ChromosomeType.parse(args[1]);
            if (type != null && type != ChromosomeType.SPECIES) {
                return alleleRegistry.getAllUidsByChromosomeType(type);
            }
        } else if (args.length == 4) {
            return Arrays.asList("primary", "secondary", "both");
        }

        return Collections.emptyList();
    }

}
