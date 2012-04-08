package processing.test.fscam_example_android;

import processing.core.*; 

import fsdev.util.fscam.*; 

import fsdev.util.fscam.*; 

import android.view.MotionEvent; 
import android.view.KeyEvent; 
import android.graphics.Bitmap; 
import java.io.*; 
import java.util.*; 

public class FSCam_Example_Android extends PApplet {



FSCam cam;
ArrayList<Float> sizes;
ArrayList<PVector> locs;
PVector touchPoint, pTouchPoint;
float oldDist;
static final int NONE = 0;
static final int ORBIT = 1;
static final int ZOOM = 2;
int mode = NONE;

public String sketchRenderer() {
  return OPENGL;
}

public void setup() {
  hint(ENABLE_DEPTH_TEST);  
  orientation(LANDSCAPE);
  cam = new FSCam(this, 250.0f, 1.22f, 2.7f, new PVector(0, 0, 0), new PVector(0, 1, 0));
  cam.engage();
  sizes = new ArrayList();
  locs = new ArrayList();
  for (int i = 0; i < 10; i++) {
    sizes.add(new Float(random(10, 50)));
    locs.add(new PVector(random(-400, 400), random(-400, 400), random(-400, 400)));
  }
  touchPoint = pTouchPoint = new PVector();
  oldDist = 1.0f;
  sphereDetail(12);
}

public void draw() {
  background(125);
  cam.view();
  directionalLight(0, 50, 200, 0, 0, 100);
  directionalLight(200, 0, 25, 0, 0, -100);
  for (int i = 0; i < 10; i++) {
    float size = sizes.get(i);
    PVector loc = locs.get(i);
    pushMatrix();
    translate(loc.x, loc.y, loc.z);
    sphere(size);
    popMatrix();
  }
  pushMatrix();
  strokeWeight(3);
  stroke(255, 0, 0);
  line(0, 0, 0, 50, 0, 0);
  stroke(0, 255, 0);
  line(0, 0, 0, 0, 50, 0);
  stroke(0, 0, 255);
  line(0, 0, 0, 0, 0, 50);
  popMatrix();
}

public void handleOrbit(FSCam camera, PVector touchPoint, PVector pTouchPoint) {
  camera.orbit(new PVector(touchPoint.x, touchPoint.y), new PVector(pTouchPoint.x, pTouchPoint.y));
}

public void handleZoom(FSCam camera, float nDist, float oDist) {
  camera.zoom(cam.calculateZoom(nDist, oDist));
}

public float calculateDist(MotionEvent e) {
  return PVector.dist(new PVector(e.getX(0), e.getY(0)), new PVector(e.getX(1), e.getY(1)));
}
public boolean surfaceTouchEvent(MotionEvent e) {
  int numPointers = e.getPointerCount();
  final int action = e.getAction() & MotionEvent.ACTION_MASK;
  int pointerIndex = (e.getAction() & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT;
  int pointerId = e.getPointerId(pointerIndex);
  
  if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
    if (pointerId == 0) {
      pTouchPoint = new PVector(e.getX(e.findPointerIndex(0)), e.getY(e.findPointerIndex(0)));
      mode = ORBIT;
    }
    else {
      if (pointerId == 1)
      {
        oldDist = calculateDist(e);
        mode = ZOOM;
      }
    }
  }
  else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
    if (pointerId == 0) {
      mode = NONE;
    }
    else {
      if (pointerId == 1) {
        mode = ORBIT;
      }
    }
  }

  else if (action == MotionEvent.ACTION_MOVE) {
    for (int i = 0; i<numPointers;i++) {
      int newId = e.getPointerId(i);
      if (newId == 0) {
        if (mode == ORBIT) {
          touchPoint = new PVector(e.getX(i), e.getY(i)); 
          handleOrbit(cam, touchPoint, pTouchPoint);
        } 
        else if (mode == ZOOM) {
          float newDist = calculateDist(e);
          handleZoom(cam, newDist, oldDist);
        }
      }
      else {
        if (newId == 1) {
          if (mode == ZOOM) {
            float newDist = calculateDist(e);
            handleZoom(cam, newDist, oldDist);
          }
        }
      }
    }
    pTouchPoint = touchPoint.get();
  }
  return super.surfaceTouchEvent(e);
}


}
