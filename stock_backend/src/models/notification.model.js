import { DataTypes } from "sequelize";
import { sequelize } from "../config/database.js";

export const Notification = sequelize.define('Notification', {
  id: {
    type: DataTypes.INTEGER,
    primaryKey: true,
    autoIncrement: true
  },
  type: {
    type: DataTypes.STRING,
    allowNull: false
  },
  message: {
    type: DataTypes.STRING,
    allowNull: false
  },
  read: {
    type: DataTypes.BOOLEAN,
    defaultValue: false
  }
}, {
  tableName: 'notifications',
  timestamps: true
});