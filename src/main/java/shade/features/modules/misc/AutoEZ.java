package shade.features.modules.misc;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import shade.core.manager.client.ConfigManager;
import shade.events.impl.EventDeath;
import shade.events.impl.PacketEvent;
import shade.features.modules.Module;
import shade.features.modules.combat.Aura;
import shade.features.modules.combat.AutoCrystal;
import shade.setting.Setting;
import shade.utility.ExoUtility;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

import static shade.features.modules.client.ClientSettings.isPl;

public final class AutoEZ extends Module {
    public static ArrayList<String> EZWORDS = new ArrayList<>();
    public Setting<Boolean> global = new Setting<>("global", true);

    String[] EZ = new String[]{
            "%player% PL PL PL PL",
            "%player% PL PL PL PL))))",
            "%player% PL PL PL",
            "%player% PL PL PL PL PL PL?",
            "%player% PL",
            "%player% PL PL PL PL",
            "PL %player% PL PL",
            "%player% PL PL PL",
            "%player% PL PL PL PL PL PL",
            "%player% PL PL PL PL PL PL)))",
            "%player% PL PL PL)))"
    };

    private final Setting<ModeEn> mode = new Setting<>("Mode", ModeEn.Basic);
    private final Setting<ServerMode> server = new Setting<>("Server", ServerMode.Universal);

    public AutoEZ() {
        super("AutoEZ", Category.MISC);
        loadEZ();
    }

    public static void loadEZ() {
        try {
            File file = new File(ConfigManager.CONFIG_FOLDER_NAME + "/misc/AutoEZ.txt");
            if (!file.exists()) file.createNewFile();
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
                    EZWORDS.clear();
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

                    EZWORDS = spamList;
                } catch (Exception ignored) {
                }
            }).start();
        } catch (IOException ignored) {
        }
    }

    @Override
    public void onEnable() {
        loadEZ();
    }

    @EventHandler
    public void onPacketReceive(PacketEvent.Receive e) {
        if (fullNullCheck()) return;
        if (server.getValue() == ServerMode.Universal) return;
        if (e.getPacket() instanceof GameMessageS2CPacket) {
            final GameMessageS2CPacket packet = e.getPacket();
            if (packet.content().getString().contains("PL PL PL")) {
                String name = ExoUtility.solveName(packet.content().getString());
                if (Objects.equals(name, "FATAL ERROR")) return;

                String finalword;
                if (mode.getValue() == ModeEn.Basic) {
                    int n;
                    n = (int) Math.floor(Math.random() * EZ.length);
                    finalword = EZ[n].replace("%player%", name);
                } else {
                    if (EZWORDS.isEmpty()) {
                        sendMessage(isPl() ? "PL PL AutoEZ PL!" : "AutoEZ.txt is empty!");
                        return;
                    }
                    finalword = EZWORDS.get(new Random().nextInt(EZWORDS.size()));
                    finalword = finalword.replaceAll("%player%", name);
                }
                mc.player.networkHandler.sendChatMessage(global.getValue() ? "!" + finalword : finalword);
            }
        }
    }

    @EventHandler
    public void onDeath(EventDeath e) {
        if (server.getValue() != ServerMode.Universal) return;
        if (Aura.target != null && Aura.target == e.getPlayer()) {
            sayEZ(e.getPlayer().getName().getString());
            return;
        }
        if (AutoCrystal.target != null && AutoCrystal.target == e.getPlayer())
            sayEZ(e.getPlayer().getName().getString());
    }

    public void sayEZ(String pn) {
        String finalword;
        if (mode.getValue() == ModeEn.Basic) {
            int n;
            n = (int) Math.floor(Math.random() * EZ.length);
            finalword = EZ[n].replace("%player%", pn);
        } else {
            if (EZWORDS.isEmpty()) {
                sendMessage(isPl() ? "PL PL AutoEZ PL!" : "AutoEZ.txt is empty!");
                return;
            }
            finalword = EZWORDS.get(new Random().nextInt(EZWORDS.size()));
            finalword = finalword.replaceAll("%player%", pn);
        }
        mc.player.networkHandler.sendChatMessage(global.getValue() ? "!" + finalword : finalword);
    }

    public enum ModeEn {
        Custom,
        Basic
    }

    public enum ServerMode {
        Universal,
        FunnyGame
    }
}
