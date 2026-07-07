package shade.features.cmd.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.block.Block;
import net.minecraft.command.CommandSource;
import net.minecraft.registry.Registries;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import shade.core.manager.client.ModuleManager;
import shade.features.cmd.Command;
import shade.features.cmd.args.SearchArgumentType;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static shade.features.modules.client.ClientSettings.isPl;

public class NukerCommand extends Command {
    public NukerCommand() {
        super("nuker");
    }

    @Override
    public void executeBuild(@NotNull LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("reset").executes(context -> {
            ModuleManager.nuker.selectedBlocks.getValue().clear();
            sendMessage(isPl() ? "Wszystkie bloki zostaly usuniete!" : "Nuker got reset!");
            return SINGLE_SUCCESS;
        }));

        builder.then(literal("add").then(arg("block", SearchArgumentType.create()).executes(context -> {
            String blockName = context.getArgument("block", String.class);

            Block result = getRegisteredBlock(blockName);
            if (result != null) {
                ModuleManager.nuker.selectedBlocks.getValue().add(result);
                sendMessage(Formatting.GREEN + blockName + (isPl() ? " dodany do Nuker" : " added to Nuker"));
            } else {
                sendMessage(Formatting.RED + (isPl() ? "Nie ma takiego bloku!" : "There is no such block!"));
            }

            return SINGLE_SUCCESS;
        })));

        builder.then(literal("del").then(arg("block", SearchArgumentType.create()).executes(context -> {
            String blockName = context.getArgument("block", String.class);

            Block result = getRegisteredBlock(blockName);
            if (result != null) {
                ModuleManager.nuker.selectedBlocks.getValue().remove(blockName);
                sendMessage(Formatting.GREEN + blockName + (isPl() ? " usuniety z Nuker" : " removed from Nuker"));
            } else {
                sendMessage(Formatting.RED + (isPl() ? "Nie ma takiego bloku!" : "There is no such block!"));
            }

            return SINGLE_SUCCESS;
        })));

        builder.executes(context -> {
            if (ModuleManager.nuker.selectedBlocks.getValue().getItemsById().isEmpty()) {
                sendMessage("Nuker list empty");
            } else {
                StringBuilder f = new StringBuilder("Nuker list: ");

                for (String name : ModuleManager.nuker.selectedBlocks.getValue().getItemsById())
                    try {
                        f.append(name).append(", ");
                    } catch (Exception ignored) {
                    }

                sendMessage(f.toString());
            }

            return SINGLE_SUCCESS;
        });
    }

    public static Block getRegisteredBlock(String blockName) {
        for (Block block : Registries.BLOCK) {
            if (block.getTranslationKey().replace("block.minecraft.", "").equalsIgnoreCase(blockName.replace("block.minecraft.", ""))) {
                return block;
            }
        }
        return null;
    }
}
