package Server;

import java.io.*;
import java.net.*;

import metaEvent.*;
import javax.swing.JOptionPane;


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
    CRC32get crc = new CRC32get();
    static double avgTime;
    
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
			long existFileSize = 0;
			int result = 1; // ConfirmDialog ��ȯ��.
			   if(!dstFile.exists()){
			    // ������,
			   }else{
			    // ������,
			    result = JOptionPane.showConfirmDialog(null, "' ���� ��ο� \n" + fileEvent.getDestDir()+ "\n"+fileEvent.getFilename()+"�� �̹� �����մϴ�. �̾�ޱ� �Ͻðڽ��ϱ�?");
			    if(result == 0){    // ��
			     existFileSize = dstFile.length();
			    }else if(result == 1){  // �ƴϿ�.
			     existFileSize = 0;
			    }
			   }

			fileOutputStream = new FileOutputStream(dstFile,true);
			long len = existFileSize;
			byte[] fileBytes = new byte[(int) len];
			System.out.format("dstFile %d \n",fileBytes.length);
			System.out.format("getFileData %d\n",fileEvent.getFileData().length);
			fileOutputStream.write(fileEvent.getFileData(),fileBytes.length,fileEvent.getFileData().length-fileBytes.length);
			fileOutputStream.flush();
			fileOutputStream.close();
	

			long totaltime = System.currentTimeMillis() - fileEvent.gettime();
			long s = fileEvent.getFileSize();
			avgTime += (double) s/(totaltime * 1000);
			
			if(checkCRCValue(fileEvent, crc.getCRC32(outputFile,fileEvent.getFileData())) == 0 ) {
				System.out.format("���Ἲ ����!\n");
			} else {
				System.out.format("���Ἲ�� ������ �� �����ϴ�!\n");
			}
			
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
	
	public long checkCRCValue(FileEvent event, long crcValue) {
		// �ٸ��� -1 return
		// ������ 0 return
		if(event.getCRC32Value() == crcValue) {
			return 0; 
		}
		else {
			return -1;
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
		String metaData;
		TcpServer server = new TcpServer(8000);
		server.waitForClient();
		metaData = server.receive();
		server.send("continue");
		
		for(int i = 0 ; i < Integer.parseInt(metaData) ; i ++) {
			server.receiveFile();
			server.send("continue");
		}
		
		System.out.println("file ���ۼӵ� : "+ 1000 * avgTime / (double) Integer.parseInt(metaData)  + "bps");
		System.out.println("file ���ۼӵ� : "+ avgTime / (double) Integer.parseInt(metaData)  + "Mb/s");
		
		server.send("������ �� �޾ҽ��ϴ�!");
		server.close();
	}
}