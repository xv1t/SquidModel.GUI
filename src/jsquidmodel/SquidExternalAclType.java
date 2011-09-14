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
public class SquidExternalAclType {
    String name = "";
    String value = "";

    public SquidExternalAclType() {
    }
    
    public SquidExternalAclType(String line) {
        parseLine(line);
    }
    
    
    public void parseLine(String line){
        // external_acl_type name parameters
        StringTokenizer st = new StringTokenizer(line);
        st.nextToken();
        name = st.nextToken();
        value = "";
        while (st.hasMoreTokens())
            value = value + st.nextToken() + " ";
            
        
    }
    
    public String getLine(){
        return "external_acl_type " + name + " " + value;
    }
    
    public String toString(){
        return name;
    }
    

}
