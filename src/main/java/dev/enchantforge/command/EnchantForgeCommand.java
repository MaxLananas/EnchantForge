package dev.enchantforge.command;

import dev.enchantforge.EnchantForge;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public final class EnchantForgeCommand implements CommandExecutor, TabCompleter {

    private static final List<String> SUBCOMMANDS = List.of("reload", "info", "list");

    private final EnchantForge plugin;

    public EnchantForgeCommand(EnchantForge plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String label,
                             @NotNull String[] args) {

        if (!sender.hasPermission("enchantforge.admin")) {
            send(sender, plugin.getConfigManager().getPrefix()
                    + plugin.getConfigManager().getMsgNoPermission());
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload" -> handleReload(sender);
            case "info"   -> sendInfo(sender);
            case "list"   -> sendList(sender);
            default       -> sendHelp(sender);
        }
        return true;
    }

    private void handleReload(CommandSender sender) {
        try {
            plugin.getConfigManager().load();
            send(sender, plugin.getConfigManager().getPrefix()
                    + plugin.getConfigManager().getMsgReloadSuccess());
        } catch (Exception ex) {
            send(sender, plugin.getConfigManager().getPrefix()
                    + plugin.getConfigManager().getMsgReloadFail());
            plugin.getLogger().severe("Reload error: " + ex.getMessage());
        }
    }

    private void sendHelp(CommandSender sender) {
        send(sender, "&8&m────────────────────────────────────────");
        send(sender, "&6&lEnchantForge &7v" + plugin.getDescription().getVersion());
        send(sender, " &e/ef reload &7— Reload the configuration");
        send(sender, " &e/ef info   &7— Show active modifiers summary");
        send(sender, " &e/ef list   &7— List all registered enchantments");
        send(sender, "&8&m────────────────────────────────────────");
    }

    private void sendInfo(CommandSender sender) {
        Map<String, Double> mods   = plugin.getConfigManager().getMultipliers();
        Set<String>         bans   = plugin.getConfigManager().getBannedEnchantments();

        send(sender, "&8&m────────────────────────────────────────");
        send(sender, "&6&lEnchantForge &7— Active modifiers");

        if (mods.isEmpty() && bans.isEmpty()) {
            send(sender, "&7No modifiers configured.");
        }

        mods.forEach((enchant, mult) -> {
            double  percent = (mult - 1.0) * 100.0;
            String  color   = percent >= 0 ? "&a" : "&c";
            String  sign    = percent >= 0 ? "+" : "";
            send(sender, " &e" + enchant + " &7→ " + color + sign + String.format("%.1f", percent) + "%");
        });

        bans.forEach(enchant ->
                send(sender, " &e" + enchant + " &7→ &c&lBANNED"));

        send(sender, "&8&m────────────────────────────────────────");
    }

    private void sendList(CommandSender sender) {
        send(sender, "&8&m────────────────────────────────────────");
        send(sender, "&6&lEnchantForge &7— All registered enchantments");

        Map<String, Double> mods = plugin.getConfigManager().getMultipliers();
        Set<String>         bans = plugin.getConfigManager().getBannedEnchantments();

        for (org.bukkit.enchantments.Enchantment enc
                : org.bukkit.Registry.ENCHANTMENT) {

            String key    = enc.getKey().getKey().toUpperCase();
            String status;

            if (bans.contains(key)) {
                status = "&c[BANNED]";
            } else if (mods.containsKey(key)) {
                double pct  = (mods.get(key) - 1.0) * 100.0;
                String sign = pct >= 0 ? "+" : "";
                status = "&a[" + sign + String.format("%.1f", pct) + "%]";
            } else {
                status = "&7[vanilla]";
            }

            send(sender, " &e" + key + " &r" + status);
        }

        send(sender, "&8&m────────────────────────────────────────");
    }

    private void send(CommandSender sender, String message) {
        Component component = plugin.getLegacy().deserialize(message);
        sender.sendMessage(component);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender,
                                                @NotNull Command command,
                                                @NotNull String label,
                                                @NotNull String[] args) {
        if (args.length == 1) {
            String input = args[0].toLowerCase();
            return SUBCOMMANDS.stream()
                    .filter(s -> s.startsWith(input))
                    .toList();
        }
        return List.of();
    }
}
