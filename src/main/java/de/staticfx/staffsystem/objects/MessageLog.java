package de.staticfx.staffsystem.objects;

import java.util.List;

public class MessageLog {

    private List<Long> times;
    private List<String> messages;


    public MessageLog(List<Long> times, List<String> messages) {
        this.times = times;
        this.messages = messages;
    }

    public List<Long> getTimes() {
        return times;
    }

    public void setTimes(List<Long> times) {
        this.times = times;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }
}
