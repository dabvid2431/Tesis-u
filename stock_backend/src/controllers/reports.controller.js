import { Sale, SaleItem, Product, Purchase, PurchaseItem, StockMovement, Category } from "../models/index.js";
import { Op, Sequelize } from "sequelize";

export const getSalesReport = async (req, res) => {
  try {
    const { start, end } = req.query;
    const where = {};
    if (start && end) {
      where.createdAt = { [Op.between]: [new Date(start), new Date(end)] };
    }
    const sales = await Sale.findAll({
      where,
      include: [{ model: SaleItem, include: [Product] }]
    });
    const totalSales = sales.length;
    const totalRevenue = sales.reduce((sum, sale) => sum + parseFloat(sale.total), 0);
    res.json({ totalSales, totalRevenue, sales });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};

export const getTopProducts = async (req, res) => {
  try {
    const topProducts = await SaleItem.findAll({
      attributes: [
        'productId',
        [Sequelize.fn('SUM', Sequelize.col('quantity')), 'totalSold']
      ],
      include: [Product],
      group: ['productId', 'Product.id'],
      order: [[Sequelize.fn('SUM', Sequelize.col('quantity')), 'DESC']],
      limit: 10
    });
    res.json(topProducts);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};

export const getLowStockProducts = async (req, res) => {
  try {
    const products = await Product.findAll({
      where: { stock: { [Op.lt]: 10 } }, // Assuming low stock < 10
      include: [Category]
    });
    res.json(products);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};

export const getStockMovements = async (req, res) => {
  try {
    const movements = await StockMovement.findAll({
      include: [Product],
      order: [['createdAt', 'DESC']]
    });
    res.json(movements);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};