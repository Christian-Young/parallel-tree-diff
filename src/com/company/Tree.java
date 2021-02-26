package com.company;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;

import java.util.ArrayList;

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

    public Tree (Document document) {
        buildTreeFromHTMLDocument(document);
    }

    public void traverseTreeSynchonously() {
        dfs(root);
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
