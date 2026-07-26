package com.miniverse.minigames.tictactoe;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TicTacToeGame implements InventoryHolder {

    public enum Mark { EMPTY, X, O }

    private final Player playerX;
    private final Player playerO;
    private final Mark[] board = new Mark[9];
    private Player currentTurn;
    private final Inventory inventory;
    private boolean finished = false;

    private static final int[][] WIN_LINES = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
            {0, 4, 8}, {2, 4, 6}
    };

    public TicTacToeGame(Player playerX, Player playerO) {
        this.playerX = playerX;
        this.playerO = playerO;
        this.currentTurn = playerX;
        for (int i = 0; i < 9; i++) board[i] = Mark.EMPTY;
        this.inventory = Bukkit.createInventory(this, 9, ChatColor.GOLD + "Tic-Tac-Toe");
        render();
    }

    public Player getPlayerX() { return playerX; }
    public Player getPlayerO() { return playerO; }
    public boolean isFinished() { return finished; }

    @Override
    public Inventory getInventory() { return inventory; }

    public boolean isParticipant(Player p) {
        return p.equals(playerX) || p.equals(playerO);
    }

    private Mark markOf(Player p) {
        return p.equals(playerX) ? Mark.X : Mark.O;
    }

    public void handleClick(Player clicker, int slot) {
        if (finished) return;
        if (!clicker.equals(currentTurn)) {
            clicker.sendMessage(ChatColor.RED + "Ye aapki turn nahi hai!");
            return;
        }
        if (slot < 0 || slot > 8 || board[slot] != Mark.EMPTY) {
            clicker.sendMessage(ChatColor.RED + "Ye cell available nahi hai!");
            return;
        }

        board[slot] = markOf(clicker);
        render();

        Mark winner = checkWinner();
        if (winner != Mark.EMPTY) {
            finished = true;
            Player winPlayer = (winner == Mark.X) ? playerX : playerO;
            Player losePlayer = (winner == Mark.X) ? playerO : playerX;
            winPlayer.sendMessage(ChatColor.GREEN + "Aap jeet gaye! \uD83C\uDF89");
            losePlayer.sendMessage(ChatColor.RED + "Aap haar gaye.");
            closeSoon();
            return;
        }
        if (isBoardFull()) {
            finished = true;
            playerX.sendMessage(ChatColor.YELLOW + "Match draw ho gaya!");
            playerO.sendMessage(ChatColor.YELLOW + "Match draw ho gaya!");
            closeSoon();
            return;
        }

        currentTurn = currentTurn.equals(playerX) ? playerO : playerX;
        playerX.sendMessage(currentTurn.equals(playerX) ? ChatColor.AQUA + "Aapki turn hai!" : ChatColor.GRAY + "Opponent ki turn.");
        playerO.sendMessage(currentTurn.equals(playerO) ? ChatColor.AQUA + "Aapki turn hai!" : ChatColor.GRAY + "Opponent ki turn.");
    }

    private void closeSoon() {
        Bukkit.getScheduler().runTaskLater(
                Bukkit.getPluginManager().getPlugin("MiniGames"),
                () -> { playerX.closeInventory(); playerO.closeInventory(); },
                60L
        );
    }

    private boolean isBoardFull() {
        for (Mark m : board) if (m == Mark.EMPTY) return false;
        return true;
    }

    private Mark checkWinner() {
        for (int[] line : WIN_LINES) {
            Mark a = board[line[0]], b = board[line[1]], c = board[line[2]];
            if (a != Mark.EMPTY && a == b && b == c) return a;
        }
        return Mark.EMPTY;
    }

    private void render() {
        for (int i = 0; i < 9; i++) {
            ItemStack item;
            switch (board[i]) {
                case X: item = new ItemStack(Material.RED_STAINED_GLASS_PANE); break;
                case O: item = new ItemStack(Material.LIME_STAINED_GLASS_PANE); break;
                default: item = new ItemStack(Material.WHITE_STAINED_GLASS_PANE); break;
            }
            ItemMeta meta = item.getItemMeta();
            String label = board[i] == Mark.EMPTY ? ChatColor.GRAY + "Empty" :
                    (board[i] == Mark.X ? ChatColor.RED + "X" : ChatColor.GREEN + "O");
            meta.setDisplayName(label);
            item.setItemMeta(meta);
            inventory.setItem(i, item);
        }
    }
}
