package dev.enchantforge;

import dev.enchantforge.command.EnchantForgeCommand;
import dev.enchantforge.config.ConfigManager;
import dev.enchantforge.listener.DamageListener;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.java.JavaPlugin;

public final class EnchantForge extends JavaPlugin {

    private static EnchantForge instance;
    private ConfigManager configManager;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

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

        getLogger().info("EnchantForge 1.0.0 activé — Enchantments reforged.");
    }

    @Override
    public void onDisable() {
        getLogger().info("EnchantForge désactivé.");
    }

    public static EnchantForge getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MiniMessage getMiniMessage() {
        return miniMessage;
    }
}
