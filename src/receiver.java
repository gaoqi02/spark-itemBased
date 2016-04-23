import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.receiver.Receiver;


public class receiver extends Receiver<String>{
	
	String path = null;
	int time =0;
	int sendquantity=0;
	int mode = 0;
	public receiver(String path_,String timeargs,String quantityaegs,String turn) {
		super(StorageLevel.MEMORY_AND_DISK_2());
		path = path_;
		time = Integer.parseInt(timeargs);
		sendquantity = Integer.parseInt(quantityaegs);
		mode = Integer.parseInt(turn);
		// TODO Auto-generated constructor stub
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		new Thread() {
			@Override public void run() {
				if(mode==2){
				try {
					InputStream in = new FileInputStream(path);
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					while(true){
						sleep(time);
						receive(reader);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  }
				if(mode==1)
				{
					try {
						InputStream in = new FileInputStream(path);
						BufferedReader reader = new BufferedReader(new InputStreamReader(in));
						String str="";
						List<String> list = new ArrayList<>();
						int count =0;
						while((str=reader.readLine()) !=null)
						{
							list.add(str);
							count++;
						}
						
						while(true){
							sleep(time);
							randomreceive(list,count);
							
						}
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}  
					
				}
			}
		}.start();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		
	}
	 private void receive(BufferedReader reader) throws IOException {
		 String userInput="";
		try {
			for(int i=0;i<sendquantity;i++){
				String str =reader.readLine();
//				while(str== null)
//				{
//					System.err.println("over the test");
//					System.exit(1);
//				}
				userInput = str;
				store(userInput);}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	}  
	
	}
	 private void randomreceive(List<String> list,int count) throws IOException {
		String userInput="";
		for(int i=0;i<sendquantity;i++){
			Random rand = new Random();
			int randNum = rand.nextInt(count);
			userInput = list.get(randNum);	
			store(userInput);}  
	
	}


}
