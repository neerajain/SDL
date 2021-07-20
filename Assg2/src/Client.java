import java.io.*; 
import java.net.*; 
import java.util.Scanner; 
public class Client 
{ 
	
	void call() {
		try
		{ 
			Scanner scn = new Scanner(System.in); 
			InetAddress ip = InetAddress.getByName("localhost"); 
			Socket s = new Socket(ip, 5056); 
	        DataInputStream dis = new DataInputStream(s.getInputStream()); 
			DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 
			BufferedReader br=new BufferedReader(new InputStreamReader(System.in));  
			String ss="", sr="";
			while (!ss.equals("stop")) 
			{ 
				sr=dis.readUTF();
				System.out.println("Admin says: "+sr);
				ss=br.readLine();
				dos.writeUTF(ss);
				dos.flush();
			} 
			s.close();
			scn.close(); 
			dis.close(); 
			dos.close(); 
		}catch(Exception e){ 
			e.printStackTrace(); 
		} 
	}
	public static void main(String[] args) throws IOException 
	{ 
		Client c = new Client();
		c.call();
	} 
} 
