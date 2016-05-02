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
import javax.swing.JTextField;

import edu.lamar.client.message.ClientMessage;
import edu.lamar.client.message.Operator;

public class InvertedIndexGui {

	private JFrame frmInvertedIndex;
	private JTextField textField;
	private Client client;
	private JButton btnSearch;

	/**
	 * Launch the application.
	 */
	public static void main(final String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					final InvertedIndexGui window = new InvertedIndexGui();
					window.client = new Client("localhost", 5555, window);
					window.btnSearch.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(final ActionEvent actionEvent) {
							final String myKeywords = window.textField.getText();
							final List<String> myKeywordList = new ArrayList<>();
							myKeywordList.add(myKeywords);
							try {
								window.client.sendToServer(new ClientMessage(myKeywordList, Operator.NONE));
							} catch (final IOException e) {
								e.printStackTrace();
							}
						}
					});
					window.frmInvertedIndex.setVisible(true);
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public InvertedIndexGui() {

		initialize();
	}

	public JFrame getFrmInvertedIndex() {
		return frmInvertedIndex;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmInvertedIndex = new JFrame();
		frmInvertedIndex.setTitle("Inverted Index");
		frmInvertedIndex.setBounds(100, 100, 450, 300);
		frmInvertedIndex.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
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
		// client =

		final GridBagConstraints gbc_btnSearch = new GridBagConstraints();
		gbc_btnSearch.insets = new Insets(0, 0, 0, 5);
		gbc_btnSearch.gridx = 2;
		gbc_btnSearch.gridy = 3;
		frmInvertedIndex.getContentPane().add(btnSearch, gbc_btnSearch);
	}

}
