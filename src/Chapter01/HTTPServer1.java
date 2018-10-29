package Chapter01;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class HTTPServer1 {
	private static Map servletCache = new HashMap();
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
		byte[] requestBuffer = new byte[size];
		socketIn.read(requestBuffer);
		String request = new String(requestBuffer);
		System.out.println(request);
		//���http����ĵ�һ��
		String firstLineOfRequest = request.substring(0, request.indexOf("\r\n"));
		//����http����ĵ�һ��
		String[] parts = firstLineOfRequest.split(" ");
		String uri = parts[1];
		//����http��Ӧ���ĵ����ͣ��˴����˼򻯴���
		String contentType;
		
		if (uri.indexOf("servlet") != -1)
		{
			String servletName = null;
			if (uri.indexOf("?") != -1)
				servletName = uri.substring(uri.indexOf("servlet/") + 8, uri.indexOf("?"));
			else
				servletName = uri.substring(uri.indexOf("servlet/") + 8, uri.length());
			
			Servlet servlet = (Servlet)servletCache.get(servletName);
			if (servlet == null)
			{
				servlet = (Servlet)Class.forName("Chapter01." + servletName).newInstance();
				servlet.init();
				servletCache.put(servletName, servlet);
			}
			//����servlet��service()����
			servlet.service(requestBuffer, socket.getOutputStream());
			Thread.sleep(1000);
			socket.close();
			return;
		}
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
		InputStream in = HTTPServer1.class.getResourceAsStream("root" + uri);
		
		/*����http��Ӧ���*/
		OutputStream socketOut = socket.getOutputStream();
		//����http��Ӧ��һ��
		socketOut.write(responseFirstLine.getBytes());
		//����http����Ӧͷ
		socketOut.write(responseHeader.getBytes());
		//����http��Ӧ����
		int len = 0;
		requestBuffer = new byte[128];
		while ((len = in.read(requestBuffer)) != -1)
			socketOut.write(requestBuffer, 0, len);
		
		Thread.sleep(1000);
		socket.close();
	}
}
