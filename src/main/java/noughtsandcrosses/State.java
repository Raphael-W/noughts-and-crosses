package noughtsandcrosses;

// Represents the state of a board
public record State(char winner, Coord fromPos, Coord toPos, int xNears, int oNears) {}