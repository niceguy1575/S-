package tcp;

import java.io.*;
import java.net.*;

import udp.FileEvent;

public class TcpServer {
	int port = 8000;
	ServerSocket server;
	Socket socket;
	ObjectInputStream inputStream;
	FileEvent fileEvent;
	File dstFile;
	FileOutputStream fileOutputStream;
	BufferedReader in;
	PrintWriter out;

	public TcpServer (int port) {
		try {
			this.port = port;
			
			System.out.println(">> ������ �����մϴ�.");
			
			this.server = new ServerSocket(port);			
		} catch (IOException e) {
			System.out.println(e.toString());	
		}
	}
	
	public void waitForClient() {
		System.out.println(">> Ŭ���̾�Ʈ�� �����ϱ� ��ٸ��� �ֽ��ϴ�.");
		try {
			// Ŭ���̾�Ʈ ���Ӷ����� ���
			socket = server.accept(); 
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
			inputStream = new ObjectInputStream(socket.getInputStream());

			printInfo();
			
		} catch (IOException e) {
			System.out.println(e.toString());
		}
	}

	public void receiveFile() {
		try {
			fileEvent = (FileEvent) inputStream.readObject();

			if (fileEvent.getStatus().equalsIgnoreCase("Error")) {
				System.out.println("������ �߻��Ͽ����ϴ�. �����մϴ�.");
				System.exit(0);
			}
			
			String outputFile = fileEvent.getDestDir() + fileEvent.getFilename();
			
			// ��ΰ� ������ ���� �����.
			if (!new File(fileEvent.getDestDir()).exists()) {
				new File(fileEvent.getDestDir()).mkdirs();
			}
			
			dstFile = new File(outputFile);
			
			fileOutputStream = new FileOutputStream(dstFile);
			fileOutputStream.write(fileEvent.getFileData());
			fileOutputStream.flush();
			fileOutputStream.close();
			
			System.out.println("Output file : " + outputFile + "is successfully saved");
			
			Thread.sleep(3000);
					
		} catch (IOException e) {
			System.out.println(e.toString());			
		}catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void receive() {
		try {
			//���� �������κ��� ���� �޽����� ȭ�鿡 ���
			System.out.println("[����] "+ in.readLine());		
		} catch (IOException e) {
			System.out.println(e.toString());			
		}
	}
	
	public void send(String msg) {
		// Ŭ���̾�Ʈ ���Ͽ� �޽��� ����
		out.println(msg);
		out.flush();
		System.out.println("[����] " + msg);		
	}
	
	public void close() {
		try {
			// Ŭ���̾�Ʈ ���� ����
			socket.close();		
		} catch(IOException e) {
			System.out.println(e.toString());
		}
	}

	public void printInfo() {
		System.out.println(">> Ŭ���̾�Ʈ�� ���ӿ� �����߽��ϴ�.");
		//���� ��Ʈ ��ȣ�� Ŭ���̾�Ʈ �ּҿ� ��Ʈ��ȣ ���
		System.out.println("     ���� ��Ʈ��ȣ: " + socket.getLocalPort());
		System.out.println("     Ŭ���̾�Ʈ �ּ�: " + socket.getInetAddress());
		System.out.println("     Ŭ���̾�Ʈ ��Ʈ��ȣ: " + socket.getPort() + '\n');
	}
	
	public static void main(String[] args) {
		TcpServer server = new TcpServer(8000);
		server.waitForClient();
		server.receiveFile();
		server.send("������ �� �޾ҽ��ϴ�!");
		server.close();
	}
}