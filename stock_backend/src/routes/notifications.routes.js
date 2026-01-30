import { Router } from "express";
import { getNotifications, markAsRead, checkLowStockNotifications } from "../controllers/notifications.controller.js";

const router = Router();

router.get("/", getNotifications);
router.put("/:id", markAsRead);
router.post("/check-low-stock", checkLowStockNotifications);

export default router;