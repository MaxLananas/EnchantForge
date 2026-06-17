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
        if (!hasEnchants(item)) return false;
        for (Enchantment e : item.getItemMeta().getEnchants().keySet()) {
            if (plugin.getConfigManager().isBanned(resolveKey(e))) return true;
        }
        return false;
    }

    public String getFirstBannedEnchantmentName(ItemStack item) {
        if (!hasEnchants(item)) return "";
        for (Enchantment e : item.getItemMeta().getEnchants().keySet()) {
            String key = resolveKey(e);
            if (plugin.getConfigManager().isBanned(key)) return key;
        }
        return "";
    }

    public double computeDamageMultiplier(ItemStack item) {
        if (!hasEnchants(item)) return 1.0;

        double total = 1.0;
        for (Map.Entry<Enchantment, Integer> entry : item.getItemMeta().getEnchants().entrySet()) {
            String key = resolveKey(entry.getKey());
            if (plugin.getConfigManager().isBanned(key)) continue;

            double base = plugin.getConfigManager().getMultiplier(key);
            if (base == 1.0) continue;

            double deviation = base - 1.0;
            int    level     = entry.getValue();
            total *= (1.0 + deviation * level);
        }
        return total;
    }

    private boolean hasEnchants(ItemStack item) {
        if (item == null) return false;
        ItemMeta meta = item.getItemMeta();
        return meta != null && !meta.getEnchants().isEmpty();
    }

    private String resolveKey(Enchantment enchantment) {
        return enchantment.getKey().getKey().toUpperCase();
    }
}
