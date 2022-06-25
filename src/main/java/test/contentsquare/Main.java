package test.contentsquare;

import test.contentsquare.algo.MovieDuplicateFinder;
import test.contentsquare.compatibility.FactoryStrategy;
import test.contentsquare.compatibility.StrategyCompatibility;
import test.contentsquare.dataprovider.MovieDataProvider;
import test.contentsquare.domain.Movie;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length <= 0) {
            System.out.println("You must provide the data file path\n");
            System.out.println("Example : java -jar target/MovieProject-1.0-SNAPSHOT.jar ../dedup-2020/movies.tsv  optional(basic or weight)\n");
            System.exit(1);
        }

        long start = System.nanoTime();

        // retrieve the movies from the file
        MovieDataProvider movieDataProvider = new MovieDataProvider();
        List<Movie> movies = movieDataProvider.provideMoviesFrom(new File(args[0]));

        if (movies == null || movies.size() <=0) {
            System.out.println("no movies found\n");
            System.exit(1);
        }

        MovieDuplicateFinder movieDuplicateFinder = new MovieDuplicateFinder();
        if (args.length >= 2) {
            StrategyCompatibility strategy = FactoryStrategy.createStrategy(args[1]);
            if (strategy != null) {
                movieDuplicateFinder.setStrategyCompatibility(strategy);
            }
        }

        File fileOut = new File("output.txt");
        try (Writer writer = new FileWriter(fileOut)) {
            movieDuplicateFinder.findDuplicates(movies, writer);
        }

        long elapsed = System.nanoTime() - start;
        System.out.printf("Elapsed time:%f seconds", elapsed / Math.pow(10, 9)); // >~ 70 seconds with weight strategy
    }
}