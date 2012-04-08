package fsg.dev.pzclone.pvr;

import processing.core.*; 

import fsg.pvzclone.pirateunits.*; 
import apwidgets.*; 
import android.widget.Toast; 

import fsg.pvzclone.pirateunits.*; 
import apwidgets.*; 

import android.view.MotionEvent; 
import android.view.KeyEvent; 
import android.graphics.Bitmap; 
import java.io.*; 
import java.util.*; 

public class PvZclone_v01 extends PApplet {





APWidgetContainer unitSelectorContainer;
int cols, rows;
Grid grid;
UnitGrid unitGrid;
UnitSelector unitSelector;
int selectedUnit;
Counters counters;
String[] pirateUnits;
ArrayList<Integer> unitButtonsList;

public void setup() {
  orientation(LANDSCAPE);
  pirateUnits = new String[] {
    null, "Bilge Rat", "Deckhand", "Pirate", "Buccaneer", "Coxswain", "Boatswain", "Exploding Bilge Rat", "Cannonier", "Quartermaster", "Captain", "Barrel of Rum", "Treasure Chest"
  };
  unitButtonsList = new ArrayList();
  for(int i = 1; i < pirateUnits.length; i++){
    unitButtonsList.add(i);
  }
  cols = 9;
  rows = 6;
  grid = new Grid(cols, rows);
  unitGrid = new UnitGrid(cols, rows, this);
  unitSelectorContainer = new APWidgetContainer(this);
  counters = new Counters();

  PVector origin = new PVector(0, 0);
  float usWidth = 100, usHeight = ((Cell)(grid.grid.get(0))).cellHeight*rows;
  Cell cell = (Cell)grid.grid.get(grid.referenceGrid[0][0]);
  usWidth = screenWidth/cols;
  usHeight = cell.cellHeight*rows;
  unitSelector = new UnitSelector(origin, usWidth, usHeight);
}

public void draw() {
  background(0);
  grid.displayGrid();
  unitSelector.displayUnitSelector();
  //pirate.displayPirate();
  if (!unitGrid.unitClasses.isEmpty()) {
    for (int i = 0; i < unitGrid.unitClasses.size();i++) {
      int unitClassId = unitGrid.unitClasses.get(i);
      switch(unitClassId) {
      case 1:         
        BilgeRat bilgeRat = (BilgeRat)unitGrid.unitArray.get(i);
        bilgeRat.displayBilgeRat();
        break;
      case 2:
        Deckhand deckhand = (Deckhand)unitGrid.unitArray.get(i);
        deckhand.displayDeckhand();
        break;
      case 3:
        Pirate pirate = (Pirate)unitGrid.unitArray.get(i);
        pirate.displayPirate();  
        break;
      case 4:
        Buccaneer buccaneer = (Buccaneer)unitGrid.unitArray.get(i);
        buccaneer.displayBuccaneer();   
        break;
      case 5:
        Coxswain coxswain = (Coxswain)unitGrid.unitArray.get(i);
        coxswain.displayCoxswain();  
        break;
      case 6:
        Boatswain boatswain = (Boatswain)unitGrid.unitArray.get(i);
        boatswain.displayBoatswain();   
        break;
      case 7:
        ExplodingBilgeRat explodingBilgeRat = (ExplodingBilgeRat)unitGrid.unitArray.get(i);
        explodingBilgeRat.displayExplodingBilgeRat();  
        break;
      case 8:
        Cannonier cannonier = (Cannonier)unitGrid.unitArray.get(i);
        cannonier.displayCannonier();         
        break;
      case 9:
        Quartermaster quartermaster = (Quartermaster)unitGrid.unitArray.get(i);
        quartermaster.displayQuartermaster(); 
        break;
      case 10:
        Captain captain = (Captain)unitGrid.unitArray.get(i);
        captain.displayCaptain();        
        break;
      case 11:
        BarrelOfRum barrelOfRum = (BarrelOfRum)unitGrid.unitArray.get(i);
        barrelOfRum.displayBarrelOfRum();   
        break;
      case 12:
        TreasureChest treasureChest = (TreasureChest)unitGrid.unitArray.get(i);
        treasureChest.displayTreasureChest();  
        break;
      case 0:
      default:
        break;
      }
    }
  }
}

public void keyPressed() {
  // doing other things here, and then:
  if (key == CODED && keyCode == KeyEvent.KEYCODE_BACK) {
    keyCode = 0;  // don't quit by default
  }
}

public boolean surfaceTouchEvent(MotionEvent event) {
  if (selectedUnit != 0) {
    float xTouch = event.getX();
    float yTouch = event.getY();
    for (int i = 0; i < grid.grid.size(); i++) {
      Cell cell = (Cell) grid.grid.get(i);
      if (cell.deployable) {
        if ((xTouch >= cell.origin.x && xTouch < cell.origin.x+cell.cellWidth) && (yTouch >= cell.origin.y && yTouch < cell.origin.y+cell.cellHeight)) { 
          PVector site = new PVector(cell.cellId.x, cell.cellId.y);
          deploy(this, selectedUnit, site);
          selectedUnit = 0;
        }
      }
    }
  }
  return super.surfaceTouchEvent(event);
}

public void onClickWidget(APWidget widget) {
  for (int i = 0; i < unitSelector.unitButtons.size(); i++) {
    UnitSelectorButton button = (UnitSelectorButton)unitSelector.unitButtons.get(i);
    if (widget == button.unitSelectorButton) {
      select(i+1);
      Toast.makeText(this, "Selected: "+button.pirateClass, Toast.LENGTH_SHORT).show();
    }
  }
}

// This is a class to handle the cells of the playing grid

public class Cell {

  private PVector origin; //where the cell will be drawn from
  public float cellWidth, cellHeight; // ...umm...duh
  private PVector anchorPoint; //the point at which allies will be fixed to within the space of the cell
  private PVector cellId; // the location id of the cell
  public boolean deployable;

  Cell(PVector _origin, float _cellWidth, float _cellHeight, PVector _cellId, boolean _deployable) {
    origin = _origin;
    cellWidth = _cellWidth;
    cellHeight = _cellHeight;
    anchorPoint = new PVector(origin.x+cellWidth/2, origin.y+cellHeight*.75f);
    cellId = _cellId;
    deployable = _deployable;
  }

  public void displayCell() {
    PVector p1 = new PVector(this.origin.x, this.origin.y);
    PVector p2 = new PVector(this.origin.x+this.cellWidth, this.origin.y);
    PVector p3 = new PVector(this.origin.x+this.cellWidth, this.origin.y+this.cellHeight);
    PVector p4 = new PVector(this.origin.x, this.origin.y+this.cellHeight);
    int fillColor = color(0, 0, 0, 0);
    int strokeColor = color(42, 215, 229, 255);

    DrawRect(p1, p2, p3, p4, fillColor, strokeColor, 0, 0);
    if (this.deployable == true) {
      ellipseMode(CENTER);
      ellipse(this.anchorPoint.x, this.anchorPoint.y, 7.5f, 7.5f);
    }
  }
}

// This is a class that functions as the base architecture of the playing field

public class Grid {

  private int rows, cols;
  private float cellWidth, cellHeight;
  private float padding; //padding around cells to accomodate for overlap
  ArrayList grid; //  this will house information for the 'grid' that will act as the playing field
  int[][] referenceGrid;

  Grid(int _cols, int _rows) {
    rows = _rows;
    cols = _cols+2;
    cellWidth = screenWidth/this.cols;
    cellHeight = screenHeight/this.rows;
    padding = 0.5f;
    grid = new ArrayList();
    gridInit();
  }

  // gridInit() builds the grid for the level, it does this only once, when the grid is instantiated
  public void gridInit() {
    referenceGrid = new int[this.cols][this.rows];
    float xCounter = 0.0f, yCounter = 0.0f; // placemarkers for the building of the grid, 
    // they keep track of where each origin of each 
    // cell is located on the screen
    int counter = 0;
    for (int i = 0; i < this.cols; i++) {
      for (int x = 0; x < this.rows; x++) {
        //create a temporary pvector to use as an origin point in the creation of the cells 
        PVector tempVector = new PVector(xCounter, yCounter); 
        PVector cellId = new PVector(i, x);

        // create a new cell in the grid at location referenceGrid.[i][x] and if it is the first make it 1.5 the regular width
        // or if it is the last cell in the each row make it half the regular cellWidth otherwise 
        // it will be the full cellWidth
        if (i == 0) {
          if (x == this.rows-1) {
            tempVector.x += padding;
            this.grid.add(counter, new Cell(tempVector, cellWidth*1.5f, cellHeight-padding, cellId, false));
            this.referenceGrid[i][x] = counter;
            counter++;
          } 
          else {
            this.grid.add(counter, new Cell(tempVector, cellWidth*1.5f, cellHeight, cellId, false));
            this.referenceGrid[i][x] = counter;
            counter++;
          }
        }
        else if (i == this.cols-1) {
          if (x == this.rows-1) {
            this.grid.add(counter, new Cell(tempVector, screenWidth-xCounter-padding, cellHeight-padding, cellId, false));
            this.referenceGrid[i][x] = counter;
            counter++;
          } 
          else {
            this.grid.add(counter, new Cell(tempVector, screenWidth-xCounter-padding, cellHeight, cellId, false));
            this.referenceGrid[i][x] = counter;
            counter++;
          }
        }
        else {
          if (x == this.rows-1) {
            this.grid.add(counter, new Cell(tempVector, cellWidth, cellHeight-padding, cellId, true));
            this.referenceGrid[i][x] = counter;
            counter++;
          } 
          else {
            this.grid.add(counter, new Cell(tempVector, cellWidth, cellHeight, cellId, true));
            this.referenceGrid[i][x] = counter;
            counter++;
          }
        }

        //println("("+i+", "+x+") - "+xCounter+", "+yCounter+" - "+cellWidth+", "+cellHeight);
        // update yCounter for the next cell
        yCounter += cellHeight;
      }
      // update xCounter for the next column
      if (i == 0) {
        xCounter += cellWidth*1.5f;
      }
      else {
        xCounter += cellWidth+padding;
      }
      // reset yCounter for the next column
      yCounter = 0.0f;
    }
  }

  // displayGrid()... well it...displays the grid
  public void displayGrid() {
    for (int i = 0; i < cols; i++) {
      for (int x = 0; x < rows; x++) {
        Cell cell = (Cell) this.grid.get(this.referenceGrid[i][x]);
        cell.displayCell();
      }
    }
  }
}

class Counters {

  int bilgeRatCounter; 
  int deckhandCounter;
  int pirateCounter;
  int buccaneerCounter;
  int coxswainCounter;
  int boatswainCounter;
  int explodingBilgeRatCounter;
  int cannonierCounter;
  int quartermasterCounter;
  int captainCounter;
  int barrelOfRumCounter;
  int treasureChestCounter;

  Counters() {
    bilgeRatCounter = 1; 
    deckhandCounter = 1;
    pirateCounter = 1;
    buccaneerCounter = 1;
    coxswainCounter = 1;
    boatswainCounter = 1;
    explodingBilgeRatCounter = 1;
    cannonierCounter = 1;
    quartermasterCounter = 1;
    captainCounter = 1;
    barrelOfRumCounter = 1;
    treasureChestCounter = 1;
  }
}

public void deploy(PApplet parent, int classId, PVector deploySite) {
  switch(classId) {
  case 1:
    println("Sending deploy("+classId+", "+deploySite+") command to unitGrid.");
    unitGrid.insertUnit(classId, deploySite); 
    break;
  case 2:
    unitGrid.insertUnit(classId, deploySite);      
    break;
  case 3:
    unitGrid.insertUnit(classId, deploySite);      
    break;
  case 4:
    unitGrid.insertUnit(classId, deploySite);      
    break;
  case 5:
    unitGrid.insertUnit(classId, deploySite);      
    break;
  case 6:
    unitGrid.insertUnit(classId, deploySite);      
    break;
  case 7:
    unitGrid.insertUnit(classId, deploySite);      
    break;
  case 8:
    unitGrid.insertUnit(classId, deploySite);      
    break;
  case 9:
    unitGrid.insertUnit(classId, deploySite);      
    break;
  case 10:
    unitGrid.insertUnit(classId, deploySite);      
    break;
  case 11:
    unitGrid.insertUnit(classId, deploySite);      
    break;
  case 12:
    unitGrid.insertUnit(classId, deploySite);      
    break;
  case 0:
  default:
    break;
  }
}

public void select(int buttonId) {
  for (int i = 0; i < unitSelector.unitButtons.size(); i++) {
    UnitSelectorButton tempButton = (UnitSelectorButton)unitSelector.unitButtons.get(i);
    if(tempButton.buttonId == buttonId){ 
      for(int x = 0; x < pirateUnits.length; x++){
        if(pirateUnits[x] == tempButton.pirateClass){
          selectedUnit = x;
          println("Selected unit: "+selectedUnit);
        }
      }                
    }
  }
}

public class UnitGrid {

  int classId;
  PVector deploySite;
  ArrayList<Object> unitArray;
  PApplet parent;
  int[][] referenceGrid;
  int cols, rows;
  ArrayList<Integer> unitClasses;

  UnitGrid(int _cols, int _rows, PApplet _parent) {
    cols = _cols;
    rows = _rows;
    unitArray = new ArrayList(cols*rows);
    parent = _parent;
    initReferenceGrid();
    unitClasses = new ArrayList();
  }

  private void initReferenceGrid() {
    int counter = 0;
    referenceGrid = new int[grid.cols][grid.rows];
    for (int i = 0; i< referenceGrid.length; i++) {
      for (int x = 0; x < referenceGrid[i].length; x++) {
        referenceGrid[i][x] = counter;
        counter++;
      }
    }
  }

  public void insertUnit(int classId, PVector deploySite) {
    println("Inserting Unit["+classId+"]...");
    switch(classId) {
    case 1:
      this.unitArray.add((new PiratesWrapper(parent, classId, new PVector((int)deploySite.x, (int)deploySite.y))).getThisUnit());
      println("Executed insertion of: "+pirateUnits[classId]);
      break;
    case 2:
      this.unitArray.add((new PiratesWrapper(parent, classId, new PVector((int)deploySite.x, (int)deploySite.y))).getThisUnit());
      println("Executed insertion of: "+pirateUnits[classId]);
      break;
    case 3:
      this.unitArray.add((new PiratesWrapper(parent, classId, new PVector((int)deploySite.x, (int)deploySite.y))).getThisUnit());
      println("Executed insertion of: "+pirateUnits[classId]);
      break;
    case 4:
      this.unitArray.add((new PiratesWrapper(parent, classId, new PVector((int)deploySite.x, (int)deploySite.y))).getThisUnit());
      println("Executed insertion of: "+pirateUnits[classId]);
      break;
    case 5:
      this.unitArray.add((new PiratesWrapper(parent, classId, new PVector((int)deploySite.x, (int)deploySite.y))).getThisUnit());
      println("Executed insertion of: "+pirateUnits[classId]);
      break;
    case 6:
      this.unitArray.add((new PiratesWrapper(parent, classId, new PVector((int)deploySite.x, (int)deploySite.y))).getThisUnit());
      println("Executed insertion of: "+pirateUnits[classId]);
      break;
    case 7:
      this.unitArray.add((new PiratesWrapper(parent, classId, new PVector((int)deploySite.x, (int)deploySite.y))).getThisUnit());
      println("Executed insertion of: "+pirateUnits[classId]);
      break;
    case 8:
      this.unitArray.add((new PiratesWrapper(parent, classId, new PVector((int)deploySite.x, (int)deploySite.y))).getThisUnit());
      println("Executed insertion of: "+pirateUnits[classId]);
      break;
    case 9:
      this.unitArray.add((new PiratesWrapper(parent, classId, new PVector((int)deploySite.x, (int)deploySite.y))).getThisUnit());
      println("Executed insertion of: "+pirateUnits[classId]);
      break;
    case 10:
      this.unitArray.add((new PiratesWrapper(parent, classId, new PVector((int)deploySite.x, (int)deploySite.y))).getThisUnit());
      println("Executed insertion of: "+pirateUnits[classId]);
      break;
    case 11:
      this.unitArray.add((new PiratesWrapper(parent, classId, new PVector((int)deploySite.x, (int)deploySite.y))).getThisUnit());
      println("Executed insertion of: "+pirateUnits[classId]);
      break;
    case 12:
      this.unitArray.add((new PiratesWrapper(parent, classId, new PVector((int)deploySite.x, (int)deploySite.y))).getThisUnit());
      println("Executed insertion of: "+pirateUnits[classId]);
      break;
    default:
      break;
    }
  }
}

public class UnitSelector {

  public float buttonWidth;
  public float buttonHeight;
  private float selectorWidth;
  private float selectorHeight;
  private PVector origin;
  public ArrayList unitButtons;
  private float padding;

  public UnitSelector(PVector _origin, float _selectorWidth, float _selectorHeight) {
    this.unitButtons = new ArrayList();
    this.origin = _origin;
    this.padding = 5;
    this.selectorWidth = _selectorWidth;
    this.selectorHeight = _selectorHeight; 
    calculateButtonSize();
    makeButtons();
  }

  public void displayUnitSelector() {
    noFill();
    smooth();
    stroke(0xFFBC3333);
    strokeWeight(4.0f);
    strokeJoin(ROUND);
    beginShape();
    vertex(this.origin.x, this.origin.y);
    vertex(this.origin.x+this.selectorWidth, this.origin.y);
    vertex(this.origin.x+this.selectorWidth, this.origin.y+this.selectorHeight);
    vertex(this.origin.x, this.origin.y+this.selectorHeight);
    vertex(this.origin.x, this.origin.y);
    endShape();
  }

  private void calculateButtonSize() {
    int numOfUnits = unitButtonsList.size();
    this.buttonWidth = selectorWidth-(this.padding*2);
    this.buttonHeight = (selectorHeight/numOfUnits);
  }

  private void makeButtons() {
    for (int i = 0; i <  unitButtonsList.size(); i++) {
      PVector vec = new PVector(this.origin.x+padding, this.origin.y+padding+i*buttonHeight);
      this.unitButtons.add(new UnitSelectorButton(this.buttonWidth, this.buttonHeight, vec, pirateUnits[unitButtonsList.get(i)], i+1));
    }
  }
}

public class UnitSelectorButton {

  float buttonWidth, buttonHeight;
  PVector buttonVec;
  String pirateClass;
  APButton unitSelectorButton;
  public int buttonId;
  
  UnitSelectorButton(float _buttonWidth, float _buttonHeight, PVector _buttonVec, String _pirateClass, int _buttonId) {
    buttonWidth = _buttonWidth;
    buttonHeight = _buttonHeight;
    buttonVec = _buttonVec;
    pirateClass = _pirateClass;
    buttonId = _buttonId;
    generateUnitSelectorButton();
  }

  public void generateUnitSelectorButton() {
     unitSelectorButton = new APButton(PApplet.parseInt(this.buttonVec.x), PApplet.parseInt(this.buttonVec.y), PApplet.parseInt(this.buttonWidth), PApplet.parseInt(this.buttonHeight), this.pirateClass);
     unitSelectorButton.setTextSize(8);
     unitSelectorContainer.addWidget(unitSelectorButton);
     println("Created button at: "+buttonVec.x+", "+buttonVec.y+" with a size of: "+this.buttonWidth+", "+this.buttonHeight+" of class: "+this.pirateClass);
  }
  
} 


//Pirates is a wrapper class for the different types of pirate units

public class PiratesWrapper {

  PApplet parent;
  int classId;
  ArrayList pirates;
  String pirateClass;
  PVector unitLoc;
  
  PiratesWrapper(PApplet _parent, int _classId, PVector _unitLoc) {
    parent = _parent;
    classId = _classId;
    pirateClass = pirateUnits[classId];
    unitLoc = _unitLoc;
  }

  public Object getThisUnit() {
    switch(classId) {
    case 1:
      println("Deployed ["+pirateUnits[classId]+"]");
      return(getBilgeRat());
    case 2:
    	println("Deployed ["+pirateUnits[classId]+"]");
      return(getDeckhand());
    case 3:
    	println("Deployed ["+pirateUnits[classId]+"]");
      return(getPirate());
    case 4:     
    	println("Deployed ["+pirateUnits[classId]+"]");
      return(getBuccaneer());
    case 5:
    	println("Deployed ["+pirateUnits[classId]+"]");
      return(getCoxswain());
    case 6:
    	println("Deployed ["+pirateUnits[classId]+"]");
      return(getBoatswain());
    case 7:
    	println("Deployed ["+pirateUnits[classId]+"]");
      return(getExplodingBilgeRat());
    case 8:
    	println("Deployed ["+pirateUnits[classId]+"]");
      return(getCannonier());
    case 9:
    	println("Deployed ["+pirateUnits[classId]+"]");
      return(getQuartermaster());
    case 10:
    	println("Deployed ["+pirateUnits[classId]+"]");
      return(getCaptain());
    case 11:
    	println("Deployed ["+pirateUnits[classId]+"]");
      return(getBarrelOfRum());
    case 12:
    	println("Deployed ["+pirateUnits[classId]+"]");
      return(getTreasureChest());
    case 0: 
    default: 
      break;
    }
    return null;
  }

  public BilgeRat getBilgeRat() {
    Cell cell = (Cell)grid.grid.get(grid.referenceGrid[(int)unitLoc.x][(int)unitLoc.y]);
    float unitWidth = cell.cellWidth*.7f, unitHeight = cell.cellHeight*.85f;
    PVector pinPoint = new PVector(cell.anchorPoint.x, cell. anchorPoint.y);
    int id = counters.bilgeRatCounter;
    counters.bilgeRatCounter++;
    unitGrid.unitClasses.add(classId);
    return(new BilgeRat(this.parent, pirateUnits[classId], pinPoint, unitWidth, unitHeight, classId, counters.bilgeRatCounter));
  }

  public Deckhand getDeckhand() {
    Cell cell = (Cell)grid.grid.get(grid.referenceGrid[(int)unitLoc.x][(int)unitLoc.y]);
    float unitWidth = cell.cellWidth*.7f, unitHeight = cell.cellHeight*.85f;
    PVector pinPoint = new PVector(cell.anchorPoint.x, cell. anchorPoint.y);
    int id = counters.deckhandCounter;
    counters.deckhandCounter++;
    unitGrid.unitClasses.add(classId);
    return(new Deckhand(this.parent, pirateUnits[classId], pinPoint, unitWidth, unitHeight, classId, counters.bilgeRatCounter));
  }

  public Pirate getPirate() {
    Cell cell = (Cell)grid.grid.get(grid.referenceGrid[(int)unitLoc.x][(int)unitLoc.y]);
    float unitWidth = cell.cellWidth*.7f, unitHeight = cell.cellHeight*.85f;
    PVector pinPoint = new PVector(cell.anchorPoint.x, cell. anchorPoint.y);
    int id = counters.pirateCounter;
    counters.pirateCounter++;
    unitGrid.unitClasses.add(classId);
    return(new Pirate(this.parent, pirateUnits[classId], pinPoint, unitWidth, unitHeight, classId, counters.bilgeRatCounter));
  }

  public Buccaneer getBuccaneer() {
    Cell cell = (Cell)grid.grid.get(grid.referenceGrid[(int)unitLoc.x][(int)unitLoc.y]);
    float unitWidth = cell.cellWidth*.7f, unitHeight = cell.cellHeight*.85f;
    PVector pinPoint = new PVector(cell.anchorPoint.x, cell. anchorPoint.y);
    int id = counters.buccaneerCounter;
    counters.buccaneerCounter++;
    unitGrid.unitClasses.add(classId);
    return(new Buccaneer(this.parent, pirateUnits[classId], pinPoint, unitWidth, unitHeight, classId, counters.buccaneerCounter));
  }

  public Coxswain getCoxswain() {
   Cell cell = (Cell)grid.grid.get(grid.referenceGrid[(int)unitLoc.x][(int)unitLoc.y]);
    float unitWidth = cell.cellWidth*.7f, unitHeight = cell.cellHeight*.85f;
    PVector pinPoint = new PVector(cell.anchorPoint.x, cell. anchorPoint.y);
    int id = counters.coxswainCounter;
    counters.coxswainCounter++;
    unitGrid.unitClasses.add(classId);
    return(new Coxswain(this.parent, pirateUnits[classId], pinPoint, unitWidth, unitHeight, classId, counters.coxswainCounter));
  }

  public Boatswain getBoatswain() {
    Cell cell = (Cell)grid.grid.get(grid.referenceGrid[(int)unitLoc.x][(int)unitLoc.y]);
    float unitWidth = cell.cellWidth*.7f, unitHeight = cell.cellHeight*.85f;
    PVector pinPoint = new PVector(cell.anchorPoint.x, cell. anchorPoint.y);
    int id = counters.boatswainCounter;
    counters.boatswainCounter++;
    unitGrid.unitClasses.add(classId);
    return(new Boatswain(this.parent, pirateUnits[classId], pinPoint, unitWidth, unitHeight, classId, counters.boatswainCounter));
  }

  public ExplodingBilgeRat getExplodingBilgeRat() {
    Cell cell = (Cell)grid.grid.get(grid.referenceGrid[(int)unitLoc.x][(int)unitLoc.y]);
    float unitWidth = cell.cellWidth*.7f, unitHeight = cell.cellHeight*.85f;
    PVector pinPoint = new PVector(cell.anchorPoint.x, cell. anchorPoint.y);
    int id = counters.explodingBilgeRatCounter;
    counters.explodingBilgeRatCounter++;
    unitGrid.unitClasses.add(classId);
    return(new ExplodingBilgeRat(this.parent, pirateUnits[classId], pinPoint, unitWidth, unitHeight, classId, counters.explodingBilgeRatCounter));
  }

  public Cannonier getCannonier() {
    Cell cell = (Cell)grid.grid.get(grid.referenceGrid[(int)unitLoc.x][(int)unitLoc.y]);
    float unitWidth = cell.cellWidth*.7f, unitHeight = cell.cellHeight*.85f;
    PVector pinPoint = new PVector(cell.anchorPoint.x, cell. anchorPoint.y);
    int id = counters.cannonierCounter;
    counters.cannonierCounter++;
    unitGrid.unitClasses.add(classId);
    return(new Cannonier(this.parent, pirateUnits[classId], pinPoint, unitWidth, unitHeight, classId, counters.cannonierCounter));
  }

  public Quartermaster getQuartermaster() {
    Cell cell = (Cell)grid.grid.get(grid.referenceGrid[(int)unitLoc.x][(int)unitLoc.y]);
    float unitWidth = cell.cellWidth*.7f, unitHeight = cell.cellHeight*.85f;
    PVector pinPoint = new PVector(cell.anchorPoint.x, cell. anchorPoint.y);
    int id = counters.quartermasterCounter;
    counters.quartermasterCounter++;
    unitGrid.unitClasses.add(classId);
    return(new Quartermaster(this.parent, pirateUnits[classId], pinPoint, unitWidth, unitHeight, classId, counters.quartermasterCounter));
  }

  public Captain getCaptain() {
    Cell cell = (Cell)grid.grid.get(grid.referenceGrid[(int)unitLoc.x][(int)unitLoc.y]);
    float unitWidth = cell.cellWidth*.7f, unitHeight = cell.cellHeight*.85f;
    PVector pinPoint = new PVector(cell.anchorPoint.x, cell. anchorPoint.y);
    int id = counters.captainCounter;
    counters.captainCounter++;
    unitGrid.unitClasses.add(classId);
    return(new Captain(this.parent, pirateUnits[classId], pinPoint, unitWidth, unitHeight, classId, counters.captainCounter));
  }

  public BarrelOfRum getBarrelOfRum() {
    Cell cell = (Cell)grid.grid.get(grid.referenceGrid[(int)unitLoc.x][(int)unitLoc.y]);
    float unitWidth = cell.cellWidth*.7f, unitHeight = cell.cellHeight*.85f;
    PVector pinPoint = new PVector(cell.anchorPoint.x, cell. anchorPoint.y);
    int id = counters.barrelOfRumCounter;
    counters.barrelOfRumCounter++;
    unitGrid.unitClasses.add(classId);
    return(new BarrelOfRum(this.parent, pirateUnits[classId], pinPoint, unitWidth, unitHeight, classId, counters.barrelOfRumCounter));
  }

  public TreasureChest getTreasureChest() {
    Cell cell = (Cell)grid.grid.get(grid.referenceGrid[(int)unitLoc.x][(int)unitLoc.y]);
    float unitWidth = cell.cellWidth*.7f, unitHeight = cell.cellHeight*.85f;
    PVector pinPoint = new PVector(cell.anchorPoint.x, cell. anchorPoint.y);
    int id = counters.treasureChestCounter;
    counters.treasureChestCounter++;
    unitGrid.unitClasses.add(classId);
    return(new TreasureChest(this.parent, pirateUnits[classId], pinPoint, unitWidth, unitHeight, classId, counters.treasureChestCounter));
  }
}

public void DrawRect(PVector p1, PVector p2, PVector p3, PVector p4, int _fillColor, int _strokeColor, float hDisplace, float vDisplace) {
  smooth();
  strokeJoin(ROUND);
  strokeWeight(2.0f);
  int fillColor = _fillColor;
  int strokeColor = _strokeColor;
  
  fill(fillColor);
  stroke(strokeColor);
  
  beginShape();
  vertex(p1.x+hDisplace, p1.y+vDisplace);
  vertex(p2.x+hDisplace, p2.y+vDisplace);
  vertex(p3.x+hDisplace, p3.y+vDisplace);
  vertex(p4.x+hDisplace, p4.y+vDisplace);
  vertex(p1.x+hDisplace, p1.y+vDisplace);
  endShape();
}



}
