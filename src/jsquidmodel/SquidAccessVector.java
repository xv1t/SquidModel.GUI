/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jsquidmodel;

import java.util.Vector;

/**
 *
 * @author xvit
 */
public class SquidAccessVector extends Vector<SquidAccess> {
    String name;
    public SquidAccessVector(String _name) {
        name = _name;
    }
    
    public void setName(String new_name){
        System.out.println("SquidAccessVector.rename("+name + "=>" +new_name+")");
        name = new_name;
        for (int i = 0; i < size(); i++) {
            SquidAccess sa = (SquidAccess) get(i);
            sa.name = new_name;                  
            
        }
    }
    

}
