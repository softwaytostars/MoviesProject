package test.contentsquare.algo;

import test.contentsquare.compatibility.WeightStrategyCompatibility;
import test.contentsquare.domain.Movie;
import test.contentsquare.compatibility.BasicStrategyCompatibility;
import test.contentsquare.compatibility.StrategyCompatibility;

import java.io.Writer;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class MovieDuplicateFinder {
    private StrategyCompatibility strategyCompatibility;

    public MovieDuplicateFinder() {
        this.strategyCompatibility = new WeightStrategyCompatibility();
    }

    // For changing the strategy of score computing for compatibility between movies
    public void setStrategyCompatibility(StrategyCompatibility strategyCompatibility) {
        this.strategyCompatibility = strategyCompatibility;
    }

    static class Match {
        String id1;
        String id2;

        double score;
        int priority;

        public Match(String id1, String id2, double score, int priority) {
            this.id1 = id1;
            this.id2 = id2;
            this.score = score;
            this.priority = priority;
        }
    }

    // areLengthCompatible tells if 2 movies are length compatible
    boolean areLengthCompatible(Movie m1, Movie m2) {
        return (2.0 * Math.abs(m1.getLength() - m2.getLength()) / (m1.getLength() + m2.getLength())) <= 0.05;
    }

    // findDuplicateCandidatesFor find all movies that are compatible within year and length with movieRef
    List<Movie> findDuplicateCandidatesFor(Movie movieRef, int from, List<Movie> sortedMoviesByYear) {
        List<Movie> result = new ArrayList<>();
        for (int i = from; i < sortedMoviesByYear.size(); i++) {
            Movie movie = sortedMoviesByYear.get(i);

            // sortedMoviesByYear are sorted by year, so stop the loop as soon as year > movieRef.year+1
            if (movie.getYear() > movieRef.getYear() + 1) {
                break;
            }
            if (areLengthCompatible(movie, movieRef)) {
                result.add(movie);
            }
        }
        return result;
    }

    // choseMatchFor will calculate a score of compatibility between movieRef and all candidates
    // It will provide the candidate with highest score > 0, empty otherwise
     Optional<Match> choseMatchFor(int index, Movie movieRef, List<Movie> candidates) {
        if (candidates.size() <= 0) return Optional.empty();
        double maxScore = 0;
        Movie result = null;
        for (Movie candidate : candidates) {
            double score = strategyCompatibility.score(movieRef, candidate);
            // Only candidates that score with a value greater than 0 are considered
            if (score > maxScore) {
                maxScore = score;
                result = candidate;
            }
        }
        if (result != null) {
            return Optional.of(new Match(movieRef.getId(), result.getId(), maxScore, index));
        }
        return Optional.empty();
    }

    // writeResult will write the matching ids into the writer
    static void writeResult(Writer writer, String id1, String id2) {
        StringBuilder builder = new StringBuilder();
        builder.append(id1);
        builder.append("\t");
        builder.append(id2);
        builder.append("\n");
        try {
            writer.write(builder.toString());
        } catch (Exception e) {
            System.out.println("Cannot write result" + e.getMessage());
        }
    }

    // findMatchIfAny is responsible for finding a match if any between the movie located at index in sortedMoviesByYear
    // and the other movies in the list
    private Optional<Match> findMatchIfAny(int index, List<Movie> sortedMoviesByYear) {
        Movie movieRef = sortedMoviesByYear.get(index);
        // The list of movies is sorted by year, no just need to compare to sublist starting to the next index
        List<Movie> candidates = findDuplicateCandidatesFor(movieRef, index + 1, sortedMoviesByYear);

        // Decide which one to take if any
        return choseMatchFor(index, movieRef, candidates);
    }

    // findDuplicates is the main algo method
    // it finds matches for each movie concurrently
    // The mechanism is based on the fact that the list is sorted by year
    public void findDuplicates(List<Movie> movies, Writer writer) {

        // Create a pool of threads
        ExecutorService executor = Executors.newFixedThreadPool(20);

        // Create a list of movies sorted by year
        List<Movie> sortedMoviesByYear = movies.stream().
                sorted(Comparator.comparingInt(Movie::getYear)).
                collect(Collectors.toList());

        // Create a future for finding a match for each movie
        List<CompletableFuture<Optional<Match>>> allFutures = new ArrayList<>();
        for (int i = 0; i < sortedMoviesByYear.size(); i++) {
            int finalI = i;
            allFutures.add(CompletableFuture.supplyAsync(() -> findMatchIfAny(finalI, sortedMoviesByYear), executor));
        }

        // Wait all threads and map to a collection of future of matches objects
        CompletableFuture<List<Optional<Match>>> allResults = CompletableFuture.allOf(
                allFutures.toArray(new CompletableFuture[0])
        ).thenApply(v -> allFutures.stream()
                .map(CompletableFuture::join) // Here the join doesn't block since it is executed once future is finished, in the callback apply
                .collect(Collectors.toList())
        );

        try {
            // Wait for all futures and map the result to a list of matches sorted by priority
            // The priority is actually the index order in the sorted movie list
            List<Match> matches = allResults.get().
                    stream().
                    filter(Optional::isPresent).
                    map(Optional::get).
                    sorted(Comparator.comparingInt(m -> m.priority)).
                    collect(Collectors.toList());

            Map<String, Match> dup = new HashMap<>();
            for (Match m : matches) {
                // Do not consider matches which one movie has already a match with more priority
                if (dup.get(m.id1) != null || dup.get(m.id2) != null) {
                    continue;
                }
                writeResult(writer, m.id1, m.id2);
                dup.put(m.id1, m);
                dup.put(m.id2, m);
            }
        } catch (Exception e) {
            System.out.println("Cannot retrieve matches :" + e.getMessage());
        } finally {
            executor.shutdown();
        }
    }
}
