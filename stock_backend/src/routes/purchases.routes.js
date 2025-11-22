import { Router } from "express";
import { getPurchases, createPurchase, deletePurchase } from "../controllers/purchases.controller.js";

const router = Router();
router.get("/", getPurchases);
router.post("/", createPurchase);
router.delete("/:id", deletePurchase);

export default router;
