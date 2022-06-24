package org.test.contentsquare;

import java.io.File;
import java.util.*;

public class MovieDataProvider {

    private void addField(Set<String> set, String val) {
        if (!"\\N".equals(val)) {
            String[] values = val.split(",");
            set.addAll(Arrays.asList(values));
        }
    }
    private Movie createMovieFrom(String line) {
        String[] value = line.split("\t");
        if (value.length < 6) return null;
        try {
            Movie movie = new Movie();
            movie.setId(value[0].trim());
            movie.setYear(Integer.parseInt(value[1].trim()));
            movie.setLength(Integer.parseInt(value[2].trim()));

            addField(movie.getGenre(), value[3].trim());
            addField(movie.getDirectors(), value[4].trim());
            addField(movie.getActors(), value[5].trim());
            movie.validate();
            return movie;
        }catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    public List<Movie> provideMoviesFrom(File file) {
        try {
            List<Movie> result = new ArrayList<>();
            Scanner scanner = new Scanner(file);
            // Read the title
            if (scanner.hasNextLine()) scanner.nextLine();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                Movie movie = createMovieFrom(line);
                if (movie != null) {
                    result.add(movie);
                } else {
                    System.out.printf("Cannot convert line %s to movie%n", line);
                }
            }
            scanner.close();
            return result;
        } catch (Exception e) {
            return null;
        }
    }
}
