package com.nicolas.activityplanner.algorithms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Clustering {

    /**
     * Runs k-medoids clustering algorithm. Points are labeled from 0 to n.
     * @param adjacency Adjacency matrix
     * @param k number of clusters
     * @param ignoreFirst whether we should consider the first point
     * @return an array of k lists of integers, each list representing a cluster.
     */
    public static List<Integer>[] cluster(long[][] adjacency, int k, boolean ignoreFirst) {
        int n = adjacency.length;
        int offset = ignoreFirst ? 1 : 0;

        List<Integer>[] clusters = new List[k];
        int[] centers = new int[k];

        for (int i = 0; i < k; i++) clusters[i] = new ArrayList<>();

        if (n - offset < k) {    // No need to cluster in this case
            for (int i = offset; i < n; i++) {
                clusters[i].add(i);
            }
        } else { // Otherwise, cluster the n points into k clusters
            // Initialize k random centers
            List<Integer> random = new ArrayList<>();
            for (int i = offset; i < n; i++) random.add(i);
            Collections.shuffle(random);
            for (int i = 0; i < k; i++) centers[i] = random.get(i);

            boolean hasChanged = false;

            do {
                // Clear clusters
                for (int i = 0; i < k; i++) clusters[i].clear();

                // Assign points to clusters
                // For each point, find the closest center (in either outgoing or incoming direction)
                for (int i = offset; i < n; i++) {
                    long min = Long.MAX_VALUE;
                    int center = 0;
                    for (int j = 0; j < k; j++) {
                        int centerVal = centers[j];
                        if (adjacency[i][centerVal] < min || adjacency[centerVal][i] < min) {
                            min = Long.min(adjacency[i][centerVal], adjacency[centerVal][i]);
                            center = j;
                        }
                    }
                    clusters[center].add(i);
                }

                // Redefine centers
                for (int i = 0; i < k; i++) {
                    long min = Long.MAX_VALUE;
                    int best = 0;
                    List<Integer> cluster = clusters[i];
                    // Find best center for cluster
                    for (int j = 0; j < cluster.size(); j++) {
                        long total = 0;
                        for (int l = 0; l < cluster.size(); j++) {
                            total += Long.min(
                                    adjacency[cluster.get(j)][cluster.get(l)],
                                    adjacency[cluster.get(l)][cluster.get(j)]
                            );
                        }
                        if (total < min) {
                            min = total;
                            best = j;
                        }
                    }
                    // Check if the same center as before
                    if (best != centers[i]) {
                        hasChanged = true;
                        centers[i] = best;
                    }
                }
            } while(hasChanged);
        }

        return clusters;
    }
}
