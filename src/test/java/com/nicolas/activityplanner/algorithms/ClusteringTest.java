package com.nicolas.activityplanner.algorithms;

import org.eclipse.jdt.internal.compiler.ast.ArrayReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;


class ClusteringTest {
    private static long seed = 123;
    private static long[][] adjacency = {  {0,100,100,100,100},
                                            {100,0,100,100,100},
                                            {100,100,0,100,100},
                                            {100,100,100,0,100},
                                            {100,100,100,100,0}};

//    @AfterEach
//    static void resetAdjacency() {
//        adjacency = new long[][] {  {0,100,100,100,100},
//                {100,0,100,100,100},
//                {100,100,0,100,100},
//                {100,100,100,0,100},
//                {100,100,100,100,0}};
//    }

    @Test
    void cluster_OneElement() {
        long[][] adjacency = {{0}};
        List<Integer>[] clusters = Clustering.cluster(adjacency, 1, false);
        assertEquals(1, clusters.length);
        List<Integer> expected = new ArrayList<>();
        expected.add(0);
        assertEquals(expected, clusters[0]);
    }

    @Test
    void cluster_OneElementIgnoreFirst() {
        long[][] adjacency = {{0,1},{2,0}};
        List<Integer>[] clusters = Clustering.cluster(adjacency, 1, true);
        assertEquals(adjacency.length - 1, clusters.length);
        List<Integer> expected = new ArrayList<>();
        expected.add(1);
        assertEquals(expected, clusters[0]);
    }

    @Test
    void cluster_NequalsK() {
        long[][] adjacency = {{0,1,2,3},{2,0,3,4}, {4,2,0,5}, {1,3,5,0}};
        int k = 4;
        List<Integer>[] clusters = Clustering.cluster(adjacency, k, false);
        assertEquals(adjacency.length, clusters.length);
        List<Integer>[] expected = new List[k];
        for (int i = 0; i < k; i++) {
            expected[i] = new ArrayList<>();
            expected[i].add(i);
            assertEquals(expected[i], clusters[i]);
        }
    }

    @Test
    void cluster_NequalsKIgnoreFirst() {
        long[][] adjacency = {{0,1,2,3,4},{2,0,3,4,2}, {4,2,0,5,5}, {1,3,5,0,2}, {1,5,2,7,0}};
        int k = 4;
        List<Integer>[] clusters = Clustering.cluster(adjacency, k, true);
        assertEquals(adjacency.length - 1, clusters.length);
        List<Integer>[] expected = new List[k];
        for (int i = 0; i < k; i++) {
            expected[i] = new ArrayList<>();
            expected[i].add(i+1);
            assertEquals(expected[i], clusters[i]);
        }
    }

    @Test
    void cluster_Simple2Clusters() {
        // Make 0,1,2 close and 3,4 close
        long[][] adjacency = {  {0,1,1,100,100},
                                {1,0,3,100,100},
                                {1,4,0,100,100},
                                {100,100,100,0,1},
                                {100,100,100,1,0}};

        int k = 2;
        Clustering.setSeed(123);
        List<Integer>[] clusters = Clustering.cluster(adjacency, k, false);
        Clustering.sortClusters(clusters);

        assertEquals(k, clusters.length);
        List<Integer>[] expected = new List[k];
        expected[0] = Arrays.asList(0,1,2);
        expected[1] = Arrays.asList(3,4);

        for (int i = 0; i < k; i++) {
            assertEquals(expected[i], clusters[i], "Failed at: " + i);
        }
    }

    @Test
    void cluster_Simple3Clusters() {
        // Make 0 and 4 close, 1 and 3 close and 2 alone
        long[][] adjacency = {  {0,100,100,100,1},
                                {100,0,100,1,100},
                                {100,100,0,100,100},
                                {100,3,100,0,100},
                                {3,100,100,100,0}};

        int k = 3;
        Clustering.setSeed(321);
        List<Integer>[] clusters = Clustering.cluster(adjacency, k, false);
        Clustering.sortClusters(clusters);

        assertEquals(k, clusters.length);
        List<Integer>[] expected = new List[k];
        expected[0] = Arrays.asList(0,4);
        expected[1] = Arrays.asList(1,3);
        expected[2] = Arrays.asList(2);

        for (int i = 0; i < k; i++) {
            assertEquals(expected[i], clusters[i], "Failed at: " + i);
        }
    }

    @Test
    void cluster_Simple1Cluster() {
        // Make 0 and 4 close, 1 and 3 close and 2 alone
        long[][] adjacency = {  {0,100,100,100,1},
                {100,0,100,1,100},
                {100,100,0,100,100},
                {100,3,100,0,100},
                {3,100,100,100,0}};

        int k = 1;
        Clustering.setSeed(321);
        List<Integer>[] clusters = Clustering.cluster(adjacency, k, false);
        Clustering.sortClusters(clusters);

        assertEquals(k, clusters.length);
        List<Integer>[] expected = new List[k];
        expected[0] = Arrays.asList(0,1,2,3,4);

        for (int i = 0; i < k; i++) {
            assertEquals(expected[i], clusters[i], "Failed at: " + i);
        }
    }

    @Test
    void cluster_Simple2ClustersWrongCenters() {
        // Make 0,1,2 close and 3,4 close and 3 slightly closer to 0,1,2 than 4
        // Optimal is [0,1,2] and [3,4]
        long[][] adjacency = {  {0,1,1,99,100},
                                {1,0,3,99,100},
                                {1,4,0,99,100},
                                {99,99,99,0,1},
                                {100,100,100,1,0}};

        int k = 2;
        Clustering.setSeed(7);
        List<Integer>[] clusters = Clustering.cluster(adjacency, k, false);
        Clustering.sortClusters(clusters);
        assertEquals(k, clusters.length);

        List<Integer>[] expected = new List[k];
        expected[0] = Arrays.asList(0,1,2);
        expected[1] = Arrays.asList(3,4);

        for (int i = 0; i < k; i++) {
            assertEquals(expected[i], clusters[i], "Failed at: " + i);
        }
    }
}