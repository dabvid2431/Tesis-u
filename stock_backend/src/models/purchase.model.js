import { DataTypes, Model } from "sequelize";
import { sequelize } from "../config/database.js";
import Supplier from "./supplier.model.js";

class Purchase extends Model {}
Purchase.init({
  id: { type: DataTypes.INTEGER, primaryKey: true, autoIncrement: true },
  supplierId: { type: DataTypes.INTEGER, allowNull: false },
  date: { type: DataTypes.DATE, defaultValue: DataTypes.NOW },
  total: { type: DataTypes.DECIMAL(10,2), defaultValue: 0 }
}, { sequelize, modelName: "purchase", tableName: "purchases" });

Purchase.belongsTo(Supplier, { foreignKey: "supplierId" });
Supplier.hasMany(Purchase);

export default Purchase;
