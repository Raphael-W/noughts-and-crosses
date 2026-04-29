package noughtsandcrosses;

public record State(char winner, Coord fromPos, Coord toPos, int xNears, int oNears) {}