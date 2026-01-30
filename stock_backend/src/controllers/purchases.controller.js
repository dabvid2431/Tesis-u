import { Purchase, PurchaseItem, Product, StockMovement } from "../models/index.js";
import { createNotification } from "./notifications.controller.js";

export const getPurchases = async (req, res) => {
  const purchases = await Purchase.findAll({ include: PurchaseItem });
  res.json(purchases);
};

export const createPurchase = async (req, res) => {
  const { supplierId, items } = req.body; // items = [{ productId, quantity, price }]

  // Basic validation
  if (!Array.isArray(items) || items.length === 0) {
    return res.status(422).json({ error: 'Items are required' });
  }
  for (const it of items) {
    if (!it.productId || typeof it.quantity !== 'number' || it.quantity <= 0) {
      return res.status(422).json({ error: 'Invalid item quantity or productId' });
    }
  }

  try {
    const purchase = await Purchase.create({ supplierId, total: 0 });
    let total = 0;

    for (const i of items) {
      const product = await Product.findByPk(i.productId);
      if (!product) {
        // Rollback: destroy created items and purchase
        await Purchase.destroy({ where: { id: purchase.id } });
        return res.status(422).json({ error: `Product ${i.productId} not found` });
      }

      await PurchaseItem.create({ purchaseId: purchase.id, ...i });

      product.stock += i.quantity;
      await product.save();

      await StockMovement.create({
        productId: i.productId,
        type: "IN",
        quantity: i.quantity,
        reference: `Purchase:${purchase.id}`
      });

      total += i.quantity * i.price;
    }

    purchase.total = total;
    await purchase.save();
    await createNotification("purchase", `Nueva compra registrada por $${total.toFixed(2)}`);
    res.status(201).json(purchase);
  } catch (err) {
    res.status(400).json({ error: err.message });
  }
};

// Delete purchase and revert stock/stock movements
export const deletePurchase = async (req, res) => {
  const id = parseInt(req.params.id, 10);
  if (isNaN(id)) return res.status(400).json({ error: "Invalid id" });

  try {
    const purchase = await Purchase.findByPk(id, { include: PurchaseItem });
    if (!purchase) return res.status(404).json({ error: "Purchase not found" });

    // Revert stock for each item and create an OUT movement
    const items = await PurchaseItem.findAll({ where: { purchaseId: id } });
    for (const it of items) {
      const product = await Product.findByPk(it.productId);
      if (product) {
        product.stock = Math.max(0, product.stock - it.quantity);
        await product.save();
      }

      await StockMovement.create({
        productId: it.productId,
        type: "OUT",
        quantity: it.quantity,
        reference: `DeletePurchase:${id}`
      });
    }

    // delete items then purchase
    await PurchaseItem.destroy({ where: { purchaseId: id } });
    await Purchase.destroy({ where: { id } });

    res.status(204).send();
  } catch (err) {
    console.error("Error deleting purchase:", err);
    res.status(500).json({ error: err.message });
  }
};
