import { Product } from "../models/index.js";
import { Op } from "sequelize";

export const getStock = async (req, res) => {
  const products = await Product.findAll();
  res.json(products);
};

export const getLowStock = async (req, res) => {
  const threshold = Number.parseInt(req.query.threshold, 10);
  const validThreshold = Number.isInteger(threshold) && threshold >= 0 ? threshold : 5;
  const products = await Product.findAll({
    where: { stock: { [Op.lt]: validThreshold } },
    order: [["stock", "ASC"]]
  });
  res.json(products);
};
