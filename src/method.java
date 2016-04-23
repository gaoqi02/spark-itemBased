import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class method {
	public static List<String> getFileString(String path)
	{
		List<String> list =new ArrayList<>();
		try {   
		    FileReader read = new FileReader(path);
		    BufferedReader br = new BufferedReader(read);
		    String tmp_str ="";
			while((tmp_str=br.readLine())!= null) {
				list.add(tmp_str);
	        }
	        br.close();
			return(list);   
	  } catch (FileNotFoundException e) {
	    e.printStackTrace();
	   
	  } catch (IOException e){
	    e.printStackTrace();   }
		return null;
	}
	
	
	
	public static List<UserItemRating> getTestUserInfo(List<String> testtrainging,int userid)
	{
		List<UserItemRating> list = new ArrayList<>();
		for(int i=0;i<testtrainging.size();i++)
		{
			//找到对应的userid
			if(testtrainging.get(i).split("@")[0].equals(Integer.toString(userid)))
			{
				String[] str1 =testtrainging.get(i).split("@")[1].split(",");				
				for(int j=0;j<str1.length;j++)
				{
					UserItemRating uir = new UserItemRating(Integer.toString(userid), 
							str1[j].split(":")[0],str1[j].split(":")[1]);
					list.add(uir);
				}
			}
		}
		return list;
	}
	public static float getTestUserAverage(List<UserItemRating> list)
	{
		float i = 0;
		for(UserItemRating u:list)
		{
			i+=Float.parseFloat(u.getRatings());
		}
		return i/list.size();
	}
	public static List<UserItemRating> FindSimilarItemByUser
	(List<UserItemRating> list,List<UserItemRating> list1)
	{
		List<UserItemRating> l = new ArrayList<>();
		for(UserItemRating u:list)
		{
			for(UserItemRating u1:list1)
			{
				if(u.getItemid().equals(u1.getItemid()))
				{
					l.add(u);
					l.add(u1);
				}
			}
		}
		return l;//这个List里面有两个用户相似的信息
	}
	public static float GetAverage(List<String> l)
	{
    	float ave = 0;
    	float count = 0;
		for(String u: l)
		{
			count += Float.parseFloat(u.split(":")[1]);
		}
		ave = count/l.size();
		return ave;
	}
	


	public static List<String> getItemUser(List<String> traininglist,String itemid)
	{
		List<String> list = new ArrayList<>();
		for(int i=0;i<traininglist.size();i++)
		{
			if(itemid.equals(traininglist.get(i).split("@")[0]))
			{
				String[] str = traininglist.get(i).split("@")[1].split(",");
				for(String s:str)
				{
					list.add(s);
				}
			}
			
		}
		return list;
		
	}

}
