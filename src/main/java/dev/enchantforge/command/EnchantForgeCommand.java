package dev.enchantforge.command;

import dev.enchantforge.EnchantForge;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
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

    private final EnchantForge plugin;
    private final LegacyComponentSerializer legacy = LegacyComponentSerializer.legacyAmpersand();

    public EnchantForgeCommand(EnchantForge plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String label,
                             @NotNull String[] args) {

        if (!sender.hasPermission("enchantforge.admin")) {
            send(sender, "&cVous n'avez pas la permission.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload" -> {
                try {
                    plugin.getConfigManager().load();
                    send(sender, plugin.getConfigManager().getPrefix()
                            + plugin.getConfigManager().getMsgReloadSuccess());
                } catch (Exception e) {
                    send(sender, plugin.getConfigManager().getPrefix()
                            + plugin.getConfigManager().getMsgReloadFail());
                    plugin.getLogger().severe("Reload error: " + e.getMessage());
                }
            }
            case "info" -> sendInfo(sender);
            default -> sendHelp(sender);
        }
        return true;
    }

    private void sendHelp(CommandSender sender) {
        send(sender, "&8&m                                        ");
        send(sender, "&6&lEnchantForge &7v" + plugin.getDescription().getVersion());
        send(sender, "&e/ef reload &7— Recharge la configuration");
        send(sender, "&e/ef info &7— Affiche les modificateurs actifs");
        send(sender, "&8&m                                        ");
    }

    private void sendInfo(CommandSender sender) {
        send(sender, "&8&m                                        ");
        send(sender, "&6&lEnchantForge &7— Modificateurs actifs");

        Map<String, Double> multipliers = plugin.getConfigManager().getMultipliers();
        Set<String> banned = plugin.getConfigManager().getBannedEnchantments();

        if (multipliers.isEmpty() && banned.isEmpty()) {
            send(sender, "&7Aucun modificateur configuré.");
        }

        multipliers.forEach((enchant, mult) -> {
            double percent = (mult - 1.0) * 100;
            String sign = percent >= 0 ? "&a+" : "&c";
            send(sender, "&e" + enchant + " &7→ " + sign + String.format("%.1f", percent) + "%&7 dégâts");
        });

        banned.forEach(enchant ->
                send(sender, "&e" + enchant + " &7→ &cBANNI"));

        send(sender, "&8&m                                        ");
    }

    private void send(CommandSender sender, String message) {
        Component component = legacy.deserialize(message);
        sender.sendMessage(component);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender,
                                                @NotNull Command command,
                                                @NotNull String label,
                                                @NotNull String[] args) {
        if (args.length == 1) {
            return List.of("reload", "info");
        }
        return List.of();
    }
}
