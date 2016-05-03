package edu.lamar.client;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import edu.lamar.client.message.ClientMessage;
import edu.lamar.client.message.Operator;

public class InvertedIndexGui {

	/**
	 * Launch the application.
	 */
	public static void main(final String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					final InvertedIndexGui window = new InvertedIndexGui();
					window.frmInvertedIndex.setVisible(true);
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private JFrame frmInvertedIndex;
	private JTextField textField;
	private Client client;

	private JButton btnSearch;
	private JTextArea resultArea;

	/**
	 * Create the application.
	 */
	public InvertedIndexGui() {

		initialize();
	}

	public JFrame getFrmInvertedIndex() {
		return frmInvertedIndex;
	}

	public JTextArea getResultPane() {
		return resultArea;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmInvertedIndex = new JFrame();
		frmInvertedIndex.setTitle("Inverted Index");
		frmInvertedIndex.setBounds(100, 100, 695, 488);
		frmInvertedIndex.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		frmInvertedIndex.getContentPane().setLayout(gridBagLayout);

		final JLabel lblEnterKeywordTo = new JLabel("Enter Keyword to Search");
		final GridBagConstraints gbc_lblEnterKeywordTo = new GridBagConstraints();
		gbc_lblEnterKeywordTo.insets = new Insets(0, 0, 5, 5);
		gbc_lblEnterKeywordTo.gridx = 1;
		gbc_lblEnterKeywordTo.gridy = 1;
		frmInvertedIndex.getContentPane().add(lblEnterKeywordTo, gbc_lblEnterKeywordTo);

		textField = new JTextField();
		final GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 5, 0);
		gbc_textField.anchor = GridBagConstraints.WEST;
		gbc_textField.gridx = 3;
		gbc_textField.gridy = 1;
		frmInvertedIndex.getContentPane().add(textField, gbc_textField);
		textField.setColumns(10);
		btnSearch = new JButton("Search");
		client = new Client("localhost", 5555, this);
		try {
			client.openConnection();
		} catch (final IOException e1) {
			e1.printStackTrace();
		}
		btnSearch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent actionEvent) {
				final String myKeywords = textField.getText();
				final List<String> myKeywordList = new ArrayList<>();
				final String[] myKeywordsArray = myKeywords.split(" ");
				if (myKeywordsArray.length > 1) {
					if (myKeywordsArray[0].equalsIgnoreCase("not")) {
						handleNotKeyword(myKeywordList, myKeywordsArray);
						return;
					} else if (myKeywordsArray[1].equalsIgnoreCase("and")) {
						handleAndOperator(myKeywordList, myKeywordsArray);
						return;
					} else if (myKeywordsArray[1].equalsIgnoreCase("or")) {
						handleOrOperator(myKeywordList, myKeywordsArray);
						return;
					} else {
						JOptionPane.showMessageDialog(frmInvertedIndex, "Please enter correct keyword");
						return;
					}
				}
				myKeywordList.add(myKeywords);
				try {
					client.sendToServer(new ClientMessage(myKeywordList, Operator.NONE));
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}

			private void handleAndOperator(final List<String> myKeywordList, final String[] myKeywordsArray) {
				if ((myKeywordsArray.length > 3) || (myKeywordsArray.length < 3)) {
					JOptionPane.showMessageDialog(frmInvertedIndex,
							"Only two keyword are supported with and operator as of now");
					return;
				}
				myKeywordList.add(myKeywordsArray[0]);
				myKeywordList.add(myKeywordsArray[2]);
				try {
					client.sendToServer(new ClientMessage(myKeywordList, Operator.AND));
				} catch (final IOException e) {
					e.printStackTrace();
				}
				return;
			}

			private void handleNotKeyword(final List<String> myKeywordList, final String[] myKeywordsArray) {
				if (myKeywordsArray.length > 2) {
					JOptionPane.showMessageDialog(frmInvertedIndex, "Please enter only one keyword after not");
					return;
				}
				myKeywordList.add(myKeywordsArray[1]);
				try {
					client.sendToServer(new ClientMessage(myKeywordList, Operator.NOT));
				} catch (final IOException e) {
					e.printStackTrace();
				}
				return;
			}

			private void handleOrOperator(final List<String> myKeywordList, final String[] myKeywordsArray) {
				if ((myKeywordsArray.length > 3) || (myKeywordsArray.length < 3)) {
					JOptionPane.showMessageDialog(frmInvertedIndex,
							"Only two keyword are supported with or operator as of now");
					return;
				}
				myKeywordList.add(myKeywordsArray[0]);
				myKeywordList.add(myKeywordsArray[2]);
				try {
					client.sendToServer(new ClientMessage(myKeywordList, Operator.OR));
				} catch (final IOException e) {
					e.printStackTrace();
				}
				return;
			}
		});
		final GridBagConstraints gbc_btnSearch = new GridBagConstraints();
		gbc_btnSearch.insets = new Insets(0, 0, 5, 5);
		gbc_btnSearch.gridx = 2;
		gbc_btnSearch.gridy = 3;
		frmInvertedIndex.getContentPane().add(btnSearch, gbc_btnSearch);

		resultArea = new JTextArea();
		resultArea.setWrapStyleWord(true);
		resultArea.setLineWrap(true);
		resultArea.setEditable(false);
		final GridBagConstraints gbc_resultArea = new GridBagConstraints();
		gbc_resultArea.gridwidth = 3;
		gbc_resultArea.insets = new Insets(0, 0, 0, 5);
		gbc_resultArea.fill = GridBagConstraints.BOTH;
		gbc_resultArea.gridx = 1;
		gbc_resultArea.gridy = 6;
		frmInvertedIndex.getContentPane().add(resultArea, gbc_resultArea);
	}

}
