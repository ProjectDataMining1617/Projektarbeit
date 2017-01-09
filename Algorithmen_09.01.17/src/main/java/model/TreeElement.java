/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;

/**
 *
 * @author 20110404
 */
public class TreeElement implements Serializable{
    String ID;
    String Label;
    int Level = 0;
    double position = 0;

    public TreeElement(String ID, String Label) {
        this.ID = ID;
        this.Label = Label;
    }

    @Override
    public String toString() {
        return Label;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getLabel() {
        return Label;
    }

    public void setLabel(String Label) {
        this.Label = Label;
    }

    public int getLevel() {
        return Level;
    }

    public void setLevel(int Level) {
        this.Level = Level;
    }

    public double getPosition() {
        return position;
    }

    public void setPosition(double position) {
        this.position = position;
    }

    
    
    
}
