package com.miniverse.minigames.commands;

import com.miniverse.minigames.GameManager;
import com.miniverse.minigames.MiniGamesPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TicTacToeCommand implements CommandExecutor {

    private final MiniGamesPlugin plugin;

    public TicTacToeCommand(MiniGamesPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Sirf players is command ko use kar sakte hain.");
            return true;
        }
        Player player = (Player) sender;
        GameManager gm = plugin.getGameManager();

        if (args.length == 0) {
            player.sendMessage(ChatColor.YELLOW + "Usage: /ttt challenge <player> | /ttt accept <player>");
            return true;
        }

        if (args[0].equalsIgnoreCase("challenge")) {
            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Usage: /ttt challenge <player>");
                return true;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null || !target.isOnline()) {
                player.sendMessage(ChatColor.RED + "Player online nahi hai.");
                return true;
            }
            if (target.equals(player)) {
                player.sendMessage(ChatColor.RED + "Khud ko challenge nahi kar sakte!");
                return true;
            }
            gm.addChallenge(player, target);
            player.sendMessage(ChatColor.GREEN + "Challenge bhej diya " + target.getName() + " ko.");
            target.sendMessage(ChatColor.GOLD + player.getName() + " ne aapko Tic-Tac-Toe challenge kiya! /ttt accept " + player.getName() + " type karein.");
            return true;
        }

        if (args[0].equalsIgnoreCase("accept")) {
            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Usage: /ttt accept <player>");
                return true;
            }
            Player challenger = Bukkit.getPlayer(args[1]);
            if (challenger == null || !challenger.isOnline()) {
                player.sendMessage(ChatColor.RED + "Player online nahi hai.");
                return true;
            }
            if (!gm.hasChallengeFrom(challenger, player)) {
                player.sendMessage(ChatColor.RED + "Koi pending challenge nahi mila.");
                return true;
            }
            gm.clearChallenge(challenger);
            gm.startTicTacToe(challenger, player);
            return true;
        }

        player.sendMessage(ChatColor.YELLOW + "Usage: /ttt challenge <player> | /ttt accept <player>");
        return true;
    }
}
