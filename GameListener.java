package com.miniverse.minigames.listeners;

import com.miniverse.minigames.MiniGamesPlugin;
import com.miniverse.minigames.ludo.LudoGame;
import com.miniverse.minigames.tictactoe.TicTacToeGame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

public class GameListener implements Listener {

    private final MiniGamesPlugin plugin;

    public GameListener(MiniGamesPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();

        if (holder instanceof TicTacToeGame) {
            event.setCancelled(true);
            if (!(event.getWhoClicked() instanceof Player)) return;
            Player p = (Player) event.getWhoClicked();
            TicTacToeGame game = (TicTacToeGame) holder;
            if (!game.isParticipant(p)) return;
            game.handleClick(p, event.getRawSlot());

        } else if (holder instanceof LudoGame) {
            event.setCancelled(true);
            if (!(event.getWhoClicked() instanceof Player)) return;
            Player p = (Player) event.getWhoClicked();
            LudoGame game = (LudoGame) holder;
            int slot = event.getRawSlot();
            if (slot == 0) {
                game.rollDice(p);
            } else if (slot >= 2 && slot <= 5) {
                game.moveToken(p, slot - 2);
            }
        }
    }
}
