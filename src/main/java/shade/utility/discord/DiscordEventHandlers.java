package shade.utility.discord;

import java.util.Arrays;
import java.util.List;
import shade.utility.discord.callbacks.JoinGameCallback;
import shade.utility.discord.callbacks.ErroredCallback;
import shade.utility.discord.callbacks.ReadyCallback;
import shade.utility.discord.callbacks.SpectateGameCallback;
import shade.utility.discord.callbacks.JoinRequestCallback;
import shade.utility.discord.callbacks.DisconnectedCallback;
import com.sun.jna.Structure;

public class DiscordEventHandlers extends Structure {
    public DisconnectedCallback disconnected;
    public JoinRequestCallback joinRequest;
    public SpectateGameCallback spectateGame;
    public ReadyCallback ready;
    public ErroredCallback errored;
    public JoinGameCallback joinGame;
    
    protected List<String> getFieldOrder() {
        return Arrays.asList("ready", "disconnected", "errored", "joinGame", "spectateGame", "joinRequest");
    }
}
