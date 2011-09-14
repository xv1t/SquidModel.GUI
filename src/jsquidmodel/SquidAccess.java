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
public class SquidAccess {
    
    String name, action, value = "";

    public SquidAccess() {
    }
    
    public SquidAccess(String line){
        /*
         g
         * http_access
         * 
         */
        StringTokenizer st = new StringTokenizer(line);
        name = st.nextToken();
        
	if (name.equals("no_cache")) name = "cache";
        if (name.equals("delay_access")) name = name + " " + st.nextToken();
        if (name.equals("cache_peer_access")) name = name + " " + st.nextToken();
        if (name.equals("header_access")) name = name + " " + st.nextToken();
        
        
        action = st.nextToken();
        while (st.hasMoreTokens()) value = value + " " + st.nextToken();
        value.trim();
        System.out.println("SquidAccess("+name+").value=" + value);
        
    }
    
    public SquidAccess(String _name, String _action, String _value){
        name = _name;
        action = _action;
        value = _value;        
    }
    
    public String getLine(){
        return name + " " + action + " " + value;
    }
    
    
    

}
