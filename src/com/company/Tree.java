package com.company;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.ArrayDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


class TreeNode {
    public int ID;
    private String value;
    private ArrayList<TreeNode> children;

    public TreeNode(String value) {
        this.value = value;
        children = new ArrayList<TreeNode>();
    }

    public String getValue() {
        return value;
    }

    public void addChild(TreeNode child) {
        children.add(child);
    }

    public ArrayList<TreeNode> getChildren() {
        return children;
    }

    public int getID()
    {
        return ID;
    }
}

public class Tree {
    ConcurrentHashMap<Integer, Integer> concurrentHash = new ConcurrentHashMap<>();
    private TreeNode root;
    // USE THIS INSTEAD OF A HARDCODED #
    private int numberOfThreads;
    AtomicInteger pCount;
    AtomicInteger idx;

    public int numberOfChildren(TreeNode node)
    {
        if (node == null) return 0;

        int children = 0;

        for (TreeNode child : node.getChildren())
            children += numberOfChildren(child);

        concurrentHash.put(node.getID(), children);
        return 1 + children;
    }

    public Tree (Document document) {
        buildTreeFromHTMLDocument(document);
//        numberOfThreads = Runtime.getRuntime().availableProcessors();
        numberOfThreads = 4;
        pCount = new AtomicInteger(0);
        idx = new AtomicInteger(0);
    }

    public void traverseTreeSynchronously() {
        System.out.println("===Traversing Synchronously===");

        long startTime = System.nanoTime();
        dfs(root);
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000;  //divide by 1000000 to get milliseconds.
        System.out.println("Synchronous execution time: " + duration);
        System.out.println(pCount.get());
    }

    public void traverseConcurrently() {
        System.out.println("===Traversing Synchronously===");
        System.out.println("Number of threads:" + numberOfThreads);
        List<TreeNode> listOfNodes = levelOrderTraversal();
        long startTime = System.nanoTime();
        for (TreeNode node : listOfNodes) {
           Thread thread = new Thread(new Runnable(){
               public void run() {
                   dfs(node);
               }
           });

           thread.start();
        }

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000;  //divide by 1000000 to get milliseconds.
        System.out.println("Concurrent execution time: " + duration);
        System.out.println(pCount);
    }

//    private int getTreeHeight(TreeNode node) {
//        return Math.max(no);
//        for (TreeNode node : )
//    }

    public List<TreeNode> levelOrderTraversal() {
        if(root == null){
           return new ArrayList<>();
        }

        Queue<TreeNode> queue = new ArrayDeque<>();
        queue.add(root);

        while(!queue.isEmpty()){
            int size = queue.size();
            List<TreeNode> currentLevel = new ArrayList<>();

            for(int i = 0 ; i < size ; i++){
                TreeNode node = queue.poll();
                currentLevel.add(node);

                for (TreeNode childNode : node.getChildren()) {
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
        root = new TreeNode(docNode.nodeName());
        root.ID = idx.getAndIncrement();
        traverseHTML(root, docNode);
    }

    private void traverseHTML(TreeNode treeNode, Node documentNode) {
        for (Node childNode : documentNode.childNodes()) {
            TreeNode newTreeNode = new TreeNode(childNode.nodeName());
            newTreeNode.ID = idx.getAndIncrement();
            treeNode.addChild(newTreeNode);
            traverseHTML(newTreeNode, childNode);
        }
    }

    private void dfs(TreeNode node) {
        if (node.getValue().equals("p")) {
            pCount.getAndIncrement();
        }
        for (TreeNode child: node.getChildren()) {
            dfs(child);
        }
    }
}
