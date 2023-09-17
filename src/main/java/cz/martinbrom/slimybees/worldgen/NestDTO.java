package cz.martinbrom.slimybees.worldgen;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;

@ParametersAreNonnullByDefault
public class NestDTO {

    private final World.Environment environment;
    private final Biome[] biomes;
    private final Material[] floorMaterials;
    private final double spawnChance;

    private String nestId;

    public NestDTO(World.Environment environment, Biome[] biomes, Material[] floorMaterials, double spawnChance) {
        Validate.notNull(environment, "嵌套环境不能为空!");
        Validate.notEmpty(biomes, "巢穴生物群系不能为空!");
        Validate.noNullElements(biomes, "巢穴生物群系不能包含空!");
        Validate.notEmpty(floorMaterials, "Nest 地面材质不能为空!");
        Validate.noNullElements(floorMaterials, "Nest地面材质不能包含空!");
        Validate.isTrue(spawnChance > 0 && spawnChance <= 1, "生成几率必须介于 0%（不包含）和 100%（包含）之间!");

        this.environment = environment;
        this.biomes = biomes;
        this.floorMaterials = floorMaterials;
        this.spawnChance = spawnChance;
    }

    @Nonnull
    public World.Environment getEnvironment() {
        return environment;
    }

    @Nonnull
    public Biome[] getBiomes() {
        return biomes;
    }

    @Nonnull
    public Material[] getFloorMaterials() {
        return floorMaterials;
    }

    public double getSpawnChance() {
        return spawnChance;
    }

    @Nonnull
    public String getNestId() {
        Validate.notNull(nestId, "嵌套 id 不能为 null，您可能忘记调用etNestItemStack()!");

        return nestId;
    }

    public void setItemStack(SlimefunItemStack nestItemStack) {
        Validate.notNull(nestItemStack, "蜂巢项目不能为空!");

        nestId = nestItemStack.getItemId();
    }

}
