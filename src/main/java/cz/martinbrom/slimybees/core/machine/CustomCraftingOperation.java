package cz.martinbrom.slimybees.core.machine;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;

import cz.martinbrom.slimybees.core.recipe.GuaranteedRecipe;
import io.github.thebusybiscuit.slimefun4.core.machines.MachineOperation;

@ParametersAreNonnullByDefault
public class CustomCraftingOperation implements MachineOperation {

    private final List<ItemStack> ingredients;
    private final List<ItemStack> outputs;

    private final int totalTicks;
    private int currentTicks = 0;

    public CustomCraftingOperation(GuaranteedRecipe recipe) {
        this(recipe.getIngredients(), recipe.getOutputs(), recipe.getDuration());
    }

    public CustomCraftingOperation(List<ItemStack> ingredients, List<ItemStack> outputs, int totalTicks) {
        Validate.notNull(ingredients, "成分不能为空或为空!");
        Validate.notNull(outputs, "输出不能为空!");
        Validate.isTrue(totalTicks >= 0, "数量必须是正整数或零, received: " + totalTicks);

        this.ingredients = ingredients;
        this.outputs = outputs;
        this.totalTicks = totalTicks;
    }

    @Override
    public void addProgress(int num) {
        Validate.isTrue(num > 0, "进行必须是活跃的!");
        currentTicks += num;
    }

    @Nonnull
    public List<ItemStack> getIngredients() {
        return ingredients;
    }

    @Nonnull
    public List<ItemStack> getOutputs() {
        return outputs;
    }

    @Override
    public int getProgress() {
        return currentTicks;
    }

    @Override
    public int getTotalTicks() {
        return totalTicks;
    }

}
