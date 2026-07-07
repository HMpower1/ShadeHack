package shade.features.modules.misc;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import shade.core.Managers;
import shade.core.manager.client.ModuleManager;
import shade.features.modules.Module;
import shade.setting.Setting;
import shade.setting.impl.SettingGroup;
import shade.utility.Timer;
import shade.utility.player.InventoryUtility;

import static shade.features.modules.client.ClientSettings.isPl;

public class AutoLeave extends Module {
    public AutoLeave() {
        super("AutoLeave", Category.MISC);
    }

    private final Setting<Boolean> antiHelperLeave = new Setting<>("AntiHelperLeave", true);
    private final Setting<Boolean> antiKTLeave = new Setting<>("AntiKTLeave", true);
    private final Setting<Boolean> autoDisable = new Setting<>("AutoDisable", true);
    private final Setting<Boolean> fastLeave = new Setting<>("InstantLeave", true);
    public static Setting<String> command = new Setting<>("Command", "hub");
    private final Setting<SettingGroup> leaveIf = new Setting<>("Leave if", new SettingGroup(false, 0));
    private final Setting<Boolean> low_hp = new Setting<>("LowHp", false).addToGroup(leaveIf);
    private final Setting<Boolean> totems = new Setting<>("Totems", false).addToGroup(leaveIf);
    private final Setting<Integer> totemsCount = new Setting<>("TotemsCount", 2, 0, 10, v -> totems.getValue());
    private final Setting<Float> leaveHp = new Setting<>("HP", 8.0f, 1f, 20.0f, v -> low_hp.getValue());
    private final Setting<LeaveMode> staff = new Setting<>("Staff", LeaveMode.None).addToGroup(leaveIf);
    private final Setting<LeaveMode> players = new Setting<>("Players", LeaveMode.Leave).addToGroup(leaveIf);
    private final Setting<Integer> distance = new Setting<>("Distance", 256, 4, 256, v -> players.getValue() != LeaveMode.None).addToGroup(leaveIf);

    private final Timer chatDelay = new Timer();

    // komentarz przetlumaczony
    private final Timer hurtTimer = new Timer();

    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null)
            return;

        if (mc.player.hurtTime > 0)
            hurtTimer.reset();

        if (antiKTLeave.getValue() && !hurtTimer.passedMs(30000))
            return;

        for (PlayerEntity pl : mc.world.getPlayers()) {
            if (pl.getScoreboardTeam() != null && antiHelperLeave.getValue()) {
                String prefix = pl.getScoreboardTeam().getPrefix().getString();
                if (isStaff(Formatting.strip(prefix)))
                    continue;
            }


            if (pl != mc.player && !Managers.FRIEND.isFriend(pl) && players.getValue() != LeaveMode.None && mc.player.squaredDistanceTo(pl.getPos()) <= distance.getPow2Value()) {
                switch (players.getValue()) {
                    case Command -> {
                        if (autoDisable.getValue()) disable();
                        sendMessage(isPl() ? "PL PL.PL. PL PL PL!" : "Logged out because there was a player!");
                        mc.player.networkHandler.sendChatCommand(command.getValue());
                        return;
                    }
                    case Leave -> {
                        leave(isPl() ? "PL PL.PL. PL PL PL" : "Logged out because there was a player");
                        return;
                    }
                }
            }
        }

        if (totems.getValue() && InventoryUtility.getItemCount(Items.TOTEM_OF_UNDYING) <= totemsCount.getValue())
            leave(isPl() ? "PL PL.PL. PL PL" : "Logged out because out of totems");

        if (mc.player.getHealth() < leaveHp.getValue() && low_hp.getValue())
            leave(isPl() ? "PL PL.PL. PL PL" : "Logged out because ur hp is low");

        if (staff.getValue() != LeaveMode.None && ModuleManager.staffBoard.isDisabled() && mc.player.age % 5 == 0)
            sendMessage(isPl() ? "PL StaffBoard!" : "Turn on StaffBoard!");
    }

    private void leave(String message) {
        if (!chatDelay.passedMs(1000))
            return;
        chatDelay.reset();

        if (autoDisable.getValue())
            disable(message);

        if (fastLeave.getValue()) sendPacket(new UpdateSelectedSlotC2SPacket(228));
        else mc.player.networkHandler.getConnection().disconnect(Text.of("[AutoLeave] " + message));
    }

    /*public void onStaff() { todo
        if (!chatDelay.passedMs(1000))
            return;
        chatDelay.reset();

        if (hurtTimer.passedMs(30000) || !antiKTLeave.getValue()) {
            switch (staff.getValue()) {
                case Command -> {
                    sendMessage(isPl() ? "PL PL.PL. PL PL specPL!" : "Logged out because helper in vanish!");
                    mc.player.networkHandler.sendChatCommand(command.getValue());
                    if (autoDisable.getValue())
                        disable();
                }
                case Leave -> leave(isPl() ? "PL PL.PL. PL PL specPL!" : "Logged out because helper in vanish!");
            }
        }
    }*/

    public static boolean isStaff(String name) {
        if (name == null) return false;

        name = name.toLowerCase();

        return name.contains("helper") || name.contains("moder") || name.contains("admin") || name.contains("owner") || name.contains("curator") || name.contains("PL") || name.contains("PL") || name.contains("PL") || name.contains("PL") || name.contains("PL");
    }

    private enum LeaveMode {
        None, Command, Leave
    }
}
