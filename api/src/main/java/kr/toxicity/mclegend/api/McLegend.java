package kr.toxicity.mclegend.api;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class McLegend extends JavaPlugin {
    private static McLegend instance;
    @Override
    public final void onLoad() {
        if (instance != null) throw new RuntimeException();
        instance = this;
    }
    public static @NotNull McLegend inst() {
        return Objects.requireNonNull(instance);
    }
}
