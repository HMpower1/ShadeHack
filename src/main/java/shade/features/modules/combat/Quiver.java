package shade.features.modules.combat;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.BowItem;
import net.minecraft.item.Items;
import net.minecraft.item.TippedArrowItem;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import shade.events.impl.EventSync;
import shade.features.modules.Module;
import shade.setting.Setting;
import shade.utility.player.InventoryUtility;
import shade.utility.player.SearchInvResult;
import shade.utility.world.HoleUtility;

import java.util.Objects;

import static shade.features.modules.client.ClientSettings.isPl;

public final class Quiver extends Module {
    private final Setting<Boolean> onlyInHole = new Setting<>("Only In Hole", false);

    private int preBowSlot;
    private int count;

    public Quiver() {
        super("Quiver", Category.COMBAT);
    }

    @Override
    public void onEnable() {
        count = 0;
        preBowSlot = mc.player.getInventory().selectedSlot;
    }

    @Override
    public void onDisable() {
        if (preBowSlot != -1)
            InventoryUtility.switchTo(preBowSlot);

        mc.options.useKey.setPressed(false);
    }

    @EventHandler
    @SuppressWarnings("unused")
    private void onSync(EventSync event) {
        if ((!HoleUtility.isHole(mc.player.getBlockPos()) && onlyInHole.getValue()) || (mc.player.isUsingItem() && !mc.player.getMainHandStack().getItem().equals(Items.BOW)))
            return;

        SearchInvResult strength = getArrow("strength");
        SearchInvResult swiftness = getArrow("swiftness");

        boolean hasStrength = !strength.found() || (mc.player.hasStatusEffect(StatusEffects.STRENGTH) && mc.player.getStatusEffect(StatusEffects.STRENGTH).getDuration() > 100);
        boolean hasSwiftness = !swiftness.found() || (mc.player.hasStatusEffect(StatusEffects.SPEED) && mc.player.getStatusEffect(StatusEffects.SPEED).getDuration() > 100);

        if (!strength.found() && !swiftness.found()) {
            disable(isPl() ? "Brak wymaganych strzal w ekwipunku! Wylaczam..." : "No arrows in hotbar! Disabling...");
            return;
        }

        if (hasSwiftness && hasStrength) {
            disable();
            return;
        }

        SearchInvResult result = InventoryUtility.findItemInHotBar(Items.BOW);
        if (!result.found()) {
            disable(isPl() ? "Brak luku na hotbarze! Wylaczam..." : "No bow in hotbar! Disabling...");
            return;
        }
        result.switchTo();

        if (BowItem.getPullProgress(mc.player.getItemUseTime()) >= 0.15) {
            releaseBow();
            switchInvSlot(strength.slot(), swiftness.slot());
            return;
        }

        if (count >= (strength.found()  && swiftness.found() ? 2 : 1)) {
            disable();
            return;
        }

        mc.options.useKey.setPressed(true);
    }

    private void releaseBow() {
        sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(mc.player.getYaw(), -90, mc.player.isOnGround()));
        mc.options.useKey.setPressed(false);
        mc.interactionManager.stopUsingItem(mc.player);
        count++;
    }

    private SearchInvResult getArrow(String name) {
        return InventoryUtility.findInInventory(stack -> {
            if (stack.getItem() instanceof TippedArrowItem tai) {
                String key = tai.getTranslationKey(stack);
                return key.contains("effect." + name);
            }
            return false;
        });
    }

    private void switchInvSlot(int from, int to) {
        if (from == -1 || to == -1)
            return;

        sendPacket(new ClientCommandC2SPacket(Objects.requireNonNull(mc.player), ClientCommandC2SPacket.Mode.STOP_SPRINTING));
        clickSlot(from);
        clickSlot(to);
        clickSlot(from);
        sendPacket(new CloseHandledScreenC2SPacket(mc.player.currentScreenHandler.syncId));
    }
}
