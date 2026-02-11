import Product from "../models/product.model.js";
import Category from "../models/category.model.js";
import Supplier from "../models/supplier.model.js";
import Client from "../models/client.model.js";
import Sale from "../models/sale.model.js";
import Purchase from "../models/purchase.model.js";
import SaleItem from "../models/saleItem.model.js";
import PurchaseItem from "../models/purchaseItem.model.js";

/**
 * Restaura todos los datos desde un backup
 * ADVERTENCIA: Elimina todos los datos existentes
 */
export const restoreBackup = async (req, res) => {
  const t = await Product.sequelize.transaction();
  
  try {
    const { products, categories, suppliers, clients, sales, purchases } = req.body;
    
    console.log("🔄 Iniciando restauración de backup...");
    console.log(`  Productos: ${products?.length || 0}`);
    console.log(`  Categorías: ${categories?.length || 0}`);
    console.log(`  Proveedores: ${suppliers?.length || 0}`);
    console.log(`  Clientes: ${clients?.length || 0}`);
    console.log(`  Ventas: ${sales?.length || 0}`);
    console.log(`  Compras: ${purchases?.length || 0}`);
    
    // PASO 1: Eliminar todos los datos existentes (en orden inverso por dependencias)
    await SaleItem.destroy({ where: {}, transaction: t });
    await PurchaseItem.destroy({ where: {}, transaction: t });
    await Sale.destroy({ where: {}, transaction: t });
    await Purchase.destroy({ where: {}, transaction: t });
    await Product.destroy({ where: {}, transaction: t });
    await Client.destroy({ where: {}, transaction: t });
    await Supplier.destroy({ where: {}, transaction: t });
    await Category.destroy({ where: {}, transaction: t });
    
    console.log("✅ Datos antiguos eliminados");
    
    // PASO 2: Insertar nuevos datos (en orden por dependencias)
    
    // Categorías primero
    if (categories && categories.length > 0) {
      await Category.bulkCreate(categories, { transaction: t });
      console.log(`✅ ${categories.length} categorías restauradas`);
    }
    
    // Proveedores
    if (suppliers && suppliers.length > 0) {
      await Supplier.bulkCreate(suppliers, { transaction: t });
      console.log(`✅ ${suppliers.length} proveedores restaurados`);
    }
    
    // Clientes
    if (clients && clients.length > 0) {
      await Client.bulkCreate(clients, { transaction: t });
      console.log(`✅ ${clients.length} clientes restaurados`);
    }
    
    // Productos (dependen de categorías y proveedores)
    if (products && products.length > 0) {
      await Product.bulkCreate(products, { transaction: t });
      console.log(`✅ ${products.length} productos restaurados`);
    }
    
    // Ventas (dependen de clientes)
    if (sales && sales.length > 0) {
      for (const sale of sales) {
        const { items, ...saleData } = sale;
        const createdSale = await Sale.create(saleData, { transaction: t });
        
        if (items && items.length > 0) {
          const saleItems = items.map(item => ({
            ...item,
            saleId: createdSale.id
          }));
          await SaleItem.bulkCreate(saleItems, { transaction: t });
        }
      }
      console.log(`✅ ${sales.length} ventas restauradas`);
    }
    
    // Compras (dependen de proveedores)
    if (purchases && purchases.length > 0) {
      for (const purchase of purchases) {
        const { items, ...purchaseData } = purchase;
        const createdPurchase = await Purchase.create(purchaseData, { transaction: t });
        
        if (items && items.length > 0) {
          const purchaseItems = items.map(item => ({
            ...item,
            purchaseId: createdPurchase.id
          }));
          await PurchaseItem.bulkCreate(purchaseItems, { transaction: t });
        }
      }
      console.log(`✅ ${purchases.length} compras restauradas`);
    }
    
    await t.commit();
    
    console.log("✅ Restauración completada exitosamente");
    
    res.json({
      success: true,
      message: "Backup restaurado exitosamente",
      restored: {
        categories: categories?.length || 0,
        suppliers: suppliers?.length || 0,
        clients: clients?.length || 0,
        products: products?.length || 0,
        sales: sales?.length || 0,
        purchases: purchases?.length || 0
      }
    });
    
  } catch (error) {
    await t.rollback();
    console.error("❌ Error en restauración:", error);
    res.status(500).json({
      success: false,
      error: "Error restaurando backup: " + error.message
    });
  }
};

/**
 * Obtiene una exportación completa de todos los datos
 */
export const exportAllData = async (req, res) => {
  try {
    const [products, categories, suppliers, clients, sales, purchases] = await Promise.all([
      Product.findAll(),
      Category.findAll(),
      Supplier.findAll(),
      Client.findAll(),
      Sale.findAll({ include: [SaleItem] }),
      Purchase.findAll({ include: [PurchaseItem] })
    ]);
    
    res.json({
      timestamp: Date.now(),
      products,
      categories,
      suppliers,
      clients,
      sales,
      purchases
    });
    
  } catch (error) {
    console.error("Error exportando datos:", error);
    res.status(500).json({ error: "Error exportando datos: " + error.message });
  }
};
