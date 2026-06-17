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

    private final Map<String, Double> multipliers   = new HashMap<>();
    private final Set<String>         banned        = new HashSet<>();

    private String prefix;
    private String msgBanned;
    private String msgReloadSuccess;
    private String msgReloadFail;
    private String msgNoPermission;

    public ConfigManager(EnchantForge plugin) {
        this.plugin = plugin;
    }

    public void load() {
        plugin.reloadConfig();
        multipliers.clear();
        banned.clear();

        ConfigurationSection section = plugin.getConfig()
                .getConfigurationSection("enchantments");

        if (section != null) {
            for (String key : section.getKeys(false)) {
                ConfigurationSection entry = section.getConfigurationSection(key);
                if (entry == null) continue;

                boolean enabled    = entry.getBoolean("enabled", true);
                boolean isBanned   = entry.getBoolean("banned",  false);
                double  multiplier = entry.getDouble("damage_multiplier", 1.0);

                if (!enabled) continue;

                String normalized = key.toUpperCase();

                if (isBanned) {
                    banned.add(normalized);
                } else if (multiplier != 1.0) {
                    multipliers.put(normalized, multiplier);
                }
            }
        }

        prefix           = plugin.getConfig().getString("messages.prefix",                "&8[&6EnchantForge&8] ");
        msgBanned        = plugin.getConfig().getString("messages.banned_enchant_blocked", "&cForbidden enchantment: &e{enchant}");
        msgReloadSuccess = plugin.getConfig().getString("messages.reload_success",         "&aConfiguration reloaded.");
        msgReloadFail    = plugin.getConfig().getString("messages.reload_fail",            "&cReload failed.");
        msgNoPermission  = plugin.getConfig().getString("messages.no_permission",          "&cNo permission.");
    }

    public Map<String, Double> getMultipliers() {
        return Collections.unmodifiableMap(multipliers);
    }

    public Set<String> getBannedEnchantments() {
        return Collections.unmodifiableSet(banned);
    }

    public double getMultiplier(String key) {
        return multipliers.getOrDefault(key.toUpperCase(), 1.0);
    }

    public boolean isBanned(String key) {
        return banned.contains(key.toUpperCase());
    }

    public String getPrefix()           { return prefix; }
    public String getMsgBanned()        { return msgBanned; }
    public String getMsgReloadSuccess() { return msgReloadSuccess; }
    public String getMsgReloadFail()    { return msgReloadFail; }
    public String getMsgNoPermission()  { return msgNoPermission; }
}
