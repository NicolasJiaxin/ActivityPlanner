package com.nicolas.activityplanner.algorithms;

import javax.swing.text.Style;

public class Tsp {
    static long MAX = 1000000000;

    static long bestTourCost = MAX;
    static int[] bestTour;

    static int n;

    static long[][] dist;

    // memoization for top-down recursion
    static long[][] memo;
    static int[][] prev;

    public static void setup(long[][] dist) {
        Tsp.dist = dist;
        Tsp.n = dist.length;
        memo = new long[n][1 << (n)];
        prev = new int[n][1 << (n)];
        bestTour = new int[n];
    }

    // Returns the min-cost of the path from vertex 0 to i using all vertices set in mask
    static long fun(int i, int mask)
    {
        // base case: only vertex 0 and i
        if (mask == ((1 << i) | 1)) {
            //prev[i][mask] = 1; // Could set it for clarity but really is trivial
            return dist[0][i];
        }
        // memoization
        if (memo[i][mask] != 0) {
            return memo[i][mask];
        }

        long res = MAX; // result of this sub-problem
        int prevVertex = 0;

        // To find the min cost path from 0 to i using all vertices in the mask, find min of
        // the costs of each path from 0 to j + dist[j][i] where j is in the mask, j!=i and j!=0
        for (int j = 1; j < n; j++)
            if ((mask & (1 << j)) != 0 && j != i) {
                long bestPrevTour = fun(j, mask & (~(1 << i))) + dist[j][i];
                if (bestPrevTour < res) {
                    res = bestPrevTour;
                    prevVertex = j;
                }
            }
        memo[i][mask] = res;
        prev[i][mask] = prevVertex;
        return res;
    }

    public static void solve() {
        long ans = MAX;
        int prevVertex = 1;
        for (int i = 0; i < n; i++) {
            // try to go from node 1 visiting all nodes in
            // between to i then return from i taking the
            // shortest route to 1
            long bestTour = fun(i, (1 << n) - 1) + dist[i][0];
            if (bestTour < ans) {
                ans = bestTour;
                prevVertex = i;
            }
        }

        bestTourCost = ans;

        // Backtrack the best tour starting at the last vertex
        int mask = (1 << n) - 1;
        bestTour[0] = 0;
        for(int i = n-2; i >= 0; i--) {
            bestTour[i+1] = prevVertex;
            int temp = prev[prevVertex][mask];
            mask = mask & (~(1 << prevVertex));
            prevVertex = temp;
        }
    }

    public static long getBestTourCost() {
        return bestTourCost;
    }

    public static int[] getBestTour() {
        return bestTour;
    }
}
