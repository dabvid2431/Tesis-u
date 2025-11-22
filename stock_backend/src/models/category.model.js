import { DataTypes, Model } from "sequelize";
import { sequelize } from "../config/database.js";

class Category extends Model {}
Category.init({
  id: { type: DataTypes.INTEGER, primaryKey: true, autoIncrement: true },
  name: { type: DataTypes.STRING(80), allowNull: false, unique: true },
  description: { type: DataTypes.STRING(255) }
}, { sequelize, modelName: "category", tableName: "categories" });

export default Category;
