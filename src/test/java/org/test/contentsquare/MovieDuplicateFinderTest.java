package org.test.contentsquare;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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

        List<MovieDuplicateFinder.WrapperMovieCandidate> result = finder.findDuplicateCandidatesFor(movieRef, otherMovies);

        List<MovieDuplicateFinder.WrapperMovieCandidate> expected = Arrays.asList(
                new MovieDuplicateFinder.WrapperMovieCandidate(0, otherMovies.get(0)),
                new MovieDuplicateFinder.WrapperMovieCandidate(1, otherMovies.get(1)));

        Assertions.assertEquals(expected.size(), result.size());
        for (int i=0; i < expected.size(); i++) {
            Assertions.assertEquals(expected.get(i).index, result.get(i).index);
            Assertions.assertEquals(expected.get(i).movie, result.get(i).movie);
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

        List<MovieDuplicateFinder.WrapperMovieCandidate> result = finder.findDuplicateCandidatesFor(movieRef, otherMovies);

        List<MovieDuplicateFinder.WrapperMovieCandidate> expected = Arrays.asList(
                new MovieDuplicateFinder.WrapperMovieCandidate(0, otherMovies.get(0)),
                new MovieDuplicateFinder.WrapperMovieCandidate(2, otherMovies.get(2)));

        Assertions.assertEquals(expected.size(), result.size());
        for (int i=0; i < expected.size(); i++) {
            Assertions.assertEquals(expected.get(i).index, result.get(i).index);
            Assertions.assertEquals(expected.get(i).movie, result.get(i).movie);
        }
    }

    @Test
    void areLengthCompatible() {
        MovieDuplicateFinder finder = new MovieDuplicateFinder();
        Assertions.assertFalse(finder.areLengthCompatible(new Movie("id1", 2000,135), new Movie("id2", 2000,66)));
        Assertions.assertTrue(finder.areLengthCompatible(new Movie("id1", 2000,135), new Movie("id2", 2000,134)));
        Random generator = new Random(System.currentTimeMillis());
        {
            int min = (int) (135 * 0.95 / 1.05) +1;
            int random = generator.nextInt(135 - min + 1) + min;
            Assertions.assertTrue(finder.areLengthCompatible(new Movie("id1", 2000, 135), new Movie("id2", 2000, random)));
        }
        {
            int max = (int) (135 *1.05 / 0.95);
            int random = generator.nextInt((max-135)+1) +135;
            Assertions.assertTrue(finder.areLengthCompatible(new Movie("id1", 2000, 135), new Movie("id2", 2000, random)));
        }
        {
            //  x < min < 135
            int min = (int) (135 * 0.95 / 1.05) +1;
            int random = generator.nextInt(min) ;
            Assertions.assertFalse(finder.areLengthCompatible(new Movie("id1", 2000, 135), new Movie("id2", 2000, random)));
        }
        {
            // 135 < max < x
            int max = (int) (135 *1.05 / 0.95);
            int random = generator.nextInt(max+1) +max;
            Assertions.assertFalse(finder.areLengthCompatible(new Movie("id1", 2000, 135), new Movie("id2", 2000, random)));
        }
    }
}