package com.company;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

public class Main {
    public static void main(String[] args) {
        String html = "<html><head></head><body><div><h1></h1><div><ul><li></li><li></li><li></li><li></li></ul></div></div><div><h1></h1><div><ul><li></li><li></li><li></li><li></li></ul></div></div></body></html>";
        Document doc = Jsoup.parse(html);

        Tree tree = new Tree(doc);
        tree.traverseTreeSynchronously();
        tree.levelOrderTraversal();
    }
}
