package cz.martinbrom.slimybees.commands;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import cz.martinbrom.slimybees.core.BeeLoreService;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

@ParametersAreNonnullByDefault
public class MakeUnknownCommand extends AbstractCommand {

    private final BeeLoreService loreService;

    public MakeUnknownCommand(BeeLoreService loreService) {
        super("makeunknown", "让这个蜜蜂未知.", "slimybees.command.make_unknown");

        this.loreService = loreService;
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            Slimefun.getLocalization().sendMessage(sender, "messages.only-players");
            return;
        }

        Player p = (Player) sender;
        PlayerInventory inv = p.getInventory();

        ItemStack result = loreService.makeUnknown(inv.getItemInMainHand());
        if (loreService.isUnknown(result)) {
            inv.setItemInMainHand(result);
            p.sendMessage(ChatColor.GREEN + "成功使蜜蜂变成未知!");
        } else {
            p.sendMessage(ChatColor.DARK_GRAY + "您手中的物品不是有效的蜜蜂!");
        }
    }

    @Nonnull
    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

}
