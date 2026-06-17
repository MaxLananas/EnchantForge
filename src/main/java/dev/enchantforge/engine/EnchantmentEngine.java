package dev.enchantforge.engine;

import dev.enchantforge.EnchantForge;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

public final class EnchantmentEngine {

    private final EnchantForge plugin;

    public EnchantmentEngine(EnchantForge plugin) {
        this.plugin = plugin;
    }

    public boolean hasBannedEnchantment(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;

        for (Enchantment enchantment : meta.getEnchants().keySet()) {
            String key = resolveKey(enchantment);
            if (plugin.getConfigManager().isBanned(key)) {
                return true;
            }
        }
        return false;
    }

    public String getFirstBannedEnchantmentName(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return "";
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return "";

        for (Enchantment enchantment : meta.getEnchants().keySet()) {
            String key = resolveKey(enchantment);
            if (plugin.getConfigManager().isBanned(key)) {
                return enchantment.getKey().getKey().toUpperCase();
            }
        }
        return "";
    }

    public double computeDamageMultiplier(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return 1.0;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return 1.0;

        double totalMultiplier = 1.0;

        for (Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
            String key = resolveKey(entry.getKey());
            if (plugin.getConfigManager().isBanned(key)) continue;

            double baseMultiplier = plugin.getConfigManager().getMultiplier(key);
            if (baseMultiplier == 1.0) continue;

            double deviation = baseMultiplier - 1.0;
            int level = entry.getValue();
            totalMultiplier *= (1.0 + deviation * level);
        }

        return totalMultiplier;
    }

    private String resolveKey(Enchantment enchantment) {
        return enchantment.getKey().getKey().toUpperCase();
    }
}
