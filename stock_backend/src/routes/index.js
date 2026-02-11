import { Router } from "express";
import categoriesRoutes from "./categories.routes.js";
import productsRoutes from "./products.routes.js";
import suppliersRoutes from "./suppliers.routes.js";
import clientsRoutes from "./clients.routes.js";
import purchasesRoutes from "./purchases.routes.js";
import salesRoutes from "./sales.routes.js";
import stockRoutes from "./stock.routes.js";
import usersRoutes from "./users.routes.js";
import reportsRoutes from "./reports.routes.js";
import notificationsRoutes from "./notifications.routes.js";
import backupRoutes from "./backup.routes.js";

const router = Router();

router.use("/categories", categoriesRoutes);
router.use("/products", productsRoutes);
router.use("/suppliers", suppliersRoutes);
router.use("/clients", clientsRoutes);
router.use("/purchases", purchasesRoutes);
router.use("/sales", salesRoutes);
router.use("/stock", stockRoutes);
router.use("/reports", reportsRoutes);
router.use("/notifications", notificationsRoutes);
router.use("/backup", backupRoutes);
router.use(usersRoutes);

export default router;
