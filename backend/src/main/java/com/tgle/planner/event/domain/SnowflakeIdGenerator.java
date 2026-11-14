package com.tgle.planner.event.domain;

import org.springframework.stereotype.Component;

import java.time.Clock;

public class SnowflakeIdGenerator {

    private static final long EPOCH = 1735689600000L;
    private static final long NODE_ID_BITS = 10L;
    private static final long SEQUENCE_BITS = 12L;
    private static final long MAX_NODE_ID = ~(-1L << NODE_ID_BITS);
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);
    private static final long NODE_ID_SHIFT = SEQUENCE_BITS;
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + NODE_ID_BITS;

    private final long nodeId;
    private final Clock clock;

    private long lastTimestamp = -1L;
    private long sequence = 0L;

    public SnowflakeIdGenerator(long nodeId, Clock clock) {
        if (nodeId < 0 || nodeId > MAX_NODE_ID) {
            throw new IllegalArgumentException("Node id must be between 0 and " + MAX_NODE_ID);
        }
        this.nodeId = nodeId;
        this.clock = clock;
    }

    public long nextId() {
        return generate();
    }

    private synchronized long generate() {
        SnowflakeState state = nextState(clock.millis(), lastTimestamp, sequence);
        lastTimestamp = state.timestamp();
        sequence = state.sequence();

        return ((lastTimestamp - EPOCH) << TIMESTAMP_SHIFT)
                | (nodeId << NODE_ID_SHIFT)
                | sequence;
    }

    private SnowflakeState nextState(long timestamp, long lastTimestamp, long lastSequence) {
        if (timestamp < lastTimestamp) {
            throw new IllegalStateException("Clock moved backwards. Refusing to generate id for "
                    + (lastTimestamp - timestamp) + "ms");
        }

        if (timestamp != lastTimestamp) {
            return new SnowflakeState(timestamp, 0L);
        }

        long nextSequence = (lastSequence + 1) & MAX_SEQUENCE;

        if (nextSequence == 0) {
            return new SnowflakeState(waitNextMillis(timestamp), nextSequence);
        }

        return new SnowflakeState(timestamp, nextSequence);
    }

    private long waitNextMillis(long lastTimestamp) {
        while (true) {
            long timestamp = clock.millis();
            if (timestamp > lastTimestamp) {
                return timestamp;
            }
            Thread.onSpinWait();
        }
    }

    private record SnowflakeState(long timestamp, long sequence) {}
}
