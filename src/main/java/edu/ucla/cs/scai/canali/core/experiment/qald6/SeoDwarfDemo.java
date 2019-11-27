/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucla.cs.scai.canali.core.experiment.qald6;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author lucia
 */
public class SeoDwarfDemo {

    public static void main(String[] args) {

        try {
            // File file = new File("./question.txt");

            //BufferedReader br = new BufferedReader(new FileReader(file));
            String st = "What is the propability of POLYGON((625614 4593754,625614 4595344,633035 4578647,623897 4575201,621903 4576792,625614 4593754))?";

            //String st = "What is the max turbidity of Cretan?";
            //while ((st = br.readLine()) != null) {
            System.out.println(st);

            System.out.println("STRING:" + st);
            CanaliW2VQASystem qas = new CanaliW2VQASystem("resource/seodwarf_res/abstract_200_20.w2v.bin", "/home/lucia/Desktop/seodwarf_data_06_11_19/output_mod/index/supportFiles/property_labels");
            //QASystem qas = new CanaliW2VQASystem("/home/lucia/repo/canali-core/resource/seodwarf_res/abstract_200_20.w2v.bin", "/home/lucia/repo/canali-core/resource/seodwarf_res/index/supportFiles/property_labels");

            //System.setProperty("kb.index.dir", "/home/lucia/nlp2sparql-data/dbpedia-processed/2015-10/dbpedia-processed_onlydbo_mini_e/index/"); //!!!
            System.setProperty("kb.index.dir", "/home/lucia/Desktop/seodwarf_data_06_11_19/output_mod/index/processed/");
            //System.setProperty("kb.index.dir", "/home/lucia/repo/canali-core/resource/seodwarf_res/index/processed/");
            ArrayList<String> systAns = new ArrayList<String>();
            systAns = qas.getAnswer(st, null);

            for (String a : systAns) {
                System.out.println("System = " + a);
            }

            JSONObject query = new JSONObject();
            query.put("query", systAns.get(0));

            //Write JSON file
            try (FileWriter file = new FileWriter("./query.json")) {

                file.write(query.toString());
                file.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }

            //}
        } catch (Exception ex) {
            Logger.getLogger(SeoDwarfDemo.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void printQuery(String query) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("./sparql_query.txt"));

            writer.write(query);
            writer.close();

        } catch (IOException ex) {
            Logger.getLogger(SeoDwarfDemo.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
