import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

public class CSVFileChooser {
	
	private JFileChooser chooser;
	private JFrame mainFrame;
	
	public static CSVFileChooserResult chooseCSVFileOnFrame(JFrame mainFrame) {
		return new CSVFileChooser(mainFrame).choose();
	}
	
	private CSVFileChooser(JFrame mainFrame) {
		initializeCSVChooser();
		this.mainFrame = mainFrame;
	}
	
	private void initializeCSVChooser() {
		chooser = new JFileChooser();
		var filter = new FileNameExtensionFilter("CSV Files", "csv");

		chooser.setFileFilter(filter);
		chooser.setMultiSelectionEnabled(false);
	}
	
	private CSVFileChooserResult choose() {
		var resultCode = chooser.showDialog(mainFrame, "Load Data");
		
		File chosenFile = null;
		boolean isChosen = false;
		if (resultCode == JFileChooser.APPROVE_OPTION && isCSVFile(chooser.getSelectedFile())) {
			chosenFile = chooser.getSelectedFile();
			isChosen = true;
		}

		return new CSVFileChooserResult(chosenFile, isChosen);
	}

	private boolean isCSVFile(File file) {
		var fileName = file.getName();
		var fileExtension = fileName.substring(fileName.lastIndexOf("."));

		return fileExtension.equals(".csv");
	}
	
	public class CSVFileChooserResult {
		private File chosenFile;
		private boolean isChosen;

		public CSVFileChooserResult(File chosenFile, boolean isChosen) {
			this.chosenFile = chosenFile;
			this.isChosen = isChosen;
		}

		public File getChosenFile() {
			return chosenFile;
		}

		public boolean isChosen() {
			return isChosen;
		}
	}

}
