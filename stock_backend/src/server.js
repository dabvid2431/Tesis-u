import app from "./app.js";
import { sequelize, Category, Supplier } from "./models/index.js";

// dynamic import of seed to avoid circular issues
async function maybeSeed() {
  try {
    const catCount = await Category.count();
    const supCount = await Supplier.count();
    if (catCount === 0 || supCount === 0) {
      console.log("Semillas faltantes: ejecutando seed.js...");
      const mod = await import("../seed.js");
      if (mod && typeof mod.seed === 'function') {
        await mod.seed();
        console.log("Seed automático completado.");
      }
    }
  } catch (err) {
    console.error("Error al ejecutar seed automático:", err);
  }
}

const PORT = process.env.PORT || 3000;

const startServer = async () => {
  try {
    await sequelize.sync({ alter: true });
    console.log("✅ Conectado a la base de datos");
    // seed defaults if missing
    await maybeSeed();
    app.listen(PORT, '0.0.0.0', () => console.log(`🚀 Servidor corriendo en http://localhost:${PORT}`));
  } catch (err) {
    console.error("❌ Error al iniciar:", err);
  }
};

startServer();
