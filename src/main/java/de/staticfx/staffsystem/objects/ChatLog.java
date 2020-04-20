package de.staticfx.staffsystem.objects;


import java.util.List;

public class ChatLog extends MessageLog {
    public ChatLog(List<Long> times, List<String> messages) {
        super(times, messages);
    }
}
