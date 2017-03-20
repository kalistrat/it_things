package com.vaadin;

import com.vaadin.data.Item;
import com.vaadin.ui.VerticalLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kalistrat on 13.01.2017.
 */
public class tTreeContentLayout extends VerticalLayout {

    tTree itTree;
    public tTreeContentLayout(String tUserLog,tTree etTree){

        this.itTree = etTree;
        this.addComponent(new tFolderLayout(1,this));


    }

    public void tTreeContentLayoutRefresh(int eLeafId, int eUserDeviceId){
        this.removeAllComponents();
        if (eUserDeviceId == 0){
            this.addComponent(new tFolderLayout(eLeafId,this));
        } else {
            this.addComponent(new tDeviceLayout(eLeafId,this));
        }
        itTree.select(eLeafId);
    }

    public String GetLeafNameById(int eLeafId){

        return (String) this.itTree.getItem(eLeafId).getItemProperty(4).getValue();

    }

    public List<Integer> GetChildLeafsById(int eLeafId){
        List<Integer> iChildLeafs = new ArrayList<Integer>();

        for (int i=0;i<this.itTree.TreeContainer.size();i++) {
            Integer iParentLeafId = (Integer) this.itTree.getItem(i + 1).getItemProperty(3).getValue();
         if (iParentLeafId.equals(eLeafId)){
             iChildLeafs.add((Integer) this.itTree.getItem(i + 1).getItemProperty(2).getValue());
         }
        }

        return iChildLeafs;
    }

    public Integer GetParentLeafById(int eLeafId){
        Integer iParentLeafId = (Integer) this.itTree.getItem(eLeafId).getItemProperty(3).getValue();
        if (iParentLeafId == null) {
            return 0;
        } else {
            return iParentLeafId;
        }
    }

}
