package cz.martinbrom.slimybees.core;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;

@ParametersAreNonnullByDefault
public class BeeMutationDTO {

    private final String firstParent;
    private final String secondParent;
    private final String child;
    private final double chance;

    public BeeMutationDTO(String firstParent, String secondParent, String child, double chance) {
        Validate.notNull(firstParent, "BeeMutation 需要两个 parents, 且第一个不能为空!");
        Validate.notNull(secondParent, "BeeMutation 需要两个 parents, 且第二个不能为空!");
        Validate.notNull(child, "BeeMutation 的子代不能为空!");
        Validate.isTrue(chance > 0 && chance < 1, "BeeMutation 的几率需要大于 0 且小于 1!");

        if (firstParent.compareTo(secondParent) < 0) {
            this.firstParent = firstParent;
            this.secondParent = secondParent;
        } else {
            this.firstParent = secondParent;
            this.secondParent = firstParent;
        }

        this.child = child;
        this.chance = chance;
    }

    @Nonnull
    public String getFirstParent() {
        return firstParent;
    }

    @Nonnull
    public String getSecondParent() {
        return secondParent;
    }

    @Nonnull
    public String getChild() {
        return child;
    }

    public double getChance() {
        return chance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof BeeMutationDTO)) {
            return false;
        }


        BeeMutationDTO other = (BeeMutationDTO) o;

        return firstParent.equals(other.getFirstParent())
                && secondParent.equals(other.getSecondParent())
                && child.equals(other.getChild());
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstParent, secondParent, child);
    }

}
