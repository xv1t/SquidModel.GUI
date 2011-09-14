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
public class DefaultSquidObject {
    String group_name = "";
    String name ="";
    String value = "";

    public DefaultSquidObject() {
    }
    public DefaultSquidObject(String _group_name, String line) {
        group_name = _group_name;
        parseLine(line);
    }
    
    public void parseLine(String line){
        StringTokenizer st = new StringTokenizer(line);
        //if (st.countTokens() < 3) return;
        if ( !st.nextToken().equals(group_name)) return;
        name = st.nextToken();
	
	if (group_name.equals("wccp2_service")) name = name + " " + st.nextToken();
	if (group_name.equals("error_map")) name = name + " " + st.nextToken();
	
        value= "";
        while (st.hasMoreTokens()){
            value = value + st.nextToken() + " ";
        }
        value.trim();
        
    }
            
    
    public String toString(){
        return name;
    }
    
    public String getLine(){
        return group_name + " " + name + " " + value;
    }

}
