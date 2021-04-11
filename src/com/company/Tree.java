package com.company;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.ArrayDeque;
import java.util.concurrent.atomic.AtomicInteger;


class TreeNode {
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
}

public class Tree {
    private TreeNode root;
    private int numberOfThreads;
    AtomicInteger pCount;


    public Tree (Document document) {
        buildTreeFromHTMLDocument(document);
        numberOfThreads = 4;
        pCount = new AtomicInteger(0);
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

    public void traverseConcurrently() {
        System.out.println("===Traversing Concurrently===");
        System.out.println("Number of threads:" + numberOfThreads);
        long startTime = System.nanoTime();

        Thread[] threads = new Thread[numberOfThreads];
        List<TreeNode> listOfNodes = levelOrderTraversal();
        int i = 0;
        for (TreeNode node : listOfNodes) {
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
        traverseHTML(root, docNode);
    }

    private void traverseHTML(TreeNode treeNode, Node documentNode) {
        for (Node childNode : documentNode.childNodes()) {
            TreeNode newTreeNode = new TreeNode(childNode.nodeName());
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
