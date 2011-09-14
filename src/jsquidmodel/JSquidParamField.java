/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jsquidmodel;

/**
 *
 * @author xvit
 *//*String, Int, List*/
public class JSquidParamField {
    String 
        name,
        value,
        type,  
        defaultValue,
        enabled,
        tool_tip,
        outFormat;
    
    Boolean  isNull;
    
    String[] 
         select_list;
    
    public JSquidParamField(              
        String  _name,
        String  _value,
        String  _type,  
        String  _defaultValue,
        String  _enabled,
        String  _tool_tip,
        String  _outFormat,
        Boolean _isNull,
        String[]  _select_list            
            ) {     
        name         = _name;
        value        = _value;
        type         =  _type;  
        defaultValue =  _defaultValue;
        enabled      =  _enabled;
        tool_tip     = _tool_tip;
        outFormat    =  _outFormat;
        isNull       =_isNull;
        select_list  = _select_list;
        
    }
    public String toString(){        
        return name;
    }
    

}
