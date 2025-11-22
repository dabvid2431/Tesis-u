import { Router } from "express";
import { getSales, createSale } from "../controllers/sales.controller.js";

const router = Router();
router.get("/", getSales);
router.post("/", createSale);

export default router;
