import { sequelize } from "./src/config/database.js";
import { Category, Supplier } from "./src/models/index.js";

async function reset() {
  try {
    console.log('Dropping all tables...');
    await sequelize.drop();

    console.log('Re-creating schema (sync { force: true })...');
    await sequelize.sync({ force: true });

    console.log('Seeding basic data...');
    const categories = [
      { name: "Papelería", description: "Artículos de papelería" },
      { name: "Tecnología", description: "Productos tecnológicos" },
      { name: "Limpieza", description: "Productos de limpieza" }
    ];

    const suppliers = [
      { name: "Proveedor Uno", contact: "proveedor1@email.com", address: "Calle 1" },
      { name: "Proveedor Dos", contact: "proveedor2@email.com", address: "Calle 2" }
    ];

    for (const cat of categories) {
      await Category.findOrCreate({ where: { name: cat.name }, defaults: cat });
    }
    for (const sup of suppliers) {
      await Supplier.findOrCreate({ where: { name: sup.name }, defaults: sup });
    }

    console.log('Reset y seed completados correctamente.');
    process.exit(0);
  } catch (err) {
    console.error('Error al resetear la DB:', err);
    process.exit(1);
  }
}

reset();
