import { Sale, SaleItem, Product, StockMovement, Client } from "../models/index.js";
import User from "../models/user.model.js";
import { createNotification, checkLowStock } from "./notifications.controller.js";

export const getSales = async (req, res) => {
  const sales = await Sale.findAll({
    include: [
      SaleItem,
      { model: Client, attributes: ["id", "name"] },
      { model: User, as: "seller", attributes: ["id", "username"] }
    ]
  });
  const enrichedSales = sales.map((sale) => {
    const plain = sale.toJSON();
    return {
      ...plain,
      clientName: plain.client?.name || null,
      sellerName: plain.seller?.username || null
    };
  });
  res.json(enrichedSales);
};

export const createSale = async (req, res) => {
  const { clientId, userId, sellerUsername, items } = req.body; // items = [{ productId, quantity, price }]
  try {
    let validUserId = Number.isInteger(Number(userId)) && Number(userId) > 0 ? Number(userId) : null;
    if (!validUserId && sellerUsername) {
      const seller = await User.findOne({ where: { username: sellerUsername } });
      if (seller) validUserId = seller.id;
    }
    const sale = await Sale.create({ clientId, userId: validUserId, total: 0 });
    let total = 0;

    for (const i of items) {
      const product = await Product.findByPk(i.productId);
      if (product.stock < i.quantity) {
        return res.status(400).json({
          error: `Stock insuficiente para ${product.name}. Disponible: ${product.stock}, solicitado: ${i.quantity}`
        });
      }

      await SaleItem.create({ saleId: sale.id, ...i });
      product.stock -= i.quantity;
      await product.save();
      await checkLowStock(product);

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
