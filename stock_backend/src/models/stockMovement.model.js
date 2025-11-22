import { DataTypes, Model } from "sequelize";
import { sequelize } from "../config/database.js";
import Product from "./product.model.js";

class StockMovement extends Model {}
StockMovement.init({
  id: { type: DataTypes.INTEGER, primaryKey: true, autoIncrement: true },
  productId: { type: DataTypes.INTEGER, allowNull: false },
  type: { type: DataTypes.ENUM("IN","OUT"), allowNull: false },
  quantity: { type: DataTypes.INTEGER, allowNull: false },
  date: { type: DataTypes.DATE, defaultValue: DataTypes.NOW },
  reference: { type: DataTypes.STRING(50) }
}, { sequelize, modelName: "stockMovement", tableName: "stock_movements" });

StockMovement.belongsTo(Product, { foreignKey: "productId" });
Product.hasMany(StockMovement);

export default StockMovement;
