package cz.martinbrom.slimybees.core.machine;

import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import cz.martinbrom.slimybees.core.genetics.BreedingResultDTO;
import io.github.thebusybiscuit.slimefun4.core.machines.MachineOperation;

@ParametersAreNonnullByDefault
public class BeeBreedingOperation implements MachineOperation {

    private final ItemStack[] parents;
    private final BreedingResultDTO result;
    private final List<ItemStack> products;
    private final Consumer<Location> effectFunction;

    private int currentTicks = 0;

    public BeeBreedingOperation(ItemStack firstParent, ItemStack secondParent, BreedingResultDTO result,
                                List<ItemStack> products, Consumer<Location> effectFunction) {
        Validate.notNull(firstParent, "第一个父项不能为空!");
        Validate.notNull(secondParent, "第二个父项不能为空!");
        Validate.notNull(result, "育种结果不能为空!");
        Validate.notNull(products, "产品不能为空!");
        Validate.notNull(effectFunction, "效果函数不能为空!");

        this.parents = new ItemStack[] { firstParent, secondParent };
        this.result = result;
        this.products = products;
        this.effectFunction = effectFunction;
    }

    @Nonnull
    public ItemStack[] getParents() {
        return parents;
    }

    @Nonnull
    public ItemStack getPrincess() {
        return result.getPrincess();
    }

    @Nonnull
    public ItemStack[] getDrones() {
        return result.getDrones();
    }

    @Nonnull
    public List<ItemStack> getProducts() {
        return products;
    }

    public void applyEffect(Location l) {
        effectFunction.accept(l);
    }

    @Override
    public void addProgress(int num) {
        Validate.isTrue(num > 0, "Progress must be positive!");
        currentTicks += num;
    }

    @Override
    public int getProgress() {
        return currentTicks;
    }

    @Override
    public int getTotalTicks() {
        return result.getTicks();
    }

}
