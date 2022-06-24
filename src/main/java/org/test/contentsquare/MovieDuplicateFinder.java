package org.test.contentsquare;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

//Edward L. Cahn director
//Rune Carlsten Director
//Çaman actor
//Paul Alexander -> duplicate
//Kunio Watanabe director
//Will Pascoe
// TODO intelligence, reconnaitre nationalité acteur et director
// recognition library for nationality
//if (Pattern.matches(".*[éèàù].*", input)) {

public class MovieDuplicateFinder {
    static class WrapperMovieCandidate {
        int index;
        Movie movie;

        public WrapperMovieCandidate(int index, Movie movie) {
            this.index = index;
            this.movie = movie;
        }
    }

     boolean areLengthCompatible(Movie m1, Movie m2) {
        return (1.0*Math.abs(m1.getLength() - m2.getLength())/(m1.getLength()+ m2.getLength())) <= 0.05;
    }
    List<WrapperMovieCandidate> findDuplicateCandidatesFor(Movie movieRef, LinkedList<Movie> otherMovies) {
        // otherMovies are sorted by year, so no need to take movies with year > movieRef+1
        return IntStream.range(0, otherMovies.size()-1).
                takeWhile(i-> otherMovies.get(i).getYear() <= movieRef.getYear()+1).
                filter(i -> areLengthCompatible(otherMovies.get(i), movieRef)).
                mapToObj(i -> new WrapperMovieCandidate(i, otherMovies.get(i))).
                collect(Collectors.toList());
    }

    private int calculateScoreCompatibility(Movie m1, Movie m2) {
        int genreInCommon = calculateSizeIntersection(m1.getGenre(), m2.getGenre());
        int directorsInCommon = calculateSizeIntersection(m1.getDirectors(), m2.getDirectors());
        int actorsInCommon = calculateSizeIntersection(m1.getActors(), m2.getActors());

        // If one of the considered set has nothing in common, do not score
        if (genreInCommon == 0 || directorsInCommon == 0 || actorsInCommon == 0) {
            return 0;
        }
        // Otherwise, consider only the set where both movies have the information
        return Math.max(0,genreInCommon) + Math.max(0,directorsInCommon) + Math.max(0,actorsInCommon);
    }

    private int calculateSizeIntersection(Set<String> set1, Set<String> set2) {
        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        return intersection.size();
    }

    private WrapperMovieCandidate choseDuplicateFor(Movie movieRef, List<WrapperMovieCandidate> candidates) {
        if (candidates.size() <=0) return null;
        int maxScore = 0;
        WrapperMovieCandidate result = null;
        for (WrapperMovieCandidate candidate : candidates) {
            int score = calculateScoreCompatibility(movieRef,candidate.movie);
            // Only candidates that score with a value greater than 0 are considered
            if (score > maxScore) {
                maxScore = score;
                result = candidate;
            }

        }
        return result;
    }

    public void findDuplicates(List<Movie> movies) {

        // Create a queue of movies sorted by year
        LinkedList<Movie> sortedMoviesByYear = movies.stream().
                sorted(Comparator.comparingInt(Movie::getYear)).
                collect(Collectors.toCollection(LinkedList::new));

        while (sortedMoviesByYear.size() > 1) {

            // Retrieve and remove the first element of the queue
            Movie movie = sortedMoviesByYear.removeFirst(); //O(1)

            // Find if there are duplicate candidates for this movie
            List<WrapperMovieCandidate> candidates = findDuplicateCandidatesFor(movie, sortedMoviesByYear);

            // Decide which one to take if any
            WrapperMovieCandidate matchMovie = choseDuplicateFor(movie, candidates);
            if (matchMovie != null) {
                // If a potential duplicate found, remove it from the queue and store it in output
                sortedMoviesByYear.remove(matchMovie.index);
                System.out.println(movie.getId()+"\t"+matchMovie.movie.getId());
            }
        }
    }
}
