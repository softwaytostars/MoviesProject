package test.contentsquare.compatibility;

import test.contentsquare.domain.Movie;

import java.util.Set;

// BasicStrategyCompatibility implements a basic strategy for calculating the compatibility between 2 movies
// The score is computed as the sum of common elements in genre, directors and actors
// However, if case of no genre in common, or no actors in common, score to zero
// If one movie has missing directors but all actors are common, then consider as possible duplicate
public class BasicStrategyCompatibility implements StrategyCompatibility {

    private double scoreFor(Set<String> set1, Set<String> set2) {
        if (Math.min(set1.size(), set2.size()) <= 0) return 0.0;
        return 1.0 * Util.calculateSizeIntersection(set1, set2) / Util.calculateSizeUnion(set1, set2);
    }

    @Override
    public double score(Movie m1, Movie m2) {
            int nSource = 0;
            boolean considerGenre = Math.min(m1.getGenre().size(), m2.getGenre().size()) > 0;
            if (considerGenre) nSource++;
            boolean considerDirector = Math.min(m1.getDirectors().size(), m2.getDirectors().size()) > 0;
            if (considerDirector) nSource++;
            boolean considerActors = Math.min(m1.getActors().size(), m2.getActors().size()) > 0;
            if (considerActors) nSource++;

            // We cannot rely only on genre to guess if they are duplicates
            if (!considerDirector && !considerActors) {
                return 0;
            }

            // We don't want to rely only on one source to guess if duplicate, but could have some exceptions
            if (nSource < 2) {
                // we could authorize 1 source if the source is the actors and they are the same (at the condition there are enough people to lower the doubt)
                boolean sameActors = m1.getActors().size() >= 3 && m1.getActors().equals(m2.getActors());
                // We could also consider that is rare for a director to create another movie the next year, even more for more than 1 director.
                // So if they are the same, we make the assumption it's a possible duplicate
                boolean sameDirectors = m1.getDirectors().size() >= 2 && m1.getDirectors().equals(m2.getDirectors());
                if (!sameActors && !sameDirectors) {
                    return 0;
                }
            }

            int directorsInCommon = Util.calculateSizeIntersection(m1.getDirectors(), m2.getDirectors());
            int actorsInCommon = Util.calculateSizeIntersection(m1.getActors(), m2.getActors());

            // If no actors nor director in common, do not even consider to guess as duplicates
            if (directorsInCommon <= 0 && actorsInCommon <= 0) {
                return 0;
            }

            // if there are a good numbers of actors provided for both movies but no one is in common,
            // There is a tiny chance that they are duplicated. Consider no duplicate
            if (considerActors && actorsInCommon <= 0 && Math.min(m1.getActors().size(), m2.getActors().size()) >= 4) {
                return 0;
            }

            double score = scoreFor(m1.getGenre(), m2.getGenre());
            score += scoreFor(m1.getDirectors(), m2.getDirectors());
            score += scoreFor(m1.getActors(), m2.getActors());

            // If too low score, do not even consider
            if (score < 0.5) return 0.0;

            return score;
    }
}

