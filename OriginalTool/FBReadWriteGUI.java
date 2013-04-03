import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.GroupLayout.*;
import java.awt.event.*;
import java.util.Date;
import java.io.File;

public class FBReadWriteGUI extends JFrame implements ActionListener { 
	private JTextField AccessIDJTextBox;
	private JLabel AccessIDJLabel;
		
	private JTextField FBIDJTextBox;
	private JLabel FBIDJLabel;
	
	private JTextField writeDirectoryTextBox;
	private JLabel writeDirectoryLabel;
	private JButton writeDirectoryButton;
	private String writeDirectoryButtonString = "Choose directory";
	private JLabel writeDirectoryButtonLabel;
	private File saveDirectory = new File(System.getProperty("user.dir"));
	private String saveDirectoryName = saveDirectory.getPath();
	
			  
	private JFileChooser fileChooser = new JFileChooser(".");
	private int fileChooserReturnValue;

	private JScrollPane jScrollPane1;
	private JLabel status;
	private JTextArea textArea;
	
	private JLabel startDateJLabel;
	private JRadioButton beginningButton;
	private String beginningButtonString = "Beginning";
	private JRadioButton selectStartDateButton;
	private ButtonGroup startDateButtonGroup;

	private JLabel stopDateJLabel;
	private JRadioButton nowButton;
	private String nowButtonString = "Now";
	private JRadioButton selectStopDateButton;
	private ButtonGroup stopDateButtonGroup;

	private String selectDateButtonString = "Select:";
	
	private JButton startButton;
	private JButton stopButton;
	private boolean stopButtonPressed = false;
	
	private String [] months = {"Month","Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
	private JLabel startMonthJLabel;
	private JComboBox startMonthComboBox;
	private JLabel stopMonthJLabel;
	private JComboBox stopMonthComboBox;
	private String startMonth = "";
	private String stopMonth = "";	

	private String [] years = {"Year","2012","2011","2010","2009","2008","2007"};
	private JLabel startYearJLabel;
	private JComboBox startYearComboBox;
	private JLabel stopYearJLabel;
	private JComboBox stopYearComboBox;
	private String startYear = "";
	private String stopYear = "";
	private String fbSite;
	
	private String untilDate="";
	private String sinceDate="";
					
	private String accessToken = ""; 
	//private String accessToken="234997979894744|Ye8Jrpi9OVx8mW4EyZJo3dgtxVA";
	
	private int nPostsRead=0;
	
	private FBReadWrite fbrw;
	private Date date = new Date ();
	
	private boolean endOfData = false;
			  
	public FBReadWriteGUI() {
		initComponents();  
	}
    
    /** This method is called from within the constructor to
     * initialize the form.
     */
    private void initComponents() {
		
        textArea = new JTextArea();
        status = new JLabel();
		  
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Facebook Post Reader/Writer 2.0 (15-Oct-2012) (c)Scott Robertson");
		  
        AccessIDJLabel = new JLabel();
		  AccessIDJLabel.setText("    Access ID:");
		  AccessIDJTextBox = new JTextField();
		  		  		  
		  FBIDJLabel = new JLabel();
		  FBIDJLabel.setText("Facebook ID:");
        FBIDJTextBox = new JTextField();
		  
		  writeDirectoryLabel = new JLabel();
		  writeDirectoryLabel.setText("Save in:");
		  writeDirectoryTextBox = new JTextField();
		  writeDirectoryTextBox.setText(saveDirectoryName);

		  writeDirectoryButtonLabel = new JLabel();
		  writeDirectoryButtonLabel.setText("");
		  
		  writeDirectoryButton = new JButton(writeDirectoryButtonString);
		  writeDirectoryButton.setActionCommand("openDirectoryChooser");
		  writeDirectoryButton.addActionListener(this);
		  
		  fileChooser.setDialogTitle("Select a directory to save your results in");
		  fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		  
		  beginningButton = new JRadioButton(beginningButtonString);
		  beginningButton.setSelected(true);
		  beginningButton.setActionCommand(beginningButtonString);
		  beginningButton.addActionListener(this);
		  selectStartDateButton = new JRadioButton(selectDateButtonString);
		  selectStartDateButton.setActionCommand(selectDateButtonString);
		  selectStartDateButton.addActionListener(this);
		  startDateButtonGroup = new ButtonGroup();
		  startDateButtonGroup.add(beginningButton);
		  startDateButtonGroup.add(selectStartDateButton);

		  nowButton = new JRadioButton(nowButtonString);
		  nowButton.setSelected(true);
		  nowButton.setActionCommand(nowButtonString);
		  nowButton.addActionListener(this);
		  selectStopDateButton = new JRadioButton(selectDateButtonString);
		  selectStopDateButton.setActionCommand(selectDateButtonString);
		  selectStopDateButton.addActionListener(this);
		  stopDateButtonGroup = new ButtonGroup();
		  stopDateButtonGroup.add(nowButton);
		  stopDateButtonGroup.add(selectStopDateButton);	
		  	  		  
		  startButton = new JButton("Start");
		  startButton.setActionCommand("start");
		  startButton.addActionListener(this);
		  
		  stopButton = new JButton("Stop");
		  stopButton.setActionCommand("stop");
		  stopButton.setEnabled(false);
		  stopButton.addActionListener(this);
		  
		  startDateJLabel = new JLabel();
		  startDateJLabel.setText("Start Date:");
		  startMonthComboBox = new JComboBox(months);
		  startMonthComboBox.setEnabled(false);	
		  startMonthComboBox.addActionListener(this);
		  startYearComboBox = new JComboBox(years);
		  startYearComboBox.setEnabled(false);	
		  startYearComboBox.addActionListener(this);
		  
		  stopDateJLabel = new JLabel();
		  stopDateJLabel.setText("Stop Date:");
		  stopMonthComboBox = new JComboBox(months);
		  stopMonthComboBox.setEnabled(false);	
		  stopMonthComboBox.addActionListener(this);
		  stopYearComboBox = new JComboBox(years);
		  stopYearComboBox.setEnabled(false);
		  stopYearComboBox.addActionListener(this);	  		  
	  		  		  
        textArea.setColumns(50);
        textArea.setLineWrap(true);
        textArea.setRows(5);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        jScrollPane1 = new JScrollPane(textArea);
		  

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
		  layout.setAutoCreateContainerGaps(true);
				
		layout.linkSize(nowButton, beginningButton);
		layout.linkSize(selectStartDateButton, selectStopDateButton);
		layout.linkSize(startMonthComboBox, stopMonthComboBox);
		layout.linkSize(startYearComboBox, stopYearComboBox);		  
        
		ParallelGroup hGroup = layout.createParallelGroup();
		
		SequentialGroup hStartDatesGroup = layout.createSequentialGroup();
		hStartDatesGroup.addGroup(layout.createSequentialGroup().
			addComponent(beginningButton).
			addComponent(selectStartDateButton).
			addComponent(startMonthComboBox).
			addComponent(startYearComboBox));

		SequentialGroup hStopDatesGroup = layout.createSequentialGroup();
		hStopDatesGroup.addGroup(layout.createSequentialGroup().
			addComponent(nowButton).
			addComponent(selectStopDateButton).
			addComponent(stopMonthComboBox).
			addComponent(stopYearComboBox));
					
		SequentialGroup hSetUpGroup = layout.createSequentialGroup();
		hSetUpGroup.addGroup(layout.createParallelGroup().
			addComponent(AccessIDJLabel, GroupLayout.Alignment.TRAILING).
			addComponent(FBIDJLabel, GroupLayout.Alignment.TRAILING).
			addComponent(writeDirectoryLabel, GroupLayout.Alignment.TRAILING).
			addComponent(writeDirectoryButton, GroupLayout.Alignment.TRAILING).
			
			addComponent(startDateJLabel, GroupLayout.Alignment.TRAILING).
			addComponent(stopDateJLabel, GroupLayout.Alignment.TRAILING));
		
   	hSetUpGroup.addGroup(layout.createParallelGroup().
      	addComponent(AccessIDJTextBox, GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE).
			addComponent(FBIDJTextBox).
			addComponent(writeDirectoryTextBox, GroupLayout.Alignment.TRAILING).
			addComponent(writeDirectoryButtonLabel, GroupLayout.Alignment.TRAILING).

			addGroup(hStartDatesGroup).
			addGroup(hStopDatesGroup));
		
	SequentialGroup hStatusGroup = layout.createSequentialGroup();
	hStatusGroup.addGroup(layout.createParallelGroup().
		addComponent(startButton).
		addComponent(stopButton));	
	hStatusGroup.addGroup(layout.createParallelGroup().
		addComponent(jScrollPane1, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE).
		addComponent(status, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE));
		
	hGroup.
		addGroup(hSetUpGroup).
		addGroup(hStatusGroup);
	
	layout.setHorizontalGroup(hGroup);
	
 	SequentialGroup vGroup = layout.createSequentialGroup();
			
	SequentialGroup vStartDatesGroup = layout.createSequentialGroup();
	vStartDatesGroup.addGroup(layout.createParallelGroup().
		addComponent(beginningButton).
		addComponent(selectStartDateButton).
		addComponent(startMonthComboBox).
		addComponent(startYearComboBox));
	
	SequentialGroup vStopDatesGroup = layout.createSequentialGroup();
	vStopDatesGroup.addGroup(layout.createParallelGroup().	
		addComponent(nowButton).
		addComponent(selectStopDateButton).
		addComponent(stopMonthComboBox).
		addComponent(stopYearComboBox));

	ParallelGroup vStatusGroup = layout.createParallelGroup();
	vStatusGroup.addGroup(layout.createSequentialGroup().
		addComponent(startButton).
		addComponent(stopButton));	
	vStatusGroup.addGroup(layout.createParallelGroup().
		addComponent(jScrollPane1, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE).
		addComponent(status, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE));
		
   vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).
            addComponent(AccessIDJLabel).
				addComponent(AccessIDJTextBox));
   vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).
            addComponent(FBIDJLabel).
				addComponent(FBIDJTextBox));

   vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).
            addComponent(writeDirectoryLabel).
				addComponent(writeDirectoryTextBox));
   vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).
            addComponent(writeDirectoryButton).
				addComponent(writeDirectoryButtonLabel));
								 
   vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).
            addComponent(startDateJLabel).
				addGroup(vStartDatesGroup));
   vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).
            addComponent(stopDateJLabel).
				addGroup(vStopDatesGroup));
						
	vGroup.addGroup(vStatusGroup);		
   layout.setVerticalGroup(vGroup);	 
	pack();
   }

public void actionPerformed(ActionEvent e) {
	if (e.getSource() == selectStartDateButton) {
		startMonthComboBox.setEnabled(true);
		startYearComboBox.setEnabled(true);		
	}
	else if (e.getSource() == selectStopDateButton) {
		stopMonthComboBox.setEnabled(true);
		stopYearComboBox.setEnabled(true);		
	}
	else if (e.getSource() == nowButton) {
		stopMonthComboBox.setSelectedIndex(0);
		stopYearComboBox.setSelectedIndex(0);		
		stopMonthComboBox.setEnabled(false);
		stopYearComboBox.setEnabled(false);
		stopMonth = "";
		stopYear = "";
		untilDate="";				
	}
	else if (e.getSource() == beginningButton) {
		startMonthComboBox.setSelectedIndex(0);
		startYearComboBox.setSelectedIndex(0);
		startMonthComboBox.setEnabled(false);
		startYearComboBox.setEnabled(false);	
		startMonth = "";
		startYear = "";
		sinceDate="";	
	}	
	else if (e.getSource() == startMonthComboBox) {
		startMonth = (String) startMonthComboBox.getSelectedItem();
	}
	else if (e.getSource() == startYearComboBox) {
		startYear = (String) startYearComboBox.getSelectedItem();
	}
	else if (e.getSource() == stopMonthComboBox) {
		stopMonth = (String) stopMonthComboBox.getSelectedItem();
	}
	else if (e.getSource() == stopYearComboBox) {
		stopYear = (String) stopYearComboBox.getSelectedItem();
	}
	
	else if (e.getSource() == writeDirectoryButton) {
		fileChooserReturnValue = fileChooser.showOpenDialog(this);
		if (fileChooserReturnValue == JFileChooser.APPROVE_OPTION) {
			saveDirectory = fileChooser.getSelectedFile();
			saveDirectoryName = saveDirectory.getPath();
			writeDirectoryTextBox.setText(saveDirectoryName);
		}
	}

	else if (e.getSource() == startButton) {
	//if ("start".equals(e.getActionCommand())) {
		accessToken = AccessIDJTextBox.getText();
		fbSite = FBIDJTextBox.getText();
		if (accessToken.equals("hichilab")) // remove in the real app
		{
			accessToken = "234997979894744|Ye8Jrpi9OVx8mW4EyZJo3dgtxVA";
		}
		if (	(! stopMonth.equals("")) &&
				(! stopYear.equals("")) ) {
			untilDate = makeUntilStr(stopMonth,stopYear);
		}
		if (	(! startMonth.equals("")) &&
				(! startYear.equals("")) ) {
			sinceDate = makeSinceStr(startMonth,startYear);
		}
		
		fbrw = new FBReadWrite(fbSite,saveDirectory,accessToken,untilDate,sinceDate);
				
		startButton.setEnabled(false);
		stopButton.setEnabled(true);
		stopButtonPressed=false;
		
		updateProgress("Opening " + fbSite + ". Wait...");	

		Thread readPostsThread = new Thread() {
			public void run() {
				try {	
					runReadPosts();
				}
				catch (Exception ex){
					//System.err.println("Error: " + ex.getMessage());
					updateProgress(fbSite + " not found\n");
					stopButton.setEnabled(false);
					startButton.setEnabled(true);
				}
				finally {}
			}
		};
		readPostsThread.start();			
	}
		  
	else if (e.getSource() == stopButton) {
	//else if ("stop".equals(e.getActionCommand())) {
		stopButton.setEnabled(false);
		stopButtonPressed=true;
		startButton.setEnabled(true);
		accessToken="";
		fbSite="";
	}		  
}	
	private void runReadPosts() throws Exception {
		while (!endOfData && !stopButtonPressed) {
			try {		
				endOfData = fbrw.readWrite();
			}
			catch (NullPointerException ex) {
				endOfData = true;
			}
			finally {
				updateProgress(fbrw.nPosts + " posts and " + fbrw.nCommentsCumulative + " comments processed through " + fbrw.lastPostDate + ". Wait...");
			}
		}
	updateProgress("Done.");
	stopButton.setEnabled(false);
	startButton.setEnabled(true);
	}
	
	private void updateProgress(final String update) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				String t;
				// Here, we can safely update the GUI
				// because we'll be called from the
				// event dispatch thread
				t = textArea.getText();
				textArea.setText(t + "\n" + update);
			}
		});
	}	

	public String makeUntilStr (String month, String year) {
		String m = "";
		int y = Integer.parseInt(year);
		int i = 0;
		if ((! month.equals("Month")) &&
			(! year.equals("Year")) ) {
			for (i=0; i<months.length; i++) {
				if (month.equals(months[i])) {break;}
			}
			if (i==12) {
				m = "Jan";	
				y = y + 1;
				year = Integer.toString(y);		
				} 
			else if ( (i<12) && (i>0) ) {
				m = months[i+1];
			}
		}
		return (m + year);
	}
	
	public String makeSinceStr (String month, String year) {
		if ((! month.equals("Month")) &&
			(! year.equals("Year")) ) {
			return (month + year);
		}
		else {
			return ("");
		}
	}
	
	public static void main(String args[]) {
		//Schedule a job for the event dispatch thread:
		//creating and showing this application's GUI.
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				//Turn off metal's use of bold fonts
				UIManager.put("swing.boldMetal", Boolean.FALSE);
				new FBReadWriteGUI().setVisible(true);
			}
		});
    }
}