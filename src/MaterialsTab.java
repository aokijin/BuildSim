import javax.swing.*;
import javax.swing.table.DefaultTableModel;

// A material: the kind, how many we have, the price each and how many are in stock
class Material extends Resource {
    private double quantity;     // how many we have in total
    private double priceEach;    // price of one piece / bag / etc.
    private double inStock;      // how many are still in stock

    public Material(String name, double quantity, double priceEach, double inStock) {
        super(name);
        if (quantity < 0 || priceEach < 0 || inStock < 0) {
            throw new InvalidInputException("The quantity, the price and the stock cannot be negative.");
        }
        this.quantity = quantity;
        this.priceEach = priceEach;
        this.inStock = inStock;
    }

    public double getQuantity() { return quantity; }
    public double getPriceEach() { return priceEach; }
    public double getInStock() { return inStock; }

    public String getKind() { return "Material"; }

    // What we paid for it. The number of days does not matter for a material.
    public double getCost(int days) { return quantity * priceEach; }

    public String toCsv() {
        return "MATERIAL," + Project.clean(getName()) + "," + quantity + "," + priceEach + "," + inStock;
    }
}

/** The Materials tab: add the kinds of material with the quantity, the price and the stock. */
public class MaterialsTab extends JPanel {

    private Main app;

    private JTextField materialNameField = new JTextField(15);
    private JTextField materialQtyField = new JTextField(15);
    private JTextField materialPriceField = new JTextField(15);
    private JTextField materialStockField = new JTextField(15);
    private JButton addMaterialButton = new JButton("Add material");
    private JButton deleteMaterialButton = new JButton("Delete selected");
    private JButton editMaterialButton = new JButton("Edit selected");
    private DefaultTableModel materialModel = Main.newModel("Material", "Quantity", "Price each (PHP)", "In stock", "Total cost (PHP)");
    private JTable materialTable = new JTable(materialModel);

    public MaterialsTab(Main app) {
        this.app = app;
        JPanel form = Main.makeForm("Material (kind)", materialNameField,
                "Quantity (how many we have)", materialQtyField,
                "Price each (PHP)", materialPriceField,
                "In stock", materialStockField);
        Main.fillTab(this, form, materialTable, addMaterialButton, editMaterialButton, deleteMaterialButton);
        addMaterialButton.addActionListener(e -> addMaterial());
        editMaterialButton.addActionListener(e -> editMaterial());
        deleteMaterialButton.addActionListener(e -> deleteMaterial());
    }

    private void editMaterial() {
        int row = Main.selectedRow(materialTable, "material"); if (row < 0) return;
        Material old = app.getProject().getMaterials().get(row);
        JTextField name = new JTextField(old.getName()), qty = new JTextField(String.valueOf(old.getQuantity()));
        JTextField price = new JTextField(String.valueOf(old.getPriceEach())), stock = new JTextField(String.valueOf(old.getInStock()));
        int result = JOptionPane.showConfirmDialog(this, Main.makeForm("Material", name, "Quantity", qty, "Price each", price, "In stock", stock), "Edit material", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;
        Double q = Main.readNumber(qty,"Quantity"), p = Main.readNumber(price,"Price each"), s = Main.readNumber(stock,"In stock");
        if(q==null||p==null||s==null||name.getText().trim().isEmpty()) return;
        for(int i=0;i<app.getProject().getMaterials().size();i++) if(i!=row && app.getProject().getMaterials().get(i).getName().equals(name.getText().trim())) { Main.message("This material is already in the list."); return; }
        app.getProject().getMaterials().set(row,new Material(name.getText().trim(),q,p,s));
        String changedFrom=old.getName(), changedTo=name.getText().trim();
        for(Shipment shipment:app.getProject().getShipments()) if(shipment.getMaterialName().equals(changedFrom)) shipment.setMaterialName(changedTo);
        app.changed();
    }

    private void addMaterial() {
        Project project = app.getProject();
        String name = materialNameField.getText().trim();
        if (name.isEmpty()) {
            Main.message("Please enter the kind of material.");
            return;
        }
        for (Material m : project.getMaterials()) {
            if (m.getName().equals(name)) {
                Main.message("This material is already in the list.");
                return;
            }
        }
        Double quantity = Main.readNumber(materialQtyField, "Quantity");
        if (quantity == null) {
            return;
        }
        Double price = Main.readNumber(materialPriceField, "Price each");
        if (price == null) {
            return;
        }
        Double stock = Main.readNumber(materialStockField, "In stock");
        if (stock == null) {
            return;
        }
        try {
            project.getMaterials().add(new Material(name, quantity, price, stock));
        } catch (InvalidInputException e) {
            Main.message(e.getMessage());
            return;
        }
        materialNameField.setText("");
        materialQtyField.setText("");
        materialPriceField.setText("");
        materialStockField.setText("");
        app.changed();
    }

    private void deleteMaterial() {
        Project project = app.getProject();
        int row = Main.selectedRow(materialTable, "material");
        if (row < 0) {
            return;
        }
        Material removed = project.getMaterials().remove(row);
        // the shipments and the late-shipment problems of this material are removed too
        for (int i = project.getShipments().size() - 1; i >= 0; i--) {
            if (project.getShipments().get(i).getMaterialName().equals(removed.getName())) {
                project.getShipments().remove(i);
            }
        }
        project.removeProblems("Shipment delay", removed.getName());
        app.changed();
    }

    public void refresh() {
        materialModel.setRowCount(0);
        for (Material m : app.getProject().getMaterials()) {
            materialModel.addRow(new Object[]{m.getName(), Main.number(m.getQuantity()), Main.money(m.getPriceEach()),
                    Main.number(m.getInStock()), Main.money(m.getQuantity() * m.getPriceEach())});
        }
    }
}
