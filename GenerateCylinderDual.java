public class GenerateCylinderDual {

	public static void main(String[] args) throws Exception {
		double R = 10;
		double H = 20;
		double step = 0.5;
		double centerX = 185.0 / 2.0;
		double centerY = 120.0 / 2.0;
		double extruderOffsetX = 80; // mm
		double extruderOffsetY = 0; // mm
		double F = 300; // mm/s -- feedrate
		double Zspeed = 300; // mm/s
		double nonPrintingMovementSpeed = 900; // mms/s

		p("G21 ; set units to millimeters");
		p("G90 ; use absolute coordinates");
		p("M82 ; use absolute distances for extrusion");
		p("G92 E0 ; reset extrusion distance");
		p("G1 Z20 F" + Zspeed + " ; lift Z");
		p("G28 ; home all axes");
		p("G1 Z20 F" + Zspeed + " ; lift Z");
		p("");

		for (double z = 0.5; z <= H; z += 0.5) {
			for (int t = 0; t < 2; t++) {
				boolean ZPositionSet = false;
				p("T" + t);
				for (int i = 0; i < 30; i++) {
					double x = R * Math.cos(i * Math.PI * 2 / 15) + centerX + (t == 1 ? extruderOffsetX : 0);
					double y = R * Math.sin(i * Math.PI * 2 / 15) + centerY + (t == 1 ? extruderOffsetY : 0);
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

		p("G1 Z" + d(H + 5.0) + " F" + Zspeed + " ; lift Z");
		p("G1 X" + d(centerX) + " Y" + d(centerY) + " F" + nonPrintingMovementSpeed + " ; go to center");
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
