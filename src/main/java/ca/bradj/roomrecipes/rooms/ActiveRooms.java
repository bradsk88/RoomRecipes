package ca.bradj.roomrecipes.rooms;

import ca.bradj.roomrecipes.core.Room;
import ca.bradj.roomrecipes.core.space.Position;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ActiveRooms<ROOM extends Room> {

    private final Map<Position, ROOM> rooms = new HashMap<>();

    // TODO: Support multiple?
    private ChangeListener<ROOM> changeListener = new ChangeListener<>() {
        @Override
        public void roomAdded(
                Position doorPos,
                ROOM room
        ) {
            // Do nothing by default
        }

        @Override
        public void roomResized(
                Position doorPos,
                ROOM oldRoom,
                ROOM newRoom
        ) {
            // Do nothing by default
        }

        @Override
        public void roomDestroyed(
                Position doorPos,
                Room room
        ) {
            // Do nothing by default
        }
    };

    public Collection<ROOM> getAll() {
        return rooms.values();
    }

    public interface ChangeListener<ROOM extends Room> {
        void roomAdded(
                Position doorPos,
                ROOM room
        );

        void roomResized(
                Position doorPos,
                ROOM oldRoom,
                ROOM newRoom
        );

        void roomDestroyed(
                Position doorPos,
                ROOM room
        );
    }

    public void addChangeListener(ChangeListener<ROOM> cl) {
        this.changeListener = cl;
    }

    public void update(Map<Position, Optional<ROOM>> rooms) {
        Map<Position, Optional<ROOM>> asOptional = this.rooms.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> Optional.of(e.getValue())));
        MapDifference<Position, Optional<ROOM>> diff = Maps.difference(asOptional, rooms);
        if (diff.areEqual()) {
            return;
        }

        Map<Position, Optional<ROOM>> oldEntries = diff.entriesOnlyOnLeft();
        for (Map.Entry<Position, Optional<ROOM>> removed : oldEntries.entrySet()) {
            if (removed.getValue().isEmpty()) {
                throw new IllegalStateException("Removing non-existent room");
            }
            this.changeListener.roomDestroyed(removed.getKey(), removed.getValue().get());
        }
        Map<Position, Optional<ROOM>> newEntries = diff.entriesOnlyOnRight();
        for (Map.Entry<Position, Optional<ROOM>> added : newEntries.entrySet()) {
            if (added.getValue().isEmpty()) {
                continue;
            }
            this.changeListener.roomAdded(added.getKey(), added.getValue().get());
        }
        Map<Position, MapDifference.ValueDifference<Optional<ROOM>>> changedEntries = diff.entriesDiffering();
        for (Map.Entry<Position, MapDifference.ValueDifference<Optional<ROOM>>> e : changedEntries.entrySet()) {
            MapDifference.ValueDifference<Optional<ROOM>> v = e.getValue();
            if (v.leftValue().isPresent() && v.rightValue().isEmpty()) {
                this.changeListener.roomDestroyed(e.getKey(), v.leftValue().get());
                continue;
            }
            if (v.leftValue().isEmpty() && v.rightValue().isPresent()) {
                this.changeListener.roomAdded(e.getKey(), v.rightValue().get());
                continue;
            }
            if (v.leftValue().isPresent() && v.rightValue().isPresent()) {
                this.changeListener.roomResized(e.getKey(), v.leftValue().get(), v.rightValue().get());
            }
        }
        this.rooms.clear();
        rooms.entrySet().stream().filter(
                v -> v.getValue().isPresent()
        ).forEach(
                v -> this.rooms.put(v.getKey(), v.getValue().get())
        );
    }
}
