package cz.martinbrom.slimybees.core;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.inventory.ItemStack;

import cz.martinbrom.slimybees.Categories;
import cz.martinbrom.slimybees.ItemStacks;
import cz.martinbrom.slimybees.RecipeTypes;
import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.genetics.BeeGeneticService;
import cz.martinbrom.slimybees.core.genetics.Genome;
import cz.martinbrom.slimybees.core.genetics.alleles.Allele;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleRegistry;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleService;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleSpecies;
import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType;
import cz.martinbrom.slimybees.core.recipe.ChanceItemStack;
import cz.martinbrom.slimybees.items.bees.BeeNest;
import cz.martinbrom.slimybees.items.bees.Drone;
import cz.martinbrom.slimybees.items.bees.Princess;
import cz.martinbrom.slimybees.utils.PatternUtil;
import cz.martinbrom.slimybees.utils.StringUtils;
import cz.martinbrom.slimybees.utils.types.Triple;
import cz.martinbrom.slimybees.worldgen.NestDTO;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;

/**
 * This class is used to easily register bee species with everything related to it.
 */
@ParametersAreNonnullByDefault
public class BeeBuilder {

    private final AlleleService alleleService;
    private final AlleleRegistry alleleRegistry;
    private final BeeRegistry beeRegistry;
    private final SlimyBeesRegistry registry;
    private final BeeGeneticService geneticService;
    private final BeeLoreService loreService;

    private final String uid;
    private final String name;
    private final ChatColor color;
    private final boolean dominant;
    private final List<ChanceItemStack> products;
    private final Allele[] partialTemplate;
    private final List<Triple<String, String, Double>> mutations;

    private boolean alwaysVisible;
    private boolean enchanted;
    private boolean secret;

    private NestDTO nest;

    public BeeBuilder(String uid, ChatColor color) {
        this(uid, color, false);
    }

    public BeeBuilder(String uid, ChatColor color, boolean dominant) {
        Validate.notEmpty(uid, "蜜蜂的 UID 不得为空!");
        Validate.isTrue(PatternUtil.SPECIES_UID_PATTERN.matcher(uid).matches(), "蜜蜂 UID 必须以蜜蜂物种为前缀 " +
                "且必须使用蛇形命名法 " + uid + "!");
        Validate.notNull(color, "蜜蜂颜色不能为空!");

        alleleService = SlimyBeesPlugin.getAlleleService();
        alleleRegistry = SlimyBeesPlugin.getAlleleRegistry();
        beeRegistry = SlimyBeesPlugin.getBeeRegistry();
        registry = SlimyBeesPlugin.getRegistry();
        geneticService = SlimyBeesPlugin.getBeeGeneticService();
        loreService = SlimyBeesPlugin.getBeeLoreService();

        this.name = StringUtils.uidToName(uid);
        this.uid = uid;
        this.color = color;
        this.dominant = dominant;

        products = new ArrayList<>();
        mutations = new ArrayList<>();

        partialTemplate = new Allele[ChromosomeType.CHROMOSOME_COUNT];
    }

    @Nonnull
    public String getUid() {
        return uid;
    }

    public boolean isNesting() {
        return nest != null;
    }

    /**
     * Marks the bee as always visible.
     * This means that the bee detail page will be visible in the BeeAtlas
     * even if the player didn't discover this species yet.
     * Can be turned off in the configuration.
     *
     * @param alwaysVisible If the bee should be always visible or not
     * @return The {@link BeeBuilder} instance for call chaining
     */
    @Nonnull
    public BeeBuilder setAlwaysVisible(boolean alwaysVisible) {
        this.alwaysVisible = alwaysVisible;

        return this;
    }

    /**
     * Marks the bee as enchanted (used for top tier species).
     * This applies a hidden enchantment to the bee {@link ItemStack}.
     *
     * @param enchanted If the bee should be enchanted or not
     * @return The {@link BeeBuilder} instance for call chaining
     */
    @Nonnull
    public BeeBuilder setEnchanted(boolean enchanted) {
        this.enchanted = enchanted;

        return this;
    }

    /**
     * Marks the bee as secret (used for top tier species).
     * This means the bee won't be visible in various places unless discovered.
     *
     * @param secret If the bee should be secret or not
     * @return The {@link BeeBuilder} instance for call chaining
     */
    @Nonnull
    public BeeBuilder setSecret(boolean secret) {
        this.secret = secret;

        return this;
    }

    /**
     * Adds a product with given chance to the bee species.
     *
     * @param item The {@link ItemStack} representing the bee product
     * @param chance The base chance that the bee will produce this item during one production cycle
     * @return The {@link BeeBuilder} instance for call chaining
     */
    @Nonnull
    public BeeBuilder addProduct(ItemStack item, double chance) {
        products.add(new ChanceItemStack(item, chance));
        return this;
    }

    /**
     * Utility method to call multiple builder methods from one variable.
     * Useful for example for adding same genes for multiple bees belonging to the same branch.
     *
     * @param groupDefinition Function(s) to update this {@link BeeBuilder} with
     * @return The {@link BeeBuilder} instance for call chaining
     */
    @Nonnull
    public BeeBuilder addGroupInformation(Consumer<BeeBuilder> groupDefinition) {
        Validate.notNull(groupDefinition, "无法通过空组定义更新 BeeBuilder!");

        groupDefinition.accept(this);
        return this;
    }

    /**
     * Adds an {@link Allele} identified by given uid and {@link ChromosomeType} to the bee species allele template.
     *
     * @param chromosomeType The {@link ChromosomeType} to update with the {@link Allele}
     * @param uid The identifier of the {@link Allele} to set
     * @return The {@link BeeBuilder} instance for call chaining
     */
    @Nonnull
    public BeeBuilder addDefaultAlleleValue(ChromosomeType chromosomeType, String uid) {
        Validate.notNull(chromosomeType, "无法设置空染色体类型的等位基因值!");
        if (chromosomeType == ChromosomeType.SPECIES) {
            throw new IllegalArgumentException("无法直接设置物种染色体!它是自动完成的!");
        }

        alleleService.set(partialTemplate, chromosomeType, uid);
        return this;
    }

    /**
     * Adds a mutation for this bee by specifying both parents and the chance that the mutation will happen.
     *
     * @param firstParentUid The uid of the first parent
     * @param secondParentUid The uid of the second parent
     * @param chance The base chance that the mutation will happen
     * @return The {@link BeeBuilder} instance for call chaining
     */
    @Nonnull
    public BeeBuilder addMutation(String firstParentUid, String secondParentUid, double chance) {
        Validate.notEmpty(firstParentUid, "第一个父级的 uid 不能为空!");
        Validate.notEmpty(secondParentUid, "第二个父级的 uid 不能为空!");

        mutations.add(new Triple<>(firstParentUid, secondParentUid, chance));
        return this;
    }

    /**
     * Adds a naturally spawning nest for this bee.
     * Also marks the bee as always visible!
     *
     * @param env The {@link World.Environment} that the nest can spawn in
     * @param validBiomes The {@link Biome}s that the nest can spawn in
     * @param validFloorMaterials The {@link Material}s that the nest can spawn on
     * @param chance The chance that the nest will spawn in a chunk with the correct biome
     * @return The {@link BeeBuilder} instance for call chaining
     */
    @Nonnull
    public BeeBuilder addNest(World.Environment env, Biome[] validBiomes, Material[] validFloorMaterials, double chance) {
        nest = new NestDTO(env, validBiomes, validFloorMaterials, chance);

        setAlwaysVisible(true);
        return this;
    }

    /**
     * Creates and registers everything needed for this bee species.
     *
     * @param plugin The {@link SlimyBeesPlugin} instance
     */
    public void register(SlimyBeesPlugin plugin) {
        AlleleSpecies species = new AlleleSpecies(uid, name, dominant, secret);
        species.setProducts(products);

        alleleRegistry.register(ChromosomeType.SPECIES, species);

        alleleService.set(partialTemplate, ChromosomeType.SPECIES, uid);
        beeRegistry.registerPartialTemplate(partialTemplate);
        if (alwaysVisible) {
            beeRegistry.registerAlwaysDisplayedSpecies(species);
        }

        Genome genome = geneticService.getGenomeFromAlleles(beeRegistry.getFullTemplate(uid));

        registerItemStacks(plugin, genome);
        registerNest(plugin, species);
        registerMutations();
    }

    private void registerMutations() {
        for (Triple<String, String, Double> dto : mutations) {
            Allele firstParent = alleleRegistry.get(ChromosomeType.SPECIES, dto.getFirst());
            Allele secondParent = alleleRegistry.get(ChromosomeType.SPECIES, dto.getSecond());
            Allele child = alleleRegistry.get(ChromosomeType.SPECIES, uid);

            if (firstParent == null || secondParent == null || child == null) {
                SlimyBeesPlugin.logger().warning("无法注册突变 " + uid + " 和 parents "
                        + dto.getFirst() + " & " + dto.getSecond() + " 因为其中一个uid没有注册!");
            } else {
                BeeMutationDTO mutation = new BeeMutationDTO(dto.getFirst(), dto.getSecond(), uid, dto.getThird());
                beeRegistry.registerMutation(mutation);
            }
        }
    }

    private void registerItemStacks(SlimyBeesPlugin plugin, Genome genome) {
        AlleleSpecies species = genome.getSpecies();
        String coloredName = color + species.getDisplayName();

        SlimefunItemStack princessStack = ItemStacks.createPrincess(species.getName(), coloredName, enchanted, "");
        SlimefunItemStack droneStack = ItemStacks.createDrone(species.getName(), coloredName, enchanted, "");

        // TODO: 01.07.21 Cleaner way to update?
        princessStack = new SlimefunItemStack(princessStack.getItemId(), loreService.updateLore(princessStack, genome));
        droneStack = new SlimefunItemStack(droneStack.getItemId(), loreService.updateLore(droneStack, genome));

        geneticService.updateItemGenome(princessStack, genome);
        geneticService.updateItemGenome(droneStack, genome);

        Princess princess = new Princess(Categories.ITEMS, princessStack, RecipeTypes.BREEDING, ItemStacks.CONSULT_BEE_ATLAS_RECIPE);
        Drone drone = new Drone(Categories.ITEMS, droneStack, RecipeTypes.BREEDING, ItemStacks.CONSULT_BEE_ATLAS_RECIPE);

        princess.register(plugin);
        princess.setHidden(true);
        drone.register(plugin);
        drone.setHidden(true);

        species.setPrincessItemStack(princessStack);
        species.setDroneItemStack(droneStack);
    }

    private void registerNest(SlimyBeesPlugin plugin, AlleleSpecies species) {
        if (isNesting()) {
            SlimefunItemStack nestItemStack = new SlimefunItemStack(
                    species.getName() + "_BEE_NEST",
                    Material.BEEHIVE,
                    color + species.getDisplayName() + "蜂蜡");

            BeeNest nestBlock = new BeeNest(nestItemStack, species.getPrincessItemStack(), species.getDroneItemStack());
            nestBlock.addRandomDrop(new RandomizedItemStack(ItemStacks.HONEY_COMB, 1, 3));

            nestBlock.register(plugin);
            nestBlock.setHidden(true);

            nest.setItemStack(nestItemStack);
            registry.registerNest(nest);
        }
    }

}
