import { DataTypes, Model } from "sequelize";
import { sequelize } from "../config/database.js";
import Client from "./client.model.js";

class Sale extends Model {}
Sale.init({
  id: { type: DataTypes.INTEGER, primaryKey: true, autoIncrement: true },
  clientId: { type: DataTypes.INTEGER, allowNull: false },
  date: { type: DataTypes.DATE, defaultValue: DataTypes.NOW },
  total: { type: DataTypes.DECIMAL(10,2), defaultValue: 0 }
}, { sequelize, modelName: "sale", tableName: "sales" });

Sale.belongsTo(Client, { foreignKey: "clientId" });
Client.hasMany(Sale);

export default Sale;
