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
public class SquidDefaultVector extends Vector<DefaultSquidObject> {

    String group_name;
    int prioritet;
    boolean sorting = true;
    
    public SquidDefaultVector() {
    }
    
    public SquidDefaultVector(String _group_name) {
	group_name = _group_name;
	prioritet = 5;
    }
    
    public SquidDefaultVector(String _group_name, int _prioritet) {
	group_name = _group_name;
	prioritet  = _prioritet;
    }
    
   // @Override
    
    public boolean removeByName(String name){
	int index = getIndexByName(name);
	if (index == -1) return false;
	
	removeElementAt(index);	
	return true;
    }
    
    public int getIndexByName(String name){
	for (int i =0; i < size(); i++)
	    if (get(i).name.equals(name)) return i;
	return -1;
    }
    
    public DefaultSquidObject get(String name){        
        return get(getIndexByName(name));
        
    }
    
    
    public void moveObject(int index, int new_index){
	
    }
    
    public boolean addOrReplaceObject(DefaultSquidObject object){
	object.group_name = group_name;
	int index = getIndexByName(object.name);
	
	if (index == -1) {
	    
	    if (sorting) {
	    /*SORTING is NOT AVIALABLE*/
	     add(object);
		}
	    else add(object);
	    
	    return false;
	}
	
		
	
	get(index).value = object.value;
	
	return true;
    }
    
}
