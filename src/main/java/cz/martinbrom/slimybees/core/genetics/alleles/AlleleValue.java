package cz.martinbrom.slimybees.core.genetics.alleles;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;

@ParametersAreNonnullByDefault
public class AlleleValue<T> {

    private final T value;
    private final boolean dominant;

    public AlleleValue(T value) {
        this(value, false);
    }

    public AlleleValue(T value, boolean dominant) {
        Validate.notNull(value, "无法从空值创建等位基因值!");

        this.value = value;
        this.dominant = dominant;
    }

    @Nonnull
    public T getValue() {
        return value;
    }

    public boolean isDominant() {
        return dominant;
    }

}
