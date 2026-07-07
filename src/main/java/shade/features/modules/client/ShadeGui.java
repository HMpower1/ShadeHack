package shade.features.modules.client;

import shade.gui.shadegui.ShadeGuiScreen;
import shade.features.modules.Module;
import shade.setting.Setting;
import shade.setting.impl.ColorSetting;

import java.awt.*;

public final class ShadeGui extends Module {
    public static final Setting<ColorSetting> onColor1 = new Setting<>("OnColor1", new ColorSetting(new Color(71, 0, 117, 255).getRGB()));
    public static final Setting<ColorSetting> onColor2 = new Setting<>("OnColor2", new ColorSetting(new Color(32, 1, 96, 255).getRGB()));
    public static final Setting<Float> scrollSpeed = new Setting<>("ScrollSpeed", 1f, 0.1F, 2.0F);

    public ShadeGui() {
        super("ShadeGui", Category.CLIENT);
    }

    @Override
    public void onEnable() {
        mc.setScreen(ShadeGuiScreen.getShadeGui());
        disable();
    }

    public static Color getColorByTheme(int id) {
        return switch (id) {
            case 0 -> new Color(37, 27, 41, 250); // glowny panel
            case 1 -> new Color(50, 35, 60, 250); // panel logo
            case 2 -> new Color(-1); // napis SHADE+ i biale ikony
            case 3, 8 -> new Color(0x656565); // wersja pod napisem
            case 4 -> new Color(50, 35, 60, 178); // panel kategorii, wybor trybu GUI wylaczony
            case 5 -> new Color(133, 93, 162, 178); // wybor trybu GUI wlaczony
            case 6 -> new Color(88, 64, 107, 178); // kolor separatora wyboru trybu
            case 7 -> new Color(25, 20, 30, 255); // kolor panelu ustawien
            case 9 -> new Color(50, 35, 60, 178);
            default -> new Color(37, 27, 41, 250); // panel kategorii
        };
    }
}
