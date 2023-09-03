package cz.martinbrom.slimybees.core.machine;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import cz.martinbrom.slimybees.core.recipe.AbstractRecipe;
import cz.martinbrom.slimybees.core.recipe.GuaranteedRecipe;
import cz.martinbrom.slimybees.core.recipe.RecipeMatchService;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemState;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import io.github.thebusybiscuit.slimefun4.libraries.dough.inventory.InvUtils;
import io.github.thebusybiscuit.slimefun4.utils.itemstack.ItemStackWrapper;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;

@ParametersAreNonnullByDefault
public abstract class AbstractElectricMachine extends AbstractMachine implements EnergyNetComponent {

    private final List<AbstractRecipe> recipes = new ArrayList<>();

    private int energyConsumedPerTick = -1;
    private int energyCapacity = -1;
    private int processingSpeed = -1;

    protected AbstractElectricMachine(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

    @Override
    protected boolean checkCraftPreconditions(Block b) {
        return takeCharge(b.getLocation());
    }

    @Nonnull
    @Override
    public EnergyNetComponentType getEnergyComponentType() {
        return EnergyNetComponentType.CONSUMER;
    }

    /**
     * This method returns the max amount of electricity this machine can hold.
     *
     * @return The max amount of electricity this Block can store.
     */
    public int getCapacity() {
        return energyCapacity;
    }

    /**
     * This method returns the amount of energy that is consumed per operation.
     *
     * @return The rate of energy consumption
     */
    public int getEnergyConsumption() {
        return energyConsumedPerTick;
    }

    /**
     * This method returns the speed at which this machine will operate.
     * This can be implemented on an instantiation-level to create different tiers
     * of machines.
     *
     * @return The speed of this machine
     */
    public int getSpeed() {
        return processingSpeed;
    }

    /**
     * This sets the energy capacity for this machine.
     * This method <strong>must</strong> be called before registering the item
     * and only before registering.
     *
     * @param capacity The amount of energy this machine can store
     * @return This method will return the current instance of {@link AbstractElectricMachine}, so that can be chained.
     */
    public final AbstractElectricMachine setCapacity(int capacity) {
        Validate.isTrue(capacity > 0, "容量必须大于零!");

        if (getState() == ItemState.UNREGISTERED) {
            this.energyCapacity = capacity;
            return this;
        } else {
            throw new IllegalStateException("注册物料后，无法修改容量.");
        }
    }

    /**
     * This sets the speed of this machine.
     *
     * @param speed The speed multiplier for this machine, must be above zero
     * @return This method will return the current instance of {@link AbstractElectricMachine}, so that can be chained.
     */
    public final AbstractElectricMachine setProcessingSpeed(int speed) {
        Validate.isTrue(speed > 0, "花费时间必须大于零!");

        this.processingSpeed = speed;
        return this;
    }

    /**
     * This method sets the energy consumed by this machine per tick.
     *
     * @param energyConsumption The energy consumed per tick
     * @return This method will return the current instance of {@link AbstractElectricMachine}, so that can be chained.
     */
    public final AbstractElectricMachine setEnergyConsumption(int energyConsumption) {
        Validate.isTrue(energyConsumption > 0, "能耗必须大于零!");
        Validate.isTrue(energyCapacity > 0, "必须先指定容量，然后才能设置消耗量.");
        Validate.isTrue(energyConsumption <= energyCapacity, "能耗不能高于容量 (" + energyCapacity + ')');

        this.energyConsumedPerTick = energyConsumption;
        return this;
    }

    /**
     * Adds a new {@link AbstractRecipe} to the machine
     *
     * @param recipe The {@link AbstractRecipe}
     */
    public void registerRecipe(AbstractRecipe recipe) {
        recipe.setDuration(recipe.getDuration() / getSpeed());
        recipes.add(recipe);
    }

    /**
     * This method will remove charge from a location if it is chargeable.
     *
     * @param l location to try to remove charge from
     * @return Whether charge was taken if its chargeable
     */
    protected boolean takeCharge(Location l) {
        Validate.notNull(l, "无法尝试从空位置接管费用!");

        if (isChargeable()) {
            int charge = getCharge(l);

            if (charge < getEnergyConsumption()) {
                return false;
            }

            setCharge(l, charge - getEnergyConsumption());
        }

        return true;
    }

    @Nullable
    @Override
    protected GuaranteedRecipe findNextRecipe(BlockMenu menu) {
        List<ItemStack> items = new ArrayList<>();

        for (int slot : getInputSlots()) {
            ItemStack item = menu.getItemInSlot(slot);

            if (item != null) {
                items.add(ItemStackWrapper.wrap(item));
            }
        }

        GuaranteedRecipe recipe = RecipeMatchService.match(items, recipes);
        if (recipe == null || !InvUtils.fitAll(menu.toInventory(), recipe.getOutputs().toArray(new ItemStack[0]), getOutputSlots())) {
            return null;
        }

        menu.toInventory().removeItem(recipe.getIngredientsCopy());

        return recipe;
    }

}
