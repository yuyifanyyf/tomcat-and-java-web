package Chapter01;
import java.io.*;
import java.net.*;

public class HTTPServer {
	public static void main(String[] args)
	{
		int port;
		ServerSocket serverSocket;
		
		try
		{
			port = Integer.parseInt(args[0]);
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.println("port = 8080 (Ĭ��)");
			port = 8080;
		}
		
		try
		{
			serverSocket = new ServerSocket(port);
			System.out.println("���������ڼ����˿ڣ�" + serverSocket.getLocalPort());
			
			while (true)
			{
				try
				{
					//�ȴ��ͻ��˵�TCP����
					final Socket socket = serverSocket.accept();
					System.out.println("��������ͻ���һ���� ��TCP���ӣ��ÿͻ��ĵ�ַΪ��" 
							+ socket.getInetAddress() + ":" + socket.getPort());
					service(socket);
				}catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public static void service(Socket socket) throws Exception {
		InputStream socketIn = socket.getInputStream();
		Thread.sleep(500);
		int size = socketIn.available();
		System.out.println(size);
		byte[] buffer = new byte[size];
		socketIn.read(buffer);
		String request = new String(buffer);
		System.out.println(request);
		//���http����ĵ�һ��
		String firstLineOfRequest = request.substring(0, request.indexOf("\r\n"));
		//����http����ĵ�һ��
		String[] parts = firstLineOfRequest.split(" ");
		String uri = parts[1];
		//����http��Ӧ���ĵ����ͣ��˴����˼򻯴���
		String contentType;
		if (uri.indexOf("html") != -1 || uri.indexOf("htm") != -1)
			contentType = "text/html";
		else if (uri.indexOf("jpg") != -1 || uri.indexOf("jpeg") != -1)
			contentType = "image/jpeg";
		else if (uri.indexOf("gif") != -1)
			contentType = "image/gif";
		else 
			contentType = "application/octet-stream";
		
		/*����HTTP��Ӧ���*/
		//HTTP��Ӧ��һ��
		String responseFirstLine = "HTTP/1.1 200 OK\r\n";
		//HTTP��Ӧͷ
		String responseHeader = "Content-Type:" + contentType + "\r\n\r\n";
		//��ö�ȡ��Ӧ����������
		//��.class�ļ�Ϊ��׼�����·������.class�ļ�������.java�ļ�
		InputStream in = HTTPServer.class.getResourceAsStream("root" + uri);
		
		/*����http��Ӧ���*/
		OutputStream socketOut = socket.getOutputStream();
		//����http��Ӧ��һ��
		socketOut.write(responseFirstLine.getBytes());
		//����http����Ӧͷ
		socketOut.write(responseHeader.getBytes());
		//����http��Ӧ����
		int len = 0;
		buffer = new byte[128];
		while ((len = in.read(buffer)) != -1)
			socketOut.write(buffer, 0, len);
		
		Thread.sleep(1000);
		socket.close();
	}
}
