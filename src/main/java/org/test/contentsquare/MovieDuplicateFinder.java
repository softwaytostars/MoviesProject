package org.test.contentsquare;

import org.test.contentsquare.compatibility.BasicStrategyCompatibility;
import org.test.contentsquare.compatibility.StrategyCompatibility;

import java.io.Writer;
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
    private StrategyCompatibility strategyCompatibility;

    public MovieDuplicateFinder() {
        this.strategyCompatibility = new BasicStrategyCompatibility();
    }

    // For changing the strategy of score computing for compatibility
    public void setStrategyCompatibility(StrategyCompatibility strategyCompatibility) {
        this.strategyCompatibility = strategyCompatibility;
    }

    static class WrapperMovieCandidate {
        int index;
        Movie movie;

        public WrapperMovieCandidate(int index, Movie movie) {
            this.index = index;
            this.movie = movie;
        }
    }

     boolean areLengthCompatible(Movie m1, Movie m2) {
        return (2.0*Math.abs(m1.getLength() - m2.getLength())/(m1.getLength()+ m2.getLength())) <= 0.05;
    }
    List<WrapperMovieCandidate> findDuplicateCandidatesFor(Movie movieRef, LinkedList<Movie> otherMovies) {
        // otherMovies are sorted by year, so no need to take movies with year > movieRef.year+1
        return IntStream.range(0, otherMovies.size()).
                takeWhile(i-> otherMovies.get(i).getYear() <= movieRef.getYear()+1).
                filter(i -> areLengthCompatible(otherMovies.get(i), movieRef)).
                mapToObj(i -> new WrapperMovieCandidate(i, otherMovies.get(i))).
                collect(Collectors.toList());
    }

    private WrapperMovieCandidate choseDuplicateFor(Movie movieRef, List<WrapperMovieCandidate> candidates) {
        if (candidates.size() <=0) return null;
        double maxScore = 0.5;
        WrapperMovieCandidate result = null;
        for (WrapperMovieCandidate candidate : candidates) {
            double score = strategyCompatibility.score(movieRef,candidate.movie);
            // Only candidates that score with a value greater than 0 are considered
            if (score > maxScore) {
                maxScore = score;
                result = candidate;
            }
        }
        return result;
    }

    private void WriteResult(Writer writer, Movie m1, Movie m2) {
        StringBuilder builder = new StringBuilder();
        builder.append(m1.getId());
        builder.append("\t");
        builder.append(m2.getId());
        builder.append("\n");
        try {
            writer.write(builder.toString());
        } catch (Exception e) {
            System.out.println("Cannot write data"+e.getMessage());
        }
    }

    public void findDuplicates(List<Movie> movies, Writer writer) {

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
                WriteResult(writer, movie, matchMovie.movie);
            }
        }
    }
}
