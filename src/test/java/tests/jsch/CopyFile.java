package tests.jsch;

import com.jcraft.jsch.*;

import javax.naming.ConfigurationException;
import java.io.*;

public class CopyFile {
	private JSch jsch;
	private Session session;
	private UserInfo ui;

	public CopyFile(String username, String host) throws JSchException, IOException, ConfigurationException {
		this(username, host, 22);
	}

	private CopyFile(String username, String host, int port) throws JSchException, IOException, ConfigurationException {
		jsch=new JSch();
		session=jsch.getSession(username, host, port);
		ui = new MyUserInfo();
		session.setUserInfo(ui);
		session.connect();
	}

	public void localToRemote(String local, String remote) throws IOException, JSchException {
		FileInputStream fis;
		// exec 'scp -t rfile' remotely
		remote=remote.replace("'", "'\"'\"'");
		remote="'"+remote+"'";
		String command="scp -t "+remote;
		Channel channel=session.openChannel("exec");
		((ChannelExec)channel).setCommand(command);

		// get I/O streams for remote scp
		OutputStream out=channel.getOutputStream();
		InputStream in=channel.getInputStream();

		channel.connect();

		if(checkAck(in)!=0){
			System.exit(0);
		}

		File _lfile = new File(local);

		// send "C0644 filesize filename", where filename should not include '/'
		long filesize=_lfile.length();
		command="C0644 "+filesize+" ";
		if(local.lastIndexOf('/')>0){
			command+=local.substring(local.lastIndexOf('/')+1);
		}
		else{
			command+=local;
		}
		command+="\n";
		out.write(command.getBytes()); out.flush();
		if(checkAck(in)!=0){
			System.exit(0);
		}

		// send a content of lfile
		fis=new FileInputStream(local);
		byte[] buf=new byte[1024];
		while(true){
			int len=fis.read(buf, 0, buf.length);
			if(len<=0) break;
			out.write(buf, 0, len); //out.flush();
		}
		fis.close();
		// send '\0'
		buf[0]=0; out.write(buf, 0, 1); out.flush();
		if(checkAck(in)!=0){
			System.exit(0);
		}
		out.close();

		channel.disconnect();
	}

	public void remoteToLocal(String remote, String local) throws IOException, JSchException {
		FileOutputStream fos;
		// exec 'scp -f rfile' remotely
		remote=remote.replace("'", "'\"'\"'");
		remote="'"+remote+"'";
		String command="scp -f "+remote;
		Channel channel=session.openChannel("exec");
		((ChannelExec)channel).setCommand(command);

		// get I/O streams for remote scp
		OutputStream out=channel.getOutputStream();
		InputStream in=channel.getInputStream();

		channel.connect();

		byte[] buf=new byte[1024];

		// send '\0'
		buf[0]=0; out.write(buf, 0, 1); out.flush();

		while(true){
			int c=checkAck(in);
			if(c!='C'){
				break;
			}

			// read '0644 '
			in.read(buf, 0, 5);

			long filesize=0L;
			while(true){
				if(in.read(buf, 0, 1)<0){
					// error
					break;
				}
				if(buf[0]==' ')break;
				filesize=filesize*10L+(long)(buf[0]-'0');
			}

			for(int i=0;;i++){
				in.read(buf, i, 1);
				if(buf[i]==(byte)0x0a){
					break;
				}
			}

			// send '\0'
			buf[0]=0; out.write(buf, 0, 1); out.flush();

			// read a content of lfile
			fos=new FileOutputStream(local );
			int foo;
			while(true){
				if(buf.length<filesize) foo=buf.length;
				else foo=(int)filesize;
				foo=in.read(buf, 0, foo);
				if(foo<0){
					// error
					break;
				}
				fos.write(buf, 0, foo);
				filesize-=foo;
				if(filesize==0L) break;
			}
			fos.close();

			if(checkAck(in)!=0){
				System.exit(0);
			}

			// send '\0'
			buf[0]=0; out.write(buf, 0, 1); out.flush();
		}
	}

	private static int checkAck(InputStream in) throws IOException{
		int b=in.read();
		// b may be 0 for success,
		//          1 for error,
		//          2 for fatal error,
		//          -1
		if(b==0) return b;
		if(b==-1) return b;

		if(b==1 || b==2){
			StringBuilder sb=new StringBuilder();
			int c;
			do {
				c=in.read();
				sb.append((char)c);
			}
			while(c!='\n');
			if(b==1){ // error
				System.out.print(sb.toString());
			}
			if(b==2){ // fatal error
				System.out.print(sb.toString());
			}
		}
		return b;
	}

	public void disconnect() {
		this.session.disconnect();
	}
}
