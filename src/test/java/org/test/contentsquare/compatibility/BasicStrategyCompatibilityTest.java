package org.test.contentsquare.compatibility;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.test.contentsquare.Movie;

import static org.junit.jupiter.api.Assertions.*;

class BasicStrategyCompatibilityTest {

    @Test
    void score() {
        BasicStrategyCompatibility basicStrategyCompatibility = new BasicStrategyCompatibility();
        Movie m1 = new Movie("id", 1910, 95);
        m1.getGenre().add("Doc");
        m1.getGenre().add("Sport");

        Movie m2 = new Movie("id", 1910, 95);
        m2.getGenre().add("Doc");
        m2.getGenre().add("Sport");
        Assertions.assertEquals(2, basicStrategyCompatibility.score(m1, m2));
    }
}