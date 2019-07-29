/** 
 * By using LX Studio, you agree to the terms of the LX Studio Software
 * License and Distribution Agreement, available at: http://lx.studio/license
 *
 * Please note that the LX license is not open-source. The license
 * allows for free, non-commercial use.
 *
 * HERON ARTS MAKES NO WARRANTY, EXPRESS, IMPLIED, STATUTORY, OR
 * OTHERWISE, AND SPECIFICALLY DISCLAIMS ANY WARRANTY OF
 * MERCHANTABILITY, NON-INFRINGEMENT, OR FITNESS FOR A PARTICULAR
 * PURPOSE, WITH RESPECT TO THE SOFTWARE.
 */

// ---------------------------------------------------------------------------
//
// Welcome to LX Studio! Getting started is easy...
// 
// (1) Quickly scan this file
// (2) Look at "Model" to define your model
// (3) Move on to "Patterns" to write your animations
// 
// ---------------------------------------------------------------------------

import java.net.SocketException;
import java.net.UnknownHostException;

// Reference to top-level LX instance
heronarts.lx.studio.LXStudio lx;

void setup() {
  size(1000, 1000, P3D);
  // fullScreen(P3D);

  ArrayList<LXPoint> points = new ArrayList<LXPoint>();
  points.add(new LXPoint(0, 0, 0));
  points.add(new LXPoint(10, 0, 0));

  LXModel model = new LXModel(points);

  lx = new heronarts.lx.studio.LXStudio(this, model, MULTITHREADED);
}

void initialize(
  final heronarts.lx.studio.LXStudio lx,
  heronarts.lx.studio.LXStudio.UI ui
) throws SocketException, UnknownHostException {
  lx.addOutput(new Output(lx).setAddress(CONTROLLER_IP));
}

void onUIReady(heronarts.lx.studio.LXStudio lx, heronarts.lx.studio.LXStudio.UI ui) {
  // Add custom UI components here
}

void draw() {
  // All is handled by LX Studio
}

// Configuration flags
final static boolean MULTITHREADED = true;

// Helpful global constants
final static int CENTIMETER = 1;
final static int METER = 100 * CENTIMETER;

final static String CONTROLLER_IP = "192.168.1.100";
