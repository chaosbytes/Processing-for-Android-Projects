import fsdev.util.fscam.*;

FSCam cam;
ArrayList<Float> sizes;
ArrayList<PVector> locs;

void setup() {
  size(500, 500, OPENGL);
  hint(ENABLE_DEPTH_TEST);  

  //cam = new FSCam(this);
  cam = new FSCam(this, 250.0f, 1.22f, 2.7f, new PVector(0, 0, 0), new PVector(0, 1, 0));
  cam.engage();
  sizes = new ArrayList();
  locs = new ArrayList();
  for (int i = 0; i < 10; i++) {
    sizes.add(new Float(random(10, 50)));
    locs.add(new PVector(random(-400, 400), random(-400, 400), random(-400, 400)));
  }
}

void draw() {
  colorMode(RGB);
  background(125);
  pushMatrix();
  cam.view();
  if (mousePressed) {
    cam.orbit(new PVector(mouseX, mouseY), new PVector(pmouseX, pmouseY));
  }
  directionalLight(0, 50, 200, 0, 0, 100);
  directionalLight(200, 0, 25, 0, 0, -100);
  popMatrix();
  pushMatrix();
  fill(255);
  stroke(255);
  for (int i = 0; i < 10; i++) {
    float size = sizes.get(i);
    PVector loc = locs.get(i);
    translate(loc.x, loc.y, loc.z);
    sphere(size);
  }
  popMatrix();
  pushMatrix();
  strokeWeight(3);
  stroke(255, 0, 0);
  line(0, 0, 0, 25, 0, 0);
  stroke(0, 255, 0);
  line(0, 0, 0, 0, 25, 0);
  stroke(0, 0, 255);
  line(0, 0, 0, 0, 0, 25);
  popMatrix();
}

void keyPressed() {
  if (key == CODED) {
    if (keyCode == DOWN) {
      cam.zoom(1000);
    } 
    else if (keyCode == UP) {
      cam.zoom(-1000);
    }
  }
}

