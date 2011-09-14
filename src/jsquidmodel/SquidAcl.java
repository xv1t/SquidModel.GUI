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
public class SquidAcl {
    public String name;
    public String type;
    public String value;
    

    public SquidAcl() {
    }
    
    public SquidAcl(String _name, String _type, String _value){
        name = _name;
        type = _type;
        value = _value;
    }
    
    public String fullLine(){
        return "acl " + name + " " + type + " " + value;
    }
    
    public SquidAcl(String line){
        /*
         * line = "acl Safe_Ports port 1 2 3 4 5 6 7  # dfh defhdh"
         *              name      type   value[s] 
         */
        line = line.split("#")[0];
        line.trim();
        StringTokenizer st = new StringTokenizer(line);
        st.nextToken();
        
        name = st.nextToken();
        type = st.nextToken();        
        value = st.nextToken();
        
        while (st.hasMoreTokens()) value = value + " " + st.nextToken();
        
        System.out.println("acl: " + name + "[" + type + "] = " + value);
    }
    
    static public String getAclNameFromLine(String line){
        line.trim();
        StringTokenizer st = new StringTokenizer(line);
        st.nextToken();
        
        return st.nextToken();        
    }
    
    static public String getAclValueFromLine(String line){
        
        line = line.split("#")[0];
       // System.out.println(line);
        line.trim();
        StringTokenizer st = new StringTokenizer(line);
        st.nextToken();
        st.nextToken();
        st.nextToken();
        
        String val = st.nextToken();
        while (st.hasMoreTokens()) val = val + " " + st.nextToken();
        
        return val;        
    }    
    
    public void valueAdd(String a){
        value = value + " " + a;
        System.out.println("acl: " + name + "/add=" + value );
        
    }
    
    public String toString(){
        return name;
    }
    

}
