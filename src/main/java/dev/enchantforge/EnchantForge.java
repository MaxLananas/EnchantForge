package dev.enchantforge;

import dev.enchantforge.command.EnchantForgeCommand;
import dev.enchantforge.config.ConfigManager;
import dev.enchantforge.listener.DamageListener;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.plugin.java.JavaPlugin;

public final class EnchantForge extends JavaPlugin {

    private static EnchantForge instance;
    private ConfigManager configManager;
    private final LegacyComponentSerializer legacy = LegacyComponentSerializer.legacyAmpersand();

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        configManager = new ConfigManager(this);
        configManager.load();

        getServer().getPluginManager().registerEvents(new DamageListener(this), this);

        var cmd = getCommand("enchantforge");
        if (cmd != null) {
            var handler = new EnchantForgeCommand(this);
            cmd.setExecutor(handler);
            cmd.setTabCompleter(handler);
        }

        getLogger().info("EnchantForge " + getDescription().getVersion() + " enabled — enchantments reforged.");
        getLogger().info("Loaded " + configManager.getMultipliers().size() + " modifier(s), "
                + configManager.getBannedEnchantments().size() + " ban(s).");
    }

    @Override
    public void onDisable() {
        getLogger().info("EnchantForge disabled.");
    }

    public static EnchantForge getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public LegacyComponentSerializer getLegacy() {
        return legacy;
    }
}
