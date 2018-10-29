package Chapter01;
import java.io.*;

public class HelloServlet implements Servlet{

	@Override
	public void init() throws Exception {
		// TODO Auto-generated method stub
		System.out.println("HelloServlet is inited");
	}

	@Override
	public void service(byte[] requestBuffer, OutputStream out) throws Exception {
		// TODO Auto-generated method stub
		String request = new String(requestBuffer);
		//获得HTTP请求的第一行
		String firstLineOfRequest = request.substring(0,  request.indexOf("\r\n"));
		//解析HTTP请求的第一行
		String[] parts = firstLineOfRequest.split(" ");
		String method = parts[0];
		String uri = parts[1];
		
		/*获得请求参数username*/
		String username = null;
		if (method.equalsIgnoreCase("get") && uri.indexOf("username=") != -1)
		{
			/*假定uri="servlet/HelloServlet?username=Tom&password=1234"*/
			//parameters="username=Tome&password=1234"
			String parameters = uri.substring(uri.indexOf("?"), uri.length());
			
			parts = parameters.split("&");
			parts = parts[0].split("=");
			username = parts[1];
		}
		if (method.equalsIgnoreCase("post"))
		{
			int locate = request.indexOf("\r\n\r\n");
			//获得响应正文
			String content = request.substring(locate + 4, request.length());
			if (content.indexOf("username=") != -1)
			{
				parts = content.split("&");
				parts = parts[0].split("=");
				username = parts[1];
			}
		}
		
		out.write("HTTP/1.1 200 OK\r\n".getBytes());
		out.write("Content-Type:text/html\r\n\r\n".getBytes());
		out.write("<html><head><title>Hello world</title></head><body>".getBytes());
		out.write(new String("<h1>hello:" + username + "</h1></body><head>").getBytes());
	}
	
}
