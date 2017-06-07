package Client;

import java.io.*;
import java.util.*;
import metaEvent.*;

public class clientUI {
			
	public static void main(String[] args) {
		
		KBArray KBArr = new KBArray();
		
		System.out.println("Welcome! We are Statistic Analysis Server!");

		System.out.println("------------ IP Address -----------------");
		System.out.println("-----------------------------------------");
		System.out.print("   > ");

		Scanner ipScan = new Scanner(System.in);
		String ipAdr = ipScan.nextLine();
		ipScan.close();

		System.out.println();
		System.out.println("-----------------------------------------");
		System.out.println("------------ 1. �м��� ������ ��� ------------");
		System.out.println("------ �м������� ����� ��θ� �Է����ּ���. -------");
		System.out.println("-- �м������� ���������� ���������� �����Ǿ��־�� �մϴ�.-");
		System.out.println("------- ���� ù���� �� ������ �̸��� �־��ּ��� ------");
		System.out.println("-----------------------------------------");
		System.out.print("   > ");
		Scanner pathScan = new Scanner(System.in);
		String srcPath = pathScan.nextLine();
		
		System.out.println("------------------------------------------");
		System.out.println("----------- 2. �мư���� ������ ��� ------------");
		System.out.println("------------------------------------------");
		System.out.print("   > ");
		String destPath = pathScan.nextLine();
		pathScan.close();
		
//		String srcPath = "C:\\prac\\";
//		String destPath = "C:\\prac1\\";

		// Client Connect
		TcpClient TCPclient = new TcpClient(ipAdr, 8000, srcPath, destPath);
		UdpClient UDPclient = new UdpClient(ipAdr, 8001, srcPath, destPath);
		
		File[] fList = KBArr.revDir(srcPath);

		List<Double> kbArr = new ArrayList<Double>();
		kbArr = KBArr.KBArr(srcPath);
		Collections.sort(kbArr ,Collections.reverseOrder());
		
		int k = KBArr.idx_64(kbArr);
		int n = kbArr.size();
		
		try {
			if( k == 0) {
				if(n==1) {
					if( kbArr.get(k) > 64 ) {
						// TCP 1
						String goBack = null;
						TCPclient.send(String.valueOf( 1 ));
						for(int i = 0 ; i < 1 ; i ++) {
							goBack = TCPclient.receive();
							if(goBack.equals("continue")) {
								TCPclient.sendFile(fList[i].getAbsolutePath());
							}
						}
						
						Thread.sleep(10000);
						String UDPSpeed = UDPclient.createConnection( 0 );
					}
					else if(kbArr.get(k) < 64) {
						
						TCPclient.send(String.valueOf( 0 ));
						//UDP 1
						String UDPSpeed = UDPclient.createConnection(1);
					}
					
				}
				else {
					TCPclient.send(String.valueOf( 0 ));
					// UDP n
					String UDPSpeed = UDPclient.createConnection(n);
				}
			}
			else if( k == 1) {
				// TCP 1 , UDP n-1
				String goBack = null;
				TCPclient.send(String.valueOf( 1 ));
				for(int i = 0 ; i < 1 ; i ++) {
					goBack = TCPclient.receive();
					if(goBack.equals("continue")) {
						TCPclient.sendFile(fList[i].getAbsolutePath());
					}
				}
				
				Thread.sleep(10000);
				String UDPSpeed = UDPclient.createConnection( n-1 );
			}
			else if( k == n-1 && k!=0 ) {
				if( kbArr.get(k) < 64) {
					// TCP n-1, UDP 1
					String goBack = null;
					TCPclient.send(String.valueOf( n-1 ));
					for(int i = 0 ; i < n-1 ; i ++) {
						goBack = TCPclient.receive();
						if(goBack.equals("continue")) {
							TCPclient.sendFile(fList[i].getAbsolutePath());
						}
					}
					
					Thread.sleep(10000);
					String UDPSpeed = UDPclient.createConnection( 1 );
				}
				else {
					//TCP n
					String goBack = null;
					TCPclient.send(String.valueOf( n ));
					for(int i = 0 ; i < n ; i ++) {
						goBack = TCPclient.receive();
						if(goBack.equals("continue")) {
							TCPclient.sendFile(fList[i].getAbsolutePath());
						}
					}
					Thread.sleep(10000);
					String UDPSpeed = UDPclient.createConnection( 0 );
				}
			}
			else {
				// TCP k, UDP n-k
				String goBack = null;
				TCPclient.send(String.valueOf( k ));
				for(int i = 0 ; i < k ; i ++) {
					goBack = TCPclient.receive();
					if(goBack.equals("continue")) {
						TCPclient.sendFile(fList[i].getAbsolutePath());
					}
				}
				
				Thread.sleep(10000);
				String UDPSpeed = UDPclient.createConnection( n-k );
			}
		}
		 catch (InterruptedException e) {
				e.printStackTrace();
		 }
		
		TCPclient.close();
		System.out.println("Client�� �����մϴ�.");
//		double finalSpeed = Double.parseDouble(TCPSpeed) + Double.parseDouble(UDPSpeed);
//		
//		finalSpeed = finalSpeed / fList.length;
//		System.out.println("file ���ۼӵ� : "+ 1000 * Math.round(finalSpeed)  + "bps");
//		System.out.println("file ���ۼӵ� : "+ Math.round(finalSpeed)  + "Mb/s");
		System.exit(0);
	}
}