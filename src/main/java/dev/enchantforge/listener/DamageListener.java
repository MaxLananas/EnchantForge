package dev.enchantforge.listener;

import dev.enchantforge.EnchantForge;
import dev.enchantforge.engine.EnchantmentEngine;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public final class DamageListener implements Listener {

    private final EnchantForge      plugin;
    private final EnchantmentEngine engine;

    public DamageListener(EnchantForge plugin) {
        this.plugin = plugin;
        this.engine = new EnchantmentEngine(plugin);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;

        ItemStack weapon = attacker.getInventory().getItemInMainHand();

        if (engine.hasBannedEnchantment(weapon)) {
            event.setCancelled(true);

            String enchantName = engine.getFirstBannedEnchantmentName(weapon);
            String raw = plugin.getConfigManager().getPrefix()
                    + plugin.getConfigManager().getMsgBanned()
                        .replace("{enchant}", enchantName);

            Component msg = plugin.getLegacy().deserialize(raw);
            attacker.sendMessage(msg);
            return;
        }

        double multiplier = engine.computeDamageMultiplier(weapon);
        if (multiplier != 1.0) {
            event.setDamage(event.getDamage() * multiplier);
        }
    }
}
