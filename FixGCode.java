import java.io.*;
import java.util.ArrayList;

public class FixGCode {

  public static void main(String[] args) throws Exception {
    if (args.length == 0) {
      System.out.println("A tool for adding/removing Gcodes from the result obtained from Slic3r.");
      System.out.println("Usage: java FixGCode <file> [<delay-between-layers> [<lift-Z-while-waiting]]");
      System.out.println();
      System.out.println("  file                 -- the GCode file to be fixed.");
      System.out.println("  delay-between-layers -- the amount of time in seconds we'll wait on every layer (default to 10s).");
      System.out.println("  lift-Z-while-waiting -- how many mm the Z axis will be lifted while waiting to go to the next layer (defaults to 10mm).");
      return;
    }

    String filename = args[0];
    if (filename.endsWith(".stl")) {
      filename = filename.substring(0, filename.length() - 4) + ".gcode";
    }
    File f = new File(filename);
    if (!f.exists()) {
      System.err.println("File " + f.getAbsolutePath() + " does not exist");
      return;
    }
    if (!f.isFile()) {
      System.err.println("Path " + f.getAbsolutePath() + " does not denote an ordinary file");
      return;
    }

    int delay = 10;
    if (args.length > 1) {
      try {
        delay = Integer.parseInt(args[1]);
      }
      catch (NumberFormatException ex) {
        System.err.println("Number of seconds expected");
        return;
      }
      if (delay < 0) {
        System.err.println("Number of seconds should be a positive number");
        return;
      }
    }

    double lift = 10;
    if (args.length > 2) {
      try {
        lift = Double.parseDouble(args[2]);
      }
      catch (NumberFormatException ex) {
        System.err.println("Lift-Z should be a floating-point number.");
        return;
      }
      if (lift < 0) {
        System.err.println("Lift-Z should be a non-negative number.");
        return;
      }
    }

    ArrayList<String> arr = new ArrayList();
    BufferedReader inp = new BufferedReader(new FileReader(f));
    
    while (true) {
      String line = inp.readLine();
      if (line == null) {
        break;
      }

      // Comment out some unwanted GCodes.
      if (line.startsWith("M190 S20") ||  // Set bed temperature (unwanted).
          line.startsWith("G28") ||       // Home all (unwanted).
          line.startsWith("M109 S37")) {  // Wait temperature (unwanted, at least for testing).
        line = "; " + line;
      }

      // Lift Z if we go to the next layer.
      if (delay > 0 && line.indexOf("move to next layer") >= 0) {
        String num = line.substring(line.indexOf("(") + 1, line.indexOf(")"));
        int layerNo = Integer.parseInt(num);
        if (layerNo >= 2) {
          String trail = line.substring(line.indexOf("Z") + 1);
          String currentZ = trail.substring(0, trail.indexOf(" "));
          double z = Double.parseDouble(currentZ);
          arr.add(line.substring(0, line.indexOf("Z") + 1) + (z + lift) + trail.substring(trail.indexOf(" ")));
          arr.add("G4 S" + delay);
        }
      }

      // Add current line.
      arr.add(line);

    }

    inp.close();
    PrintWriter out = new PrintWriter(new FileWriter(f));
    for (String line : arr) {
      out.println(line);
    }
    out.close();

    System.out.println("Done, GCode in file " + f.getAbsolutePath() + " fixed.");
  }

}
