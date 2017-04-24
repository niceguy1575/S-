package udp_tcp;

import java.io.File;
import java.util.*;

public class clientUI {
	public static void main(String[] args) {
		
		System.out.println("------------ UDP / TCP Client ------------");
		System.out.println("------------------------------------------");
		System.out.println("------------ 1. UDP Client ---------------");
		System.out.println("------------ 2. TCP Client ---------------");
		System.out.println("------------------------------------------");

		Scanner protocolScan = new Scanner(System.in);
		Scanner ipScan = new Scanner(System.in);
		int protocol = protocolScan.nextInt();
		System.out.println("selected protocol : " + protocol);
		String ipAdr = ipScan.nextLine();
		System.out.println("ip address : " + ipAdr);
		protocolScan.close();
		ipScan.close();
		
		if(protocol == 1) {
			System.out.println("\n");
			String srcPath = "C:\\prac\\";
			String destPath = "C:\\prac1\\";

			//���� �ּҿ� ��Ʈ��ȣ�� �����Ͽ� ������ ����
			UdpClient client = new UdpClient(ipAdr, 8001, srcPath, destPath);
			client.createConnection();
		}
		else if(protocol == 2) {
			System.out.println("\n");
			String srcPath = "C:\\prac\\";
			String destPath = "C:\\prac1\\";
			
			// meta data
			File directory = new File(srcPath);
			File[] fList = directory.listFiles();
			String len = String.valueOf(fList.length);
			String goBack;
			//���� �ּҿ� ��Ʈ��ȣ�� �����Ͽ� ������ ����
			TcpClient client = new TcpClient(ipAdr, 8000, srcPath, destPath);

			client.send(len);
			
			for(File file:fList){
				goBack = client.receive();
				if(goBack.equals("continue")) {
					client.sendFile(file.getAbsolutePath());
				}
			}
			
			client.receive();
			client.close();
		}
		else {
			System.out.println("�߸��� �Է��Դϴ�. ���α׷��� �����մϴ�.");
			System.exit(0);
		}
	}
}
