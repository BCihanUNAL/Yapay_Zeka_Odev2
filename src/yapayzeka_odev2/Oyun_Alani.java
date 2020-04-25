/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yapayzeka_odev2;

import javafx.scene.shape.Rectangle;
import javafx.application.Application;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;

/**
 *
 * @author cihan
 */
public class Oyun_Alani{   
    public int n;
    public int l;
    public int gameArea[][];
    public Stage stage = null;
    public Pane pane = null;
    public Scene scene = null;
    public Rectangle[][] r;
    public Label mutationRate;
    public Label maxLen;
    public Label numOfIndiv;
    public Label numOfIterations;
    public Label finishLabel;
  
    public Scene getScene(){
        if(scene != null)
            return scene;              
        pane = new Pane();
        scene = new Scene(pane, 1360, 768);
        r = new Rectangle[n+2][n+2];
        for(int i = 0; i < n+2; i++){
            for(int j = 0; j < n+2; j++){
                r[i][j] = new Rectangle(396+j*568/(n+2),100+i*568/(n+2),500/(n+2),500/(n+2));
                if(gameArea[i][j] == 1){
                    r[i][j].setFill(Color.RED);
                }
                else{
                    r[i][j].setFill(Color.WHITE);
                }
                pane.getChildren().add(r[i][j]);
            }
        }
        mutationRate = new Label("Mutasyon Oranı = ?");
        maxLen = new Label("Duvara Çarpma Sayısı = ?");
        numOfIndiv = new Label("Toplam Birey Sayısı = ?");
        numOfIterations = new Label("Toplam İterasyon Sayısı = ?");
        finishLabel = new Label("Bulunan en iyi yol");
        mutationRate.setLayoutX(100);
        mutationRate.setLayoutY(400);
        maxLen.setLayoutX(100);
        maxLen.setLayoutY(450);
        numOfIndiv.setLayoutX(100);
        numOfIndiv.setLayoutY(500);
        numOfIterations.setLayoutX(100);
        numOfIterations.setLayoutY(550);
        finishLabel.setLayoutX(600);
        finishLabel.setLayoutY(50);
        finishLabel.setVisible(false);
        pane.getChildren().addAll(mutationRate, maxLen, numOfIndiv, numOfIterations, finishLabel);
        return scene;
    }
    
    public synchronized void changeColorToBlue(int i, int j){
        r[i][j].setFill(Color.BLUE);
        /*pane.getChildren().remove(r[i][j]);
        pane.getChildren().add(r[i][j]);*/
    }
    
    public synchronized void changeColorToWhite(int i, int j){
        r[i][j].setFill(Color.WHITE);
        /*pane.getChildren().remove(r[i][j]);
        pane.getChildren().add(r[i][j]);*/
    }
    
    public Oyun_Alani(int n, int l){      
        this.n = n;
        this.l = l;
        gameArea = new int[n+2][n+2]; // 0 = bos alan, 1 = engel
        gameArea[0][0] = 1;
        gameArea[n+1][0] = 1;
        gameArea[0][n+1] = 1;
        gameArea[n+1][n+1] = 1;
        for(int i = 1 ; i < n + 1; i++){
            gameArea[0][i] = 1;
            gameArea[n+1][i] = 1;
            gameArea[i][n+1] = 1;
            gameArea[i][0] = 1;
        }
        constructArea();
    }
    
    public synchronized void changeLabels(double mut, int len, int num_indiv, int num_iter){
        mutationRate.setText("Mutasyon Oranı = " + mut);
        maxLen.setText("Duvara Çarpma Sayısı = " + len);
        numOfIndiv.setText("Birey Sayısı = " + num_indiv);
        numOfIterations.setText("İterasyon Sayısı = " + num_iter);
    }
         
    private void constructArea(){
        for(int p = 0; p < l; p++){
            int x = 1 + (int)(Math.random()*(double)(n+1));
            int y = 1 + (int)(Math.random()*(double)(n+1));
            if(x == 1 && y == 1){
                p--;
                continue;
            }
            int side = (int)(Math.random()*2.0); // 0 asagiya dogru, 1 saga dogru
            if(side == 0){
                if(y+3 > n+1 || gameArea[y][x] == 1 || gameArea[y+1][x] == 1 || gameArea[y+2][x] == 1 || gameArea[y+3][x] == 1 || (x == n && y+3 == n)){
                    p--;
                    continue;
                }
                gameArea[y][x] = 1;
                gameArea[y+1][x] = 1;
                gameArea[y+2][x] = 1;
                gameArea[y+3][x] = 1;
            }
            if(side == 1){
                if(x+3 > n+1 || gameArea[y][x] == 1 || gameArea[y][x+1] == 1 || gameArea[y][x+2] == 1 || gameArea[y][x+3] == 1 || (x+3 == n && y == n)){
                    p--;
                    continue;
                }
                gameArea[y][x] = 1;
                gameArea[y][x+1] = 1;
                gameArea[y][x+2] = 1;
                gameArea[y][x+3] = 1;
            }
        }
    }
    
}
