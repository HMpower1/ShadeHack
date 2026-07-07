package shade.injection;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shade.Shade;
import shade.gui.misc.DialogScreen;
import shade.gui.mainmenu.MainMenuScreen;
import shade.utility.render.TextureStorage;
import shade.core.manager.client.ModuleManager;
import shade.features.modules.client.ClientSettings;

import java.net.URI;

import static shade.features.modules.Module.mc;
import static shade.features.modules.client.ClientSettings.isPl;

@Mixin(TitleScreen.class)
public class MixinTitleScreen extends Screen {
    protected MixinTitleScreen(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    public void postInitHook(CallbackInfo ci) {
        if (ModuleManager.clickGui.getBind().getKey() == -1) {
            ModuleManager.clickGui.setBind(InputUtil.fromTranslationKey("key.keyboard.p").getCode(), false, false);
        }

        if (Shade.isOutdated && !FabricLoader.getInstance().isDevelopmentEnvironment()) {
            mc.setScreen(new ConfirmScreen(
                    confirm -> {
                        if (confirm) Util.getOperatingSystem().open(URI.create("https://github.com/Pan4ur/Shade-Recode/releases/download/latest/shade-1.7.jar/"));
                        else mc.stop();
                    },
                    Text.of(Formatting.RED + "You are using an outdated version of Shade Recode"), Text.of("Please update to the latest release"), Text.of("Download"), Text.of("Quit Game")));
        }
    }
}
