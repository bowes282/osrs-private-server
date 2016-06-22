package nl.osrs.world;

public class Tile {
	private int x = -1;
	private int y = -1;
	private int height = -1;
	private int f = -1;
	
	public Tile(int x, int y){
		setX(x);
		setY(y);
	}

	public Tile(int x, int y, int h){
		setX(x);
		setY(y);
		setH(h);
	}
	
	public Tile(int x, int y, int h, int f){
		setX(x);
		setY(y);
		setH(h);
		setF(f);
	}
	
	public void setX(int x){
		this.x = x;
	}
	
	public void setY(int y){
		this.y = y;
	}
	
	public void setH(int h){
		this.height = h;
	}
	
	public void setF(int f){
		this.f = f;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getH() {
		return height;
	}
	
	public int getF() {
		return f;
	}
	
}