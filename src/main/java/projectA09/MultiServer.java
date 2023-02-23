package projectA09;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Receive extends Thread {
	static List<PrintWriter> list = Collections.synchronizedList(new ArrayList<PrintWriter>());
	Socket socket = null;
	BufferedReader in = null;
	PrintWriter out = null;
	
	public Receive(Socket socket) {
		this.socket = socket;
		try {
			out = new PrintWriter(socket.getOutputStream());
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			list.add(out);
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void run() {
		String name = "";
		try {
			name = in.readLine();
			System.out.println("["+name+"새연결생성]");
			sendAll("["+name+"]님이 들어오셨습니다.");
			while(in != null) {
				String inputMsg = in.readLine();
				if("quit".equals(inputMsg)) break;
				sendAll(name+">>"+inputMsg);
			}
		} catch(IOException e) {
			System.out.println("["+name+"접속끊김]");
		} finally {
			sendAll("["+name+"]님이 나가셨습니다.");
			list.remove(out);
			try {
				socket.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		} 
	}
	private void sendAll(String s) {
		for(PrintWriter out:list) {
			out.println(s);
			out.flush();
		}
	}
}

public class MultiServer {

	public static void main(String[] args) {
		MultiServer multiServer = new MultiServer();
		multiServer.start();

	}
	
	public void start() {
		ServerSocket serverSocket = null;
		Socket socket = null;
		try {
			serverSocket = new ServerSocket(8000);
			while(true) {
				System.out.println("[클라이언트 연결대기중]");
				socket = serverSocket.accept();
				
				Receive receiveThread = new Receive(socket);
				receiveThread.start();
			}
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			if(serverSocket != null) {
				try {
					serverSocket.close();
					System.out.println("[서버종료]");
				} catch(IOException e) {
					e.printStackTrace();
					System.out.println("[서버소켓통신에러]");
				}
			}
		}
	}

}
