package test.contentsquare.compatibility;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import test.contentsquare.domain.Movie;

class BasicStrategyCompatibilityTest {

    @Test
    void score_BasicOnlyGenre() {
        BasicStrategyCompatibility basicStrategyCompatibility = new BasicStrategyCompatibility();
        Movie m1 = new Movie("id", 1910, 95);
        m1.getGenre().add("Doc");
        m1.getGenre().add("Sport");

        Movie m2 = new Movie("id", 1910, 95);
        m2.getGenre().add("Doc");
        m2.getGenre().add("Sport");
        Assertions.assertEquals(0.0, basicStrategyCompatibility.score(m1, m2));
    }

    @Test
    void score_BasicOnlyDirectors() {
        BasicStrategyCompatibility basicStrategyCompatibility = new BasicStrategyCompatibility();
        Movie m1 = new Movie("id", 1910, 95);
        m1.getDirectors().add("Stan");
        m1.getDirectors().add("Ston");

        Movie m2 = new Movie("id", 1910, 95);
        m2.getDirectors().add("Stan");
        m2.getDirectors().add("Ston");
        Assertions.assertEquals(1.0, basicStrategyCompatibility.score(m1, m2));
    }

    @Test
    void score_MissingInfoDirectorForOne() {
        BasicStrategyCompatibility basicStrategyCompatibility = new BasicStrategyCompatibility();
        Movie m1 = new Movie("id", 1942, 80);
        m1.getGenre().add("Drama");
        m1.getDirectors().add("Stan");

        Movie m2 = new Movie("id", 1942, 83);
        m2.getGenre().add("Drama");
        m2.getDirectors().add("Stan");
        m2.getActors().add("Marie");
        m2.getActors().add("Jean");
        m2.getActors().add("Robert");
        m2.getActors().add("Ginette");
        Assertions.assertEquals(2.0, basicStrategyCompatibility.score(m1, m2));
    }
}