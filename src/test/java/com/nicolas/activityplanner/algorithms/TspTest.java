package com.nicolas.activityplanner.algorithms;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TspTest {

    @Test
    void symmetric1() {
        Tsp.setup(new long[][]
                {
                        { 0, 10, 15, 20 },
                        {10,  0, 35, 25 },
                        {15, 35,  0, 30 },
                        {20, 25, 30,  0 }
                }
        );
        Tsp.solve();

        assertEquals(80, Tsp.getBestTourCost());
        assertArrayEquals(new int[] {0,1,3,2},Tsp.getBestTour());
    }

    @Test
    void symmetric2() {
        Tsp.setup(new long[][]
                {
                        { 0, 100, 15, 20 },
                        {100,  0, 35, 25 },
                        {15, 35,  0, 30 },
                        {20, 25, 30,  0 }
                }
        );
        Tsp.solve();

        assertEquals(95, Tsp.getBestTourCost());
        assertArrayEquals(new int[] {0,2,1,3},Tsp.getBestTour());
    }

    @Test
    void symmetric3() {
        Tsp.setup(new long[][]
                {
                        { 0, 10, 15, 20 },
                        {10,  0, 35, 25 },
                        {15, 35,  0, 300 },
                        {20, 25, 300,  0 }
                }
        );
        Tsp.solve();

        assertEquals(95, Tsp.getBestTourCost());
        assertArrayEquals(new int[] {0,2,1,3},Tsp.getBestTour());
    }

    @Test
    void asymmetricSimple() {
        Tsp.setup(new long[][]
                {
                        {0, 2},
                        {5, 0}
                }
        );
        Tsp.solve();

        assertEquals(7, Tsp.getBestTourCost());
        assertArrayEquals(new int[] {0,1},Tsp.getBestTour());
    }

    @Test
    void asymmetric1() {
        Tsp.setup(new long[][]
                {
                        { 0, 100, 15, 20 },
                        {10,  0, 35, 25 },
                        {15, 35,  0, 30 },
                        {20, 25, 30,  0 }
                }
        );
        Tsp.solve();

        assertEquals(80, Tsp.getBestTourCost());
        assertArrayEquals(new int[] {0,1,3,2},Tsp.getBestTour());
    }

    @Test
    void asymmetric2() {
        Tsp.setup(new long[][]
                {
                        { 0, 10, 15, 20 },
                        {100,  0, 35, 25 },
                        {15, 35,  0, 30 },
                        {20, 25, 30,  0 }
                }
        );
        Tsp.solve();

        assertEquals(80, Tsp.getBestTourCost());
        assertArrayEquals(new int[] {0,2,3,1},Tsp.getBestTour());
    }
}