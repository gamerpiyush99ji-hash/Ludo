package com.miniverse.minigames;

import com.miniverse.minigames.ludo.LudoGame;
import com.miniverse.minigames.tictactoe.TicTacToeGame;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GameManager {

    // ---- Tic-Tac-Toe state ----
    private final Map<UUID, TicTacToeGame> tttGames = new HashMap<>();
    private final Map<UUID, UUID> tttChallenges = new HashMap<>(); // challenger -> target

    public void addChallenge(Player from, Player to) {
        tttChallenges.put(from.getUniqueId(), to.getUniqueId());
    }

    public boolean hasChallengeFrom(Player from, Player to) {
        UUID target = tttChallenges.get(from.getUniqueId());
        return target != null && target.equals(to.getUniqueId());
    }

    public void clearChallenge(Player from) {
        tttChallenges.remove(from.getUniqueId());
    }

    public void startTicTacToe(Player x, Player o) {
        TicTacToeGame game = new TicTacToeGame(x, o);
        tttGames.put(x.getUniqueId(), game);
        tttGames.put(o.getUniqueId(), game);
        x.openInventory(game.getInventory());
        o.openInventory(game.getInventory());
    }

    public TicTacToeGame getTicTacToeGame(Player p) {
        return tttGames.get(p.getUniqueId());
    }

    public void endTicTacToe(TicTacToeGame game) {
        tttGames.remove(game.getPlayerX().getUniqueId());
        tttGames.remove(game.getPlayerO().getUniqueId());
    }

    // ---- Ludo state ----
    private final Map<UUID, LudoGame> ludoGames = new HashMap<>();
    private LudoGame currentLudoLobby;

    public LudoGame getOrCreateLobby() {
        if (currentLudoLobby == null || currentLudoLobby.isStarted()) {
            currentLudoLobby = new LudoGame();
        }
        return currentLudoLobby;
    }

    public LudoGame getLudoGame(Player p) {
        return ludoGames.get(p.getUniqueId());
    }

    public void registerLudoGame(LudoGame game) {
        for (Player p : game.getPlayers()) {
            ludoGames.put(p.getUniqueId(), game);
        }
    }

    public void endLudoGame(LudoGame game) {
        for (Player p : game.getPlayers()) {
            ludoGames.remove(p.getUniqueId());
        }
        if (game == currentLudoLobby) currentLudoLobby = null;
    }
}
