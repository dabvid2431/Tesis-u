import express from "express";
import cors from "cors";
import routes from "./routes/index.js";

const app = express();
app.use(cors());
app.use(express.json());
app.use("/api", routes);

// Health check
app.get("/health", (req, res) => res.json({ status: "ok" }));

export default app;

// Mensaje raíz
app.get("/", (req, res) => {
  res.send("🟢 Backend de Stock funcionando correctamente!");
});

// Health check
app.get("/health", (req, res) => {
  res.json({ status: "ok", timestamp: new Date() });
});
