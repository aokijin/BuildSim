import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;

// A shipment: a delivery of a material with the quantity, the expected date and the status
class Shipment implements CsvRecord {
    private String materialName;
    private double quantity;
    private LocalDate expectedDate;
    private String status;       // Pending, Delivered or Delayed

    public Shipment(String materialName, double quantity, LocalDate expectedDate, String status) {
        if (quantity <= 0) {
            throw new InvalidInputException("The quantity must be more than 0.");
        }
        if (!status.equals("Pending") && !status.equals("Delivered") && !status.equals("Delayed")) {
            throw new InvalidInputException("The status must be Pending, Delivered or Delayed.");
        }
        this.materialName = materialName;
        this.quantity = quantity;
        this.expectedDate = expectedDate;
        this.status = status;
    }

    public String getMaterialName() { return materialName; }
    public double getQuantity() { return quantity; }
    public LocalDate getExpectedDate() { return expectedDate; }
    public String getStatus() { return status; }
    public void setMaterialName(String value) { materialName = value; }

    public String toCsv() {
        return "SHIPMENT," + Project.clean(materialName) + "," + quantity + "," + expectedDate + "," + status;
    }
}

/** The Shipments tab: add the deliveries of the materials. */
public class ShipmentsTab extends JPanel {

    private Main app;

    private JComboBox<String> shipMaterialBox = new JComboBox<>();
    private JTextField shipQtyField = new JTextField(15);
    private JTextField shipDateField = Main.dateField(15);
    private JComboBox<String> shipStatusBox = new JComboBox<>(new String[]{"Pending", "Delivered", "Delayed"});
    private JButton addShipmentButton = new JButton("Add shipment");
    private JButton deleteShipmentButton = new JButton("Delete selected");
    private JButton editShipmentButton = new JButton("Edit selected");
    private DefaultTableModel shipmentModel = Main.newModel("Material", "Quantity", "Expected date", "Status");
    private JTable shipmentTable = new JTable(shipmentModel);

    public ShipmentsTab(Main app) {
        this.app = app;
        JPanel form = Main.makeForm("Material", shipMaterialBox,
                "Quantity", shipQtyField,
                "Expected date (yyyy-mm-dd)", shipDateField,
                "Status", shipStatusBox);
        Main.fillTab(this, form, shipmentTable, addShipmentButton, editShipmentButton, deleteShipmentButton);
        addShipmentButton.addActionListener(e -> addShipment());
        editShipmentButton.addActionListener(e -> editShipment());
        deleteShipmentButton.addActionListener(e -> deleteShipment());
    }

    private void editShipment() {
        int row=Main.selectedRow(shipmentTable,"shipment"); if(row<0)return;
        Shipment old=app.getProject().getShipments().get(row);
        JComboBox<String> material=new JComboBox<>(); for(Material m:app.getProject().getMaterials()) material.addItem(m.getName()); material.setSelectedItem(old.getMaterialName());
        JTextField qty=new JTextField(String.valueOf(old.getQuantity())); JTextField date=Main.dateField(15); date.setText(old.getExpectedDate().toString());
        JComboBox<String> status=new JComboBox<>(new String[]{"Pending","Delayed","Delivered"}); status.setSelectedItem(old.getStatus());
        int result=JOptionPane.showConfirmDialog(this,Main.makeForm("Material",material,"Quantity",qty,"Expected date",date,"Status",status),"Edit shipment",JOptionPane.OK_CANCEL_OPTION);
        if(result!=JOptionPane.OK_OPTION)return;
        Double q=Main.readNumber(qty,"Quantity"); LocalDate d=Main.readDate(date,"Expected date"); if(q==null||q==0||d==null)return;
        app.getProject().getShipments().set(row,new Shipment((String)material.getSelectedItem(),q,d,(String)status.getSelectedItem())); app.changed();
    }

    private void addShipment() {
        String materialName = (String) shipMaterialBox.getSelectedItem();
        if (materialName == null) {
            Main.message("Add a material first (Materials tab).");
            return;
        }
        Double quantity = Main.readNumber(shipQtyField, "Quantity");
        if (quantity == null) {
            return;
        }
        if (quantity == 0) {
            Main.message("Quantity must be more than 0.");
            return;
        }
        LocalDate date = Main.readDate(shipDateField, "Expected date");
        if (date == null) {
            return;
        }
        String status = (String) shipStatusBox.getSelectedItem();
        try {
            app.getProject().getShipments().add(new Shipment(materialName, quantity, date, status));
        } catch (InvalidInputException e) {
            Main.message(e.getMessage());
            return;
        }
        shipQtyField.setText("");
        shipDateField.setText("");
        app.changed();
    }

    private void deleteShipment() {
        int row = Main.selectedRow(shipmentTable, "shipment");
        if (row < 0) {
            return;
        }
        app.getProject().getShipments().remove(row);
        app.changed();
    }

    public void refresh() {
        Project p = app.getProject();

        // the material list (keeps the one that was selected)
        Object oldMaterial = shipMaterialBox.getSelectedItem();
        shipMaterialBox.removeAllItems();
        for (Material m : p.getMaterials()) {
            shipMaterialBox.addItem(m.getName());
        }
        if (oldMaterial != null) {
            shipMaterialBox.setSelectedItem(oldMaterial);
        }

        shipmentModel.setRowCount(0);
        for (Shipment s : p.getShipments()) {
            shipmentModel.addRow(new Object[]{s.getMaterialName(), Main.number(s.getQuantity()), s.getExpectedDate(), s.getStatus()});
        }
    }
}
