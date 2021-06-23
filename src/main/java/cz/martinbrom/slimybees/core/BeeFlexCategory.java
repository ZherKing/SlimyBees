package cz.martinbrom.slimybees.core;

import java.util.Arrays;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.genetics.Genome;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleRegistry;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleSpecies;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;

// impl mostly copied from Slimefun4 MultiCategory
@ParametersAreNonnullByDefault
public class BeeFlexCategory extends BaseFlexCategory {

    private static final int CATEGORY_SIZE = 36;
    private static final List<String> CHEAT_MODE_BEE_LORE = Arrays.asList("",
            ChatColor.YELLOW + "Left Click" + ChatColor.GRAY + " to get a " + ChatColor.BOLD + "Princess",
            ChatColor.YELLOW + "Right Click" + ChatColor.GRAY + " to get a " + ChatColor.BOLD + "Drone");

    private static final ItemStack NOT_DISCOVERED_ITEM = new CustomItem(Material.BARRIER, ChatColor.GRAY + "Undiscovered Species");

    public BeeFlexCategory(NamespacedKey key, ItemStack item) {
        super(key, item);
    }

    @Override
    public boolean isVisible(Player p, PlayerProfile profile, SlimefunGuideMode layout) {
        return true;
    }

    @Override
    protected void fillMenu(ChestMenu menu, Player p, PlayerProfile profile, SlimefunGuideMode layout, int page) {
        AlleleRegistry alleleRegistry = SlimyBeesPlugin.getAlleleRegistry();
        List<AlleleSpecies> allSpecies = alleleRegistry.getAllSpecies();

        SlimyBeesPlayerProfile sbProfile = SlimyBeesPlayerProfile.get(p);

        int index = 9;
        int target = (CATEGORY_SIZE * (page - 1)) - 1;

        BeeLoreService beeLoreService = SlimyBeesPlugin.getBeeLoreService();
        while (target < (allSpecies.size() - 1) && index < CATEGORY_SIZE + 9) {
            target++;

            AlleleSpecies species = allSpecies.get(target);
            if (layout == SlimefunGuideMode.SURVIVAL_MODE) {
                ItemStack beeItemStack = beeLoreService.generify(species.getDroneItemStack());
                if (sbProfile.hasDiscovered(species)) {
                    menu.addItem(index, beeItemStack, (pl, slot, item, action) -> {
                        SlimefunGuide.openCategory(profile, new BeeDetailFlexCategory(species), layout, 1);
                        return false;
                    });
                } else {
                    menu.addItem(index, NOT_DISCOVERED_ITEM, ChestMenuUtils.getEmptyClickHandler());
                }
            } else {
                ItemStack beeItemStack = beeLoreService.generify(species.getDroneItemStack(), CHEAT_MODE_BEE_LORE);
                menu.addItem(index, beeItemStack, (pl, slot, item, action) -> {
                    ItemStack itemStack;
                    if (action.isRightClicked()) {
                        itemStack = species.getDroneItemStack();
                    } else {
                        itemStack = species.getPrincessItemStack();
                    }

                    Genome genome = SlimyBeesPlugin.getBeeGeneticService().getGenome(species);
                    if (genome != null) {
                        ItemStack updatedItemStack = beeLoreService.updateLore(itemStack, genome);
                        pl.getInventory().addItem(updatedItemStack);
                    }
                    return false;
                });
            }

            index++;
        }

        int pages = target == allSpecies.size() - 1 ? page : (allSpecies.size() - 1) / CATEGORY_SIZE + 1;

        menu.addItem(46, ChestMenuUtils.getPreviousButton(p, page, pages), (pl, slot, item, action) -> {
            int next = page - 1;

            if (next != page && next > 0) {
                open(p, profile, layout, next);
            }

            return false;
        });

        menu.addItem(52, ChestMenuUtils.getNextButton(p, page, pages), (pl, slot, item, action) -> {
            int next = page + 1;

            if (next != page && next <= pages) {
                open(p, profile, layout, next);
            }

            return false;
        });

        menu.open(p);
    }

}