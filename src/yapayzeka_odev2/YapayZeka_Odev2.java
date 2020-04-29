/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yapayzeka_odev2;

import java.util.Arrays;
import java.util.Comparator;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 *
 * @author cihan
 */
public class YapayZeka_Odev2 extends Application{

    /**
     * @param args the command line arguments
     */
    
    private int num_of_individuals;
    private int max_len;
    private int num_of_iterations;
    private int n;
    private int l;
    private int min_path_len;
    private int min_manhattan_len;
    private Birey min_indiv;
    private Birey individuals[];
    private double mutation_rate;
    private Oyun_Alani a;
    private Stage s;
    private static Thread t1 = null;
    public static void main(String[] args) {
        // TODO code application logic here       
        Application.launch(args);        
        if(t1 != null){
            t1.stop();
        }
    }
    
    @Override
    public void start(Stage stage){
        showFirstScreen();       
    }
    
    private void showMainScreen(){
        //ilk olarak gerekli parametreler, degiskenler ve oyun alani olusturuluyor.
        min_path_len = 0x7FFFFFFF;
        min_manhattan_len = 0x7FFFFFFF;
        a = new Oyun_Alani(n,l);
        s.setScene(a.getScene());
        s.show();
        individuals = new Birey[num_of_individuals]; 
        t1 = new Thread(()->{
        for(int i = 0; i < individuals.length; i++)
            individuals[i] = new Birey(max_len, mutation_rate, false);               
        
        for(int i = 0; i < num_of_iterations; i++){
            //Belirlenen iterasyon sayisi boyunca yeni bireyler olusturulyor.
            double total_fitness = 0.0;
            double min_fitness = Double.MAX_VALUE;
            
            for(int j = 0; j < individuals.length; j++){ // hareket et ve arayuzu guncelle
                for(int k = 0; k < max_len && move(individuals[j], a, individuals[j].path[k], false); k++);
                if(!individuals[j].has_arrived){
                    individuals[j].fitness = 3.0 * (double)(individuals[j].x - 1 + individuals[j].y - 1) /
                         (double)(n + 1 - individuals[j].x + n + 1 - individuals[j].y) - 2.0 * individuals[j].penalty;//birey cikisa ne kadar yakinsa, ne kadar az duvara carpmis ve gectigi yollardan tekrar gecmemisse o kadar uyumlu
                    if(individuals[j].fitness < min_fitness)
                        min_fitness = individuals[j].fitness; 
                }
                else{
                    individuals[j].fitness = 4.0 * (double)(individuals[j].x - 1 + individuals[j].y - 1) - individuals[j].penalty; 
                    //gidecegi yere ulasmissa cezanin etkisini azaltip gelinen yolun etkisini arttiriyorum.
                    //System.out.println("Bulundu");
                }
                
            }
            
            for(int j = 0; j < individuals.length; j++){
                if(individuals[j].fitness == Double.MAX_VALUE)
                    continue;
                individuals[j].fitness -= min_fitness;
                total_fitness += individuals[j].fitness;
            }
            
            //Bireyler fitness degerlerine gore siralaniyor
            Arrays.sort(individuals, new Comparator<Birey>(){
                @Override
                public int compare(Birey b1, Birey b2){   
                    Double val1 = b1.fitness, val2 = b2.fitness;
                    return val2.compareTo(val1);
                }      
            });
            
           /* for(int j = 0; j < individuals.length; j++){
                for(int k = 0; k < individuals.length - 1; k++){
                    if(individuals[k].fitness > individuals[k + 1].fitness){
                        swap(individuals, k, k+1);
                    }
                }
            }*/
            
           
            //En iyi birey arayuzde gosteriliyor ve parametreler guncelleniyor.
            drawBestIndividual(individuals[individuals.length - 1], a);
            changeLabels(individuals[individuals.length - 1].current_dist, i);
            
            Birey new_individuals[] = new Birey[individuals.length];
            
            for(int j = 0; j < individuals.length; j++){ // Yeni bireyler olusturuluyor.
                double chance1 = Math.random() * total_fitness, chance2 = Math.random() * total_fitness, inc = 0.0;
                Birey parent1 = null, parent2 = null;
                for(int k = individuals.length - 1; k >= 0; k--){ // Secim yapiliyor.
                    inc += individuals[k].fitness;
                    if(inc >= chance1){
                        parent1 = individuals[k];
                        break;
                    }
                }
                inc = 0.0;
                for(int k = individuals.length - 1; k >= 0; k--){
                    inc += individuals[k].fitness;
                    if(inc >= chance2){
                        parent2 = individuals[k];
                        break;
                    }
                }
                new_individuals[j] = getChild(parent1, parent2); // cross-over yapılıp olusturulan yeni birey dizisine koyuluyor.
                for(int m = 0; m < new_individuals[j].len; m++){
                if(Math.random() < new_individuals[j].mutation_rate){ // mutasyon yapiliyor.                  
                    int incr = 1+(int)(Math.random() * 3.0);
                    new_individuals[j].path[m] = (new_individuals[j].path[m] + incr)%4;
                    if(new_individuals[j].path[m] == 0)
                        new_individuals[j].path[m] = 4;
                }
                }
            }
            //olusturulan bireyler bir sonraki iterasyonda kullanilmak uzere saklaniyor.
            individuals = new_individuals;
            
        }
        //Algoritma calistiktan sonra bulunan en iyi birey arayuzde gosteriliyor.
        drawBestIndividual(min_indiv,a);
        a.finishLabel.setVisible(true);
    });
    t1.start();
    }
    
    private void showFirstScreen(){
        //Uygulamanin ilk ekraninda kullanicidan hiperparametreler isteniyor. Eğer bu kisim bos birakilirsa onceden belirlenen parametreler kullanilacak.
        s = new Stage();
        s.setTitle("Yapay_Zeka_2.Odev");
        Pane pane = new Pane();
        
        Label bilgiLabel = new Label("Kutuları Boş Bırakmanız Halinde Önceden Belirlenmiş Parametreler Kullanılacaktır.");
        Label haritaUzunlukLabel = new Label("Lütfen haritanın uzunluğunu giriniz");
        Label engelLabel = new Label("Lütfen haritada kaç engel olması gerektiğini giriniz");
        Label bireyUzunlukLabel = new Label("Lütfen bireylerin maksimum uzunluğunu giriniz");
        Label mutasyonLabel = new Label("Lütfen mutasyon oranını giriniz");
        Label iterLabel = new Label("Lütfen iterasyon sayısını giriniz");
        Label bireySayiLabel = new Label("Lütfen popülasyondaki birey sayısını giriniz");
        
        bilgiLabel.setLayoutX(360);
        bilgiLabel.setLayoutY(50);
        haritaUzunlukLabel.setLayoutX(100);
        haritaUzunlukLabel.setLayoutY(125);
        engelLabel.setLayoutX(100);
        engelLabel.setLayoutY(200);
        bireyUzunlukLabel.setLayoutX(100);
        bireyUzunlukLabel.setLayoutY(275);
        mutasyonLabel.setLayoutX(100);
        mutasyonLabel.setLayoutY(350);
        iterLabel.setLayoutX(100);
        iterLabel.setLayoutY(425);
        bireySayiLabel.setLayoutX(100);
        bireySayiLabel.setLayoutY(500);
        pane.getChildren().addAll(haritaUzunlukLabel, engelLabel, bireyUzunlukLabel, mutasyonLabel, iterLabel, bireySayiLabel, bilgiLabel);
        
        TextArea haritaUzunlukTextArea = new TextArea();
        TextArea engelTextArea = new TextArea();
        TextArea bireyUzunlukTextArea = new TextArea();
        TextArea mutasyonTextArea = new TextArea();
        TextArea iterTextArea = new TextArea();
        TextArea bireySayiTextArea = new TextArea();
        
        haritaUzunlukTextArea.setPrefColumnCount(20);
        haritaUzunlukTextArea.setPrefRowCount(1);
        engelTextArea.setPrefColumnCount(20);
        engelTextArea.setPrefRowCount(1);
        bireyUzunlukTextArea.setPrefColumnCount(20);
        bireyUzunlukTextArea.setPrefRowCount(1);
        mutasyonTextArea.setPrefColumnCount(20);
        mutasyonTextArea.setPrefRowCount(1);
        iterTextArea.setPrefColumnCount(20);
        iterTextArea.setPrefRowCount(1);
        bireySayiTextArea.setPrefColumnCount(20);
        bireySayiTextArea.setPrefRowCount(1);
        
        haritaUzunlukTextArea.setLayoutX(780);
        haritaUzunlukTextArea.setLayoutY(100);
        engelTextArea.setLayoutX(780);
        engelTextArea.setLayoutY(175);
        bireyUzunlukTextArea.setLayoutX(780);
        bireyUzunlukTextArea.setLayoutY(250);
        mutasyonTextArea.setLayoutX(780);
        mutasyonTextArea.setLayoutY(325);
        iterTextArea.setLayoutX(780);
        iterTextArea.setLayoutY(400);
        bireySayiTextArea.setLayoutX(780);
        bireySayiTextArea.setLayoutY(475);
        pane.getChildren().addAll(haritaUzunlukTextArea, engelTextArea, bireyUzunlukTextArea, mutasyonTextArea, iterTextArea, bireySayiTextArea);
        
        Button skipPage = new Button("Algoritmayı Çalıştır");
        skipPage.setLayoutX(1200);
        skipPage.setLayoutY(720);
        pane.getChildren().add(skipPage);
        
        skipPage.setOnMouseClicked(eh->{
            if(haritaUzunlukTextArea.getText().length() == 0){
                n = 100; 
            }
            else{
                n = Integer.parseInt(haritaUzunlukTextArea.getText());
            }
            if(engelTextArea.getText().length() == 0){
                l = 100;
            }
            else{
                l = Integer.parseInt(engelTextArea.getText());
            }
            if(bireyUzunlukTextArea.getText().length() == 0){
                max_len = n*n / 8;
            }
            else{
                max_len = Integer.parseInt(bireyUzunlukTextArea.getText());
            }
            if(mutasyonTextArea.getText().length() == 0){
                mutation_rate = 1/(2.0*(double)n);
            }
            else{
                mutation_rate = Double.parseDouble(mutasyonTextArea.getText());
            }
            if(iterTextArea.getText().length() == 0){
                num_of_iterations = 5000;
            }
            else{
                num_of_iterations = Integer.parseInt(iterTextArea.getText());
            }
            if(bireySayiTextArea.getText().length() == 0){
                num_of_individuals = 200;     
            }
            else{
                num_of_individuals = Integer.parseInt(bireySayiTextArea.getText());
            }
            showMainScreen();
        });        
        
        Scene scene = new Scene(pane, 1360, 768);
        s.setScene(scene);
        s.show();
    }
    
    private void changeLabels(int current_dist, int i){       
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                a.changeLabels(mutation_rate, max_len - current_dist, num_of_individuals, i);
            }
        });
    }   
    
    private void changeColorToWhite(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for(int i = 1; i < n+1; i++){
                    for(int j = 1; j < n+1; j++){
                        if(a.gameArea[i][j] == 0)
                            a.changeColorToWhite(i, j);
                    }
                }
              
            }
        });
    }
    
    private void changeColorToBlue(int path[]){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
              int i=1, j=1;
              for(int k = 0; k < path.length; k++){
                a.changeColorToBlue(i, j);
                switch(path[k]){
                    case 1:
                        if(a.gameArea[i][j-1]!=1)
                            j--;
                        break;
                    case 2:
                        if(a.gameArea[i-1][j]!=1)
                            i--;
                        break;
                    case 3:
                        if(a.gameArea[i][j+1]!=1)
                            j++;
                        break;
                    case 4:
                        if(a.gameArea[i+1][j]!=1)
                            i++;
                        break;
                }
              }
            }
        });
    }
    
    public void drawBestIndividual(Birey  b, Oyun_Alani a){
        changeColorToWhite();
        changeColorToBlue(b.path);
        try{
            Thread.sleep(10);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public Birey getChild(Birey ana, Birey baba){
        Birey bala = new Birey(ana.len, ana.mutation_rate, true);
        int ayir = (int)(Math.random()*(double)bala.len);
        for(int i = 0; i < ayir; i++){
            bala.path[i] = ana.path[i];
        }
        for(int i = ayir; i < bala.len; i++){
            bala.path[i] = baba.path[i];
        }
        /*int flag[] = new int[bala.len];
        for(int i = 0; i < flag.length; i++){
            int sec = (int)(Math.random()*2.0);
            if(sec == 1){
                bala.path[i] = baba.path[i];
            }
            else{
                bala.path[i] = ana.path[i];
            }
        }*/
        return bala;
    }
    
    public void swap(Birey b[], int i, int j){
        Birey h = b[i];
        b[i] = b[j];
        b[j] = h;
    }
    
    public boolean move(Birey b,Oyun_Alani a,int direction, boolean changePaint){   
        long l;
        
        if(changePaint){
                    //changeColorToBlue(b.y, b.x);
        }
        switch(direction){
            case 1:
                if(a.gameArea[b.y][b.x-1] == 1){   
                   b.penalty+=10.0;
                   return true; 
                }
                b.x--;              
                if(a.r[b.y][b.x].getFill()==Color.BLUE)
                    b.penalty+=3.0;
                else
                    b.penalty++;   
                b.current_dist++;
                if(2*n-b.x-b.y < min_manhattan_len){
                    min_manhattan_len = 2*n-b.x-b.y;
                    min_indiv = b;
                }
                return true;
            case 2:
                if(a.gameArea[b.y-1][b.x] == 1){        
                   b.penalty+=10.0;
                   return true;
                }
                b.y--;               
                if(a.r[b.y][b.x].getFill()==Color.BLUE)
                    b.penalty+=3.0;
                else
                    b.penalty++;               
                b.current_dist++;
                if(2*n-b.x-b.y < min_manhattan_len){
                    min_manhattan_len = 2*n-b.x-b.y;
                    min_indiv = b;
                }
                return true;
            case 3:
                if(a.gameArea[b.y][b.x+1] == 1){
                   b.penalty+=10.0;
                   return true;
                }
                b.x++;           
                if(a.r[b.y][b.x].getFill()==Color.BLUE)
                    b.penalty+=3.0;
                else
                    b.penalty++;                
                b.current_dist++;
                if(2*n-b.x-b.y < min_manhattan_len){
                    min_manhattan_len = 2*n-b.x-b.y;
                    min_indiv = b;
                }
                if(b.y == n && b.x == n && !b.has_arrived){
                    b.has_arrived = true;
                    if(min_manhattan_len > 0 || b.current_dist < min_path_len){
                        min_path_len = b.current_dist;
                        min_indiv = b;
                    }
                }
                return true;
            case 4:
                if(a.gameArea[b.y+1][b.x] == 1){
                   b.penalty+=3.0;
                   return true;
                }
                b.y++;            
                if(a.r[b.y][b.x].getFill()==Color.BLUE)
                    b.penalty++;
                else
                    b.penalty++;                   
                
                b.current_dist++;
                if(2*n-b.x-b.y < min_manhattan_len){
                    min_manhattan_len = 2*n-b.x-b.y;
                    min_indiv = b;
                }
                if(b.y == n && b.x == n && !b.has_arrived){
                    b.has_arrived = true;
                    if(min_manhattan_len > 0 || b.current_dist < min_path_len){
                        min_path_len = b.current_dist;
                        min_indiv = b;
                    }
                }
                return true;
        }
        return false;
    }
}
