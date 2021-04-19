package com.company;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


class MapTreeNode {
    public int ID;
    private String value;
    private ArrayList<MapTreeNode> children;

    public MapTreeNode(String value) {
        this.value = value;
        children = new ArrayList<MapTreeNode>();
    }

    public String getValue() {
        return value;
    }

    public void addChild(MapTreeNode child) {
        children.add(child);
    }

    public ArrayList<MapTreeNode> getChildren() {
        return children;
    }

    public int getID()
    {
        return ID;
    }
}

public class HashMapTree {
    ConcurrentHashMap<Integer, Integer> concurrentHash = new ConcurrentHashMap<>();
    private MapTreeNode root;
    private int numberOfThreads;
    AtomicInteger pCount;
    AtomicInteger idx;

    public int numberOfChildren(MapTreeNode node)
    {
        if (node == null) return 0;

        int children = 0;

        for (MapTreeNode child : node.getChildren())
            children += numberOfChildren(child);

        concurrentHash.put(node.getID(), children);
        return 1 + children;
    }

    public HashMapTree (Document document) {
        numberOfThreads = 4;
        pCount = new AtomicInteger(0);
        idx = new AtomicInteger(0);
        buildTreeFromHTMLDocument(document);
    }

    public void traverseSynchronously() {
        System.out.println("===Traversing Synchronously===");

        long startTime = System.nanoTime();
        dfs(root);
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000;  //divide by 1000000 to get milliseconds.
        System.out.println("Synchronous execution time: " + duration);
        System.out.println(pCount.get());
    }

    public void fillChildMapConcurrently() {
        System.out.println("===Filling Child Map Concurrently===");

        Thread[] threads = new Thread[numberOfThreads];
        List<MapTreeNode> listOfNodes = levelOrderTraversal();
        int i = 0;
        for (MapTreeNode node : listOfNodes) {
            Thread thread = new Thread(new Runnable(){
                public void run() {
                    numberOfChildren(node);
                }
            });
            threads[i++] = thread;
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        printTopNNodes(4);
    }

    private void printTopNNodes(int n) {
        PriorityQueue<Integer> pq = new PriorityQueue<>();

        int i = 0;
        for (Integer key : concurrentHash.keySet()) {
            int value = concurrentHash.get(key);
            if (i++ < n) {
                pq.add(value);
            } else {
                pq.add(value);
                if (pq.size() > n) pq.poll();
            }
        }

        System.out.println("Most number of children: " + pq);
    }

    public void traverseConcurrently() {
        System.out.println("===Traversing Concurrently===");
        System.out.println("Number of threads:" + numberOfThreads);
        long startTime = System.nanoTime();

        Thread[] threads = new Thread[numberOfThreads];
        List<MapTreeNode> listOfNodes = levelOrderTraversal();
        int i = 0;
        for (MapTreeNode node : listOfNodes) {
            Thread thread = new Thread(new Runnable(){
                public void run() {
                    dfs(node);
                }
            });
            threads[i++] = thread;
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000;
        System.out.println("Concurrent execution time: " + duration);
        System.out.println(pCount);
    }

    public List<MapTreeNode> levelOrderTraversal() {
        if(root == null){
            return new ArrayList<>();
        }

        Queue<MapTreeNode> queue = new ArrayDeque<>();
        queue.add(root);

        while(!queue.isEmpty()){
            int size = queue.size();
            List<MapTreeNode> currentLevel = new ArrayList<>();

            for(int i = 0 ; i < size ; i++){
                MapTreeNode node = queue.poll();
                currentLevel.add(node);

                for (MapTreeNode childNode : node.getChildren()) {
                    queue.add(childNode);
                }
            }

            if (currentLevel.size() >= numberOfThreads) {
                return currentLevel;
            }
        }

        return new ArrayList<>();
    }

    private void buildTreeFromHTMLDocument(Document document) {
        Node docNode = document.getAllElements().first();
        root = new MapTreeNode(docNode.nodeName());
        root.ID = idx.getAndIncrement();
        traverseHTML(root, docNode);
    }

    private void traverseHTML(MapTreeNode treeNode, Node documentNode) {
        for (Node childNode : documentNode.childNodes()) {
            MapTreeNode newTreeNode = new MapTreeNode(childNode.nodeName());
            newTreeNode.ID = idx.getAndIncrement();
            treeNode.addChild(newTreeNode);
            traverseHTML(newTreeNode, childNode);
        }
    }

    private void dfs(MapTreeNode node) {
        if (node.getValue().equals("p")) {
            pCount.getAndIncrement();
        }
        for (MapTreeNode child: node.getChildren()) {
            dfs(child);
        }
    }
}
