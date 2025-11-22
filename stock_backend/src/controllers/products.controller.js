import { Product, Category } from "../models/index.js";

export const getProducts = async (req, res) => {
  const products = await Product.findAll({ include: Category });
  res.json(products);
};

export const createProduct = async (req, res) => {
  try {
    const { sku, name, brand, purchasePrice, salePrice, stock, categoryId } = req.body;
    const product = await Product.create({ sku, name, brand, purchasePrice, salePrice, stock, categoryId });
    res.status(201).json(product);
  } catch (err) {
    res.status(400).json({ error: err.message });
  }
};

export const updateProduct = async (req, res) => {
  const { id } = req.params;
  const product = await Product.findByPk(id);
  if (!product) return res.status(404).json({ error: "No encontrado" });

  const { sku, name, brand, purchasePrice, salePrice, stock, categoryId } = req.body;
  Object.assign(product, { sku, name, brand, purchasePrice, salePrice, stock, categoryId });
  await product.save();
  res.json(product);
};

export const deleteProduct = async (req, res) => {
  const { id } = req.params;
  const product = await Product.findByPk(id);
  if (!product) return res.status(404).json({ error: "No encontrado" });
  await product.destroy();
  res.json({ message: "Producto eliminado" });
};
