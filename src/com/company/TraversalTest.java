package com.company;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class TraversalTest {
    Document doc;

    public TraversalTest(String pathname) {
        try {
            File htmlFile = new File(pathname);
            Scanner sc = new Scanner(htmlFile);
            String data = sc.nextLine();

            doc = Jsoup.parse(data);
            sc.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void testConcurrent() {
        Tree tree = new Tree(doc);
        tree.traverseConcurrently();
    }

    public void testSynchronous() {
        Tree tree = new Tree(doc);
        tree.traverseSynchronously();
    }
}
