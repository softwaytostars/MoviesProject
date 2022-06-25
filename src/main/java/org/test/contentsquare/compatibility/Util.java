package org.test.contentsquare.compatibility;

import java.util.HashSet;
import java.util.Set;

public class Util {

    public static int calculateSizeIntersection(Set<String> set1, Set<String> set2) {
        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        return intersection.size();
    }

    public static int calculateSizeUnion(Set<String> set1, Set<String> set2) {
        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);
        return union.size();
    }
}
