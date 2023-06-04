package com.nicolas.activityplanner.algorithms;

import java.util.*;

public class Clustering {
    private static long seed = 123;

    public static void setSeed(long seed) {
        Clustering.seed = seed;
    }

    /**
     * Runs k-medoids clustering algorithm. Points are labeled from 0 to n.
     * @param adjacency Adjacency matrix
     * @param k number of clusters
     * @param ignoreFirst whether we should consider the first point
     * @return an array of k lists of integers, each list representing a cluster.
     */
    public static List<Integer>[] cluster(long[][] adjacency, int k, boolean ignoreFirst) {
        if (adjacency == null || k == 0 || (ignoreFirst && adjacency.length == 1)) {
            throw new IllegalArgumentException("Adjacency matrix is not big enough or k is 0");
        }
        int n = adjacency.length;
        int offset = ignoreFirst ? 1 : 0;

        List<Integer>[] clusters = new List[k];
        int[] centers = new int[k];

        for (int i = 0; i < k; i++) clusters[i] = new ArrayList<>();

        if (n - offset <= k) {    // No need to cluster in this case
            for (int i = offset; i < n; i++) {
                clusters[i-offset].add(i);
            }
        } else { // Otherwise, cluster the n points into k clusters
            // Initialize k random centers
            List<Integer> random = new ArrayList<>();
            for (int i = offset; i < n; i++) random.add(i);
            Collections.shuffle(random, new Random(seed));
            for (int i = 0; i < k; i++) centers[i] = random.get(i);

            boolean hasChanged = false;
            int maxIters = 100;
            int a = 0;

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
                    // Find best center for cluster by calculating total minimal distance
                    // from each point to all other points in cluster
                    for (int j = 0; j < cluster.size(); j++) {
                        long total = 0;
                        for (int l = 0; l < cluster.size(); l++) {
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
                    if (cluster.get(best) != centers[i]) {
                        hasChanged = true;
                        centers[i] = cluster.get(best);
                    }
                    // Put cluster center at front for convenience
//                    Collections.swap(cluster, 0, best);
                }
                a++;
            } while(hasChanged && a < maxIters);
        }

        return clusters;
    }

    /**
     * Sort each clusters individually and then sort the list of clusters by first element
     * @param clusters
     */
    public static void sortClusters(List<Integer>[] clusters) {
        // Sort each cluster individually
        for (int i = 0; i < clusters.length; i++) {
            Collections.sort(clusters[i]);
        }
        // Sort list of clusters
        // Bubble sort for now
        boolean swapped = false;
        for (int i = 0; i < clusters.length - 1; i++) {
            swapped = false;
            for (int j = 0; j < clusters.length - i - 1; j++) {
                if (clusters[j].get(0) > clusters[j+1].get(0)) {
                    List temp = clusters[j];
                    clusters[j] = clusters[j+1];
                    clusters[j+1] = temp;
                    swapped = true;
                }
            }

            if (!swapped) {
                break;
            }
        }
    }
}
