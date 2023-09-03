package cz.martinbrom.slimybees.commands;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import cz.martinbrom.slimybees.core.BeeAnalysisService;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

@ParametersAreNonnullByDefault
public class AnalyzeCommand extends AbstractCommand {

    private final BeeAnalysisService analysisService;

    public AnalyzeCommand(BeeAnalysisService analysisService) {
        super("分析", "分析未知蜜蜂并更新知识.", "slimybees.command.analyze");

        this.analysisService = analysisService;
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            Slimefun.getLocalization().sendMessage(sender, "messages.only-players");
            return;
        }

        if (args.length != 1 && args.length != 2) {
            // TODO: 04.06.21 Common method to print usage with colors and stuff
            sender.sendMessage("用法: /slimybees analyze [hand | all]");
            return;
        }

        Player p = (Player) sender;
        int analyzedCount;

        // if the last argument is missing, we assume the player means "hand" as that is the most common use-case
        if (args.length == 1 || args[1].equals("hand")) {
            analyzedCount = analyzeHeldItem(p);
        } else if (args[1].equals("all")) {
            analyzedCount = analyzeInventory(p);
        } else {
            sender.sendMessage("用法: /slimybees analyze [hand | all]");
            return;
        }

        if (analyzedCount > 0) {
            sender.sendMessage(ChatColor.GREEN + "成功分析 "
                    + ChatColor.BOLD + analyzedCount
                    + ChatColor.RESET + ChatColor.GREEN + " 蜜蜂" + (analyzedCount > 1 ? "s" : "") + "!");
        } else {
            sender.sendMessage(ChatColor.DARK_GRAY + "没有找到任何蜜蜂进行分析!");
        }
    }

    private int analyzeHeldItem(Player p) {
        PlayerInventory inv = p.getInventory();
        return analyzeSlot(p, inv, inv.getHeldItemSlot());
    }

    private int analyzeInventory(Player p) {
        int analyzedCount = 0;
        PlayerInventory inv = p.getInventory();

        // Indexes 0 through 8 refer to the hotbar. 9 through 35 refer to the main inventory.
        for (int i = 0; i < 36; i++) {
            analyzedCount += analyzeSlot(p, inv, i);
        }

        // Index 40 refers to the off hand item slot.
        return analyzedCount + analyzeSlot(p, inv, 40);
    }

    private int analyzeSlot(Player p, PlayerInventory inventory, int slot) {
        ItemStack item = inventory.getItem(slot);
        if (item != null && !item.getType().isAir()) {
            ItemStack analyzedItem = analysisService.analyze(p, item);
            if (analyzedItem != null) {
                inventory.setItem(slot, analyzedItem);
                return analyzedItem.getAmount();
            }
        }

        return 0;
    }

    @Nonnull
    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        return Arrays.asList("all", "hand");
    }

}
