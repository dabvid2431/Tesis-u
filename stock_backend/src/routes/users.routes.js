import { Router } from "express";
import { createUser, login, promoteUserToAdmin } from "../controllers/users.controller.js";

const router = Router();

router.post("/users", createUser);
router.post("/users/promote-admin", promoteUserToAdmin);
router.post("/login", login);

export default router;
