package com.tekion.demo.lead;

import lombok.*;

import java.util.Set;

public enum LeadState {

    NEW("New"),
    CONTACTED("Contacted"),
    QUALIFIED("Qualified"),
    CONVERTED("Converted"),
    LOST("Lost");

    private final String displayName;

    public boolean isTerminal() {
        return this == CONVERTED || this == LOST;
    }

    public boolean canTransitionTo(LeadState target) {
        return switch (this) {
            case NEW -> Set.of(CONTACTED, LOST).contains(target);
            case CONTACTED -> Set.of(QUALIFIED, LOST).contains(target);
            case QUALIFIED -> Set.of(CONVERTED, LOST).contains(target);
            case CONVERTED, LOST -> false;
        };
    }

    LeadState(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

