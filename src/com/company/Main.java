package com.company;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            File htmlFile = new File("four-core-html.txt");
            Scanner sc = new Scanner(htmlFile);
            String data = sc.nextLine();

            Document doc = Jsoup.parse(data);

            Tree tree = new Tree(doc);
//            tree.traverseConcurrently();
            tree.traverseTreeSynchronously();
            sc.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }


    }
}
