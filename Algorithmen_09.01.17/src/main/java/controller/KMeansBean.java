/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.primefaces.event.FileUploadEvent;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

/**
 *
 * @author 20120101
 */
@Named(value = "kmeansCon")
@SessionScoped
public class KMeansBean implements Serializable {

    private Instances inst = null;
    private List<String> spalten = new ArrayList<>();
    private int clusternum = 2;
    private String output = "";
    private String description;

    public KMeansBean() {
    }

    public List<String> getSpalten() {
        return spalten;
    }

    public void setSpalten(List<String> spalten) {
        this.spalten = spalten;
    }

    public int getClusternum() {
        return clusternum;
    }

    public void setClusternum(int clusternum) {
        this.clusternum = clusternum;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void handleFileUpload(FileUploadEvent event) {
        FacesMessage message = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, message);

        try {
            if (event.getFile().getFileName().endsWith(".arff")) {
                inst = new Instances(new InputStreamReader(event.getFile().getInputstream()));
            } else {
                CSVLoader scv = new CSVLoader();
                scv.setSource(event.getFile().getInputstream());
                inst = scv.getDataSet();
            }
            spalten = new ArrayList<>();
            for (int i = 0; i < inst.firstInstance().numAttributes(); i++) {
                spalten.add(inst.firstInstance().attribute(i).name());
            }
            this.description = this.inst.toSummaryString();
            calculate();
        } catch (IOException ex) {
            System.out.println("Fehler: " + ex);
        }
    }

    public void calculate() {
        SimpleKMeans skm = new SimpleKMeans();
        try {
            skm.setNumClusters(clusternum);
            skm.buildClusterer(inst);
            output = skm.toString();
        } catch (Exception ex) {
            Logger.getLogger(KMeansBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
