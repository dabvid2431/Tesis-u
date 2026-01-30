import { Sale, SaleItem, Product, StockMovement } from "../models/index.js";
import { createNotification } from "./notifications.controller.js";

export const getSales = async (req, res) => {
  const sales = await Sale.findAll({ include: SaleItem });
  res.json(sales);
};

export const createSale = async (req, res) => {
  const { clientId, items } = req.body; // items = [{ productId, quantity, price }]
  try {
    const sale = await Sale.create({ clientId, total: 0 });
    let total = 0;

    for (const i of items) {
      const product = await Product.findByPk(i.productId);
      if (product.stock < i.quantity) return res.status(400).json({ error: `Stock insuficiente para ${product.name}` });

      await SaleItem.create({ saleId: sale.id, ...i });
      product.stock -= i.quantity;
      await product.save();
      if (product.stock < 10) {
        await createNotification("low_stock", `Stock bajo para ${product.name}: ${product.stock} unidades`);
      }

      await StockMovement.create({
        productId: i.productId,
        type: "OUT",
        quantity: i.quantity,
        reference: `Sale:${sale.id}`
      });

      total += i.quantity * i.price;
    }

    sale.total = total;
    await sale.save();
    await createNotification("sale", `Nueva venta registrada por $${total.toFixed(2)}`);
    res.status(201).json(sale);
  } catch (err) {
    res.status(400).json({ error: err.message });
  }
};
