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
public class TreeConector implements Serializable{
    String from;
    String to;
    String Label;

    public TreeConector(String from, String to, String Label) {
        this.from = from;
        this.to = to;
        this.Label = Label;
    }

    @Override
    public String toString() {
        return Label;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getLabel() {
        return Label;
    }
    
    
    
}
