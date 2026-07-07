package shade.features.modules.player;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import shade.core.Managers;
import shade.features.modules.Module;
import shade.gui.notification.Notification;
import shade.setting.Setting;
import shade.utility.player.InventoryUtility;
import shade.utility.player.SearchInvResult;

import static shade.features.modules.client.ClientSettings.isPl;

public class ElytraReplace extends Module {
    public ElytraReplace() {
        super("ElytraReplace", Category.PLAYER);
    }

    private final Setting<Integer> durability = new Setting<>("Durability", 5, 0, 100);

    @Override
    public void onUpdate() {
        ItemStack is = mc.player.getEquippedStack(EquipmentSlot.CHEST);
        if(is.isOf(Items.ELYTRA) && ((100f - ((float) is.getDamage() / (float) is.getMaxDamage()) * 100f) <= durability.getValue())){

            SearchInvResult result = InventoryUtility.findInInventory(stack -> {
                if (stack.getItem() instanceof ElytraItem)
                    return (100f - ((float) stack.getDamage() / (float) stack.getMaxDamage()) * 100f) > durability.getValue();
                return false;
            });

            if (result.found()) {
                clickSlot(result.slot());
                clickSlot(6);
                clickSlot(result.slot());
                sendPacket(new CloseHandledScreenC2SPacket(mc.player.currentScreenHandler.syncId));
                Managers.NOTIFICATION.publicity("ElytraReplace", isPl() ? "Wymieniam elytre na nowa!" : "Swapping the old elytra for a new one!",2, Notification.Type.SUCCESS);
                sendMessage(isPl() ? "Wymieniam elytre na nowa!" : "Swapping the old elytra for a new one!");
            }
        }
    }
}
