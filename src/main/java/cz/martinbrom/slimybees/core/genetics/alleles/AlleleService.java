package cz.martinbrom.slimybees.core.genetics.alleles;

import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;

import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType;

@ParametersAreNonnullByDefault
public class AlleleService {

    private final AlleleRegistry alleleRegistry;

    public AlleleService(AlleleRegistry alleleRegistry) {
        this.alleleRegistry = alleleRegistry;
    }

    public void set(Allele[] template, ChromosomeType type, String uid) {
        Validate.notNull(template, "无法更新空等位基因模板!");
        Validate.notNull(type, "无法更新属于空染色体类型的等位基因!");
        Validate.notNull(uid, "无法通过等位基因更新具有空 uid 的等位基因!");

        Allele allele = alleleRegistry.get(type, uid);
        if (allele == null) {
            throw new IllegalArgumentException("这里没有该类型的等位基因: " + type + " 和 uid: " + uid);
        }

        template[type.ordinal()] = allele;
    }

}
