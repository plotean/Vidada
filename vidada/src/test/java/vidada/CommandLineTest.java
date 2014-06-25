package vidada;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Ignore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommandLineTest {

    private static final Logger logger = LogManager.getLogger(CommandLineTest.class.getName());


    /**
	 * @param args
	 * @throws  
	 */
	@Ignore
	public static void main(String[] args)  {
		// TODO Auto-generated method stub


		String commandLine = "E:\\MOVIEproblematic\\ffmpeg.exe -ss 25 -an -i E:\\MOVIEproblematic\\Sherlock_Holmes.mkv -vframes 1 -f image2 -s 200x160 E:\\MOVIEproblematic\\thumb5100681986568408194.png";


		Runtime runtime = Runtime.getRuntime();
        logger.info("exec: " + commandLine);
		Process process;
		try {
			process = runtime.exec(commandLine);

            logger.info("process started");

			//process.getInputStream();
			//process.getErrorStream();

			//BufferedReader outputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			///	System.out.println("Output:  " + outputReader.readLine());

			BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			String errString = null;
			while((errString = errorReader.readLine()) != null)
                logger.info(errString);



			try {
                logger.info("waition for completion");
				int err = process.waitFor();
                logger.info("finished err: " + err);
			} catch (InterruptedException e) {
                logger.error(e);
			}


		} catch (IOException e) {
            logger.error(e);
		}




	}

}
