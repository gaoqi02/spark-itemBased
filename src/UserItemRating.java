
public class UserItemRating {
	
	private String userid;
	private String itemid;
	private String ratings;
	public UserItemRating(String userid, String itemid, String ratings) {
		super();
		this.userid = userid;
		this.itemid = itemid;
		this.ratings = ratings;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getItemid() {
		return itemid;
	}
	public void setItemid(String itemid) {
		this.itemid = itemid;
	}
	public String getRatings() {
		return ratings;
	}
	public void setRatings(String ratings) {
		this.ratings = ratings;
	}
	
}
