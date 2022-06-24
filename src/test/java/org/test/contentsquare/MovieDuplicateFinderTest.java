package org.test.contentsquare;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MovieDuplicateFinderTest {


    @Test
    void findDuplicateCandidatesFor() {
        MovieDuplicateFinder finder = new MovieDuplicateFinder();

        Movie movieRef = new Movie();
        movieRef.setYear(2000);
        movieRef.setLength(1);
        LinkedList<Movie> otherMovies = new LinkedList<>(
                List.of(
                        new Movie("id1", 2000, 2 ),
                        new Movie("id2", 2001, 1 ),
                        new Movie("id3", 2004, 1 )));

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
}