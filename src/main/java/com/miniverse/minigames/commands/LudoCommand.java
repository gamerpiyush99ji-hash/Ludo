package com.miniverse.minigames.commands;

import com.miniverse.minigames.GameManager;
import com.miniverse.minigames.MiniGamesPlugin;
import com.miniverse.minigames.ludo.LudoGame;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LudoCommand implements CommandExecutor {

    private final MiniGamesPlugin plugin;

    public LudoCommand(MiniGamesPlugin plugin) {
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
            player.sendMessage(ChatColor.YELLOW + "Usage: /ludo join | /ludo start | /ludo roll | /ludo move <1-4>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "join": {
                LudoGame lobby = gm.getOrCreateLobby();
                if (lobby.addPlayer(player)) {
                    gm.registerLudoGame(lobby);
                    player.sendMessage(ChatColor.GREEN + "Aap Ludo lobby mein join ho gaye. Players: " + lobby.getPlayers().size() + "/4");
                } else {
                    player.sendMessage(ChatColor.RED + "Join nahi ho paya (lobby full ya game already started).");
                }
                break;
            }
            case "start": {
                LudoGame game = gm.getLudoGame(player);
                if (game == null) {
                    player.sendMessage(ChatColor.RED + "Aap kisi lobby mein nahi hain. Pehle /ludo join karein.");
                    return true;
                }
                if (!game.start()) {
                    player.sendMessage(ChatColor.RED + "Kam se kam 2 players chahiye start karne ke liye.");
                }
                break;
            }
            case "roll": {
                LudoGame game = gm.getLudoGame(player);
                if (game == null || !game.isStarted()) {
                    player.sendMessage(ChatColor.RED + "Koi active Ludo game nahi mila.");
                    return true;
                }
                game.rollDice(player);
                break;
            }
            case "move": {
                LudoGame game = gm.getLudoGame(player);
                if (game == null || !game.isStarted()) {
                    player.sendMessage(ChatColor.RED + "Koi active Ludo game nahi mila.");
                    return true;
                }
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /ludo move <1-4>");
                    return true;
                }
                try {
                    int idx = Integer.parseInt(args[1]) - 1;
                    game.moveToken(player, idx);
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "Token number 1-4 ke beech hona chahiye.");
                }
                break;
            }
            default:
                player.sendMessage(ChatColor.YELLOW + "Usage: /ludo join | /ludo start | /ludo roll | /ludo move <1-4>");
        }
        return true;
    }
}
