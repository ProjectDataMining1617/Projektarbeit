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
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.primefaces.event.FileUploadEvent;
import weka.classifiers.rules.OneR;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

/**
 *
 * @author 20120101
 */
@Named(value = "onerBean")
@SessionScoped
public class OneRBean implements Serializable {

    private List upload;
    private String output = "";
    private String column = "";
    private List<String> columns;
    private String description;

    Instances inst = null;

    @PostConstruct
    public void init() {
        columns.add("Keine Spalten verfügbar!");
    }

    public OneRBean() {
    }

    public List getUpload() {
        return upload;
    }

    public void setUpload(List upload) {
        this.upload = upload;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
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
            columns = new ArrayList<>();
            for (int i = 0; i < inst.firstInstance().numAttributes(); i++) {
                columns.add(inst.firstInstance().attribute(i).name());
            }
            System.out.println(columns);
        } catch (IOException ex) {
            System.out.println("Fehler: " + ex);
        }
        this.description = this.inst.toSummaryString();
        this.calculate();
    }

    public void calculate() {
        output = column;
        int index = columns.indexOf(column);
        OneR one = new OneR();
        inst.setClassIndex(index);
        inst.firstInstance();
        try {
            one.buildClassifier(inst);
            output = one.toString();// + "<br /> asdfasdfasdf";
        } catch (Exception ex) {
            Logger.getLogger(OneRBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
