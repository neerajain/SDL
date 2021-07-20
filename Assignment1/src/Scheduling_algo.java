import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class Scheduling_algo {
	
	int process;
	int at[];
	int bt[];
	int pid[];
	int ct[];
	int wt[];
	int tat[];
	int tq;
	float avgWt=0;
	float avgTat=0;
	Scanner sc = new Scanner(System.in);
	
	public void input() {
		System.out.print("Enter number of processes: ");
		process = sc.nextInt();
		
		System.out.println();
		at = new int[process];
		bt = new int[process];
		pid = new int[process];
		ct = new int[process];
		wt = new int[process];
		tat = new int[process];
		int val;
		for(int i=0;i<process;i++) 
		{
			pid[i]=i+1;
			System.out.print("Enter AT of process " + pid[i] +": ");
			val = sc.nextInt();
			at[i]=val;
		}
		System.out.println();
		for(int i=0;i<process;i++) 
		{
			System.out.print("Enter BT of process " + pid[i] +": ");
			val = sc.nextInt();
			bt[i]=val;
		}
		System.out.println();
//		FCFS();
//		SJF();
	}
	
	public void FCFS () {
		System.out.println("\nFirst Come First Serve::\n");
		int temp;
		for(int i=0;i<process;i++) 
		{
			for(int j=0;j<process-(i+1);j++)
			{
				if(at[j] > at[j+1])
				{
					temp = at[j];
					at[j] = at[j+1];
					at[j+1] = temp;
					temp = bt[j];
					bt[j] = bt[j+1];
					bt[j+1] = temp;
					temp = pid[j];
					pid[j] = pid[j+1];
					pid[j+1] = temp;
				}
			}
		}

		for(int i=0;i<process;i++) 
		{
			if(i==0)
				ct[i] = at[i] + bt[i];
			else if(at[i] <= ct[i-1])
			{
				ct[i] = ct[i-1] + bt[i];
			}
			else
			{
				ct[i] = at[i] + bt[i];
			}
			wt[i] = ct[i] - at[i] - bt[i];
			tat[i] = ct[i] - at[i];
			avgWt+=wt[i];
			avgTat+=tat[i];
		}
		avgWt = avgWt/process;
		avgTat = avgTat/process;
		displayTable();
		
		System.out.println("\nGantt Chart:");

		if(at[0]!=0)
			System.out.print("0 -> idle -> " + at[0]);
		else
			System.out.print(at[0]);
		for(int i=0;i<process;i++)
		{
			System.out.print(" -> P" + pid[i] + " -> " + ct[i]);
		}
		System.out.println();
		
		System.out.println("\nAverage WT: " + avgWt);
		System.out.println("Average TAT: " + avgTat);
		System.out.println();
	}
	
	public void displayTable() {
		System.out.println("PId" + '\t' + "AT" + '\t' + "BT" + "\t" + "CT" + "\t" + "TAT" + "\t" + "WT");
		for(int i=0;i<process;i++) 
		{
			System.out.println(pid[i] + "\t" + at[i] + "\t" + bt[i] + "\t" + ct[i] + "\t" + tat[i] + "\t" + wt[i]);
		}
	}
	
	public void SJF() {
		System.out.println("\nShortest Job First::\n");
		int k[] = new int[process];//stores burst time
		int f[] = new int[process];//flag for checking if process is completed or not
		LinkedList<Integer> gc = new LinkedList<Integer>();
		LinkedList<Integer> time = new LinkedList<Integer>();
		for(int i=0;i<process;i++)
		{
			k[i]=bt[i];
			f[i]=0;
		}
		int tot=0;// total process completed
		int st=0;// system time
		while(true) 
		{
			int c=process, min=999999;
			if(c==tot)
				break;
			for(int i=0;i<process;i++)
			{
				if((at[i]<=st)&&(f[i]==0)&&(k[i]<min))
				{
					min=k[i];
					c=i;
				}
			}
			if(c==process)
				st++;
			else
			{
				if(gc.isEmpty() || pid[c]!=gc.getLast())
				{
					gc.add(pid[c]);
					time.add(st);
				}
				k[c]--;
				st++;
				if(k[c]==0)
				{
					f[c]=1;
					ct[c]=st;
					tot++;
				}
			}
		}
		int maxCt=0;
		for(int i=0;i<process;i++)
		{
			wt[i] = ct[i] - at[i] - bt[i];
			tat[i] = ct[i] - at[i];
			avgWt+=wt[i];
			avgTat+=tat[i];
			if(maxCt<ct[i])
				maxCt=ct[i];
		}
		avgWt = avgWt/process;
		avgTat = avgTat/process;
		displayTable();

		System.out.println("\nGantt Chart:");
		for(int i=0;i<gc.size();i++) 
		{
			System.out.print( time.get(i) + " -> P" + gc.get(i) + " -> " );
		}
		System.out.println(maxCt);
		
		System.out.println("\nAverage WT: " + avgWt);
		System.out.println("Average TAT: " + avgTat);
		System.out.println();
	}
	
	
	
	public void roundRobin() {
		System.out.println("\nRound Robin::\n");
		System.out.print("Enter time quantum: ");
		tq = sc.nextInt();
		System.out.println();
		int k[] = new int[process];
		int f[] = new int[process];
		for(int i=0;i<process;i++)
		{
			k[i] = bt[i];
			f[i] = 0;
		}
		Queue<Integer> rq = new LinkedList<>();
		LinkedList<Integer> gc = new LinkedList<Integer>();
		
		rq.add(pid[0]);
		int t = 0; 
		while(true)
		{ 
			boolean done = true;
			
			if(rq.size()>0)
			{
				 int id = rq.peek();
				 rq.remove();
				 int index=-1;
				 for(int i=0;i<process;i++)
				 {
					 if(pid[i]==id)
					 {
						 gc.add(pid[i]);
						 index=i;
						 break;
					 }
				 }
				 if(k[index]>0 && f[index]==0)
				 {
					done = false;
					if (k[index] > tq)
					{ 
						t += tq; 
						k[index] -= tq;
					}
					else
					{ 
						t += k[index];   
						k[index] = 0; 
						f[index]=1;
					}
				 }
				for(int i=0;i<process;i++)
				{
					if(at[i]<=t && at[i]>t-tq && f[i]!=1)
					{
						rq.add(pid[i]);
					}
				}
				if (k[index]>0)
				{
					rq.add(pid[index]);
				}
				

				if(k[index]==0 && f[index]==1) 
				{
					f[index]=0;
					ct[index] = t;
				}
			}
			
			if (done == true) 
				break; 
		}  
		
		
		for(int i=0;i<process;i++)
		{
			tat[i]=ct[i]-at[i];
			wt[i]=tat[i]-bt[i];
            avgWt+= wt[i];
	    	avgTat+= tat[i];
		}
		avgWt = avgWt/process;
		avgTat = avgTat/process;

		displayTable();
		
		System.out.println("\nGannt Chart: ");
		for (int i = 0; i < gc.size(); i++) 
		{
            System.out.print("P" + gc.get(i));
            if(i!=gc.size()-1)
            	System.out.print(" -> ");
		}
		
		System.out.println("\nAverage WT: " + avgWt);
		System.out.println("Average TAT: " + avgTat);
		System.out.println();
	}
	
	
	public void priority(){
		System.out.println("\nPriority::\n");
		int prt[] = new int[process];
		int f[] = new int[process];
		for(int i = 0; i < process; i++)
		{
			System.out.print("Enter priority of process "+ (i+1)+ ": ");
			prt[i] = sc.nextInt();
			f[i]=0;
		}
		Queue<Integer> rq = new LinkedList<Integer>();
		LinkedList<Integer> gc = new LinkedList<Integer>();

		rq.add(pid[0]);
		int st=0;
		while(true)
		{
			boolean done = true;
			for(int i=0;i<process;i++)
			{
				if(f[i]==0)
					done = false;
			}
			if(rq.size()>0)
			{
				int id = -1;
				int mpri=-1;
				for(int rqid : rq)
				{
					if(prt[rqid-1]>mpri)
					{
						id=rqid;
						mpri=prt[rqid-1];
					}
				}
				rq.remove(id);
				int index=id-1;
				gc.add(pid[index]);
				f[index]=1;
				st+=bt[index];
				ct[index]=st;
				for(int i=0;i<process;i++)
				{
					if(at[i]<=st && at[i]>st-bt[index] && f[i]!=1)
					{
						rq.add(pid[i]);
					}
				}
			}
			
			
			if(done==true)
				break;
		}
		for(int i=0;i<process;i++)
		{
			tat[i]=ct[i]-at[i];
			wt[i]=tat[i]-bt[i];
            avgWt+= wt[i];
	    	avgTat+= tat[i];
		}
		avgWt = avgWt/process;
		avgTat = avgTat/process;
		
		System.out.println("PId" + '\t' + "AT" + '\t' + "BT" + "\t" + "Pri" + "\t" + "CT" + "\t" + "TAT" + "\t" + "WT");
		for(int i=0;i<process;i++) 
		{
			System.out.println(pid[i] + "\t" + at[i] + "\t" + bt[i] + "\t" + prt[i] + "\t" + ct[i] + "\t" + tat[i] + "\t" + wt[i]);
		}

		System.out.println("\nGannt Chart: ");
		for (int i = 0; i < gc.size(); i++) 
		{
            System.out.print("P" + gc.get(i));
            if(i!=gc.size()-1)
            System.out.print(" -> ");
		}
		
		System.out.println("\nAverage WT: " + avgWt);
		System.out.println("Average TAT: " + avgTat);
		System.out.println();
		
	}
	

}
