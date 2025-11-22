import { DataTypes, Model } from "sequelize";
import { sequelize } from "../config/database.js";
import Purchase from "./purchase.model.js";
import Product from "./product.model.js";

class PurchaseItem extends Model {}
PurchaseItem.init({
  id: { type: DataTypes.INTEGER, primaryKey: true, autoIncrement: true },
  purchaseId: { type: DataTypes.INTEGER, allowNull: false },
  productId: { type: DataTypes.INTEGER, allowNull: false },
  quantity: { type: DataTypes.INTEGER, allowNull: false },
  price: { type: DataTypes.DECIMAL(10,2), allowNull: false }
}, { sequelize, modelName: "purchaseItem", tableName: "purchase_items" });

PurchaseItem.belongsTo(Purchase, { foreignKey: "purchaseId" });
Purchase.hasMany(PurchaseItem);

PurchaseItem.belongsTo(Product, { foreignKey: "productId" });
Product.hasMany(PurchaseItem);

export default PurchaseItem;
