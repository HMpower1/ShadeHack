package shade.events.impl;

import shade.events.Event;

public class PostPlayerUpdateEvent extends Event {
    private int iterations;

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int in) {
        iterations = in;
    }
}
