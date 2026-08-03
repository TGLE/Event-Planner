package com.tgle.planner.mailpit;

import java.util.List;

public record MailpitSummaryResponse(int total, List<MailpitMessage> messages) {
    public record MailpitMessage(String ID, List<MailpitAddress> To, String Subject) {}
    public record MailpitAddress(String Name, String Address) {}
}