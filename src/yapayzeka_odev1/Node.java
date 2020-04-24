/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yapayzeka_odev1;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author cihan
 * 
 * Arama islemini daha rahat yapabilmek ve gezilen dugumleri hafizada
 * tutabilmek icin asagidaki sinif olusturulmustur.
 */
public class Node{
        private ArrayList<Long> path;
        private int x;
        private int y;
        private double weight;
        private double totalLength;
        
        public Node(int x, int y, int targetX, int targetY, double redValue, double totalLength, YapayZeka_Odev1.solveType type){    
            this.x = x;
            this.y = y;
            this.totalLength = totalLength + redValue;
            // Kullanilacak algoritmaya gore agirlik hesabi yapiliyor.
            if(type == YapayZeka_Odev1.solveType.USE_ASTAR){
                weight = this.totalLength + (redValue - 256.0)*(double)(Math.abs(x-targetX)+Math.abs(y-targetY));
            }
            else{
                weight = (redValue - 256.0)*(double)(Math.abs(x-targetX)+Math.abs(y-targetY));
                //256.0*(double)(Math.abs(x-targetX)+Math.abs(y-targetY))          
            }
        }
        
        public int getX(){
            return x;
        }
        
        public int getY(){
            return y;
        }
        
        public double getWeight(){
            return weight;
        }
        
        public double getTotalLength(){
            return totalLength;
        }
        
        public ArrayList<Long> getPath(){
            return path;
        }             
        
        public void createPath(Node n){
            path = new ArrayList<>();          
            long adder = x;
            adder = adder << 32;
            adder += y;
            path.add(adder);        
            if(n != null){
                path.addAll(n.getPath());
            }
        }
        
        public int compare(Node n){
            if(n.getWeight() < weight){
                return -1;
            }
            if(n.getWeight() == weight){
                return 0;
            }
            return 1;
        }
    }
