import { Category, Supplier, Product, Purchase, PurchaseItem, StockMovement } from "./src/models/index.js";
import { sequelize } from "./src/config/database.js";

export async function seed() {
  await sequelize.sync();

  // Categorías de ejemplo
  const categories = [
    { name: "Papelería", description: "Artículos de papelería" },
    { name: "Tecnología", description: "Productos tecnológicos" },
    { name: "Limpieza", description: "Productos de limpieza" }
  ];

  // Proveedores de ejemplo
  const suppliers = [
    { name: "Proveedor Uno", contact: "proveedor1@email.com", address: "Calle 1" },
    { name: "Proveedor Dos", contact: "proveedor2@email.com", address: "Calle 2" }
  ];

  for (const cat of categories) {
    await Category.findOrCreate({ where: { name: cat.name }, defaults: cat });
  }
  for (const sup of suppliers) {
    await Supplier.findOrCreate({ where: { name: sup.name }, defaults: sup });
  }

  // Productos de ejemplo
  const products = [
    { sku: "PAP-001", name: "Cuaderno A4", brand: "MarcaA", purchasePrice: 1.00, salePrice: 2.00, stock: 10, categoryName: "Papelería" },
    { sku: "TEC-001", name: "Mouse USB", brand: "MarcaTec", purchasePrice: 8.00, salePrice: 15.00, stock: 5, categoryName: "Tecnología" },
    { sku: "LIM-001", name: "Detergente 1L", brand: "Limpio", purchasePrice: 2.50, salePrice: 5.00, stock: 20, categoryName: "Limpieza" },
    { sku: "PAP-002", name: "Lapicero Azul", brand: "Escribe", purchasePrice: 0.20, salePrice: 0.50, stock: 100, categoryName: "Papelería" },
    { sku: "TEC-002", name: "Cable USB-C", brand: "CableMax", purchasePrice: 3.00, salePrice: 6.00, stock: 15, categoryName: "Tecnología" },
    { sku: "LIM-002", name: "Esponja", brand: "Foam", purchasePrice: 0.50, salePrice: 1.20, stock: 30, categoryName: "Limpieza" }
  ];

  for (const p of products) {
    const category = await Category.findOne({ where: { name: p.categoryName } });
    if (!category) continue;
    await Product.findOrCreate({ where: { sku: p.sku }, defaults: { sku: p.sku, name: p.name, brand: p.brand, purchasePrice: p.purchasePrice, salePrice: p.salePrice, stock: p.stock, categoryId: category.id } });
  }

  // Compras de ejemplo (creadas dentro de transacciones para consistencia)
  const supplier = await Supplier.findOne();
  const productsList = await Product.findAll({ limit: 5 });

  if (supplier && productsList.length > 0) {
    const t = await sequelize.transaction();
    try {
      const purchase = await Purchase.create({ supplierId: supplier.id, total: 0 }, { transaction: t });
      let total = 0;
      // crear 2 items de ejemplo
      const itemsData = [
        { product: productsList[0], quantity: 5, price: productsList[0].purchasePrice },
        { product: productsList[1], quantity: 2, price: productsList[1].purchasePrice }
      ];
      for (const it of itemsData) {
        await PurchaseItem.create({ purchaseId: purchase.id, productId: it.product.id, quantity: it.quantity, price: it.price }, { transaction: t });
        total += Number(it.price) * Number(it.quantity);
        // actualizar stock
        it.product.stock = Number(it.product.stock) + Number(it.quantity);
        await it.product.save({ transaction: t });
        // registrar movimiento
        await StockMovement.create({ productId: it.product.id, type: 'IN', quantity: it.quantity, reference: `PURCHASE_${purchase.id}` }, { transaction: t });
      }
      purchase.total = total.toFixed(2);
      await purchase.save({ transaction: t });
      await t.commit();
    } catch (err) {
      await t.rollback();
      console.error('Error creando compra de ejemplo:', err);
    }
  }

  console.log("Datos de ejemplo insertados correctamente.");
}

// If script is run directly, execute seed()
if (process.argv[1] && process.argv[1].toLowerCase().includes('seed.js')) {
  seed().then(() => process.exit(0)).catch(err => {
    console.error('Seed failed:', err);
    process.exit(1);
  });
}
