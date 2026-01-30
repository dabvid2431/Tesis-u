import { Notification } from "../models/index.js";

export const getNotifications = async (req, res) => {
  try {
    const notifications = await Notification.findAll({
      order: [['createdAt', 'DESC']]
    });
    res.json(notifications);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};

export const markAsRead = async (req, res) => {
  try {
    const { id } = req.params;
    const notification = await Notification.findByPk(id);
    if (!notification) return res.status(404).json({ error: "Not found" });
    notification.read = true;
    await notification.save();
    res.json({ message: "Marked as read" });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};

export const createNotification = async (type, message) => {
  try {
    await Notification.create({ type, message });
  } catch (err) {
    console.error("Error creating notification:", err);
  }
};