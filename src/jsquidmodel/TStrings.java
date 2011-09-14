/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jsquidmodel;

import java.io.*;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 *
 * @author xvit
 * This module analog Pascal TStrings
 */
public class TStrings {
    public Vector<String> items = new Vector(0,1);

    public TStrings() {
    }
    
    public TStrings(InputStream in){
        loadFromStream(in);
    }
    
    public void loadFromFile(String file){
        items.clear();
       try { 
        BufferedReader in = new BufferedReader(new FileReader(file));
        String line;
        while ((line = in.readLine()) != null ){
            items.add(line);
        }
       }
       catch (IOException e){
           
       }
    }
    
    public static String Sort1(String s){
        //StringBuilder sb = new StringBuilder(s);
        String[] arg = s.split(" ");
        Arrays.sort(arg);
        
        String result = "";
        
        for (int i = 0; i < arg.length; i++) {
            String string = arg[i];
            if ( !string.equals(" ") & string.length() > 0)
            result = result + " " + string;
            
        }
    
        /*
         * "papa mama day month baby roll" 
         * 
         */
        
        return result;
    }
    
    public void setText(String s){
        String[] arg = s.split("\n");
        items.clear();
        for (int i = 0; i < arg.length; i++)             
            items.add(arg[i]);
       
        
    }
    
    public void loadFromFile(String file, Integer minLengthLine){
        items.clear();
       try { 
        BufferedReader in = new BufferedReader(new FileReader(file));
        String line;
        while ((line = in.readLine()) != null ){
            if (line.length() < minLengthLine) items.add(line);
        }
       }
       catch (IOException e){
           
       }
    }   
    
    public void addStrings(TStrings str){
        if (str == null) return;
        if (str.items.size() == 0) return;
        
        for (int i = 0; i < str.items.size(); ++i){
            items.add(str.items.get(i));
        }
    }
    
    public void loadFromJar(String file){
        items.clear();
         InputStream sin = getClass().getClassLoader().getResourceAsStream(file);
         if (sin == null) return;
         loadFromStream(sin);
    }
    
    public TStrings grep_v(String[] arg){
        TStrings str = new TStrings();
        
        boolean a = false;
        for (int i = 0; i < items.size(); ++i){
            a = false;
            for (int j = 0; j < arg.length; j++) {
                String string = arg[j];
                if (items.get(i).indexOf(string) >= 0) a = true;
            }
            if (!a) str.items.add(items.get(i));
            
        }
        
        
        return str;
    }
    
    public void loadFromStream(InputStream inStream){
        items.clear();
        
        if (inStream == null) return;
       try { 
        BufferedReader in = new BufferedReader(new InputStreamReader(inStream));
        String line;
        while ((line = in.readLine()) != null ){
             items.add(line);
        }
       }
       catch (IOException e){
           
       }        
    }
    
    public void saveToFile(String file ){
        try{
         File f = new File(file);
         PrintWriter out = new PrintWriter(new FileWriter(f));
         for (Integer i=0; i <= items.size()-1; i++) out.println(items.get(i).toString());
         out.close();    
        
        }
        catch (Exception e){}
    }
    
    public String getText(){
        String result ="";
        
        for (Integer i=0; i <= items.size()-1; i++) result = result + items.get(i).toString() + "\n";
        
        return result;
    }
    
     public String getText(Integer start, Integer end){
        String result = "";
        for (Integer i=start; i <= end; i++) result = result + items.get(i).toString() + "\n";
        return result;
    }
    
    public StringTokenizer lineTokenizer(Integer n){
        return new StringTokenizer(getLine(n));
    }
    
    public void setLine(String s, Integer n){
        items.set(n, s);
    }
    
    public String getLine(Integer n){
        return items.get(n).toString();
    }
           
    
    public void clear(){
        items.clear();
    }
    
    public Integer getCount(){
        return items.size();
    }

}
