import { Client } from "../models/index.js";

export const getClients = async (req, res) => {
  const clients = await Client.findAll();
  res.json(clients);
};

export const createClient = async (req, res) => {
  try {
    const client = await Client.create(req.body);
    res.status(201).json(client);
  } catch (err) {
    res.status(400).json({ error: err.message });
  }
};

export const updateClient = async (req, res) => {
  const client = await Client.findByPk(req.params.id);
  if (!client) return res.status(404).json({ error: "No encontrado" });
  Object.assign(client, req.body);
  await client.save();
  res.json(client);
};

export const deleteClient = async (req, res) => {
  const client = await Client.findByPk(req.params.id);
  if (!client) return res.status(404).json({ error: "No encontrado" });
  await client.destroy();
  res.json({ message: "Cliente eliminado" });
};
