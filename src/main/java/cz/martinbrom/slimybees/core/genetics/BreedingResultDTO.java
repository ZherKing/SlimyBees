package cz.martinbrom.slimybees.core.genetics;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;

@ParametersAreNonnullByDefault
public class BreedingResultDTO {

    private final ItemStack princess;
    private final ItemStack[] drones;
    private final int ticks;

    public BreedingResultDTO(ItemStack princess, ItemStack[] drones, int ticks) {
        Validate.notNull(princess, "雌蜂不能为空!");
        Validate.notEmpty(drones, "雄峰不能为空!");
        Validate.isTrue(ticks >= 0, "数量必须是正整数或零: " + ticks);

        this.princess = princess;
        this.drones = drones;
        this.ticks = ticks;
    }

    @Nonnull
    public ItemStack getPrincess() {
        return princess;
    }

    @Nonnull
    public ItemStack[] getDrones() {
        return drones;
    }

    public int getTicks() {
        return ticks;
    }

}
