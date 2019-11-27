/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.nlpita.index.utils;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.jena.ext.com.google.common.io.Files;
import org.apache.jena.graph.Triple;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.ProfileRegistry;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.rdf.model.impl.StatementImpl;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

/**
 *
 * @author enrico coppolecchia
 */
public class SeoDwarfTripleUtil {

    String sourcePath;
    String outputPath;
    OntModel seodwarfModel = ModelFactory.createOntologyModel(ProfileRegistry.OWL_DL_LANG);
    int count =0;

    public SeoDwarfTripleUtil(String sourceDir, String destDir) {
        sourcePath = sourceDir;
        outputPath = destDir;
    }

    public void updateTriples() throws FileNotFoundException {
        File folder = new File(sourcePath);
        
        for (File file : folder.listFiles()) {
            FileInputStream fr = new FileInputStream(file);
            seodwarfModel.removeAll();
            seodwarfModel.read(fr, "RDF/XML");

            System.out.println("Reading file: " + file.getName());
            if (Files.getFileExtension(file.getAbsolutePath()).equals("rdf")) {
                modifyRDF();
            } else if (Files.getFileExtension(file.getAbsolutePath()).equals("owl")) {
                modifyOWL();
            }
            seodwarfModel.write(new PrintWriter(new FileOutputStream(new File(outputPath + file.getName()))));
        }
    }

  public void modifyRDF (){
        HashMap<Integer,LinkedList<Triple>> map = new HashMap<Integer,LinkedList<Triple>>();
        List<Statement> newLabelStatements = new ArrayList<>();
        List<Statement> removeStatements = new ArrayList<>();
        for (StmtIterator prop = seodwarfModel.listStatements();prop.hasNext();){
            Statement pr = prop.next();
            if (pr.getPredicate().toString().contains("hasIdentifier")){
                Statement newst = ResourceFactory.createStatement(pr.getSubject(), RDFS.label, pr.getObject());
                newLabelStatements.add(newst);
            }
            if (pr.getObject().toString().equals("")){
                Statement newst = ResourceFactory.createStatement(pr.getSubject(), pr.getPredicate(), pr.getObject());
                removeStatements.add(newst);
            }
            if(pr.asTriple().getObject().isBlank()){
                count++;
                if (count==34) {
                int here = 5;
                }
                System.out.println(pr.asTriple().getObject().toString());

                LinkedList<Triple> list = new LinkedList<Triple>();

                
                for(StmtIterator p = seodwarfModel.listStatements((Resource) pr.getObject(), null, (RDFNode)null);p.hasNext();){
                    Statement proper = p.next();
                    list.add(proper.asTriple());
                    System.out.println("---->"+proper.asTriple());
                 
                }
                map.put(count,list);
                
            }
        }
        
        seodwarfModel.add(newLabelStatements);
        seodwarfModel.remove(removeStatements);
        for(Integer i : map.keySet()){
            
            Resource nuova;
            
            nuova = seodwarfModel.createResource("http://seodwarf.eu/ontology/v1.0#"+"POLY00"+ i);
            
           
            map.get(i).forEach((s) -> {
                if(s.toString().contains("hasPhenomenonCoverage")){
                    nuova.addProperty(RDFS.label, filter(s.getObject().toString()));
                }
                else{
                    
                    String uri =s.getPredicate().getURI().split("#")[0];
                    String name = s.getPredicate().getURI().split("#")[1];
                    
                   
                        seodwarfModel.add(nuova.asResource(),seodwarfModel.getProperty(uri, name), s.getObject().toString().replaceAll("\"", ""));
                        System.out.println(nuova+"->>>"+nuova.getProperty(seodwarfModel.getProperty(uri, name)));
                   
                }
            });
            
            System.out.println(nuova+"->>>"+nuova.getProperty(RDFS.label));
            
            
        }
        
        System.out.println("-------------------------------------------------------------------------------------------------------------");
    }

    private void modifyOWL() {
        List<Statement> labelStatements = new ArrayList<>();
        for (StmtIterator prop = seodwarfModel.listStatements(); prop.hasNext();) {
            Statement st = prop.next();
            String subj = st.getSubject().toString();
            String obj = st.getObject().toString();

            //System.out.println("subj: " + st.getSubject().toString() + " prop: " + st.getPredicate().toString() + " obj: " + st.getObject().toString());
            if (st.getPredicate().equals(RDF.type) && !(st.getSubject().isAnon())) {
                if (obj.contains("ObjectProperty") || obj.contains("DatatypeProperty") || obj.contains("Class")) {
                    //if (st.getObject().toString().equals(st))
                    System.out.println("***subj: " + st.getSubject().toString() + " prop: " + st.getPredicate().toString() + " obj: " + st.getObject().toString());
                    String[] split = subj.split("/|#");

                    String name = split[split.length - 1];
                    System.out.println("name: " + name);
                    Pattern p = Pattern.compile("[A-Z][a-z]+|[A-Z]+(?![a-z]+)");
                    Matcher m = p.matcher(name);

                    ArrayList<String> allMatches = new ArrayList<String>();
                    while (m.find()) {
                        allMatches.add(m.group());
                    }

                    String labelString = "";
                    for (String s : allMatches) {
                        System.out.println("s: " + s);
                        labelString += " " + s;
                    }
                    labelString = labelString.trim().toLowerCase();
                    Literal label = seodwarfModel.createLiteral( labelString, "en" );

                    System.out.println("label: " + labelString);
                    Statement s = ResourceFactory.createStatement(st.getSubject(), RDFS.label, label);
                    labelStatements.add(s);
                }
            }

        }
        seodwarfModel.add(labelStatements);
    }

    public int navigate(int count, OntModel model) {
        HashMap<Integer, LinkedList<Triple>> map = new HashMap<Integer, LinkedList<Triple>>();
        for (StmtIterator prop = model.listStatements(); prop.hasNext();) {
            Statement pr = prop.next();
            if (pr.asTriple().getObject().isBlank()) {

                count++;
                System.out.println(pr.asTriple().getObject().toString());

                LinkedList<Triple> list = new LinkedList<Triple>();

                for (StmtIterator p = model.listStatements((Resource) pr.getObject(), null, (RDFNode) null); p.hasNext();) {
                    Statement proper = p.next();
                    list.add(proper.asTriple());
                    System.out.println("---->" + proper.asTriple());

                }
                map.put(count, list);

            }
        }
        for (Integer i : map.keySet()) {

            Resource nuova;

            nuova = model.createResource("http://seodwarf.eu/ontology/v1.0#" + "POLY00" + i);

            map.get(i).forEach((s) -> {
                if (s.toString().contains("hasPhenomenonCoverage")) {
                    nuova.addProperty(RDFS.label, filter(s.getObject().toString()));
                } else {

                    String uri = s.getPredicate().getURI().split("#")[0];
                    String name = s.getPredicate().getURI().split("#")[1];

                    model.add(nuova.asResource(), model.getProperty(uri, name), s.getObject().toString().replaceAll("\"", ""));
                    System.out.println(nuova + "->>>" + nuova.getProperty(model.getProperty(uri, name)));
                }
            });

            System.out.println(nuova + "->>>" + nuova.getProperty(RDFS.label));

        }

        System.out.println("-------------------------------------------------------------------------------------------------------------");
        return count;
    }

    public String filter(String s) {
        return s.substring(0, s.indexOf("^^")).replaceAll("\"", "");
    }

    public static void main(String[] args) {
        try {
            SeoDwarfTripleUtil s = new SeoDwarfTripleUtil("/home/lucia/Desktop/seodwarf_data_06_11_19/ontology/", "/home/lucia/Desktop/seodwarf_data_06_11_19/ontology_mod/");
            s.updateTriples();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
