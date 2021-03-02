package com.company;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.ArrayDeque;


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
    // USE THIS INSTEAD OF A HARDCODED #
    private int numberOfThreads;

    public Tree (Document document) {
        buildTreeFromHTMLDocument(document);
        numberOfThreads = Runtime.getRuntime().availableProcessors();
    }

    public void traverseTreeSynchronously() {
        dfs(root);
    }

    public void levelOrderTraversal() {
        List<List<String>> list = new ArrayList<>();

        if(root == null){
           return;
        }

        Queue<TreeNode> queue = new ArrayDeque<>();
        queue.add(root);

        while(!queue.isEmpty()){
            int size = queue.size();
            List<String> temp = new ArrayList<>();

            for(int i = 0 ; i < size ; i++){
                TreeNode node = queue.poll();
                temp.add(node.getValue());

                for (TreeNode childNode : node.getChildren()) {
                    queue.add(childNode);
                }
            }

            // temp = list of nodes at current level in tree.
            if (temp.size() >= numberOfThreads) {
                // Spin off threads.
            }
            list.add(temp);
        }
        System.out.println(list);
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
        System.out.println(node.getValue());
        for (TreeNode child: node.getChildren()) {
            dfs(child);
        }
    }
}
