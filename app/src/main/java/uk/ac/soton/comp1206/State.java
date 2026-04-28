package uk.ac.soton.comp1206;

public record State(char winner, Coord fromPos, Coord toPos, int xNears, int oNears) {}