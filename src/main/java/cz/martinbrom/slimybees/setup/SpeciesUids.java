package cz.martinbrom.slimybees.setup;

import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType;

import static cz.martinbrom.slimybees.utils.StringUtils.nameToUid;

/**
 * This class holds a uid for every base species in SlimyBees.
 */
public class SpeciesUids {

    // prevent instantiation
    private SpeciesUids() {}

    public static final String FOREST = nameToUid(ChromosomeType.SPECIES, "森林");
    public static final String MEADOWS = nameToUid(ChromosomeType.SPECIES, "meadows");
    public static final String STONE = nameToUid(ChromosomeType.SPECIES, "石头");
    public static final String SANDY = nameToUid(ChromosomeType.SPECIES, "沙地");
    public static final String WATER = nameToUid(ChromosomeType.SPECIES, "水");
    public static final String NETHER = nameToUid(ChromosomeType.SPECIES, "下届");
    public static final String ENDER = nameToUid(ChromosomeType.SPECIES, "末地");

    public static final String COMMON = nameToUid(ChromosomeType.SPECIES, "普通");
    public static final String CULTIVATED = nameToUid(ChromosomeType.SPECIES, "cultivated");
    public static final String NOBLE = nameToUid(ChromosomeType.SPECIES, "高贵");
    public static final String MAJESTIC = nameToUid(ChromosomeType.SPECIES, "雄伟");
    public static final String IMPERIAL = nameToUid(ChromosomeType.SPECIES, "御");
    public static final String DILIGENT = nameToUid(ChromosomeType.SPECIES, "勤劳");
    public static final String UNWEARY = nameToUid(ChromosomeType.SPECIES, "不知疲倦");
    public static final String INDUSTRIOUS = nameToUid(ChromosomeType.SPECIES, "勤劳");

    public static final String FARMER = nameToUid(ChromosomeType.SPECIES, "农民");
    public static final String WHEAT = nameToUid(ChromosomeType.SPECIES, "小麦");
    public static final String SUGAR_CANE = nameToUid(ChromosomeType.SPECIES, "甘蔗");
    public static final String MELON = nameToUid(ChromosomeType.SPECIES, "西瓜");
    public static final String PUMPKIN = nameToUid(ChromosomeType.SPECIES, "南瓜");
    public static final String POTATO = nameToUid(ChromosomeType.SPECIES, "马拉松");
    public static final String CARROT = nameToUid(ChromosomeType.SPECIES, "胡萝卜");
    public static final String BEETROOT = nameToUid(ChromosomeType.SPECIES, "甜菜根");
    public static final String COCOA = nameToUid(ChromosomeType.SPECIES, "可可");
    public static final String BERRY = nameToUid(ChromosomeType.SPECIES, "甜浆果");
    public static final String GLOW_BERRY = nameToUid(ChromosomeType.SPECIES, "发光甜浆果");

    public static final String SECRET = nameToUid(ChromosomeType.SPECIES, "神秘");

}
