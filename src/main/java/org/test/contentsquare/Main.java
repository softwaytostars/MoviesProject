package org.test.contentsquare;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length <= 0) {
            System.out.println("You must provide the data file path");
            System.exit(1);
        }

        long start = System.currentTimeMillis();

        MovieDataProvider movieDataProvider = new MovieDataProvider();
        List<Movie> movies = movieDataProvider.provideMoviesFrom(new File(args[0]));
        MovieDuplicateFinder movieDuplicateFinder = new MovieDuplicateFinder();

        File fileOut = new File("output.txt");
        try (Writer writer = new FileWriter(fileOut)) {
            movieDuplicateFinder.findDuplicates(movies, writer);
        }
        long timeElapsed = System.currentTimeMillis() - start;
        System.out.println("minutes elapsed ="+(timeElapsed/1000)/60);
    }
}