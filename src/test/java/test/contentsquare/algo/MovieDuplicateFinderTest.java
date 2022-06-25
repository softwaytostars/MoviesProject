package test.contentsquare.algo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import test.contentsquare.compatibility.StrategyCompatibility;
import test.contentsquare.domain.Movie;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class MovieDuplicateFinderTest {

    @Test
    void findDuplicateCandidatesFor_SortByYear() {
        MovieDuplicateFinder finder = new MovieDuplicateFinder();

        Movie movieRef = new Movie();
        movieRef.setYear(2000);
        movieRef.setLength(1);
        LinkedList<Movie> otherMovies = new LinkedList<>(
                List.of(
                        new Movie("id1", 2000, 1 ),
                        new Movie("id2", 2001, 1 ),
                        new Movie("id3", 2002, 1 )));

        List<Movie> result = finder.findDuplicateCandidatesFor(movieRef, 0, otherMovies);

        List<Movie> expected = Arrays.asList(otherMovies.get(0), otherMovies.get(1));

        Assertions.assertEquals(expected.size(), result.size());
        for (int i=0; i < expected.size(); i++) {
            Assertions.assertEquals(expected.get(i), result.get(i));
        }
    }

    @Test
    void findDuplicateCandidatesFor_FilterByLength() {
        MovieDuplicateFinder finder = new MovieDuplicateFinder();

        Movie movieRef = new Movie();
        movieRef.setYear(2000);
        movieRef.setLength(135);
        LinkedList<Movie> otherMovies = new LinkedList<>(
                List.of(
                        new Movie("id1", 2000, 135 ),
                        new Movie("id2", 2000, 66 ),
                        new Movie("id3", 2000, 134 )));

        List<Movie> result = finder.findDuplicateCandidatesFor(movieRef, 0, otherMovies);

        List<Movie> expected = Arrays.asList(otherMovies.get(0), otherMovies.get(2));

        Assertions.assertEquals(expected.size(), result.size());
        for (int i=0; i < expected.size(); i++) {
            Assertions.assertEquals(expected.get(i), result.get(i));
        }
    }

    @Test
    void areLengthCompatible() {
        MovieDuplicateFinder finder = new MovieDuplicateFinder();
        assertFalse(finder.areLengthCompatible(new Movie("id1", 2000,135), new Movie("id2", 2000,66)));
        assertTrue(finder.areLengthCompatible(new Movie("id1", 2000,135), new Movie("id2", 2000,134)));
        Random generator = new Random(System.currentTimeMillis());
        {
            // <= x < 135
            int min = (int) (135 * 1.95 / 2.05) +1;
            int random = generator.nextInt(135 - min + 1) + min;
            assertTrue(finder.areLengthCompatible(new Movie("id1", 2000, 135), new Movie("id2", 2000, random)));
        }
        {
            int max = (int) (135 *2.05 / 1.95);
            int random = generator.nextInt((max-135)+1) +135;
            assertTrue(finder.areLengthCompatible(new Movie("id1", 2000, 135), new Movie("id2", 2000, random)));
        }
        {
            //  x < min < 135
            int min = (int) (135 * 1.95 / 2.05) +1;
            int random = generator.nextInt(min) ;
            assertFalse(finder.areLengthCompatible(new Movie("id1", 2000, 135), new Movie("id2", 2000, random)));
        }
        {
            // 135 < max < x
            int max = (int) (135 *2.05 / 1.95);
            int random = generator.nextInt(max+1) +max;
            assertFalse(finder.areLengthCompatible(new Movie("id1", 2000, 135), new Movie("id2", 2000, random)));
        }
    }

    @Test
    void writeResult() throws IOException {
        StringWriter writer = new StringWriter();
        MovieDuplicateFinder.writeResult(writer, "id1", "id2");
        Assertions.assertEquals("id1\tid2\n", writer.toString());
        writer.close();
    }

    @Test
    void choseMatchForReturnsEmpty() {
        MovieDuplicateFinder finder = new MovieDuplicateFinder();
        StrategyCompatibility mockStrategy = Mockito.mock(StrategyCompatibility.class);
        finder.setStrategyCompatibility(mockStrategy);
        Mockito.when(mockStrategy.score(Mockito.any(), Mockito.any())).thenReturn(0.0);

        Assertions.assertTrue(finder.choseMatchFor(0, new Movie("id",2022, 19), new ArrayList<>()).isEmpty());
        Assertions.assertTrue(finder.choseMatchFor(0, new Movie("id",2022, 19), List.of(new Movie("id2", 2022, 19))).isEmpty());
    }

    @Test
    void choseMatchForReturnsNonEmpty() {
        MovieDuplicateFinder finder = new MovieDuplicateFinder();
        StrategyCompatibility mockStrategy = Mockito.mock(StrategyCompatibility.class);
        finder.setStrategyCompatibility(mockStrategy);
        Mockito.when(mockStrategy.score(Mockito.any(), Mockito.any())).thenReturn(1.0);
        Optional<MovieDuplicateFinder.Match> match = finder.choseMatchFor(9, new Movie("id1",2022, 19), List.of(new Movie("id2", 2022, 19)));
        Assertions.assertTrue(match.isPresent());
        Assertions.assertEquals("id1", match.get().id1);
        Assertions.assertEquals("id2", match.get().id2);
        Assertions.assertEquals(1.0, match.get().score);
        Assertions.assertEquals(9, match.get().priority);
    }
}