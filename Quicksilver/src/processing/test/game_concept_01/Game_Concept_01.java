package processing.test.game_concept_01;

import processing.core.*; 

import android.widget.Toast; 

import android.view.MotionEvent; 
import android.view.KeyEvent; 
import android.graphics.Bitmap; 
import java.io.*; 
import java.util.*; 

public class Game_Concept_01 extends PApplet {



ArrayList<SquareSprite> squares;
ArrayList<SquareSprite> activeSprites;
ArrayList<ParticleSystem> particleSystems;

public void setup() {
  squares = new ArrayList();
  activeSprites = new ArrayList();
  particleSystems = new ArrayList();
  squares.add(new SquareSprite(new PVector(width/2+100, height/2-100), new PVector(random(.05f, 4), random(.05f, 4))));
  squares.add(new SquareSprite(new PVector(width/2-100, height/2-100), new PVector(random(.05f, 4), random(.05f, 4))));
  squares.add(new SquareSprite(new PVector(width/2-100, height/2+100), new PVector(random(.05f, 4), random(.05f, 4))));
  squares.add(new SquareSprite(new PVector(width/2+100, height/2+100), new PVector(random(.05f, 4), random(.05f, 4))));
}

public void draw() {
  background(0);
  for (int i = 0; i < squares.size(); i++) {
    SquareSprite sprite = squares.get(i);
    //println(sprite.vel.x+", "+sprite.vel.y);
    sprite.checkCollision();
    sprite.updateLoc();
    sprite.displaySprite();
  }
  if (!particleSystems.isEmpty()) {
    for (int x = 0; x < particleSystems.size(); x++) {
      ParticleSystem partSys = particleSystems.get(x);
      partSys.displaySystem();
    }
  }
  displayConnections(activeSprites);
}

public boolean surfaceTouchEvent(MotionEvent event) {
  switch(event.getAction()) {
  case MotionEvent.ACTION_DOWN:
    for (int i = 0; i < squares.size(); i++) {
      SquareSprite sprite = squares.get(i);
      if ((dist(sprite.loc.x, sprite.loc.y, event.getRawX(), event.getRawY())) < 28) {
        sprite.activate();
        activeSprites.add(sprite);
      }
    }
    break;
  case MotionEvent.ACTION_MOVE:
    if (activeSprites.size() == 1) {
      SquareSprite sprite = activeSprites.get(0);
      smooth();
      stroke(255, 191);
      strokeWeight(3);
      line(sprite.loc.x, sprite.loc.y, event.getHistoricalX(0), event.getHistoricalY(0));
    }
    for (int i = 0; i < squares.size(); i++) {
      SquareSprite sprite = squares.get(i);
      if ((dist(sprite.loc.x, sprite.loc.y, event.getHistoricalX(0), event.getHistoricalY(0))) < 28) {
        sprite.activate();
        activeSprites.add(sprite);
      }
    }

    break;
  case MotionEvent.ACTION_UP:
    if (!activeSprites.isEmpty() && checkCompletion(activeSprites)) {
      activeSprites.clear();
    }
    ParticleSystem ps = new ParticleSystem(new PVector(width/2, height/2), 100);
    ps.generateSystem();
    particleSystems.add(ps);
    Toast.makeText(this, "Particle System Added.", Toast.LENGTH_SHORT).show();
  default:
    break;
  }
  return super.surfaceTouchEvent(event);
}

/////////////////////////////////////////////////////
//checkCompletion
/////////////////////////////////////////////////////

public boolean checkCompletion(ArrayList<SquareSprite> toCheck) {
  int counter = 0;
  boolean check = false;
  if (toCheck.size() > 1) {
    if (toCheck.get(0).equals(toCheck.get(toCheck.size()-1))) {
      for (int i = 0; i < toCheck.size(); i++) {
        SquareSprite sprite = toCheck.get(i);
        if (sprite.touched) {
          counter++;
        }
      }
      if (counter == toCheck.size()) {
        for (int i = 0; i < toCheck.size(); i++) {
          SquareSprite sprite = toCheck.get(i);
          sprite.deactivate();
          sprite.kill();
        }
        check = true;
      }
    }
  }
  return check;
}

/////////////////////////////////////////////////////
//displayConnections
/////////////////////////////////////////////////////

public void displayConnections(ArrayList<SquareSprite> toDisplay) {
  for (int i = 0; i < toDisplay.size()-1; i++) {
    SquareSprite square1 = toDisplay.get(i);
    SquareSprite square2 = toDisplay.get(i+1);
    smooth();
    stroke(255, 191);
    strokeWeight(3);
    line(square1.loc.x, square1.loc.y, square2.loc.x, square2.loc.y);
  }
}

class Particle {

  PVector loc, vel;
  float pSize, life, totalLife;
  int id;

  public Particle(PVector _loc, PVector _vel, float _pSize, float _life, int _id) {
    this.loc = _loc;
    this.vel = _vel;
    this.life = this.totalLife =  _life;
    this.pSize = _pSize;
    id = _id;
  }

  public void displayParticle() {
    //println(this.life*(255/totalLife));
    stroke(this.life*(255/totalLife));
    fill(this.life*(255/totalLife));
    //pSize = pSize*(pSize/totalLife);
    ellipseMode(CENTER);
    ellipse(this.loc.x, this.loc.y, this.pSize, this.pSize);
  }

  public void particleBurst() {
    this.loc.add(this.vel);
  }
  
  public void particleGravity(){
    this.vel.y += .05f;
  }
}

class ParticleSystem {

  ArrayList<Particle> particles;
  PVector loc;
  int numOfParticles;

  public ParticleSystem(PVector _loc, int _numOfParticles) {
    this.loc = _loc;
    this.numOfParticles = _numOfParticles;
    particles = new ArrayList();
  }

  public void generateSystem() {
    for (int i = 0; i < this.numOfParticles; i++) {
      float pSize = 50;
      float life = random(50, 200);
      PVector pVec = new PVector(random(-2, 2), random(-1.5f, 1.5f));
      particles.add(new Particle(this.loc, pVec, pSize, life, i));
    }
  }

  public void displaySystem() {
    for (int i = 0; i < this.particles.size(); i++) {
      Particle particle = particles.get(i);
      particle.displayParticle();
      particle.particleBurst();
      particle.particleGravity();
      particle.life -= 1;
      if (particle.life <=0) {
        this.particles.remove(i);
      }
    }
  }
}

class SpriteWrapper{
  
  public SpriteWrapper(){
    
  }
  
}
class CircleSprite{
  
  PVector vel;
  PVector loc;
  PImage circleSprite;
  boolean touched;
  boolean dead;
  
  public CircleSprite(PVector _loc, PVector _vel){
    orientation(LANDSCAPE);
    this.loc = _loc;
    this.vel = _vel;
    this.circleSprite = loadImage("circlesprite.png");
    this.touched = false;
    this.dead = false;
  }
  
  public void displaySprite(){
    if(dead){
      this.circleSprite = loadImage("circlespritedead.png");
    }
    else if(!touched){
      this.circleSprite = loadImage("circlesprite.png");
    }
    else {
      this.circleSprite = loadImage("circlespriteglow.png");
    }
    
    imageMode(CENTER);
    image(circleSprite, loc.x, loc.y);
  }
  
  public void kill(){
    this.dead = true;
  }
  
  public void activate(){
    if(!touched){
      this.touched = true;
    }
  }
  
  public void deactivate(){
    this.touched = false;
  }
  
  public void updateLoc(){
    this.loc.x += this.vel.x;
    this.loc.y += this.vel.y;
  }
  
  public void checkCollision(){
    if(this.loc.x <= circleSprite.width/2 || this.loc.x >= screenWidth-circleSprite.width/2){
      this.vel.x *= -1;
    }
    if(this.loc.y <= circleSprite.height/2 || this.loc.y >= screenHeight-circleSprite.width/2){
      this.vel.y *= -1;
    }
  }
  
}
class SquareSprite{
  
  PVector vel;
  PVector loc;
  PImage squareSprite;
  boolean touched;
  boolean dead;
  
  public SquareSprite(PVector _loc, PVector _vel){
    orientation(LANDSCAPE);
    this.loc = _loc;
    this.vel = _vel;
    this.squareSprite = loadImage("squaresprite.png");
    this.touched = false;
    this.dead = false;
  }
  
  public void displaySprite(){
    if(dead){
      this.squareSprite = loadImage("squarespritedead.png");
    }
    else if(!touched){
      this.squareSprite = loadImage("squaresprite.png");
    }
    else {
      this.squareSprite = loadImage("squarespriteglow.png");
    }
    
    imageMode(CENTER);
    image(squareSprite, loc.x, loc.y);
  }
  
  public void kill(){
    this.dead = true;
  }
  
  public void activate(){
    if(!touched){
      this.touched = true;
    }
  }
  
  public void deactivate(){
    this.touched = false;
  }
  
  public void updateLoc(){
    this.loc.x += this.vel.x;
    this.loc.y += this.vel.y;
  }
  
  public void checkCollision(){
    if(this.loc.x <= squareSprite.width/2 || this.loc.x >= screenWidth-squareSprite.width/2){
      this.vel.x *= -1;
    }
    if(this.loc.y <= squareSprite.height/2 || this.loc.y >= screenHeight-squareSprite.width/2){
      this.vel.y *= -1;
    }
  }
  
}
class TriangleSprite{
  
  PVector vel;
  PVector loc;
  PImage triangleSprite;
  boolean touched;
  boolean dead;
  
  public TriangleSprite(PVector _loc, PVector _vel){
    orientation(LANDSCAPE);
    this.loc = _loc;
    this.vel = _vel;
    this.triangleSprite = loadImage("trianglesprite.png");
    this.touched = false;
    this.dead = false;
  }
  
  public void displaySprite(){
    if(dead){
      this.triangleSprite = loadImage("trianglespritedead.png");
    }
    else if(!touched){
      this.triangleSprite = loadImage("trianglesprite.png");
    }
    else {
      this.triangleSprite = loadImage("trianglespriteglow.png");
    }
    
    imageMode(CENTER);
    image(triangleSprite, loc.x, loc.y);
  }
  
  public void kill(){
    this.dead = true;
  }
  
  public void activate(){
    if(!touched){
      this.touched = true;
    }
  }
  
  public void deactivate(){
    this.touched = false;
  }
  
  public void updateLoc(){
    this.loc.x += this.vel.x;
    this.loc.y += this.vel.y;
  }
  
  public void checkCollision(){
    if(this.loc.x <= triangleSprite.width/2 || this.loc.x >= screenWidth-triangleSprite.width/2){
      this.vel.x *= -1;
    }
    if(this.loc.y <= triangleSprite.height/2 || this.loc.y >= screenHeight-triangleSprite.width/2){
      this.vel.y *= -1;
    }
  }
  
}

}
