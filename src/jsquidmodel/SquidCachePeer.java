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
public class SquidCachePeer {
    String hostname;
    String values;
    
    

    public SquidCachePeer() {
    
    }
    
    public void parse(String line){
        //cache_peer mail.ru 45 45 45 45
        
        values = "";
        
        StringTokenizer st = new StringTokenizer(line);
        if (st.hasMoreTokens()) st.nextToken();
        if (st.hasMoreTokens()) hostname = st.nextToken();
        while (st.hasMoreTokens()) {
            values = values + st.nextToken() + " ";
        }
    }
    
    public String getLine(){
        return "cache_peer " + hostname + " " + values;
    }
    
    public SquidCachePeer(String line) {
        parse(line);
    }
    
    @Override
    public String toString(){
        return hostname;
    }
    
    

}
