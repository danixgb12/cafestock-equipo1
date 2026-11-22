/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package cafestock.vista;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import cafestock.controlador.ProductoControlador;
import cafestock.modelo.Producto;
import cafestock.persistencia.ProductoRepositorioCsv;
import cafestock.persistencia.RutaDatos;
import cafestock.servicio.ProductoServicio;
import java.io.IOException;
/**
 *
 * @author ksang
 */
public class PanelProductos extends javax.swing.JPanel {

    private ProductoControlador controlador;
    private String codigoEnEdicion = null;
    
    /** Recupera los productos existentes al construir la pantalla. */
    private void iniciarInventario() {
        try {
            controlador = new ProductoControlador(
                new ProductoServicio(new ProductoRepositorioCsv(RutaDatos.productos())));
            actualizarTabla();
        } catch (IOException ex) {
            btnGuardar.setEnabled(false); // evitar registros temporales si falla el CSV
            JOptionPane.showMessageDialog(this,
                "No se pudo abrir productos.csv: " + ex.getMessage(),
                "Error de almacenamiento", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarTabla() {
        DefaultTableModel tabla = (DefaultTableModel) tblProductos.getModel();
        tabla.setRowCount(0);
        if (controlador == null) return;
        for (Producto p : controlador.obtenerProductos()) {
            tabla.addRow(new Object[]{p.getCodigo(), p.getNombre(), p.getPrecio(),
                p.getStock(), p.getStockMinimo()});
        }
    }


    /**
     * Creates new form PanelProductos
     */
    public PanelProductos() {
        initComponents();
        iniciarInventario();
        txtBuscar.getDocument().addDocumentListener(
        new javax.swing.event.DocumentListener() {

                @Override
                public void insertUpdate(javax.swing.event.DocumentEvent e) {
                    filtrarProductos();
                }

                @Override
                public void removeUpdate(javax.swing.event.DocumentEvent e) {
                    filtrarProductos();
                }

                @Override
                public void changedUpdate(javax.swing.event.DocumentEvent e) {
                    filtrarProductos();
                }
            }
        );
        
        tblProductos.getSelectionModel().addListSelectionListener(e -> {

        if (e.getValueIsAdjusting()) {
            return;
        }

        int fila = tblProductos.getSelectedRow();

        if (fila == -1) {
            return;
        }

        // Cargar los datos del producto seleccionado.
        txtCodigo.setText(
            String.valueOf(tblProductos.getValueAt(fila, 0))
        );

        txtNombre.setText(
            String.valueOf(tblProductos.getValueAt(fila, 1))
        );

        txtPrecio.setText(
            String.valueOf(tblProductos.getValueAt(fila, 2))
        );

        txtStockActual.setText(
            String.valueOf(tblProductos.getValueAt(fila, 3))
        );

        txtStockMinimo.setText(
            String.valueOf(tblProductos.getValueAt(fila, 4))
        );

        codigoEnEdicion = txtCodigo.getText().trim();

        txtCodigo.setEditable(false);
        lblNuevoProducto.setText("Editar producto");
        btnGuardar.setText("Guardar cambios");
        btnGuardar.setEnabled(controlador != null);
    });
    }
    
    private void filtrarProductos() {

        String texto = txtBuscar.getText().trim();

        javax.swing.table.DefaultTableModel modelo =
            (javax.swing.table.DefaultTableModel)
            tblProductos.getModel();

        javax.swing.table.TableRowSorter<
            javax.swing.table.DefaultTableModel> filtro =
            new javax.swing.table.TableRowSorter<>(modelo);

        tblProductos.setRowSorter(filtro);

        // Si no hay texto, mostrar todos los productos.
        if (texto.isEmpty()) {
            filtro.setRowFilter(null);
            return;
        }

        filtro.setRowFilter(
            new javax.swing.RowFilter<
                javax.swing.table.DefaultTableModel, Integer>() {

                @Override
                public boolean include(
                    Entry<? extends
                    javax.swing.table.DefaultTableModel,
                    ? extends Integer> fila) {

                    String codigo =
                        String.valueOf(fila.getValue(0));

                    String nombre =
                        String.valueOf(fila.getValue(1));

                    return codigo.equalsIgnoreCase(texto)
                        || nombre.toLowerCase(java.util.Locale.ROOT)
                            .contains(texto.toLowerCase(java.util.Locale.ROOT));
                }
            }
        );
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlCabecera = new javax.swing.JPanel();
        lblMarca = new javax.swing.JLabel();
        lblSubtitulo = new javax.swing.JLabel();
        lblModulo = new javax.swing.JLabel();
        lblTitulo = new javax.swing.JLabel();
        lblDescripcion = new javax.swing.JLabel();
        pnlFormulario = new javax.swing.JPanel();
        lblNuevoProducto = new javax.swing.JLabel();
        lblCodigo = new javax.swing.JLabel();
        txtCodigo = new javax.swing.JTextField();
        lblNombre = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        lblPrecio = new javax.swing.JLabel();
        txtPrecio = new javax.swing.JTextField();
        lblStockActual = new javax.swing.JLabel();
        txtStockActual = new javax.swing.JTextField();
        lblStockMinimo = new javax.swing.JLabel();
        txtStockMinimo = new javax.swing.JTextField();
        btnGuardar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        pnlInventario = new javax.swing.JPanel();
        lblInventario = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        btnVerTodos = new javax.swing.JButton();
        lblBuscar = new javax.swing.JLabel();
        scrollProductos = new javax.swing.JScrollPane();
        tblProductos = new javax.swing.JTable();

        setBackground(new java.awt.Color(247, 243, 237));

        pnlCabecera.setBackground(new java.awt.Color(35, 70, 56));

        lblMarca.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        lblMarca.setForeground(new java.awt.Color(255, 255, 255));
        lblMarca.setText("CAFÉSTOCK");

        lblSubtitulo.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblSubtitulo.setForeground(new java.awt.Color(232, 238, 233));
        lblSubtitulo.setText("\tGestión de productos de la cafetería");

        lblModulo.setBackground(new java.awt.Color(200, 141, 88));
        lblModulo.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblModulo.setForeground(new java.awt.Color(255, 255, 255));
        lblModulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblModulo.setText("INVENTARIO");
        lblModulo.setOpaque(true);

        javax.swing.GroupLayout pnlCabeceraLayout = new javax.swing.GroupLayout(pnlCabecera);
        pnlCabecera.setLayout(pnlCabeceraLayout);
        pnlCabeceraLayout.setHorizontalGroup(
            pnlCabeceraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCabeceraLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(pnlCabeceraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlCabeceraLayout.createSequentialGroup()
                        .addComponent(lblSubtitulo)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pnlCabeceraLayout.createSequentialGroup()
                        .addComponent(lblMarca)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblModulo, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(21, 21, 21))))
        );
        pnlCabeceraLayout.setVerticalGroup(
            pnlCabeceraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCabeceraLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(pnlCabeceraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblMarca)
                    .addGroup(pnlCabeceraLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(lblModulo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblSubtitulo)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblTitulo.setForeground(new java.awt.Color(39, 55, 46));
        lblTitulo.setText("\tAdministración de productos");

        lblDescripcion.setForeground(new java.awt.Color(113, 128, 119));
        lblDescripcion.setText("\tRegistra y organiza los productos de CaféStock");

        pnlFormulario.setBackground(new java.awt.Color(255, 255, 255));

        lblNuevoProducto.setFont(new java.awt.Font("Segoe UI", 1, 17)); // NOI18N
        lblNuevoProducto.setForeground(new java.awt.Color(35, 70, 56));
        lblNuevoProducto.setText("Nuevo producto");

        lblCodigo.setForeground(new java.awt.Color(93, 106, 96));
        lblCodigo.setText("Código");

        txtCodigo.setBackground(new java.awt.Color(247, 247, 244));
        txtCodigo.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtCodigo.setForeground(new java.awt.Color(39, 55, 46));
        txtCodigo.addActionListener(this::txtCodigoActionPerformed);

        lblNombre.setForeground(new java.awt.Color(93, 106, 96));
        lblNombre.setText("Nombre del producto");

        txtNombre.setBackground(new java.awt.Color(247, 247, 244));
        txtNombre.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtNombre.setForeground(new java.awt.Color(39, 55, 46));
        txtNombre.addActionListener(this::txtNombreActionPerformed);

        lblPrecio.setForeground(new java.awt.Color(93, 106, 96));
        lblPrecio.setText("\tPrecio de venta (USD)");

        txtPrecio.setBackground(new java.awt.Color(247, 247, 244));
        txtPrecio.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtPrecio.setForeground(new java.awt.Color(39, 55, 46));
        txtPrecio.addActionListener(this::txtPrecioActionPerformed);

        lblStockActual.setForeground(new java.awt.Color(93, 106, 96));
        lblStockActual.setText("Stock actual");

        txtStockActual.setBackground(new java.awt.Color(247, 247, 244));
        txtStockActual.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtStockActual.setForeground(new java.awt.Color(39, 55, 46));
        txtStockActual.addActionListener(this::txtStockActualActionPerformed);

        lblStockMinimo.setForeground(new java.awt.Color(93, 106, 96));
        lblStockMinimo.setText("\tStock mínimo");

        txtStockMinimo.setBackground(new java.awt.Color(247, 247, 244));
        txtStockMinimo.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtStockMinimo.setForeground(new java.awt.Color(39, 55, 46));
        txtStockMinimo.addActionListener(this::txtStockMinimoActionPerformed);

        btnGuardar.setBackground(new java.awt.Color(45, 98, 73));
        btnGuardar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnGuardar.setForeground(new java.awt.Color(255, 255, 255));
        btnGuardar.setText("Guardar producto");
        btnGuardar.addActionListener(this::btnGuardarActionPerformed);

        btnLimpiar.setBackground(new java.awt.Color(234, 240, 233));
        btnLimpiar.setForeground(new java.awt.Color(35, 70, 56));
        btnLimpiar.setText("\tLimpiar");
        btnLimpiar.addActionListener(this::btnLimpiarActionPerformed);

        javax.swing.GroupLayout pnlFormularioLayout = new javax.swing.GroupLayout(pnlFormulario);
        pnlFormulario.setLayout(pnlFormularioLayout);
        pnlFormularioLayout.setHorizontalGroup(
            pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFormularioLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(lblStockMinimo)
                        .addComponent(lblStockActual)
                        .addComponent(lblPrecio)
                        .addComponent(lblNombre)
                        .addComponent(lblNuevoProducto)
                        .addComponent(lblCodigo)
                        .addComponent(txtStockActual)
                        .addComponent(txtPrecio)
                        .addComponent(txtNombre)
                        .addComponent(txtCodigo)
                        .addComponent(txtStockMinimo, javax.swing.GroupLayout.PREFERRED_SIZE, 351, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlFormularioLayout.createSequentialGroup()
                        .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(23, Short.MAX_VALUE))
        );
        pnlFormularioLayout.setVerticalGroup(
            pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFormularioLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(lblNuevoProducto)
                .addGap(37, 37, 37)
                .addComponent(lblCodigo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblNombre)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblPrecio)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblStockActual)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtStockActual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblStockMinimo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtStockMinimo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(24, 24, 24))
        );

        pnlInventario.setBackground(new java.awt.Color(255, 255, 255));

        lblInventario.setFont(new java.awt.Font("Segoe UI", 1, 17)); // NOI18N
        lblInventario.setForeground(new java.awt.Color(35, 70, 56));
        lblInventario.setText("Inventario de productos");

        txtBuscar.setBackground(new java.awt.Color(247, 247, 244));
        txtBuscar.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtBuscar.setForeground(new java.awt.Color(39, 55, 46));
        txtBuscar.setToolTipText("");
        txtBuscar.addActionListener(this::txtBuscarActionPerformed);

        btnVerTodos.setBackground(new java.awt.Color(45, 98, 73));
        btnVerTodos.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnVerTodos.setForeground(new java.awt.Color(255, 255, 255));
        btnVerTodos.setText("Ver todos");
        btnVerTodos.addActionListener(this::btnVerTodosActionPerformed);

        lblBuscar.setForeground(new java.awt.Color(93, 106, 96));
        lblBuscar.setText("Buscar por código o nombre");

        tblProductos.setForeground(new java.awt.Color(39, 55, 46));
        tblProductos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Código", "Nombre", "Precio (USD)", "Stock actual", "Stock mínimo"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblProductos.setAutoscrolls(false);
        tblProductos.setRowHeight(34);
        tblProductos.setSelectionBackground(new java.awt.Color(224, 237, 228));
        tblProductos.setSelectionForeground(new java.awt.Color(39, 55, 46));
        scrollProductos.setViewportView(tblProductos);

        javax.swing.GroupLayout pnlInventarioLayout = new javax.swing.GroupLayout(pnlInventario);
        pnlInventario.setLayout(pnlInventarioLayout);
        pnlInventarioLayout.setHorizontalGroup(
            pnlInventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlInventarioLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnlInventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scrollProductos)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlInventarioLayout.createSequentialGroup()
                        .addComponent(txtBuscar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnVerTodos))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlInventarioLayout.createSequentialGroup()
                        .addGroup(pnlInventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblInventario, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblBuscar, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(25, 25, 25))
        );
        pnlInventarioLayout.setVerticalGroup(
            pnlInventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlInventarioLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(lblInventario)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblBuscar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlInventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnVerTodos, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(scrollProductos, javax.swing.GroupLayout.PREFERRED_SIZE, 317, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(37, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblDescripcion)
                            .addComponent(lblTitulo))
                        .addContainerGap(781, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlFormulario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(pnlInventario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(36, 36, 36))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlCabecera, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlCabecera, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblTitulo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblDescripcion)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlInventario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlFormulario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(19, 19, 19))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtCodigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodigoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCodigoActionPerformed

    private void txtNombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNombreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNombreActionPerformed

    private void txtPrecioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPrecioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPrecioActionPerformed

    private void txtStockActualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtStockActualActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtStockActualActionPerformed

    private void txtStockMinimoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtStockMinimoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtStockMinimoActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        
        boolean editando = codigoEnEdicion != null;

        String codigo = editando
                ? codigoEnEdicion
                : txtCodigo.getText().trim();

        String nombre = txtNombre.getText().trim();
        String textoPrecio = txtPrecio.getText().trim().replace(",", ".");
        String textoStock = txtStockActual.getText().trim();
        String textoMinimo = txtStockMinimo.getText().trim();

        // Campos obligatorios
        if (codigo.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Debe ingresar el código del producto.");
            txtCodigo.requestFocus();
            return;
        }

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Debe ingresar el nombre del producto.");
            txtNombre.requestFocus();
            return;
        }

        // Comprobar códigos repetidos solo al registrar
        if (!editando) {
            DefaultTableModel modelo =
                (DefaultTableModel) tblProductos.getModel();

            for (int i = 0; i < modelo.getRowCount(); i++) {
                String existente =
                    String.valueOf(modelo.getValueAt(i, 0));

                if (existente.equalsIgnoreCase(codigo)) {
                    JOptionPane.showMessageDialog(this,
                        "Ya existe un producto con el código " + codigo);
                    return;
                }
            }
        }

        // Validar los valores numéricos
        java.math.BigDecimal precio;
        int stockActual;
        int stockMinimo;

        try {
            precio = new java.math.BigDecimal(textoPrecio);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Ingrese un precio numérico válido.");
            txtPrecio.requestFocus();
            return;
        }

        try {
            stockActual = Integer.parseInt(textoStock);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "El stock actual debe ser un número entero.");
            txtStockActual.requestFocus();
            return;
        }

        try {
            stockMinimo = Integer.parseInt(textoMinimo);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "El stock mínimo debe ser un número entero.");
            txtStockMinimo.requestFocus();
            return;
        }

        // Registrar o actualizar en el CSV
        try {
            if (controlador == null) {
                JOptionPane.showMessageDialog(this,
                    "El archivo de productos no está disponible.");
                return;
            }

            Producto producto = new Producto(
                codigo, nombre, precio, stockActual, stockMinimo
            );

            if (editando) {
                controlador.actualizar(producto);
            } else {
                controlador.registrar(producto);
            }

            // Recuperar los datos actualizados
            actualizarTabla();

            // Volver al modo de registro
            codigoEnEdicion = null;
            tblProductos.clearSelection();

            txtCodigo.setText("");
            txtNombre.setText("");
            txtPrecio.setText("");
            txtStockActual.setText("");
            txtStockMinimo.setText("");

            txtCodigo.setEditable(true);
            lblNuevoProducto.setText("Nuevo producto");
            btnGuardar.setText("Guardar producto");
            btnGuardar.setEnabled(true);

            txtBuscar.setText("");
            txtCodigo.requestFocusInWindow();

            JOptionPane.showMessageDialog(this,
                editando
                    ? "Cambios guardados correctamente."
                    : "Producto registrado correctamente.");

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                e.getMessage(), "Datos incorrectos",
                JOptionPane.WARNING_MESSAGE);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                "No se pudieron guardar los datos: " + e.getMessage(),
                "Error de almacenamiento",
                JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnVerTodosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerTodosActionPerformed
       txtBuscar.setText("");
       txtBuscar.requestFocusInWindow();
    }//GEN-LAST:event_btnVerTodosActionPerformed

    private void txtBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBuscarActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        txtCodigo.setText("");
        txtNombre.setText("");
        txtPrecio.setText("");
        txtStockActual.setText("");
        txtStockMinimo.setText("");

        tblProductos.clearSelection();
        codigoEnEdicion = null;
        lblNuevoProducto.setText("Nuevo producto");
        btnGuardar.setText("Guardar producto");
        txtCodigo.setEditable(true);
        btnGuardar.setEnabled(true);

        txtCodigo.requestFocusInWindow();
    }//GEN-LAST:event_btnLimpiarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JButton btnVerTodos;
    private javax.swing.JLabel lblBuscar;
    private javax.swing.JLabel lblCodigo;
    private javax.swing.JLabel lblDescripcion;
    private javax.swing.JLabel lblInventario;
    private javax.swing.JLabel lblMarca;
    private javax.swing.JLabel lblModulo;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblNuevoProducto;
    private javax.swing.JLabel lblPrecio;
    private javax.swing.JLabel lblStockActual;
    private javax.swing.JLabel lblStockMinimo;
    private javax.swing.JLabel lblSubtitulo;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel pnlCabecera;
    private javax.swing.JPanel pnlFormulario;
    private javax.swing.JPanel pnlInventario;
    private javax.swing.JScrollPane scrollProductos;
    private javax.swing.JTable tblProductos;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtPrecio;
    private javax.swing.JTextField txtStockActual;
    private javax.swing.JTextField txtStockMinimo;
    // End of variables declaration//GEN-END:variables
}
