import { DataTypes, Model } from "sequelize";
import { sequelize } from "../config/database.js";
import Sale from "./sale.model.js";
import Product from "./product.model.js";

class SaleItem extends Model {}
SaleItem.init({
  id: { type: DataTypes.INTEGER, primaryKey: true, autoIncrement: true },
  saleId: { type: DataTypes.INTEGER, allowNull: false },
  productId: { type: DataTypes.INTEGER, allowNull: false },
  quantity: { type: DataTypes.INTEGER, allowNull: false },
  price: { type: DataTypes.DECIMAL(10,2), allowNull: false }
}, { sequelize, modelName: "saleItem", tableName: "sale_items" });

SaleItem.belongsTo(Sale, { foreignKey: "saleId" });
Sale.hasMany(SaleItem);

SaleItem.belongsTo(Product, { foreignKey: "productId" });
Product.hasMany(SaleItem);

export default SaleItem;
