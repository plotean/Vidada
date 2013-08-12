package vidada;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Ignore;

public class CommandLineTest {

	/**
	 * @param args
	 * @throws  
	 */
	@Ignore
	public static void main(String[] args)  {
		// TODO Auto-generated method stub


		String commandLine = "E:\\MOVIEproblematic\\ffmpeg.exe -ss 25 -an -i E:\\MOVIEproblematic\\Sherlock_Holmes.mkv -vframes 1 -f image2 -s 200x160 E:\\MOVIEproblematic\\thumb5100681986568408194.png";


		Runtime runtime = Runtime.getRuntime();
		System.out.println("exec: " + commandLine);
		Process process;
		try {
			process = runtime.exec(commandLine);

			System.out.println("process started");

			//process.getInputStream();
			//process.getErrorStream();

			//BufferedReader outputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			///	System.out.println("Output:  " + outputReader.readLine());

			BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			String errString = null;
			while((errString = errorReader.readLine()) != null)
				System.out.println(errString);



			try {
				System.out.println("waition for completion");
				int err = process.waitFor();
				System.out.println("finished err: " + err);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}


		} catch (IOException e) {
			e.printStackTrace();
		}




	}

}
