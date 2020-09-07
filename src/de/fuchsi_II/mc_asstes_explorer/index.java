package de.fuchsi_II.mc_asstes_explorer;


import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;
import java.util.stream.*;
import com.google.gson.*;

/*	todo:
 * index file dropdown list
 * 
 * */

public class index extends JFrame implements KeyListener, MouseListener{
	//just there to remove warning
	private static final long serialVersionUID = 1L;
	
	//set Vars
	public String getindexpath(String indexfile) {
		return System.getenv("APPDATA") + "\\.minecraft\\assets\\indexes\\" + indexfile;
	}
	boolean wrongOS = false;
	Gson gson = new Gson();
	String[] allindexfiles = new File(System.getenv("APPDATA") + "\\.minecraft\\assets\\indexes\\").list();
	File indexfile = new File(getindexpath("1.16.json")); //indexfile = File(getindexpath(file_name_from_list);
	JsonObject indexjson = gson.fromJson("{}", JsonObject.class);
	JsonObject root;
	
	//create windows items
	JPanel jpPanel = new JPanel();
	
	JComboBox<?> indexDropList = new JComboBox<Object>(allindexfiles);
	DefaultListModel<Object> model = new DefaultListModel<Object>();
	JList<Object> list = new JList<Object>(model);
	JScrollPane scrollp = new JScrollPane(list,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	JLabel lblpath = new JLabel();
	JLabel lblerr = new JLabel();
	JButton btnopendef = new JButton(); //open in default programm
	JButton btnopenfs = new JButton(); //show in explorer

	//setup help functions
	public ArrayList<String> getCurrentPath() {
		ArrayList<String> returnPath = new ArrayList<String>();
		String[] path = lblpath.getText().split("/");
		for (int i = 0; i < path.length; i++) {
			if (!path[i].contains(".")) {
				returnPath.add(path[i]);
			}
		}
		return returnPath;
	}
	public String getCurrentListItem() {
		if (list.getSelectedIndex() == -1) {
			return "";
		}else {
			return model.get(list.getSelectedIndex()).toString();
		}
	}
	
	public void enter() {
		if (getCurrentListItem().chars().filter(ch -> ch == '.').count() == 0 || getCurrentListItem() == "..") {
			update(lblpath.getText() + getCurrentListItem() + "/", false);
		}else if (getCurrentListItem().chars().filter(ch -> ch == '.').count() == 1) {
			btnopendef.doClick();
		}
	}
	
	//setup KeyListener
	public void keyTyped (KeyEvent e) {
		//this is not going to be used
	}
	public void keyReleased (KeyEvent e) {
		//this is not going to be used
	}
	public void keyPressed (KeyEvent e) {
		int keycode = e.getKeyCode();
		if (keycode == KeyEvent.VK_ENTER) {
			enter();
			lblerr.setText("");
		}
	}
	
	//setup mouse event listener
	public void mousePressed(MouseEvent e) {
		//this is not going to be used
    }
    public void mouseReleased(MouseEvent e) {
    	//this is not going to be used
    }
    public void mouseEntered(MouseEvent e) {
    	//this is not going to be used
    }
    public void mouseExited(MouseEvent e) {
    	//this is not going to be used
    }
    public void mouseClicked(MouseEvent e) {
    	lblerr.setText("");
    	if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
    		enter();
    	}
    }
    
	
	public index(){
		super();
		this.setTitle("Minecraft assets explorer by Fuchsi_II");
		this.setSize(700,600);
		FensterAufbauen();
	}
	
	public void update(String newPath, boolean reverse) {
		ArrayList<String> CPath = getCurrentPath();
		String CListItem = getCurrentListItem();
		if (!CListItem.contains(".")) {
			lblpath.setText(newPath);
			model.clear();
		}
		
		Set<String> indexjsonkeys = root.keySet();
		
		
		if (CListItem == "..") {
			ArrayList<String> newerpath = CPath;
			String newestpath = "";
			newerpath.remove(newerpath.size()-1);
			for (int i = 0; i < newerpath.size(); i++) {
				newestpath += newerpath.toArray()[i] + "/";
			}
			lblpath.setText(newestpath);
			
		}else {
			ArrayList<String> allcurrentpaths = new ArrayList<>();
			for(int i = 0; i< indexjsonkeys.size();i++) {
				if (newPath.split("/").length == 0) {
					allcurrentpaths.add(indexjsonkeys.toArray()[i].toString().split("/")[0]);
				}else {
					allcurrentpaths.add("..");
					if (indexjsonkeys.toArray()[i].toString().contains("/") == false && newPath == "/") {
					}else if(indexjsonkeys.toArray()[i].toString().chars().filter(ch -> ch == '/').count() == 0) {
					}else {
						if (reverse) {
							if (!(indexjsonkeys.toArray()[i].toString().split("/").length < CPath.size()) && indexjsonkeys.toArray()[i].toString().split("/")[newPath.split("/").length - 2].equals(CPath.toArray()[CPath.size()-1])) {
								allcurrentpaths.add(indexjsonkeys.toArray()[i].toString().split("/")[newPath.split("/").length - 1]);
							}
						}else {
							if (!(indexjsonkeys.toArray()[i].toString().split("/").length < newPath.split("/").length) && indexjsonkeys.toArray()[i].toString().split("/")[newPath.split("/").length - 2].equals(CListItem)) {
								allcurrentpaths.add(indexjsonkeys.toArray()[i].toString().split("/")[newPath.split("/").length - 1]);
							}
						}
					}
				}
			}
			ArrayList<String> paths = new ArrayList<>(allcurrentpaths.stream().distinct().collect(Collectors.toList()));
			
			for(int i = 0; i< paths.size();i++) {
				model.addElement(paths.get(i));
			}
			list.setSelectedIndex(0);
			//fix for overlapping paths bug
			if (lblpath.getText().equals("/minecraft/textures/gui/")) {
				model.remove(2);
			}else if (lblpath.getText().equals("/realms/textures/gui/")) {
				model.remove(1);
			}
		}
	}
	
	public void setupstructure() {
		try {
			Scanner scan = new Scanner(indexfile);
			indexjson = gson.fromJson(scan.useDelimiter("\\Z").next(), JsonObject.class);
			root = indexjson.get("objects").getAsJsonObject();
			scan.close();
			update("/", false);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			lblerr.setText(e.getCause().toString());
		}
	}
	
	//setup textchange listener
	PropertyChangeListener cl = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getOldValue().toString().length() > evt.getNewValue().toString().length()) {
				//list.setSelectedIndex(1);
				model.clear();
				update(lblpath.getText().replace(getCurrentListItem(), ""), true);
			}
		}
	};
	
	public void FensterAufbauen(){
		jpPanel.setLayout(null);
		
		
		//setup erroer label
		lblerr.setText("");
		lblerr.setBounds(25, 20, 700, 40);
		lblerr.setFont(lblerr.getFont().deriveFont(10.0f));
		lblerr.setForeground(Color.RED);
		jpPanel.add(lblerr);
		if (!System.getProperty("os.name").contains("Windows")) {
			lblerr.setText("You are useing " + System.getProperty("os.name") + ". This tool is designed for Windows");
			wrongOS = true;
		}
		
		if (!wrongOS) {
			setupstructure();
			
			//setup list
			list.setSelectedIndex(0); 
			list.addKeyListener(this);
			list.addMouseListener(this);
			
			//setup ScrollPane
			scrollp.setBounds(25, 100, 400, 400);
			jpPanel.add(scrollp);
			
			//Setup path label
			lblpath.setText("/");
			lblpath.setBounds(25, 50, 400, 40);
			lblpath.setFont(lblpath.getFont().deriveFont(20.0f));
			lblpath.setBorder(BorderFactory.createLineBorder(Color.black, 1));
			lblpath.setBackground(Color.white);
			lblpath.setOpaque(true);
			lblpath.addPropertyChangeListener("text", cl);
			jpPanel.add(lblpath);
			
			//setup index Dropdown List
			indexDropList.setBounds(450, 80, 200, 50);
			indexDropList.setSelectedItem("1.16.json");
			jpPanel.add(indexDropList);
			indexDropList.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					JComboBox<?> cb = (JComboBox<?>) e.getSource();
			        String item = (String)cb.getSelectedItem();
			        indexfile = new File(getindexpath(item));
			        model.clear();
			        setupstructure();
				}
			});
			
			
			//setup open default button
			btnopendef.setText("Open With Default Progam");
			btnopendef.setBounds(450, 200, 200, 50);
			jpPanel.add(btnopendef);
			btnopendef.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					if (getCurrentListItem().chars().filter(ch -> ch == '.').count() == 1) {
						String pathToFile = System.getenv("appdata") + "\\.minecraft\\assets\\objects\\" + root.get(lblpath.getText().substring(1) + getCurrentListItem()).getAsJsonObject().get("hash").getAsString().substring(0, 2) + "\\" + root.get(lblpath.getText().substring(1) + getCurrentListItem()).getAsJsonObject().get("hash").getAsString();
						if (getCurrentListItem().split("\\.")[1].matches("json") || getCurrentListItem().split("\\.")[1].matches("mcmeta") || getCurrentListItem().split("\\.")[1].matches("lang") || getCurrentListItem().split("\\.")[1].matches("txt")) {//start Notepad++
						ProcessBuilder builder = new ProcessBuilder(System.getenv("PROGRAMFILES(x86)") + "\\Notepad++\\notepad++.exe", "\"" + pathToFile + "\"");
						try {
							builder.start();
						}catch (IOException e) {
							e.printStackTrace();
							lblerr.setText(e.getCause().toString());
						}
						
					}else if (getCurrentListItem().split("\\.")[1].matches("ogg")) {//start VLC
						ProcessBuilder builder = new ProcessBuilder(System.getenv("PROGRAMFILES") + "\\VideoLAN\\VLC\\vlc.exe", "\"" + pathToFile + "\"");
						try {
							builder.start();
						}catch (IOException e) {
							e.printStackTrace();
							//System.out.println(e.getCause());
							lblerr.setText(e.getCause().toString());
						}
						
					}else if (getCurrentListItem().split("\\.")[1].matches("png")) {//start photo gallery
						ProcessBuilder builder = new ProcessBuilder("C:\\Windows\\System32\\rundll32.exe", System.getenv("PROGRAMFILES") + "\\Windows Photo Viewer\\PhotoViewer.dll", "ImageView_Fullscreen", pathToFile);
						try {
							builder.start();
						}catch (IOException e) {
							e.printStackTrace();
							lblerr.setText(e.getCause().toString());
						}
					}else if (getCurrentListItem().split("\\.")[1].matches("icns")) {//show error for .icns files
						lblerr.setText("Cant open icns files. (icns files are MacOS icon files)");
					}
					}
				}
			});
			
			//setup open in explorer button
			btnopenfs.setText("Open in Windows Explorer");
			btnopenfs.setBounds(450, 300, 200, 50);
			jpPanel.add(btnopenfs);
			btnopenfs.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					if (getCurrentListItem().chars().filter(ch -> ch == '.').count() == 1) {
						ProcessBuilder builder = new ProcessBuilder("explorer", "/select,\"" + System.getenv("appdata") + "\\.minecraft\\assets\\objects\\" + root.get(lblpath.getText().substring(1) + getCurrentListItem()).getAsJsonObject().get("hash").getAsString().substring(0, 2) + "\\" + root.get(lblpath.getText().substring(1) + getCurrentListItem()).getAsJsonObject().get("hash").getAsString() + "\"");
						try {
							builder.start();
						}catch (IOException e) {
							e.printStackTrace();
							lblerr.setText(e.getCause().toString());
						}
					}else {
						ProcessBuilder builder = new ProcessBuilder("explorer", "/select,\"" + System.getenv("APPDATA") + "\\.minecraft\\assets\\indexes\\" + indexDropList.getSelectedItem() + "\"");
						lblerr.setText("Note: The highlighted file is the current indexfile");
						try {
							builder.start();
						}catch (IOException e) {
							e.printStackTrace();
							lblerr.setText(e.getCause().toString());
						}
					}
					
				}
			});
		}
		
		this.add(jpPanel);
	}
	
	public static void main(String[] args){
		index g  = new index();
		g.setVisible(true);
		
	}
}
