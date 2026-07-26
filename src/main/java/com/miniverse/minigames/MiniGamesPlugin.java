package com.miniverse.minigames;

import com.miniverse.minigames.commands.LudoCommand;
import com.miniverse.minigames.commands.TicTacToeCommand;
import com.miniverse.minigames.listeners.GameListener;
import org.bukkit.plugin.java.JavaPlugin;

public class MiniGamesPlugin extends JavaPlugin {

    private GameManager gameManager;

    @Override
    public void onEnable() {
        this.gameManager = new GameManager();
        getCommand("ttt").setExecutor(new TicTacToeCommand(this));
        getCommand("ludo").setExecutor(new LudoCommand(this));
        getServer().getPluginManager().registerEvents(new GameListener(this), this);
        getLogger().info("MiniGames plugin enabled! Tic-Tac-Toe aur Ludo ready hain.");
    }

    @Override
    public void onDisable() {
        getLogger().info("MiniGames plugin disabled.");
    }

    public GameManager getGameManager() {
        return gameManager;
    }
}
