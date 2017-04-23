package udp_tcp;

import java.io.*;
import java.net.*;

public class UdpClient {
	DatagramSocket dsock;
	DatagramPacket sPack, rPack;
	InetAddress server;
	int port = 8000;
	String srcPath, destPath;
	FileEvent event;

	public UdpClient(String ip, int port, String srcPath, String destPath) {
		try{
			server = InetAddress.getByName(ip);
			this.port = port;
			this.dsock = new DatagramSocket();
			this.srcPath = srcPath;
			this.destPath = destPath;
			
			System.out.println("connecting to server...");
		} catch(Exception e) {
			System.out.println(e);
		}
	}
	
	public void createConnection(){
		
		
		/*
		 * 1. file length
		 * 2. length receive
		 * 3. end
		 */
		try{
			
			while(true) {
				// meta data
				File directory = new File(srcPath);
				File[] fList = directory.listFiles();
				String len = String.valueOf(fList.length);
				// 1. file length
				
				String strOut = len;
				byte[] strOutByte = strOut.getBytes();
				
				// file length send
				sPack = new DatagramPacket(strOutByte, strOutByte.length, server, port);
				dsock.send(sPack);
				
				byte[] inputData = new byte[1024 * 64];
				
				// 2. length receive

				rPack = new DatagramPacket(inputData, inputData.length);
				dsock.receive(rPack);

				String response = new String(rPack.getData());
				for(int i = 0 ; i < Integer.parseInt(len); i ++){					
					if(response.charAt(0) == 'c') {
						event = getFileEvent(fList[i].getAbsolutePath());
						
						ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
						ObjectOutputStream os = new ObjectOutputStream(outputStream);
						
						os.writeObject(event);
						
						byte[] data = outputStream.toByteArray();
						
						sPack = new DatagramPacket(data, data.length, server, port);
						
						dsock.send(sPack);
						Thread.sleep(2000);
					}
				}
				System.out.println("UDP client를 종료합니다.");
				System.exit(0);
			}
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public FileEvent getFileEvent(String srcPath) {
		
		FileEvent fileEvent = new FileEvent();
		
		String fileName = srcPath.substring(srcPath.lastIndexOf("\\") + 1, srcPath.length());
		//String path = srcPath.substring(0, srcPath.lastIndexOf("/") + 1);
		fileEvent.setDestDir(destPath);
		fileEvent.setFilename(fileName);
		fileEvent.setSrcDir(srcPath);
		
		File file = new File(srcPath);
		
		if (file.isFile()) {
			try {
				DataInputStream diStream = new DataInputStream(new FileInputStream(file));
				long len = (int) file.length();
				byte[] fileBytes = new byte[(int) len];
				
				int read = 0;
				int numRead = 0;
				while (read < fileBytes.length && (numRead = diStream.read(fileBytes, read, fileBytes.length - read)) >= 0) {
					read = read + numRead;
				}

				fileEvent.setFileSize(len);
				fileEvent.setFileData(fileBytes);
				fileEvent.setStatus("Success");
				diStream.close();
			} catch (Exception e) {
				e.printStackTrace();
				fileEvent.setStatus("Error");
			}
		} else {
			System.out.println("path is not pointing to a file");
			fileEvent.setStatus("Error");
			System.out.println("UDP client를 종료합니다.");
			System.exit(0);
		}
		return fileEvent;
	}
	
	public static void main(String[] args) {
		String srcPath = "C:/prac/";
		String destPath = "C:/prac1/";

		//서버 주소와 포트번호를 지정하여 서버에 접속
		UdpClient client = new UdpClient("127.0.0.1", 8001, srcPath, destPath);
		client.createConnection();
	}
}
