/*
 * JSquidModelView.java
 */
 
package jsquidmodel;
 
 
import com.jcraft.jsch.*;
import java.awt.Component;
import java.awt.print.PrinterException;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.sql.*;
import java.util.Arrays;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import jsquidmodel.JSquidModelAboutBox;

/**
 * The application's main frame.
 */
public class JSquidModelView extends FrameView {
    String AdminDbName = "none", AdminDBDriver, AdminDBConnectionString, AdminDBUser, AdminDBPassword, AdminDBStatus = "disconnect";
    String LogDbName ="none", LogBDriver, LogDBConnectionString, LogDBUser, LogDBPassword, LogDBStatus = "disconnect";
    String UnixConnName ="none" ,UnixHost = "<none>", UnixSSHPort, UnixUser, UnixPassword, UnixSquidConf, UnixSquidExec;
    String currentFileName ="<new>";

   // Hashtable MainParams = new Hashtable();
    Connection connectionAdmin, connectionLog;
    Properties once_params = new Properties();


    DefaultMutableTreeNode treeroot = new DefaultMutableTreeNode("SquidModel");


    DefaultTreeModel       tree       = new DefaultTreeModel(treeroot);
    DefaultMutableTreeNode alloptions = new DefaultMutableTreeNode("squid.conf");
    DefaultMutableTreeNode myoptions  = new DefaultMutableTreeNode("Advanced configuration");
    DefaultMutableTreeNode delays     = new DefaultMutableTreeNode("Delays");
    DefaultMutableTreeNode acl     = new DefaultMutableTreeNode("acl");
    DefaultMutableTreeNode auth_param     = new DefaultMutableTreeNode("auth_param");
    DefaultMutableTreeNode cache_peer     = new DefaultMutableTreeNode("cache_peer");
    DefaultMutableTreeNode external_acl_type     = new DefaultMutableTreeNode("external_acl_type");
    DefaultMutableTreeNode tcp_outgoing_addr     = new DefaultMutableTreeNode("tcp_outgoing_address");
    DefaultMutableTreeNode tcp_outgoing_tos     = new DefaultMutableTreeNode("tcp_outgoing_tos");
    DefaultMutableTreeNode logformat     = new DefaultMutableTreeNode("logformat");
    DefaultMutableTreeNode access_log     = new DefaultMutableTreeNode("access_log");
    DefaultMutableTreeNode refresh_pattern     = new DefaultMutableTreeNode("refresh_pattern");
    DefaultMutableTreeNode header_access     = new DefaultMutableTreeNode("header_access");
    DefaultMutableTreeNode header_replace     = new DefaultMutableTreeNode("header_replace");


    DefaultMutableTreeNode Connections = new DefaultMutableTreeNode("Connections");
    DefaultMutableTreeNode admin = new DefaultMutableTreeNode("My administration");

    DefaultMutableTreeNode currentNode = new DefaultMutableTreeNode();
    SquidDefaultVector sshconn = new SquidDefaultVector("#ssh");
    SquidDefaultVector dbconn = new SquidDefaultVector("#db");



    /*        */

    SquidConf squidConf = new SquidConf();
    String currentParamName = "", currentAcl = "", currentAuthParam = "";
    String curremtDelayNum = "";
    String currentCachePeerName = "";
    String current_external_acl_type = "";
    String current_tcp_outgoing_addr = "";
    String current_tcp_outgoing_tos = "";
    String current_AccessName = "";
    String current_LogFormat = "";
    String current_AccessLog = "";
    String current_refresh_pattern = "";
    String current_header_replace = "";
    String lastSearchWord = "";
    int	   lastSearchCount = 0;

    String currentDefaultGroupName = "";
    String currentDefaultName = "";
    
    String currentGroupName = "";
    String currentUserName = "";
    
    String confFile = "";
    TStrings conf = new TStrings();
    String currentSshConn="", currentDBconn = "";
    String removedGruops = "", addedGroups = "";
    String defaultSSH = "";

    String[] err_pages = {
"ERR_ACCESS_DENIED",
"ERR_CACHE_ACCESS_DENIED",
"ERR_CACHE_MGR_ACCESS_DENIED",
"ERR_CANNOT_FORWARD",
"ERR_CONNECT_FAIL",
"ERR_DNS_FAIL",
"ERR_FORWARDING_DENIED",
"ERR_FTP_DISABLED",
"ERR_FTP_FAILURE",
"ERR_FTP_FORBIDDEN",
"ERR_FTP_NOT_FOUND",
"ERR_FTP_PUT_CREATED",
"ERR_FTP_PUT_ERROR",
"ERR_FTP_PUT_MODIFIED",
"ERR_FTP_UNAVAILABLE",
"ERR_INVALID_REQ",
"ERR_INVALID_RESP",
"ERR_INVALID_URL",
"ERR_LIFETIME_EXP",
"ERR_NO_RELAY",
"ERR_ONLY_IF_CACHED_MISS",
"ERR_READ_ERROR",
"ERR_READ_TIMEOUT",
"ERR_SHUTTING_DOWN",
"ERR_SOCKET_FAILURE",
"ERR_TOO_BIG",
"ERR_UNSUP_REQ",
"ERR_URN_RESOLVE",
"ERR_WRITE_ERROR",
"ERR_ZERO_SIZE_OBJECT"    
    };
    
    String[] http_status_arg = {
    "100 Continue",
"101 Switching Protocols",
"200 OK",
"201 Created",
"202 Accepted",
"203 Non-Authoritative Information",
"204 No Content",
"205 Reset Content",
"206 Partial Content",
"300 Multiple Choices",
"301 Moved Permanently",
"302 Moved Temporarily",
"303 See Other",
"304 Not Modified",
"305 Use Proxy",
"400 Bad Request",
"401 Unauthorized",
"402 Payment Required",
"403 Forbidden",
"404 Not Found",
"405 Method Not Allowed",
"406 Not Acceptable",
"407 Proxy Authentication Required",
"408 Request Timeout",
"409 Conflict",
"410 Gone",
"411 Length Required",
"412 Precondition Failed",
"413 Request Entity Too Large",
"414 Request-URI Too Long",
"415 Unsupported Media Type",
"500 Internal Server Error",
"501 Not Implemented",
"502 Bad Gateway",
"503 Service Unavailable",
"504 Gateway Timeout",
"505 HTTP Version Not Supported"
    };
    
//java.util.ResourceBundle.getBundle("jiptables/resources/JIptablesView").getString("newkey"));    


class MyRenderer extends DefaultTreeCellRenderer {
    Icon tutorialIcon;
    Icon admin_icon, user_icon, ssh_host_icon, db_icon, param_exists;

    public MyRenderer(Icon icon) {
        tutorialIcon = icon;
    }
    
    public MyRenderer(Icon admin, Icon user, Icon ssh, Icon db) {
        //tutorialIcon = icon;
        admin_icon = admin;
        user_icon = user;
        ssh_host_icon = ssh;
        db_icon = db;
    }
    

    public Component getTreeCellRendererComponent(
                        JTree tree,
                        Object value,
                        boolean sel,
                        boolean expanded,
                        boolean leaf,
                        int row,
                        boolean hasFocus) {

        super.getTreeCellRendererComponent(
                        tree, value, sel,
                        expanded, leaf, row,
                        hasFocus);
        
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        if (node.getParent() == null) return this;
        
        String caption = node.toString();
        
        if (node.getParent().toString().equals("SSH")) {
            if (node.toString().equals(defaultSSH))
            setIcon( createImageIcon("resources/img16x16/server_def2.png"));
            else setIcon( createImageIcon("resources/img16x16/server.png"));
        }

        
        
        if (node.toString().equals("My administration")) setIcon( createImageIcon("resources/img16x16/favorites.png"));
        if (node.getParent().toString().equals("My users")) setIcon( createImageIcon("resources/img16x16/edit_user.png"));
        if (node.getParent().toString().equals("My groups")) setIcon( createImageIcon("resources/img16x16/show_offliners.png"));
        
        if (node.getParent() != null)
            if (node.getParent().getParent() != null)
                if (node.getParent().getParent().toString().equals("My groups")) setIcon( createImageIcon("resources/img16x16/edit_user.png"));
        
        if (node.getParent() != null)
            if (node.getParent().getParent() != null)
                if (node.getParent().getParent().toString().equals("auth_param")) {
                    
                    String full_name = "auth_param " + node.getParent() + " " + caption;
                    if (squidConf.getOnce(full_name) != null) setIcon( createImageIcon("resources/img16x16/ok.png"));
                    
        
                }
        
        if (node.getParent() != null)
            if (node.getParent().toString().equals("acl")){
                String image = "resources/img16x16/templates.png";
                int ix = squidConf.getAclIndex(node.toString());
                SquidAcl sa = squidConf.acl.get(ix);
                
                if (sa.type.equals("proxy_auth")) image = "resources/img16x16/show_offliners.png";
                if (sa.type.equals("time")) image = "resources/img16x16/clock.png";
                if (sa.type.equals("port")) image = "resources/img16x16/stock_connect-to-url.png";
                if (sa.type.equals("src") | sa.type.equals("dst")) image = "resources/img16x16/mycomputer.png"; 
                
                setIcon( createImageIcon(image));
            }
        
        
        if (node.getParent() != null)
            if (node.getParent().toString().equals("Delays")){
                String image = "resources/img16x16/pipe.png";
 
                setIcon( createImageIcon(image));
            }      
        
        if (node.getParent() != null)
            if (node.getParent().toString().equals("deny_info") | node.getParent().toString().equals("error_map")){
                String image = "resources/img16x16/cnrdelete-all.png";
 
                setIcon( createImageIcon(image));
            }  
        
        if (node.getParent() != null)
            if (node.getParent().toString().equals("access_log") ){
                String image = "resources/img16x16/shellscript.png";
 
                setIcon( createImageIcon(image));
            }  
        
        if (node.getParent() != null)
            if (node.getParent().toString().equals("logformat") ){
                String image = "resources/img16x16/source.png";
 
                setIcon( createImageIcon(image));
            }  
        
                if (node.getParent() != null)
            if (node.getParent().toString().equals("external_acl_type") ){
                String image = "resources/img16x16/readme.png";
 
                setIcon( createImageIcon(image));
            }  
        
        if (node.getParent() != null)
            if (node.getParent().toString().equals("cache_peer")){
                String image = "resources/img16x16/proxy.png";
 
                setIcon( createImageIcon(image));
            } 
            
        
        
        
        if (squidConf.getParamTypeFromName(caption).indexOf("once.") >= 0               
                ) {
            if (squidConf.getOnce(node.toString()) != null) setIcon( createImageIcon("resources/img16x16/ok.png"));
        }
        
        if (squidConf.getParamTypeFromName(caption).equals("access"))        
            if (squidConf.getAccessGroupIndex(caption) > -1 )setIcon( createImageIcon("resources/img16x16/ok.png"));
                
            
        
        
        
        
        
        
      //  setIcon(tutorialIcon);
        /*if (leaf && isTutorialBook(value)) {
            setIcon(tutorialIcon);
            setToolTipText("This book is in the Tutorial series.");
        } else {
            setToolTipText(null); //no tool tip
        }*/ 

        return this;
    }

    protected boolean isTutorialBook(Object value) {
        /*DefaultMutableTreeNode node =
                (DefaultMutableTreeNode)value;
        BookInfo nodeInfo =
                (BookInfo)(node.getUserObject());
        String title = nodeInfo.bookName;
        
        if (title.indexOf("Tutorial") >= 0) {
            return true;
        }*/

        return false;
    }
}



    public JSquidModelView(SingleFrameApplication app) {
        super(app);

       // MainParams.put("AdminDBDriver", "AdminDBCconnectionString");



        initComponents();


        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        //FreePropertiesPage();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String)(evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer)(evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });
       // FreePropertiesPage();
    }

    @Action
    public void FreePropertiesPage(){

    }

    @Action
    public void makeConnectionAdmin(){
        /* "org.firebirdsql.jdbc.FBDriver",
                "jdbc:firebirdsql://localhost:3050/iplog",
        String driver = jTextField18.getText();
        jCheckBox8.setSelected(false);

        try{
            Class.forName(driver);

            try {
                 connectionAdmin = DriverManager.getConnection(
                         jTextField19.getText() ,
                         jTextField20.getText(),
                         jPasswordField1.getText());
                JOptionPane.showMessageDialog(
                    jComboBox1,
                    "Status: Connected",
                    "Admin database on " + driver,
                    JOptionPane.INFORMATION_MESSAGE);
                jCheckBox8.setSelected(true);
            }
            catch (SQLException a) {
            JOptionPane.showMessageDialog(
                    jComboBox1,
                    "SQL Connection error: "+a.toString(),
                    "Error: SQL connection",
                    JOptionPane.ERROR_MESSAGE);
                a.printStackTrace();
            }

        }
        catch (ClassNotFoundException e){
            JOptionPane.showMessageDialog(
                    jComboBox1,
                    "Driver load ERROR: "+e.toString(),
                    "Error load Database Driver",
                    JOptionPane.ERROR_MESSAGE);

            e.printStackTrace();
        }*/







    }

    public void loadResourceHTML(JEditorPane e, String file){
       // e.setText("Load information. Please wait...");
      //  e.repaint();
        String Prefix = "file:/home/xvit/NetBeansProjects/jSquidModel/src/jsquidmodel/";
      //  file  = /*Prefix +*/ file;
       /*  try {

                e.setPage(file);
                //e.setPage(url);
            }
            catch (IOException err) {
                System.out.println(err.getMessage());

                JOptionPane.showMessageDialog(mainPanel, err.getMessage(), file, JOptionPane.WARNING_MESSAGE);

                //err.printStackTrace();
            }
 */

        java.net.URL url = JSquidModelView.class.getResource(file);
      if (url != null) {
            try {
               // e.setPage(file);
                e.setPage(url);
            }
            catch (IOException err) {
                System.out.println(err.getMessage());

                JOptionPane.showMessageDialog(mainPanel, err.getMessage(), file, JOptionPane.WARNING_MESSAGE);

                //err.printStackTrace();
            }
      }
        else {
            System.out.println(file + ": not found");
            JOptionPane.showMessageDialog(mainPanel, file + " - \nFile not found!", file, JOptionPane.WARNING_MESSAGE);
        }
       // e.setText("Load information. Please wait..."); */
    }

    public String GetSQLValue(String s, Connection c){

        try {

        ResultSet r = c.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY).executeQuery(s);
        return r.getString(0);
        //r.cl

        }
        catch (Exception e){

        }



        return "U";
    }

    public Boolean readBoolParam(String param){
        if (connectionAdmin == null)  return null;
        if (GetSQLValue("select BOOL_BALUE from ONCE_PARAM where PARAM_NAME='"+ param +"'", connectionAdmin).equals("Y"))
        return null;
        //connectionAdmin.prepareStatement("select * from ONCE_PARAM where PARAM_NAME='"+param+"'").

        return null;
    }

    public String  readStrParam(String param){
        return squidConf.getOnce(param);
       // null;
    }

    public Integer readIntParam(String param){
        return 0;
    }

    public String getParamType_DISABLED(String param_name){
      //  String type="once.str";

        if (param_name.equals("acl_uses_indirect_client") |
            param_name.equals("allow_underscore") |
            param_name.equals("balance_on_multiple_ip") |
            param_name.equals("cache_vary") |
            param_name.equals("check_hostnames") |
            param_name.equals("client_db") |
            param_name.equals("client_persistent_connection") |
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
            param_name.equals("httpd_supress_version_string") |
            param_name.equals("icp_hit_scale") |
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
            param_name.equals("short_icon_url") |
            param_name.equals("ssl_unclean_shutdown") |
            param_name.equals("strip_query_terms") |
            param_name.equals("test_richability") |
            param_name.equals("url_rewrite_host_header") |
            param_name.equals("vary_ignore_expire") |
            param_name.equals("via") |
            param_name.equals("wccp2_rebuild_wait")
            ) return "once.bool";

         if (param_name.equals("announce_host") |
            param_name.equals("append_domain") |
            param_name.equals("as_whois_server") |
            param_name.equals("cache_effective_group") |
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
            param_name.equals("ssl_engine") |
            param_name.equals("ssl_proxy_cipher") |
            param_name.equals("ssl_proxy_client_certificate") |
            param_name.equals("sslproxy_client_key") |
            param_name.equals("ssl_proxy_version") |
            param_name.equals("umask") |
            param_name.equals("unique_hostname") |
            param_name.equals("visible_hostname") |
            param_name.equals("wccp2_router") |
            param_name.equals("wccp_router")

           ) return "once.str";
         if (param_name.equals("cache_swap_high")  |
            param_name.equals("cache_swap_low") |
            param_name.equals("delay_initial_bucket_level") |
            param_name.equals("digest_rebuild_percentage") |
            param_name.equals("quick_abort_pct")    )
         return "once.percent";





        return "unknown";
    }



    @Action
    public void reloadDelaysSection(){
        delays.removeAllChildren();
        for (Integer i = 0; i < squidConf.delays.size(); i++) {
            //SquidAccess sa = (SquidAccess) squidConf.delays.get(i);
            delays.add( new DefaultMutableTreeNode(squidConf.delays.get(i).pos));

        }
        tree.reload(delays);
    }

    @Action
    public void reloadCachePeerSection(){
        cache_peer.removeAllChildren();

        for (int i = 0; i < squidConf.cache_peers.size(); i++){
            SquidCachePeer sc = squidConf.cache_peers.get(i);
            cache_peer.add(new DefaultMutableTreeNode(sc.hostname));
        }


        tree.reload(cache_peer);
    }

    @Action
    public void realodAclSection(){
        acl.removeAllChildren();


        if (squidConf.acl.size() == 0) return;

        StringTokenizer st = new StringTokenizer(squidConf.getSortAclString());
        
        while (st.hasMoreTokens()){
            String n = st.nextToken();
            if (n.length() > 0 && !n.equals(""))
                acl.add(new DefaultMutableTreeNode(n));
        }
        
         
        
       /* for (Integer i=0; i < squidConf.acl.size(); i++){
            SquidAcl ac = (SquidAcl)squidConf.acl.get(i);
          //  System.out.println("realodAclSection: " + ac.name);
            acl.add(new DefaultMutableTreeNode(ac.name));

        } */
        tree.reload(acl);
    }
    
    public String getMsg(String key){
        return java.util.ResourceBundle.getBundle("jsquidmodel/resources/JSquidModelView").getString(key);
    }

    @Action
    public void load_squid_options(){
        alloptions.removeAllChildren();

DefaultMutableTreeNode a1 = new DefaultMutableTreeNode(getMsg("SquidCategory.Name.1").toLowerCase());
     alloptions.add(a1);
     a1.add(auth_param);
     a1.add(new DefaultMutableTreeNode("authenticate_cache_garbage_interval"));
     a1.add(new DefaultMutableTreeNode("authenticate_ttl"));
     a1.add(new DefaultMutableTreeNode("authenticate_ip_ttl"));
DefaultMutableTreeNode a2 = new DefaultMutableTreeNode(getMsg("SquidCategory.Name.2").toLowerCase());
     alloptions.add(a2);
     a2.add(external_acl_type);
     a2.add(acl);
     a2.add(new DefaultMutableTreeNode("http_access"));
     a2.add(new DefaultMutableTreeNode("http_access2"));
     a2.add(new DefaultMutableTreeNode("http_reply_access"));
     a2.add(new DefaultMutableTreeNode("icp_access"));
     a2.add(new DefaultMutableTreeNode("htcp_access"));
     a2.add(new DefaultMutableTreeNode("htcp_clr_access"));
     a2.add(new DefaultMutableTreeNode("miss_access"));
     a2.add(new DefaultMutableTreeNode("ident_lookup_access"));
     a2.add(new DefaultMutableTreeNode("reply_body_max_size"));
DefaultMutableTreeNode a3 = new DefaultMutableTreeNode("3. OPTIONS FOR X-Forwarded-For".toLowerCase());
     alloptions.add(a3);
     a3.add(new DefaultMutableTreeNode("follow_x_forwarded_for"));
     a3.add(new DefaultMutableTreeNode("acl_uses_indirect_client"));
     a3.add(new DefaultMutableTreeNode("delay_pool_uses_indirect_client"));
     a3.add(new DefaultMutableTreeNode("log_uses_indirect_client"));
DefaultMutableTreeNode a4 = new DefaultMutableTreeNode("4. NETWORK OPTIONS".toLowerCase());
     alloptions.add(a4);
     a4.add(new DefaultMutableTreeNode("http_port"));
     a4.add(new DefaultMutableTreeNode("https_port"));
     a4.add(tcp_outgoing_tos);
     a4.add(tcp_outgoing_addr);

DefaultMutableTreeNode a5 = new DefaultMutableTreeNode("5. SSL OPTIONS".toLowerCase());
     alloptions.add(a5);
     a5.add(new DefaultMutableTreeNode("ssl_unclean_shutdown"));
     a5.add(new DefaultMutableTreeNode("ssl_engine"));
     a5.add(new DefaultMutableTreeNode("sslproxy_client_certificate"));
     a5.add(new DefaultMutableTreeNode("sslproxy_client_key"));
     a5.add(new DefaultMutableTreeNode("sslproxy_version"));
     a5.add(new DefaultMutableTreeNode("sslproxy_options"));
     a5.add(new DefaultMutableTreeNode("sslproxy_cipher"));
     a5.add(new DefaultMutableTreeNode("sslproxy_cafile"));
     a5.add(new DefaultMutableTreeNode("sslproxy_capath"));
     a5.add(new DefaultMutableTreeNode("sslproxy_flags"));
     a5.add(new DefaultMutableTreeNode("sslpassword_program"));
DefaultMutableTreeNode a6 = new DefaultMutableTreeNode("6. OPTIONS WHICH AFFECT THE NEIGHBOR SELECTION ALGORITHM".toLowerCase());
     alloptions.add(a6);
     a6.add(cache_peer);
   //  a6.add(new DefaultMutableTreeNode("cache_peer_domain"));
    // a6.add(new DefaultMutableTreeNode("cache_peer_access"));
    // a6.add(new DefaultMutableTreeNode("neighbor_type_domain"));
     a6.add(new DefaultMutableTreeNode("dead_peer_timeout"));
     a6.add(new DefaultMutableTreeNode("hierarchy_stoplist"));
DefaultMutableTreeNode a7 = new DefaultMutableTreeNode("7. MEMORY CACHE OPTIONS".toLowerCase());
     alloptions.add(a7);
     a7.add(new DefaultMutableTreeNode("cache_mem"));
     a7.add(new DefaultMutableTreeNode("maximum_object_size_in_memory"));
     a7.add(new DefaultMutableTreeNode("memory_replacement_policy"));
DefaultMutableTreeNode a8 = new DefaultMutableTreeNode("8. DISK CACHE OPTIONS".toLowerCase());
     alloptions.add(a8);
     a8.add(new DefaultMutableTreeNode("cache_replacement_policy"));
     a8.add(new DefaultMutableTreeNode("cache_dir"));
     a8.add(new DefaultMutableTreeNode("store_dir_select_algorithm"));
     a8.add(new DefaultMutableTreeNode("max_open_disk_fds"));
     a8.add(new DefaultMutableTreeNode("minimum_object_size"));
     a8.add(new DefaultMutableTreeNode("maximum_object_size"));
     a8.add(new DefaultMutableTreeNode("cache_swap_low"));
     a8.add(new DefaultMutableTreeNode("cache_swap_high"));
DefaultMutableTreeNode a9 = new DefaultMutableTreeNode("9. LOGFILE OPTIONS".toLowerCase());
     alloptions.add(a9);
     a9.add(logformat);
     a9.add(access_log);
     a9.add(new DefaultMutableTreeNode("log_access"));
     a9.add(new DefaultMutableTreeNode("cache_log"));
     a9.add(new DefaultMutableTreeNode("cache_store_log"));
     a9.add(new DefaultMutableTreeNode("cache_swap_state"));
     a9.add(new DefaultMutableTreeNode("logfile_rotate"));
     a9.add(new DefaultMutableTreeNode("emulate_httpd_log"));
     a9.add(new DefaultMutableTreeNode("log_ip_on_direct"));
     a9.add(new DefaultMutableTreeNode("mime_table"));
     a9.add(new DefaultMutableTreeNode("log_mime_hdrs"));
     a9.add(new DefaultMutableTreeNode("useragent_log"));
     a9.add(new DefaultMutableTreeNode("referer_log"));
     a9.add(new DefaultMutableTreeNode("pid_filename"));
     a9.add(new DefaultMutableTreeNode("debug_options"));
     a9.add(new DefaultMutableTreeNode("log_fqdn"));
     a9.add(new DefaultMutableTreeNode("client_netmask"));
     a9.add(new DefaultMutableTreeNode("forward_log"));
     a9.add(new DefaultMutableTreeNode("strip_query_terms"));
     a9.add(new DefaultMutableTreeNode("buffered_logs"));
DefaultMutableTreeNode a10 = new DefaultMutableTreeNode("10. OPTIONS FOR FTP GATEWAYING".toLowerCase());
     alloptions.add(a10);
     a10.add(new DefaultMutableTreeNode("ftp_user"));
     a10.add(new DefaultMutableTreeNode("ftp_list_width"));
     a10.add(new DefaultMutableTreeNode("ftp_passive"));
     a10.add(new DefaultMutableTreeNode("ftp_sanitycheck"));
     a10.add(new DefaultMutableTreeNode("ftp_telnet_protocol"));
DefaultMutableTreeNode a11 = new DefaultMutableTreeNode("11. OPTIONS FOR EXTERNAL SUPPORT PROGRAMS".toLowerCase());
     alloptions.add(a11);
     a11.add(new DefaultMutableTreeNode("diskd_program"));
     a11.add(new DefaultMutableTreeNode("unlinkd_program"));
     a11.add(new DefaultMutableTreeNode("pinger_program"));
DefaultMutableTreeNode a12 = new DefaultMutableTreeNode("12. OPTIONS FOR URL REWRITING".toLowerCase());
     alloptions.add(a12);
     a12.add(new DefaultMutableTreeNode("url_rewrite_program"));
     a12.add(new DefaultMutableTreeNode("url_rewrite_children"));
     a12.add(new DefaultMutableTreeNode("url_rewrite_concurrency"));
     a12.add(new DefaultMutableTreeNode("url_rewrite_host_header"));
     a12.add(new DefaultMutableTreeNode("url_rewrite_access"));
     a12.add(new DefaultMutableTreeNode("redirector_bypass"));
     a12.add(new DefaultMutableTreeNode("location_rewrite_program"));
     a12.add(new DefaultMutableTreeNode("location_rewrite_children"));
     a12.add(new DefaultMutableTreeNode("location_rewrite_concurrency"));
     a12.add(new DefaultMutableTreeNode("location_rewrite_access"));
DefaultMutableTreeNode a13 = new DefaultMutableTreeNode("13. OPTIONS FOR TUNING THE CACHE".toLowerCase());
     alloptions.add(a13);
     a13.add(new DefaultMutableTreeNode("cache"));
     a13.add(refresh_pattern);
     a13.add(new DefaultMutableTreeNode("quick_abort_min"));
     a13.add(new DefaultMutableTreeNode("quick_abort_max"));
     a13.add(new DefaultMutableTreeNode("quick_abort_pct"));
     a13.add(new DefaultMutableTreeNode("read_ahead_gap"));
     a13.add(new DefaultMutableTreeNode("negative_ttl"));
     a13.add(new DefaultMutableTreeNode("positive_dns_ttl"));
     a13.add(new DefaultMutableTreeNode("negative_dns_ttl"));
     a13.add(new DefaultMutableTreeNode("range_offset_limit"));
     a13.add(new DefaultMutableTreeNode("minimum_expiry_time"));
     a13.add(new DefaultMutableTreeNode("store_avg_object_size"));
     a13.add(new DefaultMutableTreeNode("store_objects_per_bucket"));
DefaultMutableTreeNode a14 = new DefaultMutableTreeNode("14. HTTP OPTIONS".toLowerCase());
     alloptions.add(a14);
     a14.add(new DefaultMutableTreeNode("request_header_max_size"));
     a14.add(new DefaultMutableTreeNode("reply_header_max_size"));
     a14.add(new DefaultMutableTreeNode("request_body_max_size"));
     a14.add(new DefaultMutableTreeNode("broken_posts"));
     a14.add(new DefaultMutableTreeNode("via"));
     a14.add(new DefaultMutableTreeNode("cache_vary"));
     a14.add(new DefaultMutableTreeNode("broken_vary_encoding"));
     a14.add(new DefaultMutableTreeNode("collapsed_forwarding"));
     a14.add(new DefaultMutableTreeNode("refresh_stale_hit"));
     a14.add(new DefaultMutableTreeNode("ie_refresh"));
     a14.add(new DefaultMutableTreeNode("vary_ignore_expire"));
     a14.add(new DefaultMutableTreeNode("extension_methods"));
     a14.add(new DefaultMutableTreeNode("request_entities"));
     a14.add(header_access);
     a14.add(header_replace);
     a14.add(new DefaultMutableTreeNode("relaxed_header_parser"));
DefaultMutableTreeNode a15 = new DefaultMutableTreeNode("15. TIMEOUTS".toLowerCase());
     alloptions.add(a15);
     a15.add(new DefaultMutableTreeNode("forward_timeout"));
     a15.add(new DefaultMutableTreeNode("connect_timeout"));
     a15.add(new DefaultMutableTreeNode("peer_connect_timeout"));
     a15.add(new DefaultMutableTreeNode("read_timeout"));
     a15.add(new DefaultMutableTreeNode("request_timeout"));
     a15.add(new DefaultMutableTreeNode("persistent_request_timeout"));
     a15.add(new DefaultMutableTreeNode("client_lifetime"));
     a15.add(new DefaultMutableTreeNode("half_closed_clients"));
     a15.add(new DefaultMutableTreeNode("pconn_timeout"));
     a15.add(new DefaultMutableTreeNode("ident_timeout"));
     a15.add(new DefaultMutableTreeNode("shutdown_lifetime"));
DefaultMutableTreeNode a16 = new DefaultMutableTreeNode("16. ADMINISTRATIVE PARAMETERS".toLowerCase());
     alloptions.add(a16);
     a16.add(new DefaultMutableTreeNode("cache_mgr"));
     a16.add(new DefaultMutableTreeNode("mail_from"));
     a16.add(new DefaultMutableTreeNode("mail_program"));
     a16.add(new DefaultMutableTreeNode("cache_effective_user"));
     a16.add(new DefaultMutableTreeNode("cache_effective_group"));
     a16.add(new DefaultMutableTreeNode("httpd_suppress_version_string"));
     a16.add(new DefaultMutableTreeNode("visible_hostname"));
     a16.add(new DefaultMutableTreeNode("unique_hostname"));
     a16.add(new DefaultMutableTreeNode("hostname_aliases"));
     a16.add(new DefaultMutableTreeNode("umask"));
DefaultMutableTreeNode a17 = new DefaultMutableTreeNode("17. OPTIONS FOR THE CACHE REGISTRATION SERVICE".toLowerCase());
     alloptions.add(a17);
     a17.add(new DefaultMutableTreeNode("announce_period"));
     a17.add(new DefaultMutableTreeNode("announce_host"));
     a17.add(new DefaultMutableTreeNode("announce_file"));
     a17.add(new DefaultMutableTreeNode("announce_port"));
DefaultMutableTreeNode a18 = new DefaultMutableTreeNode("18. HTTPD-ACCELERATOR OPTIONS".toLowerCase());
     alloptions.add(a18);
     a18.add(new DefaultMutableTreeNode("httpd_accel_no_pmtu_disc"));

DefaultMutableTreeNode a19 = new DefaultMutableTreeNode("19. D" +
        "ELAY POOL PARAMETERS".toLowerCase());

     alloptions.add(a19);
 /*    a19.add(new DefaultMutableTreeNode("delay_pools"));
     a19.add(new DefaultMutableTreeNode("delay_class"));
     a19.add(new DefaultMutableTreeNode("delay_access"));
     a19.add(new DefaultMutableTreeNode("delay_parameters"));
     a19.add(new DefaultMutableTreeNode("delay_initial_bucket_level")); */
     a19.add(delays);
     reloadDelaysSection();
     a19.add(new DefaultMutableTreeNode("delay_initial_bucket_level"));


DefaultMutableTreeNode a20 = new DefaultMutableTreeNode("20. WCCPv1 AND WCCPv2 CONFIGURATION OPTIONS".toLowerCase());
     alloptions.add(a20);
     a20.add(new DefaultMutableTreeNode("wccp_router"));
     a20.add(new DefaultMutableTreeNode("wccp2_router"));
     a20.add(new DefaultMutableTreeNode("wccp_version"));
     a20.add(new DefaultMutableTreeNode("wccp2_rebuild_wait"));
     a20.add(new DefaultMutableTreeNode("wccp2_forwarding_method"));
     a20.add(new DefaultMutableTreeNode("wccp2_return_method"));
     a20.add(new DefaultMutableTreeNode("wccp2_assignment_method"));
     a20.add(new DefaultMutableTreeNode("wccp2_service"));
     a20.add(new DefaultMutableTreeNode("wccp2_service_info"));
     a20.add(new DefaultMutableTreeNode("wccp2_weight"));
     a20.add(new DefaultMutableTreeNode("wccp_address"));
     a20.add(new DefaultMutableTreeNode("wccp2_address"));
DefaultMutableTreeNode a21 = new DefaultMutableTreeNode("21. PERSISTENT CONNECTION HANDLING".toLowerCase());
     alloptions.add(a21);
     a21.add(new DefaultMutableTreeNode("client_persistent_connections"));
     a21.add(new DefaultMutableTreeNode("server_persistent_connections"));
     a21.add(new DefaultMutableTreeNode("persistent_connection_after_error"));
     a21.add(new DefaultMutableTreeNode("detect_broken_pconn"));
DefaultMutableTreeNode a22 = new DefaultMutableTreeNode("22. CACHE DIGEST OPTIONS".toLowerCase());
     alloptions.add(a22);
     a22.add(new DefaultMutableTreeNode("digest_generation"));
     a22.add(new DefaultMutableTreeNode("digest_bits_per_entry"));
     a22.add(new DefaultMutableTreeNode("digest_rebuild_period"));
     a22.add(new DefaultMutableTreeNode("digest_rewrite_period"));
     a22.add(new DefaultMutableTreeNode("digest_swapout_chunk_size"));
     a22.add(new DefaultMutableTreeNode("digest_rebuild_chunk_percentage"));
DefaultMutableTreeNode a23 = new DefaultMutableTreeNode("23. SNMP OPTIONS".toLowerCase());
     alloptions.add(a23);
     a23.add(new DefaultMutableTreeNode("snmp_port"));
     a23.add(new DefaultMutableTreeNode("snmp_access"));
     a23.add(new DefaultMutableTreeNode("snmp_incoming_address"));
     a23.add(new DefaultMutableTreeNode("snmp_outgoing_address"));
DefaultMutableTreeNode a24 = new DefaultMutableTreeNode("24. ICP OPTIONS".toLowerCase());
     alloptions.add(a24);
     a24.add(new DefaultMutableTreeNode("icp_port"));
     a24.add(new DefaultMutableTreeNode("htcp_port"));
     a24.add(new DefaultMutableTreeNode("log_icp_queries"));
     a24.add(new DefaultMutableTreeNode("udp_incoming_address"));
     a24.add(new DefaultMutableTreeNode("udp_outgoing_address"));
     a24.add(new DefaultMutableTreeNode("icp_hit_stale"));
     a24.add(new DefaultMutableTreeNode("minimum_direct_hops"));
     a24.add(new DefaultMutableTreeNode("minimum_direct_rtt"));
     a24.add(new DefaultMutableTreeNode("netdb_low"));
     a24.add(new DefaultMutableTreeNode("netdb_high"));
     a24.add(new DefaultMutableTreeNode("netdb_ping_period"));
     a24.add(new DefaultMutableTreeNode("query_icmp"));
     a24.add(new DefaultMutableTreeNode("test_reachability"));
     a24.add(new DefaultMutableTreeNode("icp_query_timeout"));
     a24.add(new DefaultMutableTreeNode("maximum_icp_query_timeout"));
DefaultMutableTreeNode a25 = new DefaultMutableTreeNode("25. MULTICAST ICP OPTIONS".toLowerCase());
     alloptions.add(a25);
     a25.add(new DefaultMutableTreeNode("mcast_groups"));
     a25.add(new DefaultMutableTreeNode("mcast_miss_addr"));
     a25.add(new DefaultMutableTreeNode("mcast_miss_ttl"));
     a25.add(new DefaultMutableTreeNode("mcast_miss_port"));
     a25.add(new DefaultMutableTreeNode("mcast_miss_encode_key"));
     a25.add(new DefaultMutableTreeNode("mcast_icp_query_timeout"));
DefaultMutableTreeNode a26 = new DefaultMutableTreeNode("26. INTERNAL ICON OPTIONS".toLowerCase());
     alloptions.add(a26);
     a26.add(new DefaultMutableTreeNode("icon_directory"));
     a26.add(new DefaultMutableTreeNode("global_internal_static"));
     a26.add(new DefaultMutableTreeNode("short_icon_urls"));
DefaultMutableTreeNode a27 = new DefaultMutableTreeNode("27. ERROR PAGE OPTIONS".toLowerCase());
     alloptions.add(a27);
     a27.add(new DefaultMutableTreeNode("error_directory"));
     a27.add(new DefaultMutableTreeNode("error_map"));
     a27.add(new DefaultMutableTreeNode("err_html_text"));
     a27.add(new DefaultMutableTreeNode("deny_info"));
DefaultMutableTreeNode a28 = new DefaultMutableTreeNode("28. OPTIONS INFLUENCING REQUEST FORWARDING".toLowerCase());
     alloptions.add(a28);
     a28.add(new DefaultMutableTreeNode("nonhierarchical_direct"));
     a28.add(new DefaultMutableTreeNode("prefer_direct"));
     a28.add(new DefaultMutableTreeNode("always_direct"));
     a28.add(new DefaultMutableTreeNode("never_direct"));
DefaultMutableTreeNode a29 = new DefaultMutableTreeNode("29. ADVANCED NETWORKING OPTIONS".toLowerCase());
     alloptions.add(a29);
     a29.add(new DefaultMutableTreeNode("incoming_icp_average"));
     a29.add(new DefaultMutableTreeNode("incoming_http_average"));
     a29.add(new DefaultMutableTreeNode("incoming_dns_average"));
     a29.add(new DefaultMutableTreeNode("min_icp_poll_cnt"));
     a29.add(new DefaultMutableTreeNode("min_dns_poll_cnt"));
     a29.add(new DefaultMutableTreeNode("min_http_poll_cnt"));
     a29.add(new DefaultMutableTreeNode("tcp_recv_bufsize"));
DefaultMutableTreeNode a30 = new DefaultMutableTreeNode("30. DNS OPTIONS".toLowerCase());
     alloptions.add(a30);
     a30.add(new DefaultMutableTreeNode("check_hostnames"));
     a30.add(new DefaultMutableTreeNode("allow_underscore"));
     a30.add(new DefaultMutableTreeNode("cache_dns_program"));
     a30.add(new DefaultMutableTreeNode("dns_children"));
     a30.add(new DefaultMutableTreeNode("dns_retransmit_interval"));
     a30.add(new DefaultMutableTreeNode("dns_timeout"));
     a30.add(new DefaultMutableTreeNode("dns_defnames"));
     a30.add(new DefaultMutableTreeNode("dns_nameservers"));
     a30.add(new DefaultMutableTreeNode("hosts_file"));
     a30.add(new DefaultMutableTreeNode("dns_testnames"));
     a30.add(new DefaultMutableTreeNode("append_domain"));
     a30.add(new DefaultMutableTreeNode("ignore_unknown_nameservers"));
     a30.add(new DefaultMutableTreeNode("ipcache_size"));
     a30.add(new DefaultMutableTreeNode("ipcache_low"));
     a30.add(new DefaultMutableTreeNode("ipcache_high"));
     a30.add(new DefaultMutableTreeNode("fqdncache_size"));
DefaultMutableTreeNode a31 = new DefaultMutableTreeNode("31. MISCELLANEOUS".toLowerCase());
     alloptions.add(a31);
     a31.add(new DefaultMutableTreeNode("memory_pools"));
     a31.add(new DefaultMutableTreeNode("memory_pools_limit"));
     a31.add(new DefaultMutableTreeNode("forwarded_for"));
     a31.add(new DefaultMutableTreeNode("cachemgr_passwd"));
     a31.add(new DefaultMutableTreeNode("client_db"));
     a31.add(new DefaultMutableTreeNode("reload_into_ims"));
     a31.add(new DefaultMutableTreeNode("maximum_single_addr_tries"));
     a31.add(new DefaultMutableTreeNode("retry_on_error"));
     a31.add(new DefaultMutableTreeNode("as_whois_server"));
     a31.add(new DefaultMutableTreeNode("offline_mode"));
     a31.add(new DefaultMutableTreeNode("uri_whitespace"));
     a31.add(new DefaultMutableTreeNode("coredump_dir"));
     a31.add(new DefaultMutableTreeNode("chroot"));
     a31.add(new DefaultMutableTreeNode("balance_on_multiple_ip"));
     a31.add(new DefaultMutableTreeNode("pipeline_prefetch"));
     a31.add(new DefaultMutableTreeNode("high_response_time_warning"));
     a31.add(new DefaultMutableTreeNode("high_page_fault_warning"));
     a31.add(new DefaultMutableTreeNode("high_memory_warning"));
     a31.add(new DefaultMutableTreeNode("sleep_after_fork"));
   }

    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = JSquidModelApp.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }    

    @Action
    public void onStartup() throws IOException{

        ImageIcon tutorialIcon = createImageIcon("resources/img16x16/favorites.png");
        
        //if (tutorialIcon == null)
        
        jTree1.setCellRenderer(new MyRenderer(
                createImageIcon("resources/img16x16/favorites.png"),
                createImageIcon("resources/img16x16/kuser.png"),
                createImageIcon("resources/img16x16/server.png"),
                createImageIcon("resources/img16x16/database.png")
                
                ));
        
        jTabbedPane1.removeAll();
        jTabbedPane1.addTab("welcome", welcome);

        squidConf.minimalConf();

        reloadTree() ;
        
        
        
        
        
        confFile = System.getProperty("user.home") + File.separator + 
                "SquidModel.conf";
        //
        
        File f = new File(confFile);
        if (!f.exists()) f.createNewFile();
        conf.loadFromFile(confFile);  
       // JOptionPane.showMessageDialog(mainPanel, conf.getText());
        loadConfFromfile();
        
        relodConn();
        
        jList2.setComponentPopupMenu(popup_user_groups);
       // ImageIcon ico = createImageIcon("resources/img16x16/server.png");
        getFrame().setIconImage(new ImageIcon(getClass().getResource( 
                "resources/img48x48/agt_web.png")).getImage());
        //
       // getFrame().setIconImage((Image)ico);

    }


    @Action
    public void softReloadTree(){
        realodAclSection();
        reloadDelaysSection();
        reloadUsers();
        //reloadSSHconn();
        reloadCachePeerSection();
        reloadExternalAclTypes();
        reloadTcpOutgouingAddr();
        reloadTcpOutgouingTos();
        reloadLogFormat();
        reloadAccessLog();
        reloadRefreshPattern();
	reloadHeaderAccess();
	reloadHeaderReplace();
	tree.reload(auth_param);
        tree.reload(acl);
        tree.reload(delays);
        tree.reload(cache_peer);
         reloadAdmin();
       // tree.reload(Connection);
     //   tree.reload(users);


	/**
	 NEW
	 *

	 **/
	reloadDefaultSquidObjects(
		findNode("wccp2_service"),
		squidConf.getDefaultVectorFromName("wccp2_service")
		);

        reloadDefaultSquidObjects(
		findNode("wccp2_service_info"),
		squidConf.getDefaultVectorFromName("wccp2_service_info")
		);
	

    reloadDefaultSquidObjects(
		findNode("error_map"),
		squidConf.getDefaultVectorFromName("error_map")
		);
    
    reloadDefaultSquidObjects(
		findNode("deny_info"),
		squidConf.getDefaultVectorFromName("deny_info")
		);  
    
     reloadDefaultSquidObjects(
		findNode("cachemgr_passwd"),
		squidConf.getDefaultVectorFromName("cachemgr_passwd")
		);     


    }

    @Action
    public void reloadTcpOutgouingAddr(){
        tcp_outgoing_addr.removeAllChildren();

        for (int i = 0; i < squidConf.tcp_out_addr.size(); ++i)
         tcp_outgoing_addr.add(
                 new DefaultMutableTreeNode(
                 squidConf.tcp_out_addr.get(i).name
                 ));



        tree.reload(tcp_outgoing_addr);
    }

    @Action
    public void reloadTcpOutgouingTos(){
        tcp_outgoing_tos.removeAllChildren();

        for (int i = 0; i < squidConf.tcp_out_tos.size(); ++i)
         tcp_outgoing_tos.add(
                 new DefaultMutableTreeNode(
                 squidConf.tcp_out_tos.get(i).name
                 ));



        tree.reload(tcp_outgoing_tos);
    }

    @Action
    public void reloadAccessLog(){
        access_log.removeAllChildren();
        for (int i = 0; i < squidConf.access_log.size(); ++i)
         access_log.add(
                 new DefaultMutableTreeNode(
                 squidConf.access_log.get(i).name
                 ));
        tree.reload(access_log);
    }

    @Action
    public void reloadHeaderAccess(){
        header_access.removeAllChildren();


	for (int i = 0; i < squidConf.access.size(); i++){
	    String n = squidConf.access.get(i).name;
	    if (squidConf.access.get(i).name.indexOf("header_access") >= 0) {
		header_access.add(new DefaultMutableTreeNode(n.split(" ")[1]));
	    }
	}
	tree.reload(header_access);

    }

    @Action
    public void reloadRefreshPattern(){
        refresh_pattern.removeAllChildren();
        for (int i = 0; i < squidConf.refresh_pattern.size(); ++i)
         refresh_pattern.add(
                 new DefaultMutableTreeNode(
                 squidConf.refresh_pattern.get(i).name
                 ));
        tree.reload(refresh_pattern);
    }

    public void reloadDefaultSquidObjects(DefaultMutableTreeNode node, Vector<DefaultSquidObject> vector){
	if (node == null) {
	    System.out.println("reloadDefaultSquidObjects: node is NULL");
	    return;
	}
	node.removeAllChildren();
	
	if (vector == null) {
	    System.out.println("reloadDefaultSquidObjects: vector is NULL");
	    return;
	}
	
	
	
	
	for (int i = 0; i < vector.size(); ++i)
         node.add(
                 new DefaultMutableTreeNode(
                 vector.get(i).name
                 ));
        tree.reload(node);
    }

    @Action
    public void reloadHeaderReplace(){

	reloadDefaultSquidObjects(header_replace,  squidConf.header_replace);


    }


    @Action
    public void reloadLogFormat(){
        logformat.removeAllChildren();
        for (int i = 0; i < squidConf.logformat.size(); ++i)
         logformat.add(
                 new DefaultMutableTreeNode(
                 squidConf.logformat.get(i).name
                 ));
        tree.reload(logformat);
    }

    @Action
    public void reloadAuth_param(){
        auth_param.removeAllChildren();
        DefaultMutableTreeNode
                basic = new DefaultMutableTreeNode("basic"),
                digest = new DefaultMutableTreeNode("digest"),
                ntlm = new DefaultMutableTreeNode("ntlm"),
                negotiate = new DefaultMutableTreeNode("negotiate");

        basic.add(new DefaultMutableTreeNode("program"));
        basic.add(new DefaultMutableTreeNode("children"));
        basic.add(new DefaultMutableTreeNode("concurrency"));
        basic.add(new DefaultMutableTreeNode("realm"));
        basic.add(new DefaultMutableTreeNode("credentialsttl"));
        basic.add(new DefaultMutableTreeNode("casesensitive"));
        basic.add(new DefaultMutableTreeNode("blankpassword"));

        digest.add(new DefaultMutableTreeNode("program"));
        digest.add(new DefaultMutableTreeNode("children"));
        digest.add(new DefaultMutableTreeNode("concurrency"));
        digest.add(new DefaultMutableTreeNode("realm"));
        digest.add(new DefaultMutableTreeNode("nonce_garbage_interval"));
        digest.add(new DefaultMutableTreeNode("nonce_max_duration"));
        digest.add(new DefaultMutableTreeNode("nonce_max_count"));
        digest.add(new DefaultMutableTreeNode("nonce_strictness"));
        digest.add(new DefaultMutableTreeNode("check_nonce_count"));
        digest.add(new DefaultMutableTreeNode("post_workaround"));


        ntlm.add(new DefaultMutableTreeNode("program"));
        ntlm.add(new DefaultMutableTreeNode("children"));
        ntlm.add(new DefaultMutableTreeNode("keep_alive"));

        negotiate.add(new DefaultMutableTreeNode("program"));
        negotiate.add(new DefaultMutableTreeNode("children"));
        negotiate.add(new DefaultMutableTreeNode("keep_alive"));

        auth_param.add(basic);
        auth_param.add(digest);
        auth_param.add(ntlm);
        auth_param.add(negotiate);


    }

    @Action public void reloadUsers(){

    }
    
    @Action
    public void saveConf(){
        conf.clear();
        for (int i = 0; i < sshconn.size(); i++){
            conf.items.add(sshconn.get(i).getLine());
        }
        
        for (int i = 0; i < dbconn.size(); i++){
            conf.items.add(dbconn.get(i).getLine());
        }
        
        
        conf.saveToFile(confFile);
        //JOptionPane.showMessageDialog(mainPanel, conf.getText());
    }

    @Action
    public void relodConn(){
        Connections.removeAllChildren();
        
        DefaultMutableTreeNode DB = new DefaultMutableTreeNode("DB");
        DefaultMutableTreeNode SSH = new DefaultMutableTreeNode("SSH");
        
        Connections.add(DB);
        Connections.add(SSH);   
        
        for (int i = 0 ; i < sshconn.size(); i++){
            SSH.add(new DefaultMutableTreeNode(sshconn.get(i).name));
        }
        
        for (int i = 0 ; i < dbconn.size(); i++){
            DB.add(new DefaultMutableTreeNode(dbconn.get(i).name));
        }        
       // ERROR
        tree.reload(Connections);
     //   Connection.add(new DefaultMutableTreeNode("localhost"));

    }
    
    
    public boolean nodeContainChild(DefaultMutableTreeNode parent, String childName){
        
        for (int i = 0; i < parent.getChildCount(); ++i){
            if (parent.getChildAt(i).toString().equals(childName)) return true;
        }
        
        return false;
    }
    
    /* This function
     * relaod users and groups in Administrative
     */
    @Action
    public void reloadAdmin(){
        admin.removeAllChildren();
        DefaultMutableTreeNode groups = new DefaultMutableTreeNode("My groups");
        DefaultMutableTreeNode users = new DefaultMutableTreeNode("My users");
       // Properties pr = new Properties();
        
        admin.add(groups);
        admin.add(users);
        
        for (int i = 0; i < squidConf.acl.size(); i++){
            SquidAcl sa = squidConf.acl.get(i);
            if (sa.type.equals("proxy_auth")){
                DefaultMutableTreeNode gr = new DefaultMutableTreeNode(sa.name);
                groups.add(gr);

                
                StringTokenizer st = new StringTokenizer(TStrings.Sort1(sa.value));
                while (st.hasMoreTokens()){
                    String userName = st.nextToken();
                    if (squidConf.getUserGroupsCount(userName) >0)
                    {                            
                        if (!nodeContainChild(users, userName))
                        users.add(new DefaultMutableTreeNode(userName));
                        gr.add(new DefaultMutableTreeNode(userName));
                    }
                    else
                        squidConf.delDefaultObjectFromVector("#user", userName);
                }
                
            }
        }
        
        
        
        
        tree.reload(admin);
    }


    @Action
    public void reloadTree(){
        //

        treeroot.removeAllChildren();
        treeroot.add(alloptions);
      //  Connection.removeAllChildren();

        
        treeroot.add(Connections);
        
        treeroot.add(admin);
        
        

     /*   treeroot.add(myoptions);
        myoptions.add(nodeConnections);
        myoptions.add(new DefaultMutableTreeNode("Users"));
        myoptions.add(new DefaultMutableTreeNode("Channels")); */




        load_squid_options();
	reloadAuth_param();
	
	softReloadTree();
	
        tree.reload();

    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = JSquidModelApp.getApplication().getMainFrame();
            aboutBox = new JSquidModelAboutBox(mainFrame);
            //(JSquidModelAboutBox)aboutBox.j
            //aboutBox.setLine1("WAR");
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        JSquidModelApp.getApplication().show(aboutBox);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        mainPanel = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        acl_page = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jComboBox4 = new javax.swing.JComboBox();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jTextField28 = new javax.swing.JTextField();
        log_page = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        group_page = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jLabel118 = new javax.swing.JLabel();
        jLabel120 = new javax.swing.JLabel();
        jScrollPane12 = new javax.swing.JScrollPane();
        jList3 = new javax.swing.JList();
        welcome = new javax.swing.JPanel();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        jLabel119 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        once_str = new javax.swing.JPanel();
        jTextField22 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jSeparator22 = new javax.swing.JSeparator();
        jButton69 = new javax.swing.JButton();
        once_bool = new javax.swing.JPanel();
        jComboBox2 = new javax.swing.JComboBox();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jSeparator23 = new javax.swing.JSeparator();
        jButton70 = new javax.swing.JButton();
        once_time = new javax.swing.JPanel();
        jTextField27 = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox();
        jButton13 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jSeparator24 = new javax.swing.JSeparator();
        jButton71 = new javax.swing.JButton();
        once_size = new javax.swing.JPanel();
        jButton11 = new javax.swing.JButton();
        jTextField20 = new javax.swing.JTextField();
        jComboBox3 = new javax.swing.JComboBox();
        jButton12 = new javax.swing.JButton();
        jSeparator25 = new javax.swing.JSeparator();
        jButton72 = new javax.swing.JButton();
        once_percent = new javax.swing.JPanel();
        jSlider1 = new javax.swing.JSlider();
        jTextField21 = new javax.swing.JTextField();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jSeparator26 = new javax.swing.JSeparator();
        jButton73 = new javax.swing.JButton();
        custom_message = new javax.swing.JPanel();
        jLabel41 = new javax.swing.JLabel();
        once_httpport = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jTextField18 = new javax.swing.JTextField();
        jTextField19 = new javax.swing.JTextField();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jCheckBox8 = new javax.swing.JCheckBox();
        jCheckBox9 = new javax.swing.JCheckBox();
        jCheckBox10 = new javax.swing.JCheckBox();
        jCheckBox11 = new javax.swing.JCheckBox();
        jCheckBox12 = new javax.swing.JCheckBox();
        jCheckBox13 = new javax.swing.JCheckBox();
        jTextField23 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jTextField24 = new javax.swing.JTextField();
        jTextField25 = new javax.swing.JTextField();
        jTextField26 = new javax.swing.JTextField();
        jButton74 = new javax.swing.JButton();
        jSeparator31 = new javax.swing.JSeparator();
        acl_common = new javax.swing.JPanel();
        jLabel39 = new javax.swing.JLabel();
        jTextField29 = new javax.swing.JTextField();
        jButton15 = new javax.swing.JButton();
        jButton16 = new javax.swing.JButton();
        jScrollPane9 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jCheckBox14 = new javax.swing.JCheckBox();
        conf_text = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        access_page = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jButton19 = new javax.swing.JButton();
        jButton20 = new javax.swing.JButton();
        jButton77 = new javax.swing.JButton();
        auth_str = new javax.swing.JPanel();
        jTextField30 = new javax.swing.JTextField();
        jButton17 = new javax.swing.JButton();
        jButton18 = new javax.swing.JButton();
        delay_prop = new javax.swing.JPanel();
        jLabel43 = new javax.swing.JLabel();
        jComboBox5 = new javax.swing.JComboBox();
        jLabel40 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jTextField31 = new javax.swing.JTextField();
        jTextField32 = new javax.swing.JTextField();
        jTextField33 = new javax.swing.JTextField();
        jTextField34 = new javax.swing.JTextField();
        jTextField35 = new javax.swing.JTextField();
        jTextField36 = new javax.swing.JTextField();
        jButton21 = new javax.swing.JButton();
        jButton22 = new javax.swing.JButton();
        delay_page = new javax.swing.JPanel();
        jButton23 = new javax.swing.JButton();
        jComboBox6 = new javax.swing.JComboBox();
        jLabel48 = new javax.swing.JLabel();
        acl_time = new javax.swing.JPanel();
        jButton24 = new javax.swing.JButton();
        jButton25 = new javax.swing.JButton();
        jScrollPane10 = new javax.swing.JScrollPane();
        jTable4 = new javax.swing.JTable();
        cache_peer_page = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jComboBox7 = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        jCheckBox3 = new javax.swing.JCheckBox();
        jCheckBox4 = new javax.swing.JCheckBox();
        jCheckBox5 = new javax.swing.JCheckBox();
        jCheckBox6 = new javax.swing.JCheckBox();
        jCheckBox7 = new javax.swing.JCheckBox();
        jCheckBox15 = new javax.swing.JCheckBox();
        jCheckBox16 = new javax.swing.JCheckBox();
        jCheckBox17 = new javax.swing.JCheckBox();
        jCheckBox18 = new javax.swing.JCheckBox();
        jCheckBox19 = new javax.swing.JCheckBox();
        jCheckBox20 = new javax.swing.JCheckBox();
        jCheckBox21 = new javax.swing.JCheckBox();
        jCheckBox22 = new javax.swing.JCheckBox();
        jCheckBox23 = new javax.swing.JCheckBox();
        jCheckBox24 = new javax.swing.JCheckBox();
        jLabel12 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jComboBox8 = new javax.swing.JComboBox();
        jLabel15 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jTextField8 = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel17 = new javax.swing.JLabel();
        jTextField9 = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jTextField10 = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jTextField11 = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jTextField12 = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jTextField13 = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jTextField14 = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jTextField15 = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jTextField16 = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        jTextField17 = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jTextField37 = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        jTextField38 = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        jComboBox9 = new javax.swing.JComboBox();
        jLabel29 = new javax.swing.JLabel();
        jComboBox10 = new javax.swing.JComboBox();
        jButton3 = new javax.swing.JButton();
        jButton26 = new javax.swing.JButton();
        ssh_page = new javax.swing.JPanel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jTextField40 = new javax.swing.JTextField();
        jTextField43 = new javax.swing.JTextField();
        jPasswordField1 = new javax.swing.JPasswordField();
        jButton63 = new javax.swing.JButton();
        jButton64 = new javax.swing.JButton();
        jLabel34 = new javax.swing.JLabel();
        jTextField39 = new javax.swing.JTextField();
        jTabbedPane3 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        jTextField41 = new javax.swing.JTextField();
        jLabel49 = new javax.swing.JLabel();
        jTextField42 = new javax.swing.JTextField();
        jButton65 = new javax.swing.JButton();
        jButton66 = new javax.swing.JButton();
        jButton67 = new javax.swing.JButton();
        jCheckBox91 = new javax.swing.JCheckBox();
        external_acl_page = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        jCheckBox25 = new javax.swing.JCheckBox();
        jTextField45 = new javax.swing.JTextField();
        jTextField46 = new javax.swing.JTextField();
        jTextField47 = new javax.swing.JTextField();
        jTextField48 = new javax.swing.JTextField();
        jTextField49 = new javax.swing.JTextField();
        jTextField50 = new javax.swing.JTextField();
        jLabel55 = new javax.swing.JLabel();
        jTextField51 = new javax.swing.JTextField();
        jLabel56 = new javax.swing.JLabel();
        jTextField52 = new javax.swing.JTextField();
        jButton27 = new javax.swing.JButton();
        jButton28 = new javax.swing.JButton();
        jLabel57 = new javax.swing.JLabel();
        jTextField53 = new javax.swing.JTextField();
        https_port_page = new javax.swing.JPanel();
        jLabel58 = new javax.swing.JLabel();
        jLabel59 = new javax.swing.JLabel();
        jCheckBox26 = new javax.swing.JCheckBox();
        jCheckBox27 = new javax.swing.JCheckBox();
        jCheckBox28 = new javax.swing.JCheckBox();
        jTextField54 = new javax.swing.JTextField();
        jTextField55 = new javax.swing.JTextField();
        jTextField56 = new javax.swing.JTextField();
        jLabel60 = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        jLabel63 = new javax.swing.JLabel();
        jLabel64 = new javax.swing.JLabel();
        jLabel65 = new javax.swing.JLabel();
        jLabel66 = new javax.swing.JLabel();
        jLabel67 = new javax.swing.JLabel();
        jLabel68 = new javax.swing.JLabel();
        jLabel69 = new javax.swing.JLabel();
        jLabel70 = new javax.swing.JLabel();
        jLabel71 = new javax.swing.JLabel();
        jLabel72 = new javax.swing.JLabel();
        jLabel73 = new javax.swing.JLabel();
        jLabel74 = new javax.swing.JLabel();
        jTextField57 = new javax.swing.JTextField();
        jTextField58 = new javax.swing.JTextField();
        jTextField59 = new javax.swing.JTextField();
        jTextField60 = new javax.swing.JTextField();
        jTextField61 = new javax.swing.JTextField();
        jTextField63 = new javax.swing.JTextField();
        jTextField64 = new javax.swing.JTextField();
        jTextField65 = new javax.swing.JTextField();
        jTextField66 = new javax.swing.JTextField();
        jTextField67 = new javax.swing.JTextField();
        jTextField68 = new javax.swing.JTextField();
        jTextField69 = new javax.swing.JTextField();
        jTextField70 = new javax.swing.JTextField();
        jTextField71 = new javax.swing.JTextField();
        jButton29 = new javax.swing.JButton();
        jButton30 = new javax.swing.JButton();
        jComboBox11 = new javax.swing.JComboBox();
        jSeparator5 = new javax.swing.JSeparator();
        tcp_out_addr_page = new javax.swing.JPanel();
        jLabel75 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton31 = new javax.swing.JButton();
        jButton32 = new javax.swing.JButton();
        tcp_out_tos = new javax.swing.JPanel();
        jScrollPane11 = new javax.swing.JScrollPane();
        jTable5 = new javax.swing.JTable();
        jLabel76 = new javax.swing.JLabel();
        jButton33 = new javax.swing.JButton();
        jButton34 = new javax.swing.JButton();
        sslproxy_flags_page = new javax.swing.JPanel();
        jCheckBox29 = new javax.swing.JCheckBox();
        jCheckBox30 = new javax.swing.JCheckBox();
        jButton35 = new javax.swing.JButton();
        jCheckBox31 = new javax.swing.JCheckBox();
        jCheckBox32 = new javax.swing.JCheckBox();
        jCheckBox33 = new javax.swing.JCheckBox();
        jButton36 = new javax.swing.JButton();
        neighbor_page = new javax.swing.JPanel();
        jLabel78 = new javax.swing.JLabel();
        jComboBox12 = new javax.swing.JComboBox();
        jTextField62 = new javax.swing.JTextField();
        jButton37 = new javax.swing.JButton();
        jButton38 = new javax.swing.JButton();
        replacement_policy = new javax.swing.JPanel();
        jComboBox13 = new javax.swing.JComboBox();
        jButton39 = new javax.swing.JButton();
        jButton40 = new javax.swing.JButton();
        cache_dir = new javax.swing.JPanel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        ufs = new javax.swing.JPanel();
        jLabel77 = new javax.swing.JLabel();
        jTextField73 = new javax.swing.JTextField();
        jLabel80 = new javax.swing.JLabel();
        jLabel81 = new javax.swing.JLabel();
        jTextField74 = new javax.swing.JTextField();
        jTextField75 = new javax.swing.JTextField();
        aufs = new javax.swing.JPanel();
        jLabel82 = new javax.swing.JLabel();
        jTextField76 = new javax.swing.JTextField();
        jLabel83 = new javax.swing.JLabel();
        jLabel84 = new javax.swing.JLabel();
        jTextField77 = new javax.swing.JTextField();
        jTextField78 = new javax.swing.JTextField();
        diskd = new javax.swing.JPanel();
        jLabel85 = new javax.swing.JLabel();
        jTextField79 = new javax.swing.JTextField();
        jLabel86 = new javax.swing.JLabel();
        jLabel87 = new javax.swing.JLabel();
        jTextField80 = new javax.swing.JTextField();
        jTextField81 = new javax.swing.JTextField();
        jLabel88 = new javax.swing.JLabel();
        jTextField82 = new javax.swing.JTextField();
        jLabel89 = new javax.swing.JLabel();
        jTextField83 = new javax.swing.JTextField();
        coss = new javax.swing.JPanel();
        jLabel90 = new javax.swing.JLabel();
        jLabel91 = new javax.swing.JLabel();
        jLabel92 = new javax.swing.JLabel();
        jLabel93 = new javax.swing.JLabel();
        jLabel94 = new javax.swing.JLabel();
        jTextField84 = new javax.swing.JTextField();
        jSlider2 = new javax.swing.JSlider();
        jTextField85 = new javax.swing.JTextField();
        jTextField86 = new javax.swing.JTextField();
        jTextField87 = new javax.swing.JTextField();
        jLabel79 = new javax.swing.JLabel();
        jCheckBox34 = new javax.swing.JCheckBox();
        jTextField72 = new javax.swing.JTextField();
        jLabel95 = new javax.swing.JLabel();
        jTextField88 = new javax.swing.JTextField();
        jButton41 = new javax.swing.JButton();
        jButton42 = new javax.swing.JButton();
        store_alg_page = new javax.swing.JPanel();
        jComboBox14 = new javax.swing.JComboBox();
        jButton43 = new javax.swing.JButton();
        jButton44 = new javax.swing.JButton();
        jSeparator32 = new javax.swing.JSeparator();
        jButton76 = new javax.swing.JButton();
        logformat_page = new javax.swing.JPanel();
        jLabel96 = new javax.swing.JLabel();
        jTextField89 = new javax.swing.JTextField();
        jButton45 = new javax.swing.JButton();
        jButton46 = new javax.swing.JButton();
        access_log_page = new javax.swing.JPanel();
        jLabel98 = new javax.swing.JLabel();
        jComboBox15 = new javax.swing.JComboBox();
        jLabel99 = new javax.swing.JLabel();
        jComboBox16 = new javax.swing.JComboBox();
        jComboBox17 = new javax.swing.JComboBox();
        jComboBox18 = new javax.swing.JComboBox();
        jComboBox19 = new javax.swing.JComboBox();
        jButton47 = new javax.swing.JButton();
        jButton48 = new javax.swing.JButton();
        jButton78 = new javax.swing.JButton();
        refresh_pattern_page = new javax.swing.JPanel();
        jCheckBox35 = new javax.swing.JCheckBox();
        jLabel97 = new javax.swing.JLabel();
        jTextField90 = new javax.swing.JTextField();
        jLabel100 = new javax.swing.JLabel();
        jSlider3 = new javax.swing.JSlider();
        jLabel101 = new javax.swing.JLabel();
        jTextField91 = new javax.swing.JTextField();
        jCheckBox36 = new javax.swing.JCheckBox();
        jCheckBox37 = new javax.swing.JCheckBox();
        jCheckBox38 = new javax.swing.JCheckBox();
        jCheckBox39 = new javax.swing.JCheckBox();
        jCheckBox40 = new javax.swing.JCheckBox();
        jCheckBox41 = new javax.swing.JCheckBox();
        jCheckBox42 = new javax.swing.JCheckBox();
        jButton49 = new javax.swing.JButton();
        jButton50 = new javax.swing.JButton();
        header_replace_page = new javax.swing.JPanel();
        jLabel102 = new javax.swing.JLabel();
        jTextField92 = new javax.swing.JTextField();
        jButton51 = new javax.swing.JButton();
        jButton52 = new javax.swing.JButton();
        wccp2_service_page = new javax.swing.JPanel();
        jLabel103 = new javax.swing.JLabel();
        jComboBox20 = new javax.swing.JComboBox();
        jLabel104 = new javax.swing.JLabel();
        jTextField93 = new javax.swing.JTextField();
        jLabel105 = new javax.swing.JLabel();
        jTextField94 = new javax.swing.JTextField();
        jSeparator12 = new javax.swing.JSeparator();
        jButton53 = new javax.swing.JButton();
        jButton54 = new javax.swing.JButton();
        wccp2_service_info_page = new javax.swing.JPanel();
        jLabel106 = new javax.swing.JLabel();
        jComboBox21 = new javax.swing.JComboBox();
        jLabel107 = new javax.swing.JLabel();
        jCheckBox43 = new javax.swing.JCheckBox();
        jCheckBox44 = new javax.swing.JCheckBox();
        jCheckBox45 = new javax.swing.JCheckBox();
        jCheckBox46 = new javax.swing.JCheckBox();
        jCheckBox47 = new javax.swing.JCheckBox();
        jCheckBox48 = new javax.swing.JCheckBox();
        jCheckBox49 = new javax.swing.JCheckBox();
        jCheckBox50 = new javax.swing.JCheckBox();
        jCheckBox51 = new javax.swing.JCheckBox();
        jTextField95 = new javax.swing.JTextField();
        jLabel108 = new javax.swing.JLabel();
        jLabel109 = new javax.swing.JLabel();
        jTextField96 = new javax.swing.JTextField();
        jSeparator14 = new javax.swing.JSeparator();
        jSeparator15 = new javax.swing.JSeparator();
        jButton55 = new javax.swing.JButton();
        jButton56 = new javax.swing.JButton();
        deny_info_page = new javax.swing.JPanel();
        jLabel110 = new javax.swing.JLabel();
        jTextField97 = new javax.swing.JTextField();
        jLabel111 = new javax.swing.JLabel();
        jComboBox22 = new javax.swing.JComboBox();
        jSeparator17 = new javax.swing.JSeparator();
        jButton57 = new javax.swing.JButton();
        jButton58 = new javax.swing.JButton();
        cachemgr_passwd_page = new javax.swing.JPanel();
        jCheckBox52 = new javax.swing.JCheckBox();
        jCheckBox53 = new javax.swing.JCheckBox();
        jCheckBox54 = new javax.swing.JCheckBox();
        jCheckBox55 = new javax.swing.JCheckBox();
        jCheckBox56 = new javax.swing.JCheckBox();
        jCheckBox57 = new javax.swing.JCheckBox();
        jCheckBox58 = new javax.swing.JCheckBox();
        jCheckBox59 = new javax.swing.JCheckBox();
        jCheckBox60 = new javax.swing.JCheckBox();
        jCheckBox61 = new javax.swing.JCheckBox();
        jCheckBox62 = new javax.swing.JCheckBox();
        jCheckBox63 = new javax.swing.JCheckBox();
        jCheckBox64 = new javax.swing.JCheckBox();
        jCheckBox65 = new javax.swing.JCheckBox();
        jCheckBox66 = new javax.swing.JCheckBox();
        jCheckBox67 = new javax.swing.JCheckBox();
        jCheckBox68 = new javax.swing.JCheckBox();
        jSeparator18 = new javax.swing.JSeparator();
        jCheckBox69 = new javax.swing.JCheckBox();
        jCheckBox70 = new javax.swing.JCheckBox();
        jCheckBox71 = new javax.swing.JCheckBox();
        jCheckBox72 = new javax.swing.JCheckBox();
        jCheckBox73 = new javax.swing.JCheckBox();
        jCheckBox74 = new javax.swing.JCheckBox();
        jCheckBox75 = new javax.swing.JCheckBox();
        jCheckBox76 = new javax.swing.JCheckBox();
        jCheckBox77 = new javax.swing.JCheckBox();
        jCheckBox78 = new javax.swing.JCheckBox();
        jCheckBox79 = new javax.swing.JCheckBox();
        jCheckBox80 = new javax.swing.JCheckBox();
        jCheckBox81 = new javax.swing.JCheckBox();
        jCheckBox82 = new javax.swing.JCheckBox();
        jCheckBox83 = new javax.swing.JCheckBox();
        jCheckBox84 = new javax.swing.JCheckBox();
        jCheckBox85 = new javax.swing.JCheckBox();
        jSeparator19 = new javax.swing.JSeparator();
        jCheckBox86 = new javax.swing.JCheckBox();
        jCheckBox87 = new javax.swing.JCheckBox();
        jCheckBox88 = new javax.swing.JCheckBox();
        jSeparator20 = new javax.swing.JSeparator();
        jButton59 = new javax.swing.JButton();
        jButton60 = new javax.swing.JButton();
        uri_page = new javax.swing.JPanel();
        jComboBox23 = new javax.swing.JComboBox();
        jSeparator21 = new javax.swing.JSeparator();
        jButton61 = new javax.swing.JButton();
        jButton62 = new javax.swing.JButton();
        jButton75 = new javax.swing.JButton();
        user_page = new javax.swing.JPanel();
        jLabel112 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList();
        jLabel113 = new javax.swing.JLabel();
        jPasswordField2 = new javax.swing.JPasswordField();
        jLabel114 = new javax.swing.JLabel();
        jTextField44 = new javax.swing.JTextField();
        jLabel115 = new javax.swing.JLabel();
        jTextField98 = new javax.swing.JTextField();
        jLabel116 = new javax.swing.JLabel();
        jCheckBox89 = new javax.swing.JCheckBox();
        jLabel117 = new javax.swing.JLabel();
        jTextField99 = new javax.swing.JTextField();
        jCheckBox90 = new javax.swing.JCheckBox();
        jSeparator28 = new javax.swing.JSeparator();
        jSeparator29 = new javax.swing.JSeparator();
        jSeparator30 = new javax.swing.JSeparator();
        jButton68 = new javax.swing.JButton();
        jLabel121 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel122 = new javax.swing.JLabel();
        jTextField100 = new javax.swing.JTextField();
        help_page = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jEditorPane1 = new javax.swing.JEditorPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        jMenuItem18 = new javax.swing.JMenuItem();
        jSeparator9 = new javax.swing.JSeparator();
        jMenuItem16 = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JSeparator();
        jMenuItem1 = new javax.swing.JMenuItem();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem17 = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JSeparator();
        jMenuItem19 = new javax.swing.JMenuItem();
        jSeparator33 = new javax.swing.JSeparator();
        jMenuItem8 = new javax.swing.JMenuItem();
        jSeparator13 = new javax.swing.JSeparator();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem51 = new javax.swing.JMenuItem();
        jMenuItem52 = new javax.swing.JMenuItem();
        jMenuItem53 = new javax.swing.JMenuItem();
        jMenuItem54 = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        jSeparator3 = new javax.swing.JSeparator();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        jLabel2 = new javax.swing.JLabel();
        once_param_popup = new javax.swing.JPopupMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        delay_popup = new javax.swing.JPopupMenu();
        jMenuItem4 = new javax.swing.JMenuItem();
        buttonGroup1 = new javax.swing.ButtonGroup();
        acl_item_menu = new javax.swing.JPopupMenu();
        jMenuItem21 = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JSeparator();
        jMenuItem10 = new javax.swing.JMenuItem();
        jMenuItem11 = new javax.swing.JMenuItem();
        jMenuItem12 = new javax.swing.JMenuItem();
        jMenuItem13 = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JSeparator();
        jMenuItem14 = new javax.swing.JMenuItem();
        jMenuItem20 = new javax.swing.JMenuItem();
        popup_access = new javax.swing.JPopupMenu();
        jMenuItem22 = new javax.swing.JMenuItem();
        jMenuItem28 = new javax.swing.JMenuItem();
        jSeparator11 = new javax.swing.JSeparator();
        jMenuItem23 = new javax.swing.JMenuItem();
        jSeparator10 = new javax.swing.JSeparator();
        jMenuItem26 = new javax.swing.JMenuItem();
        jMenuItem24 = new javax.swing.JMenuItem();
        jMenuItem25 = new javax.swing.JMenuItem();
        jMenuItem27 = new javax.swing.JMenuItem();
        conf_menu = new javax.swing.JPopupMenu();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        jMenuItem30 = new javax.swing.JMenuItem();
        aclvalues_popup = new javax.swing.JPopupMenu();
        jMenuItem31 = new javax.swing.JMenuItem();
        jMenuItem32 = new javax.swing.JMenuItem();
        cachepeer_popup = new javax.swing.JPopupMenu();
        jMenuItem15 = new javax.swing.JMenuItem();
        external_acl_popup = new javax.swing.JPopupMenu();
        jMenuItem33 = new javax.swing.JMenuItem();
        popup_tcp_outgoing_addr = new javax.swing.JPopupMenu();
        jMenuItem34 = new javax.swing.JMenuItem();
        popup_tcpout_tos = new javax.swing.JPopupMenu();
        jMenuItem35 = new javax.swing.JMenuItem();
        popup_logformat = new javax.swing.JPopupMenu();
        jMenuItem36 = new javax.swing.JMenuItem();
        popup_AccessLog = new javax.swing.JPopupMenu();
        jMenuItem37 = new javax.swing.JMenuItem();
        popup_RefreshPattern = new javax.swing.JPopupMenu();
        jMenuItem38 = new javax.swing.JMenuItem();
        popup_header_acc = new javax.swing.JPopupMenu();
        jMenuItem39 = new javax.swing.JMenuItem();
        popup_header_replace = new javax.swing.JPopupMenu();
        jMenuItem40 = new javax.swing.JMenuItem();
        popup_def_obj = new javax.swing.JPopupMenu();
        jMenuItem41 = new javax.swing.JMenuItem();
        popup_dev_obj_item = new javax.swing.JPopupMenu();
        jMenuItem43 = new javax.swing.JMenuItem();
        jMenuItem44 = new javax.swing.JMenuItem();
        jSeparator16 = new javax.swing.JSeparator();
        jMenuItem45 = new javax.swing.JMenuItem();
        popup_ssh = new javax.swing.JPopupMenu();
        jMenuItem47 = new javax.swing.JMenuItem();
        popup_ssh_item = new javax.swing.JPopupMenu();
        jMenuItem48 = new javax.swing.JMenuItem();
        jSeparator27 = new javax.swing.JSeparator();
        jMenuItem49 = new javax.swing.JMenuItem();
        jMenuItem50 = new javax.swing.JMenuItem();
        jMenuItem42 = new javax.swing.JMenuItem();
        popup_user_groups = new javax.swing.JPopupMenu();
        jMenuItem9 = new javax.swing.JMenuItem();
        jMenuItem29 = new javax.swing.JMenuItem();
        popup_groups_userlist = new javax.swing.JPopupMenu();
        jMenuItem46 = new javax.swing.JMenuItem();
        jMenuItem55 = new javax.swing.JMenuItem();
        jMenuItem56 = new javax.swing.JMenuItem();

        mainPanel.setName("mainPanel"); // NOI18N

        jSplitPane1.setDividerSize(5);
        jSplitPane1.setMinimumSize(new java.awt.Dimension(129, 124));
        jSplitPane1.setName("jSplitPane1"); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N

        jTabbedPane1.setName("jTabbedPane1"); // NOI18N

        acl_page.setName("acl_page"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(jsquidmodel.JSquidModelApp.class).getContext().getActionMap(JSquidModelView.class, this);
        jButton1.setAction(actionMap.get("createAcl")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N

        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "arp", "browser", "ca_cert", "dst", "dstdomain", "dstdom_regex", "dst_as", "external", "ext_user", "ext_user_regex", "ident", "ident_regex", "myip", "myport", "method", "maxconn", "max_user_ip", "port", "proto", "proxy_auth", "proxy_auth_regex", "referer_regex", "req_header", "rep_header", "rep_mime_type", "snmp_community", "src", "srcdomain", "srcdom_regex", "src_as", "time", "url_regex", "urlpath_regex", "urllogin", "urlgroup" }));
        jComboBox4.setName("jComboBox4"); // NOI18N
        jComboBox4.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox4ItemStateChanged(evt);
            }
        });

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(jsquidmodel.JSquidModelApp.class).getContext().getResourceMap(JSquidModelView.class);
        jLabel36.setText(resourceMap.getString("jLabel36.text")); // NOI18N
        jLabel36.setName("jLabel36"); // NOI18N

        jLabel37.setText(resourceMap.getString("jLabel37.text")); // NOI18N
        jLabel37.setName("jLabel37"); // NOI18N

        jLabel38.setText(resourceMap.getString("jLabel38.text")); // NOI18N
        jLabel38.setName("jLabel38"); // NOI18N

        jTextField28.setText(resourceMap.getString("jTextField28.text")); // NOI18N
        jTextField28.setName("jTextField28"); // NOI18N

        javax.swing.GroupLayout acl_pageLayout = new javax.swing.GroupLayout(acl_page);
        acl_page.setLayout(acl_pageLayout);
        acl_pageLayout.setHorizontalGroup(
            acl_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(acl_pageLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(acl_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel36)
                    .addGroup(acl_pageLayout.createSequentialGroup()
                        .addGroup(acl_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel37)
                            .addComponent(jLabel38))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(acl_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(acl_pageLayout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addGroup(acl_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jButton1)
                                    .addComponent(jTextField28))))))
                .addContainerGap(841, Short.MAX_VALUE))
        );
        acl_pageLayout.setVerticalGroup(
            acl_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(acl_pageLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel36)
                .addGap(18, 18, 18)
                .addGroup(acl_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel37)
                    .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(acl_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel38)
                    .addComponent(jTextField28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1)
                .addContainerGap(360, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(resourceMap.getString("acl_page.TabConstraints.tabTitle"), acl_page); // NOI18N

        log_page.setName("log_page"); // NOI18N

        jScrollPane6.setName("jScrollPane6"); // NOI18N

        jTextPane1.setBackground(resourceMap.getColor("jTextPane1.background")); // NOI18N
        jTextPane1.setForeground(resourceMap.getColor("jTextPane1.foreground")); // NOI18N
        jTextPane1.setName("jTextPane1"); // NOI18N
        jScrollPane6.setViewportView(jTextPane1);

        javax.swing.GroupLayout log_pageLayout = new javax.swing.GroupLayout(log_page);
        log_page.setLayout(log_pageLayout);
        log_pageLayout.setHorizontalGroup(
            log_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(log_pageLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 1014, Short.MAX_VALUE)
                .addContainerGap())
        );
        log_pageLayout.setVerticalGroup(
            log_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(log_pageLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 488, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab(resourceMap.getString("log_page.TabConstraints.tabTitle"), log_page); // NOI18N

        group_page.setName("group_page"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList1.setLayoutOrientation(javax.swing.JList.HORIZONTAL_WRAP);
        jList1.setName("jList1"); // NOI18N
        jScrollPane2.setViewportView(jList1);

        jLabel118.setText(resourceMap.getString("jLabel118.text")); // NOI18N
        jLabel118.setName("jLabel118"); // NOI18N

        jLabel120.setText(resourceMap.getString("jLabel120.text")); // NOI18N
        jLabel120.setName("jLabel120"); // NOI18N

        jScrollPane12.setName("jScrollPane12"); // NOI18N

        jList3.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList3.setLayoutOrientation(javax.swing.JList.HORIZONTAL_WRAP);
        jList3.setName("jList3"); // NOI18N
        jScrollPane12.setViewportView(jList3);

        javax.swing.GroupLayout group_pageLayout = new javax.swing.GroupLayout(group_page);
        group_page.setLayout(group_pageLayout);
        group_pageLayout.setHorizontalGroup(
            group_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(group_pageLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(group_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(group_pageLayout.createSequentialGroup()
                        .addComponent(jScrollPane12, javax.swing.GroupLayout.DEFAULT_SIZE, 1014, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(group_pageLayout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1014, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(group_pageLayout.createSequentialGroup()
                        .addComponent(jLabel118)
                        .addGap(219, 219, 219))
                    .addGroup(group_pageLayout.createSequentialGroup()
                        .addComponent(jLabel120)
                        .addContainerGap(984, Short.MAX_VALUE))))
        );
        group_pageLayout.setVerticalGroup(
            group_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(group_pageLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel118)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel120)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(61, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(resourceMap.getString("group_page.TabConstraints.tabTitle"), group_page); // NOI18N

        welcome.setBackground(resourceMap.getColor("welcome.background")); // NOI18N
        welcome.setName("welcome"); // NOI18N

        jLayeredPane1.setBackground(resourceMap.getColor("jLayeredPane1.background")); // NOI18N
        jLayeredPane1.setName("jLayeredPane1"); // NOI18N

        jLabel119.setText(resourceMap.getString("jLabel119.text")); // NOI18N
        jLabel119.setName("jLabel119"); // NOI18N
        jLabel119.setBounds(320, 30, 290, 100);
        jLayeredPane1.add(jLabel119, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel1.setIcon(resourceMap.getIcon("jLabel1.icon")); // NOI18N
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        jLabel1.setBounds(10, 10, 670, 210);
        jLayeredPane1.add(jLabel1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout welcomeLayout = new javax.swing.GroupLayout(welcome);
        welcome.setLayout(welcomeLayout);
        welcomeLayout.setHorizontalGroup(
            welcomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(welcomeLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLayeredPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1014, Short.MAX_VALUE)
                .addContainerGap())
        );
        welcomeLayout.setVerticalGroup(
            welcomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(welcomeLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(260, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(resourceMap.getString("welcome.TabConstraints.tabTitle"), welcome); // NOI18N

        once_str.setName("once_str"); // NOI18N

        jTextField22.setText(resourceMap.getString("jTextField22.text")); // NOI18N
        jTextField22.setName("jTextField22"); // NOI18N

        jButton2.setAction(actionMap.get("saveOnceStrParam")); // NOI18N
        jButton2.setName("jButton2"); // NOI18N

        jButton4.setAction(actionMap.get("emptyOnceParam")); // NOI18N
        jButton4.setName("jButton4"); // NOI18N

        jSeparator22.setName("jSeparator22"); // NOI18N

        jButton69.setAction(actionMap.get("showHelp1")); // NOI18N
        jButton69.setName("jButton69"); // NOI18N

        javax.swing.GroupLayout once_strLayout = new javax.swing.GroupLayout(once_str);
        once_str.setLayout(once_strLayout);
        once_strLayout.setHorizontalGroup(
            once_strLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(once_strLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(once_strLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField22, javax.swing.GroupLayout.DEFAULT_SIZE, 1014, Short.MAX_VALUE)
                    .addComponent(jSeparator22, javax.swing.GroupLayout.DEFAULT_SIZE, 1014, Short.MAX_VALUE)
                    .addGroup(once_strLayout.createSequentialGroup()
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton69)))
                .addContainerGap())
        );
        once_strLayout.setVerticalGroup(
            once_strLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(once_strLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTextField22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator22, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(once_strLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(once_strLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton2)
                        .addComponent(jButton4))
                    .addComponent(jButton69))
                .addContainerGap(419, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(resourceMap.getString("once_str.TabConstraints.tabTitle"), once_str); // NOI18N

        once_bool.setName("once_bool"); // NOI18N

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "off", "on" }));
        jComboBox2.setName("jComboBox2"); // NOI18N

        jButton7.setAction(actionMap.get("saveOnceBool")); // NOI18N
        jButton7.setName("jButton7"); // NOI18N

        jButton8.setAction(actionMap.get("emptyOnceParam")); // NOI18N
        jButton8.setName("jButton8"); // NOI18N

        jSeparator23.setName("jSeparator23"); // NOI18N

        jButton70.setAction(actionMap.get("showHelp1")); // NOI18N
        jButton70.setName("jButton70"); // NOI18N

        javax.swing.GroupLayout once_boolLayout = new javax.swing.GroupLayout(once_bool);
        once_bool.setLayout(once_boolLayout);
        once_boolLayout.setHorizontalGroup(
            once_boolLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(once_boolLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(once_boolLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator23, javax.swing.GroupLayout.DEFAULT_SIZE, 1014, Short.MAX_VALUE)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(once_boolLayout.createSequentialGroup()
                        .addComponent(jButton7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton70)))
                .addContainerGap())
        );
        once_boolLayout.setVerticalGroup(
            once_boolLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(once_boolLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator23, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(once_boolLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton7)
                    .addComponent(jButton8)
                    .addComponent(jButton70))
                .addContainerGap(420, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(resourceMap.getString("once_bool.TabConstraints.tabTitle"), once_bool); // NOI18N

        once_time.setName("once_time"); // NOI18N

        jTextField27.setText(resourceMap.getString("jTextField27.text")); // NOI18N
        jTextField27.setToolTipText(resourceMap.getString("jTextField27.toolTipText")); // NOI18N
        jTextField27.setName("jTextField27"); // NOI18N
        jTextField27.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField27KeyTyped(evt);
            }
        });

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "0 seconds", "1 second", "5 seconds", "10 seconds", "20 seconds", "30 seconds", "1 minute", "2 minutes", "3 minutes", "5 minutes", "10 minutes", "15 minutes", "20 minutes", "30 minutes", "40 minutes", "50 minutes", "1 hour", "2 hours", "3 hours", "5 hours", "6 hours", "12 hours", "1 day", "2 days" }));
        jComboBox1.setName("jComboBox1"); // NOI18N

        jButton13.setAction(actionMap.get("emptyOnceParam")); // NOI18N
        jButton13.setName("jButton13"); // NOI18N

        jButton14.setAction(actionMap.get("saveOnceTime")); // NOI18N
        jButton14.setName("jButton14"); // NOI18N

        jSeparator24.setName("jSeparator24"); // NOI18N

        jButton71.setAction(actionMap.get("showHelp1")); // NOI18N
        jButton71.setName("jButton71"); // NOI18N

        javax.swing.GroupLayout once_timeLayout = new javax.swing.GroupLayout(once_time);
        once_time.setLayout(once_timeLayout);
        once_timeLayout.setHorizontalGroup(
            once_timeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(once_timeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(once_timeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator24, javax.swing.GroupLayout.DEFAULT_SIZE, 1014, Short.MAX_VALUE)
                    .addGroup(once_timeLayout.createSequentialGroup()
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField27, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(once_timeLayout.createSequentialGroup()
                        .addComponent(jButton14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton71)))
                .addContainerGap())
        );
        once_timeLayout.setVerticalGroup(
            once_timeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(once_timeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(once_timeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator24, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(once_timeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton14)
                    .addComponent(jButton13)
                    .addComponent(jButton71))
                .addContainerGap(411, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(resourceMap.getString("once_time.TabConstraints.tabTitle"), once_time); // NOI18N

        once_size.setName("once_size"); // NOI18N

        jButton11.setAction(actionMap.get("saveOnceSize")); // NOI18N
        jButton11.setName("jButton11"); // NOI18N

        jTextField20.setText(resourceMap.getString("jTextField20.text")); // NOI18N
        jTextField20.setName("jTextField20"); // NOI18N

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "KB", "MB", "GB" }));
        jComboBox3.setName("jComboBox3"); // NOI18N

        jButton12.setAction(actionMap.get("emptyOnceParam")); // NOI18N
        jButton12.setName("jButton12"); // NOI18N

        jSeparator25.setName("jSeparator25"); // NOI18N

        jButton72.setAction(actionMap.get("showHelp1")); // NOI18N
        jButton72.setName("jButton72"); // NOI18N

        javax.swing.GroupLayout once_sizeLayout = new javax.swing.GroupLayout(once_size);
        once_size.setLayout(once_sizeLayout);
        once_sizeLayout.setHorizontalGroup(
            once_sizeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(once_sizeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(once_sizeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator25, javax.swing.GroupLayout.DEFAULT_SIZE, 1014, Short.MAX_VALUE)
                    .addGroup(once_sizeLayout.createSequentialGroup()
                        .addComponent(jTextField20, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(once_sizeLayout.createSequentialGroup()
                        .addComponent(jButton11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton72)))
                .addContainerGap())
        );
        once_sizeLayout.setVerticalGroup(
            once_sizeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(once_sizeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(once_sizeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator25, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(once_sizeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton11)
                    .addComponent(jButton12)
                    .addComponent(jButton72))
                .addContainerGap(420, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(resourceMap.getString("once_size.TabConstraints.tabTitle"), once_size); // NOI18N

        once_percent.setName("once_percent"); // NOI18N

        jSlider1.setPaintLabels(true);
        jSlider1.setPaintTicks(true);
        jSlider1.setName("jSlider1"); // NOI18N
        jSlider1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider1StateChanged(evt);
            }
        });

        jTextField21.setName("jTextField21"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jSlider1, org.jdesktop.beansbinding.ELProperty.create("${value}"), jTextField21, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jButton9.setAction(actionMap.get("saveOncePercent")); // NOI18N
        jButton9.setName("jButton9"); // NOI18N

        jButton10.setAction(actionMap.get("emptyOnceParam")); // NOI18N
        jButton10.setName("jButton10"); // NOI18N

        jSeparator26.setName("jSeparator26"); // NOI18N

        jButton73.setAction(actionMap.get("showHelp1")); // NOI18N
        jButton73.setName("jButton73"); // NOI18N

        javax.swing.GroupLayout once_percentLayout = new javax.swing.GroupLayout(once_percent);
        once_percent.setLayout(once_percentLayout);
        once_percentLayout.setHorizontalGroup(
            once_percentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(once_percentLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(once_percentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(once_percentLayout.createSequentialGroup()
                        .addComponent(jTextField21, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSlider1, javax.swing.GroupLayout.DEFAULT_SIZE, 937, Short.MAX_VALUE))
                    .addComponent(jSeparator26, javax.swing.GroupLayout.DEFAULT_SIZE, 1014, Short.MAX_VALUE)
                    .addGroup(once_percentLayout.createSequentialGroup()
                        .addComponent(jButton9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton73)))
                .addContainerGap())
        );
        once_percentLayout.setVerticalGroup(
            once_percentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(once_percentLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(once_percentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator26, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(once_percentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton10)
                    .addComponent(jButton9)
                    .addComponent(jButton73))
                .addContainerGap(417, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(resourceMap.getString("once_percent.TabConstraints.tabTitle"), once_percent); // NOI18N

        custom_message.setName("custom_message"); // NOI18N

        jLabel41.setText(resourceMap.getString("jLabel41.text")); // NOI18N
        jLabel41.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel41.setName("jLabel41"); // NOI18N

        javax.swing.GroupLayout custom_messageLayout = new javax.swing.GroupLayout(custom_message);
        custom_message.setLayout(custom_messageLayout);
        custom_messageLayout.setHorizontalGroup(
            custom_messageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(custom_messageLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel41, javax.swing.GroupLayout.DEFAULT_SIZE, 1014, Short.MAX_VALUE)
                .addContainerGap())
        );
        custom_messageLayout.setVerticalGroup(
            custom_messageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(custom_messageLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel41, javax.swing.GroupLayout.DEFAULT_SIZE, 488, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab(resourceMap.getString("custom_message.TabConstraints.tabTitle"), custom_message); // NOI18N

        once_httpport.setName("once_httpport"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N

        jTextField18.setText(resourceMap.getString("jTextField18.text")); // NOI18N
        jTextField18.setName("jTextField18"); // NOI18N

        jTextField19.setText(resourceMap.getString("jTextField19.text")); // NOI18N
        jTextField19.setName("jTextField19"); // NOI18N

        jButton5.setAction(actionMap.get("saveOnceHttpPort")); // NOI18N
        jButton5.setName("jButton5"); // NOI18N

        jButton6.setAction(actionMap.get("emptyOnceParam")); // NOI18N
        jButton6.setName("jButton6"); // NOI18N

        jCheckBox8.setText(resourceMap.getString("jCheckBox8.text")); // NOI18N
        jCheckBox8.setName("jCheckBox8"); // NOI18N

        jCheckBox9.setText(resourceMap.getString("jCheckBox9.text")); // NOI18N
        jCheckBox9.setName("jCheckBox9"); // NOI18N

        jCheckBox10.setText(resourceMap.getString("jCheckBox10.text")); // NOI18N
        jCheckBox10.setName("jCheckBox10"); // NOI18N

        jCheckBox11.setText(resourceMap.getString("jCheckBox11.text")); // NOI18N
        jCheckBox11.setName("jCheckBox11"); // NOI18N

        jCheckBox12.setText(resourceMap.getString("jCheckBox12.text")); // NOI18N
        jCheckBox12.setName("jCheckBox12"); // NOI18N

        jCheckBox13.setText(resourceMap.getString("jCheckBox13.text")); // NOI18N
        jCheckBox13.setName("jCheckBox13"); // NOI18N

        jTextField23.setText(resourceMap.getString("jTextField23.text")); // NOI18N
        jTextField23.setName("jTextField23"); // NOI18N

        jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N

        jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N

        jLabel35.setText(resourceMap.getString("jLabel35.text")); // NOI18N
        jLabel35.setName("jLabel35"); // NOI18N

        jTextField24.setText(resourceMap.getString("jTextField24.text")); // NOI18N
        jTextField24.setName("jTextField24"); // NOI18N

        jTextField25.setText(resourceMap.getString("jTextField25.text")); // NOI18N
        jTextField25.setName("jTextField25"); // NOI18N

        jTextField26.setText(resourceMap.getString("jTextField26.text")); // NOI18N
        jTextField26.setName("jTextField26"); // NOI18N

        jButton74.setAction(actionMap.get("showHelp1")); // NOI18N
        jButton74.setName("jButton74"); // NOI18N

        jSeparator31.setName("jSeparator31"); // NOI18N

        javax.swing.GroupLayout once_httpportLayout = new javax.swing.GroupLayout(once_httpport);
        once_httpport.setLayout(once_httpportLayout);
        once_httpportLayout.setHorizontalGroup(
            once_httpportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(once_httpportLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(once_httpportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(once_httpportLayout.createSequentialGroup()
                        .addGroup(once_httpportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11)
                            .addComponent(jLabel35)
                            .addComponent(jLabel10)
                            .addComponent(jLabel3)
                            .addComponent(jLabel9))
                        .addGap(29, 29, 29)
                        .addGroup(once_httpportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField18, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
                            .addComponent(jTextField19, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
                            .addComponent(jTextField25, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
                            .addGroup(once_httpportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jCheckBox11)
                                .addComponent(jCheckBox10))
                            .addComponent(jCheckBox13, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(once_httpportLayout.createSequentialGroup()
                                .addComponent(jCheckBox12)
                                .addGap(18, 18, 18)
                                .addComponent(jTextField23))
                            .addComponent(jCheckBox8)
                            .addComponent(jCheckBox9)
                            .addComponent(jTextField24, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
                            .addComponent(jTextField26, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE))
                        .addGap(1182, 1182, 1182))
                    .addGroup(once_httpportLayout.createSequentialGroup()
                        .addComponent(jButton5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton74)
                        .addContainerGap(714, Short.MAX_VALUE))
                    .addGroup(once_httpportLayout.createSequentialGroup()
                        .addComponent(jSeparator31, javax.swing.GroupLayout.DEFAULT_SIZE, 1419, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        once_httpportLayout.setVerticalGroup(
            once_httpportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(once_httpportLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(once_httpportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(once_httpportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jTextField19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addComponent(jSeparator31, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(once_httpportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(jTextField24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(once_httpportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jTextField25, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(once_httpportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel35)
                    .addComponent(jTextField26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(once_httpportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(once_httpportLayout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(jCheckBox11))
                    .addGroup(once_httpportLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBox10)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(once_httpportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBox12)
                    .addComponent(jTextField23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBox9)
                .addGap(18, 18, 18)
                .addGroup(once_httpportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton6)
                    .addComponent(jButton5)
                    .addComponent(jButton74))
                .addContainerGap(157, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(resourceMap.getString("once_httpport.TabConstraints.tabTitle"), once_httpport); // NOI18N

        acl_common.setName("acl_common"); // NOI18N

        jLabel39.setFont(resourceMap.getFont("jLabel39.font")); // NOI18N
        jLabel39.setText(resourceMap.getString("jLabel39.text")); // NOI18N
        jLabel39.setName("jLabel39"); // NOI18N

        jTextField29.setText(resourceMap.getString("jTextField29.text")); // NOI18N
        jTextField29.setName("jTextField29"); // NOI18N

        jButton15.setAction(actionMap.get("saveCommonAcl")); // NOI18N
        jButton15.setName("jButton15"); // NOI18N

        jButton16.setAction(actionMap.get("removeCurrentAcl")); // NOI18N
        jButton16.setName("jButton16"); // NOI18N

        jScrollPane9.setName("jScrollPane9"); // NOI18N

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null}
            },
            new String [] {
                "values"
            }
        ));
        jTable3.setComponentPopupMenu(aclvalues_popup);
        jTable3.setName("jTable3"); // NOI18N
        jTable3.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTable3FocusLost(evt);
            }
        });
        jScrollPane9.setViewportView(jTable3);
        jTable3.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("jTable3.columnModel.title0")); // NOI18N

        jCheckBox14.setText(resourceMap.getString("jCheckBox14.text")); // NOI18N
        jCheckBox14.setName("jCheckBox14"); // NOI18N

        javax.swing.GroupLayout acl_commonLayout = new javax.swing.GroupLayout(acl_common);
        acl_common.setLayout(acl_commonLayout);
        acl_commonLayout.setHorizontalGroup(
            acl_commonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(acl_commonLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(acl_commonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1014, Short.MAX_VALUE)
                    .addComponent(jTextField29, javax.swing.GroupLayout.DEFAULT_SIZE, 1014, Short.MAX_VALUE)
                    .addGroup(acl_commonLayout.createSequentialGroup()
                        .addComponent(jLabel39)
                        .addGap(18, 18, 18)
                        .addComponent(jCheckBox14))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, acl_commonLayout.createSequentialGroup()
                        .addComponent(jButton16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton15)))
                .addContainerGap())
        );
        acl_commonLayout.setVerticalGroup(
            acl_commonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(acl_commonLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(acl_commonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel39)
                    .addComponent(jCheckBox14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(acl_commonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton15)
                    .addComponent(jButton16))
                .addContainerGap())
        );

        jTabbedPane1.addTab(resourceMap.getString("acl_common.TabConstraints.tabTitle"), acl_common); // NOI18N

        conf_text.setName("conf_text"); // NOI18N

        jScrollPane7.setName("jScrollPane7"); // NOI18N

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.setComponentPopupMenu(conf_menu);
        jTextArea1.setName("jTextArea1"); // NOI18N
        jScrollPane7.setViewportView(jTextArea1);

        javax.swing.GroupLayout conf_textLayout = new javax.swing.GroupLayout(conf_text);
        conf_text.setLayout(conf_textLayout);
        conf_textLayout.setHorizontalGroup(
            conf_textLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(conf_textLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 1014, Short.MAX_VALUE)
                .addContainerGap())
        );
        conf_textLayout.setVerticalGroup(
            conf_textLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(conf_textLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 488, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab(resourceMap.getString("conf_text.TabConstraints.tabTitle"), conf_text); // NOI18N

        access_page.setName("access_page"); // NOI18N

        jScrollPane8.setName("jScrollPane8"); // NOI18N

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "action", "acl1", "acl2", "acl3"
            }
        ));
        jTable2.setComponentPopupMenu(popup_access);
        jTable2.setName("jTable2"); // NOI18N
        jScrollPane8.setViewportView(jTable2);
        jTable2.getColumnModel().getColumn(0).setMaxWidth(100);
        jTable2.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("jTable2.columnModel.title0")); // NOI18N
        jTable2.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("jTable2.columnModel.title1")); // NOI18N
        jTable2.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("jTable2.columnModel.title2")); // NOI18N
        jTable2.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("jTable2.columnModel.title3")); // NOI18N

        jButton19.setAction(actionMap.get("saveCcessTable")); // NOI18N
        jButton19.setName("jButton19"); // NOI18N

        jButton20.setAction(actionMap.get("addAccessLine")); // NOI18N
        jButton20.setName("jButton20"); // NOI18N

        jButton77.setAction(actionMap.get("showHelp1")); // NOI18N
        jButton77.setName("jButton77"); // NOI18N

        javax.swing.GroupLayout access_pageLayout = new javax.swing.GroupLayout(access_page);
        access_page.setLayout(access_pageLayout);
        access_pageLayout.setHorizontalGroup(
            access_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(access_pageLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(access_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 698, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(access_pageLayout.createSequentialGroup()
                        .addComponent(jButton20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton77)))
                .addContainerGap(328, Short.MAX_VALUE))
        );
        access_pageLayout.setVerticalGroup(
            access_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(access_pageLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 448, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(access_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton20)
                    .addComponent(jButton19)
                    .addComponent(jButton77))
                .addContainerGap())
        );

        jTabbedPane1.addTab(resourceMap.getString("access_page.TabConstraints.tabTitle"), resourceMap.getIcon("access_page.TabConstraints.tabIcon"), access_page); // NOI18N

        auth_str.setName("auth_str"); // NOI18N

        jTextField30.setText(resourceMap.getString("jTextField30.text")); // NOI18N
        jTextField30.setName("jTextField30"); // NOI18N

        jButton17.setAction(actionMap.get("saveAuthParam")); // NOI18N
        jButton17.setName("jButton17"); // NOI18N

        jButton18.setAction(actionMap.get("removeAuthParam")); // NOI18N
        jButton18.setName("jButton18"); // NOI18N

        javax.swing.GroupLayout auth_strLayout = new javax.swing.GroupLayout(auth_str);
        auth_str.setLayout(auth_strLayout);
        auth_strLayout.setHorizontalGroup(
            auth_strLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(auth_strLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(auth_strLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField30, javax.swing.GroupLayout.DEFAULT_SIZE, 1014, Short.MAX_VALUE)
                    .addGroup(auth_strLayout.createSequentialGroup()
                        .addComponent(jButton17)
                        .addGap(18, 18, 18)
                        .addComponent(jButton18)))
                .addContainerGap())
        );
        auth_strLayout.setVerticalGroup(
            auth_strLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(auth_strLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTextField30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(auth_strLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton17)
                    .addComponent(jButton18))
                .addContainerGap(441, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(resourceMap.getString("auth_str.TabConstraints.tabTitle"), auth_str); // NOI18N

        delay_prop.setName("delay_prop"); // NOI18N

        jLabel43.setText(resourceMap.getString("jLabel43.text")); // NOI18N
        jLabel43.setName("jLabel43"); // NOI18N

        jComboBox5.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3" }));
        jComboBox5.setName("jComboBox5"); // NOI18N

        jLabel40.setText(resourceMap.getString("jLabel40.text")); // NOI18N
        jLabel40.setName("jLabel40"); // NOI18N

        jLabel42.setText(resourceMap.getString("jLabel42.text")); // NOI18N
        jLabel42.setName("jLabel42"); // NOI18N

        jLabel44.setText(resourceMap.getString("jLabel44.text")); // NOI18N
        jLabel44.setName("jLabel44"); // NOI18N

        jLabel45.setText(resourceMap.getString("jLabel45.text")); // NOI18N
        jLabel45.setName("jLabel45"); // NOI18N

        jLabel46.setText(resourceMap.getString("jLabel46.text")); // NOI18N
        jLabel46.setName("jLabel46"); // NOI18N

        jLabel47.setText(resourceMap.getString("jLabel47.text")); // NOI18N
        jLabel47.setName("jLabel47"); // NOI18N

        jTextField31.setText(resourceMap.getString("jTextField31.text")); // NOI18N
        jTextField31.setToolTipText(resourceMap.getString("jTextField31.toolTipText")); // NOI18N
        jTextField31.setName("jTextField31"); // NOI18N

        jTextField32.setText(resourceMap.getString("jTextField32.text")); // NOI18N
        jTextField32.setToolTipText(resourceMap.getString("jTextField32.toolTipText")); // NOI18N
        jTextField32.setName("jTextField32"); // NOI18N

        jTextField33.setText(resourceMap.getString("jTextField33.text")); // NOI18N
        jTextField33.setToolTipText(resourceMap.getString("jTextField33.toolTipText")); // NOI18N
        jTextField33.setName("jTextField33"); // NOI18N

        jTextField34.setText(resourceMap.getString("jTextField34.text")); // NOI18N
        jTextField34.setToolTipText(resourceMap.getString("jTextField34.toolTipText")); // NOI18N
        jTextField34.setName("jTextField34"); // NOI18N

        jTextField35.setText(resourceMap.getString("jTextField35.text")); // NOI18N
        jTextField35.setToolTipText(resourceMap.getString("jTextField35.toolTipText")); // NOI18N
        jTextField35.setName("jTextField35"); // NOI18N

        jTextField36.setText(resourceMap.getString("jTextField36.text")); // NOI18N
        jTextField36.setToolTipText(resourceMap.getString("jTextField36.toolTipText")); // NOI18N
        jTextField36.setName("jTextField36"); // NOI18N

        jButton21.setAction(actionMap.get("delDelay")); // NOI18N
        jButton21.setName("jButton21"); // NOI18N

        jButton22.setAction(actionMap.get("saveDelayProp")); // NOI18N
        jButton22.setName("jButton22"); // NOI18N

        javax.swing.GroupLayout delay_propLayout = new javax.swing.GroupLayout(delay_prop);
        delay_prop.setLayout(delay_propLayout);
        delay_propLayout.setHorizontalGroup(
            delay_propLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(delay_propLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(delay_propLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(delay_propLayout.createSequentialGroup()
                        .addGroup(delay_propLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(delay_propLayout.createSequentialGroup()
                                .addComponent(jLabel44)
                                .addGap(30, 30, 30)
                                .addComponent(jTextField33, javax.swing.GroupLayout.DEFAULT_SIZE, 542, Short.MAX_VALUE))
                            .addGroup(delay_propLayout.createSequentialGroup()
                                .addComponent(jLabel42)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField32, javax.swing.GroupLayout.DEFAULT_SIZE, 542, Short.MAX_VALUE))
                            .addGroup(delay_propLayout.createSequentialGroup()
                                .addGroup(delay_propLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel45)
                                    .addComponent(jLabel40)
                                    .addComponent(jLabel47)
                                    .addComponent(jLabel46)
                                    .addComponent(jLabel43))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(delay_propLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextField31, javax.swing.GroupLayout.DEFAULT_SIZE, 542, Short.MAX_VALUE)
                                    .addComponent(jComboBox5, 0, 542, Short.MAX_VALUE)
                                    .addComponent(jTextField34, javax.swing.GroupLayout.DEFAULT_SIZE, 542, Short.MAX_VALUE)
                                    .addComponent(jTextField35, javax.swing.GroupLayout.DEFAULT_SIZE, 542, Short.MAX_VALUE)
                                    .addComponent(jTextField36, javax.swing.GroupLayout.DEFAULT_SIZE, 542, Short.MAX_VALUE))))
                        .addGap(342, 342, 342))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, delay_propLayout.createSequentialGroup()
                        .addComponent(jButton21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton22)
                        .addContainerGap())))
        );
        delay_propLayout.setVerticalGroup(
            delay_propLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(delay_propLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(delay_propLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel43)
                    .addComponent(jComboBox5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(delay_propLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel40)
                    .addComponent(jTextField31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(delay_propLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel42)
                    .addComponent(jTextField32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(delay_propLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel44)
                    .addComponent(jTextField33, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(delay_propLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel45)
                    .addComponent(jTextField34, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(delay_propLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel47)
                    .addComponent(jTextField35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(delay_propLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel46)
                    .addComponent(jTextField36, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 280, Short.MAX_VALUE)
                .addGroup(delay_propLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton22)
                    .addComponent(jButton21))
                .addContainerGap())
        );

        jTabbedPane1.addTab(resourceMap.getString("delay_prop.TabConstraints.tabTitle"), delay_prop); // NOI18N

        delay_page.setName("delay_page"); // NOI18N

        jButton23.setAction(actionMap.get("addDelay")); // NOI18N
        jButton23.setName("jButton23"); // NOI18N

        jComboBox6.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3" }));
        jComboBox6.setName("jComboBox6"); // NOI18N

        jLabel48.setText(resourceMap.getString("jLabel48.text")); // NOI18N
        jLabel48.setName("jLabel48"); // NOI18N

        javax.swing.GroupLayout delay_pageLayout = new javax.swing.GroupLayout(delay_page);
        delay_page.setLayout(delay_pageLayout);
        delay_pageLayout.setHorizontalGroup(
            delay_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(delay_pageLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel48)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton23)
                .addContainerGap(845, Short.MAX_VALUE))
        );
        delay_pageLayout.setVerticalGroup(
            delay_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(delay_pageLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(delay_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel48)
                    .addComponent(jComboBox6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton23))
                .addContainerGap(466, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(resourceMap.getString("delay_page.TabConstraints.tabTitle"), delay_page); // NOI18N

        acl_time.setName("acl_time"); // NOI18N

        jButton24.setAction(actionMap.get("saveTimeAcl")); // NOI18N
        jButton24.setName("jButton24"); // NOI18N

        jButton25.setAction(actionMap.get("removeCurrentAcl")); // NOI18N
        jButton25.setName("jButton25"); // NOI18N

        jScrollPane10.setName("jScrollPane10"); // NOI18N

        jTable4.setFont(resourceMap.getFont("jTable4.font")); // NOI18N
        jTable4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Monday", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {"Tuesday", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {"Wednesday", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {"Thursday", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {"Friday", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {"Saturday", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {"Sunday", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Days", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable4.setName("jTable4"); // NOI18N
        jTable4.setSelectionBackground(resourceMap.getColor("jTable4.selectionBackground")); // NOI18N
        jTable4.setSelectionForeground(resourceMap.getColor("jTable4.selectionForeground")); // NOI18N
        jTable4.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jScrollPane10.setViewportView(jTable4);
        jTable4.getColumnModel().getColumn(0).setMinWidth(70);
        jTable4.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("jTable4.columnModel.title0")); // NOI18N
        jTable4.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("jTable4.columnModel.title10")); // NOI18N
        jTable4.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("jTable4.columnModel.title1")); // NOI18N
        jTable4.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("jTable4.columnModel.title2")); // NOI18N
        jTable4.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("jTable4.columnModel.title3")); // NOI18N
        jTable4.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("jTable4.columnModel.title4")); // NOI18N
        jTable4.getColumnModel().getColumn(6).setHeaderValue(resourceMap.getString("jTable4.columnModel.title5")); // NOI18N
        jTable4.getColumnModel().getColumn(7).setHeaderValue(resourceMap.getString("jTable4.columnModel.title6")); // NOI18N
        jTable4.getColumnModel().getColumn(8).setHeaderValue(resourceMap.getString("jTable4.columnModel.title7")); // NOI18N
        jTable4.getColumnModel().getColumn(9).setHeaderValue(resourceMap.getString("jTable4.columnModel.title8")); // NOI18N
        jTable4.getColumnModel().getColumn(10).setHeaderValue(resourceMap.getString("jTable4.columnModel.title9")); // NOI18N
        jTable4.getColumnModel().getColumn(11).setHeaderValue(resourceMap.getString("jTable4.columnModel.title11")); // NOI18N
        jTable4.getColumnModel().getColumn(12).setHeaderValue(resourceMap.getString("jTable4.columnModel.title12")); // NOI18N
        jTable4.getColumnModel().getColumn(13).setHeaderValue(resourceMap.getString("jTable4.columnModel.title13")); // NOI18N
        jTable4.getColumnModel().getColumn(14).setHeaderValue(resourceMap.getString("jTable4.columnModel.title14")); // NOI18N
        jTable4.getColumnModel().getColumn(15).setHeaderValue(resourceMap.getString("jTable4.columnModel.title15")); // NOI18N
        jTable4.getColumnModel().getColumn(16).setHeaderValue(resourceMap.getString("jTable4.columnModel.title16")); // NOI18N
        jTable4.getColumnModel().getColumn(17).setHeaderValue(resourceMap.getString("jTable4.columnModel.title17")); // NOI18N
        jTable4.getColumnModel().getColumn(18).setHeaderValue(resourceMap.getString("jTable4.columnModel.title18")); // NOI18N
        jTable4.getColumnModel().getColumn(19).setHeaderValue(resourceMap.getString("jTable4.columnModel.title19")); // NOI18N
        jTable4.getColumnModel().getColumn(20).setHeaderValue(resourceMap.getString("jTable4.columnModel.title20")); // NOI18N
        jTable4.getColumnModel().getColumn(21).setHeaderValue(resourceMap.getString("jTable4.columnModel.title21")); // NOI18N
        jTable4.getColumnModel().getColumn(22).setHeaderValue(resourceMap.getString("jTable4.columnModel.title22")); // NOI18N
        jTable4.getColumnModel().getColumn(23).setHeaderValue(resourceMap.getString("jTable4.columnModel.title23")); // NOI18N
        jTable4.getColumnModel().getColumn(24).setHeaderValue(resourceMap.getString("jTable4.columnModel.title24")); // NOI18N

        javax.swing.GroupLayout acl_timeLayout = new javax.swing.GroupLayout(acl_time);
        acl_time.setLayout(acl_timeLayout);
        acl_timeLayout.setHorizontalGroup(
            acl_timeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(acl_timeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(acl_timeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, acl_timeLayout.createSequentialGroup()
                        .addComponent(jButton25)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton24))
                    .addComponent(jScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 1014, Short.MAX_VALUE))
                .addContainerGap())
        );
        acl_timeLayout.setVerticalGroup(
            acl_timeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, acl_timeLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 442, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(acl_timeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton24)
                    .addComponent(jButton25))
                .addContainerGap())
        );

        jTabbedPane1.addTab(resourceMap.getString("acl_time.TabConstraints.tabTitle"), acl_time); // NOI18N

        cache_peer_page.setName("cache_peer_page"); // NOI18N

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        jTextField2.setEditable(false);
        jTextField2.setText(resourceMap.getString("jTextField2.text")); // NOI18N
        jTextField2.setName("jTextField2"); // NOI18N

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        jComboBox7.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "parent", "sibling", "multicast" }));
        jComboBox7.setToolTipText(resourceMap.getString("jComboBox7.toolTipText")); // NOI18N
        jComboBox7.setName("jComboBox7"); // NOI18N

        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        jTextField3.setText(resourceMap.getString("jTextField3.text")); // NOI18N
        jTextField3.setToolTipText(resourceMap.getString("jTextField3.toolTipText")); // NOI18N
        jTextField3.setName("jTextField3"); // NOI18N

        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N

        jTextField4.setText(resourceMap.getString("jTextField4.text")); // NOI18N
        jTextField4.setToolTipText(resourceMap.getString("jTextField4.toolTipText")); // NOI18N
        jTextField4.setName("jTextField4"); // NOI18N

        jCheckBox1.setText(resourceMap.getString("jCheckBox1.text")); // NOI18N
        jCheckBox1.setToolTipText(resourceMap.getString("jCheckBox1.toolTipText")); // NOI18N
        jCheckBox1.setName("jCheckBox1"); // NOI18N

        jCheckBox2.setText(resourceMap.getString("jCheckBox2.text")); // NOI18N
        jCheckBox2.setToolTipText(resourceMap.getString("jCheckBox2.toolTipText")); // NOI18N
        jCheckBox2.setName("jCheckBox2"); // NOI18N

        jCheckBox3.setText(resourceMap.getString("jCheckBox3.text")); // NOI18N
        jCheckBox3.setToolTipText(resourceMap.getString("jCheckBox3.toolTipText")); // NOI18N
        jCheckBox3.setName("jCheckBox3"); // NOI18N

        jCheckBox4.setText(resourceMap.getString("jCheckBox4.text")); // NOI18N
        jCheckBox4.setToolTipText(resourceMap.getString("jCheckBox4.toolTipText")); // NOI18N
        jCheckBox4.setName("jCheckBox4"); // NOI18N

        jCheckBox5.setText(resourceMap.getString("jCheckBox5.text")); // NOI18N
        jCheckBox5.setToolTipText(resourceMap.getString("jCheckBox5.toolTipText")); // NOI18N
        jCheckBox5.setName("jCheckBox5"); // NOI18N

        jCheckBox6.setText(resourceMap.getString("jCheckBox6.text")); // NOI18N
        jCheckBox6.setToolTipText(resourceMap.getString("jCheckBox6.toolTipText")); // NOI18N
        jCheckBox6.setName("jCheckBox6"); // NOI18N

        jCheckBox7.setText(resourceMap.getString("jCheckBox7.text")); // NOI18N
        jCheckBox7.setToolTipText(resourceMap.getString("jCheckBox7.toolTipText")); // NOI18N
        jCheckBox7.setName("jCheckBox7"); // NOI18N

        jCheckBox15.setText(resourceMap.getString("jCheckBox15.text")); // NOI18N
        jCheckBox15.setToolTipText(resourceMap.getString("jCheckBox15.toolTipText")); // NOI18N
        jCheckBox15.setName("jCheckBox15"); // NOI18N

        jCheckBox16.setText(resourceMap.getString("jCheckBox16.text")); // NOI18N
        jCheckBox16.setToolTipText(resourceMap.getString("jCheckBox16.toolTipText")); // NOI18N
        jCheckBox16.setName("jCheckBox16"); // NOI18N

        jCheckBox17.setText(resourceMap.getString("jCheckBox17.text")); // NOI18N
        jCheckBox17.setName("jCheckBox17"); // NOI18N

        jCheckBox18.setText(resourceMap.getString("jCheckBox18.text")); // NOI18N
        jCheckBox18.setName("jCheckBox18"); // NOI18N

        jCheckBox19.setText(resourceMap.getString("jCheckBox19.text")); // NOI18N
        jCheckBox19.setName("jCheckBox19"); // NOI18N

        jCheckBox20.setText(resourceMap.getString("jCheckBox20.text")); // NOI18N
        jCheckBox20.setName("jCheckBox20"); // NOI18N

        jCheckBox21.setText(resourceMap.getString("jCheckBox21.text")); // NOI18N
        jCheckBox21.setName("jCheckBox21"); // NOI18N

        jCheckBox22.setText(resourceMap.getString("jCheckBox22.text")); // NOI18N
        jCheckBox22.setName("jCheckBox22"); // NOI18N

        jCheckBox23.setText(resourceMap.getString("jCheckBox23.text")); // NOI18N
        jCheckBox23.setName("jCheckBox23"); // NOI18N

        jCheckBox24.setText(resourceMap.getString("jCheckBox24.text")); // NOI18N
        jCheckBox24.setName("jCheckBox24"); // NOI18N

        jLabel12.setText(resourceMap.getString("jLabel12.text")); // NOI18N
        jLabel12.setName("jLabel12"); // NOI18N

        jTextField5.setText(resourceMap.getString("jTextField5.text")); // NOI18N
        jTextField5.setName("jTextField5"); // NOI18N

        jLabel13.setText(resourceMap.getString("jLabel13.text")); // NOI18N
        jLabel13.setName("jLabel13"); // NOI18N

        jTextField6.setText(resourceMap.getString("jTextField6.text")); // NOI18N
        jTextField6.setName("jTextField6"); // NOI18N

        jLabel14.setText(resourceMap.getString("jLabel14.text")); // NOI18N
        jLabel14.setName("jLabel14"); // NOI18N

        jComboBox8.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "1", "2", "3", "4" }));
        jComboBox8.setName("jComboBox8"); // NOI18N

        jLabel15.setText(resourceMap.getString("jLabel15.text")); // NOI18N
        jLabel15.setName("jLabel15"); // NOI18N

        jTextField7.setText(resourceMap.getString("jTextField7.text")); // NOI18N
        jTextField7.setName("jTextField7"); // NOI18N

        jLabel16.setText(resourceMap.getString("jLabel16.text")); // NOI18N
        jLabel16.setName("jLabel16"); // NOI18N

        jTextField8.setText(resourceMap.getString("jTextField8.text")); // NOI18N
        jTextField8.setName("jTextField8"); // NOI18N

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator2.setName("jSeparator2"); // NOI18N

        jLabel17.setText(resourceMap.getString("jLabel17.text")); // NOI18N
        jLabel17.setName("jLabel17"); // NOI18N

        jTextField9.setText(resourceMap.getString("jTextField9.text")); // NOI18N
        jTextField9.setName("jTextField9"); // NOI18N

        jLabel18.setText(resourceMap.getString("jLabel18.text")); // NOI18N
        jLabel18.setName("jLabel18"); // NOI18N

        jTextField10.setText(resourceMap.getString("jTextField10.text")); // NOI18N
        jTextField10.setName("jTextField10"); // NOI18N

        jLabel19.setText(resourceMap.getString("jLabel19.text")); // NOI18N
        jLabel19.setName("jLabel19"); // NOI18N

        jTextField11.setText(resourceMap.getString("jTextField11.text")); // NOI18N
        jTextField11.setName("jTextField11"); // NOI18N

        jLabel20.setText(resourceMap.getString("jLabel20.text")); // NOI18N
        jLabel20.setName("jLabel20"); // NOI18N

        jTextField12.setText(resourceMap.getString("jTextField12.text")); // NOI18N
        jTextField12.setName("jTextField12"); // NOI18N

        jLabel21.setText(resourceMap.getString("jLabel21.text")); // NOI18N
        jLabel21.setName("jLabel21"); // NOI18N

        jTextField13.setText(resourceMap.getString("jTextField13.text")); // NOI18N
        jTextField13.setName("jTextField13"); // NOI18N

        jLabel22.setText(resourceMap.getString("jLabel22.text")); // NOI18N
        jLabel22.setName("jLabel22"); // NOI18N

        jTextField14.setText(resourceMap.getString("jTextField14.text")); // NOI18N
        jTextField14.setName("jTextField14"); // NOI18N

        jLabel23.setText(resourceMap.getString("jLabel23.text")); // NOI18N
        jLabel23.setName("jLabel23"); // NOI18N

        jTextField15.setText(resourceMap.getString("jTextField15.text")); // NOI18N
        jTextField15.setName("jTextField15"); // NOI18N

        jLabel24.setText(resourceMap.getString("jLabel24.text")); // NOI18N
        jLabel24.setName("jLabel24"); // NOI18N

        jTextField16.setText(resourceMap.getString("jTextField16.text")); // NOI18N
        jTextField16.setName("jTextField16"); // NOI18N

        jLabel25.setText(resourceMap.getString("jLabel25.text")); // NOI18N
        jLabel25.setName("jLabel25"); // NOI18N

        jTextField17.setText(resourceMap.getString("jTextField17.text")); // NOI18N
        jTextField17.setName("jTextField17"); // NOI18N

        jLabel26.setText(resourceMap.getString("jLabel26.text")); // NOI18N
        jLabel26.setName("jLabel26"); // NOI18N

        jTextField37.setText(resourceMap.getString("jTextField37.text")); // NOI18N
        jTextField37.setName("jTextField37"); // NOI18N

        jLabel27.setText(resourceMap.getString("jLabel27.text")); // NOI18N
        jLabel27.setName("jLabel27"); // NOI18N

        jTextField38.setText(resourceMap.getString("jTextField38.text")); // NOI18N
        jTextField38.setName("jTextField38"); // NOI18N

        jLabel28.setText(resourceMap.getString("jLabel28.text")); // NOI18N
        jLabel28.setName("jLabel28"); // NOI18N

        jComboBox9.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "on", "auto" }));
        jComboBox9.setName("jComboBox9"); // NOI18N

        jLabel29.setText(resourceMap.getString("jLabel29.text")); // NOI18N
        jLabel29.setName("jLabel29"); // NOI18N

        jComboBox10.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "on", "off", "auto" }));
        jComboBox10.setName("jComboBox10"); // NOI18N

        jButton3.setAction(actionMap.get("saveCachePeer")); // NOI18N
        jButton3.setName("jButton3"); // NOI18N

        jButton26.setAction(actionMap.get("deleteCachePeer")); // NOI18N
        jButton26.setName("jButton26"); // NOI18N

        javax.swing.GroupLayout cache_peer_pageLayout = new javax.swing.GroupLayout(cache_peer_page);
        cache_peer_page.setLayout(cache_peer_pageLayout);
        cache_peer_pageLayout.setHorizontalGroup(
            cache_peer_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cache_peer_pageLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(cache_peer_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(cache_peer_pageLayout.createSequentialGroup()
                        .addComponent(jCheckBox24)
                        .addGap(305, 305, 305))
                    .addGroup(cache_peer_pageLayout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addGap(307, 307, 307))
                    .addGroup(cache_peer_pageLayout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addGap(8, 8, 8)
                        .addComponent(jTextField7, javax.swing.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE))
                    .addGroup(cache_peer_pageLayout.createSequentialGroup()
                        .addGroup(cache_peer_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(cache_peer_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField2, javax.swing.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE)
                            .addComponent(jComboBox7, 0, 273, Short.MAX_VALUE)))
                    .addGroup(cache_peer_pageLayout.createSequentialGroup()
                        .addGroup(cache_peer_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(cache_peer_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField4, javax.swing.GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE)
                            .addComponent(jTextField3, javax.swing.GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE)))
                    .addGroup(cache_peer_pageLayout.createSequentialGroup()
                        .addGroup(cache_peer_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14)
                            .addComponent(jLabel15))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(cache_peer_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField6, javax.swing.GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE)
                            .addComponent(jComboBox8, 0, 272, Short.MAX_VALUE)))
                    .addGroup(cache_peer_pageLayout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addGap(36, 36, 36)
                        .addGroup(cache_peer_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField8, javax.swing.GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE)
                            .addComponent(jTextField5, javax.swing.GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, cache_peer_pageLayout.createSequentialGroup()
                        .addGroup(cache_peer_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(cache_peer_pageLayout.createSequentialGroup()
                                .addComponent(jCheckBox15)
                                .addGap(66, 66, 66))
                            .addGroup(cache_peer_pageLayout.createSequentialGroup()
                                .addComponent(jCheckBox7)
                                .addGap(50, 50, 50))
                            .addComponent(jCheckBox6)
                            .addGroup(cache_peer_pageLayout.createSequentialGroup()
                                .addComponent(jCheckBox5)
                                .addGap(103, 103, 103))
                            .addGroup(cache_peer_pageLayout.createSequentialGroup()
                                .addComponent(jCheckBox2)
                                .addGap(70, 70, 70))
                            .addGroup(cache_peer_pageLayout.createSequentialGroup()
                                .addComponent(jCheckBox3)
                                .addGap(85, 85, 85))
                            .addGroup(cache_peer_pageLayout.createSequentialGroup()
                                .addComponent(jCheckBox4)
                                .addGap(51, 51, 51))
                            .addComponent(jCheckBox1, javax.swing.GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(cache_peer_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBox19)
                            .addComponent(jCheckBox20)
                            .addComponent(jCheckBox17)
                            .addComponent(jCheckBox16)
                            .addComponent(jCheckBox18)
                            .addComponent(jCheckBox21)
                            .addComponent(jCheckBox22)
                            .addComponent(jCheckBox23))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(cache_peer_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(cache_peer_pageLayout.createSequentialGroup()
                        .addGroup(cache_peer_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel17)
                            .addComponent(jLabel18)
                            .addComponent(jLabel19))
                        .addGap(74, 74, 74)
                        .addGroup(cache_peer_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField9, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jTextField10, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(cache_peer_pageLayout.createSequentialGroup()
                        .addComponent(jButton26)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3))
                    .addGroup(cache_peer_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, cache_peer_pageLayout.createSequentialGroup()
                            .addComponent(jLabel29)
                            .addGap(11, 11, 11)
                            .addComponent(jComboBox9, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, cache_peer_pageLayout.createSequentialGroup()
                            .addGroup(cache_peer_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel26)
                                .addComponent(jLabel27)
                                .addComponent(jLabel28))
                            .addGap(13, 13, 13)
                            .addGroup(cache_peer_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jTextField38)
                                .addComponent(jComboBox10, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jTextField37, javax.swing.GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE))))
                    .addGroup(cache_peer_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, cache_peer_pageLayout.createSequentialGroup()
                            .addComponent(jLabel25)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jTextField17, javax.swing.GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, cache_peer_pageLayout.createSequentialGroup()
                            .addComponent(jLabel24)
                            .addGap(42, 42, 42)
                            .addComponent(jTextField16, javax.swing.GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, cache_peer_pageLayout.createSequentialGroup()
                            .addGroup(cache_peer_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel20)
                                .addComponent(jLabel21)
                                .addComponent(jLabel22)
                                .addComponent(jLabel23))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(cache_peer_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jTextField15, javax.swing.GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE)
                                .addComponent(jTextField14, javax.swing.GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE)
                                .addComponent(jTextField12, javax.swing.GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE)
                                .addComponent(jTextField13, javax.swing.GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE)))))
                .addGap(835, 835, 835))
        );
        cache_peer_pageLayout.setVerticalGroup(
            cache_peer_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cache_peer_pageLayout.createSequentialGroup()
                .addGroup(cache_peer_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(cache_peer_pageLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(cache_peer_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, cache_peer_pageLayout.createSequentialGroup()
                                .addGroup(cache_peer_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel17)
                                    .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(cache_peer_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel18)
                                    .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(cache_peer_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel19)
                                    .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(cache_peer_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel20)
                                    .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(cache_peer_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel21)
                                    .addComponent(jTextField13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(cache_peer_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel22)
                                    .addComponent(jTextField14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(cache_peer_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel23)
                                    .addComponent(jTextField15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(cache_peer_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel24)
                                    .addComponent(jTextField16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(cache_peer_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel25)
                                    .addComponent(jTextField17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(cache_peer_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel26)
                                    .addComponent(jTextField37, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(cache_peer_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel27)
                                    .addComponent(jTextField38, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(6, 6, 6)
                                .addGroup(cache_peer_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel28)
                                    .addComponent(jComboBox10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(cache_peer_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel29)
                                    .addComponent(jComboBox9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(cache_peer_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jButton26)
                                    .addComponent(jButton3)))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, cache_peer_pageLayout.createSequentialGroup()
                                .addGroup(cache_peer_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel4)
                                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(cache_peer_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel6)
                                    .addComponent(jComboBox7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(cache_peer_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel7)
                                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(cache_peer_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel8)
                                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(cache_peer_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(cache_peer_pageLayout.createSequentialGroup()
                                        .addComponent(jCheckBox1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jCheckBox2)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jCheckBox3)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jCheckBox4)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jCheckBox5)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jCheckBox6)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jCheckBox7)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jCheckBox15)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jCheckBox24))
                                    .addGroup(cache_peer_pageLayout.createSequentialGroup()
                                        .addComponent(jCheckBox16)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jCheckBox17)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jCheckBox18)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jCheckBox19)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jCheckBox20)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jCheckBox21)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jCheckBox22)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jCheckBox23)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(cache_peer_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel12)
                                    .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(cache_peer_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel13)
                                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(cache_peer_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel14)
                                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(cache_peer_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel15)
                                    .addComponent(jComboBox8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(cache_peer_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel16)
                                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(cache_peer_pageLayout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 431, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(57, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(resourceMap.getString("cache_peer_page.TabConstraints.tabTitle"), cache_peer_page); // NOI18N

        ssh_page.setName("ssh_page"); // NOI18N

        jLabel30.setText(resourceMap.getString("jLabel30.text")); // NOI18N
        jLabel30.setName("jLabel30"); // NOI18N

        jLabel31.setText(resourceMap.getString("jLabel31.text")); // NOI18N
        jLabel31.setName("jLabel31"); // NOI18N

        jLabel33.setText(resourceMap.getString("jLabel33.text")); // NOI18N
        jLabel33.setName("jLabel33"); // NOI18N

        jTextField40.setText(resourceMap.getString("jTextField40.text")); // NOI18N
        jTextField40.setName("jTextField40"); // NOI18N

        jTextField43.setText(resourceMap.getString("jTextField43.text")); // NOI18N
        jTextField43.setName("jTextField43"); // NOI18N

        jPasswordField1.setText(resourceMap.getString("jPasswordField1.text")); // NOI18N
        jPasswordField1.setName("jPasswordField1"); // NOI18N

        jButton63.setAction(actionMap.get("saveSSH")); // NOI18N
        jButton63.setName("jButton63"); // NOI18N

        jButton64.setAction(actionMap.get("delSsh")); // NOI18N
        jButton64.setName("jButton64"); // NOI18N

        jLabel34.setText(resourceMap.getString("jLabel34.text")); // NOI18N
        jLabel34.setName("jLabel34"); // NOI18N

        jTextField39.setText(resourceMap.getString("jTextField39.text")); // NOI18N
        jTextField39.setName("jTextField39"); // NOI18N

        jTabbedPane3.setName("jTabbedPane3"); // NOI18N

        jPanel2.setName("jPanel2"); // NOI18N

        jLabel32.setText(resourceMap.getString("jLabel32.text")); // NOI18N
        jLabel32.setName("jLabel32"); // NOI18N

        jTextField41.setText(resourceMap.getString("jTextField41.text")); // NOI18N
        jTextField41.setName("jTextField41"); // NOI18N

        jLabel49.setText(resourceMap.getString("jLabel49.text")); // NOI18N
        jLabel49.setName("jLabel49"); // NOI18N

        jTextField42.setText(resourceMap.getString("jTextField42.text")); // NOI18N
        jTextField42.setName("jTextField42"); // NOI18N

        jButton65.setAction(actionMap.get("getProcInfo")); // NOI18N
        jButton65.setFont(resourceMap.getFont("jButton65.font")); // NOI18N
        jButton65.setName("jButton65"); // NOI18N

        jButton66.setAction(actionMap.get("downloadSquidConf1")); // NOI18N
        jButton66.setFont(resourceMap.getFont("jButton66.font")); // NOI18N
        jButton66.setName("jButton66"); // NOI18N

        jButton67.setAction(actionMap.get("uploadConf")); // NOI18N
        jButton67.setName("jButton67"); // NOI18N

        jCheckBox91.setText(resourceMap.getString("jCheckBox91.text")); // NOI18N
        jCheckBox91.setName("jCheckBox91"); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel32)
                    .addComponent(jLabel49)
                    .addComponent(jCheckBox91))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField42, javax.swing.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)
                    .addComponent(jTextField41, javax.swing.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jButton67, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton65, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton66, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel32)
                    .addComponent(jTextField41, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel49)
                    .addComponent(jTextField42, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton65)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton66)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton67)
                    .addComponent(jCheckBox91))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        jTabbedPane3.addTab(resourceMap.getString("jPanel2.TabConstraints.tabTitle"), jPanel2); // NOI18N

        javax.swing.GroupLayout ssh_pageLayout = new javax.swing.GroupLayout(ssh_page);
        ssh_page.setLayout(ssh_pageLayout);
        ssh_pageLayout.setHorizontalGroup(
            ssh_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ssh_pageLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jButton63)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton64)
                .addGap(834, 834, 834))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ssh_pageLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ssh_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, ssh_pageLayout.createSequentialGroup()
                        .addGroup(ssh_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel34)
                            .addComponent(jLabel31)
                            .addComponent(jLabel30))
                        .addGap(65, 65, 65)
                        .addGroup(ssh_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPasswordField1)
                            .addComponent(jTextField43)
                            .addComponent(jTextField39, javax.swing.GroupLayout.DEFAULT_SIZE, 349, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, ssh_pageLayout.createSequentialGroup()
                        .addComponent(jLabel33)
                        .addGap(74, 74, 74)
                        .addComponent(jTextField40)))
                .addContainerGap(551, Short.MAX_VALUE))
            .addGroup(ssh_pageLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 471, Short.MAX_VALUE)
                .addGap(561, 561, 561))
        );
        ssh_pageLayout.setVerticalGroup(
            ssh_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ssh_pageLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ssh_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel34)
                    .addComponent(jTextField39, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ssh_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30)
                    .addComponent(jTextField43, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ssh_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel31)
                    .addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ssh_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel33)
                    .addComponent(jTextField40, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(90, 90, 90)
                .addGroup(ssh_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton63)
                    .addComponent(jButton64))
                .addGap(79, 79, 79))
        );

        jTabbedPane1.addTab(resourceMap.getString("ssh_page.TabConstraints.tabTitle"), ssh_page); // NOI18N

        external_acl_page.setName("external_acl_page"); // NOI18N

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        jLabel50.setText(resourceMap.getString("jLabel50.text")); // NOI18N
        jLabel50.setName("jLabel50"); // NOI18N

        jLabel51.setText(resourceMap.getString("jLabel51.text")); // NOI18N
        jLabel51.setName("jLabel51"); // NOI18N

        jLabel52.setText(resourceMap.getString("jLabel52.text")); // NOI18N
        jLabel52.setName("jLabel52"); // NOI18N

        jLabel53.setText(resourceMap.getString("jLabel53.text")); // NOI18N
        jLabel53.setName("jLabel53"); // NOI18N

        jLabel54.setText(resourceMap.getString("jLabel54.text")); // NOI18N
        jLabel54.setName("jLabel54"); // NOI18N

        jCheckBox25.setText(resourceMap.getString("jCheckBox25.text")); // NOI18N
        jCheckBox25.setToolTipText(resourceMap.getString("jCheckBox25.toolTipText")); // NOI18N
        jCheckBox25.setName("jCheckBox25"); // NOI18N

        jTextField45.setText(resourceMap.getString("jTextField45.text")); // NOI18N
        jTextField45.setToolTipText(resourceMap.getString("jTextField45.toolTipText")); // NOI18N
        jTextField45.setName("jTextField45"); // NOI18N

        jTextField46.setText(resourceMap.getString("jTextField46.text")); // NOI18N
        jTextField46.setToolTipText(resourceMap.getString("jTextField46.toolTipText")); // NOI18N
        jTextField46.setName("jTextField46"); // NOI18N

        jTextField47.setText(resourceMap.getString("jTextField47.text")); // NOI18N
        jTextField47.setToolTipText(resourceMap.getString("jTextField47.toolTipText")); // NOI18N
        jTextField47.setName("jTextField47"); // NOI18N

        jTextField48.setText(resourceMap.getString("jTextField48.text")); // NOI18N
        jTextField48.setToolTipText(resourceMap.getString("jTextField48.toolTipText")); // NOI18N
        jTextField48.setName("jTextField48"); // NOI18N

        jTextField49.setText(resourceMap.getString("jTextField49.text")); // NOI18N
        jTextField49.setToolTipText(resourceMap.getString("jTextField49.toolTipText")); // NOI18N
        jTextField49.setName("jTextField49"); // NOI18N

        jTextField50.setText(resourceMap.getString("jTextField50.text")); // NOI18N
        jTextField50.setToolTipText(resourceMap.getString("jTextField50.toolTipText")); // NOI18N
        jTextField50.setName("jTextField50"); // NOI18N

        jLabel55.setText(resourceMap.getString("jLabel55.text")); // NOI18N
        jLabel55.setName("jLabel55"); // NOI18N

        jTextField51.setText(resourceMap.getString("jTextField51.text")); // NOI18N
        jTextField51.setToolTipText(resourceMap.getString("jTextField51.toolTipText")); // NOI18N
        jTextField51.setName("jTextField51"); // NOI18N

        jLabel56.setText(resourceMap.getString("jLabel56.text")); // NOI18N
        jLabel56.setName("jLabel56"); // NOI18N

        jTextField52.setText(resourceMap.getString("jTextField52.text")); // NOI18N
        jTextField52.setToolTipText(resourceMap.getString("jTextField52.toolTipText")); // NOI18N
        jTextField52.setName("jTextField52"); // NOI18N

        jButton27.setAction(actionMap.get("saveExternalAclType")); // NOI18N
        jButton27.setName("jButton27"); // NOI18N

        jButton28.setAction(actionMap.get("delExternalAclType")); // NOI18N
        jButton28.setName("jButton28"); // NOI18N

        jLabel57.setText(resourceMap.getString("jLabel57.text")); // NOI18N
        jLabel57.setName("jLabel57"); // NOI18N

        jTextField53.setEditable(false);
        jTextField53.setText(resourceMap.getString("jTextField53.text")); // NOI18N
        jTextField53.setName("jTextField53"); // NOI18N

        javax.swing.GroupLayout external_acl_pageLayout = new javax.swing.GroupLayout(external_acl_page);
        external_acl_page.setLayout(external_acl_pageLayout);
        external_acl_pageLayout.setHorizontalGroup(
            external_acl_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(external_acl_pageLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(external_acl_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(external_acl_pageLayout.createSequentialGroup()
                        .addGroup(external_acl_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel57)
                            .addGroup(external_acl_pageLayout.createSequentialGroup()
                                .addGap(85, 85, 85)
                                .addComponent(jTextField53, javax.swing.GroupLayout.DEFAULT_SIZE, 1526, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(external_acl_pageLayout.createSequentialGroup()
                        .addGroup(external_acl_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(external_acl_pageLayout.createSequentialGroup()
                                .addGroup(external_acl_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel52)
                                    .addComponent(jLabel53)
                                    .addComponent(jLabel54)
                                    .addComponent(jLabel55)
                                    .addComponent(jLabel56))
                                .addGap(12, 12, 12)
                                .addGroup(external_acl_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextField51, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                                    .addComponent(jTextField52, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                                    .addComponent(jTextField48, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                                    .addComponent(jTextField49, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                                    .addComponent(jTextField50, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                                    .addComponent(jCheckBox25)))
                            .addGroup(external_acl_pageLayout.createSequentialGroup()
                                .addGroup(external_acl_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel50)
                                    .addComponent(jLabel51)
                                    .addComponent(jLabel5))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(external_acl_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jTextField45, javax.swing.GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE)
                                    .addComponent(jTextField46)
                                    .addComponent(jTextField47))))
                        .addGap(1299, 1299, 1299))
                    .addGroup(external_acl_pageLayout.createSequentialGroup()
                        .addComponent(jButton28)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton27)
                        .addContainerGap(819, Short.MAX_VALUE))))
        );
        external_acl_pageLayout.setVerticalGroup(
            external_acl_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, external_acl_pageLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(external_acl_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel57)
                    .addComponent(jTextField53, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(external_acl_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jTextField45, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(external_acl_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel50)
                    .addComponent(jTextField46, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(external_acl_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel51)
                    .addComponent(jTextField47, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(external_acl_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel52)
                    .addComponent(jTextField48, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(external_acl_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel53)
                    .addComponent(jTextField49, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(external_acl_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel54)
                    .addComponent(jTextField50, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(external_acl_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(external_acl_pageLayout.createSequentialGroup()
                        .addComponent(jTextField51, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(external_acl_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField52, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel56)))
                    .addComponent(jLabel55))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox25)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(external_acl_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton28)
                    .addComponent(jButton27))
                .addGap(246, 246, 246))
        );

        jTabbedPane1.addTab(resourceMap.getString("external_acl_page.TabConstraints.tabTitle"), external_acl_page); // NOI18N

        https_port_page.setName("https_port_page"); // NOI18N

        jLabel58.setText(resourceMap.getString("jLabel58.text")); // NOI18N
        jLabel58.setName("jLabel58"); // NOI18N

        jLabel59.setText(resourceMap.getString("jLabel59.text")); // NOI18N
        jLabel59.setName("jLabel59"); // NOI18N

        jCheckBox26.setText(resourceMap.getString("jCheckBox26.text")); // NOI18N
        jCheckBox26.setName("jCheckBox26"); // NOI18N

        jCheckBox27.setText(resourceMap.getString("jCheckBox27.text")); // NOI18N
        jCheckBox27.setName("jCheckBox27"); // NOI18N

        jCheckBox28.setText(resourceMap.getString("jCheckBox28.text")); // NOI18N
        jCheckBox28.setName("jCheckBox28"); // NOI18N

        jTextField54.setText(resourceMap.getString("jTextField54.text")); // NOI18N
        jTextField54.setName("jTextField54"); // NOI18N

        jTextField55.setText(resourceMap.getString("jTextField55.text")); // NOI18N
        jTextField55.setToolTipText(resourceMap.getString("jTextField55.toolTipText")); // NOI18N
        jTextField55.setName("jTextField55"); // NOI18N

        jTextField56.setText(resourceMap.getString("jTextField56.text")); // NOI18N
        jTextField56.setName("jTextField56"); // NOI18N

        jLabel60.setText(resourceMap.getString("jLabel60.text")); // NOI18N
        jLabel60.setName("jLabel60"); // NOI18N

        jLabel61.setText(resourceMap.getString("jLabel61.text")); // NOI18N
        jLabel61.setName("jLabel61"); // NOI18N

        jLabel62.setText(resourceMap.getString("jLabel62.text")); // NOI18N
        jLabel62.setName("jLabel62"); // NOI18N

        jLabel63.setText(resourceMap.getString("jLabel63.text")); // NOI18N
        jLabel63.setName("jLabel63"); // NOI18N

        jLabel64.setText(resourceMap.getString("jLabel64.text")); // NOI18N
        jLabel64.setName("jLabel64"); // NOI18N

        jLabel65.setText(resourceMap.getString("jLabel65.text")); // NOI18N
        jLabel65.setName("jLabel65"); // NOI18N

        jLabel66.setText(resourceMap.getString("jLabel66.text")); // NOI18N
        jLabel66.setName("jLabel66"); // NOI18N

        jLabel67.setText(resourceMap.getString("jLabel67.text")); // NOI18N
        jLabel67.setName("jLabel67"); // NOI18N

        jLabel68.setText(resourceMap.getString("jLabel68.text")); // NOI18N
        jLabel68.setName("jLabel68"); // NOI18N

        jLabel69.setText(resourceMap.getString("jLabel69.text")); // NOI18N
        jLabel69.setName("jLabel69"); // NOI18N

        jLabel70.setText(resourceMap.getString("jLabel70.text")); // NOI18N
        jLabel70.setName("jLabel70"); // NOI18N

        jLabel71.setText(resourceMap.getString("jLabel71.text")); // NOI18N
        jLabel71.setName("jLabel71"); // NOI18N

        jLabel72.setText(resourceMap.getString("jLabel72.text")); // NOI18N
        jLabel72.setName("jLabel72"); // NOI18N

        jLabel73.setText(resourceMap.getString("jLabel73.text")); // NOI18N
        jLabel73.setName("jLabel73"); // NOI18N

        jLabel74.setText(resourceMap.getString("jLabel74.text")); // NOI18N
        jLabel74.setName("jLabel74"); // NOI18N

        jTextField57.setText(resourceMap.getString("jTextField57.text")); // NOI18N
        jTextField57.setToolTipText(resourceMap.getString("jTextField57.toolTipText")); // NOI18N
        jTextField57.setName("jTextField57"); // NOI18N

        jTextField58.setText(resourceMap.getString("jTextField58.text")); // NOI18N
        jTextField58.setToolTipText(resourceMap.getString("jTextField58.toolTipText")); // NOI18N
        jTextField58.setName("jTextField58"); // NOI18N

        jTextField59.setText(resourceMap.getString("jTextField59.text")); // NOI18N
        jTextField59.setToolTipText(resourceMap.getString("jTextField59.toolTipText")); // NOI18N
        jTextField59.setName("jTextField59"); // NOI18N

        jTextField60.setText(resourceMap.getString("jTextField60.text")); // NOI18N
        jTextField60.setToolTipText(resourceMap.getString("jTextField60.toolTipText")); // NOI18N
        jTextField60.setName("jTextField60"); // NOI18N

        jTextField61.setText(resourceMap.getString("jTextField61.text")); // NOI18N
        jTextField61.setToolTipText(resourceMap.getString("jTextField61.toolTipText")); // NOI18N
        jTextField61.setName("jTextField61"); // NOI18N

        jTextField63.setText(resourceMap.getString("jTextField63.text")); // NOI18N
        jTextField63.setToolTipText(resourceMap.getString("jTextField63.toolTipText")); // NOI18N
        jTextField63.setName("jTextField63"); // NOI18N

        jTextField64.setText(resourceMap.getString("jTextField64.text")); // NOI18N
        jTextField64.setToolTipText(resourceMap.getString("jTextField64.toolTipText")); // NOI18N
        jTextField64.setName("jTextField64"); // NOI18N

        jTextField65.setText(resourceMap.getString("jTextField65.text")); // NOI18N
        jTextField65.setToolTipText(resourceMap.getString("jTextField65.toolTipText")); // NOI18N
        jTextField65.setName("jTextField65"); // NOI18N

        jTextField66.setText(resourceMap.getString("jTextField66.text")); // NOI18N
        jTextField66.setToolTipText(resourceMap.getString("jTextField66.toolTipText")); // NOI18N
        jTextField66.setName("jTextField66"); // NOI18N

        jTextField67.setText(resourceMap.getString("jTextField67.text")); // NOI18N
        jTextField67.setToolTipText(resourceMap.getString("jTextField67.toolTipText")); // NOI18N
        jTextField67.setName("jTextField67"); // NOI18N

        jTextField68.setText(resourceMap.getString("jTextField68.text")); // NOI18N
        jTextField68.setName("jTextField68"); // NOI18N

        jTextField69.setText(resourceMap.getString("jTextField69.text")); // NOI18N
        jTextField69.setToolTipText(resourceMap.getString("jTextField69.toolTipText")); // NOI18N
        jTextField69.setName("jTextField69"); // NOI18N

        jTextField70.setText(resourceMap.getString("jTextField70.text")); // NOI18N
        jTextField70.setToolTipText(resourceMap.getString("jTextField70.toolTipText")); // NOI18N
        jTextField70.setName("jTextField70"); // NOI18N

        jTextField71.setText(resourceMap.getString("jTextField71.text")); // NOI18N
        jTextField71.setToolTipText(resourceMap.getString("jTextField71.toolTipText")); // NOI18N
        jTextField71.setName("jTextField71"); // NOI18N

        jButton29.setAction(actionMap.get("save_https_port")); // NOI18N
        jButton29.setName("jButton29"); // NOI18N

        jButton30.setAction(actionMap.get("emptyOnceParam")); // NOI18N
        jButton30.setName("jButton30"); // NOI18N

        jComboBox11.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "1", "2", "3", "4" }));
        jComboBox11.setToolTipText(resourceMap.getString("jComboBox11.toolTipText")); // NOI18N
        jComboBox11.setName("jComboBox11"); // NOI18N

        jSeparator5.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator5.setName("jSeparator5"); // NOI18N

        javax.swing.GroupLayout https_port_pageLayout = new javax.swing.GroupLayout(https_port_page);
        https_port_page.setLayout(https_port_pageLayout);
        https_port_pageLayout.setHorizontalGroup(
            https_port_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(https_port_pageLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(https_port_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(https_port_pageLayout.createSequentialGroup()
                        .addComponent(jCheckBox26)
                        .addGap(18, 18, 18)
                        .addComponent(jCheckBox27))
                    .addGroup(https_port_pageLayout.createSequentialGroup()
                        .addComponent(jButton29)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton30))
                    .addGroup(https_port_pageLayout.createSequentialGroup()
                        .addGroup(https_port_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel58)
                            .addComponent(jLabel59))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(https_port_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField55)
                            .addComponent(jTextField54, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE)))
                    .addGroup(https_port_pageLayout.createSequentialGroup()
                        .addGroup(https_port_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel60)
                            .addComponent(jCheckBox28)
                            .addComponent(jLabel61)
                            .addComponent(jLabel62)
                            .addComponent(jLabel63)
                            .addComponent(jLabel64))
                        .addGap(12, 12, 12)
                        .addGroup(https_port_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField61)
                            .addComponent(jTextField60)
                            .addComponent(jTextField59)
                            .addComponent(jTextField56)
                            .addComponent(jTextField58)
                            .addComponent(jTextField57, javax.swing.GroupLayout.DEFAULT_SIZE, 174, Short.MAX_VALUE))))
                .addGap(7, 7, 7)
                .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(https_port_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel74)
                    .addGroup(https_port_pageLayout.createSequentialGroup()
                        .addGroup(https_port_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel65)
                            .addComponent(jLabel66)
                            .addComponent(jLabel67)
                            .addComponent(jLabel68)
                            .addComponent(jLabel69)
                            .addComponent(jLabel70)
                            .addComponent(jLabel71)
                            .addComponent(jLabel72)
                            .addComponent(jLabel73))
                        .addGap(12, 12, 12)
                        .addGroup(https_port_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField71, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jTextField70, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jTextField69, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jTextField68, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jTextField67, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jTextField66, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jTextField65, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jTextField64, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jTextField63, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jComboBox11, 0, 324, Short.MAX_VALUE))))
                .addGap(357, 357, 357))
        );
        https_port_pageLayout.setVerticalGroup(
            https_port_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(https_port_pageLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(https_port_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(https_port_pageLayout.createSequentialGroup()
                        .addGroup(https_port_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel65)
                            .addComponent(jComboBox11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(https_port_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                            .addComponent(jLabel66)
                            .addComponent(jTextField63, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(https_port_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                            .addComponent(jLabel67)
                            .addComponent(jTextField64, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(https_port_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                            .addComponent(jLabel68)
                            .addComponent(jTextField65, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(https_port_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                            .addComponent(jLabel69)
                            .addComponent(jTextField66, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(https_port_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                            .addComponent(jLabel70)
                            .addComponent(jTextField67, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(https_port_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                            .addComponent(jLabel71)
                            .addComponent(jTextField68, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(https_port_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                            .addComponent(jLabel72)
                            .addComponent(jTextField69, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(https_port_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                            .addComponent(jLabel73)
                            .addComponent(jTextField70, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(https_port_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                            .addComponent(jLabel74)
                            .addComponent(jTextField71, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(https_port_pageLayout.createSequentialGroup()
                        .addGroup(https_port_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel58)
                            .addComponent(jTextField54, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(https_port_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel59)
                            .addComponent(jTextField55, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(https_port_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jCheckBox27)
                            .addComponent(jCheckBox26))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(https_port_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jCheckBox28)
                            .addComponent(jTextField56, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(https_port_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel60)
                            .addComponent(jTextField57, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(https_port_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel61)
                            .addComponent(jTextField58, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(https_port_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel62)
                            .addComponent(jTextField59, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(https_port_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel63)
                            .addComponent(jTextField60, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(https_port_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel64)
                            .addComponent(jTextField61, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(261, 261, 261)
                        .addGroup(https_port_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton29)
                            .addComponent(jButton30)))
                    .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 449, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(resourceMap.getString("https_port_page.TabConstraints.tabTitle"), https_port_page); // NOI18N

        tcp_out_addr_page.setName("tcp_out_addr_page"); // NOI18N

        jLabel75.setText(resourceMap.getString("jLabel75.text")); // NOI18N
        jLabel75.setName("jLabel75"); // NOI18N

        jScrollPane5.setName("jScrollPane5"); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null}
            },
            new String [] {
                "Acl"
            }
        ));
        jTable1.setName("jTable1"); // NOI18N
        jScrollPane5.setViewportView(jTable1);
        jTable1.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("jTable1.columnModel.title0")); // NOI18N

        jButton31.setAction(actionMap.get("delTcpOutAddr")); // NOI18N
        jButton31.setName("jButton31"); // NOI18N

        jButton32.setAction(actionMap.get("saveTcpOutAddr")); // NOI18N
        jButton32.setName("jButton32"); // NOI18N

        javax.swing.GroupLayout tcp_out_addr_pageLayout = new javax.swing.GroupLayout(tcp_out_addr_page);
        tcp_out_addr_page.setLayout(tcp_out_addr_pageLayout);
        tcp_out_addr_pageLayout.setHorizontalGroup(
            tcp_out_addr_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tcp_out_addr_pageLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tcp_out_addr_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel75)
                    .addGroup(tcp_out_addr_pageLayout.createSequentialGroup()
                        .addComponent(jButton31)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton32)))
                .addContainerGap(819, Short.MAX_VALUE))
        );
        tcp_out_addr_pageLayout.setVerticalGroup(
            tcp_out_addr_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tcp_out_addr_pageLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel75)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addGroup(tcp_out_addr_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton31)
                    .addComponent(jButton32))
                .addContainerGap())
        );

        jTabbedPane1.addTab(resourceMap.getString("tcp_out_addr_page.TabConstraints.tabTitle"), tcp_out_addr_page); // NOI18N

        tcp_out_tos.setName("tcp_out_tos"); // NOI18N

        jScrollPane11.setName("jScrollPane11"); // NOI18N

        jTable5.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null}
            },
            new String [] {
                "Acl"
            }
        ));
        jTable5.setName("jTable5"); // NOI18N
        jScrollPane11.setViewportView(jTable5);
        jTable5.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("jTable1.columnModel.title0")); // NOI18N

        jLabel76.setText(resourceMap.getString("jLabel76.text")); // NOI18N
        jLabel76.setName("jLabel76"); // NOI18N

        jButton33.setAction(actionMap.get("delTcpOutTos")); // NOI18N
        jButton33.setName("jButton33"); // NOI18N

        jButton34.setAction(actionMap.get("saveTcpOutTos")); // NOI18N
        jButton34.setName("jButton34"); // NOI18N

        javax.swing.GroupLayout tcp_out_tosLayout = new javax.swing.GroupLayout(tcp_out_tos);
        tcp_out_tos.setLayout(tcp_out_tosLayout);
        tcp_out_tosLayout.setHorizontalGroup(
            tcp_out_tosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tcp_out_tosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tcp_out_tosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel76)
                    .addGroup(tcp_out_tosLayout.createSequentialGroup()
                        .addComponent(jButton33)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton34)))
                .addContainerGap(819, Short.MAX_VALUE))
        );
        tcp_out_tosLayout.setVerticalGroup(
            tcp_out_tosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tcp_out_tosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel76)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tcp_out_tosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton33)
                    .addComponent(jButton34))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(resourceMap.getString("tcp_out_tos.TabConstraints.tabTitle"), tcp_out_tos); // NOI18N

        sslproxy_flags_page.setToolTipText(resourceMap.getString("sslproxy_flags_page.toolTipText")); // NOI18N
        sslproxy_flags_page.setName("sslproxy_flags_page"); // NOI18N

        jCheckBox29.setText(resourceMap.getString("jCheckBox29.text")); // NOI18N
        jCheckBox29.setToolTipText(resourceMap.getString("jCheckBox29.toolTipText")); // NOI18N
        jCheckBox29.setName("jCheckBox29"); // NOI18N

        jCheckBox30.setText(resourceMap.getString("jCheckBox30.text")); // NOI18N
        jCheckBox30.setToolTipText(resourceMap.getString("jCheckBox30.toolTipText")); // NOI18N
        jCheckBox30.setName("jCheckBox30"); // NOI18N

        jButton35.setAction(actionMap.get("saveSslProxyFlags")); // NOI18N
        jButton35.setName("jButton35"); // NOI18N

        jCheckBox31.setText(resourceMap.getString("jCheckBox31.text")); // NOI18N
        jCheckBox31.setToolTipText(resourceMap.getString("jCheckBox31.toolTipText")); // NOI18N
        jCheckBox31.setName("jCheckBox31"); // NOI18N

        jCheckBox32.setText(resourceMap.getString("jCheckBox32.text")); // NOI18N
        jCheckBox32.setToolTipText(resourceMap.getString("jCheckBox32.toolTipText")); // NOI18N
        jCheckBox32.setName("jCheckBox32"); // NOI18N

        jCheckBox33.setText(resourceMap.getString("jCheckBox33.text")); // NOI18N
        jCheckBox33.setToolTipText(resourceMap.getString("jCheckBox33.toolTipText")); // NOI18N
        jCheckBox33.setName("jCheckBox33"); // NOI18N

        jButton36.setAction(actionMap.get("emptyOnceParam")); // NOI18N
        jButton36.setName("jButton36"); // NOI18N

        javax.swing.GroupLayout sslproxy_flags_pageLayout = new javax.swing.GroupLayout(sslproxy_flags_page);
        sslproxy_flags_page.setLayout(sslproxy_flags_pageLayout);
        sslproxy_flags_pageLayout.setHorizontalGroup(
            sslproxy_flags_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sslproxy_flags_pageLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sslproxy_flags_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBox29)
                    .addComponent(jCheckBox30)
                    .addGroup(sslproxy_flags_pageLayout.createSequentialGroup()
                        .addComponent(jButton35)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton36))
                    .addComponent(jCheckBox31)
                    .addComponent(jCheckBox32)
                    .addComponent(jCheckBox33))
                .addContainerGap(819, Short.MAX_VALUE))
        );
        sslproxy_flags_pageLayout.setVerticalGroup(
            sslproxy_flags_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sslproxy_flags_pageLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jCheckBox29)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBox30)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBox31)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBox32)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBox33)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 327, Short.MAX_VALUE)
                .addGroup(sslproxy_flags_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton35)
                    .addComponent(jButton36))
                .addContainerGap())
        );

        jTabbedPane1.addTab(resourceMap.getString("sslproxy_flags_page.TabConstraints.tabTitle"), sslproxy_flags_page); // NOI18N

        neighbor_page.setName("neighbor_page"); // NOI18N

        jLabel78.setText(resourceMap.getString("jLabel78.text")); // NOI18N
        jLabel78.setName("jLabel78"); // NOI18N

        jComboBox12.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "parent", "sibling" }));
        jComboBox12.setName("jComboBox12"); // NOI18N

        jTextField62.setText(resourceMap.getString("jTextField62.text")); // NOI18N
        jTextField62.setName("jTextField62"); // NOI18N

        jButton37.setAction(actionMap.get("saveNeighbor")); // NOI18N
        jButton37.setName("jButton37"); // NOI18N

        jButton38.setAction(actionMap.get("delNeighbor")); // NOI18N
        jButton38.setName("jButton38"); // NOI18N

        javax.swing.GroupLayout neighbor_pageLayout = new javax.swing.GroupLayout(neighbor_page);
        neighbor_page.setLayout(neighbor_pageLayout);
        neighbor_pageLayout.setHorizontalGroup(
            neighbor_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(neighbor_pageLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(neighbor_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(neighbor_pageLayout.createSequentialGroup()
                        .addComponent(jLabel78)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(neighbor_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBox12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField62, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(neighbor_pageLayout.createSequentialGroup()
                        .addComponent(jButton37)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton38)))
                .addContainerGap(725, Short.MAX_VALUE))
        );
        neighbor_pageLayout.setVerticalGroup(
            neighbor_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(neighbor_pageLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jComboBox12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(neighbor_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel78)
                    .addComponent(jTextField62, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 405, Short.MAX_VALUE)
                .addGroup(neighbor_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton37)
                    .addComponent(jButton38))
                .addContainerGap())
        );

        jTabbedPane1.addTab(resourceMap.getString("neighbor_page.TabConstraints.tabTitle"), neighbor_page); // NOI18N

        replacement_policy.setToolTipText(resourceMap.getString("replacement_policy.toolTipText")); // NOI18N
        replacement_policy.setName("replacement_policy"); // NOI18N

        jComboBox13.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "lru", "heap GDSF", "heap LFUDA", "heap LRU" }));
        jComboBox13.setToolTipText(resourceMap.getString("jComboBox13.toolTipText")); // NOI18N
        jComboBox13.setName("jComboBox13"); // NOI18N

        jButton39.setAction(actionMap.get("emptyOnceParam")); // NOI18N
        jButton39.setName("jButton39"); // NOI18N

        jButton40.setAction(actionMap.get("saveReplacmentPpolicy")); // NOI18N
        jButton40.setName("jButton40"); // NOI18N

        javax.swing.GroupLayout replacement_policyLayout = new javax.swing.GroupLayout(replacement_policy);
        replacement_policy.setLayout(replacement_policyLayout);
        replacement_policyLayout.setHorizontalGroup(
            replacement_policyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(replacement_policyLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(replacement_policyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox13, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(replacement_policyLayout.createSequentialGroup()
                        .addComponent(jButton39)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton40)))
                .addContainerGap(771, Short.MAX_VALUE))
        );
        replacement_policyLayout.setVerticalGroup(
            replacement_policyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(replacement_policyLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jComboBox13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 430, Short.MAX_VALUE)
                .addGroup(replacement_policyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton39)
                    .addComponent(jButton40))
                .addContainerGap())
        );

        jTabbedPane1.addTab(resourceMap.getString("replacement_policy.TabConstraints.tabTitle"), replacement_policy); // NOI18N

        cache_dir.setName("cache_dir"); // NOI18N

        jTabbedPane2.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        jTabbedPane2.setName("jTabbedPane2"); // NOI18N

        ufs.setName("ufs"); // NOI18N

        jLabel77.setText(resourceMap.getString("jLabel77.text")); // NOI18N
        jLabel77.setName("jLabel77"); // NOI18N

        jTextField73.setText(resourceMap.getString("jTextField73.text")); // NOI18N
        jTextField73.setName("jTextField73"); // NOI18N

        jLabel80.setText(resourceMap.getString("jLabel80.text")); // NOI18N
        jLabel80.setName("jLabel80"); // NOI18N

        jLabel81.setText(resourceMap.getString("jLabel81.text")); // NOI18N
        jLabel81.setName("jLabel81"); // NOI18N

        jTextField74.setText(resourceMap.getString("jTextField74.text")); // NOI18N
        jTextField74.setName("jTextField74"); // NOI18N

        jTextField75.setText(resourceMap.getString("jTextField75.text")); // NOI18N
        jTextField75.setName("jTextField75"); // NOI18N

        javax.swing.GroupLayout ufsLayout = new javax.swing.GroupLayout(ufs);
        ufs.setLayout(ufsLayout);
        ufsLayout.setHorizontalGroup(
            ufsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ufsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ufsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ufsLayout.createSequentialGroup()
                        .addComponent(jLabel81)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField75, javax.swing.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE))
                    .addGroup(ufsLayout.createSequentialGroup()
                        .addGroup(ufsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel77)
                            .addComponent(jLabel80))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(ufsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField74, javax.swing.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
                            .addComponent(jTextField73, javax.swing.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE))))
                .addContainerGap())
        );
        ufsLayout.setVerticalGroup(
            ufsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ufsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ufsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel77)
                    .addComponent(jTextField73, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ufsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel80)
                    .addComponent(jTextField74, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ufsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel81)
                    .addComponent(jTextField75, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(80, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab(resourceMap.getString("ufs.TabConstraints.tabTitle"), ufs); // NOI18N

        aufs.setName("aufs"); // NOI18N

        jLabel82.setText(resourceMap.getString("jLabel82.text")); // NOI18N
        jLabel82.setName("jLabel82"); // NOI18N

        jTextField76.setText(resourceMap.getString("jTextField76.text")); // NOI18N
        jTextField76.setName("jTextField76"); // NOI18N

        jLabel83.setText(resourceMap.getString("jLabel83.text")); // NOI18N
        jLabel83.setName("jLabel83"); // NOI18N

        jLabel84.setText(resourceMap.getString("jLabel84.text")); // NOI18N
        jLabel84.setName("jLabel84"); // NOI18N

        jTextField77.setText(resourceMap.getString("jTextField77.text")); // NOI18N
        jTextField77.setName("jTextField77"); // NOI18N

        jTextField78.setText(resourceMap.getString("jTextField78.text")); // NOI18N
        jTextField78.setName("jTextField78"); // NOI18N

        javax.swing.GroupLayout aufsLayout = new javax.swing.GroupLayout(aufs);
        aufs.setLayout(aufsLayout);
        aufsLayout.setHorizontalGroup(
            aufsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(aufsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(aufsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(aufsLayout.createSequentialGroup()
                        .addComponent(jLabel82)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField76, javax.swing.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE))
                    .addGroup(aufsLayout.createSequentialGroup()
                        .addGroup(aufsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel83)
                            .addComponent(jLabel84))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(aufsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField77, javax.swing.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
                            .addComponent(jTextField78, javax.swing.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE))))
                .addContainerGap())
        );
        aufsLayout.setVerticalGroup(
            aufsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(aufsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(aufsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel83)
                    .addComponent(jTextField78, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(aufsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel84)
                    .addComponent(jTextField77, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(aufsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel82)
                    .addComponent(jTextField76, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(80, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab(resourceMap.getString("aufs.TabConstraints.tabTitle"), aufs); // NOI18N

        diskd.setName("diskd"); // NOI18N

        jLabel85.setText(resourceMap.getString("jLabel85.text")); // NOI18N
        jLabel85.setName("jLabel85"); // NOI18N

        jTextField79.setText(resourceMap.getString("jTextField79.text")); // NOI18N
        jTextField79.setName("jTextField79"); // NOI18N

        jLabel86.setText(resourceMap.getString("jLabel86.text")); // NOI18N
        jLabel86.setName("jLabel86"); // NOI18N

        jLabel87.setText(resourceMap.getString("jLabel87.text")); // NOI18N
        jLabel87.setName("jLabel87"); // NOI18N

        jTextField80.setText(resourceMap.getString("jTextField80.text")); // NOI18N
        jTextField80.setName("jTextField80"); // NOI18N

        jTextField81.setText(resourceMap.getString("jTextField81.text")); // NOI18N
        jTextField81.setName("jTextField81"); // NOI18N

        jLabel88.setText(resourceMap.getString("jLabel88.text")); // NOI18N
        jLabel88.setName("jLabel88"); // NOI18N

        jTextField82.setText(resourceMap.getString("jTextField82.text")); // NOI18N
        jTextField82.setName("jTextField82"); // NOI18N

        jLabel89.setText(resourceMap.getString("jLabel89.text")); // NOI18N
        jLabel89.setName("jLabel89"); // NOI18N

        jTextField83.setText(resourceMap.getString("jTextField83.text")); // NOI18N
        jTextField83.setName("jTextField83"); // NOI18N

        javax.swing.GroupLayout diskdLayout = new javax.swing.GroupLayout(diskd);
        diskd.setLayout(diskdLayout);
        diskdLayout.setHorizontalGroup(
            diskdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(diskdLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(diskdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(diskdLayout.createSequentialGroup()
                        .addComponent(jLabel85)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField79, javax.swing.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE))
                    .addGroup(diskdLayout.createSequentialGroup()
                        .addGroup(diskdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel86)
                            .addComponent(jLabel87)
                            .addComponent(jLabel89)
                            .addComponent(jLabel88))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(diskdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField81, javax.swing.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
                            .addComponent(jTextField80, javax.swing.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
                            .addComponent(jTextField83, javax.swing.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
                            .addComponent(jTextField82, javax.swing.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE))))
                .addContainerGap())
        );
        diskdLayout.setVerticalGroup(
            diskdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(diskdLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(diskdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel86)
                    .addComponent(jTextField81, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(diskdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel87)
                    .addComponent(jTextField80, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(diskdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel85)
                    .addComponent(jTextField79, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(diskdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel89)
                    .addComponent(jTextField83, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(diskdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel88)
                    .addComponent(jTextField82, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(30, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab(resourceMap.getString("diskd.TabConstraints.tabTitle"), diskd); // NOI18N

        coss.setName("coss"); // NOI18N

        jLabel90.setText(resourceMap.getString("jLabel90.text")); // NOI18N
        jLabel90.setName("jLabel90"); // NOI18N

        jLabel91.setText(resourceMap.getString("jLabel91.text")); // NOI18N
        jLabel91.setName("jLabel91"); // NOI18N

        jLabel92.setText(resourceMap.getString("jLabel92.text")); // NOI18N
        jLabel92.setName("jLabel92"); // NOI18N

        jLabel93.setText(resourceMap.getString("jLabel93.text")); // NOI18N
        jLabel93.setName("jLabel93"); // NOI18N

        jLabel94.setText(resourceMap.getString("jLabel94.text")); // NOI18N
        jLabel94.setName("jLabel94"); // NOI18N

        jTextField84.setText(resourceMap.getString("jTextField84.text")); // NOI18N
        jTextField84.setName("jTextField84"); // NOI18N

        jSlider2.setName("jSlider2"); // NOI18N
        jSlider2.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider2StateChanged(evt);
            }
        });

        jTextField85.setText(resourceMap.getString("jTextField85.text")); // NOI18N
        jTextField85.setName("jTextField85"); // NOI18N

        jTextField86.setText(resourceMap.getString("jTextField86.text")); // NOI18N
        jTextField86.setName("jTextField86"); // NOI18N

        jTextField87.setText(resourceMap.getString("jTextField87.text")); // NOI18N
        jTextField87.setName("jTextField87"); // NOI18N

        javax.swing.GroupLayout cossLayout = new javax.swing.GroupLayout(coss);
        coss.setLayout(cossLayout);
        cossLayout.setHorizontalGroup(
            cossLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cossLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(cossLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(cossLayout.createSequentialGroup()
                        .addComponent(jLabel90)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 64, Short.MAX_VALUE)
                        .addComponent(jTextField84, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(cossLayout.createSequentialGroup()
                        .addComponent(jLabel91)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSlider2, javax.swing.GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE))
                    .addGroup(cossLayout.createSequentialGroup()
                        .addGroup(cossLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel92)
                            .addComponent(jLabel93)
                            .addComponent(jLabel94))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(cossLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField87, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
                            .addComponent(jTextField86, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
                            .addComponent(jTextField85, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGap(12, 12, 12))
        );
        cossLayout.setVerticalGroup(
            cossLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cossLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(cossLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel90)
                    .addComponent(jTextField84, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(cossLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(cossLayout.createSequentialGroup()
                        .addComponent(jLabel91)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(cossLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel92)
                            .addComponent(jTextField85, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(cossLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel93)
                            .addComponent(jTextField86, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jSlider2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(cossLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel94)
                    .addComponent(jTextField87, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(34, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab(resourceMap.getString("coss.TabConstraints.tabTitle"), coss); // NOI18N

        jLabel79.setText(resourceMap.getString("jLabel79.text")); // NOI18N
        jLabel79.setName("jLabel79"); // NOI18N

        jCheckBox34.setText(resourceMap.getString("jCheckBox34.text")); // NOI18N
        jCheckBox34.setName("jCheckBox34"); // NOI18N

        jTextField72.setText(resourceMap.getString("jTextField72.text")); // NOI18N
        jTextField72.setName("jTextField72"); // NOI18N

        jLabel95.setText(resourceMap.getString("jLabel95.text")); // NOI18N
        jLabel95.setName("jLabel95"); // NOI18N

        jTextField88.setText(resourceMap.getString("jTextField88.text")); // NOI18N
        jTextField88.setName("jTextField88"); // NOI18N

        jButton41.setAction(actionMap.get("emptyOnceParam")); // NOI18N
        jButton41.setName("jButton41"); // NOI18N

        jButton42.setAction(actionMap.get("saveCacheDir")); // NOI18N
        jButton42.setName("jButton42"); // NOI18N

        javax.swing.GroupLayout cache_dirLayout = new javax.swing.GroupLayout(cache_dir);
        cache_dir.setLayout(cache_dirLayout);
        cache_dirLayout.setHorizontalGroup(
            cache_dirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cache_dirLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(cache_dirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(cache_dirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 424, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, cache_dirLayout.createSequentialGroup()
                            .addGroup(cache_dirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, cache_dirLayout.createSequentialGroup()
                                    .addComponent(jLabel79)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jTextField88, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE))
                                .addGroup(cache_dirLayout.createSequentialGroup()
                                    .addComponent(jLabel95)
                                    .addGap(14, 14, 14)
                                    .addComponent(jTextField72)))
                            .addGap(18, 18, 18)
                            .addComponent(jCheckBox34)))
                    .addGroup(cache_dirLayout.createSequentialGroup()
                        .addComponent(jButton41)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton42)))
                .addContainerGap(600, Short.MAX_VALUE))
        );
        cache_dirLayout.setVerticalGroup(
            cache_dirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cache_dirLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(cache_dirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBox34)
                    .addComponent(jTextField72, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel95))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(cache_dirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField88, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel79))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 230, Short.MAX_VALUE)
                .addGroup(cache_dirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton41)
                    .addComponent(jButton42))
                .addContainerGap())
        );

        jTabbedPane1.addTab(resourceMap.getString("cache_dir.TabConstraints.tabTitle"), cache_dir); // NOI18N

        store_alg_page.setToolTipText(resourceMap.getString("store_alg_page.toolTipText")); // NOI18N
        store_alg_page.setName("store_alg_page"); // NOI18N

        jComboBox14.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "least-load", "round-robin" }));
        jComboBox14.setName("jComboBox14"); // NOI18N

        jButton43.setAction(actionMap.get("emptyOnceParam")); // NOI18N
        jButton43.setName("jButton43"); // NOI18N

        jButton44.setAction(actionMap.get("saveStoreDirAlg")); // NOI18N
        jButton44.setName("jButton44"); // NOI18N

        jSeparator32.setName("jSeparator32"); // NOI18N

        jButton76.setAction(actionMap.get("showHelp1")); // NOI18N
        jButton76.setName("jButton76"); // NOI18N

        javax.swing.GroupLayout store_alg_pageLayout = new javax.swing.GroupLayout(store_alg_page);
        store_alg_page.setLayout(store_alg_pageLayout);
        store_alg_pageLayout.setHorizontalGroup(
            store_alg_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(store_alg_pageLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(store_alg_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator32, javax.swing.GroupLayout.DEFAULT_SIZE, 1014, Short.MAX_VALUE)
                    .addComponent(jComboBox14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(store_alg_pageLayout.createSequentialGroup()
                        .addComponent(jButton43)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton44)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton76)))
                .addContainerGap())
        );
        store_alg_pageLayout.setVerticalGroup(
            store_alg_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(store_alg_pageLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jComboBox14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator32, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(store_alg_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(store_alg_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton44)
                        .addComponent(jButton76))
                    .addComponent(jButton43))
                .addContainerGap(414, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(resourceMap.getString("store_alg_page.TabConstraints.tabTitle"), store_alg_page); // NOI18N

        logformat_page.setToolTipText(resourceMap.getString("logformat_page.toolTipText")); // NOI18N
        logformat_page.setName("logformat_page"); // NOI18N

        jLabel96.setText(resourceMap.getString("jLabel96.text")); // NOI18N
        jLabel96.setName("jLabel96"); // NOI18N

        jTextField89.setText(resourceMap.getString("jTextField89.text")); // NOI18N
        jTextField89.setName("jTextField89"); // NOI18N

        jButton45.setAction(actionMap.get("delLogFormat")); // NOI18N
        jButton45.setName("jButton45"); // NOI18N

        jButton46.setAction(actionMap.get("saveLogFormat")); // NOI18N
        jButton46.setName("jButton46"); // NOI18N

        javax.swing.GroupLayout logformat_pageLayout = new javax.swing.GroupLayout(logformat_page);
        logformat_page.setLayout(logformat_pageLayout);
        logformat_pageLayout.setHorizontalGroup(
            logformat_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(logformat_pageLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(logformat_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(logformat_pageLayout.createSequentialGroup()
                        .addComponent(jLabel96)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField89, javax.swing.GroupLayout.DEFAULT_SIZE, 969, Short.MAX_VALUE))
                    .addGroup(logformat_pageLayout.createSequentialGroup()
                        .addComponent(jButton45)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton46)))
                .addContainerGap())
        );
        logformat_pageLayout.setVerticalGroup(
            logformat_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(logformat_pageLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(logformat_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel96)
                    .addComponent(jTextField89, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 435, Short.MAX_VALUE)
                .addGroup(logformat_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton45)
                    .addComponent(jButton46))
                .addContainerGap())
        );

        jTabbedPane1.addTab(resourceMap.getString("logformat_page.TabConstraints.tabTitle"), logformat_page); // NOI18N

        access_log_page.setName("access_log_page"); // NOI18N

        jLabel98.setText(resourceMap.getString("jLabel98.text")); // NOI18N
        jLabel98.setName("jLabel98"); // NOI18N

        jComboBox15.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox15.setName("jComboBox15"); // NOI18N

        jLabel99.setText(resourceMap.getString("jLabel99.text")); // NOI18N
        jLabel99.setName("jLabel99"); // NOI18N

        jComboBox16.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox16.setName("jComboBox16"); // NOI18N

        jComboBox17.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox17.setName("jComboBox17"); // NOI18N

        jComboBox18.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox18.setName("jComboBox18"); // NOI18N

        jComboBox19.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox19.setName("jComboBox19"); // NOI18N

        jButton47.setAction(actionMap.get("delAccLog")); // NOI18N
        jButton47.setName("jButton47"); // NOI18N

        jButton48.setAction(actionMap.get("saveAccLog")); // NOI18N
        jButton48.setName("jButton48"); // NOI18N

        jButton78.setAction(actionMap.get("tailLogFile")); // NOI18N
        jButton78.setName("jButton78"); // NOI18N

        javax.swing.GroupLayout access_log_pageLayout = new javax.swing.GroupLayout(access_log_page);
        access_log_page.setLayout(access_log_pageLayout);
        access_log_pageLayout.setHorizontalGroup(
            access_log_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(access_log_pageLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(access_log_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(access_log_pageLayout.createSequentialGroup()
                        .addComponent(jButton47)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton48)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton78))
                    .addGroup(access_log_pageLayout.createSequentialGroup()
                        .addComponent(jLabel98)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox15, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(access_log_pageLayout.createSequentialGroup()
                        .addGap(74, 74, 74)
                        .addGroup(access_log_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jComboBox17, javax.swing.GroupLayout.Alignment.LEADING, 0, 276, Short.MAX_VALUE)
                            .addComponent(jComboBox19, javax.swing.GroupLayout.Alignment.LEADING, 0, 276, Short.MAX_VALUE)
                            .addComponent(jComboBox18, javax.swing.GroupLayout.Alignment.LEADING, 0, 276, Short.MAX_VALUE)
                            .addComponent(jComboBox16, javax.swing.GroupLayout.Alignment.LEADING, 0, 276, Short.MAX_VALUE)))
                    .addComponent(jLabel99, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(717, 717, 717))
        );
        access_log_pageLayout.setVerticalGroup(
            access_log_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(access_log_pageLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(access_log_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel98)
                    .addComponent(jComboBox15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(access_log_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel99)
                    .addGroup(access_log_pageLayout.createSequentialGroup()
                        .addComponent(jComboBox16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(179, 179, 179)
                .addGroup(access_log_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton47)
                    .addComponent(jButton48)
                    .addComponent(jButton78))
                .addContainerGap(143, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(resourceMap.getString("access_log_page.TabConstraints.tabTitle"), access_log_page); // NOI18N

        refresh_pattern_page.setName("refresh_pattern_page"); // NOI18N

        jCheckBox35.setText(resourceMap.getString("jCheckBox35.text")); // NOI18N
        jCheckBox35.setName("jCheckBox35"); // NOI18N

        jLabel97.setText(resourceMap.getString("jLabel97.text")); // NOI18N
        jLabel97.setName("jLabel97"); // NOI18N

        jTextField90.setText(resourceMap.getString("jTextField90.text")); // NOI18N
        jTextField90.setName("jTextField90"); // NOI18N

        jLabel100.setText(resourceMap.getString("jLabel100.text")); // NOI18N
        jLabel100.setName("jLabel100"); // NOI18N

        jSlider3.setName("jSlider3"); // NOI18N
        jSlider3.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider3StateChanged(evt);
            }
        });

        jLabel101.setText(resourceMap.getString("jLabel101.text")); // NOI18N
        jLabel101.setName("jLabel101"); // NOI18N

        jTextField91.setText(resourceMap.getString("jTextField91.text")); // NOI18N
        jTextField91.setName("jTextField91"); // NOI18N

        jCheckBox36.setText(resourceMap.getString("jCheckBox36.text")); // NOI18N
        jCheckBox36.setName("jCheckBox36"); // NOI18N

        jCheckBox37.setText(resourceMap.getString("jCheckBox37.text")); // NOI18N
        jCheckBox37.setName("jCheckBox37"); // NOI18N

        jCheckBox38.setText(resourceMap.getString("jCheckBox38.text")); // NOI18N
        jCheckBox38.setName("jCheckBox38"); // NOI18N

        jCheckBox39.setText(resourceMap.getString("jCheckBox39.text")); // NOI18N
        jCheckBox39.setName("jCheckBox39"); // NOI18N

        jCheckBox40.setText(resourceMap.getString("jCheckBox40.text")); // NOI18N
        jCheckBox40.setName("jCheckBox40"); // NOI18N

        jCheckBox41.setText(resourceMap.getString("jCheckBox41.text")); // NOI18N
        jCheckBox41.setName("jCheckBox41"); // NOI18N

        jCheckBox42.setText(resourceMap.getString("jCheckBox42.text")); // NOI18N
        jCheckBox42.setName("jCheckBox42"); // NOI18N

        jButton49.setAction(actionMap.get("delRefreshPattern")); // NOI18N
        jButton49.setName("jButton49"); // NOI18N

        jButton50.setAction(actionMap.get("saveRefreshPattern")); // NOI18N
        jButton50.setName("jButton50"); // NOI18N

        javax.swing.GroupLayout refresh_pattern_pageLayout = new javax.swing.GroupLayout(refresh_pattern_page);
        refresh_pattern_page.setLayout(refresh_pattern_pageLayout);
        refresh_pattern_pageLayout.setHorizontalGroup(
            refresh_pattern_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(refresh_pattern_pageLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(refresh_pattern_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(refresh_pattern_pageLayout.createSequentialGroup()
                        .addGroup(refresh_pattern_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel97)
                            .addComponent(jLabel101))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                        .addGroup(refresh_pattern_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField91)
                            .addComponent(jTextField90, javax.swing.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)))
                    .addGroup(refresh_pattern_pageLayout.createSequentialGroup()
                        .addComponent(jLabel100)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSlider3, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jCheckBox36)
                    .addComponent(jCheckBox37)
                    .addComponent(jCheckBox38)
                    .addComponent(jCheckBox39)
                    .addComponent(jCheckBox40)
                    .addComponent(jCheckBox41)
                    .addComponent(jCheckBox42)
                    .addGroup(refresh_pattern_pageLayout.createSequentialGroup()
                        .addComponent(jButton49)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton50))
                    .addComponent(jCheckBox35, javax.swing.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE))
                .addGap(726, 726, 726))
        );
        refresh_pattern_pageLayout.setVerticalGroup(
            refresh_pattern_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(refresh_pattern_pageLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jCheckBox35)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(refresh_pattern_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel97)
                    .addComponent(jTextField90, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(refresh_pattern_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel101)
                    .addComponent(jTextField91, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(34, 34, 34)
                .addGroup(refresh_pattern_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel100)
                    .addComponent(jSlider3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addComponent(jCheckBox36)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBox39)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBox38)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBox37)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBox40)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBox41)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBox42)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 146, Short.MAX_VALUE)
                .addGroup(refresh_pattern_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton49)
                    .addComponent(jButton50))
                .addContainerGap())
        );

        jTabbedPane1.addTab(resourceMap.getString("refresh_pattern_page.TabConstraints.tabTitle"), refresh_pattern_page); // NOI18N

        header_replace_page.setName("header_replace_page"); // NOI18N

        jLabel102.setText(resourceMap.getString("jLabel102.text")); // NOI18N
        jLabel102.setName("jLabel102"); // NOI18N

        jTextField92.setText(resourceMap.getString("jTextField92.text")); // NOI18N
        jTextField92.setName("jTextField92"); // NOI18N

        jButton51.setAction(actionMap.get("delHeaderReplace")); // NOI18N
        jButton51.setName("jButton51"); // NOI18N

        jButton52.setAction(actionMap.get("saveHeaderReplace")); // NOI18N
        jButton52.setName("jButton52"); // NOI18N

        javax.swing.GroupLayout header_replace_pageLayout = new javax.swing.GroupLayout(header_replace_page);
        header_replace_page.setLayout(header_replace_pageLayout);
        header_replace_pageLayout.setHorizontalGroup(
            header_replace_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(header_replace_pageLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(header_replace_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(header_replace_pageLayout.createSequentialGroup()
                        .addComponent(jLabel102)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField92, javax.swing.GroupLayout.DEFAULT_SIZE, 969, Short.MAX_VALUE))
                    .addGroup(header_replace_pageLayout.createSequentialGroup()
                        .addComponent(jButton51)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton52)))
                .addContainerGap())
        );
        header_replace_pageLayout.setVerticalGroup(
            header_replace_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(header_replace_pageLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(header_replace_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel102)
                    .addComponent(jTextField92, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 435, Short.MAX_VALUE)
                .addGroup(header_replace_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton51)
                    .addComponent(jButton52))
                .addContainerGap())
        );

        jTabbedPane1.addTab(resourceMap.getString("header_replace_page.TabConstraints.tabTitle"), header_replace_page); // NOI18N

        wccp2_service_page.setName("wccp2_service_page"); // NOI18N

        jLabel103.setText(resourceMap.getString("jLabel103.text")); // NOI18N
        jLabel103.setName("jLabel103"); // NOI18N

        jComboBox20.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "standart", "dynamic" }));
        jComboBox20.setEnabled(false);
        jComboBox20.setName("jComboBox20"); // NOI18N

        jLabel104.setText(resourceMap.getString("jLabel104.text")); // NOI18N
        jLabel104.setName("jLabel104"); // NOI18N

        jTextField93.setEditable(false);
        jTextField93.setText(resourceMap.getString("jTextField93.text")); // NOI18N
        jTextField93.setEnabled(false);
        jTextField93.setName("jTextField93"); // NOI18N

        jLabel105.setText(resourceMap.getString("jLabel105.text")); // NOI18N
        jLabel105.setName("jLabel105"); // NOI18N

        jTextField94.setText(resourceMap.getString("jTextField94.text")); // NOI18N
        jTextField94.setName("jTextField94"); // NOI18N

        jSeparator12.setName("jSeparator12"); // NOI18N

        jButton53.setAction(actionMap.get("deleteDefaultObject")); // NOI18N
        jButton53.setName("jButton53"); // NOI18N

        jButton54.setAction(actionMap.get("saveDefaultObject")); // NOI18N
        jButton54.setName("jButton54"); // NOI18N

        javax.swing.GroupLayout wccp2_service_pageLayout = new javax.swing.GroupLayout(wccp2_service_page);
        wccp2_service_page.setLayout(wccp2_service_pageLayout);
        wccp2_service_pageLayout.setHorizontalGroup(
            wccp2_service_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(wccp2_service_pageLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(wccp2_service_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(wccp2_service_pageLayout.createSequentialGroup()
                        .addGroup(wccp2_service_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel103, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel104)
                            .addComponent(jLabel105))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(wccp2_service_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField94)
                            .addComponent(jTextField93)
                            .addComponent(jComboBox20, 0, 150, Short.MAX_VALUE)))
                    .addComponent(jSeparator12, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1014, Short.MAX_VALUE)
                    .addGroup(wccp2_service_pageLayout.createSequentialGroup()
                        .addComponent(jButton53)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton54)))
                .addContainerGap())
        );
        wccp2_service_pageLayout.setVerticalGroup(
            wccp2_service_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(wccp2_service_pageLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(wccp2_service_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel103)
                    .addComponent(jComboBox20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(wccp2_service_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel104)
                    .addComponent(jTextField93, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(wccp2_service_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel105)
                    .addComponent(jTextField94, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator12, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(wccp2_service_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton53)
                    .addComponent(jButton54))
                .addContainerGap(370, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(resourceMap.getString("wccp2_service_page.TabConstraints.tabTitle"), wccp2_service_page); // NOI18N

        wccp2_service_info_page.setName("wccp2_service_info_page"); // NOI18N

        jLabel106.setText(resourceMap.getString("jLabel106.text")); // NOI18N
        jLabel106.setName("jLabel106"); // NOI18N

        jComboBox21.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "tcp", "udp" }));
        jComboBox21.setName("jComboBox21"); // NOI18N

        jLabel107.setText(resourceMap.getString("jLabel107.text")); // NOI18N
        jLabel107.setName("jLabel107"); // NOI18N

        jCheckBox43.setText(resourceMap.getString("jCheckBox43.text")); // NOI18N
        jCheckBox43.setName("jCheckBox43"); // NOI18N

        jCheckBox44.setText(resourceMap.getString("jCheckBox44.text")); // NOI18N
        jCheckBox44.setName("jCheckBox44"); // NOI18N

        jCheckBox45.setText(resourceMap.getString("jCheckBox45.text")); // NOI18N
        jCheckBox45.setName("jCheckBox45"); // NOI18N

        jCheckBox46.setText(resourceMap.getString("jCheckBox46.text")); // NOI18N
        jCheckBox46.setName("jCheckBox46"); // NOI18N

        jCheckBox47.setText(resourceMap.getString("jCheckBox47.text")); // NOI18N
        jCheckBox47.setName("jCheckBox47"); // NOI18N

        jCheckBox48.setText(resourceMap.getString("jCheckBox48.text")); // NOI18N
        jCheckBox48.setName("jCheckBox48"); // NOI18N

        jCheckBox49.setText(resourceMap.getString("jCheckBox49.text")); // NOI18N
        jCheckBox49.setName("jCheckBox49"); // NOI18N

        jCheckBox50.setText(resourceMap.getString("jCheckBox50.text")); // NOI18N
        jCheckBox50.setName("jCheckBox50"); // NOI18N

        jCheckBox51.setText(resourceMap.getString("jCheckBox51.text")); // NOI18N
        jCheckBox51.setName("jCheckBox51"); // NOI18N

        jTextField95.setText(resourceMap.getString("jTextField95.text")); // NOI18N
        jTextField95.setName("jTextField95"); // NOI18N

        jLabel108.setText(resourceMap.getString("jLabel108.text")); // NOI18N
        jLabel108.setName("jLabel108"); // NOI18N

        jLabel109.setText(resourceMap.getString("jLabel109.text")); // NOI18N
        jLabel109.setName("jLabel109"); // NOI18N

        jTextField96.setText(resourceMap.getString("jTextField96.text")); // NOI18N
        jTextField96.setName("jTextField96"); // NOI18N

        jSeparator14.setName("jSeparator14"); // NOI18N

        jSeparator15.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator15.setName("jSeparator15"); // NOI18N

        jButton55.setAction(actionMap.get("deleteDefaultObject")); // NOI18N
        jButton55.setName("jButton55"); // NOI18N

        jButton56.setAction(actionMap.get("saveDefaultObject")); // NOI18N
        jButton56.setName("jButton56"); // NOI18N

        javax.swing.GroupLayout wccp2_service_info_pageLayout = new javax.swing.GroupLayout(wccp2_service_info_page);
        wccp2_service_info_page.setLayout(wccp2_service_info_pageLayout);
        wccp2_service_info_pageLayout.setHorizontalGroup(
            wccp2_service_info_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(wccp2_service_info_pageLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(wccp2_service_info_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(wccp2_service_info_pageLayout.createSequentialGroup()
                        .addGroup(wccp2_service_info_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel106)
                            .addComponent(jLabel107))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(wccp2_service_info_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jCheckBox43)
                            .addComponent(jCheckBox44)
                            .addComponent(jCheckBox45)
                            .addComponent(jCheckBox46)
                            .addComponent(jCheckBox47)
                            .addComponent(jCheckBox48)
                            .addComponent(jCheckBox49)
                            .addComponent(jCheckBox50, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jCheckBox51)
                            .addComponent(jComboBox21, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jSeparator15, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(wccp2_service_info_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(wccp2_service_info_pageLayout.createSequentialGroup()
                                .addComponent(jLabel108, javax.swing.GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(wccp2_service_info_pageLayout.createSequentialGroup()
                                .addComponent(jLabel109)
                                .addGap(34, 34, 34)))
                        .addGroup(wccp2_service_info_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField96)
                            .addComponent(jTextField95, javax.swing.GroupLayout.DEFAULT_SIZE, 336, Short.MAX_VALUE)))
                    .addGroup(wccp2_service_info_pageLayout.createSequentialGroup()
                        .addComponent(jButton55)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton56))
                    .addComponent(jSeparator14, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1014, Short.MAX_VALUE))
                .addContainerGap())
        );
        wccp2_service_info_pageLayout.setVerticalGroup(
            wccp2_service_info_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(wccp2_service_info_pageLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(wccp2_service_info_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(wccp2_service_info_pageLayout.createSequentialGroup()
                        .addGroup(wccp2_service_info_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel106)
                            .addComponent(jComboBox21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(wccp2_service_info_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel107)
                            .addGroup(wccp2_service_info_pageLayout.createSequentialGroup()
                                .addComponent(jCheckBox43)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCheckBox44)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCheckBox45)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCheckBox46)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCheckBox47)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCheckBox48)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCheckBox49)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCheckBox50)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCheckBox51))))
                    .addComponent(jSeparator15, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(wccp2_service_info_pageLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jLabel109))
                    .addGroup(wccp2_service_info_pageLayout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addGroup(wccp2_service_info_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel108)
                            .addComponent(jTextField95, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField96, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator14, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addGroup(wccp2_service_info_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton55)
                    .addComponent(jButton56))
                .addContainerGap(211, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(resourceMap.getString("wccp2_service_info_page.TabConstraints.tabTitle"), wccp2_service_info_page); // NOI18N

        deny_info_page.setName("deny_info_page"); // NOI18N

        jLabel110.setText(resourceMap.getString("jLabel110.text")); // NOI18N
        jLabel110.setName("jLabel110"); // NOI18N

        jTextField97.setText(resourceMap.getString("jTextField97.text")); // NOI18N
        jTextField97.setEnabled(false);
        jTextField97.setName("jTextField97"); // NOI18N

        jLabel111.setText(resourceMap.getString("jLabel111.text")); // NOI18N
        jLabel111.setName("jLabel111"); // NOI18N

        jComboBox22.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox22.setName("jComboBox22"); // NOI18N

        jSeparator17.setName("jSeparator17"); // NOI18N

        jButton57.setAction(actionMap.get("deleteDefaultObject")); // NOI18N
        jButton57.setName("jButton57"); // NOI18N

        jButton58.setAction(actionMap.get("saveDefaultObject")); // NOI18N
        jButton58.setName("jButton58"); // NOI18N

        javax.swing.GroupLayout deny_info_pageLayout = new javax.swing.GroupLayout(deny_info_page);
        deny_info_page.setLayout(deny_info_pageLayout);
        deny_info_pageLayout.setHorizontalGroup(
            deny_info_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(deny_info_pageLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(deny_info_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator17, javax.swing.GroupLayout.DEFAULT_SIZE, 1014, Short.MAX_VALUE)
                    .addGroup(deny_info_pageLayout.createSequentialGroup()
                        .addGroup(deny_info_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel110)
                            .addComponent(jLabel111))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(deny_info_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jComboBox22, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jTextField97, javax.swing.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)))
                    .addGroup(deny_info_pageLayout.createSequentialGroup()
                        .addComponent(jButton57)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton58)))
                .addContainerGap())
        );
        deny_info_pageLayout.setVerticalGroup(
            deny_info_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(deny_info_pageLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(deny_info_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel110)
                    .addComponent(jTextField97, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(deny_info_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel111)
                    .addComponent(jComboBox22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator17, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(deny_info_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton57)
                    .addComponent(jButton58))
                .addContainerGap(389, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(resourceMap.getString("deny_info_page.TabConstraints.tabTitle"), deny_info_page); // NOI18N

        cachemgr_passwd_page.setName("cachemgr_passwd_page"); // NOI18N

        jCheckBox52.setText(resourceMap.getString("jCheckBox52.text")); // NOI18N
        jCheckBox52.setName("jCheckBox52"); // NOI18N

        jCheckBox53.setText(resourceMap.getString("jCheckBox53.text")); // NOI18N
        jCheckBox53.setName("jCheckBox53"); // NOI18N

        jCheckBox54.setText(resourceMap.getString("jCheckBox54.text")); // NOI18N
        jCheckBox54.setName("jCheckBox54"); // NOI18N

        jCheckBox55.setText(resourceMap.getString("jCheckBox55.text")); // NOI18N
        jCheckBox55.setName("jCheckBox55"); // NOI18N

        jCheckBox56.setText(resourceMap.getString("jCheckBox56.text")); // NOI18N
        jCheckBox56.setName("jCheckBox56"); // NOI18N

        jCheckBox57.setText(resourceMap.getString("jCheckBox57.text")); // NOI18N
        jCheckBox57.setName("jCheckBox57"); // NOI18N

        jCheckBox58.setText(resourceMap.getString("jCheckBox58.text")); // NOI18N
        jCheckBox58.setName("jCheckBox58"); // NOI18N

        jCheckBox59.setText(resourceMap.getString("jCheckBox59.text")); // NOI18N
        jCheckBox59.setName("jCheckBox59"); // NOI18N

        jCheckBox60.setText(resourceMap.getString("jCheckBox60.text")); // NOI18N
        jCheckBox60.setName("jCheckBox60"); // NOI18N

        jCheckBox61.setText(resourceMap.getString("jCheckBox61.text")); // NOI18N
        jCheckBox61.setName("jCheckBox61"); // NOI18N

        jCheckBox62.setText(resourceMap.getString("jCheckBox62.text")); // NOI18N
        jCheckBox62.setName("jCheckBox62"); // NOI18N

        jCheckBox63.setText(resourceMap.getString("jCheckBox63.text")); // NOI18N
        jCheckBox63.setName("jCheckBox63"); // NOI18N

        jCheckBox64.setText(resourceMap.getString("jCheckBox64.text")); // NOI18N
        jCheckBox64.setName("jCheckBox64"); // NOI18N

        jCheckBox65.setText(resourceMap.getString("jCheckBox65.text")); // NOI18N
        jCheckBox65.setName("jCheckBox65"); // NOI18N

        jCheckBox66.setText(resourceMap.getString("jCheckBox66.text")); // NOI18N
        jCheckBox66.setName("jCheckBox66"); // NOI18N

        jCheckBox67.setText(resourceMap.getString("jCheckBox67.text")); // NOI18N
        jCheckBox67.setName("jCheckBox67"); // NOI18N

        jCheckBox68.setText(resourceMap.getString("jCheckBox68.text")); // NOI18N
        jCheckBox68.setName("jCheckBox68"); // NOI18N

        jSeparator18.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator18.setName("jSeparator18"); // NOI18N

        jCheckBox69.setText(resourceMap.getString("jCheckBox69.text")); // NOI18N
        jCheckBox69.setName("jCheckBox69"); // NOI18N

        jCheckBox70.setText(resourceMap.getString("jCheckBox70.text")); // NOI18N
        jCheckBox70.setName("jCheckBox70"); // NOI18N

        jCheckBox71.setText(resourceMap.getString("jCheckBox71.text")); // NOI18N
        jCheckBox71.setName("jCheckBox71"); // NOI18N

        jCheckBox72.setText(resourceMap.getString("jCheckBox72.text")); // NOI18N
        jCheckBox72.setName("jCheckBox72"); // NOI18N

        jCheckBox73.setText(resourceMap.getString("jCheckBox73.text")); // NOI18N
        jCheckBox73.setName("jCheckBox73"); // NOI18N

        jCheckBox74.setText(resourceMap.getString("jCheckBox74.text")); // NOI18N
        jCheckBox74.setName("jCheckBox74"); // NOI18N

        jCheckBox75.setText(resourceMap.getString("jCheckBox75.text")); // NOI18N
        jCheckBox75.setName("jCheckBox75"); // NOI18N

        jCheckBox76.setText(resourceMap.getString("jCheckBox76.text")); // NOI18N
        jCheckBox76.setName("jCheckBox76"); // NOI18N

        jCheckBox77.setText(resourceMap.getString("jCheckBox77.text")); // NOI18N
        jCheckBox77.setName("jCheckBox77"); // NOI18N

        jCheckBox78.setText(resourceMap.getString("jCheckBox78.text")); // NOI18N
        jCheckBox78.setName("jCheckBox78"); // NOI18N

        jCheckBox79.setText(resourceMap.getString("jCheckBox79.text")); // NOI18N
        jCheckBox79.setName("jCheckBox79"); // NOI18N

        jCheckBox80.setText(resourceMap.getString("jCheckBox80.text")); // NOI18N
        jCheckBox80.setName("jCheckBox80"); // NOI18N

        jCheckBox81.setText(resourceMap.getString("jCheckBox81.text")); // NOI18N
        jCheckBox81.setName("jCheckBox81"); // NOI18N

        jCheckBox82.setText(resourceMap.getString("jCheckBox82.text")); // NOI18N
        jCheckBox82.setName("jCheckBox82"); // NOI18N

        jCheckBox83.setText(resourceMap.getString("jCheckBox83.text")); // NOI18N
        jCheckBox83.setName("jCheckBox83"); // NOI18N

        jCheckBox84.setText(resourceMap.getString("jCheckBox84.text")); // NOI18N
        jCheckBox84.setName("jCheckBox84"); // NOI18N

        jCheckBox85.setText(resourceMap.getString("jCheckBox85.text")); // NOI18N
        jCheckBox85.setName("jCheckBox85"); // NOI18N

        jSeparator19.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator19.setName("jSeparator19"); // NOI18N

        jCheckBox86.setText(resourceMap.getString("jCheckBox86.text")); // NOI18N
        jCheckBox86.setName("jCheckBox86"); // NOI18N

        jCheckBox87.setText(resourceMap.getString("jCheckBox87.text")); // NOI18N
        jCheckBox87.setName("jCheckBox87"); // NOI18N

        jCheckBox88.setText(resourceMap.getString("jCheckBox88.text")); // NOI18N
        jCheckBox88.setName("jCheckBox88"); // NOI18N

        jSeparator20.setName("jSeparator20"); // NOI18N

        jButton59.setAction(actionMap.get("deleteDefaultObject")); // NOI18N
        jButton59.setName("jButton59"); // NOI18N

        jButton60.setAction(actionMap.get("saveDefaultObject")); // NOI18N
        jButton60.setName("jButton60"); // NOI18N

        javax.swing.GroupLayout cachemgr_passwd_pageLayout = new javax.swing.GroupLayout(cachemgr_passwd_page);
        cachemgr_passwd_page.setLayout(cachemgr_passwd_pageLayout);
        cachemgr_passwd_pageLayout.setHorizontalGroup(
            cachemgr_passwd_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cachemgr_passwd_pageLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(cachemgr_passwd_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(cachemgr_passwd_pageLayout.createSequentialGroup()
                        .addGroup(cachemgr_passwd_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBox52)
                            .addComponent(jCheckBox53)
                            .addComponent(jCheckBox54)
                            .addComponent(jCheckBox55)
                            .addComponent(jCheckBox56)
                            .addComponent(jCheckBox57)
                            .addComponent(jCheckBox58)
                            .addComponent(jCheckBox59)
                            .addComponent(jCheckBox60)
                            .addComponent(jCheckBox61)
                            .addComponent(jCheckBox62)
                            .addComponent(jCheckBox63)
                            .addComponent(jCheckBox64)
                            .addComponent(jCheckBox65)
                            .addComponent(jCheckBox66)
                            .addComponent(jCheckBox67)
                            .addComponent(jCheckBox68))
                        .addGap(18, 18, 18)
                        .addComponent(jSeparator18, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(cachemgr_passwd_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(cachemgr_passwd_pageLayout.createSequentialGroup()
                                .addGroup(cachemgr_passwd_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jCheckBox74)
                                    .addComponent(jCheckBox75)
                                    .addComponent(jCheckBox76)
                                    .addComponent(jCheckBox77)
                                    .addComponent(jCheckBox78)
                                    .addComponent(jCheckBox79)
                                    .addComponent(jCheckBox80)
                                    .addComponent(jCheckBox71)
                                    .addComponent(jCheckBox72)
                                    .addComponent(jCheckBox73)
                                    .addComponent(jCheckBox81)
                                    .addComponent(jCheckBox82)
                                    .addComponent(jCheckBox83)
                                    .addComponent(jCheckBox84)
                                    .addComponent(jCheckBox69)
                                    .addComponent(jCheckBox70))
                                .addGap(3, 3, 3)
                                .addComponent(jSeparator19, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(cachemgr_passwd_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jCheckBox86)
                                    .addComponent(jCheckBox87)
                                    .addComponent(jCheckBox88)))
                            .addComponent(jCheckBox85)))
                    .addComponent(jSeparator20, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(cachemgr_passwd_pageLayout.createSequentialGroup()
                        .addComponent(jButton59)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton60)))
                .addContainerGap(640, Short.MAX_VALUE))
        );
        cachemgr_passwd_pageLayout.setVerticalGroup(
            cachemgr_passwd_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cachemgr_passwd_pageLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(cachemgr_passwd_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(cachemgr_passwd_pageLayout.createSequentialGroup()
                        .addGroup(cachemgr_passwd_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(cachemgr_passwd_pageLayout.createSequentialGroup()
                                .addGroup(cachemgr_passwd_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(cachemgr_passwd_pageLayout.createSequentialGroup()
                                        .addComponent(jCheckBox86)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jCheckBox87)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jCheckBox88))
                                    .addGroup(cachemgr_passwd_pageLayout.createSequentialGroup()
                                        .addGroup(cachemgr_passwd_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(jSeparator19, javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, cachemgr_passwd_pageLayout.createSequentialGroup()
                                                .addComponent(jCheckBox69)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jCheckBox70)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jCheckBox71)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jCheckBox72)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jCheckBox73)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jCheckBox74)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jCheckBox75)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jCheckBox76)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jCheckBox77)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jCheckBox78)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jCheckBox79)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jCheckBox80)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jCheckBox81)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jCheckBox82)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jCheckBox83)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jCheckBox84)))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jCheckBox85)))
                                .addGap(6, 6, 6))
                            .addGroup(cachemgr_passwd_pageLayout.createSequentialGroup()
                                .addComponent(jSeparator18, javax.swing.GroupLayout.DEFAULT_SIZE, 399, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                        .addGap(53, 53, 53))
                    .addGroup(cachemgr_passwd_pageLayout.createSequentialGroup()
                        .addComponent(jCheckBox52)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBox53)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBox54)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBox55)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBox56)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBox57)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBox58)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBox59)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBox60)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBox61)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBox62)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBox63)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBox64)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBox65)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBox66)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBox67)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBox68)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(jSeparator20, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(cachemgr_passwd_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton59)
                    .addComponent(jButton60))
                .addContainerGap())
        );

        jTabbedPane1.addTab(resourceMap.getString("cachemgr_passwd_page.TabConstraints.tabTitle"), cachemgr_passwd_page); // NOI18N

        uri_page.setName("uri_page"); // NOI18N

        jComboBox23.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "strip", "deny", "allow", "encode", "chop" }));
        jComboBox23.setName("jComboBox23"); // NOI18N

        jSeparator21.setName("jSeparator21"); // NOI18N

        jButton61.setAction(actionMap.get("emptyOnceParam")); // NOI18N
        jButton61.setName("jButton61"); // NOI18N

        jButton62.setAction(actionMap.get("saveUri")); // NOI18N
        jButton62.setName("jButton62"); // NOI18N

        jButton75.setAction(actionMap.get("showHelp1")); // NOI18N
        jButton75.setName("jButton75"); // NOI18N

        javax.swing.GroupLayout uri_pageLayout = new javax.swing.GroupLayout(uri_page);
        uri_page.setLayout(uri_pageLayout);
        uri_pageLayout.setHorizontalGroup(
            uri_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(uri_pageLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(uri_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator21, javax.swing.GroupLayout.DEFAULT_SIZE, 1014, Short.MAX_VALUE)
                    .addComponent(jComboBox23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(uri_pageLayout.createSequentialGroup()
                        .addComponent(jButton61)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton62)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton75)))
                .addContainerGap())
        );
        uri_pageLayout.setVerticalGroup(
            uri_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(uri_pageLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jComboBox23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator21, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(uri_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton61)
                    .addComponent(jButton62)
                    .addComponent(jButton75))
                .addContainerGap(414, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(resourceMap.getString("uri_page.TabConstraints.tabTitle"), uri_page); // NOI18N

        user_page.setToolTipText(resourceMap.getString("user_page.toolTipText")); // NOI18N
        user_page.setName("user_page"); // NOI18N

        jLabel112.setText(resourceMap.getString("jLabel112.text")); // NOI18N
        jLabel112.setName("jLabel112"); // NOI18N

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        jList2.setFont(resourceMap.getFont("jList2.font")); // NOI18N
        jList2.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList2.setLayoutOrientation(javax.swing.JList.VERTICAL_WRAP);
        jList2.setName("jList2"); // NOI18N
        jScrollPane3.setViewportView(jList2);

        jLabel113.setText(resourceMap.getString("jLabel113.text")); // NOI18N
        jLabel113.setName("jLabel113"); // NOI18N

        jPasswordField2.setName("jPasswordField2"); // NOI18N
        jPasswordField2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jPasswordField2KeyPressed(evt);
            }
        });

        jLabel114.setText(resourceMap.getString("jLabel114.text")); // NOI18N
        jLabel114.setName("jLabel114"); // NOI18N

        jTextField44.setText(resourceMap.getString("jTextField44.text")); // NOI18N
        jTextField44.setName("jTextField44"); // NOI18N

        jLabel115.setText(resourceMap.getString("jLabel115.text")); // NOI18N
        jLabel115.setName("jLabel115"); // NOI18N

        jTextField98.setText(resourceMap.getString("jTextField98.text")); // NOI18N
        jTextField98.setName("jTextField98"); // NOI18N

        jLabel116.setText(resourceMap.getString("jLabel116.text")); // NOI18N
        jLabel116.setName("jLabel116"); // NOI18N

        jCheckBox89.setText(resourceMap.getString("jCheckBox89.text")); // NOI18N
        jCheckBox89.setName("jCheckBox89"); // NOI18N

        jLabel117.setText(resourceMap.getString("jLabel117.text")); // NOI18N
        jLabel117.setName("jLabel117"); // NOI18N

        jTextField99.setText(resourceMap.getString("jTextField99.text")); // NOI18N
        jTextField99.setName("jTextField99"); // NOI18N

        jCheckBox90.setText(resourceMap.getString("jCheckBox90.text")); // NOI18N
        jCheckBox90.setName("jCheckBox90"); // NOI18N

        jSeparator28.setName("jSeparator28"); // NOI18N

        jSeparator29.setName("jSeparator29"); // NOI18N

        jSeparator30.setName("jSeparator30"); // NOI18N

        jButton68.setAction(actionMap.get("saveUser")); // NOI18N
        jButton68.setName("jButton68"); // NOI18N

        jLabel121.setText(resourceMap.getString("jLabel121.text")); // NOI18N
        jLabel121.setName("jLabel121"); // NOI18N

        jTextField1.setText(resourceMap.getString("jTextField1.text")); // NOI18N
        jTextField1.setToolTipText(resourceMap.getString("jTextField1.toolTipText")); // NOI18N
        jTextField1.setName("jTextField1"); // NOI18N

        jLabel122.setText(resourceMap.getString("jLabel122.text")); // NOI18N
        jLabel122.setName("jLabel122"); // NOI18N

        jTextField100.setText(resourceMap.getString("jTextField100.text")); // NOI18N
        jTextField100.setName("jTextField100"); // NOI18N

        javax.swing.GroupLayout user_pageLayout = new javax.swing.GroupLayout(user_page);
        user_page.setLayout(user_pageLayout);
        user_pageLayout.setHorizontalGroup(
            user_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(user_pageLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(user_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBox90)
                    .addComponent(jCheckBox89)
                    .addComponent(jSeparator29, javax.swing.GroupLayout.DEFAULT_SIZE, 1014, Short.MAX_VALUE)
                    .addComponent(jSeparator28, javax.swing.GroupLayout.DEFAULT_SIZE, 1014, Short.MAX_VALUE)
                    .addGroup(user_pageLayout.createSequentialGroup()
                        .addGroup(user_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel113, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel114)
                            .addComponent(jLabel112, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(user_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 941, Short.MAX_VALUE)
                            .addComponent(jPasswordField2, javax.swing.GroupLayout.DEFAULT_SIZE, 941, Short.MAX_VALUE)
                            .addComponent(jTextField44, javax.swing.GroupLayout.DEFAULT_SIZE, 941, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, user_pageLayout.createSequentialGroup()
                        .addGroup(user_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel116, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel117, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel115))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(user_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField98, javax.swing.GroupLayout.DEFAULT_SIZE, 944, Short.MAX_VALUE)
                            .addComponent(jTextField99, javax.swing.GroupLayout.DEFAULT_SIZE, 944, Short.MAX_VALUE)))
                    .addComponent(jButton68)
                    .addComponent(jSeparator30, javax.swing.GroupLayout.DEFAULT_SIZE, 1014, Short.MAX_VALUE)
                    .addGroup(user_pageLayout.createSequentialGroup()
                        .addGroup(user_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel121)
                            .addComponent(jLabel122))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(user_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField100)
                            .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE))))
                .addContainerGap())
        );
        user_pageLayout.setVerticalGroup(
            user_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(user_pageLayout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(user_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel112))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(user_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel113)
                    .addComponent(jPasswordField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(user_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel114)
                    .addComponent(jTextField44, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator28, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel115)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(user_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField98, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel116))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(user_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel117)
                    .addComponent(jTextField99, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator29, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(user_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel121)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBox89)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox90)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(user_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel122)
                    .addComponent(jTextField100, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21)
                .addComponent(jSeparator30, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton68)
                .addGap(63, 63, 63))
        );

        jTabbedPane1.addTab(resourceMap.getString("user_page.TabConstraints.tabTitle"), user_page); // NOI18N

        help_page.setName("help_page"); // NOI18N

        jScrollPane4.setName("jScrollPane4"); // NOI18N

        jEditorPane1.setContentType(resourceMap.getString("jEditorPane1.contentType")); // NOI18N
        jEditorPane1.setEditable(false);
        jEditorPane1.setText(resourceMap.getString("jEditorPane1.text")); // NOI18N
        jEditorPane1.setName("jEditorPane1"); // NOI18N
        jScrollPane4.setViewportView(jEditorPane1);

        javax.swing.GroupLayout help_pageLayout = new javax.swing.GroupLayout(help_page);
        help_page.setLayout(help_pageLayout);
        help_pageLayout.setHorizontalGroup(
            help_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(help_pageLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 1014, Short.MAX_VALUE)
                .addContainerGap())
        );
        help_pageLayout.setVerticalGroup(
            help_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(help_pageLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 488, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab(resourceMap.getString("help_page.TabConstraints.tabTitle"), help_page); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1043, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 625, Short.MAX_VALUE)
                .addContainerGap())
        );

        jSplitPane1.setRightComponent(jPanel1);

        jScrollPane1.setMinimumSize(new java.awt.Dimension(122, 22));
        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTree1.setMinimumSize(new java.awt.Dimension(100, 0));
        jTree1.setModel(tree);
        jTree1.setName("jTree1"); // NOI18N
        jTree1.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                jTree1ValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jTree1);

        jSplitPane1.setLeftComponent(jScrollPane1);

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1196, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 625, Short.MAX_VALUE)
                .addContainerGap())
        );

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        jMenuItem18.setAction(actionMap.get("clearConfiguration")); // NOI18N
        jMenuItem18.setName("jMenuItem18"); // NOI18N
        fileMenu.add(jMenuItem18);

        jSeparator9.setName("jSeparator9"); // NOI18N
        fileMenu.add(jSeparator9);

        jMenuItem16.setAction(actionMap.get("openSquidConfFile")); // NOI18N
        jMenuItem16.setName("jMenuItem16"); // NOI18N
        fileMenu.add(jMenuItem16);

        jSeparator6.setName("jSeparator6"); // NOI18N
        fileMenu.add(jSeparator6);

        jMenuItem1.setAction(actionMap.get("saveToFile")); // NOI18N
        jMenuItem1.setName("jMenuItem1"); // NOI18N
        fileMenu.add(jMenuItem1);

        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        jMenu2.setText(resourceMap.getString("jMenu2.text")); // NOI18N
        jMenu2.setName("jMenu2"); // NOI18N

        jMenuItem17.setAction(actionMap.get("reloadTree")); // NOI18N
        jMenuItem17.setName("jMenuItem17"); // NOI18N
        jMenu2.add(jMenuItem17);

        jSeparator4.setName("jSeparator4"); // NOI18N
        jMenu2.add(jSeparator4);

        jMenuItem19.setAction(actionMap.get("showConfText")); // NOI18N
        jMenuItem19.setName("jMenuItem19"); // NOI18N
        jMenu2.add(jMenuItem19);

        jSeparator33.setName("jSeparator33"); // NOI18N
        jMenu2.add(jSeparator33);

        jMenuItem8.setAction(actionMap.get("findTreeObject")); // NOI18N
        jMenuItem8.setName("jMenuItem8"); // NOI18N
        jMenu2.add(jMenuItem8);

        jSeparator13.setName("jSeparator13"); // NOI18N
        jMenu2.add(jSeparator13);

        menuBar.add(jMenu2);

        jMenu1.setText(resourceMap.getString("jMenu1.text")); // NOI18N
        jMenu1.setName("jMenu1"); // NOI18N

        jMenuItem2.setAction(actionMap.get("applyConf")); // NOI18N
        jMenuItem2.setName("jMenuItem2"); // NOI18N
        jMenu1.add(jMenuItem2);

        jMenu3.setText(resourceMap.getString("jMenu3.text")); // NOI18N
        jMenu3.setName("jMenu3"); // NOI18N

        jMenuItem51.setText(resourceMap.getString("jMenuItem51.text")); // NOI18N
        jMenuItem51.setName("jMenuItem51"); // NOI18N
        jMenu3.add(jMenuItem51);

        jMenuItem52.setText(resourceMap.getString("jMenuItem52.text")); // NOI18N
        jMenuItem52.setName("jMenuItem52"); // NOI18N
        jMenu3.add(jMenuItem52);

        jMenuItem53.setText(resourceMap.getString("jMenuItem53.text")); // NOI18N
        jMenuItem53.setName("jMenuItem53"); // NOI18N
        jMenu3.add(jMenuItem53);

        jMenuItem54.setText(resourceMap.getString("jMenuItem54.text")); // NOI18N
        jMenuItem54.setName("jMenuItem54"); // NOI18N
        jMenu3.add(jMenuItem54);

        jMenu1.add(jMenu3);

        menuBar.add(jMenu1);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        jSeparator3.setName("jSeparator3"); // NOI18N
        helpMenu.add(jSeparator3);

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setValue(46);
        progressBar.setName("progressBar"); // NOI18N

        jLabel2.setFont(resourceMap.getFont("jLabel2.font")); // NOI18N
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(statusMessageLabel)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 1000, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusAnimationLabel)
                .addContainerGap())
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1196, Short.MAX_VALUE)
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(statusPanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                        .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(statusMessageLabel)
                            .addComponent(statusAnimationLabel)
                            .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(3, 3, 3))
                    .addGroup(statusPanelLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 15, Short.MAX_VALUE))))
        );

        once_param_popup.setName("once_param_popup"); // NOI18N

        jMenuItem3.setAction(actionMap.get("emptyOnceParam")); // NOI18N
        jMenuItem3.setName("jMenuItem3"); // NOI18N
        once_param_popup.add(jMenuItem3);

        delay_popup.setName("delay_popup"); // NOI18N

        jMenuItem4.setAction(actionMap.get("delDelay")); // NOI18N
        jMenuItem4.setName("jMenuItem4"); // NOI18N
        delay_popup.add(jMenuItem4);

        acl_item_menu.setName("acl_item_menu"); // NOI18N

        jMenuItem21.setText(resourceMap.getString("jMenuItem21.text")); // NOI18N
        jMenuItem21.setEnabled(false);
        jMenuItem21.setName("jMenuItem21"); // NOI18N
        acl_item_menu.add(jMenuItem21);

        jSeparator8.setName("jSeparator8"); // NOI18N
        acl_item_menu.add(jSeparator8);

        jMenuItem10.setAction(actionMap.get("moveAclFirst")); // NOI18N
        jMenuItem10.setName("jMenuItem10"); // NOI18N
        acl_item_menu.add(jMenuItem10);

        jMenuItem11.setAction(actionMap.get("moveAclUp")); // NOI18N
        jMenuItem11.setName("jMenuItem11"); // NOI18N
        acl_item_menu.add(jMenuItem11);

        jMenuItem12.setAction(actionMap.get("moveAclDown")); // NOI18N
        jMenuItem12.setName("jMenuItem12"); // NOI18N
        acl_item_menu.add(jMenuItem12);

        jMenuItem13.setAction(actionMap.get("moveAclLast")); // NOI18N
        jMenuItem13.setName("jMenuItem13"); // NOI18N
        acl_item_menu.add(jMenuItem13);

        jSeparator7.setName("jSeparator7"); // NOI18N
        acl_item_menu.add(jSeparator7);

        jMenuItem14.setAction(actionMap.get("saveCommonAcl")); // NOI18N
        jMenuItem14.setName("jMenuItem14"); // NOI18N
        acl_item_menu.add(jMenuItem14);

        jMenuItem20.setAction(actionMap.get("removeCurrentAcl")); // NOI18N
        jMenuItem20.setName("jMenuItem20"); // NOI18N
        acl_item_menu.add(jMenuItem20);

        popup_access.setName("popup_access"); // NOI18N

        jMenuItem22.setAction(actionMap.get("addAccessLine")); // NOI18N
        jMenuItem22.setName("jMenuItem22"); // NOI18N
        popup_access.add(jMenuItem22);

        jMenuItem28.setAction(actionMap.get("insertAccessLine")); // NOI18N
        jMenuItem28.setName("jMenuItem28"); // NOI18N
        popup_access.add(jMenuItem28);

        jSeparator11.setName("jSeparator11"); // NOI18N
        popup_access.add(jSeparator11);

        jMenuItem23.setAction(actionMap.get("delAccessLine")); // NOI18N
        jMenuItem23.setName("jMenuItem23"); // NOI18N
        popup_access.add(jMenuItem23);

        jSeparator10.setName("jSeparator10"); // NOI18N
        popup_access.add(jSeparator10);

        jMenuItem26.setAction(actionMap.get("moveAccessFirst")); // NOI18N
        jMenuItem26.setName("jMenuItem26"); // NOI18N
        popup_access.add(jMenuItem26);

        jMenuItem24.setAction(actionMap.get("accessLineUp")); // NOI18N
        jMenuItem24.setName("jMenuItem24"); // NOI18N
        popup_access.add(jMenuItem24);

        jMenuItem25.setAction(actionMap.get("moveAccessDown")); // NOI18N
        jMenuItem25.setName("jMenuItem25"); // NOI18N
        popup_access.add(jMenuItem25);

        jMenuItem27.setAction(actionMap.get("moveAccessLast")); // NOI18N
        jMenuItem27.setName("jMenuItem27"); // NOI18N
        popup_access.add(jMenuItem27);

        conf_menu.setName("conf_menu"); // NOI18N

        jMenuItem6.setAction(actionMap.get("clearConfiguration")); // NOI18N
        jMenuItem6.setName("jMenuItem6"); // NOI18N
        conf_menu.add(jMenuItem6);

        jMenuItem5.setAction(actionMap.get("saveToFile")); // NOI18N
        jMenuItem5.setName("jMenuItem5"); // NOI18N
        conf_menu.add(jMenuItem5);

        jMenuItem7.setAction(actionMap.get("openSquidConfFile")); // NOI18N
        jMenuItem7.setName("jMenuItem7"); // NOI18N
        conf_menu.add(jMenuItem7);

        jSeparator1.setName("jSeparator1"); // NOI18N
        conf_menu.add(jSeparator1);

        jMenuItem30.setAction(actionMap.get("loadConfFromText")); // NOI18N
        jMenuItem30.setName("jMenuItem30"); // NOI18N
        conf_menu.add(jMenuItem30);

        aclvalues_popup.setName("aclvalues_popup"); // NOI18N

        jMenuItem31.setAction(actionMap.get("insert10values")); // NOI18N
        jMenuItem31.setName("jMenuItem31"); // NOI18N
        aclvalues_popup.add(jMenuItem31);

        jMenuItem32.setAction(actionMap.get("deleteAclValues")); // NOI18N
        jMenuItem32.setName("jMenuItem32"); // NOI18N
        aclvalues_popup.add(jMenuItem32);

        cachepeer_popup.setName("cachepeer_popup"); // NOI18N

        jMenuItem15.setAction(actionMap.get("addCachePeer")); // NOI18N
        jMenuItem15.setName("jMenuItem15"); // NOI18N
        cachepeer_popup.add(jMenuItem15);

        external_acl_popup.setName("external_acl_popup"); // NOI18N

        jMenuItem33.setAction(actionMap.get("addExternalAclType")); // NOI18N
        jMenuItem33.setName("jMenuItem33"); // NOI18N
        external_acl_popup.add(jMenuItem33);

        popup_tcp_outgoing_addr.setName("popup_tcp_outgoing_addr"); // NOI18N

        jMenuItem34.setAction(actionMap.get("addTcpoutgoingAddr")); // NOI18N
        jMenuItem34.setName("jMenuItem34"); // NOI18N
        popup_tcp_outgoing_addr.add(jMenuItem34);

        popup_tcpout_tos.setName("popup_tcpout_tos"); // NOI18N

        jMenuItem35.setAction(actionMap.get("addTcpOutTos")); // NOI18N
        jMenuItem35.setName("jMenuItem35"); // NOI18N
        popup_tcpout_tos.add(jMenuItem35);

        popup_logformat.setName("popup_logformat"); // NOI18N

        jMenuItem36.setAction(actionMap.get("newLogFormat")); // NOI18N
        jMenuItem36.setName("jMenuItem36"); // NOI18N
        popup_logformat.add(jMenuItem36);

        popup_AccessLog.setName("popup_AccessLog"); // NOI18N

        jMenuItem37.setAction(actionMap.get("newAccessLog")); // NOI18N
        jMenuItem37.setName("jMenuItem37"); // NOI18N
        popup_AccessLog.add(jMenuItem37);

        popup_RefreshPattern.setName("popup_RefreshPattern"); // NOI18N

        jMenuItem38.setAction(actionMap.get("newRefreshPattern")); // NOI18N
        jMenuItem38.setName("jMenuItem38"); // NOI18N
        popup_RefreshPattern.add(jMenuItem38);

        popup_header_acc.setName("popup_header_acc"); // NOI18N

        jMenuItem39.setAction(actionMap.get("newHeaderAccess")); // NOI18N
        jMenuItem39.setName("jMenuItem39"); // NOI18N
        popup_header_acc.add(jMenuItem39);

        popup_header_replace.setName("popup_header_replace"); // NOI18N

        jMenuItem40.setAction(actionMap.get("newHeaderReplace")); // NOI18N
        jMenuItem40.setName("jMenuItem40"); // NOI18N
        popup_header_replace.add(jMenuItem40);

        popup_def_obj.setName("popup_def_obj"); // NOI18N

        jMenuItem41.setAction(actionMap.get("addDefObj")); // NOI18N
        jMenuItem41.setName("jMenuItem41"); // NOI18N
        popup_def_obj.add(jMenuItem41);

        popup_dev_obj_item.setName("popup_dev_obj_item"); // NOI18N

        jMenuItem43.setAction(actionMap.get("addDefObj")); // NOI18N
        jMenuItem43.setName("jMenuItem43"); // NOI18N
        popup_dev_obj_item.add(jMenuItem43);

        jMenuItem44.setAction(actionMap.get("saveDefaultObject")); // NOI18N
        jMenuItem44.setName("jMenuItem44"); // NOI18N
        popup_dev_obj_item.add(jMenuItem44);

        jSeparator16.setName("jSeparator16"); // NOI18N
        popup_dev_obj_item.add(jSeparator16);

        jMenuItem45.setAction(actionMap.get("deleteDefaultObject")); // NOI18N
        jMenuItem45.setName("jMenuItem45"); // NOI18N
        popup_dev_obj_item.add(jMenuItem45);

        popup_ssh.setName("popup_ssh"); // NOI18N

        jMenuItem47.setAction(actionMap.get("newSsh")); // NOI18N
        jMenuItem47.setName("jMenuItem47"); // NOI18N
        popup_ssh.add(jMenuItem47);

        popup_ssh_item.setName("popup_ssh_item"); // NOI18N

        jMenuItem48.setAction(actionMap.get("newSsh")); // NOI18N
        jMenuItem48.setName("jMenuItem48"); // NOI18N
        popup_ssh_item.add(jMenuItem48);

        jSeparator27.setName("jSeparator27"); // NOI18N
        popup_ssh_item.add(jSeparator27);

        jMenuItem49.setAction(actionMap.get("saveSSH")); // NOI18N
        jMenuItem49.setName("jMenuItem49"); // NOI18N
        popup_ssh_item.add(jMenuItem49);

        jMenuItem50.setAction(actionMap.get("delSsh")); // NOI18N
        jMenuItem50.setName("jMenuItem50"); // NOI18N
        popup_ssh_item.add(jMenuItem50);

        jMenuItem42.setAction(actionMap.get("setDefSSH")); // NOI18N
        jMenuItem42.setName("jMenuItem42"); // NOI18N
        popup_ssh_item.add(jMenuItem42);

        popup_user_groups.setName("popup_user_groups"); // NOI18N

        jMenuItem9.setAction(actionMap.get("removeUserFromGr")); // NOI18N
        jMenuItem9.setName("jMenuItem9"); // NOI18N
        popup_user_groups.add(jMenuItem9);

        jMenuItem29.setAction(actionMap.get("addUserGr")); // NOI18N
        jMenuItem29.setName("jMenuItem29"); // NOI18N
        popup_user_groups.add(jMenuItem29);

        popup_groups_userlist.setName("popup_groups_userlist"); // NOI18N

        jMenuItem46.setText(resourceMap.getString("jMenuItem46.text")); // NOI18N
        jMenuItem46.setName("jMenuItem46"); // NOI18N
        popup_groups_userlist.add(jMenuItem46);

        jMenuItem55.setText(resourceMap.getString("jMenuItem55.text")); // NOI18N
        jMenuItem55.setName("jMenuItem55"); // NOI18N
        popup_groups_userlist.add(jMenuItem55);

        jMenuItem56.setText(resourceMap.getString("jMenuItem56.text")); // NOI18N
        jMenuItem56.setName("jMenuItem56"); // NOI18N
        popup_groups_userlist.add(jMenuItem56);

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

private void jTree1ValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_jTree1ValueChanged
// TODO add your handling code here:
    //

    //evt.
    //;



    jTree1.setToolTipText("");
    jTabbedPane1.removeAll();
    DefaultMutableTreeNode node = (DefaultMutableTreeNode)(evt.getPath()).getLastPathComponent();
    getFrame().setTitle("SquidModel");
    if (node == null) {
        System.out.print("jTree1ValueChanged: NULL is selected");
        return;
    }
    currentNode = node;

   // System.out.println("node: " + node.toString());
        //TreeNode[] tp = node.getPath();




    if (node.isRoot()){
        jTabbedPane1.addTab("Squid model", welcome);
        welcome.repaint();
        System.out.print("jTree1ValueChanged: ROOT is selected");

        return;
    }


    String path = "";
    for (int i = 0; i < node.getPath().length - 1; ++i){
        path = path + node.getPath()[i] + "/";
    }


    getFrame().setTitle(path + node.toString() + " - " + currentFileName);
    currentParamName = node.toString();





    System.out.print("jTree1ValueChanged: n=" + currentParamName);

    jTree1.setComponentPopupMenu(null);

    if (node.toString().equals("squid.conf")){

        showConfText();
        jTree1.setComponentPopupMenu(conf_menu);
        return;
    }


    if (node.toString().equals("header_replace")){
        jTree1.setComponentPopupMenu(popup_header_replace);

    }

    if (node.getParent().toString().equals("header_replace")){
        jTree1.setComponentPopupMenu(popup_header_replace);

	current_header_replace = node.toString();
	int ix = squidConf.getheader_replaceIndex(current_header_replace);

	if (ix == -1) return;
	jTabbedPane1.addTab("header_replace " + current_header_replace, header_replace_page);
	jTextField92.setText(
		squidConf.header_replace.get(ix).value
		);


    }
    
    
    if (node.toString().equals("wccp2_service") | 
	node.toString().equals("error_map") |     
	node.toString().equals("deny_info") |  
	node.toString().equals("cachemgr_passwd") |  
	node.toString().equals("wccp2_service_info")){
	jTree1.setComponentPopupMenu(popup_def_obj);
	currentDefaultGroupName = node.toString();
    }
    
    if (node.getParent().toString().equals("cachemgr_passwd")){

	currentDefaultName = node.toString();
	currentDefaultGroupName = node.getParent().toString();
	
	jTree1.setComponentPopupMenu(popup_dev_obj_item);

	jTabbedPane1.addTab(currentDefaultGroupName + " " + currentDefaultName,
		cachemgr_passwd_page);

	String value = squidConf.getDefaultObjectValue(currentDefaultGroupName, currentDefaultName).trim();   
	jCheckBox52.setSelected(false);
	jCheckBox53.setSelected(false);
	jCheckBox54.setSelected(false);
	jCheckBox55.setSelected(false);
	jCheckBox56.setSelected(false);
	jCheckBox57.setSelected(false);
	jCheckBox58.setSelected(false);
	jCheckBox59.setSelected(false);
	
	jCheckBox60.setSelected(false);
	jCheckBox61.setSelected(false);
	jCheckBox62.setSelected(false);
	jCheckBox63.setSelected(false);
	jCheckBox64.setSelected(false);
	jCheckBox65.setSelected(false);
	jCheckBox66.setSelected(false);
	jCheckBox67.setSelected(false);
	jCheckBox68.setSelected(false);
	jCheckBox69.setSelected(false);
	
	jCheckBox70.setSelected(false);
	jCheckBox71.setSelected(false);
	jCheckBox72.setSelected(false);
	jCheckBox73.setSelected(false);
	jCheckBox74.setSelected(false);
	jCheckBox75.setSelected(false);
	jCheckBox76.setSelected(false);
	jCheckBox77.setSelected(false);
	jCheckBox78.setSelected(false);
	jCheckBox79.setSelected(false);
	
	jCheckBox80.setSelected(false);
	jCheckBox81.setSelected(false);
	jCheckBox82.setSelected(false);
	jCheckBox83.setSelected(false);
	jCheckBox84.setSelected(false);
	jCheckBox85.setSelected(false);
	jCheckBox86.setSelected(false);
	jCheckBox87.setSelected(false);
	jCheckBox88.setSelected(false);
	
	
	
	StringTokenizer st = new StringTokenizer(value);
	while (st.hasMoreTokens()){
	    String tok = st.nextToken();
	    if (tok.equals(jCheckBox52.getText())) jCheckBox52.setSelected(true);
	    if (tok.equals(jCheckBox53.getText())) jCheckBox53.setSelected(true);
	    if (tok.equals(jCheckBox54.getText())) jCheckBox54.setSelected(true);
	    if (tok.equals(jCheckBox55.getText())) jCheckBox55.setSelected(true);
	    if (tok.equals(jCheckBox56.getText())) jCheckBox56.setSelected(true);
	    if (tok.equals(jCheckBox57.getText())) jCheckBox57.setSelected(true);
	    if (tok.equals(jCheckBox58.getText())) jCheckBox58.setSelected(true);
	    if (tok.equals(jCheckBox59.getText())) jCheckBox59.setSelected(true);
	    
	    
	    
	    
	    if (tok.equals(jCheckBox60.getText())) jCheckBox60.setSelected(true);
	    if (tok.equals(jCheckBox61.getText())) jCheckBox61.setSelected(true);
	    if (tok.equals(jCheckBox62.getText())) jCheckBox62.setSelected(true);
	    if (tok.equals(jCheckBox63.getText())) jCheckBox63.setSelected(true);
	    if (tok.equals(jCheckBox64.getText())) jCheckBox64.setSelected(true);
	    if (tok.equals(jCheckBox65.getText())) jCheckBox65.setSelected(true);
	    if (tok.equals(jCheckBox66.getText())) jCheckBox66.setSelected(true);
	    if (tok.equals(jCheckBox67.getText())) jCheckBox67.setSelected(true);
	    if (tok.equals(jCheckBox68.getText())) jCheckBox68.setSelected(true);
	    if (tok.equals(jCheckBox69.getText())) jCheckBox69.setSelected(true);
	    
	    
	    if (tok.equals(jCheckBox70.getText())) jCheckBox70.setSelected(true);
	    if (tok.equals(jCheckBox71.getText())) jCheckBox71.setSelected(true);
	    if (tok.equals(jCheckBox72.getText())) jCheckBox72.setSelected(true);
	    if (tok.equals(jCheckBox73.getText())) jCheckBox73.setSelected(true);
	    if (tok.equals(jCheckBox74.getText())) jCheckBox74.setSelected(true);
	    if (tok.equals(jCheckBox75.getText())) jCheckBox75.setSelected(true);
	    if (tok.equals(jCheckBox76.getText())) jCheckBox76.setSelected(true);
	    if (tok.equals(jCheckBox77.getText())) jCheckBox77.setSelected(true);
	    if (tok.equals(jCheckBox78.getText())) jCheckBox78.setSelected(true);
	    if (tok.equals(jCheckBox79.getText())) jCheckBox79.setSelected(true);
	    
	    if (tok.equals(jCheckBox80.getText())) jCheckBox80.setSelected(true);
	    if (tok.equals(jCheckBox81.getText())) jCheckBox81.setSelected(true);
	    if (tok.equals(jCheckBox82.getText())) jCheckBox82.setSelected(true);
	    if (tok.equals(jCheckBox83.getText())) jCheckBox83.setSelected(true);
	    if (tok.equals(jCheckBox84.getText())) jCheckBox84.setSelected(true);
	    if (tok.equals(jCheckBox85.getText())) jCheckBox85.setSelected(true);
	    if (tok.equals(jCheckBox86.getText())) jCheckBox86.setSelected(true);
	    if (tok.equals(jCheckBox87.getText())) jCheckBox87.setSelected(true);
	    if (tok.equals(jCheckBox88.getText())) jCheckBox88.setSelected(true);
	}
	
	
    }   
    
    if (node.getParent().toString().equals("My groups")){
        currentGroupName = node.toString();
        jTabbedPane1.addTab(currentGroupName, group_page);
        
        
        DefaultListModel 
                modelUsers  =  new DefaultListModel(),
                modelAccess =  new DefaultListModel();
        //Properties p1 = new Properties();
        
        jList1.setModel(modelUsers);
        jList3.setModel(modelAccess);
        
        
        SquidAcl sa = squidConf.getAcl(currentGroupName);
        if (sa == null) return;
        
        StringTokenizer st = new StringTokenizer(sa.value);
        int users = 0;
        while (st.hasMoreTokens()) {
            modelUsers.addElement(st.nextToken());
            users++;
        }
        
        jLabel118.setText("Users ("+users+")");
        
        for (int i = 0; i<squidConf.access.size(); ++i) {
            SquidAccessVector sav = squidConf.access.get(i);
            
            for (int j = 0; j < sav.size(); j++){
                if (sav.get(j).value.indexOf(currentGroupName) >= 0) modelAccess.addElement(sav.name);
            }
        }
        
        
        
        
        
    }
    
    DefaultMutableTreeNode parent2 = new DefaultMutableTreeNode("NONE");
    
    
    if (node.getParent() != null)
        if (node.getParent().getParent() != null) parent2 = (DefaultMutableTreeNode) node.getParent().getParent();
    
    if (node.getParent().toString().equals("My users") | parent2.toString().equals("My groups")){
        currentUserName = node.toString();

        

        
        
         DefaultListModel l =  new DefaultListModel();
        l.removeAllElements();
       // l.addElement("test");
        for (int i = 0; i < squidConf.acl.size(); i++){
            if (squidConf.acl.get(i).type.equals("proxy_auth"))
                if (squidConf.acl.get(i).value.indexOf(currentUserName)>=0)
                   l.addElement(squidConf.acl.get(i).name.toString());
        }
        jList2.setModel(l);
        jPasswordField2.setText("");
        jPasswordField2.setToolTipText("");
        
        jTextField1.setText("");
        jTextField44.setText("");
        jTextField98.setText("");
        jTextField99.setText("");
        jTextField100.setText("0");

        jCheckBox89.setSelected(false);
        jCheckBox90.setSelected(false);
        
        String value = squidConf.getDefaultObjectValue("#user",currentUserName);
        removedGruops = "";
        addedGroups = "";
        
        if (value == null) {
            jTabbedPane1.addTab(currentUserName + " * (not saved)", user_page);
            return;
        };
        jTabbedPane1.addTab(currentUserName, user_page);

        StringTokenizer st = new StringTokenizer(value);
        while (st.hasMoreTokens()){
            String tok = st.nextToken();
            if (tok.indexOf("password=") == 0) jPasswordField2.setText(tok.split("=")[1]);
            if (tok.indexOf("email=") == 0) jTextField44.setText(tok.split("=")[1]);
            if (tok.indexOf("limitmonth=") == 0) jTextField98.setText(tok.split("=")[1]);
            if (tok.indexOf("limitday=") == 0) jTextField99.setText(tok.split("=")[1]);
            if (tok.indexOf("expireddate=") == 0) jTextField1.setText(tok.split("=")[1]);
            if (tok.indexOf("disablenever=true") == 0) jCheckBox89.setSelected(true);
            if (tok.indexOf("disable=true") == 0) jCheckBox90.setSelected(true);
            if (tok.indexOf("id=") == 0) jTextField100.setText(tok.split("=")[1]);
            
            
        }
        
        jPasswordField2.setToolTipText(jPasswordField2.getText());
        
    }    
    
    if (node.getParent().toString().equals("deny_info")){

	currentDefaultName = node.toString();
	currentDefaultGroupName = node.getParent().toString();
	
	jTree1.setComponentPopupMenu(popup_dev_obj_item);

	jTabbedPane1.addTab(currentDefaultGroupName + " " + currentDefaultName,
		deny_info_page);

	String value = squidConf.getDefaultObjectValue(currentDefaultGroupName, currentDefaultName).trim();   
	
	
	jTextField97.setText(currentDefaultName);
	jComboBox22.removeAllItems();
        for (int i = 0; i < squidConf.acl.size(); i++) {
            jComboBox22.addItem(
                squidConf.acl.get(i).name
                    );
        }	
	//JOptionPane.showMessageDialog(mainPanel, "[" + value + "]");
	jComboBox22.setSelectedItem(value);
	
	
	
	/*
	         combo.addItem("");


	 */
	
    }
    
    if (node.getParent().toString().equals("wccp2_service_info")){

	currentDefaultName = node.toString();
	currentDefaultGroupName = node.getParent().toString();
	
	jTree1.setComponentPopupMenu(popup_dev_obj_item);

	jTabbedPane1.addTab(currentDefaultGroupName + " " + currentDefaultName,
		wccp2_service_info_page);

	String value = squidConf.getDefaultObjectValue(currentDefaultGroupName, currentDefaultName);  
	
	jCheckBox43.setSelected(false);
	jCheckBox44.setSelected(false);
	jCheckBox45.setSelected(false);
	jCheckBox46.setSelected(false);
	jCheckBox47.setSelected(false);
	jCheckBox48.setSelected(false);
	jCheckBox49.setSelected(false);
	jCheckBox50.setSelected(false);
	jCheckBox51.setSelected(false);
	jTextField95.setText("");
	jTextField96.setText("");
	
	if (value == null) return;
	
	//
	
	StringTokenizer st = new StringTokenizer(value);
	while (st.hasMoreTokens()){
	    String tok = st.nextToken();
	    System.out.println("+++++++++++++++tok=" + tok);
	    if (tok.indexOf("protocol=") > -1) jComboBox21.setSelectedItem(tok.split("=")[1]);
	    if (tok.indexOf("priority=") > -1) jTextField95.setText(tok.split("=")[1]);
	    if (tok.indexOf("ports=") > -1) jTextField96.setText(tok.split("=")[1]);
	    if (tok.indexOf("flags=") > -1){
		StringTokenizer s2 = new StringTokenizer(tok.split("=")[1], ",");
		
		while (s2.hasMoreTokens()){
		    String t2 = s2.nextToken(",");
		    if (t2.equals(jCheckBox43.getText())) jCheckBox43.setSelected(true);
		    if (t2.equals(jCheckBox44.getText())) jCheckBox44.setSelected(true);
		    if (t2.equals(jCheckBox45.getText())) jCheckBox45.setSelected(true);
		    if (t2.equals(jCheckBox46.getText())) jCheckBox46.setSelected(true);
		    if (t2.equals(jCheckBox47.getText())) jCheckBox47.setSelected(true);
		    if (t2.equals(jCheckBox48.getText())) jCheckBox48.setSelected(true);
		    if (t2.equals(jCheckBox49.getText())) jCheckBox49.setSelected(true);
		    if (t2.equals(jCheckBox50.getText())) jCheckBox50.setSelected(true);
		    if (t2.equals(jCheckBox51.getText())) jCheckBox51.setSelected(true);
	    
		}
	    }
	    
	}
	
	
	
	
	
	/*
	    line = line + " protocol=" + jComboBox21.getSelectedItem().toString() + " ";
	    String flags = "";
	    
	    
	    if (jCheckBox43.isSelected()) flags = flags + jCheckBox43.getText() + ",";
	    if (jCheckBox44.isSelected()) flags = flags + jCheckBox44.getText() + ",";
	    if (jCheckBox45.isSelected()) flags = flags + jCheckBox45.getText() + ",";
	    if (jCheckBox46.isSelected()) flags = flags + jCheckBox46.getText() + ",";
	    if (jCheckBox47.isSelected()) flags = flags + jCheckBox47.getText() + ",";
	    if (jCheckBox48.isSelected()) flags = flags + jCheckBox48.getText() + ",";
	    if (jCheckBox49.isSelected()) flags = flags + jCheckBox49.getText() + ",";
	    if (jCheckBox40.isSelected()) flags = flags + jCheckBox40.getText() + ",";
	    if (jCheckBox51.isSelected()) flags = flags + jCheckBox51.getText() + ",";
	  
	    
	    
	    if (flags.length() > 0) line = line + " flags=" + flags.substring(0, flags.length()-1) + "	";
	    
	    if (jTextField95.getText().length() > 0) line = line + " prioritet=" + jTextField95.getText() + " ";
	    if (jTextField96.getText().length() > 0) line = line + " ports=" + jTextField96.getText() + " ";	 
	 */	
    }
    
    if (node.getParent().toString().equals("error_map")){

	currentDefaultName = node.toString();
	currentDefaultGroupName = node.getParent().toString();
	
	jTree1.setComponentPopupMenu(popup_dev_obj_item);
    
    }
    

    if (node.getParent().toString().equals("wccp2_service")){

	currentDefaultName = node.toString();
	currentDefaultGroupName = node.getParent().toString();
	
	jTree1.setComponentPopupMenu(popup_dev_obj_item);

	jTabbedPane1.addTab(currentDefaultGroupName + " " + currentDefaultName,
		wccp2_service_page);

	String value = squidConf.getDefaultObjectValue(currentDefaultGroupName, currentDefaultName);
	jComboBox20.setSelectedItem(null);
	jTextField93.setText("");
	jTextField94.setText("");



	
	
	StringTokenizer st = new StringTokenizer(currentDefaultName);
	if (st.hasMoreTokens()) jComboBox20.setSelectedItem(st.nextToken());	
	if (st.hasMoreTokens())jTextField93.setText(st.nextToken());
	//JOptionPane.showMessageDialog(mainPanel, value);
//	if (value.indexOf("password=") > -1 ) jTextField94.setText(value.split("=")[1]);
    }


    if (node.toString().equals("refresh_pattern")){
        jTree1.setComponentPopupMenu(popup_RefreshPattern);
    }



    if (node.getParent().toString().equals("refresh_pattern")){
        jTree1.setComponentPopupMenu(popup_RefreshPattern);

       // more
        current_refresh_pattern = node.toString();
        jTabbedPane1.addTab("refresh_pattern " + current_refresh_pattern, refresh_pattern_page);

        jCheckBox35.setSelected(false);
        jCheckBox36.setSelected(false);
        jCheckBox37.setSelected(false);
        jCheckBox38.setSelected(false);
        jCheckBox39.setSelected(false);
        jCheckBox40.setSelected(false);
        jCheckBox41.setSelected(false);
        jCheckBox42.setSelected(false);

        jTextField90.setText("");
        jTextField91.setText("");

        int rix = squidConf.getrefreshPatternIndex(current_refresh_pattern);
        if (rix == -1) return;

        StringTokenizer st = new StringTokenizer(
                squidConf.refresh_pattern.get(rix).value
                );

        String _i = st.nextToken();
        if (_i.equals("-i")) {
            jCheckBox35.setSelected(true);
            jTextField90.setText(st.nextToken());
            jSlider3.setValue(
                    Integer.parseInt(
                            st.nextToken().split("%")[0]
                        )
                    );
            jTextField91.setText(st.nextToken());

        }
        else {
            jTextField90.setText(_i);
            jSlider3.setValue(
                    Integer.parseInt(
                            st.nextToken().split("%")[0]
                        )
                    );
            jTextField91.setText(st.nextToken());
        }

        while (st.hasMoreTokens()){
            String s = st.nextToken();

            if (s.equals("override-expire"))jCheckBox36.setSelected(true);
            if (s.equals("ignore-reload"))jCheckBox37.setSelected(true);
            if (s.equals("reload-into-ims"))jCheckBox38.setSelected(true);
            if (s.equals("override-lastmod"))jCheckBox39.setSelected(true);
            if (s.equals("ignore-no-cache"))jCheckBox40.setSelected(true);
            if (s.equals("ignore-private"))jCheckBox41.setSelected(true);
            if (s.equals("ignore-auth"))jCheckBox42.setSelected(true);
        }


        /*

        String line = "refresh_pattern " + current_refresh_pattern + " ";
        if (jCheckBox35.isSelected()) line = line + " -i ";
        line = line + jTextField90.getText() + " " +
                jSlider3.getValue() + "% " +
                jTextField91.getText();

        if (jCheckBox36.isSelected()) line = line + " ";
        if (jCheckBox37.isSelected()) line = line + " ";
        if (jCheckBox38.isSelected()) line = line + " ";
        if (jCheckBox39.isSelected()) line = line + " ";
        if (jCheckBox40.isSelected()) line = line + " ";
        if (jCheckBox41.isSelected()) line = line + " ";
        if (jCheckBox42.isSelected()) line = line + " ";
        */

    }


    if (node.toString().equals("access_log")){
        jTree1.setComponentPopupMenu(popup_AccessLog);
    }

    if (node.getParent().toString().equals("access_log")){
        jTree1.setComponentPopupMenu(popup_AccessLog);
        current_AccessLog = node.toString();
        jTabbedPane1.addTab("access_log " + current_AccessLog, access_log_page);

        jComboBox15.removeAllItems();
        jComboBox15.addItem("");
        for (int i = 0; i < squidConf.logformat.size(); i++) {
            jComboBox15.addItem(squidConf.logformat.get(i).name);

        }

        jComboBox16.removeAllItems(); jComboBox16.addItem("");
        jComboBox17.removeAllItems(); jComboBox17.addItem("");
        jComboBox18.removeAllItems(); jComboBox18.addItem("");
        jComboBox19.removeAllItems(); jComboBox19.addItem("");

        for (int i= 0; i < squidConf.acl.size(); i++){
            jComboBox16.addItem(squidConf.acl.get(i).name);
            jComboBox17.addItem(squidConf.acl.get(i).name);
            jComboBox18.addItem(squidConf.acl.get(i).name);
            jComboBox19.addItem(squidConf.acl.get(i).name);

            jComboBox16.addItem("!"+squidConf.acl.get(i).name);
            jComboBox17.addItem("!"+squidConf.acl.get(i).name);
            jComboBox18.addItem("!"+squidConf.acl.get(i).name);
            jComboBox19.addItem("!"+squidConf.acl.get(i).name);

        }
        jComboBox15.setSelectedItem(null);
        jComboBox16.setSelectedItem(null);
        jComboBox17.setSelectedItem(null);
        jComboBox18.setSelectedItem(null);
        jComboBox19.setSelectedItem(null);

        int accix = squidConf.getAccessLogIndex(current_AccessLog);

        if (accix == -1) return;

        StringTokenizer st = new StringTokenizer(squidConf.access_log.get(accix).value);

        if (st.hasMoreTokens()) {
            String t = st.nextToken();
            if (squidConf.getLogFormatIndex(t) >= 0){
                jComboBox15.setSelectedItem(t);
                if (st.hasMoreTokens()) jComboBox16.setSelectedItem(st.nextToken());
                if (st.hasMoreTokens()) jComboBox17.setSelectedItem(st.nextToken());
                if (st.hasMoreTokens()) jComboBox18.setSelectedItem(st.nextToken());
                if (st.hasMoreTokens()) jComboBox19.setSelectedItem(st.nextToken());
            }
            else {
                if (st.hasMoreTokens()) jComboBox16.setSelectedItem(st.nextToken());
                if (st.hasMoreTokens()) jComboBox17.setSelectedItem(st.nextToken());
                if (st.hasMoreTokens()) jComboBox18.setSelectedItem(st.nextToken());
                if (st.hasMoreTokens()) jComboBox19.setSelectedItem(st.nextToken());

            }

        }





    }





    if (node.toString().equals("logformat")){
        jTree1.setComponentPopupMenu(popup_logformat);
    }

    if (node.getParent().toString().equals("logformat")) {
        jTree1.setComponentPopupMenu(popup_logformat);
        current_LogFormat = node.toString();
         jTabbedPane1.addTab("logformat " + current_LogFormat, logformat_page);
         jTextField89.setText(squidConf.logformat.get(squidConf.getLogFormatIndex(current_LogFormat)).value);
    }

    /*auth_params*/

    if (node.getParent().toString().equals("basic") |
        node.getParent().toString().equals("ntlm")  |
        node.getParent().toString().equals("negotiate") |
        node.getParent().toString().equals("digest")
            ){
        currentAuthParam = "auth_param " + node.getParent().toString() + " " +
                node.toString();
        currentParamName = currentAuthParam;
    }

    if (node.toString().equals("sslproxy_flags")){
        currentParamName = "sslproxy_flags";

        jTabbedPane1.addTab("sslproxy_flags", sslproxy_flags_page);
        jCheckBox29.setSelected(false);
        jCheckBox30.setSelected(false);
        jCheckBox31.setSelected(false);
        jCheckBox32.setSelected(false);
        jCheckBox33.setSelected(false);


        if (squidConf.onceParams.getProperty("sslproxy_flags") == null) return;

        String val = squidConf.onceParams.getProperty("sslproxy_flags");
        StringTokenizer st = new StringTokenizer(val);
        /*
DONT_VERIFY_PEER	 Accept certificates even if they fail to verify
NO_DEFAULT_CA	 Don't use the default CA list built in to OpenSSL
NO_SESSION_REUSE	 Don't allow for session reuse. Each connection will result in a new SSL session.
VERIFY_CRL	 Verify CRL lists when accepting client certificates
VERIFY_CRL_ALL	 Verify CRL lists for all certificates in the client certificate chain
         */
        while (st.hasMoreTokens()){
            String v = st.nextToken();
            if (v.equals("DONT_VERIFY_PEER") ) jCheckBox29.setSelected(true);
            if (v.equals("NO_DEFAULT_CA") ) jCheckBox30.setSelected(true);
            if (v.equals("NO_SESSION_REUSE") ) jCheckBox31.setSelected(true);
            if (v.equals("VERIFY_CRL") ) jCheckBox32.setSelected(true);
            if (v.equals("VERIFY_CRL_ALL") ) jCheckBox33.setSelected(true);


        }
    }

    if (node.toString().equals("store_dir_select_algorithm")){
        currentParamName = node.toString();
        jTabbedPane1.addTab(node.toString(), store_alg_page);

        jComboBox14.setSelectedItem(null);

        if (squidConf.getOnce(currentParamName) == null) return;
        jComboBox14.setSelectedItem(squidConf.getOnce(currentParamName));

    }
    
    if (node.toString().equals("uri_whitespace")){
        jTabbedPane1.addTab(node.toString(), uri_page);
	jComboBox23.setSelectedItem(null);
	
	currentParamName = "uri_whitespace";
	
	if (squidConf.getOnce("uri_whitespace") == null) return;
	jComboBox23.setSelectedItem(squidConf.getOnce("uri_whitespace"));
	
    }

    if (node.toString().equals("cache_dir")){
        jTabbedPane1.addTab(node.toString(), cache_dir);
        JTextField[] arr = {
            jTextField72,
            jTextField73,
            jTextField74,
            jTextField75,
            jTextField76,
            jTextField77,
            jTextField78,
            jTextField79,
            jTextField80,
            jTextField81,
            jTextField82,
            jTextField83,
            jTextField84,
            jTextField85,
            jTextField86,
            jTextField87,
            jTextField88
        };
        eraseTextFields(arr);
        jSlider2.setValue(0);
        jCheckBox34.setSelected(false);
        jTabbedPane2.setSelectedIndex(0);

        //if (squ)

        String val = squidConf.getOnce("cache_dir");

        if (val == null) return;

        //val = "coss block-size=8 overwrite-percent=67 max-stripe-waste=45 membufs=34 maxfullbufs=765 read-only max-size=345 min-size=4";

        StringTokenizer st = new StringTokenizer(val);


        String type = st.nextToken();
        if (type.equals("ufs")){

            if (st.hasMoreTokens())     jTextField73.setText(st.nextToken());
            if (st.hasMoreTokens())     jTextField74.setText(st.nextToken());
            if (st.hasMoreTokens())     jTextField75.setText(st.nextToken());

            while (st.hasMoreTokens()){
                String o = st.nextToken();
                if (o.equals("read-only")) jCheckBox34.setSelected(true);
                if (o.indexOf("min-size=") > -1) jTextField72.setText(o.split("=")[1]);
                if (o.indexOf("max-size=") > -1) jTextField88.setText(o.split("=")[1]);
            }

        }

         if (type.equals("aufs")){
            jTabbedPane2.setSelectedIndex(1);
            if (st.hasMoreTokens())     jTextField78.setText(st.nextToken());
            if (st.hasMoreTokens())     jTextField77.setText(st.nextToken());
            if (st.hasMoreTokens())     jTextField76.setText(st.nextToken());

            while (st.hasMoreTokens()){
                String o = st.nextToken();
                if (o.equals("read-only")) jCheckBox34.setSelected(true);
                if (o.indexOf("min-size=") > -1) jTextField72.setText(o.split("=")[1]);
                if (o.indexOf("max-size=") > -1) jTextField88.setText(o.split("=")[1]);

            }



        }

         if (type.equals("diskd")){
            jTabbedPane2.setSelectedIndex(2);
            if (st.hasMoreTokens())     jTextField81.setText(st.nextToken());
            if (st.hasMoreTokens())     jTextField80.setText(st.nextToken());
            if (st.hasMoreTokens())     jTextField79.setText(st.nextToken());

            while (st.hasMoreTokens()){
                String o = st.nextToken();
                if (o.equals("read-only")) jCheckBox34.setSelected(true);
                if (o.indexOf("min-size=") > -1) jTextField72.setText(o.split("=")[1]);
                if (o.indexOf("max-size=") > -1) jTextField88.setText(o.split("=")[1]);
                if (o.indexOf("Q1=") > -1) jTextField83.setText(o.split("=")[1]);
                if (o.indexOf("Q2=") > -1) jTextField82.setText(o.split("=")[1]);

            }
        }

         if (type.equals("coss"))   {
            jTabbedPane2.setSelectedIndex(3);
            while (st.hasMoreTokens()){
                String o = st.nextToken();
                if (o.equals("read-only")) jCheckBox34.setSelected(true);
                if (o.indexOf("min-size=") > -1) jTextField72.setText(o.split("=")[1]);
                if (o.indexOf("max-size=") > -1) jTextField88.setText(o.split("=")[1]);
                if (o.indexOf("block-size=") > -1) jTextField84.setText(o.split("=")[1]);
                if (o.indexOf("overwrite-percent=") > -1)
                    if (o.split("=")[1].length()>=0)
                        jSlider2.setValue(Integer.parseInt(o.split("=")[1]));

                if (o.indexOf("max-stripe-waste=") > -1) jTextField85.setText(o.split("=")[1]);
                if (o.indexOf("membufs=") > -1) jTextField86.setText(o.split("=")[1]);
                if (o.indexOf("maxfullbufs=") > -1) jTextField87.setText(o.split("=")[1]);



            }
         }




    }

    if (node.toString().equals("cache_replacement_policy") | node.toString().equals("memory_replacement_policy")){
        currentParamName = node.toString();

        jTabbedPane1.addTab(node.toString(), replacement_policy);
        jComboBox13.setSelectedItem(squidConf.getOnce(currentParamName));
    }

    if (node.toString().equals("tcp_outgoing_address")){
        jTree1.setComponentPopupMenu(popup_tcp_outgoing_addr);

    }

    if (node.toString().equals("tcp_outgoing_tos")){
        jTree1.setComponentPopupMenu(popup_tcpout_tos);

    }

    if (node.getParent().toString().equals("tcp_outgoing_tos")){
        jTabbedPane1.addTab("tcp_outgoing_tos " + node.toString(), tcp_out_tos);
    current_tcp_outgoing_tos = node.toString();

        JComboBox combo = new JComboBox();
        combo.addItem("");

        for (int i = 0; i < squidConf.acl.size(); i++) {
            combo.addItem(
                squidConf.acl.get(i).name
                    );
            combo.addItem("!" +
                squidConf.acl.get(i).name
                    );
        }

        for (int i = 0; i < jTable1.getRowCount(); i++){
            jTable5.setValueAt("", i, 0);
        }

        jTable5.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(combo));
        jLabel76.setText(current_tcp_outgoing_tos);

        DefaultSquidObject tcp = squidConf.tcp_out_tos.get(
                    squidConf.getTcpOutTosIndex(current_tcp_outgoing_tos)
                );

        StringTokenizer st = new StringTokenizer(tcp.value);
        int counter = 0;
        while (st.hasMoreTokens()){
            jTable5.setValueAt(st.nextToken(), counter, 0);
            counter++;
        }

    }

    if (node.getParent().toString().equals("tcp_outgoing_address")){
        jTabbedPane1.addTab("tcp_outgoing_address " + node.toString(), tcp_out_addr_page);

        current_tcp_outgoing_addr = node.toString();

        JComboBox combo = new JComboBox();
        combo.addItem("");

        for (int i = 0; i < squidConf.acl.size(); i++) {
            combo.addItem(
                squidConf.acl.get(i).name
                    );
            combo.addItem("!" +
                squidConf.acl.get(i).name
                    );
        }

        for (int i = 0; i < jTable1.getRowCount(); i++){
            jTable1.setValueAt("", i, 0);
        }

        jTable1.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(combo));
        jLabel75.setText(current_tcp_outgoing_addr);

        DefaultSquidObject tcp = squidConf.tcp_out_addr.get(
                    squidConf.getTcpOutAddrIndex(current_tcp_outgoing_addr)
                );

        StringTokenizer st = new StringTokenizer(tcp.value);
        int counter = 0;
        while (st.hasMoreTokens()){
            jTable1.setValueAt(st.nextToken(), counter, 0);
            counter++;
        }

    }

    if (node.toString().equals("external_acl_type") ){
        jTree1.setComponentPopupMenu(external_acl_popup);
    }

    if (node.getParent().toString().equals("external_acl_type") ){
        current_external_acl_type = node.toString();
        jTabbedPane1.addTab("external_acl_type " + current_external_acl_type, external_acl_page);



        int externalIndex = squidConf.getExternalAclTypeIndex(current_external_acl_type);
        if (externalIndex == -1) return;

        SquidExternalAclType eat = squidConf.external_acl_types.get(externalIndex);
        jTextField53.setText(current_external_acl_type);

        jTextField45.setText("");
        jTextField46.setText("");
        jTextField47.setText("");
        jTextField48.setText("");
        jTextField49.setText("");
        jTextField50.setText("");
        jTextField51.setText("");
        jTextField52.setText("");
        jCheckBox25.setSelected(false);
        StringTokenizer st = new StringTokenizer(eat.value);
        String Format = "", helper = "";
        Boolean format_ended = false, format_sterted = false;

        while (st.hasMoreTokens()){
            String token = st.nextToken();
            if (token.indexOf("ttl=") >= 0) jTextField45.setText(token.split("=")[1]);
            if (token.indexOf("negative_ttl=") >= 0) jTextField46.setText(token.split("=")[1]);
            if (token.indexOf("children=") >= 0) jTextField47.setText(token.split("=")[1]);
            if (token.indexOf("concurrency=") >= 0) jTextField48.setText(token.split("=")[1]);
            if (token.indexOf("cache=") >= 0) jTextField49.setText(token.split("=")[1]);
            if (token.indexOf("grace=") >= 0) jTextField50.setText(token.split("=")[1]);
            if (token.indexOf("%") >= 0) {
                Format = Format + token + " ";
                format_sterted = true;
            }

            if (format_sterted & token.indexOf("%") < 0) format_ended = true;

            if (format_ended) helper = helper + token + " ";

        }
        jTextField51.setText(Format);
        jTextField52.setText(helper);



    }


    if (currentParamName.equals("cache_peer")){
        jTree1.setComponentPopupMenu(cachepeer_popup);
    }

    if (node.getParent().toString().equals("cache_peer")){
        jTabbedPane1.addTab("cache_peer " + currentParamName, cache_peer_page);
        currentCachePeerName = node.toString();
        current_AccessName = "cache_peer_access " + currentCachePeerName;

        jTabbedPane1.addTab(current_AccessName, access_page);

        currentParamName = "cache_peer_domain " + node.toString();
        jTabbedPane1.addTab(currentParamName, once_str);
        jTextField22.setText(squidConf.getOnce(currentParamName));

        jTabbedPane1.addTab("neighbor_type_domain", neighbor_page);
        jComboBox12.setSelectedItem(null);
        jTextField62.setText("");
        if (squidConf.getOnce("neighbor_type_domain " + node.toString()) != null){
            StringTokenizer st = new StringTokenizer(squidConf.getOnce("neighbor_type_domain " + node.toString()));

            jComboBox12.setSelectedItem(st.nextToken());
            String v = "";
            while (st.hasMoreTokens()){
                v = v + st.nextToken() + " ";
            }
            jTextField62.setText(v);

        }




       /*access start*/
        jTree1.setComponentPopupMenu(popup_access);

        JComboBox combo1 = new JComboBox();
        JComboBox combo2 = new JComboBox();
        combo2.addItem("deny");
        combo2.addItem("allow");



        combo1.removeAllItems();
        combo1.addItem("");
        for (int i = 0; i < squidConf.acl.size(); i++) {
            SquidAcl sa = (SquidAcl) squidConf.acl.get(i);
            combo1.addItem(sa.name);
            combo1.addItem("!"  + sa.name);

        }
        jTable2.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(combo2));
        jTable2.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(combo1));
        jTable2.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(combo1));
        jTable2.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(combo1));

        DefaultTableModel dt = (DefaultTableModel) jTable2.getModel();
        dt.setRowCount(0);

        System.out.println("squidConf.getAccessGroupIndex("+ current_AccessName + ")");

        if (squidConf.getAccessGroupIndex(current_AccessName) >= 0){
            SquidAccessVector sav = (SquidAccessVector) squidConf.access.get(squidConf.getAccessGroupIndex(current_AccessName));
            for (int i = 0; i < sav.size(); i++) {
                SquidAccess sa = (SquidAccess) sav.get(i);
                StringTokenizer st = new StringTokenizer(sa.value);
                System.out.println(sa.value);
                /*******/
                String[] row = {sa.action, "", "", ""};
                if (st.hasMoreTokens())row[1] = st.nextToken();
                if (st.hasMoreTokens())row[2] = st.nextToken();
                if (st.hasMoreTokens())row[3] = st.nextToken();
                dt.addRow(row);
            }
        }

        /*access end*/




        int cache_index = squidConf.getCachePeerIndex(currentCachePeerName);

        if (cache_index == -1){
            System.out.println("Internal error: cache_peer " + currentCachePeerName + " not found");
            return;
        }

        SquidCachePeer sc = squidConf.cache_peers.get(cache_index);
        StringTokenizer st = new StringTokenizer(sc.values);

        jTextField2.setText(sc.hostname);
        jComboBox7.setSelectedItem(st.nextToken().toString());
        jTextField3.setText(st.nextToken());
        jTextField4.setText(st.nextToken());

        jCheckBox1.setSelected(false);
        jCheckBox2.setSelected(false);
        jCheckBox3.setSelected(false);
        jCheckBox4.setSelected(false);
        jCheckBox5.setSelected(false);
        jCheckBox6.setSelected(false);
        jCheckBox7.setSelected(false);
        jCheckBox15.setSelected(false);
        jCheckBox24.setSelected(false);
        jCheckBox16.setSelected(false);
        jCheckBox17.setSelected(false);
        jCheckBox18.setSelected(false);
        jCheckBox19.setSelected(false);
        jCheckBox20.setSelected(false);
        jCheckBox21.setSelected(false);
        jCheckBox22.setSelected(false);
        jCheckBox23.setSelected(false);

        jTextField8.setText("");
        jTextField5.setText("");
        jTextField6.setText("");
        jTextField7.setText("");
        jTextField9.setText("");
        jTextField10.setText("");
        jTextField11.setText("");
        jTextField12.setText("");
        jTextField13.setText("");
        jTextField14.setText("");
        jTextField15.setText("");
        jTextField16.setText("");
        jTextField17.setText("");
        jTextField37.setText("");
        jTextField38.setText("");

        jComboBox8.setSelectedItem(null);
        jComboBox9.setSelectedItem(null);
        jComboBox10.setSelectedItem(null);

        while (st.hasMoreTokens()){
            String next = st.nextToken();
            if (next.equals("proxy-only"))          jCheckBox1.setSelected(true);
            if (next.equals("no-query"))            jCheckBox2.setSelected(true);
            if (next.equals("default"))             jCheckBox3.setSelected(true);
            if (next.equals("round-robin"))         jCheckBox4.setSelected(true);
            if (next.equals("carp"))                jCheckBox5.setSelected(true);
            if (next.equals("multicast-responder")) jCheckBox6.setSelected(true);
            if (next.equals("closest-only"))        jCheckBox7.setSelected(true);
            if (next.equals("no-digest"))           jCheckBox15.setSelected(true);
            if (next.equals("ssl"))                 jCheckBox24.setSelected(true);
            if (next.equals("no-netdb-exchange"))   jCheckBox16.setSelected(true);
            if (next.equals("no-delay"))            jCheckBox17.setSelected(true);
            if (next.equals("allow-miss"))          jCheckBox18.setSelected(true);
            if (next.equals("htcp"))                jCheckBox19.setSelected(true);
            if (next.equals("htcp-oldsquid"))       jCheckBox20.setSelected(true);
            if (next.equals("origin-server"))       jCheckBox21.setSelected(true);
            if (next.equals("userhash"))            jCheckBox22.setSelected(true);
            if (next.equals("sourcehash"))          jCheckBox23.setSelected(true);

            if (next.indexOf("sslcert=")>=0) jTextField8.setText(next.split("=")[1]);
            if (next.indexOf("sslkey=")>=0) jTextField5.setText(next.split("=")[1]);
            if (next.indexOf("sslversion=")>=0) jTextField6.setText(next.split("=")[1]);
            if (next.indexOf("ssloptions=")>=0) jTextField7.setText(next.split("=")[1]);
            if (next.indexOf("weight=")>=0) jTextField9.setText(next.split("=")[1]);
            if (next.indexOf("ttl=")>=0) jTextField10.setText(next.split("=")[1]);
            if (next.indexOf("login=")>=0) jTextField11.setText(next.split("=")[1]);
            if (next.indexOf("connect-timeout=")>=0) jTextField12.setText(next.split("=")[1]);
            if (next.indexOf("digest-url=")>=0) jTextField13.setText(next.split("=")[1]);
            if (next.indexOf("max-conn=")>=0) jTextField14.setText(next.split("=")[1]);
            if (next.indexOf("monitorurl=")>=0) jTextField15.setText(next.split("=")[1]);
            if (next.indexOf("monitorsize=")>=0) jTextField16.setText(next.split("=")[1]);
            if (next.indexOf("monitorinterval=")>=0) jTextField17.setText(next.split("=")[1]);
            if (next.indexOf("monitortimeout=")>=0) jTextField37.setText(next.split("=")[1]);
            if (next.indexOf("forcedomain=")>=0) jTextField38.setText(next.split("=")[1]);

            if (next.indexOf("sslcipher=")>=0) jComboBox8.setSelectedItem(next.split("=")[1].toString());
            if (next.indexOf("front-end-https=")>=0) jComboBox10.setSelectedItem(next.split("=")[1].toString());
            if (next.indexOf("connection-auth=")>=0) jComboBox9.setSelectedItem(next.split("=")[1].toString());

       }


    }

    if (node.toString().equals("header_access")){
	jTree1.setComponentPopupMenu(popup_header_acc);
    }

    /*Access 19*/
    if ( squidConf.getParamTypeFromName(currentParamName).equals("access") |
            node.getParent().toString().equals("header_access")

            ){

        current_AccessName = node.toString();

        if (node.getParent().toString().equals("header_access")){
	    jTree1.setComponentPopupMenu(popup_header_acc);

            current_AccessName = "header_access " + node.toString();

	}


        jTabbedPane1.addTab(current_AccessName, access_page);

        jTree1.setComponentPopupMenu(popup_access);

        JComboBox combo1 = new JComboBox();
        JComboBox combo2 = new JComboBox();
        combo2.addItem("deny");
        combo2.addItem("allow");



        combo1.removeAllItems();
        combo1.addItem("");
        for (int i = 0; i < squidConf.acl.size(); i++) {
            SquidAcl sa = (SquidAcl) squidConf.acl.get(i);
            combo1.addItem(sa.name);
            combo1.addItem("!"  + sa.name);

        }
        jTable2.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(combo2));
        jTable2.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(combo1));
        jTable2.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(combo1));
        jTable2.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(combo1));

        DefaultTableModel dt = (DefaultTableModel) jTable2.getModel();
        dt.setRowCount(0);


        if (squidConf.getAccessGroupIndex(current_AccessName) >= 0){
            SquidAccessVector sav = (SquidAccessVector) squidConf.access.get(squidConf.getAccessGroupIndex(current_AccessName));
            for (int i = 0; i < sav.size(); i++) {
                SquidAccess sa = (SquidAccess) sav.get(i);
                StringTokenizer st = new StringTokenizer(sa.value);
                System.out.println(sa.value);
                /*******/
                String[] row = {sa.action, "", "", ""};
                if (st.hasMoreTokens())row[1] = st.nextToken();
                if (st.hasMoreTokens())row[2] = st.nextToken();
                if (st.hasMoreTokens())row[3] = st.nextToken();
                dt.addRow(row);
            }
        }

    }


    if ( squidConf.getParamTypeFromName(currentParamName).equals("once.bool") |
         squidConf.getParamTypeFromName(currentParamName).equals("auth.bool")
            ){

        jComboBox2.setSelectedItem(squidConf.getOnce(currentParamName));
       // loadResourceHTML(jEditorPane1, "resources/html/squidconf/" + currentParamName +".html");
        jTabbedPane1.addTab(currentParamName, once_bool);
        jTree1.setComponentPopupMenu(once_param_popup);

    }

    if ( squidConf.getParamTypeFromName(currentParamName).equals("once.size")){

        jTextField20.setText(null);
        jComboBox3.setSelectedItem(null);

        if (squidConf.getOnce(currentParamName) != null) {

              StringTokenizer st = new StringTokenizer(squidConf.getOnce(currentParamName).toString());
              jTextField20.setText(st.nextToken());
              if (st.hasMoreTokens()){
                  jComboBox3.setSelectedItem(st.nextToken());
              }

        }

        jTabbedPane1.addTab(currentParamName, once_size);
        jTree1.setComponentPopupMenu(once_param_popup);
    }

    if ( squidConf.getParamTypeFromName(currentParamName).equals("once.time") |
         squidConf.getParamTypeFromName(currentParamName).equals("auth.time")
            ){

        jTextField27.setText(null);
        jComboBox1.setSelectedItem(null);

        if (squidConf.getOnce(currentParamName) != null) {

            jComboBox1.setSelectedItem(squidConf.getOnce(currentParamName));

            if (jComboBox1.getSelectedItem() == null) jTextField27.setText(squidConf.getOnce(currentParamName));

            /*  StringTokenizer st = new StringTokenizer(squidConf.getOnce(currentParamName).toString());
              jTextField20.setText(st.nextToken());
              if (st.hasMoreTokens()){
                  jComboBox3.setSelectedItem(st.nextToken());
              } */

        }

        jTabbedPane1.addTab(currentParamName, once_time);
        jTree1.setComponentPopupMenu(once_param_popup);
    }


    if (squidConf.getParamTypeFromName(currentParamName).equals("once.percent")){
       // jLabel7.setText(n);

        jSlider1.setValue(0);
       // System.out.println();
       // System.out.println(currentParamName+"=["+squidConf.getOnce(currentParamName)+"]");

        //Integer.
        if (squidConf.getOnce(currentParamName) != null)
        jSlider1.setValue(Integer.parseInt(squidConf.getOnce(currentParamName)));

      //  loadResourceHTML(jEditorPane3, "resources/html/squidconf/" + currentParamName +".html");
        jTabbedPane1.addTab(currentParamName, once_percent);
        jTree1.setComponentPopupMenu(once_param_popup);
        //jPanel1.add(once_percent);
    }

    if (squidConf.getParamTypeFromName(currentParamName).equals("once.str") |
        squidConf.getParamTypeFromName(currentParamName).equals("once.int") |
        squidConf.getParamTypeFromName(currentParamName).equals("once.ip") |
        squidConf.getParamTypeFromName(currentParamName).equals("auth.str") |
        squidConf.getParamTypeFromName(currentParamName).equals("auth.int") |
        squidConf.getParamTypeFromName(currentParamName).equals("once.file")){
        System.out.println("hope.str");
        jTextField22.setText(squidConf.getOnce(currentParamName));
     //   loadResourceHTML(jEditorPane2, "resources/html/squidconf/" + currentParamName +".html");
        jTabbedPane1.addTab(currentParamName, once_str);
        jTree1.setComponentPopupMenu(once_param_popup);

       // jPanel1.add(once_str);
    }

    if (currentParamName.equals("https_port")){

        jTabbedPane1.addTab(currentParamName, https_port_page);
        JTextField[] arr = {
            jTextField54,
            jTextField55,
            jTextField56,
            jTextField57,
            jTextField58,
            jTextField59,
            jTextField60,
            jTextField61,
            jTextField63,
            jTextField64,
            jTextField65,
            jTextField66,
            jTextField67,
            jTextField68,
            jTextField69,
            jTextField70,
            jTextField71,
            jTextField71
        };
        eraseTextFields(arr);

        jCheckBox26.setSelected(false);
        jCheckBox27.setSelected(false);
        jCheckBox28.setSelected(false);
        jComboBox11.setSelectedItem(null);


        String line = squidConf.onceParams.getProperty("https_port");
        if (line == null) return;

        StringTokenizer st = new StringTokenizer(line);
        st.nextToken();

        String port = st.nextToken();
        if (port.split(":").length == 1) jTextField55.setText(port);
        if (port.split(":").length == 2) {
            jTextField54.setText(port.split(":")[0]);
            jTextField55.setText(port.split(":")[1]);
        }

        while (st.hasMoreTokens()){
            String token = st.nextToken();
            if (token.equals("accel")) jCheckBox26.setSelected(true);
            if (token.equals("vhost")) jCheckBox27.setSelected(true);

            if (token.indexOf("vport") >= 0) jCheckBox28.setSelected(true);
            if (token.indexOf("vport=") >= 0) jTextField56.setText(token.split("=")[1]);

            if (token.indexOf("defaultsite=") >= 0) jTextField57.setText(token.split("=")[1]);
            if (token.indexOf("urlgroup=") >= 0) jTextField58.setText(token.split("=")[1]);
            if (token.indexOf("protocol=") >= 0) jTextField59.setText(token.split("=")[1]);
            if (token.indexOf("cert=") >= 0) jTextField60.setText(token.split("=")[1]);
            if (token.indexOf("key=") >= 0) jTextField61.setText(token.split("=")[1]);

            if (token.indexOf("version=") >= 0) jComboBox11.setSelectedItem(token.split("=")[1]);

            if (token.indexOf("cipher=") >= 0) jTextField63.setText(token.split("=")[1]);
            if (token.indexOf("options=") >= 0) jTextField64.setText(token.split("=")[1]);
            if (token.indexOf("clientca=") >= 0) jTextField65.setText(token.split("=")[1]);
            if (token.indexOf("cafile=") >= 0) jTextField66.setText(token.split("=")[1]);
            if (token.indexOf("capath=") >= 0) jTextField67.setText(token.split("=")[1]);
            if (token.indexOf("crlfile=") >= 0) jTextField68.setText(token.split("=")[1]);
            if (token.indexOf("dhparams=") >= 0) jTextField69.setText(token.split("=")[1]);
            if (token.indexOf("sslflags=") >= 0) jTextField70.setText(token.split("=")[1]);
            if (token.indexOf("sslcontext=") >= 0) jTextField71.setText(token.split("=")[1]);



        }


    }

    if (currentParamName.equals("http_port")){

        /*
         http_port localhost:3128 transaprent tproxy accell vhost vport[=2345] urlgroup=*** protocol=http no-connection-auth

         */
        jTextField18.setText("");
        jTextField19.setText("");

        jTextField23.setText("");
        jTextField24.setText("");
        jTextField25.setText("");
        jTextField26.setText("");

         jCheckBox8.setSelected(false);
         jCheckBox9.setSelected(false);
        jCheckBox10.setSelected(false);
        jCheckBox11.setSelected(false);
        jCheckBox12.setSelected(false);
        jCheckBox13.setSelected(false);






       // String http_port = "http_port localhost:3128 transaprent no-connection-auth tproxy accell vhost vport=2345 urlgroup=*** protocol=http";

        //http_port = "";

        if (squidConf.onceParams.containsKey(currentParamName)) {
            String http_port = squidConf.getOnce(currentParamName).toString();

            StringTokenizer shtt = new StringTokenizer(http_port);
           // shtt.nextToken();
            String s1 = shtt.nextToken();

            String[] s1_args = s1 .split(":");

            if (s1_args.length == 1) jTextField19.setText(s1_args[0]);
            if (s1_args.length == 2) {
                jTextField18.setText(s1_args[0]);
                jTextField19.setText(s1_args[1]);
            }



            //
            System.out.println("http_port_first: " + s1);

            while (shtt.hasMoreTokens()){

                //System.out.println();

                String token=shtt.nextToken();
                System.out.println("http_port_token: " + token);
                /*  tproxy  */
                if (token.equals("transparent")) jCheckBox8.setSelected(true);
                if (token.equals("no-connection-auth")) jCheckBox13.setSelected(true);
                if (token.equals("tproxy")) jCheckBox9.setSelected(true);
                if (token.equals("vhost")) jCheckBox11.setSelected(true);
                if (token.equals("accell")) jCheckBox10.setSelected(true);
                if (token.equals("vport")) jCheckBox10.setSelected(true);


                /*vport[=2345] urlgroup=*** protocol=http*/

                if (token.indexOf("vport=")==0){
                    jCheckBox12.setSelected(true);
                    jTextField23.setText(token.split("=")[1]);
                }

                 if (token.indexOf("urlgroup=")==0) jTextField25.setText(token.split("=")[1]);
                 if (token.indexOf("protocol=")==0) jTextField26.setText(token.split("=")[1]);



            }
        }//http_port.length() > 0
        ///JOptionPane.showMessageDialog(mainPanel, http_arg.toString());



        jTabbedPane1.addTab(currentParamName, once_httpport);

    }

    if (currentParamName.equals("acl")){
        jTree1.setToolTipText("<html>total acl: <b>"+squidConf.acl.size()+"</b>");
        jTextField28.setText("");
        jTabbedPane1.addTab("Access lists", acl_page);

       // jComboBox4.requestFocusInWindow();
       // jTree1.setToolTipText("<html>Total: <b>" + squidConf.acl.size() + "</b>");
    }



    if (node.getParent().toString().equals("acl")){
       // JOptionPane.showMessageDialog(jComboBox1, "This delay...");
        currentAcl = node.toString();
        Integer index = squidConf.getAclIndex(currentAcl);
        jTree1.setComponentPopupMenu(acl_item_menu);

        SquidAcl sa = (SquidAcl)squidConf.acl.get(index);

        if (sa.type.equals("time")){
            String[] week = {"M", "T", "W", "H", "F", "A", "S"};
            jTabbedPane1.addTab("acl/" + currentAcl, acl_time);
            //acl Always time M T W H F A S 0:00-23:59
            //acl Always time M T W H F A S 0:00-23:00
            jTable4.setCellSelectionEnabled(true);

            int start_day=-1, end_day=10, start_hour=0, end_hour=24, end_minutes=0;

            for (int i = 0; i < week.length-1; ++i){
                if (sa.value.indexOf(week[i]) >= 0) {
                    start_day = i;
                    break;
                }
            }

            for (int i = week.length-1; i >= 0; --i){
                if (sa.value.indexOf(week[i]) >= 0) {
                    end_day = i;
                    break;
                }
            }

            StringTokenizer st = new StringTokenizer(sa.value);
            //super("test");
            while (st.hasMoreTokens()){
                String s = st.nextToken();
                if (s.indexOf("-") >= 0){
                    //0:00-23:59    0:00-23:00
                    start_hour = Integer.parseInt(s.split("-")[0].split(":")[0]);
                      end_hour = Integer.parseInt(s.split("-")[1].split(":")[0]);
                      end_minutes = Integer.parseInt(s.split("-")[1].split(":")[1]);
                }
            }

            System.out.println("start_hour=" + start_hour + "- end hour="+end_hour);

            ListSelectionModel lm = jTable4.getSelectionModel();

            jTable4.selectAll();
            if (end_hour == 23 & end_minutes == 59) end_hour++;


            jTable4.setColumnSelectionInterval(start_hour + 1, end_hour );
            jTable4.setRowSelectionInterval(start_day, end_day);



        }
        else {

                DefaultTableModel tbl3 = (DefaultTableModel) jTable3.getModel();
                tbl3.setRowCount(0);

                StringTokenizer st = new StringTokenizer(sa.value);

                //if (st.hasMoreTokens())
                String ss = "";
                Boolean sel = false;
                while (st.hasMoreTokens()) {
                    String[] row = {""};
                    row[0] = st.nextToken();
                    if (row[0].equals("-i")){
                        sel = true;
                    }
                    else   {
                        tbl3.addRow(row);
                        ss = ss + row[0] + " ";
                    }
                }

                jCheckBox14.setEnabled(false);
                if (sa.type.indexOf("regex") >= 0) jCheckBox14.setEnabled(true);

                jCheckBox14.setSelected(sel);
                jTextField29.setText(ss);
                jLabel39.setText(sa.type);

                if (sa.type.equals("proxy_auth")){
                    squidConf.getUsers();
                }

                jTabbedPane1.addTab("acl/" + currentAcl, acl_common);
        }
        jTree1.setToolTipText("<html><h1>ACL</h1>index: <b>"+(index+1)+"</b><br>name: <b>" + sa.name + "</b><br>type: <b>" +
                sa.type  +"</b><br>value: " + sa.value);

     // jPanel1.add(delay_prop);
    }

    /*delays*/

    if (node.toString().equals("Delays")){
        jTabbedPane1.addTab("Delays", delay_page);
    }

    if (node.getParent().toString().equals("Delays")){
       // JOptionPane.showMessageDialog(jComboBox1, "This delay...");
       current_AccessName =  "delay_access " + node.toString();
       jTabbedPane1.addTab(currentParamName, access_page);
       curremtDelayNum = node.toString();


       SquidDelay sd = (SquidDelay) squidConf.delays.get(Integer.parseInt(node.toString())-1);
       jComboBox5.setSelectedItem(sd.classNum);
       jTextField31.setText(sd.aggregate_restore);
       jTextField32.setText(sd.aggregate_maximum);

       jTextField33.setText(sd.individual_restore);
       jTextField34.setText(sd.individual_maximum);

       jTextField35.setText(sd.network_restore);
       jTextField36.setText(sd.network_maximum);

       jTabbedPane1.addTab("delay_parameters", delay_prop);



        JComboBox combo1 = new JComboBox();
        JComboBox combo2 = new JComboBox();
        combo2.addItem("deny");
        combo2.addItem("allow");



        combo1.removeAllItems();
        combo1.addItem("");
        for (int i = 0; i < squidConf.acl.size(); i++) {
            SquidAcl sa = (SquidAcl) squidConf.acl.get(i);
            combo1.addItem(sa.name);
            combo1.addItem("!"  + sa.name);

        }
        jTable2.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(combo2));
        jTable2.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(combo1));
        jTable2.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(combo1));
        jTable2.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(combo1));

        DefaultTableModel dt = (DefaultTableModel) jTable2.getModel();
        dt.setRowCount(0);


        if (squidConf.getAccessGroupIndex(current_AccessName) >= 0){
            SquidAccessVector sav = (SquidAccessVector) squidConf.access.get(squidConf.getAccessGroupIndex(current_AccessName));
            for (int i = 0; i < sav.size(); i++) {
                SquidAccess sa = (SquidAccess) sav.get(i);
                StringTokenizer st = new StringTokenizer(sa.value);
                System.out.println(sa.value);
                /*******/
                String[] row = {sa.action, "", "", ""};
                if (st.hasMoreTokens())row[1] = st.nextToken();
                if (st.hasMoreTokens())row[2] = st.nextToken();
                if (st.hasMoreTokens())row[3] = st.nextToken();
                dt.addRow(row);
            }
        }


    }
    
    if (node.toString().equals("SSH")){
        jTree1.setComponentPopupMenu(popup_ssh);
    }
    
    if (node.getParent().toString().equals("SSH")){
        jTabbedPane1.addTab(node.toString(), ssh_page);
        currentSshConn = node.toString();
        jTree1.setComponentPopupMenu(popup_ssh_item);
        
        
          
                jTextField39.setText("");  
                jTextField43.setText("");
                jPasswordField1.setText("");
                jTextField40.setText("");
                jTextField41.setText("");
        
        
        StringTokenizer st = new StringTokenizer(
                    sshconn.get(currentSshConn).value
                );
                
        while (st.hasMoreTokens()){
            String tok = st.nextToken();
            if (tok.indexOf("address=") >= 0) 
                jTextField39.setText(tok.split("=")[1]);
            
            if (tok.indexOf("user=") >= 0) 
                jTextField43.setText(tok.split("=")[1]);
            
            if (tok.indexOf("password=") >= 0) 
                jPasswordField1.setText(encode(tok.split("=")[1]));
            
            if (tok.indexOf("port=") >= 0) 
                jTextField40.setText(tok.split("=")[1]);
            
            if (tok.indexOf("confpath=") >= 0) 
                jTextField41.setText(tok.split("=")[1]);
            
            if (tok.indexOf("exepath=") >= 0)                 
                jTextField42.setText(tok.split("=")[1]);
        }
    }






    //JOptionPane.showMessageDialog(mainPanel, jPanel1.getComponentCount());
    if (jTabbedPane1.getComponentCount() == 0)
    {
        System.out.print("add a welcome page...");
        jTabbedPane1.addTab(currentParamName, welcome);
        welcome.repaint();
    }

    jTabbedPane1.repaint();
    if (node == null) return;
}//GEN-LAST:event_jTree1ValueChanged

private void jSlider1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider1StateChanged
// TODO add your handling code here:
    jSlider1.setToolTipText("<html><h1>" + jSlider1.getValue() + "%");
}//GEN-LAST:event_jSlider1StateChanged

private void jTextField27KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField27KeyTyped
// TODO add your handling code here:
    jComboBox1.setSelectedItem(null);
}//GEN-LAST:event_jTextField27KeyTyped

private void jTable3FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTable3FocusLost
// TODO add your handling code here:
 //   JOptionPane.showMessageDialog(mainPanel, "focus LOST");

    DefaultTableModel dt = (DefaultTableModel) jTable3.getModel();



    jTextField29.setText("");
    String t = "";
    for (int i = 0; i < dt.getRowCount(); i++) {
        String value = (String) dt.getValueAt(i, 0);

        if (value != null){
            if (value.length() > 0){
        t = t + dt.getValueAt(i, 0) + " ";}

        }




    }

    for (int i = dt.getRowCount()-1; i>=0; i--) {
        String value = (String) dt.getValueAt(i, 0);

        if (value == null)
            dt.removeRow(i);

        else {
            if (value.length() == 0 | value.equals(" ")) dt.removeRow(i);
        }




    }
    t.trim();
    jTextField29.setText(t);
}//GEN-LAST:event_jTable3FocusLost

private void jComboBox4ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox4ItemStateChanged
// TODO add your handling code here:
   // jTextField28.requestFocusInWindow();
}//GEN-LAST:event_jComboBox4ItemStateChanged

private void jSlider2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider2StateChanged
// TODO add your handling code here:\
    jSlider2.setToolTipText("<html><h1>"  + jSlider2.getValue() + "%");
}//GEN-LAST:event_jSlider2StateChanged

private void jSlider3StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider3StateChanged
// TODO add your handling code here:
    jSlider3.setToolTipText("<html><h1>"+ jSlider3.getValue() + "%");
}//GEN-LAST:event_jSlider3StateChanged

private void jPasswordField2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPasswordField2KeyPressed
// TODO add your handling code here:
    jPasswordField2.setToolTipText(jPasswordField2.getText());
    
}//GEN-LAST:event_jPasswordField2KeyPressed



public void Log(String s){
    jTextPane1.setText(jTextPane1.getText() + "\n" + s);
    jTabbedPane1.addTab("Log", log_page);

    //jTextPane1.setText(jTextPane1.getText() +  s);
    
    jTabbedPane1.setSelectedIndex(jTabbedPane1.getTabCount()-1);
    jTabbedPane1.repaint();
}
    @Action
    public void showStatus(){
        String text = "<html>AdminDB: <b>" + AdminDbName + "/"+AdminDBStatus+"</b>, LogDB: <b>" + LogDbName + "/"+LogDBStatus+"</b>";
       // JOptionPane.showMessageDialog(mainPanel, text);
        jLabel2.setText(text);
        jLabel2.repaint();
    }





    @Action
    public void saveOnceStrParam() {
        if (jTextField22.getText().length() == 0) {
            JOptionPane.showMessageDialog(mainPanel, "You are must type a value of this option!");
            return;
        }
        squidConf.setOnce(currentParamName, jTextField22.getText());
        jTree1.repaint();
    }

    @Action
    public void testTest() throws JSchException {
        String praza = decode("abcdefABCDEF=123456789");
        JOptionPane.showMessageDialog(mainPanel, 
                "decode()=" + praza + "\n" +
                "encode=" + encode(praza)
                //runSSH("smrunix3", "root", "gfhjkm", 22, "tail /var/log/messages").getText()
                );

    }
    
    public TStrings runSSH(String host, String user, final String password, int port, String command) 
            {
        
       TStrings str = new TStrings();
       System.out.println("ssh: try connect to " + host + ":" + port + " and exec command: " + command);
        

       UserInfo ui = new UserInfo() {

            public String getPassphrase() {
                throw new UnsupportedOperationException("getPassphraseNot supported yet.");
            }

            public String getPassword() {
                return password;
            }

            public boolean promptPassword(String message) {
                return true;
            }

            public boolean promptPassphrase(String message) {
                throw new UnsupportedOperationException("promptPassphraseNot supported yet.");
            }

            public boolean promptYesNo(String message) {
                return true;
            }

            public void showMessage(String message) {
                throw new UnsupportedOperationException("showMessageNot supported yet.");
            }
        };
        try {
        JSch jsch=new JSch();
        Session session = jsch.getSession(user, host, port);
        session.setUserInfo(ui);
        session.connect();

        Channel channel=session.openChannel("exec");        
        ((ChannelExec)channel).setCommand(command);
        
        //channel.setInputStream
        try {
        channel.setInputStream(null);    
            
        InputStream in=channel.getInputStream();
        channel.connect();
        
       // TStrings  str = new TStrings();
        str.loadFromStream(in);
      //  JOptionPane.showMessageDialog(mainPanel, str.getText());
        
        
        }
        catch (IOException e) {
            str.clear();
            str.items.add("ERROR: " + e.getMessage());
            Log(e.getMessage());
           // JOptionPane.showMessageDialog(mainPanel, e.getMessage());
        }
        channel.disconnect();
        session.disconnect();        }
        catch (JSchException e) {
            str.clear();
            str.items.add("ERROR: " + e.getMessage());
            Log(e.getMessage());
        }
    
        return str;
    }

    @Action
    public void emptyOnceParam() {
    if (JOptionPane.showConfirmDialog(mainPanel, "Remove option ["+currentParamName+"]?") == 1) return;

        jTextField22.setText("");
        jTextField18.setText("");
        jTextField19.setText("");

        jTextField27.setText("");
        jComboBox1.setSelectedItem(null);


        jTextField18.setText("");
        jTextField19.setText("");
        jTextField20.setText("");

        jTextField23.setText("");
        jTextField24.setText("");
        jTextField25.setText("");
        jTextField26.setText("");



         jCheckBox8.setSelected(false);
         jCheckBox9.setSelected(false);
        jCheckBox10.setSelected(false);
        jCheckBox11.setSelected(false);
        jCheckBox12.setSelected(false);
        jCheckBox13.setSelected(false);



        squidConf.onceParams.remove(currentParamName);
        jComboBox2.setSelectedItem(null);
        jComboBox3.setSelectedItem(null);
        jComboBox13.setSelectedItem(null);
        jComboBox14.setSelectedItem(null);
	jComboBox23.setSelectedItem(null);

        jSlider1.setValue(0);

        jCheckBox29.setSelected(false);
        jCheckBox30.setSelected(false);
        jCheckBox31.setSelected(false);
        jCheckBox32.setSelected(false);
        jCheckBox33.setSelected(false);

        JTextField[] arr = {
            jTextField72,
            jTextField73,
            jTextField74,
            jTextField75,
            jTextField76,
            jTextField77,
            jTextField78,
            jTextField79,
            jTextField80,
            jTextField81,
            jTextField82,
            jTextField83,
            jTextField84,
            jTextField85,
            jTextField86,
            jTextField87,
            jTextField88
        };
        eraseTextFields(arr);
        jSlider2.setValue(0);
        jCheckBox34.setSelected(false);
        jTabbedPane2.setSelectedIndex(0);
    }

    @Action
    public void saveOnceBool() {
        squidConf.setOnce(currentParamName, jComboBox2.getSelectedItem().toString());
       jTree1.repaint();
    }

    @Action
    public void saveOncePercent() {
        //(busyIconIndex)
        squidConf.setOnce(currentParamName, String.valueOf(jSlider1.getValue()));
        jTree1.repaint();
    }

    @Action
    public void saveOnceSize() {
        if (jTextField20.getText().length() == 0) {
            JOptionPane.showMessageDialog(mainPanel, "You are must a value of size!");
            return;
        }
        squidConf.setOnce(currentParamName, jTextField20.getText() + " " + jComboBox3.getSelectedItem().toString());;
        jTree1.repaint();
    }

    @Action
    public void saveOnceHttpPort() {
        String l = "";
        if (jTextField18.getText().length() == 0)
        l = jTextField19.getText();
        else l = jTextField18.getText() + ":" + jTextField19.getText();

        if (jTextField19.getText().length() == 0){
            //squidConf.onceParams.remove("http_port");
            JOptionPane.showMessageDialog(mainPanel, "Please type a tcp port number!");
            return;
        }

        l = l + " ";

        if (jTextField24.getText().length() > 0) l = l +"defaultsite="+jTextField24.getText()+" ";
        if (jTextField25.getText().length() > 0) l = l +"urlgroup="+jTextField25.getText()+" ";
        if (jTextField26.getText().length() > 0) l = l +"protocol="+jTextField26.getText()+" ";
        if (jTextField23.getText().length() > 0) l = l +"vport="+jTextField23.getText()+" ";
        if (jTextField23.getText().length() == 0 & jCheckBox12.isSelected())
            l = l +"vport ";



        //jCheckBox10.isSelected()
        if (jCheckBox10.isSelected()){
            l = l  + "accell ";
        }

        if (jCheckBox11.isSelected()){
            l = l  + "vhost ";
        }

        if (jCheckBox13.isSelected()){
            l = l  + "no-connection-auth ";
        }

        if (jCheckBox8.isSelected()){
            l = l  + "transparent ";
        }

        if (jCheckBox9.isSelected()){
            l = l  + "tproxy";
        }

       squidConf.setOnce(currentParamName, l);
       // JOptionPane.showMessageDialog(mainPanel, l);
       jTree1.repaint();
    }

    @Action
    public void openSquidConfFile() {
        //OpenFileDialog od = new OpenFileDialog(, true);
        JFileChooser fc = new JFileChooser();
        Integer res = fc.showOpenDialog(mainPanel);
        System.out.println("resuklt: " + res);
        if (res == 0){
        //

        currentFileName =fc.getSelectedFile().toString();
        System.out.println("open file: "+ currentFileName);


        squidConf.loadFromFile(currentFileName);
        squidConf.readText();
       // reloadTree();

       // realodAclSection();
        softReloadTree();
        jTree1.setSelectionPath(new TreePath(treeroot.getPath()));
        getFrame().setTitle("SquidModel - " + currentFileName);
        readOptionsFromFile1();

        }

    }

    @Action
    public void saveOnceTime() {

       if (jTextField27.getText().length() == 0 & jComboBox1.getSelectedItem() == null){
           JOptionPane.showMessageDialog(mainPanel, "Error: Please select a time value from list, or type custom time value. \nParameter ["+currentParamName+"] could not be saved!");
           return;
       }


        if (jTextField27.getText().length() == 0)
            squidConf.setOnce(currentParamName, jComboBox1.getSelectedItem().toString());
       else
       {squidConf.setOnce(currentParamName,jTextField27.getText());
jTree1.repaint();
       }


    }

    @Action
    public void clearConfiguration() {
        if (JOptionPane.showConfirmDialog(mainPanel, "Clear this configuration?") != JOptionPane.OK_OPTION) return;
        squidConf.Text.clear();

        squidConf.readText();
        squidConf.minimalConf();
        reloadTree();
    }

    @Action
    public void createAcl() {
        if (jTextField28.getText().length() == 0){
            JOptionPane.showMessageDialog(mainPanel, "Please type a name of new access list.");
           // jTextField28.seta
            return;
        }

        if ( squidConf.getAclIndex(jTextField28.getText()) != -1)
        {
            JOptionPane.showMessageDialog(mainPanel, "Please set another name for your access list. \nThis name allready exists in this configuraion");
            return;
        }

        else
        {
            String val = "none";
            if (jComboBox4.getSelectedItem().toString().equals("src") &
                    jTextField28.getText().equals("all") ) val = "0.0.0.0/0.0.0.0";

            if (jComboBox4.getSelectedItem().toString().equals("src") &
                    jTextField28.getText().equals("localhost") ) val = "127.0.0.1/255.255.255.255";

            if (jComboBox4.getSelectedItem().toString().equals("port") &
                    jTextField28.getText().equals("Safe_ports") ) val = "80 443";

            if (jComboBox4.getSelectedItem().toString().equals("time")  ) val = "M T W H F A S";


            squidConf.acl.add(new SquidAcl(jTextField28.getText(), jComboBox4.getSelectedItem().toString(), val));
            realodAclSection();
        }
    }

    @Action
    public void removeCurrentAcl() {

        if (currentAcl.equals("all") | currentAcl.equals("localhost") | currentAcl.equals("Safe_ports")){
            JOptionPane.showMessageDialog(mainPanel, "Sorry, this element can't be deleted...");
            return;
        }

        if (JOptionPane.showConfirmDialog(mainPanel, "Remove this ["+currentAcl+"] access list?") != JOptionPane.OK_OPTION) return;
        if (squidConf.getAclEntryAccessCount(currentAcl) > 0) {
            JOptionPane.showMessageDialog(mainPanel, "Sorry, couldnot be deleted, this element used in same accesses ["+squidConf.getAclEntryAccessToString(currentAcl)+"]...");
            return;
        }
        
        {  squidConf.acl.removeElementAt(squidConf.getAclIndex(currentAcl));
           softReloadTree();
        }

    }

    @Action
    public void saveCommonAcl() {
        if (jTextField29.getText().length() == 0){
            JOptionPane.showMessageDialog(mainPanel, "ERROR: Value cannot be empty!");
            return;
        }
        SquidAcl sa = (SquidAcl)squidConf.acl.get(squidConf.getAclIndex(currentAcl));
        sa.value = jTextField29.getText();
        if (jCheckBox14.isSelected()) sa.value = "-i " + sa.value;

        if (sa.type.equals("proxy_auth")) reloadAdmin();
        
    }

    @Action
    public void showConfText() {
        jTabbedPane1.removeAll();
        squidConf.makeConfText();
        jTextArea1.setText(squidConf.Text.getText());
        jTabbedPane1.addTab("squid.conf", conf_text);
    }

    @Action
    public void saveToFile() {
        JFileChooser fc = new JFileChooser();
        //fc.set
        Integer res = fc.showSaveDialog(mainPanel);
        if (res != 0) return;
        //if c.
        
        
        //JOptionPane.showMessageDialog(mainPanel, res + " " + fc.getSelectedFile().toString());
        squidConf.Text.saveToFile(fc.getSelectedFile().toString());
   
    }
    
    /*
     This method load options file for this program
     */
    public void loadConfFromfile(){
        sshconn.clear();
        dbconn.clear();
        
        
        for (int i = 0;  i < conf.items.size(); ++i){
            String line = conf.getLine(i);
            StringTokenizer st = new StringTokenizer(line);
            String pname = "";
            if (st.hasMoreTokens()) pname = st.nextToken();
            
            if (pname.equals("#ssh")) {
                sshconn.addOrReplaceObject(new DefaultSquidObject(pname, line));
            }
            
            if (pname.equals("#db")) {
                sshconn.addOrReplaceObject(new DefaultSquidObject(pname, line));
            }
        }
        
        readOptionsFromFile1();
        
    }
    
    


    @Action
    public void moveAclFirst() {

       if (squidConf.moveAcl(currentAcl, 0)){
        realodAclSection();
        tree.reload(acl);
        jTree1.setSelectionPath(new TreePath(acl));


       }
    }

    @Action
    public void moveAclUp() {
       if  (squidConf.moveAcl(currentAcl, squidConf.getAclIndex(currentAcl) - 1)){

        realodAclSection();
        tree.reload(acl);
        jTree1.setSelectionPath(new TreePath(acl));
       }
    }

    @Action
    public void moveAclDown() {
        if (squidConf.moveAcl(currentAcl, squidConf.getAclIndex(currentAcl) + 1)){

        realodAclSection();
        tree.reload(acl);
        jTree1.setSelectionPath(new TreePath(acl));
        }
    }

    @Action
    public void moveAclLast() {
        if (squidConf.moveAcl(currentAcl, squidConf.acl.size() - 1)){

        realodAclSection();
        tree.reload(acl);
        jTree1.setSelectionPath(new TreePath(acl));
        }
    }

    @Action
    public void addAccessLine() {
        DefaultTableModel dt = (DefaultTableModel) jTable2.getModel();
        String[] row = {"", "", "", ""};
        dt.addRow(row);

    }

    @Action
    public void saveCcessTable() throws PrinterException {
        if (squidConf.acl.size() == 0) {
            JOptionPane.showMessageDialog(mainPanel, "Acl list  is empty, please create it!");
            return;
        }


        DefaultTableModel dt = (DefaultTableModel) jTable2.getModel();
        if (dt.getRowCount() == 0) return;

        if (JOptionPane.showConfirmDialog(
                mainPanel,
                "Save this access to " + current_AccessName + "?",
                "Access lines confirmation",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE) != JOptionPane.OK_OPTION) return;

        int access_group_ix = squidConf.getAccessGroupIndex(current_AccessName);
        if (access_group_ix >= 0) {
                //SquidAccessVector sav = (SquidAccessVector) squidConf.access.get(access_group_ix);
                //sav.clear();
		squidConf.access.removeElementAt(access_group_ix);

	}

        String line = "";
        for (int i = 0; i < dt.getRowCount(); i++) {
            line =current_AccessName + " ";
            if (dt.getValueAt(i, 0).toString().length() > 0 & dt.getValueAt(i, 1).toString().length() > 0)
                {
                    for (int j = 0; j < dt.getColumnCount(); j++)
                        if (dt.getValueAt(i, j).toString().length() > 0)
                        line = line + dt.getValueAt(i, j) + " ";

                    line.trim();
                    if (line.length() > 0)
                    squidConf.addAccess(new SquidAccess(line));
                 }



        }

	if (current_AccessName.indexOf("header_access") > -1 ) reloadHeaderAccess();
        jTree1.repaint();
       // jTable2.print();
       // JOptionPane.showMessageDialog(mainPanel, "Save is complete!");
    }

    @Action
    public void delAccessLine() {
        int sel = jTable2.getSelectedRow();
        if (sel < 0){
            JOptionPane.showMessageDialog(mainPanel, "Please select action!");
            return;
        }
        DefaultTableModel dt = (DefaultTableModel) jTable2.getModel();
        String[] row = {"", "", "", ""};

        dt.insertRow(sel, row);
        dt.removeRow(sel+1);

        //dt.get
    }

    @Action
    public void accessLineUp() {
        int sel = jTable2.getSelectedRow();
        if (sel <= 0){
            JOptionPane.showMessageDialog(mainPanel, "Please select action!");
            return;
        }
        DefaultTableModel dt = (DefaultTableModel) jTable2.getModel();
        dt.moveRow(sel, sel, sel-1);
    }

    @Action
    public void moveAccessDown() {
        int sel = jTable2.getSelectedRow();
        if (sel < 0){
            JOptionPane.showMessageDialog(mainPanel, "Please select action!");
            return;
        }
        DefaultTableModel dt = (DefaultTableModel) jTable2.getModel();
        if (sel < dt.getRowCount())
         dt.moveRow(sel, sel, sel+1);
    }

    @Action
    public void moveAccessFirst() {
        int sel = jTable2.getSelectedRow();
        if (sel < 0){
            JOptionPane.showMessageDialog(mainPanel, "Please select action!");
            return;
        }
        DefaultTableModel dt = (DefaultTableModel) jTable2.getModel();
         dt.moveRow(sel, sel, 0);

    }

    @Action
    public void moveAccessLast() {
         int sel = jTable2.getSelectedRow();
        if (sel < 0){
            JOptionPane.showMessageDialog(mainPanel, "Please select action!");
            return;
        }
         DefaultTableModel dt = (DefaultTableModel) jTable2.getModel();
         dt.moveRow(sel, sel, dt.getRowCount()-1);
    }

    @Action
    public void insertAccessLine() {
         int sel = jTable2.getSelectedRow();
        if (sel < 0){
            JOptionPane.showMessageDialog(mainPanel, "Please select action!");
            return;
        }
        DefaultTableModel dt = (DefaultTableModel) jTable2.getModel();
        String[] row = {"", "", "", ""};

        dt.insertRow(sel, row);
        //dt.removeRow(sel+1);

    }

    @Action
    public void saveDelayProp() {
        SquidDelay sd = (SquidDelay) squidConf.delays.get(Integer.parseInt(curremtDelayNum) - 1);
        sd.classNum = jComboBox5.getSelectedItem().toString();

        sd.aggregate_restore = jTextField31.getText();
        sd.aggregate_maximum = jTextField32.getText();

        sd.individual_maximum = "";
        sd.individual_restore = "";

        sd.network_maximum    = "";
        sd.network_restore    = "";

        if (sd.classNum.equals("2") | sd.classNum.equals("3")){

        sd.individual_restore = jTextField33.getText();
        sd.individual_maximum = jTextField34.getText();
        }


        if (sd.classNum.equals("3")){

        sd.network_restore = jTextField35.getText();
        sd.network_maximum = jTextField36.getText();
        }

        /*
                jComboBox5.setSelectedItem(sd.classNum);
       jTextField31.setText(sd.aggregate_restore);
       jTextField32.setText(sd.aggregate_maximum);

       jTextField33.setText(sd.individual_restore);
       jTextField34.setText(sd.individual_maximum);

       jTextField35.setText(sd.network_restore);
       jTextField36.setText(sd.network_maximum);
         */

    }

    @Action
    public void delDelay() {
        /*
         *
         *
         *
         *
         *
         *
         *
         */
        if (JOptionPane.showConfirmDialog(mainPanel, "Delete delay " + curremtDelayNum + "?") != JOptionPane.OK_OPTION) return;
        squidConf.deleteDelay(curremtDelayNum);

        reloadDelaysSection();
        tree.reload(delays);
    }

    @Action
    public void addDelay() {
        SquidDelay sd = new SquidDelay();
        sd.classNum = jComboBox6.getSelectedItem().toString();

        sd.setPos(squidConf.delays.size() + 1);
        squidConf.delays.add(sd);
        reloadDelaysSection();
	selectNode(sd.pos);
    }
 
void expandLeafs(JTree tree, TreePath path) {
     TreeModel model = tree.getModel();
      Object node = path.getLastPathComponent();

      for (int i = 0; i < model.getChildCount(node); i++) {
          Object child = model.getChild(node, i);
           if(header_access.equals(child.toString())) {
              path.pathByAddingChild(child);
              tree.setSelectionPath(path);
              break;
             } else {
               expandLeafs(tree, path.pathByAddingChild(child));
              }
         }
}

  public void selectFound(JTree tree, TreePath path) {
    TreeModel model = tree.getModel();

    // Включаем это условие, если надо остановиться на первом найденом значении
    // if (found) return; // не забыть установить found = false перед началом другого сеанса поиска

    Object node = path.getLastPathComponent();

    for (int i = 0; i < model.getChildCount(node); i++) {
      Object child = model.getChild(node, i);
      TreePath childPath = path.pathByAddingChild(child);

      if (header_access.equals(child.toString())) {
        tree.setSelectionPath(childPath);
        // found = true; // boolean found - private field
      } else {
        expandLeafs(tree, childPath);
      }

    }
  }

    public DefaultMutableTreeNode findNode(String word){
	DefaultMutableTreeNode d = treeroot.getNextNode();
	int counter = 0;

	while(d != null){

	    d = d.getNextNode();

	    if (d != null)

	    if (d.toString().toLowerCase().indexOf(word.toLowerCase()) >= 0)
	    {
		System.out.println("node for word " + word + " - found!");
		return d;
	    }
	}
	System.out.println("Sorry node for word " + word + " - NOT found!");
	return null;
    }


    @Action
    public void findTreeObject() {

        String find = JOptionPane.showInputDialog(mainPanel, "Find what?", lastSearchWord);
        if (find == null) return;

	DefaultMutableTreeNode d = findNode(find);
	//lastSearchWord = find;


	if (d != null) {
	    TreePath tp = new TreePath(d.getPath());
	    jTree1.setSelectionPath(tp);
	}
	else
	    JOptionPane.showMessageDialog(mainPanel, "Sorry. This word ["+find+"] not finded...");


  //  while (treeroot.getNextNode() != null){



   // }




    //  JOptionPane.showMessageDialog(
//	      mainPanel,
//	      treeroot.getChildCount()
//	      );


	//Defaultt
		//sm.setSelectionPaths(paths)
	//jTree1.setSelectionPath(path)
	//TreePath tp = new TreePath(header_access);


	/*header_access
	//header_replace.getPath();

	//return;

	selectFound(jTree1, new TreePath(treeroot));




        DefaultTreeModel dt = (DefaultTreeModel) jTree1.getModel();
        System.out.println("total: " + treeroot.getChildCount());*/
        //jTree1.fi

    }



    @Action
    public void loadConfFromText() {

        if (JOptionPane.showConfirmDialog(mainPanel, "Really change configuration from this text?") != JOptionPane.OK_OPTION) return;

        squidConf.Text.setText(jTextArea1.getText());
        squidConf.readText();

        softReloadTree();

        showConfText();
        readOptionsFromFile1();

    }

    @Action
    public void insert10values() {
       DefaultTableModel dt =  (DefaultTableModel) jTable3.getModel();
       dt.setRowCount(dt.getRowCount() + 10);
    }

    @Action
    public void deleteAclValues() {
        DefaultTableModel dt =  (DefaultTableModel) jTable3.getModel();

        int[] rows = jTable3.getSelectedRows();
        if   (rows == null){
            JOptionPane.showMessageDialog(mainPanel, "Please select a values...");

            return;
        }
        //String[] row = {""};
       for (int i = 0; i < rows.length; i++){
           dt.setValueAt(null, rows[i], 0);
       }
    }

    @Action
    public void saveTimeAcl() {
        int[] columns = jTable4.getSelectedColumns();
        int[] rows    = jTable4.getSelectedRows();

        String[] week = {"M", "T", "W", "H", "F", "A", "S"};
        String days = "";

        for (int i = 0; i < rows.length; i++){
            days = days + week[rows[i]] + " ";
        }

        int hour_start=columns[0]-1;

        if (hour_start < 0) hour_start = 0;

        int hour_end=columns[columns.length-1];


        SquidAcl sa = (SquidAcl) squidConf.acl.get(squidConf.getAclIndex(currentAcl));

        if (hour_end <= 23)
        sa.value = days + " " + hour_start + ":00-" + hour_end + ":00";
        else sa.value = days + " " + hour_start + ":00-" + (hour_end-1) + ":59";

        if (hour_start == 0 & hour_end == 24) sa.value = days;

        //acl Always time M T W H F A S 0:00-23:59
       System.out.println("setTimeAcl("+currentAcl+", '"+ sa.value+"')");
    }

    @Action
    public void saveCachePeer() {
        if (jTextField2.getText().length() == 0) {
            JOptionPane.showMessageDialog(mainPanel, "Please type a host name");
            jTextField2.requestFocusInWindow();
            return;
        }
        String line = "cache_peer " +  jTextField2.getText() + " " +
                jComboBox7.getSelectedItem().toString() + " " +
                jTextField3.getText() + " " +
                jTextField4.getText() + " ";

        if ( jCheckBox1.isSelected()) line = line + "proxy-only ";
        if ( jCheckBox2.isSelected()) line = line + "no-query ";
        if ( jCheckBox3.isSelected()) line = line + "default ";
        if ( jCheckBox4.isSelected()) line = line + "round-robin ";
        if ( jCheckBox5.isSelected()) line = line + "carp ";
        if ( jCheckBox6.isSelected()) line = line + "multicast-responder ";
        if ( jCheckBox7.isSelected()) line = line + "closest-only ";
        if (jCheckBox15.isSelected()) line = line + "no-digest ";
        if (jCheckBox24.isSelected()) line = line + "ssl ";

        if (jCheckBox16.isSelected()) line = line + "no-netdb-exchange ";
        if (jCheckBox17.isSelected()) line = line + "no-delay ";
        if (jCheckBox18.isSelected()) line = line + "allow-miss ";
        if (jCheckBox19.isSelected()) line = line + "htcp ";
        if (jCheckBox20.isSelected()) line = line + "htcp-oldsquid ";
        if (jCheckBox21.isSelected()) line = line + "origin-server ";
        if (jCheckBox22.isSelected()) line = line + "userhash ";
        if (jCheckBox23.isSelected()) line = line + "sourcehash ";

        if (jTextField8.getText().length() > 0 ) line = line + "sslcert=" + jTextField8.getText() + " ";
        if (jTextField5.getText().length() > 0 ) line = line + "sslkey=" + jTextField5.getText() + " ";
        if (jTextField6.getText().length() > 0 ) line = line + "sslversion=" + jTextField6.getText() + " ";
        if (jTextField7.getText().length() > 0 ) line = line + "ssloptions=" + jTextField7.getText() + " ";

        if (jComboBox8.getSelectedItem() != null)
        if (jComboBox8.getSelectedItem().toString().length() > 0)
            line = line + "sslcipher=" + jComboBox8.getSelectedItem().toString() + " ";

        if (jTextField9.getText().length() > 0 )  line = line + "weight=" + jTextField9.getText() + " ";
        if (jTextField10.getText().length() > 0 ) line = line + "ttl=" + jTextField10.getText() + " ";
        if (jTextField11.getText().length() > 0 ) line = line + "login=" + jTextField11.getText() + " ";
        if (jTextField12.getText().length() > 0 ) line = line + "connect-timeout=" + jTextField12.getText() + " ";
        if (jTextField13.getText().length() > 0 ) line = line + "digest-url=" + jTextField13.getText() + " ";
        if (jTextField14.getText().length() > 0 ) line = line + "max-conn=" + jTextField14.getText() + " ";
        if (jTextField15.getText().length() > 0 ) line = line + "monitorurl=" + jTextField15.getText() + " ";
        if (jTextField16.getText().length() > 0 ) line = line + "monitorsize=" + jTextField16.getText() + " ";
        if (jTextField17.getText().length() > 0 ) line = line + "monitorinterval=" + jTextField17.getText() + " ";
        if (jTextField37.getText().length() > 0 ) line = line + "monitortimeout=" + jTextField37.getText() + " ";
        if (jTextField38.getText().length() > 0 ) line = line + "forcedomain=" + jTextField38.getText() + " ";

        if (jComboBox10.getSelectedItem() != null)
        if (jComboBox10.getSelectedItem().toString().length() > 0)
            line = line + "front-end-https=" + jComboBox10.getSelectedItem().toString() + " ";

        if (jComboBox9.getSelectedItem() != null)
        if (jComboBox9.getSelectedItem().toString().length() > 0)
            line = line + "connection-auth=" + jComboBox9.getSelectedItem().toString() + " ";


        SquidCachePeer sc = squidConf.cache_peers.get(
                squidConf.getCachePeerIndex(currentCachePeerName)
                );
        sc.parse(line);




        System.out.println(line);
    }

    @Action
    public void reloadExternalAclTypes (){
        external_acl_type.removeAllChildren();

        for (int i = 0; i < squidConf.external_acl_types.size(); i++) {
           external_acl_type.add(new DefaultMutableTreeNode(
                   squidConf.external_acl_types.get(i).name
                   ));

        }


        tree.reload(external_acl_type);

    }

    @Action
    public void deleteCachePeer() {

        if (JOptionPane.showConfirmDialog(mainPanel, "Delete cache peer ["+currentCachePeerName+"]?") != JOptionPane.OK_OPTION) return;

        int cai = squidConf.getCachePeerIndex(currentCachePeerName);
        int cac = squidConf.getAccessGroupIndex("cache_peer_access " + currentCachePeerName);

        if (cai > -1 ) squidConf.cache_peers.removeElementAt(cai);
        if (cac > -1 ) squidConf.access.removeElementAt(cac);

        squidConf.onceParams.remove("cache_peer_domain " + currentCachePeerName);
        squidConf.onceParams.remove("neighbor_type_domain " + currentCachePeerName);


        reloadCachePeerSection();
        tree.reload(cache_peer);
    }

    @Action
    public void addCachePeer() {
        //cache_peer parent.foo.net       parent    3128  3130  proxy-only default

        String new_peer_name = JOptionPane.showInputDialog(mainPanel, "Type a hostname of new peer");

        if (new_peer_name.length() == 0) {
            JOptionPane.showMessageDialog(mainPanel, "Sorry, type a valid hostname...");
            return;
        }

        if (squidConf.getCachePeerIndex(new_peer_name) >= 0) {
            JOptionPane.showMessageDialog(mainPanel, "Sorry, this peer hostname is used. Please, type another hostname");
            return;
        }

        if (JOptionPane.showConfirmDialog(mainPanel, "Add a new peer " + new_peer_name) != JOptionPane.OK_OPTION) return;

        squidConf.cache_peers.add(new SquidCachePeer("cache_peer " + new_peer_name +" parent 3128 3130 "));
        reloadCachePeerSection();
    }

    public String paramA(String param_name, String param_value){

        if (param_value.length() > 0) return param_name + "=" + param_value + " ";

        return "";
    }

    @Action
    public void saveExternalAclType() {

        if (
                jTextField51.getText().length() == 0 |
                jTextField52.getText().length() == 0
                ){
            JOptionPane.showMessageDialog(mainPanel, "Please set Format and helper correctly!");
            return;
        }

        String line = "external_acl_type " +
                current_external_acl_type + " ";


        line = line + paramA("ttl", jTextField45.getText());
        line = line + paramA("negative_ttl", jTextField46.getText());
        line = line + paramA("children", jTextField47.getText());
        line = line + paramA("concurrency", jTextField48.getText());
        line = line + paramA("cache", jTextField49.getText());
        line = line + paramA("grace", jTextField50.getText());
        if (jCheckBox25.isSelected()) line = line + "protocol=2.5 ";

        line = line + (jTextField51.getText() + " ");
        line = line + (jTextField52.getText() + " ");



        int index = squidConf.getExternalAclTypeIndex(current_external_acl_type);
        squidConf.external_acl_types.get(index).parseLine(line);


    }

    @Action
    public void delExternalAclType() {
       if (JOptionPane.showConfirmDialog(mainPanel, "Delete external acl type ["+current_external_acl_type+"]?") != JOptionPane.OK_OPTION) return;

       squidConf.external_acl_types.removeElementAt(
               squidConf.getExternalAclTypeIndex(current_external_acl_type)
               );

       reloadExternalAclTypes();



    }

    @Action
    public void addExternalAclType() {
        String more = JOptionPane.showInputDialog("New name")
                ;
        if (more.length() == 0) return;
        squidConf.external_acl_types.add(new SquidExternalAclType("external_acl_type " + more + " %LOGIN /bin/nologin"));
        reloadExternalAclTypes();
    }

    @Action
    public void save_https_port() {

        if (jTextField55.getText().length() == 0)
                JOptionPane.showMessageDialog(mainPanel, "Please type a valid port");

        String line = "https_port ";

        if (jTextField54.getText().length() > 0)
            line = line + jTextField54.getText() + ":";
        line = line + jTextField55.getText() + " ";

        if (jCheckBox26.isSelected()) line = line + "accel ";
        if (jCheckBox27.isSelected()) line = line + "vhost ";
        if (jCheckBox28.isSelected() & jTextField56.getText().length() > 0)
            line = line + "vport=" + jTextField56.getText() + " ";

        if (jCheckBox28.isSelected() & jTextField56.getText().length() == 0)
            line = line + "vport ";
        line = line + paramA("defaultsite", jTextField56.getText());
        line = line + paramA("urlgroup", jTextField58.getText());
        line = line + paramA("protocol", jTextField59.getText());
        line = line + paramA("cert", jTextField60.getText());
        line = line + paramA("key", jTextField61.getText());

        if (jComboBox11.getSelectedItem() != null)
        if (jComboBox11.getSelectedItem().toString().length() > 0)
            line = line + "version=" + jComboBox11.getSelectedItem().toString() + " ";

        line = line + paramA("cipher", jTextField63.getText());
        line = line + paramA("options", jTextField64.getText());
        line = line + paramA("clientca", jTextField65.getText());
        line = line + paramA("cafile", jTextField66.getText());
        line = line + paramA("capath", jTextField67.getText());
        line = line + paramA("crlfile", jTextField68.getText());
        line = line + paramA("dhparams", jTextField69.getText());
        line = line + paramA("sslflags", jTextField70.getText());
        line = line + paramA("sslcontext", jTextField71.getText());


        squidConf.onceParams.setProperty(currentParamName, line);
        System.out.println(line);

    }

    public void eraseTextFields(JTextField[] arr){
        for (int i = 0; i < arr.length; i++) {
            JTextField jTextField = arr[i];
            jTextField.setText("");
        }

    }

    @Action
    public void delTcpOutAddr() {
       if (JOptionPane.showConfirmDialog(mainPanel, "Delete this outgoing address ["+current_tcp_outgoing_addr+"]?") != JOptionPane.OK_OPTION) return;

       int index = squidConf.getTcpOutAddrIndex(current_tcp_outgoing_addr);

       if (index == -1) return;
       squidConf.tcp_out_addr.removeElementAt(index);
       reloadTcpOutgouingAddr();
       //
    }

    @Action
    public void saveTcpOutAddr() {
        String line = "tcp_outgoing_address " + current_tcp_outgoing_addr;
        String acl_list = "";

        DefaultTableModel dt = (DefaultTableModel) jTable1.getModel();
        for (int i = 0; i < dt.getRowCount(); i++) {
            if (dt.getValueAt(i, 0) != null)
            if (dt.getValueAt(i, 0).toString().length() > 0 )
                acl_list = acl_list +dt.getValueAt(i, 0).toString() + " ";

        }

        if (acl_list.length()  == 0) {
            JOptionPane.showMessageDialog(mainPanel, "Please select a acl");
            return;
        }

        line = line + " " + acl_list;

        System.out.println(line);

        squidConf.tcp_out_addr.get(
                squidConf.getTcpOutAddrIndex(current_tcp_outgoing_addr)
                ).value = acl_list;

    }

    @Action
    public void addTcpoutgoingAddr() {
        String n = JOptionPane.showInputDialog("Enter a new tcp outgoing ip address");
        if (squidConf.acl.size() == 0) {
            JOptionPane.showMessageDialog(mainPanel, "Acl list  is empty, please create it!");
            return;
        }
        if (n == null) return;
        if (n.length() == 0) return;
        squidConf.tcp_out_addr.add(
                new DefaultSquidObject(
                    "tcp_outgoing_address",
                    "tcp_outgoing_address " + n + " all")
                );

        reloadTcpOutgouingAddr();
	selectNode(n);
    }

    @Action
    public void addTcpOutTos() {
        String n = JOptionPane.showInputDialog("Enter a new tcp outgoing tos bit");
        if (squidConf.acl.size() == 0) {
            JOptionPane.showMessageDialog(mainPanel, "Acl list  is empty, please create it!");
            return;
        }
        if (n == null) return;
        if (n.length() == 0) return;
        squidConf.tcp_out_tos.add(
                new DefaultSquidObject(
                    "tcp_outgoing_tos",
                    "tcp_outgoing_tos " + n + " all")
                );

        reloadTcpOutgouingTos();
	selectNode(n);
    }

    @Action
    public void saveTcpOutTos() {
        String line = "tcp_outgoing_tos " + current_tcp_outgoing_tos;
        String acl_list = "";

        DefaultTableModel dt = (DefaultTableModel) jTable5.getModel();
        for (int i = 0; i < dt.getRowCount(); i++) {
            if (dt.getValueAt(i, 0) != null)
            if (dt.getValueAt(i, 0).toString().length() > 0 )
                acl_list = acl_list +dt.getValueAt(i, 0).toString() + " ";

        }

        if (acl_list.length()  == 0) {
            JOptionPane.showMessageDialog(mainPanel, "Please select a acl");
            return;
        }

        line = line + " " + acl_list;

        System.out.println(line);

        squidConf.tcp_out_tos.get(
                squidConf.getTcpOutTosIndex(current_tcp_outgoing_tos)
                ).value = acl_list;
    }

    @Action
    public void delTcpOutTos() {
       if (JOptionPane.showConfirmDialog(mainPanel, "Delete this outgoing tos ["+current_tcp_outgoing_tos+"]?") != JOptionPane.OK_OPTION) return;

       int index = squidConf.getTcpOutTosIndex(current_tcp_outgoing_tos);

       if (index == -1) return;
       squidConf.tcp_out_tos.removeElementAt(index);
       reloadTcpOutgouingTos();
       //
    }

    @Action
    public void saveSslProxyFlags() {
        String line = "";
/*
 DONT_VERIFY_PEER	 Accept certificates even if they fail to verify
NO_DEFAULT_CA	 Don't use the default CA list built in to OpenSSL
NO_SESSION_REUSE	 Don't allow for session reuse. Each connection will result in a new SSL session.
VERIFY_CRL	 Verify CRL lists when accepting client certificates
VERIFY_CRL_ALL	 Verify CRL lists for all certificates in the client certificate chain
 */
        if (jCheckBox29.isSelected()) line = "DONT_VERIFY_PEER";
        if (jCheckBox30.isSelected()) line = line + " NO_DEFAULT_CA";
        if (jCheckBox31.isSelected()) line = line + " NO_SESSION_REUSE";
        if (jCheckBox32.isSelected()) line = line + " VERIFY_CRL";
        if (jCheckBox33.isSelected()) line = line + " VERIFY_CRL_ALL";

        if (line.length() == 0) {
            JOptionPane.showMessageDialog(mainPanel, "Please select a SSL flags...");
            return;
        }

        squidConf.onceParams.setProperty("sslproxy_flags", line);
      //  System.out.println(line);
    }

    @Action
    public void saveNeighbor() {
        //String line = "";
        squidConf.setOnce(
                "neighbor_type_domain " + currentCachePeerName,
                jComboBox12.getSelectedItem().toString() + " " + jTextField62.getText());

    }

    @Action
    public void delNeighbor() {
        squidConf.onceParams.remove("neighbor_type_domain " + currentCachePeerName);
    }

    @Action
    public void saveReplacmentPpolicy() {
        if (jComboBox13.getSelectedItem().toString().length() > 0)
            squidConf.onceParams.setProperty(currentParamName, jComboBox13.getSelectedItem().toString());
        else
            squidConf.onceParams.remove (currentParamName);
    }

    @Action
    public void saveCacheDir() {
        String[] types={"ufs", "aufs", "diskd", "coss"};
        String line =  types[jTabbedPane2.getSelectedIndex()];
        //
        if (line.equals("ufs")){
            line = line + " " +
                   jTextField73.getText() + " " +
                   jTextField74.getText() + " " +
                   jTextField75.getText() + " ";
            line = line + paramA("min-size", jTextField72.getText());
            line = line + paramA("max-size", jTextField88.getText());
            if (jCheckBox34.isSelected()) line = " " + line + " read-only";
        }

        if (line.equals("aufs")){
            line = line + " " +
                   jTextField78.getText() + " " +
                   jTextField77.getText() + " " +
                   jTextField76.getText() + " ";
            line = line + paramA("min-size", jTextField72.getText());
            line = line + paramA("max-size", jTextField88.getText());
            if (jCheckBox34.isSelected()) line = " " + line + " read-only";
        }

        if (line.equals("diskd")){
            line = line + " " +
                   jTextField81.getText() + " " +
                   jTextField80.getText() + " " +
                   jTextField79.getText() + " ";

            line = line + paramA("min-size", jTextField72.getText());
            line = line + paramA("max-size", jTextField88.getText());
            if (jCheckBox34.isSelected()) line = " " + line + " read-only";

            line = line + paramA("Q1", jTextField83.getText());
            line = line + paramA("Q2", jTextField82.getText());
        }

      //  System.out.println(jTabbedPane2.getSelectedIndex() + "/" +line);
        squidConf.setOnce(currentParamName ,line);

    }

    @Action
    public void saveStoreDirAlg() {
        if (jComboBox14.getSelectedItem().toString().length() == 0){

            squidConf.onceParams.remove(currentParamName);
            return;
        }

        //store_dir_select_algorithm
        squidConf.setOnce(currentParamName, jComboBox14.getSelectedItem().toString());


    }

    @Action
    public void delLogFormat() {
        if (current_LogFormat.equals("squid") |
            current_LogFormat.equals("squidmime") |
            current_LogFormat.equals("common") |
            current_LogFormat.equals("combined")
                )
        {
            JOptionPane.showMessageDialog(mainPanel, "Sorry, this logformat cannot be removed...");
            return;
        }

       if (JOptionPane.showConfirmDialog(mainPanel, "Delete this logformat ["+current_LogFormat+"]?") != JOptionPane.OK_OPTION) return;



        squidConf.logformat.removeElementAt(squidConf.getLogFormatIndex(current_LogFormat) );
        reloadLogFormat();
    }

    @Action
    public void saveLogFormat() {
        squidConf.logformat.get(
                squidConf.getLogFormatIndex(current_LogFormat)
                ).value = jTextField89.getText();
    }

    @Action
    public void newLogFormat() {
        String n = JOptionPane.showInputDialog("Enter a new logformat name");

        if (n == null) return;
        if (n.length() == 0) return;

        if (squidConf.getLogFormatIndex(n) >= 0) {
            JOptionPane.showMessageDialog(mainPanel, "This logformat is used");
            return;
        }
        squidConf.logformat.add(new DefaultSquidObject("logformat", "logformat " + n + " %ts.%03tu %6tr %>a %Ss/%03Hs %<st %rm %ru %un %Sh/%<A %mt"));
        reloadLogFormat();

    }

    @Action
    public void newAccessLog() {
        String n = JOptionPane.showInputDialog("Enter a new file name", "/var/lib/squid/logs/access.log");

        if (n == null) return;
        if (n.length() == 0) return;

        if (squidConf.getAccessLogIndex(n) >= 0) {
            JOptionPane.showMessageDialog(mainPanel, "This file is used");
            return;
        }

        squidConf.access_log.add(new DefaultSquidObject("access_log", "access_log " + n + " squid"));
        reloadAccessLog();
    selectNode(n);

    }

    @Action
    public void delAccLog() {
       if (JOptionPane.showConfirmDialog(mainPanel, "Delete this file ["+current_AccessLog+"]?") != JOptionPane.OK_OPTION) return;



        squidConf.access_log.removeElementAt(squidConf.getAccessLogIndex(current_AccessLog) );
        reloadAccessLog();
    }

    @Action
    public void saveAccLog() {
        String line = "access_log " + current_AccessLog + " ";

        if (jComboBox15.getSelectedItem() != null)
        if (jComboBox15.getSelectedItem().toString().length() > 0)
            line = line + jComboBox15.getSelectedItem().toString() + " ";

        if (jComboBox16.getSelectedItem() != null)
        if (jComboBox16.getSelectedItem().toString().length() > 0)
            line = line + jComboBox16.getSelectedItem().toString() + " ";

        if (jComboBox17.getSelectedItem() != null)
        if (jComboBox17.getSelectedItem().toString().length() > 0)
            line = line + jComboBox17.getSelectedItem().toString() + " ";

         if (jComboBox18.getSelectedItem() != null)
         if (jComboBox18.getSelectedItem().toString().length() > 0)
            line = line + jComboBox18.getSelectedItem().toString() + " ";

         if (jComboBox19.getSelectedItem() != null)
        if (jComboBox19.getSelectedItem().toString().length() > 0)
            line = line + jComboBox19.getSelectedItem().toString() + " ";


        squidConf.access_log.get(
                squidConf.getAccessLogIndex(current_AccessLog)
                ).parseLine(line);
        System.out.println(line);
    }

    @Action
    public void delRefreshPattern() {
       if (JOptionPane.showConfirmDialog(mainPanel, "Delete this pattern ["+current_refresh_pattern+"]?") != JOptionPane.OK_OPTION) return;



        squidConf.refresh_pattern.removeElementAt(squidConf.getrefreshPatternIndex(current_refresh_pattern) );
        reloadRefreshPattern();
    }

    @Action
    public void saveRefreshPattern() {
        String line = "refresh_pattern " + current_refresh_pattern + " ";
        if (jCheckBox35.isSelected()) line = line + " -i ";
        line = line + jTextField90.getText() + " " +
                jSlider3.getValue() + "% " +
                jTextField91.getText();

        if (jCheckBox36.isSelected()) line = line + " override-expire";
        if (jCheckBox37.isSelected()) line = line + " ignore-reload";
        if (jCheckBox38.isSelected()) line = line + " reload-into-ims";
        if (jCheckBox39.isSelected()) line = line + " override-lastmod";
        if (jCheckBox40.isSelected()) line = line + " ignore-no-cache";
        if (jCheckBox41.isSelected()) line = line + " ignore-private";
        if (jCheckBox42.isSelected()) line = line + " ignore-auth";


        //System.out.println(line);
        squidConf.refresh_pattern.get(
                squidConf.getrefreshPatternIndex(current_refresh_pattern)
                ).parseLine(line);


    }

    @Action
    public void newRefreshPattern() {
        String n = JOptionPane.showInputDialog("Enter a new pattern regex");

        if (n == null) return;
        if (n.length() == 0) return;

        if (squidConf.getrefreshPatternIndex(n) >= 0) {
            JOptionPane.showMessageDialog(mainPanel, "This pattern is used");
            return;
        }

        squidConf.refresh_pattern.add(
                new DefaultSquidObject(
                "refresh_pattern",
                "refresh_pattern " + n + " 1440 20% 10080"));
        reloadRefreshPattern();
    }

    @Action
    public void newHeaderAccess() {
        String n = JOptionPane.showInputDialog("Enter a new header name");

        if (n == null) return;
        if (n.length() == 0) return;

        if (squidConf.getAccessGroupIndex("header_access " + n) >= 0) {
            JOptionPane.showMessageDialog(mainPanel, "This header is used");
            return;
        }

        squidConf.addAccess(new SquidAccess("header_access " + n + " allow all"));
        reloadHeaderAccess();
    }

    @Action
    public void delHeaderReplace() {
       if (JOptionPane.showConfirmDialog(mainPanel,
	       "Delete this element ["+current_header_replace+"]?") != JOptionPane.OK_OPTION) return;



        squidConf.header_replace.removeElementAt(squidConf.getheader_replaceIndex(current_header_replace) );
        reloadHeaderReplace();
    }

    @Action
    public void saveHeaderReplace() {
	String line = jTextField92.getText();

	if (line.length() == 0){
	    delHeaderReplace();
	    return;
	}

	squidConf.header_replace.get(
		squidConf.getheader_replaceIndex(current_header_replace)
		).value = line;

    }

    @Action
    public void newHeaderReplace() {
        String n = JOptionPane.showInputDialog("Enter a new header replace");

        if (n == null) return;
        if (n.length() == 0) return;

        if (squidConf.getheader_replaceIndex( n ) >= 0) {
            JOptionPane.showMessageDialog(mainPanel, "This header replace element is used");
            return;
        }

        squidConf.header_replace.add(new DefaultSquidObject("header_replace", "header_replace " + n + " some text"));
        reloadHeaderReplace();
    }

    @Action
    public void deleteDefaultObject() {
       if (JOptionPane.showConfirmDialog(mainPanel,
	       "Delete this element ["+currentDefaultGroupName + " " + currentDefaultName+"]?") != JOptionPane.OK_OPTION) return;

	       squidConf.delDefaultObjectFromVector(currentDefaultGroupName, currentDefaultName);
	       reloadDefaultSquidObjects(
		       (DefaultMutableTreeNode) currentNode.getParent(),
		       squidConf.getDefaultVectorFromName(currentDefaultGroupName)
		       );
    }

    @Action
    public void saveDefaultObject() {

	String line = currentDefaultGroupName + " " + currentDefaultName + " ";

	if (currentDefaultGroupName.equals("wccp2_service")) {

	   String pass = jTextField94.getText();
	   pass = pass.trim();
            if  (pass.length() > 2)
		line = line + " password=" + pass;

	}
	
	if (currentDefaultGroupName.equals("deny_info")) {

	   line = line + jComboBox22.getSelectedItem().toString();

	}
	
	if (currentDefaultGroupName.equals("cachemgr_passwd")) {

	   //line = line + jComboBox22.getSelectedItem().toString();
	   
	   if (jCheckBox52.isSelected()) line = line + jCheckBox52.getText() + " ";
	   if (jCheckBox53.isSelected()) line = line + jCheckBox53.getText() + " ";
	   if (jCheckBox54.isSelected()) line = line + jCheckBox54.getText() + " ";
	   if (jCheckBox55.isSelected()) line = line + jCheckBox55.getText() + " ";
	   if (jCheckBox56.isSelected()) line = line + jCheckBox56.getText() + " ";
	   if (jCheckBox57.isSelected()) line = line + jCheckBox57.getText() + " ";
	   if (jCheckBox58.isSelected()) line = line + jCheckBox58.getText() + " ";
	   if (jCheckBox59.isSelected()) line = line + jCheckBox59.getText() + " ";
	   
	   if (jCheckBox60.isSelected()) line = line + jCheckBox60.getText() + " ";   
	   if (jCheckBox61.isSelected()) line = line + jCheckBox61.getText() + " ";
	   if (jCheckBox62.isSelected()) line = line + jCheckBox62.getText() + " ";
	   if (jCheckBox63.isSelected()) line = line + jCheckBox63.getText() + " ";
	   if (jCheckBox64.isSelected()) line = line + jCheckBox64.getText() + " ";
	   if (jCheckBox65.isSelected()) line = line + jCheckBox65.getText() + " ";	   
	   if (jCheckBox66.isSelected()) line = line + jCheckBox66.getText() + " ";
	   if (jCheckBox67.isSelected()) line = line + jCheckBox67.getText() + " ";
	   if (jCheckBox68.isSelected()) line = line + jCheckBox68.getText() + " ";
	   if (jCheckBox69.isSelected()) line = line + jCheckBox69.getText() + " ";
	   
	   if (jCheckBox70.isSelected()) line = line + jCheckBox70.getText() + " ";   
	   if (jCheckBox71.isSelected()) line = line + jCheckBox71.getText() + " ";
	   if (jCheckBox72.isSelected()) line = line + jCheckBox72.getText() + " ";
	   if (jCheckBox73.isSelected()) line = line + jCheckBox73.getText() + " ";
	   if (jCheckBox74.isSelected()) line = line + jCheckBox74.getText() + " ";
	   if (jCheckBox75.isSelected()) line = line + jCheckBox75.getText() + " ";	   
	   if (jCheckBox76.isSelected()) line = line + jCheckBox76.getText() + " ";
	   if (jCheckBox77.isSelected()) line = line + jCheckBox77.getText() + " ";
	   if (jCheckBox78.isSelected()) line = line + jCheckBox78.getText() + " ";
	   if (jCheckBox79.isSelected()) line = line + jCheckBox79.getText() + " ";   
	   
	   if (jCheckBox80.isSelected()) line = line + jCheckBox80.getText() + " ";   
	   if (jCheckBox81.isSelected()) line = line + jCheckBox81.getText() + " ";
	   if (jCheckBox82.isSelected()) line = line + jCheckBox82.getText() + " ";
	   if (jCheckBox83.isSelected()) line = line + jCheckBox83.getText() + " ";
	   if (jCheckBox84.isSelected()) line = line + jCheckBox84.getText() + " ";
	   if (jCheckBox85.isSelected()) line = line + jCheckBox85.getText() + " ";	   
	   if (jCheckBox86.isSelected()) line = line + jCheckBox86.getText() + " ";
	   if (jCheckBox87.isSelected()) line = line + jCheckBox87.getText() + " ";
	   if (jCheckBox88.isSelected()) line = line + jCheckBox88.getText() + " ";
	}
	
	if (currentDefaultGroupName.equals("wccp2_service_info")) {
	    line = line + " protocol=" + jComboBox21.getSelectedItem().toString() + " ";
	    String flags = "";
	    
	    
	    if (jCheckBox43.isSelected()) flags = flags + jCheckBox43.getText() + ",";
	    if (jCheckBox44.isSelected()) flags = flags + jCheckBox44.getText() + ",";
	    if (jCheckBox45.isSelected()) flags = flags + jCheckBox45.getText() + ",";
	    if (jCheckBox46.isSelected()) flags = flags + jCheckBox46.getText() + ",";
	    if (jCheckBox47.isSelected()) flags = flags + jCheckBox47.getText() + ",";
	    if (jCheckBox48.isSelected()) flags = flags + jCheckBox48.getText() + ",";
	    if (jCheckBox49.isSelected()) flags = flags + jCheckBox49.getText() + ",";
	    if (jCheckBox50.isSelected()) flags = flags + jCheckBox50.getText() + ",";
	    if (jCheckBox51.isSelected()) flags = flags + jCheckBox51.getText() + ",";
	  
	    
	    
	    if (flags.length() > 0) line = line + " flags=" + flags.substring(0, flags.length()-1) + "	";
	    
	    if (jTextField95.getText().length() > 0) line = line + " priority=" + jTextField95.getText() + " ";
	    if (jTextField96.getText().length() > 0) line = line + " ports=" + jTextField96.getText() + " ";
	   

	}
	

	squidConf.addDefualtObjectInVector(
	(new DefaultSquidObject(currentDefaultGroupName, line)));

    }
    
    public void selectNode(String name){
	DefaultMutableTreeNode d = findNode(name);
	//lastSearchWord = find;


	if (d != null) {
	    TreePath tp = new TreePath(d.getPath());
	    jTree1.setSelectionPath(tp);	
	}	
    }

    @Action
    public void addDefObj() {
	
	String new_line = "", n = "";
	
	if (currentDefaultGroupName.equals("wccp2_service")){  
	    
	String[] types = {"standart", "dynamic"};
	String s = (String)JOptionPane.showInputDialog(
                    mainPanel,
                    "WCCP2_SERVICE",
                    "Select type",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    types,
                    "standart");
	
        n = JOptionPane.showInputDialog("Enter a new name of element for " + currentDefaultGroupName + " type: " + s);
	new_line = currentDefaultGroupName +" " + s + " " + n;}
	
	if (currentDefaultGroupName.equals("wccp2_service_info")){ 
	      n = JOptionPane.showInputDialog("Enter a new name of element for " + currentDefaultGroupName);
	      new_line = currentDefaultGroupName +" " + n + " protocol=tcp ";
	  //    System.out.println("===========" + new_line);
	}
	
	if (currentDefaultGroupName.equals("cachemgr_passwd")){ 
	      n = JOptionPane.showInputDialog("Enter a new password of element for " + currentDefaultGroupName);
	      new_line = currentDefaultGroupName +" " + n + " shutdown";
	  //    System.out.println("===========" + new_line);
	}
	
	
	if (currentDefaultGroupName.equals("error_map")){ 
	      n = JOptionPane.showInputDialog("Enter a URL " + currentDefaultGroupName);
	      String s = (String)JOptionPane.showInputDialog(
                    mainPanel,
                    "Error map",
                    "Select http status for " + n,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    http_status_arg,
                    "404 Not Found");
	      
	      
	      new_line = currentDefaultGroupName +" " + n + " " + s.substring(0, 3);
	  //    System.out.println("===========" + new_line);
	}
	
	if (currentDefaultGroupName.equals("deny_info")){ 
	    n = JOptionPane.showInputDialog("Enter a new name of element for url (if you do not type a url, then you must select a standart page)");
	    
	    if (n.length() == 0) n = (String)JOptionPane.showInputDialog(
                    mainPanel,
                    "Error pages",
                    "Select err page ",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    err_pages,
                    "");
	    new_line = currentDefaultGroupName + " " + n +" all";
	}
	
	
	
        if (n == null) return;
        if (n.length() == 0) return;	
	    if (squidConf.getDefaultObjectValue(currentDefaultGroupName, n) != null) {
		JOptionPane.showMessageDialog(mainPanel, "This name is used");
		return;
	    }

	squidConf.addDefualtObjectInVector(
		new DefaultSquidObject(currentDefaultGroupName, new_line)
		);
	reloadDefaultSquidObjects(currentNode, squidConf.getDefaultVectorFromName(currentDefaultGroupName));
	selectNode(n);
	
	
	
	
    }

    @Action
    public void showGlobalVars() {
	squidConf.getDefaultVectorInfo();
	
	JOptionPane.showMessageDialog(mainPanel, 
		 "currentDefaultGroupName=" + currentDefaultGroupName + "\n" +
		 "currentDefaultName=" + currentDefaultName + "\n"
		 
		);
    }

    @Action
    public void selectErrPage() {
	String s = (String)JOptionPane.showInputDialog(
                    mainPanel,
                    "Error pages",
                    "Select err page ",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    err_pages,
                    "");
	jTextField97.setText(s);
    }

    @Action
    public void saveUri() {
	squidConf.onceParams.setProperty("uri_whitespace", jComboBox23.getSelectedItem().toString());
    }
    
    public String encode(String s){
        String result = "";
        String sepa = s.substring(0, 6);
        
       /*/ result = sepa + ">>>";
        StringTokenizer st = new StringTokenizer(s, sepa);
        while (st.hasMoreTokens()){
            String tok = st.nextToken(sepa);
            result = result + "+" + tok;
        }
        */
        for (int i = 0 ; i <s.split(sepa).length; i++){
            
            String  tok = s.split(sepa)[i];
            //int m = Integer.parseInt();
           
            //int b = java.math.
            if (tok.length() > 0) {
                //Math.sqrt(i)
                Double m = Math.sqrt(256*256 - Integer.parseInt(tok));
                char ch = (char)m.byteValue();
                
            result = ch   + result;
            }
        }
        
        
        return result;
    }
    /*     
     * This function NOT a secure!!!!!, simple hide password from conf 
     */
    public String decode(String pass){
        java.util.Random rnd = new java.util.Random();
        int sepa = rnd.nextInt(100000)+100000;
        
        
        String result = "";
        for (int i =0; i < pass.length(); i++){
            byte b = (byte)pass.charAt(i) ;
            int m = 256*256 - b*b;
            result = sepa + ""  + m + ""  + result;
        }
        
        return result;
    }

    @Action
    public void saveSSH() {
        String line = "#ssh " + currentSshConn + " ";
        if (jTextField39.getText().length() == 0) line = line + "address=" + currentSshConn + " ";
        else line = line + "address=" + jTextField39.getText() + " ";
        
        if (jTextField43.getText().length() == 0) line = line + "user=root ";
        else line = line + "user=" + jTextField43.getText() + " ";
        
        if (jPasswordField1.getText().length() > 0) 
            line = line + "password=" + decode(jPasswordField1.getText()) + " ";
        
        
        if (jTextField40.getText().length() == 0) line = line + "port=22 ";
        else line = line + "port=" + jTextField40.getText() + " ";
        
        if (jTextField41.getText().length() == 0) line = line + "confpath=/etc/squid/squid.conf ";
        else line = line + "confpath=" + jTextField41.getText() + " ";
        
        if (jTextField42.getText().length() == 0) line = line + "exepath=squid ";
        else line = line + "exepath=" + jTextField42.getText() + " "; 
       // JOptionPane.showMessageDialog(mainPanel, line);
        
        sshconn.get(currentSshConn).parseLine(line);
        
         
        saveConf();
    }

    @Action
    public void newSsh() {
        String h = JOptionPane.showInputDialog("Enter a valid ssh hostname");
        if (h.length() == 0) return;
        sshconn.addOrReplaceObject(new DefaultSquidObject("#ssh", 
                "#ssh " + h + " address=" + h + " user=root port=22 password=" + decode("password")));
        relodConn();
    }

    @Action
    public void delSsh() {
        if (JOptionPane.showConfirmDialog(mainPanel, "Delete this SSH host?") != JOptionPane.OK_OPTION) return;
        sshconn.removeByName(currentSshConn);
        relodConn();
        saveConf();
    }

    @Action
    public void getProcInfo() throws JSchException {
        //(String host, String user, final String password, int port, String command)
     /*JOptionPane.showMessageDialog(mainPanel, runSSH(
        jTextField39.getText(),
        jTextField43.getText(),
        jPasswordField1.getText(),
        Integer.parseInt(jTextField40.getText()),
        "ps ax |grep squid; netstat -ntap |grep squid"
        ).getText());*/
                   Log(
            runSSH(
           jTextField39.getText(),
           jTextField43.getText(),
           jPasswordField1.getText(),
           Integer.parseInt(jTextField40.getText()),
           "whereis squid; squid -v; ps ax |grep squid; netstat -ntap |grep squid"
                ).getText());
        
        

    }

    @Action
    public void downloadSquidConf1() {
        if (JOptionPane.showConfirmDialog(mainPanel, getMsg("DownLoadSquidConfMessage")) != JOptionPane.OK_OPTION) return;        
        squidConf.Text.setText(
          runSSH(
           jTextField39.getText(),
           jTextField43.getText(),
           jPasswordField1.getText(),
           Integer.parseInt(jTextField40.getText()),
           "cat " + jTextField41.getText()
                ).getText() 
                );
        
        if (squidConf.Text.getLine(0).indexOf("ERROR") >= 0) return;       
        currentFileName = jTextField39.getText() + ":" + jTextField41.getText();
        squidConf.readText();
        softReloadTree();
        currentFileName = jTextField39.getText() + ":" + jTextField41.getText();
        //readOptionsFromFile1();
        
    }
    
    public void setDefSshParameters(
                String host,
                String username,
                String password,
                String port,
                String def_conf_path,
                String def_exe_name
            ){
        UnixHost = host;
        UnixSSHPort = port;
        UnixSquidConf = def_conf_path;
        UnixUser = username;
        UnixPassword = password;
        UnixSquidExec = def_exe_name;
    }
    
    public void readOptionsFromFile1(){
        String val = squidConf.getDefaultObjectValue("#squidmodel", "option");   
        if (val != null) {
           // JOptionPane.showMessageDialog(mainPanel, val);
            StringTokenizer st = new StringTokenizer(val);
            while (st.hasMoreTokens()){
                String tok = st.nextToken();
                if (tok.indexOf("default_ssh_host=") >= 0) {
                    defaultSSH = tok.split("=")[1];
                    jTree1.repaint();
                }
                
            }
        }
    }

    @Action
    public void uploadConf()
    {
        //file1 user@remotehost:file2 password
        if (JOptionPane.showConfirmDialog(mainPanel, "This operation is clearing remote file. Proceed?") != JOptionPane.OK_OPTION) return;        
        if (JOptionPane.showConfirmDialog(mainPanel, "Are you sure?") != JOptionPane.OK_OPTION) return;                  
        squidConf.makeConfText();
        
        
        String tmpFile = System.getProperty("user.home") + File.separator + "squid.conf-" + jTextField39.getText();
        
        squidConf.Text.saveToFile(tmpFile);
        String[] arg = {tmpFile, jTextField43.getText() + "@" + jTextField39.getText() + ":" + jTextField41.getText(), jPasswordField1.getText()};
         Log("connect to host...");
         
        ScpTo.main(arg);
        
        if (jCheckBox91.isSelected()){
           
            
             Log(
            runSSH(
           jTextField39.getText(),
           jTextField43.getText(),
           jPasswordField1.getText(),
           Integer.parseInt(jTextField40.getText()),
           "squid -k parse; squid -k reconfigure; sleep 1; tail /var/lib/squid/logs/cache.log"
                ).getText());
        }
    }

    @Action
    public void saveUser() {
        DefaultListModel dl = (DefaultListModel) jList2.getModel();
        StringTokenizer st = new StringTokenizer(removedGruops);
        
        if (dl.size() == 0) {
            
               if (JOptionPane.showConfirmDialog(mainPanel, "User do not contain in groups. Delete this user?")  != JOptionPane.OK_OPTION) return;        
               //if (squidConf.getUsersCountInGroup())
               
               while (st.hasMoreTokens()){
                   String gr = st.nextToken();
                   squidConf.removeUserFromgroup(currentUserName, gr);
                   
                   int ix = squidConf.getAclIndex(gr);
                   if (squidConf.getUsersCountInGroup(gr) == 0) 
                       squidConf.acl.removeElementAt(ix);
                   realodAclSection();
                   
               }
               
               squidConf.delDefaultObjectFromVector("#user", currentUserName);
               reloadAdmin();
               return;
        }
        
        if (removedGruops.length() > 0) {
            if (JOptionPane.showConfirmDialog(mainPanel, "Remove user from groups: " + removedGruops)  != JOptionPane.OK_OPTION) return;        
            while (st.hasMoreTokens()){
                String gr = st.nextToken();
                int ix = squidConf.getAclIndex(gr);
                squidConf.removeUserFromgroup(currentUserName, gr);
                if (squidConf.getUsersCountInGroup(gr) == 0) 
                       squidConf.acl.removeElementAt(ix);
                
               }
            
            
            realodAclSection();
        }
        
        if (addedGroups.length() > 0){
            StringTokenizer ag = new StringTokenizer(addedGroups);
            while (ag.hasMoreTokens()){
                String tok = ag.nextToken();
                squidConf.addUserToGroup(currentUserName, tok);
            }
        }
        
        
        String line = "#user " + currentUserName + " ";
        
        if ("0".equals(jTextField100.getText()))
            jTextField100.setText(String.valueOf(squidConf.getNextUserGenId()));
        
        if (jPasswordField2.getText().length() > 0) line = line + " password=" + jPasswordField2.getText() + " ";
        if (jTextField44.getText().length() > 0) line = line + " email=" + jTextField44.getText() + " ";
        if (jTextField98.getText().length() > 0) line = line + " limitmonth=" + jTextField98.getText() + " ";
        if (jTextField99.getText().length() > 0) line = line + " limitday=" + jTextField99.getText() + " ";
        if (jTextField100.getText().length() > 0) line = line + " id=" + jTextField100.getText() + " ";
        //ЗДесь проверяем если у юзера нету ID то назначаем ему следующий...
        

        if (jTextField1.getText().length() == 10) { 
            
            
            line = line + " expireddate=" + jTextField1.getText() + " ";
        
        }
        else {
            //
            if (jTextField1.getText().length() > 0) JOptionPane.showMessageDialog(mainPanel, "<html>Field <b>Expired date</b> must be a next format: <h2>YYYY.MM.DD</h2>" +
                    "for example: <u>2010.01.09</u>, <u>2010.12.31</u> and etc.");
            jTextField1.setText("");}
        
        
        if (jCheckBox89.isSelected()) line = line + " disablenever=true ";
        else line = line + " disablenever=false ";
        
        if (jCheckBox90.isSelected()) line = line + " disable=true ";
        else line = line + " disable=false ";
        
        
        
        
        squidConf.addDefualtObjectInVector(new DefaultSquidObject("#user", line));
        
        
        //reloadAdmin();
        
    }

    @Action
    public void showHelp1()  {
        jTabbedPane1.addTab("Help for " + currentParamName, help_page);
        jTabbedPane1.setSelectedIndex(jTabbedPane1.getTabCount()-1);

        TStrings str = new TStrings();
        String name = currentParamName;
        
        if (currentNode.getParent() != null)
            if (currentNode.getParent().getParent() != null)
                if (currentNode.getParent().getParent().toString().equals("auth_param"))
                    name = "auth_param";
        
        str.loadFromJar("jsquidmodel/man26/"+name+".html");

        String[] ar = {"href"};
        jEditorPane1.setText("<html>" + str.grep_v(ar).getText().toString());

    }

    @Action
    public void removeUserFromGr() {
        String gr = (String) jList2.getSelectedValue();
        if (gr == null) return;
        
        
        
        if (JOptionPane.showConfirmDialog(mainPanel, "<html>Remove user <u>[" + currentUserName + "]</u> from group <u>[" + gr + "]</u>?")  != JOptionPane.OK_OPTION) return;        
        DefaultListModel dl = (DefaultListModel)jList2.getModel();
        
        if (dl.size() == 1) {
            if (JOptionPane.showConfirmDialog(mainPanel, "WARNING: This operation remove a user from configuration titally. Proceed?")  != JOptionPane.OK_OPTION) return;        
        }
        
        if (squidConf.getUsersCountInGroup(gr) == 1) {
            if (JOptionPane.showConfirmDialog(mainPanel, "WARNING: This group a last ,which a contain a user "+currentUserName+" and this group "+gr+" be was a deleted with a acl. Proceed?")  != JOptionPane.OK_OPTION) return;        
        }
        
        dl.removeElementAt(jList2.getSelectedIndex());
        removedGruops = removedGruops + gr + " ";
        //System.out.println(gr);
    }

    @Action
    public void addUserGr() {
	      //String n = JOptionPane.showInputDialog("Enter a URL " + currentDefaultGroupName);
	      String s = (String)JOptionPane.showInputDialog(
                    mainPanel,
                    "Add a group",
                    "Select a group..",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    squidConf.groupsWithoutUser(currentUserName).split(" "),
                    "");     
              if (s == null) return;
              if (s.length() <= 1) return;
              DefaultListModel dl = (DefaultListModel) jList2.getModel();
              addedGroups = addedGroups + " " + s;
              dl.addElement(s);
              
    }

    @Action
    public void applyConf() {
    }

    @Action
    public void setDefSSH() {
        defaultSSH = currentSshConn;
        jTree1.repaint();
        
        setDefSshParameters(
                jTextField39.getText(),
                jTextField43.getText(),
                jPasswordField1.getText(),
                jTextField40.getText(),
                jTextField41.getText(),
                jTextField42.getText()
                
                );
        
        squidConf.addDefualtObjectInVector(new DefaultSquidObject("#squidmodel", "#squidmodel options default_ssh_host=" + defaultSSH));
    }

    @Action
    public void tailLogFile() {
        if (UnixHost.equals("<none>")){
            JOptionPane.showMessageDialog(mainPanel, "Please choose a default ssh host... ");
            return;
        }
        Log(
        runSSH(
                UnixHost,
                UnixUser,
                UnixPassword,
                Integer.parseInt(UnixSSHPort),
                "tail -n 20 " + current_AccessLog
                ).getText()
            );
    }



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel access_log_page;
    private javax.swing.JPanel access_page;
    private javax.swing.JPanel acl_common;
    private javax.swing.JPopupMenu acl_item_menu;
    private javax.swing.JPanel acl_page;
    private javax.swing.JPanel acl_time;
    private javax.swing.JPopupMenu aclvalues_popup;
    private javax.swing.JPanel aufs;
    private javax.swing.JPanel auth_str;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JPanel cache_dir;
    private javax.swing.JPanel cache_peer_page;
    private javax.swing.JPanel cachemgr_passwd_page;
    private javax.swing.JPopupMenu cachepeer_popup;
    private javax.swing.JPopupMenu conf_menu;
    private javax.swing.JPanel conf_text;
    private javax.swing.JPanel coss;
    private javax.swing.JPanel custom_message;
    private javax.swing.JPanel delay_page;
    private javax.swing.JPopupMenu delay_popup;
    private javax.swing.JPanel delay_prop;
    private javax.swing.JPanel deny_info_page;
    private javax.swing.JPanel diskd;
    private javax.swing.JPanel external_acl_page;
    private javax.swing.JPopupMenu external_acl_popup;
    private javax.swing.JPanel group_page;
    private javax.swing.JPanel header_replace_page;
    private javax.swing.JPanel help_page;
    private javax.swing.JPanel https_port_page;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton22;
    private javax.swing.JButton jButton23;
    private javax.swing.JButton jButton24;
    private javax.swing.JButton jButton25;
    private javax.swing.JButton jButton26;
    private javax.swing.JButton jButton27;
    private javax.swing.JButton jButton28;
    private javax.swing.JButton jButton29;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton30;
    private javax.swing.JButton jButton31;
    private javax.swing.JButton jButton32;
    private javax.swing.JButton jButton33;
    private javax.swing.JButton jButton34;
    private javax.swing.JButton jButton35;
    private javax.swing.JButton jButton36;
    private javax.swing.JButton jButton37;
    private javax.swing.JButton jButton38;
    private javax.swing.JButton jButton39;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton40;
    private javax.swing.JButton jButton41;
    private javax.swing.JButton jButton42;
    private javax.swing.JButton jButton43;
    private javax.swing.JButton jButton44;
    private javax.swing.JButton jButton45;
    private javax.swing.JButton jButton46;
    private javax.swing.JButton jButton47;
    private javax.swing.JButton jButton48;
    private javax.swing.JButton jButton49;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton50;
    private javax.swing.JButton jButton51;
    private javax.swing.JButton jButton52;
    private javax.swing.JButton jButton53;
    private javax.swing.JButton jButton54;
    private javax.swing.JButton jButton55;
    private javax.swing.JButton jButton56;
    private javax.swing.JButton jButton57;
    private javax.swing.JButton jButton58;
    private javax.swing.JButton jButton59;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton60;
    private javax.swing.JButton jButton61;
    private javax.swing.JButton jButton62;
    private javax.swing.JButton jButton63;
    private javax.swing.JButton jButton64;
    private javax.swing.JButton jButton65;
    private javax.swing.JButton jButton66;
    private javax.swing.JButton jButton67;
    private javax.swing.JButton jButton68;
    private javax.swing.JButton jButton69;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton70;
    private javax.swing.JButton jButton71;
    private javax.swing.JButton jButton72;
    private javax.swing.JButton jButton73;
    private javax.swing.JButton jButton74;
    private javax.swing.JButton jButton75;
    private javax.swing.JButton jButton76;
    private javax.swing.JButton jButton77;
    private javax.swing.JButton jButton78;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox10;
    private javax.swing.JCheckBox jCheckBox11;
    private javax.swing.JCheckBox jCheckBox12;
    private javax.swing.JCheckBox jCheckBox13;
    private javax.swing.JCheckBox jCheckBox14;
    private javax.swing.JCheckBox jCheckBox15;
    private javax.swing.JCheckBox jCheckBox16;
    private javax.swing.JCheckBox jCheckBox17;
    private javax.swing.JCheckBox jCheckBox18;
    private javax.swing.JCheckBox jCheckBox19;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox20;
    private javax.swing.JCheckBox jCheckBox21;
    private javax.swing.JCheckBox jCheckBox22;
    private javax.swing.JCheckBox jCheckBox23;
    private javax.swing.JCheckBox jCheckBox24;
    private javax.swing.JCheckBox jCheckBox25;
    private javax.swing.JCheckBox jCheckBox26;
    private javax.swing.JCheckBox jCheckBox27;
    private javax.swing.JCheckBox jCheckBox28;
    private javax.swing.JCheckBox jCheckBox29;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JCheckBox jCheckBox30;
    private javax.swing.JCheckBox jCheckBox31;
    private javax.swing.JCheckBox jCheckBox32;
    private javax.swing.JCheckBox jCheckBox33;
    private javax.swing.JCheckBox jCheckBox34;
    private javax.swing.JCheckBox jCheckBox35;
    private javax.swing.JCheckBox jCheckBox36;
    private javax.swing.JCheckBox jCheckBox37;
    private javax.swing.JCheckBox jCheckBox38;
    private javax.swing.JCheckBox jCheckBox39;
    private javax.swing.JCheckBox jCheckBox4;
    private javax.swing.JCheckBox jCheckBox40;
    private javax.swing.JCheckBox jCheckBox41;
    private javax.swing.JCheckBox jCheckBox42;
    private javax.swing.JCheckBox jCheckBox43;
    private javax.swing.JCheckBox jCheckBox44;
    private javax.swing.JCheckBox jCheckBox45;
    private javax.swing.JCheckBox jCheckBox46;
    private javax.swing.JCheckBox jCheckBox47;
    private javax.swing.JCheckBox jCheckBox48;
    private javax.swing.JCheckBox jCheckBox49;
    private javax.swing.JCheckBox jCheckBox5;
    private javax.swing.JCheckBox jCheckBox50;
    private javax.swing.JCheckBox jCheckBox51;
    private javax.swing.JCheckBox jCheckBox52;
    private javax.swing.JCheckBox jCheckBox53;
    private javax.swing.JCheckBox jCheckBox54;
    private javax.swing.JCheckBox jCheckBox55;
    private javax.swing.JCheckBox jCheckBox56;
    private javax.swing.JCheckBox jCheckBox57;
    private javax.swing.JCheckBox jCheckBox58;
    private javax.swing.JCheckBox jCheckBox59;
    private javax.swing.JCheckBox jCheckBox6;
    private javax.swing.JCheckBox jCheckBox60;
    private javax.swing.JCheckBox jCheckBox61;
    private javax.swing.JCheckBox jCheckBox62;
    private javax.swing.JCheckBox jCheckBox63;
    private javax.swing.JCheckBox jCheckBox64;
    private javax.swing.JCheckBox jCheckBox65;
    private javax.swing.JCheckBox jCheckBox66;
    private javax.swing.JCheckBox jCheckBox67;
    private javax.swing.JCheckBox jCheckBox68;
    private javax.swing.JCheckBox jCheckBox69;
    private javax.swing.JCheckBox jCheckBox7;
    private javax.swing.JCheckBox jCheckBox70;
    private javax.swing.JCheckBox jCheckBox71;
    private javax.swing.JCheckBox jCheckBox72;
    private javax.swing.JCheckBox jCheckBox73;
    private javax.swing.JCheckBox jCheckBox74;
    private javax.swing.JCheckBox jCheckBox75;
    private javax.swing.JCheckBox jCheckBox76;
    private javax.swing.JCheckBox jCheckBox77;
    private javax.swing.JCheckBox jCheckBox78;
    private javax.swing.JCheckBox jCheckBox79;
    private javax.swing.JCheckBox jCheckBox8;
    private javax.swing.JCheckBox jCheckBox80;
    private javax.swing.JCheckBox jCheckBox81;
    private javax.swing.JCheckBox jCheckBox82;
    private javax.swing.JCheckBox jCheckBox83;
    private javax.swing.JCheckBox jCheckBox84;
    private javax.swing.JCheckBox jCheckBox85;
    private javax.swing.JCheckBox jCheckBox86;
    private javax.swing.JCheckBox jCheckBox87;
    private javax.swing.JCheckBox jCheckBox88;
    private javax.swing.JCheckBox jCheckBox89;
    private javax.swing.JCheckBox jCheckBox9;
    private javax.swing.JCheckBox jCheckBox90;
    private javax.swing.JCheckBox jCheckBox91;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox10;
    private javax.swing.JComboBox jComboBox11;
    private javax.swing.JComboBox jComboBox12;
    private javax.swing.JComboBox jComboBox13;
    private javax.swing.JComboBox jComboBox14;
    private javax.swing.JComboBox jComboBox15;
    private javax.swing.JComboBox jComboBox16;
    private javax.swing.JComboBox jComboBox17;
    private javax.swing.JComboBox jComboBox18;
    private javax.swing.JComboBox jComboBox19;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JComboBox jComboBox20;
    private javax.swing.JComboBox jComboBox21;
    private javax.swing.JComboBox jComboBox22;
    private javax.swing.JComboBox jComboBox23;
    private javax.swing.JComboBox jComboBox3;
    private javax.swing.JComboBox jComboBox4;
    private javax.swing.JComboBox jComboBox5;
    private javax.swing.JComboBox jComboBox6;
    private javax.swing.JComboBox jComboBox7;
    private javax.swing.JComboBox jComboBox8;
    private javax.swing.JComboBox jComboBox9;
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel100;
    private javax.swing.JLabel jLabel101;
    private javax.swing.JLabel jLabel102;
    private javax.swing.JLabel jLabel103;
    private javax.swing.JLabel jLabel104;
    private javax.swing.JLabel jLabel105;
    private javax.swing.JLabel jLabel106;
    private javax.swing.JLabel jLabel107;
    private javax.swing.JLabel jLabel108;
    private javax.swing.JLabel jLabel109;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel110;
    private javax.swing.JLabel jLabel111;
    private javax.swing.JLabel jLabel112;
    private javax.swing.JLabel jLabel113;
    private javax.swing.JLabel jLabel114;
    private javax.swing.JLabel jLabel115;
    private javax.swing.JLabel jLabel116;
    private javax.swing.JLabel jLabel117;
    private javax.swing.JLabel jLabel118;
    private javax.swing.JLabel jLabel119;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel120;
    private javax.swing.JLabel jLabel121;
    private javax.swing.JLabel jLabel122;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel87;
    private javax.swing.JLabel jLabel88;
    private javax.swing.JLabel jLabel89;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel90;
    private javax.swing.JLabel jLabel91;
    private javax.swing.JLabel jLabel92;
    private javax.swing.JLabel jLabel93;
    private javax.swing.JLabel jLabel94;
    private javax.swing.JLabel jLabel95;
    private javax.swing.JLabel jLabel96;
    private javax.swing.JLabel jLabel97;
    private javax.swing.JLabel jLabel98;
    private javax.swing.JLabel jLabel99;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JList jList1;
    private javax.swing.JList jList2;
    private javax.swing.JList jList3;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem13;
    private javax.swing.JMenuItem jMenuItem14;
    private javax.swing.JMenuItem jMenuItem15;
    private javax.swing.JMenuItem jMenuItem16;
    private javax.swing.JMenuItem jMenuItem17;
    private javax.swing.JMenuItem jMenuItem18;
    private javax.swing.JMenuItem jMenuItem19;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem20;
    private javax.swing.JMenuItem jMenuItem21;
    private javax.swing.JMenuItem jMenuItem22;
    private javax.swing.JMenuItem jMenuItem23;
    private javax.swing.JMenuItem jMenuItem24;
    private javax.swing.JMenuItem jMenuItem25;
    private javax.swing.JMenuItem jMenuItem26;
    private javax.swing.JMenuItem jMenuItem27;
    private javax.swing.JMenuItem jMenuItem28;
    private javax.swing.JMenuItem jMenuItem29;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem30;
    private javax.swing.JMenuItem jMenuItem31;
    private javax.swing.JMenuItem jMenuItem32;
    private javax.swing.JMenuItem jMenuItem33;
    private javax.swing.JMenuItem jMenuItem34;
    private javax.swing.JMenuItem jMenuItem35;
    private javax.swing.JMenuItem jMenuItem36;
    private javax.swing.JMenuItem jMenuItem37;
    private javax.swing.JMenuItem jMenuItem38;
    private javax.swing.JMenuItem jMenuItem39;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem40;
    private javax.swing.JMenuItem jMenuItem41;
    private javax.swing.JMenuItem jMenuItem42;
    private javax.swing.JMenuItem jMenuItem43;
    private javax.swing.JMenuItem jMenuItem44;
    private javax.swing.JMenuItem jMenuItem45;
    private javax.swing.JMenuItem jMenuItem46;
    private javax.swing.JMenuItem jMenuItem47;
    private javax.swing.JMenuItem jMenuItem48;
    private javax.swing.JMenuItem jMenuItem49;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem50;
    private javax.swing.JMenuItem jMenuItem51;
    private javax.swing.JMenuItem jMenuItem52;
    private javax.swing.JMenuItem jMenuItem53;
    private javax.swing.JMenuItem jMenuItem54;
    private javax.swing.JMenuItem jMenuItem55;
    private javax.swing.JMenuItem jMenuItem56;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JPasswordField jPasswordField2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator10;
    private javax.swing.JSeparator jSeparator11;
    private javax.swing.JSeparator jSeparator12;
    private javax.swing.JSeparator jSeparator13;
    private javax.swing.JSeparator jSeparator14;
    private javax.swing.JSeparator jSeparator15;
    private javax.swing.JSeparator jSeparator16;
    private javax.swing.JSeparator jSeparator17;
    private javax.swing.JSeparator jSeparator18;
    private javax.swing.JSeparator jSeparator19;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator20;
    private javax.swing.JSeparator jSeparator21;
    private javax.swing.JSeparator jSeparator22;
    private javax.swing.JSeparator jSeparator23;
    private javax.swing.JSeparator jSeparator24;
    private javax.swing.JSeparator jSeparator25;
    private javax.swing.JSeparator jSeparator26;
    private javax.swing.JSeparator jSeparator27;
    private javax.swing.JSeparator jSeparator28;
    private javax.swing.JSeparator jSeparator29;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator30;
    private javax.swing.JSeparator jSeparator31;
    private javax.swing.JSeparator jSeparator32;
    private javax.swing.JSeparator jSeparator33;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JSlider jSlider1;
    private javax.swing.JSlider jSlider2;
    private javax.swing.JSlider jSlider3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTabbedPane jTabbedPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTable jTable4;
    private javax.swing.JTable jTable5;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField10;
    private javax.swing.JTextField jTextField100;
    private javax.swing.JTextField jTextField11;
    private javax.swing.JTextField jTextField12;
    private javax.swing.JTextField jTextField13;
    private javax.swing.JTextField jTextField14;
    private javax.swing.JTextField jTextField15;
    private javax.swing.JTextField jTextField16;
    private javax.swing.JTextField jTextField17;
    private javax.swing.JTextField jTextField18;
    private javax.swing.JTextField jTextField19;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField20;
    private javax.swing.JTextField jTextField21;
    private javax.swing.JTextField jTextField22;
    private javax.swing.JTextField jTextField23;
    private javax.swing.JTextField jTextField24;
    private javax.swing.JTextField jTextField25;
    private javax.swing.JTextField jTextField26;
    private javax.swing.JTextField jTextField27;
    private javax.swing.JTextField jTextField28;
    private javax.swing.JTextField jTextField29;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField30;
    private javax.swing.JTextField jTextField31;
    private javax.swing.JTextField jTextField32;
    private javax.swing.JTextField jTextField33;
    private javax.swing.JTextField jTextField34;
    private javax.swing.JTextField jTextField35;
    private javax.swing.JTextField jTextField36;
    private javax.swing.JTextField jTextField37;
    private javax.swing.JTextField jTextField38;
    private javax.swing.JTextField jTextField39;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField40;
    private javax.swing.JTextField jTextField41;
    private javax.swing.JTextField jTextField42;
    private javax.swing.JTextField jTextField43;
    private javax.swing.JTextField jTextField44;
    private javax.swing.JTextField jTextField45;
    private javax.swing.JTextField jTextField46;
    private javax.swing.JTextField jTextField47;
    private javax.swing.JTextField jTextField48;
    private javax.swing.JTextField jTextField49;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField50;
    private javax.swing.JTextField jTextField51;
    private javax.swing.JTextField jTextField52;
    private javax.swing.JTextField jTextField53;
    private javax.swing.JTextField jTextField54;
    private javax.swing.JTextField jTextField55;
    private javax.swing.JTextField jTextField56;
    private javax.swing.JTextField jTextField57;
    private javax.swing.JTextField jTextField58;
    private javax.swing.JTextField jTextField59;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField60;
    private javax.swing.JTextField jTextField61;
    private javax.swing.JTextField jTextField62;
    private javax.swing.JTextField jTextField63;
    private javax.swing.JTextField jTextField64;
    private javax.swing.JTextField jTextField65;
    private javax.swing.JTextField jTextField66;
    private javax.swing.JTextField jTextField67;
    private javax.swing.JTextField jTextField68;
    private javax.swing.JTextField jTextField69;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField70;
    private javax.swing.JTextField jTextField71;
    private javax.swing.JTextField jTextField72;
    private javax.swing.JTextField jTextField73;
    private javax.swing.JTextField jTextField74;
    private javax.swing.JTextField jTextField75;
    private javax.swing.JTextField jTextField76;
    private javax.swing.JTextField jTextField77;
    private javax.swing.JTextField jTextField78;
    private javax.swing.JTextField jTextField79;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jTextField80;
    private javax.swing.JTextField jTextField81;
    private javax.swing.JTextField jTextField82;
    private javax.swing.JTextField jTextField83;
    private javax.swing.JTextField jTextField84;
    private javax.swing.JTextField jTextField85;
    private javax.swing.JTextField jTextField86;
    private javax.swing.JTextField jTextField87;
    private javax.swing.JTextField jTextField88;
    private javax.swing.JTextField jTextField89;
    private javax.swing.JTextField jTextField9;
    private javax.swing.JTextField jTextField90;
    private javax.swing.JTextField jTextField91;
    private javax.swing.JTextField jTextField92;
    private javax.swing.JTextField jTextField93;
    private javax.swing.JTextField jTextField94;
    private javax.swing.JTextField jTextField95;
    private javax.swing.JTextField jTextField96;
    private javax.swing.JTextField jTextField97;
    private javax.swing.JTextField jTextField98;
    private javax.swing.JTextField jTextField99;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JTree jTree1;
    private javax.swing.JPanel log_page;
    private javax.swing.JPanel logformat_page;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JPanel neighbor_page;
    private javax.swing.JPanel once_bool;
    private javax.swing.JPanel once_httpport;
    private javax.swing.JPopupMenu once_param_popup;
    private javax.swing.JPanel once_percent;
    private javax.swing.JPanel once_size;
    private javax.swing.JPanel once_str;
    private javax.swing.JPanel once_time;
    private javax.swing.JPopupMenu popup_AccessLog;
    private javax.swing.JPopupMenu popup_RefreshPattern;
    private javax.swing.JPopupMenu popup_access;
    private javax.swing.JPopupMenu popup_def_obj;
    private javax.swing.JPopupMenu popup_dev_obj_item;
    private javax.swing.JPopupMenu popup_groups_userlist;
    private javax.swing.JPopupMenu popup_header_acc;
    private javax.swing.JPopupMenu popup_header_replace;
    private javax.swing.JPopupMenu popup_logformat;
    private javax.swing.JPopupMenu popup_ssh;
    private javax.swing.JPopupMenu popup_ssh_item;
    private javax.swing.JPopupMenu popup_tcp_outgoing_addr;
    private javax.swing.JPopupMenu popup_tcpout_tos;
    private javax.swing.JPopupMenu popup_user_groups;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JPanel refresh_pattern_page;
    private javax.swing.JPanel replacement_policy;
    private javax.swing.JPanel ssh_page;
    private javax.swing.JPanel sslproxy_flags_page;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JPanel store_alg_page;
    private javax.swing.JPanel tcp_out_addr_page;
    private javax.swing.JPanel tcp_out_tos;
    private javax.swing.JPanel ufs;
    private javax.swing.JPanel uri_page;
    private javax.swing.JPanel user_page;
    private javax.swing.JPanel wccp2_service_info_page;
    private javax.swing.JPanel wccp2_service_page;
    private javax.swing.JPanel welcome;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;

    private JDialog aboutBox;
}
