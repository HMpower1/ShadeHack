package shade.features.cmd.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import shade.features.cmd.Command;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static shade.features.modules.client.ClientSettings.isPl;

public class HorseSpeedCommand extends Command {
    public HorseSpeedCommand() {
        super("gethorsespeed", "horsespeed");
    }

    @Override
    public void executeBuild(@NotNull LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            if (mc.player.getVehicle() != null && mc.player.getVehicle() instanceof HorseEntity horse) {
                if (!horse.isSaddled()) {
                    if (isPl()) sendMessage(Formatting.RED + "Nie masz siodla!");
                    else sendMessage(Formatting.RED + "You don't have a saddle!");
                    return SINGLE_SUCCESS;
                }

                float speed = horse.forwardSpeed * 43.17f;

                float ratio = speed / 14.512f;

                String verbose = "";

                if (ratio < 0.3)
                    verbose = isPl() ? "Masz slaba konia :(" : "Your horse is shitty :(";

                if (ratio > 0.3 && ratio < 0.6)
                    verbose = isPl() ? "Masz normalnego konia" : "Your horse is normal";

                if (ratio > 0.6)
                    verbose = isPl() ? "Masz dobrego konia :)" : "Your horse is good :)";

                if (ratio > 0.9)
                    verbose = isPl() ? "Masz bardzo dobrego konia :)" : "Your horse is very good :)";

                if (isPl()) sendMessage(Formatting.GREEN + "Predkosc konia: " + speed + " z 14.512. " + verbose);
                else sendMessage(Formatting.GREEN + "Horse speed: " + speed + " out of 14.512. " + verbose);
            } else {
                if (isPl()) sendMessage(Formatting.RED + "Nie masz konia!");
                else sendMessage(Formatting.RED + "You don't have a horse!");
            }
            return SINGLE_SUCCESS;
        });
    }
}
