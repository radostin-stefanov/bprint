diameter = 20;
height = 20;
width = 0.6;

difference() {
  cylinder(r=diameter / 2, h=height);
  translate([0, 0, -1]) cylinder(r=(diameter - width) / 2, h=height + 2);
}
