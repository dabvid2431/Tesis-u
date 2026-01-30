import { Notification } from "../models/index.js";
import { Product } from "../models/index.js";
import { Op } from "sequelize";

export const getNotifications = async (req, res) => {
  try {
    // Primero, verificar y crear notificaciones de stock bajo
    await checkAllLowStockProducts();
    
    const notifications = await Notification.findAll({
      order: [['createdAt', 'DESC']],
      limit: 50
    });
    res.json(notifications);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};

export const markAsRead = async (req, res) => {
  try {
    const { id } = req.params;
    const notification = await Notification.findByPk(id);
    if (!notification) return res.status(404).json({ error: "Not found" });
    notification.read = true;
    await notification.save();
    res.json({ message: "Marked as read" });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};

export const createNotification = async (type, message) => {
  try {
    await Notification.create({ type, message });
    console.log(`✅ Notificación creada: ${type} - ${message}`);
  } catch (err) {
    console.error("Error creating notification:", err);
  }
};

export const checkLowStock = async (product) => {
  // Check if product stock is below threshold (10 units)
  if (product && product.stock < 10) {
    // Check if notification already exists for this product today
    const existingNotification = await Notification.findOne({
      where: {
        type: "low_stock",
        message: { [Op.like]: `%${product.name}%` }
      }
    });
    
    if (!existingNotification) {
      await createNotification("low_stock", `⚠️ Stock bajo para ${product.name}: quedan ${product.stock} unidades`);
    }
  }
};

export const checkLowStockNotifications = async (req, res) => {
  try {
    const lowStockProducts = await Product.findAll({
      where: {
        stock: { [Op.lt]: 10 }
      }
    });

    for (const product of lowStockProducts) {
      await checkLowStock(product);
    }

    res.json({ 
      message: `Se verificaron ${lowStockProducts.length} productos con stock bajo`,
      products: lowStockProducts.length
    });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};

export const checkAllLowStockProducts = async () => {
  try {
    const lowStockProducts = await Product.findAll({
      where: {
        stock: { [Op.lt]: 10 }
      }
    });

    for (const product of lowStockProducts) {
      await checkLowStock(product);
    }
  } catch (err) {
    console.error("Error checking low stock products:", err);
  }
};