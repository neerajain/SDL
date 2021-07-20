import java.io.*; 
import java.net.*; 
public class Server 
{ 
	void calls() throws IOException  {
		ServerSocket ss = new ServerSocket(5056);
		int i=0;
		System.out.println("Server Started...");
		while (true) 
		{ 
			Socket s = null; 
			try
			{ 
				s = ss.accept();
				i++;
				System.out.println("A new patient is connected : " + s); 
				DataInputStream dis = new DataInputStream(s.getInputStream()); 
				DataOutputStream dos = new DataOutputStream(s.getOutputStream());  
				Thread t = new ClientHandler(s, dis, dos,i); 
				t.start(); 
				
			} 
			catch (Exception e){ 
				s.close(); 
				e.printStackTrace(); 
			} 
        }
	}
	public static void main(String[] args) throws IOException
	{ 
		Server s = new Server();
		s.calls();
	} 
} 

class ClientHandler extends Thread 
{ 
	final DataInputStream dis; 
	final DataOutputStream dos; 
	final Socket s; 
	final int i;
	
	public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos,int i) 
	{ 
		this.s = s; 
		this.dis = dis; 
		this.dos = dos; 
		this.i = i;
	} 

	@Override
	public void run() 
	{ 
		try{
			BufferedReader br=new BufferedReader(new InputStreamReader(System.in)); 
			dos.writeUTF("Welcome!!!");
			dos.flush();
			String sr="", ss="";
			while (!sr.equals("stop")) 
			{ 
				sr=dis.readUTF();
				System.out.println("Patient "+i+" says: "+sr);
				if(!sr.equals("stop")) {
					ss = br.readLine();
					dos.writeUTF(ss);
					dos.flush();
				}
			} 
				this.dis.close(); 
				this.dos.close(); 
		}		
		catch(IOException e){ 
			e.printStackTrace(); 
		} 
	} 
} 
