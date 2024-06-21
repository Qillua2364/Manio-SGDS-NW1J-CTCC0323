import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.prefs.Preferences;

public class GroceryDeliveryApp {

    private static DefaultTableModel itemTableModel;
    private static ArrayList<String[]> addedItems = new ArrayList<>();
    private static JCheckBox rememberCredentialsCheckbox;
    private static JCheckBox rememberDetailsCheckbox;
    private static boolean rememberCredentials = false;
    private static Preferences preferences = Preferences.userNodeForPackage(GroceryDeliveryApp.class);
    private static JLabel mainNotificationLabel = new JLabel();
    private static JLabel cartNotificationLabel = new JLabel();
    private static JFrame mainFrame;
    private static JFrame itemListFrame;
    private static DefaultTableModel cartTableModel; // Added for cart table model

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GroceryDeliveryApp::createLoginWindow);
    }

    private static void centerWindow(Window frame) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(screenSize.width / 2 - frame.getSize().width / 2, screenSize.height / 2 - frame.getSize().height / 2);
    }

    private static void setCustomBackground(JFrame frame) {
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon backgroundImage = new ImageIcon(GroceryDeliveryApp.class.getResource("/framebg.jpg"));
                g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        frame.setContentPane(backgroundPanel);
    }

    private static void setRoundCorners(JFrame frame) {
        frame.setUndecorated(true);
        frame.setShape(new RoundRectangle2D.Double(0, 0, frame.getWidth(), frame.getHeight(), 50, 50));
    }

    private static void createLoginWindow() {
        JFrame frame = new JFrame("LOGIN");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon backgroundImage = new ImageIcon(GroceryDeliveryApp.class.getResource("/framebg.jpg"));
                g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(new GridBagLayout());

        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new GridLayout(6, 1, 10, 10));
        loginPanel.setOpaque(false);

        JLabel userLabel = new JLabel("Username:");
        JTextField userText = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordText = new JPasswordField();
        JButton loginButton = new JButton("Login");
        rememberCredentialsCheckbox = new JCheckBox("Remember my credentials");

        loginPanel.add(userLabel);
        loginPanel.add(userText);
        loginPanel.add(passwordLabel);
        loginPanel.add(passwordText);
        loginPanel.add(loginButton);
        loginPanel.add(rememberCredentialsCheckbox);

        if (preferences.getBoolean("rememberCredentials", false)) {
            userText.setText(preferences.get("username", ""));
            passwordText.setText(preferences.get("password", ""));
            rememberCredentialsCheckbox.setSelected(true);
        }

        backgroundPanel.add(loginPanel, new GridBagConstraints());

        String correctUsername = "ronmanio";
        String correctPassword = "12345678";

        loginButton.addActionListener(e -> {
            String username = userText.getText();
            String password = new String(passwordText.getPassword());

            if (username.equals(correctUsername) && password.equals(correctPassword)) {
                rememberCredentials = rememberCredentialsCheckbox.isSelected();
                if (rememberCredentials) {
                    preferences.putBoolean("rememberCredentials", true);
                    preferences.put("username", username);
                    preferences.put("password", password);
                } else {
                    preferences.remove("rememberCredentials");
                    preferences.remove("username");
                    preferences.remove("password");
                }
                JOptionPane.showMessageDialog(frame, "Login successful!");
                frame.dispose();
                createMainWindow();
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid username or password.");
            }
        });

        frame.add(backgroundPanel);
        setRoundCorners(frame);
        frame.setVisible(true);
        centerWindow(frame);
    }

    private static void createMainWindow() {
        mainFrame = new JFrame("SGDS");
        mainFrame.setSize(800, 600);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setCustomBackground(mainFrame);
        setRoundCorners(mainFrame);
        mainFrame.setLayout(new BorderLayout());

        Object[][] products = {
                {"1", "Rice", "₱20", "Grains"},
                {"2", "Eggs", "₱25", "Dairy"},
                {"3", "Coke", "₱30", "Beverages"},
                {"4", "Chicken", "₱12", "Meat"},
                {"5", "Vinegar", "₱15", "Condiments"},
                {"6", "Sauce", "₱45", "Condiments"},
                {"7", "Bread", "₱35", "Bakery"},
                {"8", "Beer", "₱55", "Beverages"},
                {"9", "Nuts", "₱10", "Snacks"},
                {"10", "Chicken Wings", "₱150", "Beverages"},
                {"11", "C2 apple", "₱45", "Beverages"},
                {"12", "Redhorse", "₱65", "Beverages"},
                {"13", "Piattos", "₱15", "Snacks"},
                {"14", "Mr.chips", "₱10", "Snacks"},
                {"15", "Mt. Dew", "₱20", "Beverages"},
                {"16", "Fish Sauce", "₱25", "Condiments"},
                {"17", "Magic Sarap", "₱9", "Condiments"},
                {"18", "Pepper", "₱20", "Condiments"},
                {"19", "sky flakes", "₱7", "Snacks"},
                {"20", "Vcut", "₱20", "Snacks"},
                {"21", "Milk", "₱30", "Dairy"},
                {"22", "Fish", "₱100", "Meat"},
                {"23", "Cow Meat", "₱200", "Meat"},
                {"24", "Nissan GTR R34", "₱500,000", "Car"}
        };

        itemTableModel = new DefaultTableModel(products, new String[]{"ID", "Description", "Price", "Category"});
        JTable productTable = new JTable(itemTableModel);
        JScrollPane productScrollPane = new JScrollPane(productTable);

        JButton addItemButton = new JButton("Add Item");
        JButton itemListButton = new JButton("Item List");
        JButton logoutButton = new JButton("Logout");
        JButton searchButton = new JButton("Search");
        JTextField searchTextField = new JTextField(20);
        JButton resetFilterButton = new JButton("Reset Filter");

        String[] categories = {"All", "Grains", "Dairy", "Beverages", "Meat", "Condiments", "Bakery", "Snacks", "Car"};
        JComboBox<String> categoryComboBox = new JComboBox<>(categories);
        categoryComboBox.addActionListener(e -> {
            String selectedCategory = (String) categoryComboBox.getSelectedItem();
            itemTableModel.setRowCount(0);

            for (Object[] product : products) {
                if (selectedCategory.equals("All") || product[3].equals(selectedCategory)) {
                    itemTableModel.addRow(product);
                }
            }
        });

        String[] prices = {"All", "₱10 and below", "₱11-₱20", "₱21-₱50", "₱51 and above"};
        JComboBox<String> priceComboBox = new JComboBox<>(prices);
        priceComboBox.addActionListener(e -> {
            String selectedPrice = (String) priceComboBox.getSelectedItem();
            itemTableModel.setRowCount(0);

            for (Object[] product : products) {
                double price = Double.parseDouble(((String) product[2]).replace("₱", ""));
                if (selectedPrice.equals("All") ||
                        (selectedPrice.equals("₱10 and below") && price <= 10) ||
                        (selectedPrice.equals("₱11-₱20") && price >= 11 && price <= 20) ||
                        (selectedPrice.equals("₱21-₱50") && price >= 21 && price <= 50) ||
                        (selectedPrice.equals("₱51 and above") && price >= 51)) {
                    itemTableModel.addRow(product);
                }
            }
        });

        addItemButton.addActionListener(e -> {
            int selectedRow = productTable.getSelectedRow();
            if (selectedRow != -1) {
                String[] selectedItem =                     new String[4]; // Changed size from 5 to 4 to match the structure of products array

                for (int i = 0; i < 4; i++) { // Changed iteration limit from 5 to 4
                    selectedItem[i] = (String) productTable.getValueAt(selectedRow, i); // Cast to String
                }

                addedItems.add(selectedItem); // Add selected item to addedItems list
                updateCartTable(); // Update cart table to reflect changes
                showItemAddedNotification(); // Show notification
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Please select an item to add to cart.");
            }
        });

        itemListButton.addActionListener(e -> createItemListWindow());

        logoutButton.addActionListener(e -> {
            // Dispose of the current main window
            mainFrame.dispose();

            // Dispose of the item list window if it's open
            if (itemListFrame != null && itemListFrame.isVisible()) {
                itemListFrame.dispose();
            }

            // Open the login window again
            createLoginWindow();
        });

        searchButton.addActionListener(e -> {
            String searchText = searchTextField.getText();
            itemTableModel.setRowCount(0);

            for (Object[] product : products) {
                if (((String) product[1]).contains(searchText)) {
                    itemTableModel.addRow(product);
                }
            }
        });

        resetFilterButton.addActionListener(e -> {
            itemTableModel.setRowCount(0);
            for (Object[] product : products) {
                itemTableModel.addRow(product);
            }
        });

        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.add(categoryComboBox);
        topPanel.add(priceComboBox);
        topPanel.add(searchTextField);
        topPanel.add(searchButton);
        topPanel.add(resetFilterButton);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.add(addItemButton);
        bottomPanel.add(itemListButton);
        bottomPanel.add(logoutButton);

        mainNotificationLabel.setForeground(Color.GREEN);
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        leftPanel.add(mainNotificationLabel, BorderLayout.NORTH);

        mainFrame.add(topPanel, BorderLayout.NORTH);
        mainFrame.add(productScrollPane, BorderLayout.CENTER);
        mainFrame.add(bottomPanel, BorderLayout.SOUTH);
        mainFrame.add(leftPanel, BorderLayout.WEST);
        mainFrame.setVisible(true);
        centerWindow(mainFrame);
    }

    private static void createItemListWindow() {
        itemListFrame = new JFrame("SHOPPING CART");
        itemListFrame.setSize(600, 400);
        itemListFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setCustomBackground(itemListFrame);
        setRoundCorners(itemListFrame);
        itemListFrame.setLayout(new BorderLayout());

        // Initialize or update the cart table model
        cartTableModel = new DefaultTableModel(new String[]{"ID", "Description", "Price", "Category"}, 0);
        updateCartTable(); // Populate cart table with existing added items

        JTable cartTable = new JTable(cartTableModel);
        JScrollPane cartScrollPane = new JScrollPane(cartTable);

        JButton confirmDeliveryButton = new JButton("Confirm Delivery");
        confirmDeliveryButton.addActionListener(e -> {
            if (addedItems.isEmpty()) {
                JOptionPane.showMessageDialog(itemListFrame, "Please pick an item first.");
            } else {
                itemListFrame.dispose();
                createDeliveryWindow();
            }
        });

        cartNotificationLabel.setForeground(Color.GREEN);
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.add(confirmDeliveryButton);
        bottomPanel.add(cartNotificationLabel);

        itemListFrame.add(cartScrollPane, BorderLayout.CENTER);
        itemListFrame.add(bottomPanel, BorderLayout.SOUTH);
        itemListFrame.setVisible(true);
        centerWindow(itemListFrame);
    }

    private static void updateCartTable() {
        cartTableModel.setRowCount(0); // Clear existing rows

        for (String[] item : addedItems) {
            cartTableModel.addRow(item); // Add each item in addedItems to cart table model
        }
    }

    private static void showItemAddedNotification() {
        mainNotificationLabel.setText("Item added to cart!");
        Timer timer = new Timer(3000, e -> mainNotificationLabel.setText(""));
        timer.setRepeats(false);
        timer.start();
    }

    private static void createDeliveryWindow() {
        JFrame frame = new JFrame("Grocery Delivery App - Delivery Details");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setCustomBackground(frame);
        setRoundCorners(frame);
        frame.setLayout(new GridLayout(7, 2, 10, 10)); // Modified to 7 rows

        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField();
        JLabel addressLabel = new JLabel("Address:");
        JTextField addressField = new JTextField();
        JLabel phoneLabel = new JLabel("Phone:");
        JTextField phoneField = new JTextField();
        JButton confirmButton = new JButton("Confirm");
        JButton closeAppButton = new JButton("Close App");

        // Checkbox for remembering delivery details
        rememberDetailsCheckbox = new JCheckBox("Remember my details");

        // Load remembered details if checkbox was previously selected
        if (preferences.getBoolean("rememberDetails", false)) {
            nameField.setText(preferences.get("name", ""));
            addressField.setText(preferences.get("address", ""));
            phoneField.setText(preferences.get("phone", ""));
            rememberDetailsCheckbox.setSelected(true);
        }

        confirmButton.addActionListener(e -> {
            if (rememberDetailsCheckbox.isSelected()) {
                preferences.putBoolean("rememberDetails", true);
                preferences.put("name", nameField.getText());
                preferences.put("address", addressField.getText());
                preferences.put("phone", phoneField.getText());
            } else {
                preferences.remove("rememberDetails");
                preferences.remove("name");
                preferences.remove("address");
                preferences.remove("phone");
            }

            JOptionPane.showMessageDialog(frame, "Order confirmed! Your delivery is on its way.");
            addedItems.clear();
            frame.dispose();
            mainFrame.dispose();
            createMainWindow();
        });

        closeAppButton.addActionListener(e -> {
            if (rememberDetailsCheckbox.isSelected()) {
                preferences.putBoolean("rememberDetails", true);
                preferences.put("name", nameField.getText());
                preferences.put("address", addressField.getText());
                preferences.put("phone", phoneField.getText());
            } else {
                preferences.remove("rememberDetails");
                preferences.remove("name");
                preferences.remove("address");
                preferences.remove("phone");
            }

            JOptionPane.showMessageDialog(frame, "Order confirmed! Closing application.");
            System.exit(0);
        });

        frame.add(nameLabel);
        frame.add(nameField);
        frame.add(addressLabel);
        frame.add(addressField);
        frame.add(phoneLabel);
        frame.add(phoneField);
        frame.add(rememberDetailsCheckbox);
        frame.add(new JLabel());
        frame.add(confirmButton);
        frame.add(closeAppButton);

        frame.setVisible(true);
        centerWindow(frame);
    }
}

