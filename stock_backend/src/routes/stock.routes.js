import { Router } from "express";
import { getStock, getLowStock } from "../controllers/stock.controller.js";

const router = Router();
router.get("/", getStock);
router.get("/low", getLowStock);

export default router;
