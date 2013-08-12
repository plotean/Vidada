package vidada.views.settings;

import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

import vidada.model.settings.GlobalSettings;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

@SuppressWarnings("serial")
public class AboutPanel extends JPanel {

	GlobalSettings settings = GlobalSettings.getInstance();


	public AboutPanel() {

		String legalNote = 
				"This program is free software: you can redistribute it and/or modify" +
						" it under the terms of the GNU General Public License as published by" +
						" the Free Software Foundation, either version 3 of the License, " +
						"or (at your option) any later version.";

		String warrantyNote = 
				"This program is distributed in the hope that it will be useful," +
						" but WITHOUT ANY WARRANTY; without even the implied warranty" +
						" of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.";



		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,},
				new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));

		JLabel lblInfo = new JLabel("Info");
		lblInfo.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		add(lblInfo, "2, 2");

		JLabel lblVidadaIsFree = new JLabel("<html>Vidada is your smart media organizer which runs on any platform.<br>This software is free and OpenSource - if you want to contribute, request a new feature or report a bug visit <a href=\"https://github.com/IsNull/Vidada\">github.com/IsNull/Vidada</a>.<br></html>");
		add(lblVidadaIsFree, "4, 4");

		JLabel lblVersion = new JLabel("Version");
		add(lblVersion, "2, 6");

		JLabel txtVersion = new JLabel(settings.getVersionInfo());
		add(txtVersion, "4, 6");

		JLabel lblCredits = new JLabel("Credits");
		lblCredits.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		add(lblCredits, "2, 10");

		JLabel lblVidadaUsesSeveral = new JLabel(
				"<html>Vidada uses several 3th party projects<br>" +
						"<ul>" +
						"<li><b>ffmpeg</b> - for extracting thumbnails</li>" +
						"<li><b>vlcj</b> - for the direct play feature</li>" +
						"<li><b>db4o</b> - object database for persistence</li>" +
						"<li><b>archimedesj</b> - Java paterns/util library</li>" +
						"</ul>"+
				"</html>");
		add(lblVidadaUsesSeveral, "4, 12");

		JLabel lblLegal = new JLabel("Legal");
		lblLegal.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		add(lblLegal, "2, 16");

		JLabel lblTest = new JLabel("<html>Copyrigth © 2012-2013 P. Büttiker<br><br>" + legalNote + "<br><br>"+warrantyNote+"</html>");
		add(lblTest, "4, 18");
	}


}
