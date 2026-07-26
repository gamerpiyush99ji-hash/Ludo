# MiniGames Plugin (Spigot/Paper)

In-game Tic-Tac-Toe aur Ludo — dono ek hi plugin mein, chest-GUI ke through khela jaata hai.

## Build kaise karein

Requirement: Java 17+ aur Maven installed hona chahiye.

```
cd minigames-plugin
mvn clean package
```

Build hone ke baad `target/MiniGames.jar` milegi. Us jar ko apne server ke `plugins/` folder mein daal kar server restart karein.

(Note: is machine par internet nahi hai isliye maine yahin par jar build nahi ki hai — aapko apne PC/server par `mvn clean package` chalana hoga. Spigot 1.20.4 API use hui hai, isliye server bhi 1.20.x Paper/Spigot hona chahiye.)

## Tic-Tac-Toe

- `/ttt challenge <player>` — kisi player ko challenge bhejein
- `/ttt accept <player>` — challenge accept karein, GUI khul jayegi
- 3x3 grid mein glass pane par click karke apna mark (X/O) place karein
- Win/draw hone par 3 second baad GUI apne aap band ho jayegi

## Ludo (2-4 players)

- `/ludo join` — lobby mein join karein
- `/ludo start` — game shuru karein (min 2 players chahiye)
- GUI mein "Roll Dice" (bone item) par click karein
- Fir apna token (colored wool) par click karke move karein
- 6 laane par home se token nikal sakte hain, aur extra turn milta hai (max 3 baar lagatar)
- Har move ke baad sabko chat mein pura board status dikhega
- Jo player apne saare 4 tokens finish kara le, wahi jeeta

## Design notes (important)

Ye ek **simplified** Ludo hai — asli 15x15 wale board ka exact layout GUI mein represent karna practically possible nahi hai, isliye:
- Shared path 40 squares ka hai (har player 10 squares door se enter karta hai)
- Har player ke entry square "safe" hote hain (opponent capture nahi kar sakta)
- Baaki rules (dice, capturing, home stretch, extra turn on 6) classic Ludo jaise hi hain

Chahen to main isse aur customize/expand kar sakta hoon — jaise real board ko `/ludo` command se ek physical 2D board banwana map/schematic ke through, ya cricket/football/chess add karna.
