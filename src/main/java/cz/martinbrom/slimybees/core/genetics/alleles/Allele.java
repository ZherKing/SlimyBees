package cz.martinbrom.slimybees.core.genetics.alleles;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;

import cz.martinbrom.slimybees.utils.PatternUtil;
import cz.martinbrom.slimybees.utils.StringUtils;

@ParametersAreNonnullByDefault
public class Allele implements Comparable<Allele> {

    private final String uid;
    private final String name;
    private final String displayName;
    private final boolean dominant;

    public Allele(String uid, String name, boolean dominant) {
        Validate.notEmpty(uid, "等位基因 uid 不得为空或为空！");
        Validate.isTrue(PatternUtil.UID_PATTERN.matcher(uid).matches(), "等位基因 uid 必须以前缀开头，" +
                "并且以小写蛇形命名法命名，但得到了 " + uid + "！");
        Validate.notEmpty(name, "等位基因名称不得为无或空!");
        Validate.isTrue(PatternUtil.UPPER_SNAKE.matcher(name).matches(), "等位基因名称必须为" +
                "大写蛇形命名法命名，但得到了 " + name + "！");

        this.uid = uid;
        this.name = name;
        this.displayName = StringUtils.humanizeSnake(name);
        this.dominant = dominant;
    }

    @Nonnull
    public String getUid() {
        return uid;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nonnull
    public String getDisplayName() {
        return displayName;
    }

    public boolean isDominant() {
        return dominant;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Allele allele = (Allele) o;
        return uid.equals(allele.uid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid);
    }

    @Override
    public int compareTo(Allele allele) {
        return getUid().compareTo(allele.getUid());
    }

}
