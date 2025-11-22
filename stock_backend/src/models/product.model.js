import { DataTypes, Model } from "sequelize";
import { sequelize } from "../config/database.js";
import Category from "./category.model.js";

class Product extends Model {}
Product.init({
  id: { type: DataTypes.INTEGER, primaryKey: true, autoIncrement: true },
  sku: { type: DataTypes.STRING(50), unique: true, allowNull: false },
  name: { type: DataTypes.STRING(150), allowNull: false },
  brand: { type: DataTypes.STRING(80) },
  purchasePrice: { type: DataTypes.DECIMAL(10,2), allowNull: false },
  salePrice: { type: DataTypes.DECIMAL(10,2), allowNull: false },
  stock: { type: DataTypes.INTEGER, defaultValue: 0 },
  categoryId: { type: DataTypes.INTEGER, allowNull: false }
}, { sequelize, modelName: "product", tableName: "products" });

Product.belongsTo(Category, { foreignKey: "categoryId" });
Category.hasMany(Product);

export default Product;
