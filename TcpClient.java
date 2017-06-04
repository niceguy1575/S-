package Client;

import java.io.*;
import java.net.*;
import metaEvent.*;

public class TcpClient {
	Socket socket;
	ObjectOutputStream outputStream;
	boolean isConnected = false;
	FileEvent fileEvent;
	BufferedReader in;
	PrintWriter out;
	String srcPath, destPath, ip;
	int port = 8000;
	CRC32get crc = new CRC32get();
	
	public TcpClient(String ip, int port, String srcPath, String destPath) {
		
		this.ip = ip;
		this.port = port;
		this.srcPath = srcPath;
		this.destPath = destPath;
		
		while(!isConnected) {
			try {
				socket = new Socket(ip, port);	
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
				outputStream = new ObjectOutputStream(socket.getOutputStream());
				//���� ���Ͽ� ��Ʈ���� ����
				printInfo();
				isConnected = true;

			} catch (IOException e) {
				System.out.println(e.toString());			
			}
		}
	}
	
	public String receive() {
		String mesg = null;
		try {
			//���� �������κ��� ���� �޽����� ȭ�鿡 ���
//			System.out.println("[����] "+ in.readLine());
			mesg = in.readLine();
			return mesg;
		} catch (IOException e) {
			System.out.println(e.toString());			
		}
		return mesg;
	}
	
	public void send(String msg) {
		//���� ���Ͽ� �޽��� ����
		out.println(msg);
		out.flush();
		System.out.println("[Ŭ���̾�Ʈ] " + msg);		
	}
	
	public void sendFile(String srcPath) {
		FileEvent fileEvent = new FileEvent();
		
		String fileName = srcPath.substring(srcPath.lastIndexOf("\\") + 1, srcPath.length());
		//String path = srcPath.substring(0, srcPath.lastIndexOf("/") + 1);
		fileEvent.setDestDir(destPath);
		fileEvent.setFilename(fileName);
		fileEvent.setSrcDir(srcPath);
		
		File file = new File(srcPath);

			if (file.isFile()) {
				try {
//					DataInputStream diStream = new DataInputStream(new FileInputStream(file));
					long len = (int) file.length();
					byte[] fileBytes = new byte[(int) len];

//					int read = 0;
//					int numRead = 0;
//					while (read < fileBytes.length && (numRead = diStream.read(fileBytes, read, fileBytes.length - read)) >= 0) {
//						read = read + numRead;
////					    if(){
////		                     System.exit(0);
////		                  }
//					}
//					
					long startTime = System.currentTimeMillis();
					fileEvent.settime(startTime);
					fileEvent.setFileSize(len);
					fileEvent.setFileData(fileBytes);
					fileEvent.setStatus("Success");
					fileEvent.setCRC32Value(crc.getCRC32(srcPath,fileBytes));
					
					System.out.println((int) fileBytes.length+"\n");

					for(int i=0; i<fileBytes.length; i++){
						System.out.println(fileBytes[i]);
					}
//					System.out.println();
//					for(int i=0; i<sendBytes.length; i++){
//					System.out.println(sendBytes[i]);
//			}
					
//					diStream.close();
				} catch (Exception e) {
					e.printStackTrace();
					fileEvent.setStatus("Error");
				}
			} else {
				System.out.println("path is not pointing to a file");
				fileEvent.setStatus("Error");
				System.out.println("TCP client�� �����մϴ�.");
				System.exit(0);
			}
			// write file
			try {
				outputStream.writeObject(fileEvent);
				System.out.println("���� ���� �Ϸ�");
				Thread.sleep(3000);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
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
		System.out.println(">> ���� ���ӿ� �����߽��ϴ�.");
		//���� ��Ʈ ��ȣ�� Ŭ���̾�Ʈ �ּҿ� ��Ʈ��ȣ ���
		System.out.println("     ���� �ּ�: " + socket.getInetAddress());
		System.out.println("     ���� ��Ʈ��ȣ: " + socket.getPort());
		System.out.println("     Ŭ������Ʈ ��Ʈ��ȣ: " + socket.getLocalPort() + '\n');
	}
}