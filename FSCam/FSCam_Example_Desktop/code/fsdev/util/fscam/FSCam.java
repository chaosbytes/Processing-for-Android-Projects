package fsdev.util.fscam;

import processing.core.*;

public class FSCam {
	private final static float comp = 0.005f;
	private static final float TWO_PI = (float) (2*Math.PI);
	private PVector eye, target, upAxis;
	private float radius, theta, phi;
	private boolean running;
	private PApplet parent;

	public FSCam(PApplet _parent) {
		parent = _parent;
		this.radius = 250.0f;
		this.theta = 1.22f;
		this.phi = 2.7f;
		this.eye = new PVector();
		this.target = new PVector(0, 0, 0);
		this.upAxis = new PVector(0, 1, 0);
		running = false;
		updateEye();
	}

	public FSCam(PApplet _parent, float _radius, float _theta, float _phi,
			PVector _target, PVector _upAxis) {
		parent = _parent;
		this.radius = _radius;
		this.theta = _theta;
		this.phi = _phi;
		this.eye = new PVector();
		this.target = _target.get();
		this.upAxis = _upAxis.get();
		running = false;
		updateEye();
	}

	public void view() {
		if (this.running) {
			parent.camera(eye.x, eye.y, eye.z, target.x, target.y, target.z, upAxis.x,
					upAxis.y, upAxis.z);
		}
	}

	public void engage() {
		running = true;
	}

	public void disengage() {
		running = false;
	}

	public void orbit(PVector vec, PVector pVec) {
		theta += (vec.x - pVec.x) * comp;
		theta = correctAngle(theta, TWO_PI);
		phi += (vec.y - pVec.y) * comp;
		phi = correctAngle(phi, TWO_PI);
		updateEye();
	}

	private float correctAngle(float ang, float range) {
		if (ang < 0) {
			ang += range;
		} else if (ang > range) {
			ang -= range;
		}
		return ang;
	}

	private void updateEye() {
		correctUpAxis();
		this.eye.x = (float) (this.radius * Math.sin(this.theta) * Math.sin(this.phi));
		this.eye.y = (float) (this.radius * -Math.cos(this.phi));
		this.eye.z = (float) (this.radius * -Math.cos(this.theta) * Math.sin(this.phi));
	}

	public float calculateZoom(float newDist, float oldDist) {
		return newDist - oldDist;
	}

	public void zoom(float zoomScale) {
		this.radius += zoomScale * comp;
		this.updateEye();
	}

	public void reset() {
		this.radius = 250.0f;
		this.theta = 1.22f;
		this.phi = 2.7f;
		this.updateEye();
	}

	private void correctUpAxis() {
		if (this.phi > 0 && this.phi <= Math.PI) {
			this.upAxis.y = 1;
		} else if (this.phi > Math.PI && this.phi <= TWO_PI) {
			this.upAxis.y = (float) -1.0;
		}
	}
}
