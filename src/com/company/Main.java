package com.company;

public class Main {
    public static void main(String[] args) {
        TraversalTest traversalTest = new TraversalTest("four-core-html.txt");
        traversalTest.testSynchronous();
        traversalTest.testConcurrent();

        HashMapTreeTest mapTreeTest = new HashMapTreeTest("four-core-html.txt");
        mapTreeTest.fillChildMapConcurrently();
    }
}