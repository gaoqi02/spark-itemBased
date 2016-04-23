import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import com.google.common.collect.Lists;

import scala.Tuple2;


public class sparkitem  {
	public static float ave;
	public static List<String> trainingtTestUser=new ArrayList<>();
	public static List<String> traininglist=new ArrayList<>();
	public static void main(final String[] args){
		if (args.length < 8) {
		      System.err.println
		      ("Usage: item <training> <test> <training_test> "
		      		+ "<sleeptime> <sendquantity> <outputpath> <1-random,2-order> <duration>");
		      System.exit(1);
		    }
		JavaStreamingContext jssc = 
	    		new JavaStreamingContext("local[4]", "sparkitem",new Duration(Integer.parseInt(args[7])));
//		SparkConf sparkConf = new SparkConf().setAppName("sparkitem");
//	    JavaStreamingContext jssc = new JavaStreamingContext(sparkConf,  new Duration(Integer.parseInt(args[7])));
	    receiver r=new receiver(args[1],args[3],args[4],args[6]);
	    r.onStart();
	    JavaReceiverInputDStream<String> lines = jssc.receiverStream(r);

	    JavaDStream<String> inputstream = lines.flatMap(new FlatMapFunction<String, String>() {
	        /**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			@Override
			public Iterable<String> call(String arg0) throws Exception {
				return Lists.newArrayList(arg0.split("\n"));
			}
	      });

	    JavaPairDStream<String, String> mapstreamitem = inputstream.mapToPair(
	    	      new PairFunction<String, String, String>() {

					private static final long serialVersionUID = 1L;
					
					@Override 
					public Tuple2<String, String> call(String s) throws IOException {
						if(traininglist.size()==0){
							traininglist = method.getFileString(args[0]);
							trainingtTestUser = method.getFileString(args[2]);
						}
						String[] userinfo = s.split(","); 
						String userid = userinfo[0];
			        	String itemid = userinfo[1];
			        	//这个要测的用户的相关信息
						List<UserItemRating> useritemratinglist = 
								method.getTestUserInfo(trainingtTestUser,Integer.parseInt(userid));
						//关于这个item的相关信息
						List<String> Item_Userlist = method.getItemUser(traininglist,itemid);
						//获得这个指定的item的平均分
						float ave = method.GetAverage(Item_Userlist);
						float upper=0;
						float downer=0;
						for(UserItemRating u:useritemratinglist)
						{
							//当前这个itemid
							String itemuid = u.getItemid();
							String itemdafen = u.getRatings();
							//从training中获取这个itemuid的用户打分信息
							List<String> tmp_itemlist = method.getItemUser(traininglist,itemuid);
							float tmp_ave = method.GetAverage(tmp_itemlist);
							String str=itemid+";"+itemuid+"@";
							for(String tmp:tmp_itemlist)
							{
								String tmp_userid = tmp.split(":")[0];
								for(String item:Item_Userlist)
								{
									if(tmp_userid.equals(item.split(":")[0]))
									{
										str += item.split(":")[1]+"_"+tmp.split(":")[1]+",";
									}
								}
							}
							float up=0;
							float downleft=0;
							float downright=0;
							if(str.split("@").length>1){
							String[] test = str.split("@")[1].split(",");
							for(int i=0;i<test.length;i++)
							{
								up +=
						(Float.parseFloat(test[i].split("_")[0])-ave)*(Float.parseFloat(test[i].split("_")[1])-tmp_ave);
								downleft += 
						(Float.parseFloat(test[i].split("_")[0])-ave)*(Float.parseFloat(test[i].split("_")[0])-ave);
								downright +=
						(Float.parseFloat(test[i].split("_")[1])-tmp_ave)*(Float.parseFloat(test[i].split("_")[1])-tmp_ave);				
							}
							float pearson = (float) (up/(Math.sqrt(downleft)*Math.sqrt(downright)));
							if(!Float.toString(pearson).equals("NaN"))
							{
								upper+=pearson*Float.parseFloat(itemdafen);
								downer+=Math.abs(pearson);
							}
						}}
						float result=upper/downer;
						if(result>5)
						{result=5;}
						if(result<1)
						{result=1;}
	    	        	Tuple2<String, String> tu = new Tuple2<String, String>(s, Float.toString(result));
						return tu;
	    	        }
	    	      });
			   JavaPairDStream<String, String> finalteststream=mapstreamitem.reduceByKey(new Function2<String, String, String>() {
					
					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;
		
					@Override
					public String call(String arg0, String arg1) throws Exception {
						// TODO Auto-generated method stub
						return arg0;
					}
				
			    	
			    });
			   finalteststream.foreach(new Function<JavaPairRDD<String, String>, Void>() {

					@Override
					public Void call(JavaPairRDD<String, String> arg0)
							throws Exception {
						 try {
					            // 打开一个随机访问文件流，按读写方式
					            RandomAccessFile randomFile = 
					            		new RandomAccessFile(args[5], "rw");
					           List<Tuple2<String, String>> tulist = arg0.collect();
					           String sfianl = "";
					           for(int i=0;i<tulist.size();i++)
					           {
					        	   sfianl += tulist.get(i)._1+":"+tulist.get(i)._2+"\n";
					           }
					            long fileLength = randomFile.length();
					            randomFile.seek(fileLength);
					            randomFile.writeBytes(sfianl);
					        } catch (IOException e) {
					            e.printStackTrace();
					        }
						
						return null;
					}
			    	
				});
	    
		finalteststream.print();
		jssc.start(); 
		jssc.awaitTermination(); 

	}
	
	
}
