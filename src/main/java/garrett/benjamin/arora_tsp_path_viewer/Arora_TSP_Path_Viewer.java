/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package garrett.benjamin.arora_tsp_path_viewer;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * @author Benjamin Garrett
 */
public class Arora_TSP_Path_Viewer extends JFrame {
    
    private final String DISPLAY_PARTITIONS = "Display Partitions";
    private boolean displayPartitions;
    
    private final String DISPLAY_PARTITIONS_AND_PORTALS = 
            "Display Partitions and Portals";
    private boolean displayPartitionsAndPortals;
    
    private final String DISPLAY_ARORA = "Display Arora Path";
    private boolean displayArora;
    private final String DISPLAY_ACO = "Display ACO Tour";
    private boolean displayAco;
    private final String DISPLAY_OPTIMAL = "Display Optimal Tour";
    private boolean displayOptimal;
    private final String DISPLAY_CONCORDE = "Display Concorde Tour";
    private boolean displayConcorde;
    
    private boolean displayOriginalPoints;
    private boolean displayDataPoints;
    
    
    private final String NAME = "NAME:";
    private String instance_name;
    
    private final String TYPE = "TYPE:";
    private String instance_type;
    
    private final String COMMENT = "COMMENT:";
    private String instance_comment;
    
    private final String ORIGINAL_POINTS = "ORIGINAL_POINTS:";
    private PathPoint2D [] originalPoint;
    private int numOriginalPoints;
    
    private final String DATA_POINTS = "DATA_POINTS:";
    private PathPoint2D [] dataPoint;
    private int numDataPoints;
    
    private final String PORTALS = "PORTALS:";
    private PathPoint2D [] portalPoint;
    private int numPortalPoints;
    
    private final String PARTITIONS = "PARTITIONS:";
    private PathPoint2D [] partitionMin;
    private PathPoint2D [] partitionMax;
    private int numPartitions;
    
    private final String ARORA_PATH = "ARORA_PATH:";
    private PathPoint2D [] aroraPathPoint;
    private int numAroraPathPoints;
    
    private final String ARORA_TOUR = "ARORA_TOUR:";
    private PathPoint2D [] aroraTourPoint;
    private int numAroraTourPoints;
    
    private final String OPTIMAL_TOUR = "OPTIMAL_TOUR:";
    private PathPoint2D [] optimalTourPoint;
    private int numOptimalTourPoints;
    
    private final String CONCORDE_TOUR = "CONCORDE_TOUR:";
    private PathPoint2D [] concordeTourPoint;
    private int numConcordeTourPoints;
    
    private final String ACO_TOUR = "ACO_TOUR:";
    private PathPoint2D [] acoTourPoint;
    private int numAcoTourPoints;
    
    private final int FRAME_WIDTH = 700;
    private final int FRAME_HEIGHT = 700;
    private final int TOP_MARGIN = 40;
    private final int RIGHT_MARGIN = 20;
    private final int BOTTOM_MARGIN = 20;
    private final int LEFT_MARGIN = 20;
    private final int INNER_MARGIN = 20;
    private final double FUDGE_FACTOR = 0.625;
    private final int OVAL_DIAMETER = 5;
    private final int RECTANGLE_SIZE = 5;
    
    private int effectiveWidth, effectiveHeight;
        
    private Point2D topLeft;
    private Point2D topRight;
    private Point2D bottomLeft;
    private Point2D bottomRight;
    
    private Line2D topEdge, rightEdge, bottomEdge, leftEdge;
    private Line2D boundingBoxTop, boundingBoxRight, boundingBoxBottom, boundingBoxLeft;
       

    public Arora_TSP_Path_Viewer() throws FileNotFoundException, IOException {
        
        int i;
        
        displayOriginalPoints = false;
        displayDataPoints = false;
        
        numOriginalPoints = 0;
        numDataPoints = 0;
        numPortalPoints = 0;
        numPartitions = 0;
        numAroraPathPoints = 0;
        numAroraTourPoints = 0;
        numOptimalTourPoints = 0;
        numConcordeTourPoints = 0;
        
        effectiveWidth = FRAME_WIDTH - RIGHT_MARGIN - LEFT_MARGIN - INNER_MARGIN - INNER_MARGIN;
        effectiveHeight = FRAME_HEIGHT - TOP_MARGIN - BOTTOM_MARGIN - INNER_MARGIN - INNER_MARGIN;
        
        topLeft = new Point2D.Double( LEFT_MARGIN, TOP_MARGIN );
        topRight = new Point2D.Double( FRAME_WIDTH-RIGHT_MARGIN, TOP_MARGIN );
        bottomLeft = new Point2D.Double( LEFT_MARGIN, FRAME_HEIGHT-BOTTOM_MARGIN );
        bottomRight = new Point2D.Double( FRAME_WIDTH-RIGHT_MARGIN, FRAME_HEIGHT-BOTTOM_MARGIN );
        
        Container c = new Container();
        
        Object[] possibilities = { 
            DISPLAY_ARORA, DISPLAY_PARTITIONS, DISPLAY_PARTITIONS_AND_PORTALS, 
            DISPLAY_CONCORDE, DISPLAY_OPTIMAL };
        
        String s = (String)JOptionPane.showInputDialog(
                c,
                "Choose which tour to display",
                "Display type dialog",
                JOptionPane.PLAIN_MESSAGE,
                null,
                possibilities,
                DISPLAY_ARORA);
        
        if( s.compareTo(DISPLAY_ARORA)==0 ){
            displayArora = true;
        } else {
            displayArora = false;
        }
        
        if( s.compareTo(DISPLAY_PARTITIONS)==0 ){
            displayPartitions = true;
        } else {
            displayPartitions = false;
        }
        
        if( s.compareTo(DISPLAY_PARTITIONS_AND_PORTALS)==0 ){
            displayPartitionsAndPortals = true;
        } else {
            displayPartitionsAndPortals = false;
        }
        
        if( s.compareTo(DISPLAY_CONCORDE)==0 ){
            displayConcorde = true;
        } else {
            displayConcorde = false;
        }
        
        if( s.compareTo(DISPLAY_ACO)==0 ){
            displayAco = true;
        } else {
            displayAco = false;
        }
        if( s.compareTo(DISPLAY_OPTIMAL)==0 ){
            displayOptimal = true;
        } else {
            displayOptimal = false;
        }
        
        JFileChooser fc = new JFileChooser();
        int returnVal = fc.showOpenDialog(c);
        if(returnVal == JFileChooser.APPROVE_OPTION){
            File file = fc.getSelectedFile();
            this.setTitle(file.getName());
            readAroraTSPResultsFile( file );
            convertPoints();
            topEdge = new Line2D.Double( topLeft, topRight );
            rightEdge = new Line2D.Double( topRight, bottomRight );
            bottomEdge = new Line2D.Double( bottomLeft, bottomRight );
            leftEdge = new Line2D.Double( bottomLeft, topLeft );
            
            this.setSize( FRAME_WIDTH, FRAME_HEIGHT );
            this.setVisible( true );
        }
    }
    
    private void readAroraTSPResultsFile( File file ) throws FileNotFoundException, IOException {

        int i;
        Integer t;
        BufferedReader br = new BufferedReader( new FileReader( file ) );
        String s;
        String [] result;
        Double x, y, x1, y1, x2, y2;
        
        while( (s = br.readLine()) != null ){            
            result = s.split("\\s");
            System.out.println("result=" + result[0] );
            if(result[0].compareTo(NAME)==0){
                System.out.println("Name found: " + result[1]);
            }
            if(result[0].compareTo(TYPE)==0){
                System.out.println("Type found: " + result[1]);
            }
            if(result[0].compareTo(COMMENT)==0){
                System.out.print("Comment found: ");
                for(i=1; i<result.length; i++){
                    System.out.print(result[i] + " ");
                }
                System.out.print("\n");                    
            }
            /* original points */
            if(result[0].compareTo(ORIGINAL_POINTS)==0){
                System.out.println("Original points found: ");
                numOriginalPoints = (new Integer(result[1])).intValue();
                System.out.println("numOriginalPoints = " + numOriginalPoints );
                originalPoint = new PathPoint2D[numOriginalPoints];
                for(i=0; i < numOriginalPoints; i++){
                    s = br.readLine();
                    result = s.split("\\s");
                    x = new Double(result[0]);
                    y = new Double(result[1]);
                    originalPoint[i] = new PathPoint2D( PathPoint2D.DATA_POINT );
                    originalPoint[i].setLocation(x.doubleValue(), y.doubleValue());
                }
            }
            /* data points */
            if(result[0].compareTo(DATA_POINTS)==0){
                System.out.println("Data points found: ");
                numDataPoints = (new Integer(result[1])).intValue();
                System.out.println("numDataPoints = " + numDataPoints );
                dataPoint = new PathPoint2D[numDataPoints];
                for(i=0; i < numDataPoints; i++){
                    s = br.readLine();
                    result = s.split("\\s");
                    x = new Double(result[0]);
                    y = new Double(result[1]);
                    dataPoint[i] = new PathPoint2D( PathPoint2D.DATA_POINT );
                    dataPoint[i].setLocation(x.doubleValue(), y.doubleValue());
                }
            }
            /* portal points */
            if(result[0].compareTo(PORTALS)==0){
                System.out.print("Portals found: ");
                numPortalPoints = (new Integer(result[1])).intValue();
                System.out.println("numPortalPoints = " + numPortalPoints );
                portalPoint = new PathPoint2D[numPortalPoints];
                for(i=0; i < numPortalPoints; i++){
                    s = br.readLine();
                    result = s.split("\\s");
                    x1 = new Double(result[0]);
                    y1 = new Double(result[1]);
                    portalPoint[i] = new PathPoint2D( PathPoint2D.PORTAL_POINT );
                    portalPoint[i].setLocation(x1, y1);
                }
            }
            /* partitions */
            if(result[0].compareTo(PARTITIONS)==0){
                System.out.print("Partitions found: ");
                numPartitions = (new Integer(result[1])).intValue();
                System.out.println("numPartitions = " + numPartitions );
                partitionMin = new PathPoint2D[numPartitions];
                partitionMax = new PathPoint2D[numPartitions];
                for(i=0; i < numPartitions; i++){
                    s = br.readLine();
                    result = s.split("\\s");
                    x1 = new Double(result[0]);
                    y1 = new Double(result[1]);
                    x2 = new Double(result[2]);
                    y2 = new Double(result[3]);
                    partitionMin[i] = new PathPoint2D( PathPoint2D.PARTITION );
                    partitionMax[i] = new PathPoint2D( PathPoint2D.PARTITION );
                    partitionMin[i].setLocation(x1, y1);
                    partitionMax[i].setLocation(x2, y2);
                }
            }
            /* arora path */
            if(result[0].compareTo(ARORA_PATH)==0){
                System.out.print("Arora path found: ");
                numAroraPathPoints = (new Integer(result[1])).intValue();
                System.out.println("numAroraPathPoints = " + numAroraPathPoints );
                aroraPathPoint = new PathPoint2D[numAroraPathPoints];
                for(i=0; i < numAroraPathPoints; i++){
                    s = br.readLine();
                    result = s.split("\\s");
                    x = new Double(result[0]);
                    y = new Double(result[1]);
                    t = new Integer(result[2]);
                    System.out.println( "i="+ i + " x="+x+" y="+y+" t = " + t );
                    if(t.intValue()==PathPoint2D.DATA_POINT){
                    	System.out.println( "instantiating new DATA_POINT");
	                    aroraPathPoint[i] = new PathPoint2D( PathPoint2D.DATA_POINT );
	                    aroraPathPoint[i].setLocation(x.doubleValue(), y.doubleValue());
                    } else {
                    	if(t.intValue()==PathPoint2D.PORTAL_POINT){
                    		System.out.println( "instantiating new PORTAL_POINT");
    	                    aroraPathPoint[i] = new PathPoint2D( PathPoint2D.PORTAL_POINT );
    	                    aroraPathPoint[i].setLocation(x.doubleValue(), y.doubleValue());                    		
                    	} else {
                    		System.out.println( "instantiating new PARTITION");
    	                    aroraPathPoint[i] = new PathPoint2D( PathPoint2D.PARTITION );
    	                    aroraPathPoint[i].setLocation(x.doubleValue(), y.doubleValue());                    		
                    	}
                    }
                }
            }
            /* arora tour */
            if(result[0].compareTo(ARORA_TOUR)==0){
                System.out.print("Arora tour points found: ");
                numAroraTourPoints = (new Integer(result[1])).intValue();
                System.out.println("numAroraTourPoints = " + numAroraTourPoints );
                aroraTourPoint = new PathPoint2D[numAroraTourPoints];
                for(i=0; i < numAroraTourPoints; i++){
                    s = br.readLine();
                    result = s.split("\\s");
                    x1 = new Double(result[0]);
                    y1 = new Double(result[1]);
                    aroraTourPoint[i] = new PathPoint2D( PathPoint2D.DATA_POINT );
                    aroraTourPoint[i].setLocation(x1, y1);
                }
            }            
            /* optimal tour */
            if(result[0].compareTo(OPTIMAL_TOUR)==0){
                System.out.print("Optimal tour points found: ");
                numOptimalTourPoints = (new Integer(result[1])).intValue();
                System.out.println("numOptimalTourPoints = " + numOptimalTourPoints );
                optimalTourPoint = new PathPoint2D[numOptimalTourPoints];
                for(i=0; i < numOptimalTourPoints; i++){
                    s = br.readLine();
                    result = s.split("\\s");
                    x1 = new Double(result[0]);
                    y1 = new Double(result[1]);
                    optimalTourPoint[i] = new PathPoint2D( PathPoint2D.DATA_POINT );
                    optimalTourPoint[i].setLocation(x1, y1);
                }
            }
            /* concorde tour */
            if(result[0].compareTo(CONCORDE_TOUR)==0){
                System.out.print("Concorde tour points found: ");
                numConcordeTourPoints = (new Integer(result[1])).intValue();
                System.out.println("numAcoTourPoints = " + numConcordeTourPoints );
                concordeTourPoint = new PathPoint2D[numConcordeTourPoints];
                for(i=0; i < numConcordeTourPoints; i++){
                    s = br.readLine();
                    result = s.split("\\s");
                    x1 = new Double(result[0]);
                    y1 = new Double(result[1]);
                    concordeTourPoint[i] = new PathPoint2D( PathPoint2D.DATA_POINT );
                    concordeTourPoint[i].setLocation(x1, y1);
                }
            }
            /* aco tour */
            if(result[0].compareTo(ACO_TOUR)==0){
                System.out.print("ACO tour points found: ");
                numAcoTourPoints = (new Integer(result[1])).intValue();
                System.out.println("numAcoTourPoints = " + numAcoTourPoints );
                acoTourPoint = new PathPoint2D[numAcoTourPoints];
                for(i=0; i < numAcoTourPoints; i++){
                    s = br.readLine();
                    result = s.split("\\s");
                    x1 = new Double(result[0]);
                    y1 = new Double(result[1]);
                    acoTourPoint[i] = new PathPoint2D( PathPoint2D.DATA_POINT );
                    acoTourPoint[i].setLocation(x1, y1);
                }
            }
        }
        
        System.out.println("Done parsing tspr file");
        
    }

    
    private void convertPoints(){

    	int i;
    	double alpha, beta, minY, maxY, minX, maxX, width, height, x, y;
        double dx1, dy1, dx2, dy2;
        
        System.out.println("convert points");
    	    	
        // Place on fourth quadrant & translate to origin
    	minY = partitionMin[0].getY();
    	maxY = partitionMax[0].getY();
    	minX = partitionMin[0].getX();
    	maxX = partitionMax[0].getX();
    	
        for(i=0; i < partitionMin.length; i++){
            if(partitionMin[i].getX() < minX){
                minX = partitionMin[i].getX();
            }
            if(partitionMin[i].getY() < minY){
                minY = partitionMin[i].getY();
            }
            if(partitionMin[i].getX() > maxX){
                maxX = partitionMin[i].getX();
            }
            if(partitionMin[i].getY() > maxY){
                maxY = partitionMin[i].getY();
            }
        }
        for(i=0; i < partitionMax.length; i++){
            if(partitionMax[i].getX() < minX){
                minX = partitionMax[i].getX();
            }
            if(partitionMax[i].getY() < minY){
                minY = partitionMax[i].getY();
            }
            if(partitionMax[i].getX() > maxX){
                maxX = partitionMax[i].getX();
            }
            if(partitionMax[i].getY() > maxY){
                maxY = partitionMax[i].getY();
            }
        }
        dx1 = minX * (-1);
        dy1 = maxY * (-1);
        width = maxX - minX;
    	height = maxY - minY;
    	if( width > height ){
    		alpha = effectiveWidth / width;
    	} else {
    		alpha = effectiveHeight / height;
    	}                
        dx2 = LEFT_MARGIN + INNER_MARGIN;
        dy2 = TOP_MARGIN + INNER_MARGIN;
        
        // Conversion begins here
        
        // minX, minY, maxX, maxY
        minX = ( (minX + dx1) * alpha ) + dx2;
        minY = ( (minY + dy1) * (-1) * alpha ) + dy2;
        maxX = ( (maxX + dx1) * alpha ) + dx2;
        maxY = ( (maxY + dy1) * (-1) * alpha ) + dy2;
        boundingBoxTop = new Line2D.Double( minX, maxY, maxX, maxY );
        boundingBoxRight = new Line2D.Double( maxX, maxY, maxX, minY );
        boundingBoxBottom = new Line2D.Double( minX, minY, maxX, minY );
        boundingBoxLeft = new Line2D.Double( minX, minY, minX, maxY );
        // originalPoint
        if(numOriginalPoints > 0){
            for(i=0; i<originalPoint.length; i++){
                x = ( (originalPoint[i].getX() + dx1) * alpha ) + dx2;
                y = ( (originalPoint[i].getY() + dy1) * (-1) * alpha ) + dy2;
                originalPoint[i].setLocation(x, y);
            }
        }
        // dataPoint
        if(numDataPoints > 0){
            for(i=0; i<dataPoint.length; i++){
                x = ( (dataPoint[i].getX() + dx1) * alpha ) + dx2;
                y = ( (dataPoint[i].getY() + dy1) * (-1) * alpha ) + dy2;
                dataPoint[i].setLocation(x, y);
            }
        }
        
        // portalPoint
        if(numPortalPoints > 0){
            for(i=0; i<portalPoint.length; i++){
                x = ( (portalPoint[i].getX() + dx1) * alpha ) + dx2;
                y = ( (portalPoint[i].getY() + dy1) * (-1) * alpha ) + dy2;
                portalPoint[i].setLocation(x, y);
            }
        }
        // partitions
        if(numPartitions > 0){
            for(i=0; i<partitionMin.length; i++){
                x = ( (partitionMin[i].getX() + dx1) * alpha ) + dx2;
                y = ( (partitionMin[i].getY() + dy1) * (-1) * alpha ) + dy2;
                partitionMin[i].setLocation(x, y);                        
            }
            for(i=0; i<partitionMax.length; i++){
                x = ( (partitionMax[i].getX() + dx1) * alpha ) + dx2;
                y = ( (partitionMax[i].getY() + dy1) * (-1) * alpha ) + dy2;
                partitionMax[i].setLocation(x, y);
            }
        }
        // aroraPathPoint
        if(numAroraPathPoints > 0){
            for(i=0; i<aroraPathPoint.length; i++){
                x = ( (aroraPathPoint[i].getX() + dx1) * alpha ) + dx2;
                y = ( (aroraPathPoint[i].getY() + dy1) * (-1) * alpha ) + dy2;
                aroraPathPoint[i].setLocation(x, y);            
            }
        }
        // aroraTourPoint
        if(numAroraTourPoints > 0){
            for(i=0; i<aroraTourPoint.length; i++){
                x = ( (aroraTourPoint[i].getX() + dx1) * alpha ) + dx2;
                y = ( (aroraTourPoint[i].getY() + dy1) * (-1) * alpha ) + dy2;
                aroraTourPoint[i].setLocation(x, y);
            }
        }
        // optimalTourPoint
        if(numOptimalTourPoints > 0){
            for(i=0; i<optimalTourPoint.length; i++){
                x = ( (optimalTourPoint[i].getX() + dx1) * alpha ) + dx2;
                y = ( (optimalTourPoint[i].getY() + dy1) * (-1) * alpha ) + dy2;
                optimalTourPoint[i].setLocation(x, y);
            }
        }
        // concordeTourPoint
        if(numConcordeTourPoints > 0){
            for(i=0; i<concordeTourPoint.length; i++){
                x = ( (concordeTourPoint[i].getX() + dx1) * alpha ) + dx2;
                y = ( (concordeTourPoint[i].getY() + dy1) * (-1) * alpha ) + dy2;
                concordeTourPoint[i].setLocation(x, y);
            }
        }
        // acoTourPoint
        if(numAcoTourPoints > 0){
            for(i=0; i<acoTourPoint.length; i++){
                x = ( (acoTourPoint[i].getX() + dx1) * alpha ) + dx2;
                y = ( (acoTourPoint[i].getY() + dy1) * (-1) * alpha ) + dy2;
                acoTourPoint[i].setLocation(x, y);
            }
        }
                
    }
    
    
    @Override
    public void paint(Graphics g){
        
        int i, x, y, w, h, new_x, new_y;
        Polygon p;
        
        w = 10;
        h = 10;
        
        Graphics2D g2d = (Graphics2D)g;
        
        /*
        g2d.setPaint(Color.green);
        g2d.draw(topEdge);
        g2d.draw(rightEdge);
        g2d.draw(bottomEdge);
        g2d.draw(leftEdge);
        */
        
        /* 
        g2d.draw(boundingBoxTop);
        g2d.draw(boundingBoxRight);
        g2d.draw(boundingBoxBottom);
        g2d.draw(boundingBoxLeft);       
        
        
        */
        
        
        // originalPoint
        if(displayOriginalPoints){
            if(numOriginalPoints > 0){
                g2d.setPaint(Color.blue);
                for(i=0; i<originalPoint.length; i++){
                    new_x = (int)originalPoint[i].getX() - (int)(OVAL_DIAMETER/2);
                    new_y = (int)originalPoint[i].getY() - (int)(OVAL_DIAMETER/2);
                    g2d.drawOval(new_x, new_y, OVAL_DIAMETER, OVAL_DIAMETER);
                }
            }
        }
        
        
        // dataPoint
        if(displayDataPoints){
            if(numDataPoints > 0){
                g2d.setPaint(Color.green);
                for(i=0; i<dataPoint.length; i++){
                    new_x = (int)dataPoint[i].getX() - (int)(OVAL_DIAMETER/2);
                    new_y = (int)dataPoint[i].getY() - (int)(OVAL_DIAMETER/2);
                    g2d.drawOval(new_x, new_y, OVAL_DIAMETER, OVAL_DIAMETER);
                }
            }
        }
        
        if(displayPartitions){
            
            // partitions
            if(numPartitions > 0){
                for(i=0; i<partitionMin.length; i++){
                    p = new Polygon();
                    p.addPoint((int)partitionMin[i].getX(), (int)partitionMin[i].getY());
                    p.addPoint((int)partitionMin[i].getX(), (int)partitionMax[i].getY());
                    p.addPoint((int)partitionMax[i].getX(), (int)partitionMax[i].getY());
                    p.addPoint((int)partitionMax[i].getX(), (int)partitionMin[i].getY());
                    p.addPoint((int)partitionMin[i].getX(), (int)partitionMin[i].getY());            
                    g2d.drawPolygon(p);
                }
            }
            
            // aroraPathPoint
            if(numAroraPathPoints > 0){
                g2d.setPaint(Color.black);
                for(i=0; i<aroraPathPoint.length; i++){
                    if(aroraPathPoint[i].getType()==PathPoint2D.DATA_POINT){
                        /*
                        new_x = (int)aroraPathPoint[i].getX() - (int)(OVAL_DIAMETER/2);
                        new_y = (int)aroraPathPoint[i].getY() - (int)(OVAL_DIAMETER/2);
                        g2d.drawOval(new_x, new_y, OVAL_DIAMETER, OVAL_DIAMETER);
                        * 
                        */
                    } else {
                        
                        if(aroraPathPoint[i].getType()==PathPoint2D.PORTAL_POINT){
                            new_x = (int)aroraPathPoint[i].getX() - (int)(RECTANGLE_SIZE/2);
                            new_y = (int)aroraPathPoint[i].getY() - (int)(RECTANGLE_SIZE/2);
                            g2d.drawRect(new_x, new_y, RECTANGLE_SIZE, RECTANGLE_SIZE);
                        }
                     
                    }

                }

            }
            
        }
        
        if(displayPartitionsAndPortals){

            // portalPoint
            if(numPortalPoints > 0){
                g2d.setPaint(Color.black);
                for(i=0; i<portalPoint.length; i++){
                    new_x = (int)portalPoint[i].getX() - (int)(RECTANGLE_SIZE/2);
                    new_y = (int)portalPoint[i].getY() - (int)(RECTANGLE_SIZE/2);            
                    g2d.drawRect(new_x, new_y, RECTANGLE_SIZE, RECTANGLE_SIZE);
                }
            }
            
            // partitions
            if(numPartitions > 0){
                for(i=0; i<partitionMin.length; i++){
                    p = new Polygon();
                    p.addPoint((int)partitionMin[i].getX(), (int)partitionMin[i].getY());
                    p.addPoint((int)partitionMin[i].getX(), (int)partitionMax[i].getY());
                    p.addPoint((int)partitionMax[i].getX(), (int)partitionMax[i].getY());
                    p.addPoint((int)partitionMax[i].getX(), (int)partitionMin[i].getY());
                    p.addPoint((int)partitionMin[i].getX(), (int)partitionMin[i].getY());            
                    g2d.drawPolygon(p);
                }
            }
            
            // aroraPathPoint
            if(numAroraPathPoints > 0){
                g2d.setPaint(Color.black);
                for(i=0; i<aroraPathPoint.length; i++){
                    if(aroraPathPoint[i].getType()==PathPoint2D.DATA_POINT){
                        new_x = (int)aroraPathPoint[i].getX() - (int)(OVAL_DIAMETER/2);
                        new_y = (int)aroraPathPoint[i].getY() - (int)(OVAL_DIAMETER/2);
                        g2d.drawOval(new_x, new_y, OVAL_DIAMETER, OVAL_DIAMETER);
                    } else {
                        if(aroraPathPoint[i].getType()==PathPoint2D.PORTAL_POINT){
                            new_x = (int)aroraPathPoint[i].getX() - (int)(RECTANGLE_SIZE/2);
                            new_y = (int)aroraPathPoint[i].getY() - (int)(RECTANGLE_SIZE/2);
                            g2d.drawRect(new_x, new_y, RECTANGLE_SIZE, RECTANGLE_SIZE);
                        }
                    }

                }

            }
            
        }
        
        
        if(displayArora){
            
            // portalPoint
            if(numPortalPoints > 0){
                g2d.setPaint(Color.black);
                for(i=0; i<portalPoint.length; i++){
                    new_x = (int)portalPoint[i].getX() - (int)(RECTANGLE_SIZE/2);
                    new_y = (int)portalPoint[i].getY() - (int)(RECTANGLE_SIZE/2);            
                    g2d.drawRect(new_x, new_y, RECTANGLE_SIZE, RECTANGLE_SIZE);
                }
            }
            
            // partitions
            if(numPartitions > 0){
                for(i=0; i<partitionMin.length; i++){
                    p = new Polygon();
                    p.addPoint((int)partitionMin[i].getX(), (int)partitionMin[i].getY());
                    p.addPoint((int)partitionMin[i].getX(), (int)partitionMax[i].getY());
                    p.addPoint((int)partitionMax[i].getX(), (int)partitionMax[i].getY());
                    p.addPoint((int)partitionMax[i].getX(), (int)partitionMin[i].getY());
                    p.addPoint((int)partitionMin[i].getX(), (int)partitionMin[i].getY());            
                    g2d.drawPolygon(p);
                }
            }

            // aroraPathPoint
            if(numAroraPathPoints > 0){
                g2d.setPaint(Color.red);
                for(i=0; i<aroraPathPoint.length; i++){
                    if(aroraPathPoint[i].getType()==PathPoint2D.DATA_POINT){
                        new_x = (int)aroraPathPoint[i].getX() - (int)(OVAL_DIAMETER/2);
                        new_y = (int)aroraPathPoint[i].getY() - (int)(OVAL_DIAMETER/2);
                        g2d.drawOval(new_x, new_y, OVAL_DIAMETER, OVAL_DIAMETER);
                    } else {
                        if(aroraPathPoint[i].getType()==PathPoint2D.PORTAL_POINT){
                            new_x = (int)aroraPathPoint[i].getX() - (int)(RECTANGLE_SIZE/2);
                            new_y = (int)aroraPathPoint[i].getY() - (int)(RECTANGLE_SIZE/2);
                            g2d.drawRect(new_x, new_y, RECTANGLE_SIZE, RECTANGLE_SIZE);
                        }                
                    }

                }
                p = new Polygon();
                for(i=0; i<aroraPathPoint.length; i++){
                    p.addPoint( (int)aroraPathPoint[i].getX(), 
                                (int)aroraPathPoint[i].getY() );
                }
                g2d.drawPolygon(p);
            }
            
            // aroraTourPoint
            if(numAroraTourPoints > 0){
                g2d.setPaint(Color.green);
                p = new Polygon();
                for(i=0; i<aroraTourPoint.length; i++){
                    p.addPoint( (int)aroraTourPoint[i].getX(), 
                                (int)aroraTourPoint[i].getY() );
                }
                g2d.drawPolygon(p);
            }
                
        }
        if(displayAco){
            // acoTourPoint
            if(numAcoTourPoints > 0){
                g2d.setPaint(Color.ORANGE);
                p = new Polygon();
                for(i=0; i<acoTourPoint.length; i++){
                    p.addPoint( (int)acoTourPoint[i].getX(), 
                                (int)acoTourPoint[i].getY() );
                }
                g2d.drawPolygon(p);
            }
        }
        if(displayConcorde){
            // concordeTourPoint
            if(numConcordeTourPoints > 0){
                g2d.setPaint(Color.ORANGE);
                p = new Polygon();
                for(i=0; i<concordeTourPoint.length; i++){
                    p.addPoint( (int)concordeTourPoint[i].getX(), 
                                (int)concordeTourPoint[i].getY() );
                }
                g2d.drawPolygon(p);
            }
        }
        if(displayOptimal){
            // optimalTourPoint
            if(numOptimalTourPoints > 0){
                g2d.setPaint(Color.MAGENTA);
                p = new Polygon();
                for(i=0; i<optimalTourPoint.length; i++){
                    p.addPoint( (int)optimalTourPoint[i].getX(), 
                                (int)optimalTourPoint[i].getY() );
                }
                g2d.drawPolygon(p);
            }
        }
        
    }
    
    public static void main( String[] args ) throws FileNotFoundException, IOException {
        
        Arora_TSP_Path_Viewer atpv = new Arora_TSP_Path_Viewer();
        atpv.addWindowListener(
                new WindowAdapter(){
                    @Override
                    public void windowClosing(WindowEvent e){
                        System.exit(0);
                    }
                });
        
    }
    
}
