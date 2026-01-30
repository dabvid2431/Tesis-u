import { Router } from "express";
import { getNotifications, markAsRead } from "../controllers/notifications.controller.js";

const router = Router();

router.get("/", getNotifications);
router.put("/:id/read", markAsRead);

export default router;