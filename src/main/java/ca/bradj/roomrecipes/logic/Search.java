package ca.bradj.roomrecipes.logic;

import ca.bradj.roomrecipes.core.Room;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

public class Search<T> {
    private final @Nullable T room;
    private final boolean end;

    private Search(
            T room,
            boolean end
    ) {
        this.room = room;
        this.end = end;
    }

    public static Search<Room> end(Room room) {
        return new Search<>(room, true);
    }

    public static <X> Search<X> from(Optional<X> room) {
        return new Search<>(room.orElse(null), false);
    }

    public static <X> Search<X> empty() {
        return new Search<>(null, false);
    }

    public static <X> Search<X> of(X room) {
        return new Search<>(room, false);
    }

    public boolean isPresent() {
        return room != null;
    }

    public boolean isEmpty() {
        return room == null;
    }

    public T get() {
        return room;
    }

    public <U> Search<U> map(Function<? super T, ? extends U> mapper) {
        U u = Optional.ofNullable(room).map(mapper).orElse(null);
        return new Search<>(u, end);
    }

    public Optional<T> toOptional() {
        return Optional.ofNullable(room);
    }

    public boolean isEnd() {
        return end;
    }
}
