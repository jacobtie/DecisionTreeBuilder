import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.FlowLayout;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.FormSpecs;
import javax.swing.JButton;
import javax.swing.JComponent;

import net.miginfocom.swing.MigLayout;
import javax.swing.GroupLayout;
import javax.swing.InputVerifier;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.SwingUtilities;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JTextField;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.SwingConstants;
import java.awt.Color;

public class DecisionTreeRunner {

	private JFrame frame;
	private JTextField txtPercentage;
	private Dataset dataset;
	private JTextField txtThreshhold;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DecisionTreeRunner window = new DecisionTreeRunner();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public DecisionTreeRunner() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 861, 610);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JLabel lblTitle = new JLabel("ID3 Decision Tree Runner");
		lblTitle.setFont(new Font("Tahoma", Font.PLAIN, 32));

		JButton btnChooseCsvFile = new JButton("Choose Training CSV");

		btnChooseCsvFile.setFont(new Font("Tahoma", Font.PLAIN, 16));

		JLabel lblSelectedFile = new JLabel("No File Selected");
		lblSelectedFile.setFont(new Font("Tahoma", Font.PLAIN, 16));

		JButton btnBuildTree = new JButton("Build Tree");
		btnBuildTree.setEnabled(false);
		btnBuildTree.setFont(new Font("Tahoma", Font.PLAIN, 16));

		txtPercentage = new JTextField();

		txtPercentage.setText("85");
		txtPercentage.setFont(new Font("Tahoma", Font.PLAIN, 16));
		txtPercentage.setColumns(10);

		JLabel lblTraining = new JLabel("% Training");
		lblTraining.setFont(new Font("Tahoma", Font.PLAIN, 16));

		JLabel lblTreeOutput = new JLabel("No Tree Created");
		lblTreeOutput.setBackground(Color.WHITE);
		lblTreeOutput.setVerticalAlignment(SwingConstants.TOP);
		lblTreeOutput.setFont(new Font("Tahoma", Font.PLAIN, 16));
		
		txtThreshhold = new JTextField();
		txtThreshhold.setText("5");
		txtThreshhold.setFont(new Font("Tahoma", Font.PLAIN, 16));
		txtThreshhold.setColumns(10);
		
		JLabel lblDepthThreshhold = new JLabel("Depth Threshhold");
		lblDepthThreshhold.setFont(new Font("Tahoma", Font.PLAIN, 16));
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(32)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblTreeOutput, GroupLayout.PREFERRED_SIZE, 322, GroupLayout.PREFERRED_SIZE)
							.addContainerGap())
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(btnChooseCsvFile, GroupLayout.PREFERRED_SIZE, 189, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED, 79, Short.MAX_VALUE)
									.addComponent(txtPercentage, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(lblTraining, GroupLayout.PREFERRED_SIZE, 101, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(txtThreshhold, GroupLayout.PREFERRED_SIZE, 78, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(lblDepthThreshhold, GroupLayout.PREFERRED_SIZE, 134, GroupLayout.PREFERRED_SIZE)
									.addGap(32)
									.addComponent(btnBuildTree, GroupLayout.PREFERRED_SIZE, 149, GroupLayout.PREFERRED_SIZE))
								.addComponent(lblTitle)
								.addComponent(lblSelectedFile, GroupLayout.PREFERRED_SIZE, 305, GroupLayout.PREFERRED_SIZE))
							.addContainerGap())))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(15)
					.addComponent(lblTitle)
					.addGap(28)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnChooseCsvFile, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)
						.addComponent(txtPercentage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblTraining)
						.addComponent(btnBuildTree, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
						.addComponent(txtThreshhold, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblDepthThreshhold))
					.addGap(18)
					.addComponent(lblSelectedFile)
					.addGap(18)
					.addComponent(lblTreeOutput, GroupLayout.PREFERRED_SIZE, 359, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(32, Short.MAX_VALUE))
		);
		frame.getContentPane().setLayout(groupLayout);

		/**********************************************************************/
		/* END AUTOGENERATED FORM SETUP, START HANDLERS */
		/**********************************************************************/

		btnChooseCsvFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				var result = CSVFileChooser.chooseCSVFileOnFrame(frame);

				if (result.isChosen()) {
					dataset = Dataset.fromCSV(result.getChosenFile());
					var message = dataset.getName() + " chosen";
					lblSelectedFile.setText(message);

					if (isValidPercent(txtPercentage.getText()) && isValidDepth(txtThreshhold.getText())) {
						btnBuildTree.setEnabled(true);
					}
				}

			}
		});

		btnBuildTree.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				var trainTestPartition = dataset.partition(Double.parseDouble(txtPercentage.getText()) / 100.0);
				var tree = new DecisionTree(trainTestPartition.getTrainSet(), Integer.parseInt(txtThreshhold.getText()));

				var treeOutput = tree.toString();

				lblTreeOutput.setText("<html><pre>" + treeOutput + "</pre></html>");
			}
		});

		txtPercentage.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				onChange();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				onChange();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				onChange();
			}

			private void onChange() {
				var enteredValue = txtPercentage.getText();
				if (dataset != null && isValidDepth(txtThreshhold.getText()) && isValidPercent(enteredValue)) {
					btnBuildTree.setEnabled(true);
				} else {
					btnBuildTree.setEnabled(false);
				}
			}
		});
		
		txtThreshhold.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				onChange();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				onChange();				
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				onChange();
			}
			
			private void onChange() {
				var enteredValue = txtThreshhold.getText();
				if (dataset != null && isValidPercent(txtPercentage.getText()) && isValidDepth(enteredValue)) {
					btnBuildTree.setEnabled(true);
				} else {
					btnBuildTree.setEnabled(false);
				}
			}
		});
	}

	private static boolean isValidPercent(String value) {
		double percentage = 0;
		try {
			percentage = Double.parseDouble(value);
		} catch (NumberFormatException ex) {
			return false;
		}

		if (percentage <= 0 || percentage > 100) {
			return false;
		}

		return true;
	}
	
	private static boolean isValidDepth(String value) {
		int depth = 0;
		
		try {
			depth = Integer.parseInt(value);
		} catch (NumberFormatException ex) {
			return false;
		}
		
		if (depth <= 1) {
			return false;
		}
		
		return true;
	}
}
