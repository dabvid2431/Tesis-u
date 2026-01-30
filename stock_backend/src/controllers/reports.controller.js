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
    // Get all sales items grouped by product
    const topProductsRaw = await SaleItem.findAll({
      attributes: [
        'productId',
        [Sequelize.fn('SUM', Sequelize.col('quantity')), 'totalSold']
      ],
      group: ['productId'],
      order: [[Sequelize.fn('SUM', Sequelize.col('quantity')), 'DESC']],
      limit: 10,
      raw: true
    });
    
    // Get product details for each
    const result = [];
    for (const item of topProductsRaw) {
      const product = await Product.findByPk(item.productId, {
        attributes: ['id', 'name', 'salePrice', 'stock']
      });
      if (product) {
        result.push({
          name: product.name,
          totalSold: parseInt(item.totalSold),
          Product: product.toJSON()
        });
      }
    }
    
    res.json(result);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};

export const getLowStockProducts = async (req, res) => {
  try {
    const products = await Product.findAll({
      where: { stock: { [Op.lt]: 10 } }, // Low stock < 10
      include: [{ model: Category, attributes: ['id', 'name'] }],
      attributes: ['id', 'name', 'salePrice', 'stock'],
      order: [['stock', 'ASC']]
    });
    res.json(products);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};

export const getStockMovements = async (req, res) => {
  try {
    const movements = await StockMovement.findAll({
      order: [['createdAt', 'DESC']],
      limit: 50,
      raw: false
    });
    
    // Map to include product details by fetching separately
    const result = [];
    for (const m of movements) {
      const product = await Product.findByPk(m.productId, {
        attributes: ['id', 'name', 'salePrice', 'stock']
      });
      
      const productName = product?.name || 'Desconocido';
      result.push({
        id: m.id,
        type: m.type,
        quantity: m.quantity,
        createdAt: m.createdAt,
        productId: m.productId,
        productName: productName,
        Product: product ? product.toJSON() : null
      });
    }
    
    res.json(result);
  } catch (err) {
    console.error('Stock movements error:', err);
    res.status(500).json({ error: err.message });
  }
};