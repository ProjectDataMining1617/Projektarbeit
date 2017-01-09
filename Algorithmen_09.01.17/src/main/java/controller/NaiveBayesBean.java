/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.primefaces.event.FileUploadEvent;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

/**
 *
 * @author 20120101
 */
@Named(value = "naiveBayesBean")
@SessionScoped
public class NaiveBayesBean implements Serializable{
    private Instances data;
    private NaiveBayes classifier;
    private String description;
    private Integer index;
    private List<Attribute> attributes;
    private String model;
    private String modeloutput="Bitte Spalte auswählen";
    
    public String getModeloutput() {
        return modeloutput;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    public Instances getData() {
        return data;
    }

    
    public void setData(Instances data) {
        this.data = data;
    }

    public NaiveBayes getClassifier() {
        return classifier;
    }

    public void setClassifier(NaiveBayes classifier) {
        this.classifier = classifier;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
        indexChange();
    }
    
    public NaiveBayesBean() {
        this.classifier = new NaiveBayes();
        this.attributes = new LinkedList<>();
    }
    
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
    
    public void handleFileUpload(FileUploadEvent event) throws FileNotFoundException, IOException, Exception {
        System.out.println("File uploaded!");
        System.out.println(event.getFile().getContentType());
        //this.data = new Instances(new FileReader("web/resources/data/weather.nominal.arff"));
        
        //Daten als Instanzen aufbereiten
        //CSV Converter
        if(event.getFile().getFileName().endsWith(".csv")){
            CSVLoader csv = new CSVLoader();
            
            BufferedReader br = new BufferedReader(new InputStreamReader(event.getFile().getInputstream()));
            String s;
            StringBuilder sb = new StringBuilder();
            while ((s = br.readLine()) != null) {
                sb.append(s.replace(";", ","));
                //sb.append(s.replace("\"", ""));
                sb.append("\n");
                System.out.println(sb.toString());
            }
            csv.setSource(new ByteArrayInputStream(sb.toString().getBytes()));
            this.data = csv.getDataSet();
        }else{
            this.data = new Instances(new InputStreamReader(event.getFile().getInputstream()));
        }
        
        this.attributes.clear();
        //Attribute auslesen und in Bean bereitstellen
        for (int i = 0; i < this.data.numAttributes(); i++) {
            this.attributes.add(data.attribute(i));
        }
        //Meta-Daten der hochgeladenen Daten bereitstellen
        this.description = this.data.toSummaryString();
        //Klasse als letzte Spalte annehmen
        this.data.setClassIndex(this.data.numAttributes()-1);
        this.index = this.data.classAttribute().index();
        
        //Daten analysieren
        this.classifier.buildClassifier(this.data);
        //Text im Interface setzen
        this.modeloutput = "Model für Klasse " + this.data.classAttribute().name();
        this.model = this.classifier.toString();
    }
    
    public void indexChange(){
        this.data.setClassIndex(this.index);
        try {
            analyse();
        } catch (Exception ex) {
            Logger.getLogger(NaiveBayesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void analyse() throws Exception{
        this.classifier.buildClassifier(data);
        //Text im Interface setzen
        this.modeloutput = "Model für Klasse " + this.data.classAttribute().name();
        this.model = this.classifier.toString();
    }
}
