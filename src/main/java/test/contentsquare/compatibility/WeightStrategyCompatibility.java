package test.contentsquare.compatibility;

import test.contentsquare.domain.Movie;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

// WeightStrategyCompatibility implements a weight strategy for calculating the compatibility between 2 movies
public class WeightStrategyCompatibility implements StrategyCompatibility{

    enum TypeField {
        GENRE,
        DIRECTORS,
        ACTORS
    }

     static class FieldComputeScore {
        private final Set<String> m1Set;
        private final Set<String> m2Set;
        private final double weight;

        // lazy evaluation because it has a cost time
        public int getIntersectionSize() {
            if (intersectionSize >= 0) return intersectionSize;
            if (canCompare) {
                intersectionSize = Util.calculateSizeIntersection(m1Set, m2Set);
            }
            return intersectionSize;
        }
        // lazy evaluation because it has a cost time
        public int getUnionSize() {
            if (unionSize >= 0) return unionSize;
            if (canCompare) {
                unionSize = Util.calculateSizeUnion(m1Set, m2Set);
            }
            return unionSize;
        }

        private int intersectionSize = -1;
        private int unionSize = -1;
        private final boolean canCompare;

        public FieldComputeScore(double weight, Set<String> m1Set, Set<String> m2Set) {
            this.m1Set = m1Set;
            this.m2Set = m2Set;
            this.weight = weight;
            canCompare = Math.min(m1Set.size(), m2Set.size()) > 0;
            if (!canCompare) {
                intersectionSize = 0;
                unionSize = Math.max(m1Set.size(), m2Set.size());
            }

        }
    }

    @Override
    public double score(Movie m1, Movie m2) {
            Map<TypeField, FieldComputeScore> computeInfosByField = new HashMap<>();
            computeInfosByField.put(TypeField.GENRE, new FieldComputeScore(1.0, m1.getGenre(), m2.getGenre()));
            computeInfosByField.put(TypeField.DIRECTORS, new FieldComputeScore(2.0, m1.getDirectors(), m2.getDirectors()));
            computeInfosByField.put(TypeField.ACTORS, new FieldComputeScore(3.0, m1.getActors(), m2.getActors()));

            // I chose to not rely only on genre for guessing if they are duplicates
            if (!computeInfosByField.get(TypeField.DIRECTORS).canCompare && !computeInfosByField.get(TypeField.ACTORS).canCompare) {
                return 0;
            }

            // If there are no actors to compare to, but directors, then at least, there should be >= 2 directors in each to compare
            // Given that the probability that 2 same directors make together a second movie in 1 year in range should be small.
            // That risks to diminish K/M but have a better K/N.
            if (!computeInfosByField.get(TypeField.ACTORS).canCompare && Math.min(m1.getDirectors().size(), m2.getDirectors().size()) < 2) {
                return 0;
            }

            // If no common actors, that's difficult to tell. At least require 2 same directors. That risks to diminish K/M but have a better K/N.
            if (computeInfosByField.get(TypeField.DIRECTORS).getIntersectionSize() < 2 && computeInfosByField.get(TypeField.ACTORS).getIntersectionSize() <= 0) {
                return 0;
            }

            // Calculate a probability score (barycentre). The weight taken is the one attributed for the type of field multiplied by
            // intersection size. Because we want to reflect that a good ratio with a large size is much better than a ratio with a small size.
            double numerator = 0.0;
            double denominator = 0.0;
            for (FieldComputeScore fieldComputeScore : computeInfosByField.values()) {
                double weight = fieldComputeScore.weight * Math.max(1, fieldComputeScore.getIntersectionSize());
                double ratio = 0.0;
                if (fieldComputeScore.getUnionSize() > 0) {
                    ratio = 1.0 * fieldComputeScore.getIntersectionSize() / fieldComputeScore.getUnionSize();
                }
                numerator += ratio * weight;
                denominator += weight;
            }

            if (denominator <= 0.0) return 0.0;

            double proba = numerator / denominator;
            if(proba < 0.65) return 0.0;
            return proba;
    }
}
