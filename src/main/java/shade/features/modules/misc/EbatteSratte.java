package shade.features.modules.misc;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;
import shade.core.manager.client.ConfigManager;
import shade.events.impl.EventAttack;
import shade.features.modules.Module;
import shade.setting.Setting;
import shade.utility.Timer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class EbatteSratte extends Module {
    private final Setting<Integer> delay = new Setting<>("Delay", 5, 1, 30);
    private final Setting<Server> server = new Setting<>("Server", Server.FunnyGame);
    private final Setting<Messages> mode = new Setting<>("Mode", Messages.Default);

    private static final String[] WORDS = new String[]{
            "PL TBOPL MATPL PLTPL PLOPLEPL PL PL PL EPLAHAPL PLHA",
            "PL PL PL PL PL PL PL PL",
            "PL PL PL PL PL PL PL PL PL",
            "PL PL PL PL PL",
            "PL PL PL PL PL PL PL",
            "PL PL PL PL PL xPLpPL PL PL PL PL PL PL PL PL PL?",
            "PL PLHPL PLAHAPL , PL PL MATPL PLBECPL PL PL PL PL PL PL PL PL PLHAPL PL PL PL 180PL PL PL PL PL PL PL HAXPL PL PLATPL PL PL PL",
            "PL PL PL PL PL PL PL PL, PL PL PL PL",
            "PL PL PL PL PL.PL. PL PL PLATPL PL PL PL 99 PL PL PL PL PL PL PL PL PL9TPL PL PL PL PL PL PL PL HAXPL PLOPLAPL EPLAHPL PL PL PL PL PLATPL PLOPL PLOPL PL PL EPLAPLAPL PL PL PL 2 PL",
            "PL PL PL PL PL PL PL PL PL XPL PL PL, PL PL PL PLAKOPL PL PL PL?)",
            "PL PL PL PLBPL PLATPL PL PL PL PL PL PL PL PL EPLHPL PL PL XPL PL PL9PL PL?))",
            "PL PL PL PLUPL PL PL PL PL PL PL PL PL PL PL 13 PL PL PL PL PL PL PL PL PL PL PL PL PLOPLAPL PLAHPL PLA PL PLATEPHAPL PLETPL.",
            "PL jako PL, PL PL PL PL",
            "Your mom owned by Exohack Recode",
            "PL PL PL PL PL PL PL PL PL PL PL PL PL PL PL PL PL PL PL PL PL PL PL PL PL PL PL",
            "1",
            "PL 1 PL 1 PL PL PL",
            "PL - PL PL, PL - PL PL"
    };

    private final Timer timer = new Timer();
    private ArrayList<String> words = new ArrayList<>();

    public EbatteSratte() {
        super("EbatteSratte", Module.Category.MISC);
        loadEZ();
    }

    @Override
    public void onEnable() {
        loadEZ();
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onAttackEntity(@NotNull EventAttack event) {
        if (event.getEntity() instanceof PlayerEntity && !event.isPre()) {
            if (timer.passedS(delay.getValue())) {
                PlayerEntity entity = (PlayerEntity) event.getEntity();
                if (entity == null) return;

                int n;

                if (mode.getValue() == Messages.Default) n = (int) Math.floor(Math.random() * WORDS.length);
                else n = (int) Math.floor(Math.random() * words.size());

                String chatPrefix = switch (server.getValue()) {
                    case FunnyGame -> "!";
                    case OldServer -> ">";
                    case DirectMessage -> "/msg ";
                    case Local -> "";
                };

                if (chatPrefix.contains("/"))
                    mc.getNetworkHandler().sendChatCommand("/msg " + entity.getName().getString() + " " + (mode.getValue() == Messages.Default ? WORDS[n] : words.get(n)));
                else
                    mc.getNetworkHandler().sendChatMessage(chatPrefix + entity.getName().getString() + " " + (mode.getValue() == Messages.Default ? WORDS[n] : words.get(n)));


                timer.reset();
            }
        }
    }

    public void loadEZ() {
        try {
            File file = new File(ConfigManager.CONFIG_FOLDER_NAME + "/misc/EbatteSratte.txt");
            if (!file.exists() && !file.createNewFile())
                sendMessage("Error with creating file");

            new Thread(() -> {
                try {
                    FileInputStream fis = new FileInputStream(file);
                    InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
                    BufferedReader reader = new BufferedReader(isr);
                    ArrayList<String> lines = new ArrayList<>();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        lines.add(line);
                    }
                    boolean newline = false;
                    for (String l : lines) {
                        if (l.isEmpty()) {
                            newline = true;
                            break;
                        }
                    }
                    words.clear();
                    ArrayList<String> spamList = new ArrayList<>();
                    if (newline) {
                        StringBuilder spamChunk = new StringBuilder();
                        for (String l : lines) {
                            if (l.isEmpty()) {
                                if (!spamChunk.isEmpty()) {
                                    spamList.add(spamChunk.toString());
                                    spamChunk = new StringBuilder();
                                }
                            } else spamChunk.append(l).append(" ");
                        }
                        spamList.add(spamChunk.toString());
                    } else spamList.addAll(lines);

                    words = spamList;
                } catch (Exception ignored) {
                }
            }).start();
        } catch (IOException ignored) {
        }
    }

    public enum Server {
        FunnyGame,
        DirectMessage,
        OldServer,
        Local
    }

    public enum Messages {
        Default,
        Custom
    }
}
