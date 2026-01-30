import { Router } from "express";
import { getSalesReport, getTopProducts, getLowStockProducts, getStockMovements } from "../controllers/reports.controller.js";

const router = Router();

router.get("/sales", getSalesReport);
router.get("/top-products", getTopProducts);
router.get("/low-stock", getLowStockProducts);
router.get("/stock-movements", getStockMovements);

export default router;