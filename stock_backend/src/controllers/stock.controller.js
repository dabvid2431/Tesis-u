import { Product, StockMovement } from "../models/index.js";

export const getStock = async (req, res) => {
  const products = await Product.findAll();
  res.json(products);
};

export const getLowStock = async (req, res) => {
  const products = await Product.findAll({ where: { stock: { lt: 5 } } });
  res.json(products);
};
