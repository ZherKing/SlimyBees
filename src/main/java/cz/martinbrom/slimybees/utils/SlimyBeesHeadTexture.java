package cz.martinbrom.slimybees.utils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.libraries.dough.common.CommonPatterns;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;

@ParametersAreNonnullByDefault
public enum SlimyBeesHeadTexture {

    // drone & princess are the same for now
    DRONE("4420c9c43e095880dcd2e281c81f47b163b478f58a584bb61f93e6e10a155f31"),
    PRINCESS("4420c9c43e095880dcd2e281c81f47b163b478f58a584bb61f93e6e10a155f31"),
    HIVE("b64169076dbdb87f279448d5a16ff78bb0a2b57503c218e21732dba9f7f9f55a");

    private final String texture;
    private final UUID uuid;

    SlimyBeesHeadTexture(String texture) {
        Validate.notNull(texture, "纹理不能为空");
        Validate.isTrue(CommonPatterns.HEXADECIMAL.matcher(texture).matches(), "纹理必须是十六进制.");

        this.texture = texture;
        this.uuid = UUID.nameUUIDFromBytes(texture.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Returns the texture hash for this particular head.
     *
     * @return The associated texture hash
     */
    @Nonnull
    public String getTexture() {
        return texture;
    }

    /**
     * Returns the {@link UUID} for this {@link SlimyBeesHeadTexture}.
     * The {@link UUID} is generated from the texture and cached for
     * performance reasons.
     *
     * @return The {@link UUID} for this {@link SlimyBeesHeadTexture}
     */
    @Nonnull
    public UUID getUniqueId() {
        return uuid;
    }

    /**
     * Returns an {@link ItemStack} with the given texture assigned to it.
     *
     * @return A custom head {@link ItemStack}
     */
    @Nonnull
    public ItemStack getAsItemStack() {
        return SlimefunUtils.getCustomHead(getTexture());
    }

}
