package com.simulation.ifcparser;

import java.io.File;
import java.util.*;
import com.apstex.ifc2x3toolbox.ifc2x3.*;
import com.apstex.ifc2x3toolbox.ifcmodel.IfcModel;
import com.apstex.step.core.LIST;
import com.apstex.step.core.SET;

public class IfcParser {
    IfcModel ifcModel;
    public IfcParser(String ifcModelPath) throws Exception{
        ifcModel = new IfcModel();
        File stepFile = new File(ifcModelPath);
        ifcModel.readStepFile(stepFile);
    }

    public List<IfcFooting> getIfcFooting(){
        List<IfcFooting> ifcFootings = new LinkedList<>();
        for (IfcFooting ifcFooting : ifcModel.getCollection(IfcFooting.class)) {
            ifcFootings.add(ifcFooting);
        }
        return ifcFootings;
    }

    public void printIfcFootingStepLine(){
        System.out.println("[ ifcFootingStepLine #SIZE=" + getIfcFooting().size() + " ]");
        for (IfcFooting ifcFooting : getIfcFooting()) {
            System.out.println("" + ifcFooting.getStepLine());
        }
    }

    //後でクラスジェネリクスにする
    public LIST<IfcCartesianPoint> getIfcWallCartesianPoints(IfcFooting ifcFooting){
        LIST<IfcCartesianPoint> ifcCartesianPoints = new LIST<>();
        IfcProductDefinitionShape definitionShape = (IfcProductDefinitionShape)ifcFooting.getRepresentation();
        LIST<IfcRepresentation> shapeRepresentation = definitionShape.getRepresentations();
        if(shapeRepresentation.size()!=1) System.out.println("shapeRepresentationの配列サイズが一つではありません、コードを書き直してください");
        for (IfcRepresentation ifcRepresentation : shapeRepresentation) {
            SET<IfcRepresentationItem> ifcRepresentationItems = ifcRepresentation.getItems();
            if(ifcRepresentationItems.size()!=1) System.out.println("ifcRepresentationItemsの配列サイズが一つではありません、コードを書き直してください");
            for (IfcRepresentationItem representationItem : ifcRepresentationItems) {
                IfcFacetedBrep ifcFacetedBrep = (IfcFacetedBrep)representationItem;
                IfcClosedShell ifcClosedShell = ifcFacetedBrep.getOuter();
                SET<IfcFace> ifcFaces = ifcClosedShell.getCfsFaces();
                if(ifcFaces.size()!=1) System.out.println("ifcFacesの配列サイズが一つではありません、コードを書き直してください");
                for (IfcFace ifcFace : ifcFaces) {
                    SET<IfcFaceBound> ifcFaceBounds = ifcFace.getBounds();
                    if(ifcFaceBounds.size()!=1) System.out.println("ifcFaceBoundsの配列サイズが一つではありません、コードを書き直してください");
                    for (IfcFaceBound ifcFaceBound : ifcFaceBounds) {
                        IfcPolyLoop ifcPolyLoop = (IfcPolyLoop)ifcFaceBound.getBound();
                        ifcCartesianPoints = ifcPolyLoop.getPolygon();
                    }
                }
            }
        }
        return ifcCartesianPoints;
    }

    public List<Double> getIfcFootingPointValueList(IfcFooting ifcFooting,int index){// index 0~3
        List<IfcCartesianPoint> ifcCartesianPoints = getIfcWallCartesianPoints(ifcFooting);
        List<Double> ValueList = new ArrayList<>();
        ifcCartesianPoints.get(index).getCoordinates();
        for (IfcLengthMeasure ifcLengthMeasure : ifcCartesianPoints.get(index).getCoordinates()) {
            ValueList.add(ifcLengthMeasure.getValue());
        }
        return ValueList;
    }


    public List<IfcWall> getIfcWall(){
        List<IfcWall> ifcWalls = new ArrayList<>();
        for (IfcWall ifcWall : ifcModel.getCollection(IfcWall.class)) {
            ifcWalls.add(ifcWall);
        }
        return ifcWalls;
    }

    public void printIfcWallStepLine(){
        System.out.println("[ ifcWallStepLine #SIZE="+ getIfcWall().size() +" ]");
        for (IfcWall ifcWall : getIfcWall()) {
            System.out.println("" + ifcWall.getStepLine());
        }
    }

    //後でクラスジェネリクスにする
    public LIST<IfcCartesianPoint> getIfcWallCartesianPoints(IfcWall wall){
        LIST<IfcCartesianPoint> ifcCartesianPoints = new LIST<>();
        IfcProductDefinitionShape definitionShape = (IfcProductDefinitionShape)wall.getRepresentation();
        LIST<IfcRepresentation> shapeRepresentation = definitionShape.getRepresentations();
        if(shapeRepresentation.size()!=1) System.out.println("shapeRepresentationの配列サイズが一つではありません、コードを書き直してください");
        for (IfcRepresentation ifcRepresentation : shapeRepresentation) {
            SET<IfcRepresentationItem> ifcRepresentationItems = ifcRepresentation.getItems();
            if(ifcRepresentationItems.size()!=1) System.out.println("ifcRepresentationItemsの配列サイズが一つではありません、コードを書き直してください");
            for (IfcRepresentationItem representationItem : ifcRepresentationItems) {
                IfcFacetedBrep ifcFacetedBrep = (IfcFacetedBrep)representationItem;
                IfcClosedShell ifcClosedShell = ifcFacetedBrep.getOuter();
                SET<IfcFace> ifcFaces = ifcClosedShell.getCfsFaces();
                if(ifcFaces.size()!=1) System.out.println("ifcFacesの配列サイズが一つではありません、コードを書き直してください");
                for (IfcFace ifcFace : ifcFaces) {
                    SET<IfcFaceBound> ifcFaceBounds = ifcFace.getBounds();
                    if(ifcFaceBounds.size()!=1) System.out.println("ifcFaceBoundsの配列サイズが一つではありません、コードを書き直してください");
                    for (IfcFaceBound ifcFaceBound : ifcFaceBounds) {
                        IfcPolyLoop ifcPolyLoop = (IfcPolyLoop)ifcFaceBound.getBound();
                        ifcCartesianPoints = ifcPolyLoop.getPolygon();
                    }
                }
            }
        }
        return ifcCartesianPoints;
    }

    public List<Double> getIfcWallCartesianPointValueList(IfcWall wall,int index){// index 0~3
        List<IfcCartesianPoint> ifcCartesianPoints = getIfcWallCartesianPoints(wall);
        List<Double> ValueList = new ArrayList<>();
        ifcCartesianPoints.get(index).getCoordinates();
        for (IfcLengthMeasure ifcLengthMeasure : ifcCartesianPoints.get(index).getCoordinates()) {
            ValueList.add(ifcLengthMeasure.getValue());
        }
        return ValueList;
    }


    public static void main(String[] args) throws Exception {
        IfcParser ifcParser = new IfcParser("/Users/rys9469/Documents/build_data/10-20room.ifc");
        List<IfcWall> ifcWalls = ifcParser.getIfcWall();
        System.out.println("helloIfcParser");
    }
}
