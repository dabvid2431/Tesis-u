import { Router } from "express";
import { restoreBackup, exportAllData } from "../controllers/backup.controller.js";

const router = Router();

// POST /api/backup/restore - Restaura todos los datos desde un backup
router.post("/restore", restoreBackup);

// GET /api/backup/export - Exporta todos los datos
router.get("/export", exportAllData);

export default router;
