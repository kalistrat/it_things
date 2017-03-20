package com.vaadin;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.Tree;
import java.sql.*;
/**
 * Created by kalistrat on 18.11.2016.
 */
public class tTree extends Tree {

    static final private String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final private String DB_URL = "jdbc:mysql://localhost/things";
    static final private String USER = "kalistrat";
    static final private String PASS = "045813";

    public HierarchicalContainer TreeContainer;
    //public String iUserLog;

    public tTree(String eUserLog,tMainView eMainView){

        this.TreeContainer = tTreeGetData(eUserLog);

        setItemCaptionPropertyId(4);


        for (int j=1;j<this.TreeContainer.size()+1;j++) {
            String IconStr =  (String) this.TreeContainer.getItem(j).getItemProperty(5).getValue();

            if (IconStr.equals("FOLDER")) {
                setItemIcon(j, FontAwesome.FOLDER);
            }
            if (IconStr.equals("TACHOMETER")) {
                setItemIcon(j, FontAwesome.TACHOMETER);
            }
            if (IconStr.equals("VIDEO_CAMERA")) {
                setItemIcon(j, FontAwesome.VIDEO_CAMERA);
            }
        }


        setContainerDataSource(this.TreeContainer);

        //Разворачиваю дерево
        for (Object id : this.rootItemIds()) {
            this.expandItemsRecursively(id);
        }

        this.select(1);

        //Добавляю слушатель для каждого листочка дерева
        this.addListener(new Property.ValueChangeListener() {

            public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
                if(event.getProperty().getValue() != null)
                {
                    //String atribut = getItemCaption(event.getProperty().getValue());
                    //System.out.println(event.getProperty().getValue());
                    Item SelectedItem = TreeContainer.getItem(event.getProperty().getValue());
                    //SelectedItem.getItemProperty(5).getValue();
                    eMainView.TreeContentUsr.tTreeContentLayoutRefresh((int) SelectedItem.getItemProperty(2).getValue(),(int) SelectedItem.getItemProperty(6).getValue());

                }
            }
        });

    }

    public HierarchicalContainer tTreeGetData(String qUserLog){

        HierarchicalContainer iTreeContainer = new HierarchicalContainer();

        iTreeContainer.addContainerProperty(1, Integer.class, null);
        iTreeContainer.addContainerProperty(2, Integer.class, null);
        iTreeContainer.addContainerProperty(3, Integer.class, null);
        iTreeContainer.addContainerProperty(4, String.class, null);
        iTreeContainer.addContainerProperty(5, String.class, null);
        iTreeContainer.addContainerProperty(6, Integer.class, null);
        iTreeContainer.addContainerProperty(7, String.class, null);


        try {
            Class.forName(JDBC_DRIVER);
            Connection Con = DriverManager.getConnection(DB_URL, USER, PASS);

            String TreeSql = "select udt.user_devices_tree_id\n" +
                    ",udt.leaf_id\n" +
                    ",udt.parent_leaf_id\n" +
                    ",udt.leaf_name\n" +
                    ",ifnull(act.icon_code,'FOLDER') icon_code\n" +
                    ",ifnull(udt.user_device_id,0) user_device_id\n" +
                    ",act.action_type_name\n" +
                    "from user_devices_tree udt\n" +
                    "join users u on u.user_id=udt.user_id\n" +
                    "left join user_device ud on ud.user_device_id=udt.user_device_id\n" +
                    "left join device dev on dev.device_id=ud.device_id\n" +
                    "left join action_type act on act.action_type_id=dev.action_type_id\n" +
                    "where u.user_log=?";

            PreparedStatement TreeSqlStmt = Con.prepareStatement(TreeSql);
            TreeSqlStmt.setString(1,qUserLog);


            ResultSet TreeSqlRs = TreeSqlStmt.executeQuery();

            while (TreeSqlRs.next()) {

                Item newItem = iTreeContainer.getItem(iTreeContainer.addItem());
                Integer UsrDevTreeId = TreeSqlRs.getInt(1);
                Integer UsrLeafId = TreeSqlRs.getInt(2);
                Integer UsrParentLeafId = TreeSqlRs.getInt(3);
                String UsrLeafName = TreeSqlRs.getString(4);
                String UsrLeafIcon = TreeSqlRs.getString(5);
                Integer UsrDeviceId = TreeSqlRs.getInt(6);
                String UsrActionType = TreeSqlRs.getString(7);

                newItem.getItemProperty(1).setValue(UsrDevTreeId);
                newItem.getItemProperty(2).setValue(UsrLeafId);
                newItem.getItemProperty(3).setValue(UsrParentLeafId);
                newItem.getItemProperty(4).setValue(UsrLeafName);
                newItem.getItemProperty(5).setValue(UsrLeafIcon);
                newItem.getItemProperty(6).setValue(UsrDeviceId);
                newItem.getItemProperty(7).setValue(UsrActionType);
                //FontAwesome.SIGN_IN
                        //Button v = new Button("ere");
                //v.setIcon();

            }


            Con.close();

        } catch (SQLException se3) {
            //Handle errors for JDBC
            se3.printStackTrace();
        } catch (Exception e13) {
            //Handle errors for Class.forName
            e13.printStackTrace();
        }

        //String s1 = (String) iTreeContainer.getItem(1).getItemProperty(4).getValue();
        //System.out.println(s1);


        for (int i = 1; i < iTreeContainer.size()+1; i++){
            if ((Integer) iTreeContainer.getItem(i).getItemProperty(3).getValue() != 0) {
                iTreeContainer.setParent(i, iTreeContainer.getItem(i).getItemProperty(3).getValue());
                //String s1 = (String) iTreeContainer.getItem(i).getItemProperty(4).getValue();
                //String s1 = String.valueOf(iTreeContainer.getItem(i).getItemProperty(3).getValue());

                //System.out.println(s1);
            }
        }

        return iTreeContainer;

    }



}
