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
		byte[] buffer = new byte[size];
		socketIn.read(buffer);
		String request = new String(buffer);
		System.out.println(request);
		//获得http请求的第一行
		String firstLineOfRequest = request.substring(0, request.indexOf("\r\n"));
		//解析http请求的第一行
		String[] parts = firstLineOfRequest.split(" ");
		String uri = parts[1];
		//决定http响应正文的类型，此处做了简化处理
		String contentType;
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
		InputStream in = HTTPServer.class.getResourceAsStream("root" + uri);
		
		/*发送http响应结果*/
		OutputStream socketOut = socket.getOutputStream();
		//发送http响应第一行
		socketOut.write(responseFirstLine.getBytes());
		//发送http的响应头
		socketOut.write(responseHeader.getBytes());
		//发送http响应正文
		int len = 0;
		buffer = new byte[128];
		while ((len = in.read(buffer)) != -1)
			socketOut.write(buffer, 0, len);
		
		Thread.sleep(1000);
		socket.close();
	}
}
