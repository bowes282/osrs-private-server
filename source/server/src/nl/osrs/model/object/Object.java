package nl.osrs.model.object;

public class Object {
	private int id;
	private int x;
	private int y;
	private int height;
	
	private int face;
	private int type;
	
	private int ticksLeft;
	private int replacementId;
	
	public Object(int id, int x, int y) {
		this.setId(id);
		this.setX(x);
		this.setY(y);
		this.setHeight(0);
	}
	
	public Object(int id, int x, int y, int ticksLeft, int replacementId) {
		this.setId(id);
		this.setX(x);
		this.setY(y);
		this.setTicksLeft(ticksLeft);
		this.setReplacementId(replacementId);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getFace() {
		return face;
	}

	public void setFace(int face) {
		this.face = face;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getTicksLeft() {
		return ticksLeft;
	}

	public void setTicksLeft(int ticksLeft) {
		this.ticksLeft = ticksLeft;
	}

	public int getReplacementId() {
		return replacementId;
	}

	public void setReplacementId(int replacementId) {
		this.replacementId = replacementId;
	}
}