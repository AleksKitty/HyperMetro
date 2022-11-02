# HyperMetro

Project description: https://hyperskill.org/projects/120?track=15

Generates metro based on json input file. Finds the shortest path from one station to another. BreadthFirstSearch algorithm doesn't сonsider time and Dijkstras algorithm consider time. 

Examples of input:

**For BreadthFirstSearch algorithm:**

/route "Waterloo & City line" "Waterloo" "Waterloo & City line" "Bank"
/route "Victoria line" "Victoria" "Northern line" "Oval"
/route "Victoria line" "Green Park" "Northern line" "Oval"

**Dijkstras algorithm:**

/fastest-route "District line" "Richmond" "District line" "Gunnersbury"
/fastest-route "Victoria line" "Brixton" "Northern line" "Angel"
