package shade.features.modules.player;

import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import shade.Shade;
import shade.core.Managers;
import shade.core.manager.client.ModuleManager;
import shade.events.impl.EventTick;
import shade.features.modules.Module;

public class TpsSync extends Module {
    public TpsSync() {
        super("TpsSync", Module.Category.PLAYER);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onTick(EventTick e) {
        if (ModuleManager.timer.isEnabled()) return;
        if (Managers.SERVER.getTPS() > 1)
            Shade.TICK_TIMER = Managers.SERVER.getTPS() / 20f;
        else Shade.TICK_TIMER = 1f;
    }

    @Override
    public void onDisable() {
        Shade.TICK_TIMER = 1f;
    }
}
