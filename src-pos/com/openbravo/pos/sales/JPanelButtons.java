//    uniCenta oPOS  - Touch Friendly Point Of Sale
//    Copyright (c) 2009-2014 uniCenta & previous Openbravo POS works
//    http://www.unicenta.com
//
//    This file is part of uniCenta oPOS
//
//    uniCenta oPOS is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//   uniCenta oPOS is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with uniCenta oPOS.  If not, see <http://www.gnu.org/licenses/>.

package com.openbravo.pos.sales;

import com.openbravo.data.loader.LocalRes;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.AppUser;
import com.openbravo.pos.util.ThumbNailBuilder;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author JG uniCenta
 */
public class JPanelButtons extends javax.swing.JPanel {

    private static final Logger logger = Logger.getLogger("com.openbravo.pos.sales.JPanelButtons");

    private static SAXParser m_sp = null;
    
    private Properties props;
    private Map<String, String> events;
    
    private ThumbNailBuilder tnbmacro;
    
    private JPanelTicket panelticket;
    
    /** Creates new form JPanelButtons
     * @param sConfigKey
     * @param panelticket */
    public JPanelButtons(String sConfigKey, JPanelTicket panelticket) {
        initComponents();
        
        // Load categories default thumbnail
        tnbmacro = new ThumbNailBuilder(24, 24, "com/openbravo/images/run_script.png");
        
        this.panelticket = panelticket;
        
        props = new Properties();
        events = new HashMap<>();
        
        String sConfigRes = panelticket.getResourceAsXML(sConfigKey);
        
        if (sConfigRes != null) {
            try {
                if (m_sp == null) {
                    SAXParserFactory spf = SAXParserFactory.newInstance();
                    m_sp = spf.newSAXParser();
                }
                m_sp.parse(new InputSource(new StringReader(sConfigRes)), new ConfigurationHandler());

            } catch (ParserConfigurationException ePC) {
                logger.log(Level.WARNING, LocalRes.getIntString("exception.parserconfig"), ePC);
            } catch (SAXException eSAX) {
                logger.log(Level.WARNING, LocalRes.getIntString("exception.xmlfile"), eSAX);
            } catch (IOException eIO) {
                logger.log(Level.WARNING, LocalRes.getIntString("exception.iofile"), eIO);
            }
        }     
    
    }
    
    /**
     *
     * @param user
     */
    public void setPermissions(AppUser user) {
        for (Component c : this.getComponents()) {
            String sKey = c.getName();
            if (sKey == null || sKey.equals("")) {
                c.setEnabled(true);
            } else {
                c.setEnabled(user.hasPermission(c.getName()));
            }
        }
    }
    
    /**
     *
     * @param key
     * @return
     */
    public String getProperty(String key) {
        return props.getProperty(key);
    }

    /**
     *
     * @param key
     * @param defaultvalue
     * @return
     */
    public String getProperty(String key, String defaultvalue) {
        return props.getProperty(key, defaultvalue);
    }
     
    /**
     *
     * @param key
     * @return
     */
    public String getEvent(String key) {
        return events.get(key);
    }
    
    private class ConfigurationHandler extends DefaultHandler {       
        @Override
        public void startDocument() throws SAXException {}
        @Override
        public void endDocument() throws SAXException {}    
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException{
            switch (qName) {
                case "button":
                    // The button title text
                    String titlekey = attributes.getValue("titlekey");
                    if (titlekey == null) {
                        titlekey = attributes.getValue("name");
                    }
                    String title = titlekey == null
                            ? attributes.getValue("title")
                            : AppLocal.getIntString(titlekey);
                    // adding the button to the panel
                    JButton btn = new JButtonFunc(attributes.getValue("key"), 
                            attributes.getValue("image"), 
                            title);
                    // The template resource or the code resource
                    final String template = attributes.getValue("template");
                    if (template == null) {
                        final String code = attributes.getValue("code");
                        btn.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent evt) {
                                panelticket.evalScriptAndRefresh(code);
                            }
                        });
                    } else {
                        btn.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent evt) {
                                panelticket.printTicket(template);
                            }
                        });     
                    }
                    add(btn);
                    break;
                case "event":
                    events.put(attributes.getValue("key"), attributes.getValue("code"));
                    break;
                default:
                    String value = attributes.getValue("value");
                    if (value != null) {                  
                        props.setProperty(qName, attributes.getValue("value"));
                    }
                    break;
            }
        }      
        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {}
        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {}
    }  
        
    private class JButtonFunc extends JButton {
       
        public JButtonFunc(String sKey, String sImage, String title) {
            
            setName(sKey);
            setText(title);
            setIcon(new ImageIcon(tnbmacro.getThumbNail(panelticket.getResourceAsImage(sImage))));
            setFocusPainted(false);
            setFocusable(false);
            setRequestFocusEnabled(false);
            setMargin(new Insets(8, 14, 8, 14));  
        }         
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        setPreferredSize(new java.awt.Dimension(450, 77));
        setRequestFocusEnabled(false);

        jTextArea1.setColumns(20);
        jTextArea1.setRows(50);
        jTextArea1.setText("Click here to scan customer ID...");
        jScrollPane1.setViewportView(jTextArea1);

        jButton1.setText("Check Age");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Clear");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 296, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(88, 88, 88))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton2)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton2))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        //JOptionPane.showMessageDialog(null,"the button worked","AGE VERIFICATION",JOptionPane.INFORMATION_MESSAGE);
        String age = jTextArea1.getText().toLowerCase();
        String dob1 = "Date of birth".toLowerCase();
        String dob2 = "DOB".toLowerCase();
        String dob3 = "Birthday".toLowerCase();
        int DOBindex = 0;
        String dob = "";
        boolean ageFound = false;
        if (age.contains(dob1)){
            ageFound = true;
            DOBindex = age.indexOf(dob1);
            //System.out.print("Found Date of birth");
        }
        if (age.contains(dob2)){
            ageFound = true;
            DOBindex = age.indexOf(dob2);
            //System.out.print("Found DOB");
        }
        if (age.contains(dob3)){
            ageFound = true;
            DOBindex = age.indexOf(dob3);
            //System.out.print("Found Birthday");
        }
        //System.out.print(DOBindex);
        if (ageFound == true){
            String cut = age.substring(DOBindex);
            Pattern pattern = Pattern.compile("(\\d)+([/\\\\]|-)(\\d)+([/\\\\]|-)(\\d\\d)+", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(cut);
            int i = 0;
            while(i < 1 && matcher.find()){
                dob = matcher.group(0).toString();
                i++;
            }
        }
        //JOptionPane.showMessageDialog(null,"DOB: " + dob,"AGE VERIFICATION",JOptionPane.INFORMATION_MESSAGE);
        Date dobfinal = null;
        String[] formatStrings = {"M/y", "M/d/y", "M-d-y"};  
        for (String formatString : formatStrings)
        {
            try
            {
                dobfinal = new SimpleDateFormat(formatString).parse(dob);
            }
            catch (ParseException e) {}
        }
        
        if (dobfinal == null){
            JOptionPane.showMessageDialog(null,"Could not find date of birth, please check manually","AGE VERIFICATION",JOptionPane.INFORMATION_MESSAGE);
        }
        else{
            Date currentDate = new Date();
            long current = currentDate.getTime();
            long dateOfBirth = dobfinal.getTime();
            long ageDays = Math.abs((current - dateOfBirth)/(1000*60*60*24));
            long daysTill21 = 7665;
            if (ageDays > daysTill21){
                JOptionPane.showMessageDialog(null,"Customer is over 21","AGE VERIFICATION",JOptionPane.INFORMATION_MESSAGE);
            }
            else{
                JOptionPane.showMessageDialog(null,"ALERT: CUSTOMER IS UNDER 21","AGE VERIFICATION",JOptionPane.INFORMATION_MESSAGE);
            }
        }  
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        jTextArea1.setText("");
    }//GEN-LAST:event_jButton2ActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables
    
}
