/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jsquidmodel;

//import com.sun.org.apache.bcel.internal.generic.AALOAD;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 *
 * @author xvit
 * This class provide for read/write squid.conf
 * objects and create logical objects
 */
public class SquidConf {
    public TStrings                     Text = new TStrings();
    public TStrings                     ErrMessages = new TStrings();
    public Properties                   onceParams = new Properties();    
    public Vector<SquidAcl>             acl = new Vector();
    
    public String fileName;
    public Vector<SquidAccessVector>     access = new Vector();    
    public Vector<SquidDelay>            delays = new Vector();
    public Vector<SquidCachePeer>       cache_peers = new Vector();
    public Vector<SquidExternalAclType> external_acl_types = new Vector();
    public Vector<DefaultSquidObject>   tcp_out_addr = new Vector<DefaultSquidObject>();
    public Vector<DefaultSquidObject>   tcp_out_tos = new Vector<DefaultSquidObject>();
    public Vector<DefaultSquidObject>   logformat = new Vector<DefaultSquidObject>();
    public Vector<DefaultSquidObject>   access_log = new Vector<DefaultSquidObject>();
    public Vector<DefaultSquidObject>   refresh_pattern = new Vector<DefaultSquidObject>(); 
    public Vector<DefaultSquidObject>   header_replace = new Vector<DefaultSquidObject>();
    
    public Vector<SquidDefaultVector>	defaultVectors = new Vector<SquidDefaultVector>();
    public String			  version = "2.6";
    public int                          user_id_gen = -1;
    
    
    public  SquidConf(){
        
    }
    
    public static String sortLine(String line) {
        
        return "";
    }
    
    public String getDefaultObjectValue(String group_name, String name){
	SquidDefaultVector sdv = getDefaultVectorFromName(group_name);
	if (sdv == null) return null;	
	
	int index = sdv.getIndexByName(name);
	if (index == -1) return null;	
	
	if (sdv.get(index).value.length() == 0)  return null;
	
	return sdv.get(index).value;
    }
    
    public String getSortAclString(){
        String result = "";
        
        for (int i = 0; i < acl.size(); i++) {
            result = result + " " + acl.get(i).name;
        }
        
        
        return TStrings.Sort1(result);
    }
    
    
    public void addDefualtObjectInVector(DefaultSquidObject object){
	SquidDefaultVector sdv = getDefaultVectorFromName(object.group_name);
	
	if (sdv == null) {
	    sdv = new SquidDefaultVector(object.group_name);	
	    defaultVectors.add(sdv);
	}
	sdv.addOrReplaceObject(object);
	System.out.println(">>>>>>>>>>add def squid object: group=" + object.group_name + "; name=" + object.name);
    }
    
    public void delDefaultObjectFromVector(String group_name, String name){
	for (int i = 0; i < getDefaultVectorFromName(group_name).size(); i++)
	    if (getDefaultVectorFromName(group_name).get(i).name.equals(name))
		getDefaultVectorFromName(group_name).removeElementAt(i);
    }
    
    public SquidDefaultVector getDefaultVectorFromName(String name){	
	for (int i =0; i < defaultVectors.size(); ++i)
	    if (defaultVectors.get(i).group_name.equals(name))
	    {	System.out.println("getDefaultVectorFromName: Vector " + name + " was found ");
		
		return defaultVectors.get(i);
		
	    }
	System.out.println("getDefaultVectorFromName: Sorry, Vector " + name + " NOT found ");
	return null;
    }
    
   
    public SquidConf(String file){
        loadFromFile(file);
    }
    
    public int getAccessLogIndex(String name){
        for (int i = 0; i < access_log.size(); i++) {
            if (access_log.get(i).name.equals(name)) return i;
            
        }
        return -1;
    }     
    
    public int getLogFormatIndex(String name){
        for (int i = 0; i < logformat.size(); i++) {
            if (logformat.get(i).name.equals(name)) return i;
            
        }
        return -1;
    }  
    
    public int getheader_replaceIndex(String name){
        for (int i = 0; i < header_replace.size(); i++) {
            if (header_replace.get(i).name.equals(name)) return i;
            
        }
        return -1;
    }  
    
    public int getrefreshPatternIndex(String name){
        for (int i = 0; i < refresh_pattern.size(); i++) {
            if (refresh_pattern.get(i).name.equals(name)) return i;
            
        }
        return -1;
    }      
    
    public int getTcpOutAddrIndex(String name){
        for (int i = 0; i < tcp_out_addr.size(); i++) {
            if (tcp_out_addr.get(i).name.equals(name)) return i;
            
        }
        return -1;
    }
    
    public int getTcpOutTosIndex(String name){
        for (int i = 0; i < tcp_out_tos.size(); i++) {
            if (tcp_out_tos.get(i).name.equals(name)) return i;
            
        }
        return -1;
    }    
    
    public int getAccessGroupIndex(String name){
        for (int i = 0; i < access.size(); i++) {
            SquidAccessVector sav =  access.get(i);
            if (sav.name.equals(name)) return i;
        }
        
        return -1;
    }
    
    public void addAccess(SquidAccess sa){
        int index = getAccessGroupIndex(sa.name);
        if (index < 0){
            SquidAccessVector sav = new SquidAccessVector(sa.name);
            sav.add(sa);
            access.add(sav);
        }
        else
        {
            SquidAccessVector sav = access.get(index);
            sav.add(sa);
        }
        
    }
    
    public void deleteCachePeer(String hostname){
        
        int cache_ix = getCachePeerIndex(hostname);
        
        if (cache_ix == -1) return;
        cache_peers.removeElementAt(cache_ix);
        
        int acc_ix = getAccessGroupIndex("cache_peer_access " + hostname);
        if (acc_ix == -1) return;
        access.removeElementAt(acc_ix);
        
    }
    
    public int getCachePeerIndex(String hostname){
        
        for (int i = 0; i < cache_peers.size(); i++) {
            System.out.println("search cache peer: " + cache_peers.get(i).hostname);
            if (cache_peers.get(i).hostname.equals(hostname)) return i;           
        
        }
      
        
        return -1;
    }
    
    public void loadFromFile(String file){
        
        Text.loadFromFile(file);  
        fileName = file;
        System.out.println("SquidConf.loadFromFile: " + Text.getCount() + " lines");
    }
    
    public void sortDelays(){
        /*
         d
         * 1 3 5 4 2 
         *         
         */
    }
    
    public void minimalConf(){
        acl.add( new  SquidAcl("acl all         src  0.0.0.0/0.0.0.0"));
        acl.add( new  SquidAcl("acl localhost   src  127.0.0.1/255.255.255.255"));
        acl.add( new  SquidAcl("acl Safe_ports  port 80 443"));
/*


*/        
        logformat.add(new DefaultSquidObject("logformat", "logformat squid  %ts.%03tu %6tr %>a %Ss/%03Hs %<st %rm %ru %ul %Sh/%<A %mt"));
        logformat.add(new DefaultSquidObject("logformat", "logformat squidmime  %ts.%03tu %6tr %>a %Ss/%03Hs %<st %rm %ru %ul %Sh/%<A %mt [%>h] [%<h]"));        
        logformat.add(new DefaultSquidObject("logformat", "logformat common %>a %ui %ul [%tl] \"%rm %ru HTTP/%rv\" %Hs %<st %Ss:%Sh"));
        logformat.add(new DefaultSquidObject("logformat", "logformat combined %>a %ui %ul [%tl] \"%rm %ru HTTP/%rv\" %Hs %<st \"%{Referer}>h\" \"%{User-Agent}>h\" %Ss:%Sh"));          
     //   logformat.add(new DefaultSquidObject("logformat", "logformat none"));
        
        
        
        
        
        onceParams.setProperty("http_port", " 8080");
        
        addAccess(new SquidAccess("http_access allow localhost"));
        addAccess(new SquidAccess("http_access allow Safe_ports"));
        addAccess(new SquidAccess("http_access deny all"));
    }
    
    public void analizeConfig(){
        //access log
        //if (access_log.size() > 0)
        
        if (access_log.size() > 0){
            if (getLogFormatIndex("squid") == -1)
                   logformat.add(new DefaultSquidObject(
                           "logformat", 
                           "logformat squid  %ts.%03tu %6tr %>a %Ss/%03Hs %<st %rm %ru %un %Sh/%<A %mt"));
            if (getLogFormatIndex("squidmime") == -1)
                    logformat.add(new DefaultSquidObject(
                            "logformat", 
                            "logformat squidmime  %ts.%03tu %6tr %>a %Ss/%03Hs %<st %rm %ru %un %Sh/%<A %mt [%>h] [%<h]"));        
            if (getLogFormatIndex("common") == -1)
                    logformat.add(new DefaultSquidObject(
                            "logformat", 
                            "logformat common %>a %ui %un [%tl] \"%rm %ru HTTP/%rv\" %Hs %<st %Ss:%Sh"));
            if (getLogFormatIndex("combined") == -1)
                    logformat.add(new DefaultSquidObject(
                            "logformat", 
                            "logformat combined %>a %ui %un [%tl] \"%rm %ru HTTP/%rv\" %Hs %<st \"%{Referer}>h\" \"%{User-Agent}>h\" %Ss:%Sh"));          

        }
        
        for (int i = 0; i < acl.size(); i++){
            if (acl.get(i).type.equals("proxy_auth")){
                StringTokenizer st = new StringTokenizer(acl.get(i).value);
                while (st.hasMoreTokens()){
                    String user = st.nextToken();
                    if (getDefaultObjectValue("#user", user) == null)
                    addDefualtObjectInVector(new DefaultSquidObject("#user", "#user " + user + " disable=true password=disabled id=" + getNextUserGenId()));
                }
            }
        }
    }
    
    public void readText(){
        onceParams.clear();
        acl.clear();
        access.clear();
        delays.clear();
        cache_peers.clear();
        external_acl_types.clear();
        tcp_out_addr.clear();
        tcp_out_tos.clear();
        logformat.clear();
        access_log.clear();
        refresh_pattern.clear();
	header_replace.clear();
	
	defaultVectors.clear();
	
        

        
        System.out.println("readText");
        for (Integer i = 0; i <= Text.getCount()-1; i++){
            String line = Text.getLine(i);
            StringTokenizer option;
            String param_name = "";
            line.trim();
          //  
            
            if (line.indexOf("#user") == 0) {
              
               addDefualtObjectInVector(new DefaultSquidObject("#user", line));
               
            }
            
            if (line.indexOf("#squidmodel") == 0) {
              
               addDefualtObjectInVector(new DefaultSquidObject("#squidmodel", line));
               
            }
            
            if (line.length() > 1 && line.indexOf("#") != 0){
                System.out.println(i+". " + line);
                option = Text.lineTokenizer(i);
                param_name = option.nextToken();
                
                //System.out.println("once: " + param_name + "=" + getParamTypeFromName(param_name) + "-" + getParamTypeFromName(param_name).indexOf("once."));
                
                if (getParamTypeFromName(param_name).indexOf("once.") == 0){
                    
                    onceParams.setProperty(param_name, line.substring(param_name.length()).trim());
                    System.out.println(getParamTypeFromName(param_name)+": " + param_name + "=" + onceParams.getProperty(param_name));
                }
                
                if (param_name.equals("neighbor_type_domain")){
                    StringTokenizer st  = new StringTokenizer(line);
                    st.nextToken();
                    param_name = "neighbor_type_domain " + st.nextToken();// + " " + st.nextToken();
                    onceParams.setProperty(param_name, line.substring(param_name.length()).trim());
                    System.out.println(param_name + ": " + onceParams.getProperty(param_name));
                }
                
                if (param_name.equals("cache_peer_domain")){
                    StringTokenizer st  = new StringTokenizer(line);
                    st.nextToken();
                    param_name = "cache_peer_domain " + st.nextToken();// + " " + st.nextToken();
                    onceParams.setProperty(param_name, line.substring(param_name.length()).trim());
                    System.out.println(param_name + ": " + onceParams.getProperty(param_name));
                }
                
                if (param_name.equals("auth_param")){
                    StringTokenizer st  = new StringTokenizer(line);
                    st.nextToken();
                    param_name = "auth_param " + st.nextToken() + " " + st.nextToken();
                    onceParams.setProperty(param_name, line.substring(param_name.length()).trim());
                    System.out.println(param_name + ": " + onceParams.getProperty(param_name));
                }
                
                if (param_name.equals("cache_peer")) {
                    cache_peers.add(new SquidCachePeer(line));
                }
                
                if (param_name.equals("external_acl_type")) {
                    external_acl_types.add(new SquidExternalAclType(line));
                }
                
                if (param_name.equals("tcp_outgoing_address")) {
                    tcp_out_addr.add(new DefaultSquidObject(param_name, line));
                }
                
                if (param_name.equals("logformat")) {
                    logformat.add(new DefaultSquidObject(param_name, line));
                }
		
		
		
		if (param_name.equals("wccp2_service")) {
                    addDefualtObjectInVector(new DefaultSquidObject(param_name, line));
                }
		
		if (param_name.equals("wccp2_service_info")) {
                    addDefualtObjectInVector(new DefaultSquidObject(param_name, line));
                }
		
		if (param_name.equals("error_map")) {
                    addDefualtObjectInVector(new DefaultSquidObject(param_name, line));
                }
		
		if (param_name.equals("deny_info")) {
                    addDefualtObjectInVector(new DefaultSquidObject(param_name, line));
                }
		
		if (param_name.equals("cachemgr_passwd")) {
                    addDefualtObjectInVector(new DefaultSquidObject(param_name, line));
                }
		
		if (param_name.equals("header_replace")) {
                    header_replace.add(new DefaultSquidObject(param_name, line));
                }
                
                if (param_name.equals("refresh_pattern")) {
                    refresh_pattern.add(new DefaultSquidObject(param_name, line));
                }
                
                if (param_name.equals("access_log")) {
                    access_log.add(new DefaultSquidObject(param_name, line));
                }
                
                if (param_name.equals("tcp_outgoing_tos")) {
                    tcp_out_tos.add(new DefaultSquidObject(param_name, line));
                    System.out.println(line);
                }
                
                if (param_name.equals("acl")) {
                    if (getAclIndex(SquidAcl.getAclNameFromLine(line)) == -1) 
                    acl.add( new  SquidAcl(line));
                    else
                    {
                        SquidAcl o = (SquidAcl)acl.get(getAclIndex(SquidAcl.getAclNameFromLine(line)));
                        o.valueAdd(SquidAcl.getAclValueFromLine(line));
                    }
                    
                   // System.out.println("acl: " + line);
                }
                
                if (getParamTypeFromName(param_name).equals("access")) {
                    addAccess(new SquidAccess(line));
                    System.out.println("readText().access: " + line);
                }
		
		if (param_name.equals("header_access")) {
                    addAccess(new SquidAccess(line));
                    System.out.println("readText().access: " + line);
                }
                

                
            }
            
            
            
        }
        
        // Load delays
        System.out.println("   load delays from file...");
        
     if (onceParams.getProperty("delay_pools") != null) {
        for (int i = 0; i<Integer.parseInt(onceParams.getProperty("delay_pools")); i++){
            //Fill the delay array null array
            
            delays.add(new SquidDelay());
        }
        
        for (Integer i = 0; i <= Text.getCount()-1; i++){
            String line = Text.getLine(i);
            StringTokenizer option;
            String param_name = "";
            line.trim();
          //  
            
            if (line.length() > 1 && line.indexOf("#") != 0){
             //   System.out.println(i+". " + line);
                option = Text.lineTokenizer(i);
                param_name = option.nextToken();
                

                
                if (param_name.equals("delay_parameters")){
                    System.out.println(line);
                   // SquidDelay sd = new c;
                     delays.get(new SquidDelay(line).posInt-1).parseLine(line);
                    
                   // sd2.parseLine(line);
                }
                
                if  (param_name.equals("delay_access")){
                    /*delay_access 2 allow lotsa_little_clients*/
                    SquidAccess sa = new SquidAccess();
                    StringTokenizer st = new StringTokenizer(line);
                    st.nextToken();
                    sa.name = param_name + " " + st.nextToken();
                    sa.action = st.nextToken();
                    sa.value = "";
                    
                    while (st.hasMoreTokens()) sa.value = sa.value + st.nextToken() + " ";
                    
                    addAccess(sa);
                }
                
            }
            
	}   
            
        }  //for
        
        analizeConfig();
	getDefaultVectorInfo();
	
	
    }
    
    public void getDefaultVectorInfo(){
	System.out.println("getDefaultVectorInfo");
	for (int i = 0; i < defaultVectors.size(); i++) {
	    System.out.println(defaultVectors.get(i).group_name);
	    for (int j = 0; j < defaultVectors.get(i).size(); j++){
		System.out.println("   >>>>" + defaultVectors.get(i).get(j).name);
	    }
	    
	}
    }
    
    public Boolean checkAclDep(String acl_name){
        
        return false;
    }
    
    public int getExternalAclTypeIndex(String name){
        for (int i = 0; i < external_acl_types.size(); ++i)
            if (external_acl_types.get(i).name.equals(name)) return i;
        
        return -1;
    }
    
    public int getDelayIndex(String delayNum){
        for (int i = 0; i < delays.size(); i++) {
            SquidDelay sd = delays.get(i);
            if (sd.pos.equals(delayNum)) return i;
            
        }
        return -1;
    }
    
    public void deleteDelay(String delayNum){
        int ix = getDelayIndex(delayNum);
        if (ix == -1) return;
        
        int group_ix = getAccessGroupIndex("delay_access " + delayNum);
        if (group_ix == -1) return;
        
        SquidDelay sd =  delays.get(ix);
        
        
        
        

        
        if (sd.posInt == delays.size()){
            
            access.removeElementAt(group_ix);
            delays.removeElementAt(ix);  
            return;
        }
        else {
            for (int i = delays.size()-1; i>ix; i--) {
                SquidDelay sd2 =  delays.get(i);
                
                if (sd2.posInt > sd.posInt) {
                    
                    Integer gr_ix = getAccessGroupIndex("delay_access " + sd2.pos);
                    sd2.setPos(sd2.posInt-1);
                    if (gr_ix != -1) {
                        SquidAccessVector sav = access.get(gr_ix);
                        //sav.name = ;
                        sav.setName("delay_access " + String.valueOf(sd2.posInt));
                            
                    }
                }
                
            }
            access.removeElementAt(group_ix);
            delays.removeElementAt(ix);  
        }
               
        
        
        
        
        
       
        
        

            
        
    }
    
    public SquidAcl getAcl(String name){
        int ix = getAclIndex(name);
        if (ix == -1) return null;       
        
        return acl.get(ix);
    }
    
    public Integer getAclIndex(String aclName){

        
        for (Integer i=0; i<acl.size(); i++){
            SquidAcl obj = (SquidAcl)acl.get(i);

            if (obj.name.equals(aclName))
            {

                return i;
            }
        }

        return -1;
    }
    
    static Boolean moveVectorElement(Vector vector, Integer ix, Integer new_index){
        if (ix == new_index) return false;
        if (new_index < 0) return false;
        if (new_index > vector.size()-1) return false;
        
        //System.out.println(vector.size() +  "/ix=" + ix + ", new_index=" + new_index + ": " + vector.toString());
        
        if (ix > new_index) {
            /*up*/
            vector.insertElementAt(vector.get(ix), new_index);
            vector.removeElementAt(ix+1);
        }
        
        if (ix < new_index) {
            /*down*/
            
            vector.insertElementAt(vector.get(ix), new_index+1);
            vector.removeElementAt(ix);
        }          
        //System.out.println(vector.toString());
        return true;
    }
    
    public Boolean moveAcl(String acl_name, Integer new_index){
        Integer ix = getAclIndex(acl_name);
        Vector vector = acl;
        
        return SquidConf.moveVectorElement(vector, ix, new_index);
             
    }
    
    public void removeUserFromAllGroups(String user){
        int result = 0;
        for (int i = 0 ; i < acl.size(); i++){
            if (acl.get(i).type.equals("proxy_auth"))
                if (acl.get(i).value.indexOf(user) >= 0) {
                    StringTokenizer st = new StringTokenizer(acl.get(i).value);
                    String newval = "";
                    while (st.hasMoreTokens()){
                        String tok = st.nextToken();
                        if (!tok.equals(user)) newval = newval + tok + " ";
                    }
                    acl.get(i).value = newval;
                }
                    
        }
    }
    
    public String groupsWithoutUser(String user){
    String result = ""    ;
    
        for (int i = 0 ; i < acl.size(); i++)
            if (acl.get(i).type.equals("proxy_auth"))      
                if (acl.get(i).value.indexOf(user) == -1)
                result = result + " " + acl.get(i).name;
            return result;        
    }
    
    public String allGroupsToString(){
    String result = ""    ;
    
        for (int i = 0 ; i < acl.size(); i++)
            if (acl.get(i).type.equals("proxy_auth"))        
                result = result + " " + acl.get(i).name;
            return result;
    }
    
    public int getUsersCountInGroup(String group){
        int acl_ix = getAclIndex(group);
        
        if (acl_ix == -1) return 0;                 
        StringTokenizer st = new StringTokenizer(acl.get(acl_ix).value);
        return st.countTokens();
    }
    
    
    public void addUserToGroup(String user, String group){
        if (userInGroup(user, group)) return;
        int acl_ix = getAclIndex(group);
        
        if (acl_ix == -1) return;    
        acl.get(acl_ix).value = acl.get(acl_ix).value + " " + user;
    }
    
    /*
     * Считаем сколько команд доступа используют данный список
     * 
     */
    public int getAclEntryAccessCount(String aclname){
        int result = 0;
        for (int i = 0; i < access.size(); i++){
            SquidAccessVector sa = access.get(i);
            for (int j = 0; j < sa.size(); j++) {
                if (sa.get(j).value.indexOf(aclname) >= 0) result++;
                
            }
        }
        
        return result;
    }
    
    public String getAclEntryAccessToString(String aclname){
        String result = "";
        for (int i = 0; i < access.size(); i++){
            SquidAccessVector sa = access.get(i);
            for (int j = 0; j < sa.size(); j++) {
                if (sa.get(j).value.indexOf(aclname) >= 0) result = result + sa.name + " " ;
                
            }
        }
        
        return result;
    }
            
    
    public void removeUserFromgroup(String user, String group){
        if (!userInGroup(user, group)) return;
        int acl_ix = getAclIndex(group);
        
        if (acl_ix == -1) return;    
                    StringTokenizer st = new StringTokenizer(acl.get(acl_ix).value);
                    String newval = "";
                    while (st.hasMoreTokens()){
                        String tok = st.nextToken();
                        if (!tok.equals(user)) newval = newval + tok + " ";
                    }
                    acl.get(acl_ix).value = newval;        
    }
    
    public boolean userInGroup(String user, String group){
     
        int acl_ix = getAclIndex(group);
        
        if (acl_ix == -1) return false;        

        if (acl.get(acl_ix).value.indexOf(user) >= 0) return true;
        return false;
    }
    
    /*
     * Get count of user group
     */
    public int getUserGroupsCount(String user){
        int result = 0;
        for (int i = 0 ; i < acl.size(); i++){
            if (acl.get(i).type.equals("proxy_auth"))
                if (acl.get(i).value.indexOf(user) >= 0) result++;
        }
        
        return result;
    }
    
    public int getUsersCount(){
        SquidDefaultVector vUsers = getDefaultVectorFromName("#user");
        if (vUsers != null){
            return vUsers.size();
        }
            
        return 0;
    }
    
    public int getNextUserGenId(){
        SquidDefaultVector vUsers = getDefaultVectorFromName("#user");
       // System.out.println(">>>getNextUserGenId");
        if (vUsers != null){
            int id =0;
            int xid = -1;
            for (int i = 0; i < vUsers.size(); i++){
                //System.out.println("    check id in :" + vUsers.get(i).name);
                if (vUsers.get(i).value != null) {
                    StringTokenizer st = new StringTokenizer(vUsers.get(i).value);
                    while (st.hasMoreTokens()){
                        String tok = st.nextToken();
                        if (tok.indexOf("id=") == 0)
                           xid = Integer.parseInt(tok.split("=")[1]);
                            
                        }
                    
                    if (xid >= id) id = xid;
                   // System.out.println("         xid=" + xid + " (id=)"+id);
                    }
                
               // System.out.println( vUsers.get(i).value);
                
            }
            return id+1;
        }

       return 1;
    }
    
    public void getUsers(){
        Properties users = new Properties();
        for (int i = 0; i < acl.size(); i++) {
            SquidAcl sa = (SquidAcl) acl.get(i);
            if (sa.type.equals("proxy_auth")) {
                StringTokenizer st = new StringTokenizer(sa.value);
                while (st.hasMoreTokens()) users.setProperty(st.nextToken(), "user");
            }
        }
        users.list(System.out);
    }
    
    public void writeDefaultsVectorsHidden(){
	for (int i = 0; i < defaultVectors.size(); ++i){
	     if (defaultVectors.get(i).group_name.indexOf("#") == 0) {
		 Text.items.add("# " + defaultVectors.get(i).group_name);
		 for (int j = 0; j < defaultVectors.get(i).size(); j++) {
		     Text.items.add(defaultVectors.get(i).get(j).getLine());
		 }
         Text.items.add("# " + defaultVectors.get(i).group_name+"_end");
	     }
	}
    }    
    
    public void writeDefaultsVectorsByPrioritet(int prioritet){
	for (int i = 0; i < defaultVectors.size(); ++i){
	     if (defaultVectors.get(i).prioritet == prioritet && 
                     defaultVectors.get(i).group_name.indexOf("#") == -1
                     ) {
		 Text.items.add("# " + defaultVectors.get(i).group_name);
		 for (int j = 0; j < defaultVectors.get(i).size(); j++) {
		     Text.items.add(defaultVectors.get(i).get(j).getLine());
		 }
	     }
	}
    }
    
    public void makeConfText(){
       if (delays.size() > 0)
           onceParams.setProperty("delay_pools", String.valueOf(delays.size()));
        
        
       Text.clear();
       

       Text.items.add("#  this file generated by SquidModel");       
        writeDefaultsVectorsHidden();
       
       
       Object[] obj = onceParams.keySet().toArray();
       if (obj.length > 0) Text.items.add("#Options"); 
       for (Integer i=0; i<= obj.length-1; i++)
           if (obj[i].toString().indexOf("auth_param") == -1 &
              obj[i].toString().indexOf("cache_peer_domain") == -1 &
              obj[i].toString().indexOf("neighbor_type_domain") == -1
             
           )
           Text.items.add(obj[i].toString() + " " + onceParams.getProperty(obj[i].toString()));
       
       for (int i=0; i<logformat.size(); ++i){
           Text.items.add(logformat.get(i).getLine()); 
       }
       
       Text.items.add("#Auth"); 
       for (Integer i=0; i<= obj.length-1; i++)
           if (obj[i].toString().indexOf("auth_param") == 0)
           Text.items.add(obj[i].toString() + " " + onceParams.getProperty(obj[i].toString()));
     
              
       if (external_acl_types.size() > 0) Text.items.add("#external_acl_types"); 
       for (int i = 0 ; i<external_acl_types.size(); ++i)
           Text.items.add(external_acl_types.get(i).getLine());
       
       
       if (acl.size() > 0) Text.items.add("#acl"); 
       for (Integer i=0; i<= acl.size() -1; i++){     
           SquidAcl ac = (SquidAcl)acl.get(i);
           Text.items.add(ac.fullLine());
           // System.out.println( i + ". " + obj[i].toString() + "=" + once_params.getProperty(obj[i].toString())
        }
       
       if (refresh_pattern.size() > 0) Text.items.add("#refresh_patterns"); 
       for (int i = 0; i < refresh_pattern.size(); i++)
            Text.items.add(refresh_pattern.get(i).getLine());
       
       if (tcp_out_addr.size() > 0) Text.items.add("#tcp_outgoing_address"); 
       for (int i = 0; i < tcp_out_addr.size(); i++)
            Text.items.add(tcp_out_addr.get(i).getLine());
       
       if (tcp_out_tos.size() > 0) Text.items.add("#tcp_outgoing_tos"); 
       for (int i = 0; i < tcp_out_tos.size(); i++)
            Text.items.add(tcp_out_tos.get(i).getLine());
       
       if (access_log.size() > 0) Text.items.add("#access_log"); 
       for (int i = 0; i < access_log.size(); i++)
            Text.items.add(access_log.get(i).getLine());   
       
       if (header_replace.size() > 0) Text.items.add("#header_replace"); 
       for (int i = 0; i < header_replace.size(); i++)
            Text.items.add(header_replace.get(i).getLine());  

       
       
       if (cache_peers.size() > 0) Text.items.add("# cache peers");
        for (int i = 0; i < cache_peers.size(); i++) {
            Text.items.add(cache_peers.get(i).getLine());
            
        }
       
      // onceParams.list(System.out);
       
       for (int i=0; i<= obj.length-1; i++)
           if (obj[i].toString().indexOf("cache_peer_domain") == 0 | 
               obj[i].toString().indexOf("neighbor_type_domain") == 0)
           Text.items.add(obj[i].toString() + " " + onceParams.getProperty(obj[i].toString()));
       
       
       if (delays.size() > 0) {
           Text.items.add("#delays"); 
         //  Text.items.add("delay_pools " + delays.size());
       
       }
       
        for (int i = 0; i < delays.size(); i++) {
            //SquidDelay sd =  delays.get(i);
            Text.items.add(
                "delay_class "   + 
                delays.get(i).pos + " " +
                delays.get(i).classNum);
                
               
            Text.items.add(delays.get(i).toString());
            
        }
       
        if (access.size() > 0) Text.items.add("#access"); 
        for (int i = 0; i < access.size(); i++) {
            SquidAccessVector sav =  access.get(i);
            for (int j = 0; j < sav.size(); j++) {
                SquidAccess sa = (SquidAccess) sav.get(j);
                Text.items.add(sa.getLine());
                
            }
            
        }
       
       /*
	fg
	* NEW
	
	*/
       writeDefaultsVectorsByPrioritet(5);
       
       
    }
    
    public String getText(){
        return Text.getText();
    }
    
    public void saveToFile(String file){
        /*once
           Object[] obj = once_params.keySet().toArray();
        
        
       for (Integer i=0; i<= obj.length-1; i++){
           // System.out.println( i + ". " + obj[i].toString() + "=" + once_params.getProperty(obj[i].toString())
                    
                    );
        }
         */
      
        
        
        /**/
        
        Text.saveToFile(file);
    }    
    
    public void saveToFile(){
        Text.saveToFile(fileName);
    }
    
    public String getOnce(String param){
       // System.out.println("query param: " + param + "==" + onceParams.getProperty(param));    
        
        return onceParams.getProperty(param);
    }
    
    public void setOnce(String param, String value){
        onceParams.setProperty(param, value);
        System.out.println("set: " + param + " to [" + value + "]");
        
    }
    
    public void setOnce(String param, Integer value){
        onceParams.setProperty(param, value.toString());
        System.out.println("set: " + param + " to [" + value + "]");
    }
    
    public void setOnce(String param, Boolean value){
         if (value) onceParams.setProperty(param, "on");
         else onceParams.setProperty(param, "off");
         
         System.out.println("set: " + param + " to [" + value + "]");
    }
    
    public void emptyOnce(String param){
        onceParams.remove(param);
        System.out.println("remove: " + param);
    }
    

    public String getParamTypeFromName(String param_name){
        
        if (param_name.equals("acl_uses_indirect_client") |
            param_name.equals("allow_underscore") |
            param_name.equals("balance_on_multiple_ip") | 
            param_name.equals("buffered_logs") | 
            param_name.equals("cache_vary") | 
            param_name.equals("check_hostnames") | 
            param_name.equals("client_db") |             
            param_name.equals("client_persistent_connections") | 
            param_name.equals("collapsed_forwarding") | 
            param_name.equals("delay_pool_uses_indirect_client") | 
            param_name.equals("detect_broken_pconn") |            
            param_name.equals("digest_generation") | 
            param_name.equals("dns_defnames") | 
            param_name.equals("dns_testnames") | 
            param_name.equals("emulate_httpd_log") |             
            param_name.equals("forwarded_for") | 
            param_name.equals("ftp_passive") | 
            param_name.equals("ftp_sanitycheck") | 
            param_name.equals("ftp_telnet_protocol") |                
            param_name.equals("global_internal_static") | 
            param_name.equals("half_closed_clients") | 
            param_name.equals("httpd_accel_no_pmtu_disc") | 
            param_name.equals("httpd_suppress_version_string") |             
            param_name.equals("icp_hit_stale") | 
            param_name.equals("ie_refresh") | 
            param_name.equals("ignore_unknown_nameservers") | 
            param_name.equals("log_fqdn") |            
            param_name.equals("log_icp_queries") | 
            param_name.equals("log_ip_on_direct") | 
            param_name.equals("log_mime_hdrs") | 
            param_name.equals("log_uses_indirect_client") |             
            param_name.equals("memory_pools") | 
            param_name.equals("nonhierarchical_direct") | 
            param_name.equals("offline_mode") | 
            param_name.equals("persistent_connection_after_error") | 
            param_name.equals("pipeline_prefetch") | 
            param_name.equals("prefer_direct") | 
            param_name.equals("query_icmp") | 
            param_name.equals("redirector_bypass") | 
            param_name.equals("relaxed_header_parser") | 
            param_name.equals("reload_into_ims") | 
            param_name.equals("request_entities") | 
            param_name.equals("retry_on_error") | 
            param_name.equals("server_persistent_connections") | 
            param_name.equals("short_icon_urls") | 
            param_name.equals("ssl_unclean_shutdown") | 
            param_name.equals("strip_query_terms") | 
            param_name.equals("test_reachability") | 
            param_name.equals("url_rewrite_host_header") | 
            param_name.equals("vary_ignore_expire") | 
            param_name.equals("via") | 
            param_name.equals("wccp2_rebuild_wait") 
            ) return "once.bool";
        
         if (param_name.equals("announce_host") |
            param_name.equals("append_domain") |
            param_name.equals("as_whois_server") | 
            param_name.equals("cache_effective_group") | 
      //      param_name.equals("cache_peer_domain") | 
            param_name.equals("cache_effective_user") | 
            param_name.equals("cache_mgr") |             
            param_name.equals("debug_options") | 
            param_name.equals("dns_nameservers") | 
            param_name.equals("err_html_text") | 
            param_name.equals("extension_methods") |    
            param_name.equals("ftp_user") |   
            param_name.equals("hierarchy_stoplist") | 
            param_name.equals("hostname_aliases") | 
            param_name.equals("mail_program") | 
            param_name.equals("mail_from") | 
            param_name.equals("mcast_groups") |             
            param_name.equals("mcast_miss_encode_key") | 
    //        param_name.equals("neighbor_type_domain") | 
            param_name.equals("ssl_engine") | 
            param_name.equals("sslproxy_cipher") | 
            param_name.equals("sslproxy_options") | 
            param_name.equals("sslproxy_client_certificate") |                
            param_name.equals("sslproxy_client_key") | 
            param_name.equals("sslproxy_version") | 
            param_name.equals("umask") | 
            
            param_name.equals("unique_hostname") |             
            param_name.equals("visible_hostname")         
           ) return "once.str"; 
        
         if (param_name.equals("cache_swap_high")  |
            param_name.equals("cache_swap_low") |
            param_name.equals("delay_initial_bucket_level") |
            param_name.equals("digest_rebuild_chunk_percentage") |
            param_name.equals("quick_abort_pct")    )
         return "once.percent";
        
        if (param_name.equals("announce_port")  |
            param_name.equals("digest_bits_per_entry") |
            param_name.equals("delay_pools") |
            param_name.equals("digest_swapout_chunk_size") |
            param_name.equals("dns_children") |
            param_name.equals("fqdncache_size") |
            param_name.equals("ftp_list_width") |
            param_name.equals("high_page_fault_warning") |            
            param_name.equals("high_response_time_warning") |
            param_name.equals("htcp_port") |
            param_name.equals("udp_port") |
            param_name.equals("icp_query_timeout") |
	    param_name.equals("icp_port") |
            param_name.equals("incoming_dns_average") |
            param_name.equals("incoming_http_average") |               
            param_name.equals("incoming_icp_average") |
            param_name.equals("ipcache_high") |
            param_name.equals("ipcache_low") |
            param_name.equals("ipcache_size") |
            param_name.equals("location_rewrite_children") |
            param_name.equals("location_rewrite_concurrency") |            
            param_name.equals("logfile_rotate") |
            param_name.equals("max_open_disk_fds") |
            param_name.equals("maximum_icp_query_timeout") |
            param_name.equals("maximum_single_addr_tries") |
            param_name.equals("mcast_icp_query_timeout") |
            param_name.equals("mcast_miss_port") |  
            param_name.equals("mcast_miss_ttl") |
            param_name.equals("min_dns_poll_cnt") |
            param_name.equals("min_http_poll_cnt") |
            param_name.equals("min_icp_poll_cnt") |
            param_name.equals("maximum_direct_hops") |   
            param_name.equals("minimum_direct_hops") | 
            param_name.equals("minimum_direct_rtt") |
            param_name.equals("netdb_high") |
            param_name.equals("netdb_low") |
            param_name.equals("sleep_after_fork") |
            param_name.equals("store_objects_per_bucket") |
            param_name.equals("snmp_port") |               
            param_name.equals("url_rewrite_children") |
            param_name.equals("url_rewrite_concurrency") |
            param_name.equals("wccp2_weight") |
	    param_name.equals("wccp2_assignment_method") |
	    param_name.equals("wccp2_forwarding_method") |
	    param_name.equals("wccp2_return_method") |
            param_name.equals("wccp_version") )
         return "once.int";
        
  if (param_name.equals("announce_file")  |                
      param_name.equals("cache_dns_program")  |          
      param_name.equals("cache_log")  |
      param_name.equals("cache_store_log")  | 
      param_name.equals("cache_swap_state")  |          
      param_name.equals("chroot")  |
      param_name.equals("coredump_dir")  |                 
      param_name.equals("diskd_program")  |          
      param_name.equals("error_directory")  |
      param_name.equals("forward_log")  | 
      param_name.equals("hosts_file")  |          
      param_name.equals("icon_directory")  |
      param_name.equals("location_rewrite_program")  |          //???????????
      param_name.equals("mime_table")  |          
      param_name.equals("pid_filename")  | 
      param_name.equals("pinger_program")  |           
      param_name.equals("referer_log")  |          
      param_name.equals("sslproxy_cafile")  |
      param_name.equals("sslproxy_capath")  | 
      param_name.equals("sslpassword_program")  |          
      param_name.equals("unlinkd_program")  |
      param_name.equals("url_rewrite_program")  |         
      param_name.equals("useragent_log")   )
         return "once.file";   
        
        
  if (param_name.equals("announce_period")  | 
      param_name.equals("authenticate_cache_garbage_interval")  |          
      param_name.equals("authenticate_ip_ttl")  |
      param_name.equals("authenticate_ttl")  | 
      param_name.equals("client_lifetime")  |          
      param_name.equals("connect_timeout")  |
      param_name.equals("dead_peer_timeout")  | 
      param_name.equals("digest_rebuild_period")  |          
      param_name.equals("digest_rewrite_period")  |
      param_name.equals("dns_retransmit_interval")  | 
      param_name.equals("dns_timeout")  |   
      param_name.equals("forward_timeout")  |            
      param_name.equals("ident_timeout")  |
      param_name.equals("minimum_expiry_time")  |         
      param_name.equals("negative_ttl")  |       
      param_name.equals("negative_dns_ttl")  |  
      param_name.equals("netdb_ping_period")  |          
      param_name.equals("pconn_timeout")  |          	
      param_name.equals("peer_connect_timeout")  |    
      param_name.equals("persistent_request_timeout")  |          
      param_name.equals("positive_dns_ttl")  |
      param_name.equals("read_timeout")  | 
      param_name.equals("refresh_stale_hit")  |          
      param_name.equals("request_timeout")  |
      param_name.equals("shutdown_lifetime") )
         return "once.time";          
 
  if (param_name.equals("cache_mem")  | 
      param_name.equals("high_memory_warning")  |          
      param_name.equals("maximum_object_size")  |
      param_name.equals("maximum_object_size_in_memory")  | 
      param_name.equals("minimum_object_size")  |          
      param_name.equals("memory_pools_limit")  |          
      param_name.equals("quick_abort_min")  |
      param_name.equals("quick_abort_max")  |   
      param_name.equals("range_offset_limit")  |          
      param_name.equals("read_ahead_gap")  |
      param_name.equals("reply_body_max_size")  | 
      param_name.equals("reply_header_max_size")  |          
      param_name.equals("request_body_max_size")  |
      param_name.equals("request_header_max_size")  |                   
      param_name.equals("store_avg_object_size")  |          
      param_name.equals("tcp_recv_bufsize")  )
         return "once.size";          
   
  if (param_name.equals("client_netmask")  | 
      param_name.equals("mcast_miss_addr")  | 
      param_name.equals("snmp_incoming_address")  |          
      param_name.equals("snmp_outgoing_address")  |
      param_name.equals("udp_incoming_address")  |   
      param_name.equals("udp_outgoing_address")  |          
      param_name.equals("wccp2_address")  |
      param_name.equals("wccp_address")  | 
      param_name.equals("wccp2_router") |
      param_name.equals("wccp_router"))
         return "once.ip"; 
        
  if (param_name.equals("cache_dir")  | 
      param_name.equals("cache_replacement_policy")  |          
    //  param_name.equals("cache_peer_domain")  |           
      param_name.equals("http_port")  | 
      param_name.equals("https_port")  |  
      param_name.equals("memory_replacement_policy")  |  
    //  param_name.equals("neighbor_type_domain")  |            
      param_name.equals("sslproxy_flags")  |
      param_name.equals("store_dir_select_algorithm")  |   
      param_name.equals("uri_whitespace")  |          
      param_name.equals("wccp2_assignment_method")  |
      param_name.equals("wccp2_forwarding_method_method")  ) 
   //   param_name.equals("wccp2_service") |
    //  param_name.equals("wccp2_service_info"))
   return "once.formatted";    
        
  if (param_name.equals("always_direct")  | 
      param_name.equals("broken_posts")  |          
      param_name.equals("broken_vary_encoding")  |
      param_name.equals("cache")  |  
	param_name.equals("no_cache")  | 	
      param_name.equals("cache_peer_access")  |   
      param_name.equals("follow_x_forwarded_for")  |             
      param_name.equals("htcp_access")  |
      param_name.equals("htcp_clr_access")  | 
      param_name.equals("http_access")  |
      param_name.equals("http_access2")  |          
      param_name.equals("http_reply_access")  |
      param_name.equals("icp_access")  |   
      param_name.equals("ident_lookup_access")  |             
      param_name.equals("location_rewrite_access")  |
      param_name.equals("log_access")  |          
      param_name.equals("miss_access")  |
      param_name.equals("never_direct")  | 
      param_name.equals("snmp_access")  |      
      param_name.equals("url_rewrite_access"))
   return "access";  
        
  if (param_name.equals("auth_param basic program")  | 
      param_name.equals("auth_param basic realm")  |          
      param_name.equals("auth_param digest program")  |
      param_name.equals("auth_param digest realm")  |   
      param_name.equals("auth_param ntlm program")  |          
      param_name.equals("auth_param negotiate program"))
   return "auth.str";  
        
   if (param_name.equals("auth_param basic children")  | 
      param_name.equals("auth_param basic concurrency")  |          
      param_name.equals("auth_param digest children")  |
      param_name.equals("auth_param digest concurrency")  |            
      param_name.equals("auth_param digest nonce_max_count")  |   
      param_name.equals("auth_param ntlm children")  |  
      param_name.equals("auth_param negotiate children"))
   return "auth.int";  
        
   if (param_name.equals("auth_param basic casesensitive")  | 
      param_name.equals("auth_param basic blankpassword")  |          
      param_name.equals("auth_param digest nonce_strictness")  |
      param_name.equals("auth_param digest check_nonce_count")  |   
      param_name.equals("auth_param digest post_workaround")  |  
      param_name.equals("auth_param ntlm keep_alive")  |          
      param_name.equals("auth_param negotiate keep_alive"))
   return "auth.bool";    
        
   if (param_name.equals("auth_param basic credentialsttl")  | 
      param_name.equals("auth_param digest nonce_garbage_interval")  |          
      param_name.equals("auth_param digest nonce_max_duration"))
   return "auth.time";   
        
        
        
        
        return "unknown";
    }
}
