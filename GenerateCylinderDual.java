/**
 *
 * A simple tool for generating GCode to print a cylinder with dual extrusion. Use the terminal.
 *
 * Compile with:
 *   javac GenerateCylinderDual.java
 *
 * Run with:
 *   java GenerateCylinderDual > cylinder.gcode
 *
 * Load the result file (cylinder.gcode) in PrintRun.
 *
 * You change various parameters like the radius, height, position and print speed directly in the code,
 * then recompile and generate the cylinder again.
 */
public class GenerateCylinderDual {

	public static void main(String[] args) throws Exception {
		double R = 10;
		double H = 20;
		double step = 0.5;
		double centerX = 62; // 92
		double centerY = 67; // 72
		double firstExtruderOffsetX = -19; // mm
		double firstExtruderOffsetY = 0; // mm
		double secondExtruderOffsetX = 13; // mm
		double secondExtruderOffsetY = 3; // mm
		double F = 300; // feedrate in mm/min
		double Zspeed = 120; // mm/min
		double nonPrintingMovementSpeed = 3000; // mms/min
		int cylinderSteps = 30;

		p("G21 ; set units to millimeters");
		p("G90 ; use absolute coordinates");
		p("M82 ; use absolute distances for extrusion");
		p("G92 E0 ; reset extrusion distance");
//		p("G1 Z20 F" + Zspeed + " ; lift Z");
//		p("G28 ; home all axes");
		p("G1 Z20 F" + Zspeed + " ; lift Z");
		p("");

		for (double z = 0.5; z <= H; z += 0.5) {
			for (int t = 0; t < 2; t++) {
				boolean ZPositionSet = false;
				p("T" + t);
				for (int i = 0; i <= cylinderSteps; i++) {
					double angle = i * Math.PI * 2 / cylinderSteps;
					double x = R * Math.cos(angle) + centerX + (t == 0 ? firstExtruderOffsetX : secondExtruderOffsetX);
					double y = R * Math.sin(angle) + centerY + (t == 0 ? firstExtruderOffsetY : secondExtruderOffsetY);

					if (x < 0 || y < 0) {
						System.err.println("Negative coordinates: " + x + ", " + y);
						return;
					}

					p("G1 X" + d(x) + " Y" + d(y) + (i == 0 ? " F" + nonPrintingMovementSpeed : i == 1 ? " F" + F : ""));
					if (!ZPositionSet) {
						p("G1 Z" + d(z) + " F" + Zspeed + " ; set Z position");
						ZPositionSet = true;
					}
				}
				p("");
				if (z < 20.0) {
					p("G1 Z" + d(20.0) + " F" + Zspeed + " ; lift Z");
					p("");
				}
			}
		}

		p("G1 Z" + d(H + 20.0) + " F" + Zspeed + " ; lift Z");
		double x = centerX + (secondExtruderOffsetX - firstExtruderOffsetX) / 2;
		double y = centerY + (secondExtruderOffsetY - firstExtruderOffsetY) / 2;
		p("G1 X" + d(x) + " Y" + d(y) + " F" + nonPrintingMovementSpeed + " ; go to center");
		p("M84     ; disable motors");
	}

	public static void p(String s) {
		System.out.println(s);
	}

	public static String d(double d) {
		char[] ch = new char[3];
		int p = (int) ((d - (int) d) * 1000);
		for (int i = ch.length - 1; i >= 0; i--) {
			ch[i] = (char) ('0' + (p % 10));
			p /= 10;
		}
		return "" + (int) d + "." + new String(ch);
	}

}
