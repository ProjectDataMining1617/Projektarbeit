/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import model.TreeConector;
import model.TreeElement;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.diagram.ConnectEvent;
import org.primefaces.model.diagram.DefaultDiagramModel;
import org.primefaces.model.diagram.Element;
import org.primefaces.model.diagram.Connection;
import org.primefaces.model.diagram.connector.StraightConnector;
import org.primefaces.model.diagram.endpoint.BlankEndPoint;
import org.primefaces.model.diagram.endpoint.DotEndPoint;
import org.primefaces.model.diagram.endpoint.EndPoint;
import org.primefaces.model.diagram.endpoint.EndPointAnchor;
import org.primefaces.model.diagram.overlay.LabelOverlay;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

/**
 *
 * @author 20120101
 */
@Named(value = "decisionTreeBean")
@SessionScoped
public class DecisionTreeBean implements Serializable {
    private DefaultDiagramModel model;
    private List<TreeElement> liNodeElement;
    private List<TreeConector> liConnElement;
    private Instances inst;
    private List<String> spalten;
    private String selectedspalte;
    private String dis;
    
    @PostConstruct
    public void init() {
        dis = "";
        selectedspalte = " ";
        spalten = new LinkedList<>();
        inst = null;
    }

    public DefaultDiagramModel getModel() {
        return model;
    }

    public void setModel(DefaultDiagramModel model) {
        this.model = model;
    }

    public Instances getInst() {
        return inst;
    }

    public void setInst(Instances inst) {
        this.inst = inst;
    }

    public List<String> getSpalten() {
        return spalten;
    }

    public void setSpalten(List<String> spalten) {
        this.spalten = spalten;
    }

    public String getSelectedspalte() {
        return selectedspalte;
    }

    public void setSelectedspalte(String selectedspalte) {
        this.selectedspalte = selectedspalte;
    }

    public String getDis() {
        return dis;
    }

    public void setDis(String dis) {
        this.dis = dis;
    }
    
     public void generateTree(int positiontomine) {
        System.out.println("Start Generating Tree");
        model = new DefaultDiagramModel();
        model.setMaxConnections(-1);
        try {
            inst.setClassIndex(positiontomine);
            J48 tree;
            String[] options = new String[1];
            options[0] = "-U";
            tree = new J48();
            tree.setOptions(options);     // set the options
            tree.buildClassifier(inst);   // build classifier
            dis = inst.toSummaryString();
            System.out.println(tree.graph());
            System.out.println(tree.toString());
            List<String> liNodeStr = new LinkedList<>();
            List<String> liConnStr = new LinkedList<>();

            liNodeElement = new LinkedList<>();
            liConnElement = new LinkedList<>();

            BufferedReader br = new BufferedReader(new StringReader(tree.graph()));
            br.readLine();
            String tmp;
            while ((tmp = br.readLine()) != null) {
                if (tmp.contains("}")) {
                    break;
                } else if (tmp.contains("->")) {
                    liConnStr.add(tmp);
                } else {
                    liNodeStr.add(tmp);
                }
            }

            System.out.println(liConnStr);
            System.out.println(liNodeStr);

            for (String s : liNodeStr) {
                String[] arr = s.split(" ");
                String entitie1 = arr[0];
                arr = s.split("\"");
                String entitie2 = arr[1];

                System.out.println("ID:" + entitie1 + " Name:" + entitie2);
                TreeElement te = new TreeElement(entitie1, entitie2);
                liNodeElement.add(te);
            }

            for (String s : liConnStr) {
                String[] arr = s.split(" ");
                arr = arr[0].split("->");
                String from = arr[0];
                String to = arr[1];

                arr = s.split("\"");
                String label = arr[1];

                System.out.println("From:" + from + " To:" + to + "Label:" + label);
                TreeConector ce = new TreeConector(from, to, label);
                liConnElement.add(ce);
            }

            //-----------------------------------------------------------------------------------------------
            for (TreeElement te : liNodeElement) {
                if (te.getID().equals("N0")) {
                    System.out.println("inside");
                    genlevel(te, 0);
                    te.setPosition(25);
                    genposition(te, 50);
                }
            }

            for (TreeElement te : liNodeElement) {
                Element el = new Element(te, te.getPosition() + "em", te.getLevel() * 15 + "em");
                el.addEndPoint(new BlankEndPoint(EndPointAnchor.TOP));
                el.addEndPoint(new BlankEndPoint(EndPointAnchor.BOTTOM));
                model.addElement(el);
            }
            List<Element> ellist = model.getElements();
            for (TreeConector tc : liConnElement) {
                Element beginn = null;
                for (Element e : ellist) {
                    TreeElement t;
                    t = (TreeElement) e.getData();
                    if (t.getID().equals(tc.getFrom())) {
                        beginn = e;
                        break;
                    }
                }
                Element endeee = null;
                for (Element e : ellist) {
                    TreeElement t;
                    t = (TreeElement) e.getData();
                    if (t.getID().equals(tc.getTo())) {
                        endeee = e;
                        break;
                    }
                }

                StraightConnector connector = new StraightConnector();

                connector.setPaintStyle("{strokeStyle:'#F28D2A', lineWidth:3}");
                connector.setHoverPaintStyle("{strokeStyle:'#F28D2A'}");
                Connection con = new Connection(beginn.getEndPoints().get(1), endeee.getEndPoints().get(0), connector);

                con.getOverlays().add(new LabelOverlay(tc.getLabel(), "flow-label", 0.5));
                model.connect(con);
            }

        } catch (Exception ex) {
            Logger.getLogger(DecisionTreeBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
     
    private EndPoint createEndPoint(EndPointAnchor anchor) {
        DotEndPoint endPoint = new DotEndPoint(anchor);
        endPoint.setStyle("{fillStyle:'#404a4e'}");
        endPoint.setHoverStyle("{fillStyle:'#20282b'}");

        return endPoint;
    }
    
    public void onSelect(ConnectEvent event) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Selected",
                "From " + event.getSourceElement().getData() + " To " + event.getTargetElement().getData());

        FacesContext.getCurrentInstance().addMessage(null, msg);
        RequestContext.getCurrentInstance().update("form:messages");
    }

    public void genlevel(TreeElement te, int akt) {
        List<TreeElement> tt = new LinkedList<>();
        for (TreeConector tc : liConnElement) {
            if (tc.getFrom().equals(te.getID())) {
                for (TreeElement zee : liNodeElement) {
                    if (zee.getID().equals(tc.getTo())) {
                        zee.setLevel(akt + 1);
                        System.out.println(zee.getID() + ": " + akt);
                        genlevel(zee, akt + 1);
                    }
                }
            }
        }
    }

    public void genposition(TreeElement te, double width) {
        int anzahl = 0;
        for (TreeConector tc : liConnElement) {
            if (tc.getFrom().equals(te.getID())) {
                anzahl++;
            }
        }
        if (anzahl != 0) {

            double start = te.getPosition() - (width / 2);
            double sprung = (width / anzahl);
            start += sprung / 2;
            for (TreeConector tc : liConnElement) {
                if (tc.getFrom().equals(te.getID())) {
                    for (TreeElement zee : liNodeElement) {
                        if (zee.getID().equals(tc.getTo())) {
                            zee.setPosition(start);
                            start += sprung;
                            genposition(zee, sprung);

                        }
                    }
                }
            }
        }
    }

    public void handleFileUpload(FileUploadEvent event) {
        FacesMessage message = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, message);

        try {
            boolean aarf = false;
            if (event.getFile().getFileName().endsWith(".arff")) {
                inst = new Instances(new InputStreamReader(event.getFile().getInputstream()));
            } else {
                CSVLoader scv = new CSVLoader();
                scv.setSource(event.getFile().getInputstream());
                inst = scv.getDataSet();
            }
            spalten = new LinkedList<>();
            for (int i = 0; i < inst.firstInstance().numAttributes(); i++) {
                spalten.add(inst.firstInstance().attribute(i).name());
            }
            System.out.println(spalten);
            generateTree(0);
        } catch (IOException ex) {
            Logger.getLogger(DecisionTreeBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void onSelectChange() {
        System.out.println("Dropdown");
        int id = spalten.indexOf(selectedspalte);
        generateTree(id);
    }
}
