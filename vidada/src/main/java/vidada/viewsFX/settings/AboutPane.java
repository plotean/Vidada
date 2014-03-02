package vidada.viewsFX.settings;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import vidada.Application;

public class AboutPane extends AbstractSettingsPane {

	public AboutPane(){


		String aboutVidada = "Vidada is your smart media organizer which runs on any platform.\nThis software is free and OpenSource - if you want to contribute, request a new feature or report a bug visit https://github.com/IsNull/Vidada.";

		String legalNote = 
				"The MIT License (MIT)\n\n" +

				"Copyright (c) 2014 P.Büttiker and contributors\n" +

				"Permission is hereby granted, free of charge, to any person obtaining a copy " +
				"of this software and associated documentation files (the 'Software'), to deal " +
				"in the Software without restriction, including without limitation the rights " +
				"to use, copy, modify, merge, publish, distribute, sublicense, and/or sell " +
				"copies of the Software, and to permit persons to whom the Software is " +
				"furnished to do so, subject to the following conditions:" +

				"The above copyright notice and this permission notice shall be included in " +
				"all copies or substantial portions of the Software.";

		String warrantyNote = 
				"This program is distributed in the hope that it will be useful," +
						" but WITHOUT ANY WARRANTY; without even the implied warranty" +
						" of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.";


		/*
"<html>Vidada uses several 3th party projects<br>" +
"<ul>" +
"<li><b>ffmpeg</b> - for extracting thumbnails</li>" +
"<li><b>vlcj</b> - for the direct play feature</li>" +
"<li><b>db4o</b> - object database for persistence</li>" +
"<li><b>archimedesj</b> - Java paterns/util library</li>" +
"</ul>"+
"</html>"*/

		Package pack = Application.class.getPackage();
		String version = pack.getImplementationVersion();
		Label versionLabel = new Label();

		if(version != null){
			versionLabel.setText("Version: " + version);
		}else {
			versionLabel.setText("Version information not available.");
			versionLabel.setDisable(true);
		}

		BorderPane.setMargin(versionLabel, new Insets(10));
		setTop(versionLabel);

		TextArea root = new TextArea(aboutVidada + "\n\n"+legalNote + "\n\n" + warrantyNote);
		root.setWrapText(true);
		BorderPane.setMargin(root, new Insets(10));
		setCenter(root);
	}


}
