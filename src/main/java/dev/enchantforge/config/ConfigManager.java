package dev.enchantforge.config;

import dev.enchantforge.EnchantForge;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class ConfigManager {

    private final EnchantForge plugin;

    private final Map<String, Double> multipliers = new HashMap<>();
    private final Set<String> bannedEnchantments = new HashSet<>();

    private String prefix;
    private String msgBanned;
    private String msgReloadSuccess;
    private String msgReloadFail;

    public ConfigManager(EnchantForge plugin) {
        this.plugin = plugin;
    }

    public void load() {
        plugin.reloadConfig();
        multipliers.clear();
        bannedEnchantments.clear();

        ConfigurationSection section = plugin.getConfig()
                .getConfigurationSection("enchantments.modifiers");

        if (section != null) {
            for (String key : section.getKeys(false)) {
                ConfigurationSection enchSection = section.getConfigurationSection(key);
                if (enchSection == null) continue;

                boolean enabled = enchSection.getBoolean("enabled", true);
                boolean banned = enchSection.getBoolean("banned", false);
                double multiplier = enchSection.getDouble("damage_multiplier", 1.0);

                if (!enabled) continue;

                if (banned) {
                    bannedEnchantments.add(key.toUpperCase());
                } else {
                    multipliers.put(key.toUpperCase(), multiplier);
                }
            }
        }

        prefix = plugin.getConfig().getString("messages.prefix", "&8[&6EnchantForge&8] ");
        msgBanned = plugin.getConfig().getString("messages.banned_enchant_blocked",
                "&cL'enchantement &e{enchant} &cest interdit.");
        msgReloadSuccess = plugin.getConfig().getString("messages.reload_success",
                "&aRechargement réussi.");
        msgReloadFail = plugin.getConfig().getString("messages.reload_fail",
                "&cErreur de rechargement.");
    }

    public Map<String, Double> getMultipliers() {
        return Collections.unmodifiableMap(multipliers);
    }

    public Set<String> getBannedEnchantments() {
        return Collections.unmodifiableSet(bannedEnchantments);
    }

    public double getMultiplier(String enchantmentKey) {
        return multipliers.getOrDefault(enchantmentKey.toUpperCase(), 1.0);
    }

    public boolean isBanned(String enchantmentKey) {
        return bannedEnchantments.contains(enchantmentKey.toUpperCase());
    }

    public String getPrefix() { return prefix; }
    public String getMsgBanned() { return msgBanned; }
    public String getMsgReloadSuccess() { return msgReloadSuccess; }
    public String getMsgReloadFail() { return msgReloadFail; }
}
