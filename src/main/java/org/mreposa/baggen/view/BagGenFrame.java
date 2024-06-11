package org.mreposa.baggen.view;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.awt.event.*;

public class BagGenFrame extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;

    private static final String APP_TITLE = "AD&D G1-2-3 Giant Bag Generator";
    private static final String APP_VERSION = "2.0.0";
    private static final String ABOUT_MESSAGE = APP_TITLE + "\n" + "Version " + APP_VERSION + "\n" + "Michael Reposa\n" + "08-JUN-2024\n\nADVANCED DUNGEONS & DRAGONS, ADVANCED D&D, AD&D, and 'G1-2-3 Against the Giants' are trademarks owned by TSR Hobbies, Inc.";

    private static final int PANEL_WIDTH = 1200;
    private static final int PANEL_HEIGHT = 800;
    private static final int MAX_AREA_COUNT = 12;

    private final JMenuBar menuBar;
    private final ArrayList<JEditorPane> displayAreas = new ArrayList<>();
    private int bagCount = 1;

    public BagGenFrame() {
        super();

        JPanel basePanel;
        JPanel displayPanel;
        JPanel buttonPanel;
        JButton generateButton;
        ButtonGroup buttonGroup;

        this.setTitle(APP_TITLE);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.menuBar = new JMenuBar();
        createFileMenu();
        createHelpMenu();
        this.setJMenuBar(this.menuBar);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                exitApp();
            }
        });

        basePanel = new JPanel();
        basePanel.setLayout(new BorderLayout());
        Dimension d = new Dimension(PANEL_WIDTH, PANEL_HEIGHT);
        basePanel.setSize(d);
        basePanel.setPreferredSize(d);
        add(basePanel);

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        generateButton = new JButton("Generate Giant Bags");
        Dimension bd = new Dimension(150, 20);
        generateButton.setSize(bd);
        generateButton.setPreferredSize(bd);
        generateButton.setMaximumSize(bd);
        generateButton.addActionListener(e -> requestAndDisplay());
        buttonPanel.add(generateButton);
        basePanel.add(buttonPanel, BorderLayout.NORTH);

        displayPanel = new JPanel();
        displayPanel.setLayout(new GridLayout(2,6));
        buttonGroup = new ButtonGroup();

        for (int a = 0; a < MAX_AREA_COUNT; a++) {
            JEditorPane displayArea;
            displayArea = new JEditorPane();
            displayArea.setSize(d);
            displayArea.setPreferredSize(d);

            this.displayAreas.add(displayArea);
            displayPanel.add(displayArea);

            JRadioButton btn = new JRadioButton();
            btn.setText(String.valueOf(a + 1));
            btn.addActionListener(this);
            buttonGroup.add(btn);
            buttonPanel.add(btn);
            if (a == 0) {
                buttonGroup.setSelected(btn.getModel(), true);
            }
        }

        basePanel.add(displayPanel, BorderLayout.CENTER);

        this.pack();
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String ac = e.getActionCommand();
        if (ac != null) {
            this.bagCount = Integer.parseInt(ac);
        }
    }

    private void requestAndDisplay() {
        for (int t = 0; t < MAX_AREA_COUNT; t++) {
            this.displayAreas.get(t).setText("");
        }

        try {
            StringBuilder sb = makeRequest();
            displayBags(sb);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Could not connect to service: " + e.getMessage(), "Service Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private StringBuilder makeRequest() throws URISyntaxException, IOException {
        StringBuilder sb = new StringBuilder();

        URI uri = new URI("http://localhost:8080/generate-bag?count=" + this.bagCount);
        URLConnection conn = uri.toURL().openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        String inputLine;
        while ((inputLine = in.readLine()) != null)
            sb.append(inputLine);

        in.close();

        return sb;
    }

    private void displayBags(StringBuilder sb) {
        if (sb != null) {
            JSONObject obj = new JSONObject(sb.toString());
            JSONArray bags = obj.getJSONArray("bags");
            for (int b = 0; b < bags.length(); b++) {
                JSONObject bag = bags.getJSONObject(b);
                String bagContents = bag.getString("contents");
                String[] contentsArray = bagContents.split(",");

                StringBuilder output = new StringBuilder();
                output.append("Bag #");
                output.append(bag.getInt("bagId"));
                output.append(" contains ");
                output.append(contentsArray.length);
                output.append(" items:\n\n");
                for (String giantBagItem : contentsArray) {
                    output.append("     ");
                    output.append(giantBagItem);
                    output.append("\n");
                }

                this.displayAreas.get(b).setText(output.toString());
            }
        }
    }

    /*
     * Shut down the application
     */
    private void exitApp() {
        this.dispose();
        System.exit(0);
    }

    private void createFileMenu() {
        JMenu menu = new JMenu("File");
        menuBar.add(menu);

        // File -> Exit
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke('X', InputEvent.CTRL_DOWN_MASK));
        exitMenuItem.addActionListener(e -> exitApp());
        menu.add(exitMenuItem);
    }

    private void createHelpMenu() {
        JMenu menu = new JMenu("Help");
        menuBar.add(menu);

        // Help -> About...
        JMenuItem menuItem = new JMenuItem("About...");
        menuItem.addActionListener(arg0 -> showAbout());
        menu.add(menuItem);
    }

    private void showAbout() {
        JOptionPane.showMessageDialog(this, ABOUT_MESSAGE, "About", JOptionPane.PLAIN_MESSAGE);
    }
}
