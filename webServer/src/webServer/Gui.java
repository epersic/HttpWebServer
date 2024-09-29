package webServer;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class Gui {

    private JFrame f; // The main frame
    private boolean isOn = false; // State for ON/OFF toggle
    private JTextArea messageArea; // Text area to show messages
    private JToggleButton onOffButton; // ON/OFF button
    private JTextField portInput; // Port input field

    public Gui() {
        // Create the main frame
        f = new JFrame("Proxy HTTP server");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(800, 600); // Fixed size

        // Create the text area (message box) on the right
        messageArea = new JTextArea();
        messageArea.setEditable(false); // Messages should not be edited by the user
        JScrollPane scrollPane = new JScrollPane(messageArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(400, 350));

        // Create the panel for buttons and inputs on the left
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS)); // Vertical layout

        // Create the ON/OFF toggle button (initially disabled)
        onOffButton = new JToggleButton("OFF");
        onOffButton.setPreferredSize(new Dimension(150, 50));
        onOffButton.setEnabled(false); // Initially disabled
        onOffButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Align center

        // Add action listener to the toggle button
        onOffButton.addActionListener(e -> {
            isOn = !isOn;
            onOffButton.setText(isOn ? "ON" : "OFF");
            appendMessage("Toggled to " + (isOn ? "ON" : "OFF"));
        });

        // Create the "Save the Requests" button
        JButton saveButton = new JButton("Save the Requests");
        saveButton.setPreferredSize(new Dimension(150, 50));
        saveButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Align center

        // Add action listener for the Save button
        saveButton.addActionListener(e -> appendMessage("Save button pressed"));

        // Create the input field for port
        JLabel portLabel = new JLabel("Port:");
        portInput = new JTextField();
        portInput.setMaximumSize(new Dimension(150, 30)); // Fixed width

        // Add a DocumentListener to the port input field to enable/disable ON/OFF button based on input
        portInput.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                toggleOnOffButton();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                toggleOnOffButton();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                toggleOnOffButton();
            }

            // Enable or disable the ON/OFF button based on whether the port input is empty or not
            private void toggleOnOffButton() {
                String portText = portInput.getText().trim();
                onOffButton.setEnabled(!portText.isEmpty());
            }
        });

        // Add components to the control panel
        controlPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Spacer
        controlPanel.add(onOffButton);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Spacer
        controlPanel.add(saveButton);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Spacer
        controlPanel.add(portLabel);
        controlPanel.add(portInput);
        controlPanel.add(Box.createVerticalGlue()); // Pushes everything up

        // Use a split pane to divide the control panel (left) and the message box (right)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, controlPanel, scrollPane);
        splitPane.setDividerLocation(200); // Set initial divider location
        splitPane.setEnabled(false); // Disable resizing by dragging

        // Add the split pane to the main frame
        f.getContentPane().add(splitPane);

        // Make the frame visible
        f.setVisible(true);
    }

    // Function to append a message to the text area
    public void appendMessage(String message) {
        messageArea.append(message + "\n");
    }

    // Function to get the port input value
    public String getPort() {
        return portInput.getText();
    }

    // Function to check if the server is ON
    public boolean isServerOn() {
        return isOn;
    }

    // Function to show the GUI window
    public void show() {
        f.setVisible(true);
    }

    // Function to hide the GUI window
    public void hide() {
        f.setVisible(false);
    }
}
