package org.test.contentsquare;

import java.io.File;
import java.util.List;

public class Main {
    public static void main(String[] args)
    {
        if (args.length <= 0) {
            System.out.println("You must provide the data file path");
            System.exit(1);
        }
        MovieDataProvider movieDataProvider = new MovieDataProvider();
        List<Movie> movies = movieDataProvider.provideMoviesFrom(new File(args[0]));
        MovieDuplicateFinder movieDuplicateFinder = new MovieDuplicateFinder();
        movieDuplicateFinder.findDuplicates(movies);
    }
}