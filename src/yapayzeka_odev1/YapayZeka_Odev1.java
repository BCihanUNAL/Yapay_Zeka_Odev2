/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yapayzeka_odev1;


import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JLabel;
/**
 *
 * @author cihan
 */
public class YapayZeka_Odev1 extends JApplet{
    public static enum solveType{USE_ASTAR, USE_BEST_FIRST};
    private static BufferedImage img;
    /**
     * Resmin okunmasi ve ekrana bastirilmasi icin awt ve swing kutuphanelerindeki gerekli siniflar kullanilmistir.
     */
    public static void main(String[] args){
        img = null;
        try{
            System.out.println("Lütfen İncelemek istediğiniz resmin bulunduğu adresi yazınız.");
            Scanner s = new Scanner(System.in);
            String file_path = s.nextLine();
            img = ImageIO.read(new File(file_path));
            double img_array[][] = new double[img.getHeight()][img.getWidth()];
            
           /**
            * Resimdeki kirmizi pikseller [0,255] arasi degerler ile gosterilmektedir.
            * Kirmizi degeri tum pikselleri 255 olan bir resim icin ulasilmak istenen noktaya olan uzaklik 0 olacak ve kullanilacak butun sezgisel fonksiyonlar iyimser olmaktan cikacaktir.
            * Bunu onlemek icin her noktanin degeri 511 - kirmizi_deger olarak belirlenmistir. Bu sayede piksellere [256,511] arasi degerler atanmaktadir.
            * Kullanilan sezgisel fonksiyon 256 * manhattan_dist(endX, endY) olarak belirlenmistir.
            */
            
            for(int i = 0; i < img.getHeight(); i++){
                for(int j = 0; j < img.getWidth(); j++){
                    img_array[i][j] = (double)(511 - ((img.getRGB(j,i) >> 16) & 0xFF));  
                }
            }
         
            long startTime = System.currentTimeMillis();
            solveWithHeap(img_array, solveType.USE_ASTAR, 0, 0, 1750, 1000);
            long elapsedTime1 = System.currentTimeMillis() - startTime;
            
            
            startTime = System.currentTimeMillis();
            solveWithHeap(img_array, solveType.USE_BEST_FIRST, 0, 0, 1750, 1000);
            long elapsedTime2 = System.currentTimeMillis() - startTime;
            
            
            startTime = System.currentTimeMillis();
            solveWithStack(img_array, solveType.USE_ASTAR, 0, 0, 1750, 1000);
            long elapsedTime3 = System.currentTimeMillis() - startTime;
            
            startTime = System.currentTimeMillis();
            solveWithStack(img_array, solveType.USE_BEST_FIRST, 0, 0, 1750, 1000);
            long elapsedTime4 = System.currentTimeMillis() - startTime;
            
            System.out.println("(A* | heap) The execution time  : " + (float) ((float) elapsedTime1/1000));
            System.out.println("(Best First | Heap) The execution time  : " + (float) ((float) elapsedTime2/1000));
            System.out.println("(A* | Stack) The execution time  : " + (float) ((float) elapsedTime3 / 1000));
            System.out.println("(Best First | Stack) The execution time  : " + (float) ((float) elapsedTime4 / 1000));
            
          
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * 
     * @param img_array Resmin butun pikselleri icin belirlenen bedel fonksiyonu (511 - kirmizi(x,y))
     * @param type A* veya Best First Search algoritmalarindan hangisi kullanilacak
     * @param startX Baslangic noktasinin apsisi
     * @param startY Baslangic noktasinin ordinati
     * @param endX Bitis noktasinin apsisi
     * @param endY Bitis noktasinin ordinati
     */
    public static void solveWithStack(double[][] img_array, solveType type, 
            int startX, int startY, int endX, int endY)
    {
        //Stack olarak kullanilmasi icin node tipinde bir dizi olusturulup baslangic noktasi diziye atiliyor.
        Node array[] = new Node[img_array.length * img_array[0].length];         
        array[0] = new Node(startX, startY, endX, endY, img_array[startY][startX], 0, type);
        //createPath, getPath fonksiyonlari gelinen yolu akilda tutmak ve gerektiginde yazdirmak icin kullaniliyor.
        array[0].createPath(null);
        Node element = array[0];
        
       /**
        * A* algoritmasinda her noktaya ilk ugrayista bulunan uzaklik degeri, o noktaya ulasmak icin gerekli en dusuk uzaklik degeridir.(A* Optimaldir)
        * Bu yuzden her noktaya sadece 1 kez ugrandiginda o noktaya olan optimum yol bulunur ve o yola tekrar girmeye gerek yoktur.
        * Tekrara dusmemek icin HashMap ile gezilen dugumleri tutuyorum. 
        * Best First Search olusturulan sezgisel fonksiyon sayesinde duz bir sekilde hedefe ilerler. Tekrarlari denetlemek onemsizdir.
        */
        HashMap<Long,Boolean> map = new HashMap<>();
        insertToMap(map, startX, startY);
        int arr_len = 1;
        int max_len = arr_len;
        int total_pop = 0;
        
        while(arr_len != 0 && (array[0].getX() != endX || array[0].getY() != endY)){
            //Stack'ten eleman cekiliyor.
            element = array[0];
            int x = element.getX();
            int y = element.getY();
            arr_len--;
            total_pop++;
            
            //Stack'ten cekilen eleman siliniyor.
            for(int i = 0; i < arr_len; i++){
                array[i] = array[i + 1];
            }
            //Stack'ten cekilen elemanin 4-komsulugundaki dugumler incelenmek uzere stack'e atiliyor
            for(int i = -1; i <= 1; i++){                 
                if(i == 0)
                       continue;
                
                if(x + i >= 0 && x + i < img_array[0].length && !checkMap(map, x+i, y)){
                     array[arr_len] = new Node(x+i, y, endX, endY, img_array[y][x+i], element.getTotalLength(), type);                                                    
                     array[arr_len++].createPath(element);   
                     insertToMap(map, x+i, y);
                     if(arr_len > max_len){
                         max_len = arr_len;
                     }
                     
                     for(int k = arr_len - 2; k >= 0; k--){                   
                         if(array[k].compare(array[k+1]) == -1){
                             swap(array, k, k+1);
                         }
                     }
                }

             
                
                if(y + i >= 0 && y + i < img_array.length && !checkMap(map, x, y+i)){
                     array[arr_len] = new Node(x, y+i, endX, endY, img_array[y+i][x], element.getTotalLength(), type);
                     array[arr_len++].createPath(element);       
                     insertToMap(map, x, y+i);
                     if(arr_len > max_len){
                         max_len = arr_len;
                     }
                     
                     for(int k = arr_len - 2; k >= 0; k--){                   
                         if(array[k].compare(array[k+1]) == -1){
                             swap(array, k, k+1);
                         }
                     }
                }            
            }
        }
        /*ArrayList<Long> path = array[0].getPath();
        for(int i = 0; i < path.size(); i++){
            long xy = path.get(i);
            int x = (int)(xy >> 32);
            int y = (int)(xy&0x7FFFFFFFL);
            System.out.println("Coordinates are x: " + x + " y: " + y);
        }*/
        if(type == solveType.USE_ASTAR){
            System.out.println("(A* | Stack) Total Elements Popped: " + total_pop + "Maximum Stack Size: " + max_len);
        }
        else{
            System.out.println("(Best First | Stack) Total Elements Popped: " + total_pop + "Maximum Stack Size: " + max_len);
        }
        //Asagidaki fonksiyonda yeni bir JFrame olusturulup bulunan yol kullaniciya gosteriliyor.
        String sonuc = "YZ_Sonuc ";
        if(type == solveType.USE_ASTAR)
            sonuc += "(A* | Stack)";
        else
            sonuc += "(Best First | Stack)";
        printPictureToScreen(array[1], 0x7FFFFFFF, sonuc);
       
    }
    
    /**
     * 
     * @param img_array Resmin butun pikselleri icin belirlenen bedel fonksiyonu (511 - kirmizi(x,y))
     * @param type A* veya Best First Search algoritmalarindan hangisi kullanilacak
     * @param startX Baslangic noktasinin apsisi
     * @param startY Baslangic noktasinin ordinati
     * @param endX Bitis noktasinin apsisi
     * @param endY Bitis noktasinin ordinati
     */
    public static void solveWithHeap(double img_array[][], solveType type,
            int startX, int startY, int endX, int endY)
    {
        Node array[] = new Node[img_array.length * img_array[0].length + 1];
        array[1] = new Node(startX, startY, endX, endY, img_array[startY][startX], 0, type);
        array[1].createPath(null);
        Node element = array[1];
        int arr_len = 1;
        int max_len = arr_len;
        int total_pop = 0;
        HashMap<Long, Boolean> map = new HashMap<>();
        insertToMap(map, startX, startY);
        //Buraya kadar olan kisimlar stack fonksiyonu ile ayni mantikta. İslem kolayligi icin heap'in baslangic elemani 1 olarak belirlendi.
        
        while(arr_len != 0 && (array[1].getX() != endX || array[1].getY() != endY)){
            //Heap'ten eleman cekiliyor.
            element = array[1];         
            int x = element.getX();
            int y = element.getY();
            //Heap'in son elemani en basa geliyor ve 1. eleman siliniyor.
            array[1] = array[arr_len--];   
            total_pop++;
            
            //Heap'ten cekilen elemanin yerine gelen sondaki eleman uygun oldugu yere yerlestiriliyor.
            int i = 1;
            while(i <= arr_len){
                if(2*i+1 <= arr_len){
                    if(array[2*i].compare(array[2*i+1]) == -1){
                        if(array[i].compare(array[2*i+1]) == -1){
                            swap(array, i, 2*i+1);
                            i*=2;
                            i++; 
                            continue;
                        }
                    }
                }
                if(2*i <= arr_len){
                    if(array[i].compare(array[2*i]) == -1){
                        swap(array, i, 2*i);
                        i*=2;
                        continue;
                    }
                }
                break;
            }
            
            //Heap'ten cekilen elemanin 4-komsulugundaki dugumler incelenmek uzere stack'e atiliyor
            for(int j = -1; j <= 1; j++){                 
                if(j == 0)
                       continue;                       
                
                if(x + j >= 0 && x + j < img_array[0].length && !checkMap(map, x+j, y)){               
                    array[++arr_len] = new Node(x+j, y, endX, endY, img_array[y][x+j], element.getTotalLength(), type);
                    array[arr_len].createPath(element);
                    if(arr_len > max_len){
                        max_len = arr_len;
                    }
                    insertToMap(map, x+j, y);
                    
                    //Yeni eklenen eleman heap'te uygun oldugu yere yerlestiriliyor.
                    int k = arr_len;
                    while(k>=2){
                        if(array[k/2].compare(array[k]) == -1){
                            swap(array, k/2, k);
                        }
                        k /= 2;
                    }
                    
                }

                if(y + j >= 0 && y + j < img_array.length && !checkMap(map, x, y+j)){
                    array[++arr_len] = new Node(x, y+j, endX, endY, img_array[y+j][x], element.getTotalLength(), type); // hata olabilir.
                    array[arr_len].createPath(element);
                    if(arr_len > max_len){
                        max_len = arr_len;
                    }
                    insertToMap(map, x, y+j);
                    
                    int k = arr_len;
                    while(k>=2){
                        if(array[k/2].compare(array[k]) == -1){
                            swap(array, k/2, k);
                        }
                        k /= 2;
                    }
                }                       
            }         
        }
        /*ArrayList<Long> path = array[1].getPath();
        for(int i = 0; i < path.size(); i++){
            long xy = path.get(i);
            int x = (int)(xy >> 32);
            int y = (int)(xy&0x7FFFFFFFL);
            System.out.println("Coordinates are x: " + x + " y: " + y);
        }*/
        if(type == solveType.USE_ASTAR){
            System.out.println("(A* | Heap) Total Elements Popped: " + total_pop + "Maximum Heap Size: " + max_len);
        }
        else{
            System.out.println("(Best First | Heap) Total Elements Popped: " + total_pop + "Maximum Heap Size: " + max_len);
        }
        //Olusturulan resim ekrana basiliyor.
        String sonuc = "YZ_Sonuc ";
        if(type == solveType.USE_ASTAR)
            sonuc += "(A* | Heap)";
        else
            sonuc += "(Best First | Heap)";
        printPictureToScreen(array[1], 0x7FFFFFFF, sonuc);
        
    }
    
    private static void swap(Node array[], int x, int y){
        Node swap = array[x];
        array[x] = array[y];
        array[y] = swap;
    }
    
    private static boolean checkMap(HashMap<Long, Boolean> map, int x, int y){
        long val = (long)x;
        val <<= 32;
        val += (long)y;
        return map.containsKey(val);
    }
    
    private static void insertToMap(HashMap<Long, Boolean> map, int x, int y){
        long val = (long)x;
        val <<= 32;
        val += (long)y;
        map.put(val,true);
    }
    
    private static void printPictureToScreen(Node node, int rgb, String title){
        ArrayList<Long> path = node.getPath();
        BufferedImage print_img = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);    
    
        for(int y = 0; y < img.getHeight(); y++){
            for(int x = 0; x < img.getWidth(); x++){
                print_img.setRGB(x, y, img.getRGB(x, y));
            }
        }
        
        for(int i = 0; i < path.size(); i++){
            int y = (int)(path.get(i) & 0x7FFFFFFFL);
            int x = (int)(path.get(i) >> 32);
            print_img.setRGB(x, y, rgb);
        }
        
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JLabel label = new JLabel();
        label.setIcon(new ImageIcon(print_img));
        frame.getContentPane().add(label);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
    }
}
