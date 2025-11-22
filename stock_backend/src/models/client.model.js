import { DataTypes, Model } from "sequelize";
import { sequelize } from "../config/database.js";

class Client extends Model {}
Client.init({
  id: { type: DataTypes.INTEGER, primaryKey: true, autoIncrement: true },
  name: { type: DataTypes.STRING(150), allowNull: false },
  phone: { type: DataTypes.STRING(50) },
  email: { type: DataTypes.STRING(150) },
  address: { type: DataTypes.STRING(255) }
}, { sequelize, modelName: "client", tableName: "clients" });

export default Client;
