import User from "../models/user.model.js";
import bcrypt from "bcryptjs";

export const createUser = async (req, res) => {
  try {
    const { username, password, role, adminCode } = req.body;
    if (!username || !password) {
      return res.status(400).json({ message: "Todos los campos son requeridos" });
    }

    const requestedRole = String(role || "vendedor").trim().toLowerCase();
    if (!["admin", "vendedor"].includes(requestedRole)) {
      return res.status(400).json({ message: "Rol no válido" });
    }

    const exists = await User.findOne({ where: { username } });
    if (exists) {
      return res.status(409).json({ message: "El usuario ya existe" });
    }

    let finalRole = "vendedor";
    if (requestedRole === "admin") {
      const currentAdmins = await User.count({ where: { role: "admin" } });
      if (currentAdmins === 0) {
        // Se permite crear el primer admin sin código (bootstrap inicial)
        finalRole = "admin";
      } else {
        const expectedAdminCode = process.env.ADMIN_REGISTRATION_CODE;
        if (!expectedAdminCode) {
          return res.status(403).json({
            message: "Ya existe un admin. Registro admin deshabilitado sin código de verificación"
          });
        }
        if (!adminCode || String(adminCode).trim() !== expectedAdminCode) {
          return res.status(403).json({ message: "Código de verificación de administrador inválido" });
        }
        finalRole = "admin";
      }
    }

    const hashedPassword = await bcrypt.hash(password, 10);
    const user = await User.create({ username, password: hashedPassword, role: finalRole });
    res.status(201).json({ id: user.id, username: user.username, role: user.role });
  } catch (err) {
    res.status(500).json({ message: "Error al crear usuario", error: err.message });
  }
};

export const promoteUserToAdmin = async (req, res) => {
  try {
    const { adminUsername, adminPassword, targetUsername } = req.body;

    if (!adminUsername || !adminPassword || !targetUsername) {
      return res.status(400).json({ message: "adminUsername, adminPassword y targetUsername son requeridos" });
    }

    const adminUser = await User.findOne({ where: { username: adminUsername } });
    if (!adminUser) {
      return res.status(401).json({ message: "Credenciales de administrador inválidas" });
    }
    if (adminUser.role !== "admin") {
      return res.status(403).json({ message: "Solo un administrador puede promover usuarios" });
    }

    const valid = await bcrypt.compare(adminPassword, adminUser.password);
    if (!valid) {
      return res.status(401).json({ message: "Credenciales de administrador inválidas" });
    }

    const targetUser = await User.findOne({ where: { username: targetUsername } });
    if (!targetUser) {
      return res.status(404).json({ message: "Usuario objetivo no encontrado" });
    }

    if (targetUser.role === "admin") {
      return res.status(200).json({
        message: "El usuario ya es administrador",
        user: { id: targetUser.id, username: targetUser.username, role: targetUser.role }
      });
    }

    targetUser.role = "admin";
    await targetUser.save();

    return res.status(200).json({
      message: "Usuario promovido a administrador",
      user: { id: targetUser.id, username: targetUser.username, role: targetUser.role }
    });
  } catch (err) {
    return res.status(500).json({ message: "Error al promover usuario", error: err.message });
  }
};

export const login = async (req, res) => {
  try {
    const { username, password } = req.body;
    const user = await User.findOne({ where: { username } });
    if (!user) {
      return res.status(401).json({ message: "Usuario o contraseña incorrectos" });
    }
    const valid = await bcrypt.compare(password, user.password);
    if (!valid) {
      return res.status(401).json({ message: "Usuario o contraseña incorrectos" });
    }
    res.json({ id: user.id, username: user.username, role: user.role });
  } catch (err) {
    res.status(500).json({ message: "Error al iniciar sesión", error: err.message });
  }
};
