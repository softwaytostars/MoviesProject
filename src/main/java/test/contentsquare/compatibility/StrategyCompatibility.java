package test.contentsquare.compatibility;

import test.contentsquare.domain.Movie;

public interface StrategyCompatibility {
    double score(Movie m1, Movie m2);
}
