package com.miniverse.minigames.ludo;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * A simplified Ludo implementation (not the exact classic board geometry,
 * but same core rules): shared loop of 40 squares, each player enters at
 * an offset of 10 squares apart, then a 6-square home stretch to finish.
 * Roll a 6 to leave home or to get an extra turn. Landing on an opponent
 * on a non-safe square sends it back home.
 */
public class LudoGame implements InventoryHolder {

    public enum Color { RED, GREEN, YELLOW, BLUE }

    private static final int LOOP_LENGTH = 40;
    private static final int FINISH = 46; // 40..45 = home stretch, 46 = finished

    private final List<Player> players = new ArrayList<>();
    private final Map<UUID, Color> colorOf = new HashMap<>();
    private final Map<UUID, int[]> tokens = new HashMap<>(); // -1 = at home base
    private final Inventory inventory;

    private boolean started = false;
    private int currentPlayerIndex = 0;
    private int lastRoll = -1;
    private int consecutiveSixes = 0;
    private boolean rolledThisTurn = false;

    public LudoGame() {
        this.inventory = Bukkit.createInventory(this, 9, ChatColor.GOLD + "Ludo");
    }

    @Override
    public Inventory getInventory() { return inventory; }

    public boolean isStarted() { return started; }
    public List<Player> getPlayers() { return players; }

    public boolean addPlayer(Player p) {
        if (started || players.size() >= 4 || players.contains(p)) return false;
        players.add(p);
        colorOf.put(p.getUniqueId(), Color.values()[players.size() - 1]);
        tokens.put(p.getUniqueId(), new int[]{-1, -1, -1, -1});
        return true;
    }

    public boolean start() {
        if (players.size() < 2) return false;
        started = true;
        currentPlayerIndex = 0;
        renderInventory();
        for (Player p : players) p.openInventory(inventory);
        broadcastBoard();
        Bukkit.broadcastMessage(ChatColor.GOLD + "Ludo shuru ho gaya! Pehli turn: " + ChatColor.AQUA + players.get(0).getName());
        return true;
    }

    public Player getCurrentPlayer() { return players.get(currentPlayerIndex); }

    public void rollDice(Player p) {
        if (!p.equals(getCurrentPlayer())) {
            p.sendMessage(ChatColor.RED + "Ye aapki turn nahi hai!");
            return;
        }
        if (rolledThisTurn) {
            p.sendMessage(ChatColor.RED + "Aap already roll kar chuke hain. Ab token move karein.");
            return;
        }
        lastRoll = new Random().nextInt(6) + 1;
        rolledThisTurn = true;
        Bukkit.broadcastMessage(ChatColor.YELLOW + p.getName() + " ne dice roll kiya: " + ChatColor.GOLD + lastRoll);

        if (!hasValidMove(p)) {
            p.sendMessage(ChatColor.GRAY + "Koi valid move nahi hai, turn skip ho raha hai.");
            endTurn();
            return;
        }
        renderInventory();
    }

    private boolean hasValidMove(Player p) {
        for (int pos : tokens.get(p.getUniqueId())) {
            if (canMove(pos)) return true;
        }
        return false;
    }

    private boolean canMove(int pos) {
        if (pos == FINISH) return false;
        if (pos == -1) return lastRoll == 6;
        return pos + lastRoll <= FINISH;
    }

    public void moveToken(Player p, int tokenIndex) {
        if (!p.equals(getCurrentPlayer())) {
            p.sendMessage(ChatColor.RED + "Ye aapki turn nahi hai!");
            return;
        }
        if (!rolledThisTurn) {
            p.sendMessage(ChatColor.RED + "Pehle dice roll karein!");
            return;
        }
        if (tokenIndex < 0 || tokenIndex > 3) return;

        int[] t = tokens.get(p.getUniqueId());
        int pos = t[tokenIndex];
        if (!canMove(pos)) {
            p.sendMessage(ChatColor.RED + "Ye token move nahi ho sakta.");
            return;
        }

        int newPos = (pos == -1) ? 0 : pos + lastRoll;
        t[tokenIndex] = newPos;
        checkCapture(p, newPos);
        broadcastBoard();

        if (allFinished(p)) {
            Bukkit.broadcastMessage(ChatColor.GOLD + "" + ChatColor.BOLD + p.getName() + " Ludo jeet gaye! \uD83C\uDFC6");
            for (Player pl : players) pl.closeInventory();
            started = false;
            return;
        }

        if (lastRoll == 6 && ++consecutiveSixes < 3) {
            rolledThisTurn = false;
            lastRoll = -1;
            renderInventory();
            p.sendMessage(ChatColor.AQUA + "6 laane par extra turn mila!");
        } else {
            endTurn();
        }
    }

    private void checkCapture(Player p, int newPos) {
        if (newPos >= LOOP_LENGTH) return; // in home stretch, can't be captured
        int actualSquare = (getEntryOffset(p) + newPos) % LOOP_LENGTH;
        boolean safe = (actualSquare % 10 == 0); // each player's entry square is safe
        if (safe) return;

        for (Player other : players) {
            if (other.equals(p)) continue;
            int[] ot = tokens.get(other.getUniqueId());
            for (int i = 0; i < 4; i++) {
                if (ot[i] >= 0 && ot[i] < LOOP_LENGTH) {
                    int otherSquare = (getEntryOffset(other) + ot[i]) % LOOP_LENGTH;
                    if (otherSquare == actualSquare) {
                        ot[i] = -1;
                        other.sendMessage(ChatColor.RED + p.getName() + " ne aapka token capture kar liya!");
                        p.sendMessage(ChatColor.GREEN + "Aapne " + other.getName() + " ka token capture kar liya!");
                    }
                }
            }
        }
    }

    private boolean allFinished(Player p) {
        for (int pos : tokens.get(p.getUniqueId())) if (pos != FINISH) return false;
        return true;
    }

    private int getEntryOffset(Player p) {
        return colorOf.get(p.getUniqueId()).ordinal() * 10;
    }

    private void endTurn() {
        consecutiveSixes = 0;
        rolledThisTurn = false;
        lastRoll = -1;
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        renderInventory();
        Bukkit.broadcastMessage(ChatColor.AQUA + "Ab turn hai: " + getCurrentPlayer().getName());
    }

    private void broadcastBoard() {
        StringBuilder sb = new StringBuilder(ChatColor.GOLD + "--- Ludo Board ---\n");
        for (Player p : players) {
            Color c = colorOf.get(p.getUniqueId());
            sb.append(colorCode(c)).append(c.name()).append(" (").append(p.getName()).append("): ");
            for (int pos : tokens.get(p.getUniqueId())) sb.append(describe(pos)).append(" ");
            sb.append("\n");
        }
        for (Player p : players) p.sendMessage(sb.toString());
    }

    private String describe(int pos) {
        if (pos == -1) return "[Home]";
        if (pos == FINISH) return "[Finished]";
        if (pos >= LOOP_LENGTH) return "[Stretch-" + (pos - LOOP_LENGTH + 1) + "]";
        return "[Sq" + pos + "]";
    }

    private ChatColor colorCode(Color c) {
        switch (c) {
            case RED: return ChatColor.RED;
            case GREEN: return ChatColor.GREEN;
            case YELLOW: return ChatColor.YELLOW;
            default: return ChatColor.BLUE;
        }
    }

    public void renderInventory() {
        inventory.clear();
        ItemStack dice = new ItemStack(Material.BONE);
        ItemMeta dm = dice.getItemMeta();
        dm.setDisplayName(ChatColor.YELLOW + "Roll Dice" + (lastRoll > 0 ? " (Last: " + lastRoll + ")" : ""));
        dice.setItemMeta(dm);
        inventory.setItem(0, dice);

        Player current = getCurrentPlayer();
        int[] t = tokens.get(current.getUniqueId());
        Material mat = woolFor(colorOf.get(current.getUniqueId()));
        for (int i = 0; i < 4; i++) {
            ItemStack item = new ItemStack(mat);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.WHITE + "Token " + (i + 1) + " " + describe(t[i]));
            item.setItemMeta(meta);
            inventory.setItem(2 + i, item);
        }
    }

    private Material woolFor(Color c) {
        switch (c) {
            case RED: return Material.RED_WOOL;
            case GREEN: return Material.GREEN_WOOL;
            case YELLOW: return Material.YELLOW_WOOL;
            default: return Material.BLUE_WOOL;
        }
    }
}
