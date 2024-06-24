/*
 * Copyright © Wynntils 2022-2024.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package com.wynntils.mc.event;

import com.wynntils.core.text.StyledText;
import net.minecraftforge.eventbus.api.Event;

public class ScoreboardSetScoreEvent extends Event {
    private final StyledText owner;
    private final String objectiveName;

    public ScoreboardSetScoreEvent(StyledText owner, String objectiveName) {
        this.owner = owner;
        this.objectiveName = objectiveName;
    }

    public StyledText getOwner() {
        return owner;
    }

    public String getObjectiveName() {
        return objectiveName;
    }

    public static class Set extends ScoreboardSetScoreEvent {
        private final int score;

        public Set(StyledText owner, String objectiveName, int score) {
            super(owner, objectiveName);
            this.score = score;
        }

        public int getScore() {
            return score;
        }
    }

    public static class Reset extends ScoreboardSetScoreEvent {
        public Reset(StyledText owner, String objectiveName) {
            super(owner, objectiveName);
        }
    }
}
