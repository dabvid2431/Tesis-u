import { Category } from "../models/index.js";

export const getCategories = async (req, res) => {
  const categories = await Category.findAll();
  res.json(categories);
};

export const createCategory = async (req, res) => {
  const { name, description } = req.body;
  try {
    const category = await Category.create({ name, description });
    res.status(201).json(category);
  } catch (err) {
    res.status(400).json({ error: err.message });
  }
};

export const updateCategory = async (req, res) => {
  const { id } = req.params;
  const { name, description } = req.body;
  const category = await Category.findByPk(id);
  if (!category) return res.status(404).json({ error: "No encontrado" });
  category.name = name;
  category.description = description;
  await category.save();
  res.json(category);
};

export const deleteCategory = async (req, res) => {
  const { id } = req.params;
  const category = await Category.findByPk(id);
  if (!category) return res.status(404).json({ error: "No encontrado" });
  await category.destroy();
  res.json({ message: "Categoría eliminada" });
};
