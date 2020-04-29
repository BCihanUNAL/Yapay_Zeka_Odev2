/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yapayzeka_odev2;

import java.util.ArrayList;

/**
 *
 * @author cihan
 * Bireylerin uzerinde daha rahat islem yapabilmek icin asagidaki sinif ve fonksiyonlar olusturulmustur.
 */
public class Birey {
    public int x;
    public int y;
    public int len;
    public int current_dist;
    public int[] path;
    public double mutation_rate;
    public double fitness;
    public boolean has_arrived;
    public double penalty;
   
    
    public Birey(int len, double mutation_rate, boolean isChild){
        x = 1;
        y = 1;        
        this.len = len;
        fitness = 0;
        this.mutation_rate = mutation_rate;
        current_dist = 0;
        penalty = 0.0;     
        path = new int[len];
        has_arrived = false;
        if(!isChild)
            createPath();
    }
    
    private void createPath(){
        for(int i = 0; i < path.length; i++){
            path[i] = 1+(int)(Math.random()*4.0);
        }
    }
}
