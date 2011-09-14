/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jsquidmodel;

import java.util.StringTokenizer;

/**
 *
 * @author xvit
 */
public class SquidDelay  {
    String classNum = "";
    String aggregate_restore = "",
           aggregate_maximum = "",
           individual_restore = "",
           individual_maximum = "",
           network_restore = "",
           network_maximum = "";
    String pos = "";
    int posInt;

    public SquidDelay() {
        
    }
    
    @Override
    public String toString(){
 
            
        if (classNum.equals("2"))
            return "delay_parameters " + pos + " " + aggregate_restore + "/" + aggregate_maximum + " " +
                    individual_restore + "/" + individual_maximum;

        if (classNum.equals("3"))
            return "delay_parameters " + pos + " " + aggregate_restore + "/" + aggregate_maximum + " " +
                    individual_restore + "/" + individual_maximum + " " +
                    network_restore + "/" + network_maximum;

            return "delay_parameters " + pos + " " + aggregate_restore + "/" + aggregate_maximum;
                
    }
    
    
    public String toString(int pos){
        
        if (classNum.equals("2"))
            return "delay_parameters " + pos + " " + aggregate_restore + "/" + aggregate_maximum + " " +
                    individual_restore + "/" + individual_maximum;

        if (classNum.equals("3"))
            return "delay_parameters " + pos + " " + aggregate_restore + "/" + aggregate_maximum + " " +
                    individual_restore + "/" + individual_maximum + " " +
                    network_restore + "/" + network_maximum;

            return "delay_parameters " + pos + " " + aggregate_restore + "/" + aggregate_maximum;        
    }    
    
    public void setPos(int n){
        posInt = n;
        pos = String.valueOf(n);
    }
    
    public void parseLine (String parameters_line){
        
        parameters_line.trim();
        System.out.println(parameters_line);
        StringTokenizer st = new StringTokenizer(parameters_line);
        st.nextToken();
        setPos(Integer.parseInt(st.nextToken()));
        String prm = "";
        prm = st.nextToken();
        aggregate_restore = prm.split("/")[0];
        aggregate_maximum = prm.split("/")[1];
        classNum = "1";
        
        if (st.hasMoreTokens()) {
               prm = st.nextToken();
               individual_restore = prm.split("/")[0];
               individual_maximum = prm.split("/")[1]; 
               classNum = "2"; 
        }
        
        if (st.hasMoreTokens()) {
               prm = st.nextToken();
               network_restore = prm.split("/")[0];
               network_maximum = prm.split("/")[1]; 
               classNum = "3";       
              
        }        
    }
    
    public SquidDelay(String parameters_line) {
        /*
         gf
         * delay_parameters 2 32000/32000 8000/8000 600/8000
         * 
         * 
         */

        parseLine(parameters_line);
        
        
    }    

}
