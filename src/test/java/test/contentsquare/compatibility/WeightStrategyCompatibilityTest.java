package test.contentsquare.compatibility;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import test.contentsquare.domain.Movie;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WeightStrategyCompatibilityTest {

    @Test
    void FieldComputeScore_test() {
        {
            WeightStrategyCompatibility.FieldComputeScore f = new WeightStrategyCompatibility.FieldComputeScore(1, new HashSet<>(), new HashSet<>(List.of("a")));
            Assertions.assertEquals(0, f.getIntersectionSize());
            Assertions.assertEquals(1, f.getUnionSize());
        }
        {
            WeightStrategyCompatibility.FieldComputeScore f = new WeightStrategyCompatibility.FieldComputeScore(1, new HashSet<>(List.of("a")), new HashSet<>());
            Assertions.assertEquals(0, f.getIntersectionSize());
            Assertions.assertEquals(1, f.getUnionSize());
        }
        {
            WeightStrategyCompatibility.FieldComputeScore f = new WeightStrategyCompatibility.FieldComputeScore(1, new HashSet<>(List.of("a")), new HashSet<>(List.of("a", "b")));
            Assertions.assertEquals(1, f.getIntersectionSize());
            Assertions.assertEquals(2, f.getUnionSize());
        }
        {
            WeightStrategyCompatibility.FieldComputeScore f = new WeightStrategyCompatibility.FieldComputeScore(1, new HashSet<>(List.of("a", "b")), new HashSet<>(List.of("a", "b")));
            Assertions.assertEquals(2, f.getIntersectionSize());
            Assertions.assertEquals(2, f.getUnionSize());
        }
    }

    @Test
    void score() {
        WeightStrategyCompatibility strategyCompatibility =  new WeightStrategyCompatibility();

        // Cannot compare genre, but directors and actors are identical
        {
            Movie m1 = new Movie();
            m1.getDirectors().add("d1");
            m1.getActors().add("a1");

            Movie m2 = new Movie();
            m2.getGenre().add("g1");
            m2.getDirectors().add("d1");
            m2.getActors().add("a1");
            Assertions.assertEquals(0.8333333333333334, strategyCompatibility.score(m1, m2));
        }

        // No actors can be compared and there is only one director in common
        {
            Movie m1 = new Movie();
            m1.getGenre().add("g1");
            m1.getDirectors().add("d1");

            Movie m2 = new Movie();
            m2.getGenre().add("g1");
            m2.getDirectors().add("d1");

            Assertions.assertEquals(0, strategyCompatibility.score(m1, m2));
        }

        // Cannot compare genre nor directors but 2 actors are identical
        {
            Movie m1 = new Movie();
            m1.getActors().add("a1");
            m1.getActors().add("a2");

            Movie m2 = new Movie();
            m2.getActors().add("a1");
            m2.getActors().add("a2");
            Assertions.assertEquals(0.6666666666666666, strategyCompatibility.score(m1, m2));
        }

        // Cannot compare genre nor directors and only 1 actors is identical
        {
            Movie m1 = new Movie();
            m1.getActors().add("a1");

            Movie m2 = new Movie();
            m2.getActors().add("a1");
            Assertions.assertEquals(0, strategyCompatibility.score(m1, m2));
        }

        // Only genre to compare to
        {
            Movie m1 = new Movie();
            m1.getGenre().addAll(Arrays.asList("g1", "g2", "g3", "g4"));

            Movie m2 = new Movie();
            m2.getGenre().addAll(Arrays.asList("g1", "g2", "g3", "g4"));
            Assertions.assertEquals(0, strategyCompatibility.score(m1, m2));
        }

        // No common actors and not enougth directors in common
        {
            Movie m1 = new Movie();
            m1.getGenre().addAll(Arrays.asList("g1", "g2", "g3", "g4"));
            m1.getDirectors().addAll(Arrays.asList("d1", "d2", "d3", "d4"));
            m1.getActors().addAll(Arrays.asList("a1", "a2", "a3", "a4"));

            Movie m2 = new Movie();
            m2.getGenre().addAll(Arrays.asList("g1", "g2", "g3", "g4"));
            m2.getDirectors().addAll(Arrays.asList("d1", "d22", "d33", "d44"));
            m2.getActors().addAll(Arrays.asList("a11", "a22", "a33", "a44"));
            Assertions.assertEquals(0, strategyCompatibility.score(m1, m2));
        }

    }
}