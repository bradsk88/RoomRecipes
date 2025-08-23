package ca.bradj.roomrecipes.testutil;

import ca.bradj.roomrecipes.core.space.Position;
import ca.bradj.roomrecipes.logic.LevelRoomDetector;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.function.Consumer;

public class Debugger {
    public static @Nullable Consumer<String> on(String date) {
        // Define the expected format of the input string
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            // Parse the input string into a LocalDate object
            LocalDate specificDate = LocalDate.parse(date, formatter);

            // Get today's date
            LocalDate today = LocalDate.now();

            // Compare the two dates
            if (today.isBefore(specificDate.plusDays(15))) {
                return System.out::println;
            }
        } catch (DateTimeParseException e) {
            // Handle cases where the input string is not in the correct format
            System.err.println("Invalid date format: " + date);
        }
        return (str) -> {
        };
    }

    public static @NotNull String getDebugArt(ImmutableSet<Position> positions) {
        String[][] art = new String[200][200];
        for (Position position : positions) {
            LevelRoomDetector.captureAsArtPixel(
                    position, new Position(0, 0), art, true, "W", " ", 5
            );
        }

        return LevelRoomDetector.doGetDebugArt(
                ImmutableMap.of(
                        new Position(0, 0),
                        art
                ), true
        ).get(new Position(0, 0));
    }
}
