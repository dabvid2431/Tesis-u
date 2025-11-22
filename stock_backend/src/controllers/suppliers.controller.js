import { Supplier } from "../models/index.js";

export const getSuppliers = async (req, res) => {
  const suppliers = await Supplier.findAll();
  res.json(suppliers);
};

export const createSupplier = async (req, res) => {
  try {
    const supplier = await Supplier.create(req.body);
    res.status(201).json(supplier);
  } catch (err) {
    res.status(400).json({ error: err.message });
  }
};

export const updateSupplier = async (req, res) => {
  const supplier = await Supplier.findByPk(req.params.id);
  if (!supplier) return res.status(404).json({ error: "No encontrado" });
  Object.assign(supplier, req.body);
  await supplier.save();
  res.json(supplier);
};

export const deleteSupplier = async (req, res) => {
  const supplier = await Supplier.findByPk(req.params.id);
  if (!supplier) return res.status(404).json({ error: "No encontrado" });
  await supplier.destroy();
  res.json({ message: "Proveedor eliminado" });
};
