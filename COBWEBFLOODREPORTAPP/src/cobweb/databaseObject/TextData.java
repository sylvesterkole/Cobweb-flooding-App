package cobweb.databaseObject;

public class TextData {
	public int getImageID() {
		return imageID;
	}
	public void setImageID(int imageID) {
		this.imageID = imageID;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFlowVelocity() {
		return flowVelocity;
	}
	public void setFlowVelocity(String flowVelocity) {
		this.flowVelocity = flowVelocity;
	}
	public String getDepth() {
		return depth;
	}
	public void setDepth(String depth) {
		this.depth = depth;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	
	
	
	private int imageID;
	private  String type="ReportType";
	private String flowVelocity="Flow Velocity";
	private  String depth="Depth Estimate";
	private String note="Note";
	
	
}
