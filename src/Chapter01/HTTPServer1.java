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
			System.out.println("port = 8080 (默认)");
			port = 8080;
		}
		
		try
		{
			serverSocket = new ServerSocket(port);
			System.out.println("服务器正在监听端口：" + serverSocket.getLocalPort());
			
			while (true)
			{
				try
				{
					//等待客户端的TCP请求
					final Socket socket = serverSocket.accept();
					System.out.println("建立了与客户的一个新 的TCP连接，该客户的地址为：" 
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
		//获得http请求的第一行
		String firstLineOfRequest = request.substring(0, request.indexOf("\r\n"));
		//解析http请求的第一行
		String[] parts = firstLineOfRequest.split(" ");
		String uri = parts[1];
		//决定http响应正文的类型，此处做了简化处理
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
			//调用servlet的service()方法
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
		
		/*创建HTTP相应结果*/
		//HTTP响应第一行
		String responseFirstLine = "HTTP/1.1 200 OK\r\n";
		//HTTP响应头
		String responseHeader = "Content-Type:" + contentType + "\r\n\r\n";
		//获得读取响应正文输入流
		//以.class文件为基准的相对路径，是.class文件，不是.java文件
		InputStream in = HTTPServer1.class.getResourceAsStream("root" + uri);
		
		/*发送http响应结果*/
		OutputStream socketOut = socket.getOutputStream();
		//发送http响应第一行
		socketOut.write(responseFirstLine.getBytes());
		//发送http的响应头
		socketOut.write(responseHeader.getBytes());
		//发送http响应正文
		int len = 0;
		requestBuffer = new byte[128];
		while ((len = in.read(requestBuffer)) != -1)
			socketOut.write(requestBuffer, 0, len);
		
		Thread.sleep(1000);
		socket.close();
	}
}
