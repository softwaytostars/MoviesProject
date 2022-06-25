package org.test.contentsquare.compatibility;

import org.test.contentsquare.Movie;

import java.util.Set;

// BasicStrategyCompatibility implements a basic strategy for calculating the compatibility between 2 movies
// The score is computed as the sum of common elements in genre, directors and actors
// However, if case of no genre in common, or no actors in common, score to zero
// If one movie has missing directors but all actors are common, then consider as possible duplicate
public class BasicStrategyCompatibility implements StrategyCompatibility{

    private double scoreFor(Set<String> set1, Set<String> set2) {
        if (Math.min(set1.size(),set2.size()) <=0) return 0.0;
        return 1.0*Util.calculateSizeIntersection(set1, set2)/Util.calculateSizeUnion(set1, set2);
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

        // We cannot rely only on one source to guess if duplicate,
        // except maybe if the source is the actors and they are all in common and with enough people to not doubt
        boolean allActors = m1.getActors().size() >=3 && m1.getActors().equals(m2.getActors()) ;
        if (nSource < 2 && !allActors) {
            return 0;
        }

        int genreInCommon = Util.calculateSizeIntersection(m1.getGenre(), m2.getGenre());
        int directorsInCommon = Util.calculateSizeIntersection(m1.getDirectors(), m2.getDirectors());
        int actorsInCommon = Util.calculateSizeIntersection(m1.getActors(), m2.getActors());

        // If no actors nor director in common, do not even consider to guess as duplicates
        if (directorsInCommon <=0 && actorsInCommon <= 0) {
            return 0;
        }

        double score = scoreFor(m1.getGenre(), m2.getGenre());
        score += scoreFor(m1.getDirectors(), m2.getDirectors());
        score += scoreFor(m1.getActors(), m2.getActors());
        return score;

//
//        // If all fields are equal (given one of them not empty), then consider as a possible duplicate
//        if (m1.getGenre().equals(m2.getGenre()) &&
//                m1.getActors().equals(m2.getActors()) &&
//                m1.getDirectors().equals(m2.getDirectors()) &&
//        (genreInCommon > 0 || actorsInCommon > 0 || directorsInCommon > 0)) {
//            return genreInCommon + directorsInCommon + actorsInCommon;
//        }
//
//        // If one of the considered set has nothing in common, do not score
//        if (genreInCommon == 0 || actorsInCommon == 0) {
//            return 0;
//        }
//        boolean allActors = actorsInCommon == m1.getActors().size() && m1.getActors().size() == m2.getActors().size();
//
//        // If missing director but all actors consider it, otherwise do not consider as duplicate
//        if (Math.min(m1.getDirectors().size(), m2.getDirectors().size()) == 0 && !allActors) {
//            return 0;
//        }
//        // Otherwise, consider only the set where both movies have the information
//        return genreInCommon + directorsInCommon + actorsInCommon;
    }
}
